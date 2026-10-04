package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.DailyService;
import town.sunshine.corerpg.PlayerData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * D177 P1 挂机庭 (docs/design/DESIGN-ember-afk-p1-2026-10-04.md). Reuses the {@code ember_afk} world of
 * {@link town.sunshine.corerpg.AfkTierService}; under P1 the four tiers open by main-story first clears and pay only
 * account-bound 余烬币 + 余烬经验 (never items, shards, cores, marks or gear).
 * <ul>
 *   <li><b>Online</b> (泡点 / rest area): every full minute a living player spends anywhere in the AFK world counts;
 *   each {@code round_minutes} pays one round at the player's highest unlocked tier.</li>
 *   <li><b>Offline</b> (WoW out-of-inn rest, 1/4 rate): logged-out minutes ÷ {@code offline_ratio}, at most
 *   {@code offline_rounds} rounds a day, paid on the next join.</li>
 *   <li><b>Cap</b>: online + offline share {@code daily_rounds} a day (stamina-day reset, {@link DailyService#today()}).</li>
 * </ul>
 * Exactly once: the day's round counter is advanced (and flushed) BEFORE the ledger rows {@code p1afk-<day>/c<k>} and
 * {@code x<k>} are recorded; ledger keys are unique per day, so a retry never pays twice and a crash in between loses
 * at most that round (never duplicates). Delivery is the run ledger's {@link EmberRunService#deliver}.
 */
public final class EmberAfkService implements Listener {

    static final String C_MIN = "p1_afk_min";     // counted online minutes toward the next round (today)
    static final String C_ROUND = "p1_afk_round"; // rounds paid today (online + offline)
    static final String C_OFF = "p1_afk_off";     // offline rounds paid today
    static final String C_QUIT = "p1_afk_quit";   // epoch minute of the last quit (period "all"); absent = none
    static final String LEDGER_PREFIX = "p1afk-";
    private static final String P = ChatColor.GOLD + "[挂机庭] " + ChatColor.GRAY;

    public static final class Tier {
        public final int n; public final String name, requires; public final int coin, xp;
        Tier(int n, String name, String requires, int coin, int xp) {
            this.n = n; this.name = name; this.requires = requires; this.coin = Math.max(0, coin); this.xp = Math.max(0, xp);
        }
    }

    private static volatile EmberAfkService instance;
    public static EmberAfkService get() { return instance; }

    private final CoreRpgPlugin plugin;
    private volatile boolean enabled;
    private volatile boolean legacyPayouts;
    private volatile String world = "ember_afk";
    private volatile int roundMinutes = 10, dailyRounds = 12, offlineRatio = 4, offlineRounds = 6;
    private volatile List<Tier> tiers = Collections.emptyList();
    private int taskId = -1;
    private final Set<String> fullNotified = Collections.synchronizedSet(new HashSet<String>());

    public EmberAfkService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // ---------------------------------------------------------------- config

    public void reload() {
        EmberMode m = EmberMode.get();
        ConfigurationSection s = m == null || m.config() == null ? null : m.config().getConfigurationSection("afk");
        enabled = s != null && s.getBoolean("enabled", false);
        List<Tier> ts = new ArrayList<Tier>();
        if (s != null) {
            world = s.getString("world", "ember_afk");
            legacyPayouts = s.getBoolean("legacy_payouts", false);
            roundMinutes = Math.max(1, s.getInt("round_minutes", 10));
            dailyRounds = Math.max(0, s.getInt("daily_rounds", 12));
            offlineRatio = Math.max(1, s.getInt("offline_ratio", 4));
            offlineRounds = Math.max(0, Math.min(dailyRounds, s.getInt("offline_rounds", 6)));
            for (Map<?, ?> t : s.getMapList("tiers")) {
                try {
                    ts.add(new Tier(Integer.parseInt(String.valueOf(t.get("n"))), String.valueOf(t.get("name")),
                            String.valueOf(t.get("requires")).toLowerCase(Locale.ROOT),
                            Integer.parseInt(String.valueOf(t.get("coin"))), Integer.parseInt(String.valueOf(t.get("xp")))));
                } catch (RuntimeException e) {
                    plugin.getLogger().warning("[P1 afk] bad tier row " + t + ": " + e);
                }
            }
        }
        Collections.sort(ts, (a, b) -> a.n - b.n);
        tiers = Collections.unmodifiableList(ts);
        if (taskId != -1) { Bukkit.getScheduler().cancelTask(taskId); taskId = -1; }
        if (enabled) taskId = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1200L, 1200L).getTaskId();
        plugin.getLogger().info("[P1 afk] " + (enabled ? "on" : "off") + " world=" + world + " round=" + roundMinutes + "m daily=" + dailyRounds
                + " offline=1/" + offlineRatio + " max " + offlineRounds + " tiers=" + ts.size() + " legacy_payouts=" + legacyPayouts);
    }

    /** D177 rules apply: config on and P1 active. */
    public boolean p1() { return enabled && EmberMode.active(); }
    public String world() { return world; }
    public List<Tier> tiers() { return tiers; }
    public int dailyRounds() { return dailyRounds; }
    public int roundMinutes() { return roundMinutes; }

    /** P1: the legacy AFK mobs (MM {@code corerpg mmgive}/{@code mmxp}) and kill coin / kill XP pay nothing in the AFK world. */
    public static boolean blocksLegacyPayout(World w) {
        EmberAfkService a = instance;
        return a != null && a.p1() && !a.legacyPayouts && w != null && w.getName().equalsIgnoreCase(a.world);
    }

    // ---------------------------------------------------------------- tiers

    private PlayerData data(UUID u) { try { return plugin.getDataStore().get(u); } catch (RuntimeException e) { return null; } }

    private static boolean flag(PlayerData d, String key) {
        EmberRunService rs = EmberRunService.get();
        return d != null && rs != null && rs.progressFlag(d, key);
    }

    public Tier tier(int n) { for (Tier t : tiers) if (t.n == n) return t; return null; }

    /** Highest tier whose main-story requirement this player has first-cleared; null = none (before Q01). */
    public Tier tierFor(PlayerData d) {
        Tier best = null;
        for (Tier t : tiers) if (flag(d, t.requires)) best = t;
        return best;
    }

    public boolean tierUnlocked(Player p, int n) {
        Tier t = tier(n);
        return t == null || flag(data(p.getUniqueId()), t.requires);
    }

    public String requiresLabel(int n) {
        Tier t = tier(n);
        return t == null ? "" : "首通 " + t.requires.toUpperCase(Locale.ROOT);
    }

    // ---------------------------------------------------------------- accrual

    static int epochMinute(long ms) { return (int) (ms / 60000L); }

    /** Offline rounds earned by {@code offMinutes} logged out (pure, unit-tested). */
    static int offlineRoundsFor(int offMinutes, int ratio, int roundMinutes) {
        if (offMinutes <= 0 || ratio <= 0 || roundMinutes <= 0) return 0;
        return offMinutes / ratio / roundMinutes;
    }

    /** Rounds that may still be paid now (pure, unit-tested): daily cap, and the offline sub-cap for offline credit. */
    static int grantable(int want, int roundsToday, int dailyCap, boolean offline, int offToday, int offlineCap) {
        int room = Math.max(0, dailyCap - roundsToday);
        if (offline) room = Math.min(room, Math.max(0, offlineCap - offToday));
        return Math.max(0, Math.min(want, room));
    }

    private void tick() {
        if (!p1()) return;
        World w = Bukkit.getWorld(world);
        if (w == null) return;
        String day = DailyService.today();
        for (Player p : w.getPlayers()) {
            if (p.isDead() || !p.isOnline()) continue;
            PlayerData d = data(p.getUniqueId());
            Tier t = tierFor(d);
            if (d == null || t == null) continue;
            if (d.periodCount(C_ROUND, day) >= dailyRounds) {
                if (fullNotified.add(p.getUniqueId() + "@" + day))
                    p.sendMessage(P + "今日挂机收益已满（" + dailyRounds + "/" + dailyRounds + " 轮）· 明天 0 点重置。§e想更快变强还是打主线本。");
                continue;
            }
            int m = d.addPeriodCount(C_MIN, day, 1);
            if (m >= roundMinutes) {
                d.addPeriodCount(C_MIN, day, -m);
                pay(p, d, day, t, 1, false, null);
            }
        }
    }

    /** Pays up to {@code want} rounds; returns the rounds actually paid. */
    int pay(Player p, PlayerData d, String day, Tier t, int want, boolean offline, String note) {
        int start = d.periodCount(C_ROUND, day);
        int give = grantable(want, start, dailyRounds, offline, d.periodCount(C_OFF, day), offlineRounds);
        if (give <= 0) return 0;
        EmberRunService rs = EmberRunService.get();
        if (rs == null) return 0;
        UUID u = p.getUniqueId();
        d.addPeriodCount(C_ROUND, day, give);
        if (offline) d.addPeriodCount(C_OFF, day, give);
        plugin.getDataStore().flushMutation(u); // counter first: a crash before the rows loses the round, never doubles it
        String run = LEDGER_PREFIX + day;
        int coin = t.coin * give, xp = t.xp * give;
        if (coin > 0) rs.grantRow(u, run, "c" + (start + 1), "coin:" + coin);
        if (xp > 0) rs.grantRow(u, run, "x" + (start + 1), "xp:" + xp);
        plugin.getLogger().info("[P1 afk] " + p.getName() + " " + (offline ? "offline " : "") + "rounds " + (start + 1) + "-" + (start + give)
                + " tier " + t.n + " -> coin " + coin + " xp " + xp + " (" + day + ")");
        rs.deliverQuiet(p);
        p.sendMessage(P + (note == null ? "" : note) + "§a第 " + (start + give) + " 轮结算 §7(" + t.name + ")：§f余烬币 +" + coin + " §7· §f余烬经验 +" + xp
                + " §8· 今日 " + (start + give) + "/" + dailyRounds);
        return give;
    }

    // ---------------------------------------------------------------- offline (quit → join)

    private void stampQuit(Player p) {
        PlayerData d = data(p.getUniqueId());
        if (d == null) return;
        int now = epochMinute(System.currentTimeMillis());
        d.addPeriodCount(C_QUIT, "all", now - d.periodCount(C_QUIT, "all"));
    }

    @EventHandler(priority = EventPriority.LOW) // before CoreRpgPlugin.onQuit (NORMAL) unloads + saves the player
    public void onQuit(PlayerQuitEvent e) {
        if (!p1()) return;
        stampQuit(e.getPlayer());
    }

    /** onDisable: quit events come after plugins are disabled, so stamp everyone online now (saved by dataStore.saveAll). */
    public void shutdown() {
        if (taskId != -1) { Bukkit.getScheduler().cancelTask(taskId); taskId = -1; }
        if (!p1()) return;
        for (Player p : Bukkit.getOnlinePlayers()) stampQuit(p);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline() || !p1()) return;
            PlayerData d = data(p.getUniqueId());
            if (d == null) return;
            int q = d.periodCount(C_QUIT, "all");
            if (q <= 0) return;
            d.addPeriodCount(C_QUIT, "all", -q); // consumed once: reconnect cycling never re-credits the same time
            plugin.getDataStore().flushMutation(p.getUniqueId());
            Tier t = tierFor(d);
            if (t == null) return;
            int off = epochMinute(System.currentTimeMillis()) - q;
            int rounds = offlineRoundsFor(off, offlineRatio, roundMinutes);
            if (rounds <= 0) return;
            String day = DailyService.today();
            int paid = pay(p, d, day, t, rounds, true, "离线 " + (off / 60) + " 小时 " + (off % 60) + " 分 → ");
            if (paid <= 0 && d.periodCount(C_OFF, day) >= offlineRounds)
                p.sendMessage(P + "今日离线挂机已折满 " + offlineRounds + " 轮（在线挂机还能拿到每日上限）。");
        }, 80L);
    }

    // ---------------------------------------------------------------- PAPI %corerpg_p1_afk_<key>%

    public String papi(Player p, PlayerData d, String key) {
        if (d == null) return "";
        String day = DailyService.today();
        Tier t = tierFor(d);
        if (key.length() == 2 && key.charAt(0) == 't') { // t1..t4: 1 = unlocked
            Tier x = tier(key.charAt(1) - '0');
            return x != null && flag(d, x.requires) ? "1" : "0";
        }
        int rounds = d.periodCount(C_ROUND, day);
        switch (key) {
            case "on": return p1() ? "1" : "0";
            case "tier": return t == null ? "§7未解锁（首通 Q01 后开放）" : "§f" + t.name + " §7(" + "首通 " + t.requires.toUpperCase(Locale.ROOT) + ")";
            case "rate": return t == null ? "—" : "§f每 " + roundMinutes + " 分钟 §e" + t.coin + " 币 + " + t.xp + " 经验 §7· 每日最多 §e"
                    + t.coin * dailyRounds + " 币 + " + t.xp * dailyRounds + " 经验";
            case "today": return "§f" + rounds + "/" + dailyRounds + " 轮 §7· 离线已折 " + d.periodCount(C_OFF, day) + "/" + offlineRounds;
            case "rounds": return String.valueOf(rounds);
            case "next": {
                if (!p1()) return "§7未开启";
                if (t == null) return "§7首通 Q01 后开放";
                if (rounds >= dailyRounds) return "§a今日已满 · 明天 0 点重置";
                boolean here = p.getWorld() != null && p.getWorld().getName().equalsIgnoreCase(world);
                int left = Math.max(1, roundMinutes - d.periodCount(C_MIN, day));
                return here ? "§a计时中 · 下一轮还差约 " + left + " 分钟" : "§e进入挂机庭开始计时（已攒 " + d.periodCount(C_MIN, day) + "/" + roundMinutes + " 分）";
            }
            default: return "";
        }
    }

    /** One status line for the legacy /corerpg afk list under P1. */
    public String statusLine(Player p) {
        PlayerData d = data(p.getUniqueId());
        return papi(p, d, "today") + " §7· " + papi(p, d, "next");
    }
}
