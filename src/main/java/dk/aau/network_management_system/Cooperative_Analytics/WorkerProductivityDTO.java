package dk.aau.network_management_system.Cooperative_Analytics;

public class WorkerProductivityDTO {
    private Long workerId;
    private String workerName;
    private Double totalCollectedKg;
    private Long numberOfWeighings;
    private Double avgWeightPerWeighing;
    
    // Constructor
    public WorkerProductivityDTO() {}
    
    public WorkerProductivityDTO(Long workerId, String workerName, 
                                  Double totalCollectedKg, Long numberOfWeighings,
                                  Double avgWeightPerWeighing) {
        this.workerId = workerId;
        this.workerName = workerName;
        this.totalCollectedKg = totalCollectedKg;
        this.numberOfWeighings = numberOfWeighings;
        this.avgWeightPerWeighing = avgWeightPerWeighing;
    }
    
    // Getters and Setters
    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }
    
    public String getWorkerName() { return workerName; }
    public void setWorkerName(String workerName) { this.workerName = workerName; }
    
    public Double getTotalCollectedKg() { return totalCollectedKg; }
    public void setTotalCollectedKg(Double totalCollectedKg) { 
        this.totalCollectedKg = totalCollectedKg; 
    }
    
    public Long getNumberOfWeighings() { return numberOfWeighings; }
    public void setNumberOfWeighings(Long numberOfWeighings) { 
        this.numberOfWeighings = numberOfWeighings; 
    }
    
    public Double getAvgWeightPerWeighing() { return avgWeightPerWeighing; }
    public void setAvgWeightPerWeighing(Double avgWeightPerWeighing) { 
        this.avgWeightPerWeighing = avgWeightPerWeighing; 
    }
}