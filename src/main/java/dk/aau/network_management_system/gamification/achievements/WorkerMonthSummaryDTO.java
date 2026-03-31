package dk.aau.network_management_system.gamification.achievements;

import java.util.List;

// Summary of a worker's gamification stats for a given month
public class WorkerMonthSummaryDTO {
    
    private Long workerId;
    private String workerName;
    private String yearMonth; // 'YYYY-MM'
    private double totalWeightKg;
    private int daysWorked;
    private int achievementsUnlocked;
    private int totalXpEarned; // achievement XP + understaffed XP
    private List<AchievementDTO> achievements; // details of achievements unlocked
    
    public Long getWorkerId() {return workerId;}
    public void setWorkerId(Long v) {this.workerId = v;}
    public String getWorkerName() {return workerName;}
    public void setWorkerName(String v) {this.workerName = v;}
    public String getYearMonth() {return yearMonth;}
    public void setYearMonth(String v) {this.yearMonth = v;}
    public double getTotalWeightKg() {return totalWeightKg;}
    public void setTotalWeightKg(double v) {this.totalWeightKg = v;}
    public int getDaysWorked() {return daysWorked;}
    public void setDaysWorked(int v) {this.daysWorked = v;}
    public int getAchievementsUnlocked() {return achievementsUnlocked;}
    public void setAchievementsUnlocked(int v) {this.achievementsUnlocked = v;}
    public int getTotalXpEarned() {return totalXpEarned;}
    public void setTotalXpEarned(int v) {this.totalXpEarned = v;}
    public List<AchievementDTO> getAchievements() {return achievements;}
    public void setAchievements(List<AchievementDTO> v) {this.achievements = v;}
}
