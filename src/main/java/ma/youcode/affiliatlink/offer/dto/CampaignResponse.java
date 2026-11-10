
package ma.youcode.affiliatlink.offer.dto;

import ma.youcode.affiliatlink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatlink.offer.entity.enums.PayoutModel;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CampaignResponse(
        Long id,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal budget,
        PayoutModel payoutModel,
        BigDecimal payoutValue,
        CampaignStatus status,
        Long offerId
) {
}
