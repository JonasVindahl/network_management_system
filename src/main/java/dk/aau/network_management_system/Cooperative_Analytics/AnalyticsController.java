package dk.aau.network_management_system.Cooperative_Analytics;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;


@RestController
@RequestMapping("/api")
public class AnalyticsController {

    private final AnalyticsService service;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public AnalyticsController(AnalyticsService service, AuthenticatedUser authenticatedUser) {
        this.service = service;
        this.authenticatedUser = authenticatedUser;
    }

    //opdateret for presmission
    @GetMapping("/performance")
    public ResponseEntity<CooperativePerformanceDTO> getPerformance(
            @RequestParam(required = false) Long cooperativeId) {
        
        // Workers kan ikke tilgå cooperative performance
        if (authenticatedUser.isWorker()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Workers cannot access cooperative performance data");
        }
        
        // Bestem hvilken cooperative der skal hentes data for
        Long targetCooperativeId;
        
        if (authenticatedUser.isAdmin()) {
            // Skal ændres - admins er ikke bundet til cooperative
            targetCooperativeId = cooperativeId != null 
                ? cooperativeId 
                : authenticatedUser.getCooperativeId();
        } else {
            // Manager kan KUN se egen cooperative (ignorer cooperativeId param)
            targetCooperativeId = authenticatedUser.getCooperativeId();
        }
        
        CooperativePerformanceDTO result = service.getCooperativePerformance(targetCooperativeId);
        return ResponseEntity.ok(result);
    }


     //GET - All worker productivity in cooperative
    @GetMapping("/productivity")
    public ResponseEntity<List<WorkerProductivityDTO>> getProductivity(
            @RequestParam(required = false) Long cooperativeId,
            @RequestParam(required = false) Long workerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        // Default dates
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
     
        // Auth

        Long targetCooperativeId;
        Long targetWorkerId;
        
        if (authenticatedUser.isWorker()) {
            // Workers kan KUN se deres egen productivity
            targetCooperativeId = authenticatedUser.getCooperativeId();
            targetWorkerId = authenticatedUser.getWorkerId();
            
        } else if (authenticatedUser.isManager()) {
            // Manager kan se alle workers i egen cooperative
            targetCooperativeId = authenticatedUser.getCooperativeId();
            targetWorkerId = workerId; 
            
        } else {
        // Admin SKAL specificere cooperativeId
        if (cooperativeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Admin must specify cooperativeId parameter");
        }
        targetCooperativeId = cooperativeId;
        targetWorkerId = workerId; // Optional - kan være null (alle workers)
    }
        
        // Til at hente data 

        List<WorkerProductivityDTO> result;
        
        if (targetWorkerId != null) {
            // Specific worker
            result = service.getWorkerProductivity(
                targetCooperativeId, targetWorkerId, startDate, endDate);
        } else {
            // All workers in cooperative
            result = service.getAllWorkerProductivity(
                targetCooperativeId, startDate, endDate);
        }
        
        return ResponseEntity.ok(result);
    }


    // GET cooperative revenue and sales + averge priceperkg
    //<FX> curl -X GET "http://127.0.0.1:8080/api/cooperative/analytics/1/revenue?startDate=2025-11-01T00:00:00&endDate=2025-11-30T23:59:59"
    @GetMapping("/revenue")
        public ResponseEntity<List<RevenueDTO>> getRevenue(
                @RequestParam(required = false) Long cooperativeId,
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
    
            // Sæt defaults hvis null
                if (startDate == null) {
                    startDate = LocalDateTime.now().minusMonths(12); // f.eks. sidste måned
                }
                if (endDate == null) {
                    endDate = LocalDateTime.now();
                }

        Long targetCooperativeId;
        
        if (authenticatedUser.isWorker()) {
            // Workers kan KUN se deres egen productivity
            targetCooperativeId = authenticatedUser.getCooperativeId();
            
        } else if (authenticatedUser.isManager()) {
            // Manager kan se alle workers i egen cooperative
            targetCooperativeId = authenticatedUser.getCooperativeId();
            
        } else {
        // Admin SKAL specificere cooperativeId
        if (cooperativeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Admin must specify cooperativeId parameter");
        }
        targetCooperativeId = cooperativeId;
    }

        List<RevenueDTO> result;

        result = service.getRevenue(targetCooperativeId, startDate, endDate);
        
        return ResponseEntity.ok(result);
    }



    @GetMapping("/stock")
    public ResponseEntity<List<StockByMaterialDTO>> getStockByMaterial(
                @RequestParam(required = false) Long cooperativeId) {    
       
        Long targetCooperativeId;
        
        if (authenticatedUser.isWorker()) {
            // Workers kan KUN se deres egen productivity
            targetCooperativeId = authenticatedUser.getCooperativeId();
            
        } else if (authenticatedUser.isManager()) {
            // Manager kan se alle workers i egen cooperative
            targetCooperativeId = authenticatedUser.getCooperativeId();
            
        } else {
        // Admin SKAL specificere cooperativeId
        if (cooperativeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Admin must specify cooperativeId parameter");
        }
        targetCooperativeId = cooperativeId;
        }

        List<StockByMaterialDTO> result;

            result = service.getStockByMaterial(targetCooperativeId);
            
            return ResponseEntity.ok(result);
    }


}

