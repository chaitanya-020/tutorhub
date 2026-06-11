package tutorhub.common;

/**
 * Thrown when a tenant-scoped operation runs without an active academy.
 * Mapped to HTTP 400. In practice @PreAuthorize usually rejects such requests
 * first (with 403), so this is mainly a safety net.
 */
public class MissingAcademyContextException extends RuntimeException {

    public MissingAcademyContextException() {
        super("This operation requires an active academy. Set the X-Academy-Id header "
                + "to an academy you belong to.");
    }
}