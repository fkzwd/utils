package com.vk.dwzkf.utils.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;

public abstract class ProcessorChain<T extends Processor<R>, R> {
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected final List<T> processors;

    public ProcessorChain(List<T> processors) {
        processors.sort(Comparator.comparing(this::getOrderNumber));
        this.processors = processors;
    }

    public void handle(R target) {
        ChainValidator chainValidator = new ChainValidator();
        processors.forEach(p -> {
            try {
                log.debug("Handling target'{}' in processor'{}' order'{}'",
                        target, p, getOrderNumber(p)
                );
                p.handleTarget(target, chainValidator);
            } catch (Exception e) {
                log.error("Exception while processing target'{}' in processor'{}' Error:'{}'",
                        target,
                        p,
                        e.getMessage(),
                        e
                );
            }
        });
        chainValidator.clearMap();
    }

    @PostConstruct
    public void logProcessors() {
        processors.forEach(p -> log.info("Registered processor'{}': order[{}] processor[{}] for [{}]",
                p.getClass().getSimpleName(),
                getOrderNumber(p),
                p, this)
        );
    }

    /**
     * Add supporting set order without name like {@link ProcessorOrder#value()}
     * instead of {@link ProcessorOrder#orderNumber()}
     *
     * can use {@code @ProcessorOrder(1)} or {@code @ProcessorOrder(orderNumber = 1)}
     *
     * return value that is min, cause default is {@link Integer#MAX_VALUE}
     * @param processor - processor
     * @return actual order
     */
    private Integer getOrderNumber(Processor<R> processor) {
        if (processor.getClass().isAnnotationPresent(ProcessorOrder.class)) {
            int firstValue = processor.getClass().getAnnotation(ProcessorOrder.class).orderNumber();
            int secondValue = processor.getClass().getAnnotation(ProcessorOrder.class).value();
            return Math.min(firstValue, secondValue);
        } else {
            return Integer.MAX_VALUE;
        }
    }
}
