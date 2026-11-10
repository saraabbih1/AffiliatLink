
package ma.youcode.affiliatlink.offer.mapper;
import ma.youcode.affiliatlink.offer.dto.CampaignResponse;
import ma.youcode.affiliatlink.offer.dto.CreateCampaignRequest;
import ma.youcode.affiliatlink.offer.entity.Campaign;
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
