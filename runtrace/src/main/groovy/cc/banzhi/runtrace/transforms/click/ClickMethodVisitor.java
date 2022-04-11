package cc.banzhi.runtrace.transforms.click;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @program: ZRunTrace
 * @description: 点击事件埋点处理
 * @author: zoufengli01
 * @create: 2022/3/30 6:01 下午
 **/
public class ClickMethodVisitor extends MethodVisitor {
    private final String className;

    public ClickMethodVisitor(int api,
                              MethodVisitor methodVisitor,
                              String className) {
        super(api, methodVisitor);
        this.className = className;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        mv.visitLdcInsn(className);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/RunTraceObserver",
                "runClick", "(Ljava/lang/String;Landroid/view/View;)V", false);
    }
}
