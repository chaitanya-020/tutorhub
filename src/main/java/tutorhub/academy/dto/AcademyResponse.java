package tutorhub.academy.dto;

import tutorhub.academy.Academy;

import java.time.Instant;

/**
 * Outgoing representation of an academy. Keeping a separate response type means
 * the API contract never leaks the JPA entity (and you control exactly which
 * fields are exposed).
 */
public record AcademyResponse(
        Long id,
        String name,
        String slug,
        Instant createdAt,
        Instant updatedAt
) {
    public static AcademyResponse from(Academy a) {
        return new AcademyResponse(
                a.getId(),
                a.getName(),
                a.getSlug(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }
}