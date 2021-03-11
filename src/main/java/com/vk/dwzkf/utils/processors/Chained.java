package com.vk.dwzkf.utils.processors;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Chained {
    @SuppressWarnings("rawtypes") Class<? extends ProcessorChain> value();
}
