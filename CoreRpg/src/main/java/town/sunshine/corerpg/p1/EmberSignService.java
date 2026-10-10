package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.DailyService;
import town.sunshine.corerpg.PlayerData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * D180 P1 每日签到 + 在线时长奖励 (docs/design/DESIGN-ember-signin-online-2026-10-04.md).
 * <ul>
 *   <li><b>签到</b> (HoYoLAB / Black Desert / Lost Ark cumulative calendar): one sign-in per stamina day; the reward is
 *   the n-th sign-in of the calendar month ({@code signin.special[n]} else {@code signin.daily}; n &gt; 28 →
 *   {@code signin.extra}); a missed day never resets anything. 补签 (HoYoLAB rule): at most {@code makeup_per_month}
 *   a month and one a day, only for the current month, fills the earliest missed day, and needs today's sign-in plus
 *   {@code makeup_needs_online} counted minutes today (no coin, no cards).</li>
 *   <li><b>在线时长</b> (PlayTime+ / QZOnlineReward style): counted active minutes per day — online, outside
 *   {@code online.exclude_worlds} (empty since D180 rev 2: the AFK world counts like anywhere else), and some real input (look / click / interact /
 *   attack / chat / hotbar / sneak / command) within {@code idle_minutes}. Position-only movement (water streams,
 *   pistons, being pushed) never counts. Milestones are claimed in the menu; reached-but-unclaimed ones are paid
 *   automatically after the day rolls over.</li>
 * </ul>
 * Pays only account-bound things: 余烬币 / 余烬经验 / 锻造印记 / 首领徽记 (account counters). Exactly once: the day / month
 * state counter is advanced and flushed BEFORE the once-only PENDING ledger rows ({@code p1sign-<yyyy-MM>/n<k>*},
 * {@code p1online-<day>/m<min>*}) are written and delivered by {@link EmberRunService#deliverQuiet}; a crash in between
 * loses that reward, never doubles it. A server clock that went backwards freezes sign-in and counting until it catches up.
 * <p>D216 / ARCH S2-3: ordinary daily coin/xp, makeup_per_month and sigmark_fallback_coin read
 * {@link EmberEconomy#amount} (S23); online milestone totals are checked against S24 golden; ledger delivery tags
 * S23/S24 via {@link EmberEconomy#sourceForGrant}.
 */
public final class EmberSignService implements Listener {

    static final String C_SMASK = "p1_sign_mask";  // @yyyy-MM: bit (d-1) = day d of the month signed (incl. make-ups)
    static final String C_SMK = "p1_sign_mk";      // @yyyy-MM: make-ups used this month
    static final String C_SMKD = "p1_sign_mkd";    // @day: make-up used today
    static final String C_SLAST = "p1_sign_last";  // @all: yyyymmdd of the latest real sign-in (clock-back guard)
    static final String C_OMIN = "p1_on_min";      // @day: counted active minutes
    static final String C_OCLAIM = "p1_on_claim";  // @day: bit i = milestone i claimed
    static final String C_OLAST = "p1_on_last";    // @all: yyyymmdd of the day C_OMIN / C_OCLAIM belong to
    static final String SIGN_RUN = "p1sign-", ON_RUN = "p1online-";
    public static final String MENU = "ember_p1_sign";
    private static final String P = ChatColor.GOLD + "[签到] " + ChatColor.GRAY;
    private static final String PO = ChatColor.GOLD + "[在线] " + ChatColor.GRAY;

    /** One reward cell: account-bound amounts only. */
    public static final class Reward {
        public final int coin, xp, mark, sigmark;
        public Reward(int coin, int xp, int mark, int sigmark) {
            this.coin = Math.max(0, coin); this.xp = Math.max(0, xp); this.mark = Math.max(0, mark); this.sigmark = Math.max(0, sigmark);
        }
        static Reward of(ConfigurationSection s) {
            return s == null ? new Reward(0, 0, 0, 0) : new Reward(s.getInt("coin"), s.getInt("xp"), s.getInt("mark"), s.getInt("sigmark"));
        }
        static Reward of(Map<?, ?> m) { return new Reward(i(m, "coin"), i(m, "xp"), i(m, "mark"), i(m, "sigmark")); }
        private static int i(Map<?, ?> m, String k) { Object v = m.get(k); return v == null ? 0 : Integer.parseInt(String.valueOf(v)); }
        public String label() {
            List<String> l = new ArrayList<String>();
            if (coin > 0) l.add("余烬币 " + coin);
            if (xp > 0) l.add("经验 " + xp);
            if (mark > 0) l.add("锻造印记 ×" + mark);
            if (sigmark > 0) l.add("首领徽记 ×" + sigmark);
            return l.isEmpty() ? "—" : String.join(" · ", l);
        }
    }

    public static final class Milestone {
        public final int min; public final Reward r;
        Milestone(int min, Reward r) { this.min = min; this.r = r; }
    }

    private static volatile EmberSignService instance;
    public static EmberSignService get() { return instance; }

    private final CoreRpgPlugin plugin;
    private volatile boolean signOn, onlineOn;
    private volatile Reward daily = new Reward(0, 0, 0, 0), extra = new Reward(0, 0, 0, 0);
    private volatile Map<Integer, Reward> special = Collections.emptyMap();
    private volatile int fallbackCoin = 60, makeupPerMonth = 3, makeupNeeds = 60, idleMinutes = 5;
    private volatile Set<String> excluded = Collections.emptySet();
    private volatile boolean afkCombatCounts = true; // D180 rev 2 (owner 10-05 00:04): AFK auto-combat = activity
    private volatile List<Milestone> milestones = Collections.emptyList();
    private final Map<UUID, Long> lastAct = new ConcurrentHashMap<UUID, Long>();
    private int taskId = -1;

    public EmberSignService(CoreRpgPlugin plugin) {
        this.plugin = plugin;
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // ---------------------------------------------------------------- config

    public void reload() {
        EmberMode m = EmberMode.get();
        ConfigurationSection s = m == null || m.config() == null ? null : m.config().getConfigurationSection("signin");
        ConfigurationSection o = m == null || m.config() == null ? null : m.config().getConfigurationSection("online");
        signOn = s != null && s.getBoolean("enabled", false);
        onlineOn = o != null && o.getBoolean("enabled", false);
        if (s != null) {
            // D216: ordinary daily coin/xp from EmberEconomy S23 (yml still ships the same numbers; golden-pinned)
            Reward yDaily = Reward.of(s.getConfigurationSection("daily"));
            daily = new Reward(EmberEconomy.amount("S23", "daily.coin"), EmberEconomy.amount("S23", "daily.xp"),
                    yDaily.mark, yDaily.sigmark);
            extra = Reward.of(s.getConfigurationSection("extra"));
            Map<Integer, Reward> sp = new TreeMap<Integer, Reward>();
            ConfigurationSection ss = s.getConfigurationSection("special");
            if (ss != null) for (String k : ss.getKeys(false)) {
                try { sp.put(Integer.valueOf(k), Reward.of(ss.getConfigurationSection(k))); }
                catch (RuntimeException e) { plugin.getLogger().warning("[P1 sign] bad special " + k + ": " + e); }
            }
            special = Collections.unmodifiableMap(sp);
            fallbackCoin = Math.max(0, s.getInt("sigmark_fallback_coin", EmberEconomy.amount("S23", "sigmark_fallback_coin")));
            makeupPerMonth = Math.max(0, s.getInt("makeup_per_month", EmberEconomy.amount("S23", "makeup_per_month")));
            makeupNeeds = Math.max(0, s.getInt("makeup_needs_online", 60));
        }
        List<Milestone> ms = new ArrayList<Milestone>();
        if (o != null) {
            idleMinutes = Math.max(1, o.getInt("idle_minutes", 5));
            Set<String> ex = new HashSet<String>();
            for (String w : o.getStringList("exclude_worlds")) ex.add(w.toLowerCase(Locale.ROOT));
            excluded = Collections.unmodifiableSet(ex);
            afkCombatCounts = o.getBoolean("afk_combat_counts", true);
            for (Map<?, ?> r : o.getMapList("milestones")) {
                try { ms.add(new Milestone(Integer.parseInt(String.valueOf(r.get("min"))), Reward.of(r))); }
                catch (RuntimeException e) { plugin.getLogger().warning("[P1 online] bad milestone " + r + ": " + e); }
            }
        }
        Collections.sort(ms, (a, b) -> a.min - b.min);
        while (ms.size() > 30) ms.remove(ms.size() - 1); // claim mask is an int
        milestones = Collections.unmodifiableList(ms);
        // D216: milestone cells stay yml-driven; totals must match S24 golden (no silent drift)
        int coinSum = 0, xpSum = 0, top = 0;
        for (Milestone mile : ms) { coinSum += mile.r.coin; xpSum += mile.r.xp; if (mile.min > top) top = mile.min; }
        if (!ms.isEmpty() && (coinSum != EmberEconomy.amount("S24", "coin_total")
                || xpSum != EmberEconomy.amount("S24", "xp_total")
                || top != EmberEconomy.amount("S24", "top_min")))
            plugin.getLogger().warning("[P1 online] milestone totals coin=" + coinSum + " xp=" + xpSum + " top=" + top
                    + " != EmberEconomy S24 (" + EmberEconomy.amount("S24", "coin_total") + "/"
                    + EmberEconomy.amount("S24", "xp_total") + "/" + EmberEconomy.amount("S24", "top_min") + ")");
        if (taskId != -1) { Bukkit.getScheduler().cancelTask(taskId); taskId = -1; }
        if (onlineOn) taskId = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1200L, 1200L).getTaskId();
        long now = System.currentTimeMillis();
        for (Player p : Bukkit.getOnlinePlayers()) lastAct.putIfAbsent(p.getUniqueId(), now);
        plugin.getLogger().info("[P1 sign] signin=" + (signOn ? "on" : "off") + " special=" + special.keySet() + " makeup=" + makeupPerMonth + "/month needs "
                + makeupNeeds + "m · online=" + (onlineOn ? "on" : "off") + " idle=" + idleMinutes + "m exclude=" + excluded + " afk_combat=" + afkCombatCounts + " milestones=" + ms.size());
    }

    public boolean signP1() { return signOn && EmberMode.active(); }
    public boolean onlineP1() { return onlineOn && EmberMode.active(); }
    public void shutdown() { if (taskId != -1) { Bukkit.getScheduler().cancelTask(taskId); taskId = -1; } }

    // ---------------------------------------------------------------- pure rules (unit-tested)

    static int bit(int dom) { return dom >= 1 && dom <= 31 ? 1 << (dom - 1) : 0; }
    static boolean signedOn(int mask, int dom) { return (mask & bit(dom)) != 0; }
    static int count(int mask) { return Integer.bitCount(mask & 0x7fffffff); }
    /** earliest day 1..dom-1 not in mask (HoYoLAB: make-up fills the first missed date), 0 = none */
    static int earliestMissed(int mask, int dom) {
        for (int d = 1; d < dom && d <= 31; d++) if (!signedOn(mask, d)) return d;
        return 0;
    }
    /** why a make-up is refused now (null = allowed) */
    static String makeupBlock(int mask, int dom, int usedMonth, int perMonth, int usedToday, int minutesToday, int needs) {
        if (!signedOn(mask, dom)) return "先签今天，再补签";
        if (usedMonth >= perMonth) return "本月补签次数已用完（" + usedMonth + "/" + perMonth + "）";
        if (usedToday > 0) return "今天已经补签过 1 次（每天最多 1 次）";
        if (minutesToday < needs) return "今天有效在线 " + minutesToday + "/" + needs + " 分钟后可补签";
        if (earliestMissed(mask, dom) == 0) return "本月没有漏签";
        return null;
    }
    /** 0 = not reached, 1 = claimable, 2 = claimed */
    static int milestoneState(int minutes, int claimedMask, int i, int min) {
        if ((claimedMask & (1 << i)) != 0) return 2;
        return minutes >= min ? 1 : 0;
    }
    static int dayInt(LocalDate d) { return d.getYear() * 10000 + d.getMonthValue() * 100 + d.getDayOfMonth(); }
    static String dayStr(int v) { return String.format(Locale.ROOT, "%04d-%02d-%02d", v / 10000, v / 100 % 100, v % 100); }
    static String month(LocalDate d) { return String.format(Locale.ROOT, "%04d-%02d", d.getYear(), d.getMonthValue()); }
    /** does this minute count? (pure) */
    static boolean counts(boolean excludedWorld, Long lastActMs, long nowMs, int idleMin) {
        return counts(excludedWorld, false, lastActMs, nowMs, idleMin);
    }
    /** D180 rev 2: running AFK auto-combat counts as activity (no idle stop while it runs); the daily cap is the 120-min tier */
    static boolean counts(boolean excludedWorld, boolean autoCombat, Long lastActMs, long nowMs, int idleMin) {
        if (excludedWorld) return false;
        return autoCombat || (lastActMs != null && nowMs - lastActMs <= idleMin * 60000L);
    }
    private boolean autoCombat(Player p) {
        if (!afkCombatCounts) return false;
        EmberAfkService a = EmberAfkService.get();
        return a != null && a.p1() && p.getWorld() != null && p.getWorld().getName().equalsIgnoreCase(a.world()) && a.fighting(p.getUniqueId());
    }

    public Reward rewardFor(int n) {
        Reward r = special.get(Integer.valueOf(n));
        if (r != null) return r;
        return n > 28 ? extra : daily;
    }
    public List<Milestone> milestones() { return milestones; }

    // ---------------------------------------------------------------- helpers

    private PlayerData data(UUID u) { try { return plugin.getDataStore().get(u); } catch (RuntimeException e) { return null; } }
    private static LocalDate today() { return LocalDate.now(DailyService.zone()); }

    /** tier of the highest first-cleared main map (1..3); 1 before any clear */
    int markTier(PlayerData d) {
        EmberRunService rs = EmberRunService.get();
        int t = 1;
        if (rs != null && rs.maps() != null)
            for (EmberRunMaps.MapDef m : rs.maps().maps.values())
                if (m.key.startsWith("q") && rs.progressFlag(d, m.key)) t = Math.max(t, m.tier);
        return Math.max(1, Math.min(3, t));
    }

    /** the latest (story order) first-cleared map that has signatures; null = none yet */
    String sigMap(PlayerData d) {
        EmberRunService rs = EmberRunService.get();
        String best = null;
        if (rs != null) for (String k : EmberSignature.maps()) if (rs.progressFlag(d, k)) best = k;
        return best;
    }

    /** writes the once-only rows for {@code r} (state already advanced + flushed) and delivers; returns the chat text */
    private String grant(Player p, PlayerData d, String run, String key, Reward r) {
        EmberRunService rs = EmberRunService.get();
        if (rs == null) return "（结算服务未加载，奖励稍后到账）";
        UUID u = p.getUniqueId();
        int coin = r.coin;
        List<String> got = new ArrayList<String>();
        if (r.sigmark > 0) {
            String map = sigMap(d);
            if (map == null) coin += fallbackCoin;
            else if (rs.grantRow(u, run, key + "s", "sigmark:" + map + ":" + r.sigmark)) got.add(map.toUpperCase(Locale.ROOT) + " 首领徽记 ×" + r.sigmark);
        }
        if (r.mark > 0) {
            int t = markTier(d);
            if (rs.grantRow(u, run, key + "m", "mark:" + t + ":" + r.mark)) got.add("T" + t + " 锻造印记 ×" + r.mark);
        }
        if (coin > 0 && rs.grantRow(u, run, key + "c", "coin:" + coin)) got.add("余烬币 " + coin);
        if (r.xp > 0 && rs.grantRow(u, run, key + "x", "xp:" + r.xp)) got.add("余烬经验 " + r.xp);
        rs.deliverQuiet(p);
        return got.isEmpty() ? "（已领过）" : String.join("§7、§f", got);
    }

    private boolean ready(Player p, PlayerData d, boolean sign) {
        if (!(sign ? signP1() : onlineP1())) { p.sendMessage((sign ? P : PO) + "未开启"); return false; }
        if (d == null || d.isLoadFailed()) { p.sendMessage((sign ? P : PO) + ChatColor.RED + "角色数据未加载，稍后再试"); return false; }
        return true;
    }

    // ---------------------------------------------------------------- 签到

    public void claimSign(Player p) {
        PlayerData d = data(p.getUniqueId());
        if (!ready(p, d, true)) return;
        LocalDate now = today();
        int ti = dayInt(now), dom = now.getDayOfMonth();
        String mon = month(now);
        if (d.periodCount(C_SLAST, "all") > ti) { p.sendMessage(P + ChatColor.RED + "服务器日期异常（早于上次签到），暂停签到"); return; }
        int mask = d.periodCount(C_SMASK, mon);
        if (signedOn(mask, dom)) { p.sendMessage(P + "今天已经签到了（本月第 " + count(mask) + " 次）· 明天 0 点再来"); return; }
        d.addPeriodCount(C_SMASK, mon, bit(dom));
        d.addPeriodCount(C_SLAST, "all", ti - d.periodCount(C_SLAST, "all"));
        int n = count(mask | bit(dom));
        plugin.getDataStore().flushMutation(p.getUniqueId()); // state first: a crash before the rows loses the cell, never doubles it
        String got = grant(p, d, SIGN_RUN + mon, "n" + n, rewardFor(n));
        plugin.getLogger().info("[P1 sign] " + p.getName() + " sign " + DailyService.today() + " -> n=" + n + " " + rewardFor(n).label());
        p.sendMessage(P + "§a签到成功 · 本月第 " + n + " 次：§f" + got);
        Reward next = rewardFor(n + 1);
        if (n < 28) p.sendMessage(P + "§7下一次（第 " + (n + 1) + " 次）：" + next.label() + nextBig(n));
    }

    private String nextBig(int n) {
        for (Integer k : special.keySet()) if (k > n) return " §8· 第 " + k + " 次大格：" + special.get(k).label();
        return "";
    }

    /** D542: null = makeup allowed now; else refusal reason. */
    public String makeupWhy(Player p) {
        PlayerData d = data(p.getUniqueId());
        if (d == null || !signP1()) return "签到未开放";
        LocalDate now = today();
        int dom = now.getDayOfMonth();
        String mon = month(now), day = DailyService.today();
        if (d.periodCount(C_SLAST, "all") > dayInt(now)) return "服务器日期异常";
        int mask = d.periodCount(C_SMASK, mon);
        return makeupBlock(mask, dom, d.periodCount(C_SMK, mon), makeupPerMonth, d.periodCount(C_SMKD, day), minutesToday(d), makeupNeeds);
    }

    public boolean isMakeupReady(Player p) { return makeupWhy(p) == null; }

    /**
     * D542 admin smoke: force today signed + earliest miss + enough online minutes + clear today makeup latch.
     * Returns false when month day==1 (no prior miss possible).
     */
    public boolean adminSeedMakeup(Player p) {
        if (p == null) return false;
        PlayerData d = data(p.getUniqueId());
        if (d == null || !signP1()) return false;
        LocalDate now = today();
        int dom = now.getDayOfMonth();
        if (dom <= 1) return false;
        String mon = month(now), day = DailyService.today();
        int ti = dayInt(now);
        int mask = d.periodCount(C_SMASK, mon);
        // ensure today signed
        if (!signedOn(mask, dom)) {
            d.addPeriodCount(C_SMASK, mon, bit(dom));
            mask |= bit(dom);
            int last = d.periodCount(C_SLAST, "all");
            if (last < ti) d.addPeriodCount(C_SLAST, "all", ti - last);
        }
        // ensure day 1 missed
        if (signedOn(mask, 1)) d.addPeriodCount(C_SMASK, mon, -bit(1));
        // online minutes
        int ol = d.periodCount(C_OLAST, "all");
        if (ol != ti) {
            d.addPeriodCount(C_OLAST, "all", ti - ol);
            int curMin = d.periodCount(C_OMIN, day);
            if (curMin != 0) d.addPeriodCount(C_OMIN, day, -curMin);
        }
        int mins = d.periodCount(C_OMIN, day);
        if (mins < makeupNeeds) d.addPeriodCount(C_OMIN, day, makeupNeeds - mins);
        // clear today makeup used
        int mkd = d.periodCount(C_SMKD, day);
        if (mkd > 0) d.addPeriodCount(C_SMKD, day, -mkd);
        plugin.getDataStore().flushMutation(p.getUniqueId());
        return isMakeupReady(p);
    }

    public void makeup(Player p) {
        PlayerData d = data(p.getUniqueId());
        if (!ready(p, d, true)) return;
        LocalDate now = today();
        int dom = now.getDayOfMonth();
        String mon = month(now), day = DailyService.today();
        if (d.periodCount(C_SLAST, "all") > dayInt(now)) { p.sendMessage(P + ChatColor.RED + "服务器日期异常，暂停补签"); return; }
        int mask = d.periodCount(C_SMASK, mon);
        String why = makeupBlock(mask, dom, d.periodCount(C_SMK, mon), makeupPerMonth, d.periodCount(C_SMKD, day), minutesToday(d), makeupNeeds);
        if (why != null) { p.sendMessage(P + ChatColor.YELLOW + why); return; }
        int miss = earliestMissed(mask, dom);
        d.addPeriodCount(C_SMASK, mon, bit(miss));
        d.addPeriodCount(C_SMK, mon, 1);
        d.addPeriodCount(C_SMKD, day, 1);
        int n = count(mask | bit(miss));
        plugin.getDataStore().flushMutation(p.getUniqueId());
        String got = grant(p, d, SIGN_RUN + mon, "n" + n, rewardFor(n));
        plugin.getLogger().info("[P1 sign] " + p.getName() + " makeup " + mon + "-" + miss + " -> n=" + n);
        p.sendMessage(P + "§a补签 " + now.getMonthValue() + " 月 " + miss + " 日 · 本月第 " + n + " 次：§f" + got
                + " §8（本月补签 " + d.periodCount(C_SMK, mon) + "/" + makeupPerMonth + "）");
    }

    // ---------------------------------------------------------------- 在线时长

    int minutesToday(PlayerData d) {
        if (d == null) return 0;
        return d.periodCount(C_OLAST, "all") == dayInt(today()) ? d.periodCount(C_OMIN, DailyService.today()) : 0;
    }

    /** day changed since the counters were written: pay the old day's reached-but-unclaimed milestones once. false = clock went back */
    private boolean rollover(Player p, PlayerData d, int ti) {
        int last = d.periodCount(C_OLAST, "all");
        if (last == ti) return true;
        if (last > ti) return false; // clock backwards: freeze until it catches up (no re-earning a dropped day)
        List<Milestone> owed = new ArrayList<Milestone>();
        String old = last > 0 ? dayStr(last) : null;
        if (old != null) {
            int mins = d.periodCount(C_OMIN, old), claimed = d.periodCount(C_OCLAIM, old), add = 0;
            for (int i = 0; i < milestones.size(); i++)
                if (milestoneState(mins, claimed, i, milestones.get(i).min) == 1) { owed.add(milestones.get(i)); add |= 1 << i; }
            if (add != 0) d.addPeriodCount(C_OCLAIM, old, add);
        }
        d.addPeriodCount(C_OLAST, "all", ti - last);
        plugin.getDataStore().flushMutation(p.getUniqueId());
        if (!owed.isEmpty()) {
            List<String> g = new ArrayList<String>();
            for (Milestone m : owed) g.add(grant(p, d, ON_RUN + old, "m" + m.min, m.r));
            plugin.getLogger().info("[P1 online] " + p.getName() + " auto-settle " + old + " " + owed.size() + " milestone(s)");
            p.sendMessage(PO + "§a" + old.substring(5) + " 在线奖励没领的已自动补发：§f" + String.join("§7、§f", g));
        }
        return true;
    }

    private void tick() {
        if (!onlineP1()) return;
        long now = System.currentTimeMillis();
        LocalDate t = today();
        int ti = dayInt(t);
        String day = DailyService.today();
        for (Player p : Bukkit.getOnlinePlayers()) {
            PlayerData d = data(p.getUniqueId());
            if (d == null || d.isLoadFailed()) continue;
            if (!rollover(p, d, ti)) continue;
            boolean ex = p.getWorld() != null && excluded.contains(p.getWorld().getName().toLowerCase(Locale.ROOT));
            if (!counts(ex, autoCombat(p), lastAct.get(p.getUniqueId()), now, idleMinutes)) continue;
            int before = d.periodCount(C_OMIN, day);
            int m = d.addPeriodCount(C_OMIN, day, 1);
            notifyOnlineMilestones(p, d, day, before, m); // D518 online path
        }
    }


    /** D518: milestones newly crossed in (before, after] — honor online path auto/ask/mute. */
    void notifyOnlineMilestones(Player p, PlayerData d, String day, int before, int after) {
        if (p == null || d == null || day == null || after <= before) return;
        for (int i = 0; i < milestones.size(); i++) {
            Milestone ms = milestones.get(i);
            if (ms.min <= before || ms.min > after) continue;
            if (milestoneState(after, d.periodCount(C_OCLAIM, day), i, ms.min) != 1) continue;
            if (EmberOnlinePath.shouldAutoClaim(d)) {
                claimOnline(p, i);
                continue;
            }
            if (EmberOnlinePath.shouldMute(d)) continue;
            ConfirmTokens.sendButton(p, PO + "§a今日有效在线满 " + ms.min + " 分钟 · 可领：§f" + ms.r.label() + " ", "[去领取]",
                    "/corerpg p1 sign menu", "主菜单 → 签到 · 在线");
        }
    }

    /** idx = milestone index (0-based) or -1 = every reached one */
    public void claimOnline(Player p, int idx) {
        PlayerData d = data(p.getUniqueId());
        if (!ready(p, d, false)) return;
        int ti = dayInt(today());
        if (!rollover(p, d, ti)) { p.sendMessage(PO + ChatColor.RED + "服务器日期异常，暂停领取"); return; }
        String day = DailyService.today();
        int mins = d.periodCount(C_OMIN, day), claimed = d.periodCount(C_OCLAIM, day), add = 0;
        List<Milestone> pay = new ArrayList<Milestone>();
        for (int i = 0; i < milestones.size(); i++) {
            if (idx >= 0 && i != idx) continue;
            if (milestoneState(mins, claimed, i, milestones.get(i).min) == 1) { pay.add(milestones.get(i)); add |= 1 << i; }
        }
        if (pay.isEmpty()) {
            Milestone next = null;
            for (int i = 0; i < milestones.size(); i++) if (milestoneState(mins, claimed, i, milestones.get(i).min) == 0) { next = milestones.get(i); break; }
            p.sendMessage(PO + (next == null ? "今天的在线奖励都领完了 · 明天 0 点重置" : "还没到：今日有效在线 " + mins + "/" + next.min + " 分钟"));
            return;
        }
        d.addPeriodCount(C_OCLAIM, day, add);
        plugin.getDataStore().flushMutation(p.getUniqueId());
        List<String> g = new ArrayList<String>();
        for (Milestone m : pay) g.add(m.min + " 分钟：" + grant(p, d, ON_RUN + day, "m" + m.min, m.r));
        plugin.getLogger().info("[P1 online] " + p.getName() + " claim " + day + " " + pay.size() + " milestone(s) at " + mins + "m");
        p.sendMessage(PO + "§a领取 · §f" + String.join("§7；§f", g));
    }

    // ---------------------------------------------------------------- activity (any real input; never position-only movement)

    private void act(Player p) { if (p != null) lastAct.put(p.getUniqueId(), System.currentTimeMillis()); }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Location f = e.getFrom(), t = e.getTo();
        if (t != null && (f.getYaw() != t.getYaw() || f.getPitch() != t.getPitch())) act(e.getPlayer());
    }
    @EventHandler(priority = EventPriority.MONITOR) public void onInteract(PlayerInteractEvent e) { act(e.getPlayer()); }
    @EventHandler(priority = EventPriority.MONITOR) public void onClick(InventoryClickEvent e) { if (e.getWhoClicked() instanceof Player) act((Player) e.getWhoClicked()); }
    @EventHandler(priority = EventPriority.MONITOR) public void onChat(AsyncPlayerChatEvent e) { act(e.getPlayer()); }
    @EventHandler(priority = EventPriority.MONITOR) public void onCmd(PlayerCommandPreprocessEvent e) { act(e.getPlayer()); }
    @EventHandler(priority = EventPriority.MONITOR) public void onHeld(PlayerItemHeldEvent e) { act(e.getPlayer()); }
    @EventHandler(priority = EventPriority.MONITOR) public void onSneak(PlayerToggleSneakEvent e) { act(e.getPlayer()); }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent e) { if (e.getDamager() instanceof Player) act((Player) e.getDamager()); }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) { lastAct.remove(e.getPlayer().getUniqueId()); }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        act(p);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline()) return;
            PlayerData d = data(p.getUniqueId());
            if (d == null || d.isLoadFailed()) return;
            if (onlineP1()) rollover(p, d, dayInt(today()));
            if (!signP1()) return;
            LocalDate now = today();
            int mask = d.periodCount(C_SMASK, month(now));
            if (signedOn(mask, now.getDayOfMonth())) return;
            // D520 sign path: auto / ask / mute
            if (EmberSignPath.shouldAuto(d)) {
                claimSign(p);
                return;
            }
            if (EmberSignPath.shouldMute(d)) return;
            int n = count(mask) + 1;
            ConfirmTokens.sendButton(p, P + "§e今天还没签到 §7· 本月第 " + n + " 次：§f" + rewardFor(n).label() + " ", "[去签到]",
                    "/corerpg p1 sign menu", "打开签到 · 在线页（点格子签到）");
            p.sendMessage(P + "§7不用打命令：主菜单 → §f签到 · 在线");
        }, 100L);
    }

    // ---------------------------------------------------------------- command /corerpg p1 sign|online …

    public boolean cmd(CommandSender s, String[] args) {
        if (args.length >= 5 && "test".equalsIgnoreCase(args[2])) { // admin smoke hook: /corerpg p1 online test <player> <minutes>
            if (!s.hasPermission("corerpg.admin")) { s.sendMessage(ChatColor.RED + "需要 corerpg.admin"); return true; }
            Player t = Bukkit.getPlayerExact(args[3]);
            PlayerData d = t == null ? null : data(t.getUniqueId());
            if (d == null) { s.sendMessage(PO + "玩家不在线"); return true; }
            int add;
            try { add = Integer.parseInt(args[4]); } catch (NumberFormatException e) { s.sendMessage(PO + "分钟须为整数"); return true; }
            if (!rollover(t, d, dayInt(today()))) { s.sendMessage(PO + "时钟异常"); return true; }
            String day = DailyService.today();
            int before = d.periodCount(C_OMIN, day);
            int m = d.addPeriodCount(C_OMIN, day, add);
            plugin.getDataStore().flushMutation(t.getUniqueId());
            notifyOnlineMilestones(t, d, day, before, m); // D518: same path as tick
            plugin.getLogger().info("[P1 online] TEST " + s.getName() + " +" + add + "m -> " + t.getName() + " " + m + "m");
            s.sendMessage(PO + t.getName() + " 今日有效在线 = " + m + " 分钟（测试）");
            return true;
        }
        if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
        Player p = (Player) s;
        String sub = args.length >= 2 ? args[1].toLowerCase(Locale.ROOT) : "sign";
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "menu";
        if (sub.equals("online") || sub.equals("在线")) {
            if (op.equals("claim")) {
                int idx = -1;
                if (args.length >= 4) try { idx = Integer.parseInt(args[3]) - 1; } catch (NumberFormatException ignored) {}
                claimOnline(p, idx);
                return true;
            }
            openMenu(p);
            return true;
        }
        if (op.equals("claim")) claimSign(p);
        else if (op.equals("makeup") || op.equals("补签")) makeup(p);
        else openMenu(p);
        return true;
    }

    public void openMenu(Player p) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (p.isOnline()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "trmenu open " + MENU + " " + p.getName());
        });
    }

    /** legacy {@code /corerpg sign} under P1: the P1 sign-in (never the legacy coin payout) */
    public boolean legacySign(CommandSender s) {
        if (s instanceof Player) claimSign((Player) s);
        return true;
    }

    // ---------------------------------------------------------------- PAPI %corerpg_p1_sign_<key>% / %corerpg_p1_online_<key>%

    public String signPapi(Player p, PlayerData d, String key) {
        if (d == null) return "";
        LocalDate now = today();
        int dom = now.getDayOfMonth();
        String mon = month(now);
        int mask = d.periodCount(C_SMASK, mon), n = count(mask);
        boolean signedToday = signedOn(mask, dom);
        if (key.length() >= 2 && (key.charAt(0) == 'd' || key.charAt(0) == 'k' || key.charAt(0) == 'r') && Character.isDigit(key.charAt(1))) {
            int c;
            try { c = Integer.parseInt(key.substring(1)); } catch (NumberFormatException e) { return ""; }
            if (key.charAt(0) == 'd') return c <= n ? "1" : "0";
            if (key.charAt(0) == 'k') return signP1() && !signedToday && c == n + 1 ? "1" : "0";
            return rewardFor(c).label();
        }
        String day = DailyService.today();
        switch (key) {
            case "on": return signP1() ? "1" : "0";
            case "today": return signedToday ? "1" : "0";
            case "count": return String.valueOf(n);
            case "month": return now.getMonthValue() + " 月";
            case "head": return "§f" + now.getMonthValue() + " 月已签 §e" + n + " §f次" + (signedToday ? " §a· 今天已签" : " §e· 今天未签");
            case "next": return signedToday ? "§7明天：第 " + (n + 1) + " 次 · " + rewardFor(n + 1).label() : "§e今天：第 " + (n + 1) + " 次 · " + rewardFor(n + 1).label();
            case "mkleft": return String.valueOf(Math.max(0, makeupPerMonth - d.periodCount(C_SMK, mon)));
            case "mkcan": return makeupBlock(mask, dom, d.periodCount(C_SMK, mon), makeupPerMonth, d.periodCount(C_SMKD, day), minutesToday(d), makeupNeeds) == null ? "1" : "0";
            case "mk": {
                String why = makeupBlock(mask, dom, d.periodCount(C_SMK, mon), makeupPerMonth, d.periodCount(C_SMKD, day), minutesToday(d), makeupNeeds);
                int miss = earliestMissed(mask, dom);
                return why == null ? "§a可补签 " + now.getMonthValue() + " 月 " + miss + " 日（第 " + (n + 1) + " 次 · " + rewardFor(n + 1).label() + "）" : "§7" + why;
            }
            case "missed": { int c = 0; for (int i = 1; i < dom; i++) if (!signedOn(mask, i)) c++; return String.valueOf(c); }
            default: return "";
        }
    }

    public String onlinePapi(Player p, PlayerData d, String key) {
        if (d == null) return "";
        int mins = minutesToday(d);
        int claimed = d.periodCount(C_OLAST, "all") == dayInt(today()) ? d.periodCount(C_OCLAIM, DailyService.today()) : 0;
        if (key.length() >= 2 && "dkmr".indexOf(key.charAt(0)) >= 0 && Character.isDigit(key.charAt(1))) {
            int i;
            try { i = Integer.parseInt(key.substring(1)) - 1; } catch (NumberFormatException e) { return ""; }
            if (i < 0 || i >= milestones.size()) return key.charAt(0) == 'r' || key.charAt(0) == 'm' ? "—" : "0";
            Milestone ms = milestones.get(i);
            int st = milestoneState(mins, claimed, i, ms.min);
            switch (key.charAt(0)) {
                case 'd': return st == 2 ? "1" : "0";
                case 'k': return st == 1 ? "1" : "0";
                case 'm': return String.valueOf(ms.min);
                default: return ms.r.label();
            }
        }
        switch (key) {
            case "on": return onlineP1() ? "1" : "0";
            case "min": return String.valueOf(mins);
            case "can": { int c = 0; for (int i = 0; i < milestones.size(); i++) if (milestoneState(mins, claimed, i, milestones.get(i).min) == 1) c++; return String.valueOf(c); }
            case "state": {
                if (!onlineP1()) return "§7未开启";
                if (p == null) return "";
                if (p.getWorld() != null && excluded.contains(p.getWorld().getName().toLowerCase(Locale.ROOT))) return "§e这个世界不计在线时长";
                if (autoCombat(p)) return "§a计时中（挂机庭自动战斗）";
                return counts(false, lastAct.get(p.getUniqueId()), System.currentTimeMillis(), idleMinutes) ? "§a计时中" : "§e停表中：" + idleMinutes + " 分钟没有操作";
            }
            case "next": {
                for (int i = 0; i < milestones.size(); i++)
                    if (milestoneState(mins, claimed, i, milestones.get(i).min) == 0) return "§7下一档 " + milestones.get(i).min + " 分钟 · 还差 " + (milestones.get(i).min - mins) + " 分钟";
                return "§a今天的档位都到了";
            }
            default: return "";
        }
    }
}
