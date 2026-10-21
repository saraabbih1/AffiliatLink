package ma.youcode.affiliatlink.profile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import ma.youcode.affiliatlink.profile.entity.enums.Niche;

public record PublisherCreateRequest(
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull Niche niche,
        String bio,
        @NotBlank String country
) {
}