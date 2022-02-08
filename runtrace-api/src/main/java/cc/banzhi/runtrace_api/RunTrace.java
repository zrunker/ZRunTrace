package cc.banzhi.runtrace_api;

import android.util.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: ZRunTrace
 * @description: 自定义监测注解，埋点监听方法，打印日志，监听方法执行时长
 * @author: zoufengli01
 * @create: 2022/1/21 3:16 下午
 **/
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RunTrace {

    // Trace别名
    String aliasName() default "";

    // 额外参数
    String extras() default "";

    // 是否开启监听
    boolean enableObserver() default true;

    // 是否上传网络
    boolean enableUpload() default false;

    // 日志tag - 默认类名
    String tag() default "";

    // 日志级别
    @LogLevel int level() default Log.INFO;

    // 是否开启日志打印
    boolean enableLog() default false;

    // 是否开启统计方法执行时长
    boolean enableTime() default false;
}
