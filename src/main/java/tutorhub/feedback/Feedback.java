package tutorhub.feedback;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorhub.common.BaseAuditableEntity;
import tutorhub.submission.Submission;
import tutorhub.user.User;

/**
 * A tutor's note on a submission. author is the User who wrote it.
 * Tenant scope is derived through submission -> assignment -> course -> academy.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "feedback")
public class Feedback extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(columnDefinition = "text", nullable = false)
    private String body;
}