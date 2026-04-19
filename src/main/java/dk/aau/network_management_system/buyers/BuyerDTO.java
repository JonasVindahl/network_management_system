package dk.aau.network_management_system.buyers;

public class BuyerDTO {

    private Long buyerId;
    private String buyerName;

    public BuyerDTO(Long buyerId, String buyerName) {
        this.buyerId = buyerId;
        this.buyerName = buyerName;
    }

    public Long getBuyerId() { return buyerId; }
    public void setBuyerId(Long buyerId) { this.buyerId = buyerId; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
}
