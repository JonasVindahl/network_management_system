package dk.aau.network_management_system.Cooperative_Analytics;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository<CooperativeEntity, Long> {

    @Query(value = """
        SELECT 
            s.sale_id,
            'REGULAR' as sale_type,
            s.created_at,
            s.sold_at,
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
            csc.contributed_weight,
            cs.price_kg,
            b.buyer_name,
            (SELECT COUNT(*) FROM collective_sale_contribution 
             WHERE collective_sale_id = cs.collective_sale_id) as coop_count
        FROM collective_sale cs
        JOIN collective_sale_contribution csc 
          ON cs.collective_sale_id = csc.collective_sale_id
        JOIN materials m ON cs.material_id = m.material_id
        JOIN buyers b ON cs.buyer_id = b.buyer_id
        WHERE csc.cooperative_id = :cooperativeId
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
            csc.contributed_weight,
            cs.price_kg,
            b.buyer_name,
            (SELECT COUNT(*) FROM collective_sale_contribution 
             WHERE collective_sale_id = cs.collective_sale_id) as coop_count
        FROM collective_sale cs
        JOIN collective_sale_contribution csc 
          ON cs.collective_sale_id = csc.collective_sale_id
        JOIN materials m ON cs.material_id = m.material_id
        JOIN buyers b ON cs.buyer_id = b.buyer_id
        WHERE csc.cooperative_id = :cooperativeId
          AND cs.sold_at IS NULL
        ORDER BY cs.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findActiveCollectiveSales(@Param("cooperativeId") Long cooperativeId);
}