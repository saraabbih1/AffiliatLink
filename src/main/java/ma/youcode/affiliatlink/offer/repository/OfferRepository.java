package ma.youcode.affiliatlink.offer.repository;

import ma.youcode.affiliatlink.offer.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OfferRepository extends JpaRepository<Offer, Long>,
        JpaSpecificationExecutor<Offer> {

    @Query("SELECT o.advertiserId FROM Offer o WHERE o.id = :id")
    Optional<Long> findAdvertiserIdById(@Param("id") Long id);

    @Query("SELECT o.id FROM Offer o WHERE o.advertiserId = :advertiserId")
    List<Long> findOfferIdsByAdvertiserId(@Param("advertiserId") Long advertiserId);


}