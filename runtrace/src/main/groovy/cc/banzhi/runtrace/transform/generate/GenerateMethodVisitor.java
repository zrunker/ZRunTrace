package cc.banzhi.runtrace.transform.generate;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.HashMap;

import cc.banzhi.runtrace.transform.analyze.dto.AnalyzeMethodBean;
import cc.banzhi.runtrace.transform.analyze.dto.AnalyzeVariableBean;
import cc.banzhi.runtrace.transform.utils.TransformUtil;

/**
 * @program: ZRunTrace
 * @description: 生成MethodVisitor
 * @author: zoufengli01
 * @create: 2022/1/24 10:56 下午
 **/
public class GenerateMethodVisitor extends MethodVisitor {
    private final AnalyzeMethodBean analyzeMethodBean;
    // 注解信息
    private HashMap<String, Object> annotationMap;
    // 局部变量信息
    private ArrayList<AnalyzeVariableBean> variableList;

    // 日志Tag
    private Object tag;
    // 日志等级
    private int level;
    // 是否开启日志打印
    private boolean enableLog = true;
    // 是否开启回调
    private boolean enableObserver = true;

    // 是否为静态方法
    private boolean isStatic;
    // 方法参数集合（不包括非静态方法中的this）
    private Type[] argumentArrays;
    // 局部变量表中数量/位置
    private int localVarPosition;

    // 统计时间【局部变量表】开始位置
    private int startTimeIndex;

    public GenerateMethodVisitor(int api, MethodVisitor methodVisitor,
                                 AnalyzeMethodBean analyzeMethodBean) {
        super(api, methodVisitor);
        this.analyzeMethodBean = analyzeMethodBean;
        if (analyzeMethodBean != null) {
            System.out.println(analyzeMethodBean.toString());
            this.annotationMap = analyzeMethodBean.getAnnotationMap();
            this.tag = annotationMap.get("tag");
            if (!TransformUtil.isNotEmpty(tag)) {
                this.tag = TransformUtil.formatClassName(analyzeMethodBean.getClassName());
            }
            Object levelObj = annotationMap.get("level");
            if (TransformUtil.isNotEmpty(levelObj)) {
                this.level = (int) levelObj;
            }
            Object enableLogObj = annotationMap.get("enableLog");
            if (TransformUtil.isNotEmpty(enableLogObj)) {
                this.enableLog = (boolean) enableLogObj;
            }
            Object enableObserverObj = annotationMap.get("enableObserver");
            if (TransformUtil.isNotEmpty(enableObserverObj)) {
                this.enableObserver = (boolean) enableObserverObj;
            }
            this.variableList = analyzeMethodBean.getVariableList();
            if (variableList != null && variableList.size() > 0) {
                AnalyzeVariableBean lastVariableBean = variableList.get(variableList.size() - 1);
                if (lastVariableBean != null) {
                    this.localVarPosition = lastVariableBean.getIndex() + 1;
                } else {
                    this.localVarPosition = variableList.size();
                }
            }
            this.isStatic = analyzeMethodBean.isStatic();
            this.argumentArrays = Type.getArgumentTypes(analyzeMethodBean.getDescriptor());
        }
    }

    @Override
    public void visitCode() {
        super.visitCode();
        if (analyzeMethodBean != null && annotationMap != null) {
            // 监测参数
            generateLog();
            // 监测执行时长
            boolean enableTime = (boolean) annotationMap.get("enableTime");
            if (enableTime) {
                startTimeIndex = localVarPosition + 1;
                generateTimeStart();
            }
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN
                && analyzeMethodBean != null
                && annotationMap != null) {
            boolean enableTime = (boolean) annotationMap.get("enableTime");
            if (enableTime) {
                generateTimeEnd();
            }
        }
        super.visitInsn(opcode);
    }

