
package ma.youcode.affiliatlink.offer.mapper;

import ma.youcode.affiliatlink.offer.dto.CreateOfferRequest;
import ma.youcode.affiliatlink.offer.dto.OfferResponse;
import ma.youcode.affiliatlink.offer.dto.UpdateOfferRequest;
import ma.youcode.affiliatlink.offer.entity.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Offer toEntity(CreateOfferRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "advertiserId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "tags", ignore = true)
    void updateEntity(
            UpdateOfferRequest request,
            @MappingTarget Offer offer
    );

    @Mapping(
            target = "tags",
            expression = "java(offer.getTags().stream()" +
                    ".map(tag -> tag.getName())" +
                    ".collect(java.util.stream.Collectors.toSet()))"
    )
    OfferResponse toResponse(Offer offer);
}
