package dk.aau.network_management_system.materials;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "measurements", schema = "public")
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weighting_id")
    private Long weightingId;

    @Column(name = "weight_kg", nullable = false)
    private BigDecimal weightKg;

    @Column(name = "time_stamp", nullable = false)
    private LocalDateTime timeStamp;

    @Column(name = "wastepicker", nullable = false)
    private Long wastepicker;

    @Column(name = "material", nullable = false)
    private Long material;

    @Column(name = "device", nullable = false)
    private Long device;

    @Column(name = "bag_filled", nullable = false)
    private boolean bagFilled;

    public Long getWeightingId()                 { return weightingId; }
    public void setWeightingId(Long id)          { this.weightingId = id; }
    public BigDecimal getWeightKg()              { return weightKg; }
    public void setWeightKg(BigDecimal kg)       { this.weightKg = kg; }
    public LocalDateTime getTimeStamp()          { return timeStamp; }
    public void setTimeStamp(LocalDateTime t)    { this.timeStamp = t; }
    public Long getWastepicker()                 { return wastepicker; }
    public void setWastepicker(Long w)           { this.wastepicker = w; }
    public Long getMaterial()                    { return material; }
    public void setMaterial(Long m)              { this.material = m; }
    public Long getDevice()                      { return device; }
    public void setDevice(Long d)                { this.device = d; }
    public boolean isBagFilled()                 { return bagFilled; }
    public void setBagFilled(boolean b)          { this.bagFilled = b; }
}