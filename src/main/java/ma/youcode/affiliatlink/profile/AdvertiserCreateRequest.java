
package ma.youcode.affiliatlink.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdvertiserCreateRequest(
        @NotBlank @Email String email,
        @NotBlank String companyName,
        @NotBlank String sector,
        String website
) {}
