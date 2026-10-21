
package ma.youcode.affiliatlink.profile.service;

import ma.youcode.affiliatlink.profile.entity.Profile;
import ma.youcode.affiliatlink.profile.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Optional<Profile> findByEmail(String email) {
        return profileRepository.findByEmail(email);
    }
}
