
package ma.youcode.affiliatlink.campaign.controller;

import ma.youcode.affiliatlink.campaign.dto.CampaignResponse;
import ma.youcode.affiliatlink.campaign.dto.CreateCampaignRequest;
import ma.youcode.affiliatlink.campaign.enums.CampaignStatus;
import ma.youcode.affiliatlink.campaign.service.CampaignService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CampaignController {

    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping("/offres/{offerId}/campaigns")
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignResponse createCampaign(
            @PathVariable Long offerId,
            @RequestBody CreateCampaignRequest request
    ) {
        return campaignService.createCampaign(offerId, request);
    }

    @PatchMapping("/campaigns/{campaignId}/status")
    public CampaignResponse updateCampaignStatus(
            @PathVariable Long campaignId,
            @RequestParam CampaignStatus status
    ) {
        return campaignService.updateCampaignStatus(campaignId, status);
    }
    @GetMapping("/campaigns")
    public List<CampaignResponse> getCampaignsByStatus(
            @RequestParam CampaignStatus status
    ) {
        return campaignService.getCampaignsByStatus(status);
    }
}
