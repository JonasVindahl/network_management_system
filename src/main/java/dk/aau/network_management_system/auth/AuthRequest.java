package dk.aau.network_management_system.auth;

public class AuthRequest {
    private String cpf;
    private String password;

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}