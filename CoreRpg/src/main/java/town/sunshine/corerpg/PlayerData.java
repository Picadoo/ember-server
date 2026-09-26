package town.sunshine.corerpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    /** Season pass paid track (shop SKU pass_unlock). */
    private boolean seasonPassPaid;

    public boolean isSeasonPassPaid() { return seasonPassPaid; }
    public void setSeasonPassPaid(boolean v) { seasonPassPaid = v; dirty = true; }

    /** ISO week id last weekly free ticket grant (DailyService.weekId()). */
    private String weeklyTicketGrantWeekId = "";
    // 1.8.1: weekly free raid ticket + starter blade re-issue date
    private String raidTicketGrantWeekId = "";
    private String lootWeekMarks = ""; // "key=week;key=week"
    public String getLootWeekMarks() { return lootWeekMarks == null ? "" : lootWeekMarks; }
    public void setLootWeekMarks(String v) { lootWeekMarks = v == null ? "" : v; dirty = true; }
    public void addLootWeekMark(String key, String week) {
        StringBuilder sb = new StringBuilder();
        for (String part : getLootWeekMarks().split(";")) if (!part.isEmpty() && !part.startsWith(key + "=")) sb.append(part).append(';');
        sb.append(key).append('=').append(week);
        setLootWeekMarks(sb.toString());
    }
    private String starterReissueDate = "";
    public String getRaidTicketGrantWeekId() { return raidTicketGrantWeekId == null ? "" : raidTicketGrantWeekId; }
    public void setRaidTicketGrantWeekId(String v) { raidTicketGrantWeekId = v == null ? "" : v; dirty = true; }
    public String getStarterReissueDate() { return starterReissueDate == null ? "" : starterReissueDate; }
    public void setStarterReissueDate(String v) { starterReissueDate = v == null ? "" : v; dirty = true; }
    /** yyyy-MM-dd last abyss free ticket grant (DailyService.today()). */
    private String abyssTicketGrantDate = "";

    public String getWeeklyTicketGrantWeekId() { return weeklyTicketGrantWeekId == null ? "" : weeklyTicketGrantWeekId; }
    public void setWeeklyTicketGrantWeekId(String v) { weeklyTicketGrantWeekId = v == null ? "" : v; dirty = true; }

    public String getAbyssTicketGrantDate() { return abyssTicketGrantDate == null ? "" : abyssTicketGrantDate; }
    public void setAbyssTicketGrantDate(String v) { abyssTicketGrantDate = v == null ? "" : v; dirty = true; }

    /** Weekly ticket shop counters (reset when weeklyTicketShopWeekId != DailyService.weekId()). */
    private int weeklyTicketsGranted;
    private int weeklyTicketsBought;
    private String weeklyTicketShopWeekId = "";

    public int getWeeklyTicketsGranted() { return weeklyTicketsGranted; }
    public void setWeeklyTicketsGranted(int v) { weeklyTicketsGranted = Math.max(0, v); dirty = true; }

    public int getWeeklyTicketsBought() { return weeklyTicketsBought; }
    public void setWeeklyTicketsBought(int v) { weeklyTicketsBought = Math.max(0, v); dirty = true; }

    public String getWeeklyTicketShopWeekId() { return weeklyTicketShopWeekId == null ? "" : weeklyTicketShopWeekId; }
    public void setWeeklyTicketShopWeekId(String v) { weeklyTicketShopWeekId = v == null ? "" : v; dirty = true; }

    /** Light VIP stub (no LuckPerms). */
    private int vipTier;
    private String vipDailyClaimDate = "";
    /** 1.4.10: last day the free pass-track supply was claimed. */
    private String passFreeClaimDate = "";
    public String getPassFreeClaimDate() { return passFreeClaimDate == null ? "" : passFreeClaimDate; }
    public void setPassFreeClaimDate(String v) { passFreeClaimDate = v == null ? "" : v; dirty = true; }

    /** 1.5.0: MM elite/boss level rewards (daily cap) + season pass XP. */
    private String killLevelsDate = "";
    private int killLevelsToday;
    private int passXp;
    private String passXpDate = "";
    private int passXpToday;
    public String getKillLevelsDate() { return killLevelsDate == null ? "" : killLevelsDate; }
    public void setKillLevelsDate(String v) { killLevelsDate = v == null ? "" : v; dirty = true; }
    public int getKillLevelsToday() { return killLevelsToday; }
    public void setKillLevelsToday(int v) { killLevelsToday = Math.max(0, v); dirty = true; }
    public int getPassXp() { return passXp; }
    public void setPassXp(int v) { passXp = Math.max(0, v); dirty = true; }
    public String getPassXpDate() { return passXpDate == null ? "" : passXpDate; }
    public void setPassXpDate(String v) { passXpDate = v == null ? "" : v; dirty = true; }
    public int getPassXpToday() { return passXpToday; }
    public void setPassXpToday(int v) { passXpToday = Math.max(0, v); dirty = true; }

    /** 1.6.0: ember level XP (progress within current level) + daily caps; pass season/claims; VIP top-up. */
    private int emberXp;
    // 1.8.0 mainline quest: chapter 0 = not started; questDone = whole volume finished
    private int questChapter = 0;
    private int questStep = 0;
    private int questCount = 0;
    private boolean questDone = false;
    public int getQuestChapter() { return questChapter; }
    public void setQuestChapter(int v) { questChapter = v; dirty = true; }
    public int getQuestStep() { return questStep; }
    public void setQuestStep(int v) { questStep = v; dirty = true; }
    public int getQuestCount() { return questCount; }
    public void setQuestCount(int v) { questCount = v; dirty = true; }
    public boolean isQuestDone() { return questDone; }
    public void setQuestDone(boolean v) { questDone = v; dirty = true; }
    private String emberXpDate = "";
    private int emberXpKillToday;
    private int emberXpCombatToday;
    private String passSeasonId = "";
    private int passFreeClaimedLevel;
    private int passPaidClaimedLevel;
    private int vipToppedUp;
    public int getEmberXp() { return emberXp; }
    public void setEmberXp(int v) { emberXp = Math.max(0, v); dirty = true; }
    public String getEmberXpDate() { return emberXpDate == null ? "" : emberXpDate; }
    public void setEmberXpDate(String v) { emberXpDate = v == null ? "" : v; dirty = true; }
    public int getEmberXpKillToday() { return emberXpKillToday; }
    public void setEmberXpKillToday(int v) { emberXpKillToday = Math.max(0, v); dirty = true; }
    public int getEmberXpCombatToday() { return emberXpCombatToday; }
    public void setEmberXpCombatToday(int v) { emberXpCombatToday = Math.max(0, v); dirty = true; }
    public String getPassSeasonId() { return passSeasonId == null ? "" : passSeasonId; }
    public void setPassSeasonId(String v) { passSeasonId = v == null ? "" : v; dirty = true; }
    public int getPassFreeClaimedLevel() { return passFreeClaimedLevel; }
    public void setPassFreeClaimedLevel(int v) { passFreeClaimedLevel = Math.max(0, v); dirty = true; }
    public int getPassPaidClaimedLevel() { return passPaidClaimedLevel; }
    public void setPassPaidClaimedLevel(int v) { passPaidClaimedLevel = Math.max(0, v); dirty = true; }
    public int getVipToppedUp() { return vipToppedUp; }
    public void setVipToppedUp(int v) { vipToppedUp = Math.max(0, v); dirty = true; }

    public int getVipTier() { return vipTier; }
    public void setVipTier(int v) { vipTier = Math.max(0, v); dirty = true; }

    public String getVipDailyClaimDate() { return vipDailyClaimDate == null ? "" : vipDailyClaimDate; }
    public void setVipDailyClaimDate(String v) { vipDailyClaimDate = v == null ? "" : v; dirty = true; }

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
    /** Per-pet level; default 1, max from pet.yml feed.max_level. */
    private final Map<String, Integer> petLevels = new LinkedHashMap<String, Integer>();

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
        if (!petLevels.containsKey(id)) {
            petLevels.put(id, Integer.valueOf(1));
            dirty = true;
        }
    }

    public String getActivePet() { return activePet == null ? "" : activePet; }
    public void setActivePet(String v) { activePet = v == null ? "" : v; dirty = true; }

    public Map<String, Integer> getPetLevels() {
        return Collections.unmodifiableMap(petLevels);
    }
    public void setPetLevels(Map<String, Integer> map) {
        petLevels.clear();
        if (map != null) {
            for (Map.Entry<String, Integer> e : map.entrySet()) {
                if (e.getKey() == null || e.getKey().isEmpty()) continue;
                int lv = e.getValue() == null ? 1 : Math.max(1, e.getValue().intValue());
                petLevels.put(e.getKey(), Integer.valueOf(lv));
            }
        }
        dirty = true;
    }
    /** Default level 1 when unset. */
    public int getPetLevel(String id) {
        if (id == null || id.isEmpty()) return 1;
        Integer v = petLevels.get(id);
        return v == null ? 1 : Math.max(1, v.intValue());
    }
    public void setPetLevel(String id, int level) {
        if (id == null || id.isEmpty()) return;
        petLevels.put(id, Integer.valueOf(Math.max(1, level)));
        dirty = true;
    }

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

    // —— guild / 盟约 ——
    private String guildId = "";
    private String guildDonateDate = "";
    private int guildDonateToday;

    public String getGuildId() { return guildId == null ? "" : guildId; }
    public void setGuildId(String v) { guildId = v == null ? "" : v; dirty = true; }

    public String getGuildDonateDate() { return guildDonateDate == null ? "" : guildDonateDate; }
    public void setGuildDonateDate(String v) { guildDonateDate = v == null ? "" : v; dirty = true; }

    public int getGuildDonateToday() { return guildDonateToday; }
    public void setGuildDonateToday(int v) { guildDonateToday = Math.max(0, v); dirty = true; }

    // —— guild weekly boss ——
    private String guildBossWeekId = "";
    private int guildBossUsed;

    public String getGuildBossWeekId() { return guildBossWeekId == null ? "" : guildBossWeekId; }
    public void setGuildBossWeekId(String v) { guildBossWeekId = v == null ? "" : v; dirty = true; }

    public int getGuildBossUsed() { return guildBossUsed; }
    public void setGuildBossUsed(int v) { guildBossUsed = Math.max(0, v); dirty = true; }

    // —— arena / 竞技 ——
    private int arenaPoints;
    private int arenaWins;
    private int arenaLosses;
    private String arenaDailyClaimDate = "";
    private String arenaQueueMode = "";

    public int getArenaPoints() { return arenaPoints; }
    public void setArenaPoints(int v) { arenaPoints = Math.max(0, v); dirty = true; }

    public int getArenaWins() { return arenaWins; }
    public void setArenaWins(int v) { arenaWins = Math.max(0, v); dirty = true; }

    public int getArenaLosses() { return arenaLosses; }
    public void setArenaLosses(int v) { arenaLosses = Math.max(0, v); dirty = true; }

    public String getArenaDailyClaimDate() { return arenaDailyClaimDate == null ? "" : arenaDailyClaimDate; }
    public void setArenaDailyClaimDate(String v) { arenaDailyClaimDate = v == null ? "" : v; dirty = true; }

    public String getArenaQueueMode() { return arenaQueueMode == null ? "" : arenaQueueMode; }
    public void setArenaQueueMode(String v) { arenaQueueMode = v == null ? "" : v; dirty = true; }

    /** Refund spent points into available pool and clear unlocked nodes. */
    public void clearTalentAllocation() {
        talentPointsSpent = 0;
        talentNodes.clear();
        dirty = true;
    }


    // —— material warehouse / 材料仓 ——
    private int warehouseSlotsUnlocked = 8;
    private final java.util.List<WarehouseSlot> warehouseSlots = new java.util.ArrayList<WarehouseSlot>();

    public static final class WarehouseSlot {
        public String niId = "";
        public long amount;

        public WarehouseSlot() {}
        public WarehouseSlot(String niId, long amount) {
            this.niId = niId == null ? "" : niId;
            this.amount = Math.max(0L, amount);
        }
    }

    public int getWarehouseSlotsUnlocked() { return warehouseSlotsUnlocked; }
    public void setWarehouseSlotsUnlocked(int v) { warehouseSlotsUnlocked = Math.max(0, v); dirty = true; }

    public java.util.List<WarehouseSlot> getWarehouseSlots() { return warehouseSlots; }
    public void setWarehouseSlots(java.util.List<WarehouseSlot> slots) {
        warehouseSlots.clear();
        if (slots != null) {
            for (WarehouseSlot s : slots) {
                if (s == null || s.niId == null || s.niId.isEmpty() || s.amount <= 0L) continue;
                warehouseSlots.add(new WarehouseSlot(s.niId, s.amount));
            }
        }
        dirty = true;
    }

    /** Compact JSON: [{"niId":"...","amount":N},...] */
    public String getWarehouseJson() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < warehouseSlots.size(); i++) {
            WarehouseSlot s = warehouseSlots.get(i);
            if (i > 0) sb.append(',');
            sb.append("{\"niId\":\"").append(escapeJson(s.niId)).append("\",\"amount\":").append(s.amount).append('}');
        }
        sb.append(']');
        return sb.toString();
    }

    public void setWarehouseJson(String json) {
        warehouseSlots.clear();
        if (json == null || json.trim().isEmpty()) { dirty = true; return; }
        String s = json.trim();
        // very small parser for [{...},{...}]
        int i = 0;
        while (i < s.length()) {
            int idKey = s.indexOf("\"niId\"", i);
            if (idKey < 0) idKey = s.indexOf("\"id\"", i);
            if (idKey < 0) break;
            int colon = s.indexOf(':', idKey);
            if (colon < 0) break;
            int q1 = s.indexOf('"', colon + 1);
            if (q1 < 0) break;
            int q2 = s.indexOf('"', q1 + 1);
            if (q2 < 0) break;
            String niId = s.substring(q1 + 1, q2);
            int amtKey = s.indexOf("\"amount\"", q2);
            if (amtKey < 0) { i = q2 + 1; continue; }
            int colon2 = s.indexOf(':', amtKey);
            if (colon2 < 0) break;
            int j = colon2 + 1;
            while (j < s.length() && (s.charAt(j) == ' ' || s.charAt(j) == '\t')) j++;
            int k = j;
            while (k < s.length() && (Character.isDigit(s.charAt(k)))) k++;
            long amount = 0L;
            try { amount = Long.parseLong(s.substring(j, k)); } catch (NumberFormatException ignored) {}
            if (niId != null && !niId.isEmpty() && amount > 0L) {
                warehouseSlots.add(new WarehouseSlot(niId, amount));
            }
            i = Math.max(k, q2 + 1);
        }
        dirty = true;
    }

    private static String escapeJson(String in) {
        if (in == null) return "";
        StringBuilder sb = new StringBuilder(in.length());
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == '\\' || c == '"') sb.append('\\').append(c);
            else if (c == '\n') sb.append("\\n");
            else if (c == '\r') sb.append("\\r");
            else if (c == '\t') sb.append("\\t");
            else sb.append(c);
        }
        return sb.toString();
    }


    // —— calamity daily chest (Shanghai day, yyyy-MM-dd) ——
    private String calamityChestDate = "";

    public String getCalamityChestDate() { return calamityChestDate == null ? "" : calamityChestDate; }
    public void setCalamityChestDate(String v) { calamityChestDate = v == null ? "" : v; dirty = true; }

    // —— raid weekly first-clear ring (DailyService.weekId, e.g. 2026-W37) ——
    private String raidRingWeek = "";

    public String getRaidRingWeek() { return raidRingWeek == null ? "" : raidRingWeek; }
    public void setRaidRingWeek(String v) { raidRingWeek = v == null ? "" : v; dirty = true; }

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
