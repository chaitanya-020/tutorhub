package tutorhub.academy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tutorhub.academy.dto.AcademyResponse;
import tutorhub.academy.dto.CreateAcademyRequest;
import tutorhub.academy.dto.UpdateAcademyRequest;
import tutorhub.common.ForbiddenException;
import tutorhub.membership.Membership;
import tutorhub.membership.MembershipRepository;
import tutorhub.membership.Role;
import tutorhub.user.User;
import tutorhub.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pure unit tests: no Spring context, no database. The repositories are mocked,
 * so we test ONLY the logic inside AcademyService. Fast and focused.
 */
@ExtendWith(MockitoExtension.class)
class AcademyServiceTest {

    @Mock AcademyRepository academyRepository;
    @Mock MembershipRepository membershipRepository;
    @Mock UserRepository userRepository;

    @InjectMocks AcademyService academyService;

    @Test
    void create_makesCreatorADirector() {
        CreateAcademyRequest request = new CreateAcademyRequest("Bright Minds", "bright-minds");
        Academy saved = new Academy();
        saved.setName("Bright Minds");
        saved.setSlug("bright-minds");
        when(academyRepository.save(any(Academy.class))).thenReturn(saved);
        when(userRepository.getReferenceById(1L)).thenReturn(new User());

        AcademyResponse response = academyService.create(request, 1L);

        assertThat(response.name()).isEqualTo("Bright Minds");
        // The key behaviour: a DIRECTOR membership is created for the creator.
        verify(membershipRepository).save(argThat(m -> m.getRole() == Role.DIRECTOR));
    }

    @Test
    void update_byNonMember_isForbidden() {
        when(membershipRepository.findByUserIdAndAcademyId(1L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> academyService.update(5L, new UpdateAcademyRequest("New name"), 1L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void update_byNonDirectorMember_isForbidden() {
        Membership tutorMembership = new Membership();
        tutorMembership.setRole(Role.TUTOR);
        when(membershipRepository.findByUserIdAndAcademyId(1L, 5L)).thenReturn(Optional.of(tutorMembership));

        assertThatThrownBy(() -> academyService.update(5L, new UpdateAcademyRequest("New name"), 1L))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getById_byNonMember_isForbidden() {
        when(membershipRepository.findByUserIdAndAcademyId(1L, 5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> academyService.getById(5L, 1L))
                .isInstanceOf(ForbiddenException.class);
    }
}