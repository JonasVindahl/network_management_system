package dk.aau.network_management_system.Sales;

import java.time.Instant;

import jakarta.validation.constraints.Positive;

public class UpdateSaleDTO {

    @Positive (message = "Weight must be positive")
    private Double weight;

    @Positive (message = "Price per Kilo must be positive")
    private Double priceKg;

    private Long materialId;
    private Long buyerId;
    private Instant expectedSaleDate;

    public UpdateSaleDTO() {}

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getPriceKg() { return priceKg; }
    public void setPriceKg(Double priceKg) { this.priceKg = priceKg; }

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }

    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }

    public Instant getExpectedSaleDate() { return expectedSaleDate; }

    public void setExpectedSaleDate(Instant expectedSaleDate) {
        this.expectedSaleDate = expectedSaleDate;
    }
}
