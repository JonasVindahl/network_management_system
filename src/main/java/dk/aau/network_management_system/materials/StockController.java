package dk.aau.network_management_system.materials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import dk.aau.network_management_system.auth.AuthenticatedUser;
import dk.aau.network_management_system.auth.PermissionHelper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockRepository stockRepository;
    private final PermissionHelper permissionHelper;
    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public StockController(StockRepository stockRepository,
                           PermissionHelper permissionHelper,
                           AuthenticatedUser authenticatedUser) {
        this.stockRepository = stockRepository;
        this.permissionHelper = permissionHelper;
        this.authenticatedUser = authenticatedUser;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Void> addStock(@RequestBody @Valid AddStockDTO dto) {
        permissionHelper.requireManagerOrAdmin();

        Long cooperativeId = authenticatedUser.getCooperativeId();
        if (cooperativeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Authenticated user is not associated with a cooperative");
        }

        int updated = stockRepository.addToStockDecimal(cooperativeId, dto.getMaterialId(), dto.getAmount());
        if (updated == 0) {
            stockRepository.insertStockRow(cooperativeId, dto.getMaterialId(), dto.getAmount());
        }

        return ResponseEntity.noContent().build();
    }
}
