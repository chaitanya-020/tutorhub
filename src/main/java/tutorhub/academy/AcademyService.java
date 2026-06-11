package tutorhub.academy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorhub.academy.dto.AcademyResponse;
import tutorhub.academy.dto.CreateAcademyRequest;
import tutorhub.academy.dto.UpdateAcademyRequest;
import tutorhub.common.ForbiddenException;
import tutorhub.membership.Membership;
import tutorhub.membership.Role;
import tutorhub.user.User;
import tutorhub.user.UserRepository;
import tutorhub.membership.MembershipRepository;
import java.util.List;

/**
 * Academy-level operations. Note these authorize directly against the path id
 * (via the current user's membership), NOT via the X-Academy-Id header — the
 * academy is the resource being addressed here.
 */
@Service
@Transactional
public class AcademyService {

    private final AcademyRepository academyRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;

    public AcademyService(AcademyRepository academyRepository,
                          MembershipRepository membershipRepository,
                          UserRepository userRepository) {
        this.academyRepository = academyRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
    }

    /** Creating an academy makes the creator its DIRECTOR. */
    public AcademyResponse create(CreateAcademyRequest request, Long currentUserId) {
        Academy academy = new Academy();
        academy.setName(request.name());
        academy.setSlug(request.slug());
        academy = academyRepository.save(academy);

        User creator = userRepository.getReferenceById(currentUserId);
        Membership membership = new Membership();
        membership.setAcademy(academy);
        membership.setUser(creator);
        membership.setRole(Role.DIRECTOR);
        membershipRepository.save(membership);

        return AcademyResponse.from(academy);
    }

    /** Only the academies the current user belongs to. */
    @Transactional(readOnly = true)
    public List<AcademyResponse> listForUser(Long currentUserId) {
        return membershipRepository.findByUserId(currentUserId).stream()
                .map(m -> AcademyResponse.from(m.getAcademy()))
                .toList();
    }

    @Transactional(readOnly = true)
    public AcademyResponse getById(Long id, Long currentUserId) {
        requireMember(currentUserId, id);
        return AcademyResponse.from(academyRepository.getReferenceById(id));
    }

    public AcademyResponse update(Long id, UpdateAcademyRequest request, Long currentUserId) {
        requireDirector(currentUserId, id);
        Academy academy = academyRepository.getReferenceById(id);
        academy.setName(request.name());
        return AcademyResponse.from(academyRepository.save(academy));
    }

    public void delete(Long id, Long currentUserId) {
        requireDirector(currentUserId, id);
        academyRepository.deleteById(id);
    }

    private Membership requireMember(Long userId, Long academyId) {
        return membershipRepository.findByUserIdAndAcademyId(userId, academyId)
                .orElseThrow(() -> new ForbiddenException("You are not a member of this academy."));
    }

    private void requireDirector(Long userId, Long academyId) {
        Membership membership = requireMember(userId, academyId);
        if (membership.getRole() != Role.DIRECTOR) {
            throw new ForbiddenException("Only a DIRECTOR can perform this action.");
        }
    }
}