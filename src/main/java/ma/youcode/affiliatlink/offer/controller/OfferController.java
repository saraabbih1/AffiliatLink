
package ma.youcode.affiliatlink.offer.controller;

import jakarta.validation.Valid;
import ma.youcode.affiliatlink.offer.dto.CreateOfferRequest;
import ma.youcode.affiliatlink.offer.dto.OfferResponse;
import ma.youcode.affiliatlink.offer.dto.OfferSearchCriteria;
import ma.youcode.affiliatlink.offer.dto.UpdateOfferRequest;
import ma.youcode.affiliatlink.offer.service.OfferService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

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
    public Page<OfferResponse> search(
            @RequestParam(required = false) String niche,
            @RequestParam(required = false) Long advertiserId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid pagination parameters"
            );
        }

        Set<String> allowedFields = Set.of(
                "id",
                "title",
                "createdAt",
                "niche"
        );

        String[] sortParams = sort.split(",", -1);

        if (sortParams.length > 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sort format"
            );
        }

        String field = sortParams[0].trim();

        if (!allowedFields.contains(field)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sort field: " + field
            );
        }

        Sort.Direction direction;

        try {
            direction = sortParams.length > 1
                    ? Sort.Direction.fromString(sortParams[1].trim())
                    : Sort.Direction.ASC;
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sort direction"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(direction, field)
        );

        OfferSearchCriteria criteria = new OfferSearchCriteria(
                niche,
                advertiserId,
                tag,
                keyword
        );

        return offerService.search(criteria, pageable);
    }
}
