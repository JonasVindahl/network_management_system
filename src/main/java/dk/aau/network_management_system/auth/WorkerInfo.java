package dk.aau.network_management_system.auth;

public class WorkerInfo {
    private final Long workerId;
    private final Long cooperativeId;
    private final String role;
    
    public WorkerInfo(Long workerId, Long cooperativeId, String role) {
        this.workerId = workerId;
        this.cooperativeId = cooperativeId;
        this.role = role;
    }
    
    public Long getWorkerId() { return workerId; }
    public Long getCooperativeId() { return cooperativeId; }
    public String getRole() { return role; }
}