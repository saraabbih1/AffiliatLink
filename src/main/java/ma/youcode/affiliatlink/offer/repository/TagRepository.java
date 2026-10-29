
package ma.youcode.affiliatlink.offer.repository;

import ma.youcode.affiliatlink.offer.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);
}
