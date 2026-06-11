package tutorhub.enrollment.dto;

import tutorhub.enrollment.Enrollment;

import java.time.Instant;

public record EnrollmentResponse(
        Long enrollmentId,
        Long courseId,
        Long studentId,
        String studentEmail,
        String studentName,
        Instant enrolledAt
) {
    public static EnrollmentResponse from(Enrollment e) {
        return new EnrollmentResponse(
                e.getId(),
                e.getCourse().getId(),
                e.getStudent().getId(),
                e.getStudent().getEmail(),
                e.getStudent().getDisplayName(),
                e.getEnrolledAt()
        );
    }
}