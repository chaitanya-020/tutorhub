package tutorhub.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorhub.academy.Academy;
import tutorhub.academy.AcademyRepository;
import tutorhub.common.ResourceNotFoundException;
import tutorhub.course.dto.CourseResponse;
import tutorhub.course.dto.CreateCourseRequest;
import tutorhub.course.dto.UpdateCourseRequest;

import java.util.List;

/**
 * Business logic for courses. On create we resolve the academyId to a real
 * Academy (404 if it doesn't exist) before attaching it to the new course.
 */
@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final AcademyRepository academyRepository;

    public CourseService(CourseRepository courseRepository,
                         AcademyRepository academyRepository) {
        this.courseRepository = courseRepository;
        this.academyRepository = academyRepository;
    }

    public CourseResponse create(CreateCourseRequest request) {
        Academy academy = academyRepository.findById(request.academyId())
                .orElseThrow(() -> ResourceNotFoundException.of("Academy", request.academyId()));

        Course course = new Course();
        course.setAcademy(academy);
        course.setTitle(request.title());
        course.setSubject(request.subject());
        course.setTerm(request.term());
        return CourseResponse.from(courseRepository.save(course));
    }

    @Transactional(readOnly = true)
    public CourseResponse getById(Long id) {
        return courseRepository.findById(id)
                .map(CourseResponse::from)
                .orElseThrow(() -> ResourceNotFoundException.of("Course", id));
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> list(Long academyId) {
        List<Course> courses = (academyId == null)
                ? courseRepository.findAll()
                : courseRepository.findByAcademyId(academyId);
        return courses.stream().map(CourseResponse::from).toList();
    }

    public CourseResponse update(Long id, UpdateCourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Course", id));
        course.setTitle(request.title());
        course.setSubject(request.subject());
        course.setTerm(request.term());
        return CourseResponse.from(courseRepository.save(course));
    }

    public void delete(Long id) {
        if (!courseRepository.existsById(id)) {
            throw ResourceNotFoundException.of("Course", id);
        }
        courseRepository.deleteById(id);
    }
}