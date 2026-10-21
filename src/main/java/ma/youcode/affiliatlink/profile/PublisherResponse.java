package ma.youcode.affiliatlink.profile;

import ma.youcode.affiliatlink.profile.entity.enums.Niche;
import ma.youcode.affiliatlink.profile.entity.enums.ProfileStatus;

import java.time.LocalDateTime;

public record PublisherResponse(
        Long id,
        String email,
        ProfileStatus status,
        LocalDateTime createdAt,
        String firstName,
        String lastName,
        Niche niche,
        String bio,
        String country
) {
}