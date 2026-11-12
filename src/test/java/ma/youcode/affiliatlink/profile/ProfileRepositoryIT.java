
package ma.youcode.affiliatlink.profile;

import ma.youcode.affiliatlink.config.TestcontainersConfiguration;
import ma.youcode.affiliatlink.profile.entity.Publisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ma.youcode.affiliatlink.profile.entity.enums.Niche;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class ProfileRepositoryIT {

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void shouldFindProfileByEmail() {

        Publisher publisher = new Publisher();
        publisher.setEmail("publisher@test.com");
        publisher.setFirstName("Sara");
        publisher.setLastName("Abbih");
        publisher.setNiche(Niche.TECH);
        publisher.setCountry("Morocco");

        profileRepository.saveAndFlush(publisher);


        var result = profileRepository.findByEmail("publisher@test.com");


        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("publisher@test.com");
    }
    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {

        var result = profileRepository.findByEmail("unknown@test.com");

        assertThat(result).isEmpty();
    }
}
