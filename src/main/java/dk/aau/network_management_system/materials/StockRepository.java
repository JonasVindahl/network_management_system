package dk.aau.network_management_system.materials;

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

    @Modifying
    @Query(value = """
    UPDATE public.stock
    SET
        total_sold_kg    = total_sold_kg    + :amount,
        current_stock_kg = current_stock_kg - :amount
    WHERE cooperative = :cooperativeId
      AND material    = :materialId
    """, nativeQuery = true)
    int recordSale(
            @Param("cooperativeId") long cooperativeId,
            @Param("materialId") long materialId,
            @Param("amount") double amount
    );
}