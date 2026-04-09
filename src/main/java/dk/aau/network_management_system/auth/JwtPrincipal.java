package dk.aau.network_management_system.auth;

public class JwtPrincipal {

    private final String cpf;
    private final String role;
    private final Long cooperativeId;
    private final Long workerId;

    public JwtPrincipal(String cpf, String role, Long cooperativeId, Long workerId) {
        this.cpf = cpf;
        this.role = role;
        this.cooperativeId = cooperativeId;
        this.workerId = workerId;
    }

    public String getCpf() { return cpf; }
    public String getRole() { return role; }
    public Long getCooperativeId() { return cooperativeId; }
    public Long getWorkerId() { return workerId; }
}
