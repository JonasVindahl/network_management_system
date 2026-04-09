package dk.aau.network_management_system.Collective_Sale;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

public class CreateCollectiveSaleDTO {

    @NotNull(message = "Material ID is required")
    private Long materialId;

    @NotNull(message = "Buyer ID is required")
    private Long buyerId;

    @NotNull(message = "Price per kg is required")
    @Positive(message = "Price per kg must be positive")
    private BigDecimal pricePerKg;

    @NotNull(message = "Expected sale date is required")
    private Instant expectedSaleDate;

    public CreateCollectiveSaleDTO() {}

    public CreateCollectiveSaleDTO(Long materialId, Long buyerId, BigDecimal pricePerKg, Instant expectedSaleDate) {
        this.materialId = materialId;
        this.buyerId = buyerId;
        this.pricePerKg = pricePerKg;
        this.expectedSaleDate = expectedSaleDate;
    }

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }

    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }

    public BigDecimal getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(BigDecimal pricePerKg) { this.pricePerKg = pricePerKg; }

    public Instant getExpectedSaleDate() { return expectedSaleDate; }
    public void setExpectedSaleDate(Instant expectedSaleDate) { this.expectedSaleDate = expectedSaleDate; }
}
