package com.vk.dwzkf.utils.exception;

public class ExceptionUtil {
    public static Class<Exception1> exceptionParent = Exception1.class;
    public static Class<Exception2> exceptionChild = Exception2.class;
    public static Class<RuntimeException1> runtimeParent = RuntimeException1.class;
    public static Class<RuntimeException2> runtimeChild = RuntimeException2.class;

    public static void exceptionParent() throws Exception1 {
        throw new Exception1();
    }

    public static void exceptionChild() throws Exception2 {
        throw new Exception2();
    }

    public static void runtimeParent() {
        throw new RuntimeException1();
    }

    public static void runtimeChild() {
        throw new RuntimeException2();
    }
}
