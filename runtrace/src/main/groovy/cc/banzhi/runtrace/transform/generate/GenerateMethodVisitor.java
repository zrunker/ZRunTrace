package cc.banzhi.runtrace.transform.generate;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.HashMap;

import cc.banzhi.runtrace.transform.analyze.dto.AnalyzeMethodBean;
import cc.banzhi.runtrace.transform.analyze.dto.AnalyzeVariableBean;

/**
 * @program: ZRunTrace
 * @description: 生成MethodVisitor
 * @author: zoufengli01
 * @create: 2022/1/24 10:56 下午
 **/
public class GenerateMethodVisitor extends MethodVisitor {
    private final AnalyzeMethodBean analyzeMethodBean;
    private HashMap<String, Object> annotationMap;
    // 日志Tag
    private Object tag;
    // 是否静态
    private boolean isStatic;
    // 定义统计开始时间-局部变量表位置
    private int startTimeIndex;

    private boolean isNotEmpty(Object value) {
        return value != null && !"".equals(value);
    }

    public GenerateMethodVisitor(int api, MethodVisitor methodVisitor,
                                 AnalyzeMethodBean analyzeMethodBean) {
        super(api, methodVisitor);
        this.analyzeMethodBean = analyzeMethodBean;
        if (analyzeMethodBean != null) {
            System.out.println(analyzeMethodBean.toString());
            this.annotationMap = analyzeMethodBean.getAnnotationMap();
            this.tag = annotationMap.get("tag");
            if (!isNotEmpty(this.tag)) {
                this.tag = analyzeMethodBean.getClassName();
            }
            // 非静态方法第一个参数为this
            this.isStatic = analyzeMethodBean.isStatic();
        }
    }

    @Override
    public void visitCode() {
        super.visitCode();
        if (analyzeMethodBean != null && annotationMap != null) {
            generateDesc(annotationMap.get("desc"));

            ArrayList<AnalyzeVariableBean> variableList = analyzeMethodBean.getVariableList();
            generateLog(variableList);

            boolean enableTime = (boolean) annotationMap.get("enableTime");
            if (enableTime) {
                int offset = isStatic ? 0 : -1;
                startTimeIndex = variableList.size() + 2 + offset;
                generateTimeStart(startTimeIndex);
            }
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN
                && analyzeMethodBean != null && annotationMap != null) {
            boolean enableTime = (boolean) annotationMap.get("enableTime");
            if (enableTime) {
                generateTimeEnd(analyzeMethodBean.getName(), startTimeIndex);
            }
        }
        super.visitInsn(opcode);
    }

    /**
     * 生成描述信息
     *
     * @param desc 描述信息
     */
    private void generateDesc(Object desc) {
        if (isNotEmpty(desc)) {
            mv.visitLdcInsn(tag);
            mv.visitLdcInsn(desc);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log",
                    "i", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mv.visitInsn(Opcodes.POP);
        }
    }

    /**
     * 生成参数日志
     *
     * @param variableList 参数列表
     */
    private void generateLog(ArrayList<AnalyzeVariableBean> variableList) {
        if (variableList != null && variableList.size() > 0) {
            int offset = isStatic ? 0 : -1;
            int index = variableList.size() + 1 + offset;

            mv.visitTypeInsn(Opcodes.NEW, "java/util/HashMap");
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashMap",
                    "<init>", "()V", false);
            mv.visitVarInsn(Opcodes.ASTORE, index);

            mv.visitVarInsn(Opcodes.ALOAD, index);
            mv.visitLdcInsn("source");
            mv.visitLdcInsn(analyzeMethodBean.getClassName() + "#"
                    + analyzeMethodBean.getName() + analyzeMethodBean.getDescriptor());
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(Opcodes.POP);

            Object extras = annotationMap.get("extras");
            if (isNotEmpty(extras)) {
                mv.visitVarInsn(Opcodes.ALOAD, index);
                mv.visitLdcInsn("extras");
                mv.visitLdcInsn(extras);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put",
                        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
                mv.visitInsn(Opcodes.POP);
            }

//            mv.visitVarInsn(Opcodes.ALOAD, index);
//            mv.visitLdcInsn("desc");
//            mv.visitLdcInsn(annotationMap.get("desc"));
//            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put",
//                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
//            mv.visitInsn(Opcodes.POP);

            for (int i = 0; i < variableList.size(); i++) {
                AnalyzeVariableBean item = variableList.get(i);
                String name = item.getName();
                if (!"this".equals(name)) {
                    mv.visitVarInsn(Opcodes.ALOAD, index);
                    mv.visitLdcInsn(name);
                    String descriptor = item.getDescriptor();
                    int opCode = Type.getType(descriptor).getOpcode(Opcodes.ILOAD);
                    mv.visitVarInsn(opCode, item.getIndex());
                    if (opCode != Opcodes.ALOAD) {
                        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/TypeUtil",
                                "toObj", "(" + descriptor + ")Ljava/lang/Object;", false);
                    }
                    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put",
                            "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
                    mv.visitInsn(Opcodes.POP);
                }
            }

            mv.visitLdcInsn(tag);
            mv.visitInsn((Integer) annotationMap.get("level") + 1);
            boolean enableUpload = (boolean) annotationMap.get("enableUpload");
            mv.visitInsn(enableUpload ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
            mv.visitVarInsn(Opcodes.ALOAD, index);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/RunTraceUtil",
                    "runTrace", "(Ljava/lang/String;IZLjava/util/HashMap;)V", false);
        }
    }

    /**
     * 生成执行时间统计-开始时间
     *
     * @param index 操作数栈索引位置
     */
    private void generateTimeStart(int index) {
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
                "currentTimeMillis", "()J", false);
        mv.visitVarInsn(Opcodes.LSTORE, index);
    }

    /**
     * 生成执行时间统计-总时长
     *
     * @param methodName     方法名
     * @param startTimeIndex 开始时间操作数栈索引位置
     */
    private void generateTimeEnd(String methodName, int startTimeIndex) {
        if (tag != null && methodName != null) {
            mv.visitLdcInsn(tag);
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder",
                    "<init>", "()V", false);
            mv.visitLdcInsn(methodName + " \u6267\u884c\u603b\u65f6\u957f\uff08ms\uff09\uff1a");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                    "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
                    "currentTimeMillis", "()J", false);
            mv.visitVarInsn(Opcodes.LLOAD, startTimeIndex);
            mv.visitInsn(Opcodes.LSUB);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                    "(J)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
                    "()Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i",
                    "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mv.visitInsn(Opcodes.POP);
        }
    }
}
