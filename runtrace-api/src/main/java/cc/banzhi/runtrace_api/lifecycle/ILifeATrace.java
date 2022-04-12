package cc.banzhi.runtrace_api.lifecycle;

import android.content.Intent;
import android.os.Bundle;

import cc.banzhi.runtrace_api.IRunTraceLisenter;

/**
 * @program: ZRunTrace
 * @description: Activity生命周期埋点回调
 * @author: zoufengli01
 * @create: 2022/4/12 4:35 下午
 **/
public interface ILifeATrace extends IRunTraceLisenter {
    void onACreate(String tag, Bundle savedInstanceState);

    void onAStart(String tag);

    void onAResume(String tag);

    void onARestart(String tag);

    void onAPause(String tag);

    void onAStop(String tag);

    void onADestroy(String tag);

    void onANewIntent(String tag, Intent intent);
}
