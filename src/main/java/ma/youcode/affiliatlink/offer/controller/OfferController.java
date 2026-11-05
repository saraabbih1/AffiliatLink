
package ma.youcode.affiliatlink.offer.controller;

import jakarta.validation.Valid;
import ma.youcode.affiliatlink.offer.dto.CreateOfferRequest;
import ma.youcode.affiliatlink.offer.dto.OfferResponse;
import ma.youcode.affiliatlink.offer.dto.UpdateOfferRequest;
import ma.youcode.affiliatlink.offer.service.OfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
@RestController
@RequestMapping("/api/offres")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    public ResponseEntity<OfferResponse> create(
            @Valid @RequestBody CreateOfferRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(offerService.create(request));
    }

    @PutMapping("/{id}")
    public OfferResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOfferRequest request
    ) {
        return offerService.update(id, request);
    }

    @GetMapping("/{id}")
    public OfferResponse getById(@PathVariable Long id) {
        return offerService.getById(id);
    }

    @GetMapping
    public Page<OfferResponse> getAllOffers(
            @RequestParam(required = false) Long advertiserId,
            @RequestParam(required = false) String niche,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return offerService.getAllOffers(
                advertiserId, niche, tag, keyword, pageable
        );
    }
}
