package tutorhub.membership.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tutorhub.membership.Role;

/**
 * Adds an existing (already-registered) user to the active academy with a role.
 * role is parsed from a string like "TUTOR" by Jackson.
 */
public record AddMemberRequest(

        @NotBlank @Email
        String email,

        @NotNull
        Role role
) {}