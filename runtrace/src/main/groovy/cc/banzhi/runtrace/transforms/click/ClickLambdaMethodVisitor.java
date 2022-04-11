package cc.banzhi.runtrace.transforms.click;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.List;

/**
 * @program: ZRunTrace
 * @description: 点击事件埋点处理
 * @author: zoufengli01
 * @create: 2022/3/30 6:01 下午
 * https://opensource.sensorsdata.cn/opensource/asm-%E5%AE%9E%E7%8E%B0-hook-lambda-%E5%92%8C%E6%96%B9%E6%B3%95%E5%BC%95%E7%94%A8-%E6%95%B0%E6%8D%AE%E9%87%87%E9%9B%86/
 **/
public class ClickLambdaMethodVisitor extends MethodVisitor {
    private final String className;
    private final ClassVisitor cv;
    private final int count;

    public ClickLambdaMethodVisitor(int api,
                                    MethodVisitor methodVisitor,
                                    String className,
                                    ClassVisitor cv,
                                    int count) {
        super(api, methodVisitor);
        this.className = className;
        this.cv = cv;
        this.count = count;
    }

    /**
     * @param name                     = onClick
     * @param descriptor               = ()Landroid/view/View$OnClickListener;
     * @param bootstrapMethodHandle    引导方法是LambdaMetafacotry.metafactory
     *                                 = java/lang/invoke/LambdaMetafactory.metafactory(
     *                                 Ljava/lang/invoke/MethodHandles$Lookup;
     *                                 Ljava/lang/String;
     *                                 Ljava/lang/invoke/MethodType;
     *                                 Ljava/lang/invoke/MethodType; - 注释1. 抽象方法的签名描述信息
     *                                 Ljava/lang/invoke/MethodHandle; - 注释2. 方法句柄
     *                                 Ljava/lang/invoke/MethodType; - 注释1的具体实现
     *                                 )Ljava/lang/invoke/CallSite; (6)
     * @param bootstrapMethodArguments = [
     *                                 (Landroid/view/View;)V, - 方法参数描述符
     *                                 cc/banzhi/zruntrace/MainActivity.lambda$onCreate$0(Landroid/view/View;)V (6),
     *                                 (Landroid/view/View;)V - 实现方法参数描述符
     *                                 ]
     */
    @Override
    public void visitInvokeDynamicInsn(
            String name,
            String descriptor,
            Handle bootstrapMethodHandle,
            Object... bootstrapMethodArguments) {
        // name = onClick
        // descriptor = ()Landroid/view/View$OnClickListener;

        // bootstrapMethodHandle = java/lang/invoke/LambdaMetafactory.metafactory(
        // Ljava/lang/invoke/MethodHandles$Lookup;
        // Ljava/lang/String;
        // Ljava/lang/invoke/MethodType;
        // Ljava/lang/invoke/MethodType;
        // Ljava/lang/invoke/MethodHandle;
        // Ljava/lang/invoke/MethodType;
        // )Ljava/lang/invoke/CallSite; (6)

        // bootstrapMethodArguments = [(Landroid/view/View;)V,
        // cc/banzhi/zruntrace/MainActivity.lambda$onCreate$0(Landroid/view/View;)V (6),
        // (Landroid/view/View;)V]
        List<Object> argumentList = Arrays.asList(bootstrapMethodArguments);
        if ("onClick".equals(name)
                && "()Landroid/view/View$OnClickListener;".equals(descriptor)
                && argumentList.size() > 2) {
            // 函数式接口描述符，参数+返回值
            Type descriptorType = Type.getType(descriptor);
            // 实现方法参数描述符，(Landroid/view/View;)V
            Type methodImplType = (Type) argumentList.get(2);
//            // 方法参数描述符
//            Type methodType = (Type) argumentList.get(0);
//            // 方法描述描述符
//            String methodDesc = methodType.getDescriptor();

//            // 方法签名，方法名+参数+返回值，onClick(Landroid/view/View;)V
//            String methodNameAndDesc = name + methodDesc;

            // 函数式接口参数描述符
            Type[] types = descriptorType.getArgumentTypes();
            StringBuilder middleMethodDesc;
            if (types.length == 0) {
                middleMethodDesc = new StringBuilder(methodImplType.getDescriptor());
            } else {
                middleMethodDesc = new StringBuilder("(");
                for (Type item : types) {
                    middleMethodDesc.append(item.getDescriptor());
                }
                middleMethodDesc.append(
                        methodImplType.getDescriptor().replace("(", ""));
            }

            // 定义方法名：lambda$「外部方法名」$trace「增量」
            String middleMethodName = "lambda$" + name + "$trace" + count;
            // 获取方法Type
            Type middleMethodType = Type.getType(middleMethodDesc.toString());
            // 获取方法参数数组
            Type[] argumentTypes = middleMethodType.getArgumentTypes();

            // invokeDynamic原先的MethodHandle
            // cc/banzhi/zruntrace/MainActivity.lambda$onCreate$0(Landroid/view/View;)V
            Handle oldMethodHandle = (Handle) argumentList.get(1);
            // 要替换的MethodHandle，新的MethodHandle
            Handle newMethodHandle = new Handle(
                    Opcodes.H_INVOKESTATIC, className, middleMethodName, middleMethodDesc.toString(), false);
            argumentList.set(1, newMethodHandle);

            // 生成的中间方法
            MethodNode methodNode =
                    new MethodNode(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                            middleMethodName, middleMethodDesc.toString(), null, null);
            methodNode.visitCode();

            // 插入埋点监控，一定会有View参数
            methodNode.visitLdcInsn(className);
            if (argumentTypes.length > 0) {
                int index = 0;
                for (Type item : argumentTypes) {
                    if ("Landroid/view/View;".equals(item.getDescriptor())) {
                        methodNode.visitVarInsn(item.getOpcode(Opcodes.ILOAD), index);
                        break;
                    }
                    index += item.getSize();
                }
            }
            methodNode.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/banzhi/runtrace_api/RunTraceObserver",
                    "runClick", "(Ljava/lang/String;Landroid/view/View;)V", false);

            // 定义Opcode
            int opcode;
            switch (oldMethodHandle.getTag()) {
                case Opcodes.H_INVOKEINTERFACE:
                    opcode = Opcodes.INVOKEINTERFACE;
                    break;
                case Opcodes.H_INVOKESPECIAL:
                    opcode = Opcodes.INVOKESPECIAL;
                    break;
                case Opcodes.H_NEWINVOKESPECIAL:
                    methodNode.visitTypeInsn(Opcodes.NEW, oldMethodHandle.getOwner());
                    methodNode.visitInsn(Opcodes.DUP);
                    opcode = Opcodes.INVOKESPECIAL;
                    break;
                case Opcodes.H_INVOKESTATIC:
                    opcode = Opcodes.INVOKESTATIC;
                    break;
                case Opcodes.H_INVOKEVIRTUAL:
                    opcode = Opcodes.INVOKEVIRTUAL;
                    break;
                default:
                    opcode = 0;
                    break;
            }

            if (argumentTypes.length > 0) {
                int index = 0;
                for (Type item : argumentTypes) {
                    methodNode.visitVarInsn(item.getOpcode(Opcodes.ILOAD), index);
                    index += item.getSize();
                }
            }

            // 调用原来的lambda实现
            methodNode.visitMethodInsn(
                    opcode, oldMethodHandle.getOwner(), oldMethodHandle.getName(), oldMethodHandle.getDesc(), false);
            Type returnType = middleMethodType.getReturnType();
            int returnOpcodes = returnType.getOpcode(Opcodes.IRETURN);
            methodNode.visitInsn(returnOpcodes);
            methodNode.visitEnd();
            methodNode.accept(this.cv);

            super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, argumentList.toArray());
        } else {
            super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
        }
    }
}
