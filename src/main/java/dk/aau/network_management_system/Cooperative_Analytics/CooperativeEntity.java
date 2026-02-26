package dk.aau.network_management_system.Cooperative_Analytics;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cooperative", schema = "public")
public class CooperativeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cooperative_id")
    private Long cooperativeId;
    
    @Column(name = "cooperative_name", nullable = false)
    private String cooperativeName;
    
    @Column(name = "cooperative_location", nullable = false)
    private String cooperativeLocation;
    
    @Column(name = "contact_email", nullable = false)
    private String contactEmail;
    
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    public CooperativeEntity() {}
    
    // Getters and Setters
    public Long getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(Long cooperativeId) { 
        this.cooperativeId = cooperativeId; 
    }
    
    public String getCooperativeName() { return cooperativeName; }
    public void setCooperativeName(String cooperativeName) { 
        this.cooperativeName = cooperativeName; 
    }
    
    public String getCooperativeLocation() { return cooperativeLocation; }
    public void setCooperativeLocation(String cooperativeLocation) { 
        this.cooperativeLocation = cooperativeLocation; 
    }
    
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { 
        this.contactEmail = contactEmail; 
    }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { 
        this.phoneNumber = phoneNumber; 
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { 
        this.lastUpdated = lastUpdated; 
    }
}