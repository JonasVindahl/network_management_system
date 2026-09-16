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

class CreateSaleDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validDto_ShouldHaveNoViolations() {
        CreateSaleDTO dto = new CreateSaleDTO();
        dto.setMaterialId(1L);
        dto.setWeight(100.0);
        dto.setPriceKg(2.5);
        dto.setBuyerId(1L);
        dto.setExpectedSaleDate(Instant.now());

        Set<ConstraintViolation<CreateSaleDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void nullWeight_ShouldViolate() {
        CreateSaleDTO dto = new CreateSaleDTO();
        dto.setMaterialId(1L);
        dto.setPriceKg(2.5);
        dto.setBuyerId(1L);
        dto.setExpectedSaleDate(Instant.now());

        Set<ConstraintViolation<CreateSaleDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("weight")));
    }

    @Test
    void negativeWeight_ShouldViolate() {
        CreateSaleDTO dto = new CreateSaleDTO();
        dto.setMaterialId(1L);
        dto.setWeight(-10.0);
        dto.setPriceKg(2.5);
        dto.setBuyerId(1L);
        dto.setExpectedSaleDate(Instant.now());

        Set<ConstraintViolation<CreateSaleDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("weight")));
    }

    @Test
    void weightExceedsMax_ShouldViolate() {
        CreateSaleDTO dto = new CreateSaleDTO();
        dto.setMaterialId(1L);
        dto.setWeight(1_000_001.0);
        dto.setPriceKg(2.5);
        dto.setBuyerId(1L);
        dto.setExpectedSaleDate(Instant.now());

        Set<ConstraintViolation<CreateSaleDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("weight")));
    }

    @Test
    void priceKgExceedsMax_ShouldViolate() {
        CreateSaleDTO dto = new CreateSaleDTO();
        dto.setMaterialId(1L);
        dto.setWeight(100.0);
        dto.setPriceKg(100_001.0);
        dto.setBuyerId(1L);
        dto.setExpectedSaleDate(Instant.now());

        Set<ConstraintViolation<CreateSaleDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("priceKg")));
    }

    @Test
    void nullMaterialId_ShouldViolate() {
        CreateSaleDTO dto = new CreateSaleDTO();
        dto.setWeight(100.0);
        dto.setPriceKg(2.5);
        dto.setBuyerId(1L);
        dto.setExpectedSaleDate(Instant.now());

        Set<ConstraintViolation<CreateSaleDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("materialId")));
    }

    @Test
    void boundaryWeightMax_ShouldBeValid() {
        CreateSaleDTO dto = new CreateSaleDTO();
        dto.setMaterialId(1L);
        dto.setWeight(1_000_000.0);
        dto.setPriceKg(100_000.0);
        dto.setBuyerId(1L);
        dto.setExpectedSaleDate(Instant.now());

        Set<ConstraintViolation<CreateSaleDTO>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }
}