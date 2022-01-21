package cc.banzhi.runtrace.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: ZRunTrace
 * @description: 自定义跟踪注解
 * @author: zoufengli01
 * @create: 2022/1/21 3:16 下午
 **/
@Target(ElementType.METHOD)
public @interface RunTrace {
    String tag() default "Trace";
    String desc() default "";
}
