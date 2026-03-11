package dk.aau.network_management_system.multiplier;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class MultiplierDTO {
    
    private Long cooperativeId;
    
    @NotNull(message = "Material ID is required")
    private Long materialId;
    
    @NotNull(message = "Multiplier value is required")
    @Positive(message = "Multiplier value must be positive")
    private Double multiplierValue;
    
    // Constructors
    public MultiplierDTO() {}
    
    public MultiplierDTO(Long cooperativeId, Long materialId, Double multiplierValue) {
        this.cooperativeId = cooperativeId;
        this.materialId = materialId;
        this.multiplierValue = multiplierValue;
    }
    
    // Getters and Setters
    public Long getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(Long cooperativeId) { this.cooperativeId = cooperativeId; }
    
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    
    public Double getMultiplierValue() { return multiplierValue; }
    public void setMultiplierValue(Double multiplierValue) { 
        this.multiplierValue = multiplierValue; 
    }
}