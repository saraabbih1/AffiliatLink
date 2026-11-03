
package ma.youcode.affiliatlink.offer.dto;

public record OfferSearchCriteria(
        String niche,
        Long advertiserId,
        String tag,
        String keyword
) {
}
