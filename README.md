
## 基于Transform API+ASM+注解来实现一个监测方法开始执行时间的小实例

##### 编译测试

1）在相应模块中的build.gradle中引入`RunTracePlugin`插件和`runtrace-api`组件：

``` 
apply plugin: 'com.android.application'
// 引入自定义插件
apply plugin: 'cc.banzhi.runtrace'

...

dependencies {
		...
    implementation project(':runtrace-api')
}
```

2）在`MainActivity`中添加注解测试方法`public int add(int a, int b)`：

``` 
// 编译之前代码
public class MainActivity extends AppCompatActivity {
  	...
    // 添加注解
    @RunTrace
    public int add(int a, int b) {
        System.out.println("测试a+b");
        return a + b;
    }
}
```

3）将工程进行build操作，之后在build/intermediates/javac/debug/classes目录下找到`MainActivity.class`文件，打开文件查看执行结果：

``` 
public class MainActivity extends AppCompatActivity {
  	...
  
    // 编译之后生成结果
    @RunTrace
    public int add(int a, int b) {
        Log.i("cc/banzhi/zruntrace/MainActivity", DateUtil.getCurrentTime());
        System.out.println("测试a+b");
        return a + b;
    }
}
```

可以看到`public int add(int a, int b)`方法中生成代码内容符合预期结果，至此监测方法开始执行时间小实例就完成了。