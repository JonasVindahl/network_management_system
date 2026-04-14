package dk.aau.network_management_system.Collective_Sale_Reports;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dk.aau.network_management_system.auth.AuthenticatedUser;
import dk.aau.network_management_system.auth.PermissionHelper;

@RestController
@RequestMapping("/api/reports/normal")
public class SaleReportsController {

    private final SaleReportsService saleReportsService;
    private final PermissionHelper permissionHelper;
    private final AuthenticatedUser authenticatedUser; 


    public SaleReportsController(SaleReportsService saleReportsService, PermissionHelper permissionHelper, AuthenticatedUser authenticatedUser) {
        this.saleReportsService = saleReportsService;
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
            targetCooperativeId = permissionHelper.determineTargetCooperative(cooperativeId);
        } else if (cooperativeId != null) {
            targetCooperativeId = cooperativeId;
        }
       

        SaleReportDTO report = saleReportsService.getSaleReport(
            saleId,
            targetCooperativeId
        );

        return ResponseEntity.ok(report);
    }
}