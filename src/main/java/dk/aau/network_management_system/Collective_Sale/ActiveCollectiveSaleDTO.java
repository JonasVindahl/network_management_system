package dk.aau.network_management_system.Collective_Sale;

import java.math.BigDecimal;
import java.time.Instant;

public class ActiveCollectiveSaleDTO {

    private Long collectiveSaleId;
    private String materialName;
    private String buyerName;
    private BigDecimal pricePerKg;
    private Instant expectedSaleDate;
    private Instant createdAt;
    private Long creatorCooperativeId;
    private String myStatus; // ACCEPTED or INVITED for managers; null for admins viewing all

    public ActiveCollectiveSaleDTO(Long collectiveSaleId, String materialName, String buyerName,
                                   BigDecimal pricePerKg, Instant expectedSaleDate, Instant createdAt,
                                   Long creatorCooperativeId, String myStatus) {
        this.collectiveSaleId = collectiveSaleId;
        this.materialName = materialName;
        this.buyerName = buyerName;
        this.pricePerKg = pricePerKg;
        this.expectedSaleDate = expectedSaleDate;
        this.createdAt = createdAt;
        this.creatorCooperativeId = creatorCooperativeId;
        this.myStatus = myStatus;
    }

    public Long getCollectiveSaleId() { return collectiveSaleId; }
    public void setCollectiveSaleId(Long collectiveSaleId) { this.collectiveSaleId = collectiveSaleId; }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }

    public String getBuyerName() { return buyerName; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }

    public BigDecimal getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(BigDecimal pricePerKg) { this.pricePerKg = pricePerKg; }

    public Instant getExpectedSaleDate() { return expectedSaleDate; }
    public void setExpectedSaleDate(Instant expectedSaleDate) { this.expectedSaleDate = expectedSaleDate; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Long getCreatorCooperativeId() { return creatorCooperativeId; }
    public void setCreatorCooperativeId(Long creatorCooperativeId) { this.creatorCooperativeId = creatorCooperativeId; }

    public String getMyStatus() { return myStatus; }
    public void setMyStatus(String myStatus) { this.myStatus = myStatus; }
}
