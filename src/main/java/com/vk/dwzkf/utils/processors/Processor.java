package com.vk.dwzkf.utils.processors;

public abstract class Processor<T> {
    public void handleTarget(T target, ChainValidator chainValidator) {
        if (validateTarget(target, chainValidator)) {
            processTarget(target, chainValidator);
        }
    }

    protected abstract boolean validateTarget(T target, ChainValidator chainValidator);

    protected abstract void processTarget(T target, ChainValidator chainValidator);
}
