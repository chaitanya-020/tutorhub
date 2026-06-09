package tutorhub.academy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tutorhub.common.BaseAuditableEntity;

/**
 * The tenant root. Every academy-owned row is ultimately isolated by which
 * Academy it belongs to. Academy itself has no parent.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "academy")
public class Academy extends BaseAuditableEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;
}