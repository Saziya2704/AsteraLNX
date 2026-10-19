package com.newasteralnx.util;

import java.util.function.Predicate;

/**
 * Generates the next human-readable code (e.g. SHP000001) by probing for an unused value.
 * A hard upper bound prevents runaway loops if the exists-check is misbehaved.
 */
public final class SequentialCodes {

    private static final int MAX_ATTEMPTS = 10_000;

    private SequentialCodes() {
    }

    public static String next(String prefix, long start, Predicate<String> exists) {
        long number = Math.max(start, 1);
        String code = CodeGenerator.next(prefix, number);
        int attempts = 0;
        while (exists.test(code)) {
            if (++attempts > MAX_ATTEMPTS) {
                throw new IllegalStateException(
                        "Unable to generate unique code for prefix '" + prefix + "' after " + MAX_ATTEMPTS + " attempts");
            }
            number++;
            code = CodeGenerator.next(prefix, number);
        }
        return code;
    }
}
