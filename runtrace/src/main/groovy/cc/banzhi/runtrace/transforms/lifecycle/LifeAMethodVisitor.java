package cc.banzhi.runtrace.transforms.lifecycle;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @program: ZRunTrace
 * @description: Activity生命周期埋点
 * @author: zoufengli01
 * @create: 2022/3/30 6:01 下午
 **/
public class LifeAMethodVisitor extends MethodVisitor {
    private final String className;
    private final String name;
    private final String descriptor;

    public LifeAMethodVisitor(int api,
                              MethodVisitor methodVisitor,
                              String className,
                              String name,
                              String descriptor) {
        super(api, methodVisitor);
        this.className = className;
        this.name = name;
        this.descriptor = descriptor;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        if ("onRestart".equals(name) && "()V".equals(descriptor)) {
            mv.visitLdcInsn(className);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/RunTraceObserver",
                    "onARestart", "(Ljava/lang/String;)V", false);
        } else if ("onResume".equals(name) && "()V".equals(descriptor)) {
            mv.visitLdcInsn(className);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/RunTraceObserver",
                    "onAResume", "(Ljava/lang/String;)V", false);
        } else if ("onStart".equals(name) && "()V".equals(descriptor)) {
            mv.visitLdcInsn(className);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/RunTraceObserver",
                    "onAStart", "(Ljava/lang/String;)V", false);
        } else if ("onStop".equals(name) && "()V".equals(descriptor)) {
            mv.visitLdcInsn(className);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/RunTraceObserver",
                    "onAStop", "(Ljava/lang/String;)V", false);
        } else if ("onPause".equals(name) && "()V".equals(descriptor)) {
            mv.visitLdcInsn(className);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/RunTraceObserver",
                    "onAPause", "(Ljava/lang/String;)V", false);
        } else if ("onDestroy".equals(name) && "()V".equals(descriptor)) {
            mv.visitLdcInsn(className);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/RunTraceObserver",
                    "onADestroy", "(Ljava/lang/String;)V", false);
        } else if ("onNewIntent".equals(name) && "(Landroid/content/Intent;)V".equals(descriptor)) {
            mv.visitLdcInsn(className);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/RunTraceObserver",
                    "onANewIntent", "(Ljava/lang/String;Landroid/content/Intent;)V", false);
        } else if ("onCreate".equals(name) && "(Landroid/os/Bundle;)V".equals(descriptor)) {
            mv.visitLdcInsn(className);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/RunTraceObserver",
                    "onACreate", "(Ljava/lang/String;Landroid/os/Bundle;)V", false);
        }
    }
}
