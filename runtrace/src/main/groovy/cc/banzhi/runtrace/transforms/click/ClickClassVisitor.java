package cc.banzhi.runtrace.transforms.click;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;

/**
 * @program: ZRunTrace
 * @description:
 * @author: zoufengli01
 * @create: 2022/3/30 6:01 下午
 **/
public class ClickClassVisitor extends ClassVisitor {
    // 类名称
    private String className;
    private String[] interfaces;

    public ClickClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
        this.interfaces = interfaces;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        if (interfaces != null && interfaces.length > 0) {
            // 根据方法名+方法描述判断是否需要修改方法体；
            // 例如，当前遍历到View的onClick方法时，name是onClick，desc是(Landroid/view/View;)V；
            // 则满足修改条件onClick(Landroid/view/View;)V
            // 当第一个条件满足后，还需进一步判断当前类是否实现了View$OnClickListener接口
//            if ("onClick(Landroid/view/View;)V".equals(name + descriptor)
//                    && Arrays.asList(interfaces).contains("android/view/View$OnClickListener")) {
                return new ClickMethodVisitor(Opcodes.ASM7,
                        cv.visitMethod(access, name, descriptor, signature, exceptions),
                        className, cv);
//            }
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
