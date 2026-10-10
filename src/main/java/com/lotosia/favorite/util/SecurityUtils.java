package com.lotosia.favorite.util;

import com.lotosia.favorite.config.JwtAuthDetails;
import com.lotosia.favorite.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author: nijataghayev
 * Utility class to securely extract current authenticated user information from SecurityContext.
 */

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static Long getCurrentUserId() {
        return getDetails().userId();
    }

    public static String getCurrentEmail() {
        return getDetails().email();
    }

    private static JwtAuthDetails getDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getDetails() instanceof JwtAuthDetails details) || details.userId() == null) {
            throw new UnauthorizedException("Autentifikasiya məlumatları tapılmadı və ya etibarsızdır");
        }
        return details;
    }
}
