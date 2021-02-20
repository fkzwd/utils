package com.vk.dwzkf.utils;

import org.junit.Test;

import java.io.FileNotFoundException;

import static com.vk.dwzkf.utils.CodeUtil.*;

public class CodeUtilTest {
    enum SomeEnum {
        VALUE, OTHER, ADDITIONAL
    }

    private static SomeEnum getRandom() {
        return null;
    }

    @Test
    public void test() {
        SomeEnum value = getRandom();
        if (!value.equals(SomeEnum.OTHER)) {
            //some code
        }

        if (not(value.equals(SomeEnum.OTHER))) {
            //some code
        }
    }

    @Test
    public void strings() {
        String s = "ABCDCDCDDC" +
                "ASDASDASDASD" +
                "ASDASDASDASDASD";

        String s2 = concat(
                "ABCDEFG",
                "ASDASDASD",
                "ASDASDASDASD");
    }

    @Test
    public void testEndsWithAny() {
        String s1 = "ABCDEF";
        if (s1.endsWith("F") || s1.endsWith("EF") || s1.endsWith("DEF")) {
            //actions
        }

        if (endsWithAny(s1, "F", "EF", "DEF")) {
            //actions
        }
    }

    @Test
    public void safeExecuting() {
        Long l1;
        try {
            l1 = Long.parseLong("z");
        } catch (NumberFormatException e) {
            l1 = 0L;
        }

        Long l = safeExecute(() -> Long.parseLong("z"), 0L);

        Long l2 = safeExecute(() -> Long.parseLong("z"),
                FileNotFoundException.class, 0L);
    }

    @Test
    public void testEverything() {
        String someParameter = "aasdasdasdasdasd123";
        String MAY_END_THIS = "3";
        String AVAILABLE_TOO = "23";
        String NOT_AVAILABLE = "z23";

        if (endsWithAny(someParameter, MAY_END_THIS, AVAILABLE_TOO)
                && not(endsWithAny(someParameter, NOT_AVAILABLE))) {
            String result = concat(someParameter, MAY_END_THIS, AVAILABLE_TOO);
            Long resultLong = safeExecute(() -> Long.parseLong(result), -1L);
            System.out.printf("Result is: %d", resultLong);
        }
    }

    @Test
    public void testNothing() {
        String someParameter = "aasdasdasdasdasd123";
        String MAY_END_THIS = "3";
        String AVAILABLE_TOO = "23";
        String NOT_AVAILABLE = "z23";

        if (someParameter.endsWith(MAY_END_THIS)
                || someParameter.endsWith(AVAILABLE_TOO) &&
                !someParameter.endsWith(NOT_AVAILABLE)) {
            String result = someParameter + MAY_END_THIS + AVAILABLE_TOO;
            Long resultLong;
            try {
                resultLong = Long.parseLong(result);
            } catch (NumberFormatException exception) {
                resultLong = -1L;
            }
            System.out.printf("Result is: %d", resultLong);
        }
    }
}
