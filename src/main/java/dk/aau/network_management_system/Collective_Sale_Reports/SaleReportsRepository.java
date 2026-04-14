package dk.aau.network_management_system.Collective_Sale_Reports;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dk.aau.network_management_system.Cooperative_Analytics.CooperativeEntity;

@Repository
public interface SaleReportsRepository extends JpaRepository<CooperativeEntity, Long> {

    @Query(value = """
        SELECT
            s.sale_id,
            s.created_at,
            s.sold_at,
            s.cancelled_at,
            s.expected_sale_date,
            s.weight,
            s.price_kg,
            s.weight * s.price_kg AS total_revenue,
            m.material_id,
            m.material_name,
            b.buyer_id,
            b.buyer_name,
            w.worker_id,
            w.worker_name,
            c.cooperative_id,
            c.cooperative_name
        FROM sales s
        JOIN materials m ON s.material = m.material_id
        JOIN buyers b ON s.buyer = b.buyer_id
        JOIN workers w ON s.responsible = w.worker_id
        JOIN cooperative c ON s.cooperative_id = c.cooperative_id
        WHERE s.sale_id = :saleId
        """, nativeQuery = true)
    List<Object[]> findSaleReport(@Param("saleId") Long saleId);

    @Query(value = """
        SELECT COUNT(*) > 0
        FROM sales
        WHERE sale_id = :saleId
          AND cooperative_id = :cooperativeId
        """, nativeQuery = true)
    boolean isSaleOwnedByCooperative(
        @Param("saleId") Long saleId,
        @Param("cooperativeId") Long cooperativeId
    );

    @Query(value = """
        SELECT COUNT(*) > 0
        FROM sales
        WHERE sale_id = :saleId
        """, nativeQuery = true)
    boolean saleExists(@Param("saleId") Long saleId);
}