
package ma.youcode.affiliatlink.offer.entity;

import jakarta.persistence.*;
import ma.youcode.affiliatlink.offer.entity.enums.CampaignStatus;
import ma.youcode.affiliatlink.offer.entity.enums.PayoutModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "campaigns")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal budget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayoutModel payoutModel;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal payoutValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CampaignStatus status = CampaignStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    protected Campaign() {
    }

    public Campaign(
            String name,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal budget,
            PayoutModel payoutModel,
            BigDecimal payoutValue,
            Offer offer) {
        this.name = Objects.requireNonNull(name);
        this.startDate = Objects.requireNonNull(startDate);
        this.endDate = Objects.requireNonNull(endDate);
        this.budget = Objects.requireNonNull(budget);
        this.payoutModel = Objects.requireNonNull(payoutModel);
        this.payoutValue = Objects.requireNonNull(payoutValue);
        this.offer = Objects.requireNonNull(offer);
        this.status = CampaignStatus.DRAFT;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public PayoutModel getPayoutModel() {
        return payoutModel;
    }

    public BigDecimal getPayoutValue() {
        return payoutValue;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public Offer getOffer() {
        return offer;
    }
}
