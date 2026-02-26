package dk.aau.network_management_system.auth;

public class WorkerInfo {
    private Long workerId;
    private Long cooperativeId;
    private String role;
    
    public WorkerInfo(Long workerId, Long cooperativeId, String role) {
        this.workerId = workerId;
        this.cooperativeId = cooperativeId;
        this.role = role;
    }
    
    public Long getWorkerId() { return workerId; }
    public Long getCooperativeId() { return cooperativeId; }
    public String getRole() { return role; }
}