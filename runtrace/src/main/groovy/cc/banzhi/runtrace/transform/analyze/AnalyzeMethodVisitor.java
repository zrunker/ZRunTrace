package cc.banzhi.runtrace.transform.analyze;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cc.banzhi.runtrace.transform.analyze.dto.AnalyzeMethodBean;
import cc.banzhi.runtrace.transform.cache.AnalyzeMethodCache;
import cc.banzhi.runtrace.transform.cache.Constants;

/**
 * @program: ZRunTrace
 * @description: 解析方法数据实例
 * @author: zoufengli01
 * @create: 2022/1/24 4:25 下午
 **/
public class AnalyzeMethodVisitor extends MethodVisitor {
    // 是否Trace
    private boolean isRunTrace;
    // 方法信息
    private final AnalyzeMethodBean analyzeMethodBean;

    public AnalyzeMethodVisitor(int api, MethodVisitor methodVisitor,
                                String className, int access, String name, String descriptor, boolean isRunTrace) {
        super(api, methodVisitor);
        this.isRunTrace = isRunTrace;
        this.analyzeMethodBean = new AnalyzeMethodBean(className, name, descriptor,
                access, (access & Opcodes.ACC_STATIC) != 0);
    }

    @Override
    public void visitEnd() {
        if (isRunTrace && analyzeMethodBean != null) {
            AnalyzeMethodCache.put(analyzeMethodBean.createKey(), analyzeMethodBean);
        }
        super.visitEnd();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (!isRunTrace) {
            isRunTrace = Constants.ANNOTATION_NAME.equals(descriptor);
        }
        if (isRunTrace && analyzeMethodBean != null) {
            return new MethodAnnotationVisitor(Opcodes.ASM7);
        }
        return super.visitAnnotation(descriptor, visible);
    }

    /**
     * 在visitAnnotation访问之后执行
     */
    @Override
    public void visitLocalVariable(String name, String descriptor, String signature,
                                   Label start, Label end, int index) {
        if (isRunTrace && analyzeMethodBean != null) {
            System.out.println("name = " + name + " descriptor = " + descriptor + " index = " + index);
            // 非静态方法第一个参数为this
            analyzeMethodBean.putVariableList(name, descriptor, index);
        }
        super.visitLocalVariable(name, descriptor, signature, start, end, index);
    }

    /**
     * 内部类解析方法注解
     */
    private class MethodAnnotationVisitor extends AnnotationVisitor {
        public MethodAnnotationVisitor(int api) {
            super(api);
        }

        @Override
        public void visit(String name, Object value) {
            super.visit(name, value);
            if (analyzeMethodBean != null) {
                analyzeMethodBean.putAnnotationMap(name, value);
            }
        }
    }
}
