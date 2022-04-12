package cc.banzhi.runtrace.transforms.lifecycle;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.Status;
import com.android.build.api.transform.TransformOutputProvider;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import cc.banzhi.runtrace.transforms.BaseTransform;

/**
 * @program: ZRunTrace
 * @description: Activity生命周期埋点
 * @author: zoufengli01
 * @create: 2022/4/12 4:28 下午
 **/
public class LifeTraceTransform extends BaseTransform {

    public LifeTraceTransform(Project project) {
        super(project);
    }

    @Override
    protected String getTaskName() {
        return "LifeTraceTransform";
    }

    /**
     * 遍历JarInput
     *
     * @param jarInput       待遍历数据
     * @param outputProvider 输出Provider
     * @param isIncremental  是否支持增量更新
     */
    @Override
    protected void traverseJarInput(JarInput jarInput,
                                    TransformOutputProvider outputProvider, boolean isIncremental) throws IOException {
        if (jarInput == null) {
            return;
        }

        File inputFile = jarInput.getFile();
        if (inputFile == null || !inputFile.exists()) {
            return;
        }

        // 获取MD5防止jar包重名被覆盖
        String md5Name = DigestUtils.md5Hex(inputFile.getAbsolutePath());
        String jarName = jarInput.getName();
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length() - 4);
        }
        File dest = outputProvider.getContentLocation(md5Name + jarName,
                jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);

        // 支持增量更新
        if (isIncremental) {
            Status status = jarInput.getStatus();
            if (status == Status.REMOVED) {
                // 删除
                File file = outputProvider.getContentLocation(
                        inputFile.getAbsolutePath(), jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                if (file.exists()) {
                    FileUtils.forceDelete(file);
                }
                return;
            } else if (status == Status.NOTCHANGED) {
                // 不做任何处理，仅仅复制提供给下一个Transform
                FileUtils.copyFile(inputFile, dest);
                return;
            } else if (status == Status.ADDED
                    || status == Status.CHANGED) {
                // 正常处理，向下执行
            }
        }

        if (inputFile.getAbsolutePath().endsWith(".jar")
                && checkJar(jarName)) {
            File tempFile = null;
            FileOutputStream fos = null;
            JarOutputStream jos = null;
            JarFile jarFile = null;

            try {
                tempFile = new File(inputFile.getParent() +
                        File.separator + "temp_" + System.currentTimeMillis() + ".jar");
                fos = new FileOutputStream(tempFile);
                jos = new JarOutputStream(fos);

                jarFile = new JarFile(inputFile);
                Enumeration<JarEntry> enumeration = jarFile.entries();
                // 遍历
                while (enumeration.hasMoreElements()) {
                    JarEntry jarEntry = enumeration.nextElement();
                    String entryName = jarEntry.getName();
                    ZipEntry zipEntry = new ZipEntry(entryName);
                    jos.putNextEntry(zipEntry);
                    // 处理Class
                    if (checkClass(entryName)) {
                        System.out.println("处理Class：" + entryName);
//                        // 解析
//                        try (InputStream is = jarFile.getInputStream(jarEntry)) {
//                            analyzeClass(is);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }

                        // 生成
                        try (InputStream is = jarFile.getInputStream(jarEntry)) {
                            byte[] bytes = generateClass(is);
                            if (bytes != null) {
                                jos.write(bytes);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try (InputStream is = jarFile.getInputStream(jarEntry)) {
                            jos.write(IOUtils.toByteArray(is));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    jos.closeEntry();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (jos != null) {
                    try {
                        jos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (tempFile.exists()) {
                FileUtils.copyFile(tempFile, dest);
                // 删除临时文件
                FileUtils.forceDelete(tempFile);
            }
        } else {
            FileUtils.copyFile(inputFile, dest);
        }
    }

    /**
     * 遍历DirectoryInput
     *
     * @param dirInput       待遍历数据
     * @param outputProvider 输出Provider
     * @param isIncremental  是否支持增量更新
     */
    @Override
    protected void traverseDirectoryInput(DirectoryInput dirInput,
                                          TransformOutputProvider outputProvider, boolean isIncremental) throws IOException {
        if (dirInput == null) {
            return;
        }

        File dest = outputProvider.getContentLocation(
                dirInput.getName(), dirInput.getContentTypes(), dirInput.getScopes(), Format.DIRECTORY);
        FileUtils.forceMkdir(dest);

        if (isIncremental) { // 增量更新
            String srcDirPath = dirInput.getFile().getAbsolutePath();
            String destDirPath = dest.getAbsolutePath();

            Map<File, Status> fileStatusMap = dirInput.getChangedFiles();
            for (Map.Entry<File, Status> changedFile : fileStatusMap.entrySet()) {
                Status status = changedFile.getValue();
                File inputFile = changedFile.getKey();
                String destFilePath = inputFile.getAbsolutePath().replace(srcDirPath, destDirPath);
                File destFile = new File(destFilePath);
                switch (status) {
                    case NOTCHANGED:
                        // 不做任何处理，仅仅复制提供给下一个Transform
                        FileUtils.copyFile(inputFile, dest);
                        break;
                    case ADDED:
                    case CHANGED:
                        traverseDirectory(inputFile);
                        FileUtils.touch(destFile);
                        FileUtils.copyFile(inputFile, destFile);
                        break;
                    case REMOVED:
                        if (destFile.exists()) {
                            FileUtils.forceDelete(destFile);
                        }
                        break;
                }
            }
        } else { // 非增量更新
            File inputFile = dirInput.getFile();
            traverseDirectory(inputFile);
            FileUtils.copyDirectory(inputFile, dest);
        }
    }

    /**
     * 递归遍历文件
     *
     * @param file 待处理数据
     */
    private void traverseDirectory(File file) {
        if (file != null && file.exists()) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (checkClass(fileName)) {
                    System.out.println("处理Class：" + fileName);
//                    // 解析
//                    try (FileInputStream is = new FileInputStream(file)) {
//                        analyzeClass(is);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    // 生成
                    FileInputStream is = null;
                    FileOutputStream os = null;
                    try {
                        is = new FileInputStream(file);
                        byte[] bytes = generateClass(is);
                        if (bytes != null && bytes.length > 0) {
                            os = new FileOutputStream(file);
                            os.write(bytes);
                            os.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File item : files) {
                        traverseDirectory(item);
                    }
                }
            }
        }
    }

    /**
     * ASM生成Class
     *
     * @param is 待处理Class文件输入流
     */
    private byte[] generateClass(InputStream is) throws IOException {
        if (is != null && is.available() > 0) {
            ClassReader classReader = new ClassReader(is);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            ClassVisitor classVisitor = new LifeClassVisitor(Opcodes.ASM7, classWriter, project);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        }
        return null;
    }
}