    /**
     * 生成参数日志
     */
    private void generateLog() {
        if (tag != null && analyzeMethodBean != null && annotationMap != null
                && argumentArrays != null && (enableLog || enableObserver)) {
            mv.visitTypeInsn(Opcodes.NEW, "java/util/HashMap");
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashMap",
                    "<init>", "()V", false);
            mv.visitVarInsn(Opcodes.ASTORE, localVarPosition);

            mv.visitVarInsn(Opcodes.ALOAD, localVarPosition);
            mv.visitLdcInsn("traceSource");
            mv.visitLdcInsn(TransformUtil.formatClassName(analyzeMethodBean.getClassName()) + "#"
                    + analyzeMethodBean.getName() + analyzeMethodBean.getDescriptor());
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(Opcodes.POP);

            mv.visitVarInsn(Opcodes.ALOAD, localVarPosition);
            mv.visitLdcInsn("traceTime");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/utils/DateUtil",
                    "getCurrentTime", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put",
                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
            mv.visitInsn(Opcodes.POP);

            Object aliasName = annotationMap.get("aliasName");
            if (TransformUtil.isNotEmpty(aliasName)) {
                mv.visitVarInsn(Opcodes.ALOAD, localVarPosition);
                mv.visitLdcInsn("aliasName");
                mv.visitLdcInsn(aliasName);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put",
                        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
                mv.visitInsn(Opcodes.POP);
            }

            Object extras = annotationMap.get("extras");
            if (TransformUtil.isNotEmpty(extras)) {
                mv.visitVarInsn(Opcodes.ALOAD, localVarPosition);
                mv.visitLdcInsn("extras");
                mv.visitLdcInsn(extras);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put",
                        "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
                mv.visitInsn(Opcodes.POP);
            }

            // 方法参数的个数，非静态方法第一个参数为this
            int offset = isStatic ? 0 : 1;
            int paramSize = argumentArrays.length + offset;
            if (variableList != null && variableList.size() >= paramSize) {
                for (int i = 0; i < paramSize; i++) {
                    AnalyzeVariableBean item = variableList.get(i);
                    String name = item.getName();
                    String descriptor = item.getDescriptor();
                    for (Type type : argumentArrays) {
                        if (descriptor.equals(type.getDescriptor())
                                && !"this".equals(name)) {
                            mv.visitVarInsn(Opcodes.ALOAD, localVarPosition);
                            mv.visitLdcInsn(name);
                            int opCode = Type.getType(descriptor).getOpcode(Opcodes.ILOAD);
                            mv.visitVarInsn(opCode, item.getIndex());
                            if (opCode != Opcodes.ALOAD) {
                                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/String",
                                        "valueOf", "(" + descriptor + ")Ljava/lang/String;", false);
                            }
                            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "put",
                                    "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
                            mv.visitInsn(Opcodes.POP);
                            break;
                        }
                    }
                }
            }

            // 日志
            if (enableLog) {
                mv.visitLdcInsn(tag);
                mv.visitVarInsn(Opcodes.ALOAD, localVarPosition);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/HashMap", "toString",
                        "()Ljava/lang/String;", false);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", TransformUtil.getLogFunByLevel(level),
                        "(Ljava/lang/String;Ljava/lang/String;)I", false);
                mv.visitInsn(Opcodes.POP);
            }

            // 回调
            if (enableObserver) {
                mv.visitLdcInsn(tag);
                mv.visitIntInsn(Opcodes.BIPUSH, level);
                boolean enableUpload = (boolean) annotationMap.get("enableUpload");
                mv.visitInsn(enableUpload ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
                mv.visitVarInsn(Opcodes.ALOAD, localVarPosition);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/RunTraceObserver",
                        "runTrace", "(Ljava/lang/String;IZLjava/util/HashMap;)V", false);
            }
        }
    }

    /**
     * 生成执行时间统计-开始时间
     */
    private void generateTimeStart() {
        if (analyzeMethodBean != null && (enableLog || enableObserver)) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
                    "currentTimeMillis", "()J", false);
            mv.visitVarInsn(Opcodes.LSTORE, startTimeIndex);
        }
    }

    /**
     * 生成执行时间统计-总时长
     */
    private void generateTimeEnd() {
        if (tag != null && analyzeMethodBean != null && (enableLog || enableObserver)) {
            if (enableLog) {
                mv.visitLdcInsn(tag);
                mv.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
                mv.visitInsn(Opcodes.DUP);
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder",
                        "<init>", "()V", false);
                mv.visitLdcInsn("\n\u7edf\u8ba1\u65b9\u6cd5\u6267\u884c\u65f6\u957f\uff1a\n------------"
                        + analyzeMethodBean.getName() + "---------------\n\u6267\u884c\u8017\u65f6\uff1a");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis",
                        "()J", false);
                mv.visitVarInsn(Opcodes.LLOAD, startTimeIndex);
                mv.visitInsn(Opcodes.LSUB);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                        "(J)Ljava/lang/StringBuilder;", false);
                mv.visitLdcInsn("\n\u5f53\u524d\u7ebf\u7a0b\uff1a");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Thread", "currentThread",
                        "()Ljava/lang/Thread;", false);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", "getName",
                        "()Ljava/lang/String;", false);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitLdcInsn("\n------------" + analyzeMethodBean.getName() + "---------------");
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append",
                        "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString",
                        "()Ljava/lang/String;", false);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", TransformUtil.getLogFunByLevel(level),
                        "(Ljava/lang/String;Ljava/lang/String;)I", false);
                mv.visitInsn(Opcodes.POP);
            }

            // 回调
            if (enableObserver) {
                mv.visitLdcInsn(tag);
                mv.visitIntInsn(Opcodes.BIPUSH, level);
                boolean enableUpload = (boolean) annotationMap.get("enableUpload");
                mv.visitInsn(enableUpload ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis",
                        "()J", false);
                mv.visitVarInsn(Opcodes.LLOAD, startTimeIndex);
                mv.visitInsn(Opcodes.LSUB);
                mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/RunTraceObserver",
                        "runTime", "(Ljava/lang/String;IZJ)V", false);
            }
        }
    }
}
