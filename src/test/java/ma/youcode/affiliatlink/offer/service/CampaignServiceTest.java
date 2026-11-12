
package ma.youcode.affiliatlink.offer.service;

import ma.youcode.affiliatlink.offer.OfferApi;
import ma.youcode.affiliatlink.offer.entity.Campaign;
import ma.youcode.affiliatlink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatlink.offer.mapper.CampaignMapper;
import ma.youcode.affiliatlink.offer.repository.CampaignRepository;
import ma.youcode.affiliatlink.profile.ProfileApi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private CampaignMapper campaignMapper;

    @Mock
    private OfferApi offerApi;

    @Mock
    private ProfileApi profileApi;

    @InjectMocks
    private CampaignService campaignService;

    @Test
    void shouldPauseAllActiveCampaignsForAdvertiser() {

        Long advertiserId = 1L;
        Long offerId = 10L;

        Campaign campaign1 = mock(Campaign.class);
        Campaign campaign2 = mock(Campaign.class);

        when(offerApi.findOfferIdsByAdvertiserId(advertiserId))
                .thenReturn(List.of(offerId));

        when(campaignRepository.findByOfferIdAndStatus(
                offerId, CampaignStatus.ACTIVE))
                .thenReturn(List.of(campaign1, campaign2));

        campaignService.pauseActiveCampaignsForAdvertiser(advertiserId);

        verify(campaign1).setStatus(CampaignStatus.PAUSED);
        verify(campaign2).setStatus(CampaignStatus.PAUSED);

        verify(campaignRepository).saveAll(List.of(campaign1, campaign2));
    }
    @Test
    void shouldRejectInvalidTransitionFromDraftToEnded() {

        Long campaignId = 1L;
        Campaign campaign = mock(Campaign.class);

        when(campaignRepository.findById(campaignId))
                .thenReturn(Optional.of(campaign));

        when(campaign.getStatus())
                .thenReturn(CampaignStatus.DRAFT);


        assertThatThrownBy(() ->
                campaignService.updateCampaignStatus(
                        campaignId,
                        CampaignStatus.ENDED
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid campaign status transition");

        verify(campaignRepository, never()).save(any(Campaign.class));
    }
    @Test
    void shouldRejectActivationOfExpiredCampaign() {

        Long campaignId = 1L;
        Campaign campaign = mock(Campaign.class);

        when(campaignRepository.findById(campaignId))
                .thenReturn(Optional.of(campaign));

        when(campaign.getStatus())
                .thenReturn(CampaignStatus.DRAFT);

        when(campaign.getEndDate())
                .thenReturn(LocalDate.now().minusDays(1));


        assertThatThrownBy(() ->
                campaignService.updateCampaignStatus(
                        campaignId,
                        CampaignStatus.ACTIVE
                )
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot activate an expired campaign");

        verify(campaignRepository, never()).save(any(Campaign.class));
    }


}
