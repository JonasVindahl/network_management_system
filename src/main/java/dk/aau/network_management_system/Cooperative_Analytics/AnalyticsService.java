package dk.aau.network_management_system.Cooperative_Analytics;

import java.time.LocalDateTime;
import java.util.List;
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
        public AnalyticsService(AnalyticsRepository repository, AuthenticatedUser authenticatedUser){
            this.repository = repository;
            this.authenticatedUser = authenticatedUser;
        }


    // GET - ALL Worker productivity
    public List<WorkerProductivityDTO> getAllWorkerProductivity(
            Long cooperativeId, LocalDateTime startDate, LocalDateTime endDate) {
        
        // Workers cannot see ALL workers
        if (authenticatedUser.isWorker()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Workers can only view their own productivity");
        }
        
        // Managers can only see own cooperative
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
                ((Number) row[0]).longValue(),      // worker_id
                (String) row[1],                    // worker_name
                ((Number) row[2]).doubleValue(),    // total_collected_kg
                ((Number) row[3]).longValue(),      // number_of_weighings
                ((Number) row[4]).doubleValue()     // avg_weight_per_weighing
            ))
            .collect(Collectors.toList());
    }


    //GET - Worker productivity
    public List<WorkerProductivityDTO> getWorkerProductivity(
            Long cooperativeId, Long workerId, LocalDateTime startDate, LocalDateTime endDate) {
        
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
    }


    //GET - Stock for material and sold
    public List<StockByMaterialDTO>  getStockByMaterial(Long cooperativeId){
        
        List<Object[]> stock = repository.getStockByMaterial(cooperativeId);

        return stock.stream()    
            .map(row -> new StockByMaterialDTO(
                (String) row[0],                 // material_name
                ((Number) row[1]).doubleValue(), // total_collected_kg
                ((Number) row[2]).doubleValue(), // total_sold_kg
                ((Number) row[3]).doubleValue()  // current_stock_kg
            ))
            .collect(Collectors.toList());
    }


    //GET - revenue for cooperative and sales + average kg price 
    public List<RevenueDTO> getRevenue(
        Long cooperativeId, LocalDateTime startDate, LocalDateTime endDate){
            
        List<Object[]> revenue = repository.findRevenueRaw(cooperativeId, startDate, endDate);

        
        return revenue.stream()    
            .map(row -> new RevenueDTO(
            ((Number) row[0]).doubleValue(),  // total_revenue
            ((Number) row[1]).longValue(),    // total_sales
            ((Number) row[2]).doubleValue()   // avg_price_per_kg
            ))
            .collect(Collectors.toList());
        }


    public CooperativePerformanceDTO getCooperativePerformance(Long cooperativeId){
        
        if (authenticatedUser.isWorker()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Workers cannot access cooperative performance data");
        }
        
        if (!authenticatedUser.isAdmin() && 
            !cooperativeId.equals(authenticatedUser.getCooperativeId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "You can only access your own cooperative's data");
        }
        
        //hent data fra repository
        List<Object[]> results = repository.findCooperativePerformanceRaw(cooperativeId);
        
        //håndtere null
        if (results == null || results.isEmpty()) {
            return new CooperativePerformanceDTO(0.0, 0.0, 0.0, 0);
        }
        
        // hent første række
        Object[] raw = results.get(0);
        
        Double totalCollected = raw[0] != null ? ((Number) raw[0]).doubleValue() : 0.0;
        Double totalSold = raw[1] != null ? ((Number) raw[1]).doubleValue() : 0.0;
        Double currentStock = raw[2] != null ? ((Number) raw[2]).doubleValue() : 0.0;
        Integer activeWorkers = raw[3] != null ? ((Number) raw[3]).intValue() : 0;
        
        return new CooperativePerformanceDTO(
            totalCollected,
            totalSold,
            currentStock,
            activeWorkers
        ); 
    }
}