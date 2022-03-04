package cc.banzhi.runtrace.transform.analyze;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;

import cc.banzhi.runtrace.transform.cache.Constants;
import cc.banzhi.runtrace.transform.utils.TransformUtil;

/**
 * @program: ZRunTrace
 * @description: 自定义解析ClassVisitor
 * @author: zoufengli01
 * @create: 2022/1/24 4:22 下午
 **/
public class AnalyzeClassVisitor extends ClassVisitor {
    // Class名称
    private String className;
    // 是否Trace
    private boolean isRunTrace;
    // 注解信息
    private HashMap<String, Object> annotationMap;

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
                className, access, name, descriptor, isRunTrace, annotationMap);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        this.isRunTrace = Constants.ANNOTATION_NAME.equals(descriptor);
        if (isRunTrace) {
            return new ClassAnnotationVisitor(Opcodes.ASM7);
        }
        return super.visitAnnotation(descriptor, visible);
    }

    /**
     * 内部类解析方法注解
     */
    private class ClassAnnotationVisitor extends AnnotationVisitor {
        public ClassAnnotationVisitor(int api) {
            super(api);
        }

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
            if (TransformUtil.isNotEmpty(name)
                    && TransformUtil.isNotEmpty(value)) {
                if (annotationMap == null) {
                    annotationMap = TransformUtil.createAnnotationMap(className);
                }
                annotationMap.put(name, value);
            }
        }
    }
}
