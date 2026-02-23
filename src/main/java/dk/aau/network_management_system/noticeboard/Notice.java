package dk.aau.network_management_system.noticeboard;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notice_board")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String noticeId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private long dateAdded;

    private long modifiedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityLevel priority;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false)
    private long timeAlive;

    // Optional: target audience - null means all cooperatives
    private String cooperativeId;

    public Notice() {
        this.dateAdded = Instant.now().toEpochMilli();
        this.modifiedDate = this.dateAdded;
    }

    public Notice(String title, String message, PriorityLevel priority, String senderId, long timeAlive, String cooperativeId) {
        this.title = title;
        this.message = message;
        this.priority = priority;
        this.senderId = senderId;
        this.timeAlive = timeAlive;
        this.cooperativeId = cooperativeId;
        this.dateAdded = Instant.now().toEpochMilli();
        this.modifiedDate = this.dateAdded;
    }

    // Getters and Setters
    public String getNoticeId() { return noticeId; }
    public void setNoticeId(String noticeId) { this.noticeId = noticeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getDateAdded() { return dateAdded; }
    public void setDateAdded(long dateAdded) { this.dateAdded = dateAdded; }

    public long getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(long modifiedDate) { this.modifiedDate = modifiedDate; }

    public PriorityLevel getPriority() { return priority; }
    public void setPriority(PriorityLevel priority) { this.priority = priority; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public long getTimeAlive() { return timeAlive; }
    public void setTimeAlive(long timeAlive) { this.timeAlive = timeAlive; }

    public String getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(String cooperativeId) { this.cooperativeId = cooperativeId; }

    public boolean isExpired() {
        return Instant.now().toEpochMilli() > (dateAdded + timeAlive);
    }
}
