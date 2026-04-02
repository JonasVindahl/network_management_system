package dk.aau.network_management_system.gamification.achievements;

import java.time.Instant;

public class AchievementDTO {
    private Long achievementId;
    private String achievementKey;
    private String achievementName;
    private String description;
    private String category;
    private double thresholdValue;
    private int xpReward;
    private String difficulty;

    private double progressValue; // achievements tracking progress
    private boolean unlocked;
    private Instant unlockedAt;

    public AchievementDTO() {}
    public Long getAchievementId() {return achievementId;}
    public void setAchievementId(Long v) {this.achievementId = v;} // v = local variable (value)
    public String getAchievementKey()  {return achievementKey;}
    public void setAchievementKey(String v) {this.achievementKey = v;}
    public String getAchievementName() {return achievementName;}
    public void setAchievementName(String v) {this.achievementName = v;}
    public String getDescription() {return description;}
    public void setDescription(String v) {this.description = v;}
    public String getCategory() {return category;}
    public void setCategory(String v) {this.category = v;}
    public double getThresholdValue() {return thresholdValue;}
    public void setThresholdValue(double v) {this.thresholdValue = v;}
    public int getXpReward() {return xpReward;}
    public void setXpReward(int v) {this.xpReward = v;}
    public String getDifficulty() {return difficulty;}
    public void setDifficulty(String v) {this.difficulty = v;}
    public double getProgressValue() {return progressValue;}
    public void setProgressValue(double v) {this.progressValue = v;}
    public boolean isUnlocked() {return unlocked;}
    public void setUnlocked(boolean v) {this.unlocked = v;}
    public Instant getUnlockedAt() {return unlockedAt;}
    public void setUnlockedAt(Instant v) {this.unlockedAt = v;}

}