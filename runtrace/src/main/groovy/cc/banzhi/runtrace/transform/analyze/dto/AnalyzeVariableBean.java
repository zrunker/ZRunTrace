package cc.banzhi.runtrace.transform.analyze.dto;

/**
 * @program: ZRunTrace
 * @description: 变量数据类
 * @author: zoufengli01
 * @create: 2022/1/24 10:38 下午
 **/
public class AnalyzeVariableBean {
    private final String name;
    private final String descriptor;
    private final int index;

    public AnalyzeVariableBean(String name, String descriptor, int index) {
        this.name = name;
        this.descriptor = descriptor;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public String toString() {
        return "AnalyzeVariableBean{" +
                "name='" + name + '\'' +
                ", descriptor='" + descriptor + '\'' +
                ", index=" + index +
                '}';
    }
}
