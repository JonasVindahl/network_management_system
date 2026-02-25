package dk.aau.network_management_system.Cooperative_Analytics;

public class RevenueDTO {
    private Double totalRevenue;
    private Long totalSales;
    private Double avgPricePerKg;
    
    public RevenueDTO() {}
    
    public RevenueDTO(Double totalRevenue, Long totalSales, Double avgPricePerKg) {
        this.totalRevenue = totalRevenue;
        this.totalSales = totalSales;
        this.avgPricePerKg = avgPricePerKg;
    }
    
    //Getters and Setters
    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { 
        this.totalRevenue = totalRevenue; 
    }
    
    public Long getTotalSales() { return totalSales; }
    public void setTotalSales(Long totalSales) { this.totalSales = totalSales; }
    
    public Double getAvgPricePerKg() { return avgPricePerKg; }
    public void setAvgPricePerKg(Double avgPricePerKg) { 
        this.avgPricePerKg = avgPricePerKg; 
    }
}