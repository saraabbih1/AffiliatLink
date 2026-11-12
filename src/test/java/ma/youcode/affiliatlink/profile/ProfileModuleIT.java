
package ma.youcode.affiliatlink.profile;

import ma.youcode.affiliatlink.config.TestcontainersConfiguration;
import ma.youcode.affiliatlink.profile.entity.Advertiser;
import ma.youcode.affiliatlink.profile.entity.enums.ProfileStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.PublishedEvents;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest
@Import(TestcontainersConfiguration.class)
class ProfileModuleIT {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileService profileService;

    @Test
    void shouldPublishEventWhenAdvertiserIsSuspended(
            PublishedEvents events
    ) {

        Advertiser advertiser = new Advertiser();
        advertiser.setEmail("module-test@test.com");
        advertiser.setCompanyName("Test Company");
        advertiser.setSector("Technology");

        advertiser.changeStatus(ProfileStatus.ACTIVE);
        advertiser = profileRepository.saveAndFlush(advertiser);
        Long advertiserId = advertiser.getId();


        profileService.updateProfileStatus(
                advertiser.getId(),
                ProfileStatus.SUSPENDED
        );


        assertThat(events.ofType(ProfileSuspended.class)
                .matching(event -> event.profileId().equals(advertiserId)))
                .hasSize(1);
    }
}
