package cc.banzhi.runtrace_api.code;

import java.util.HashMap;

import cc.banzhi.runtrace_api.IRunTrace;

/**
 * @program: ZRunTrace
 * @description:
 * @author: zoufengli01
 * @create: 2022/3/30 4:38 下午
 **/
public interface ICodeTrace extends IRunTrace {

    /**
     * 基于@RunTrace注解，监听方法参数信息
     *
     * @param tag      日志TAG
     * @param level    日志等级
     * @param isUpload 是否上传
     * @param paramMap 参数信息
     */
    void runTrace(String tag, int level, boolean isUpload,
                  HashMap<String, Object> paramMap);

    /**
     * 基于@RunTrace注解，监听方法运行时长
     *
     * @param tag      日志TAG
     * @param level    日志等级
     * @param isUpload 是否上传
     * @param time     总时长（ms）
     */
    void runTime(String tag, int level, boolean isUpload,
                 long time);
}
