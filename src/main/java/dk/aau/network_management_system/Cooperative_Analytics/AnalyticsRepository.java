package dk.aau.network_management_system.Cooperative_Analytics;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsRepository extends JpaRepository<CooperativeEntity, Long> {
    

    //Cooperatives overview info
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


    //All Worker productivity
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

    //Specific worker productivity
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

    
    //Stock per material/ sold pr material 
    @Query(value = """
        SELECT 
            mat.material_name as materialName,
            s.total_collected_kg as totalCollected,
            s.total_sold_kg as totalSold,
            s.current_stock_kg as currentStock
        FROM stock s
        JOIN materials mat ON s.material = mat.material_id
        WHERE s.cooperative = :cooperativeId
        ORDER BY s.current_stock_kg DESC
        """, nativeQuery = true)
    List<Object[]> getStockByMaterial(
        @Param("cooperativeId") Long cooperativeId
    );
 

    //revenue for cooperative
    @Query(value = """
    SELECT 
        COALESCE(SUM(sa.weight * sa.price_kg), 0) as totalRevenue,
        COUNT(sa.sale_id) as totalSales,
        COALESCE(AVG(sa.price_kg), 0) as avgPricePerKg
    FROM sales sa
    JOIN workers w ON sa.responsible = w.worker_id
    WHERE w.cooperative = :cooperativeId
      AND sa.date >= CAST(:startDate AS date)
      AND sa.date <= CAST(:endDate AS date)
    """, nativeQuery = true)
    List<Object[]> findRevenueRaw(
        @Param("cooperativeId") Long cooperativeId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

}