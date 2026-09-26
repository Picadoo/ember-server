package town.sunshine.corerpg;

import java.util.ArrayList;
import java.util.List;

/** Per-player RPG daily state (YAML keys per shell requirements). */
public final class PlayerData {

    private int coin;
    private String lastSignDate = "";
    private int activity;
    private final List<Integer> activityClaimed = new ArrayList<Integer>();
    private String bountyId = "";
    private int bountyProgress;
    private boolean bountyClaimed;
    private int killsToday;
    private int playMinutesToday;
    private String lastActivityDate = "";
    private int activityFromKills;
    private int activityFromOnline;
    private int abyssUsedToday;
    private boolean dirty;

    // —— covenant / talent / crystal cash ——
    private String covenant = "none";
    private String covenantChosenAt = "";
    private int talentPointsEarned;
    private int talentPointsSpent;
    private final List<String> talentNodes = new ArrayList<String>();
    private String talentFreeResetDate = "";
    private int talentFreeResetsUsed;
    private int crystalCash;
    private int emberLevel = 10;

    public int getCoin() { return coin; }
    public void setCoin(int coin) { this.coin = Math.max(0, coin); dirty = true; }
    public void addCoin(int amount) {
        if (amount == 0) return;
        this.coin = Math.max(0, this.coin + amount);
        dirty = true;
    }
    public boolean takeCoin(int amount) {
        if (amount <= 0) return true;
        if (coin < amount) return false;
        coin -= amount; dirty = true; return true;
    }

    public String getLastSignDate() { return lastSignDate; }
    public void setLastSignDate(String v) { lastSignDate = v == null ? "" : v; dirty = true; }

    public int getActivity() { return activity; }
    public void setActivity(int activity) {
        this.activity = Math.max(0, Math.min(100, activity)); dirty = true;
    }
    public void addActivity(int points) {
        if (points <= 0) return;
        setActivity(this.activity + points);
    }

    public List<Integer> getActivityClaimed() { return activityClaimed; }
    public void setActivityClaimed(List<Integer> claimed) {
        activityClaimed.clear();
        if (claimed != null) activityClaimed.addAll(claimed);
        dirty = true;
    }
    public boolean hasClaimedThreshold(int threshold) {
        return activityClaimed.contains(Integer.valueOf(threshold));
    }
    public void addClaimedThreshold(int threshold) {
        if (!activityClaimed.contains(Integer.valueOf(threshold))) {
            activityClaimed.add(Integer.valueOf(threshold)); dirty = true;
        }
    }

    public String getBountyId() { return bountyId; }
    public void setBountyId(String v) { bountyId = v == null ? "" : v; dirty = true; }

    public int getBountyProgress() { return bountyProgress; }
    public void setBountyProgress(int v) { bountyProgress = Math.max(0, v); dirty = true; }
    public void addBountyProgress(int n) { if (n > 0) { bountyProgress += n; dirty = true; } }

    public boolean isBountyClaimed() { return bountyClaimed; }
    public void setBountyClaimed(boolean v) { bountyClaimed = v; dirty = true; }

    public int getKillsToday() { return killsToday; }
    public void setKillsToday(int v) { killsToday = Math.max(0, v); dirty = true; }
    public void addKillToday() { killsToday++; dirty = true; }

    public int getPlayMinutesToday() { return playMinutesToday; }
    public void setPlayMinutesToday(int v) { playMinutesToday = Math.max(0, v); dirty = true; }
    public void addPlayMinute() { playMinutesToday++; dirty = true; }

    public String getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(String v) { lastActivityDate = v == null ? "" : v; dirty = true; }

    public int getActivityFromKills() { return activityFromKills; }
    public void setActivityFromKills(int v) { activityFromKills = Math.max(0, v); dirty = true; }

    public int getActivityFromOnline() { return activityFromOnline; }
    public void setActivityFromOnline(int v) { activityFromOnline = Math.max(0, v); dirty = true; }

    public boolean isDirty() { return dirty; }
    public void markClean() { dirty = false; }
    public int getAbyssUsedToday() { return abyssUsedToday; }
    public void setAbyssUsedToday(int v) { abyssUsedToday = Math.max(0, v); dirty = true; }
    public void markAbyssUsed() { abyssUsedToday++; dirty = true; }

    public void markDirty() { dirty = true; }

    public String getCovenant() { return covenant == null || covenant.isEmpty() ? "none" : covenant; }
    public void setCovenant(String v) {
        if (v == null || v.isEmpty()) covenant = "none";
        else covenant = v.toLowerCase();
        dirty = true;
    }
    public boolean hasCovenant() {
        String c = getCovenant();
        return c != null && !"none".equalsIgnoreCase(c) && !c.isEmpty();
    }

    public String getCovenantChosenAt() { return covenantChosenAt; }
    public void setCovenantChosenAt(String v) { covenantChosenAt = v == null ? "" : v; dirty = true; }

    public int getTalentPointsEarned() { return talentPointsEarned; }
    public void setTalentPointsEarned(int v) { talentPointsEarned = Math.max(0, v); dirty = true; }
    public void addTalentPointsEarned(int n) {
        if (n == 0) return;
        talentPointsEarned = Math.max(0, talentPointsEarned + n);
        dirty = true;
    }

