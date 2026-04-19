package dk.aau.network_management_system.gamification.leaderboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dk.aau.network_management_system.auth.PermissionHelper;

@RestController
@RequestMapping("/api/leaderboard")

public class LeaderboardController {
         private final LeaderboardService leaderboardService;
         private final PermissionHelper permissionHelper;

         public LeaderboardController(LeaderboardService leaderboardService, PermissionHelper permissionHelper) {
            this.leaderboardService = leaderboardService;
            this.permissionHelper = permissionHelper;
         }

    // GET /api/gamification/leaderboard
    @GetMapping
    public ResponseEntity<LeaderboardDTO> getCurrentLeaderboard(@RequestParam(required = false) Long cooperativeId) {
        Long targetCoop = permissionHelper.determineTargetCooperative(cooperativeId);
        return ResponseEntity.ok(leaderboardService.getCurrentLeaderboard(targetCoop));
    }

    // GET /api/gamification/leaderboard/history?cooperativeId=&yearMonth=2025-05&weekNumber=2
    // Henter et leaderboard snapshot for en specifik uge og måned

    @GetMapping("/history")
    public ResponseEntity<LeaderboardDTO> getHistoricalLeaderboard(
        @RequestParam(required = false) Long cooperativeId,
        @RequestParam String yearMonth,
        @RequestParam int weekNumber) {

            Long targetCoop = permissionHelper.determineTargetCooperative(cooperativeId);
            return ResponseEntity.ok(leaderboardService.getLeaderboard(targetCoop, yearMonth, weekNumber));
        }
}
