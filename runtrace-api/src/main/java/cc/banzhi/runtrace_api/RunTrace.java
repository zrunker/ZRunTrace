package cc.banzhi.runtrace_api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: ZRunTrace
 * @description: 自定义监测注解
 * @author: zoufengli01
 * @create: 2022/1/21 3:16 下午
 **/
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface RunTrace {

}
