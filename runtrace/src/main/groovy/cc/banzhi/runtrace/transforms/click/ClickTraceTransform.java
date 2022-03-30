package cc.banzhi.runtrace.transforms.click;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.TransformOutputProvider;

import java.io.IOException;

import cc.banzhi.runtrace.transforms.BaseTransform;

/**
 * @program: ZRunTrace
 * @description: 点击事件埋点监听
 * @author: zoufengli01
 * @create: 2022/3/30 3:52 下午
 **/
public class ClickTraceTransform extends BaseTransform {

    @Override
    protected String getTaskName() {
        return "ClickTraceTransform";
    }

    /**
     * 遍历JarInput
     *
     * @param jarInput       待遍历数据
     * @param outputProvider 输出Provider
     * @param isIncremental  是否支持增量更新
     */
    @Override
    protected void traverseJarInput(JarInput jarInput, TransformOutputProvider outputProvider,
                                    boolean isIncremental) throws IOException {

    }

    /**
     * 遍历DirectoryInput
     *
     * @param dirInput       待遍历数据
     * @param outputProvider 输出Provider
     * @param isIncremental  是否支持增量更新
     */
    @Override
    protected void traverseDirectoryInput(DirectoryInput dirInput, TransformOutputProvider outputProvider,
                                          boolean isIncremental) throws IOException {

    }
}
