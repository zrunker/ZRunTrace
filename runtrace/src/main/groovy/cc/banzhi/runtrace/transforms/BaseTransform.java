package cc.banzhi.runtrace.transforms;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.ide.common.internal.WaitableExecutor;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import cc.banzhi.runtrace.RunTraceExtension;

/**
 * @program: ZRunTrace
 * @description: Transform基类
 * @author: zoufengli01
 * @create: 2022/3/30 3:57 下午
 **/
public abstract class BaseTransform extends Transform {
    protected final Project project;

    public BaseTransform(Project project) {
        this.project = project;
    }

//    /**
//     * 异步任务
//     */
//    private final WaitableExecutor waitableExecutor = WaitableExecutor.useGlobalSharedThreadPool();

    /**
     * 定义一个任务名称
     */
    protected abstract String getTaskName();

    /**
     * 遍历JarInput
     *
     * @param jarInput       待遍历数据
     * @param outputProvider 输出Provider
     * @param isIncremental  是否支持增量更新
     */
    protected abstract void traverseJarInput(JarInput jarInput,
                                             TransformOutputProvider outputProvider, boolean isIncremental) throws IOException;

    /**
     * 遍历DirectoryInput
     *
     * @param dirInput       待遍历数据
     * @param outputProvider 输出Provider
     * @param isIncremental  是否支持增量更新
     */
    protected abstract void traverseDirectoryInput(DirectoryInput dirInput,
                                                   TransformOutputProvider outputProvider, boolean isIncremental) throws IOException;

    /**
     * 为Transform定义一个唯一的名称
     */
    @Override
    public String getName() {
        return getTaskName();
    }

    /**
     * 定义Transform接收的输入文件类型
     */
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        // Class输入
        return TransformManager.CONTENT_CLASS;
    }

    /**
     * 定义Transform的作用域
     */
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        // 作用域为整个项目，可应用于app模块，不能应用于lib，否则会出现如下异常：
        // Transforms with scopes '[SUB_PROJECTS, EXTERNAL_LIBRARIES]' cannot be applied to library projects.
        // 如果想要在lib上使用可以设置为TransformManager.PROJECT_ONLY
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    /**
     * 是否支持增量
     */
    @Override
    public boolean isIncremental() {
        return true;
    }

    /**
     * Transform的执行主函数
     */
    @Override
    public void transform(TransformInvocation transformInvocation)
            throws InterruptedException, IOException, TransformException {
        System.out.println("---------------开始执行：" + getTaskName() + "---------------");
        super.transform(transformInvocation);
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        boolean isIncremental = transformInvocation.isIncremental();
        if (!isIncremental) {
            outputProvider.deleteAll();
        }
        // 遍历输入目录
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        for (TransformInput input : inputs) {
            // 遍历jar包，（Module和AAR等第三方引用，一般情况下会以classes.jar的方式存在）
            Collection<JarInput> jarInputs = input.getJarInputs();
            if (jarInputs != null && jarInputs.size() > 0) {
                for (JarInput jarInput : jarInputs) {
//                    // 开启异步并发
//                    waitableExecutor.execute(() -> {
                    traverseJarInput(jarInput, outputProvider, isIncremental);
//                        return null;
//                    });
                }
            }

            // 遍历dir目录，（一般情况下为当前引入插件的Module目录）
            Collection<DirectoryInput> directoryInputs = input.getDirectoryInputs();
            if (directoryInputs != null && directoryInputs.size() > 0) {
                for (DirectoryInput dirInput : directoryInputs) {
//                    // 开启异步并发
//                    waitableExecutor.execute(() -> {
                    traverseDirectoryInput(dirInput, outputProvider, isIncremental);
//                        return null;
//                    });
                }
            }
        }

//        // 等待所有任务结束
//        waitableExecutor.waitForTasksWithQuickFail(true);
    }

    protected boolean checkJar(String jarName) {
        return !jarName.startsWith("androidx.")
                && !jarName.startsWith("org.jetbrains")
                && !jarName.startsWith("com.squareup.")
                && !jarName.startsWith("com.google.")
                && !jarName.startsWith("org.apache.")
                && !jarName.startsWith("org.slf4j");
    }

    protected boolean checkClass(String className) {
        if (className.endsWith(".class")
                && !className.startsWith("androidx/")
                && !className.startsWith("android/")
                && !className.startsWith("com/google/android/material/")
                && !className.startsWith("cc/banzhi/runtrace_api/")) {
            int position = className.lastIndexOf("/");
            if (position > 0) {
                try {
                    className = className.substring(position + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return !className.startsWith("R$")
                    && !className.equals("R.class")
                    && !className.equals("BuildConfig.class");
        }
        return false;
    }
}
