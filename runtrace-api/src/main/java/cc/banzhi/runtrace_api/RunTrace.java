package cc.banzhi.runtrace_api;

import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * @program: ZRunTrace
 * @description: 自定义跟踪注解
 * @author: zoufengli01
 * @create: 2022/1/21 3:16 下午
 **/
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface RunTrace {
    // 日志tag - 默认类名
    String tag() default "Trace";

    // 日志级别
    int level() default Log.INFO;

    // 描述信息
    String desc() default "";

    // 额外参数
    String extras() default "";

    // 是否开启统计方法执行时间
    boolean enableTime() default false;

    // 是否上传网络
    boolean enableUpload() default false;
}
