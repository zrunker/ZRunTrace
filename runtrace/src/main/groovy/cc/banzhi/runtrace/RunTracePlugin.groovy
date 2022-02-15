package cc.banzhi.runtrace

import cc.banzhi.runtrace.transform.RunTraceTransform
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.plugins.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

// 自定义插件：实现Plugin类接口，重写apply方法
class RunTracePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (project.plugins.hasPlugin(AppPlugin)) {
            // 注册Transform任务
            BaseExtension android = project.extensions.getByType(BaseExtension)
            // BaseExtension内部维护着_transforms集合，registerTransform是将TraceTransform实例添加到集合内
            if (android != null) {
                android.registerTransform(new RunTraceTransform())
            }
        }
    }
}