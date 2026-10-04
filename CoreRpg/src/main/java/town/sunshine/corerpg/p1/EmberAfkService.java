package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.AfkTierService;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.DailyService;
import town.sunshine.corerpg.NiBridge;
import town.sunshine.corerpg.PlayerData;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * D177 rev 2 P1 挂机庭 = auto-combat idle (自动战斗挂机, docs/design/DESIGN-ember-afk-p1-2026-10-04.md). The stand-still
 * timer of 1.65.9 is gone: a player standing in an unlocked tier of the {@code ember_afk} world fights by itself.
 * <ul>
 *   <li><b>Personal packs</b>: {@code pack} mobs of the tier (main-story MM model, own hp / atk) spawn around the player,
 *   only hit / are hit by their owner; a new pack follows {@code respawn_seconds} after the last one dies. Legacy AFK
 *   monsters do not spawn while this is on.</li>
 *   <li><b>Auto attack</b> every {@code swing_ticks} on the nearest own mob in {@code attack_range}: a real full-charge
 *   player attack, so the P1 melee formula, crits and set procs apply ({@link EmberCombatListener#onMelee}).
 *   <b>Auto 烬斩</b> whenever it is off cooldown and an own mob is in reach (same damage as the manual skill).</li>
 *   <li><b>Survival</b> uses the real P1 HP / M; out-of-combat regen ({@code regen_pct} of max HP / s) only while no own
 *   mob is alive. Death: auto-respawn at the tier pad, combat goes on; {@code death_stop} deaths within
 *   {@code death_window_minutes} stop auto-combat (step down a tier or get stronger).</li>
 *   <li><b>Rewards per kill</b>: each rewarded kill adds the tier's daily amounts to per-resource accumulators;
 *   every {@code daily_kills} accumulated units pay 1 (deterministic, no RNG). {@code daily_kills} rewarded kills a day
 *   (online + offline), then auto-combat stops.</li>
 *   <li><b>Offline</b>: measured online kill rate (EMA of kills per auto-combat minute) × offline minutes ×
 *   {@code offline.ratio}, at the tier last fought, at most {@code offline.max_kills} a day, counted in the daily cap.</li>
 * </ul>
 * Exactly once: settlement advances the accumulators and the kill counter, flushes, then records ledger rows
 * {@code p1afk-<day>/<res><kill#>} (unique per day); a crash in between loses at most the unsettled kills, never doubles.
 */
public final class EmberAfkService implements Listener {

    static final String C_KILL = "p1_afk_kill";    // rewarded kills today (online + offline)
    static final String C_OFFK = "p1_afk_offk";    // offline kills credited today
    static final String C_ACC = "p1_afk_acc_";     // + res: accumulator (units of 1 / daily_kills) today
    static final String C_QUIT = "p1_afk_quit";    // epoch minute of the last quit (period "all"); absent = none
    static final String C_KPM = "p1_afk_kpm";      // measured kills / auto-combat minute × 100 (period "all", EMA)
    static final String C_FMIN = "p1_afk_fmin";    // measured auto-combat minutes (period "all", capped 10000)
    static final String C_LT = "p1_afk_lt";        // tier last auto-fought (period "all")
    static final String LEDGER_PREFIX = "p1afk-";
    static final String[] RES = { "coin", "xp", "shard", "bone", "core", "blank" };
    static final String[] RES_KEY = { "c", "x", "s", "b", "k", "p" };
    static final String[] RES_NAME = { "余烬币", "余烬经验", "余烬碎片", "骨尘", "核心碎片", "装备胚料" };
    static final Set<String> BOUND_MATS = new HashSet<String>(Arrays.asList(
            EmberUpgradeRules.MAT_SHARD, EmberUpgradeRules.MAT_CORE, EmberUpgradeRules.MAT_BONE, EmberUpgradeRules.MAT_BLANK));
    private static final String P = ChatColor.GOLD + "[挂机庭] " + ChatColor.GRAY;

    public static final class Tier {
        public final int n; public final String name, requires, mm; public final double hp, atk, interval;
        final int[] daily = new int[RES.length];
        Tier(int n, String name, String requires, String mm, double hp, double atk, double interval) {
            this.n = n; this.name = name; this.requires = requires; this.mm = mm;
            this.hp = Math.max(1, hp); this.atk = Math.max(0, atk); this.interval = Math.max(0.5, interval);
        }
        public int daily(int r) { return daily[r]; }
    }

    /** One auto-fighting player (in memory; the paid state lives in PlayerData). */
    static final class Fight {
        final UUID u; int tier; boolean on = true;
        final Set<UUID> mobs = new HashSet<UUID>();
        long nextWave, nextSwing, lastHurt, minuteStart, startMs;
        int minuteKills, sessKills, unsettled;
        String pauseNote;
        final Deque<Long> deaths = new ArrayDeque<Long>();
        Fight(UUID u, long now) { this.u = u; this.minuteStart = now; this.startMs = now; }
    }

    static final class Mob {
        final UUID owner; final int tier; final double atk; final long ivMs; long lastHit;
        Mob(UUID owner, int tier, double atk, double iv) { this.owner = owner; this.tier = tier; this.atk = atk; this.ivMs = (long) (iv * 1000); }
    }

    private static volatile EmberAfkService instance;
    public static EmberAfkService get() { return instance; }

    private final CoreRpgPlugin plugin;
    private volatile boolean enabled, legacyPayouts;
    private volatile String world = "ember_afk";
    private volatile int pack = 3, swingTicks = 14, dailyKills = 2400, settleEvery = 100, deathStop = 3, deathWindowMin = 10;
    private volatile double respawnSec = 4, spawnRadius = 4, attackRange = 3.2, leash = 14, regenPct = 0.08;
    private volatile double offRatio = 0.25; private volatile int offMax = 1200, offHours = 12, offMinFight = 5;
    private volatile List<Tier> tiers = Collections.emptyList();
    private int taskId = -1;
    private long ticks;
    private final Map<UUID, Fight> fights = new HashMap<UUID, Fight>();
    private final Map<UUID, Mob> mobs = new HashMap<UUID, Mob>();
    private final Set<UUID> autoOff = new HashSet<UUID>(); // players who switched auto-combat off (this session)
    private boolean spawning;

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
            dailyKills = Math.max(0, s.getInt("daily_kills", 2400));
            settleEvery = Math.max(1, s.getInt("settle_every", 100));
            ConfigurationSection c = s.getConfigurationSection("combat");
            if (c != null) {
                pack = Math.max(1, Math.min(6, c.getInt("pack", 3)));
                respawnSec = Math.max(1, c.getDouble("respawn_seconds", 4));
                spawnRadius = Math.max(2, c.getDouble("spawn_radius", 4));
                attackRange = Math.max(1.5, c.getDouble("attack_range", 3.2));
                swingTicks = Math.max(11, c.getInt("swing_ticks", 14)); // ≥ the 1.6 /s full-charge cap
                leash = Math.max(6, c.getDouble("leash", 14));
                regenPct = Math.max(0, c.getDouble("regen_pct", 0.08));
                deathStop = Math.max(1, c.getInt("death_stop", 3));
                deathWindowMin = Math.max(1, c.getInt("death_window_minutes", 10));
            }
            ConfigurationSection o = s.getConfigurationSection("offline");
            if (o != null) {
                offRatio = Math.max(0, Math.min(1, o.getDouble("ratio", 0.25)));
                offMax = Math.max(0, o.getInt("max_kills", 1200));
                offHours = Math.max(0, o.getInt("max_hours", 12));
                offMinFight = Math.max(1, o.getInt("min_fight_minutes", 5));
            }
            for (Map<?, ?> t : s.getMapList("tiers")) {
                try {
                    Tier x = new Tier(Integer.parseInt(String.valueOf(t.get("n"))), String.valueOf(t.get("name")),
                            String.valueOf(t.get("requires")).toLowerCase(Locale.ROOT), String.valueOf(t.get("mm")),
                            num(t.get("hp"), 100), num(t.get("atk"), 2), num(t.get("interval"), 2.5));
                    for (int r = 0; r < RES.length; r++) x.daily[r] = (int) Math.max(0, num(t.get(RES[r]), 0));
                    ts.add(x);
                } catch (RuntimeException e) {
                    plugin.getLogger().warning("[P1 afk] bad tier row " + t + ": " + e);
                }
            }
        }
        Collections.sort(ts, (a, b) -> a.n - b.n);
        tiers = Collections.unmodifiableList(ts);
        if (taskId != -1) { Bukkit.getScheduler().cancelTask(taskId); taskId = -1; }
        if (enabled) taskId = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 40L, 2L).getTaskId();
        else stopAll("挂机庭自动战斗已关闭");
        plugin.getLogger().info("[P1 afk] " + (enabled ? "on" : "off") + " auto-combat world=" + world + " pack=" + pack + " respawn=" + respawnSec
                + "s swing=" + swingTicks + "t daily_kills=" + dailyKills + " offline=" + offRatio + " max " + offMax + "/" + offHours + "h tiers=" + ts.size()
                + " legacy_payouts=" + legacyPayouts);
    }

    private static double num(Object o, double def) {
        if (o == null) return def;
        try { return Double.parseDouble(String.valueOf(o)); } catch (NumberFormatException e) { return def; }
    }

    /** D177 rules apply: config on and P1 active. */
    public boolean p1() { return enabled && EmberMode.active(); }
    /** D180 rev 2: is auto-combat running for this player right now (main thread)? */
    public boolean fighting(UUID u) { return u != null && fights.containsKey(u); }
    public String world() { return world; }
    public List<Tier> tiers() { return tiers; }
    public int dailyKills() { return dailyKills; }

    /** P1: the legacy AFK mobs (MM {@code corerpg mmgive}/{@code mmxp}) and kill coin / kill XP pay nothing in the AFK world. */
    public static boolean blocksLegacyPayout(World w) {
        EmberAfkService a = instance;
        return a != null && a.p1() && !a.legacyPayouts && w != null && w.getName().equalsIgnoreCase(a.world);
    }

    private boolean inWorld(Entity e) { return e != null && e.getWorld() != null && e.getWorld().getName().equalsIgnoreCase(world); }

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

    private int tierHere(Player p) {
        AfkTierService a = plugin.getAfkTierService();
        return a == null ? 0 : a.tierNumberAt(p.getLocation());
    }

    // ---------------------------------------------------------------- pure math (unit-tested)

    /** Units paid when an accumulator holds {@code acc} (1 unit = {@code per} accumulated); [paid, remainder]. */
    static int[] settleUnits(int acc, int per) {
        if (acc <= 0 || per <= 0) return new int[] { 0, Math.max(0, acc) };
        return new int[] { acc / per, acc % per };
    }

    /** Kills still rewardable now: daily room, and the offline sub-cap for offline credit. */
    static int grantable(int want, int killsToday, int dailyCap, boolean offline, int offToday, int offlineCap) {
        int room = Math.max(0, dailyCap - killsToday);
        if (offline) room = Math.min(room, Math.max(0, offlineCap - offToday));
        return Math.max(0, Math.min(want, room));
    }

    /** Offline kills for {@code offMinutes} logged out at a measured rate of {@code kpm100}/100 kills per minute. */
    static int offlineKills(int offMinutes, int kpm100, double ratio, int maxHours) {
        if (offMinutes <= 0 || kpm100 <= 0 || ratio <= 0 || maxHours <= 0) return 0;
        long m = Math.min(offMinutes, maxHours * 60L);
        return (int) Math.min(Integer.MAX_VALUE, (long) Math.floor(m * (kpm100 / 100.0) * ratio));
    }

    /** EMA of kills / minute ×100 (first minutes weigh more until 5 are measured). */
    static int ema(int prev100, int minutes, int killsThisMinute) {
        double a = minutes < 5 ? 1.0 / (minutes + 1) : 0.2;
        return (int) Math.round((1 - a) * prev100 + a * killsThisMinute * 100.0);
    }

    static int epochMinute(long ms) { return (int) (ms / 60000L); }

    // ---------------------------------------------------------------- the loop (every 2 ticks)

    private void tick() {
        ticks += 2;
        if (!p1()) { if (!fights.isEmpty()) stopAll(null); return; }
        World w = Bukkit.getWorld(world);
        if (w == null) return;
        long now = System.currentTimeMillis();
        if (ticks % 100 == 0) purgeLegacy(w);
        for (Player p : w.getPlayers()) {
            UUID u = p.getUniqueId();
            if (p.isDead() || !p.isOnline()) continue;
            Fight f = fights.get(u);
            if (f == null) {
                if (autoOff.contains(u)) { regenIdle(p, null, now); continue; }
                f = start(p, now);
                if (f == null) { regenIdle(p, null, now); continue; }
            }
            step(p, f, now);
        }
        // owners who left the world / logged out
        for (Iterator<Map.Entry<UUID, Fight>> it = fights.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<UUID, Fight> en = it.next();
            Player p = Bukkit.getPlayer(en.getKey());
            if (p != null && p.isOnline() && inWorld(p)) continue;
            end(p, en.getValue(), false);
            it.remove();
        }
    }

    private Fight start(Player p, long now) {
        PlayerData d = data(p.getUniqueId());
        if (d == null || tierFor(d) == null) return null;
        if (dailyKills <= 0 || d.periodCount(C_KILL, DailyService.today()) >= dailyKills) return null;
        Fight f = new Fight(p.getUniqueId(), now);
        f.tier = tierHere(p);
        f.nextWave = now + 1500L;
        fights.put(p.getUniqueId(), f);
        p.sendMessage(P + "§a自动战斗开始 §7· 角色会自己打附近的怪、冷却好了自动放烬斩、战利品直接进账"
                + " §8（挂机庭菜单可暂停）");
        return f;
    }

    private void step(Player p, Fight f, long now) {
        PlayerData d = data(p.getUniqueId());
        if (d == null) return;
        String day = DailyService.today();
        if (d.periodCount(C_KILL, day) >= dailyKills) {
            stop(p, f, "§a今日挂机收益已满（" + dailyKills + "/" + dailyKills + " 只）· 明天 0 点重置。§e想更快变强还是打主线本。");
            return;
        }
        int here = tierHere(p);
        if (here != f.tier) { clearMobs(f); f.tier = here; f.nextWave = now + 1500L; f.pauseNote = null; }
        Tier t = tier(f.tier);
        if (t == null || !flag(d, t.requires)) {
            if (f.pauseNote == null) {
                f.pauseNote = t == null ? "这里不是挂机层" : "本层需要" + requiresLabel(t.n);
                p.sendMessage(P + "§e自动战斗暂停：" + f.pauseNote + "（走回已解锁的层继续）");
            }
            regenIdle(p, f, now);
            return;
        }
        // prune dead / gone / leashed mobs
        Location pl = p.getLocation();
        for (Iterator<UUID> it = f.mobs.iterator(); it.hasNext(); ) {
            Entity e = Bukkit.getEntity(it.next());
            boolean gone = !(e instanceof LivingEntity) || e.isDead() || !e.isValid() || e.getWorld() != p.getWorld();
            if (!gone && e.getLocation().distanceSquared(pl) > leash * leash) { e.remove(); gone = true; }
            if (gone) { if (e != null) mobs.remove(e.getUniqueId()); it.remove(); }
        }
        if (f.mobs.isEmpty()) {
            if (f.nextWave == 0) f.nextWave = now + (long) (respawnSec * 1000);
            regenIdle(p, f, now);
            if (now >= f.nextWave) { spawnPack(p, f, t); f.nextWave = 0; }
        } else {
            LivingEntity near = null; double best = Double.MAX_VALUE;
            for (UUID id : f.mobs) {
                Entity e = Bukkit.getEntity(id);
                if (!(e instanceof LivingEntity)) continue;
                if (ticks % 20 == 0 && e instanceof Creature) ((Creature) e).setTarget(p);
                double ds = e.getLocation().distanceSquared(pl);
                if (ds < best) { best = ds; near = (LivingEntity) e; }
            }
            if (near != null && best <= attackRange * attackRange) {
                EmberLoadoutService ls = plugin.getEmberLoadouts();
                EmberPlayerState st = ls == null ? null : ls.state(p.getUniqueId());
                if (st != null && st.skillCdUntil <= now && autoSkill(p, f, st, near, now))
                    f.nextSwing = Math.max(f.nextSwing, now + 550L); // the skill hit owns the i-frames; swing right after
                if (now >= f.nextSwing) {
                    f.nextSwing = now + swingTicks * 50L;
                    swing(p, near);
                }
            }
        }
        if (ticks % 40 == 0) { // live ActionBar: tier · today's kills / cap · kill speed
            long ms = Math.max(60000L, now - f.startMs);
            String bar = "§6挂机 §f" + t.name + " §7· 今日 §f" + d.periodCount(C_KILL, day) + "/" + dailyKills + " §7只 · §f"
                    + Math.round(f.sessKills * 3600000.0 / ms) + " §7只/小时" + (f.deaths.isEmpty() ? "" : " §c· 阵亡 " + f.deaths.size() + "/" + deathStop);
            p.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, new net.md_5.bungee.api.chat.TextComponent(bar));
        }
        if (now - f.minuteStart >= 60000L) { // measured rate (for offline credit)
            int min = Math.min(10000, d.periodCount(C_FMIN, "all"));
            int k = d.periodCount(C_KPM, "all");
            d.addPeriodCount(C_KPM, "all", ema(k, min, f.minuteKills) - k);
            if (min < 10000) d.addPeriodCount(C_FMIN, "all", 1);
            d.addPeriodCount(C_LT, "all", f.tier - d.periodCount(C_LT, "all"));
            f.minuteKills = 0;
            f.minuteStart = now;
        }
    }

    private void swing(Player p, LivingEntity le) {
        double attr = 1.0;
        AttributeInstance ai = p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        if (ai != null) attr = ai.getValue();
        ItemStack hand = p.getInventory().getItemInMainHand();
        int sharp = hand == null ? 0 : hand.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
        le.damage(attr + ChargeEstimator.sharpnessBonus(sharp), p); // a real full-charge player attack → onMelee
    }

    /** Same damage, radius, arc and targets as the manual 烬斩 (SkillService.castEmberSlashP1), own mobs only, silent. */
    private boolean autoSkill(Player p, Fight f, EmberPlayerState st, LivingEntity near, long now) {
        EmberMode mode = EmberMode.get();
        EmberLoadoutService ls = plugin.getEmberLoadouts();
        if (mode == null || ls == null) return false;
        double range = mode.d("skill.radius", 3.5);
        int maxTargets = Math.max(1, mode.i("skill.max_targets", 5));
        int cd = Math.max(0, mode.i("skill.cooldown_seconds", 8));
        face(p, near);
        Location eye = p.getEyeLocation();
        Vector look = eye.getDirection().normalize();
        double cosHalf = Math.cos(Math.toRadians(Math.max(1.0, mode.d("skill.arc_degrees", 100.0)) / 2.0));
        List<LivingEntity> ts = new ArrayList<LivingEntity>();
        for (UUID id : f.mobs) {
            Entity e = Bukkit.getEntity(id);
            if (!(e instanceof LivingEntity) || e.isDead()) continue;
            Vector to = ((LivingEntity) e).getEyeLocation().toVector().subtract(eye.toVector());
            double dist = to.length();
            if (dist > range || dist < 0.05 || look.dot(to.normalize()) < cosHalf) continue;
            ts.add((LivingEntity) e);
        }
        if (ts.isEmpty()) return false;
        double dmg = EmberFormula.skill(EmberMode.tables(), ls.get(p).b);
        String tag = String.format(Locale.ROOT, "A12 烬斩(自动) 1.5×B=%.2f", dmg);
        for (int i = 0; i < ts.size() && i < maxTargets; i++) EmberCombatListener.dealP1(p, ts.get(i), dmg, EmberSetEngine.Kind.SKILL, tag);
        st.skillCdUntil = now + cd * 1000L;
        return true;
    }

    private static void face(Player p, Entity e) {
        Location l = p.getLocation();
        Vector v = e.getLocation().toVector().add(new Vector(0, 1.0, 0)).subtract(p.getEyeLocation().toVector());
        if (v.lengthSquared() < 1e-4) return;
        l.setDirection(v);
        p.teleport(l, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    private void regenIdle(Player p, Fight f, long now) {
        if (regenPct <= 0 || ticks % 10 != 0) return; // every 0.5 s
        if (f != null && !f.mobs.isEmpty()) return;
        if (f != null && now - f.lastHurt < 1000L) return;
        double max = EmberHeal.maxHp(p);
        if (p.getHealth() < max) EmberHeal.heal(p, max * regenPct * 0.5, "D177 挂机庭脱战回复");
    }

    private void spawnPack(Player p, Fight f, Tier t) {
        Location c = p.getLocation();
        for (int i = 0; i < pack; i++) {
            Location at = spot(c, i);
            spawning = true;
            Entity e;
            try { e = EmberRunBridges.spawnMythic(t.mm, at, plugin.getLogger()); } finally { spawning = false; }
            if (!(e instanceof LivingEntity)) continue;
            LivingEntity le = (LivingEntity) e;
            AttributeInstance a = le.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (a != null) a.setBaseValue(t.hp);
            le.setHealth(Math.min(t.hp, le.getMaxHealth()));
            le.setRemoveWhenFarAway(false);
            le.setCustomName("§7[" + t.name + "] §f" + (le.getCustomName() == null ? le.getName() : le.getCustomName()) + " §8· " + p.getName());
            le.setCustomNameVisible(false);
            if (le instanceof Creature) ((Creature) le).setTarget(p);
            mobs.put(le.getUniqueId(), new Mob(p.getUniqueId(), t.n, t.atk, t.interval));
            f.mobs.add(le.getUniqueId());
        }
    }

    private Location spot(Location c, int i) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int tries = 0; tries < 8; tries++) {
            double ang = (2 * Math.PI * i / Math.max(1, pack)) + r.nextDouble(-0.6, 0.6);
            double dist = spawnRadius * (0.75 + 0.25 * r.nextDouble());
            Location l = c.clone().add(Math.cos(ang) * dist, 0, Math.sin(ang) * dist);
            for (int dy : new int[] { 0, 1, -1, 2, -2 }) {
                Block feet = l.getWorld().getBlockAt(l.getBlockX(), c.getBlockY() + dy, l.getBlockZ());
                if (!feet.getType().isSolid() && !feet.getRelative(0, 1, 0).getType().isSolid() && feet.getRelative(0, -1, 0).getType().isSolid()
                        && !feet.isLiquid())
                    return new Location(c.getWorld(), feet.getX() + 0.5, feet.getY(), feet.getZ() + 0.5);
            }
        }
        return c.clone().add(1.5, 0, 0);
    }

    private void clearMobs(Fight f) {
        for (UUID id : f.mobs) {
            mobs.remove(id);
            Entity e = Bukkit.getEntity(id);
            if (e != null) e.remove();
        }
        f.mobs.clear();
    }

    /** Legacy AFK monsters (MM spawners / natural) are removed while P1 auto-combat is on; own mobs stay. */
    private void purgeLegacy(World w) {
        for (LivingEntity le : w.getLivingEntities()) {
            if (mobs.containsKey(le.getUniqueId())) continue;
            if (le instanceof Monster || le instanceof Slime) le.remove();
        }
    }

    // ---------------------------------------------------------------- start / stop

    private void stop(Player p, Fight f, String msg) {
        end(p, f, true);
        fights.remove(f.u);
        if (p != null && msg != null) p.sendMessage(P + msg);
    }

    private void end(Player p, Fight f, boolean online) {
        clearMobs(f);
        PlayerData d = data(f.u);
        if (d != null && f.unsettled > 0) settle(p, f.u, d, DailyService.today(), online ? "本轮" : null);
        if (p != null && p.isOnline() && f.sessKills > 0) {
            long ms = Math.max(1, System.currentTimeMillis() - f.startMs);
            p.sendMessage(P + "本次自动战斗 " + (ms / 60000) + " 分钟 · 击杀 " + f.sessKills + " 只（约 " + (int) Math.round(f.sessKills * 3600000.0 / ms) + " 只/小时）");
        }
    }

    private void stopAll(String msg) {
        for (Fight f : new ArrayList<Fight>(fights.values())) stop(Bukkit.getPlayer(f.u), f, msg);
        fights.clear();
    }

    /** Menu toggle: {@code /corerpg afk fight [on|off]} (no argument = flip). */
    public void toggle(Player p, String arg) {
        if (!p1()) { p.sendMessage(P + "§c挂机庭未开放。"); return; }
        UUID u = p.getUniqueId();
        boolean wantOn = "on".equalsIgnoreCase(arg) || (!"off".equalsIgnoreCase(arg) && autoOff.contains(u));
        if (wantOn) {
            autoOff.remove(u);
            if (!inWorld(p)) p.sendMessage(P + "已设为自动战斗 · 进入挂机庭（枢纽菜单 → 挂机庭）就会开始。");
            else if (!fights.containsKey(u) && start(p, System.currentTimeMillis()) == null) p.sendMessage(P + "§e现在不能开打：" + statusWord(p, data(u)));
        } else {
            autoOff.add(u);
            Fight f = fights.get(u);
            if (f != null) stop(p, f, "§e自动战斗已暂停 §7· 菜单里再点一次继续。");
            else p.sendMessage(P + "§e自动战斗已暂停。");
        }
    }

    // ---------------------------------------------------------------- kills → rewards

    @EventHandler(priority = EventPriority.HIGH)
    public void onMobDeath(EntityDeathEvent e) {
        Mob m = mobs.remove(e.getEntity().getUniqueId());
        if (m == null) return;
        e.getDrops().clear();
        e.setDroppedExp(0);
        Fight f = fights.get(m.owner);
        if (f != null) f.mobs.remove(e.getEntity().getUniqueId());
        Player p = Bukkit.getPlayer(m.owner);
        if (f == null || p == null || !p.isOnline()) return;
        if (e.getEntity().getKiller() != p) return; // only the owner's own hits count (no env / admin / foreign kills)
        PlayerData d = data(m.owner);
        Tier t = tier(m.tier);
        if (d == null || t == null) return;
        String day = DailyService.today();
        if (grantable(1, d.periodCount(C_KILL, day), dailyKills, false, 0, 0) <= 0) return;
        credit(d, day, t, 1);
        f.sessKills++; f.minuteKills++; f.unsettled++;
        if (f.unsettled >= settleEvery || d.periodCount(C_KILL, day) >= dailyKills) { settle(p, m.owner, d, day, null); f.unsettled = 0; }
    }

    private void credit(PlayerData d, String day, Tier t, int kills) {
        d.addPeriodCount(C_KILL, day, kills);
        for (int r = 0; r < RES.length; r++) if (t.daily[r] > 0) d.addPeriodCount(C_ACC + RES[r], day, t.daily[r] * kills);
    }

    /** Pays every whole unit in the accumulators. Counter first (flushed), then the once-only ledger rows. */
    void settle(Player p, UUID u, PlayerData d, String day, String note) {
        EmberRunService rs = EmberRunService.get();
        if (rs == null || dailyKills <= 0) return;
        int kill = d.periodCount(C_KILL, day);
        int[] pay = new int[RES.length];
        boolean any = false;
        for (int r = 0; r < RES.length; r++) {
            int[] s = settleUnits(d.periodCount(C_ACC + RES[r], day), dailyKills);
            if (s[0] <= 0) continue;
            pay[r] = s[0];
            d.addPeriodCount(C_ACC + RES[r], day, -s[0] * dailyKills);
            any = true;
        }
        if (!any) return;
        plugin.getDataStore().flushMutation(u); // a crash after this loses the units, never doubles them
        String run = LEDGER_PREFIX + day;
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < RES.length; r++) {
            if (pay[r] <= 0) continue;
            String res = r == 0 ? "coin:" + pay[r] : r == 1 ? "xp:" + pay[r]
                    : "bmat:" + (r == 2 ? EmberUpgradeRules.MAT_SHARD : r == 3 ? EmberUpgradeRules.MAT_BONE : r == 4 ? EmberUpgradeRules.MAT_CORE : EmberUpgradeRules.MAT_BLANK) + ":" + pay[r];
            rs.grantRow(u, run, RES_KEY[r] + kill, res);
            sb.append(" §f").append(RES_NAME[r]).append(" +").append(pay[r]);
        }
        plugin.getLogger().info("[P1 afk] " + (p == null ? u.toString() : p.getName()) + " settle @kill " + kill + " ->" + ChatColor.stripColor(sb.toString()) + " (" + day + ")");
        if (p != null && p.isOnline()) {
            rs.deliverQuiet(p);
            p.sendMessage(P + (note == null ? "" : note) + "§a挂机战利品到账：" + sb + " §8· 今日 " + kill + "/" + dailyKills + " 只");
        }
    }

    // ---------------------------------------------------------------- mob ↔ player rules

    /** Own mobs hit only their owner, at the tier's fixed atk and interval (like main-story run mobs); ×M follows at HIGHEST. */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onMobHit(EntityDamageByEntityEvent e) {
        Entity src = e.getDamager();
        if (src instanceof Projectile && ((Projectile) src).getShooter() instanceof Entity) src = (Entity) ((Projectile) src).getShooter();
        Mob m = mobs.get(src.getUniqueId());
        if (m != null) {
            if (!(e.getEntity() instanceof Player) || !e.getEntity().getUniqueId().equals(m.owner)) { e.setCancelled(true); return; }
            long now = System.currentTimeMillis();
            if (now - m.lastHit < m.ivMs - 50L) { e.setCancelled(true); return; }
            m.lastHit = now;
            e.setDamage(m.atk);
            Fight f = fights.get(m.owner);
            if (f != null) f.lastHurt = now;
            return;
        }
        Mob v = mobs.get(e.getEntity().getUniqueId());
        if (v != null && !src.getUniqueId().equals(v.owner)) e.setCancelled(true); // no kill-stealing / pooling on another's pack
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTarget(EntityTargetEvent e) {
        Mob m = mobs.get(e.getEntity().getUniqueId());
        if (m != null && e.getTarget() != null && !e.getTarget().getUniqueId().equals(m.owner)) e.setCancelled(true);
    }

    /** Only own packs spawn in the AFK world while P1 auto-combat is on (no legacy spawners → no mob stacking). */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent e) {
        if (spawning || !p1() || !inWorld(e.getEntity())) return;
        if (e.getEntity() instanceof Monster || e.getEntity() instanceof Slime) e.setCancelled(true);
    }

    /** Environmental damage to own mobs (fall, suffocation, fire) is ignored; they die to their owner only. */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onMobEnv(EntityDamageEvent e) {
        if (e instanceof EntityDamageByEntityEvent || !mobs.containsKey(e.getEntity().getUniqueId())) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Fight f = fights.get(p.getUniqueId());
        if (f == null) return;
        long now = System.currentTimeMillis();
        clearMobs(f);
        f.deaths.addLast(now);
        while (!f.deaths.isEmpty() && now - f.deaths.peekFirst() > deathWindowMin * 60000L) f.deaths.removeFirst();
        if (f.deaths.size() >= deathStop) {
            autoOff.add(p.getUniqueId());
            stop(p, f, "§c" + deathWindowMin + " 分钟内阵亡 " + f.deaths.size() + " 次，自动战斗已停止。§7这一层对你现在的装备太难："
                    + "去低一层挂，或回主线本刷装备/强化后再来（菜单里可重新开始）。");
        } else {
            f.nextWave = now + 5000L;
            p.sendMessage(P + "§e阵亡（" + f.deaths.size() + "/" + deathStop + "）· 自动复活后继续。§7装备不够时死得多，连续 " + deathStop + " 次会停下。");
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> { if (p.isOnline() && p.isDead()) p.spigot().respawn(); }, 20L);
    }

    // ---------------------------------------------------------------- account-bound P1 materials (D73 no trading)

    /** P1 upgrade materials and P1 gear (both can carry 挂机庭 value: bound shards / cores enhance gear, bound blanks forge it) */
    private boolean boundMat(ItemStack s) {
        if (s == null || s.getType() == Material.AIR || !EmberMode.active()) return false;
        NiBridge ni = plugin.getNiBridge();
        String id = ni == null ? null : ni.getNiId(s);
        if (id != null && BOUND_MATS.contains(id)) return true;
        EmberLoadoutService ls = plugin.getEmberLoadouts();
        return ls != null && ls.items().hasData(s);
    }

    /** item frames / armor stands as a mailbox */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onFrame(org.bukkit.event.player.PlayerInteractEntityEvent e) {
        if (!enabled || !(e.getRightClicked() instanceof org.bukkit.entity.ItemFrame)) return;
        if (!boundMat(e.getPlayer().getInventory().getItemInMainHand()) && !boundMat(e.getPlayer().getInventory().getItemInOffHand())) return;
        e.setCancelled(true);
        e.getPlayer().sendMessage(P + "§c余烬材料和装备账号绑定，不能放进展示框。");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onStand(org.bukkit.event.player.PlayerArmorStandManipulateEvent e) {
        if (!enabled || !boundMat(e.getPlayerItem())) return;
        e.setCancelled(true);
        e.getPlayer().sendMessage(P + "§c余烬材料和装备账号绑定，不能放到盔甲架上。");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        if (!enabled || !boundMat(e.getItemDrop().getItemStack())) return;
        e.setCancelled(true);
        e.getPlayer().sendMessage(P + "§c余烬材料和装备账号绑定，不能丢出或转给别人（材料存仓库，装备放装备库）。");
    }

    /** world storage another account could open: chests / shulkers / hoppers / droppers / furnaces … and entity storage
     *  (horse, mule, storage minecart). Not the player's own inventory / crafting grid / ender chest / workbench, not plugin GUIs. */
    static boolean blockContainer(InventoryHolder h, InventoryType t) {
        if (t == InventoryType.CRAFTING || t == InventoryType.PLAYER || t == InventoryType.CREATIVE || t == InventoryType.ENDER_CHEST
                || t == InventoryType.WORKBENCH || h instanceof Player) return false;
        return h instanceof org.bukkit.block.BlockState || h instanceof org.bukkit.block.DoubleChest || h instanceof Entity;
    }

    /** Putting P1 materials into world containers (chests, shulkers, hoppers, horses …) — the alt-account mailbox. */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onContainerClick(InventoryClickEvent e) {
        if (!enabled || e.getView().getTopInventory() == null) return;
        if (!blockContainer(e.getView().getTopInventory().getHolder(), e.getView().getTopInventory().getType())) return;
        boolean top = e.getRawSlot() >= 0 && e.getRawSlot() < e.getView().getTopInventory().getSize();
        boolean in = (top && boundMat(e.getCursor()))                                   // place into the container
                || (!top && e.isShiftClick() && boundMat(e.getCurrentItem()))            // shift-move into it
                || (top && e.getClick() == org.bukkit.event.inventory.ClickType.NUMBER_KEY && e.getHotbarButton() >= 0
                    && boundMat(e.getWhoClicked().getInventory().getItem(e.getHotbarButton()))); // hotbar swap into it
        if (!in) return;
        e.setCancelled(true);
        e.getWhoClicked().sendMessage(P + "§c余烬材料和装备账号绑定，不能放进箱子 / 容器（材料存仓库，装备放装备库）。");
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onContainerDrag(InventoryDragEvent e) {
        if (!enabled || !boundMat(e.getOldCursor())) return;
        if (!blockContainer(e.getView().getTopInventory().getHolder(), e.getView().getTopInventory().getType())) return;
        int size = e.getView().getTopInventory().getSize();
        for (int raw : e.getRawSlots()) if (raw < size) { e.setCancelled(true); return; }
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
        Player p = e.getPlayer();
        Fight f = fights.remove(p.getUniqueId());
        if (f != null) end(p, f, false);
        autoOff.remove(p.getUniqueId());
        stampQuit(p);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Fight f = fights.remove(e.getPlayer().getUniqueId());
        if (f != null) end(e.getPlayer(), f, true);
    }

    /** onDisable: quit events come after plugins are disabled — settle, remove packs, stamp everyone online now. */
    public void shutdown() {
        if (taskId != -1) { Bukkit.getScheduler().cancelTask(taskId); taskId = -1; }
        for (Fight f : new ArrayList<Fight>(fights.values())) {
            clearMobs(f);
            PlayerData d = data(f.u);
            if (d != null && f.unsettled > 0) settle(null, f.u, d, DailyService.today(), null);
        }
        fights.clear();
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
            String day = DailyService.today();
            if (d.periodCount(C_KILL, day) > 0) settle(p, p.getUniqueId(), d, day, null); // anything left from a crash / disable
            int q = d.periodCount(C_QUIT, "all");
            if (q <= 0) return;
            d.addPeriodCount(C_QUIT, "all", -q); // consumed once: reconnect cycling never re-credits the same time
            plugin.getDataStore().flushMutation(p.getUniqueId());
            int off = epochMinute(System.currentTimeMillis()) - q;
            if (d.periodCount(C_FMIN, "all") < offMinFight) {
                if (off >= 60 && tierFor(d) != null)
                    p.sendMessage(P + "离线挂机按你在线自动战斗的实测击杀速度折算：先在挂机庭在线打满 " + offMinFight + " 分钟。");
                return;
            }
            Tier t = tier(d.periodCount(C_LT, "all"));
            if (t == null || !flag(d, t.requires)) return;
            int want = offlineKills(off, d.periodCount(C_KPM, "all"), offRatio, offHours);
            int give = grantable(want, d.periodCount(C_KILL, day), dailyKills, true, d.periodCount(C_OFFK, day), offMax);
            if (give <= 0) {
                if (want > 0 && d.periodCount(C_OFFK, day) >= offMax) p.sendMessage(P + "今日离线挂机已折满 " + offMax + " 只（在线自动战斗还能打到每日上限）。");
                return;
            }
            d.addPeriodCount(C_OFFK, day, give);
            credit(d, day, t, give);
            settle(p, p.getUniqueId(), d, day, "离线 " + (off / 60) + " 小时 " + (off % 60) + " 分（" + t.name + "，按实测 " + fmt(d.periodCount(C_KPM, "all") / 100.0)
                    + " 只/分 × " + (int) Math.round(offRatio * 100) + "% = " + give + " 只）→ ");
        }, 80L);
    }

    private static String fmt(double v) { return String.format(Locale.ROOT, "%.1f", v); }

    // ---------------------------------------------------------------- PAPI %corerpg_p1_afk_<key>%

    private String statusWord(Player p, PlayerData d) {
        if (!p1()) return "§7未开启";
        if (d == null || tierFor(d) == null) return "§7首通 Q01 后开放";
        if (d.periodCount(C_KILL, DailyService.today()) >= dailyKills) return "§a今日已满 · 明天 0 点重置";
        Fight f = fights.get(p.getUniqueId());
        if (f != null) return f.pauseNote != null ? "§e暂停：" + f.pauseNote : "§a自动战斗中";
        if (autoOff.contains(p.getUniqueId())) return "§e已暂停（点击继续）";
        return inWorld(p) ? "§e待机" : "§7进入挂机庭自动开打";
    }

    public String papi(Player p, PlayerData d, String key) {
        if (d == null) return "";
        String day = DailyService.today();
        Tier top = tierFor(d);
        if (key.length() == 2 && key.charAt(0) == 't') { // t1..t4: 1 = unlocked
            Tier x = tier(key.charAt(1) - '0');
            return x != null && flag(d, x.requires) ? "1" : "0";
        }
        int kills = d.periodCount(C_KILL, day);
        Fight f = fights.get(p.getUniqueId());
        long now = System.currentTimeMillis();
        double kph = f == null || f.sessKills <= 0 ? d.periodCount(C_KPM, "all") * 0.6 : f.sessKills * 3600000.0 / Math.max(60000L, now - f.startMs);
        Tier cur = f != null && tier(f.tier) != null ? tier(f.tier) : tier(d.periodCount(C_LT, "all")) != null ? tier(d.periodCount(C_LT, "all")) : top;
        switch (key) {
            case "on": return p1() ? "1" : "0";
            case "tier": return top == null ? "§7未解锁（首通 Q01 后开放）" : "§f" + top.name + " §7(" + "首通 " + top.requires.toUpperCase(Locale.ROOT) + ")";
            case "here": return cur == null ? "—" : "§f" + cur.name;
            case "state": return statusWord(p, d);
            case "kills": return String.valueOf(kills);
            case "today": return "§f" + kills + "/" + dailyKills + " 只 §7· 离线已折 " + d.periodCount(C_OFFK, day) + "/" + offMax;
            case "kph": return kph <= 0 ? "§7还没测到" : "§f" + Math.round(kph) + " 只/小时";
            case "session": return f == null ? "—" : "§f" + f.sessKills + " 只 · " + ((now - f.startMs) / 60000) + " 分钟";
            case "loot": { // loot / hour at the current (or last measured) rate and tier
                if (cur == null || kph <= 0) return "§7开打后显示";
                StringBuilder sb = new StringBuilder();
                for (int r = 0; r < RES.length; r++) {
                    if (cur.daily[r] <= 0) continue;
                    double v = kph * cur.daily[r] / Math.max(1, dailyKills);
                    sb.append(sb.length() == 0 ? "§f" : " §7· §f").append(RES_NAME[r]).append(' ').append(v >= 10 ? String.valueOf(Math.round(v)) : fmt(v));
                }
                return sb + " §7/小时";
            }
            case "cap": {
                if (cur == null) return "—";
                StringBuilder sb = new StringBuilder();
                for (int r = 0; r < RES.length; r++) if (cur.daily[r] > 0)
                    sb.append(sb.length() == 0 ? "§f" : " §7· §f").append(RES_NAME[r]).append(' ').append(cur.daily[r]);
                return sb + " §7（" + cur.name + "，" + dailyKills + " 只封顶）";
            }
            case "offline": return d.periodCount(C_FMIN, "all") < offMinFight ? "§7在线自动战斗满 " + offMinFight + " 分钟后开启"
                    : "§f实测 " + fmt(d.periodCount(C_KPM, "all") / 100.0) + " 只/分 × " + (int) Math.round(offRatio * 100) + "% · 每日最多 " + offMax + " 只";
            case "next": return statusWord(p, d);
            case "rounds": return String.valueOf(kills);
            default: return "";
        }
    }

    /** One status line for the legacy /corerpg afk list under P1. */
    public String statusLine(Player p) {
        PlayerData d = data(p.getUniqueId());
        return papi(p, d, "today") + " §7· " + papi(p, d, "state");
    }
}
