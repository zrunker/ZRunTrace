package cc.banzhi.runtrace.transform.analyze;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cc.banzhi.runtrace.transform.cache.Constants;

/**
 * @program: ZRunTrace
 * @description: 自定义解析ClassVisitor
 * @author: zoufengli01
 * @create: 2022/1/24 4:22 下午
 **/
public class AnalyzeClassVisitor extends ClassVisitor {
    // 是否Trace
    private boolean isRunTrace;
    // Class名称
    private String className;

    public AnalyzeClassVisitor(int api) {
        super(api);
    }

    @Override
    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        // name = "<init>" 构造时执行
        return new AnalyzeMethodVisitor(Opcodes.ASM7,
                super.visitMethod(access, name, descriptor, signature, exceptions),
                className, access, name, descriptor, isRunTrace);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        isRunTrace = Constants.ANNOTATION_NAME.equals(descriptor);
        // TODO 解析注解
        return super.visitAnnotation(descriptor, visible);
    }
}
