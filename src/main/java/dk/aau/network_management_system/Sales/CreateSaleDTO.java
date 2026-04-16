package dk.aau.network_management_system.Sales;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

public class CreateSaleDTO {

    @NotNull(message = "materialId is required")
    private Long materialId;

    @NotNull(message = "weight is required")
    @Positive(message = "weight must be positive")
    private Double weight;

    @NotNull(message = "priceKg is required")
    @Positive(message = "priceKg must be positive")
    private Double priceKg;

    @NotNull(message = "buyerId is required")
    private Long buyerId;

    @NotNull(message = "expectedSaleDate is required")
    private Instant expectedSaleDate;

    public CreateSaleDTO() {}

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getPriceKg() { return priceKg; }
    public void setPriceKg(Double priceKg) { this.priceKg = priceKg; }

    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }

    public Instant getExpectedSaleDate() { return expectedSaleDate; }
    public void setExpectedSaleDate(Instant expectedSaleDate) {
        this.expectedSaleDate = expectedSaleDate;
    }
}