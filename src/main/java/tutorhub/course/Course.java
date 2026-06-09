package tutorhub.course;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorhub.academy.Academy;
import tutorhub.common.BaseAuditableEntity;
import tutorhub.user.User;

/**
 * A class/cohort inside one academy (e.g. "SAT Math - Fall 2026").
 * Carries academy_id, so it is directly tenant-scoped. The tutor is optional
 * so you can create courses in Phase 1 before any users exist.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "course")
public class Course extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academy_id", nullable = false)
    private Academy academy;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id")
    private User tutor;
}