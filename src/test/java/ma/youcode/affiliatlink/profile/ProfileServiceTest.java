
package ma.youcode.affiliatlink.profile;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.Optional;
import java.util.List;
import ma.youcode.affiliatlink.profile.entity.Advertiser;
import ma.youcode.affiliatlink.common.DuplicateEmailException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import ma.youcode.affiliatlink.profile.entity.enums.ProfileStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import ma.youcode.affiliatlink.profile.entity.Publisher;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ma.youcode.affiliatlink.profile.entity.enums.Niche;
import org.junit.jupiter.api.Test;
@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ProfileService profileService;
    @Test
    void shouldCreatePublisherSuccessfully() {


        PublisherCreateRequest request = new PublisherCreateRequest(
                "publisher@test.com",
                "Sarra",
                "Abbih",
                Niche.TECH,
                "Tech content creator",
                "Morocco"
        );
        when(profileRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());
        when(profileRepository.save(any(Publisher.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        PublisherResponse response = profileService.createPublisher(request);

        assertThat(response.email()).isEqualTo("publisher@test.com");
        assertThat(response.firstName()).isEqualTo("Sarra");
        assertThat(response.lastName()).isEqualTo("Abbih");
        assertThat(response.niche()).isEqualTo(Niche.TECH);
        assertThat(response.status()).isEqualTo(ProfileStatus.PENDING);

        verify(profileRepository).save(any(Publisher.class));
    }
    @Test
    void shouldRejectDuplicatePublisherEmail() {

        PublisherCreateRequest request = new PublisherCreateRequest(
                "publisher@test.com",
                "Sarra",
                "Abbih",
                Niche.TECH,
                "Tech content creator",
                "Morocco"
        );

        when(profileRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(new Publisher()));

        assertThatThrownBy(() -> profileService.createPublisher(request))
                .isInstanceOf(DuplicateEmailException.class);

        verify(profileRepository, never()).save(any(Publisher.class));
    }
    @Test
    void shouldFindPublisherById() {

        Long publisherId = 1L;

        Publisher publisher = new Publisher();
        publisher.setEmail("publisher@test.com");
        publisher.setFirstName("Sarra");
        publisher.setLastName("Abbih");

        when(profileRepository.findById(publisherId))
                .thenReturn(Optional.of(publisher));

        Optional<PublisherResponse> result =
                profileService.findPublisherById(publisherId);

        assertThat(result).isPresent();
        assertThat(result.get().email()).isEqualTo("publisher@test.com");
        assertThat(result.get().firstName()).isEqualTo("Sarra");

        verify(profileRepository).findById(publisherId);
    }

    @Test
    void shouldReturnEmptyWhenPublisherNotFound() {

        Long publisherId = 999L;

        when(profileRepository.findById(publisherId))
                .thenReturn(Optional.empty());

        Optional<PublisherResponse> result =
                profileService.findPublisherById(publisherId);

        assertThat(result).isEmpty();

        verify(profileRepository).findById(publisherId);
    }

    @Test
    void shouldFindAllPublishers() {

        Publisher publisher = new Publisher();
        publisher.setEmail("publisher@test.com");
        publisher.setFirstName("Sara");

        Advertiser advertiser = new Advertiser();
        advertiser.setEmail("advertiser@test.com");

        when(profileRepository.findAll())
                .thenReturn(List.of(publisher, advertiser));

        List<PublisherResponse> result =
                profileService.findAllPublishers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).email())
                .isEqualTo("publisher@test.com");

        verify(profileRepository).findAll();
    }

    @Test
    void shouldCreateAdvertiserSuccessfully() {


        AdvertiserCreateRequest request = new AdvertiserCreateRequest(
                "advertiser@test.com",
                "Tech Company",
                "Technology",
                "https://techcompany.com"
        );

        when(profileRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        when(profileRepository.save(any(Advertiser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AdvertiserResponse response =
                profileService.createAdvertiser(request);

        assertThat(response.email()).isEqualTo("advertiser@test.com");
        assertThat(response.companyName()).isEqualTo("Tech Company");
        assertThat(response.sector()).isEqualTo("Technology");
        assertThat(response.website()).isEqualTo("https://techcompany.com");
        assertThat(response.status()).isEqualTo(ProfileStatus.PENDING);

        verify(profileRepository).save(any(Advertiser.class));
    }

    @Test
    void shouldRejectDuplicateAdvertiserEmail() {

        AdvertiserCreateRequest request = new AdvertiserCreateRequest(
                "advertiser@test.com",
                "Tech Company",
                "Technology",
                "https://techcompany.com"
        );

        when(profileRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(new Advertiser()));

        assertThatThrownBy(() -> profileService.createAdvertiser(request))
                .isInstanceOf(DuplicateEmailException.class);

        verify(profileRepository, never()).save(any(Advertiser.class));
    }

    @Test
    void shouldFindAdvertiserById() {

        Long advertiserId = 1L;

        Advertiser advertiser = new Advertiser();
        advertiser.setEmail("advertiser@test.com");
        advertiser.setCompanyName("Tech Company");
        advertiser.setSector("Technology");
        advertiser.setWebsite("https://techcompany.com");

        when(profileRepository.findById(advertiserId))
                .thenReturn(Optional.of(advertiser));

        AdvertiserResponse response =
                profileService.findAdvertiserById(advertiserId);

        assertThat(response.email()).isEqualTo("advertiser@test.com");
        assertThat(response.companyName()).isEqualTo("Tech Company");
        assertThat(response.sector()).isEqualTo("Technology");
        assertThat(response.website()).isEqualTo("https://techcompany.com");

        verify(profileRepository).findById(advertiserId);
    }

}
