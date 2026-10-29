
package ma.youcode.affiliatlink.offer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateOfferRequest(
        @NotBlank @Size(max = 150)
        String title,

        String description,

        @NotBlank @Size(max = 2048)
        String landingUrl,

        @NotBlank @Size(max = 100)
        String niche,

        Set<@NotBlank @Size(max = 50) String> tags
) {
}
