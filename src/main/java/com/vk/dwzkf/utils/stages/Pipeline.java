package com.vk.dwzkf.utils.stages;


import com.vk.dwzkf.utils.stages.exception.BreakOnStageFailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Not thread safe
 * @param <T>
 */
public class Pipeline<T extends Throwable> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Class<T> topLevelException;
    private final List<Stage<?>> stages = new LinkedList<>();

    private Consumer<? extends Throwable> uncaughtExceptionHandler;
    private Consumer<T> caughtExceptionHandler;
    private boolean failOnException = false;


    public Pipeline(Class<T> topLevelException) {
        this.topLevelException = topLevelException;
    }

    private List<Stage<?>> run(List<Stage<?>> stages) throws BreakOnStageFailException {
        for (Stage<?> stage : stages) {
            try {
                stage.execute();
            } catch (Throwable exception) {
                if (topLevelException.isInstance(exception)) {
                    log.warn("Catch exception on stage{}, stageMetadata{}, stageState:{}. Exception message: {}",
                            stage,
                            stage.getStageMetadata(),
                            stage.getStageState(),
                            exception.getMessage()
                    );
                    handleException(exception, caughtExceptionHandler);
                } else {
                    log.error("Exception occurred. {}", exception.getMessage(), exception);
                    handleException(exception, uncaughtExceptionHandler);
                    throw exception;
                }
            }
            if (stage.getStageState() == StageState.FAILED && failOnException) {
                throw new BreakOnStageFailException("Pipeline ended cause stage failed." +
                        " Failed StageMetadata: "
                        + stage.getStageMetadata(),
                        stage,
                        stages);
            }
        }
        return stages;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void handleException(Throwable exception, Consumer catcher) {
        if (catcher != null) {
            try {
                catcher.accept(exception);
            } catch (Throwable t) {
                log.error("Exception while executing action of exception handler. {}", t.getMessage(), t);
            }
        }
    }

    private List<Stage<?>> run() throws BreakOnStageFailException {
        return run(stages);
    }

    public Pipeline<T> addStage(Stage<?> stage) {
        stages.add(stage);
        return this;
    }

    public void clear() {
        stages.clear();
    }

    /**
     * Sets the handler for exception that would be thrown
     * if handler throws exception it would be logged and eaten by try-catch statement
     *
     * @param uncaughtExceptionHandler - Consumer<Throwable>
     * @return - this
     */
    public Pipeline<T> setUncaughtExceptionHandler(Consumer<Throwable> uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        return this;
    }

    /**
     * Sets the handler for exception that must be caught by pipeline
     * if handler throws exception it would be logged and eaten by try-catch statement
     *
     * @param caughtExceptionHandler - Consumer<T>
     * @return - this
     */
    public Pipeline<T> setCaughtExceptionHandler(Consumer<T> caughtExceptionHandler) {
        this.caughtExceptionHandler = caughtExceptionHandler;
        return this;
    }

    public List<Stage<?>> breakOnAnyStageFailed() throws BreakOnStageFailException {
        failOnException = true;
        return run();
    }

    public List<Stage<?>> continueOnAnyStageFailed() {
        failOnException = false;
        try {
            return run();
        } catch (BreakOnStageFailException e) {
            return stages;
        }
    }
}

