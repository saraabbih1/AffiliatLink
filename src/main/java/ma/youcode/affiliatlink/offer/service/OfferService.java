
package ma.youcode.affiliatlink.offer.service;

import ma.youcode.affiliatlink.common.ResourceNotFoundException;
import ma.youcode.affiliatlink.offer.OfferApi;
import ma.youcode.affiliatlink.offer.dto.CreateOfferRequest;
import ma.youcode.affiliatlink.offer.dto.OfferResponse;
import ma.youcode.affiliatlink.offer.dto.UpdateOfferRequest;
import ma.youcode.affiliatlink.offer.entity.Offer;
import ma.youcode.affiliatlink.offer.entity.Tag;
import ma.youcode.affiliatlink.offer.mapper.OfferMapper;
import ma.youcode.affiliatlink.offer.repository.OfferRepository;
import ma.youcode.affiliatlink.offer.repository.TagRepository;
import ma.youcode.affiliatlink.profile.ProfileApi;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OfferService implements OfferApi {

    private final OfferRepository offerRepository;
    private final TagRepository tagRepository;
    private final OfferMapper offerMapper;
    private final ProfileApi profileApi;

    public OfferService(
            OfferRepository offerRepository,
            TagRepository tagRepository,
            OfferMapper offerMapper,
            ProfileApi profileApi
    ) {
        this.offerRepository = offerRepository;
        this.tagRepository = tagRepository;
        this.offerMapper = offerMapper;
        this.profileApi = profileApi;
    }

    @Transactional
    public OfferResponse create(CreateOfferRequest request) {
        if (!profileApi.isAdvertiserActive(request.advertiserId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Advertiser does not exist or is not ACTIVE"
            );
        }

        Offer offer = offerMapper.toEntity(request);
        offer.setTags(resolveTags(request.tags()));

        Offer savedOffer = offerRepository.save(offer);
        return offerMapper.toResponse(savedOffer);
    }

    @Transactional
    public OfferResponse update(Long id, UpdateOfferRequest request) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offer not found with id: " + id
                ));

        offerMapper.updateEntity(request, offer);
        offer.setTags(resolveTags(request.tags()));

        Offer updatedOffer = offerRepository.save(offer);
        return offerMapper.toResponse(updatedOffer);
    }

    @Transactional(readOnly = true)
    public OfferResponse getById(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offer not found with id: " + id
                ));

        return offerMapper.toResponse(offer);
    }





    private Set<Tag> resolveTags(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return new LinkedHashSet<>();
        }

        Set<String> normalizedNames = names.stream()
                .map(name -> name.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return normalizedNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() ->
                                tagRepository.save(new Tag(name))
                        ))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long offerId) {
        return offerRepository.existsById(offerId);
    }
}
