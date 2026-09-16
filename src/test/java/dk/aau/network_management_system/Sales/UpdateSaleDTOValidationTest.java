package dk.aau.network_management_system.Sales;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateSaleDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validDto_ShouldHaveNoViolations() {
        UpdateSaleDTO dto = new UpdateSaleDTO();
        dto.setWeight(100.0);
        dto.setPriceKg(2.5);
        dto.setMaterialId(1L);
        dto.setBuyerId(1L);
        dto.setExpectedSaleDate(Instant.now());

        Set<ConstraintViolation<UpdateSaleDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void negativeWeight_ShouldViolate() {
        UpdateSaleDTO dto = new UpdateSaleDTO();
        dto.setWeight(-1.0);

        Set<ConstraintViolation<UpdateSaleDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("weight")));
    }

    @Test
    void weightExceedsMax_ShouldViolate() {
        UpdateSaleDTO dto = new UpdateSaleDTO();
        dto.setWeight(1_000_001.0);

        Set<ConstraintViolation<UpdateSaleDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("weight")));
    }

    @Test
    void priceKgExceedsMax_ShouldViolate() {
        UpdateSaleDTO dto = new UpdateSaleDTO();
        dto.setPriceKg(100_001.0);

        Set<ConstraintViolation<UpdateSaleDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("priceKg")));
    }

    @Test
    void nullFields_ShouldBeValid() {
        UpdateSaleDTO dto = new UpdateSaleDTO();

        Set<ConstraintViolation<UpdateSaleDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void boundaryWeightMax_ShouldBeValid() {
        UpdateSaleDTO dto = new UpdateSaleDTO();
        dto.setWeight(1_000_000.0);

        Set<ConstraintViolation<UpdateSaleDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void boundaryPriceKgMax_ShouldBeValid() {
        UpdateSaleDTO dto = new UpdateSaleDTO();
        dto.setPriceKg(100_000.0);

        Set<ConstraintViolation<UpdateSaleDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}