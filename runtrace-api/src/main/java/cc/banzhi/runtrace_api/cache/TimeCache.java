package cc.banzhi.runtrace_api.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: ZRunTrace
 * @description: 用于方法中耗时统计缓存，这样做的好处是外部可以随意定义自己想要的格式
 * @author: zoufengli01
 * @create: 2022/1/27 9:22 下午
 **/
public class TimeCache {
    private static final ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();

    public static void put(String key) {
        map.put(key, System.currentTimeMillis());
    }

    public static String get(String key, String methodName) {
        // 获取缓存时间
        long startTime = 0;
        if (map.containsKey(key)) {
            Long obj = map.get(key);
            startTime = obj == null ? 0 : obj;
            map.remove(key);
        }
        // 构造返回数据
        return "------------" + methodName + "---------------" +
               "\n执行耗时：" + (System.currentTimeMillis() - startTime) +
               "\n当前线程：" + Thread.currentThread().getName() +
               "\n------------" + methodName + "---------------";
    }

}
