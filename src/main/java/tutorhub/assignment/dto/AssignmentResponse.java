package tutorhub.assignment.dto;

import tutorhub.assignment.Assignment;

import java.time.Instant;

public record AssignmentResponse(
        Long id,
        Long courseId,
        String title,
        String instructions,
        Instant dueDate,
        Integer maxScore,
        Instant createdAt,
        Instant updatedAt
) {
    public static AssignmentResponse from(Assignment a) {
        return new AssignmentResponse(
                a.getId(),
                a.getCourse().getId(),
                a.getTitle(),
                a.getInstructions(),
                a.getDueDate(),
                a.getMaxScore(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }
}