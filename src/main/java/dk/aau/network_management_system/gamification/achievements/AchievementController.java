package dk.aau.network_management_system.gamification.achievements;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dk.aau.network_management_system.auth.AuthenticatedUser;
import dk.aau.network_management_system.auth.PermissionHelper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/gamification/achievements")
public class AchievementController {
    private final AchievementService achievementService;
    private final AuthenticatedUser authenticatedUser;
    private final PermissionHelper permissionHelper;

    public AchievementController (AchievementService achievementService, 
        AuthenticatedUser authenticatedUser, 
        PermissionHelper permissionHelper) {
        this.achievementService = achievementService;
        this.authenticatedUser = authenticatedUser;
        this.permissionHelper = permissionHelper;
    }

    // GET /api/gamification/achievements
    @GetMapping
    public ResponseEntity<List<AchievementDTO>> listAchievements(
        @RequestParam(required = false) Long cooperativeId) {
            Long targetCoop = permissionHelper.determineTargetCooperative(cooperativeId);
            return ResponseEntity.ok(achievementService.listAllAchievements(targetCoop));
        }

    // PATCH /api/gamification/achievements/{achievementId}/xp
        @PatchMapping("/{achievementId}/xp")
        public ResponseEntity<Void> updateAchievementXp(
            @PathVariable Long achievementId,
            @RequestParam(required = false) Long cooperativeId,
            @Valid @RequestBody UpdateAchievementXPDTO dto) {
                permissionHelper.requireManagerOrAdmin();
                Long targetCoop = permissionHelper.determineTargetCooperative(cooperativeId);
                dto.setAchievementId(achievementId);
                achievementService.updateAchievementXp(targetCoop, dto);
                return ResponseEntity.noContent().build();
            }

    // GET /api/gamification/achievements/workers/{workerId}/month
    @GetMapping("/workers/{workerId}/month")
    public ResponseEntity<WorkerMonthSummaryDTO> getWorkerMonthSummary(
        @PathVariable Long workerId,
        @RequestParam(required = false) String yearMonth,
        @RequestParam(required = false) Long cooperativeId) {
            Long targetCoop = permissionHelper.determineTargetCooperative(cooperativeId);
            Long targetWorker = permissionHelper.determineTargetWorker(workerId);
            return ResponseEntity.ok(achievementService.getWorkerMonthSummary(targetWorker, yearMonth, targetCoop));
        }

// GET /api/gamification/achievements/workers/{workerId}/top-month
    @GetMapping("/workers/{workerId}/top-month")
    public ResponseEntity<WorkerMonthSummaryDTO> getTopMonthThisYear(
            @PathVariable Long workerId,
            @RequestParam(required = false) Long cooperativeId) {
 
        Long targetCoop = permissionHelper.determineTargetCooperative(cooperativeId);
        Long targetWorker = permissionHelper.determineTargetWorker(workerId);
        return ResponseEntity.ok(
            achievementService.getTopMonthThisYear(targetWorker, targetCoop));
    }
 
    // GET /api/gamification/achievements/workers/{workerId}/top-day
    @GetMapping("/workers/{workerId}/top-day")
    public ResponseEntity<Map<String, Object>> getTopDayInMonth(
            @PathVariable Long workerId,
            @RequestParam(required = false) String yearMonth,
            @RequestParam(required = false) Long cooperativeId) {
 
        Long targetCoop = permissionHelper.determineTargetCooperative(cooperativeId);
        Long targetWorker = permissionHelper.determineTargetWorker(workerId);
        return ResponseEntity.ok(
            achievementService.getTopDayInMonth(targetWorker, yearMonth, targetCoop));
    }
}