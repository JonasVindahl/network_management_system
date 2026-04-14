package dk.aau.network_management_system.Collective_Sale_Reports;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaleReportDTO {

    private Long saleId;
    private LocalDateTime createdAt;
    private LocalDateTime soldAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime expectedSaleDate;

    private BigDecimal weight;
    private BigDecimal priceKg;
    private BigDecimal totalRevenue;

    private Long materialId;
    private String materialName;

    private Long buyerId;
    private String buyerName;

    private Long responsibleWorkerId;
    private String responsibleWorkerName;

    private Long cooperativeId;
    private String cooperativeName;

    public SaleReportDTO(Object[] row) {
        this.saleId               = toLong(row[0]);
        this.createdAt            = toLocalDateTime(row[1]);
        this.soldAt               = toLocalDateTime(row[2]);
        this.cancelledAt          = toLocalDateTime(row[3]);
        this.expectedSaleDate     = toLocalDateTime(row[4]);
        this.weight               = toBigDecimal(row[5]);
        this.priceKg              = toBigDecimal(row[6]);
        this.totalRevenue         = toBigDecimal(row[7]);
        this.materialId           = toLong(row[8]);
        this.materialName         = (String) row[9];
        this.buyerId              = toLong(row[10]);
        this.buyerName            = (String) row[11];
        this.responsibleWorkerId  = toLong(row[12]);
        this.responsibleWorkerName = (String) row[13];
        this.cooperativeId        = toLong(row[14]);
        this.cooperativeName      = (String) row[15];
    }

    private Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        return Long.parseLong(o.toString());
    }

    private BigDecimal toBigDecimal(Object o) {
        if (o == null) return null;
        if (o instanceof BigDecimal bd) return bd;
        return new BigDecimal(o.toString());
    }

    private LocalDateTime toLocalDateTime(Object o) {
        if (o == null) return null;
        if (o instanceof LocalDateTime ldt) return ldt;
        if (o instanceof java.sql.Timestamp ts) return ts.toLocalDateTime();
        return LocalDateTime.parse(o.toString());
    }

    public Long getSaleId()                      { return saleId; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public LocalDateTime getSoldAt()             { return soldAt; }
    public LocalDateTime getCancelledAt()        { return cancelledAt; }
    public LocalDateTime getExpectedSaleDate()   { return expectedSaleDate; }
    public BigDecimal getWeight()                { return weight; }
    public BigDecimal getPriceKg()               { return priceKg; }
    public BigDecimal getTotalRevenue()          { return totalRevenue; }
    public Long getMaterialId()                  { return materialId; }
    public String getMaterialName()              { return materialName; }
    public Long getBuyerId()                     { return buyerId; }
    public String getBuyerName()                 { return buyerName; }
    public Long getResponsibleWorkerId()         { return responsibleWorkerId; }
    public String getResponsibleWorkerName()     { return responsibleWorkerName; }
    public Long getCooperativeId()               { return cooperativeId; }
    public String getCooperativeName()           { return cooperativeName; }
}