package dk.aau.network_management_system.Cooperative_Analytics;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsRepository extends JpaRepository<CooperativeEntity, Long> {

    @Query(value = """
        SELECT 
            COALESCE(SUM(s.total_collected_kg), 0) as total_collected,
            COALESCE(SUM(s.total_sold_kg), 0) as total_sold,
            COALESCE(SUM(s.current_stock_kg), 0) as current_stock,
            (SELECT COUNT(*) FROM workers WHERE cooperative = :cooperativeId AND exit_date IS NULL) as active_workers
        FROM stock s
        WHERE s.cooperative = :cooperativeId
        """, nativeQuery = true)
    List<Object[]> findCooperativePerformanceRaw(
        @Param("cooperativeId") Long cooperativeId);


    @Query(value = """
        SELECT 
            w.worker_id,
            w.worker_name,
            COALESCE(SUM(m.weight_kg), 0) as total_collected_kg,
            COUNT(m.weighting_id) as number_of_weighings,
            COALESCE(AVG(m.weight_kg), 0) as avg_weight_per_weighing
        FROM workers w
        LEFT JOIN measurements m ON w.worker_id = m.wastepicker
        WHERE w.cooperative = :cooperativeId
          AND user_type = 'W'
          AND (m.time_stamp IS NULL OR m.time_stamp >= :startDate)
          AND (m.time_stamp IS NULL OR m.time_stamp <= :endDate)
        GROUP BY w.worker_id, w.worker_name
        ORDER BY total_collected_kg DESC
        """, nativeQuery = true)
    List<Object[]> findAllWorkerProductivityRaw(
        @Param("cooperativeId") Long cooperativeId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query(value = """
        SELECT 
            w.worker_id,
            w.worker_name,
            COALESCE(SUM(m.weight_kg), 0) as total_collected_kg,
            COUNT(m.weighting_id) as number_of_weighings,
            COALESCE(AVG(m.weight_kg), 0) as avg_weight_per_weighing
        FROM workers w
        LEFT JOIN measurements m ON w.worker_id = m.wastepicker
        WHERE w.cooperative = :cooperativeId
          AND w.worker_id = :workerid
          AND user_type = 'W'
          AND (m.time_stamp IS NULL OR m.time_stamp >= :startDate)
          AND (m.time_stamp IS NULL OR m.time_stamp <= :endDate)
        GROUP BY w.worker_id, w.worker_name
        ORDER BY total_collected_kg DESC
        """, nativeQuery = true)
    List<Object[]> findWorkerProductivityRaw(
        @Param("cooperativeId") Long cooperativeId,
        @Param("workerid") Long workerId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );


    @Query(value = """
        SELECT 
            mat.material_name,
            s.total_collected_kg,
            s.total_sold_kg,
            s.current_stock_kg,
            mat.material_id
        FROM stock s
        JOIN materials mat ON s.material = mat.material_id
        WHERE s.cooperative = :cooperativeId
        ORDER BY s.current_stock_kg DESC
        """, nativeQuery = true)
    List<Object[]> getStockByMaterial(
        @Param("cooperativeId") Long cooperativeId
    );


    @Query(value = """
        SELECT 
            COALESCE(SUM(sa.weight * sa.price_kg), 0) as totalRevenue,
            COUNT(sa.sale_id) as totalSales,
            COALESCE(AVG(sa.price_kg), 0) as avgPricePerKg
        FROM sales sa
        JOIN workers w ON sa.responsible = w.worker_id
        WHERE w.cooperative = :cooperativeId
          AND sa.sold_at BETWEEN :startDate AND :endDate
        """, nativeQuery = true)
    List<Object[]> findRevenueRaw(
        @Param("cooperativeId") Long cooperativeId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    //sidste 5 salg (ny)
    @Query(value = """
        SELECT
            s.material,
            s.weight,
            s.price_kg,
            s.sold_at
        FROM sales s
        JOIN workers w ON s.responsible = w.worker_id
        WHERE s.material = :materialId
        AND w.cooperative = :cooperativeId
        AND s.sold_at IS NOT NULL
        ORDER BY s.sold_at DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> LastSalesCooperativeRaw(
        @Param("cooperativeId") Long cooperativeId,
        @Param("materialId") Long materialId
    );

}