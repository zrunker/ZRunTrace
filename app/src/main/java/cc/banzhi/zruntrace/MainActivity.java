package cc.banzhi.zruntrace;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.banzhi.runtrace_api.RunTrace;
import cc.banzhi.testlib.TestX;

@RunTrace
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init("onCreate", 100);
        init('z');
        init(12345.90f);
        init(true);
    }

    public void init() {
    }

    @RunTrace(aliasName = "测试", level = Log.VERBOSE, enableTime = true, enableUpload = true)
    private void init(String name, int index) {
        Log.d("MainActivity", "这是一个测试信息 name = " + name + " index = " + index);
    }

    @RunTrace(aliasName = "测试2", level = Log.ERROR, enableTime = false, enableUpload = true)
    public void init(char name) {
        Log.d("MainActivity", "这是一个测试信息 - 2 name = " + name);
    }

    @RunTrace(aliasName = "测试3", tag = "MainActivity----Test----")
    public static void init(float name) {
        Log.d("MainActivity", "这是一个测试信息 - 3 name = " + name);
        new TestX(101010101);
    }

    @RunTrace
    public static void init(boolean name) {
        Log.d("MainActivity", "这是一个测试信息 - 4 name = " + name);
    }

    @RunTrace(extras = "json")
    public void start(List<String> list) {
    }

    @RunTrace
    public void start(Map<String, String> map) {
    }


    @RunTrace
    public void start(Set<String> set) {
    }

    @RunTrace
    public void start(String[] arr) {
    }

    @RunTrace
    public void start(TestX data) {
    }

    @RunTrace
    public void start(Boolean data) {
    }

    @RunTrace
    public void start(StringBuilder data) {
    }
}