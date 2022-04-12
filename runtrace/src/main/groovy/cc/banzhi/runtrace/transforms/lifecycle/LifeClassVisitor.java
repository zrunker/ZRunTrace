package cc.banzhi.runtrace.transforms.lifecycle;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @program: ZRunTrace
 * @description: Activity生命周期埋点
 * @author: zoufengli01
 * @create: 2022/4/12 4:30 下午
 **/
public class LifeClassVisitor extends ClassVisitor {
    // 类名称
    private String className;
    private String superName;

    public LifeClassVisitor(int api, ClassVisitor classVisitor) {
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
        this.superName = superName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        if ("android/support/v4/app/FragmentActivity".equals(superName)
                || "androidx/appcompat/app/AppCompatActivity".equals(superName)) {
            return new LifeAMethodVisitor(Opcodes.ASM7,
                    cv.visitMethod(access, name, descriptor, signature, exceptions),
                    className, name, descriptor);
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
