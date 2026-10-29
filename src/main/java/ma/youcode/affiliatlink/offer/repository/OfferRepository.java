
package ma.youcode.affiliatlink.offer.repository;

import ma.youcode.affiliatlink.offer.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OfferRepository extends JpaRepository<Offer, Long>,
        JpaSpecificationExecutor<Offer> {
}
