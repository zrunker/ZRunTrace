package cc.banzhi.runtrace.transforms.lifecycle;

import org.gradle.api.Project;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cc.banzhi.runtrace.RunTraceExtension;

/**
 * @program: ZRunTrace
 * @description: Activity生命周期埋点
 * @author: zoufengli01
 * @create: 2022/4/12 4:30 下午
 **/
public class LifeClassVisitor extends ClassVisitor {
    // 类名称
    private String className;
    private boolean isExecute;
    private RunTraceExtension extension;

    public LifeClassVisitor(int api, ClassVisitor classVisitor, Project project) {
        super(api, classVisitor);
        extension = project.getExtensions().getByType(RunTraceExtension.class);
        System.out.println(extension.toString());
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
        // superName：androidx/appcompat/app/AppCompatActivity
        System.out.println("superName = " + superName);
        if (extension != null
                && extension.getLifeActivitySuperName() != null
                && !"".equals(extension.getLifeActivitySuperName())) {
            String lifeActivitySuperName = extension.getLifeActivitySuperName()
                    .replaceAll("\\.", "/");
            isExecute = superName != null &&
                    superName.endsWith(lifeActivitySuperName);
        } else {
            isExecute = "android/support/v4/app/FragmentActivity".equals(superName)
                    || "androidx/appcompat/app/AppCompatActivity".equals(superName);
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
                                     String signature, String[] exceptions) {
        if (isExecute) {
            return new LifeAMethodVisitor(Opcodes.ASM7,
                    cv.visitMethod(access, name, descriptor, signature, exceptions),
                    className, name, descriptor);
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

}
