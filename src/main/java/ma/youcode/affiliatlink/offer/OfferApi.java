package ma.youcode.affiliatlink.offer;

import java.util.List;
import java.util.Optional;

public interface OfferApi {

    boolean existsById(Long offerId);

    Optional<Long> findAdvertiserIdByOfferId(Long offerId);

    List<Long> findOfferIdsByAdvertiserId(Long advertiserId);
}