package cc.banzhi.runtrace.transform.generate;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @program: ZRunTrace
 * @description: 生成ClassVisitor
 * @author: zoufengli01
 * @create: 2022/1/24 10:54 下午
 **/
public class GenerateClassVisitor extends ClassVisitor {
    // 类名称
    private String className;

    public GenerateClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        return new GenerateMethodVisitor(Opcodes.ASM7,
                cv.visitMethod(access, name, descriptor, signature, exceptions), className);
    }
}
