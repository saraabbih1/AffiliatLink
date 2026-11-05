package ma.youcode.affiliatlink.campaign.repository;

import ma.youcode.affiliatlink.campaign.entity.Campaign;
import ma.youcode.affiliatlink.campaign.enums.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByOfferIdAndStatus(
            Long offerId,
            CampaignStatus status
    );
    List<Campaign> findByStatus(CampaignStatus status);
}