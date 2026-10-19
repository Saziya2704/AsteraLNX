package com.newasteralnx.util;

import com.newasteralnx.exception.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Long requireCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AuthenticationException("Authentication is required");
        }
        try {
            return Long.parseLong(authentication.getPrincipal().toString());
        } catch (NumberFormatException exception) {
            throw new AuthenticationException("Invalid authentication principal");
        }
    }

    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return authentication.getAuthorities().stream()
                .anyMatch(granted -> authority.equals(granted.getAuthority()));
    }
}
