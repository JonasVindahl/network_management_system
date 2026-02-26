package dk.aau.network_management_system.noticeboard;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NoticeDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Priority is required")
    @Min(value = 1, message = "Priority must be between 1 and 3")
    @Max(value = 3, message = "Priority must be between 1 and 3")
    private Integer priority;  // Ændret til Integer

    @NotNull(message = "Creator ID is required")
    private Long createdBy;

    @NotNull(message = "Expiry time is required")
    @Future(message = "Expiry time must be in the future")
    private Instant expiresAt;

    private Long cooperativeId;

    public NoticeDTO() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public Long getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(Long cooperativeId) { this.cooperativeId = cooperativeId; }
}