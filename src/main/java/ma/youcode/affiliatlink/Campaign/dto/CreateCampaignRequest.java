package ma.youcode.affiliatlink.campaign.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCampaignRequest(
        String name,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal budget,
        String payoutModel,
        BigDecimal payoutValue
) {
}