package com.newasteralnx.util;

public final class CodeGenerator {

    private CodeGenerator() {
    }

    public static String next(String prefix, long number) {
        return prefix + String.format("%06d", number);
    }
}
