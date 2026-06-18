package tutorhub.assignment;

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
import tutorhub.course.Course;

import java.time.Instant;

/**
 * Work issued in a course. Tenant scope is derived through the course.
 * instructions is mapped to a TEXT column for long content.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "assignment")
public class Assignment extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String instructions;

    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @Column(name = "max_score", nullable = false)
    private Integer maxScore;

    /**
     * When a due-soon reminder was sent for this assignment. Null until sent;
     * the reminder job uses this to avoid emailing students twice.
     */
    @Column(name = "reminder_sent_at")
    private Instant reminderSentAt;
}