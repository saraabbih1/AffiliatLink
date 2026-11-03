
package ma.youcode.affiliatlink.offer.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long advertiserId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 2048)
    private String landingUrl;

    @Column(nullable = false, length = 100)
    private String niche;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "offer_tags",
            joinColumns = @JoinColumn(name = "offer_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            uniqueConstraints = @UniqueConstraint(
                    columnNames = {"offer_id", "tag_id"}
            )
    )
    private Set<Tag> tags = new HashSet<>();

    protected Offer() {
    }

    public Offer(Long advertiserId, String title, String description,
                 String landingUrl, String niche) {
        this.advertiserId = advertiserId;
        this.title = title;
        this.description = description;
        this.landingUrl = landingUrl;
        this.niche = niche;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLandingUrl() {
        return landingUrl;
    }

    public String getNiche() {
        return niche;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<Tag> getTags() {
        return tags;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLandingUrl(String landingUrl) {
        this.landingUrl = landingUrl;
    }

    public void setNiche(String niche) {
        this.niche = niche;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = new HashSet<>(tags);
    }

}
