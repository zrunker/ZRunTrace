package cc.banzhi.runtrace_api;

import cc.banzhi.runtrace_api.click.IClickTrace;
import cc.banzhi.runtrace_api.code.ICodeTrace;
import cc.banzhi.runtrace_api.lifecycle.ILifeATrace;

/**
 * @program: ZRunTrace
 * @description: 对外监测
 * @author: zoufengli01
 * @create: 2022/4/11 4:49 下午
 **/
public class RunTrace {
    public static void addClickTrace(IClickTrace clickTrace) {
        RunTraceObserver.addRunTrace(clickTrace);
    }

    public static void addCodeTrace(ICodeTrace clickTrace) {
        RunTraceObserver.addRunTrace(clickTrace);
    }

    public static void addLifeATrace(ILifeATrace clickTrace) {
        RunTraceObserver.addRunTrace(clickTrace);
    }
}
