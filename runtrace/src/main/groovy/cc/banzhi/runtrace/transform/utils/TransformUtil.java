package cc.banzhi.runtrace.transform.utils;

/**
 * @program: ZRunTrace
 * @description: Transform工具类
 * @author: zoufengli01
 * @create: 2022/1/28 11:14 上午
 **/
public class TransformUtil {

    // 非空判断
    public static boolean isNotEmpty(Object value) {
        return value != null && !"".equals(value);
    }

}
