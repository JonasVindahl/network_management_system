package dk.aau.network_management_system.Collective_Sale;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dk.aau.network_management_system.auth.PermissionHelper;

@RestController
@RequestMapping("/api/collective-sale")
public class CollectiveSaleController {

    private final CollectiveSaleService service;
    private final PermissionHelper permissionHelper;

    @Autowired
    public CollectiveSaleController(CollectiveSaleService service, PermissionHelper permissionHelper) {
        this.service = service;
        this.permissionHelper = permissionHelper;
    }

    @GetMapping
    public ResponseEntity<List<ActiveCollectiveSaleDTO>> getActiveSales() {
        permissionHelper.requireManagerOrAdmin();
        return ResponseEntity.ok(service.getActiveSales());
    }

    @GetMapping("/invitations")
    public ResponseEntity<List<CollectiveSaleInvitationDTO>> getPendingInvitations() {
        permissionHelper.requireManagerOrAdmin();
        return ResponseEntity.ok(service.getPendingInvitations());
    }

    @PostMapping
    public ResponseEntity<Long> createCollectiveSale(@RequestBody @Valid CreateCollectiveSaleDTO dto) {
        permissionHelper.requireManagerOrAdmin();
        Long saleId = service.createCollectiveSale(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saleId);
    }

    @PostMapping("/{saleId}/invite")
    public ResponseEntity<Void> inviteCooperative(
            @PathVariable Long saleId,
            @RequestBody @Valid InviteCooperativeDTO dto) {
        permissionHelper.requireManagerOrAdmin();
        service.inviteCooperative(saleId, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{saleId}/join")
    public ResponseEntity<Void> joinCollectiveSale(@PathVariable Long saleId) {
        permissionHelper.requireManagerOrAdmin();
        service.joinCollectiveSale(saleId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{saleId}/contribution")
    public ResponseEntity<Void> updateContribution(
            @PathVariable Long saleId,
            @RequestBody @Valid UpdateContributionDTO dto) {
        permissionHelper.requireManagerOrAdmin();
        service.updateContribution(saleId, dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{saleId}/material")
    public ResponseEntity<Void> updateSaleMaterial(
            @PathVariable Long saleId,
            @RequestBody @Valid UpdateSaleMaterialDTO dto) {
        permissionHelper.requireManagerOrAdmin();
        service.updateSaleMaterial(saleId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{saleId}/leave")
    public ResponseEntity<Void> leaveCollectiveSale(@PathVariable Long saleId) {
        permissionHelper.requireManagerOrAdmin();
        service.leaveCollectiveSale(saleId);
        return ResponseEntity.ok().build();
    }
}
