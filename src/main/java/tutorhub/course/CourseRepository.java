package tutorhub.course;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * findByAcademyId is a "derived query": Spring Data reads the method name and
 * writes the SQL for you (where academy_id = ?). No implementation needed.
 */
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByAcademyId(Long academyId);
}