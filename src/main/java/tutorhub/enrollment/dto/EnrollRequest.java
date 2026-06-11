package tutorhub.enrollment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Enroll an existing academy member (by email) into a course.
 */
public record EnrollRequest(

        @NotBlank @Email
        String studentEmail
) {}