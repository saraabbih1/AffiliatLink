
package ma.youcode.affiliatlink.profile;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/advertisers")
public class AdvertiserController {

    private final ProfileService profileService;

    public AdvertiserController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<AdvertiserResponse> createAdvertiser(
            @Valid @RequestBody AdvertiserCreateRequest request) {

        AdvertiserResponse created =
                profileService.createAdvertiser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    @GetMapping("/{id}")
    public ResponseEntity<AdvertiserResponse> getAdvertiserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                profileService.findAdvertiserById(id)
        );
    }
    @GetMapping
    public ResponseEntity<List<AdvertiserResponse>> getAllAdvertisers() {
        return ResponseEntity.ok(profileService.findAllAdvertisers());
    }
}
