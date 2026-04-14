package dk.aau.network_management_system.Collective_Sale;

import jakarta.validation.constraints.NotNull;

public class UpdateSaleMaterialDTO {

    @NotNull(message = "Material ID is required")
    private Long materialId;

    public UpdateSaleMaterialDTO() {}

    public UpdateSaleMaterialDTO(Long materialId) {
        this.materialId = materialId;
    }

    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
}
