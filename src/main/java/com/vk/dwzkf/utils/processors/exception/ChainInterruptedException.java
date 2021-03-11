package com.vk.dwzkf.utils.processors.exception;

import com.vk.dwzkf.utils.processors.ChainValidator;
import com.vk.dwzkf.utils.processors.Processor;
import lombok.Getter;

@Getter
public class ChainInterruptedException extends RuntimeException {
    private final Processor<?> interruptedBy;
    private final Object target;
    private final ChainValidator chainValidator;

    public ChainInterruptedException(String message,
                                     Processor<?> interruptedBy,
                                     Object target,
                                     ChainValidator chainValidator) {

        super(message);
        this.interruptedBy = interruptedBy;
        this.target = target;
        this.chainValidator = chainValidator;
    }
}
