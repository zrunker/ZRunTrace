package cc.banzhi.runtrace_api;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

import cc.banzhi.runtrace_api.click.IClickTrace;
import cc.banzhi.runtrace_api.code.ICodeTrace;
import cc.banzhi.runtrace_api.lifecycle.ILifeATrace;

/**
 * @program: ZRunTrace
 * @description: 监测信息观察者
 * @author: zoufengli01
 * @create: 2022/1/24 6:46 下午
 **/
public class RunTraceObserver {
    private static final ArrayList<IRunTraceLisenter> list = new ArrayList<>();

    public static void addRunTrace(IRunTraceLisenter iRunTrace) {
        if (iRunTrace != null) {
            list.add(iRunTrace);
        }
    }

    public static void removeRunTrace(IRunTraceLisenter iRunTrace) {
        if (iRunTrace != null) {
            list.remove(iRunTrace);
        }
    }

    /**
     * 接收方法参数信息
     *
     * @param tag      日志TAG，默认为包名+类名称
     * @param level    日志等级
     * @param isUpload 是否上传
     * @param paramMap 参数信息
     */
    public static void runTrace(String tag, int level, boolean isUpload,
                                HashMap<String, Object> paramMap) {
        if (list.size() > 0) {
            for (IRunTraceLisenter item : list) {
                if (item instanceof ICodeTrace) {
                    ((ICodeTrace) item).runTrace(tag, level, isUpload, paramMap);
                }
            }
        }
    }

    /**
     * 接收方法运行总时长
     *
     * @param tag      日志TAG，默认为包名+类名称
     * @param level    日志等级
     * @param isUpload 是否上传
     * @param time     总时长（ms）
     */
    public static void runTime(String tag, int level, boolean isUpload,
                               long time) {
        if (list.size() > 0) {
            for (IRunTraceLisenter item : list) {
                if (item instanceof ICodeTrace) {
                    ((ICodeTrace) item).runTime(tag, level, isUpload, time);
                }
            }
        }
    }

    /**
     * 点击事件监听
     *
     * @param tag 日志TAG，，默认为包名+类名称
     * @param v   被点击的View
     */
    public static void runClick(String tag, View v) {
        if (list.size() > 0) {
            for (IRunTraceLisenter item : list) {
                if (item instanceof IClickTrace) {
                    ((IClickTrace) item).runClick(tag, v);
                }
            }
        }
    }

    /**
     * Activity-onCreate方法监测
     *
     * @param tag                日志TAG，，默认为包名+类名称
     * @param savedInstanceState 启动携带数据
     */
    public static void onACreate(String tag, Bundle savedInstanceState) {
        if (list.size() > 0) {
            for (IRunTraceLisenter item : list) {
                if (item instanceof ILifeATrace) {
                    ((ILifeATrace) item).onACreate(tag, savedInstanceState);
                }
            }
        }
    }

    /**
     * Activity-onStart方法监测
     *
     * @param tag 日志TAG，，默认为包名+类名称
     */
    public static void onAStart(String tag) {
        if (list.size() > 0) {
            for (IRunTraceLisenter item : list) {
                if (item instanceof ILifeATrace) {
                    ((ILifeATrace) item).onAStart(tag);
                }
            }
        }
    }

    /**
     * Activity-onResume方法监测
     *
     * @param tag 日志TAG，，默认为包名+类名称
     */
    public static void onAResume(String tag) {
        if (list.size() > 0) {
            for (IRunTraceLisenter item : list) {
                if (item instanceof ILifeATrace) {
                    ((ILifeATrace) item).onAResume(tag);
                }
            }
        }
    }

    /**
     * Activity-onRestart方法监测
     *
     * @param tag 日志TAG，，默认为包名+类名称
     */
    public static void onARestart(String tag) {
        if (list.size() > 0) {
            for (IRunTraceLisenter item : list) {
                if (item instanceof ILifeATrace) {
                    ((ILifeATrace) item).onARestart(tag);
                }
            }
        }
    }

    /**
     * Activity-onPause方法监测
     *
     * @param tag 日志TAG，，默认为包名+类名称
     */
    public static void onAPause(String tag) {
        if (list.size() > 0) {
            for (IRunTraceLisenter item : list) {
                if (item instanceof ILifeATrace) {
                    ((ILifeATrace) item).onAPause(tag);
                }
            }
        }
    }

    /**
     * Activity-onStop方法监测
     *
     * @param tag 日志TAG，，默认为包名+类名称
     */
    public static void onAStop(String tag) {
        if (list.size() > 0) {
            for (IRunTraceLisenter item : list) {
                if (item instanceof ILifeATrace) {
                    ((ILifeATrace) item).onAStop(tag);
                }
            }
        }
    }

    /**
     * Activity-onDestroy方法监测
     *
     * @param tag 日志TAG，，默认为包名+类名称
     */
    public static void onADestroy(String tag) {
        if (list.size() > 0) {
            for (IRunTraceLisenter item : list) {
                if (item instanceof ILifeATrace) {
                    ((ILifeATrace) item).onADestroy(tag);
                }
            }
        }
    }

    /**
     * Activity-onNewIntent方法监测
     *
     * @param tag    日志TAG，，默认为包名+类名称
     * @param intent 启动携带数据
     */
    public static void onANewIntent(String tag, Intent intent) {
        if (list.size() > 0) {
            for (IRunTraceLisenter item : list) {
                if (item instanceof ILifeATrace) {
                    ((ILifeATrace) item).onANewIntent(tag, intent);
                }
            }
        }
    }
}
