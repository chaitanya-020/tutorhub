package tutorhub.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Note we take academyId (a plain Long), not an Academy object. The client
 * references the parent by id; the service resolves it to a real entity.
 */
public record CreateCourseRequest(

        @NotNull
        Long academyId,

        @NotBlank @Size(max = 255)
        String title,

        @NotBlank @Size(max = 255)
        String subject,

        @NotBlank @Size(max = 255)
        String term
) {}