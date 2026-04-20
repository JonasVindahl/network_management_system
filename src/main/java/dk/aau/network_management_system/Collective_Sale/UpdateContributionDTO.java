package dk.aau.network_management_system.Collective_Sale;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class UpdateContributionDTO {

    @NotNull(message = "Weight is required")
    @PositiveOrZero(message = "Weight must be zero or positive")
    private BigDecimal weight;

    public UpdateContributionDTO() {}

    public UpdateContributionDTO(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
}
