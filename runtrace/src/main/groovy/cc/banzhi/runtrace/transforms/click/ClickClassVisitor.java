package cc.banzhi.runtrace.transforms.click;

import org.objectweb.asm.ClassVisitor;

/**
 * @program: ZRunTrace
 * @description:
 * @author: zoufengli01
 * @create: 2022/3/30 6:01 下午
 **/
public class ClickClassVisitor extends ClassVisitor {

    public ClickClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }
}
