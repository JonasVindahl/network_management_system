package dk.aau.network_management_system.gamification;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dk.aau.network_management_system.auth.PermissionHelper;

@RestController
@RequestMapping("/api/gamification/levels")
public class LevelController {

    private final LevelService levelService;
    private final PermissionHelper permissionHelper;

    public LevelController(LevelService levelService, PermissionHelper permissionHelper) {
        this.levelService = levelService;
        this.permissionHelper = permissionHelper;
    }

    // GET /api/gamification/levels
    // Returnerer alle 10 level-definitioner med XP-krav

    @GetMapping
    public ResponseEntity<List<LevelDTO>> getAllLevels() {
        return ResponseEntity.ok(levelService.getAllLevels());
    }

    // GET /api/gamification/levels/worker/{workerId}
    // Returnerer en workers nuværende level, total XP og XP til næste level
    // Workers kan kun se sig selv

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<LevelDTO> getWorkerLevel(@PathVariable Long workerId) {
        Long targetWorker = permissionHelper.determineTargetWorker(workerId);
        return ResponseEntity.ok(levelService.getWorkerLevel(targetWorker));
    }
}