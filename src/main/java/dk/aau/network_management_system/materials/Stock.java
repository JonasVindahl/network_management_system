package dk.aau.network_management_system.materials;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "stock", schema = "public")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long stockId;

    @Column(name = "cooperative", nullable = false)
    private Long cooperative;

    @Column(name = "material", nullable = false)
    private Long material;

    @Column(name = "total_collected_kg", nullable = false)
    private BigDecimal totalCollectedKg;

    @Column(name = "total_sold_kg", nullable = false)
    private BigDecimal totalSoldKg;

    @Column(name = "current_stock_kg", nullable = false)
    private BigDecimal currentStockKg;

    public Long getStockId()                             { return stockId; }
    public void setStockId(Long id)                      { this.stockId = id; }
    public Long getCooperative()                         { return cooperative; }
    public void setCooperative(Long c)                   { this.cooperative = c; }
    public Long getMaterial()                            { return material; }
    public void setMaterial(Long m)                      { this.material = m; }
    public BigDecimal getTotalCollectedKg()              { return totalCollectedKg; }
    public void setTotalCollectedKg(BigDecimal kg)       { this.totalCollectedKg = kg; }
    public BigDecimal getTotalSoldKg()                   { return totalSoldKg; }
    public void setTotalSoldKg(BigDecimal kg)            { this.totalSoldKg = kg; }
    public BigDecimal getCurrentStockKg()                { return currentStockKg; }
    public void setCurrentStockKg(BigDecimal kg)         { this.currentStockKg = kg; }
}