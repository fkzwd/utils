package com.vk.dwzkf.utils.router;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class SynchronizedHandler<T> {
    private static final AtomicInteger threadCounter = new AtomicInteger(0);
    private static final AtomicInteger beanCounter = new AtomicInteger(0);
    private static final String THREAD_NAME_PREFIX = "Req-Handler-";
    private static final String BEAN_NAME_PREFIX = "Req-Handler-Bean-";

    private static final ThreadFactory factory = runnable -> new Thread(null,
            runnable,
            THREAD_NAME_PREFIX + threadCounter.incrementAndGet()
    );

    private final ExecutorService executor = Executors.newSingleThreadExecutor(factory);
    @Getter
    private final String name;

    public SynchronizedHandler() {
        this.name = BEAN_NAME_PREFIX + beanCounter.incrementAndGet();
    }

    public abstract void handle(T data);

    public void apply(T data) {
        executor.execute(() -> handle(data));
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }
}
