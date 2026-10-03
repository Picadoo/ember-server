package town.sunshine.corerpg.p1;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import town.sunshine.corerpg.DailyService;
import town.sunshine.corerpg.NiBridge;
import town.sunshine.corerpg.PlayerData;

/**
 * D139 国庆限时活动「烟火庙会」. Everything comes from ember-v1-festival.yml: the window (start ≤ now &lt; end, ISO-8601 with
 * offset), the event currency (a NeigeItems item), the event dungeon (a MapDef injected into EmberRunService's maps as
 * {@code events:}), the festival charm (own slot: never the main hand / selected charm / a set piece; H / D capped at a
 * standard T3 charm; effect 烟火迸发), the event title and trail. The event pays no 余烬币 / items / marks and costs no
 * stamina, so the main-line economy is unchanged (p2econ). Counters (PlayerData period counters):
 * <ul>
 *   <li>p3_fest_entry_&lt;id&gt;@&lt;day&gt; — event entries today (counted when the player is inside the instance)</li>
 *   <li>p3_fest_charm@all / p3_fest_charm_on@all — charm owned / worn (charm id is the counter suffix)</li>
 *   <li>p3_fest_trail_&lt;trail id&gt;@all — event trail bought (EmberCosmetics#earned reads it)</li>
 *   <li>p2_title_&lt;id&gt;@all — event clears (= the event title, via EmberCosmetics#onRaidClear)</li>
 * </ul>
 */
public final class EmberFestival implements Listener {

    public static final String FILE = "ember-v1-festival.yml";
    static final String C_ENTRY = "p3_fest_entry_";
    static final String C_OWN = "p3_fest_charm_";
    static final String C_ON = "p3_fest_charmon_";
    static final String C_TRAIL = "p3_fest_trail_";
    private static final String P = ChatColor.GOLD + "[国庆] " + ChatColor.GRAY;
    private static final DateTimeFormatter SHOW = DateTimeFormatter.ofPattern("M月d日 HH:mm");

    private static volatile EmberFestival instance;
    public static EmberFestival get() { return instance; }

    private final JavaPlugin plugin;
    private EmberRunService runs;

    // ---- config (reloaded by load())
    boolean enabled;
    String id = "fest", name = "活动";
    long startMs, endMs;
    String coinNi = "", coinName = "活动币";
    int dailyEntries = 3;
    String requires = "q04";
    String charmId = "fest_charm", charmName = "活动护符";
    double charmHp, charmDef, coef, radius = 3, icd = 6;
    int targets = 3, priceEvent = 60, afterCoin = 15000, afterBadge = 300;
    String titleId = "", titleLabel = "", trailId = "", trailLabel = "";
    int trailPrice = 30;
    /** D146 leftover-coin exchange: title id / label / price, 余烬徽 rate + per-character cap, trail after the event */
    String memoId = "", memoLabel = "";
    int memoPrice = 0, badgeRate = 0, badgeCap = 0;
    boolean trailAfter;
    double dropChance = 0.25;
    int dropElite = 2, dropTreasure = 3, clearBonus = 10;
    String dungeonKey = "";
    Map<String, Object> dungeonRaw = Collections.emptyMap();
    Map<?, ?> theme = Collections.emptyMap();

    private final Map<UUID, Long> lastBurst = new HashMap<UUID, Long>();
    private final Map<UUID, Integer> bursts = new HashMap<UUID, Integer>();
    private boolean bursting;

    private EmberFestival(JavaPlugin plugin) { this.plugin = plugin; }

    /** creates the singleton on first use and (re)reads the file; called from EmberRunService#load */
    static synchronized EmberFestival load(JavaPlugin plugin) {
        if (instance == null) instance = new EmberFestival(plugin);
        instance.reload();
        return instance;
    }

    void setRuns(EmberRunService r) { runs = r; }

