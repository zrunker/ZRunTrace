package cc.banzhi.runtrace.transforms;


import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;

import java.io.IOException;
import java.util.Set;

/**
 * @program: ZRunTrace
 * @description: 自定义Transform处理注解
 * @author: zoufengli01
 * @create: 2022/1/13 5:11 下午
 **/
class RunTraceTransform extends Transform {
    /**
     * 为Transform定义一个唯一的名称
     */
    @Override
    String getName() {
        return "RunTraceTransform";
    }

    /**
     * 定义Transform接收的输入文件类型
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        // Class文件
        return TransformManager.CONTENT_CLASS;
    }

    /**
     * 定义Transform的作用域
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    /**
     * 是否支持增量
     */
    @Override
    boolean isIncremental() {
        return false;
    }

    /**
     * Transform的执行主函数
     */
    @Override
    void transform(TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

    }
}
