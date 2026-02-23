package dk.aau.network_management_system.multiplier;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity //database
@Table(name = "Cooperative_material_multiplier", schema = "public") //tabel fra database
public class CooperativeMaterialMultiplier {
    

    //Sætter primary key og genere uuid tilfældigt UUID med Hibernate's
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "cooperative_material_multiplier_id")
    private UUID id;
    

    // alle de forskellige kolonner i tebellen for "Cooperative_material_multiplier"
    // nullable kan vi bruge til at sætte om kolonnen må være tom/NULL
    @Column(name = "cooperative_id", nullable = false)
    private Long cooperativeId;
    
    @Column(name = "material_id", nullable = false)
    private Long materialId;
    
    @Column(name = "multiplier_value", nullable = false)
    private Double multiplierValue;
    
    // denne kolonne opdatere databsen selv
    @Column(name = "last_updated", insertable = false, updatable = false)
    private LocalDateTime lastUpdated;

    // https://www.baeldung.com/java-why-getters-setters
    // dette er getters and setters uden dem kan spring ikke Konvertere objektet til JSON(som er standart for rest api)
    // opretter metoder der kan tilgå de private "felter"
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Long getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(Long cooperativeId) { this.cooperativeId = cooperativeId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public Double getMultiplierValue() { return multiplierValue; }
    public void setMultiplierValue(Double multiplierValue) { this.multiplierValue = multiplierValue; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}