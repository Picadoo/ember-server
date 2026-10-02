package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.MailService;
import town.sunshine.corerpg.NiBridge;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.ProgressService;
import town.sunshine.corerpg.StaminaService;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ember-v1.0-P1 G04 unified settlement for the P1 main maps (book §9.1–9.4, §20.5):
 * entry (reserve → create instance → commit / release), RunSession lifecycle, one boss-gated settlement per eligible
 * participant through the idempotent RewardLedger, delivery with retry (inventory full → mail for materials, P1 items
 * wait in the ledger for /corerpg p1 claim), first clears, forge marks, starter kit and target family.
 */
public final class EmberRunService implements Listener {

    public static final String P = ChatColor.GOLD + "[余烬] " + ChatColor.GRAY;
    public static final String FILE = "ember-v1-runs.yml";
    static final String C_TARGET = "p1_target";
    static final String C_MARK = "p1_mark_t";
    static final String C_STARTER = "p1_starter";
    static final String C_UNLOCK = "p1_unlock_";
    static final String C_FIRST = EmberForgeService.FLAG_PREFIX; // p1_first_clear_<map>@<content version>

    private static volatile EmberRunService instance;

    private final CoreRpgPlugin plugin;
    private final EmberLoadoutService loadouts;
    private final EmberRunStore store;
    private EmberRunMaps maps;
    private final SecureRandom rnd = new SecureRandom();

    private final Map<String, EmberRunSession> sessions = new LinkedHashMap<String, EmberRunSession>();
    private final Map<String, EmberRunDirector> byWorld = new HashMap<String, EmberRunDirector>();
    private final Map<UUID, EmberRunDirector> byEntity = new HashMap<UUID, EmberRunDirector>();
    private final Map<UUID, Object[]> passes = new HashMap<UUID, Object[]>(); // uuid → {mapKey, until ms}
    private final Map<UUID, Long> starterChecked = new HashMap<UUID, Long>();
    private int skillDepth;
    private final KnockbackGuard kbGuard = new KnockbackGuard();
    private int spawnBlocks;
    private int taskId = -1;

    public EmberRunService(CoreRpgPlugin plugin, EmberLoadoutService loadouts, EmberItemStore mirror) {
        this.plugin = plugin;
        this.loadouts = loadouts;
        this.store = new EmberRunStore(plugin, mirror);
        instance = this;
        load();
    }

    public static EmberRunService get() { return instance; }

    Logger log() { return plugin.getLogger(); }

    public EmberRunMaps maps() { return maps; }

    // ------------------------------------------------------------------ config

    public void load() {
        File f = new File(plugin.getDataFolder(), FILE);
        if (!f.isFile()) {
            try { plugin.saveResource(FILE, false); } catch (IllegalArgumentException ignored) { }
        }
        Map<?, ?> root = Collections.emptyMap();
        try (Reader r = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
            Object o = new Yaml().load(r);
            if (o instanceof Map) root = (Map<?, ?>) o;
        } catch (Throwable t) {
            log().log(Level.WARNING, "[P1 run] cannot read " + FILE + ": " + t.getMessage());
        }
        maps = EmberRunMaps.parse(root);
        for (String e : maps.validate()) log().warning("[P1 run] " + FILE + ": " + e);
        log().info("[P1 run] maps " + maps.maps.keySet() + " cost=" + maps.cost + " party=" + maps.partyMin + ".." + maps.partyMax);
    }

