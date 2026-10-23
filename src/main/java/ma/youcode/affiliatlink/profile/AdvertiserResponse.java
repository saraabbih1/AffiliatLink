
package ma.youcode.affiliatlink.profile;

import ma.youcode.affiliatlink.profile.entity.enums.ProfileStatus;
import java.time.LocalDateTime;

public record AdvertiserResponse(
        Long id,
        String email,
        ProfileStatus status,
        LocalDateTime createdAt,
        String companyName,
        String sector,
        String website
) {}
