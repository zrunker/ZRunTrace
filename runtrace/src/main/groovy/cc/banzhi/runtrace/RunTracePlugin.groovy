package cc.banzhi.runtrace

import cc.banzhi.runtrace.transforms.RunTraceTransform
import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

// 自定义插件：实现Plugin类接口，重写apply方法
class RunTracePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
//        // 定义名RunTraceTask的任务
//        project.task("RunTraceTask") {
//            printf("自定义插件，执行RunTraceTask")
//        }

        // 注册Transform任务
        // AppExtension <- AbstractAppExtension <- TestedExtension <- BaseExtension
        // 这里直接获取BaseExtension进行注册也是可以的
        AppExtension app = project.extensions.getByType(AppExtension)
        // BaseExtension内部维护着_transforms集合，registerTransform是将TraceTransform实例添加到集合内
        app.registerTransform(new RunTraceTransform())
    }
}