package dk.aau.network_management_system.materials;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "material_bag_state", schema = "public")
public class MaterialBagState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bag_state_id")
    private Long bagStateId;

    @Column(name = "cooperative_id", nullable = false)
    private Long cooperativeId;

    @Column(name = "material_id", nullable = false)
    private Long materialId;

    @Column(name = "is_begun", nullable = false)
    private boolean isBegun;

    @Column(name = "current_kg", nullable = false)
    private BigDecimal currentKg;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    public Long getBagStateId()                      { return bagStateId; }
    public void setBagStateId(Long id)               { this.bagStateId = id; }
    public Long getCooperativeId()                   { return cooperativeId; }
    public void setCooperativeId(Long id)            { this.cooperativeId = id; }
    public Long getMaterialId()                      { return materialId; }
    public void setMaterialId(Long id)               { this.materialId = id; }
    public boolean isBegun()                         { return isBegun; }
    public void setBegun(boolean begun)              { this.isBegun = begun; }
    public BigDecimal getCurrentKg()                 { return currentKg; }
    public void setCurrentKg(BigDecimal kg)          { this.currentKg = kg; }
    public LocalDateTime getLastUpdated()            { return lastUpdated; }
    public void setLastUpdated(LocalDateTime t)      { this.lastUpdated = t; }
}