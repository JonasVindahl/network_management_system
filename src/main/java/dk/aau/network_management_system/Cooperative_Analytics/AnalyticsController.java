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
@RequestMapping("/api/cooperative/analytics")
public class AnalyticsController {

    private final AnalyticsService service;


    @Autowired
    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }


    // GET cooperative performance overview
    @GetMapping("/{cooperativeId}/performance")
    public ResponseEntity<CooperativePerformanceDTO> getPerformance(
            @PathVariable Long cooperativeId) {
        
        CooperativePerformanceDTO result = service.getCooperativePerformance(cooperativeId);
        return ResponseEntity.ok(result);
    }


     // GET all worker productivity in cooperative
    @GetMapping("/{cooperativeId}/workers/productivity")
    public ResponseEntity<List<WorkerProductivityDTO>> getAllWorkerProductivity(
            @PathVariable Long cooperativeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<WorkerProductivityDTO> result = service.getAllWorkerProductivity(
            cooperativeId, startDate, endDate
        );
        return ResponseEntity.ok(result);
    }


     // GET specific worker productivity
    @GetMapping("/{cooperativeId}/workers/{workerId}/productivity")
    public ResponseEntity<List<WorkerProductivityDTO>> getWorkerProductivity(
            @PathVariable Long cooperativeId,
            @PathVariable Long workerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<WorkerProductivityDTO> result = service.getWorkerProductivity(
            cooperativeId, workerId, startDate, endDate
        );
        return ResponseEntity.ok(result);
    }


    // GET coopertiv stock hvor hvert item viser også solg collected 
    @GetMapping("/{cooperativeId}/stock")
    public ResponseEntity<List<StockByMaterialDTO>> getStockByMaterial(
            @PathVariable Long cooperativeId
    ){

        List<StockByMaterialDTO> result = service.getStockByMaterial(cooperativeId);
        return ResponseEntity.ok(result);
    }

    // GET cooperative revenue and sales + averge priceperkg
    //<EKSEMPEL> curl -X GET "http://127.0.0.1:8080/api/cooperative/analytics/1/revenue?startDate=2025-11-01T00:00:00&endDate=2025-11-30T23:59:59"
    @GetMapping("/{cooperativeId}/revenue")
        public ResponseEntity<List<RevenueDTO>> getRevenue(
                @PathVariable Long cooperativeId,
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
            
            List<RevenueDTO> result = service.getRevenue(cooperativeId, startDate, endDate);
            return ResponseEntity.ok(result);
    }


}

