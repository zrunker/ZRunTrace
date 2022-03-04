package cc.banzhi.runtrace_api.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @program: AIChinese
 * @description: 时间管理类
 * @author: zoufengli01
 * @create: 2021/11/19 5:20 下午
 **/
public class DateUtil {

    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return sdf.format(new Date());
    }
}