    public int getTalentPointsSpent() { return talentPointsSpent; }
    public void setTalentPointsSpent(int v) { talentPointsSpent = Math.max(0, v); dirty = true; }

    public int getTalentPointsAvailable() {
        return Math.max(0, talentPointsEarned - talentPointsSpent);
    }

    public List<String> getTalentNodes() { return talentNodes; }
    public void setTalentNodes(List<String> nodes) {
        talentNodes.clear();
        if (nodes != null) {
            for (String n : nodes) {
                if (n != null && !n.isEmpty()) talentNodes.add(n);
            }
        }
        dirty = true;
    }
    public boolean hasTalentNode(String nodeId) {
        return nodeId != null && talentNodes.contains(nodeId);
    }
    public void addTalentNode(String nodeId) {
        if (nodeId == null || nodeId.isEmpty()) return;
        if (!talentNodes.contains(nodeId)) {
            talentNodes.add(nodeId);
            dirty = true;
        }
    }
    public void clearTalentNodes() {
        if (!talentNodes.isEmpty()) {
            talentNodes.clear();
            dirty = true;
        }
    }

    public String getTalentFreeResetDate() { return talentFreeResetDate; }
    public void setTalentFreeResetDate(String v) { talentFreeResetDate = v == null ? "" : v; dirty = true; }

    public int getTalentFreeResetsUsed() { return talentFreeResetsUsed; }
    public void setTalentFreeResetsUsed(int v) { talentFreeResetsUsed = Math.max(0, v); dirty = true; }

    public int getCrystalCash() { return crystalCash; }
    public void setCrystalCash(int v) { crystalCash = Math.max(0, v); dirty = true; }
    public void addCrystalCash(int amount) {
        if (amount == 0) return;
        crystalCash = Math.max(0, crystalCash + amount);
        dirty = true;
    }
    public boolean takeCrystalCash(int amount) {
        if (amount <= 0) return true;
        if (crystalCash < amount) return false;
        crystalCash -= amount;
        dirty = true;
        return true;
    }

    public int getEmberLevel() { return emberLevel; }
    public void setEmberLevel(int v) { emberLevel = Math.max(1, v); dirty = true; }

    // —— cash shop / monthly / daily tickets (DESIGN-ember-cash-monthly) ——
    private boolean monthlyCard;
    private String monthlyExpireDate = "";
    private String monthlyLastGrantDate = "";
    private int dailyTicketsGranted;
    private int dailyTicketsBought;
    private int dailyEntriesUsed;
    private String dailyCashResetDate = "";
    private boolean dailyFreeGranted;

    public boolean isMonthlyCard() { return monthlyCard; }
    public void setMonthlyCard(boolean v) { monthlyCard = v; dirty = true; }

    public String getMonthlyExpireDate() { return monthlyExpireDate; }
    public void setMonthlyExpireDate(String v) { monthlyExpireDate = v == null ? "" : v; dirty = true; }

    public String getMonthlyLastGrantDate() { return monthlyLastGrantDate; }
    public void setMonthlyLastGrantDate(String v) { monthlyLastGrantDate = v == null ? "" : v; dirty = true; }

    public int getDailyTicketsGranted() { return dailyTicketsGranted; }
    public void setDailyTicketsGranted(int v) { dailyTicketsGranted = Math.max(0, v); dirty = true; }

    public int getDailyTicketsBought() { return dailyTicketsBought; }
    public void setDailyTicketsBought(int v) { dailyTicketsBought = Math.max(0, v); dirty = true; }

    public int getDailyEntriesUsed() { return dailyEntriesUsed; }
    public void setDailyEntriesUsed(int v) { dailyEntriesUsed = Math.max(0, v); dirty = true; }

    public String getDailyCashResetDate() { return dailyCashResetDate; }
    public void setDailyCashResetDate(String v) { dailyCashResetDate = v == null ? "" : v; dirty = true; }

    public boolean isDailyFreeGranted() { return dailyFreeGranted; }
    public void setDailyFreeGranted(boolean v) { dailyFreeGranted = v; dirty = true; }

    // —— friends / mentor / settings ——
    private final List<String> friends = new ArrayList<String>();
    private final List<String> pendingIn = new ArrayList<String>();
    private final List<String> pendingOut = new ArrayList<String>();
    private String mentorName = "";
    private String mentorPending = "";
    private boolean settingsSound = true;
    private boolean settingsTip = true;
    private boolean settingsPrivacy = false;

    public List<String> getFriends() { return friends; }
    public void setFriends(List<String> list) {
        friends.clear();
        if (list != null) {
            for (String s : list) {
                if (s != null && !s.isEmpty()) friends.add(s);
            }
        }
        dirty = true;
    }

    public List<String> getPendingIn() { return pendingIn; }
    public void setPendingIn(List<String> list) {
        pendingIn.clear();
        if (list != null) {
            for (String s : list) {
                if (s != null && !s.isEmpty()) pendingIn.add(s);
            }
        }
        dirty = true;
    }

