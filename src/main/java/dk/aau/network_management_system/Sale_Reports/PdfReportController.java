package dk.aau.network_management_system.Sale_Reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dk.aau.network_management_system.auth.AuthenticatedUser;
import dk.aau.network_management_system.auth.PermissionHelper;

@RestController
@RequestMapping("/api/reports/pdf")
public class PdfReportController {

    private final PdfReportService pdfReportService;
    private final PermissionHelper permissionHelper;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public PdfReportController(PdfReportService pdfReportService,
                               PermissionHelper permissionHelper,
                               AuthenticatedUser authenticatedUser) {
        this.pdfReportService  = pdfReportService;
        this.permissionHelper  = permissionHelper;
        this.authenticatedUser = authenticatedUser;
    }

    @GetMapping("/normal-sale/{saleId}")
    public ResponseEntity<byte[]> normalSaleReport(
            @PathVariable Long saleId,
            @RequestParam(required = false) Long cooperativeId) {

        permissionHelper.requireManagerOrAdmin();

        Long targetCooperativeId = resolveCooperative(cooperativeId);

        byte[] pdf = pdfReportService.generateNormalSaleReport(saleId, targetCooperativeId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"normal-sale-report-" + saleId + ".pdf\"")
                .body(pdf);
    }

 
    @GetMapping("/collective-sale/{saleId}")
    public ResponseEntity<byte[]> collectiveSaleReport(
            @PathVariable Long saleId,
            @RequestParam(required = false) Long cooperativeId) {

        permissionHelper.requireManagerOrAdmin();

        Long targetCooperativeId = resolveCooperative(cooperativeId);

        byte[] pdf = pdfReportService.generateCollectiveSaleReport(saleId, targetCooperativeId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"collective-sale-report-" + saleId + ".pdf\"")
                .body(pdf);
    }

 
    private Long resolveCooperative(Long requestedCooperativeId) {
        if (authenticatedUser.isAdmin()) {
            return requestedCooperativeId;
        }
        return permissionHelper.determineTargetCooperative(requestedCooperativeId);
    }
}
