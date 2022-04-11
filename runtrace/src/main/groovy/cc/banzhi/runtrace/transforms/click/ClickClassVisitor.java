package cc.banzhi.runtrace.transforms.click;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: ZRunTrace
 * @description: 点击事件埋点
 * @author: zoufengli01
 * @create: 2022/3/30 6:01 下午
 **/
public class ClickClassVisitor extends ClassVisitor {
    // 类名称
    private String className;
    private AtomicInteger mCounter = new AtomicInteger(0);

    public ClickClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version,
                      int access,
                      String name,
                      String signature,
                      String superName,
                      String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        if ("onClick(Landroid/view/View;)V".equals(name + descriptor)) {
            return new ClickMethodVisitor(Opcodes.ASM7,
                    cv.visitMethod(access, name, descriptor, signature, exceptions),
                    className);
        } else {
            return new ClickLambdaMethodVisitor(Opcodes.ASM7,
                    cv.visitMethod(access, name, descriptor, signature, exceptions),
                    className, cv, mCounter.incrementAndGet());
        }
    }
}
