package dk.aau.network_management_system.Collective_Sale;
import java.math.BigDecimal;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "collective_sale")
public class CollectiveSaleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "collective_sale_id")
    private Long collectiveSaleId;

    @Column(name = "material_id", nullable = false)
    private Long materialId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "price_kg", nullable = false)
    private BigDecimal pricePerKg;

    @Column(name = "expected_sale_date", nullable = false)
    private Instant expectedSaleDate;

    @Column(name = "total_weight")
    private BigDecimal totalWeight;

    @Column(name = "creator_cooperative_id", nullable = false)
    private Long creatorCooperativeId;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "sold_at")
    private Instant soldAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    // No-arg constructor required by JPA
    public CollectiveSaleEntity() {
        this.createdAt = Instant.now();
    }

    public CollectiveSaleEntity(Long materialId, Long buyerId, BigDecimal pricePerKg,
            Instant expectedSaleDate, BigDecimal totalWeight,
            Long creatorCooperativeId) {
		this.materialId = materialId;
		this.buyerId = buyerId;
		this.pricePerKg = pricePerKg;
		this.expectedSaleDate = expectedSaleDate;
		this.totalWeight = totalWeight;
		this.creatorCooperativeId = creatorCooperativeId;
		this.createdAt = Instant.now();
	}

    public Long getCollectiveSaleId() { return collectiveSaleId; }
    public void setCollectiveSaleId(Long collectiveSaleId) { this.collectiveSaleId = collectiveSaleId; }

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }

    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }

    public BigDecimal getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(BigDecimal pricePerKg) { this.pricePerKg = pricePerKg; }

    public Instant getExpectedSaleDate() { return expectedSaleDate; }
    public void setExpectedSaleDate(Instant expectedSaleDate) { this.expectedSaleDate = expectedSaleDate; }

    public BigDecimal getTotalWeight() { return totalWeight; }
    public void setTotalWeight(BigDecimal totalWeight) { this.totalWeight = totalWeight; }

    public Long getCreatorCooperativeId() { return creatorCooperativeId; }
    public void setCreatorCooperativeId(Long creatorCooperativeId) { this.creatorCooperativeId = creatorCooperativeId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getSoldAt() { return soldAt; }
    public void setSoldAt(Instant soldAt) { this.soldAt = soldAt; }

    public Instant getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Instant cancelledAt) { this.cancelledAt = cancelledAt; }
}
