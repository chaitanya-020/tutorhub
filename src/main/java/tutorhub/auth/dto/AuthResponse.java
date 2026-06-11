package tutorhub.auth.dto;

/**
 * Returned by register and login. The client stores the token and sends it back
 * as "Authorization: Bearer <token>" on every subsequent request.
 */
public record AuthResponse(
        String token,
        String tokenType,
        String email,
        String displayName
) {
    public static AuthResponse bearer(String token, String email, String displayName) {
        return new AuthResponse(token, "Bearer", email, displayName);
    }
}