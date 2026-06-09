package tutorhub.submission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorhub.assignment.Assignment;
import tutorhub.common.BaseAuditableEntity;
import tutorhub.user.User;

/**
 * A student's response to an assignment. One submission per student per
 * assignment (unique constraint). score stays null until graded.
 *
 * The @Version field enables optimistic locking: if two tutors load the same
 * submission and both try to grade it, the second save fails instead of
 * silently overwriting the first. We use this in Phase 4.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "submission",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_submission_assignment_student",
                columnNames = {"assignment_id", "student_id"}
        )
)
public class Submission extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(columnDefinition = "text")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubmissionStatus status;

    @Column
    private Integer score;

    @Version
    @Column(nullable = false)
    private Long version;
}