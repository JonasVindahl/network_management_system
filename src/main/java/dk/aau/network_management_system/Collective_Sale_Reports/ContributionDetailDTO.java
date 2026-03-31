package dk.aau.network_management_system.Collective_Sale_Reports;



//For et enkelt coop bidrag i collective sale
public class ContributionDetailDTO {
    
    private Long cooperativeId;
    private String cooperativeName;
    private Double contributedWeight;
    private Double percentageOfTotal;
    private Double revenueShare;

    public ContributionDetailDTO(){}
    
    public ContributionDetailDTO(
    Long cooperativeId, String cooperativeName, 
    Double contributedWeight, Double totalWeight,
    Double revenueShare) {

        this.cooperativeId = cooperativeId;
        this.cooperativeName = cooperativeName;
        this.contributedWeight = contributedWeight;
        this.revenueShare = revenueShare;


        //procent bidragelse for coop
        if(totalWeight != null && totalWeight > 0){
            this.percentageOfTotal = (contributedWeight / totalWeight) * 100;
        } else {
            this.percentageOfTotal = 0.0;
        }
    }
        //getters and Setters
        public Long getCooperativeId() { return cooperativeId; }
        public void setCooperativeId(Long cooperativeId) { this.cooperativeId = cooperativeId; }
        
        public String getCooperativeName() { return cooperativeName; }
        public void setCooperativeName(String cooperativeName) { 
            this.cooperativeName = cooperativeName; 
        }
        
        public Double getContributedWeight() { return contributedWeight; }
        public void setContributedWeight(Double contributedWeight) { 
            this.contributedWeight = contributedWeight; 
        }
        
        public Double getPercentageOfTotal() { return percentageOfTotal; }
        public void setPercentageOfTotal(Double percentageOfTotal) { 
            this.percentageOfTotal = percentageOfTotal; 
        }
        
        public Double getRevenueShare() { return revenueShare; }
        public void setRevenueShare(Double revenueShare) { this.revenueShare = revenueShare; }



}
    

