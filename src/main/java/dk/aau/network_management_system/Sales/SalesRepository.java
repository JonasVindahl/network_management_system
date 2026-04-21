package dk.aau.network_management_system.Sales;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dk.aau.network_management_system.Cooperative_Analytics.CooperativeEntity;

@Repository
public interface SalesRepository extends JpaRepository<CooperativeEntity, Long> {

    @Query(value = """
        SELECT 
            s.sale_id,
            'REGULAR' as sale_type,
            s.created_at,
            s.sold_at,
            s.cancelled_at,
            s.expected_sale_date,
            m.material_name,
            s.weight,
            s.price_kg,
            b.buyer_name
        FROM sales s
        JOIN materials m ON s.material = m.material_id
        JOIN buyers b ON s.buyer = b.buyer_id
        WHERE s.cooperative_id = :cooperativeId
          AND s.sold_at IS NOT NULL
          AND s.sold_at >= :startDate
          AND s.sold_at <= :endDate
        ORDER BY s.sold_at DESC
        """, nativeQuery = true)
    List<Object[]> findRegularSalesHistory(
            @Param("cooperativeId") Long cooperativeId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query(value = """
        SELECT 
            cs.collective_sale_id,
            'COLLECTIVE' as sale_type,
            cs.created_at,
            cs.sold_at,
            cs.expected_sale_date,
            m.material_name,
            COALESCE(csc.contributed_weight, 0),
            cs.price_kg,
            b.buyer_name,
            (SELECT COUNT(*) FROM collective_sale_contribution
             WHERE collective_sale_id = cs.collective_sale_id
               AND status = 'ACCEPTED') as coop_count
        FROM collective_sale cs
        JOIN collective_sale_contribution csc 
          ON cs.collective_sale_id = csc.collective_sale_id
        JOIN materials m ON cs.material_id = m.material_id
        JOIN buyers b ON cs.buyer_id = b.buyer_id
        WHERE csc.cooperative_id = :cooperativeId
          AND csc.status = 'ACCEPTED'
          AND cs.sold_at IS NOT NULL
          AND cs.sold_at >= :startDate
          AND cs.sold_at <= :endDate
        ORDER BY cs.sold_at DESC
        """, nativeQuery = true)
    List<Object[]> findCollectiveSalesHistory(
            @Param("cooperativeId") Long cooperativeId,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate
    );

    @Query(value = """
        SELECT 
            s.sale_id,
            'REGULAR' as sale_type,
            s.created_at,
            s.sold_at,
            s.cancelled_at,
            s.expected_sale_date,
            m.material_name,
            s.weight,
            s.price_kg,
            b.buyer_name
        FROM sales s
        JOIN materials m ON s.material = m.material_id
        JOIN buyers b ON s.buyer = b.buyer_id
        WHERE s.cooperative_id = :cooperativeId
          AND s.sold_at IS NULL
          AND s.cancelled_at IS NULL
        ORDER BY s.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findActiveRegularSales(@Param("cooperativeId") Long cooperativeId);

    @Query(value = """
        SELECT 
            cs.collective_sale_id,
            'COLLECTIVE' as sale_type,
            cs.created_at,
            cs.sold_at,
            cs.expected_sale_date,
            m.material_name,
            COALESCE(csc.contributed_weight, 0),
            cs.price_kg,
            b.buyer_name,
            (SELECT COUNT(*) FROM collective_sale_contribution
             WHERE collective_sale_id = cs.collective_sale_id
               AND status = 'ACCEPTED') as coop_count
        FROM collective_sale cs
        JOIN collective_sale_contribution csc 
          ON cs.collective_sale_id = csc.collective_sale_id
        JOIN materials m ON cs.material_id = m.material_id
        JOIN buyers b ON cs.buyer_id = b.buyer_id
        WHERE csc.cooperative_id = :cooperativeId
          AND csc.status = 'ACCEPTED'
          AND cs.sold_at IS NULL
        ORDER BY cs.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findActiveCollectiveSales(@Param("cooperativeId") Long cooperativeId);

    @Modifying
    @Query(value = """
        INSERT INTO sales
            (material, weight, price_kg, buyer, responsible, cooperative_id, expected_sale_date, created_at)
        VALUES
            (:materialId, :weight, :priceKg, :buyerId, :responsible, :cooperativeId, :expectedSaleDate, now())
        """, nativeQuery = true)
    void insertSale(
            @Param("materialId") Long materialId,
            @Param("weight") Double weight,
            @Param("priceKg") Double priceKg,
            @Param("buyerId") Long buyerId,
            @Param("responsible") Long responsible,
            @Param("cooperativeId") Long cooperativeId,
            @Param("expectedSaleDate") Instant expectedSaleDate
    );

    @Modifying
    @Query(value = """
        UPDATE sales SET
            material           = COALESCE(:materialId, material),
            weight             = COALESCE(:weight, weight),
            price_kg           = COALESCE(:priceKg, price_kg),
            buyer              = COALESCE(:buyerId, buyer),
            expected_sale_date = COALESCE(:expectedSaleDate, expected_sale_date)
        WHERE sale_id = :saleId
          AND cooperative_id = :cooperativeId
          AND sold_at IS NULL
          AND cancelled_at IS NULL
        """, nativeQuery = true)
    int updateSale(
            @Param("saleId") Long saleId,
            @Param("cooperativeId") Long cooperativeId,
            @Param("materialId") Long materialId,
            @Param("weight") Double weight,
            @Param("priceKg") Double priceKg,
            @Param("buyerId") Long buyerId,
            @Param("expectedSaleDate") Instant expectedSaleDate
    );

    @Modifying
    @Query(value = """
        UPDATE sales
        SET sold_at = now()
        WHERE sale_id = :saleId
          AND cooperative_id = :cooperativeId
          AND sold_at IS NULL
          AND cancelled_at IS NULL
        """, nativeQuery = true)
    int completeSale(
            @Param("saleId") Long saleId,
            @Param("cooperativeId") Long cooperativeId
    );

    @Modifying
    @Query(value = """
        UPDATE sales
        SET cancelled_at = now()
        WHERE sale_id = :saleId
          AND cooperative_id = :cooperativeId
          AND sold_at IS NULL
          AND cancelled_at IS NULL
        """, nativeQuery = true)
    int cancelSale(
            @Param("saleId") Long saleId,
            @Param("cooperativeId") Long cooperativeId
    );

    @Query(value = """
        SELECT material, weight
        FROM sales
        WHERE sale_id = :saleId
          AND cooperative_id = :cooperativeId
        """, nativeQuery = true)
    List<Object[]> findMaterialAndWeightBySaleId(
            @Param("saleId") Long saleId,
            @Param("cooperativeId") Long cooperativeId
    );

    @Query(value = """
    SELECT
        s.sale_id,
        'REGULAR' as sale_type,
        s.created_at,
        s.sold_at,
        s.cancelled_at,
        s.expected_sale_date,
        m.material_name,
        s.weight,
        s.price_kg,
        b.buyer_name
    FROM sales s
    JOIN materials m ON m.material_id = s.material
    JOIN buyers   b ON b.buyer_id     = s.buyer
    WHERE s.cooperative_id = :cooperativeId
      AND s.sold_at     IS NULL
      AND s.cancelled_at IS NULL
    ORDER BY s.created_at DESC
    """, nativeQuery = true)
    List<Object[]> findActiveSalesByCooperative(@Param("cooperativeId") Long cooperativeId);

    @Query(value = """
    SELECT
        s.sale_id,
        'REGULAR' as sale_type,
        s.created_at,
        s.sold_at,
        s.cancelled_at,
        s.expected_sale_date,
        m.material_name,
        s.weight,
        s.price_kg,
        b.buyer_name
    FROM sales s
    JOIN materials m ON m.material_id = s.material
    JOIN buyers   b ON b.buyer_id     = s.buyer
    WHERE s.cooperative_id = :cooperativeId
      AND (s.sold_at IS NOT NULL OR s.cancelled_at IS NOT NULL)
    ORDER BY COALESCE(s.sold_at, s.cancelled_at) DESC
    """, nativeQuery = true)
    List<Object[]> findSalesHistoryByCooperative(@Param("cooperativeId") Long cooperativeId);

}