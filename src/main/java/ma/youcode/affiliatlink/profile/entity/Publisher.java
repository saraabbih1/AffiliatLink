
package ma.youcode.affiliatlink.profile.entity;

import jakarta.persistence.*;
import ma.youcode.affiliatlink.profile.entity.enums.Niche;

@Entity
@Table(name = "publishers")
@PrimaryKeyJoinColumn(name = "profile_id")
public class Publisher extends Profile {

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Niche niche;

    @Column(length = 1000)
    private String bio;

    @Column(nullable = false, length = 100)
    private String country;

    public Publisher() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Niche getNiche() {
        return niche;
    }

    public void setNiche(Niche niche) {
        this.niche = niche;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
