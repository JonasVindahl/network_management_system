package dk.aau.network_management_system.Cooperative_Analytics;

import java.time.Instant;

public class SaleDTO {
    
    private Long saleId;
    private String saleType;  
    private Instant createdAt;
    private Instant soldAt;   
    private Instant expectedSaleDate;
    private String materialName;
    private Double weight;
    private Double pricePerKg;
    private Double totalRevenue;
    private String buyerName;
    private String status;  //udregnes
    
    // collective sales only
    private Long collectiveSaleId;
    private Integer cooperativeCount;
    
    public SaleDTO() {}
    
    // Normal sale constructor
    public SaleDTO(Long saleId, String saleType, Instant createdAt, Instant soldAt,
                   Instant expectedSaleDate, String materialName, Double weight, 
                   Double pricePerKg, String buyerName) {
        this.saleId = saleId;
        this.saleType = saleType;
        this.createdAt = createdAt;
        this.soldAt = soldAt;
        this.expectedSaleDate = expectedSaleDate;
        this.materialName = materialName;
        this.weight = weight;
        this.pricePerKg = pricePerKg;
        this.totalRevenue = weight * pricePerKg;
        this.buyerName = buyerName;
        this.status = soldAt == null ? "PENDING" : "COMPLETED";
    }
    
    // collectiv sale constructor
    public SaleDTO(Long collectiveSaleId, String saleType, Instant createdAt, 
                   Instant soldAt, Instant expectedSaleDate, String materialName,
                   Double weight, Double pricePerKg, String buyerName, 
                   Integer cooperativeCount) {
        this(null, saleType, createdAt, soldAt, expectedSaleDate, 
             materialName, weight, pricePerKg, buyerName);
        this.collectiveSaleId = collectiveSaleId;
        this.cooperativeCount = cooperativeCount;
    }
    
    // getters and Setters
    public Long getSaleId() { return saleId; }
    public void setSaleId(Long saleId) { this.saleId = saleId; }
    
    public String getSaleType() { return saleType; }
    public void setSaleType(String saleType) { this.saleType = saleType; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getSoldAt() { return soldAt; }
    public void setSoldAt(Instant soldAt) { 
        this.soldAt = soldAt;
        this.status = soldAt == null ? "PENDING" : "COMPLETED";
    }
    
    public Instant getExpectedSaleDate() { return expectedSaleDate; }
    public void setExpectedSaleDate(Instant expectedSaleDate) { 
        this.expectedSaleDate = expectedSaleDate; 
    }
    
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { 
        this.weight = weight;
        if (this.pricePerKg != null) {
            this.totalRevenue = weight * this.pricePerKg;
        }
    }
    
    public Double getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(Double pricePerKg) { 
        this.pricePerKg = pricePerKg;
        if (this.weight != null) {
            this.totalRevenue = this.weight * pricePerKg;
        }
    }
    
    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Long getCollectiveSaleId() { return collectiveSaleId; }
    public void setCollectiveSaleId(Long collectiveSaleId) { 
        this.collectiveSaleId = collectiveSaleId; 
    }
    
    public Integer getCooperativeCount() { return cooperativeCount; }
    public void setCooperativeCount(Integer cooperativeCount) { 
        this.cooperativeCount = cooperativeCount; 
    }
}