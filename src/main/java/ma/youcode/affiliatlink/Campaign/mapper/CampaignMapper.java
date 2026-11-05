
package ma.youcode.affiliatlink.campaign.mapper;

import ma.youcode.affiliatlink.campaign.dto.CampaignResponse;
import ma.youcode.affiliatlink.campaign.dto.CreateCampaignRequest;
import ma.youcode.affiliatlink.campaign.entity.Campaign;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "offerId", source = "offerId")
    Campaign toEntity(CreateCampaignRequest request, Long offerId);

    CampaignResponse toResponse(Campaign campaign);
}
