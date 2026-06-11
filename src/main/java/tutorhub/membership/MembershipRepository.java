package tutorhub.membership;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Optional<Membership> findByUserIdAndAcademyId(Long userId, Long academyId);

    List<Membership> findByAcademyId(Long academyId);

    List<Membership> findByUserId(Long userId);

    boolean existsByUserIdAndAcademyId(Long userId, Long academyId);
}