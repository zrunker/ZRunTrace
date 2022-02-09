package cc.banzhi.runtrace.transform.utils;

import java.util.HashMap;

/**
 * @program: ZRunTrace
 * @description: Transform工具类
 * @author: zoufengli01
 * @create: 2022/1/28 11:14 上午
 **/
public class TransformUtil {

    /**
     * 非空判断
     *
     * @param value 待处理数据
     */
    public static boolean isNotEmpty(Object value) {
        return value != null && !"".equals(value);
    }

    /**
     * 创建AnnotationMap（注解），取决于RunTrace注解默认值
     *
     * @param className 类名
     */
    public static HashMap<String, Object> createAnnotationMap(String className) {
        HashMap<String, Object> annotationMap = new HashMap<>();
        annotationMap.put("extras", null);
        annotationMap.put("aliasName", null);
        annotationMap.put("enableObserver", true);
        annotationMap.put("enableUpload", false);
        annotationMap.put("tag", className);
        annotationMap.put("level", 4);
        annotationMap.put("enableLog", false);
        annotationMap.put("enableTime", false);
        return annotationMap;
    }

    /**
     * 根据日志等级查询方法名称
     *
     * @param level 日志等级
     */
    public static String getLogFunByLevel(int level) {
        String fun;
        switch (level) {
            case 2:
                fun = "v";
                break;
            case 3:
                fun = "d";
                break;
            case 5:
                fun = "w";
                break;
            case 6:
                fun = "e";
                break;
            case 4:
            default:
                fun = "i";
                break;
        }
        return fun;
    }

}