    public List<String> getPendingOut() { return pendingOut; }
    public void setPendingOut(List<String> list) {
        pendingOut.clear();
        if (list != null) {
            for (String s : list) {
                if (s != null && !s.isEmpty()) pendingOut.add(s);
            }
        }
        dirty = true;
    }

    public String getMentorName() { return mentorName == null ? "" : mentorName; }
    public void setMentorName(String v) { mentorName = v == null ? "" : v; dirty = true; }

    public String getMentorPending() { return mentorPending == null ? "" : mentorPending; }
    public void setMentorPending(String v) { mentorPending = v == null ? "" : v; dirty = true; }

    public boolean isSettingsSound() { return settingsSound; }
    public void setSettingsSound(boolean v) { settingsSound = v; dirty = true; }

    public boolean isSettingsTip() { return settingsTip; }
    public void setSettingsTip(boolean v) { settingsTip = v; dirty = true; }

    public boolean isSettingsPrivacy() { return settingsPrivacy; }
    public void setSettingsPrivacy(boolean v) { settingsPrivacy = v; dirty = true; }

    // —— ladder / power_score (display only) ——
    private int powerScore;
    private int abyssBest;
    private int abyssBestWeek;
    private String abyssWeekId = "";
    private int weeklyBestSec; // 0 = none; lower is better
    private String lastKnownName = "";

    public int getPowerScore() { return powerScore; }
    public void setPowerScore(int v) { powerScore = Math.max(0, v); dirty = true; }

    public int getAbyssBest() { return abyssBest; }
    public void setAbyssBest(int v) { abyssBest = Math.max(0, v); dirty = true; }

    public int getAbyssBestWeek() { return abyssBestWeek; }
    public void setAbyssBestWeek(int v) { abyssBestWeek = Math.max(0, v); dirty = true; }

    public String getAbyssWeekId() { return abyssWeekId == null ? "" : abyssWeekId; }
    public void setAbyssWeekId(String v) { abyssWeekId = v == null ? "" : v; dirty = true; }

    public int getWeeklyBestSec() { return weeklyBestSec; }
    public void setWeeklyBestSec(int v) { weeklyBestSec = Math.max(0, v); dirty = true; }

    public String getLastKnownName() { return lastKnownName == null ? "" : lastKnownName; }
    public void setLastKnownName(String v) { lastKnownName = v == null ? "" : v; dirty = true; }

    // —— pets / 使魔 (cosmetic) ——
    private final List<String> petsUnlocked = new ArrayList<String>();
    private String activePet = "";

    public List<String> getPetsUnlocked() { return petsUnlocked; }
    public void setPetsUnlocked(List<String> list) {
        petsUnlocked.clear();
        if (list != null) {
            for (String s : list) {
                if (s != null && !s.isEmpty() && !petsUnlocked.contains(s)) petsUnlocked.add(s);
            }
        }
        dirty = true;
    }
    public boolean isPetUnlocked(String id) {
        return id != null && petsUnlocked.contains(id);
    }
    public void unlockPet(String id) {
        if (id == null || id.isEmpty()) return;
        if (!petsUnlocked.contains(id)) {
            petsUnlocked.add(id);
            dirty = true;
        }
    }

    public String getActivePet() { return activePet == null ? "" : activePet; }
    public void setActivePet(String v) { activePet = v == null ? "" : v; dirty = true; }

    /** Record abyss floor progress; updates historical + current Asia/Shanghai week best. */
    public void recordAbyssFloor(int floor) {
        if (floor <= 0) return;
        if (floor > abyssBest) abyssBest = floor;
        String week = DailyService.weekId();
        if (week == null) week = "";
        if (!week.equals(abyssWeekId)) {
            abyssWeekId = week;
            abyssBestWeek = floor;
        } else if (floor > abyssBestWeek) {
            abyssBestWeek = floor;
        }
        dirty = true;
    }

    /** Effective abyss value for current week board (0 if week rolled). */
    public int effectiveAbyssWeek() {
        String week = DailyService.weekId();
        if (week != null && week.equals(getAbyssWeekId())) return abyssBestWeek;
        return 0;
    }

    /** Record weekly dungeon clear time (seconds); lower wins; 0 ignored. */
    public void recordWeeklyBestSec(int sec) {
        if (sec <= 0) return;
        if (weeklyBestSec <= 0 || sec < weeklyBestSec) {
            weeklyBestSec = sec;
            dirty = true;
        }
    }

    /** Refund spent points into available pool and clear unlocked nodes. */
    public void clearTalentAllocation() {
        talentPointsSpent = 0;
        talentNodes.clear();
        dirty = true;
    }

    public void resetDaily(String today) {
        activity = 0;
        activityClaimed.clear();
        bountyId = "";
        bountyProgress = 0;
        bountyClaimed = false;
        killsToday = 0;
        playMinutesToday = 0;
        activityFromKills = 0;
        activityFromOnline = 0;
        abyssUsedToday = 0;
        lastActivityDate = today;
        dirty = true;
    }
}
