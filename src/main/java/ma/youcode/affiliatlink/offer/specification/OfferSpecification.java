
package ma.youcode.affiliatlink.offer.specification;

import ma.youcode.affiliatlink.offer.entity.Offer;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;
import java.util.Locale;

public class OfferSpecification {

    public static Specification<Offer> hasNiche(String niche) {
        return (root, query, cb) -> {
            if (niche == null || niche.isBlank()) {
                return cb.conjunction();
            }

            return cb.equal(
                    cb.lower(root.get("niche")),
                    niche.trim().toLowerCase(Locale.ROOT)
            );
        };
    }

    public static Specification<Offer> hasAdvertiserId(Long advertiserId) {
        return (root, query, cb) -> {
            if (advertiserId == null) {
                return cb.conjunction();
            }

            return cb.equal(root.get("advertiserId"), advertiserId);
        };
    }

    public static Specification<Offer> hasTag(String tag) {
        return (root, query, cb) -> {
            if (tag == null || tag.isBlank()) {
                return cb.conjunction();
            }

            query.distinct(true);

            return cb.equal(
                    cb.lower(root.join("tags", JoinType.INNER).get("name")),
                    tag.trim().toLowerCase(Locale.ROOT)
            );
        };
    }

    public static Specification<Offer> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            return cb.like(
                    cb.lower(root.get("title")),
                    "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%"
            );
        };
    }
}
