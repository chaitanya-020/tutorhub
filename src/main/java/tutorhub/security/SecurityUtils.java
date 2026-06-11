package tutorhub.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Convenience accessor for the currently authenticated user's id, read from the
 * SecurityContext that JwtAuthenticationFilter populated. Used where a service
 * needs "who am I" without threading the id through every method.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    public static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AppUserPrincipal principal) {
            return principal.getId();
        }
        throw new IllegalStateException("No authenticated user in the security context");
    }
}