
package ma.youcode.affiliatlink.offer.entity;

import jakarta.persistence.*;

import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "tags", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)    private String name;

    protected Tag() {
    }

    public Tag(String name) {
        Objects.requireNonNull(name, "Tag name cannot be null");

        String normalizedName = name.trim().toLowerCase(Locale.ROOT);

        if (normalizedName.isBlank()) {
            throw new IllegalArgumentException("Tag name cannot be blank");
        }

        if (normalizedName.length() > 50) {
            throw new IllegalArgumentException("Tag name cannot exceed 50 characters");
        }

        this.name = normalizedName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