    /** Called once after enable: restart recovery (§20.5) + the director ticker. */
    public void start() {
        recoverAfterRestart();
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, 20L, 5L);
    }

    public void shutdown() {
        if (taskId >= 0) Bukkit.getScheduler().cancelTask(taskId);
        taskId = -1;
        for (EmberRunDirector d : new ArrayList<EmberRunDirector>(byWorld.values())) d.finish();
        byWorld.clear();
        byEntity.clear();
    }

    // ------------------------------------------------------------------ legacy gates (E02 / E11)

    /** True inside a P1 main-map instance: old MM drops, DP clear rewards, quest / kill / xp payouts must not run. */
    public static boolean blocksLegacy(World w) {
        if (w == null) return false;
        EmberRunService s = instance;
        if (s != null && s.maps != null) return s.maps.byWorld(w.getName()) != null;
        return w.getName().toLowerCase(Locale.ROOT).startsWith("dungeon_emberq0");
    }

    public static boolean blocksLegacy(Entity e) { return e != null && blocksLegacy(e.getWorld()); }

    // ------------------------------------------------------------------ PlayerData counters

    private PlayerData data(UUID id) { return plugin.getDataStore().get(id); }

    public boolean unlocked(PlayerData d, EmberRunMaps.MapDef m) {
        return m.requires == null || m.requires.isEmpty() || d.periodCount(C_UNLOCK + m.key, "all") > 0;
    }

    /** B2.143: first clear of a map key at its configured content version (false when the map is not defined yet). */
    public boolean firstClearedKey(PlayerData d, String key) {
        EmberRunMaps.MapDef m = key == null ? null : maps.byKey(key);
        return m != null && firstCleared(d, m);
    }

    /** first clear of {@code key} by a real run, or the admin stub flag (@all) used before the map existed. */
    public boolean progressFlag(PlayerData d, String key) {
        if (key == null) return true;
        return firstClearedKey(d, key) || d.periodCount(C_FIRST + key, "all") > 0;
    }

    public boolean firstCleared(PlayerData d, EmberRunMaps.MapDef m) {
        return d.periodCount(C_FIRST + m.key, m.contentVersion) > 0;
    }

    public String target(PlayerData d) {
        int i = d.periodCount(C_TARGET, "all");
        return i >= 1 && i <= 3 ? EmberRunRules.FAMILIES[i - 1] : null;
    }

    public int marks(PlayerData d, int tier) { return d.periodCount(C_MARK + tier, "all"); }

    // ------------------------------------------------------------------ entry (§20.5 reserve → create → commit)

    private EmberRunSession openSessionOf(UUID id) {
        for (EmberRunSession s : sessions.values()) if (s.open() && s.participants.contains(id) && !s.left.contains(id)) return s;
        return null;
    }

    /**
     * Entry for /corerpg enter q01..q05 (TicketEntryService routes the P1 kinds here). Validates every participant
     * (unlock, stamina, not already in a run), reserves 30 stamina each, creates the session, then lets DP create the
     * instance; members not inside 2 s later get their reservation released.
     */
    public boolean tryEnter(final Player leader, String mapKey) { return tryEnter(leader, mapKey, false); }

    /** §18.1 challenge: open for every map once the player has their own Q07 first clear. */
    public boolean challengeOpen(PlayerData d) {
        return maps.challenge != null && progressFlag(d, maps.challenge.requires);
    }

    /**
     * @param challenge §18.1 challenge difficulty: every participant needs their own Q07 first clear; T3 drops / marks,
     *                  challenge HP and damage, no first-clear package, same 30 stamina
     */
    public boolean tryEnter(final Player leader, String mapKey, final boolean challenge) {
        if (!EmberMode.active()) {
            leader.sendMessage(P + "新模式（ember-v1.0-P1）尚未开启，主线 Q 本暂不可进入。");
            return true;
        }
        final EmberRunMaps.MapDef m = maps.byKey(mapKey);
        if (m == null) { leader.sendMessage(P + "未知主线本 " + mapKey); return true; }
        if (challenge && maps.challenge == null) { leader.sendMessage(P + "挑战版未配置。"); return true; }
        if (!EmberRunBridges.teamLeader(leader)) { leader.sendMessage(P + "组队时由队长开本。"); return true; }
        List<UUID> ids = EmberRunBridges.teamMembers(leader);
        List<Player> party = new ArrayList<Player>();
        List<String> problems = new ArrayList<String>();
        for (UUID u : ids) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline()) { problems.add("队员不在线：" + u.toString().substring(0, 8)); continue; }
            party.add(p);
        }
        if (party.size() < maps.partyMin || party.size() > maps.partyMax)
            problems.add("人数 " + maps.partyMin + "～" + maps.partyMax + "，当前 " + party.size());
        StaminaService st = plugin.getStaminaService();
        if (st == null) problems.add("体力服务未就绪");
        for (Player p : party) {
            PlayerData d = data(p.getUniqueId());
            if (challenge && !challengeOpen(d)) {
                problems.add(p.getName() + " 未开放挑战版（需本人首通 " + maps.challenge.requires.toUpperCase(Locale.ROOT) + "）");
            } else if (!challenge && !unlocked(d, m)) {
                EmberRunMaps.MapDef req = maps.byKey(m.requires);
                problems.add(p.getName() + " 未解锁（需先首通 " + (req == null ? m.requires : req.key.toUpperCase(Locale.ROOT) + " " + req.name) + "）");
            }
            if (openSessionOf(p.getUniqueId()) != null) problems.add(p.getName() + " 已在另一局主线本中");
            if (p.getWorld().getName().startsWith("dungeon_")) problems.add(p.getName() + " 仍在副本内");
            if (st != null && st.staminaOf(p) < maps.cost) problems.add(p.getName() + " 体力不足（需 " + maps.cost + "，当前 " + st.staminaOf(p) + "）");
        }
        if (!problems.isEmpty()) {
            for (Player p : party) for (String s : problems) p.sendMessage(P + ChatColor.RED + s);
            if (!party.contains(leader)) for (String s : problems) leader.sendMessage(P + ChatColor.RED + s);
            return true;
        }
        // create the session first (seed, snapshot, extra event fixed now — never re-rolled on reconnect)
        final EmberRunSession s = new EmberRunSession();
        s.runId = m.key + (challenge ? "c" : "") + "-" + Long.toString(System.currentTimeMillis(), 36) + "-" + Integer.toString(rnd.nextInt(36 * 36 * 36), 36);
        s.mapKey = m.key;
        s.dungeon = m.dungeon;
        s.mapVersion = m.mapVersion;
        s.ruleVersion = maps.ruleVersion;
        s.contentVersion = m.contentVersion;
        s.challenge = challenge;
        s.tier = challenge ? maps.challenge.tier : m.tier;
        s.seed = rnd.nextLong();
        s.created = System.currentTimeMillis();
        s.leader = leader.getUniqueId();
        s.extra = EmberRunRules.rollExtra(new java.util.Random(EmberRunRules.subSeed(s.seed, "extra")).nextDouble());
        if (forcedExtra != null) { // admin test hook, one shot
            log().info("[P1 run] " + s.runId + " extra forced " + s.extra.id + " → " + forcedExtra.id + " (admin test)");
            s.extra = forcedExtra;
            forcedExtra = null;
        }
        for (Player p : party) {
            s.participants.add(p.getUniqueId());
            String t = target(data(p.getUniqueId()));
            s.target.put(p.getUniqueId(), t == null ? "" : t);
        }
        // reserve
        List<Player> reserved = new ArrayList<Player>();
        for (Player p : party) {
            StaminaService.ConsumeResult r = st.reserveFlat(p, maps.cost);
            if (!r.ok) {
                for (Player q : reserved) release(s, q.getUniqueId(), "预留失败回滚");
                for (Player q : party) q.sendMessage(P + ChatColor.RED + (r.failMessage == null ? "体力不足" : r.failMessage));
                return true;
            }
            s.cost.put(p.getUniqueId(), r.cost);
            ledgerRow(p.getUniqueId(), s.runId, "cost", "stamina:" + r.cost, EmberRunRules.ST_RESERVED);
            reserved.add(p);
        }
        sessions.put(s.runId, s);
        store.save(s);
        long until = System.currentTimeMillis() + maps.passSeconds * 1000L;
        for (Player p : party) {
            passes.put(p.getUniqueId(), new Object[]{m.key, until});
            p.sendMessage(P + "§e" + m.key.toUpperCase(Locale.ROOT) + " " + m.name + (challenge ? " §c挑战版" : "") + " §7正在创建实例……（已预留体力 " + maps.cost + "）");
        }
        boolean ok = plugin.getTicketEntryService() != null
                && plugin.getTicketEntryService().dispatchStart(leader, m.dungeon);
        if (!ok) {
            abort(s, "DP 实例创建失败", true);
            return true;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> verifyEntry(s), 40L);
        return true;
    }

    private void verifyEntry(EmberRunSession s) {
        if (!s.open()) return;
        for (UUID u : new ArrayList<UUID>(s.participants)) {
            Player p = Bukkit.getPlayer(u);
            boolean in = p != null && p.isOnline() && s.world != null && p.getWorld().getName().equals(s.world);
            if (in) commit(s, u);
            else if (!s.committed.contains(u)) {
                release(s, u, "未进入实例");
                s.participants.remove(u);
                if (p != null) p.sendMessage(P + "没能进入实例，预留的体力已退还。原因见上方 DP 提示（人数 / 冷却约 5 秒）。");
            }
            passes.remove(u);
        }
        if (s.committed.isEmpty()) {
            s.state = EmberRunSession.ABORTED;
            s.reason = "nobody entered";
            store.save(s);
            sessions.remove(s.runId);
            return;
        }
        if (EmberRunSession.PREPARE.equals(s.state)) s.state = EmberRunSession.ENTERED;
        // A18: party HP multiplier locked now for the whole run
        s.partySize = s.committed.size();
        s.hpFactor = EmberRunRules.hpFactor(s.partySize);
        store.save(s);
        tellRun(s, (s.challenge ? "§c挑战版 §7· 掉落 T3 · " : "§7") + "主线本开始 · " + s.partySize + " 人（敌方生命 ×" + String.format(Locale.ROOT, "%.2f", s.hpFactor)
                + "）· 走进前方房间开战 · 击败首领后统一结算");
    }

    private void commit(EmberRunSession s, UUID u) {
        if (!s.committed.add(u)) return;
        EmberRunRules.Ledger l = store.ledger(u);
        l.mark(s.runId, "cost", EmberRunRules.ST_COMMITTED, System.currentTimeMillis());
        store.saveLedger(u, Collections.singletonList(l.get(s.runId, "cost")));
    }

    /** Releases a reservation once (idempotent through the ledger "cost" row status). */
    private void release(EmberRunSession s, UUID u, String why) {
        EmberRunRules.Ledger l = store.ledger(u);
        EmberRunRules.Row r = l.get(s.runId, "cost");
        if (r == null || EmberRunRules.ST_RELEASED.equals(r.status)) return;
        Integer c = s.cost.get(u);
        StaminaService st = plugin.getStaminaService();
        if (c != null && c > 0 && st != null) st.releaseFlat(u, c);
        l.mark(s.runId, "cost", EmberRunRules.ST_RELEASED, System.currentTimeMillis());
        store.saveLedger(u, Collections.singletonList(r));
        log().info("[P1 run] " + s.runId + " release " + u + " (" + why + ")");
    }

    /** PAPI %corerpg_p1_pass_q01%: only a CoreRpg-paid entry passes the DP js-condition. */
    public boolean hasPass(UUID id, String mapKey) {
        Object[] p = passes.get(id);
        if (p == null) return false;
        if (System.currentTimeMillis() > (Long) p[1]) { passes.remove(id); return false; }
        return mapKey.equalsIgnoreCase((String) p[0]);
    }

    // ------------------------------------------------------------------ lifecycle

    private void tick() {
        long now = System.currentTimeMillis();
        for (EmberRunDirector d : new ArrayList<EmberRunDirector>(byWorld.values())) {
            try {
                if (!d.finished()) d.tick(now);
            } catch (Throwable t) {
                log().log(Level.WARNING, "[P1 run] director " + d.s.runId + " tick failed", t);
            }
        }
    }

    private EmberRunDirector attach(EmberRunSession s, World w) {
        EmberRunDirector d = byWorld.get(w.getName());
        if (d != null && d.s == s) return d;
        EmberRunMaps.MapDef m = maps.byKey(s.mapKey);
        if (m == null) return null;
        s.world = w.getName();
        d = new EmberRunDirector(this, s, m, w);
        byWorld.put(w.getName(), d);
        d.attach();
        store.save(s);
        log().info("[P1 run] " + s.runId + " bound to " + w.getName() + " extra=" + s.extra.id);
        return d;
    }

    void index(UUID e, EmberRunDirector d) { byEntity.put(e, d); }

    void unindex(UUID e) { byEntity.remove(e); }

    void onRoomStarted(EmberRunSession s, EmberRunMaps.Room r, boolean b, int spawned, int planned) {
        if (EmberRunSession.ENTERED.equals(s.state)) s.state = EmberRunSession.FIGHTING;
        store.save(s);
        tellRun(s, "§e" + r.label + " §7· 敌人 " + spawned + (spawned < planned ? "/" + planned : "") + "（变体 " + (b ? "B" : "A") + "）");
    }

    void onRoomCleared(EmberRunSession s, EmberRunMaps.Room r, boolean last) {
        store.save(s);
        tellRun(s, "§a" + r.label + " 已清空" + (r.door != null ? " · 门已打开" : "") + (last ? "" : ""));
    }

    void onBossSpawned(EmberRunSession s, EmberRunMaps.Boss b) {
        tellRun(s, "§c首领 " + b.name + (s.challenge ? "（挑战）" : "") + " §7现身 · 招式都有预警，看清地面火线再躲");
    }

    void onExtraSpawned(EmberRunSession s) {
        tellRun(s, "§6侧边出现了「" + s.extra.label + "」§7（可跳过；奖励记为待结算，击败首领才发放）");
    }

    void onExtraDone(EmberRunSession s) {
        if (s.extraDone || !s.open()) return;
        s.extraDone = true;
        store.save(s);
        tellRun(s, "§6「" + s.extra.label + "」完成 §7· 额外奖励已记为待结算，击败首领后统一发放");
    }

    /** Server-side breakage (mob / boss cannot spawn): abort and give the stamina back. */
    void onBroken(EmberRunSession s, String why) {
        tellRun(s, ChatColor.RED + "本局异常终止：" + why + " · 体力退还");
        abort(s, why, true);
    }

    void tellRun(EmberRunSession s, String msg) {
        for (UUID u : s.participants) {
            Player p = Bukkit.getPlayer(u);
            if (p != null && p.isOnline()) p.sendMessage(P + msg);
        }
    }

    private void abort(EmberRunSession s, String why, boolean refund) {
        if (!s.open()) return;
        s.state = EmberRunSession.ABORTED;
        s.reason = why;
        if (refund) for (UUID u : s.participants) release(s, u, why);
        store.save(s);
        endInstance(s, false);
    }

    private void fail(EmberRunSession s, String why) {
        if (!s.open() || EmberRunSession.SETTLING.equals(s.state)) return;
        s.state = EmberRunSession.FAILED;
        s.reason = why;
        store.save(s);
        tellRun(s, ChatColor.RED + "本局失败：" + why + "（已开战不退体力；未结算的额外奖励作废）");
        endInstance(s, false);
    }

    /** Ends the DP instance via the ember-p1 script group; falls back to /dp leave for anyone still inside. */
    private void endInstance(final EmberRunSession s, final boolean complete) {
        final String world = s.world;
        Bukkit.getScheduler().runTaskLater(plugin, () -> { if (!s.open()) sessions.remove(s.runId); }, 1200L);
        EmberRunDirector d = world == null ? null : byWorld.get(world);
        if (d != null) d.finish();
        if (world == null) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            World w = Bukkit.getWorld(world);
            if (w == null || w.getPlayers().isEmpty() || !stillOurs(world, s)) return;
            String group = complete ? "ember_p1_complete" : "ember_p1_fail";
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dp script trigger " + group + " " + w.getPlayers().get(0).getName());
        }, complete ? 60L : 20L);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            World w = Bukkit.getWorld(world);
            if (w == null || !stillOurs(world, s)) return;
            for (Player p : new ArrayList<Player>(w.getPlayers())) {
                EmberRunSession other = openSessionOf(p.getUniqueId());
                if (other != null && other != s) continue; // already in their next run
                p.sendMessage(P + "实例未自动关闭，正在离开……");
                p.performCommand("dp leave");
            }
        }, complete ? 600L : 400L);
    }

    /**
     * B2.166: DP's pre-folder cache hands the SAME world name to the next run of that map; the delayed end-of-run
     * fallbacks of a finished run must not act on (kick out of) a newer run bound to the reused world.
     */
    private boolean stillOurs(String world, EmberRunSession s) {
        EmberRunDirector cur = byWorld.get(world);
        if (cur != null && cur.s != s) return false;
        for (EmberRunSession o : sessions.values()) if (o != s && o.open() && world.equals(o.world)) return false;
        return true;
    }

    // ------------------------------------------------------------------ boss kill → settlement (§9, §20.5)

    private void onBossKilled(EmberRunDirector d, LivingEntity boss, boolean byParticipant) {
        EmberRunSession s = d.s;
        if (!s.open() || EmberRunSession.SETTLING.equals(s.state)) return; // duplicate death event → nothing (E02)
        if (!byParticipant) {
            onBroken(s, "首领非玩家击杀（" + (boss.getLastDamageCause() == null ? "?" : boss.getLastDamageCause().getCause()) + "）");
            return;
        }
        s.state = EmberRunSession.SETTLING;
        store.save(s);
        EmberRunMaps.MapDef m = d.def;
        World w = d.w;
        for (UUID u : s.participants) {
            Player p = Bukkit.getPlayer(u);
            boolean present = !s.left.contains(u) && (p == null || !p.isOnline() || p.getWorld().equals(w));
            boolean ok = EmberRunRules.eligible(s.committed.contains(u), s.acted.contains(u), present, s.died.contains(u));
            if (!ok) {
                if (p != null) p.sendMessage(P + ChatColor.RED + "本局没有你的结算资格（未参与战斗或中途离开）。");
                log().info("[P1 run] " + s.runId + " not eligible " + u + " committed=" + s.committed.contains(u)
                        + " acted=" + s.acted.contains(u) + " present=" + present + " died=" + s.died.contains(u));
                continue;
            }
            settleFor(s, m, u);
        }
        s.state = EmberRunSession.COMPLETE;
        store.save(s);
        tellRun(s, "§a" + m.boss.name + " 已击败 · 结算完成，实例稍后关闭");
        if (!d.anomalies.isEmpty()) log().warning("[P1 run] " + s.runId + " anomalies: " + d.anomalies);
        endInstance(s, true);
    }

    private void settleFor(EmberRunSession s, EmberRunMaps.MapDef m, UUID u) {
        PlayerData pd = data(u);
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.runId = s.runId;
        in.player = u.toString();
        in.seed = s.seed;
        in.tier = s.tier;
        in.target = s.targetOf(u);
        in.bossKilled = true;
        in.extra = s.extra;
        in.extraDone = s.extraDone;
        // §18.1: challenge runs never carry the first-clear package (first clears are per map + content version, normal)
        in.firstClear = s.challenge || firstCleared(pd, m) ? null : m.firstClear;
        if (s.challenge && maps.challenge != null) in.qualityWeights = maps.challenge.quality;
        List<EmberRunRules.Grant> grants = EmberRunRules.settle(in);
        EmberRunRules.Ledger l = store.ledger(u);
        List<EmberRunRules.Row> changed = new ArrayList<EmberRunRules.Row>();
        long now = System.currentTimeMillis();
        boolean[] created = new boolean[1];
        for (EmberRunRules.Grant g : grants) {
            String st = g.kind == EmberRunRules.Kind.CHOICE ? EmberRunRules.ST_AWAIT : EmberRunRules.ST_PENDING;
            EmberRunRules.Row r = l.record(s.runId, g.key, g.encode(), st, now, created);
            if (created[0]) changed.add(r);
        }
        if (in.firstClear != null) pd.addPeriodCount(C_FIRST + m.key, m.contentVersion, 1); // §9.4: once per character + content version
        store.saveLedger(u, changed);
        plugin.getDataStore().flushMutation(u);
        log().info("[P1 run] " + s.runId + " settle " + u + " rows+" + changed.size() + (in.firstClear != null ? " (first clear)" : "")
                + (s.challenge ? " (challenge T" + s.tier + ")" : ""));
        Player p = Bukkit.getPlayer(u);
        if (p != null && p.isOnline()) {
            deliver(p);
            if (in.firstClear != null && maps.challenge != null && m.key.equals(maps.challenge.requires)) endOfP1(p);
        }
    }

    /** Batch 3: the Q07 first clear ends the P1 main line (§2 table, §18.1): say what it opened, once. */
    private void endOfP1(Player p) {
        p.sendMessage(P + "§6§l余烬主线 P1 完结§r §7— 已首通最后一张主线图 Q07。");
        p.sendMessage(P + "§a已开放：§fT3 定向锻造§7（工坊）· §fT2→T3 升阶§7 · §f七图挑战版§7（/corerpg enter <q01..q07> challenge，掉落 T3 与 T3 印记）");
        p.sendMessage(P + "§7长线目标：同族 T3 两件套 +9 = 觉醒III（/corerpg p1 status 查看成套进度）");
        p.playSound(p.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
    }

    // ------------------------------------------------------------------ delivery (E10: retry the original result)

    private void ledgerRow(UUID u, String run, String key, String result, String status) {
        EmberRunRules.Ledger l = store.ledger(u);
        boolean[] c = new boolean[1];
        EmberRunRules.Row r = l.record(run, key, result, status, System.currentTimeMillis(), c);
        if (c[0]) store.saveLedger(u, Collections.singletonList(r));
    }

    /** Applies every pending row once; rows that cannot be delivered now stay pending with their original result. */
    public int deliver(Player p) {
        UUID u = p.getUniqueId();
        EmberRunRules.Ledger l = store.ledger(u);
        List<EmberRunRules.Row> open = l.open();
        if (open.isEmpty()) return 0;
        PlayerData d = data(u);
        NiBridge ni = plugin.getNiBridge();
        List<EmberRunRules.Row> changed = new ArrayList<EmberRunRules.Row>();
        List<String> got = new ArrayList<String>();
        int waiting = 0, choices = 0;
        Map<String, Integer> mail = new LinkedHashMap<String, Integer>();
        List<EmberRunRules.Row> mailRows = new ArrayList<EmberRunRules.Row>();
        long now = System.currentTimeMillis();
        for (EmberRunRules.Row r : open) {
            if (EmberRunRules.ST_AWAIT.equals(r.status)) { choices++; continue; }
            EmberRunRules.Grant g = EmberRunRules.Grant.decode(r.key, r.result);
            if (g == null) { log().warning("[P1 run] undecodable ledger row " + r.runId + "/" + r.key + " = " + r.result); continue; }
            boolean done = false;
            switch (g.kind) {
                case COIN:
                    d.addCoin(g.amount);
                    got.add("余烬币 " + g.amount);
                    done = true;
                    break;
                case XP: {
                    ProgressService ps = plugin.getProgressService();
                    if (ps != null) ps.grantFlatEmberXp(p, g.amount, "余烬主线");
                    got.add("余烬经验 " + g.amount);
                    done = true;
                    break;
                }
                case POTION: { // D32: bound heal potions, only in town and only when they all fit (else stays pending)
                    if (blocksLegacy(p.getWorld())) break;
                    EmberSupplyService sup = plugin.getEmberSupplies();
                    if (sup == null || g.amount <= 0) { done = g.amount <= 0; break; }
                    if (freeSlots(p) < g.amount) { waiting++; break; }
                    int given = sup.give(p, g.amount, "death_refund");
                    if (given < g.amount) log().warning("[P1 run] death refund " + p.getName() + " gave " + given + "/" + g.amount + " (" + r.runId + ")");
                    got.add("今日首次倒下退还 回复药 ×" + given);
                    done = true;
                    break;
                }
                case STAMINA: {
                    StaminaService st = plugin.getStaminaService();
                    if (st != null) st.releaseFlat(u, g.amount);
                    got.add("体力 " + g.amount);
                    done = true;
                    break;
                }
                case MARK: {
                    int tier = Integer.parseInt(g.id);
                    d.addPeriodCount(C_MARK + tier, "all", g.amount);
                    got.add("T" + tier + " 锻造印记 " + g.amount + "（共 " + marks(d, tier) + "）");
                    done = true;
                    break;
                }
                case UNLOCK: {
                    if (d.periodCount(C_UNLOCK + g.id, "all") <= 0) d.addPeriodCount(C_UNLOCK + g.id, "all", 1);
                    EmberRunMaps.MapDef nm = maps.byKey(g.id);
                    got.add("开放 " + g.id.toUpperCase(Locale.ROOT) + (nm == null ? "（后续批次）" : " " + nm.name));
                    done = true;
                    break;
                }
                case MAT: {
                    if (ni != null && freeSlots(p) >= (g.amount + 63) / 64 + 1) {
                        if (ni.giveNiItem(p, g.id, g.amount)) {
                            got.add(ni.displayName(g.id) + " ×" + g.amount);
                            done = true;
                        }
                    }
                    if (!done) { mail.merge(g.id, g.amount, Integer::sum); mailRows.add(r); }
                    break;
                }
                case ITEM: {
                    String res = giveItem(p, g);
                    if (res != null) { got.add(res); done = true; } else waiting++;
                    break;
                }
                default:
                    break;
            }
            if (done) { r.status = EmberRunRules.ST_DELIVERED; r.updated = now; changed.add(r); }
        }
        if (!mail.isEmpty()) {
            MailService ms = plugin.getMailService();
            String mid = "p1_" + Long.toString(now, 36);
            if (ms != null && ms.isEnabled() && ms.deliverCustom(u, mid, "余烬主线结算材料", "背包已满，未能直接放入的结算材料。", mail)) {
                for (EmberRunRules.Row r : mailRows) { r.status = EmberRunRules.ST_MAILED; r.updated = now; changed.add(r); }
                got.add("材料已转邮件（背包已满）");
            } else {
                waiting += mailRows.size();
            }
        }
        if (!changed.isEmpty()) {
            store.saveLedger(u, changed);
            plugin.getDataStore().flushMutation(u);
        }
        if (!got.isEmpty()) p.sendMessage(P + "§a结算到账：§f" + String.join("§7、§f", got));
        if (waiting > 0) p.sendMessage(P + ChatColor.YELLOW + waiting + " 项奖励因背包已满暂存（结果已锁定，不会重抽）：空出格子后 /corerpg p1 claim");
        if (choices > 0) p.sendMessage(P + ChatColor.YELLOW + "首通自选待领取：/corerpg p1 firstclear <scorch|burst|sustain>（焚烬/烬爆/炽愈）或 冒险 菜单");
        return changed.size();
    }

    private static int freeSlots(Player p) {
        int n = 0;
        ItemStack[] st = p.getInventory().getStorageContents();
        for (ItemStack i : st) if (i == null || i.getType() == org.bukkit.Material.AIR) n++;
        return n;
    }

    /** @return label when given (or already present with the same uid), null when the inventory has no room. */
    private String giveItem(Player p, EmberRunRules.Grant g) {
        EmberRunRules.ItemRoll it = g.item;
        String src = g.key.startsWith("fc_") || g.key.startsWith("starter_") ? "quest" : "drop";
        EmberItemData d = new EmberItemData(g.id, EmberItemData.templateId(it.tier == 0 ? "none" : it.family, it.slot, it.tier),
                it.tier == 0 ? "none" : it.family, it.slot, it.tier, it.quality, it.craft, 0, 0, true, src,
                EmberItemData.DATA_VERSION, 0);
        for (ItemStack s : p.getInventory().getContents()) {
            if (s == null || !loadouts.items().hasData(s)) continue;
            EmberItems.Read r = loadouts.items().read(s);
            if (r != null && r.data != null && g.id.equals(r.data.uid)) return d.shortLabel() + "（已在背包）";
        }
        if (freeSlots(p) <= 0) return null;
        ItemStack stack = loadouts.items().create(d);
        if (stack == null) return null;
        loadouts.remember(d, p.getUniqueId());
        p.getInventory().addItem(stack);
        return d.shortLabel();
    }

    // ------------------------------------------------------------------ restart recovery (§20.5)

    private void recoverAfterRestart() {
        for (EmberRunSession s : store.loadOpen()) {
            if (EmberRunSession.SETTLING.equals(s.state)) {
                // rewards were recorded before the stop; deliveries stay pending in the ledgers
                s.state = EmberRunSession.COMPLETE;
                s.reason = "restart during settlement (ledger rows kept)";
            } else {
                s.state = EmberRunSession.ABORTED;
                s.reason = "server restart";
                for (UUID u : s.participants) {
                    EmberRunRules.Ledger l = store.ledger(u);
                    EmberRunRules.Row r = l.get(s.runId, "cost");
                    if (r == null || EmberRunRules.ST_RELEASED.equals(r.status)) continue;
                    l.mark(s.runId, "cost", EmberRunRules.ST_RELEASED, System.currentTimeMillis());
                    boolean[] c = new boolean[1];
                    Integer cost = s.cost.get(u);
                    EmberRunRules.Row refund = l.record(s.runId, "refund", "stamina:" + (cost == null ? 0 : cost),
                            EmberRunRules.ST_PENDING, System.currentTimeMillis(), c);
                    List<EmberRunRules.Row> ch = new ArrayList<EmberRunRules.Row>();
                    ch.add(r);
                    if (c[0]) ch.add(refund);
                    store.saveLedger(u, ch);
                }
            }
            store.save(s);
            log().info("[P1 run] recovered " + s.runId + " → " + s.state + " (" + s.reason + ")");
        }
    }

    // ------------------------------------------------------------------ events

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChangedWorld(PlayerChangedWorldEvent e) {
        final Player p = e.getPlayer();
        World from = e.getFrom(), to = p.getWorld();
        EmberRunMaps.MapDef m = maps.byWorld(to.getName());
        if (m != null) {
            EmberRunSession s = openSessionOf(p.getUniqueId());
            if (s != null && s.mapKey.equals(m.key) && (s.world == null || s.world.equals(to.getName()))) {
                attach(s, to);
                if (!EmberRunSession.PREPARE.equals(s.state)) commit(s, p.getUniqueId());
                spreadLater(s, m, p);
            } else {
                p.sendMessage(P + ChatColor.RED + "没有找到你的主线本入场记录：本实例不会生成怪物，也不会结算。请 /dp leave 后从 冒险 菜单进入。");
            }
        }
        if (maps.byWorld(from.getName()) != null) {
            EmberRunDirector d = byWorld.get(from.getName());
            if (d != null && d.s.participants.contains(p.getUniqueId()) && d.s.open()) {
                if (!d.s.died.contains(p.getUniqueId())) d.s.left.add(p.getUniqueId());
                store.save(d.s);
                checkWipe(d.s);
            }
            if (p.getGameMode() == GameMode.SPECTATOR) p.setGameMode(GameMode.SURVIVAL);
        }
        if (m == null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!p.isOnline() || blocksLegacy(p.getWorld())) return;
                deliver(p);
                starterKit(p);
            }, 20L);
        }
    }

    /**
     * B2.146: DP drops the whole party on one spawn block; members 2..n are moved to the map's spread points so
     * nobody starts inside another player (and the boss-circle lock does not hit the whole party at once).
     * Only a player who is still standing on the spawn is moved.
     */
    private void spreadLater(final EmberRunSession s, final EmberRunMaps.MapDef m, final Player p) {
        final int idx = s.participants.indexOf(p.getUniqueId());
        final EmberRunMaps.Pt to = m.spreadPoint(idx);
        if (to == null || m.spawn == null) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline() || p.isDead() || !p.getWorld().getName().equals(s.world == null ? p.getWorld().getName() : s.world)) return;
            Location l = p.getLocation();
            double dx = l.getX() - (m.spawn.x + 0.5), dz = l.getZ() - (m.spawn.z + 0.5);
            if (dx * dx + dz * dz > 2.5 * 2.5 || Math.abs(l.getY() - m.spawn.y) > 1.5) return;
            p.teleport(new Location(p.getWorld(), to.x + 0.5, to.y, to.z + 0.5, l.getYaw(), l.getPitch()));
        }, 30L);
    }

    /** B2.139: logged back into a Q instance whose run no longer includes this player (released / ended / restart). */
    public boolean orphanedIn(Player p) {
        if (p == null || maps.byWorld(p.getWorld().getName()) == null) return false;
        EmberRunSession s = openSessionOf(p.getUniqueId());
        return s == null || (s.world != null && !s.world.equals(p.getWorld().getName()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        if (maps.byWorld(p.getWorld().getName()) != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!p.isOnline() || !orphanedIn(p)) return;
                p.sendMessage(P + ChatColor.YELLOW + "你所在的主线本已结束或已取消（未开战的体力会退还）：正在送你离开实例……");
                p.performCommand("dp leave");
            }, 20L);
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline() || blocksLegacy(p.getWorld())) return;
            deliver(p);
            starterKit(p);
        }, 60L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        UUID u = e.getPlayer().getUniqueId();
        passes.remove(u);
        starterChecked.remove(u);
        if (openSessionOf(u) == null) store.unload(u);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onUnload(WorldUnloadEvent e) {
        EmberRunDirector d = byWorld.remove(e.getWorld().getName());
        if (d == null) return;
        d.finish();
        EmberRunSession s = d.s;
        if (s.open() && !EmberRunSession.SETTLING.equals(s.state)) {
            if (s.fightStarted()) fail(s, "实例已关闭（超时 / 全员离开）");
            else abort(s, "开战前实例关闭", true);
        }
        sessions.remove(s.runId);
    }

    /** B2.157: template item entities (dropped torches saved into the ember_daily* maps) never reach a P1 player. */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkLoad(org.bukkit.event.world.ChunkLoadEvent e) {
        if (!blocksLegacy(e.getWorld())) return;
        int n = EmberRunDirector.purgeItems(e.getChunk());
        if (n > 0) log().fine("[P1 run] removed " + n + " template item entities in " + e.getWorld().getName() + " " + e.getChunk().getX() + "," + e.getChunk().getZ());
    }

    /** Natural / spawner mobs never join a P1 run; MythicMobs spawns are CUSTOM. */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent e) {
        if (!blocksLegacy(e.getEntity().getWorld())) return;
        CreatureSpawnEvent.SpawnReason r = e.getSpawnReason();
        if (r != CreatureSpawnEvent.SpawnReason.CUSTOM && r != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG
                && r != CreatureSpawnEvent.SpawnReason.DEFAULT) {
            e.setCancelled(true);
            if (spawnBlocks++ % 50 == 0) log().info("[P1 run] blocked " + r + " spawn of " + e.getEntityType() + " in " + e.getEntity().getWorld().getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCombust(EntityCombustEvent e) {
        if (byEntity.containsKey(e.getEntity().getUniqueId())) e.setCancelled(true); // no sunlight burning for run mobs
    }

    /** Run-mob → player: pin the raw hit to the table value and enforce the minimum interval (book ch. 11–13 §6/§7). */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onMobHit(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Entity src = e.getDamager();
        boolean projectile = false;
        if (src instanceof Projectile && ((Projectile) src).getShooter() instanceof Entity) {
            src = (Entity) ((Projectile) src).getShooter();
            projectile = true;
        }
        EmberRunDirector d = byEntity.get(src.getUniqueId());
        if (d == null) return;
        EmberRunDirector.Tracked t = d.mobs.get(src.getUniqueId());
        if (t == null) return;
        Player p = (Player) e.getEntity();
        if (skillDepth > 0) { markActed(d.s, p); return; } // boss / caster skill: raw value set by skillHit
        long now = System.currentTimeMillis();
        if (t.caster() || t.atk <= 0) { e.setCancelled(true); return; }
        if (t.boss() && d.bossCasting()) { e.setCancelled(true); return; } // §14/§15: no normal hit while a skill winds up
        if (projectile && p.getLocation().distance(src.getLocation()) > t.range + 1.0) { e.setCancelled(true); return; }
        if (now - t.lastHit < (long) (t.interval * 1000) - 50L) { e.setCancelled(true); return; }
        t.lastHit = now;
        if (t.boss()) kbGuard.mark(p.getUniqueId(), now); // B2.167: no vanilla knockback from the boss melee either
        double before = e.getDamage();
        e.setDamage(t.atk);
        EmberDamageTrace.note(e, before, "G04 主线本怪物伤害固定 atk=" + EmberDamageTrace.fmt(t.atk));
        markActed(d.s, p);
    }

    /** B2.167: drop the vanilla knockback velocity of a boss skill / boss melee hit (the P1 push is a teleport). */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVelocity(org.bukkit.event.player.PlayerVelocityEvent e) {
        long now = System.currentTimeMillis();
        if (kbGuard.consume(e.getPlayer().getUniqueId(), now)) e.setCancelled(true);
        if (kbGuard.size() > 64) kbGuard.prune(now);
    }

    /** Player → run mob: participation record (§20.5 合法参战). */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerHit(EntityDamageByEntityEvent e) {
        EmberRunDirector d = byEntity.get(e.getEntity().getUniqueId());
        if (d == null) return;
        Entity src = e.getDamager();
        if (src instanceof Projectile && ((Projectile) src).getShooter() instanceof Entity) src = (Entity) ((Projectile) src).getShooter();
        if (src instanceof Player) markActed(d.s, (Player) src);
    }

    private void markActed(EmberRunSession s, Player p) {
        if (s.committed.contains(p.getUniqueId()) && s.acted.add(p.getUniqueId())) store.save(s);
    }

    void skillHit(EmberRunSession s, Player p, LivingEntity src, double dmg) {
        skillDepth++;
        try {
            p.setNoDamageTicks(0);
            kbGuard.mark(p.getUniqueId(), System.currentTimeMillis()); // B2.167: only the P1 push (≤ kb) moves the player
            p.damage(dmg, src);
        } finally {
            skillDepth--;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMobDeath(EntityDeathEvent e) {
        EmberRunDirector d = byEntity.remove(e.getEntity().getUniqueId());
        if (d == null) return;
        e.getDrops().clear(); // E02: no vanilla / MM drops from run mobs
        e.setDroppedExp(0);
        EmberRunDirector.Tracked t = d.mobs.get(e.getEntity().getUniqueId());
        if (t == null) return;
        boolean byPlayer = e.getEntity().getKiller() != null && d.s.committed.contains(e.getEntity().getKiller().getUniqueId());
        if (!byPlayer && t.boss()) byPlayer = !d.s.acted.isEmpty(); // DoT / set-event final blow after real participation
        if (d.onDeath(t)) onBossKilled(d, e.getEntity(), byPlayer);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock() == null) return;
        EmberRunDirector d = byWorld.get(e.getPlayer().getWorld().getName());
        if (d == null) return;
        Block b = e.getClickedBlock();
        if (d.chest != null && b.getLocation().equals(d.chest)) {
            e.setCancelled(true);
            if (d.s.committed.contains(e.getPlayer().getUniqueId())) {
                markActed(d.s, e.getPlayer());
                d.clickChest(b);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        EmberRunDirector d = byWorld.get(p.getWorld().getName());
        if (d == null || !d.s.committed.contains(p.getUniqueId()) || !d.s.open()) return;
        d.s.died.add(p.getUniqueId());
        store.save(d.s);
        deathRefund(p, d.s);
        checkWipe(d.s);
    }

    /** D32 / B2.172: a heal potion drunk inside a P1 main run (called by LifeService after the cooldown check). */
    public void notePotion(Player p) {
        EmberRunDirector d = byWorld.get(p.getWorld().getName());
        if (d == null || !d.s.open() || !d.s.committed.contains(p.getUniqueId())) return;
        d.s.potions.merge(p.getUniqueId(), 1, Integer::sum);
        store.save(d.s);
    }

    public static int deathRefundMax() {
        EmberMode m = EmberMode.get();
        return m == null ? 5 : m.i("death_refund.max_potions", 5);
    }

    /**
     * D32 / B2.172: the first P1 main-run death of the stamina day refunds the heal potions used in that run (up to
     * death_refund.max_potions). One ledger row per (player, day): record() never overwrites, so a second death the
     * same day, a relog, a restart or a duplicate death event pays nothing more. The row is delivered in town.
     */
    private void deathRefund(Player p, EmberRunSession s) {
        int max = deathRefundMax();
        if (max <= 0) return;
        UUID u = p.getUniqueId();
        Integer used = s.potions.get(u);
        int n = EmberRunRules.deathRefundCount(used == null ? 0 : used, max);
        String day = town.sunshine.corerpg.DailyService.today();
        EmberRunRules.Ledger l = store.ledger(u);
        boolean[] c = new boolean[1];
        EmberRunRules.Row r = l.record(EmberRunRules.deathRefundRun(day), EmberRunRules.DEATH_REFUND_KEY,
                "potion:" + n + ":" + s.runId, n > 0 ? EmberRunRules.ST_PENDING : EmberRunRules.ST_DELIVERED,
                System.currentTimeMillis(), c);
        if (!c[0]) {
            log().info("[P1 run] death refund " + p.getName() + " " + day + ": already used today (" + r.result + ", " + r.status + ")");
            return;
        }
        store.saveLedger(u, Collections.singletonList(r));
        log().info("[P1 run] death refund " + p.getName() + " " + day + " run " + s.runId + ": " + n + " potion(s) (used " + (used == null ? 0 : used) + ", max " + max + ")");
        p.sendMessage(P + (n > 0 ? "§a今日首次倒下：退还本局用掉的回复药 ×" + n + "（回城到账，绑定）。今天再倒下不再退还。"
                : "今日首次倒下：本局没有用回复药，无可退还（今天再倒下也不再退还）。"));
    }

    /** §20.5: no revive system — a dead member watches; the run fails when nobody is left standing. */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent e) {
        final Player p = e.getPlayer();
        EmberRunDirector d = byWorld.get(p.getWorld().getName());
        if (d == null || !d.s.died.contains(p.getUniqueId()) || !d.s.open()) return;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (p.isOnline() && blocksLegacy(p.getWorld())) {
                p.setGameMode(GameMode.SPECTATOR);
                p.sendMessage(P + "你已倒下：本局观战等待队友（仍保留本次结算资格）。");
            }
        }, 2L);
    }

    private void checkWipe(EmberRunSession s) {
        if (!s.open() || EmberRunSession.SETTLING.equals(s.state)) return;
        for (UUID u : s.committed) if (!s.died.contains(u) && !s.left.contains(u)) return;
        if (s.fightStarted()) { // B2.138: say what actually happened
            int dead = 0;
            for (UUID u : s.committed) if (s.died.contains(u)) dead++;
            boolean solo = s.committed.size() == 1;
            String why = dead == s.committed.size() ? (solo ? "倒下" : "全员倒下")
                    : dead == 0 ? (solo ? "离开副本" : "全员离开副本") : "全员倒下或离开";
            fail(s, why);
        }
        else abort(s, "开战前全员离开", true);
    }

    // ------------------------------------------------------------------ starter kit (§3.1)

    /** A character with no P1 gear gets one T0 blade + one T0 charm, once (ledger rows make it idempotent). */
    public void starterKit(final Player p) {
        if (!EmberMode.active() || p == null) return;
        final UUID u = p.getUniqueId();
        PlayerData d = data(u);
        if (d.periodCount(C_STARTER, "all") > 0) return;
        Long last = starterChecked.get(u);
        if (last != null && System.currentTimeMillis() - last < 60000L) return;
        starterChecked.put(u, System.currentTimeMillis());
        for (ItemStack s : p.getInventory().getContents()) {
            if (s != null && loadouts.items().hasData(s)) { markStarter(u, "has P1 item"); return; }
        }
        EmberItemStore db = loadouts.store();
        if (db != null && db.usable()) {
            db.loadOwnerItems(u, rows -> {
                for (EmberItemStore.Row r : rows.values()) if ("active".equals(r.state)) { markStarter(u, "has P1 rows"); return; }
                Player q = Bukkit.getPlayer(u);
                if (q != null && q.isOnline()) giveStarter(q);
            });
        } else {
            giveStarter(p);
        }
    }

    private void markStarter(UUID u, String why) {
        PlayerData d = data(u);
        if (d.periodCount(C_STARTER, "all") > 0) return;
        d.addPeriodCount(C_STARTER, "all", 1);
        plugin.getDataStore().flushMutation(u);
        log().fine("[P1 run] starter skipped for " + u + " (" + why + ")");
    }

    private void giveStarter(Player p) {
        UUID u = p.getUniqueId();
        PlayerData d = data(u);
        if (d.periodCount(C_STARTER, "all") > 0) return;
        for (String slot : new String[]{"blade", "charm"}) {
            String key = "starter_" + slot;
            String uid = EmberRunRules.rewardUid(u.getMostSignificantBits(), u.toString(), "starter", key);
            ledgerRow(u, "starter", key, "item:" + uid + ":none:" + slot + ":0:0:0", EmberRunRules.ST_PENDING);
        }
        d.addPeriodCount(C_STARTER, "all", 1);
        plugin.getDataStore().flushMutation(u);
        p.sendMessage(P + "§e新模式起步：发放 T0 刃 + T0 护符各一件（绑定）。手持护符 /corerpg p1 charm select 选定。");
        deliver(p);
        EmberSupplyService sup = plugin.getEmberSupplies();
        if (sup != null) sup.giveStarter(p); // B2.169 / D28: §3.1 基础补给, once (guarded by C_STARTER above)
    }

    // ------------------------------------------------------------------ commands

    public static final java.util.Set<String> OPS = new java.util.HashSet<String>(java.util.Arrays.asList(
            "target", "marks", "firstclear", "claim", "run", "runs", "enter"));

    public boolean cmd(CommandSender s, String sub, String[] args) {
        switch (sub) {
            case "target": return cmdTarget(s, args);
            case "marks": return cmdMarks(s, args);
            case "firstclear": return cmdFirstClear(s, args);
            case "claim":
                if (!(s instanceof Player)) return true;
                if (blocksLegacy(((Player) s).getWorld())) { s.sendMessage(P + "出本后再领取。"); return true; }
                if (deliver((Player) s) == 0 && store.ledger(((Player) s).getUniqueId()).open().isEmpty()) // B2.138
                    s.sendMessage(P + "没有可领取的暂存奖励。");
                return true;
            case "enter":
                if (!(s instanceof Player) || args.length < 3) { s.sendMessage(P + "/corerpg p1 enter <q01..q07> [challenge]"); return true; }
                return tryEnter((Player) s, args[2].toLowerCase(Locale.ROOT), args.length >= 4 && isChallengeWord(args[3]));
            default:
                return cmdRuns(s, args);
        }
    }

    /** "challenge" / "c" / "挑战" (after the map key) */
    public static boolean isChallengeWord(String w) {
        if (w == null) return false;
        String x = w.toLowerCase(Locale.ROOT);
        return "challenge".equals(x) || "c".equals(x) || "ch".equals(x) || "挑战".equals(w) || "挑战版".equals(w);
    }

    public static void helpLines(CommandSender s) {
        s.sendMessage(P + "/corerpg enter q01..q07 — 主线本（30 体力，1～3 人）· /corerpg p1 run — 解锁/待领/当前局");
        s.sendMessage(P + "/corerpg enter q01..q07 challenge — 挑战版（本人首通 Q07 后开放；T3 掉落与印记，敌人更强）");
        s.sendMessage(P + "/corerpg p1 target <scorch|burst|sustain|none> — 掉落目标族（入场时快照）");
        s.sendMessage(P + "/corerpg p1 marks [exchange <族> <blade|charm> [阶]] — 8 枚同阶印记换标准件");
        s.sendMessage(P + "/corerpg p1 firstclear <族> — 领取首通自选 · /corerpg p1 claim — 补领暂存奖励");
        s.sendMessage(P + "/corerpg p1 shop [buy [n]] — 补给商：回复药 " + EmberSupplyService.price() + " 余烬币/瓶（绑定，城内购买）");
        if (s.hasPermission("corerpg.admin")) s.sendMessage(P + "/corerpg p1 audit [玩家] · audit restore <玩家> <uid前缀> — 物品与 DB 对账 / 补发");
    }

    private boolean cmdTarget(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        PlayerData d = data(p.getUniqueId());
        if (args.length < 3) {
            String t = target(d);
            p.sendMessage(P + "当前掉落目标族：" + (t == null ? "未选择（三族各 1/3）" : EmberItemData.familyName(t) + "（60%，另两族各 20%）"));
            p.sendMessage(P + "/corerpg p1 target <scorch|burst|sustain|none> · 只影响之后入场的局（入场时快照）");
            return true;
        }
        String f = args[2].toLowerCase(Locale.ROOT);
        int idx = 0;
        for (int i = 0; i < 3; i++) if (EmberRunRules.FAMILIES[i].equals(f)) idx = i + 1;
        if (idx == 0 && !"none".equals(f)) { p.sendMessage(P + ChatColor.RED + "族：scorch（焚烬）/ burst（烬爆）/ sustain（炽愈）/ none"); return true; }
        d.addPeriodCount(C_TARGET, "all", idx - d.periodCount(C_TARGET, "all"));
        plugin.getDataStore().flushMutation(p.getUniqueId());
        p.sendMessage(P + "§a掉落目标族已设为 " + (idx == 0 ? "无（三族均分）" : EmberItemData.familyName(f)) + " §7· 下次入场生效");
        return true;
    }

    private boolean cmdMarks(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        PlayerData d = data(p.getUniqueId());
        if (args.length < 3 || !"exchange".equalsIgnoreCase(args[2])) {
            p.sendMessage(P + "锻造印记（账户绑定）：T1 " + marks(d, 1) + " · T2 " + marks(d, 2) + " · T3 " + marks(d, 3));
            p.sendMessage(P + "8 枚同阶印记 → 指定族+部位的同阶标准件（成色标准、精工 0、+0）：/corerpg p1 marks exchange <族> <blade|charm> [阶]");
            p.sendMessage(P + "T2 定向锻造：" + (progressFlag(d, "q04") ? "§a已开放" : "§7需本人首通 Q04")
                    + " §7· T3：" + (progressFlag(d, "q07") ? "§a已开放" : "§7需本人首通 Q07"));
            return true;
        }
        if (blocksLegacy(p.getWorld())) { p.sendMessage(P + "出本后再兑换。"); return true; }
        if (args.length < 5) { p.sendMessage(P + "/corerpg p1 marks exchange <scorch|burst|sustain> <blade|charm> [1-3]"); return true; }
        String fam = args[3].toLowerCase(Locale.ROOT), slot = args[4].toLowerCase(Locale.ROOT);
        int tier = 1;
        if (args.length >= 6) try { tier = Integer.parseInt(args[5].replace("t", "").replace("T", "")); } catch (NumberFormatException ignored) { }
        String err = EmberRunRules.exchangeCheck(marks(d, tier), tier, tier, fam, slot,
                progressFlag(d, EmberRunRules.directedForgeFlag(tier)));
        if (err != null) { p.sendMessage(P + ChatColor.RED + err); return true; }
        if (freeSlots(p) <= 0) { p.sendMessage(P + ChatColor.RED + "背包已满，空出一格再兑换。"); return true; }
        d.addPeriodCount(C_MARK + tier, "all", -EmberRunRules.MARKS_PER_EXCHANGE);
        String run = "mark-" + Long.toString(System.currentTimeMillis(), 36) + "-" + Integer.toString(rnd.nextInt(1296), 36);
        String uid = EmberRunRules.rewardUid(rnd.nextLong(), p.getUniqueId().toString(), run, "mark_item");
        ledgerRow(p.getUniqueId(), run, "mark_item", "item:" + uid + ":" + fam + ":" + slot + ":" + tier + ":0:0", EmberRunRules.ST_PENDING);
        plugin.getDataStore().flushMutation(p.getUniqueId());
        p.sendMessage(P + "§a已用 " + EmberRunRules.MARKS_PER_EXCHANGE + " 枚 T" + tier + " 印记兑换 " + EmberItemData.familyName(fam)
                + EmberItemData.slotName(slot) + "（剩余 " + marks(d, tier) + "）");
        deliver(p);
        return true;
    }

    private boolean cmdFirstClear(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        EmberRunRules.Ledger l = store.ledger(p.getUniqueId());
        List<EmberRunRules.Row> waiting = new ArrayList<EmberRunRules.Row>();
        for (EmberRunRules.Row r : l.open()) if (EmberRunRules.ST_AWAIT.equals(r.status)) waiting.add(r);
        if (waiting.isEmpty()) { p.sendMessage(P + "没有待领取的首通自选。"); return true; }
        if (args.length < 3) {
            for (EmberRunRules.Row r : waiting) {
                EmberRunRules.Grant g = EmberRunRules.Grant.decode(r.key, r.result);
                p.sendMessage(P + "待选：" + r.key.substring(3, 6).toUpperCase(Locale.ROOT) + " T" + (g == null ? 1 : g.amount) + " 标准"
                        + EmberItemData.slotName(g == null ? "blade" : g.id) + " → /corerpg p1 firstclear <scorch|burst|sustain>");
            }
            p.sendMessage(P + "§7Q02 可选与武器不同的族（那样不能成套），系统不替你强选。");
            return true;
        }
        if (blocksLegacy(p.getWorld())) { p.sendMessage(P + "出本后再领取。"); return true; }
        String fam = args[2].toLowerCase(Locale.ROOT);
        if (!EmberRunRules.validFamily(fam)) { p.sendMessage(P + ChatColor.RED + "族：scorch / burst / sustain"); return true; }
        EmberRunRules.Row pick = waiting.get(0);
        if (args.length >= 4) for (EmberRunRules.Row r : waiting) if (r.key.startsWith("fc_" + args[3].toLowerCase(Locale.ROOT) + "_")) pick = r;
        EmberRunRules.Grant g = EmberRunRules.Grant.decode(pick.key, pick.result);
        if (g == null) return true;
        long seed = EmberRunRules.subSeed(p.getUniqueId().getLeastSignificantBits(), pick.runId);
        EmberRunRules.Grant item = EmberRunRules.choiceItem(seed, p.getUniqueId().toString(), pick.runId, pick.key, fam, g.id, g.amount);
        boolean[] c = new boolean[1];
        long now = System.currentTimeMillis();
        EmberRunRules.Row ir = l.record(pick.runId, item.key, item.encode(), EmberRunRules.ST_PENDING, now, c);
        pick.status = EmberRunRules.ST_DELIVERED;
        pick.updated = now;
        List<EmberRunRules.Row> ch = new ArrayList<EmberRunRules.Row>();
        ch.add(pick);
        if (c[0]) ch.add(ir);
        store.saveLedger(p.getUniqueId(), ch);
        deliver(p);
        return true;
    }

    /** admin test hook: the next started run uses this extra event instead of the seeded roll (one shot). */
    private volatile EmberRunRules.Extra forcedExtra;

    private boolean cmdRuns(CommandSender s, String[] args) {
        boolean admin = s.hasPermission("corerpg.admin");
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        if (admin && "extra".equals(op) && args.length >= 4) {
            forcedExtra = "clear".equalsIgnoreCase(args[3]) ? null : EmberRunRules.Extra.parse(args[3]);
            s.sendMessage(P + "下一局额外事件（仅一次，测试用）= " + (forcedExtra == null ? "按种子" : forcedExtra.id));
            return true;
        }
        if (admin && "marks".equals(op) && args.length >= 6) { // test grant: runs marks <玩家> <阶> <±n>
            Player t = Bukkit.getPlayerExact(args[3]);
            if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
            int tier, n;
            try { tier = Integer.parseInt(args[4]); n = Integer.parseInt(args[5]); }
            catch (NumberFormatException ex) { s.sendMessage(P + "阶与数量需为整数"); return true; }
            PlayerData d = data(t.getUniqueId());
            d.addPeriodCount(C_MARK + tier, "all", Math.max(n, -marks(d, tier)));
            plugin.getDataStore().flushMutation(t.getUniqueId());
            s.sendMessage(P + t.getName() + " T" + tier + " 锻造印记 = " + marks(d, tier));
            log().info("[P1 run] admin " + s.getName() + " marks " + t.getName() + " T" + tier + " " + n + " → " + marks(d, tier));
            return true;
        }
        if (admin && "list".equals(op)) {
            for (EmberRunSession x : sessions.values()) {
                EmberRunDirector d = x.world == null ? null : byWorld.get(x.world);
                s.sendMessage(P + x.runId + " " + x.state + " world=" + x.world + " party=" + x.participants.size()
                        + " hp×" + x.hpFactor + " extra=" + x.extra.id + (d == null ? "" : " · " + d.describe()));
            }
            if (sessions.isEmpty()) s.sendMessage(P + "没有进行中的主线本。");
            return true;
        }
        if (admin && "unlock".equals(op) && args.length >= 5) {
            Player t = Bukkit.getPlayerExact(args[3]);
            if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
            String key = args[4].toLowerCase(Locale.ROOT);
            boolean clear = args.length >= 6 && "clear".equalsIgnoreCase(args[5]);
            PlayerData d = data(t.getUniqueId());
            d.addPeriodCount(C_UNLOCK + key, "all", (clear ? 0 : 1) - d.periodCount(C_UNLOCK + key, "all"));
            plugin.getDataStore().flushMutation(t.getUniqueId());
            s.sendMessage(P + t.getName() + " " + key + " unlock=" + !clear);
            return true;
        }
        if (admin && "firstclear".equals(op) && args.length >= 5) {
            Player t = Bukkit.getPlayerExact(args[3]);
            EmberRunMaps.MapDef m = maps.byKey(args[4]);
            if (t == null || m == null) { s.sendMessage(P + "玩家不在线或地图未知"); return true; }
            PlayerData d = data(t.getUniqueId());
            boolean clear = args.length >= 6 && "clear".equalsIgnoreCase(args[5]);
            d.addPeriodCount(C_FIRST + m.key, m.contentVersion, (clear ? 0 : 1) - d.periodCount(C_FIRST + m.key, m.contentVersion));
            plugin.getDataStore().flushMutation(t.getUniqueId());
            s.sendMessage(P + t.getName() + " " + m.key + "@" + m.contentVersion + " first_clear=" + !clear);
            return true;
        }
        if (admin && "starter".equals(op) && args.length >= 4) {
            Player t = Bukkit.getPlayerExact(args[3]);
            if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
            PlayerData d = data(t.getUniqueId());
            if (args.length >= 5 && "reset".equalsIgnoreCase(args[4])) {
                d.addPeriodCount(C_STARTER, "all", -d.periodCount(C_STARTER, "all"));
                starterChecked.remove(t.getUniqueId());
                s.sendMessage(P + t.getName() + " starter flag reset（已发过的 T0 行仍在账本里，不会再发同一件）");
            } else {
                starterChecked.remove(t.getUniqueId());
                starterKit(t);
                s.sendMessage(P + t.getName() + " starter check run (flag=" + d.periodCount(C_STARTER, "all") + ")");
            }
            plugin.getDataStore().flushMutation(t.getUniqueId());
            return true;
        }
        if (!(s instanceof Player)) {
            s.sendMessage(P + "/corerpg p1 runs list | unlock <玩家> <q02..q05> [clear] | firstclear <玩家> <q01..> [clear] | starter <玩家> [reset] | marks <玩家> <阶> <±n> | extra <none|treasure|elite|chest|clear>");
            return true;
        }
        Player p = (Player) s;
        PlayerData d = data(p.getUniqueId());
        for (EmberRunMaps.MapDef m : maps.maps.values()) {
            p.sendMessage(P + "§e" + m.key.toUpperCase(Locale.ROOT) + " " + m.name + " §7" + stateLabel(d, m) + " · " + maps.cost
                    + " 体力 · 掉落 " + m.dropLabel + " · 首通：" + m.firstClearLabel());
        }
        String t = target(d);
        p.sendMessage(P + "挑战版（七图）：" + (challengeOpen(d) ? "§a已开放 §7· /corerpg enter <q01..q07> challenge · 掉落 T3"
                : "§7需本人首通 " + (maps.challenge == null ? "Q07" : maps.challenge.requires.toUpperCase(Locale.ROOT))));
        p.sendMessage(P + "目标族 " + (t == null ? "未选" : EmberItemData.familyName(t)) + " · 印记 T1 " + marks(d, 1)
                + " · T2 " + marks(d, 2) + " · T3 " + marks(d, 3)
                + " · 暂存 " + store.ledger(p.getUniqueId()).open().size() + " 项");
        EmberRunSession cur = openSessionOf(p.getUniqueId());
        if (cur != null) {
            EmberRunDirector dd = cur.world == null ? null : byWorld.get(cur.world);
            p.sendMessage(P + "当前局 " + cur.runId + " · " + cur.state + (dd == null ? "" : " · " + dd.describe()));
        }
        if (admin) p.sendMessage(P + "§8admin: /corerpg p1 runs list | unlock | firstclear | starter");
        return true;
    }

    public String stateLabel(PlayerData d, EmberRunMaps.MapDef m) {
        if (firstCleared(d, m)) return "已首通";
        if (unlocked(d, m)) return "已解锁";
        EmberRunMaps.MapDef req = maps.byKey(m.requires);
        return "未解锁（需 " + (req == null ? m.requires : req.key.toUpperCase(Locale.ROOT)) + " 首通）";
    }

    // ------------------------------------------------------------------ PAPI %corerpg_p1_*%

    public String placeholder(Player p, String key) {
        if (p == null || maps == null) return "";
        PlayerData d = data(p.getUniqueId());
        if (key.startsWith("pass_")) return hasPass(p.getUniqueId(), key.substring(5)) ? "yes" : "no";
        if ("target".equals(key)) { String t = target(d); return t == null ? "未选择" : EmberItemData.familyName(t); }
        if (key.startsWith("marks_t")) {
            try { return String.valueOf(marks(d, Integer.parseInt(key.substring(7)))); } catch (NumberFormatException e) { return "0"; }
        }
        if ("pending".equals(key)) return String.valueOf(store.ledger(p.getUniqueId()).open().size());
        if ("active".equals(key)) return EmberMode.active() ? "yes" : "no";
        if ("forge_t2".equals(key)) return progressFlag(d, "q04") ? "已开放" : "需本人首通 Q04";
        if ("forge_t3".equals(key)) return progressFlag(d, "q07") ? "已开放" : "需本人首通 Q07";
        if ("challenge".equals(key)) return challengeOpen(d) ? "已开放" : "需本人首通 Q07";
        if ("awaken".equals(key) || "awaken_next".equals(key) || "set_progress".equals(key)) {
            EmberLoadoutService ls = plugin.getEmberLoadouts();
            EmberLoadout l = ls == null ? null : ls.get(p);
            if (l == null) return "";
            return "awaken".equals(key) ? l.setLabel() : "set_progress".equals(key) ? l.setProgress() : l.nextAwakeningHint();
        }
        int us = key.indexOf('_');
        if (us > 0) {
            EmberRunMaps.MapDef m = maps.byKey(key.substring(0, us));
            String f = key.substring(us + 1);
            if (m != null) {
                switch (f) {
                    case "state": return stateLabel(d, m);
                    case "open": return unlocked(d, m) ? "yes" : "no";
                    case "cleared": return firstCleared(d, m) ? "yes" : "no";
                    case "name": return m.name;
                    case "cost": return maps.cost + " 体力";
                    case "tier": return m.dropLabel;
                    case "purpose": return m.purpose;
                    case "fc": return firstCleared(d, m) ? "已领取" : m.firstClearLabel();
                    default: return "";
                }
            }
        }
        return "";
    }
}
