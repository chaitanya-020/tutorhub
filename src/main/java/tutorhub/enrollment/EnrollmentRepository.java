package tutorhub.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    boolean existsByCourseIdAndStudentId(Long courseId, Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    // The underscores walk the path explicitly: student.id and course.academy.id.
    // Used to list the courses a given student is enrolled in within one academy.
    List<Enrollment> findByStudent_IdAndCourse_Academy_Id(Long studentId, Long academyId);

    Optional<Enrollment> findByIdAndCourseId(Long id, Long courseId);
}