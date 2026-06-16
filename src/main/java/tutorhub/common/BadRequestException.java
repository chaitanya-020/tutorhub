package tutorhub.common;

/**
 * Thrown for domain-rule violations that aren't simple field validation
 * (e.g. a grade above the assignment's max score). Mapped to HTTP 400.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}