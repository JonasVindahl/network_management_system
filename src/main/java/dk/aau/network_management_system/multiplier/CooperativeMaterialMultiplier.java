package dk.aau.network_management_system.multiplier;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cooperative_material_multiplier")
public class CooperativeMaterialMultiplier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cooperative_material_multiplier_id")
    private UUID multiplierId;
    
    @Column(name = "cooperative_id", nullable = false)
    private Long cooperativeId;
    
    @Column(name = "material_id", nullable = false)
    private Long materialId;
    
    @Column(name = "multiplier_value", nullable = false)
    private Double multiplierValue;
    
    @Column(name = "last_updated")
    private Instant lastUpdated;
    
    // constructor til JPA
    public CooperativeMaterialMultiplier() {
        this.lastUpdated = Instant.now();
    }
    
    // constructor
    public CooperativeMaterialMultiplier(Long cooperativeId, Long materialId, Double multiplierValue) {
        this.cooperativeId = cooperativeId;
        this.materialId = materialId;
        this.multiplierValue = multiplierValue;
        this.lastUpdated = Instant.now();
    }
    
    // getters and Setters 
    public UUID getMultiplierId() {
        return multiplierId;
    }
    
    public void setMultiplierId(UUID multiplierId) {
        this.multiplierId = multiplierId;
    }
    
    public Long getCooperativeId() {
        return cooperativeId;
    }
    
    public void setCooperativeId(Long cooperativeId) {
        this.cooperativeId = cooperativeId;
    }
    
    public Long getMaterialId() {
        return materialId;
    }
    
    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }
    
    public Double getMultiplierValue() {
        return multiplierValue;
    }
    
    public void setMultiplierValue(Double multiplierValue) {
        this.multiplierValue = multiplierValue;
    }
    
    public Instant getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}