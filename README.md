
## 基于Transform API+ASM+注解实现代码埋点小实例

##### 测试使用

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

```java
// 编译之前代码
public class MainActivity extends AppCompatActivity {
  	...
    // 添加注解
    @RunTrace(
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
}
```

3）将工程进行build操作，之后在build/intermediates/javac/debug/classes目录下找到`MainActivity.class`文件，打开文件查看执行结果：

```java
public class MainActivity extends AppCompatActivity {
  	...
  
    // 编译之后生成结果
    @RunTrace(
        aliasName = "TestEventName",
        extras = "{\"value\": \"测试\"}",
        enableUpload = true,
        enableTime = true,
        enableLog = true
    )
    public int add(int a, int b) {
      	/*定义HashMap存储埋点数据*/
        HashMap var3 = new HashMap();
        var3.put("traceSource", "cc.banzhi.zruntrace.MainActivity#add(II)I");
        var3.put("traceTime", DateUtil.getCurrentTime());
        var3.put("aliasName", "TestEventName");
        var3.put("extras", "{\"value\": \"测试\"}");
        var3.put("a", String.valueOf(a));
        var3.put("b", String.valueOf(b));
      
      	/*输出埋点数据*/
        Log.i("cc.banzhi.zruntrace.MainActivity", var3.toString());
      
      	/*将埋数据传递给监视器*/
        RunTraceObserver.runTrace("cc.banzhi.zruntrace.MainActivity", 4, true, var3);
      
      	/*定义参数统计方法开始执行时间*/
        long var4 = System.currentTimeMillis();
        
      	/*方法内容*/
      	System.out.println("测试a+b");
        int var10000 = a + b;
      
      	/*打印方法执行耗时总时长*/
        Log.i("cc.banzhi.zruntrace.MainActivity", "\n统计方法执行时长：\n------------add---------------\n执行耗时：" + (System.currentTimeMillis() - var4) + "\n当前线程：" + Thread.currentThread().getName() + "\n------------add---------------");
      
      	/*将方法执行耗时总时长传递给监视器*/
        RunTraceObserver.runTime("cc.banzhi.zruntrace.MainActivity", 4, true, System.currentTimeMillis() - var4);
      	
      	/*返回结果*/
        return var10000;
    }
}
```

可以看到`public int add(int a, int b)`方法中生成代码内容符合预期结果，至此埋点小实例就完成了。