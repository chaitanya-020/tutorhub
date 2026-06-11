package tutorhub.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * The academy is NO LONGER in the body — it comes from the active-academy
 * context (the X-Academy-Id header), enforced server-side. A client can't
 * create a course in an academy it doesn't belong to.
 */
public record CreateCourseRequest(

        @NotBlank @Size(max = 255)
        String title,

        @NotBlank @Size(max = 255)
        String subject,

        @NotBlank @Size(max = 255)
        String term
) {}