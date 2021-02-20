package com.vk.dwzkf.utils.stages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class Stage<T extends Throwable> implements Runnable {
    private static final int DEFAULT_ATTEMPTS_COUNT = 1;

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Class<T> exceptionClass;
    private final int attemptCounts;
    private final StageMetadata stageMetadata;
    private StageState stageState = StageState.CREATED;
    private Runnable action;
    private Consumer<T> caughtExceptionHandler;
    private Consumer<Throwable> uncaughtExceptionHandler;

    private boolean failOnException = true;

    public Stage(Class<T> exceptionClass,
                 int attemptCounts,
                 StageMetadata stageMetadata) {

        if (attemptCounts < 0) {
            throw new IllegalArgumentException("Cannot create object with attemptsCount < 0");
        }
        this.exceptionClass = exceptionClass;
        this.attemptCounts = attemptCounts;
        this.stageMetadata = stageMetadata;
    }

    public Stage(Class<T> exceptionClass, int attemptCounts) {
        this(exceptionClass, attemptCounts, StageMetadata.empty());
    }

    public Stage(Class<T> exceptionClass,
                 int attemptCounts,
                 StageMetadata stageMetadata,
                 Runnable action) {

        this(exceptionClass, attemptCounts, stageMetadata);
        this.action = action;
    }

    public Stage(Class<T> exceptionClass, Runnable action) {
        this(exceptionClass, DEFAULT_ATTEMPTS_COUNT, StageMetadata.empty(), action);
    }

    public StageMetadata getStageMetadata() {
        return stageMetadata;
    }

    public StageState getStageState() {
        return stageState;
    }

    @Override
    public void run() {
        if (action != null) {
            action.run();
        }
    }

    public Stage<T> setCaughtExceptionHandler(Consumer<T> handler) {
        this.caughtExceptionHandler = handler;
        return this;
    }

    public Stage<T> setUncaughtExceptionHandler(Consumer<Throwable> handler) {
        this.uncaughtExceptionHandler = handler;
        return this;
    }

    public void execute(Runnable r) {
        if (action == null) {
            this.action = r;
        } else {
            throw new IllegalStateException("Cannot reset runnable.");
        }
        execute();
    }

    public Stage<T> successOnException() {
        failOnException = false;
        return this;
    }

    public Stage<T> failOnException() {
        failOnException = true;
        return this;
    }

    public void execute() {
        stageState = StageState.STARTED;
        log.info("Stage started. Stage metadata: `{}`", stageMetadata);
        for (int attempt = 1; attempt <= attemptCounts; attempt++) {
            try {
                run();
                stageState = StageState.EXECUTED_SUCCESSFULLY;
                log.info("Action successfully executed.");
                return;
            } catch (Throwable t) {
                if (exceptionClass.isInstance(t)) {
                    log.warn("Exception occurred while executing task. Catch exception`{}`. Stage metadata`{}`. Error message:`{}`. Attempts left: {}",
                            t.getClass(),
                            stageMetadata.toString(),
                            t.getMessage(),
                            attemptCounts - attempt
                    );
                    handleException(t, caughtExceptionHandler);
                } else {
                    stageState = StageState.FAILED;
                    handleException(t, uncaughtExceptionHandler);
                    throw t;
                }
            }
        }
        if (failOnException) {
            stageState = StageState.FAILED;
        } else {
            stageState = StageState.EXECUTED_SUCCESSFULLY;
        }
    }

    public static Stage<Exception> of(Runnable action) {
        return new Stage<>(Exception.class, DEFAULT_ATTEMPTS_COUNT, StageMetadata.empty(), action);
    }

    public static Stage<Exception> of(Runnable action, int attemptCounts) {
        return new Stage<>(Exception.class, attemptCounts, StageMetadata.empty(), action);
    }

    public static <T extends Throwable> Stage<T> of(Runnable action,
                                                    int attemptCounts,
                                                    Class<T> exceptionClass) {
        return new Stage<>(exceptionClass, attemptCounts, StageMetadata.empty(), action);
    }

    public static Stage<Throwable> anyThrowable(Runnable action) {
        return new Stage<>(Throwable.class, 1, StageMetadata.empty(), action);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <R extends Throwable> void handleException(R exception, Consumer handler) {
        if (handler == null) {
            return;
        }
        try {
            handler.accept(exception);
        } catch (Exception e) {
            log.error("Exception occurred while handling exception{}. {}", exception.getClass(), e.getMessage(), e);
        }
    }
}
