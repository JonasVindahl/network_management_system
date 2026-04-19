package dk.aau.network_management_system.buyers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.aau.network_management_system.auth.PermissionHelper;

@RestController
@RequestMapping("/api/buyers")
public class BuyerController {

    private final BuyerService service;
    private final PermissionHelper permissionHelper;

    @Autowired
    public BuyerController(BuyerService service, PermissionHelper permissionHelper) {
        this.service = service;
        this.permissionHelper = permissionHelper;
    }

    @GetMapping
    public ResponseEntity<List<BuyerDTO>> listBuyers() {
        permissionHelper.requireManagerOrAdmin();
        return ResponseEntity.ok(service.listBuyers());
    }
}
