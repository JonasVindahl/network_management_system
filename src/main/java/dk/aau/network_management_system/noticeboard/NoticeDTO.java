package dk.aau.network_management_system.noticeboard;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class NoticeDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Priority is required")
    private PriorityLevel priority;

    @NotNull(message = "Sender ID is required")
    private Long senderId;

    @NotNull(message = "Expiry time is required")
    private Instant expiresAt;

    // null = visible to all cooperatives
    private Long cooperativeId;

    public NoticeDTO() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public PriorityLevel getPriority() { return priority; }
    public void setPriority(PriorityLevel priority) { this.priority = priority; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Instant getTimeAlive() { return expiresAt; }
    public void setTimeAlive(Instant expiresAt) { this.expiresAt = expiresAt; }

    public Long getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(Long cooperativeId) { this.cooperativeId = cooperativeId; }
}
