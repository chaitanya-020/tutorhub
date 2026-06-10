package tutorhub.course.dto;

import tutorhub.course.Course;

import java.time.Instant;

/**
 * We expose academyId and tutorId rather than nesting whole objects, keeping the
 * payload flat and avoiding accidental lazy-loading of related entities.
 * Reading academy.getId() on the lazy proxy does NOT hit the database.
 */
public record CourseResponse(
        Long id,
        Long academyId,
        String title,
        String subject,
        String term,
        Long tutorId,
        Instant createdAt,
        Instant updatedAt
) {
    public static CourseResponse from(Course c) {
        return new CourseResponse(
                c.getId(),
                c.getAcademy().getId(),
                c.getTitle(),
                c.getSubject(),
                c.getTerm(),
                c.getTutor() != null ? c.getTutor().getId() : null,
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}