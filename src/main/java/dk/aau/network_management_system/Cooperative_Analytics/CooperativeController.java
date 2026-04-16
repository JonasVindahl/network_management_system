package dk.aau.network_management_system.Cooperative_Analytics;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.aau.network_management_system.auth.PermissionHelper;

@RestController
@RequestMapping("/api/cooperatives")
public class CooperativeController {

    private final CooperativeService service;
    private final PermissionHelper permissionHelper;

    @Autowired
    public CooperativeController(CooperativeService service, PermissionHelper permissionHelper) {
        this.service = service;
        this.permissionHelper = permissionHelper;
    }

    @GetMapping
    public ResponseEntity<List<CooperativeDTO>> listCooperatives() {
        permissionHelper.requireManagerOrAdmin();
        return ResponseEntity.ok(service.listCooperatives());
    }
}
