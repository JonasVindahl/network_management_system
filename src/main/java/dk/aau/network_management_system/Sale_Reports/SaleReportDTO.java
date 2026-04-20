package dk.aau.network_management_system.Sale_Reports;

import java.time.Instant;

public class SaleReportDTO {

    private Long saleId;
    private String status; 
    
    private Long materialId;
    private String materialName;
    
    private Long buyerId;
    private String buyerName;
    
    private Long responsibleWorkerId;
    private String responsibleWorkerName;
    
    private Long cooperativeId;
    private String cooperativeName;
    
    private Instant createdAt;
    private Instant soldAt;
    private Instant cancelledAt;
    private Instant expectedSaleDate;
    
    private Double weight;
    private Double pricePerKg;
    private Double totalRevenue;
    
    public SaleReportDTO() {}
    
    public SaleReportDTO(Long saleId, Long materialId, String materialName,
                        Long buyerId, String buyerName,
                        Long responsibleWorkerId, String responsibleWorkerName,
                        Long cooperativeId, String cooperativeName,
                        Instant createdAt, Instant soldAt, Instant cancelledAt,
                        Instant expectedSaleDate,
                        Double weight, Double pricePerKg) {
        this.saleId = saleId;
        this.materialId = materialId;
        this.materialName = materialName;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.responsibleWorkerId = responsibleWorkerId;
        this.responsibleWorkerName = responsibleWorkerName;
        this.cooperativeId = cooperativeId;
        this.cooperativeName = cooperativeName;
        this.createdAt = createdAt;
        this.soldAt = soldAt;
        this.cancelledAt = cancelledAt;
        this.expectedSaleDate = expectedSaleDate;
        this.weight = weight;
        this.pricePerKg = pricePerKg;
        this.totalRevenue = weight * pricePerKg;
        
        if (cancelledAt != null) {
            this.status = "CANCELLED";
        } else if (soldAt != null) {
            this.status = "SOLD";
        } else {
            this.status = "ACTIVE";
        }
    }
    
    // Getters and Setters
    public Long getSaleId() { return saleId; }
    public void setSaleId(Long saleId) { this.saleId = saleId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    
    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }
    
    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    
    public Long getResponsibleWorkerId() { return responsibleWorkerId; }
    public void setResponsibleWorkerId(Long responsibleWorkerId) { 
        this.responsibleWorkerId = responsibleWorkerId; 
    }
    
    public String getResponsibleWorkerName() { return responsibleWorkerName; }
    public void setResponsibleWorkerName(String responsibleWorkerName) { 
        this.responsibleWorkerName = responsibleWorkerName; 
    }
    
    public Long getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(Long cooperativeId) { this.cooperativeId = cooperativeId; }
    
    public String getCooperativeName() { return cooperativeName; }
    public void setCooperativeName(String cooperativeName) { 
        this.cooperativeName = cooperativeName; 
    }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getSoldAt() { return soldAt; }
    public void setSoldAt(Instant soldAt) { 
        this.soldAt = soldAt;
        updateStatus();
    }
    
    public Instant getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Instant cancelledAt) { 
        this.cancelledAt = cancelledAt;
        updateStatus();
    }
    
    public Instant getExpectedSaleDate() { return expectedSaleDate; }
    public void setExpectedSaleDate(Instant expectedSaleDate) { 
        this.expectedSaleDate = expectedSaleDate; 
    }
    
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

    private void updateStatus() {
        if (cancelledAt != null) {
            this.status = "CANCELLED";
        } else if (soldAt != null) {
            this.status = "SOLD";
        } else {
            this.status = "ACTIVE";
        }
    }
}