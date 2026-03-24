package dk.aau.network_management_system.gamification;

public class LevelDTO {
    
    private int levelNumber; 
    private String levelName;
    private int xpRequired; // kummulativt XP krævet for dette level
    private int xpToNext; // XP mangler for at nå næste level

    // worker specifikke felter
    private Long workerId;
    private int totalXP;
    private boolean isCurrentLevel; // indikerer om dette er workerens nuværende level

    public int getLevelNumber() {return levelNumber;}
    public void setLevelNumber(int v) {this.levelNumber = v;}
    public String getLevelName() {return levelName;}
    public void setLevelName(String v) {this.levelName = v;}
    public int getXpRequired() {return xpRequired;}
    public void setXpRequired(int v) {this.xpRequired = v;}
    public int getXpToNext() {return xpToNext;}
    public void setXpToNext(int v) {this.xpToNext = v;}
    public Long getWorkerId() {return workerId;}
    public void setWorkerId(Long v) {this.workerId = v;}
    public int getTotalXP() {return totalXP;}
    public void setTotalXP(int v) {this.totalXP = v;}
    public boolean isCurrentLevel() {return isCurrentLevel;}
    public void setCurrentLevel(boolean v) {this.isCurrentLevel = v;}
    
}
