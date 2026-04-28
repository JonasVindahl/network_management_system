package dk.aau.network_management_system.multiplier;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public class MultiplierDTO {
    private Long cooperativeId;
    private Long materialId;
    private String materialName;

    @NotNull
    @DecimalMin(value = "0.1", message = "Multiplier must be at least 0.1")
    @DecimalMax(value = "10", message = "Multiplier cannot exceed 10")
    @Digits(integer = 2, fraction = 3, message = "Max 3 decimal places")
    private Double multiplierValue;

    public MultiplierDTO() {}

    public MultiplierDTO(Long cooperativeId, Long materialId, String materialName, Double multiplierValue) {
        this.cooperativeId = cooperativeId;
        this.materialId = materialId;
        this.materialName = materialName;
        this.multiplierValue = multiplierValue;
    }

    // Getters and Setters
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    
    public Long getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(Long cooperativeId) { this.cooperativeId = cooperativeId; }
    
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    
    public Double getMultiplierValue() { return multiplierValue; }
    public void setMultiplierValue(Double multiplierValue) { 
        this.multiplierValue = multiplierValue; 
    }
}