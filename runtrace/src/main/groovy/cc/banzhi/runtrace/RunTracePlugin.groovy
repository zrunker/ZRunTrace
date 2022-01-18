package cc.banzhi.runtrace

import cc.banzhi.runtrace.transforms.RunTraceTransform
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
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
        // 这里直接使用BaseExtension也是可以的
        AppExtension app = project.extensions.getByType(AppExtension)
        app.registerTransform(new RunTraceTransform())
    }
}