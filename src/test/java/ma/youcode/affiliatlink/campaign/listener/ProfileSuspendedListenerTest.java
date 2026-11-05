
package ma.youcode.affiliatlink.campaign.listener;

import ma.youcode.affiliatlink.campaign.service.CampaignService;
import ma.youcode.affiliatlink.profile.ProfileSuspended;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfileSuspendedListenerTest {

    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private ProfileSuspendedListener listener;

    @Test
    void shouldPauseCampaignsWhenProfileIsSuspended() {

        Long profileId = 1L;

        ProfileSuspended event = new ProfileSuspended(profileId);

        listener.onProfileSuspended(event);

        verify(campaignService)
                .pauseActiveCampaignsForAdvertiser(profileId);
    }
}
