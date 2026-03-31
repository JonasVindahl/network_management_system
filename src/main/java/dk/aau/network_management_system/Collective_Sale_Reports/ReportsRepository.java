package dk.aau.network_management_system.Collective_Sale_Reports;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dk.aau.network_management_system.Cooperative_Analytics.CooperativeEntity;


@Repository
public interface ReportsRepository extends JpaRepository<CooperativeEntity, Long> {


    // Salg info + cooperatives bidragelse
    @Query(value = """
        SELECT 
            cs.collective_sale_id,
            cs.material_id,
            m.material_name,
            cs.buyer_id,
            b.buyer_name,
            cs.created_at,
            cs.sold_at,
            cs.expected_sale_date,
            cs.total_weight,
            cs.price_kg,
            csc.cooperative_id,
            c.cooperative_name,
            csc.contributed_weight,
            csc.revenue_share
        FROM collective_sale cs
        JOIN materials m ON cs.material_id = m.material_id
        JOIN buyers b ON cs.buyer_id = b.buyer_id
        JOIN collective_sale_contribution csc 
            ON cs.collective_sale_id = csc.collective_sale_id
        JOIN cooperative c ON csc.cooperative_id = c.cooperative_id
        WHERE cs.collective_sale_id = :saleId
        ORDER BY csc.contributed_weight DESC
        """, nativeQuery = true)
    List<Object[]> findCollectiveSaleReport(@Param("saleId") Long saleId);


    //tjekker om coop er i collectiv salg (Bruges til permissions)
    @Query(value = """
        SELECT COUNT(*) > 0
        FROM collective_sale_contribution
        WHERE collective_sale_id = :saleId
          AND cooperative_id = :cooperativeId
        """, nativeQuery = true)
    boolean isCooperativeParticipant(
        @Param("saleId") Long saleId,
        @Param("cooperativeId") Long cooperativeId
    );


    // Tjekker om salget eksistere
    @Query(value = """
        SELECT COUNT(*) > 0
        FROM collective_sale
        WHERE collective_sale_id = :saleId
        """, nativeQuery = true)
    boolean collectiveSaleExists(@Param("saleId") Long saleId);

}

