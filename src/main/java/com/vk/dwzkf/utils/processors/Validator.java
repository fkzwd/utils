package com.vk.dwzkf.utils.processors;

public interface Validator<T> {
    boolean validate(T target);
}
