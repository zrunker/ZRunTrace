package cc.banzhi.runtrace_api;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @program: ZRunTrace
 * @description: 监测信息观察者
 * @author: zoufengli01
 * @create: 2022/1/24 6:46 下午
 **/
public class RunTraceObserver {
    private static final ArrayList<IRunTrace> list = new ArrayList<>();

    public static void addRunTrace(IRunTrace iRunTrace) {
        if (iRunTrace != null) {
            list.add(iRunTrace);
        }
    }

    public static void removeRunTrace(IRunTrace iRunTrace) {
        if (iRunTrace != null) {
            list.remove(iRunTrace);
        }
    }

    /**
     * 方法参数信息
     *
     * @param tag      日志TAG
     * @param level    日志等级
     * @param isUpload 是否上传
     * @param paramMap 参数信息
     */
    public static void runTrace(String tag, int level, boolean isUpload,
                                HashMap<String, Object> paramMap) {
        if (paramMap != null) {
            switch (level) {
                case Log.DEBUG:
                    Log.d(tag, paramMap.toString());
                    break;
                case Log.VERBOSE:
                    Log.v(tag, paramMap.toString());
                    break;
                case Log.WARN:
                    Log.w(tag, paramMap.toString());
                    break;
                case Log.ERROR:
                    Log.e(tag, paramMap.toString());
                    break;
                case Log.INFO:
                default:
                    Log.i(tag, paramMap.toString());
                    break;
            }
        }
        if (list.size() > 0) {
            for (IRunTrace item : list) {
                if (item != null) {
                    item.runTrace(tag, level, isUpload, paramMap);
                }
            }
        }
    }

    /**
     * 方法运行时长
     *
     * @param tag      日志TAG
     * @param level    日志等级
     * @param isUpload 是否上传
     * @param time     总时长（ms）
     */
    public static void runTime(String tag, int level, boolean isUpload,
                               long time) {
        if (list.size() > 0) {
            for (IRunTrace item : list) {
                if (item != null) {
                    item.runTime(tag, level, isUpload, time);
                }
            }
        }
    }
}
