package dk.aau.network_management_system.Collective_Sale;

import jakarta.validation.constraints.NotNull;

public class InviteCooperativeDTO {

    @NotNull(message = "Cooperative ID is required")
    private Long cooperativeId;

    public InviteCooperativeDTO() {}

    public InviteCooperativeDTO(Long cooperativeId) {
        this.cooperativeId = cooperativeId;
    }

    public Long getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(Long cooperativeId) { this.cooperativeId = cooperativeId; }
}
