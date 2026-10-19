package com.newasteralnx.constant;

public final class PasswordRules {

    public static final String REGEXP =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$";

    public static final String MESSAGE =
            "Password must be at least 8 characters and contain one uppercase letter, one lowercase letter, one number and one special character";

    private PasswordRules() {
    }
}
