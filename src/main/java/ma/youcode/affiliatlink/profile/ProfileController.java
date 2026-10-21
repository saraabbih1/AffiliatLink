
package ma.youcode.affiliatlink.profile;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ma.youcode.affiliatlink.profile.PublisherCreateRequest;
import ma.youcode.affiliatlink.profile.PublisherResponse;
import java.util.List;

@RestController
@RequestMapping("/api/publishers")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<PublisherResponse> createPublisher(
            @Valid @RequestBody PublisherCreateRequest request) {

        PublisherResponse created = profileService.createPublisher(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherResponse> findPublisherById(
            @PathVariable Long id) {

        return profileService.findPublisherById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping
    public ResponseEntity<List<PublisherResponse>> findAllPublishers() {
        return ResponseEntity.ok(profileService.findAllPublishers());
    }
}
