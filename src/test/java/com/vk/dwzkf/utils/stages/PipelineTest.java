package com.vk.dwzkf.utils.stages;

import com.vk.dwzkf.utils.exception.Exception1;
import com.vk.dwzkf.utils.exception.ExceptionUtil;
import com.vk.dwzkf.utils.exception.RuntimeException1;
import org.junit.Test;

import static com.vk.dwzkf.utils.exception.ExceptionUtil.*;

public class PipelineTest {
    @Test
    public void pipelineDontBreakIfLowException() {
        Pipeline<RuntimeException1> pipeline = new Pipeline<>(RuntimeException1.class);
        pipeline.addStage(Stage.of(ExceptionUtil::runtimeParent, 1, exceptionChild))
                .continueOnAnyStageFailed();
    }
}
