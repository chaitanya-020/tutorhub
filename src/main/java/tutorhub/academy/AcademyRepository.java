package tutorhub.academy;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data generates the implementation at runtime. Extending JpaRepository
 * gives you save / findById / findAll / deleteById / existsById for free.
 */
public interface AcademyRepository extends JpaRepository<Academy, Long> {
}