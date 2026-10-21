
package ma.youcode.affiliatlink.profile.repository;

import ma.youcode.affiliatlink.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByEmail(String email);
}
