
package ma.youcode.affiliatlink.offer.service;

import ma.youcode.affiliatlink.common.ResourceNotFoundException;
import ma.youcode.affiliatlink.offer.dto.CreateOfferRequest;
import ma.youcode.affiliatlink.offer.dto.OfferResponse;
import ma.youcode.affiliatlink.offer.dto.UpdateOfferRequest;
import ma.youcode.affiliatlink.offer.entity.Offer;
import ma.youcode.affiliatlink.offer.entity.Tag;
import ma.youcode.affiliatlink.offer.mapper.OfferMapper;
import ma.youcode.affiliatlink.offer.repository.OfferRepository;
import ma.youcode.affiliatlink.offer.repository.TagRepository;
import ma.youcode.affiliatlink.profile.ProfileApi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private OfferMapper offerMapper;

    @Mock
    private ProfileApi profileApi;

    @InjectMocks
    private OfferService offerService;

    @Test
    void shouldRejectOfferWhenAdvertiserIsInactive() {
        CreateOfferRequest request = mock(CreateOfferRequest.class);

        when(request.advertiserId()).thenReturn(1L);
        when(profileApi.isAdvertiserActive(1L)).thenReturn(false);

        assertThatThrownBy(() -> offerService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT");

        verify(offerRepository, never()).save(any());
    }

    @Test
    void shouldCreateOfferWhenAdvertiserIsActive() {
        CreateOfferRequest request = mock(CreateOfferRequest.class);
        Offer offer = mock(Offer.class);
        OfferResponse response = mock(OfferResponse.class);

        when(request.advertiserId()).thenReturn(1L);
        when(profileApi.isAdvertiserActive(1L)).thenReturn(true);
        when(offerMapper.toEntity(request)).thenReturn(offer);
        when(offerRepository.save(offer)).thenReturn(offer);
        when(offerMapper.toResponse(offer)).thenReturn(response);

        OfferResponse result = offerService.create(request);

        assertThat(result).isSameAs(response);
        verify(offerRepository).save(offer);
    }

    @Test
    void shouldNormalizeAndDeduplicateTags() {
        CreateOfferRequest request = new CreateOfferRequest(
                1L,
                "Java Course",
                "Learn Java",
                "https://example.com",
                "Technology",
                Set.of(" Java ", "JAVA", "Spring")
        );

        Offer offer = mock(Offer.class);
        OfferResponse response = mock(OfferResponse.class);

        when(profileApi.isAdvertiserActive(1L)).thenReturn(true);
        when(offerMapper.toEntity(request)).thenReturn(offer);

        when(tagRepository.findByName(anyString()))
                .thenReturn(Optional.empty());

        when(tagRepository.save(any(Tag.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(offerRepository.save(offer)).thenReturn(offer);
        when(offerMapper.toResponse(offer)).thenReturn(response);

        offerService.create(request);

        verify(offer).setTags(argThat(tags ->
                tags.size() == 2
                        && tags.stream()
                        .map(Tag::getName)
                        .collect(java.util.stream.Collectors.toSet())
                        .equals(Set.of("java", "spring"))
        ));

        verify(tagRepository, times(2)).save(any(Tag.class));
    }

    @Test
    void shouldThrowExceptionWhenOfferNotFound() {
        Long offerId = 999L;

        when(offerRepository.findById(offerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.getById(offerId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Offer not found with id: 999");

        verify(offerMapper, never()).toResponse(any(Offer.class));
    }

    @Test
    void shouldRejectUpdateWhenOfferNotFound() {
        Long offerId = 999L;
        UpdateOfferRequest request = mock(UpdateOfferRequest.class);

        when(offerRepository.findById(offerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.update(offerId, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(offerRepository, never()).save(any());
    }

    @Test
    void shouldReturnOfferWhenIdExists() {
        Long offerId = 1L;
        Offer offer = mock(Offer.class);
        OfferResponse response = mock(OfferResponse.class);

        when(offerRepository.findById(offerId))
                .thenReturn(Optional.of(offer));
        when(offerMapper.toResponse(offer))
                .thenReturn(response);

        OfferResponse result = offerService.getById(offerId);

        assertThat(result).isSameAs(response);
        verify(offerMapper).toResponse(offer);
    }

    @Test
    void shouldUpdateExistingOffer() {
        Long offerId = 1L;

        UpdateOfferRequest request = new UpdateOfferRequest(
                "Updated title",
                "Updated description",
                "https://example.com",
                "Java",
                Set.of()
        );

        Offer offer = mock(Offer.class);
        OfferResponse response = mock(OfferResponse.class);

        when(offerRepository.findById(offerId))
                .thenReturn(Optional.of(offer));
        when(offerRepository.save(offer))
                .thenReturn(offer);
        when(offerMapper.toResponse(offer))
                .thenReturn(response);

        OfferResponse result = offerService.update(offerId, request);

        assertThat(result).isSameAs(response);
        verify(offerMapper).updateEntity(request, offer);
        verify(offer).setTags(anySet());
        verify(offerRepository).save(offer);
    }

    @Test
    void shouldReturnEmptyTagsWhenTagsAreNull() {
        CreateOfferRequest request = new CreateOfferRequest(
                1L,
                "Java Offer",
                "Description",
                "https://example.com",
                "Java",
                null
        );

        Offer offer = mock(Offer.class);
        OfferResponse response = mock(OfferResponse.class);

        when(profileApi.isAdvertiserActive(1L)).thenReturn(true);
        when(offerMapper.toEntity(request)).thenReturn(offer);
        when(offerRepository.save(offer)).thenReturn(offer);
        when(offerMapper.toResponse(offer)).thenReturn(response);

        offerService.create(request);

        verify(offer).setTags(argThat(Set::isEmpty));
        verifyNoInteractions(tagRepository);
    }

    @Test
    void shouldReturnOfferExistsResult() {
        when(offerRepository.existsById(1L)).thenReturn(true);

        assertThat(offerService.existsById(1L)).isTrue();
    }

    @Test
    void shouldReturnAdvertiserIdForOffer() {
        when(offerRepository.findAdvertiserIdById(1L))
                .thenReturn(Optional.of(10L));

        assertThat(offerService.findAdvertiserIdByOfferId(1L))
                .contains(10L);
    }

    @Test
    void shouldReturnOfferIdsForAdvertiser() {
        when(offerRepository.findOfferIdsByAdvertiserId(10L))
                .thenReturn(List.of(1L, 2L));

        assertThat(offerService.findOfferIdsByAdvertiserId(10L))
                .containsExactly(1L, 2L);
    }

    @Test
    void shouldReturnPaginatedOffers() {
        Offer offer = mock(Offer.class);
        OfferResponse response = mock(OfferResponse.class);

        PageRequest pageable = PageRequest.of(0, 10);
        Page<Offer> page = new PageImpl<>(List.of(offer));

        when(offerRepository.findAll(
                org.mockito.ArgumentMatchers
                        .<Specification<Offer>>any(),
                eq(pageable)
        )).thenReturn(page);

        when(offerMapper.toResponse(offer)).thenReturn(response);

        Page<OfferResponse> result = offerService.getAllOffers(
                null, null, null, null, pageable
        );

        assertThat(result.getContent()).containsExactly(response);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }
}
