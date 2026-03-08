package dk.aau.network_management_system.Cooperative_Analytics;


import java.time.LocalDate;

public class Last5SalesDTO {
    private Long material;
    private Double weight;
    private Double priceKg;
    private LocalDate date;

    public Last5SalesDTO() {}

    public Last5SalesDTO(Long material, Double weight, Double priceKg, LocalDate date) {
        this.material = material;
        this.weight = weight;
        this.priceKg = priceKg;
        this.date = date;
    }

    public Long getMaterial() { return material; }
    public void setMaterial(Long material) { this.material = material; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Double getPriceKg() { return priceKg; }
    public void setPriceKg(Double priceKg) { this.priceKg = priceKg; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}