package dk.aau.network_management_system.Cooperative_Analytics;

public class CooperativeDTO {

    private Long cooperativeId;
    private String cooperativeName;

    public CooperativeDTO(Long cooperativeId, String cooperativeName) {
        this.cooperativeId = cooperativeId;
        this.cooperativeName = cooperativeName;
    }

    public Long getCooperativeId() { return cooperativeId; }
    public void setCooperativeId(Long cooperativeId) { this.cooperativeId = cooperativeId; }

    public String getCooperativeName() { return cooperativeName; }
    public void setCooperativeName(String cooperativeName) { this.cooperativeName = cooperativeName; }
}
