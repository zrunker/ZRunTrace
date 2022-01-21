package cc.banzhi.runtrace

import cc.banzhi.runtrace.transforms.RunTraceTransform
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

// 自定义插件：实现Plugin类接口，重写apply方法
class RunTracePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // 注册Transform任务
        BaseExtension android = project.extensions.getByType(BaseExtension)
        // BaseExtension内部维护着_transforms集合，registerTransform是将TraceTransform实例添加到集合内
        android.registerTransform(new RunTraceTransform())
    }
}