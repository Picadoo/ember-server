package town.sunshine.corerpg;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Free weekly / abyss dungeon ticket auto-grants (cash.yml weekly/abyss).
 * Daily free tickets stay in CashService; this class is called after them on join.
 */
public final class TicketGrantService {

    private final JavaPlugin plugin;
    private final NiBridge ni;
    private final PlayerDataStore dataStore;

    private int weeklyFreeTickets = 1;
    private String weeklyTicketNiId = "ticket_ember_weekly";
    private int abyssFreeTickets = 1;
    private String abyssTicketNiId = "ticket_ember_abyss";
    private int raidFreeTickets = 1;
    private String raidTicketNiId = "ticket_ember_raid";
    private int eliteFreeTickets = 1;
    private String eliteTicketNiId = "ticket_ember_elite";
    private int eliteHardCap = 1;

    public TicketGrantService(JavaPlugin plugin, NiBridge ni, PlayerDataStore dataStore) {
        this.plugin = plugin;
        this.ni = ni;
        this.dataStore = dataStore;
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "cash.yml");
        if (!file.exists()) {
            plugin.saveResource("cash.yml", false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        InputStream in = plugin.getResource("cash.yml");
        if (in != null) {
            YamlConfiguration def = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            cfg.setDefaults(def);
            cfg.options().copyDefaults(false);
        }
        ConfigurationSection weekly = cfg.getConfigurationSection("weekly");
        if (weekly != null) {
            weeklyFreeTickets = weekly.getInt("free_tickets", 1);
            weeklyTicketNiId = weekly.getString("ticket_ni_id", "ticket_ember_weekly");
        } else {
            weeklyFreeTickets = 1;
            weeklyTicketNiId = "ticket_ember_weekly";
        }
        ConfigurationSection raid = cfg.getConfigurationSection("raid");
        raidFreeTickets = raid != null ? raid.getInt("free_tickets", 1) : 1;
        raidTicketNiId = raid != null ? raid.getString("ticket_ni_id", "ticket_ember_raid") : "ticket_ember_raid";
        ConfigurationSection abyss = cfg.getConfigurationSection("abyss");
        if (abyss != null) {
            abyssFreeTickets = abyss.getInt("free_tickets", 1);
            abyssTicketNiId = abyss.getString("ticket_ni_id", "ticket_ember_abyss");
        } else {
            abyssFreeTickets = 1;
            abyssTicketNiId = "ticket_ember_abyss";
        }
        ConfigurationSection elite = cfg.getConfigurationSection("elite");
        if (elite != null) {
            eliteFreeTickets = elite.getInt("free_tickets", 1);
            eliteTicketNiId = elite.getString("ticket_ni_id", "ticket_ember_elite");
            eliteHardCap = Math.max(1, elite.getInt("hard_cap", 1));
        } else {
            eliteFreeTickets = 1;
            eliteTicketNiId = "ticket_ember_elite";
            eliteHardCap = 1;
        }
        if (ni != null) {
            ni.warnMissingOnceIfAbsent(weeklyTicketNiId);
            ni.warnMissingOnceIfAbsent(abyssTicketNiId);
            ni.warnMissingOnceIfAbsent(eliteTicketNiId);
        }
    }

    public int getWeeklyFreeTickets() { return weeklyFreeTickets; }
    public String getWeeklyTicketNiId() { return weeklyTicketNiId; }
    public int getAbyssFreeTickets() { return abyssFreeTickets; }
    public String getAbyssTicketNiId() { return abyssTicketNiId; }
    public int getEliteFreeTickets() { return eliteFreeTickets; }
    public String getEliteTicketNiId() { return eliteTicketNiId; }

    /**
     * Grant weekly + abyss free tickets if due. Returns parts for a combined [门票] message
     * (e.g. "周票×1", "深渊票×1") — empty if nothing newly given.
     */
    public List<String> grantOnJoin(Player player, PlayerData data) {
        List<String> parts = new ArrayList<String>();
        if (player == null || data == null) return parts;
        int w = grantWeeklyIfNeeded(player, data);
        if (w > 0) parts.add("周票×" + w);
        int a = grantAbyssIfNeeded(player, data);
        if (a > 0) parts.add("深渊票×" + a);
        int rd = grantRaidIfNeeded(player, data);
        if (rd > 0) parts.add("团本票×" + rd);
        int el = grantEliteIfNeeded(player, data);
        if (el > 0) parts.add("精英票×" + el);
        return parts;
    }

    /** @return amount newly given, or 0 if already granted / failed / disabled */
    public int grantWeeklyIfNeeded(Player player, PlayerData data) {
        int want = Math.max(0, weeklyFreeTickets);
        if (want <= 0) return 0;
        String week = DailyService.weekId();
        if (week.equals(data.getWeeklyTicketGrantWeekId())) return 0;
        if (!giveNi(player, weeklyTicketNiId, want)) {
            plugin.getLogger().warning("Failed to give free weekly tickets to " + player.getName()
                    + " — will retry next join/delay");
            return 0;
        }
        // Keep shop week counters in sync with free grant (hard_cap shares weeklyTicketsGranted).
        if (!week.equals(data.getWeeklyTicketShopWeekId())) {
            data.setWeeklyTicketsBought(0);
            data.setWeeklyTicketsGranted(0);
            data.setWeeklyTicketShopWeekId(week);
        }
        data.setWeeklyTicketGrantWeekId(week);
        data.setWeeklyTicketsGranted(data.getWeeklyTicketsGranted() + want);
        return want;
    }

    /** @return amount newly given, or 0 if already granted / failed / disabled */
    public int grantAbyssIfNeeded(Player player, PlayerData data) {
        int want = Math.max(0, abyssFreeTickets);
        if (want <= 0) return 0;
        String today = DailyService.today();
        if (today.equals(data.getAbyssTicketGrantDate())) return 0;
        if (!giveNi(player, abyssTicketNiId, want)) {
            plugin.getLogger().warning("Failed to give free abyss tickets to " + player.getName()
                    + " — will retry next join/delay");
            return 0;
        }
        data.setAbyssTicketGrantDate(today);
        return want;
    }

    public boolean needsWeeklyGrant(PlayerData data) {
        if (weeklyFreeTickets <= 0) return false;
        return !DailyService.weekId().equals(data.getWeeklyTicketGrantWeekId());
    }

    /** 1.8.1: weekly free raid ticket (menu promised 每周团本票×1). */
    public int grantRaidIfNeeded(Player player, PlayerData data) {
        int want = Math.max(0, raidFreeTickets);
        if (want <= 0) return 0;
        String week = DailyService.weekId();
        if (week.equals(data.getRaidTicketGrantWeekId())) return 0;
        if (!giveNi(player, raidTicketNiId, want)) return 0;
        data.setRaidTicketGrantWeekId(week);
        return want;
    }

    public boolean needsRaidGrant(PlayerData data) {
        return raidFreeTickets > 0 && !DailyService.weekId().equals(data.getRaidTicketGrantWeekId());
    }

    public boolean needsAbyssGrant(PlayerData data) {
        if (abyssFreeTickets <= 0) return false;
        return !DailyService.today().equals(data.getAbyssTicketGrantDate());
    }

    /** Stage 4.4: weekly free elite ticket; hard_cap hold (default 1); not in shop daily pool. */
    public int grantEliteIfNeeded(Player player, PlayerData data) {
        int want = Math.max(0, eliteFreeTickets);
        if (want <= 0) return 0;
        String week = DailyService.weekId();
        if (week.equals(data.getEliteTicketGrantWeekId())) return 0;
        int have = ni == null ? 0 : ni.countInInventory(player, eliteTicketNiId);
        int room = Math.max(0, eliteHardCap - have);
        int give = Math.min(want, room);
        if (give <= 0) {
            // Already at hard cap — still mark week so we do not retry forever
            data.setEliteTicketGrantWeekId(week);
            return 0;
        }
        if (!giveNi(player, eliteTicketNiId, give)) {
            plugin.getLogger().warning("Failed to give free elite tickets to " + player.getName()
                    + " — will retry next join/delay");
            return 0;
        }
        data.setEliteTicketGrantWeekId(week);
        return give;
    }

    public boolean needsEliteGrant(PlayerData data) {
        return eliteFreeTickets > 0 && !DailyService.weekId().equals(data.getEliteTicketGrantWeekId());
    }

        public void cmdShowTickets(Player player) {
        if (player == null) return;
        PlayerData data = dataStore.get(player.getUniqueId());
        String week = DailyService.weekId();
        String today = DailyService.today();
        int dailyBag = ni == null ? 0 : ni.countInInventory(player, "ticket_ember_daily");
        // Prefer cash service id if present via weekly/abyss config; daily id is fixed contract
        int weeklyBag = ni == null ? 0 : ni.countInInventory(player, weeklyTicketNiId);
        int abyssBag = ni == null ? 0 : ni.countInInventory(player, abyssTicketNiId);

        boolean dailyGranted = data.isDailyFreeGranted();
        boolean weeklyGranted = week.equals(data.getWeeklyTicketGrantWeekId());
        boolean abyssGranted = today.equals(data.getAbyssTicketGrantDate());

        player.sendMessage(ChatColor.GOLD + "[门票] " + ChatColor.GRAY + "今日 " + today
                + " · 本周 " + week);
        player.sendMessage(ChatColor.AQUA + "  日票背包 §f" + dailyBag
                + ChatColor.GRAY + " · 今日免费已发：" + (dailyGranted ? ChatColor.GREEN + "是" : ChatColor.YELLOW + "否")
                + ChatColor.DARK_GRAY + "（granted " + data.getDailyTicketsGranted() + "）");
        player.sendMessage(ChatColor.AQUA + "  周票背包 §f" + weeklyBag
                + ChatColor.GRAY + " · 本周免费已发：" + (weeklyGranted ? ChatColor.GREEN + "是" : ChatColor.YELLOW + "否")
                + ChatColor.DARK_GRAY + "（granted " + data.getWeeklyTicketsGranted()
                + " · 购 " + data.getWeeklyTicketsBought() + "）");
        player.sendMessage(ChatColor.AQUA + "  深渊票背包 §f" + abyssBag
                + ChatColor.GRAY + " · 今日免费已发：" + (abyssGranted ? ChatColor.GREEN + "是" : ChatColor.YELLOW + "否"));
        int eliteBag = ni == null ? 0 : ni.countInInventory(player, eliteTicketNiId);
        boolean eliteGranted = week.equals(data.getEliteTicketGrantWeekId());
        player.sendMessage(ChatColor.AQUA + "  精英票背包 §f" + eliteBag
                + ChatColor.GRAY + " · 本周免费已发：" + (eliteGranted ? ChatColor.GREEN + "是" : ChatColor.YELLOW + "否")
                + ChatColor.DARK_GRAY + "（持有硬顶 " + eliteHardCap + "）");
    }

    private boolean giveNi(Player player, String niId, int amount) {
        if (amount <= 0) return true;
        if (ni == null) return false;
        return ni.giveNiItem(player, niId, amount);
    }
}
