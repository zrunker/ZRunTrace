package cc.banzhi.runtrace.transform;


import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Status;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.ide.common.internal.WaitableExecutor;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import cc.banzhi.runtrace.transform.analyze.AnalyzeClassVisitor;
import cc.banzhi.runtrace.transform.generate.GenerateClassVisitor;

/**
 * @program: ZRunTrace
 * @description: 自定义Transform处理注解
 * @author: zoufengli01
 * @create: 2022/1/13 5:11 下午
 **/
public class RunTraceTransform extends Transform {
    /**
     * 异步任务
     */
    private final WaitableExecutor waitableExecutor = WaitableExecutor.useGlobalSharedThreadPool();

    /**
     * 为Transform定义一个唯一的名称
     */
    @Override
    public String getName() {
        return "RunTraceTransform";
    }

    /**
     * 定义Transform接收的输入文件类型
     */
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        // Class输入
        return TransformManager.CONTENT_CLASS;
    }

    /**
     * 定义Transform的作用域
     */
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        // 作用域为整个项目，可应用于app模块，不能应用于lib，否则会出现如下异常：
        // Transforms with scopes '[SUB_PROJECTS, EXTERNAL_LIBRARIES]' cannot be applied to library projects.
        // 如果想要在lib上使用可以设置为TransformManager.PROJECT_ONLY
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    /**
     * 是否支持增量
     */
    @Override
    public boolean isIncremental() {
        return true;
    }

    /**
     * Transform的执行主函数
     */
    @Override
    public void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);
        System.out.println("执行------RunTraceTransform------");
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        boolean isIncremental = transformInvocation.isIncremental();
        if (!isIncremental) {
            outputProvider.deleteAll();
        }
        // 遍历输入目录
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        for (TransformInput input : inputs) {
            // 遍历jar包，（Module和AAR等第三方引用，一般情况下会以classes.jar的方式存在）
            Collection<JarInput> jarInputs = input.getJarInputs();
            if (jarInputs != null && jarInputs.size() > 0) {
                for (JarInput jarInput : jarInputs) {
                    // 开启异步并发
                    waitableExecutor.execute(() -> {
                        traverseJarInput(jarInput, outputProvider, isIncremental);
                        return null;
                    });
                }
            }

            // 遍历dir目录，（一般情况下为当前引入插件的Module目录）
            Collection<DirectoryInput> directoryInputs = input.getDirectoryInputs();
            if (directoryInputs != null && directoryInputs.size() > 0) {
                for (DirectoryInput dirInput : directoryInputs) {
                    // 开启异步并发
                    waitableExecutor.execute(() -> {
                        traverseDirectoryInput(dirInput, outputProvider, isIncremental);
                        return null;
                    });
                }
            }
        }

        // 等待所有任务结束
        waitableExecutor.waitForTasksWithQuickFail(true);
    }

    /**
     * 遍历JarInput
     *
     * @param jarInput       待遍历数据
     * @param outputProvider 输出Provider
     * @param isIncremental  是否支持增量更新
     */
    private void traverseJarInput(JarInput jarInput,
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

        if (inputFile.getAbsolutePath().endsWith(".jar")) {
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
                        // 解析
                        try (InputStream is = jarFile.getInputStream(jarEntry)) {
                            analyzeClass(is);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

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

//    /**
//     * 遍历DirectoryInput
//     *
//     * @param directoryInputs 待遍历集合
//     * @param outputProvider  输出Provider
//     */
//    private void traverseDirectoryInput(Collection<DirectoryInput> directoryInputs,
//                                        TransformOutputProvider outputProvider) throws IOException {
//        if (directoryInputs != null && directoryInputs.size() > 0) {
//            for (DirectoryInput dirInput : directoryInputs) {
//                if (dirInput == null) {
//                    continue;
//                }
//                File inputFile = dirInput.getFile();
//                traverseDirectory(inputFile);
//                // 将处理之后的目录拷贝到输出目录
//                File outputFile = outputProvider.getContentLocation(
//                        dirInput.getName(), dirInput.getContentTypes(), dirInput.getScopes(), Format.DIRECTORY);
//                FileUtils.copyDirectory(inputFile, outputFile);
//            }
//        }
//    }

    /**
     * 遍历DirectoryInput
     *
     * @param dirInput       待遍历数据
     * @param outputProvider 输出Provider
     * @param isIncremental  是否支持增量更新
     */
    private void traverseDirectoryInput(DirectoryInput dirInput,
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
                    // 解析
                    try (FileInputStream is = new FileInputStream(file)) {
                        analyzeClass(is);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
     * ASM解析Class
     *
     * @param is 待处理Class文件输入流
     */
    private void analyzeClass(InputStream is) throws IOException {
        if (is != null && is.available() > 0) {
            ClassReader classReader = new ClassReader(is);
            ClassVisitor classVisitor = new AnalyzeClassVisitor(Opcodes.ASM7);
            classReader.accept(classVisitor, ClassReader.SKIP_FRAMES);
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
            ClassVisitor classVisitor = new GenerateClassVisitor(Opcodes.ASM7, classWriter);
            classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
            return classWriter.toByteArray();
        }
        return null;
    }

    private boolean checkClass(String className) {
        if (className.endsWith(".class")
                && !className.startsWith("androidx/")
                && !className.startsWith("android/")
                && !className.startsWith("com/google/android/material/")
                && !className.startsWith("cc/banzhi/runtrace_api/")) {
            int position = className.lastIndexOf("/");
            if (position > 0) {
                try {
                    className = className.substring(position + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return !className.startsWith("R$")
                    && !className.equals("R.class")
                    && !className.equals("BuildConfig.class");
        }
        return false;
    }
}
