package tutorhub.academy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Update payload. We allow renaming but keep the slug fixed, since it's the
 * stable public identifier.
 */
public record UpdateAcademyRequest(

        @NotBlank
        @Size(max = 255)
        String name
) {}