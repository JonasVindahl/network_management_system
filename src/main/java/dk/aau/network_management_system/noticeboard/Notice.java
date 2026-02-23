package dk.aau.network_management_system.noticeboard;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notice_board")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // bigserial → IDENTITY
    @Column(name = "notice_id")
    private Long noticeId;                              // Long, ikke String


    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT", name = "content")
    private String content;         

    @Column(name = "created_at")
    private Instant createdAt;                          // timestamp, ikke long

    @Column(name = "last_updated")
    private Instant lastUpdated;                        // timestamp, ikke long

    @Enumerated(EnumType.ORDINAL)                       // smallint → ORDINAL
    @Column(nullable = false)
    private PriorityLevel priority;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;                             // bigint FK, ikke String

    @Column(name = "expires_at")
    private Instant expiresAt;                          // direkte timestamp i stedet for timeAlive

    @Column(name = "cooperative_id")
    private Long cooperativeId;                         // bigint, ikke String

    public Notice() {
        this.createdAt = Instant.now();
        this.lastUpdated = this.createdAt;
    }

    public Notice(String title, String message, PriorityLevel priority, Long senderId, Instant timeAlive, Long cooperativeId) {
        this.title = title;
        this.content = message;
        this.priority = priority;
        this.createdBy = senderId;
        this.expiresAt = timeAlive;
        this.cooperativeId = cooperativeId;
        this.createdAt = Instant.now();
        this.lastUpdated = this.createdAt;
    }

    // Getters and Setters
    public Long getNoticeId() { return noticeId; }
    public void setNoticeId(Long noticeId) { this.noticeId = noticeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return content; }
    public void setMessage(String message) { this.content = message; }

    public Instant getDateAdded() { return createdAt; }
    public void setDateAdded(Instant dateAdded) { this.createdAt = dateAdded; }

    public Instant getModifiedDate() { return lastUpdated; }
    public void setModifiedDate(Instant modifiedDate) { this.lastUpdated = modifiedDate; }

    public PriorityLevel getPriority() { return priority; }
    public void setPriority(PriorityLevel priority) { this.priority = priority; }

    public Long getSenderId() { return createdBy; }
    public void setSenderId(Long senderId) { this.createdBy = senderId; }

    public Instant getTimeAlive() { return expiresAt; }
    public void setTimeAlive(Instant timeAlive) { this.expiresAt = timeAlive; }

    public Long getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(Long cooperativeId) { this.cooperativeId = cooperativeId; }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
