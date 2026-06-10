package tutorhub.common;

import java.time.Instant;
import java.util.Map;

/**
 * The JSON shape returned for every error, so clients get a consistent body
 * instead of a stack trace. fieldErrors is only populated for validation failures.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        Map<String, String> fieldErrors
) {
    public static ApiError of(int status, String error, String message) {
        return new ApiError(Instant.now(), status, error, message, null);
    }
}