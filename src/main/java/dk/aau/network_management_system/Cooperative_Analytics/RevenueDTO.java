package dk.aau.network_management_system.Cooperative_Analytics;
public class RevenueDTO {
    private Double totalRevenue;
    private Long totalSales;
    private Double avgPricePerKg;
    private String materialName;  // <-- manglede
    private Long materialId;

    public RevenueDTO() {}
    
    public RevenueDTO(Double totalRevenue, Long totalSales, Double avgPricePerKg, String materialName, Long materialId) {
        this.totalRevenue = totalRevenue;
        this.totalSales = totalSales;
        this.avgPricePerKg = avgPricePerKg;
        this.materialName = materialName;
        this.materialId = materialId;
    }
    
    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public Long getTotalSales() { return totalSales; }
    public void setTotalSales(Long totalSales) { this.totalSales = totalSales; }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    
    public Double getAvgPricePerKg() { return avgPricePerKg; }
    public void setAvgPricePerKg(Double avgPricePerKg) { this.avgPricePerKg = avgPricePerKg; }
}