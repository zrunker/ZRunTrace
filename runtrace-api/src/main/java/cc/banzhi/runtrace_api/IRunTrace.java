package cc.banzhi.runtrace_api;

import java.util.HashMap;

/**
 * @program: ZRunTrace
 * @description: 监测接口
 * @author: zoufengli01
 * @create: 2022/1/25 3:29 下午
 **/
public interface IRunTrace {

    /**
     * 方法参数信息
     *
     * @param tag      日志TAG
     * @param level    日志等级
     * @param isUpload 是否上传
     * @param paramMap 参数信息
     */
    void runTrace(String tag, int level, boolean isUpload,
                  HashMap<String, Object> paramMap);

    /**
     * 方法运行时长
     *
     * @param tag      日志TAG
     * @param level    日志等级
     * @param isUpload 是否上传
     * @param time     总时长（ms）
     */
    void runTime(String tag, int level, boolean isUpload,
                 long time);
}
