package tutorhub.assignment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Page<Assignment> findByCourseId(Long courseId, Pageable pageable);

    Optional<Assignment> findByIdAndCourseId(Long id, Long courseId);
}