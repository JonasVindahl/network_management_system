package dk.aau.network_management_system.noticeboard;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notice_board")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT", name = "content")
    private String content;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "last_updated")
    private Instant lastUpdated;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private PriorityLevel priority;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "expires_at")
    private Instant expiresAt;

    // null = visible to all cooperatives
    @Column(name = "cooperative_id")
    private Long cooperativeId;

    public Notice() {
        this.createdAt = Instant.now();
        this.lastUpdated = this.createdAt;
    }

    public Notice(String title, String content, PriorityLevel priority, Long createdBy, Instant expiresAt, Long cooperativeId) {
        this.title = title;
        this.content = content;
        this.priority = priority;
        this.createdBy = createdBy;
        this.expiresAt = expiresAt;
        this.cooperativeId = cooperativeId;
        this.createdAt = Instant.now();
        this.lastUpdated = this.createdAt;
    }

    // Getters and Setters
    public Long getNoticeId() { return noticeId; }
    public void setNoticeId(Long noticeId) { this.noticeId = noticeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }

    public PriorityLevel getPriority() { return priority; }
    public void setPriority(PriorityLevel priority) { this.priority = priority; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public Long getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(Long cooperativeId) { this.cooperativeId = cooperativeId; }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }
}
