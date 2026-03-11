package dk.aau.network_management_system.Cooperative_Analytics;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesRepository extends JpaRepository<CooperativeEntity, Long> {
    
   // normale salg historik
    @Query(value = """
        SELECT 
            s.sale_id,
            'REGULAR' as sale_type,
            s.date as sale_date,
            m.material_name,
            s.weight,
            s.price_kg,
            b.buyer_name,
            s.status
        FROM sales s
        JOIN materials m ON s.material = m.material_id
        JOIN buyers b ON s.buyer = b.buyer_id
        WHERE s.cooperative_id = :cooperativeId
          AND s.status = 'COMPLETED'
          AND s.date >= :startDate
          AND s.date <= :endDate
        ORDER BY s.date DESC
        """, nativeQuery = true)
    List<Object[]> findRegularSalesHistory(
        @Param("cooperativeId") Long cooperativeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // collectiv salg historik
    @Query(value = """
        SELECT 
            cs.collective_sale_id,
            'COLLECTIVE' as sale_type,
            CAST(cs.sold_at AS DATE) as sale_date,
            m.material_name,
            csc.contributed_weight,
            cs.price_kg,
            b.buyer_name,
            cs.status,
            (SELECT COUNT(*) FROM collective_sale_contribution 
             WHERE collective_sale_id = cs.collective_sale_id) as coop_count
        FROM collective_sale cs
        JOIN collective_sale_contribution csc ON cs.collective_sale_id = csc.collective_sale_id
        JOIN materials m ON cs.material_id = m.material_id
        JOIN buyers b ON cs.buyer_id = b.buyer_id
        WHERE csc.cooperative_id = :cooperativeId
          AND cs.status = 'SOLD'
          AND CAST(cs.sold_at AS DATE) >= :startDate
          AND CAST(cs.sold_at AS DATE) <= :endDate
        ORDER BY cs.sold_at DESC
        """, nativeQuery = true)
    List<Object[]> findCollectiveSalesHistory(
        @Param("cooperativeId") Long cooperativeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // aktive salg 
    @Query(value = """
        SELECT 
            s.sale_id,
            'REGULAR' as sale_type,
            s.date as sale_date,
            m.material_name,
            s.weight,
            s.price_kg,
            b.buyer_name,
            s.status
        FROM sales s
        JOIN materials m ON s.material = m.material_id
        JOIN buyers b ON s.buyer = b.buyer_id
        WHERE s.cooperative_id = :cooperativeId
          AND s.status = 'PENDING'
        ORDER BY s.sale_id DESC
        """, nativeQuery = true)
    List<Object[]> findActiveRegularSales(@Param("cooperativeId") Long cooperativeId);
    
    //aktive colletiv salg
    @Query(value = """
        SELECT 
            cs.collective_sale_id,
            'COLLECTIVE' as sale_type,
            CAST(cs.created_at AS DATE) as created_date,
            m.material_name,
            csc.contributed_weight,
            cs.price_kg,
            b.buyer_name,
            cs.status,
            (SELECT COUNT(*) FROM collective_sale_contribution 
             WHERE collective_sale_id = cs.collective_sale_id) as coop_count
        FROM collective_sale cs
        JOIN collective_sale_contribution csc ON cs.collective_sale_id = csc.collective_sale_id
        JOIN materials m ON cs.material_id = m.material_id
        JOIN buyers b ON cs.buyer_id = b.buyer_id
        WHERE csc.cooperative_id = :cooperativeId
          AND cs.status = 'ACTIVE'
        ORDER BY cs.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findActiveCollectiveSales(@Param("cooperativeId") Long cooperativeId);
}