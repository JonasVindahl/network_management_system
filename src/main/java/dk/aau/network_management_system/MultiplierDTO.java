package dk.aau.network_management_system;

// gør at klienten kun kan sende de nederse 3 
// skal ændres til kun at være material og muliplier 
// cooperative id skal vi gerne have fra man logger ind
public class MultiplierDTO {
    private Long cooperativeId;
    private Long materialId;
    private Double multiplierValue;

    // getters and setteres
    public Long getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(Long cooperativeId) { this.cooperativeId = cooperativeId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public Double getMultiplierValue() { return multiplierValue; }
    public void setMultiplierValue(Double multiplierValue) { this.multiplierValue = multiplierValue; }
}