    @SuppressWarnings("unchecked")
    private void reload() {
        File f = new File(plugin.getDataFolder(), FILE);
        if (!f.isFile()) {
            try { plugin.saveResource(FILE, false); } catch (IllegalArgumentException ignored) { }
        }
        Map<?, ?> root = Collections.emptyMap();
        if (f.isFile()) {
            try (Reader r = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
                Object o = new Yaml().load(r);
                if (o instanceof Map) root = (Map<?, ?>) o;
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "[P1 fest] cannot read " + FILE + ": " + t.getMessage());
            }
        }
        enabled = Boolean.TRUE.equals(root.get("enabled"));
        id = str(root.get("id"), "fest");
        name = str(root.get("name"), "活动");
        startMs = time(root.get("start"), Long.MAX_VALUE);
        endMs = time(root.get("end"), Long.MIN_VALUE);
        if (endMs <= startMs) { enabled = false; plugin.getLogger().warning("[P1 fest] " + FILE + ": end must be after start → event off"); }
        Map<?, ?> cur = map(root.get("currency"));
        coinNi = str(cur.get("ni"), "");
        coinName = str(cur.get("name"), "活动币");
        dailyEntries = (int) num(root.get("daily_entries"), 3);
        requires = str(root.get("requires"), "q04").toLowerCase(Locale.ROOT);
        Map<?, ?> ch = map(root.get("charm"));
        charmId = str(ch.get("id"), "fest_charm");
        charmName = str(ch.get("name"), "活动护符");
        charmHp = num(ch.get("hp"), 0);
        charmDef = num(ch.get("def"), 0);
        Map<?, ?> ef = map(ch.get("effect"));
        coef = Math.max(0, num(ef.get("coef"), 0));
        targets = Math.max(0, (int) num(ef.get("targets"), 3));
        radius = Math.max(0.5, num(ef.get("radius"), 3));
        icd = Math.max(0.5, num(ef.get("icd"), 6));
        priceEvent = (int) num(ch.get("price_event"), 60);
        Map<?, ?> af = map(ch.get("after"));
        afterCoin = (int) num(af.get("price_coin"), 0);
        afterBadge = (int) num(af.get("price_badge"), 0);
        Map<?, ?> ti = map(root.get("title"));
        titleId = str(ti.get("id"), "");
        titleLabel = str(ti.get("label"), "");
        Map<?, ?> tr = map(root.get("trail"));
        trailId = str(tr.get("id"), "");
        trailLabel = str(tr.get("label"), "");
        trailPrice = (int) num(tr.get("price_event"), 30);
        Map<?, ?> ex = map(root.get("exchange"));
        Map<?, ?> mo = map(ex.get("memo"));
        memoId = str(mo.get("id"), "");
        memoLabel = str(mo.get("label"), "");
        memoPrice = (int) num(mo.get("price"), 0);
        Map<?, ?> bx = map(ex.get("badge"));
        badgeRate = (int) num(bx.get("rate"), 0);
        badgeCap = (int) num(bx.get("cap"), 0);
        trailAfter = Boolean.TRUE.equals(ex.get("trail_after"));
        Map<?, ?> dr = map(root.get("drops"));
        dropChance = num(dr.get("mob_chance"), 0.25);
        dropElite = (int) num(dr.get("elite"), 2);
        dropTreasure = (int) num(dr.get("treasure"), 3);
        clearBonus = (int) num(dr.get("clear"), 10);
        Map<?, ?> dg = map(root.get("dungeon"));
        dungeonKey = str(dg.get("key"), "").toLowerCase(Locale.ROOT);
        dungeonRaw = dg.isEmpty() || dungeonKey.isEmpty() ? Collections.<String, Object>emptyMap() : (Map<String, Object>) dg;
        theme = map(root.get("map_theme"));
        plugin.getLogger().info("[P1 fest] " + id + " " + name + " enabled=" + enabled + " " + show(startMs) + " → " + show(endMs)
                + " (now " + phase() + ") charm " + charmId + " hp " + charmHp + " def " + charmDef + " burst " + coef + "×B ×" + targets
                + " r" + radius + " icd " + icd + "s · dungeon " + dungeonKey);
    }

    /** the runs-yml {@code events:} section built from the festival file (empty when the event is off) */
    Map<String, Object> eventsSection() {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        if (!dungeonRaw.isEmpty()) m.put(dungeonKey, dungeonRaw);
        return m;
    }

    // ------------------------------------------------------------------ window

    public boolean active() { long now = System.currentTimeMillis(); return enabled && now >= startMs && now < endMs; }
    public boolean ended() { return System.currentTimeMillis() >= endMs; }
    String phase() { long now = System.currentTimeMillis(); return !enabled ? "off" : now < startMs ? "not started" : now < endMs ? "open" : "ended"; }
    static String show(long ms) {
        if (ms == Long.MAX_VALUE || ms == Long.MIN_VALUE) return "?";
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(ms), DailyService.zone()).format(SHOW);
    }
    String windowText() { return show(startMs) + " — " + show(endMs) + "（北京时间）"; }
    String leftText() {
        long left = endMs - System.currentTimeMillis();
        if (left <= 0) return "已结束";
        long h = left / 3600000L;
        return h >= 24 ? "剩 " + h / 24 + " 天 " + h % 24 + " 小时" : "剩 " + Math.max(1, h) + " 小时" + (h < 1 ? "内" : "");
    }

    public boolean isEventKey(String key) { return key != null && !dungeonKey.isEmpty() && dungeonKey.equalsIgnoreCase(key); }

    // ------------------------------------------------------------------ entries

    int entriesToday(PlayerData d) { return d == null ? 0 : d.periodCount(C_ENTRY + id, DailyService.today()); }
    void countEntry(PlayerData d) { if (d != null) d.addPeriodCount(C_ENTRY + id, DailyService.today(), 1); }

    /** enter checks for one party member; null = ok */
    String entryProblem(Player p, PlayerData d) {
        if (!enabled) return name + " 未开启";
        long now = System.currentTimeMillis();
        if (now < startMs) return name + " 还没开始（" + windowText() + "）";
        if (now >= endMs) return name + " 已结束（" + windowText() + "）";
        if (runs != null && !runs.progressFlag(d, requires)) return p.getName() + " 未开放活动本（需本人首通 " + requires.toUpperCase(Locale.ROOT) + "）";
        if (dailyEntries > 0 && entriesToday(d) >= dailyEntries) return p.getName() + " 今天的活动本次数已满（" + entriesToday(d) + "/" + dailyEntries + "，0 点重置）";
        return null;
    }

    // ------------------------------------------------------------------ charm slot

    public boolean owns(PlayerData d) { return d != null && d.periodCount(C_OWN + charmId, "all") > 0; }
    public boolean worn(PlayerData d) { return owns(d) && d.periodCount(C_ON + charmId, "all") > 0; }
    boolean trailOwned(PlayerData d, String trail) { return d != null && d.periodCount(C_TRAIL + trail, "all") > 0; }
    /** the event trail and the D146 memo title: owned through the shop counter (C_TRAIL + id) */
    public boolean isFestTrail(String cosmeticId) {
        return cosmeticId != null && (!trailId.isEmpty() && cosmeticId.equalsIgnoreCase(trailId) || !memoId.isEmpty() && cosmeticId.equalsIgnoreCase(memoId));
    }
    static final String C_XBADGE = "p3_fest_xbadge_"; // + event id, period "all": 余烬徽 already converted (D146 cap)
    int badgeConverted(PlayerData d) { return d == null ? 0 : d.periodCount(C_XBADGE + id, "all"); }

    /** {H, D} of the worn festival charm (P1 mode only, before the clamp in EmberLoadout), or null when not worn */
    double[] wornStats(Player p) {
        if (p == null || runs == null || !EmberMode.isP1(p)) return null;
        PlayerData d = runs.dataOf(p.getUniqueId());
        if (!worn(d)) return null;
        return new double[] {charmHp, charmDef};
    }

    // ------------------------------------------------------------------ run hooks (EmberRunService)

    /** a run mob died (any P1 run). 烟火迸发 for the killer's worn charm; 国庆币 drops inside the event dungeon. */
    void onRunMobDeath(EmberRunDirector d, String role, LivingEntity dead) {
        if (runs == null || d == null || dead == null) return;
        Player k = dead.getKiller();
        if (k != null && !d.s.committed.contains(k.getUniqueId())) k = null;
        if (k == null && d.s.committed.size() == 1) k = Bukkit.getPlayer(d.s.committed.iterator().next()); // DoT / burst final blow, solo
        if (k == null || !k.isOnline()) return;
        if (d.def.event && !"boss".equals(role) && !"add".equals(role)) {
            int n = "elite".equals(role) ? dropElite : "treasure".equals(role) ? dropTreasure : (Math.random() < dropChance ? 1 : 0);
            if (n > 0) giveCoins(k, n, false);
        }
        if (!bursting && coef > 0 && targets > 0) burst(k, dead, d);
    }

    private void burst(Player p, LivingEntity dead, EmberRunDirector d) {
        PlayerData pd = runs.dataOf(p.getUniqueId());
        if (!worn(pd)) return;
        long now = System.currentTimeMillis();
        Long last = lastBurst.get(p.getUniqueId());
        if (last != null && now - last < (long) (icd * 1000)) return;
        lastBurst.put(p.getUniqueId(), now); // cooldown first: a burst kill never starts another burst
        Location c = dead.getLocation();
        List<LivingEntity> cand = new ArrayList<LivingEntity>();
        for (Entity en : c.getWorld().getNearbyEntities(c, radius, radius, radius)) {
            if (!(en instanceof LivingEntity) || en == dead || en instanceof Player) continue;
            LivingEntity le = (LivingEntity) en;
            if (le.isDead() || !le.isValid() || le.isInvulnerable() || !runs.isRunMob(le.getUniqueId())) continue;
            if (le.getLocation().distance(c) > radius) continue;
            cand.add(le);
        }
        final Location cc = c;
        cand.sort((a, b) -> Double.compare(a.getLocation().distanceSquared(cc), b.getLocation().distanceSquared(cc)));
        double b = runs.loadouts() == null ? 0 : runs.loadouts().get(p).b;
        double amt = coef * b;
        c.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, c.clone().add(0, 1, 0), 40, 0.6, 0.6, 0.6, 0.12);
        c.getWorld().spawnParticle(Particle.REDSTONE, c.clone().add(0, 1.2, 0), 20, 0.5, 0.5, 0.5, 0); // red dust
        c.getWorld().playSound(c, Sound.ENTITY_FIREWORK_LARGE_BLAST, 0.8f, 1.0f);
        c.getWorld().playSound(c, Sound.ENTITY_FIREWORK_TWINKLE, 0.6f, 1.2f);
        String tag = String.format(Locale.ROOT, "烟火迸发 %.2f×B(%.2f)=%.2f", coef, b, amt);
        int hit = 0;
        bursting = true;
        try {
            for (LivingEntity le : cand) {
                if (hit >= targets) break;
                if (amt > 0) EmberCombatListener.dealP1(p, le, amt, EmberSetEngine.Kind.EXPLOSION, tag);
                hit++;
            }
        } finally {
            bursting = false;
        }
        Integer n = bursts.get(p.getUniqueId());
        bursts.put(p.getUniqueId(), (n == null ? 0 : n) + 1);
        plugin.getLogger().info("[P1 fest] burst " + p.getName() + " " + d.s.runId + " " + tag + " hit " + hit);
    }

    /** boss of the event dungeon killed by the party: per eligible player instead of the main-line settlement */
    void onClear(EmberRunSession s, UUID u, EmberCosmetics cos) {
        PlayerData pd = runs.dataOf(u);
        Player p = Bukkit.getPlayer(u);
        int before = EmberCosmetics.raidClears(pd, id);
        if (cos != null) cos.onRaidClear(p, pd, id); // the clear tally doubles as the event title (D83 pattern)
        else pd.addPeriodCount(EmberCosmetics.C_EARNED + id, "all", 1);
        runs.flushData(u);
        if (p != null && p.isOnline()) {
            if (clearBonus > 0) giveCoins(p, clearBonus, true);
            p.sendMessage(P + "§a通关「" + dungeonRaw.getOrDefault("name", name) + "」§7· 活动通关第 " + (before + 1) + " 次 · 今日次数 "
                    + entriesToday(pd) + "/" + dailyEntries + " · " + coinName + " ×" + countCoins(p) + "（背包）");
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, new String[]{"[活动商店]", "/corerpg p1 fest", "用" + coinName + "换「" + charmName + "」和限时足迹", "GOLD"});
        }
        runs.log().info("[P1 fest] " + s.runId + " clear " + u + " (#" + (before + 1) + ")");
    }

    /** the run's extra event finished in the event dungeon: the chest pays `treasure` 国庆币 to everyone inside (mobs pay on death) */
    void onExtraDone(EmberRunSession s) {
        boolean chest = s.extra == EmberRunRules.Extra.CHEST;
        for (UUID u : s.committed) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline() || s.world == null || !p.getWorld().getName().equals(s.world)) continue;
            if (chest && dropTreasure > 0) giveCoins(p, dropTreasure, true);
            p.sendMessage(P + "§6「" + s.extra.label + "」完成" + (chest && dropTreasure > 0 ? " §7· " + coinName + " +" + dropTreasure : ""));
        }
    }

    private void giveCoins(Player p, int n, boolean quiet) {
        NiBridge ni = ((town.sunshine.corerpg.CoreRpgPlugin) plugin).getNiBridge();
        if (ni == null || coinNi.isEmpty()) return;
        ni.giveNiItem(p, coinNi, n);
        if (!quiet) p.sendMessage(P + "§6+" + n + " " + coinName);
    }

    int countCoins(Player p) {
        NiBridge ni = ((town.sunshine.corerpg.CoreRpgPlugin) plugin).getNiBridge();
        return ni == null || coinNi.isEmpty() ? 0 : ni.countInInventory(p, coinNi);
    }

    // ------------------------------------------------------------------ commands /corerpg p1 fest …

    boolean command(CommandSender s, String[] args) {
        String a = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "menu";
        if ("coins".equals(a) || "reload".equals(a) || "mapbuild".equals(a) || "reset".equals(a)) return admin(s, a, args);
        if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
        Player p = (Player) s;
        PlayerData d = runs.dataOf(p.getUniqueId());
        switch (a) {
            case "menu":
                Bukkit.getScheduler().runTaskLater(plugin, () -> { if (p.isOnline()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "trmenu open ember_p1_fest " + p.getName()); }, 1L);
                return true;
            case "enter":
                if (!active()) { p.sendMessage(P + ChatColor.RED + name + " " + (ended() ? "已结束" : "未开放") + "（" + windowText() + "）"); return true; }
                return runs.tryEnter(p, dungeonKey);
            case "buy": return buy(p, d, args.length >= 4 ? args[3].toLowerCase(Locale.ROOT) : "charm", args.length >= 5 ? args[4].toLowerCase(Locale.ROOT) : "");
            case "equip":
            case "unequip":
                if (!owns(d)) { p.sendMessage(P + ChatColor.RED + "还没有「" + charmName + "」"); return true; }
                boolean on = "equip".equals(a);
                int cur = d.periodCount(C_ON + charmId, "all");
                d.addPeriodCount(C_ON + charmId, "all", (on ? 1 : 0) - cur);
                runs.flushData(p.getUniqueId());
                if (runs.loadouts() != null) runs.loadouts().invalidate(p.getUniqueId());
                p.sendMessage(P + (on ? "§a已佩戴「" + charmName + "§a」§7（独立的活动护符位：不占主手和已选护符，不算套装）· " + effectText()
                        : "已取下「" + charmName + "」"));
                return true;
            default:
                status(p, d);
                return true;
        }
    }

    String effectText() {
        String st = (charmHp > 0 ? "生命 +" + fmt(charmHp) + " " : "") + (charmDef > 0 ? "防御 +" + fmt(charmDef) + " " : "");
        return (st.isEmpty() ? "不加属性 · " : st + "· ") + "烟火迸发：你打过的敌人倒下时炸开烟火，伤及周围 " + fmt(radius) + " 格内最多 " + targets
                + " 个敌人（" + fmt(coef) + "×攻击），每 " + fmt(icd) + " 秒最多一次；只在主线本类副本里生效";
    }

    private void status(Player p, PlayerData d) {
        p.sendMessage(P + "§6" + name + " §7" + windowText() + " · " + (active() ? "§a进行中 " + leftText() : ended() ? "§c已结束" : "§e未开始"));
        p.sendMessage(P + "活动本今日 " + entriesToday(d) + "/" + dailyEntries + " · 活动通关 " + EmberCosmetics.raidClears(d, id) + " 次 · "
                + coinName + " " + countCoins(p) + " · 「" + charmName + "」" + (worn(d) ? "§a佩戴中" : owns(d) ? "§7已拥有" : "§8未拥有")
                + (bursts.containsKey(p.getUniqueId()) ? " §7· 本次上线迸发 " + bursts.get(p.getUniqueId()) + " 次" : ""));
        p.sendMessage(P + "§7" + effectText());
    }

    private boolean buy(Player p, PlayerData d, String what, String pay) {
        if ("memo".equals(what) || "badge".equals(what)) return exchange(p, d, what, pay); // D146
        boolean charm = !"trail".equals(what);
        if (runs != null && !runs.progressFlag(d, requires)) { p.sendMessage(P + ChatColor.RED + "需要本人首通 " + requires.toUpperCase(Locale.ROOT) + " 才能买"); return true; }
        if (charm && owns(d)) { p.sendMessage(P + "已经有「" + charmName + "」了"); return true; }
        if (!charm && trailOwned(d, trailId)) { p.sendMessage(P + "已经有足迹「" + trailLabel + "§7」了"); return true; }
        long now = System.currentTimeMillis();
        if (!enabled && !charm || now < startMs) { p.sendMessage(P + ChatColor.RED + name + " 还没开始（" + windowText() + "）"); return true; }
        NiBridge ni = ((town.sunshine.corerpg.CoreRpgPlugin) plugin).getNiBridge();
        if (now < endMs && enabled) { // event shop: 国庆币 only
            int price = charm ? priceEvent : trailPrice;
            int have = countCoins(p);
            if (have < price || ni == null || !ni.consumeExact(p, coinNi, price)) {
                p.sendMessage(P + ChatColor.RED + coinName + "不够（要 " + price + "，背包里 " + have + "）· 活动本小怪和首领掉落");
                return true;
            }
            grant(p, d, charm, coinName + " " + price);
            return true;
        }
        if (!charm && trailAfter) { // D146: after the event, only with 国庆币 already earned
            int have = countCoins(p);
            if (have < trailPrice || ni == null || !ni.consumeExact(p, coinNi, trailPrice)) {
                p.sendMessage(P + ChatColor.RED + coinName + "不够（要 " + trailPrice + "，背包里 " + have + "）· 活动已结束，不再掉落");
                return true;
            }
            grant(p, d, false, coinName + " " + trailPrice);
            return true;
        }
        if (!charm) { p.sendMessage(P + ChatColor.RED + "限时足迹只在活动期间出售（" + windowText() + "）"); return true; }
        // after the event: permanent price, 余烬币 or 余烬徽
        boolean badge = "badge".equals(pay) || "徽".equals(pay);
        if (badge) {
            if (afterBadge <= 0 || EmberSeason.badges(d) < afterBadge) { p.sendMessage(P + ChatColor.RED + "余烬徽不够（要 " + afterBadge + "，有 " + EmberSeason.badges(d) + "）"); return true; }
            d.addPeriodCount(EmberSeason.C_BADGE, "all", -afterBadge);
            grant(p, d, true, "余烬徽 " + afterBadge);
        } else {
            if (afterCoin <= 0 || !d.takeCoin(afterCoin)) { p.sendMessage(P + ChatColor.RED + "余烬币不够（要 " + afterCoin + "，有 " + d.getCoin() + "）"); return true; }
            grant(p, d, true, "余烬币 " + afterCoin);
        }
        return true;
    }

    /** D146: leftover 国庆币 → the memo title, or → 余烬徽 (rate : 1, capped per character). Works from start on, forever. */
    private boolean exchange(Player p, PlayerData d, String what, String amount) {
        if (System.currentTimeMillis() < startMs) { p.sendMessage(P + ChatColor.RED + name + " 还没开始（" + windowText() + "）"); return true; }
        NiBridge ni = ((town.sunshine.corerpg.CoreRpgPlugin) plugin).getNiBridge();
        int have = countCoins(p);
        if ("memo".equals(what)) {
            if (memoId.isEmpty() || memoPrice <= 0) { p.sendMessage(P + "纪念称号未开放。"); return true; }
            if (trailOwned(d, memoId)) { p.sendMessage(P + "已经有称号「" + memoLabel + "§7」了"); return true; }
            if (have < memoPrice || ni == null || !ni.consumeExact(p, coinNi, memoPrice)) {
                p.sendMessage(P + ChatColor.RED + coinName + "不够（要 " + memoPrice + "，背包里 " + have + "）");
                return true;
            }
            d.addPeriodCount(C_TRAIL + memoId, "all", 1);
            runs.flushData(p.getUniqueId());
            runs.log().info("[P1 fest] exchange " + p.getName() + " memo " + memoId + " for " + memoPrice + " " + coinName);
            p.sendMessage(P + "§a获得称号「" + memoLabel + "§a」§7（只做展示）· 付了 " + coinName + " " + memoPrice);
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, new String[]{"[装上称号]", "/corerpg p1 title " + memoId, "装上纪念称号", "GREEN"});
            return true;
        }
        if (badgeRate <= 0 || badgeCap <= 0) { p.sendMessage(P + "余烬徽兑换未开放。"); return true; }
        int left = badgeCap - badgeConverted(d);
        if (left <= 0) { p.sendMessage(P + "余烬徽兑换已到上限（每人 " + badgeCap + " 徽）。剩下的" + coinName + "可以换纪念称号或留作纪念。"); return true; }
        int want = "all".equals(amount) || amount.isEmpty() ? left : (int) Math.max(0, num(amount, 0));
        int n = Math.min(Math.min(want, left), have / badgeRate);
        if (n <= 0) { p.sendMessage(P + ChatColor.RED + coinName + "不够（" + badgeRate + " 枚换 1 徽，背包里 " + have + "）"); return true; }
        if (ni == null || !ni.consumeExact(p, coinNi, n * badgeRate)) { p.sendMessage(P + ChatColor.RED + "扣除" + coinName + "失败，什么都没换"); return true; }
        d.addPeriodCount(C_XBADGE + id, "all", n);
        d.addPeriodCount(EmberSeason.C_BADGE, "all", n);
        runs.flushData(p.getUniqueId());
        runs.log().info("[P1 fest] exchange " + p.getName() + " " + (n * badgeRate) + " " + coinName + " → " + n + " badges (" + badgeConverted(d) + "/" + badgeCap + ")");
        p.sendMessage(P + "§a" + coinName + " " + (n * badgeRate) + " → 余烬徽 +" + n + " §7（共 " + EmberSeason.badges(d) + "；兑换额度 " + badgeConverted(d) + "/" + badgeCap + "）· 余烬徽只买外观");
        return true;
    }

    private void grant(Player p, PlayerData d, boolean charm, String paid) {
        if (charm) {
            d.addPeriodCount(C_OWN + charmId, "all", 1);
            d.addPeriodCount(C_ON + charmId, "all", 1 - d.periodCount(C_ON + charmId, "all")); // worn right away
        } else d.addPeriodCount(C_TRAIL + trailId, "all", 1);
        runs.flushData(p.getUniqueId());
        if (runs.loadouts() != null) runs.loadouts().invalidate(p.getUniqueId());
        runs.log().info("[P1 fest] buy " + p.getName() + " " + (charm ? charmId : trailId) + " for " + paid);
        p.sendMessage(P + "§a获得" + (charm ? "「" + charmName + "§a」（已佩戴）" : "足迹「" + trailLabel + "§a」") + " §7· 付了 " + paid);
        if (charm) p.sendMessage(P + "§7" + effectText());
        else town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, new String[]{"[装上足迹]", "/corerpg p1 trail " + trailId, "装上限时足迹", "GREEN"});
    }

    private boolean admin(CommandSender s, String a, String[] args) {
        if (!s.hasPermission("corerpg.admin")) { s.sendMessage(P + "无权限"); return true; }
        if ("reload".equals(a)) { runs.load(); s.sendMessage(P + "已重读 " + FILE + "（进行中的局不变）· " + phase()); return true; }
        if ("mapbuild".equals(a)) {
            String base = args.length >= 4 ? args[3] : "ember_daily_tide_v1";
            new town.sunshine.corerpg.p1.map.FestMapTheme(plugin).build(s, str(dungeonRaw.get("template"), "ember_fest_" + id), base, theme);
            return true;
        }
        Player t = args.length >= 4 ? Bukkit.getPlayerExact(args[3]) : null;
        if (t == null) { s.sendMessage(P + "/corerpg p1 fest coins|reset <玩家> [数量]"); return true; }
        PlayerData d = runs.dataOf(t.getUniqueId());
        if ("reset".equals(a)) { // test helper: forget charm / trail / today's entries
            for (String c : new String[]{C_OWN + charmId, C_ON + charmId, C_TRAIL + trailId}) d.addPeriodCount(c, "all", -d.periodCount(c, "all"));
            d.addPeriodCount(C_ENTRY + id, DailyService.today(), -entriesToday(d));
            runs.flushData(t.getUniqueId());
            if (runs.loadouts() != null) runs.loadouts().invalidate(t.getUniqueId());
            s.sendMessage(P + "已重置 " + t.getName() + " 的活动护符 / 足迹 / 今日次数");
            return true;
        }
        int n = args.length >= 5 ? (int) num(args[4], 0) : 0;
        if (n > 0) giveCoins(t, n, false);
        s.sendMessage(P + t.getName() + " " + coinName + " " + countCoins(t));
        return true;
    }

    // ------------------------------------------------------------------ PAPI %corerpg_p1_fest_*%

    String papi(Player p, PlayerData d, String key) {
        switch (key) {
            case "open": return active() ? "1" : "0";
            case "after": return ended() ? "1" : "0";
            case "name": return name;
            case "window": return windowText();
            case "state": return !enabled ? "§8未开启" : active() ? "§a进行中 · " + leftText() : ended() ? "§c已结束" : "§e未开始";
            case "entries": return entriesToday(d) + "/" + dailyEntries;
            case "clears": return String.valueOf(EmberCosmetics.raidClears(d, id));
            case "coins": return p == null ? "0" : String.valueOf(countCoins(p));
            case "coinname": return coinName;
            case "req": return runs != null && runs.progressFlag(d, requires) ? "§a已满足" : "§c需本人首通 " + requires.toUpperCase(Locale.ROOT);
            case "charm": return worn(d) ? "§a已拥有 · 佩戴中" : owns(d) ? "§7已拥有 · 未佩戴" : "§8未拥有";
            case "charm_own": return owns(d) ? "1" : "0";
            case "charm_name": return charmName;
            case "charm_effect": return effectText();
            case "charm_price": return active() ? priceEvent + " " + coinName
                    : ended() ? afterCoin + " 余烬币 或 " + afterBadge + " 余烬徽（活动结束后的常驻价）" : "活动开始后出售";
            case "trail": return trailOwned(d, trailId) ? "§a已拥有" : active() || ended() && trailAfter ? "§7" + trailPrice + " " + coinName + (active() ? "" : "（用剩下的）") : "§8限时（已下架）";
            case "memo": return memoId.isEmpty() ? "§8未开放" : trailOwned(d, memoId) ? "§a已拥有" : "§7" + memoPrice + " " + coinName;
            case "memo_name": return memoLabel;
            case "xbadge": return badgeCap <= 0 ? "§8未开放" : badgeConverted(d) >= badgeCap ? "§a已换满 " + badgeCap + " 徽" : "§7已换 " + badgeConverted(d) + "/" + badgeCap + " 徽 · " + badgeRate + " 枚换 1 徽";
            case "xrate": return String.valueOf(badgeRate);
            case "xcap": return String.valueOf(badgeCap);
            case "trail_name": return trailLabel;
            case "title": return EmberCosmetics.raidClears(d, id) > 0 ? "§a已获得 " + titleLabel : active() ? "§7活动期间通关一次即得 " + titleLabel : "§8限时（已结束）";
            default: return null;
        }
    }

    // ------------------------------------------------------------------ helpers

    private static String fmt(double v) { return v == Math.rint(v) ? String.valueOf((long) v) : String.format(Locale.ROOT, "%.2f", v).replaceAll("0+$", ""); }
    private static String str(Object o, String def) { return o == null ? def : String.valueOf(o); }
    private static double num(Object o, double def) {
        if (o instanceof Number) return ((Number) o).doubleValue();
        if (o != null) try { return Double.parseDouble(String.valueOf(o)); } catch (NumberFormatException ignored) { }
        return def;
    }
    private static Map<?, ?> map(Object o) { return o instanceof Map ? (Map<?, ?>) o : Collections.emptyMap(); }
    static long time(Object o, long def) {
        if (o == null) return def;
        if (o instanceof java.util.Date) return ((java.util.Date) o).getTime(); // SnakeYAML parses unquoted timestamps
        try { return OffsetDateTime.parse(String.valueOf(o)).toInstant().toEpochMilli(); } catch (RuntimeException e) { return def; }
    }
}
