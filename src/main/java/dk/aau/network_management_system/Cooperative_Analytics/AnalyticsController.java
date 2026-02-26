package dk.aau.network_management_system.Cooperative_Analytics;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class AnalyticsController {

    private final AnalyticsService service;


    @Autowired
    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }


    //GET - Cooperative performance overview
    @GetMapping("/performance")
    public ResponseEntity<CooperativePerformanceDTO> getPerformance(
            @PathVariable Long cooperativeId) {
        
        CooperativePerformanceDTO result = service.getCooperativePerformance(cooperativeId);
        return ResponseEntity.ok(result);
    }


     //GET - All worker productivity in cooperative
    @GetMapping("/workers/productivity")
    public ResponseEntity<List<WorkerProductivityDTO>> getAllWorkerProductivity(
            @PathVariable Long cooperativeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    
            // Sæt defaults hvis null
            if (startDate == null) {
                startDate = LocalDateTime.now().minusMonths(1); // f.eks. sidste måned
            }
            if (endDate == null) {
                endDate = LocalDateTime.now();
            }
        List<WorkerProductivityDTO> result = service.getAllWorkerProductivity(
            cooperativeId, startDate, endDate
        );
        return ResponseEntity.ok(result);
    }


     //GET - Specific worker productivity
    @GetMapping("/workers/{workerId}/productivity")
    public ResponseEntity<List<WorkerProductivityDTO>> getWorkerProductivity(
            @PathVariable Long cooperativeId,
            @PathVariable Long workerId,
                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    
        // Sæt defaults hvis null
            if (startDate == null) {
                startDate = LocalDateTime.now().minusMonths(1); // f.eks. sidste måned
            }
            if (endDate == null) {
                endDate = LocalDateTime.now();
            }
        List<WorkerProductivityDTO> result = service.getWorkerProductivity(
            cooperativeId, workerId, startDate, endDate
        );
        return ResponseEntity.ok(result);
    }


    //GET - Coopertiv stock - Sold, Material, colleted...
    @GetMapping("/stock")
    public ResponseEntity<List<StockByMaterialDTO>> getStockByMaterial(
            @PathVariable Long cooperativeId
    ){

        List<StockByMaterialDTO> result = service.getStockByMaterial(cooperativeId);
        return ResponseEntity.ok(result);
    }

    // GET cooperative revenue and sales + averge priceperkg
    //<FX> curl -X GET "http://127.0.0.1:8080/api/cooperative/analytics/1/revenue?startDate=2025-11-01T00:00:00&endDate=2025-11-30T23:59:59"
    @GetMapping("/revenue")
        public ResponseEntity<List<RevenueDTO>> getRevenue(
                @PathVariable Long cooperativeId,
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    
            // Sæt defaults hvis null
                if (startDate == null) {
                    startDate = LocalDateTime.now().minusMonths(12); // f.eks. sidste måned
                }
                if (endDate == null) {
                    endDate = LocalDateTime.now();
                }
            List<RevenueDTO> result = service.getRevenue(cooperativeId, startDate, endDate);
            return ResponseEntity.ok(result);
    }


}

