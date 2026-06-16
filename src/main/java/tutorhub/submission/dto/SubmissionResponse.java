package tutorhub.submission.dto;

import tutorhub.submission.Submission;
import tutorhub.submission.SubmissionStatus;

import java.time.Instant;

/**
 * version is included so that, when grading arrives in Phase 4b, the client can
 * send back the version it last saw and we can detect concurrent edits.
 */
public record SubmissionResponse(
        Long id,
        Long assignmentId,
        Long studentId,
        String studentName,
        String content,
        SubmissionStatus status,
        Integer score,
        Long version,
        Instant createdAt,
        Instant updatedAt
) {
    public static SubmissionResponse from(Submission s) {
        return new SubmissionResponse(
                s.getId(),
                s.getAssignment().getId(),
                s.getStudent().getId(),
                s.getStudent().getDisplayName(),
                s.getContent(),
                s.getStatus(),
                s.getScore(),
                s.getVersion(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }
}