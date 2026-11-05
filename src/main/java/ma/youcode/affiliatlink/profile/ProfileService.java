
package ma.youcode.affiliatlink.profile;

import ma.youcode.affiliatlink.common.DuplicateEmailException;
import ma.youcode.affiliatlink.common.ResourceNotFoundException;
import ma.youcode.affiliatlink.profile.entity.Advertiser;
import ma.youcode.affiliatlink.profile.entity.Profile;
import ma.youcode.affiliatlink.profile.entity.Publisher;
import ma.youcode.affiliatlink.profile.entity.enums.ProfileStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService implements ProfileApi {

    private static final Logger log =
            LoggerFactory.getLogger(ProfileService.class);

    private final ProfileRepository profileRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProfileService(
            ProfileRepository profileRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.profileRepository = profileRepository;
        this.eventPublisher = eventPublisher;
    }

    public Optional<PublisherResponse> findPublisherById(Long id) {
        log.debug("Fetching publisher with ID: {}", id);

        return profileRepository.findById(id)
                .filter(Publisher.class::isInstance)
                .map(Publisher.class::cast)
                .map(this::toResponse);
    }

    public PublisherResponse createPublisher(PublisherCreateRequest request) {

        if (profileRepository.findByEmail(request.email()).isPresent()) {
            log.warn("Publisher creation failed: email already exists");
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

        log.info("Publisher created successfully with ID: {}", saved.getId());

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
        log.debug("Fetching all publishers");

        return profileRepository.findAll().stream()
                .filter(Publisher.class::isInstance)
                .map(Publisher.class::cast)
                .map(this::toResponse)
                .toList();
    }

    public AdvertiserResponse createAdvertiser(AdvertiserCreateRequest request) {

        if (profileRepository.findByEmail(request.email()).isPresent()) {
            log.warn("Advertiser creation failed: email already exists");
            throw new DuplicateEmailException(request.email());
        }

        Advertiser advertiser = new Advertiser();
        advertiser.setEmail(request.email());
        advertiser.setCompanyName(request.companyName());
        advertiser.setSector(request.sector());
        advertiser.setWebsite(request.website());

        Advertiser saved = profileRepository.save(advertiser);

        log.info("Advertiser created successfully with ID: {}", saved.getId());

        return new AdvertiserResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getCompanyName(),
                saved.getSector(),
                saved.getWebsite()
        );
    }

    public AdvertiserResponse findAdvertiserById(Long id) {
        log.debug("Fetching advertiser with ID: {}", id);

        Advertiser advertiser = profileRepository.findById(id)
                .filter(Advertiser.class::isInstance)
                .map(Advertiser.class::cast)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Advertiser not found"));

        return new AdvertiserResponse(
                advertiser.getId(),
                advertiser.getEmail(),
                advertiser.getStatus(),
                advertiser.getCreatedAt(),
                advertiser.getCompanyName(),
                advertiser.getSector(),
                advertiser.getWebsite()
        );
    }

    public List<AdvertiserResponse> findAllAdvertisers() {
        log.debug("Fetching all advertisers");

        return profileRepository.findAll().stream()
                .filter(Advertiser.class::isInstance)
                .map(Advertiser.class::cast)
                .map(advertiser -> new AdvertiserResponse(
                        advertiser.getId(),
                        advertiser.getEmail(),
                        advertiser.getStatus(),
                        advertiser.getCreatedAt(),
                        advertiser.getCompanyName(),
                        advertiser.getSector(),
                        advertiser.getWebsite()
                ))
                .toList();
    }

    @Transactional
    public void updateProfileStatus(Long id, ProfileStatus status) {

        log.info("Updating profile ID: {} to status: {}", id, status);

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Profile not found with ID: {}", id);
                    return new ResourceNotFoundException("Profile not found");
                });

        ProfileStatus previousStatus = profile.getStatus();

        profile.changeStatus(status);
        profileRepository.save(profile);

        log.info("Profile ID: {} status changed from {} to {}",
                id, previousStatus, status);

        if (previousStatus == ProfileStatus.ACTIVE
                && status == ProfileStatus.SUSPENDED) {

            log.info("Publishing ProfileSuspended event for profile ID: {}",
                    profile.getId());

            eventPublisher.publishEvent(
                    new ProfileSuspended(profile.getId())
            );
        }
    }

    @Override
    public boolean isAdvertiserActive(Long advertiserId) {
        log.debug("Checking if advertiser ID: {} is active", advertiserId);

        return profileRepository.findById(advertiserId)
                .filter(Advertiser.class::isInstance)
                .map(Advertiser.class::cast)
                .map(advertiser ->
                        advertiser.getStatus() == ProfileStatus.ACTIVE)
                .orElse(false);
    }
}
