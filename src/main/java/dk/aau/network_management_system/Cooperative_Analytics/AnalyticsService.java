package dk.aau.network_management_system.Cooperative_Analytics;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;

@Service
public class AnalyticsService {

    private final AnalyticsRepository repository;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public AnalyticsService(AnalyticsRepository repository, AuthenticatedUser authenticatedUser) {
        this.repository = repository;
        this.authenticatedUser = authenticatedUser;
    }

   public List<CooperativePerformanceDTO> getCooperativePerformance(
        Long cooperativeId, LocalDateTime startDate, LocalDateTime endDate) {

    if (authenticatedUser.isWorker()) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "Workers cannot access cooperative performance data");
    }

    if (!authenticatedUser.isAdmin() &&
        !cooperativeId.equals(authenticatedUser.getCooperativeId())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "You can only access your own cooperative's data");
    }

    List<Object[]> results = repository.findCooperativePerformanceRaw(cooperativeId);

    return results.stream()
        .map(row -> new CooperativePerformanceDTO(
            ((Number) row[0]).doubleValue(),
            ((Number) row[1]).doubleValue(),
            ((Number) row[2]).doubleValue(),
            ((Number) row[3]).intValue()
        ))
        .collect(Collectors.toList());
}

    public List<WorkerProductivityDTO> getAllWorkerProductivity(
            Long cooperativeId, LocalDateTime startDate, LocalDateTime endDate) {

        if (authenticatedUser.isWorker()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Workers can only view their own productivity");
        }

        if (!authenticatedUser.isAdmin() &&
            !cooperativeId.equals(authenticatedUser.getCooperativeId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You can only access your own cooperative's data");
        }

        List<Object[]> raw = repository.findAllWorkerProductivityRaw(
            cooperativeId, startDate, endDate
        );

        return raw.stream()
            .map(row -> new WorkerProductivityDTO(
                ((Number) row[0]).longValue(),    // worker_id
                (String) row[1],                  // worker_name
                ((Number) row[2]).doubleValue(),  // total_collected_kg
                ((Number) row[3]).longValue(),    // number_of_weighings
                ((Number) row[4]).doubleValue()   // avg_weight_per_weighing
            ))
            .collect(Collectors.toList());
    }

    public List<WorkerProductivityDTO> getWorkerProductivity(
            Long cooperativeId, Long workerId, LocalDateTime startDate, LocalDateTime endDate) {

        List<Object[]> raw = repository.findWorkerProductivityRaw(
            cooperativeId, workerId, startDate, endDate
        );

        return raw.stream()
            .map(row -> new WorkerProductivityDTO(
                ((Number) row[0]).longValue(),    // worker_id
                (String) row[1],                  // worker_name
                ((Number) row[2]).doubleValue(),  // total_collected_kg
                ((Number) row[3]).longValue(),    // number_of_weighings
                ((Number) row[4]).doubleValue()   // avg_weight_per_weighing
            ))
            .collect(Collectors.toList());
    }

    // tilføjet row[4] for materialId - skal bruges til frontend
    public List<StockByMaterialDTO> getStockByMaterial(Long cooperativeId) {

        if (authenticatedUser.isWorker()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Workers cannot access stock data");
        }

        if (!authenticatedUser.isAdmin() &&
            !cooperativeId.equals(authenticatedUser.getCooperativeId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You can only access your own cooperative's data");
        }

        List<Object[]> stock = repository.getStockByMaterial(cooperativeId);

        return stock.stream()
            .map(row -> new StockByMaterialDTO(
                (String) row[0],                  // material_name
                ((Number) row[1]).doubleValue(),  // total_collected_kg
                ((Number) row[2]).doubleValue(),  // total_sold_kg
                ((Number) row[3]).doubleValue(),  // current_stock_kg
                ((Number) row[4]).longValue()     // material_id
            ))
            .collect(Collectors.toList());
    }


    public List<RevenueDTO> getRevenue(
            Long cooperativeId, Long materialId, LocalDateTime startDate, LocalDateTime endDate) {

        if (authenticatedUser.isWorker()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Workers cannot access revenue data");
        }

        if (!authenticatedUser.isAdmin() &&
            !cooperativeId.equals(authenticatedUser.getCooperativeId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You can only access your own cooperative's data");
        }

        List<Object[]> revenue = repository.findRevenueRaw(cooperativeId, startDate, endDate, materialId);

        return revenue.stream()
            .map(row -> new RevenueDTO(
            ((Number) row[0]).doubleValue(),  // totalRevenue
            ((Number) row[1]).longValue(),    // totalSales
            ((Number) row[2]).doubleValue(),  // avgPricePerKg
            (String)  row[3],                 // materialName
            ((Number) row[4]).longValue()     // materialId
        ))
            .collect(Collectors.toList());
    }

    public List<Last5SalesDTO> findLastSalesForCooperative(
            Long cooperativeId, Long materialId) {

        if (authenticatedUser.isWorker()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Workers cannot access sales data");
        }

        if (!authenticatedUser.isAdmin() &&
            !cooperativeId.equals(authenticatedUser.getCooperativeId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You can only access your own cooperative's data");
        }

        List<Object[]> raw = repository.LastSalesCooperativeRaw(cooperativeId, materialId);

        return raw.stream()
            .map(row -> new Last5SalesDTO(
                ((Number) row[0]).longValue(),    // material
                ((Number) row[1]).doubleValue(),  // weight
                ((Number) row[2]).doubleValue(),  // price_kg
                // sold_at til Timestamp
                row[3] instanceof java.sql.Timestamp ts
                    ? ts.toLocalDateTime().toLocalDate()
                    : ((java.sql.Date) row[3]).toLocalDate()
            ))
            .collect(Collectors.toList());
    }


    public List<Map<String, Object>> getAllMaterials() {
        List<Object[]> raw = repository.getMaterialsWithSales();
        return raw.stream()
            .map(row -> {
                Map<String, Object> m = new HashMap<>();
                m.put("materialId", ((Number) row[0]).longValue());
                m.put("materialName", (String) row[1]);
                return m;
            })
            .collect(Collectors.toList());
    }

public List<Last5SalesDTO> findLastSalesAllCooperatives(Long materialId) {

    if (authenticatedUser.isWorker()) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "Workers cannot access sales data");
    }

    List<Object[]> raw = repository.lastSalesAllCooperativesRaw(materialId);

    return raw.stream()
        .map(row -> new Last5SalesDTO(
            ((Number) row[0]).longValue(),    // material
            ((Number) row[1]).doubleValue(),  // weight
            ((Number) row[2]).doubleValue(),  // price_kg
            row[3] instanceof java.sql.Timestamp ts
                ? ts.toLocalDateTime().toLocalDate()
                : ((java.sql.Date) row[3]).toLocalDate()
        ))
        .collect(Collectors.toList());
}

}
