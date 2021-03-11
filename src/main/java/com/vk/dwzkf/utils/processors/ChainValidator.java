package com.vk.dwzkf.utils.processors;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChainValidator {
    private final Map<Class<?>, ValidationResult> validatorMap = new ConcurrentHashMap<>();

    public void setValidated(Class<?> klass) {
        validatorMap.put(klass, ValidationResult.VALID);
    }

    public void setInvalidated(Class<?> klass) {
        validatorMap.put(klass, ValidationResult.INVALID);
    }

    public void setValidationState(Class<?> klass, ValidationResult validationResult) {
        validatorMap.put(klass, validationResult);
    }

    void clearMap() {
        validatorMap.clear();
    }

    public <T> boolean compute(Validator<T> instance, T target) {
        boolean result;
        Class<?> klass = instance.getClass();
        if (getValidationState(klass) == ValidationResult.NOT_PROCESSED) {
            result = instance.validate(target);
            setValidationState(klass, ValidationResult.fromBoolean(result));
            log.debug("Validating: target {} on instance {}. Result {} ",
                    target,
                    instance,
                    result
            );
        } else {
            log.debug("Validating: target {} already validated. Validation result'{}'",
                    target,
                    getValidationState(klass) == ValidationResult.VALID
            );
            result = getValidationState(klass) == ValidationResult.VALID;
        }
        return result;
    }

    public ValidationResult getValidationState(Class<?> klass) {
        ValidationResult result = validatorMap.get(klass);
        if (result == null) {
            return ValidationResult.NOT_PROCESSED;
        }
        return result;
    }
}