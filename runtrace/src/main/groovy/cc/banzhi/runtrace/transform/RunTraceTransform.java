package cc.banzhi.runtrace.transform;


import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;

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
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
        // 整个项目
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    /**
     * 是否支持增量
     */
    @Override
    public boolean isIncremental() {
        return false;
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
        if (!transformInvocation.isIncremental()) {
            outputProvider.deleteAll();
        }
        // 遍历输入目录
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        for (TransformInput input : inputs) {
            // 遍历jar包
            Collection<JarInput> jarInputs = input.getJarInputs();
            for (JarInput jarInput : jarInputs) {
                File inputFile = jarInput.getFile();
                File outputFile = outputProvider.getContentLocation(
                        jarInput.getName(), jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                FileUtils.copyFile(inputFile, outputFile);
            }

            // 遍历dir目录 - 一般情况下都是处理dir目录，而非jar
            Collection<DirectoryInput> directoryInputs = input.getDirectoryInputs();
            traverseDirectoryInput(directoryInputs, outputProvider);
        }
    }

//    /**
//     * 遍历JarInput
//     *
//     * @param jarInputs 待遍历集合
//     */
//    private void traverseJarInput(Collection<JarInput> jarInputs)
//            throws IOException {
//        if (jarInputs != null && jarInputs.size() > 0) {
//            for (JarInput jarInput : jarInputs) {
//                JarFile jarFile = new JarFile(jarInput.getFile());
//                // 解析jarFile
//                Enumeration<JarEntry> enumerations = jarFile.entries();
//                while (enumerations.hasMoreElements()) {
//                    JarEntry jarEntry = enumerations.nextElement();
//                    String entryName = jarEntry.getName();
//                    // 处理Class文件
//                    if (checkClass(entryName)) {
//                        InputStream is = jarFile.getInputStream(jarEntry);
//                        analyzeClass(is);
//                    }
//                }
//            }
//        }
//    }

    /**
     * 遍历DirectoryInput
     *
     * @param directoryInputs 待遍历集合
     * @param outputProvider  输出Provider
     */
    private void traverseDirectoryInput(Collection<DirectoryInput> directoryInputs,
                                        TransformOutputProvider outputProvider) throws IOException {
        for (DirectoryInput dirInput : directoryInputs) {
            File inputFile = dirInput.getFile();
            traverseFile(inputFile);
            // 将处理之后的目录拷贝到输出目录
            File outputFile = outputProvider.getContentLocation(
                    dirInput.getName(), dirInput.getContentTypes(), dirInput.getScopes(), Format.DIRECTORY);
            FileUtils.copyDirectory(inputFile, outputFile);
        }
    }

    /**
     * 递归遍历文件
     *
     * @param file 待处理数据
     */
    private void traverseFile(File file) {
        if (file != null && file.exists()) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (checkClass(fileName)) {
                    // 解析
                    analyzeClass(file);
                    // 生成
                    generateClass(file);
                }
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File item : files) {
                        traverseFile(item);
                    }
                }
            }
        }
    }

    /**
     * 解析Class
     *
     * @param file 待处理Class文件
     */
    private void analyzeClass(File file) {
        if (file != null && file.exists()) {
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
                ClassReader classReader = new ClassReader(is);
                ClassVisitor classVisitor = new AnalyzeClassVisitor(Opcodes.ASM7);
                classReader.accept(classVisitor, ClassReader.SKIP_FRAMES);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 生成Class
     *
     * @param file 待处理Class文件
     */
    private void generateClass(File file) {
        if (file != null && file.exists()) {
            FileInputStream is = null;
            FileOutputStream os = null;
            try {
                is = new FileInputStream(file);
                ClassReader classReader = new ClassReader(is);
                ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
                ClassVisitor classVisitor = new GenerateClassVisitor(Opcodes.ASM7, classWriter);
                classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES);
                byte[] bytes = classWriter.toByteArray();
                os = new FileOutputStream(file.getAbsolutePath());
                os.write(bytes);
                os.flush();
            } catch (Exception e) {
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
    }

    private boolean checkClass(String name) {
        return name.endsWith(".class")
                && !name.startsWith("R\\$")
                && !name.equals("R.class")
                && !name.equals("BuildConfig.class");
    }
}
