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
     * 创建AnnotationMap
     *
     * @param className 类名
     */
    public static HashMap<String, Object> createAnnotationMap(String className) {
        HashMap<String, Object> annotationMap = new HashMap<>();
        annotationMap.put("tag", className);
        annotationMap.put("level", 4);
        annotationMap.put("desc", null);
        annotationMap.put("extras", null);
        annotationMap.put("enableTime", false);
        annotationMap.put("enableUpload", false);
        return annotationMap;
    }

}
