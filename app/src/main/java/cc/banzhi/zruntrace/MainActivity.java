package cc.banzhi.zruntrace;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.banzhi.runtrace_api.code.CodeTrace;
import cc.banzhi.testlib.TestX;

@CodeTrace
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.tv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.tv_test).setOnClickListener(v -> {

        });

        init("onCreate", 100);
        init('z');
        init(12345.90f);
        init(true);
    }

    public void init() {
    }

    @CodeTrace(aliasName = "测试",
            level = Log.VERBOSE,
            enableLog = true,
            enableTime = true,
            enableUpload = true)
    private void init(String name, int index) {
        Log.d("MainActivity", "这是一个测试信息 name = " + name + " index = " + index);
    }

    @CodeTrace(aliasName = "测试2", level = Log.ERROR, enableTime = false, enableUpload = true)
    public void init(char name) {
        Log.d("MainActivity", "这是一个测试信息 - 2 name = " + name);
    }

    @CodeTrace(aliasName = "测试3", tag = "MainActivity----Test----")
    public static void init(float name) {
        Log.d("MainActivity", "这是一个测试信息 - 3 name = " + name);
        new TestX(101010101);
    }

    @CodeTrace
    public static void init(boolean name) {
        Log.d("MainActivity", "这是一个测试信息 - 4 name = " + name);
    }

    @CodeTrace(extras = "json")
    public void start(List<String> list) {
    }

    @CodeTrace
    public void start(Map<String, String> map) {
    }


    @CodeTrace
    public void start(Set<String> set) {
    }

    @CodeTrace
    public void start(String[] arr) {
    }

    @CodeTrace
    public void start(TestX data) {
    }

    @CodeTrace
    public void start(Boolean data) {
    }

    @CodeTrace(
            aliasName = "TestEventName",
            extras = "{\"value\": \"测试\"}",
            enableUpload = true,
            enableTime = true,
            enableLog = true
    )
    public int add(int a, int b) {
        System.out.println("测试a+b");
        return a + b;
    }

    @Override
    public void onClick(View v) {

    }
}