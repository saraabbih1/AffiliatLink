
package ma.youcode.affiliatlink.offer.controller;

import ma.youcode.affiliatlink.offer.dto.CampaignResponse;
import ma.youcode.affiliatlink.offer.dto.CreateCampaignRequest;
import ma.youcode.affiliatlink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatlink.offer.service.CampaignService;
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
