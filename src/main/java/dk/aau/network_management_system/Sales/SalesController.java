package dk.aau.network_management_system.Sales;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import dk.aau.network_management_system.auth.PermissionHelper;

@RestController
@RequestMapping("/api/sales")
public class SalesController {
    
    private final SalesService service;
    private final PermissionHelper permissionHelper;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public SalesController(SalesService service, PermissionHelper permissionHelper, AuthenticatedUser authenticatedUser) {
        this.service = service;
        this.permissionHelper = permissionHelper;
        this.authenticatedUser = authenticatedUser;
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<SaleDTO>> getSalesHistory(
            @RequestParam(required = false) Long cooperativeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(defaultValue = "ALL") String type) {
        
        // permission check
        permissionHelper.requireManagerOrAdmin();
        
        // default date
        if (endDate == null) {
            endDate = Instant.now();
        }
        if (startDate == null) {
            startDate = endDate.minus(30, ChronoUnit.DAYS);
        }
        
        // fejlhåndtering for forkert input 
        if (!type.matches("(?i)(REGULAR|COLLECTIVE|ALL)")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid type. Must be REGULAR, COLLECTIVE, or ALL");
        }
        
        // target cooperative
        Long targetCooperativeId = permissionHelper.determineTargetCooperative(cooperativeId);
        
        // henter sales history
        List<SaleDTO> result = service.getSalesHistory(
            targetCooperativeId, startDate, endDate, type
        );
        
        return ResponseEntity.ok(result);
    }
    

    @GetMapping("/active")
    public ResponseEntity<List<SaleDTO>> getActiveSales(
            @RequestParam(required = false) Long cooperativeId,
            @RequestParam(defaultValue = "ALL") String type) {
        
        // permission check
        permissionHelper.requireManagerOrAdmin();
        
        if (!type.matches("(?i)(REGULAR|COLLECTIVE|ALL)")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Invalid type. Must be REGULAR, COLLECTIVE, or ALL");
        }
        
        Long targetCooperativeId = permissionHelper.determineTargetCooperative(cooperativeId);
        
        List<SaleDTO> result = service.getActiveSales(targetCooperativeId, type);
        
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> createSale(@Valid @RequestBody CreateSaleDTO dto) {
        permissionHelper.requireManagerOrAdmin();
        Long cooperativeId = permissionHelper.determineTargetCooperative(null);
        Long workerId = authenticatedUser.getWorkerId();
        service.createSale(cooperativeId, workerId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{saleId}")
    public ResponseEntity<Void> updateSale(
            @PathVariable Long saleId,
            @Valid @RequestBody UpdateSaleDTO dto) {
        permissionHelper.requireManagerOrAdmin();
        Long cooperativeId = permissionHelper.determineTargetCooperative(null);
        service.updateSale(saleId, cooperativeId, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{saleId}/complete")
    public ResponseEntity<Void> completeSale(@PathVariable Long saleId) {
        permissionHelper.requireManagerOrAdmin();
        Long cooperativeId = permissionHelper.determineTargetCooperative(null);
        service.completeSale(saleId, cooperativeId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{saleId}/cancel")
    public ResponseEntity<Void> cancelSale(@PathVariable Long saleId) {
        permissionHelper.requireManagerOrAdmin();
        Long cooperativeId = permissionHelper.determineTargetCooperative(null);
        service.cancelSale(saleId, cooperativeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SaleDTO>> getNormalSales(
            @RequestParam(required = false) Long cooperativeId,
            @RequestParam(defaultValue = "ACTIVE") String status) {

        permissionHelper.requireManagerOrAdmin();

        if (!status.matches("(?i)(ACTIVE|HISTORY)")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid status. Must be ACTIVE or HISTORY");
        }

        Long targetCooperativeId = permissionHelper.determineTargetCooperative(cooperativeId);

        return ResponseEntity.ok(service.getNormalSales(targetCooperativeId, status));
    }
}