package dk.aau.network_management_system.materials;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Modifying
    @Query(value = """
        UPDATE public.stock
        SET
            total_collected_kg = total_collected_kg + :amount,
            current_stock_kg   = current_stock_kg   + :amount
        WHERE
            cooperative = :cooperativeId
            AND material = :materialId
        """, nativeQuery = true)
    int addToStock(
        @Param("cooperativeId") long cooperativeId,
        @Param("materialId")    long materialId,
        @Param("amount")        double amount
    );

    // Atomic reservation / release. Subtracts :delta from current_stock_kg.
    // If :delta > 0, only applies when current_stock_kg >= :delta (else 0 rows).
    // If :delta <= 0, always applies (returns stock).
    @Modifying
    @Query(value = """
        UPDATE public.stock
        SET current_stock_kg = current_stock_kg - :delta
        WHERE cooperative = :cooperativeId
          AND material = :materialId
          AND (:delta <= 0 OR current_stock_kg >= :delta)
        """, nativeQuery = true)
    int adjustStock(
        @Param("cooperativeId") Long cooperativeId,
        @Param("materialId")    Long materialId,
        @Param("delta")         BigDecimal delta
    );

    @Query(value = """
        SELECT current_stock_kg
        FROM public.stock
        WHERE cooperative = :cooperativeId
          AND material = :materialId
        """, nativeQuery = true)
    Optional<BigDecimal> findCurrentStock(
        @Param("cooperativeId") Long cooperativeId,
        @Param("materialId")    Long materialId
    );
}