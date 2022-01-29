package cc.banzhi.runtrace_api;

import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: ZRunTrace
 * @description: 自定义跟踪注解
 * @author: zoufengli01
 * @create: 2022/1/21 3:16 下午
 **/
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RunTrace {
    // 日志tag - 默认类名
    String tag() default "";

    // 日志级别
    int level() default Log.INFO;

    // 是否开启日志打印
    boolean enableLog() default true;

    // 是否开启监听
    boolean enableObserver() default true;

    // 额外参数
    String extras() default "";

    // 别名
    String aliasName() default "";

    // 是否上传网络
    boolean enableUpload() default false;

    // 是否开启统计方法执行时长
    boolean enableTime() default false;
}
