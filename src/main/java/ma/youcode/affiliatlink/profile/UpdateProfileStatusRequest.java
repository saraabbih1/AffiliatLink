
        package ma.youcode.affiliatlink.profile;

import jakarta.validation.constraints.NotNull;
import ma.youcode.affiliatlink.profile.entity.enums.ProfileStatus;

public record UpdateProfileStatusRequest(
        @NotNull ProfileStatus status
) {}
