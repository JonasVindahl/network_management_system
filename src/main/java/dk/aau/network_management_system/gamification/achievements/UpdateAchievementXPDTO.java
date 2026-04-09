package dk.aau.network_management_system.gamification.achievements; 

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateAchievementXPDTO {
    @NotNull
    private Long achievementId;

    @NotNull
    @Min(0)
    private Integer xpReward;

    public Long getAchievementId() {return achievementId;}
    public void setAchievementId(Long v) {this.achievementId = v;}
    public Integer getXpReward() {return xpReward;}
    public void setXpReward(Integer v) {this.xpReward = v;}
}
