
package ma.youcode.affiliatlink.profile;

import ma.youcode.affiliatlink.common.DuplicateEmailException;
import ma.youcode.affiliatlink.profile.entity.Publisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Optional<PublisherResponse> findPublisherById(Long id) {
        return profileRepository.findById(id)
                .filter(Publisher.class::isInstance)
                .map(Publisher.class::cast)
                .map(this::toResponse);
    }

    public PublisherResponse createPublisher(PublisherCreateRequest request) {

        if (profileRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateEmailException(request.email());
        }

        Publisher publisher = new Publisher();
        publisher.setEmail(request.email());
        publisher.setFirstName(request.firstName());
        publisher.setLastName(request.lastName());
        publisher.setNiche(request.niche());
        publisher.setBio(request.bio());
        publisher.setCountry(request.country());

        Publisher saved = profileRepository.save(publisher);
        return toResponse(saved);
    }

    private PublisherResponse toResponse(Publisher publisher) {
        return new PublisherResponse(
                publisher.getId(),
                publisher.getEmail(),
                publisher.getStatus(),
                publisher.getCreatedAt(),
                publisher.getFirstName(),
                publisher.getLastName(),
                publisher.getNiche(),
                publisher.getBio(),
                publisher.getCountry()
        );
    }

    public List<PublisherResponse> findAllPublishers() {
        return profileRepository.findAll().stream()
                .filter(Publisher.class::isInstance)
                .map(Publisher.class::cast)
                .map(this::toResponse)
                .toList();
    }
}
