package dk.aau.network_management_system.Sales;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SaleDTOTest {

    @Test
    void constructor_NoSoldAtNoCancelledAt_ShouldHavePendingStatus() {
        SaleDTO dto = new SaleDTO(1L, "REGULAR", Instant.now(), null, null,
                Instant.now().plusSeconds(86400), "Glass", 100.0, 2.5, "Buyer A");

        assertEquals("PENDING", dto.getStatus());
        assertEquals(250.0, dto.getTotalRevenue());
        assertEquals(1L, dto.getSaleId());
        assertEquals("Glass", dto.getMaterialName());
        assertEquals("Buyer A", dto.getBuyerName());
    }

    @Test
    void constructor_WithSoldAt_ShouldHaveCompletedStatus() {
        SaleDTO dto = new SaleDTO(1L, "REGULAR", Instant.now(), Instant.now(), null,
                null, "Glass", 100.0, 2.5, "Buyer A");

        assertEquals("COMPLETED", dto.getStatus());
    }

    @Test
    void constructor_WithCancelledAt_ShouldHaveCancelledStatus() {
        SaleDTO dto = new SaleDTO(1L, "REGULAR", Instant.now(), null, Instant.now(),
                null, "Glass", 100.0, 2.5, "Buyer A");

        assertEquals("CANCELLED", dto.getStatus());
    }

    @Test
    void collectiveConstructor_ShouldSetCollectiveFields() {
        SaleDTO dto = new SaleDTO(1L, "COLLECTIVE", Instant.now(), null, null,
                "Glass", 200.0, 3.0, "Buyer B", 5);

        assertEquals(1L, dto.getCollectiveSaleId());
        assertEquals(5, dto.getCooperativeCount());
        assertEquals(600.0, dto.getTotalRevenue());
        assertEquals("PENDING", dto.getStatus());
    }

    @Test
    void setSoldAt_ShouldUpdateStatusToCompleted() {
        SaleDTO dto = new SaleDTO();
        dto.setSoldAt(Instant.now());
        assertEquals("COMPLETED", dto.getStatus());
    }

    @Test
    void setSoldAt_Null_ShouldUpdateStatusToPending() {
        SaleDTO dto = new SaleDTO();
        dto.setSoldAt(Instant.now());
        dto.setSoldAt(null);
        assertEquals("PENDING", dto.getStatus());
    }

    @Test
    void setCancelledAt_ShouldUpdateStatusToCancelled() {
        SaleDTO dto = new SaleDTO();
        dto.setCancelledAt(Instant.now());
        assertEquals("CANCELLED", dto.getStatus());
    }

    @Test
    void setWeightAndPrice_ShouldCalculateRevenue() {
        SaleDTO dto = new SaleDTO();
        dto.setWeight(50.0);
        dto.setPricePerKg(4.0);
        assertEquals(200.0, dto.getTotalRevenue());
    }

    @Test
    void constructor_RevenueIsWeightTimesPricePerKg() {
        SaleDTO dto = new SaleDTO(1L, "REGULAR", Instant.now(), null, null,
                null, "Plastic", 75.0, 8.0, "Buyer C");

        assertEquals(600.0, dto.getTotalRevenue());
    }

    @Test
    void zeroWeight_ShouldHaveZeroRevenue() {
        SaleDTO dto = new SaleDTO(1L, "REGULAR", Instant.now(), null, null,
                null, "Glass", 0.0, 5.0, "Buyer A");

        assertEquals(0.0, dto.getTotalRevenue());
    }

    @Test
    void zeroPricePerKg_ShouldHaveZeroRevenue() {
        SaleDTO dto = new SaleDTO(1L, "REGULAR", Instant.now(), null, null,
                null, "Glass", 100.0, 0.0, "Buyer A");

        assertEquals(0.0, dto.getTotalRevenue());
    }

    @Test
    void veryLargeWeight_ShouldHandleWithoutOverflow() {
        SaleDTO dto = new SaleDTO(1L, "REGULAR", Instant.now(), null, null,
                null, "Sand", 1_000_000.0, 100.0, "Buyer Big");

        assertEquals(100_000_000.0, dto.getTotalRevenue());
    }

    @Test
    void decimalWeightAndPrice_ShouldCalculateCorrectly() {
        SaleDTO dto = new SaleDTO(1L, "REGULAR", Instant.now(), null, null,
                null, "Gold", 0.5, 1234.56, "Buyer Lux");

        assertEquals(617.28, dto.getTotalRevenue(), 0.001);
    }

    @Test
    void nullExpectedSaleDate_ShouldNotThrow() {
        SaleDTO dto = new SaleDTO(1L, "REGULAR", Instant.now(), null, null,
                null, "Glass", 100.0, 2.5, "Buyer A");

        assertNull(dto.getExpectedSaleDate());
        assertEquals("PENDING", dto.getStatus());
    }

    @Test
    void nullBuyerName_ShouldNotThrow() {
        SaleDTO dto = new SaleDTO(1L, "REGULAR", Instant.now(), null, null,
                Instant.now(), "Glass", 100.0, 2.5, null);

        assertNull(dto.getBuyerName());
    }

    @Test
    void emptyMaterialName_ShouldBeAllowed() {
        SaleDTO dto = new SaleDTO(1L, "REGULAR", Instant.now(), null, null,
                null, "", 100.0, 2.5, "Buyer A");

        assertEquals("", dto.getMaterialName());
    }

    @Test
    void negativeWeight_ShouldGiveNegativeRevenue() {
        SaleDTO dto = new SaleDTO(1L, "REGULAR", Instant.now(), null, null,
                null, "Glass", -50.0, 10.0, "Buyer A");

        assertEquals(-500.0, dto.getTotalRevenue());
        assertEquals(-50.0, dto.getWeight());
    }

    @Test
    void collectiveConstructor_NullCooperativeCount() {
        SaleDTO dto = new SaleDTO(1L, "COLLECTIVE", Instant.now(), null, null,
                "Glass", 100.0, 2.5, "Buyer B", null);

        assertNull(dto.getCooperativeCount());
    }

}