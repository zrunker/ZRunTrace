package cc.banzhi.zruntrace;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import cc.banzhi.runtrace_api.RunTrace;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @RunTrace
    public int add(int a, int b) {
        System.out.println("测试a+b");
        return a + b;
    }
}