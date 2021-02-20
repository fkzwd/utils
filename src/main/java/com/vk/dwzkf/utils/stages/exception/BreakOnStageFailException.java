package com.vk.dwzkf.utils.stages.exception;

import com.vk.dwzkf.utils.stages.Stage;

import java.util.List;

public class BreakOnStageFailException extends Exception {
    private final Stage<?> failedStage;
    private final List<Stage<?>> allStages;



    public BreakOnStageFailException(String message, Stage<?> failedStage, List<Stage<?>> allStages) {
        super(message);
        this.failedStage = failedStage;
        this.allStages = allStages;
    }
}