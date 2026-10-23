
package ma.youcode.affiliatlink.profile.entity;

import jakarta.persistence.*;
import ma.youcode.affiliatlink.profile.entity.enums.ProfileStatus;
import ma.youcode.affiliatlink.common.InvalidStatusTransitionException;
import java.time.LocalDateTime;

@Entity
@Table(name = "profile_profiles")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProfileStatus status = ProfileStatus.PENDING;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Profile() {
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ProfileStatus getStatus() {
        return status;
    }

    public void changeStatus(ProfileStatus newStatus) {
        boolean allowed =
                (status == ProfileStatus.PENDING && newStatus == ProfileStatus.ACTIVE)
                        || (status == ProfileStatus.ACTIVE && newStatus == ProfileStatus.SUSPENDED)
                        || (status == ProfileStatus.SUSPENDED && newStatus == ProfileStatus.ACTIVE);

        if (!allowed) {
            throw new InvalidStatusTransitionException(
                    "Cannot change status from " + status + " to " + newStatus
            );
        }

        this.status = newStatus;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
