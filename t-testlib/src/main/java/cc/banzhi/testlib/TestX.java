package cc.banzhi.testlib;

import android.util.Log;

import java.util.HashMap;
import java.util.List;

import cc.banzhi.runtrace_api.RunTraceObserver;
import cc.banzhi.runtrace_api.code.CodeTrace;
import cc.banzhi.runtrace_api.utils.DateUtil;


@CodeTrace(enableTime = true, enableUpload = true, enableLog = true)
public class TestX {
    private int num;

    class InnerClassA {

    }

    public TestX(int a) {
        Log.d("TestX",
                "\n统计方法执行时长：\n------------<init>---------------" +
                        "\n执行耗时：0" +
                        "\n当前线程：main" +
                        "\n------------<init>---------------");

    }

    private void init(int a) {
        RunTraceObserver.runTrace("Test", 10, false, null);
        HashMap<String, Object> map = new HashMap<>();
        Log.d("Test", map.toString());

        RunTraceObserver.runTime("Test", 10, true, System.currentTimeMillis() - 10);
    }

    private static void init() {
    }

    @CodeTrace(enableTime = true, level = Log.VERBOSE, enableLog = false)
    private static void initTest2(Object name, int index) {
    }

    @CodeTrace(enableTime = true, level = Log.DEBUG, enableObserver = false)
    private void initTest3(Object name, int index) {
    }

    @CodeTrace(enableTime = true)
    private static void initTest4(Object name, int index) {
    }

    @CodeTrace(enableTime = true, level = Log.WARN)
    private static void initTest5(Object name, int index) {
    }

    @CodeTrace(enableTime = true, level = Log.ERROR)
    private static void initTest6(Object name, int index) {
    }

    @CodeTrace(enableTime = true)
    private static void initTest7(Object name, int index) {
        Log.d("TestX",
                "\n------------<init>---------------" +
                        "\n执行耗时：0" +
                        "\n当前线程：main" +
                        "\n------------<init>---------------");
    }

    @CodeTrace(aliasName = "测试", enableTime = true, enableUpload = true)
    private void initTest(Object name, int index,
                          HashMap<String, Object> map, String[] arrs,
                          float a, double b, char c, Long d, short e,
                          byte f, long h, List<String> list) {
        String m1 = String.valueOf(a);

        String m2 = String.valueOf(index);

        Log.i("Test", "--------------------------------");
        Log.i("Test", "描述信息");

        final HashMap<String, Object> temp = new HashMap<>();
        temp.put("name", name);
        temp.put("executeTime", DateUtil.getCurrentTime());
        RunTraceObserver.runTrace("Test", 2, false, temp);

        final long num = System.currentTimeMillis();

        HashMap<String, Object> temp2 = new HashMap<>();
        temp2.put("name", name);
        temp2.put("executeTime", DateUtil.getCurrentTime());
        RunTraceObserver.runTrace("Test", 2, false, temp2);

        long startTime = System.currentTimeMillis();

        Object num1 = 10;

        Log.i("Test", "--------------------------------");
    }
}