package cc.banzhi.runtrace.transforms.code.cache;

import java.util.HashMap;
import java.util.Map;

import cc.banzhi.runtrace.transforms.code.analyze.dto.AnalyzeMethodBean;
import cc.banzhi.runtrace.transforms.code.utils.TransformUtil;

/**
 * @program: ZRunTrace
 * @description: 方法数据缓存类
 * @author: zoufengli01
 * @create: 2022/1/24 10:09 下午
 **/
public class AnalyzeMethodCache {
    private static final Map<String, AnalyzeMethodBean> methodMap = new HashMap<>();

    public static void put(String key, AnalyzeMethodBean data) {
        if (data != null && TransformUtil.isNotEmpty(key)) {
            methodMap.put(key, data);
        }
    }

    public static AnalyzeMethodBean get(String key) {
        if (TransformUtil.isNotEmpty(key)) {
            return methodMap.get(key);
        }
        return null;
    }

    // 生成缓存key
    public static String transKey(String className, String name, String descriptor) {
        return "className-" + className + ";name-" + name + ";descriptor-" + descriptor;
    }
}
