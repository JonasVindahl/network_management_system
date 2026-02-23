package dk.aau.network_management_system.noticeboard;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NoticeDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Priority is required")
    private PriorityLevel priority;

    @NotBlank(message = "Sender ID is required")
    private String senderId;

    @NotNull(message = "Time alive is required")
    private long timeAlive;

    // null = visible to all cooperatives
    private String cooperativeId;

    public NoticeDTO() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public PriorityLevel getPriority() { return priority; }
    public void setPriority(PriorityLevel priority) { this.priority = priority; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public long getTimeAlive() { return timeAlive; }
    public void setTimeAlive(long timeAlive) { this.timeAlive = timeAlive; }

    public String getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(String cooperativeId) { this.cooperativeId = cooperativeId; }
}
