package tutorhub.membership;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorhub.academy.Academy;
import tutorhub.academy.AcademyRepository;
import tutorhub.common.ResourceNotFoundException;
import tutorhub.membership.dto.AddMemberRequest;
import tutorhub.membership.dto.MemberResponse;
import tutorhub.security.TenantContext;
import tutorhub.user.User;
import tutorhub.user.UserRepository;

import java.util.List;

@Service
@Transactional
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final AcademyRepository academyRepository;
    private final UserRepository userRepository;

    public MembershipService(MembershipRepository membershipRepository,
                             AcademyRepository academyRepository,
                             UserRepository userRepository) {
        this.membershipRepository = membershipRepository;
        this.academyRepository = academyRepository;
        this.userRepository = userRepository;
    }

    public MemberResponse addMember(AddMemberRequest request) {
        Long academyId = TenantContext.academyId();

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No registered user with email " + request.email()));

        Academy academy = academyRepository.getReferenceById(academyId);

        Membership membership = new Membership();
        membership.setAcademy(academy);
        membership.setUser(user);
        membership.setRole(request.role());
        // Adding the same user twice is blocked by the unique (user, academy)
        // constraint, which surfaces as a 409 via the global handler.
        return MemberResponse.from(membershipRepository.save(membership));
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> listMembers() {
        return membershipRepository.findByAcademyId(TenantContext.academyId())
                .stream().map(MemberResponse::from).toList();
    }
}