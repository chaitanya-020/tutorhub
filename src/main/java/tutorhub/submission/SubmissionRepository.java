package tutorhub.submission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Page<Submission> findByAssignmentId(Long assignmentId, Pageable pageable);

    Page<Submission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId, Pageable pageable);

    boolean existsByAssignmentIdAndStudentId(Long assignmentId, Long studentId);
}