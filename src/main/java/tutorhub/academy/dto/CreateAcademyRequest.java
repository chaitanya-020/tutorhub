package tutorhub.academy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Incoming payload for creating an academy. Validation annotations are enforced
 * by @Valid in the controller; failures become a 400 with field details.
 */
public record CreateAcademyRequest(

        @NotBlank
        @Size(max = 255)
        String name,

        @NotBlank
        @Size(max = 255)
        @Pattern(regexp = "^[a-z0-9-]+$",
                message = "slug must contain only lowercase letters, numbers, and hyphens")
        String slug
) {}