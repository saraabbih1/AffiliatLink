package ma.youcode.affiliatlink.profile.entity;

import ma.youcode.affiliatlink.profile.entity.enums.Niche;
import ma.youcode.affiliatlink.profile.entity.enums.ProfileStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ProfileTest{
    @Test
    void shouldCreatePublisherWithPendingtatus(){
        Publisher publisher = new Publisher();
        publisher.setEmail("publisher@test.com");
        publisher.setFirstName("sarra");
        publisher.setLastName("abbih");
        publisher.setNiche(Niche.TECH);
        publisher.setBio("Java developer");
        publisher.setCountry("Morocco");


        assertThat(publisher.getEmail()).isEqualTo("publisher@test.com");
        assertThat(publisher.getFirstName()).isEqualTo("sarra");
        assertThat(publisher.getLastName()).isEqualTo("abbih");
        assertThat(publisher.getNiche()).isEqualTo(Niche.TECH);
        assertThat(publisher.getBio()).isEqualTo("Java developer");
        assertThat(publisher.getCountry()).isEqualTo("Morocco");
        assertThat(publisher.getStatus()).isEqualTo(ProfileStatus.PENDING);
        assertThat(publisher.getCreatedAt()).isNotNull();



    }

    @Test
    void shouldCreateAdvertiserWithPendingStatus() {
        Advertiser advertiser = new Advertiser();
        advertiser.setEmail("advertiser@test.com");
        advertiser.setCompanyName("Tech Company");
        advertiser.setSector("Technology");
        advertiser.setWebsite("https://tech.com");

        assertThat(advertiser.getEmail()).isEqualTo("advertiser@test.com");
        assertThat(advertiser.getCompanyName()).isEqualTo("Tech Company");
        assertThat(advertiser.getSector()).isEqualTo("Technology");
        assertThat(advertiser.getWebsite()).isEqualTo("https://tech.com");
        assertThat(advertiser.getStatus()).isEqualTo(ProfileStatus.PENDING);
        assertThat(advertiser.getCreatedAt()).isNotNull();
    }


}
