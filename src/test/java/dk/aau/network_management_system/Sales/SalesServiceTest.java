package dk.aau.network_management_system.Sales;

import dk.aau.network_management_system.auth.AuthenticatedUser;
import dk.aau.network_management_system.materials.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesServiceTest {

    @Mock
    private SalesRepository repository;

    @Mock
    private AuthenticatedUser authenticatedUser;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private SalesService salesService;

    @Test
    void getNormalSales_Active_ShouldMapRegularSales() {
        when(authenticatedUser.isAdmin()).thenReturn(true);

        Instant now = Instant.now();
        Object[] row = new Object[]{
                1L, "REGULAR",
                Timestamp.from(now), null, null,
                Timestamp.from(now.plusSeconds(86400)),
                "Glass", 100.0, 2.5, "Buyer A"
        };

        List<Object[]> rows = new ArrayList<>();
        rows.add(row);
        when(repository.findActiveSalesByCooperative(1L)).thenReturn(rows);

        List<SaleDTO> result = salesService.getNormalSales(1L, "ACTIVE");

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getSaleId());
        assertEquals("REGULAR", result.get(0).getSaleType());
        assertEquals("Glass", result.get(0).getMaterialName());
        assertEquals(100.0, result.get(0).getWeight());
        assertEquals(2.5, result.get(0).getPricePerKg());
        assertEquals(250.0, result.get(0).getTotalRevenue());
        assertEquals("Buyer A", result.get(0).getBuyerName());
        assertEquals("PENDING", result.get(0).getStatus());
    }

    @Test
    void getNormalSales_History_ShouldMapCompletedSales() {
        when(authenticatedUser.isAdmin()).thenReturn(true);

        Instant now = Instant.now();
        Object[] row = new Object[]{
                2L, "REGULAR",
                Timestamp.from(now), Timestamp.from(now), null,
                Timestamp.from(now.plusSeconds(86400)),
                "Metal", 50.0, 10.0, "Buyer B"
        };

        List<Object[]> historyRows = new ArrayList<>();
        historyRows.add(row);
        when(repository.findSalesHistoryByCooperative(1L)).thenReturn(historyRows);

        List<SaleDTO> result = salesService.getNormalSales(1L, "HISTORY");

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getSaleId());
        assertEquals("COMPLETED", result.get(0).getStatus());
        assertEquals(500.0, result.get(0).getTotalRevenue());
    }

    @Test
    void getNormalSales_Active_SortsByCreatedAtDescending() {
        when(authenticatedUser.isAdmin()).thenReturn(true);

        Instant now = Instant.now();
        Object[] row1 = new Object[]{
                1L, "REGULAR",
                Timestamp.from(now.minusSeconds(100)), null, null,
                null, "Glass", 100.0, 2.5, "Buyer A"
        };
        Object[] row2 = new Object[]{
                2L, "REGULAR",
                Timestamp.from(now), null, null,
                null, "Metal", 50.0, 10.0, "Buyer B"
        };

        List<Object[]> activeRows = new ArrayList<>();
        activeRows.add(row1);
        activeRows.add(row2);
        when(repository.findActiveSalesByCooperative(1L)).thenReturn(activeRows);

        List<SaleDTO> result = salesService.getNormalSales(1L, "ACTIVE");

        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getSaleId());
        assertEquals(1L, result.get(1).getSaleId());
    }

    @Test
    void getNormalSales_Active_WithNullCreatedAt() {
        when(authenticatedUser.isAdmin()).thenReturn(true);

        Object[] rowWithNull = new Object[]{
                1L, "REGULAR",
                null, null, null,
                null, "Glass", 100.0, 2.5, "Buyer A"
        };
        Object[] rowWithDate = new Object[]{
                2L, "REGULAR",
                Timestamp.from(Instant.now()), null, null,
                null, "Metal", 50.0, 10.0, "Buyer B"
        };

        List<Object[]> mixedRows = new ArrayList<>();
        mixedRows.add(rowWithNull);
        mixedRows.add(rowWithDate);
        when(repository.findActiveSalesByCooperative(1L)).thenReturn(mixedRows);

        List<SaleDTO> result = salesService.getNormalSales(1L, "ACTIVE");

        assertEquals(2, result.size());
        assertNull(result.get(1).getCreatedAt());
    }

    @Test
    void getNormalSales_EmptyList_ShouldReturnEmpty() {
        when(authenticatedUser.isAdmin()).thenReturn(true);

        when(repository.findActiveSalesByCooperative(1L)).thenReturn(new ArrayList<>());

        List<SaleDTO> result = salesService.getNormalSales(1L, "ACTIVE");

        assertTrue(result.isEmpty());
    }

    @Test
    void getNormalSales_BothNullCreatedAt_ShouldNotThrow() {
        when(authenticatedUser.isAdmin()).thenReturn(true);

        Object[] row1 = new Object[]{
                1L, "REGULAR", null, null, null,
                null, "Glass", 100.0, 2.5, "Buyer A"
        };
        Object[] row2 = new Object[]{
                2L, "REGULAR", null, null, null,
                null, "Metal", 50.0, 10.0, "Buyer B"
        };

        List<Object[]> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        when(repository.findActiveSalesByCooperative(1L)).thenReturn(rows);

        List<SaleDTO> result = salesService.getNormalSales(1L, "ACTIVE");

        assertEquals(2, result.size());
    }

    @Test
    void getNormalSales_LargeValues_ShouldNotOverflow() {
        when(authenticatedUser.isAdmin()).thenReturn(true);

        Object[] row = new Object[]{
                1L, "REGULAR",
                Timestamp.from(Instant.now()), null, null,
                null, "Sand", 999_999_999.0, 999_999.99, "Buyer Big"
        };

        List<Object[]> rows = new ArrayList<>();
        rows.add(row);
        when(repository.findActiveSalesByCooperative(1L)).thenReturn(rows);

        List<SaleDTO> result = salesService.getNormalSales(1L, "ACTIVE");

        assertEquals(999_999_999.0, result.get(0).getWeight());
        assertEquals(999_999.99, result.get(0).getPricePerKg());
        assertTrue(result.get(0).getTotalRevenue() > 0);
    }

}