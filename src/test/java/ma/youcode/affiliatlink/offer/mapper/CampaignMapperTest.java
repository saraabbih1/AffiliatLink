
package ma.youcode.affiliatlink.offer.mapper;

import ma.youcode.affiliatlink.offer.dto.CampaignResponse;
import ma.youcode.affiliatlink.offer.dto.CreateCampaignRequest;
import ma.youcode.affiliatlink.offer.entity.Campaign;
import ma.youcode.affiliatlink.offer.entity.enums.PayoutModel;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignMapperTest {

    private final CampaignMapper mapper =
            Mappers.getMapper(CampaignMapper.class);

    @Test
    void shouldMapCreateRequestToCampaign() {
        CreateCampaignRequest request = new CreateCampaignRequest(
                "Black Friday",
                LocalDate.of(2026, 11, 20),
                LocalDate.of(2026, 11, 30),
                new BigDecimal("1000"),
                "CPC",
                new BigDecimal("2")
        );

        Campaign campaign = mapper.toEntity(request, 1L);

        assertThat(campaign.getOfferId()).isEqualTo(1L);
        assertThat(campaign.getStatus()).isNotNull();
        assertThat(campaign.getEndDate())
                .isEqualTo(LocalDate.of(2026, 11, 30));
    }

    @Test
    void shouldMapCampaignToResponse() {
        Campaign campaign = new Campaign(
                "Black Friday",
                LocalDate.of(2026, 11, 20),
                LocalDate.of(2026, 11, 30),
                new BigDecimal("1000"),
                PayoutModel.CPC,
                new BigDecimal("2"),
                1L
        );

        CampaignResponse response = mapper.toResponse(campaign);

        assertThat(response).isNotNull();
    }

    @Test
    void shouldReturnNullWhenCampaignIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void shouldCreateCampaignWhenOnlyOfferIdIsProvided() {
        Campaign campaign = mapper.toEntity(null, 1L);

        assertThat(campaign).isNotNull();
        assertThat(campaign.getOfferId()).isEqualTo(1L);
    }
}
