package dk.aau.network_management_system.Cooperative_Analytics;

import java.time.LocalDate;

// -----------------------------------
// DATABASE ÆNDRING FOR VIDREUDVIKLING:
// Der skal tilføjes en form for status med expected sold
// sold_at må godt være null skal rettes
// -----------------------------------


public class SaleDTO {
    
    private Long saleId;
    private String saleType; 
    private LocalDate saleDate;
    private String materialName;
    private Double weight;
    private Double pricePerKg;
    private Double totalRevenue;
    private String buyerName;
    
    private Long collectiveSaleId;
    private Integer cooperativeCount;  
    
    // Constructors
    public SaleDTO() {}
    
    // constructor normalt salg
    public SaleDTO(Long saleId, String saleType, LocalDate saleDate, 
                   String materialName, Double weight, Double pricePerKg, 
                   String buyerName) {
        this.saleId = saleId;
        this.saleType = saleType;
        this.saleDate = saleDate;
        this.materialName = materialName;
        this.weight = weight;
        this.pricePerKg = pricePerKg;
        this.totalRevenue = weight * pricePerKg;
        this.buyerName = buyerName;
    }
    
    //constructor collective salg
    public SaleDTO(Long collectiveSaleId, String saleType, LocalDate saleDate,
                   String materialName, Double weight, Double pricePerKg,
                   String buyerName, String status, Integer cooperativeCount) {
        this(null, saleType, saleDate, materialName, weight, pricePerKg, buyerName);
        this.collectiveSaleId = collectiveSaleId;
        this.cooperativeCount = cooperativeCount;
    }
    
    // getters and Setters
    public Long getSaleId() { return saleId; }
    public void setSaleId(Long saleId) { this.saleId = saleId; }
    
    public String getSaleType() { return saleType; }
    public void setSaleType(String saleType) { this.saleType = saleType; }
    
    public LocalDate getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }
    
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    
    public Double getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(Double pricePerKg) { this.pricePerKg = pricePerKg; }
    
    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    
    public Long getCollectiveSaleId() { return collectiveSaleId; }
    public void setCollectiveSaleId(Long collectiveSaleId) { 
        this.collectiveSaleId = collectiveSaleId; 
    }
    
    public Integer getCooperativeCount() { return cooperativeCount; }
    public void setCooperativeCount(Integer cooperativeCount) { 
        this.cooperativeCount = cooperativeCount; 
    }
}