package cc.banzhi.runtrace.transform.analyze.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import cc.banzhi.runtrace.transform.cache.AnalyzeMethodCache;

/**
 * @program: ZRunTrace
 * @description: 方法数据类
 * @author: zoufengli01
 * @create: 2022/1/24 9:31 下午
 **/
public class AnalyzeMethodBean {
    private final String className;
    private final String name;
    private final String descriptor;
    private final int access;
    private final boolean isStatic;
    // 注解
    private final HashMap<String, Object> annotationMap;
    // 变量
    private final ArrayList<AnalyzeVariableBean> variableList;

    public AnalyzeMethodBean(String className, String name,
                             String descriptor, int access, boolean isStatic) {
        this.className = className;
        this.name = name;
        this.descriptor = descriptor;
        this.access = access;
        this.isStatic = isStatic;
        // 设置注解
        this.annotationMap = new HashMap<>();
        this.annotationMap.put("tag", className);
        this.annotationMap.put("level", 4);
        this.annotationMap.put("desc", null);
        this.annotationMap.put("source", null);
        this.annotationMap.put("extras", null);
        this.annotationMap.put("enableTime", false);
        this.annotationMap.put("enableUpload", false);
        // 初始化变量集合
        this.variableList = new ArrayList<>();
    }

    // 生成缓存key
    public String createKey() {
        return AnalyzeMethodCache.transKey(className, name, descriptor);
    }

    public void putAnnotationMap(String key, Object obj) {
        if (key != null && !"".equals(key)
                && obj != null && !"".equals(obj)) {
            this.annotationMap.put(key, obj);
        }
    }

    public void putVariableList(String name, String descriptor, int index) {
        if (name != null && !"".equals(name)
                && descriptor != null && !"".equals(descriptor) && index >= 0) {
            AnalyzeVariableBean data = new AnalyzeVariableBean(name, descriptor, index);
            this.variableList.add(data);
        }
    }

    public ArrayList<AnalyzeVariableBean> getVariableList() {
        variableList.sort(new Comparator<AnalyzeVariableBean>() {
            @Override
            public int compare(AnalyzeVariableBean t0, AnalyzeVariableBean t1) {
                return t0.getIndex() - t1.getIndex();
            }
        });
        return variableList;
    }

    public HashMap<String, Object> getAnnotationMap() {
        return annotationMap;
    }

    public String getName() {
        return name;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public String getClassName() {
        return className;
    }

    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public String toString() {
        return "AnalyzeMethodBean{" +
                "className='" + className + '\'' +
                ", name='" + name + '\'' +
                ", descriptor='" + descriptor + '\'' +
                ", access=" + access +
                ", isStatic=" + isStatic +
                ", annotationMap=" + annotationMap +
                ", variableList=" + variableList +
                '}';
    }
}
