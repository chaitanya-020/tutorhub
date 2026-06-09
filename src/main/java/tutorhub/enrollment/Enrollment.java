package tutorhub.enrollment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorhub.common.BaseAuditableEntity;
import tutorhub.course.Course;
import tutorhub.user.User;

import java.time.Instant;

/**
 * A student joining a course. The unique constraint prevents enrolling the same
 * student twice. Tenant scope is derived through the course (course -> academy).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "enrollment",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_enrollment_course_student",
                columnNames = {"course_id", "student_id"}
        )
)
public class Enrollment extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "enrolled_at", nullable = false)
    private Instant enrolledAt;
}