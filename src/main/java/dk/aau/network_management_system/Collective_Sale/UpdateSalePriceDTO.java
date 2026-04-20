package dk.aau.network_management_system.Collective_Sale;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class UpdateSalePriceDTO {

    @NotNull(message = "Price per kg is required")
    @DecimalMin(value = "0.01", message = "Price per kg must be greater than 0")
    private BigDecimal pricePerKg;

    public UpdateSalePriceDTO() {}

    public UpdateSalePriceDTO(BigDecimal pricePerKg) {
        this.pricePerKg = pricePerKg;
    }

    public BigDecimal getPricePerKg() { return pricePerKg; }
    public void setPricePerKg(BigDecimal pricePerKg) { this.pricePerKg = pricePerKg; }
}
