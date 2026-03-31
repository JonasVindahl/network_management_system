package dk.aau.network_management_system.Cooperative_Analytics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;  
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;

@Service
public class AnalyticsService {
    
    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);
    
    private final AnalyticsRepository repository;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public AnalyticsService(AnalyticsRepository repository, AuthenticatedUser authenticatedUser) {
        this.repository = repository;
        this.authenticatedUser = authenticatedUser;
    }

    public List<CooperativePerformanceDTO> getCooperativePerformance(Long cooperativeId) {
        
        validateManagerOrAdmin();
        validateCooperativeOwnership(cooperativeId);
        
        try {
            List<Object[]> results = repository.findCooperativePerformanceRaw(cooperativeId);
            
            return results.stream()
                .map(row -> new CooperativePerformanceDTO(
                    ((Number) row[0]).doubleValue(),  // total_collected
                    ((Number) row[1]).doubleValue(),  // total_sold
                    ((Number) row[2]).doubleValue(),  // current_stock
                    ((Number) row[3]).intValue()      // active_workers
                ))
                .collect(Collectors.toList());
                
        } catch (DataAccessException e) {
            log.error("Database error while fetching cooperative performance for cooperative {}", 
                     cooperativeId, e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error retrieving cooperative performance data"
            );
        } catch (ClassCastException | NullPointerException e) {
            log.error("Data mapping error in cooperative performance for cooperative {}", 
                     cooperativeId, e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error processing performance data"
            );
        }
    }

    public List<WorkerProductivityDTO> getAllWorkerProductivity(
            Long cooperativeId, LocalDateTime startDate, LocalDateTime endDate) {
        
        validateManagerOrAdmin();
        validateCooperativeOwnership(cooperativeId);
        
        try {
            List<Object[]> raw = repository.findAllWorkerProductivityRaw(
                cooperativeId, startDate, endDate
            );
            
            return raw.stream()
                .map(row -> new WorkerProductivityDTO(
                    ((Number) row[0]).longValue(),      // worker_id
                    (String) row[1],                    // worker_name
                    ((Number) row[2]).doubleValue(),    // total_collected_kg
                    ((Number) row[3]).longValue(),      // number_of_weighings
                    ((Number) row[4]).doubleValue()     // avg_weight_per_weighing
                ))
                .collect(Collectors.toList());
                
        } catch (DataAccessException e) {
            log.error("Database error while fetching worker productivity for cooperative {}", 
                     cooperativeId, e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error retrieving worker productivity data"
            );
        } catch (ClassCastException | NullPointerException e) {
            log.error("Data mapping error in worker productivity for cooperative {}", 
                     cooperativeId, e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error processing productivity data"
            );
        }
    }

    public List<WorkerProductivityDTO> getWorkerProductivity(
            Long cooperativeId, Long workerId, LocalDateTime startDate, LocalDateTime endDate) {
                
        try {
            List<Object[]> raw = repository.findWorkerProductivityRaw(
                cooperativeId, workerId, startDate, endDate
            );
            
            return raw.stream()
                .map(row -> new WorkerProductivityDTO(
                    ((Number) row[0]).longValue(),      // worker_id
                    (String) row[1],                    // worker_name
                    ((Number) row[2]).doubleValue(),    // total_collected_kg
                    ((Number) row[3]).longValue(),      // number_of_weighings
                    ((Number) row[4]).doubleValue()     // avg_weight_per_weighing
                ))
                .collect(Collectors.toList());
                
        } catch (DataAccessException e) {
            log.error("Database error while fetching productivity for worker {} in cooperative {}", 
                     workerId, cooperativeId, e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error retrieving worker productivity"
            );
        } catch (ClassCastException | NullPointerException e) {
            log.error("Data mapping error for worker {} productivity", workerId, e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error processing productivity data"
            );
        }
    }

    public List<StockByMaterialDTO> getStockByMaterial(Long cooperativeId) {
        
        validateManagerOrAdmin();
        validateCooperativeOwnership(cooperativeId);
        
        try {
            List<Object[]> stock = repository.getStockByMaterial(cooperativeId);
            
            return stock.stream()    
                .map(row -> new StockByMaterialDTO(
                    (String) row[0],                 // material_name
                    ((Number) row[1]).doubleValue(), // total_collected_kg
                    ((Number) row[2]).doubleValue(), // total_sold_kg
                    ((Number) row[3]).doubleValue()  // current_stock_kg
                ))
                .collect(Collectors.toList());
                
        } catch (DataAccessException e) {
            log.error("Database error while fetching stock by material for cooperative {}", 
                     cooperativeId, e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error retrieving stock data"
            );
        } catch (ClassCastException | NullPointerException e) {
            log.error("Data mapping error in stock by material for cooperative {}", 
                     cooperativeId, e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error processing stock data"
            );
        }
    }

    public List<RevenueDTO> getRevenue(
            Long cooperativeId, LocalDateTime startDate, LocalDateTime endDate) {
        
        validateManagerOrAdmin();
        validateCooperativeOwnership(cooperativeId);
        
        try {
            List<Object[]> revenue = repository.findRevenueRaw(cooperativeId, startDate, endDate);
            
            return revenue.stream()    
                .map(row -> new RevenueDTO(
                    ((Number) row[0]).doubleValue(),  // total_revenue
                    ((Number) row[1]).longValue(),    // total_sales
                    ((Number) row[2]).doubleValue()   // avg_price_per_kg
                ))
                .collect(Collectors.toList());
                
        } catch (DataAccessException e) {
            log.error("Database error while fetching revenue for cooperative {} between {} and {}", 
                     cooperativeId, startDate, endDate, e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error retrieving revenue data"
            );
        } catch (ClassCastException | NullPointerException e) {
            log.error("Data mapping error in revenue calculation for cooperative {}", 
                     cooperativeId, e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error processing revenue data"
            );
        }
    }

    public List<Last5SalesDTO> findLastSalesForCooperative(
            Long cooperativeId, Long materialId) {
        
        validateManagerOrAdmin();
        validateCooperativeOwnership(cooperativeId);
        
        try {
            List<Object[]> raw = repository.LastSalesCooperativeRaw(cooperativeId, materialId);
            
            return raw.stream()
                .map(row -> new Last5SalesDTO(
                    ((Number) row[0]).longValue(),         // material
                    ((Number) row[1]).doubleValue(),       // weight
                    ((Number) row[2]).doubleValue(),       // price_kg
                    ((java.sql.Date) row[3]).toLocalDate() // date
                ))
                .collect(Collectors.toList());
                
        } catch (DataAccessException e) {
            log.error("Database error while fetching last sales for cooperative {} and material {}", 
                     cooperativeId, materialId, e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error retrieving sales history"
            );
        } catch (ClassCastException | NullPointerException e) {
            log.error("Data mapping error in last sales for cooperative {} and material {}", 
                     cooperativeId, materialId, e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error processing sales data"
            );
        }
    }

    private void validateManagerOrAdmin() {
        if (authenticatedUser.isWorker()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Workers cannot access this data");
        }
    }
    
    private void validateCooperativeOwnership(Long cooperativeId) {
        if (authenticatedUser.isAdmin()) {
            return;
        }
        
        Long userCooperativeId = authenticatedUser.getCooperativeId();
        
        if (!Objects.equals(cooperativeId, userCooperativeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "You can only access your own cooperative's data");
        }
    }
}