package com.vk.dwzkf.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class CodeUtil {
    private static final Logger log = LoggerFactory.getLogger(CodeUtil.class);

    /**
     * Returns the negative value of the boolean
     *
     * @param b - your boolean
     * @return - not(your boolean)
     */
    public static boolean not(boolean b) {
        return !b;
    }

    /**
     * Concatenates any number of strings with a StringBuilder
     *
     * @param string  - start String
     * @param strings - optional other strings
     * @return - VERYVERYBIGSTRING
     */
    public static String concat(String string, String... strings) {
        checkNotNull(string, strings);
        StringBuilder sb = new StringBuilder();
        sb.append(string);
        for (String s : strings) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Check is {@param target} ends with any of strings that passed after
     *
     * @param target  - source string
     * @param strings - strings for check
     * @return - true/false
     */
    public static boolean endsWithAny(String target, String... strings) {
        checkNotNull(target, strings);
        for (String s : strings) {
            if (target.endsWith(s)) return true;
        }
        return false;
    }

    /**
     * Executes supplier that have no params
     * and just return a {@param <R>} type value safely,
     * <p>
     * If exception occur and its instance of {@param E}
     * it would be caught and method return a {@param defaultValue}
     * if any other exception occur method will log it and rethrow it
     *
     * @param function       - function to execute
     * @param exceptionClass - parent class of all exceptions to be caught
     * @param defaultValue   - value if exception occur and was caught
     * @param <R>            - type of value
     * @param <E>            - type of exception
     * @return - execution result or default value
     */
    public static <R, E extends Throwable> R safeExecute(Supplier<R> function,
                                                         Class<E> exceptionClass,
                                                         R defaultValue) {
        try {
            return function.get();
        } catch (Throwable exception) {
            if (exceptionClass.isInstance(exception)) {
                log.warn("Exception occurred but would be ignored. Message: {}",
                        exception.getMessage()
                );
                return defaultValue;
            } else {
                log.error("Exception occurred while safeExecute. Message: {}",
                        exception.getMessage(),
                        exception
                );
                throw exception;
            }
        }
    }

    /**
     * {@see {@link CodeUtil#safeExecute(Supplier, Class, Object)}}
     * Catch any {@link Throwable}
     *
     * @param function     - function
     * @param defaultValue - defaultValue if exception occur
     * @param <R>          - type of value
     * @return - value or default if exception
     */
    public static <R> R safeExecute(Supplier<R> function, R defaultValue) {
        return safeExecute(function, Throwable.class, defaultValue);
    }

    /**
     * checks for all objects are not null
     *
     * @param objects - objects
     */
    private static void checkNotNull(Object... objects) {
        if (objects == null) throw badArgument();
        for (Object o : objects) {
            if (o == null) {
                throw badArgument();
            }
        }
    }

    private static IllegalArgumentException badArgument() {
        return new IllegalArgumentException("Arguments cannot be null");
    }
}
