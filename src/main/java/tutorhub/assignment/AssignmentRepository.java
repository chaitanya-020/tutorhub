package tutorhub.assignment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    Page<Assignment> findByCourseId(Long courseId, Pageable pageable);

    Optional<Assignment> findByIdAndCourseId(Long id, Long courseId);

    // Reminder job: assignments due within a window that haven't been reminded yet.
    List<Assignment> findByReminderSentAtIsNullAndDueDateBetween(Instant start, Instant end);
}