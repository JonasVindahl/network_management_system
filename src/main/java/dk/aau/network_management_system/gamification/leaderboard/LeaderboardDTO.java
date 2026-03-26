package dk.aau.network_management_system.gamification.leaderboard;

import java.time.Instant;
import java.util.List;


public class LeaderboardDTO {
    private String yearMonth;
    private int weekNumber;
    private Instant computedAt;
    private List<LeaderboardEntryDTO> entries;

    public String getYearMonth() {return yearMonth;}
    public void setYearMonth(String v) {this.yearMonth = v;}
    public int getWeekNumber() {return weekNumber;}
    public void setWeekNumber(int v) {this.weekNumber = v;}
    public Instant getComputedAt() {return computedAt;}
    public void setComputedAt(Instant v) {this.computedAt = v;}
    public List<LeaderboardEntryDTO> getEntries() {return entries;}
    public void setEntries(List<LeaderboardEntryDTO> v) {this.entries = v;}

    public static class LeaderboardEntryDTO {

        private int rankPosition;
        private Long workerId;
        private String workerName;
        private double rawXP;
        public double finalXP;
        private double randomMultiplier;

        public int getRankPosition() {return rankPosition;}
        public void setRankPosition(int v) {this.rankPosition = v;}
        public Long getWorkerId() {return workerId;}
        public void setWorkerId(Long v) {this.workerId = v;}
        public String getWorkerName() {return workerName;}
        public void setWorkerName(String v) {this.workerName = v;}
        public double getRawXP() {return rawXP;}
        public void setRawXP(double v) {this.rawXP = v;}
        public double getFinalXP() {return finalXP;}
        public void setFinalXP(double v) {this.finalXP = v;}
        public double getRandomMultiplier() {return randomMultiplier;}
        public void setRandomMultiplier(double v) {this.randomMultiplier = v;}


    }
}