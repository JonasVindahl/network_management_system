package dk.aau.network_management_system.multiplier;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dk.aau.network_management_system.auth.PermissionHelper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CooperativeMaterialMultiplierController {

    private final CooperativeMaterialMultiplierService service;
    private final PermissionHelper permissionHelper;

    @Autowired
    public CooperativeMaterialMultiplierController(
            CooperativeMaterialMultiplierService service,
            PermissionHelper permissionHelper) {
        this.service = service;
        this.permissionHelper = permissionHelper;
    }

    @PostMapping("/multipliers")
    public ResponseEntity<CooperativeMaterialMultiplier> saveOrUpdateMultiplier(
            @Valid @RequestBody MultiplierDTO dto) {

        permissionHelper.requireManagerOrAdmin();

        Long targetCooperativeId = permissionHelper.determineTargetCooperativeForWrite(
            dto.getCooperativeId()
        );

        CooperativeMaterialMultiplier result = service.saveOrUpdateMultiplier(
            targetCooperativeId,
            dto.getMaterialId(),
            dto.getMultiplierValue()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/multipliers")
    public ResponseEntity<List<CooperativeMaterialMultiplier>> getAllMultipliers(
            @RequestParam(required = false) Long cooperativeId) {

        permissionHelper.requireManagerOrAdmin();

        Long targetCooperativeId = permissionHelper.determineTargetCooperative(cooperativeId);

        List<CooperativeMaterialMultiplier> result = service.getAllMultipliers(targetCooperativeId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/multipliers/single")
    public ResponseEntity<CooperativeMaterialMultiplier> getMultiplier(
            @RequestParam(required = false) Long cooperativeId,
            @RequestParam Long materialId) {

        permissionHelper.requireManagerOrAdmin();

        Long targetCooperativeId = permissionHelper.determineTargetCooperative(cooperativeId);

        return service.getMultiplier(targetCooperativeId, materialId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}