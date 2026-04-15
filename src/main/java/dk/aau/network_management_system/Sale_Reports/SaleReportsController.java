package dk.aau.network_management_system.Sale_Reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dk.aau.network_management_system.auth.AuthenticatedUser;
import dk.aau.network_management_system.auth.PermissionHelper;

@RestController
@RequestMapping("/api/reports/sales/normal")  
public class SaleReportsController {

    private final SaleReportsService service;
    private final PermissionHelper permissionHelper;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public SaleReportsController(SaleReportsService service, 
                                PermissionHelper permissionHelper, 
                                AuthenticatedUser authenticatedUser) {
        this.service = service;
        this.permissionHelper = permissionHelper;
        this.authenticatedUser = authenticatedUser;
    }

    @GetMapping("/{saleId}")
    public ResponseEntity<SaleReportDTO> getSaleReport(
            @PathVariable Long saleId,
            @RequestParam(required = false) Long cooperativeId) {

        permissionHelper.requireManagerOrAdmin();

        Long targetCooperativeId = null;
        
        if (!authenticatedUser.isAdmin()) {
            // Manager must use their own cooperative
            targetCooperativeId = permissionHelper.determineTargetCooperative(cooperativeId);
        } else if (cooperativeId != null) {
            // Admin specified a cooperative
            targetCooperativeId = cooperativeId;
        }

        SaleReportDTO report = service.getSaleReport(saleId, targetCooperativeId);

        return ResponseEntity.ok(report);
    }
}