package tutorhub.common;

/**
 * Thrown when an authenticated user is not allowed to perform an action.
 * Mapped to HTTP 403 by GlobalExceptionHandler.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}