package cc.banzhi.runtrace_api;

import android.util.Log;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @program: ZRunTrace
 * @description: 日志级别
 * @author: zoufengli01
 * @create: 2022/2/8 4:20 下午
 **/
@IntDef({Log.VERBOSE, Log.DEBUG, Log.INFO, Log.ERROR, Log.WARN})
@Retention(RetentionPolicy.SOURCE)
public @interface LogLevel {
}
