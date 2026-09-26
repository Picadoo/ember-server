package town.sunshine.corerpg;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import town.sunshine.corerpg.storage.MysqlStorage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class PlayerDataStore {
    private final JavaPlugin plugin;
    private final File playersDir;
    private final Map<UUID, PlayerData> cache = new ConcurrentHashMap<UUID, PlayerData>();
    private int startingCoin;
    private TalentService talentService;

    public PlayerDataStore(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playersDir = new File(plugin.getDataFolder(), "players");
        if (!playersDir.exists()) playersDir.mkdirs();
        this.startingCoin = plugin.getConfig().getInt("coin.starting", 0);
    }

    public void setStartingCoin(int startingCoin) { this.startingCoin = startingCoin; }

    public void setTalentService(TalentService talentService) {
        this.talentService = talentService;
    }

    public PlayerData get(UUID uuid) {
        PlayerData data = cache.get(uuid);
        if (data == null) {
            data = load(uuid);
            cache.put(uuid, data);
        }
        ensureDaily(data);
        seedTalentIfNeeded(data);
        return data;
    }

    public Collection<UUID> cachedIds() { return cache.keySet(); }

    public void ensureDaily(PlayerData data) {
        String today = DailyService.today();
        if (!today.equals(data.getLastActivityDate())) {
            data.resetDaily(today);
        }
    }

    /**
     * On first join / earned==0: seed from talent.yml formula or starting_talent_points.
     * earned = max(existing, (ember_level - level_points_from + 1) * points_per_level),
     * capped by max_spendable; fallback min(max_spendable, starting_talent_points).
     */
    public void seedTalentIfNeeded(PlayerData data) {
        if (data.getTalentPointsEarned() > 0) return;
        int seed = 5;
        int cap = 20;
        if (talentService != null) {
            seed = talentService.computeStartingEarned(data.getEmberLevel());
            cap = talentService.getMaxSpendablePoints();
        } else {
            // config fallback before TalentService wired
            seed = plugin.getConfig().getInt("talent.starting_talent_points", 5);
        }
        if (seed <= 0) seed = 5;
        data.setTalentPointsEarned(Math.min(cap, seed));
    }


    /** UUIDs present as players/*.yml or cr_players (skips non-UUID names). */
    public java.util.List<UUID> listAllUuids() {
        MysqlStorage mysql = mysqlOrNull();
        if (mysql != null) {
            try {
                return mysql.listPlayerUuids();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "listAllUuids mysql failed", e);
            }
        }
        java.util.List<UUID> out = new java.util.ArrayList<UUID>();
        File[] files = playersDir.listFiles();
        if (files == null) return out;
        for (File f : files) {
            String n = f.getName();
            if (!n.endsWith(".yml")) continue;
            String base = n.substring(0, n.length() - 4);
            if (base.startsWith("_")) continue;
            try { out.add(UUID.fromString(base)); } catch (IllegalArgumentException ignored) {}
        }
        return out;
    }

    /** Peek cache without loading from disk. */
    public PlayerData peek(UUID uuid) { return cache.get(uuid); }

    public PlayerData load(UUID uuid) {
        PlayerData data = new PlayerData();
        FileConfiguration yaml = null;
        MysqlStorage mysql = mysqlOrNull();
        if (mysql != null) {
            try {
                String raw = mysql.loadPlayerYaml(uuid);
                if (raw != null && !raw.isEmpty()) {
                    YamlConfiguration y = new YamlConfiguration();
                    y.loadFromString(raw);
                    yaml = y;
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "MySQL load player " + uuid, e);
            } catch (InvalidConfigurationException e) {
                plugin.getLogger().log(Level.WARNING, "Invalid player yaml in MySQL " + uuid, e);
            }
        }
        if (yaml == null) {
            File file = fileFor(uuid);
            if (!file.exists()) {
                data.setCoin(startingCoin);
                data.setLastActivityDate(DailyService.today());
                data.setCovenant("none");
                data.setEmberLevel(10);
                data.markDirty();
                return data;
            }
            yaml = YamlConfiguration.loadConfiguration(file);
        }
        data.setCoin(yaml.getInt("coin", startingCoin));
        data.setLastSignDate(yaml.getString("lastSignDate", ""));
        data.setActivity(yaml.getInt("activity", 0));
        List<Integer> claimed = new ArrayList<Integer>();
        List<?> raw = yaml.getList("activityClaimed");
        if (raw != null) {
            for (Object o : raw) {
                if (o instanceof Number) claimed.add(Integer.valueOf(((Number) o).intValue()));
                else if (o != null) {
                    try { claimed.add(Integer.valueOf(Integer.parseInt(String.valueOf(o)))); }
                    catch (NumberFormatException ignored) {}
                }
            }
        }
        data.setActivityClaimed(claimed);
        data.setBountyId(yaml.getString("bountyId", ""));
        data.setBountyProgress(yaml.getInt("bountyProgress", 0));
        data.setBountyClaimed(yaml.getBoolean("bountyClaimed", false));
        data.setKillsToday(yaml.getInt("killsToday", 0));
        int play = yaml.getInt("playMinutesToday", -1);
        if (play < 0) play = yaml.getInt("onlineMinutesToday", 0);
        data.setPlayMinutesToday(play);
        String last = yaml.getString("lastActivityDate", null);
        if (last == null || last.isEmpty()) last = yaml.getString("lastActivityResetDate", "");
        data.setLastActivityDate(last);
        data.setActivityFromKills(yaml.getInt("activityFromKills", yaml.getInt("killActivityToday", 0)));
        data.setActivityFromOnline(yaml.getInt("activityFromOnline", yaml.getInt("onlineActivityGranted", 0)));
        data.setAbyssUsedToday(yaml.getInt("abyssUsedToday", 0));
        data.setCalamityChestDate(yaml.getString("calamityChestDate", ""));
        data.setRaidRingWeek(yaml.getString("raidRingWeek", ""));

        data.setCovenant(yaml.getString("covenant", "none"));
        data.setCovenantChosenAt(yaml.getString("covenantChosenAt", ""));
        data.setTalentPointsEarned(yaml.getInt("talentPointsEarned", 0));
        data.setTalentPointsSpent(yaml.getInt("talentPointsSpent", 0));
        List<String> nodes = new ArrayList<String>();
        List<?> rawNodes = yaml.getList("talentNodes");
        if (rawNodes != null) {
            for (Object o : rawNodes) {
                if (o != null) {
                    String s = String.valueOf(o).trim();
                    if (!s.isEmpty()) nodes.add(s);
                }
            }
        }
        data.setTalentNodes(nodes);
        data.setTalentFreeResetDate(yaml.getString("talentFreeResetDate", ""));
        data.setTalentFreeResetsUsed(yaml.getInt("talentFreeResetsUsed", 0));
        // Prefer contract key ember_crystal_cash; fallback legacy crystalCash
        int cash;
        if (yaml.contains("ember_crystal_cash")) {
            cash = yaml.getInt("ember_crystal_cash", 0);
        } else {
            cash = yaml.getInt("crystalCash", 0);
        }
        data.setCrystalCash(cash);
        data.setEmberLevel(yaml.getInt("emberLevel", yaml.getInt("ember_level", 10)));

        data.setMonthlyCard(yaml.getBoolean("monthlyCard", false));
        data.setSeasonPassPaid(yaml.getBoolean("seasonPassPaid", false));
        data.setMonthlyExpireDate(yaml.getString("monthlyExpireDate", ""));
        data.setMonthlyLastGrantDate(yaml.getString("monthlyLastGrantDate", ""));
        data.setDailyTicketsGranted(yaml.getInt("dailyTicketsGranted", 0));
        data.setDailyTicketsBought(yaml.getInt("dailyTicketsBought", 0));
        data.setDailyEntriesUsed(yaml.getInt("dailyEntriesUsed", 0));
        data.setDailyCashResetDate(yaml.getString("dailyCashResetDate", ""));
        data.setDailyFreeGranted(yaml.getBoolean("dailyFreeGranted", false));
        data.setWeeklyTicketGrantWeekId(yaml.getString("weeklyTicketGrantWeekId", ""));
        data.setAbyssTicketGrantDate(yaml.getString("abyssTicketGrantDate", ""));
        data.setWeeklyTicketsGranted(yaml.getInt("weeklyTicketsGranted", 0));
        data.setWeeklyTicketsBought(yaml.getInt("weeklyTicketsBought", 0));
        data.setWeeklyTicketShopWeekId(yaml.getString("weeklyTicketShopWeekId", ""));
        data.setVipTier(yaml.getInt("vipTier", 0));
        data.setVipDailyClaimDate(yaml.getString("vipDailyClaimDate", ""));
        data.setPassFreeClaimDate(yaml.getString("passFreeClaimDate", ""));
        data.setKillLevelsDate(yaml.getString("killLevelsDate", ""));
        data.setKillLevelsToday(yaml.getInt("killLevelsToday", 0));
        data.setPassXp(yaml.getInt("passXp", 0));
        data.setPassXpDate(yaml.getString("passXpDate", ""));
        data.setPassXpToday(yaml.getInt("passXpToday", 0));

        data.setFriends(stringList(yaml, "friends"));
        data.setPendingIn(stringList(yaml, "pendingIn"));
        data.setPendingOut(stringList(yaml, "pendingOut"));
        data.setMentorName(yaml.getString("mentorName", ""));
        data.setMentorPending(yaml.getString("mentorPending", ""));
        data.setSettingsSound(yaml.getBoolean("settingsSound", true));
        data.setSettingsTip(yaml.getBoolean("settingsTip", true));
        data.setSettingsPrivacy(yaml.getBoolean("settingsPrivacy", false));

        data.setPowerScore(yaml.getInt("powerScore", 0));
        data.setAbyssBest(yaml.getInt("abyssBest", 0));
        data.setAbyssBestWeek(yaml.getInt("abyssBestWeek", 0));
        data.setAbyssWeekId(yaml.getString("abyssWeekId", ""));
        data.setWeeklyBestSec(yaml.getInt("weeklyBestSec", 0));
        data.setLastKnownName(yaml.getString("lastKnownName", ""));

        data.setPetsUnlocked(stringList(yaml, "petsUnlocked"));
        data.setActivePet(yaml.getString("activePet", ""));
        data.setPetLevels(intMap(yaml, "petLevels"));

        data.setGuildId(yaml.getString("guildId", ""));
        data.setGuildDonateDate(yaml.getString("guildDonateDate", ""));
        data.setGuildDonateToday(yaml.getInt("guildDonateToday", 0));
        data.setGuildBossWeekId(yaml.getString("guildBossWeekId", ""));
        data.setGuildBossUsed(yaml.getInt("guildBossUsed", 0));

        data.setArenaPoints(yaml.getInt("arenaPoints", 0));
        data.setArenaWins(yaml.getInt("arenaWins", 0));
        data.setArenaLosses(yaml.getInt("arenaLosses", 0));
        data.setArenaDailyClaimDate(yaml.getString("arenaDailyClaimDate", ""));
        data.setArenaQueueMode(yaml.getString("arenaQueueMode", ""));

        // material warehouse (YAML keys; MySQL cr_warehouse overrides below)
        data.setWarehouseSlotsUnlocked(yaml.getInt("warehouseSlotsUnlocked", 8));
        String whJson = yaml.getString("warehouseJson", null);
        if (whJson == null || whJson.isEmpty()) {
            // legacy list form warehouseSlots: [{niId, amount}, ...]
            List<?> rawSlots = yaml.getList("warehouseSlots");
            if (rawSlots != null && !rawSlots.isEmpty()) {
                java.util.ArrayList<PlayerData.WarehouseSlot> slots = new java.util.ArrayList<PlayerData.WarehouseSlot>();
                for (Object o : rawSlots) {
                    if (!(o instanceof Map)) continue;
                    Map<?, ?> m = (Map<?, ?>) o;
                    Object idObj = m.containsKey("niId") ? m.get("niId") : m.get("id");
                    String niId = idObj == null ? "" : String.valueOf(idObj);
                    long amt = 0L;
                    Object a = m.get("amount");
                    if (a instanceof Number) amt = ((Number) a).longValue();
                    else if (a != null) {
                        try { amt = Long.parseLong(String.valueOf(a)); } catch (NumberFormatException ignored) {}
                    }
                    if (niId != null && !niId.isEmpty() && amt > 0L) {
                        slots.add(new PlayerData.WarehouseSlot(niId, amt));
                    }
                }
                data.setWarehouseSlots(slots);
            } else {
                data.setWarehouseJson("[]");
            }
        } else {
            data.setWarehouseJson(whJson);
        }


        // Prefer dedicated cr_warehouse when MySQL active
        if (mysql != null) {
            try {
                MysqlStorage.WarehouseRow row = mysql.loadWarehouse(uuid);
                if (row != null) {
                    data.setWarehouseSlotsUnlocked(row.slotsUnlocked);
                    data.setWarehouseJson(row.slotsJson == null ? "[]" : row.slotsJson);
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "MySQL load warehouse " + uuid, e);
            }
        }

        data.markClean();
        ensureDaily(data);
        return data;
    }

    public void save(UUID uuid, PlayerData data) {
        if (data == null) return;
        FileConfiguration yaml = new YamlConfiguration();
        yaml.set("coin", data.getCoin());
        yaml.set("lastSignDate", data.getLastSignDate());
        yaml.set("activity", data.getActivity());
        yaml.set("activityClaimed", new ArrayList<Integer>(data.getActivityClaimed()));
        yaml.set("bountyId", data.getBountyId());
        yaml.set("bountyProgress", data.getBountyProgress());
        yaml.set("bountyClaimed", data.isBountyClaimed());
        yaml.set("killsToday", data.getKillsToday());
        yaml.set("playMinutesToday", data.getPlayMinutesToday());
        yaml.set("lastActivityDate", data.getLastActivityDate());
        yaml.set("activityFromKills", data.getActivityFromKills());
        yaml.set("activityFromOnline", data.getActivityFromOnline());
        yaml.set("abyssUsedToday", data.getAbyssUsedToday());
        yaml.set("calamityChestDate", data.getCalamityChestDate());
        yaml.set("raidRingWeek", data.getRaidRingWeek());

        yaml.set("covenant", data.getCovenant());
        yaml.set("covenantChosenAt", data.getCovenantChosenAt());
        yaml.set("talentPointsEarned", data.getTalentPointsEarned());
        yaml.set("talentPointsSpent", data.getTalentPointsSpent());
        yaml.set("talentNodes", new ArrayList<String>(data.getTalentNodes()));
        yaml.set("talentFreeResetDate", data.getTalentFreeResetDate());
        yaml.set("talentFreeResetsUsed", data.getTalentFreeResetsUsed());
        // Write contract key; dual-write legacy for one season
        yaml.set("ember_crystal_cash", data.getCrystalCash());
        yaml.set("crystalCash", data.getCrystalCash());
        yaml.set("emberLevel", data.getEmberLevel());
        yaml.set("monthlyCard", data.isMonthlyCard());
        yaml.set("seasonPassPaid", Boolean.valueOf(data.isSeasonPassPaid()));
        yaml.set("monthlyExpireDate", data.getMonthlyExpireDate());
        yaml.set("monthlyLastGrantDate", data.getMonthlyLastGrantDate());
        yaml.set("dailyTicketsGranted", data.getDailyTicketsGranted());
        yaml.set("dailyTicketsBought", data.getDailyTicketsBought());
        yaml.set("dailyEntriesUsed", data.getDailyEntriesUsed());
        yaml.set("dailyCashResetDate", data.getDailyCashResetDate());
        yaml.set("dailyFreeGranted", data.isDailyFreeGranted());
        yaml.set("weeklyTicketGrantWeekId", data.getWeeklyTicketGrantWeekId());
        yaml.set("abyssTicketGrantDate", data.getAbyssTicketGrantDate());
        yaml.set("weeklyTicketsGranted", data.getWeeklyTicketsGranted());
        yaml.set("weeklyTicketsBought", data.getWeeklyTicketsBought());
        yaml.set("weeklyTicketShopWeekId", data.getWeeklyTicketShopWeekId());
        yaml.set("vipTier", Integer.valueOf(data.getVipTier()));
        yaml.set("vipDailyClaimDate", data.getVipDailyClaimDate());
        yaml.set("passFreeClaimDate", data.getPassFreeClaimDate());
        yaml.set("killLevelsDate", data.getKillLevelsDate());
        yaml.set("killLevelsToday", data.getKillLevelsToday());
        yaml.set("passXp", data.getPassXp());
        yaml.set("passXpDate", data.getPassXpDate());
        yaml.set("passXpToday", data.getPassXpToday());
        yaml.set("friends", new ArrayList<String>(data.getFriends()));
        yaml.set("pendingIn", new ArrayList<String>(data.getPendingIn()));
        yaml.set("pendingOut", new ArrayList<String>(data.getPendingOut()));
        yaml.set("mentorName", data.getMentorName());
        yaml.set("mentorPending", data.getMentorPending());
        yaml.set("settingsSound", Boolean.valueOf(data.isSettingsSound()));
        yaml.set("settingsTip", Boolean.valueOf(data.isSettingsTip()));
        yaml.set("settingsPrivacy", Boolean.valueOf(data.isSettingsPrivacy()));
        yaml.set("powerScore", Integer.valueOf(data.getPowerScore()));
        yaml.set("abyssBest", Integer.valueOf(data.getAbyssBest()));
        yaml.set("abyssBestWeek", Integer.valueOf(data.getAbyssBestWeek()));
        yaml.set("abyssWeekId", data.getAbyssWeekId());
        yaml.set("weeklyBestSec", Integer.valueOf(data.getWeeklyBestSec()));
        yaml.set("lastKnownName", data.getLastKnownName());
        yaml.set("petsUnlocked", new ArrayList<String>(data.getPetsUnlocked()));
        yaml.set("activePet", data.getActivePet());
        yaml.set("petLevels", new LinkedHashMap<String, Integer>(data.getPetLevels()));
        yaml.set("guildId", data.getGuildId());
        yaml.set("guildDonateDate", data.getGuildDonateDate());
        yaml.set("guildDonateToday", Integer.valueOf(data.getGuildDonateToday()));
        yaml.set("guildBossWeekId", data.getGuildBossWeekId());
        yaml.set("guildBossUsed", Integer.valueOf(data.getGuildBossUsed()));
        yaml.set("arenaPoints", Integer.valueOf(data.getArenaPoints()));
        yaml.set("arenaWins", Integer.valueOf(data.getArenaWins()));
        yaml.set("arenaLosses", Integer.valueOf(data.getArenaLosses()));
        yaml.set("arenaDailyClaimDate", data.getArenaDailyClaimDate());
        yaml.set("arenaQueueMode", data.getArenaQueueMode());
        yaml.set("warehouseSlotsUnlocked", Integer.valueOf(data.getWarehouseSlotsUnlocked()));
        yaml.set("warehouseJson", data.getWarehouseJson());
        MysqlStorage mysql = mysqlOrNull();
        if (mysql != null) {
            try {
                String name = data.getLastKnownName();
                mysql.savePlayerYaml(uuid, name, yaml.saveToString());
                mysql.saveWarehouse(uuid, data.getWarehouseSlotsUnlocked(), data.getWarehouseJson());
                data.markClean();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to save player MySQL " + uuid, e);
            }
            return;
        }
        try {
            yaml.save(fileFor(uuid));
            data.markClean();
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save player " + uuid, e);
        }
    }

    public void saveIfDirty(UUID uuid) {
        PlayerData data = cache.get(uuid);
        if (data != null && data.isDirty()) save(uuid, data);
    }

    public void saveAll() {
        for (Map.Entry<UUID, PlayerData> e : cache.entrySet()) {
            if (e.getValue().isDirty()) save(e.getKey(), e.getValue());
        }
    }

    public void unload(UUID uuid) {
        PlayerData data = cache.remove(uuid);
        if (data != null) save(uuid, data);
    }

    public void flushMutation(UUID uuid) {
        PlayerData data = cache.get(uuid);
        if (data != null) save(uuid, data);
    }



    private static Map<String, Integer> intMap(FileConfiguration yaml, String key) {
        Map<String, Integer> out = new LinkedHashMap<String, Integer>();
        ConfigurationSection sec = yaml.getConfigurationSection(key);
        if (sec == null) return out;
        for (String k : sec.getKeys(false)) {
            if (k == null || k.isEmpty()) continue;
            out.put(k, Integer.valueOf(Math.max(1, sec.getInt(k, 1))));
        }
        return out;
    }

    private static List<String> stringList(FileConfiguration yaml, String key) {
        List<String> out = new ArrayList<String>();
        List<?> raw = yaml.getList(key);
        if (raw == null) return out;
        for (Object o : raw) {
            if (o == null) continue;
            String s = String.valueOf(o).trim();
            if (!s.isEmpty()) out.add(s);
        }
        return out;
    }

    private MysqlStorage mysqlOrNull() {
        if (plugin instanceof CoreRpgPlugin) {
            MysqlStorage m = ((CoreRpgPlugin) plugin).getMysqlStorage();
            if (m != null && m.isActive()) return m;
        }
        return null;
    }

    /** YAML files on disk (for migrate-yaml-to-mysql). */
    public File getPlayersDir() { return playersDir; }

    private File fileFor(UUID uuid) {
        return new File(playersDir, uuid.toString() + ".yml");
    }
}
