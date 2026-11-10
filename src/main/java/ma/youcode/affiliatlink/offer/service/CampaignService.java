
package ma.youcode.affiliatlink.offer.service;

import ma.youcode.affiliatlink.common.ResourceNotFoundException;
import ma.youcode.affiliatlink.offer.OfferApi;
import ma.youcode.affiliatlink.offer.dto.CampaignResponse;
import ma.youcode.affiliatlink.offer.dto.CreateCampaignRequest;
import ma.youcode.affiliatlink.offer.entity.Campaign;
import ma.youcode.affiliatlink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatlink.offer.entity.enums.PayoutModel;
import ma.youcode.affiliatlink.offer.mapper.CampaignMapper;
import ma.youcode.affiliatlink.offer.repository.CampaignRepository;
import ma.youcode.affiliatlink.profile.ProfileApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class CampaignService {

    private static final Logger log =
            LoggerFactory.getLogger(CampaignService.class);

    private final CampaignRepository campaignRepository;
    private final CampaignMapper campaignMapper;
    private final OfferApi offerApi;
    private final ProfileApi profileApi;

    public CampaignService(
            CampaignRepository campaignRepository,
            CampaignMapper campaignMapper,
            OfferApi offerApi,
            ProfileApi profileApi
    ) {
        this.campaignRepository = campaignRepository;
        this.campaignMapper = campaignMapper;
        this.offerApi = offerApi;
        this.profileApi = profileApi;
    }

    @Transactional
    public CampaignResponse createCampaign(
            Long offerId,
            CreateCampaignRequest request
    ) {
        log.info("Creating campaign for offer ID: {}", offerId);

        if (!offerApi.existsById(offerId)) {
            log.warn("Offer not found with ID: {}", offerId);
            throw new ResourceNotFoundException("Offer not found");
        }

        Long advertiserId = offerApi.findAdvertiserIdByOfferId(offerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Advertiser not found")
                );

        if (!profileApi.isAdvertiserActive(advertiserId)) {
            log.warn("Advertiser ID: {} is not active", advertiserId);
            throw new IllegalStateException("Advertiser is not active");
        }

        validateCampaign(request);

        Campaign campaign = campaignMapper.toEntity(request, offerId);

        Campaign savedCampaign = campaignRepository.save(campaign);

        log.info("Campaign created successfully with ID: {}",
                savedCampaign.getId());

        return campaignMapper.toResponse(savedCampaign);
    }

    private void validateCampaign(CreateCampaignRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Campaign request is required");
        }

        if (request.startDate() == null || request.endDate() == null) {
            throw new IllegalArgumentException("Campaign dates are required");
        }

        if (!request.endDate().isAfter(request.startDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        if (request.budget() == null
                || request.budget().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Budget must be greater than zero");
        }

        if (request.payoutModel() == null || request.payoutValue() == null) {
            throw new IllegalArgumentException("Payout model and value are required");
        }

        PayoutModel payoutModel;

        try {
            payoutModel = PayoutModel.valueOf(
                    request.payoutModel().trim().toUpperCase(Locale.ROOT)
            );
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid payout model");
        }

        if (request.payoutValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payout value must be greater than zero");
        }

        if (payoutModel == PayoutModel.CPA_PERCENT
                && request.payoutValue().compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("CPA percentage cannot exceed 100");
        }
    }

    private boolean isValidTransition(
            CampaignStatus current,
            CampaignStatus next
    ) {
        return switch (current) {
            case DRAFT -> next == CampaignStatus.ACTIVE;
            case ACTIVE -> next == CampaignStatus.PAUSED
                    || next == CampaignStatus.ENDED;
            case PAUSED -> next == CampaignStatus.ACTIVE
                    || next == CampaignStatus.ENDED;
            case ENDED -> false;
        };
    }

    @Transactional
    public CampaignResponse updateCampaignStatus(
            Long campaignId,
            CampaignStatus newStatus
    ) {
        log.info("Updating campaign ID: {} to status: {}", campaignId, newStatus);

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Campaign not found")
                );

        if (newStatus == null) {
            throw new IllegalArgumentException("New status is required");
        }

        if (!isValidTransition(campaign.getStatus(), newStatus)) {
            log.warn("Invalid status transition for campaign ID: {} from {} to {}",
                    campaignId, campaign.getStatus(), newStatus);

            throw new IllegalStateException("Invalid campaign status transition");
        }

        if (newStatus == CampaignStatus.ACTIVE
                && campaign.getEndDate().isBefore(LocalDate.now())) {
            log.warn("Cannot activate expired campaign ID: {}", campaignId);
            throw new IllegalStateException("Cannot activate an expired campaign");
        }

        if (newStatus == CampaignStatus.ACTIVE) {
            List<Campaign> activeCampaigns =
                    campaignRepository.findByOfferIdAndStatus(
                            campaign.getOfferId(),
                            CampaignStatus.ACTIVE
                    );

            activeCampaigns.forEach(activeCampaign ->
                    activeCampaign.setStatus(CampaignStatus.PAUSED)
            );

            campaignRepository.saveAllAndFlush(activeCampaigns);

            if (!activeCampaigns.isEmpty()) {
                log.info("Paused {} previous active campaign(s) for offer ID: {}",
                        activeCampaigns.size(), campaign.getOfferId());
            }
        }

        campaign.setStatus(newStatus);

        Campaign updatedCampaign = campaignRepository.save(campaign);

        log.info("Campaign ID: {} status updated to: {}",
                updatedCampaign.getId(), updatedCampaign.getStatus());

        return campaignMapper.toResponse(updatedCampaign);
    }

    @Transactional(readOnly = true)
    public List<CampaignResponse> getCampaignsByStatus(CampaignStatus status) {

        if (status == null) {
            throw new IllegalArgumentException("Campaign status is required");
        }

        log.debug("Fetching campaigns with status: {}", status);

        return campaignRepository.findByStatus(status)
                .stream()
                .map(campaignMapper::toResponse)
                .toList();
    }

    @Transactional
    public void pauseActiveCampaignsForAdvertiser(Long advertiserId) {

        log.info("Pausing active campaigns for advertiser ID: {}", advertiserId);

        List<Long> offerIds = offerApi.findOfferIdsByAdvertiserId(advertiserId);

        int pausedCount = 0;

        for (Long offerId : offerIds) {

            List<Campaign> activeCampaigns =
                    campaignRepository.findByOfferIdAndStatus(
                            offerId,
                            CampaignStatus.ACTIVE
                    );

            activeCampaigns.forEach(campaign ->
                    campaign.setStatus(CampaignStatus.PAUSED)
            );

            campaignRepository.saveAll(activeCampaigns);

            pausedCount += activeCampaigns.size();
        }

        log.info("Paused {} active campaign(s) for advertiser ID: {}",
                pausedCount, advertiserId);
    }
}
