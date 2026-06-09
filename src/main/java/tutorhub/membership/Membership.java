package tutorhub.membership;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorhub.academy.Academy;
import tutorhub.common.BaseAuditableEntity;
import tutorhub.user.User;

/**
 * Links a User to an Academy with a Role. The unique constraint enforces
 * "one membership per user per academy". This row carries academy_id, so it is
 * directly tenant-scoped.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "membership",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_membership_user_academy",
                columnNames = {"user_id", "academy_id"}
        )
)
public class Membership extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academy_id", nullable = false)
    private Academy academy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
}