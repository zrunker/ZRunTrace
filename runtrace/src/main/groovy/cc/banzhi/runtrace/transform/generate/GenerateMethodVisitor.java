package cc.banzhi.runtrace.transform.generate;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @program: ZRunTrace
 * @description: 生成MethodVisitor
 * @author: zoufengli01
 * @create: 2022/1/24 10:56 下午
 **/
public class GenerateMethodVisitor extends MethodVisitor {
    private String className;
    private boolean isRunTrace;

    public GenerateMethodVisitor(int api, MethodVisitor methodVisitor, String className) {
        super(api, methodVisitor);
        this.className = className;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        if (isRunTrace) {
            generateLog();
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        isRunTrace = "Lcc/banzhi/runtrace_api/RunTrace;".equals(descriptor);
        return super.visitAnnotation(descriptor, visible);
    }

    /**
     * 生成日志
     */
    private void generateLog() {
        mv.visitLdcInsn("" + className);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/utils/DateUtil", "getCurrentTime", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(Opcodes.POP);
    }

}
