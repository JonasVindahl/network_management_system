package dk.aau.network_management_system.Collective_Sale;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CollectiveSaleRepository extends JpaRepository<CollectiveSaleEntity, Long> {

    @Query(value = """
        SELECT creator_cooperative_id
        FROM collective_sale
        WHERE collective_sale_id = :saleId
          AND sold_at IS NULL
        """, nativeQuery = true)
    Optional<Long> findActiveSaleCreator(@Param("saleId") Long saleId);

    @Query(value = """
        SELECT status
        FROM collective_sale_contribution
        WHERE collective_sale_id = :saleId
          AND cooperative_id = :cooperativeId
        """, nativeQuery = true)
    Optional<String> findContributionStatus(
        @Param("saleId") Long saleId,
        @Param("cooperativeId") Long cooperativeId
    );

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO collective_sale_contribution
            (collective_sale_id, cooperative_id, contributed_weight, status)
        VALUES (:saleId, :cooperativeId, NULL, :status)
        """, nativeQuery = true)
    void addContribution(
        @Param("saleId") Long saleId,
        @Param("cooperativeId") Long cooperativeId,
        @Param("status") String status
    );

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE collective_sale_contribution
        SET status = :newStatus
        WHERE collective_sale_id = :saleId
          AND cooperative_id = :cooperativeId
        """, nativeQuery = true)
    int updateContributionStatus(
        @Param("saleId") Long saleId,
        @Param("cooperativeId") Long cooperativeId,
        @Param("newStatus") String newStatus
    );

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE collective_sale_contribution
        SET contributed_weight = :weight
        WHERE collective_sale_id = :saleId
          AND cooperative_id = :cooperativeId
          AND status = 'ACCEPTED'
        """, nativeQuery = true)
    int updateContributionWeight(
        @Param("saleId") Long saleId,
        @Param("cooperativeId") Long cooperativeId,
        @Param("weight") BigDecimal weight
    );

    @Query(value = """
        SELECT
            cs.collective_sale_id,
            m.material_name,
            b.buyer_name,
            cs.price_kg,
            cs.expected_sale_date,
            cs.created_at,
            cs.creator_cooperative_id
        FROM collective_sale cs
        JOIN collective_sale_contribution csc
          ON cs.collective_sale_id = csc.collective_sale_id
        JOIN materials m ON cs.material_id = m.material_id
        JOIN buyers b ON cs.buyer_id = b.buyer_id
        WHERE csc.cooperative_id = :cooperativeId
          AND csc.status = 'INVITED'
          AND cs.sold_at IS NULL
        ORDER BY cs.created_at DESC
        """, nativeQuery = true)
    List<Object[]> findPendingInvitations(@Param("cooperativeId") Long cooperativeId);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE collective_sale
        SET material_id = :materialId
        WHERE collective_sale_id = :saleId
          AND sold_at IS NULL
        """, nativeQuery = true)
    int updateSaleMaterial(
        @Param("saleId") Long saleId,
        @Param("materialId") Long materialId
    );
}
