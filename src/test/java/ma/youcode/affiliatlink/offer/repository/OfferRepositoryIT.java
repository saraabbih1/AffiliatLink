
package ma.youcode.affiliatlink.offer.repository;

import ma.youcode.affiliatlink.offer.entity.Offer;
import ma.youcode.affiliatlink.offer.specification.OfferSpecification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
class OfferRepositoryIT {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.docker.compose.enabled", () -> "false");
    }

    @Autowired
    private OfferRepository offerRepository;

    @BeforeEach
    void setUp() {
        offerRepository.save(new Offer(
                10L,
                "Java Spring Boot",
                "Backend course",
                "https://example.com/java",
                "Technology"
        ));

        offerRepository.save(new Offer(
                10L,
                "React Development",
                "Frontend course",
                "https://example.com/react",
                "Technology"
        ));

        offerRepository.save(new Offer(
                20L,
                "Healthy Recipes",
                "Food content",
                "https://example.com/food",
                "Lifestyle"
        ));

        offerRepository.flush();
    }

    @Test
    void shouldFindAdvertiserIdByOfferId() {
        Offer offer = offerRepository.findAll().stream()
                .filter(o -> o.getTitle().equals("Java Spring Boot"))
                .findFirst()
                .orElseThrow();

        assertThat(offerRepository.findAdvertiserIdById(offer.getId()))
                .contains(10L);
    }

    @Test
    void shouldReturnEmptyWhenOfferDoesNotExist() {
        assertThat(offerRepository.findAdvertiserIdById(-999L))
                .isEmpty();
    }

    @Test
    void shouldFindOfferIdsByAdvertiserId() {
        List<Long> ids = offerRepository.findOfferIdsByAdvertiserId(10L);

        assertThat(ids).hasSize(2);
    }

    @Test
    void shouldReturnEmptyListForUnknownAdvertiser() {
        assertThat(offerRepository.findOfferIdsByAdvertiserId(-999L))
                .isEmpty();
    }

    @Test
    void shouldFilterOffersByAdvertiserId() {
        List<Offer> offers = offerRepository.findAll(
                OfferSpecification.hasAdvertiserId(10L)
        );

        assertThat(offers).hasSize(2);
        assertThat(offers)
                .extracting(Offer::getAdvertiserId)
                .containsOnly(10L);
    }

    @Test
    void shouldFilterOffersByNicheIgnoringCase() {
        List<Offer> offers = offerRepository.findAll(
                OfferSpecification.hasNiche(" TECHNOLOGY ")
        );

        assertThat(offers).hasSize(2);
    }

    @Test
    void shouldFilterOffersByKeywordIgnoringCase() {
        List<Offer> offers = offerRepository.findAll(
                OfferSpecification.hasKeyword(" JAVA ")
        );

        assertThat(offers)
                .extracting(Offer::getTitle)
                .containsExactly("Java Spring Boot");
    }

    @Test
    void shouldCombineMultipleFilters() {
        List<Offer> offers = offerRepository.findAll(
                OfferSpecification.hasAdvertiserId(10L)
                        .and(OfferSpecification.hasNiche("Technology"))
                        .and(OfferSpecification.hasKeyword("React"))
        );

        assertThat(offers)
                .extracting(Offer::getTitle)
                .containsExactly("React Development");
    }

    @Test
    void shouldReturnAllOffersWhenFiltersAreNull() {
        List<Offer> offers = offerRepository.findAll(
                OfferSpecification.hasAdvertiserId(null)
                        .and(OfferSpecification.hasNiche(null))
                        .and(OfferSpecification.hasKeyword(null))
                        .and(OfferSpecification.hasTag(null))
        );

        assertThat(offers).hasSize(3);
    }

    @Test
    void shouldReturnPaginatedOffers() {
        Page<Offer> page = offerRepository.findAll(
                OfferSpecification.hasNiche("Technology"),
                PageRequest.of(0, 1)
        );

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }
}
