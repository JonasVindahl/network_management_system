package dk.aau.network_management_system.materials;

public class MaterialRequest {
    private long materialId;
    private long workerId;
    private double amount;
    private boolean bagFull;
    private long deviceId;

    public long getMaterialId() { return materialId; }
    public void setMaterialId(long materialId) { this.materialId = materialId; }

    public long getWorkerId() { return workerId; }
    public void setWorkerId(long workerId) { this.workerId = workerId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public boolean isBagFull() { return bagFull; }
    public void setBagFull(boolean bagFull) { this.bagFull = bagFull; }

    public long getDeviceId() { return deviceId; }
    public void setDeviceId(long deviceId) { this.deviceId = deviceId; }
}