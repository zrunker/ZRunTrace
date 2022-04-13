package cc.banzhi.runtrace


import cc.banzhi.runtrace.transforms.lifecycle.LifeTraceTransform
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.plugins.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

// 自定义插件：实现Plugin类接口，重写apply方法
class RunTracePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // 判断当前模块是否包含'com.android.application'插件
        // 该插件需要在包含application插件模块下，对整个工程做处理
        if (project.plugins.hasPlugin(AppPlugin)) {
            // 注册Transform任务
            BaseExtension android = project.extensions.getByType(BaseExtension)
            // BaseExtension内部维护着_transforms集合，registerTransform是将TraceTransform实例添加到集合内
            if (android != null) {
                // 注册Extension
                RunTraceExtension extension = project.extensions.create("runTraceConfig", RunTraceExtension)
                project.afterEvaluate {
                    System.out.println(extension.toString())
                }

//                // 代码层级埋点
//                android.registerTransform(new CodeTraceTransform(project))
//                // 点击事件埋点
//                android.registerTransform(new ClickTraceTransform(project))
                // Activity生命周期监测
                android.registerTransform(new LifeTraceTransform(project))
            }
        }
    }
}

class RunTraceExtension {
    // 是否开启代码层级埋点
    boolean isOpenCodeTrace = true

    // 是否开启点击事件埋点
    boolean isOpenClickTrace = true

    // 是否开启Activity生命周期监测
    boolean isOpenLifeTrace = true

    // Activity父类名称，如AppCompatActivity
    String lifeActivitySuperName = ""

    @Override
    String toString() {
        return "RunTraceExtension{" +
                "isOpenCodeTrace=" + isOpenCodeTrace +
                ", isOpenClickTrace=" + isOpenClickTrace +
                ", isOpenLifeTrace=" + isOpenLifeTrace +
                ", lifeActivitySuperName='" + lifeActivitySuperName + '\'' +
                '}';
    }
}