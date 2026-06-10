package tutorhub.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Update payload. A course can't be moved to a different academy, so academyId
 * is intentionally not editable here.
 */
public record UpdateCourseRequest(

        @NotBlank @Size(max = 255)
        String title,

        @NotBlank @Size(max = 255)
        String subject,

        @NotBlank @Size(max = 255)
        String term
) {}