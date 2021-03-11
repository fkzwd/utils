package com.vk.dwzkf.utils.processors;

public enum ValidationResult {
    VALID,INVALID,NOT_PROCESSED;

    public static ValidationResult fromBoolean(boolean b) {
        return b ? VALID : INVALID;
    }
}
