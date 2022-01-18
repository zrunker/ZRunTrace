package cc.banzhi.runtrace

import org.gradle.api.Plugin
import org.gradle.api.Project

// 自定义插件：实现Plugin类接口，重写apply方法
class RunTracePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.
        // 定义名RunTraceTask的任务
        project.task("RunTraceTask") {
            printf("自定义插件，执行RunTraceTask")
        }
    }
}