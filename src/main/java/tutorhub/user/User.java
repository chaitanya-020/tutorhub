package tutorhub.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorhub.common.BaseAuditableEntity;

/**
 * A person. Deliberately NOT tenant-scoped: one login can belong to several
 * academies, and the link (plus role) lives in Membership. That mirrors how
 * real SaaS works and is what creates the "which academy am I acting in?" problem.
 *
 * Table is named "app_user" because USER is a reserved word in PostgreSQL.
 * passwordHash stays empty until Phase 2 (registration); for now users, if you
 * need any, can be inserted directly.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class User extends BaseAuditableEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "display_name", nullable = false)
    private String displayName;
}