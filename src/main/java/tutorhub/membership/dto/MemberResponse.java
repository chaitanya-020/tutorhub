package tutorhub.membership.dto;

import tutorhub.membership.Membership;
import tutorhub.membership.Role;

public record MemberResponse(
        Long membershipId,
        Long userId,
        String email,
        String displayName,
        Role role
) {
    public static MemberResponse from(Membership m) {
        return new MemberResponse(
                m.getId(),
                m.getUser().getId(),
                m.getUser().getEmail(),
                m.getUser().getDisplayName(),
                m.getRole()
        );
    }
}