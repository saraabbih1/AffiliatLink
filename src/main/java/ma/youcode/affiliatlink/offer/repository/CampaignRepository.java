package ma.youcode.affiliatlink.offer.repository;

import ma.youcode.affiliatlink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatlink.offer.entity.Campaign;
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