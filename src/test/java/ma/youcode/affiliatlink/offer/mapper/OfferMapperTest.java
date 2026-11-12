
package ma.youcode.affiliatlink.offer.mapper;

import ma.youcode.affiliatlink.offer.dto.CreateOfferRequest;
import ma.youcode.affiliatlink.offer.dto.OfferResponse;
import ma.youcode.affiliatlink.offer.dto.UpdateOfferRequest;
import ma.youcode.affiliatlink.offer.entity.Offer;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OfferMapperTest {

    private final OfferMapper mapper =
            Mappers.getMapper(OfferMapper.class);

    @Test
    void shouldMapCreateRequestToOffer() {
        CreateOfferRequest request = new CreateOfferRequest(
                10L,
                "Java Course",
                "Learn Spring Boot",
                "https://example.com",
                "Technology",
                Set.of("java")
        );

        Offer offer = mapper.toEntity(request);

        assertThat(offer.getAdvertiserId()).isEqualTo(10L);
        assertThat(offer.getTitle()).isEqualTo("Java Course");
        assertThat(offer.getDescription()).isEqualTo("Learn Spring Boot");
        assertThat(offer.getLandingUrl()).isEqualTo("https://example.com");
        assertThat(offer.getNiche()).isEqualTo("Technology");
        assertThat(offer.getTags()).isEmpty();
        assertThat(offer.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateExistingOfferWithoutChangingAdvertiser() {
        Offer offer = new Offer(
                10L,
                "Old title",
                "Old description",
                "https://old.com",
                "Old niche"
        );

        UpdateOfferRequest request = new UpdateOfferRequest(
                "New title",
                "New description",
                "https://new.com",
                "Java",
                Set.of("spring")
        );

        mapper.updateEntity(request, offer);

        assertThat(offer.getTitle()).isEqualTo("New title");
        assertThat(offer.getDescription()).isEqualTo("New description");
        assertThat(offer.getLandingUrl()).isEqualTo("https://new.com");
        assertThat(offer.getNiche()).isEqualTo("Java");
        assertThat(offer.getAdvertiserId()).isEqualTo(10L);
        assertThat(offer.getCreatedAt()).isNotNull();
        assertThat(offer.getTags()).isEmpty();
    }

    @Test
    void shouldMapOfferToResponse() {
        Offer offer = new Offer(
                10L,
                "Java Course",
                "Description",
                "https://example.com",
                "Technology"
        );

        OfferResponse response = mapper.toResponse(offer);

        assertThat(response.advertiserId()).isEqualTo(10L);
        assertThat(response.title()).isEqualTo("Java Course");
        assertThat(response.description()).isEqualTo("Description");
        assertThat(response.landingUrl()).isEqualTo("https://example.com");
        assertThat(response.niche()).isEqualTo("Technology");
        assertThat(response.tags()).isEmpty();
    }

    @Test
    void shouldReturnNullWhenCreateRequestIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenOfferIsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}
