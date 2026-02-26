package dk.aau.network_management_system.Cooperative_Analytics;

public class StockByMaterialDTO {
    private String materialName;
    private Double totalCollected;
    private Double totalSold;
    private Double currentStock;
    
    public StockByMaterialDTO() {}
    
    public StockByMaterialDTO(String materialName, Double totalCollected, 
                               Double totalSold, Double currentStock) {
        this.materialName = materialName;
        this.totalCollected = totalCollected;
        this.totalSold = totalSold;
        this.currentStock = currentStock;
    }
    
    // Getters and Setters
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { 
        this.materialName = materialName; 
    }
    
    public Double getTotalCollected() { return totalCollected; }
    public void setTotalCollected(Double totalCollected) { 
        this.totalCollected = totalCollected; 
    }
    
    public Double getTotalSold() { return totalSold; }
    public void setTotalSold(Double totalSold) { this.totalSold = totalSold; }
    
    public Double getCurrentStock() { return currentStock; }
    public void setCurrentStock(Double currentStock) { 
        this.currentStock = currentStock; 
    }
}