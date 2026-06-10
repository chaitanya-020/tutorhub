package tutorhub.academy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tutorhub.academy.dto.AcademyResponse;
import tutorhub.academy.dto.CreateAcademyRequest;
import tutorhub.academy.dto.UpdateAcademyRequest;
import tutorhub.common.ResourceNotFoundException;

import java.util.List;

/**
 * Business logic for academies. The class is @Transactional so each method runs
 * in a database transaction; reads are marked readOnly for a small optimization.
 * Controllers call this; this calls the repository.
 */
@Service
@Transactional
public class AcademyService {

    private final AcademyRepository academyRepository;

    public AcademyService(AcademyRepository academyRepository) {
        this.academyRepository = academyRepository;
    }

    public AcademyResponse create(CreateAcademyRequest request) {
        Academy academy = new Academy();
        academy.setName(request.name());
        academy.setSlug(request.slug());
        return AcademyResponse.from(academyRepository.save(academy));
    }

    @Transactional(readOnly = true)
    public AcademyResponse getById(Long id) {
        return academyRepository.findById(id)
                .map(AcademyResponse::from)
                .orElseThrow(() -> ResourceNotFoundException.of("Academy", id));
    }

    @Transactional(readOnly = true)
    public List<AcademyResponse> list() {
        return academyRepository.findAll().stream()
                .map(AcademyResponse::from)
                .toList();
    }

    public AcademyResponse update(Long id, UpdateAcademyRequest request) {
        Academy academy = academyRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Academy", id));
        academy.setName(request.name());
        return AcademyResponse.from(academyRepository.save(academy));
    }

    public void delete(Long id) {
        if (!academyRepository.existsById(id)) {
            throw ResourceNotFoundException.of("Academy", id);
        }
        academyRepository.deleteById(id);
    }
}