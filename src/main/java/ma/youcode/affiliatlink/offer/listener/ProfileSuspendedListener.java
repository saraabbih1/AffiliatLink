
package ma.youcode.affiliatlink.offer.listener;

import ma.youcode.affiliatlink.offer.service.CampaignService;
import ma.youcode.affiliatlink.profile.ProfileSuspended;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class ProfileSuspendedListener {

    private static final Logger log =
            LoggerFactory.getLogger(ProfileSuspendedListener.class);

    private final CampaignService campaignService;

    public ProfileSuspendedListener(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @ApplicationModuleListener
    public void onProfileSuspended(ProfileSuspended event) {

        log.info("Received ProfileSuspended event for profile ID: {}",
                event.profileId());

        campaignService.pauseActiveCampaignsForAdvertiser(event.profileId());

        log.info("Campaigns successfully processed for suspended profile ID: {}",
                event.profileId());
    }
}
