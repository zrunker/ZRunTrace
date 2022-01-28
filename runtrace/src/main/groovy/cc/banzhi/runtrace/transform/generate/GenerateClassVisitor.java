package cc.banzhi.runtrace.transform.generate;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cc.banzhi.runtrace.transform.analyze.dto.AnalyzeMethodBean;
import cc.banzhi.runtrace.transform.cache.AnalyzeMethodCache;

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
        String key = AnalyzeMethodCache.transKey(className, name, descriptor);
        AnalyzeMethodBean analyzeMethodBean = AnalyzeMethodCache.get(key);
        if (analyzeMethodBean != null) {
            System.out.println(analyzeMethodBean.toString());
            return new GenerateMethodVisitor(Opcodes.ASM7,
                    cv.visitMethod(access, name, descriptor, signature, exceptions), analyzeMethodBean);
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
