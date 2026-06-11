package tutorhub.security;

import tutorhub.common.MissingAcademyContextException;
import tutorhub.membership.Role;

/**
 * Holds the "active academy" (and the current user's role in it) for the duration
 * of one request, on the request's own thread. Set by TenantFilter, read by
 * services to scope every query, and cleared at the end of the request.
 *
 * A ThreadLocal (rather than a request-scoped bean) keeps this simple and
 * dependency-free — but it MUST be cleared in a finally block (TenantFilter does).
 */
public final class TenantContext {

    public record TenantInfo(Long academyId, Role role) {}

    private static final ThreadLocal<TenantInfo> HOLDER = new ThreadLocal<>();

    private TenantContext() {}

    public static void set(Long academyId, Role role) {
        HOLDER.set(new TenantInfo(academyId, role));
    }

    public static boolean isSet() {
        return HOLDER.get() != null;
    }

    /** The active academy id, or throws (400) if no academy context was set. */
    public static Long academyId() {
        TenantInfo info = HOLDER.get();
        if (info == null) {
            throw new MissingAcademyContextException();
        }
        return info.academyId();
    }

    public static Role role() {
        TenantInfo info = HOLDER.get();
        return info != null ? info.role() : null;
    }

    public static void clear() {
        HOLDER.remove();
    }
}