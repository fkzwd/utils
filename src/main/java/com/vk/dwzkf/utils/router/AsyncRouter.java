package com.vk.dwzkf.utils.router;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public abstract class AsyncRouter<T extends SynchronizedHandler<R>, R, V> {
    private final List<T> handlers = Collections.synchronizedList(new LinkedList<>());
    private final AtomicInteger atomicInteger = new AtomicInteger();
    private final Map<V, T> handlerMap = new ConcurrentHashMap<>();

    @Value("${async.router.pool.size:15}")
    private Integer poolSize;
    private final Supplier<T> handlerFactory;
    private final Function<R, V> keyExtractor;

    @PostConstruct
    public void init() {
        log.info("Init async router with pool size of {}", poolSize);
        if (poolSize < 1) {
            log.info("Pool size cannot be < 1. Set to default: 15");
            poolSize = 15;
        }
        for (int i = 0; i < poolSize; i++) {
            handlers.add(handlerFactory.get());
        }
    }

    public void handle(R data) {
        V key = keyExtractor.apply(data);
        if (key != null) {
            T handler = handlerMap.get(key);
            if (handler == null) {
                handlerMap.computeIfAbsent(key, (thisKey) -> {
                    T h = nextHandler();
                    log.info("Assigned {} for key: {}",
                            h.getName(),
                            thisKey
                    );
                    return h;
                });
            }
            handler = handlerMap.get(key);
            handler.apply(data);
        }
    }

    public Set<V> getKeys() {
        return handlerMap.keySet();
    }

    public void removeAll(Set<V> keys) {
        keys.forEach(key -> {
            T handler = handlerMap.get(key);
            if (handler != null) {
                handlerMap.remove(key);
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        handlers.forEach(T::shutdown);
    }

    public void clearMap() {
        Set<V> keys = new HashSet<>(getKeys());
        removeAll(keys);
    }

    private T nextHandler() {
        int index = atomicInteger.getAndAccumulate(handlers.size(), (i, i1) -> (i + 1) % i1);
        return handlers.get(index);
    }
}
