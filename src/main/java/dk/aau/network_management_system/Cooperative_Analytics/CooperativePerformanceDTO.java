package dk.aau.network_management_system.Cooperative_Analytics;

public class CooperativePerformanceDTO {
    private Double totalCollected;
    private Double totalSold;
    private Double currentStock;
    private Integer activeWorkers;
    
    public CooperativePerformanceDTO() {}
    
    public CooperativePerformanceDTO(Double totalCollected, Double totalSold, 
                                      Double currentStock, Integer activeWorkers) {
        this.totalCollected = totalCollected;
        this.totalSold = totalSold;
        this.currentStock = currentStock;
        this.activeWorkers = activeWorkers;
    }
    
    // Getters and Setters
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
    
    public Integer getActiveWorkers() { return activeWorkers; }
    public void setActiveWorkers(Integer activeWorkers) { 
        this.activeWorkers = activeWorkers; 
    }
}