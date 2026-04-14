package dk.aau.network_management_system.Collective_Sale_Reports;

import java.time.Instant;
import java.util.List;

public class CollectiveSaleReportDTO {

    private Long collectiveSaleId;
    private String status;

    private Long materialId;
    private String materialName;
    private Long buyerId;
    private String buyerName;
    
    private Instant createdAt;
    private Instant soldAt; 
    private Instant expectedSaleDate;
    
    private Double totalWeight;
    private Double pricePerKg;
    private Double totalRevenue;

    private Integer totalCooperatives;
    private List<ContributionDetailDTO> contributions;
    
    public CollectiveSaleReportDTO(){}

    public CollectiveSaleReportDTO(Long collectiveSaleId, Long materialId, 
                                   String materialName, Long buyerId, String buyerName,
                                   Instant createdAt, Instant soldAt, Instant expectedSaleDate,
                                   Double totalWeight, Double pricePerKg,
                                   List<ContributionDetailDTO> contributions) {
        this.collectiveSaleId = collectiveSaleId;
        this.materialId = materialId;
        this.materialName = materialName;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.createdAt = createdAt;
        this.soldAt = soldAt;  // ✅ FIXED
        this.expectedSaleDate = expectedSaleDate;
        this.totalWeight = totalWeight;
        this.pricePerKg = pricePerKg;
        this.totalRevenue = totalWeight * pricePerKg;
        this.status = soldAt == null ? "ACTIVE" : "SOLD";
        this.contributions = contributions;
        this.totalCooperatives = contributions != null ? contributions.size() : 0;
    }
    
    // Getters and Setters
    public Long getCollectiveSaleId() { return collectiveSaleId; }
    public void setCollectiveSaleId(Long collectiveSaleId) { 
        this.collectiveSaleId = collectiveSaleId; 
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    
    public Instant getSoldAt() { return soldAt; }  // ✅ FIXED
    public void setSoldAt(Instant soldAt) { 
        this.soldAt = soldAt;  // ✅ FIXED
        this.status = soldAt == null ? "ACTIVE" : "SOLD";
    }

    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
    
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getExpectedSaleDate() { return expectedSaleDate; }
    public void setExpectedSaleDate(Instant expectedSaleDate) {
        this.expectedSaleDate = expectedSaleDate;
    }

    public Double getTotalWeight() { return totalWeight; }
    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
        if (this.pricePerKg != null) {
            this.totalRevenue = totalWeight * this.pricePerKg;
        }
    }

    public Double getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(Double pricePerKg) {
        this.pricePerKg = pricePerKg;
        if (this.totalWeight != null) {
            this.totalRevenue = this.totalWeight * pricePerKg;
        }
    }

    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public Integer getTotalCooperatives() { return totalCooperatives; }
    public void setTotalCooperatives(Integer totalCooperatives) { 
        this.totalCooperatives = totalCooperatives; 
    }
    
    public List<ContributionDetailDTO> getContributions() { return contributions; }
    public void setContributions(List<ContributionDetailDTO> contributions) { 
        this.contributions = contributions;
        this.totalCooperatives = contributions != null ? contributions.size() : 0;
    }
}