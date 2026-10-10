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
import town.sunshine.corerpg.p1.encounter.RevivePoint;
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
 * <p>D230 / ARCH S3-1: rush / echo / outpost settle + menu live in {@link EmberRushService}; this class keeps thin delegates.
 * <p>D231 / ARCH S3-2: abyss fee / floor-best / menu live in {@link EmberAbyssService}; this class keeps thin delegates.
 * <p>D232 / ARCH S3-3: 自选誓约 live in {@link EmberPledgeService}; this class keeps thin delegates.
 * <p>D233 / ARCH S3-4: 团本 weekly cap / labels / settle grants / D106 falls live in {@link EmberRaidService}; this class
 * keeps thin delegates and forwards the raid {@code @EventHandler}s.
 * <p>D234 / ARCH S3-5: 招募板 live in {@link EmberRecruitService}; this class keeps thin delegates and forwards the
 * recruit {@code @EventHandler}s (season apply on join stays here).
 * <p>D235 / ARCH S3-6: entry gates (party / stamina / problem lines / mode gates), D96/D104 readiness and the weekly-rule /
 * pledge pick live in {@link EmberEntryService}.
 * <p>D237 / ARCH S3-8: session create / stamina+fee reservation / DP dispatch / verifyEntry / commit / release live in
 * {@link EmberSessionService}.
 * <p>D238 / ARCH S3-9: settlement ({@code settleFor} / {@code onBossKilled} / {@code failRefund}) live in
 * {@link EmberSettleService}; this class keeps thin delegates.
 */
public final class EmberRunService implements Listener {

    public static final String P = ChatColor.GOLD + "[余烬] " + ChatColor.GRAY;
    public static final String FILE = "ember-v1-runs.yml";
    static final String C_TARGET = "p1_target";
    static final String C_MARK = "p1_mark_t";
    static final String C_STARTER = "p1_starter";
    /** P2-1: weekly challenge rotation bonus clears, period = rotation week key */
    static final String C_ROTATION = "p2_rotation";
    static final String C_UNLOCK = "p1_unlock_";
    static final String C_FIRST = EmberForgeService.FLAG_PREFIX; // D205: fact p1_first_clear_<map>@all (see EmberFirstClear); package paid = p1_fcpay_<map>@<ver>

    private static volatile EmberRunService instance;

    private final CoreRpgPlugin plugin;
    private final EmberLoadoutService loadouts;
    private final EmberRunStore store;
    private final EmberRushService rush;
    private final EmberShortService shortExpedition;
    private final EmberAbyssService abyss;
    private final EmberPledgeService pledge;
    private final EmberRaidService raid;
    private final EmberRecruitService recruit;
    private final EmberEntryService entry;
    private final EmberSessionService session;
    private final EmberSettleService settle;
    private final EmberRunPapi papi;
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
        this.rush = new EmberRushService(this);
        this.shortExpedition = new EmberShortService(this);
        this.abyss = new EmberAbyssService(this);
        this.pledge = new EmberPledgeService(this);
        this.raid = new EmberRaidService(this);
        this.recruit = new EmberRecruitService(this);
        this.entry = new EmberEntryService(this);
        this.session = new EmberSessionService(this);
        this.settle = new EmberSettleService(this);
        this.papi = new EmberRunPapi(this);
        instance = this;
        load();
    }

    public static EmberRunService get() { return instance; }

    Logger log() { return plugin.getLogger(); }
    CoreRpgPlugin plugin() { return plugin; }

    public EmberRunMaps maps() { return maps; }

    EmberRunStore store() { return store; }

    EmberRushService rush() { return rush; }
    EmberShortService shortExpedition() { return shortExpedition; }

    EmberAbyssService abyss() { return abyss; }

    EmberPledgeService pledge() { return pledge; }

    EmberRaidService raid() { return raid; }

    EmberRecruitService recruit() { return recruit; }

    EmberEntryService entry() { return entry; }

    EmberSessionService session() { return session; }

    EmberSettleService settle() { return settle; }

    /** package: display-only leaderboard (nullable) — EmberSettleService D238 */
    EmberLeaderboard leaderboard() { return top; }

    /** package: the director bound to an instance world (null = none) — EmberRaidService D106 flow */
    EmberRunDirector director(String world) { return byWorld.get(world); }

    /** package seed for {@link EmberAbyssService#tryEnter} (same SecureRandom as other entries). */
    long nextSeed() { return rnd.nextLong(); }

    /** package: 0..46655 token for {@link EmberSessionService#runId} (same SecureRandom). */
    int nextRunToken() { return rnd.nextInt(36 * 36 * 36); }

    /** package entry point for an abyss segment (challenge=true, abyss tier, preset seed). */
    boolean enterAbyssSegment(Player leader, String mapKey, int abyssTier, long seed) {
        return enter(leader, mapKey, true, abyssTier, seed);
    }

    // ------------------------------------------------------------------ session map / passes (EmberSessionService D237)

    void putSession(EmberRunSession s) { sessions.put(s.runId, s); }

    void removeSession(String runId) { sessions.remove(runId); }

    void putPass(UUID id, String mapKey, long untilMs) { passes.put(id, new Object[]{mapKey, untilMs}); }

    void clearPass(UUID id) { passes.remove(id); }

    void ledgerRow(UUID u, String run, String key, String result, String status) {
        EmberRunRules.Ledger l = store.ledger(u);
        boolean[] c = new boolean[1];
        EmberRunRules.Row r = l.record(run, key, result, status, System.currentTimeMillis(), c);
        if (c[0]) store.saveLedger(u, Collections.singletonList(r));
    }

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
        festival = EmberFestival.load(plugin); // D139: the event dungeon rides in as root `events:`
        festival.setRuns(this);
        if (!festival.eventsSection().isEmpty()) {
            Map<Object, Object> r2 = new java.util.LinkedHashMap<Object, Object>(root);
            r2.put("events", festival.eventsSection());
            root = r2;
        }
        maps = EmberRunMaps.parse(root);
        for (String e : maps.validate()) log().warning("[P1 run] " + FILE + ": " + e);
        log().info("[P1 run] maps " + maps.maps.keySet() + " raids " + maps.raids.keySet() + " events " + maps.events.keySet() + " cost=" + maps.cost + " party=" + maps.partyMin + ".." + maps.partyMax);
    }

    /** D139 国庆 event (ember-v1-festival.yml) */
    private EmberFestival festival;
    public EmberFestival festival() { return festival; }
    boolean isRunMob(UUID e) { return byEntity.containsKey(e); }

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
    PlayerData dataOf(UUID id) { return data(id); }
    void flushData(UUID id) { plugin.getDataStore().flushMutation(id); } // D107
    EmberLoadoutService loadouts() { return loadouts; }

    /** P2-9 (D83) titles / trails (set by the plugin at enable) */
    private EmberCosmetics cosmetics;
    public void setCosmetics(EmberCosmetics c) { cosmetics = c; }
    public EmberCosmetics cosmetics() { return cosmetics; }

    /** D116 seasons + D117 weekly goals (set by the plugin at enable) */
    private EmberSeason season;
    public void setSeason(EmberSeason v) { season = v; }
    public EmberSeason season() { return season; }
    /** P2-10 (D84) display-only leaderboards */
    private EmberLeaderboard top;
    public void setLeaderboard(EmberLeaderboard t) { top = t; }

    public boolean unlocked(PlayerData d, EmberRunMaps.MapDef m) {
        return m.requires == null || m.requires.isEmpty() || d.periodCount(C_UNLOCK + m.key, "all") > 0;
    }

    /** D205: first-clear fact of a map key (any content version, or the admin stub) — unlocks / gates / hints. */
    public boolean firstClearedKey(PlayerData d, String key) {
        return EmberFirstClear.fact(d, key);
    }

    /** D205: first clear of {@code key} ever (fact key @all, legacy @ver, or the admin stub). Null key = no requirement. */
    public boolean progressFlag(PlayerData d, String key) {
        if (key == null) return true;
        return EmberFirstClear.fact(d, key);
    }

    /** D205: first-clear fact of a map (display / progression). */
    public boolean firstClearDone(PlayerData d, EmberRunMaps.MapDef m) {
        return EmberFirstClear.fact(d, m.key);
    }

    /** D205: the first-clear package of this map's current content version was paid ("first clears stay canonical"). */
    public boolean firstCleared(PlayerData d, EmberRunMaps.MapDef m) {
        return EmberFirstClear.paid(d, m.key, m.contentVersion);
    }

    public String target(PlayerData d) {
        int i = d.periodCount(C_TARGET, "all");
        return i >= 1 && i <= 3 ? EmberRunRules.FAMILIES[i - 1] : null;
    }

    public int marks(PlayerData d, int tier) { return d.periodCount(C_MARK + tier, "all"); }

    // ------------------------------------------------------------------ entry (§20.5 reserve → create → commit)

    /** package: the open session this player is in (EmberEntryService busy gate, D235) */
    EmberRunSession openSessionOf(UUID id) {
        for (EmberRunSession s : sessions.values()) if (s.open() && s.participants.contains(id) && !s.left.contains(id)) return s;
        return null;
    }

    /**
     * Entry for /corerpg enter q01..q05 (TicketEntryService routes the P1 kinds here). Validates every participant
     * (unlock, stamina, not already in a run); session create / reserve / DP live in EmberSessionService (D237);
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
        // D297 W1c: first weekly attune confirm (town only; not abyss)
        EmberGrowthService g = EmberGrowthService.get();
        if (g != null && g.maybeAttunePrompt(leader, mapKey, challenge)) return true;
        return enter(leader, mapKey, challenge, 0, 0L);
    }

    /** D297: confirm panel "就这样进本" bypasses the weekly prompt (already marked). */
    public boolean tryEnterAfterAttuneConfirm(final Player leader, String mapKey, final boolean challenge) {
        return enter(leader, mapKey, challenge, 0, 0L);
    }

    // ------------------------------------------------------------------ P2-2 abyss (book §18.3, D70) — logic in EmberAbyssService (D231 / ARCH S3-2)

    static final String C_ABYSS_BEST = EmberAbyssService.C_ABYSS_BEST;
    static final String C_RAID = EmberRaidService.C_RAID; // D233
    static final String C_BOUNTY = "p2_bounty";

    /** P2-7 (D79): ember-v1.yml bounty.daily */
    public List<EmberRunRules.BountyTier> bountyTiers() {
        EmberMode mode = EmberMode.get();
        return EmberRunRules.bountyTiers(mode == null ? null : mode.config().getMapList("bounty.daily"));
    }

    /** New-player polish: the one next thing to do on the main line (first map not yet first-cleared), then the late game. */
    public String nextStep(PlayerData d) {
        return nextStep(d, null);
    }

    /** D87: pending first-clear choice → pick it; no target family after Q01 → set one; else the next first clear. */
    public String nextStep(PlayerData d, UUID u) {
        if (u != null) for (EmberRunRules.Row r : store.ledger(u).open())
            if (EmberRunRules.ST_AWAIT.equals(r.status)) return "领取首通自选：点聊天里的按钮，或冒险页「首通自选」";
        EmberRunMaps.MapDef first = maps.maps.isEmpty() ? null : maps.maps.values().iterator().next();
        if (first != null && firstCleared(d, first) && target(d) == null) return "选掉落目标族（冒险页第 4 行）：之后约六成掉你选的族";
        EmberRunMaps.MapDef second = maps.maps.size() < 2 ? null : new ArrayList<EmberRunMaps.MapDef>(maps.maps.values()).get(1);
        if (u != null && first != null && firstCleared(d, first) && second != null && !firstCleared(d, second)) { // D96/D98: Q02 needs a T1 blade and a T1 charm (p1sim: 0 % with either at T0)
            Player op = Bukkit.getPlayer(u);
            EmberLoadoutService ls = plugin.getEmberLoadouts();
            EmberLoadout l = op == null || ls == null ? null : ls.get(op);
            if (l != null && (l.blade == null || l.blade.tier < 1)) // D98: the Q01 choice is the charm, the blade drops in Q01
                return "在 Q01 多打几局刷一件 T1 刃（Q01 偏向掉刃，拿到自动换上），再去首通 Q02";
            if (l != null && (l.charm == null || l.charm.tier < 1))
                return "领 Q01 首通自选的 T1 护符（生命约翻倍），再去首通 Q02";
        }
        for (EmberRunMaps.MapDef m : maps.maps.values()) {
            if (!firstCleared(d, m)) return "首通 " + m.key.toUpperCase(Locale.ROOT) + " " + m.name + "（首通开放下一张图）";
        }
        return "主线已完结 · 挑战版 / 深渊 · 余烬层 / 团本 · 每日委托";
    }

    /** E-review #6: a pending piece voucher (Q02) asks for family + slot, a fixed-slot choice (Q01 charm) for the family */
    private void choiceButtons(Player p, List<EmberRunRules.Row> rows) {
        for (EmberRunRules.Row r : rows) {
            if (!EmberRunRules.ST_AWAIT.equals(r.status)) continue;
            EmberRunRules.Grant g = EmberRunRules.Grant.decode(r.key, r.result);
            String map = r.key.length() >= 6 ? r.key.substring(3, 6) : "";
            if (g != null && "piece".equals(g.id)) pieceButtons(p, map, g.amount);
            else familyButtons(p, P + ChatColor.YELLOW + map.toUpperCase(Locale.ROOT) + " 首通自选待领取（悬停看三族区别），点一个族：", "/corerpg p1 firstclear");
        }
    }

    /** E-review #6: six family × slot buttons for the Q02 free targeted exchange */
    private void pieceButtons(Player p, String map, int tier) {
        java.util.List<net.md_5.bungee.api.chat.BaseComponent> parts = new java.util.ArrayList<net.md_5.bungee.api.chat.BaseComponent>();
        // F-review #6: say when the player already owns a set, mark the pieces already owned, and that it keeps
        java.util.Set<String> owned = new java.util.HashSet<String>();
        for (ItemStack x : p.getInventory().getContents()) {
            if (x == null || !loadouts.items().hasData(x)) continue;
            EmberItems.Read r = loadouts.items().read(x);
            if (r != null && r.ok() && r.data != null && r.data.tier >= tier) owned.add(r.data.family + ":" + r.data.slot);
        }
        EmberLoadout lo = loadouts.refresh(p);
        if (!"none".equals(lo.activeSet))
            p.sendMessage(P + "§7你已成套（" + EmberItemData.familyName(lo.activeSet) + "刃 + 护符）：可以换另一族备用、以后试别的套装；不急就先留着，"
                    + "这次兑换一直有效（装备页「补领」或 /corerpg p1 firstclear 再领）。");
        parts.add(new net.md_5.bungee.api.chat.TextComponent(P + ChatColor.YELLOW + map.toUpperCase(Locale.ROOT) + " 首通：一次免费定向兑换 T" + tier + "，选族和部位："));
        String[][] fam = {{"scorch", "§6焚烬"}, {"burst", "§c烬爆"}, {"sustain", "§a炽愈"}};
        for (String[] f : fam) for (String slot : new String[]{"blade", "charm"}) {
            boolean have = owned.contains(f[0] + ":" + slot);
            net.md_5.bungee.api.chat.TextComponent b = new net.md_5.bungee.api.chat.TextComponent(" §7[" + f[1] + EmberItemData.slotName(slot) + (have ? "§8·已有" : "") + "§7]");
            b.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND,
                    "/corerpg p1 firstclear " + f[0] + " " + map + " " + slot));
            b.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
                    new net.md_5.bungee.api.chat.ComponentBuilder(EmberItemData.familyBlurb(f[0]) + "\n§7点击领取 T" + tier + " 标准" + EmberItemData.slotName(slot)
                            + (have ? "\n§8你已经有这一族的 T" + tier + " " + EmberItemData.slotName(slot) + "（会多一件备用）" : "\n§7缺哪件补哪件：两件同族才成套")).create()));
            parts.add(b);
        }
        p.spigot().sendMessage(parts.toArray(new net.md_5.bungee.api.chat.BaseComponent[0]));
    }

    /** D87: one chat line of clickable family buttons running {@code command + " " + family} */
    private void familyButtons(Player p, String prefix, String command) {
        java.util.List<net.md_5.bungee.api.chat.BaseComponent> parts = new java.util.ArrayList<net.md_5.bungee.api.chat.BaseComponent>();
        parts.add(new net.md_5.bungee.api.chat.TextComponent(prefix));
        String[][] fam = {{"scorch", "§6[焚烬]", EmberItemData.familyBlurb("scorch")}, {"burst", "§c[烬爆]", EmberItemData.familyBlurb("burst")}, {"sustain", "§a[炽愈]", EmberItemData.familyBlurb("sustain")}};
        for (String[] f : fam) {
            net.md_5.bungee.api.chat.TextComponent b = new net.md_5.bungee.api.chat.TextComponent(" " + f[1] + " ");
            b.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, command + " " + f[0]));
            b.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
                    new net.md_5.bungee.api.chat.ComponentBuilder(f[2] + "\n§7点击选择").create()));
            parts.add(b);
        }
        p.spigot().sendMessage(parts.toArray(new net.md_5.bungee.api.chat.BaseComponent[0]));
    }

    /** D88: one chat line of six family×slot buttons for a mark tier (each asks for a confirm click) */
    /** D104 (midgame #3): the exchange preview compares the new +0 piece with the one in use and names the free swap. */
    private void exchangeCompare(Player p, String fam, String slot, int tier) {
        EmberTables t = EmberMode.tables();
        EmberLoadout lo = loadouts.get(p);
        EmberItemData cur = lo == null ? null : ("blade".equals(slot) ? lo.blade : lo.charm);
        boolean blade = "blade".equals(slot);
        double base = blade ? t.weaponA(tier) : t.charmH(tier);
        String stat = blade ? "攻击" : "生命 +";
        if (cur == null) {
            p.sendMessage(P + "§7兑换得到：" + stat + String.format(Locale.ROOT, "%.1f", base) + "（标准 +0）");
            return;
        }
        double g = EmberFormula.growth(t, cur.quality, cur.craft, cur.enhance);
        double now = (blade ? t.weaponA(cur.tier) : t.charmH(cur.tier)) * g;
        double moved = base * EmberFormula.growth(t, 0, 0, cur.enhance);
        p.sendMessage(P + String.format(Locale.ROOT, "§7现在用的：%s · %s%.1f §8｜ §7兑换得到：%s%.1f（+0）· 把 +%d 互换过来后 %s%.1f",
                cur.shortLabel(), stat, now, stat, base, cur.enhance, stat, moved));
        p.sendMessage(P + "§7强化可在工坊「互换」免费挪到新件；成色 / 精工不跟着走。想保留成色 / 精工就用升阶。");
    }

    private void exchangeButtons(Player p, int tier) {
        java.util.List<net.md_5.bungee.api.chat.BaseComponent> parts = new java.util.ArrayList<net.md_5.bungee.api.chat.BaseComponent>();
        parts.add(new net.md_5.bungee.api.chat.TextComponent(P + "§fT" + tier + " 兑换："));
        String[][] fam = {{"scorch", "§6焚烬"}, {"burst", "§c烬爆"}, {"sustain", "§a炽愈"}};
        for (String[] f : fam) for (String slot : new String[]{"blade", "charm"}) {
            net.md_5.bungee.api.chat.TextComponent b = new net.md_5.bungee.api.chat.TextComponent(" §7[" + f[1] + EmberItemData.slotName(slot) + "§7] ");
            b.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND,
                    "/corerpg p1 marks exchange " + f[0] + " " + slot + " " + tier));
            b.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
                    new net.md_5.bungee.api.chat.ComponentBuilder("§7点击预览，再点 [确认兑换]").create()));
            parts.add(b);
        }
        p.spigot().sendMessage(parts.toArray(new net.md_5.bungee.api.chat.BaseComponent[0]));
    }

    public String bountyLabel(PlayerData d) {
        return EmberRunRules.bountyLine(bountyTiers(), d.periodCount(C_BOUNTY, town.sunshine.corerpg.DailyService.today()));
    }

    /** D144 花样委托 tiers (ember-v1.yml bounty.variety) */
    public List<EmberRunRules.VarietyBounty> varietyBounties() {
        EmberMode mode = EmberMode.get();
        return EmberRunRules.varietyBounties(mode == null ? null : mode.config().getMapList("bounty.variety"));
    }

    /** D147 (review 10-04 #8): settlement label prefix for extra material rows, so they read as what paid them */
    static String matSource(String key) {
        if (key == null) return "";
        if ("var_affix_shard".equals(key)) return "词缀精英 ";
        if ("var_event_core".equals(key)) return "限时清房 ";
        if ("var_event_crystal".equals(key)) return "砸余烬晶 ";
        if ("var_event_escort".equals(key)) return "护宝兔 ";
        if ("var_event_hold".equals(key)) return "占点 ";
        if ("var_event_beacon".equals(key)) return "护灯 ";
        if ("var_event_relay".equals(key)) return "传火 ";
        if ("var_event_breach".equals(key)) return "裂隙 ";
        if ("var_event_chain".equals(key)) return "连斩 ";
        if ("var_event_unscathed".equals(key)) return "无伤 ";
        if ("honor_shard".equals(key)) return "勋记 ";
        if (key.startsWith("vb_")) return "花样委托 ";
        return "";
    }
    static final String C_VBOUNTY = "p4_vb_"; // + kind, period = stamina day: variety outcomes settled today

    /** D144: one line「花样委托 词缀精英 1/2 ✔ · 限时清房 0/1」for menus / settlement */
    public String varietyBountyLine(PlayerData d) {
        List<EmberRunRules.VarietyBounty> l = varietyBounties();
        if (l.isEmpty() || d == null) return "";
        String day = town.sunshine.corerpg.DailyService.today();
        StringBuilder b = new StringBuilder();
        for (EmberRunRules.VarietyBounty v : l) {
            int n = Math.min(v.count, d.periodCount(C_VBOUNTY + v.kind, day));
            b.append(b.length() == 0 ? "" : " §7· ").append(n >= v.count ? "§a✔ " : "§e").append(v.label()).append(" ").append(n).append("/").append(v.count)
                    .append(n >= v.count ? "" : "§7（" + v.rewardText() + "）");
        }
        return b.toString();
    }

    // ------------------------------------------------------------------ P2-5 raids — logic in EmberRaidService (D233 / ARCH S3-4)

    /** P2-5: settled clears of this raid in the current Monday-based week — delegated to {@link EmberRaidService} (D233). */
    public int raidWeek(PlayerData d, EmberRunMaps.MapDef m) { return raid.week(d, m); }

    /** P2-6 (D78): the weekly counter of a raid = its cap_group, else its own key — {@link EmberRaidService#capKey} (D233). */
    static String capKey(EmberRunMaps.MapDef m) { return EmberRaidService.capKey(m); }

    /** menu / PAPI label — delegated to {@link EmberRaidService} (D233). */
    public String raidLabel(PlayerData d, EmberRunMaps.MapDef m) { return raid.label(d, m); }

    public boolean abyssOpen(PlayerData d) { return abyss.open(d); }

    /** highest fully cleared abyss tier of this character (0 = none) */
    public int abyssBest(PlayerData d) { return abyss.best(d); }

    /** highest tier this character may start now (best + 1, capped by the table) */
    public int abyssMaxStart(PlayerData d) { return abyss.maxStart(d); }

    /**
     * One abyss segment = one new entry (book §18.3): the seeded map at challenge values × tier factors, 30 stamina +
     * the tier fee reserved now and refunded like stamina, settled only when its boss dies.
     * Delegated to {@link EmberAbyssService} (D231).
     */
    public boolean tryEnterAbyss(final Player leader, int tier) { return abyss.tryEnter(leader, tier); }

    /**
     * Shared entry: gates / readiness / weekly rule live in {@link EmberEntryService} (D235 / ARCH S3-6); session create,
     * seed / extra / variety, stamina + abyss-fee reservation, DP dispatch and verifyEntry live in
     * {@link EmberSessionService} (D237 / ARCH S3-8). Settlement lives in {@link EmberSettleService} (D238).
     */
    private boolean enter(final Player leader, String mapKey, final boolean challenge, final int abyss, long presetSeed) {
        boolean ch = challenge;
        if (abyss <= 0) { // D519 sticky challenge preference on cleared mainline
            EmberRunMaps.MapDef md = maps.byKey(mapKey);
            PlayerData ld = dataOf(leader.getUniqueId());
            if (md != null && !md.raid && !md.event && !md.rush && !md.shortExpedition && ld != null) {
                boolean want = EmberChallengePath.resolve(challenge, EmberChallengePath.get(ld),
                        progressFlag(ld, md.key), challengeOpen(ld)); // fact = cleared (paid package not required)
                if (want != challenge) {
                    ch = want;
                    leader.sendMessage(P + (ch ? "§c硬本·偏挑战 §7已改为挑战版进本" : "§a硬本·偏普通 §7已改为普通版进本")
                            + " §8· /corerpg p1 challengepath");
                } else ch = want;
            }
        }
        final EmberEntryService.Admit a = entry.admit(leader, mapKey, ch, abyss);
        if (a == null) return true;
        if (entry.readinessHold(leader, a.map, a.party, ch, abyss)) return true;
        if (abyss <= 0 && a.map != null) {
            if (a.map.raid) EmberGoalPath.maybeGlance(leader, this, "raid"); // D521
            else if (a.map.key != null && a.map.key.equals(featured(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone()))))
                EmberGoalPath.maybeGlance(leader, this, "featured"); // D521
        }
        return session.start(leader, a, ch, abyss, presetSeed);
    }

    /** package: DP create failed / nobody entered — {@link EmberSessionService} (D237). */
    void abort(EmberRunSession s, String why, boolean refund) {
        if (!s.open()) return;
        s.state = EmberRunSession.ABORTED;
        s.reason = why;
        if (refund) for (UUID u : s.participants) session.release(s, u, why);
        store.save(s);
        endInstance(s, false);
    }

    /** package: world-change commit — {@link EmberSessionService} (D237). */
    void commit(EmberRunSession s, UUID u) { session.commit(s, u); }

    /** F-review #5 (D124): T3 marks that would pay this fee — delegated to {@link EmberAbyssService} (D231). */
    int feeMarks(PlayerData d, int fee) { return abyss.feeMarks(d, fee); }

    /** D128 fail refund — {@link EmberSettleService} (D238). */
    private int failRefund(UUID u, EmberRunSession s) { return settle.failRefund(u, s); }

    /** D128 PAPI p1_failrefund — {@link EmberSettleService} (D238). */
    String failRefundLabel(UUID u) { return settle.failRefundLabel(u); }

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

    void onRoomStarted(EmberRunSession s, EmberRunMaps.Room r, boolean b, int spawned, int planned, String comp) {
        if (EmberRunSession.ENTERED.equals(s.state)) s.state = EmberRunSession.FIGHTING;
        if (s.fightStart == 0L) s.fightStart = System.currentTimeMillis(); // D116 raid clear time
        store.save(s);
        // D89: say what is in the room (the A/B variant letter meant nothing to players; it stays in the log)
        tellRun(s, "§e" + r.label + " §7· 敌人 " + spawned + (spawned < planned ? "/" + planned : "") + "：" + comp);
        if (!r.hint.isEmpty()) tellRun(s, "§e提示：§f" + r.hint); // D166
        log().info("[P1 run] " + s.runId + " " + r.id + " variant " + (b ? "B" : "A") + " " + comp.replaceAll("§.", ""));
        reviveFallen(s, RevivePoint.ROOM_OPEN); // D106
    }

    void onRoomCleared(EmberRunSession s, EmberRunMaps.Room r, boolean last) {
        store.save(s);
        if (raidRun(s)) { // D303 W1b: soft segment cue (≤1 line)
            tellRun(s, EmberRaidService.roomClearCue(r.label) + (r.door != null ? " §7· 门已开" : ""));
        } else {
            tellRun(s, "§a" + r.label + " 已清空" + (r.door != null ? " · 门已打开" : "") + (last ? "" : ""));
        }
    }

    void onBossSpawned(EmberRunSession s, EmberRunMaps.Boss b) {
        if (EmberRunSession.ENTERED.equals(s.state)) { // D144: a rush has no rooms — its first boss starts the fight
            s.state = EmberRunSession.FIGHTING;
            if (s.fightStart == 0L) s.fightStart = System.currentTimeMillis();
            EmberRunMaps.MapDef rm = maps.byKey(s.mapKey);
            if (rm != null && rm.rush && rm.mainRush()) { // D160: attempts are only counted for stats (p4_rush), they never gate an entry
                String wk = EmberRunRules.rotationWeekKey(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone()));
                for (UUID u : s.committed) { PlayerData pd = data(u); if (pd != null) { pd.addPeriodCount(C_RUSH, wk, 1); flushData(u); } }
                log().info("[P1 run] " + s.runId + " rush attempt counted for " + s.committed.size() + " (stats only, D160)");
            }
            store.save(s);
        }
        if (raidRun(s)) { // D303 W1b: boss-hall identity nail (≤1 line)
            tellRun(s, EmberRaidService.bossCue(s.mapKey));
        } else {
            tellRun(s, "§c首领 " + b.name + (s.challenge ? "（挑战）" : "") + " §7现身 · 招式都有预警，看清地面火线再躲");
        }
        reviveFallen(s, RevivePoint.BOSS_SPAWN); // D106
    }

    // ------------------------------------------------------------------ D106 raid falls — logic in EmberRaidService (D233 / ARCH S3-4)

    void onBossPhase(EmberRunSession s, RevivePoint point) { reviveFallen(s, point == null ? null : point.why); }
    void onBossPhase(EmberRunSession s, String why) { reviveFallen(s, why); }

    boolean isRaid(EmberRunSession s) { return raid.isRaid(s); }

    private boolean raidRun(EmberRunSession s) { return raid.isRaid(s); }

    /** committed members standing in the instance (not fallen, not left, not spectating) — {@link EmberRaidService} (D233) */
    private List<Player> livingIn(EmberRunSession s) { return raid.livingIn(s); }

    private Player nearestLiving(EmberRunSession s, Player from) { return raid.nearestLiving(s, from); }

    private void watchTeammate(Player p, EmberRunSession s, boolean tell) { raid.watchTeammate(p, s, tell); }

    /** D106: revive every fallen raid member still in the instance at 50 % HP next to a living teammate (D233 → EmberRaidService). */
    void reviveFallen(EmberRunSession s, RevivePoint point) { raid.reviveFallen(s, point == null ? null : point.why); }
    void reviveFallen(EmberRunSession s, String why) { raid.reviveFallen(s, why); }

    /** D106: once a second — fallen raid member leash (D233 → EmberRaidService). */
    void leashFallen(EmberRunDirector d) { raid.leashFallen(d); }

    /** D106: spectator-menu teleports out of the raid instance are blocked for fallen members (D233 → EmberRaidService). */
    @EventHandler(ignoreCancelled = true)
    public void onSpectateTeleport(org.bukkit.event.player.PlayerTeleportEvent e) { raid.onSpectateTeleport(e); }

    /** D106: a fallen raid member's /dp leave is held once, /dp revive refused (D233 → EmberRaidService). */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFallenLeave(org.bukkit.event.player.PlayerCommandPreprocessEvent e) { raid.onFallenLeave(e); }

    /** /corerpg p1 watch — delegated to {@link EmberRaidService} (D233). */
    private boolean cmdWatch(CommandSender sender) { return raid.cmdWatch(sender); }

    void onExtraSpawned(EmberRunSession s) {
        // D182: reward elite announces its fixed light move (dodge hint + same 10+1 rewards)
        if (s.extra == EmberRunRules.Extra.ELITE && maps != null) {
            EmberRunMaps.EliteTwists.Twist tw = maps.eliteTwists.forMap(s.mapKey);
            if (tw != null) {
                String title = tw.alt != null
                        ? ("「" + tw.name + "」§7/「" + tw.alt.name + "」")
                        : ("「" + tw.name + "」");
                String hint = tw.hint + (tw.alt != null ? " §7· " + tw.alt.hint : "");
                tellRun(s, "§e额外事件：奖励精英" + title + "§7· " + hint
                        + " · 击败 → §f碎片 +" + EmberRunRules.ELITE_SHARD + " §7+ §f核心 +" + EmberRunRules.ELITE_CORE);
                return;
            }
        }
        tellRun(s, "§6侧边出现了「" + s.extra.label + "」§7（可跳过；奖励记为待结算，击败首领才发放）");
    }

    void onExtraDone(EmberRunSession s) {
        if (s.extraDone || !s.open()) return;
        s.extraDone = true;
        store.save(s);
        EmberRunMaps.MapDef em = maps.byKey(s.mapKey);
        if (em != null && em.event && festival != null) { festival.onExtraDone(s); return; } // D139: no settlement here
        tellRun(s, "§6「" + s.extra.label + "」完成 §7· 额外奖励已记为待结算，击败首领后统一发放");
    }

    /** D138: the affixed elite of a repeat normal run died. */
    void onAffixDone(EmberRunSession s, String affix) {
        if (s.affixDone || !s.open()) return;
        s.affixDone = true;
        store.save(s);
        tellRun(s, "§6词缀精英「" + EmberRunMaps.Variety.label(affix) + "」已击败 §7· 余烬碎片 +" + maps.variety.affixShard + " 记为待结算");
        log().info("[P1 run] " + s.runId + " affix " + affix + " done");
    }

    /** D138/D171: the marked room event of a repeat normal run was resolved (ok or not). */
    void onEventResult(EmberRunSession s, boolean ok, double secs) {
        if (s.eventDone || !s.open()) return;
        String t = String.format(Locale.ROOT, "%.1f", secs);
        String kind = s.eventKind == null || s.eventKind.isEmpty() ? "timed" : s.eventKind;
        String name = EmberRunMaps.Variety.eventLabel(kind);
        if (ok) {
            s.eventDone = true;
            store.save(s);
            tellRun(s, "§b" + name + "完成 §7（" + t + " 秒）· 余烬核心碎片 +" + maps.variety.eventCore + " 记为待结算");
        } else {
            int lim = maps.variety.eventLimit(kind);
            tellRun(s, lim > 0
                    ? "§7" + name + "未达成（" + t + " 秒 / 限 " + lim + " 秒），这次没有额外核心"
                    : "§7" + name + "未达成，这次没有额外核心");
        }
        log().info("[P1 run] " + s.runId + " event " + kind + " " + (ok ? "done" : "fail") + " " + t + "s");
    }

    /** Server-side breakage (mob / boss cannot spawn): abort and give the stamina back. */
    void onBroken(EmberRunSession s, String why) {
        tellRun(s, ChatColor.RED + "本局异常终止：" + why + " · 体力退还");
        abort(s, why, true);
    }

    /** New-player polish: at run start, tell each participant where their heal potions are (or that they have none). */
    void potionCheck(EmberRunSession s) {
        EmberSupplyService sup = plugin.getEmberSupplies();
        if (sup == null) return;
        for (UUID u : s.participants) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline()) continue;
            int slot = sup.potionSlot(p);
            if (slot < 0) p.sendMessage(P + "§e你没带回复药：本局只能靠躲技能。出本后右键补给官 · 灰粮或在装备页购买（每瓶 " + EmberSupplyService.price() + " 余烬币）");
            else if (slot == 9) p.sendMessage(P + "§e回复药在背包里：按 E 拖到快捷栏，危险时按数字键切过去、按住右键喝");
            else p.sendMessage(P + "§7回复药在快捷栏第 5–9 格：按数字键切过去，按住右键喝（回复 20%，15 秒冷却）");
            EmberGrowthService g = EmberGrowthService.get(); // D455 本局签名一眼
            if (g != null) g.announceSigFeel(p);
            EmberSetService sets = plugin.getEmberSets(); // D457 本局套装/四件套 cue
            if (sets != null) sets.announceSetFeel(p);
        }
    }

    void tellRun(EmberRunSession s, String msg) {
        for (UUID u : s.participants) {
            Player p = Bukkit.getPlayer(u);
            if (p != null && p.isOnline()) p.sendMessage(P + msg);
        }
    }

    private void fail(EmberRunSession s, String why) {
        if (!s.open() || EmberRunSession.SETTLING.equals(s.state)) return;
        boolean chFail = s.challenge && s.fightStarted(); // read before the state flips to FAILED
        s.state = EmberRunSession.FAILED;
        s.reason = why;
        store.save(s);
        EmberRunMaps.MapDef fm = maps.byKey(s.mapKey);
        if (!chFail && fm != null && fm.raid && fm.weeklyCap > 0) { // recheck #3 (D133): the weekly raid count only moves on a clear
            raid.tellFailNoBurn(s, fm, why); // D233 → EmberRaidService
        } else if (!chFail && fm != null && fm.rush) { // D160: a failed rush costs nothing — say so, plus the reward state
            for (UUID u : s.participants) {
                Player p = Bukkit.getPlayer(u);
                if (p == null || !p.isOnline()) continue;
                boolean open = EmberRunRules.rushPaysReward(rushWeek(data(u), u, fm), fm.rushWeekly);
                p.sendMessage(P + ChatColor.RED + fm.rushLabel + "失败：" + why + " §a· 不扣任何东西（不耗体力、不算次数），随时可以再来"
                        + (open ? "§7（本周奖励还没领完，通关就领）" : "§7（本周奖励已领完，再来是练习）"));
                town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, new String[]{"[再来一次]", "/corerpg p1 rush " + fm.key + " go", "出副本后点；组队时由队长开", "RED"});
            }
        } else if (!chFail) tellRun(s, ChatColor.RED + "本局失败：" + why + "（已开战不退体力；未结算的额外奖励作废）");
        else for (UUID u : s.participants) { // endgame #6 (D128): the day's first failed challenge / abyss run gives half the stamina back
            int back = s.committed.contains(u) ? failRefund(u, s) : -1;
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline()) continue;
            p.sendMessage(P + ChatColor.RED + "本局失败：" + why + (back > 0
                    ? EmberSettleService.failRefundGrantedSuffix(back, EmberSettleService.failRefundPct(
                            EmberFailPath.effectiveShare(EmberFailPath.get(dataOf(u)), maps.failRefund)), s.abyss > 0)
                    : back == EmberFailPath.RESULT_SKIP ? EmberFailPath.skipSuffix()
                    : back == 0 ? EmberSettleService.failRefundAlreadyUsedSuffix()
                    : EmberSettleService.failRefundIneligibleSuffix()));
        }
        if (chFail) { // endgame #6 (D120): a failed challenge says what makes the next try likelier
            for (UUID u : s.committed) {
                Player p = Bukkit.getPlayer(u);
                if (p == null || !p.isOnline()) continue;
                try {
                    List<String> r = breakthroughRoutes(p, null);
                    if (!r.isEmpty()) p.sendMessage(P + "§7挑战版按两件 T3 调（只换刃约一半能过，两件 T3 基本稳过）· 下一次突破：§f" + r.get(0) + " §8（装备页「下一次突破」有按钮）");
                } catch (RuntimeException ignored) { /* hint only */ }
            }
        }
        // D295: fail also gets D283 playfeel summary (was settle-only — wipe taught nothing)
        EmberRunMaps.MapDef telemMap = maps.byKey(s.mapKey);
        for (UUID u : s.participants) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline()) continue;
            String feel = EmberSettleService.failPlayfeelLine(s);
            if (!feel.isEmpty()) p.sendMessage(P + feel);
            // D298: fail also counts (same eligible q01–q07口径)
            PlayerData pd = data(u);
            boolean vbApprox = !s.challenge && s.abyss == 0 && telemMap != null && !telemMap.raid && !telemMap.event
                    && (s.affixDone || s.eventDone);
            EmberPlayfeelTelemetry.record(s, telemMap, u, pd, false, vbApprox, log(), plugin.getDataFolder());
            if (pd != null) plugin.getDataStore().flushMutation(u);
        }
        endInstance(s, false);
    }

    /** Ends the DP instance via the ember-p1 script group; falls back to /dp leave for anyone still inside. */
    void endInstance(final EmberRunSession s, final boolean complete) { // package: EmberSettleService D238
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

    // ------------------------------------------------------------------ D144 余烬连战 (weekly boss rush) — logic in EmberRushService (D230 / ARCH S3-1)

    static final String C_RUSH = EmberRushService.C_RUSH;
    static final String C_RUSH_CLAIM = EmberRushService.C_RUSH_CLAIM;
    static final int RUSH_WEEKLY = EmberRushService.RUSH_WEEKLY;

    int rushWeek(PlayerData d, UUID u) { return rush.week(d, u); }

    int rushWeek(PlayerData d, UUID u, EmberRunMaps.MapDef m) { return rush.week(d, u, m); }

    static String rushRuleText(EmberRunMaps.MapDef m) { return EmberRushService.ruleText(m); }

    static String rushRewardText(EmberRunMaps.MapDef m) { return EmberRushService.rewardText(m); }

    static String rushChainText(EmberRunMaps.MapDef m) { return EmberRushService.chainText(m); }

    void onRushStart(EmberRunSession s, EmberRunMaps.MapDef m, EmberRunMaps.Boss first) { rush.onStart(s, m, first); }

    void onRushStage(EmberRunDirector d, int done, EmberRunMaps.Boss was, EmberRunMaps.Boss next, long secs, double breakSecs) {
        rush.onStage(d, done, was, next, secs, breakSecs);
    }

    /** D144 烬核同心: a clean 烬核 stack (called by the director; counted at the raid settlement, once per run) */
    void onCleanShare(EmberRunSession s, List<Player> inside) {
        int before = s.coreClean.size();
        for (Player p : inside) s.coreClean.add(p.getUniqueId());
        if (s.coreClean.size() > before) log().info("[P1 run] " + s.runId + " clean 烬核 stack n=" + inside.size());
    }

    private void rushSettle(EmberRunSession s, EmberRunMaps.MapDef m, UUID u) { rush.settle(s, m, u); }

    // ------------------------------------------------------------------ boss kill → settlement (§9, §20.5) — EmberSettleService D238

    private void onBossKilled(EmberRunDirector d, LivingEntity boss, boolean byParticipant) {
        settle.onBossKilled(d, boss, byParticipant);
    }

    // ------------------------------------------------------------------ B2.180 图录 · 装备

    /** /corerpg p1 codex [claim]: list the 20 entries, or claim every reached stage once (D42, ledger-delivered coin). */
    public boolean codexCommand(Player p, String[] args) {
        PlayerData d = data(p.getUniqueId());
        EmberLoadoutService ls = plugin.getEmberLoadouts();
        if (ls != null) { ls.backfillCodex(p); ls.refresh(p); }
        boolean claim = args.length >= 3 && "claim".equalsIgnoreCase(args[2]);
        if (!claim) {
            p.sendMessage(P + "§6图录 · 装备 §f" + EmberCodex.count(d) + "/" + EmberCodex.ENTRIES.size() + " §7（只做展示，不加属性）");
            StringBuilder sb = new StringBuilder();
            for (EmberCodex.Entry e : EmberCodex.ENTRIES)
                sb.append(EmberCodex.has(d, e.key()) ? "§a" : "§8").append(e.label()).append(' ');
            p.sendMessage(P + sb.toString().trim());
            for (int i = 0; i < EmberCodex.STAGE_AT.length; i++)
                p.sendMessage(P + "§7集齐 " + EmberCodex.STAGE_AT[i] + " 种：余烬币 " + EmberCodex.STAGE_COIN[i] + " · " + EmberCodex.stageLabel(d, i));
            StringBuilder lo = new StringBuilder(); // P2-9 (D81) where to farm what
            for (EmberRunMaps.MapDef m : maps.maps.values()) lo.append(lo.length() == 0 ? "" : " · ").append(m.key.toUpperCase(Locale.ROOT)).append(' ').append(EmberRunMaps.lootLabel(m));
            p.sendMessage(P + "§6掉落偏向 §7" + lo + "（目标族仍至少 60%；挑战 / 深渊同图同偏向）");
            p.sendMessage(P + "§6团本额外装备 §7按你的目标族（没选就按团本偏向族），成色至少精良 · 荣誉 " + (cosmetics == null ? "—" : cosmetics.earnedCount(d) + "/" + EmberCosmetics.ALL.size()) + "（主菜单「赛季 · 排行 · 周目标」）");
            EmberCodexPath.maybeAfterProgress(p, this); // D522 tip/auto when viewing
            return true;
        }
        java.util.List<Integer> can = EmberCodex.claimable(d);
        if (can.isEmpty()) { p.sendMessage(P + "§7没有可领取的图录阶段奖励（" + EmberCodex.count(d) + "/" + EmberCodex.ENTRIES.size() + "）"); return true; }
        claimCodexStages(p);
        return true;
    }

    /** D522: claim every claimable codex stage into the ledger and deliver; returns how many stages. */
    int claimCodexStages(Player p) {
        PlayerData d = data(p.getUniqueId());
        if (d == null) return 0;
        java.util.List<Integer> can = EmberCodex.claimable(d);
        if (can.isEmpty()) return 0;
        UUID u = p.getUniqueId();
        for (int i : can) {
            d.addPeriodCount(EmberCodex.C_CLAIM + i, "all", 1);
            ledgerRow(u, EmberCodex.LEDGER_RUN, "stage" + i, "coin:" + EmberCodex.STAGE_COIN[i], EmberRunRules.ST_PENDING);
            log().info("[P1 codex] " + p.getName() + " stage " + EmberCodex.STAGE_AT[i] + " -> coin " + EmberCodex.STAGE_COIN[i]);
        }
        plugin.getDataStore().flushMutation(u);
        deliver(p);
        return can.size();
    }

    // ------------------------------------------------------------------ delivery (E10: retry the original result)

    /** D177: a once-only PENDING ledger row outside a run (挂机庭 rounds); true when this call created it. Pay with deliver. */
    public boolean grantRow(UUID u, String run, String key, String result) {
        EmberRunRules.Ledger l = store.ledger(u);
        boolean[] c = new boolean[1];
        EmberRunRules.Row r = l.record(run, key, result, EmberRunRules.ST_PENDING, System.currentTimeMillis(), c);
        if (c[0]) store.saveLedger(u, Collections.singletonList(r));
        return c[0];
    }

    /** D177: deliver without the 「结算到账」 line (the caller prints its own). */
    public int deliverQuiet(Player p) {
        quietDeliver = true;
        try { return deliver(p); } finally { quietDeliver = false; }
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
        List<String> relLines = new ArrayList<String>(); // D299 W1c 相对穿着（无变化不入列）
        int waiting = 0, choices = 0;
        Map<String, Integer> mail = new LinkedHashMap<String, Integer>();
        List<EmberRunRules.Row> mailRows = new ArrayList<EmberRunRules.Row>();
        long now = System.currentTimeMillis();
        boolean[] hbEmpty = new boolean[9];
        for (int i = 0; i < 9; i++) { ItemStack x = p.getInventory().getItem(i); hbEmpty[i] = x == null || x.getType() == org.bukkit.Material.AIR; }
        // D208 (ARCH S1-4): a settlement signature is written INTO the piece when the piece is created (v2 sig_code on
        // cr_p1_item + NBT, inside the signed data) — no p1_sig_ counter. SIG rows settle after the ITEM rows below.
        Map<String, Integer> sigFor = new HashMap<String, Integer>();
        java.util.Set<String> itemOpen = new java.util.HashSet<String>(), stamped = new java.util.HashSet<String>(), itemsGiven = new java.util.HashSet<String>();
        boolean acqRepeatMark = false; String acqMarkMap = null; boolean acqStamp = false; // D456 sig acquisition clarity
        List<EmberRunRules.Row> sigRows = new ArrayList<EmberRunRules.Row>();
        for (EmberRunRules.Row r : open) {
            EmberRunRules.Grant g = EmberRunRules.Grant.decode(r.key, r.result);
            if (g == null) continue;
            if (g.kind == EmberRunRules.Kind.ITEM) itemOpen.add(g.id);
            if (g.kind == EmberRunRules.Kind.SIG && !EmberRunRules.ST_AWAIT.equals(r.status)) {
                int cut = g.id.indexOf('/');
                EmberSignature.Def sd = cut > 0 ? EmberSignature.byId(g.id.substring(cut + 1)) : null;
                if (sd != null) sigFor.put(g.id.substring(0, cut), sd.code);
            }
        }
        for (EmberRunRules.Row r : open) {
            if (EmberRunRules.ST_AWAIT.equals(r.status)) { choices++; continue; }
            EmberRunRules.Grant g = EmberRunRules.Grant.decode(r.key, r.result);
            if (g == null) { log().warning("[P1 run] undecodable ledger row " + r.runId + "/" + r.key + " = " + r.result); continue; }
            boolean done = false;
            switch (g.kind) {
                case COIN: {
                    // D215/D216: coin grants with a known REG source (incl. sign/online run id) go through grantCoin
                    String src = EmberEconomy.sourceForGrant(g.key, r.runId);
                    if (src != null) {
                        if (!EmberEconomy.grantCoin(d, src, g.amount))
                            log().warning("[P1 run] economy grantCoin refused " + src + " " + g.amount + " for " + p.getName());
                    } else {
                        d.addCoin(g.amount);
                    }
                    got.add("余烬币 " + g.amount);
                    done = true;
                    break;
                }
                case XP: {
                    // D216 / ARCH S2-3: XP grants with a known REG source are validated via grantXp
                    String src = EmberEconomy.sourceForGrant(g.key, r.runId);
                    if (src != null && !EmberEconomy.grantXp(src, g.amount))
                        log().warning("[P1 run] economy grantXp refused " + src + " " + g.amount + " for " + p.getName());
                    else {
                        ProgressService ps = plugin.getProgressService();
                        if (ps != null) ps.grantFlatEmberXp(p, g.amount, "余烬主线"); // econ-ok: grantXp validated above (S2-3)
                    }
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
                    // D216 / ARCH S2-3 + D228 / S2-8: every mark ledger row is credited by EmberEconomy (rush / outpost / raid /
                    // featured-week keys now resolve to S10–S12 / S16 / S17; unknown keys stay an untagged credit)
                    EmberEconomy.Credit cr = EmberEconomy.creditMarkLedger(d, g.key, r.runId, tier, g.amount);
                    if (cr == EmberEconomy.Credit.REFUSED)
                        log().warning("[P1 run] economy grantMark refused " + EmberEconomy.sourceForGrant(g.key, r.runId) + " " + g.key + " T" + tier + " " + g.amount + " for " + p.getName());
                    else if (cr == EmberEconomy.Credit.UNTAGGED)
                        log().info("[P1 run] untagged mark row " + r.runId + "/" + g.key + " T" + tier + " " + g.amount + " for " + p.getName());
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
                    // D216 / ARCH S2-3: mat grants with a known REG source are validated via grantMat first
                    String src = EmberEconomy.sourceForGrant(g.key, r.runId);
                    if (src != null && !EmberEconomy.grantMat(src, g.id, g.amount)) {
                        log().warning("[P1 run] economy grantMat refused " + src + " " + g.id + " " + g.amount + " for " + p.getName());
                        done = true; // refuse without delivering — keeps the ledger closed so it cannot retry forever
                        break;
                    }
                    EmberVault vlt = EmberVault.get(); // 1.62: whitelisted materials go straight into the warehouse (自动入库)
                    if (ni != null && vlt != null && vlt.autoDeposit(p, g.id, g.amount)) { // econ-ok: grantMat validated above
                        got.add(matSource(r.key) + ni.displayName(g.id) + " ×" + g.amount + "（进仓库）");
                        done = true;
                        break;
                    }
                    if (ni != null && freeSlots(p) >= (g.amount + 63) / 64 + 1) {
                        if (ni.giveNiItem(p, g.id, g.amount)) {
                            got.add(matSource(r.key) + ni.displayName(g.id) + " ×" + g.amount);
                            done = true;
                        }
                    }
                    if (!done) { mail.merge(g.id, g.amount, Integer::sum); mailRows.add(r); }
                    break;
                }
                case BMAT: { // D177 rev 2 挂机庭 loot: warehouse entry marked account-bound (never a physical item, never mailed)
                    // D221 / ARCH S2-5: AFK (and other) BMAT grants with a known REG source are validated via grantMat
                    String src = EmberEconomy.sourceForGrant(g.key, r.runId);
                    if (src != null && !EmberEconomy.grantMat(src, g.id, g.amount)) {
                        log().warning("[P1 run] economy grantMat refused " + src + " bmat " + g.id + " " + g.amount + " for " + p.getName());
                        done = true;
                        break;
                    }
                    EmberVault vlt = EmberVault.get();
                    if (ni != null && vlt != null && vlt.creditBound(p, g.id, g.amount)) { // econ-ok: grantMat validated above
                        got.add("挂机 " + ni.displayName(g.id) + " ×" + g.amount + "（进仓库 · 账号绑定）");
                        done = true;
                    } else waiting++; // stays pending (vault off / at cap) — retried on the next delivery
                    break;
                }
                case SIGMARK: { // D174 首领徽记: an account counter like the forge marks
                    // D228 / ARCH S2-8: S07 fc / S08 repeat / S09 pledge / S17 outpost / S18 echo / S23 sign-in via grantInsignia
                    EmberEconomy.Credit cr = EmberEconomy.creditInsigniaLedger(d, g.key, r.runId, g.id, g.amount);
                    if (cr == EmberEconomy.Credit.REFUSED)
                        log().warning("[P1 run] economy grantInsignia refused " + EmberEconomy.sourceForGrant(g.key, r.runId) + " " + g.key + " " + g.id + " " + g.amount + " for " + p.getName());
                    else if (cr == EmberEconomy.Credit.UNTAGGED)
                        log().info("[P1 run] untagged insignia row " + r.runId + "/" + g.key + " " + g.id + " " + g.amount + " for " + p.getName());
                    got.add(g.id.toUpperCase(Locale.ROOT) + " 首领徽记 " + g.amount + "（共 " + d.periodCount(EmberSignature.C_MARK + g.id, "all") + "）");
                    if (EmberSigAcq.isRepeatMarkKey(r.key)) { acqRepeatMark = true; acqMarkMap = g.id; } // D456
                    EmberSigChase.maybeReadyCue(p, d); // D470 rising-edge imprint-ready
                    EmberForgeMatReady.maybeReadyCue(p); // D478 forge mat-ready
                    if ("fc_sigmark".equals(r.key)) { // D174: the first clear of a signature map also announces its new unlock
                        String un = EmberSignature.IMPRINT_UNLOCK.equals(g.id) ? "烬炉烙印（用徽记把签名烙到自己的件上）"
                                : EmberSignature.DUAL_UNLOCK.equals(g.id) ? "双签名（刃 + 护符两条签名同时生效）"
                                : EmberSignature.ALT_UNLOCK.equals(g.id) ? "签名调律（7 件签名多一个换代价的调律版，" + EmberSignature.ALT_MARKS + " 枚那张图的徽记解锁）+ Q07 签名"
                                : "签名传奇（重打这张图掉它首领的签名件和徽记）";
                        town.sunshine.corerpg.ConfirmTokens.sendButton(p, P + "§6新解锁：§e" + un + " ", "[打开签名页]", "/corerpg p1 sig menu", "签名图鉴、首领徽记、烙印、开关签名");
                        p.sendMessage(P + "§7不用打命令：主菜单 → 装备 → 签名传奇"); // stage 1.5 one-line hint
                        if (EmberSignature.IMPRINT_UNLOCK.equals(g.id)) EmberSigChase.scheduleImprintOffer(p); // D464
                        if (EmberSignature.IMPRINT_UNLOCK.equals(g.id)) EmberCounterPath.scheduleOfferAfterQ02(p); // D480 Q02 wall awareness
                        if (EmberSignature.IMPRINT_UNLOCK.equals(g.id)) EmberSpicePath.scheduleOfferAfterQ02(p); // D500 spice encounter path
                        if (EmberSignature.IMPRINT_UNLOCK.equals(g.id)) EmberRoomPath.scheduleOfferAfterQ02(p); // D506 variety room path
                        if (EmberSignature.IMPRINT_UNLOCK.equals(g.id)) EmberBountyPath.scheduleOfferAfterQ02(p); // D508 variety bounty path
                        if ("q01".equals(g.id)) EmberFeaturedPath.scheduleOfferAfterQ01(p); // D488 featured map identity path
                        if ("q01".equals(g.id)) EmberFamilyPath.scheduleOfferAfterQ01(p); // D493 family combat path
                        if ("q01".equals(g.id)) EmberFlexPath.scheduleOfferAfterQ01(p); // D494 flex equip combat path
                        if ("q01".equals(g.id)) EmberExtraPath.scheduleOfferAfterQ01(p); // D501 extra (treasure/elite/chest) path
                        if ("q01".equals(g.id)) EmberPrepPath.scheduleOfferAfterQ01(p); // D502 potion prep path
                        if ("q01".equals(g.id)) EmberShortPath.scheduleOfferAfterQ01(p); // D503 short chase path
                        if ("q01".equals(g.id)) EmberTwistPath.scheduleOfferAfterQ01(p); // D504 elite twist path
                        if ("q01".equals(g.id)) EmberRefundPath.scheduleOfferAfterQ01(p); // D505 death refund path
                        if ("q01".equals(g.id)) EmberBarPath.scheduleOfferAfterQ01(p); // D512 potion hotbar path
                        if ("q01".equals(g.id)) EmberDailyPath.scheduleOfferAfterQ01(p); // D514 daily bounty path
                        if ("q01".equals(g.id)) EmberClaimPath.scheduleOfferAfterQ01(p); // D515 claim deliver path
                        if ("q01".equals(g.id)) EmberFriendPath.scheduleOfferAfterQ01(p); // D516 friend accept path
                        if ("q01".equals(g.id)) EmberEquipPath.scheduleOfferAfterQ01(p); // D517 equip auto path
                        if ("q01".equals(g.id)) EmberOnlinePath.scheduleOfferAfterQ01(p); // D518 online claim path
                        if ("q01".equals(g.id)) EmberSignPath.scheduleOfferAfterQ01(p); // D520 sign-in path
                        if ("q01".equals(g.id)) EmberCodexPath.scheduleOfferAfterQ01(p); // D522 codex stage path
                        if ("q01".equals(g.id)) EmberMailPath.scheduleOfferAfterQ01(p); // D523 mail attachment path
                        if ("q01".equals(g.id)) EmberMentorPath.scheduleOfferAfterQ01(p); // D524 mentor accept path
                        if ("q01".equals(g.id)) EmberPetPath.scheduleOfferAfterQ01(p); // D525 pet summon path
                        if ("q01".equals(g.id)) EmberStashPath.scheduleOfferAfterQ01(p); // D526 stash path
                        if ("q01".equals(g.id)) EmberGuildPath.scheduleOfferAfterQ01(p); // D527 guild invite path
                        if ("q01".equals(g.id)) EmberFeedPath.scheduleOfferAfterQ01(p); // D528 feed path
                        if ("q01".equals(g.id)) EmberTrailPath.scheduleOfferAfterQ01(p); // D529 trail path
                        if ("q01".equals(g.id)) EmberPartyPath.scheduleOfferAfterQ01(p); // D530 party invite path
                        if ("q01".equals(g.id)) EmberJunkPath.scheduleOfferAfterQ01(p); // D531 junk path
                        if ("q01".equals(g.id)) EmberTitlePath.scheduleOfferAfterQ01(p); // D532 title path
                        if (EmberSignature.DUAL_UNLOCK.equals(g.id)) EmberDualLead.scheduleOffer(p); // D466
                        if (EmberSignature.DUAL_UNLOCK.equals(g.id)) EmberSealPath.scheduleOfferAfterQ03(p); // D497 seal combat path
                        if (EmberSignature.ALT_UNLOCK.equals(g.id)) EmberRaidPath.scheduleOfferAfterQ07(p); // D483 raid focus path
                        if (EmberSignature.ALT_UNLOCK.equals(g.id)) EmberFailPath.scheduleOfferAfterQ07(p); // D507 fail stamina refund path
                        if (EmberSignature.ALT_UNLOCK.equals(g.id)) EmberFeePath.scheduleOfferAfterQ07(p); // D511 abyss fee pay path
                        if (EmberSignature.ALT_UNLOCK.equals(g.id)) EmberRecruitPath.scheduleOfferAfterQ07(p); // D513 recruit accept path
                        if (EmberSignature.ALT_UNLOCK.equals(g.id)) EmberChallengePath.scheduleOfferAfterQ07(p); // D519 challenge prefer path
                        if (EmberSignature.ALT_UNLOCK.equals(g.id)) EmberGoalPath.scheduleOfferAfterQ07(p); // D521 weekly goal focus path
                        if (EmberSignature.ALT_UNLOCK.equals(g.id)) EmberAbyssPath.scheduleOfferAfterQ07(p); // D486 abyss push/farm path
                        if (EmberSignature.ALT_UNLOCK.equals(g.id)) EmberAttunePath.scheduleOfferAfterQ07(p); // D492 attune combat path
                        if (EmberSignature.ALT_UNLOCK.equals(g.id)) EmberTunePath.scheduleOfferAfterQ07(p); // D498 tune combat path
                        String mode = modeUnlock(g.id); // D174 stage 2b: Q04 / Q05 / Q06 first clears also open a mode
                        if (mode != null) {
                            town.sunshine.corerpg.ConfirmTokens.sendButton(p, P + "§6新模式：§e" + mode + " ", "[打开进阶模式]", "/corerpg p1 modes", "首领残响 / 连战·前哨 / 自选誓约");
                            p.sendMessage(P + "§7不用打命令：主菜单 → 冒险 → 进阶模式");
                            EmberModePath.scheduleOffer(p, g.id); // D462 path pick ~2s later
                            if ("q04".equals(g.id)) EmberEchoPath.scheduleOffer(p); // D468 residual weekly path
                            if ("q04".equals(g.id)) EmberRestPath.scheduleOfferAfterQ04(p); // D509 rush rest heal path
                            if ("q04".equals(g.id)) EmberBreakPath.scheduleOfferAfterQ04(p); // D510 rush break timing path
                            if ("q04".equals(g.id)) EmberShapePath.scheduleOfferAfterQ04(p); // D490 slash shape combat path
                            if ("q05".equals(g.id)) EmberStepPath.scheduleOfferAfterQ05(p); // D491 step-dir combat path
                            if ("q05".equals(g.id)) EmberPosturePath.scheduleOfferAfterQ05(p); // D495 posture combat path
                            if ("q05".equals(g.id)) EmberStridePath.scheduleOfferAfterQ05(p); // D499 stride combat path
                            if ("q06".equals(g.id)) EmberPledgePath.scheduleOfferAfterQ06(p); // D481 pledge combat path
                            if ("q06".equals(g.id)) EmberTrialPath.scheduleOfferAfterQ06(p); // D496 trial combat path
                        }
                    }
                    done = true;
                    break;
                }
                case SIG: { // D174 → D208: settled after the ITEM rows (the piece is created with its signature inside)
                    int cut = g.id.indexOf('/');
                    if (cut <= 0 || EmberSignature.byId(g.id.substring(cut + 1)) == null) { log().warning("[P1 sig] bad stamp row " + r.runId + " " + g.id); done = true; break; }
                    sigRows.add(r);
                    break;
                }
                case ITEM: {
                    Integer sc = sigFor.get(g.id);
                    // D299 W1c：发件前快照同部位穿着，通关到账时对照（quietDeliver=补领静默；失败无 ITEM）
                    EmberItemData wornSnap = null;
                    if (!quietDeliver && g.item != null && g.item.slot != null) wornSnap = activePiece(p, g.item.slot);
                    String res = giveItem(p, g, sc == null ? 0 : sc, r);
                    if (res != null) {
                        got.add(res); done = true; itemsGiven.add(g.id);
                        if (sc != null && !res.endsWith("（已在背包）")) stamped.add(g.id);
                        if (wornSnap != null && g.item != null) {
                            String rel = EmberGearNextHint.relativeStats(g.item.quality, g.item.craft, wornSnap);
                            if (rel != null) relLines.add(rel + "§8（" + EmberItemData.slotName(g.item.slot) + "）");
                        }
                    } else waiting++;
                    break;
                }
                default:
                    break;
            }
            if (done) { r.status = EmberRunRules.ST_DELIVERED; r.updated = now; changed.add(r); }
        }
        for (EmberRunRules.Row r : sigRows) { // D208: the run's base piece carries the signature; never overwrites one
            EmberRunRules.Grant g = EmberRunRules.Grant.decode(r.key, r.result);
            int cut = g.id.indexOf('/');
            EmberSignature.Def sd = EmberSignature.byId(g.id.substring(cut + 1));
            String uid = g.id.substring(0, cut);
            String how;
            if (stamped.contains(uid)) how = "on the item";
            else if (itemOpen.contains(uid) && !itemsGiven.contains(uid)) continue; // the piece is still waiting: so is its signature
            else { // a piece delivered before 1.65.42 (v1, legacy counter read until its next durable write folds it)
                if (d.periodCount(EmberSignature.C_SIG + uid, "all") <= 0) d.addPeriodCount(EmberSignature.C_SIG + uid, "all", sd.code);
                how = "legacy counter";
            }
            EmberGrowthService.markSeen(d, sd); // stage 1.5 codex 「获得过」
            got.add("§6签名传奇！§e" + sd.name + "§f（" + sd.kindText() + "，" + sd.boss + "）");
            acqStamp = true; // D456
            log().info("[P1 sig] " + p.getName() + " stamp " + uid + " " + sd.id + " (" + r.runId + ", " + how + ")");
            r.status = EmberRunRules.ST_DELIVERED; r.updated = now; changed.add(r);
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
        tidyHotbar(p, hbEmpty);
        if (acqRepeatMark && !acqStamp && !quietDeliver) { // D456: miss clarity (no STAMP_RATE raise)
            String miss = EmberSigAcq.missLine(acqMarkMap);
            miss += EmberSigChase.missAppend(d, acqMarkMap); // D464
            got.add(miss);
        }
        if (!got.isEmpty() && !quietDeliver) p.sendMessage(P + "§a结算到账：§f" + String.join("§7、§f", got));
        if (!relLines.isEmpty() && !quietDeliver) { // D299 W1c：相对穿着一行（并列 D295 摘要，不改破绽语义）
            for (String rel : relLines) p.sendMessage(P + "§7" + rel);
        }
        sendUpgradeAsks(p); // D120
        if (waiting > 0) town.sunshine.corerpg.ConfirmTokens.sendButton(p, P + ChatColor.YELLOW + waiting + " 项奖励因背包已满暂存（结果已锁定，不会重抽）。空出格子后点：",
                "[补领]", "/corerpg p1 claim", "领取暂存的奖励（装备页也有「补领」）"); // D95
        if (choices > 0) choiceButtons(p, open);
        return changed.size();
    }

    /** D90: settlement loot that fell into a previously empty hotbar slot moves to the backpack (blade + potions keep the hotbar). */
    private void tidyHotbar(Player p, boolean[] wasEmpty) {
        org.bukkit.inventory.PlayerInventory inv = p.getInventory();
        EmberSupplyService sup = plugin.getEmberSupplies();
        for (int i = 0; i < 9; i++) {
            if (!wasEmpty[i]) continue;
            ItemStack x = inv.getItem(i);
            if (x == null || x.getType() == org.bukkit.Material.AIR) continue;
            if (sup != null && sup.isHealPotion(x)) continue;
            if (loadouts.items().hasData(x)) { EmberItems.Read r = loadouts.items().read(x); if (r != null && r.data != null && r.data.isBlade()) continue; }
            int to = -1;
            for (int j = 9; j < 36; j++) { ItemStack y = inv.getItem(j); if (y == null || y.getType() == org.bukkit.Material.AIR) { to = j; break; } }
            if (to < 0) return;
            inv.setItem(to, x);
            inv.setItem(i, null);
        }
    }

    private static int freeSlots(Player p) {
        int n = 0;
        ItemStack[] st = p.getInventory().getStorageContents();
        for (ItemStack i : st) if (i == null || i.getType() == org.bukkit.Material.AIR) n++;
        return n;
    }

    /** @return label when given (or already present with the same uid), null when the inventory has no room. */
    private String giveItem(Player p, EmberRunRules.Grant g, int sigCode, EmberRunRules.Row row) {
        EmberRunRules.ItemRoll it = g.item;
        String src = g.key.startsWith("fc_") || g.key.startsWith("starter_") ? "quest" : "drop";
        EmberItemData d = new EmberItemData(g.id, EmberItemData.templateId(it.tier == 0 ? "none" : it.family, it.slot, it.tier),
                it.tier == 0 ? "none" : it.family, it.slot, it.tier, it.quality, it.craft, 0, 0, true, src,
                EmberItemData.DATA_VERSION, 0, 0, 0, sigCode, 0, // D208: the settlement signature lives in the signed data
                EmberProvenance.forReward(g.key, row.runId, row.created)); // D245: map / REG row / run / time (deterministic per row)
        for (ItemStack s : p.getInventory().getContents()) {
            if (s == null || !loadouts.items().hasData(s)) continue;
            EmberItems.Read r = loadouts.items().read(s);
            if (r != null && r.data != null && g.id.equals(r.data.uid)) return d.shortLabel() + "（已在背包）";
        }
        if (d.isArmor()) { // D318 六槽: armor goes to the backpack (no gear library / auto-equip); wearing it = the armor page or drag
            if (freeSlots(p) <= 0) return null;
            ItemStack stack = loadouts.items().create(d);
            if (stack == null) return null;
            loadouts.remember(d, p.getUniqueId());
            p.getInventory().addItem(stack);
            return d.shortLabel() + "（护甲已放进背包，装备页「护甲」里可对比换上）";
        }
        EmberGearLib lib = EmberGearLib.get(); // 1.62 装备库: new drops go to the library while the backpack is nearly full
        if (lib != null && lib.usable()) {
            EmberItemData act = activePiece(p, d.slot);
            boolean up = act != null && d.tier >= 1 && EmberRunRules.upgradeVerdict(EmberMode.tables(), d, act,
                    loadouts.get(p) == null ? "none" : loadouts.get(p).activeSet, loadouts.get(p) == null ? 10 : loadouts.get(p).level) != EmberRunRules.UP_NONE;
            if (lib.autoStash(p, d, freeSlots(p), up, act != null)) return d.shortLabel() + "（已存入装备库）";
        }
        if (freeSlots(p) <= 0) return null;
        ItemStack stack = loadouts.items().create(d);
        if (stack == null) return null;
        loadouts.remember(d, p.getUniqueId());
        p.getInventory().addItem(stack);
        if (d.isCharm() && loadouts.autoSelectCharm(p, d.uid, d.tier)) // D85 onboarding: no more half-life new players
            return d.shortLabel() + "（已自动选定为生效护符）";
        String placed = null;
        if (d.isBlade() && d.tier >= 1) { // D85: the first real blade takes the T0 starter's hotbar slot
            int slot = starterBladeSlot(p);
            if (slot >= 0) {
                org.bukkit.inventory.PlayerInventory inv = p.getInventory();
                int at = slotOfUid(loadouts.items(), inv, d.uid);
                if (at >= 0 && at != slot) {
                    // recheck #4 (D131): the starter really goes to the backpack (rows 9..35), never to a free hotbar slot
                    // such as 5, which the new-player tips call the potion slot
                    boolean[] used = new boolean[36];
                    for (int i = 0; i < 36; i++) { ItemStack x = inv.getItem(i); used[i] = x != null && x.getType() != org.bukkit.Material.AIR; }
                    int to = at >= 9 ? at : EmberRunRules.starterTarget(used, at); // landed in the backpack already: plain swap
                    ItemStack starter = inv.getItem(slot), fresh = inv.getItem(at);
                    inv.setItem(slot, fresh);
                    if (to == at) inv.setItem(at, starter);
                    else { inv.setItem(at, null); inv.setItem(to, starter); }
                    loadouts.refresh(p);
                    placed = "已放到快捷栏第 " + (slot + 1) + " 格，起步刃" + (to >= 9 ? "移进背包" : "换到快捷栏第 " + (to + 1) + " 格");
                }
            }
        }
        // recheck #1 (D130): no early return — the D120 upgrade check also runs after the starter swap, so a new T2 blade
        // replaces the T1 in hand even while the T0 starter was still on the hotbar
        String up = offerUpgrade(p, d); // D120 (F-review #1)
        if (placed != null && up != null && up.startsWith("（比 ")) return d.shortLabel() + up; // auto-equipped: the hand slot is what matters
        if (placed != null) return d.shortLabel() + "（" + placed + "）" + (up == null ? "" : up);
        return up == null ? d.shortLabel() : d.shortLabel() + up;
    }

    /** D120: better pieces that need the player's click (sent after the 结算到账 line) */
    private final Map<UUID, List<String[]>> upgradeAsks = new java.util.concurrent.ConcurrentHashMap<UUID, List<String[]>>();

    /** the active piece of a slot: main-hand blade (or the last one, if still carried) / selected charm */
    EmberItemData activePiece(Player p, String slot) {
        EmberLoadout cur = loadouts.refresh(p);
        if ("charm".equals(slot)) return cur.charm;
        if (EmberItemData.isArmorSlot(slot)) return cur.armor(EmberItemData.armorIndex(slot)); // D318 (null = empty / 2-slot)
        if (cur.blade != null) return cur.blade;
        String last = loadouts.state(p.getUniqueId()).mainhandUid;
        if (last == null) return null;
        for (ItemStack x : p.getInventory().getContents()) {
            if (x == null || !loadouts.items().hasData(x)) continue;
            EmberItems.Read r = loadouts.items().read(x);
            if (r != null && r.ok() && r.data != null && last.equals(r.data.uid)) return r.data;
        }
        return null;
    }

    private static int slotOfUid(EmberItems items, org.bukkit.inventory.PlayerInventory inv, String uid) {
        for (int i = 0; i < 36; i++) {
            ItemStack x = inv.getItem(i);
            if (x == null || !items.hasData(x)) continue;
            EmberItems.Read r = items.read(x);
            if (r != null && r.data != null && uid.equals(r.data.uid)) return i;
        }
        return -1;
    }

    /** D120: make c the active piece of its slot; returns where it went, or null when it could not be placed */
    String equipPiece(Player p, EmberItemData c, EmberItemData active) {
        if (c.isCharm()) { loadouts.selectCharmUid(p, c.uid); return "已选定为生效护符"; }
        org.bukkit.inventory.PlayerInventory inv = p.getInventory();
        int ci = slotOfUid(loadouts.items(), inv, c.uid);
        if (ci < 0) return null;
        int ai = active == null ? -1 : slotOfUid(loadouts.items(), inv, active.uid);
        int to = ai >= 0 && ai < 9 ? ai : -1;
        if (to < 0) for (int i = 0; i < 9 && to < 0; i++) { ItemStack x = inv.getItem(i); if (x == null || x.getType() == org.bukkit.Material.AIR) to = i; }
        if (to < 0) to = inv.getHeldItemSlot();
        if (ci != to) {
            ItemStack old = inv.getItem(to);
            inv.setItem(to, inv.getItem(ci));
            inv.setItem(ci, old);
        }
        loadouts.refresh(p);
        return "已放到快捷栏第 " + (to + 1) + " 格" + (ci == to ? "" : "，原来那格的东西移到" + (ci < 9 ? "快捷栏第 " + (ci + 1) + " 格" : "背包"));
    }

    /**
     * D120 (F-review #1): the cheapest real way to the next awakening, per piece that lacks something: a better piece
     * already in the bag (free, [换上]), an 8-mark exchange, the in-place upgrade, or for enhance the free swap from an
     * enhanced old piece. One line per piece; {@code buttons} (may be null) collects [label, command, hover, colour].
     */
    List<String> breakthroughRoutes(Player p, List<String[]> buttons) {
        List<String> out = new ArrayList<String>();
        EmberLoadout l = loadouts.refresh(p);
        if (l.blade == null) { out.add("先把一把余烬刃拿在主手"); return out; }
        PlayerData d = data(p.getUniqueId());
        String fam = !"none".equals(l.blade.family) ? l.blade.family : l.charm != null ? l.charm.family : "none";
        int next = l.awakening + 1;
        if (next > 3) { out.add("已达觉醒 III：之后是追极品和深渊层数"); return out; }
        int needT = Math.max(1, next == 1 ? 1 : next), needE = next == 2 ? 6 : next == 3 ? 9 : 0;
        List<EmberItemData> bag = new ArrayList<EmberItemData>();
        for (ItemStack x : p.getInventory().getContents()) {
            if (x == null || !loadouts.items().hasData(x)) continue;
            EmberItems.Read r = loadouts.items().read(x);
            if (r != null && r.ok() && r.data != null) bag.add(r.data);
        }
        EmberForgeService forge = plugin.getEmberForge();
        for (String slot : new String[]{"blade", "charm"}) {
            EmberItemData x = "blade".equals(slot) ? l.blade : l.charm;
            String nm = EmberItemData.slotName(slot);
            boolean famOk = x != null && !"none".equals(fam) && fam.equals(x.family);
            if (x != null && famOk && x.tier >= needT) {
                // recheck #2 (D132): a better piece of the same family already in the bag → one-click [换上]
                EmberItemData better = EmberRunRules.bestCandidate(EmberMode.tables(), x, bag, null, fam, l.level);
                if (better != x) {
                    out.add(nm + "：§a背包里有更好的 " + better.shortLabel() + "§7 → 换上（免费）" + (x.enhance > better.enhance ? "，+" + x.enhance + " 可免费互换过去" : ""));
                    if (buttons != null) buttons.add(new String[]{"[换上" + nm + "]", "/corerpg p1 equip " + better.uid, "换上 " + ChatColor.stripColor(better.shortLabel()), "GREEN"});
                    if (x.enhance >= needE || better.enhance >= needE) continue;
                    x = better; // the enhance route below is about the better piece
                }
                if (x.enhance >= needE) continue;
                EmberItemData donor = null;
                for (EmberItemData b : bag) if (b.slot.equals(slot) && !b.uid.equals(x.uid) && b.enhance >= needE && (donor == null || b.enhance > donor.enhance)) donor = b;
                if (donor != null) {
                    out.add(nm + " +" + x.enhance + "→+" + needE + "：§a工坊免费互换§7，把 " + donor.shortLabel() + " §7的 +" + donor.enhance + " 挪过来");
                    if (buttons != null) buttons.add(new String[]{"[互换" + nm + "强化]", "/corerpg p1 equip " + x.uid + " swap", "免费把 +" + donor.enhance + " 挪到正在用的" + nm, "AQUA"});
                } else out.add(nm + " 强化 +" + x.enhance + "→+" + needE + "（工坊；+1~+3 必成，之后按次数保底）");
                continue;
            }
            // needs another piece: free from the bag, else marks, else upgrade
            EmberItemData best = null;
            for (EmberItemData b : bag) {
                if (!b.slot.equals(slot) || (x != null && b.uid.equals(x.uid)) || b.tier < needT || ("none".equals(fam) ? b.tier < 1 : !fam.equals(b.family))) continue;
                if (best == null || EmberRunRules.pieceValue(EmberMode.tables(), b, b.enhance, l.level) > EmberRunRules.pieceValue(EmberMode.tables(), best, best.enhance, l.level)) best = b;
            }
            String famName = "none".equals(fam) ? "" : EmberItemData.familyName(fam);
            String want = famName.isEmpty() ? nm + " 要 T" + needT + "（任一族，两件同族才成套）：" : nm + " 要 " + famName + " T" + needT + "：";
            if (best != null) {
                out.add(want + "§a背包里就有 " + best.shortLabel() + "§7 → 换上（免费）" + (x != null && x.enhance > best.enhance ? "，+" + x.enhance + " 可免费互换过去" : ""));
                if (buttons != null) {
                    buttons.add(new String[]{"[换上" + nm + "]", "/corerpg p1 equip " + best.uid, best.shortLabel(), "GREEN"});
                    if (x != null && x.enhance > best.enhance)
                        buttons.add(new String[]{"[换上并互换强化]", "/corerpg p1 equip " + best.uid + " swap", "换上并把 +" + x.enhance + " 免费挪过去（回城后）", "AQUA"});
                }
                continue;
            }
            int t = needT;
            boolean open = t == 1 || progressFlag(d, EmberRunRules.directedForgeFlag(t));
            int have = marks(d, t);
            if (open && have >= EmberRunRules.MARKS_PER_EXCHANGE && !"none".equals(fam)) {
                out.add(want + "§a用 8 枚 T" + t + " 印记兑换§7（有 " + have + " 枚）" + (x != null && x.enhance > 0 ? "，再免费互换 +" + x.enhance : ""));
                if (buttons != null) buttons.add(new String[]{"[兑换 T" + t + " " + famName + nm + "]", "/corerpg p1 marks exchange " + fam + " " + slot + " " + t, "先预览，再点确认", "AQUA"});
                continue;
            }
            String up = null;
            if (x != null && famOk && x.tier == t - 1 && EmberUpgradeRules.upgradeCost(x.tier) != null) {
                String flag = EmberUpgradeRules.upgradeFlag(x.tier);
                EmberUpgradeRules.Cost c = EmberUpgradeRules.upgradeCost(x.tier);
                List<String> lack = forge == null ? java.util.Collections.<String>emptyList() : forge.lackingFor(p, c);
                if (flag != null && !progressFlag(d, flag)) up = "升阶要先首通 " + flag.toUpperCase(Locale.ROOT);
                else if (lack.isEmpty()) {
                    out.add(want + "§a升阶 T" + x.tier + "→T" + t + "§7（" + c.label() + "，保留成色 / 精工 / 强化；手持后进工坊）");
                    continue;
                } else up = "升阶 " + c.label() + "，还缺 " + String.join("、", lack).replaceAll("§.", "");
            }
            out.add(want + (open ? "T" + t + " 印记 " + have + "/8" : "T" + t + " 兑换要先首通 " + EmberRunRules.directedForgeFlag(t).toUpperCase(Locale.ROOT))
                    + (up == null ? "" : " · " + up) + "（每局首领结算给 1 枚同阶印记）");
        }
        if (out.isEmpty()) out.add("两件都够了：" + l.nextAwakeningHint());
        return out;
    }

    /** D120: /corerpg p1 route — the breakthrough routes with buttons */
    public boolean routeCommand(Player p) {
        List<String[]> btn = new ArrayList<String[]>();
        for (String line : breakthroughRoutes(p, btn)) p.sendMessage(P + "§b下一次突破 §7" + line);
        if (!btn.isEmpty()) town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, btn.toArray(new String[0][]));
        return true;
    }

    /** D120 (extends D85): a strictly better piece becomes active by itself when nothing is invested in the old one */
    private String offerUpgrade(Player p, EmberItemData c) {
        if (c.tier < 1) return null;
        EmberItemData a = activePiece(p, c.slot);
        if (a == null) return null;
        EmberLoadout cur = loadouts.get(p);
        int lv = cur == null ? 10 : cur.level;
        String set = cur == null ? "none" : cur.activeSet;
        // recheck #2 (D132): a better piece of that slot already in the bag beats the new one (same family while a set is on)
        EmberItemData fresh = c;
        c = EmberRunRules.bestCandidate(EmberMode.tables(), fresh, bagPieces(p), a, "none".equals(set) ? null : set, lv);
        String from = c == fresh ? "" : "背包里的 " + c.shortLabel() + " 比新拿到的更好，";
        int v = EmberRunRules.upgradeVerdict(EmberMode.tables(), c, a, set, lv);
        v = EmberEquipPath.adjust(v, dataOf(p.getUniqueId())); // D517 equip path
        if (v == EmberRunRules.UP_NONE) return null;
        if (v == EmberRunRules.UP_AUTO) {
            String where = equipPiece(p, c, a);
            if (where == null) return null;
            log().info("[P1 equip] auto " + p.getName() + " " + c.uid.substring(0, 8) + " replaces " + a.uid.substring(0, 8) + (c == fresh ? "" : " (bag piece)"));
            return "（" + from + "比 " + a.shortLabel() + " 强，已自动换上" + (c == fresh ? "" : "那件") + "：" + where + "；原来那件还在背包）";
        }
        String why = v == EmberRunRules.UP_ASK_SWAP ? "原来那件强化到 +" + a.enhance + "，可以免费互换过来" : "会拆掉现在的 " + EmberItemData.familyName(cur.activeSet) + " 套装";
        List<String[]> btn = new ArrayList<String[]>();
        btn.add(new String[]{"[换上]", "/corerpg p1 equip " + c.uid, "只换上，不动强化", "GREEN"});
        if (v == EmberRunRules.UP_ASK_SWAP)
            btn.add(new String[]{"[免费互换强化]", "/corerpg p1 equip " + c.uid + " swap", "换上并把 +" + a.enhance + " 免费挪到新件（回城后操作）", "AQUA"});
        btn.add(0, new String[]{"§e更好的" + EmberItemData.slotName(c.slot) + "：" + c.shortLabel() + (c == fresh ? "" : "（背包里那件）") + " §7（" + why + "）", null, null, null});
        upgradeAsks.computeIfAbsent(p.getUniqueId(), k -> new ArrayList<String[]>()).addAll(btn);
        upgradeAsks.get(p.getUniqueId()).add(new String[]{null, null, null, null}); // group end
        return "（比现在用的强，见下面的 [换上]）";
    }

    /** every valid P1 piece in the inventory */
    private List<EmberItemData> bagPieces(Player p) {
        List<EmberItemData> bag = new ArrayList<EmberItemData>();
        for (ItemStack x : p.getInventory().getContents()) {
            if (x == null || !loadouts.items().hasData(x)) continue;
            EmberItems.Read r = loadouts.items().read(x);
            if (r != null && r.ok() && r.data != null) bag.add(r.data);
        }
        return bag;
    }

    private void sendUpgradeAsks(Player p) {
        List<String[]> l = upgradeAsks.remove(p.getUniqueId());
        if (l == null) return;
        String head = null;
        List<String[]> btn = new ArrayList<String[]>();
        for (String[] b : l) {
            if (b[0] == null) { // group end
                if (head != null && !btn.isEmpty()) town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P + head + " ", btn.toArray(new String[0][]));
                head = null; btn.clear();
            } else if (b[1] == null) head = b[0];
            else btn.add(b);
        }
    }

    /** hotbar slot of a T0 starter blade (-1 = none): only that one gets swapped for the first real blade */
    private int starterBladeSlot(Player p) {
        for (int i = 0; i < 9; i++) {
            ItemStack x = p.getInventory().getItem(i);
            if (x == null || !loadouts.items().hasData(x)) continue;
            EmberItems.Read r = loadouts.items().read(x);
            if (r != null && r.ok() && r.data != null && r.data.isBlade() && r.data.tier == 0) return i;
        }
        return -1;
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
                    EmberRunRules.Row f = l.get(s.runId, "cost_coin"); // P2-2 abyss fee: same rule as the stamina
                    Integer fee = s.fee.get(u);
                    if (f != null && !EmberRunRules.ST_RELEASED.equals(f.status)) {
                        l.mark(s.runId, "cost_coin", EmberRunRules.ST_RELEASED, System.currentTimeMillis());
                        ch.add(f);
                        boolean[] c2 = new boolean[1];
                        boolean markPaid = f.result != null && f.result.startsWith("mark:"); // F-review #5
                        EmberRunRules.Row fr = l.record(s.runId, "refund_coin", markPaid ? f.result : "coin:" + (fee == null ? 0 : fee),
                                EmberRunRules.ST_PENDING, System.currentTimeMillis(), c2);
                        if (c2[0]) ch.add(fr);
                    }
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
            EmberRunMaps.MapDef sm = s == null ? null : maps.byKey(s.mapKey); // D174 stage 2b: entries may share one DP dungeon
            if (s != null && sm != null && sm.dungeon.equalsIgnoreCase(m.dungeon) && (s.world == null || s.world.equals(to.getName()))) {
                attach(s, to);
                if (!EmberRunSession.PREPARE.equals(s.state)) commit(s, p.getUniqueId());
                spreadLater(s, sm, p);
                if (s.died.contains(p.getUniqueId())) { // §20.5: a fallen member who gets back in (respawn elsewhere, tp) keeps watching
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (p.isOnline() && p.getWorld() == to) {
                            if (raidRun(s) && s.open()) { watchTeammate(p, s, true); return; } // D106
                            p.setGameMode(GameMode.SPECTATOR);
                            p.sendMessage(P + "你本局已倒下：观战等待队友（仍保留本次结算资格）。");
                        }
                    }, 3L);
                }
            } else {
                p.sendMessage(P + ChatColor.RED + "没有找到你的主线本入场记录：本实例不会生成怪物，也不会结算。请 /dp leave 后从 冒险 菜单进入。");
            }
        }
        if (maps.byWorld(from.getName()) != null) {
            EmberRunDirector d = byWorld.get(from.getName());
            if (d != null && d.s.participants.contains(p.getUniqueId()) && d.s.open()) {
                if (!d.s.died.contains(p.getUniqueId())) d.s.left.add(p.getUniqueId());
                else if (d.def.raid) { d.s.died.remove(p.getUniqueId()); d.s.left.add(p.getUniqueId()); } // D106: a fallen raider who gives up has left
                store.save(d.s);
                checkWipe(d.s);
            }
            if (p.getGameMode() == GameMode.SPECTATOR) p.setGameMode(GameMode.SURVIVAL);
        }
        if (m == null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!p.isOnline() || blocksLegacy(p.getWorld())) return;
                EmberClaimPath.maybeAmbientDeliver(p, EmberRunService.this); // D515 claim path
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

    /** §20.5: when a committed participant dropped out of an open run (reconnect window 120 s) */
    private final Map<UUID, Long> quitAt = new java.util.concurrent.ConcurrentHashMap<UUID, Long>();
    static final long RECONNECT_MS = 120_000L;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        final Long q = quitAt.remove(p.getUniqueId());
        if (maps.byWorld(p.getWorld().getName()) != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!p.isOnline()) return;
                if (orphanedIn(p)) {
                    p.sendMessage(P + ChatColor.YELLOW + "你所在的主线本已结束或已取消（未开战的体力会退还）：正在送你离开实例……");
                    p.performCommand("dp leave");
                    return;
                }
                reconnect(p, q == null ? -1 : System.currentTimeMillis() - q);
            }, 25L);
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline()) return;
            EmberCodexPath.maybeAfterProgress(p, EmberRunService.this); // D522 hub-ok (not gated by legacy)
            if (blocksLegacy(p.getWorld())) return;
            EmberClaimPath.maybeAmbientDeliver(p, EmberRunService.this); // D515 claim path
            starterKit(p);
        }, 60L);
    }

    /** §20.5: within 120 s back to the last cleared room's safe point (boss up → RB entry side); later → out of the run. */
    void reconnect(Player p, long gapMs) {
        EmberRunDirector d = byWorld.get(p.getWorld().getName());
        if (d == null || d.finished()) return;
        if (gapMs > RECONNECT_MS) {
            p.sendMessage(P + ChatColor.YELLOW + "断线超过 120 秒，不能回到本局：正在送你离开实例（按离开副本处理）……");
            p.performCommand("dp leave");
            return;
        }
        org.bukkit.Location to = d.safePoint();
        if (to == null) return;
        p.teleport(to);
        p.setFallDistance(0f);
        p.sendMessage(P + "断线重连：已回到最近已清房的安全点" + (gapMs >= 0 ? "（离线 " + gapMs / 1000 + " 秒）" : "") + "，房间进度与生命保持不变。");
        log().info("[P1 run] reconnect " + p.getName() + " gap=" + gapMs + "ms → " + to.getBlockX() + "," + to.getBlockY() + "," + to.getBlockZ());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        UUID u = e.getPlayer().getUniqueId();
        if (openSessionOf(u) != null && maps.byWorld(e.getPlayer().getWorld().getName()) != null) quitAt.put(u, System.currentTimeMillis());
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
        double atk = t.atk * d.s.dmgFactor; // P2-5 raid party scaling (1.0 for every other run)
        e.setDamage(atk);
        EmberDamageTrace.note(e, before, "G04 主线本怪物伤害固定 atk=" + EmberDamageTrace.fmt(atk));
        markActed(d.s, p);
    }

    /**
     * D191 无伤 (unscathed): a run-mob hit (melee, projectile or skill — every path ends in an EntityDamageByEntityEvent
     * whose damager is the mob or its projectile) that really landed on a committed member. MONITOR + ignoreCancelled:
     * cancelled / zero hits (interval guard, stunned boss, caster melee) never count.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRunHitTaken(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getFinalDamage() <= 0) return;
        Entity src = e.getDamager();
        if (src instanceof Projectile && ((Projectile) src).getShooter() instanceof Entity) src = (Entity) ((Projectile) src).getShooter();
        EmberRunDirector d = byEntity.get(src.getUniqueId());
        if (d == null) return;
        Player p = (Player) e.getEntity();
        if (!d.s.committed.contains(p.getUniqueId())) return;
        d.noteHitTaken(p);
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
        if (src instanceof Player) d.noteBossHurt(e.getEntity(), e.getFinalDamage()); // D193 破招 (player damage only)
        EmberRunDirector.Tracked t = d.mobs.get(e.getEntity().getUniqueId());
        if (t != null && "regen".equals(t.affix)) d.noteRegenHurt(t, e.getFinalDamage());
    }

    private void markActed(EmberRunSession s, Player p) {
        if (s.committed.contains(p.getUniqueId()) && s.acted.add(p.getUniqueId())) store.save(s);
    }

    void skillHit(EmberRunSession s, Player p, LivingEntity src, double dmg) { skillHit(s, p, src, dmg, "mob"); }

    /** D141: class of the skill hit being dealt right now (tele = boss telegraph, share = 烬核, mob = caster line / fire circle) */
    private String skillKind;

    void skillHit(EmberRunSession s, Player p, LivingEntity src, double dmg, String kind) {
        skillDepth++;
        String prevKind = skillKind;
        skillKind = kind;
        try {
            p.setNoDamageTicks(0);
            kbGuard.mark(p.getUniqueId(), System.currentTimeMillis()); // B2.167: only the P1 push (≤ kb) moves the player
            p.damage(dmg * s.dmgFactor, src); // P2-5 raid party scaling (1.0 for every other run)
        } finally {
            skillDepth--;
            skillKind = prevKind;
        }
    }

    /**
     * D141 growth: class of a run mob — boss / affix (D138 affixed elite) / split (its split adds) / mob; null when the
     * entity is not a mob of a live P1 run.
     */
    public String mobClass(Entity e) {
        if (e == null) return null;
        EmberRunDirector d = byEntity.get(e.getUniqueId());
        if (d == null) return null;
        EmberRunDirector.Tracked t = d.mobs.get(e.getUniqueId());
        if (t == null) return d.boss != null && d.boss.le.getUniqueId().equals(e.getUniqueId()) ? "boss" : "mob";
        if (t.boss()) return "boss";
        if (t.splitAdd) return "split";
        if (t.affix != null) return "affix";
        return "mob";
    }

    /** D164 破甲: elite type (blazing / split / shield) of a run mob; "split" for split adds; null otherwise */
    public String mobAffix(Entity e) {
        if (e == null) return null;
        EmberRunDirector d = byEntity.get(e.getUniqueId());
        if (d == null) return null;
        EmberRunDirector.Tracked t = d.mobs.get(e.getUniqueId());
        if (t == null || t.boss()) return null;
        return t.splitAdd ? "split" : t.affix;
    }

    /** D141 growth: class of the hit a player is taking from {@code src} right now (null = not a run hit). */
    public String hitClass(Entity src) {
        if (skillDepth > 0 && skillKind != null) {
            if ("mob".equals(skillKind)) { String c = mobClass(src); return "affix".equals(c) || "split".equals(c) ? "affix" : "mob"; }
            return skillKind;
        }
        String c = mobClass(src);
        if (c == null) return null;
        return "split".equals(c) ? "affix" : c;
    }

    /** D142 深渊行者: this player's segment fee — delegated to {@link EmberAbyssService} (D231). */
    int feeFor(Player p, int fee) { return abyss.feeFor(p, fee); }

    /** D188 撞墙破绽: {@code src} is a run boss in its wall-crash stun (its plain melee does nothing) */
    public boolean bossStunned(Entity src) {
        if (src == null || skillDepth > 0) return false;
        EmberRunDirector d = byWorld.get(src.getWorld().getName());
        return d != null && d.bossStunned(src);
    }

    /** D142: the player stands in an abyss segment right now */
    public boolean inAbyss(Player p) {
        EmberRunDirector d = p == null ? null : byWorld.get(p.getWorld().getName());
        return d != null && d.s.abyss > 0;
    }

    public boolean isRunWorld(World w) { return w != null && byWorld.containsKey(w.getName()); }

    /** D211 烬突: true when {@code e} is the live boss Tracked of its run world (bosses take ×0.5). */
    public boolean isRunBoss(Entity e) {
        if (e == null || e.getWorld() == null) return false;
        EmberRunDirector d = byWorld.get(e.getWorld().getName());
        if (d == null) return false;
        EmberRunDirector.Tracked t = d.mobs.get(e.getUniqueId());
        return t != null && t.boss();
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onMobDeath(EntityDeathEvent e) {
        EmberRunDirector d = byEntity.remove(e.getEntity().getUniqueId());
        if (d == null) return;
        e.getDrops().clear(); // E02: no vanilla / MM drops from run mobs
        e.setDroppedExp(0);
        EmberRunDirector.Tracked t = d.mobs.get(e.getEntity().getUniqueId());
        if (t == null) return;
        if (festival != null) {
            try { festival.onRunMobDeath(d, t.role, e.getEntity()); } // D139 烟火迸发 + event drops
            catch (RuntimeException ex) { log().log(Level.WARNING, "[P1 fest] mob death hook", ex); }
        }
        boolean byPlayer = e.getEntity().getKiller() != null && d.s.committed.contains(e.getEntity().getKiller().getUniqueId());
        if (!byPlayer && t.boss()) byPlayer = !d.s.acted.isEmpty(); // DoT / set-event final blow after real participation
        if (d.onDeath(t)) onBossKilled(d, e.getEntity(), byPlayer);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        EmberRunDirector d = byWorld.get(e.getPlayer().getWorld().getName());
        if (d == null) return;
        Block b = e.getClickedBlock();
        // D171: left-click smash crystals (BlockBreak is often cancelled in dungeon worlds for bots/creative)
        if ((e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                && d.s.committed.contains(e.getPlayer().getUniqueId())
                && d.breakCrystal(b, e.getPlayer())) {
            b.setType(org.bukkit.Material.AIR);
            e.setCancelled(true);
            markActed(d.s, e.getPlayer());
            return;
        }
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (d.chest != null && b.getLocation().equals(d.chest)) {
            e.setCancelled(true);
            if (d.s.committed.contains(e.getPlayer().getUniqueId())) {
                markActed(d.s, e.getPlayer());
                d.clickChest(b);
            }
        }
    }

    /**
     * D171: smash variety crystals. Dungeon protection often cancels BlockBreak after HIGH; run MONITOR
     * ignoreCancelled=false, force AIR, and count once. Never leave a lantern behind.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onCrystalBreak(org.bukkit.event.block.BlockBreakEvent e) {
        EmberRunDirector d = byWorld.get(e.getPlayer().getWorld().getName());
        if (d == null) return;
        if (!d.s.committed.contains(e.getPlayer().getUniqueId())) return;
        if (d.breakCrystal(e.getBlock(), e.getPlayer())) {
            e.getBlock().setType(org.bukkit.Material.AIR);
            markActed(d.s, e.getPlayer());
        }
    }



    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        EmberRunDirector d = byWorld.get(p.getWorld().getName());
        if (d == null || !d.s.committed.contains(p.getUniqueId()) || !d.s.open()) return;
        if (!d.s.died.add(p.getUniqueId()) && d.def.raid) return; // D106: a watcher killed again (/kill, void) — already counted
        store.save(d.s);
        deathRefund(p, d.s);
        checkWipe(d.s);
        if (d.def.raid && d.s.open() && !livingIn(d.s).isEmpty()) raid.onFall(p, d.s); // D106 (D233 → EmberRaidService)
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
        int tableMax = deathRefundMax();
        PlayerData pd = dataOf(p.getUniqueId());
        int path = EmberRefundPath.get(pd);
        int max = EmberRefundPath.effectiveMax(path, tableMax);
        if (tableMax <= 0) return; // table off → feature off (path cannot raise)
        UUID u = p.getUniqueId();
        Integer used = s.potions.get(u);
        int n = EmberRunRules.deathRefundCount(used == null ? 0 : used, max);
        if (EmberRefundPath.valid(path) && max != tableMax) {
            log().info("[P1 run] death refund path " + EmberRefundPath.key(path) + " " + p.getName()
                    + " max " + tableMax + "→" + max);
        }
        if (n == 0 && raidRun(s)) return; // F-review #7: a raid fall with nothing to refund says nothing and keeps today's refund
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
        final EmberRunSession rs = d.s;
        if (d.def.raid) { // D106: respawn next to a living teammate, then watch them until the next revive point
            Player a = nearestLiving(rs, p);
            if (a != null) e.setRespawnLocation(a.getLocation());
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (p.isOnline() && blocksLegacy(p.getWorld())) {
                if (raidRun(rs) && rs.open() && rs.died.contains(p.getUniqueId())) { watchTeammate(p, rs, false); return; } // onDeath tells
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
        p.sendMessage(P + "§e起步装备：T0 刃 + T0 护符（绑定）。刃拿在手上（副本里按 F 放烬斩），护符放在背包里就生效。");
        quietDeliver = true; // D99: the starter kit is not a "settlement"
        try { deliver(p); } finally { quietDeliver = false; }
        EmberSupplyService sup = plugin.getEmberSupplies();
        if (sup != null) sup.giveStarter(p); // B2.169 / D28: §3.1 基础补给, once (guarded by C_STARTER above)
        town.sunshine.corerpg.FlexSkillService fx = plugin.getFlexSkillService();
        if (fx != null && fx.autoEquipStarter(p)) // D91
            p.sendMessage(P + "§e踏步已装配：§f潜行 + Q §7向前短位移 5 格（冷却 14 秒），用来躲首领的地面预警。");
    }

    // ------------------------------------------------------------------ commands

    public static final java.util.Set<String> OPS = new java.util.HashSet<String>(java.util.Arrays.asList(
            "target", "marks", "forgeroll", "随机锻造", "firstclear", "claim", "run", "runs", "enter", "abyss", "recruit", "watch", "season", "goals", "equip", "route", "fest", "国庆", "rush", "pledge", "modes"));

    public boolean cmd(CommandSender s, String sub, String[] args) {
        switch (sub) {
            case "fest":
            case "国庆":
                if (festival == null) { s.sendMessage(P + "活动未加载"); return true; }
                return festival.command(s, args); // D139
            case "target": return cmdTarget(s, args);
            case "marks": return cmdMarks(s, args);
            case "forgeroll":
            case "随机锻造": return cmdForgeRoll(s, args);
            case "firstclear": return cmdFirstClear(s, args);
            case "claim":
                if (!(s instanceof Player)) return true;
                if (blocksLegacy(((Player) s).getWorld())) { s.sendMessage(P + "出本后再领取。"); return true; }
                if (deliver((Player) s) == 0 && store.ledger(((Player) s).getUniqueId()).open().isEmpty()) // B2.138
                    s.sendMessage(P + "没有可领取的暂存奖励。");
                return true;
            case "enter":
                if (!(s instanceof Player) || args.length < 3) { s.sendMessage(P + "/corerpg p1 enter <q01..q07|sx01..sx19> [challenge]"); return true; }
                if (args.length >= 4 && "force".equalsIgnoreCase(args[args.length - 1])) entry.markForced(((Player) s).getUniqueId()); // D96 (D235 → EmberEntryService)
                return tryEnter((Player) s, args[2].toLowerCase(Locale.ROOT), args.length >= 4 && isChallengeWord(args[3]));
            case "abyss": return cmdAbyss(s, args);
            case "recruit": return recruit.cmd(s, args); // D104 / D234 → EmberRecruitService
            case "watch": return cmdWatch(s); // D106
            case "season": // D116
                if (!(s instanceof Player) || season == null) return true;
                return season.seasonCommand((Player) s, args);
            case "equip": return cmdEquip(s, args); // D120
            case "route": return s instanceof Player ? routeCommand((Player) s) : true; // D120
            case "goals": // D117
                if (!(s instanceof Player) || season == null) return true;
                return season.goalsCommand((Player) s);
            case "rush": return cmdRush(s, args); // D144
            case "pledge": return cmdPledge(s, args); // D174 stage 2b → EmberPledgeService (D232)
            case "modes": // D174 stage 2b: the 进阶模式 page (首领残响 / 连战·前哨 / 自选誓约)
                if (s instanceof Player) openMenuFor((Player) s, "ember_p1_modes");
                return true;
            default:
                return cmdRuns(s, args);
        }
    }

    // ------------------------------------------------------------------ D174 stage 2b 自选誓约 (Q06)

    static final String C_PLEDGE = EmberPledgeService.C_PLEDGE; // D232
    static final String PLEDGE_UNLOCK = EmberPledgeService.UNLOCK; // D232

    /** D174 stage 2b: the mode a map's first clear opens (null = none yet live) */
    static String modeUnlock(String mapKey) {
        if (PLEDGE_UNLOCK.equals(mapKey)) return "自选誓约（重打已首通的 Q01–Q07 普通版时自己挂规则，每条 +1 本图徽记）";
        if ("q04".equals(mapKey)) return "首领残响（每周 3 次单独再打 Q01–Q07 的强化首领，每次 2 枚那张图的徽记）";
        if ("q05".equals(mapKey)) return "连战·前哨（Q01→Q02→Q03 三首领连战，每周领一次：每张图 2 枚徽记 + 1 枚 T2 印记）";
        return null;
    }

    void openMenuFor(Player p, String menu) { // package: EmberPledgeService menu
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (p.isOnline()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "trmenu open " + menu + " " + p.getName());
        });
    }

    // ------------------------------------------------------------------ D174 stage 2b 自选誓约 — logic in EmberPledgeService (D232 / ARCH S3-3)

    /** the pledged rules that count: still in the pool (normal: true), config order */
    List<EmberRunMaps.Modifier> pledged(PlayerData d) { return pledge.pledged(d); }

    /** settlement: how many pledged rules the session carried (pool members only — a rule dropped from the pool pays nothing) */
    int pledgeCount(String modifier) { return pledge.count(modifier); }

    /**
     * the session modifier for the leader's pledge, or null: leader's own Q06 first clear, a repeat NORMAL run of a
     * signature map (Q01–Q07 — the pledge pays that map's insignia), every member already first-cleared it.
     * Delegated to {@link EmberPledgeService} (D232).
     */
    String pledgeKey(Player leader, EmberRunMaps.MapDef m, List<Player> party) { return pledge.sessionKey(leader, m, party); }

    /** /corerpg p1 pledge [toggle &lt;id&gt; | off | list] — delegated to {@link EmberPledgeService} (D232). */
    private boolean cmdPledge(CommandSender s, String[] args) { return pledge.cmd(s, args); }

    /** %corerpg_p1_pledge_head|ok|s_&lt;id&gt;|n_&lt;id&gt;|t_&lt;id&gt;% — delegated to {@link EmberPledgeService} (D232). */
    String pledgePapi(PlayerData d, String k) { return pledge.papi(d, k); }

    String pledgeHead(PlayerData d) { return pledge.head(d); }

    /** D144 /corerpg p1 rush [go] — delegated to {@link EmberRushService} (D230). */
    private boolean cmdRush(CommandSender s, String[] args) { return rush.cmd(s, args); }

    /** /corerpg p1 abyss [层] — delegated to {@link EmberAbyssService} (D231). */
    private boolean cmdAbyss(CommandSender s, String[] args) { return abyss.cmd(s, args); }

    /** D101: a player's name for messages (offline players too), never a raw uuid */
    static String nameOf(UUID u) {
        org.bukkit.OfflinePlayer op = Bukkit.getOfflinePlayer(u);
        String n = op == null ? null : op.getName();
        return n == null ? "（未知玩家）" : n;
    }

    String abyssLine(PlayerData d, EmberRunMaps.AbyssTier t) { return abyss.line(d, t); }

    static String qualityLabel(int[] q) {
        return "精良" + q[1] + "% 卓越" + q[2] + "% 极品" + q[3] + "%";
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
        s.sendMessage(P + "/corerpg p1 forgeroll <族> <blade|charm> <阶> — 随机锻造（8印记+4胚料+500币，出不了极品）");
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
            p.sendMessage(P + "当前掉落目标族：" + (t == null ? "未选择（每本按偏向族 50%，另两族各 25%）" : EmberItemData.familyName(t) + "（每本约 60% 掉这族，冒险页每本写了实际概率）") /* D100: real odds (D98 lootOdds) */);
            familyButtons(p, P + "点一个族设为目标（之后入场的局生效）：", "/corerpg p1 target");
            return true;
        }
        String f = args[2].toLowerCase(Locale.ROOT);
        int idx = 0;
        for (int i = 0; i < 3; i++) if (EmberRunRules.FAMILIES[i].equals(f)) idx = i + 1;
        if (idx == 0 && !"none".equals(f)) { p.sendMessage(P + ChatColor.RED + "族：scorch（焚烬）/ burst（烬爆）/ sustain（炽愈）/ none"); return true; }
        d.addPeriodCount(C_TARGET, "all", idx - d.periodCount(C_TARGET, "all"));
        plugin.getDataStore().flushMutation(p.getUniqueId());
        p.sendMessage(P + "§a掉落目标族已设为 " + (idx == 0 ? "无（按本的偏向族掉落）" : EmberItemData.familyName(f)) + " §7· 下次入场生效");
        return true;
    }

    /** D120: /corerpg p1 equip <uid> [swap [confirm]] — the [换上] / [免费互换强化] buttons and the gear-page route */
    private boolean cmdEquip(CommandSender s, String[] args) {
        if (!(s instanceof Player) || args.length < 3) return true;
        Player p = (Player) s;
        String want = args[2];
        boolean swap = args.length >= 4 && "swap".equalsIgnoreCase(args[3]);
        boolean go = args.length >= 5 && "confirm".equalsIgnoreCase(args[4]);
        EmberItemData c = null;
        for (ItemStack x : p.getInventory().getContents()) {
            if (x == null || !loadouts.items().hasData(x)) continue;
            EmberItems.Read r = loadouts.items().read(x);
            if (r != null && r.ok() && r.data != null && r.data.uid != null && r.data.uid.startsWith(want)) { c = r.data; break; }
        }
        if (c == null) { p.sendMessage(P + "§c背包里没有这件（可能已分解或放进了仓库）"); return true; }
        String t = loadouts.trust(p, c);
        if (t != null) { p.sendMessage(P + "§c物品校验未通过：" + t + "（稍后再点）"); return true; }
        EmberItemData a = activePiece(p, c.slot);
        if (swap && a != null && !a.uid.equals(c.uid) && a.enhance > c.enhance) {
            EmberForgeService f = plugin.getEmberForge();
            if (f == null) return true;
            if (!go) {
                String where = equipPiece(p, c, a);
                if (where != null) p.sendMessage(P + "§a已换上 " + c.shortLabel() + "§7（" + where + "）");
            }
            return f.swapUids(p, a.uid, c.uid, go, "/corerpg p1 equip " + c.uid + " swap confirm");
        }
        if (a != null && a.uid.equals(c.uid)) {
            if (!swap) { p.sendMessage(P + "这件已经在用了。"); return true; }
        }
        String where = equipPiece(p, c, a);
        if (where == null) { p.sendMessage(P + "§c放不进快捷栏，手动拿到主手即可"); return true; }
        p.sendMessage(P + "§a已换上 " + c.shortLabel() + "§7（" + where + "）" + (a != null && a.enhance > c.enhance
                ? " · 原来那件 +" + a.enhance + "，工坊「互换」可免费挪过来" : ""));
        return true;
    }

    private boolean cmdMarks(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        PlayerData d = data(p.getUniqueId());
        if (args.length < 3 || !"exchange".equalsIgnoreCase(args[2])) {
            p.sendMessage(P + "锻造印记（账户绑定）：T1 " + marks(d, 1) + " · T2 " + marks(d, 2) + " · T3 " + marks(d, 3));
            p.sendMessage(P + "8 枚同阶印记 → 指定族+部位的同阶标准件（成色标准、精工 0、+0）");
            p.sendMessage(P + "T2 定向锻造：" + (progressFlag(d, "q04") ? "§a已开放" : "§7需本人首通 Q04")
                    + " §7· T3：" + (progressFlag(d, "q07") ? "§a已开放" : "§7需本人首通 Q07"));
            boolean any = false;
            for (int t = 3; t >= 1; t--) { // D88: clickable exchange rows instead of a typed command
                int have = marks(d, t);
                if (t > 1 && !progressFlag(d, EmberRunRules.directedForgeFlag(t))) continue;
                if (have < EmberRunRules.MARKS_PER_EXCHANGE) {
                    if (have > 0) p.sendMessage(P + "§7T" + t + "：还差 " + (EmberRunRules.MARKS_PER_EXCHANGE - have) + " 枚可兑换一件");
                    continue;
                }
                any = true;
                exchangeButtons(p, t);
            }
            if (!any) p.sendMessage(P + "§7每局首领结算给 1 枚同阶印记，攒够 8 枚这里会出现兑换按钮。");
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
        if (args.length < 7 || !"confirm".equalsIgnoreCase(args[6])) { // D88: preview + confirm button (8 marks are not refunded)
            net.md_5.bungee.api.chat.TextComponent msg = new net.md_5.bungee.api.chat.TextComponent(P + "用 " + EmberRunRules.MARKS_PER_EXCHANGE
                    + " 枚 T" + tier + " 印记兑换 T" + tier + " " + EmberItemData.familyName(fam) + EmberItemData.slotName(slot) + "（标准、+0、绑定）？ ");
            net.md_5.bungee.api.chat.TextComponent ok = new net.md_5.bungee.api.chat.TextComponent("§a§l[确认兑换]");
            ok.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND,
                    "/corerpg p1 marks exchange " + fam + " " + slot + " " + tier + " confirm"));
            ok.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
                    new net.md_5.bungee.api.chat.ComponentBuilder("§7扣 " + EmberRunRules.MARKS_PER_EXCHANGE + " 枚，不退").create()));
            msg.addExtra(ok);
            p.spigot().sendMessage(msg);
            exchangeCompare(p, fam, slot, tier); // D104 (midgame #3)
            return true;
        }
        if (freeSlots(p) <= 0) { p.sendMessage(P + ChatColor.RED + "背包已满，空出一格再兑换。"); return true; }
        EmberPay pay = EmberPay.get();
        if (pay != null && pay.durable()) return redeemDurable(p, pay, fam, slot, tier);
        // YAML storage (no MySQL): the old in-memory path
        if (!EmberEconomy.spendMark(d, "C07", tier, EmberEconomy.amount("C07", "marks"))) { p.sendMessage(P + ChatColor.RED + "扣除印记失败"); return true; } // D218
        String run = "mark-" + Long.toString(System.currentTimeMillis(), 36) + "-" + Integer.toString(rnd.nextInt(1296), 36);
        String uid = EmberRunRules.rewardUid(rnd.nextLong(), p.getUniqueId().toString(), run, "mark_item");
        ledgerRow(p.getUniqueId(), run, "mark_item", "item:" + uid + ":" + fam + ":" + slot + ":" + tier + ":0:0", EmberRunRules.ST_PENDING);
        plugin.getDataStore().flushMutation(p.getUniqueId());
        p.sendMessage(P + "§a已用 " + EmberRunRules.MARKS_PER_EXCHANGE + " 枚 T" + tier + " 印记兑换 " + EmberItemData.familyName(fam)
                + EmberItemData.slotName(slot) + "（剩余 " + marks(d, tier) + "）");
        deliver(p);
        return true;
    }

    private final java.util.Set<UUID> redeemBusy = java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<UUID, Boolean>());

    /**
     * D172 (forge review X1 / X4): mark redemption on the durable payment path. Refund hold (marks) → 8 marks taken +
     * paid marker in one save → ONE DB transaction: item row (active) + ledger row (kind mark_redeem) + the piece as a
     * gear delivery + void of the hold. The piece reaches the backpack through EmberDelivery (exactly once by uid).
     * Crash before the commit → marks refunded at join; after it → the piece delivered at join.
     */
    private boolean redeemDurable(final Player p, final EmberPay pay, final String fam, final String slot, final int tier) {
        final UUID id = p.getUniqueId();
        if (!redeemBusy.add(id)) { p.sendMessage(P + "§c上一次兑换还在处理"); return true; }
        final String rid = EmberPayRules.redeemRid(id, System.currentTimeMillis(), rnd.nextInt(46656));
        final String uid = EmberRunRules.rewardUid(rnd.nextLong(), id.toString(), rid, "mark_item");
        final EmberItemData item = new EmberItemData(uid, EmberItemData.templateId(fam, slot, tier), fam, slot, tier, 0, 0, 0, 0, true, "drop",
                EmberItemData.DATA_VERSION, 0, 0, 0, 0, 0, EmberProvenance.forRedeem(rid, System.currentTimeMillis())); // D245: forge / S28
        final EmberPay.Price price = EmberPay.Price.marks(tier, EmberEconomy.amount("C07", "marks")).at("C07"); // D218: REG C07 (golden = MARKS_PER_EXCHANGE)
        final String label = EmberItemData.familyName(fam) + EmberItemData.slotName(slot);
        pay.pay(p, rid, price, "兑换没完成，退回印记", null, () ->
                loadouts.store().commitCreate(rid, "mark_redeem", id, item, price.json(),
                        "印记兑换 T" + tier + " " + label + "（" + EmberRunRules.MARKS_PER_EXCHANGE + " 枚 T" + tier + " 印记）",
                        java.util.Arrays.asList(EmberItemStore.Owed.gear(uid, "印记兑换 T" + tier + " " + label)), res -> {
                            redeemBusy.remove(id);
                            Player q = Bukkit.getPlayer(id);
                            if (res.status == EmberItemStore.TxnStatus.OK) {
                                pay.settled(id, rid);
                                log().info("[P1 run] " + id + " mark redeem " + rid + " T" + tier + " " + fam + " " + slot + " → " + uid);
                                if (q != null) {
                                    PlayerData qd = data(id);
                                    q.sendMessage(P + "§a已用 " + EmberRunRules.MARKS_PER_EXCHANGE + " 枚 T" + tier + " 印记兑换 " + label
                                            + "（剩余 " + (qd == null ? "?" : String.valueOf(marks(qd, tier))) + "）");
                                    EmberGearLib gl = EmberGearLib.get();
                                    if (gl != null) gl.delivery().kick(q);
                                }
                            } else {
                                pay.release(id, rid); // not committed → the 8 marks come back once
                                log().warning("[P1 run] " + id + " mark redeem " + rid + " not committed: " + res.status + " " + res.detail);
                                if (q != null) q.sendMessage(P + ChatColor.RED + "兑换没完成（" + res.status + "），" + EmberRunRules.MARKS_PER_EXCHANGE + " 枚 T" + tier + " 印记会退回");
                            }
                        }),
                err -> { redeemBusy.remove(id); if (p.isOnline()) p.sendMessage(P + ChatColor.RED + "没有兑换：" + err); });
        return true;
    }


    /** D430: /corerpg p1 forgeroll <fam> <blade|charm> [tier] [confirm] — 8 marks + 4 blank + 500 coin → random quality/craft piece */
    private final java.util.Set<UUID> forgeRollBusy = java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<UUID, Boolean>());

    private boolean cmdForgeRoll(CommandSender s, String[] args) {
        if (!(s instanceof Player)) { s.sendMessage(P + "玩家专用"); return true; }
        Player p = (Player) s;
        PlayerData d = data(p.getUniqueId());
        if (d == null) { p.sendMessage(P + "数据还没加载好"); return true; }
        if (args.length < 5) {
            p.sendMessage(P + "用法：/corerpg p1 forgeroll <scorch|burst|sustain> <blade|charm> <1-3> [confirm]");
            p.sendMessage(P + "花费：" + EmberForgeRollRules.MARKS + " 枚印记 + " + EmberForgeRollRules.BLANKS + " 胚料 + " + EmberForgeRollRules.COINS + " 币");
            p.sendMessage(P + EmberForgeRollRules.oddsLine());
            return true;
        }
        String fam = args[2].toLowerCase(Locale.ROOT);
        String slot = args[3].toLowerCase(Locale.ROOT);
        int tier;
        try { tier = Integer.parseInt(args[4]); } catch (NumberFormatException e) { p.sendMessage(P + "阶：1–3"); return true; }
        boolean confirm = args.length >= 6 && "confirm".equalsIgnoreCase(args[args.length - 1]);
        boolean gate = tier <= 1 || progressFlag(d, EmberRunRules.directedForgeFlag(tier));
        String refuse = EmberForgeRollRules.refusal(marks(d, tier), tier, fam, slot, gate);
        if (refuse != null) { p.sendMessage(P + ChatColor.RED + refuse); return true; }
        String label = EmberItemData.familyName(fam) + EmberItemData.slotName(slot);
        if (!confirm) {
            p.sendMessage(P + "§6随机锻造 · T" + tier + " " + label);
            p.sendMessage(P + "花费：§f" + EmberForgeRollRules.MARKS + " 枚 T" + tier + " 印记 + " + EmberForgeRollRules.BLANKS + " 胚料 + " + EmberForgeRollRules.COINS + " 币");
            p.sendMessage(P + EmberForgeRollRules.oddsLine());
            p.sendMessage(P + "§7产物：指定族部位 · 成色/精工随机 · +0 · 词条空 · 出不了极品");
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P,
                    new String[]{"[确认锻造]", "/corerpg p1 forgeroll " + fam + " " + slot + " " + tier + " confirm", "扣印记/胚料/币，随机成色精工", "GREEN"},
                    new String[]{"[取消]", "/corerpg p1 forgeroll", "不扣", "GRAY"});
            return true;
        }
        final EmberPay pay = EmberPay.get();
        if (pay == null) { p.sendMessage(P + "§c支付服务未就绪"); return true; }
        return forgeRollDurable(p, pay, fam, slot, tier);
    }

    private boolean forgeRollDurable(final Player p, final EmberPay pay, final String fam, final String slot, final int tier) {
        final UUID id = p.getUniqueId();
        if (!forgeRollBusy.add(id)) { p.sendMessage(P + "§c上一次随机锻造还在处理"); return true; }
        final String rid = EmberPayRules.forgeRollRid(id, System.currentTimeMillis(), rnd.nextInt(46656));
        final int q = EmberForgeRollRules.rollQuality(new java.util.Random(EmberPayRules.seed(rid.hashCode(), id.toString(), rid)));
        final int craft = EmberForgeRollRules.rollCraft(new java.util.Random(EmberPayRules.seed(rid.hashCode() ^ 0x9e3779b9L, id.toString(), rid + ":c")));
        final String uid = EmberRunRules.rewardUid(rnd.nextLong(), id.toString(), rid, "forge_roll");
        final EmberItemData item = new EmberItemData(uid, EmberItemData.templateId(fam, slot, tier), fam, slot, tier, q, craft, 0, 0, true, "drop",
                EmberItemData.DATA_VERSION, 0, 0, 0, 0, 0, EmberProvenance.forForgeRoll(rid, System.currentTimeMillis()));
        final EmberPay.Price price = EmberPay.Price.of(EmberForgeRollRules.matCost())
                .plus(EmberPay.Price.marks(tier, EmberForgeRollRules.MARKS)).at("C22");
        final String label = EmberItemData.familyName(fam) + EmberItemData.slotName(slot);
        pay.pay(p, rid, price, "随机锻造没完成，退回", null, () ->
                loadouts.store().commitCreate(rid, "forge_roll", id, item, price.json(),
                        "随机锻造 T" + tier + " " + label + " 成色" + q + " 精工" + craft,
                        java.util.Arrays.asList(EmberItemStore.Owed.gear(uid, "随机锻造 T" + tier + " " + label)), res -> {
                            forgeRollBusy.remove(id);
                            Player qq = Bukkit.getPlayer(id);
                            if (res.status == EmberItemStore.TxnStatus.OK) {
                                pay.settled(id, rid);
                                log().info("[P1 run] " + id + " forge_roll " + rid + " T" + tier + " " + fam + " " + slot + " q=" + q + " c=" + craft + " → " + uid);
                                if (qq != null) {
                                    qq.sendMessage(P + "§a随机锻造完成：§fT" + tier + " " + label
                                            + " §7· 成色§f" + EmberItemData.qualityName(q)
                                            + " §7· 精工§f" + (craft * 2) + "%§7 · +0 · 词条空");
                                    EmberGearLib gl = EmberGearLib.get();
                                    if (gl != null) gl.delivery().kick(qq);
                                }
                            } else {
                                pay.release(id, rid);
                                log().warning("[P1 run] " + id + " forge_roll " + rid + " not committed: " + res.status + " " + res.detail);
                                if (qq != null) qq.sendMessage(P + ChatColor.RED + "随机锻造没完成（" + res.status + "），材料会退回");
                            }
                        }),
                err -> { forgeRollBusy.remove(id); if (p.isOnline()) p.sendMessage(P + ChatColor.RED + "没有锻造：" + err); });
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
            boolean fixed = false;
            for (EmberRunRules.Row r : waiting) {
                EmberRunRules.Grant g = EmberRunRules.Grant.decode(r.key, r.result);
                if (g != null && "piece".equals(g.id)) { pieceButtons(p, r.key.substring(3, 6), g.amount); continue; }
                fixed = true;
                p.sendMessage(P + "待选：" + r.key.substring(3, 6).toUpperCase(Locale.ROOT) + " T" + (g == null ? 1 : g.amount) + " 标准"
                        + EmberItemData.slotName(g == null ? "blade" : g.id));
            }
            if (!fixed) return true;
            String t = target(data(p.getUniqueId()));
            familyButtons(p, P + "点一个族领取" + (t == null ? "（会同时设为掉落目标族）" : "（目标族 " + EmberItemData.familyName(t) + "，选同族才能成套）") + "：", "/corerpg p1 firstclear");
            p.sendMessage(P + "§7刃和护符同族才成套；选和另一件不同的族会断套装。");
            return true;
        }
        if (blocksLegacy(p.getWorld())) { p.sendMessage(P + "出本后再领取。"); return true; }
        String fam = args[2].toLowerCase(Locale.ROOT);
        if (!EmberRunRules.validFamily(fam)) { p.sendMessage(P + ChatColor.RED + "族：scorch / burst / sustain"); return true; }
        EmberRunRules.Row pick = waiting.get(0);
        if (args.length == 3) for (EmberRunRules.Row r : waiting) { // a bare family click belongs to a fixed-slot choice (Q01 charm)
            EmberRunRules.Grant rg = EmberRunRules.Grant.decode(r.key, r.result);
            if (rg != null && !"piece".equals(rg.id)) { pick = r; break; }
        }
        if (args.length >= 4) for (EmberRunRules.Row r : waiting) if (r.key.startsWith("fc_" + args[3].toLowerCase(Locale.ROOT) + "_")) pick = r;
        EmberRunRules.Grant g = EmberRunRules.Grant.decode(pick.key, pick.result);
        if (g == null) return true;
        String slot = g.id;
        if ("piece".equals(g.id)) { // E-review #6: the voucher needs a slot too
            slot = args.length >= 5 ? args[4].toLowerCase(Locale.ROOT) : "";
            if (!"blade".equals(slot) && !"charm".equals(slot)) { pieceButtons(p, pick.key.substring(3, 6), g.amount); return true; }
        }
        long seed = EmberRunRules.subSeed(p.getUniqueId().getLeastSignificantBits(), pick.runId);
        EmberRunRules.Grant item = EmberRunRules.choiceItem(seed, p.getUniqueId().toString(), pick.runId, pick.key, fam, slot, g.amount);
        boolean[] c = new boolean[1];
        long now = System.currentTimeMillis();
        EmberRunRules.Row ir = l.record(pick.runId, item.key, item.encode(), EmberRunRules.ST_PENDING, now, c);
        pick.status = EmberRunRules.ST_DELIVERED;
        pick.updated = now;
        List<EmberRunRules.Row> ch = new ArrayList<EmberRunRules.Row>();
        ch.add(pick);
        if (c[0]) ch.add(ir);
        store.saveLedger(p.getUniqueId(), ch);
        PlayerData pd = data(p.getUniqueId());
        if (target(pd) == null) { // D87: the first family a new player picks becomes the drop target (changeable)
            int idx = 0;
            for (int i = 0; i < 3; i++) if (EmberRunRules.FAMILIES[i].equals(fam)) idx = i + 1;
            pd.addPeriodCount(C_TARGET, "all", idx);
            plugin.getDataStore().flushMutation(p.getUniqueId());
            // D98: the Q01 choice is now the charm, so this target also steers the T1 blade the player farms in Q01 next
            p.sendMessage(P + "§a掉落目标族同时设为 " + EmberItemData.familyName(fam) + " §7（之后掉落约六成是这一族，接下来在 Q01 刷到的 T1 刃也大多同族，正好成套；冒险页可改）");
        }
        deliver(p);
        // D98: the Q02 choice is now a blade; when the blade in hand is another family and already enhanced, point at the
        // free enhance-track swap so the player does not keep investing in an off-set blade (p2econ: set alignment −6 pt otherwise)
        EmberLoadoutService ls = plugin.getEmberLoadouts();
        EmberLoadout lo = ls == null ? null : ls.refresh(p);
        if ("blade".equals(slot) && lo != null && lo.blade != null && lo.blade.tier >= 1 && lo.blade.enhance > 0 && !fam.equals(lo.blade.family))
            p.sendMessage(P + "§e提示：§7你手里的刃是 " + EmberItemData.familyName(lo.blade.family) + " +" + lo.blade.enhance
                    + "。想换成新的 " + EmberItemData.familyName(fam) + "刃凑套装，工坊「互换」能把强化等级免费挪过去（主手一把、副手一把）。");
        return true;
    }

    /** D99: the starter kit delivery prints no "结算到账" line (main thread only) */
    private boolean quietDeliver;

    // ------------------------------------------------------------------ E-review #5 / D234: recruit board — EmberRecruitService

    /** board TTL (alias for tests / callers that still read the pre-extract name). */
    static final long RECRUIT_TTL = EmberRecruitService.TTL_MS;

    /** %corerpg_p1_recruits%: up to two board entries for the adventure icons — {@link EmberRecruitService} (D234). */
    public String recruitsLabel() { return recruit.label(); }

    /** chat list with [申请入队] — {@link EmberRecruitService#show} (D234). */
    void showRecruits(Player p, boolean tellEmpty) { recruit.show(p, tellEmpty); }

    /** E-review #5: season apply (D116) + Q07 recruit board flash — season stays here; board → {@link EmberRecruitService} (D234). */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoinRecruits(org.bukkit.event.player.PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        if (season != null) Bukkit.getScheduler().runTaskLater(plugin, () -> { if (p.isOnline()) season.apply(p); }, 60L); // D116
        Bukkit.getScheduler().runTaskLater(plugin, () -> recruit.onJoinShow(p), 100L);
    }

    /** E-review #5: DP says nothing to a leader who refused an application ([拒绝] runs request unaccept <name>) — {@link EmberRecruitService} (D234). */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRequestRefused(org.bukkit.event.player.PlayerCommandPreprocessEvent e) {
        recruit.onRequestRefused(e);
    }


    // D96 forcedReady / D104 warnedT3 / P2-8 forcedModifier live in EmberEntryService (D235)
    // forcedExtra / forcedVariety live in EmberSessionService (D237)

    private boolean cmdRuns(CommandSender s, String[] args) {
        boolean admin = s.hasPermission("corerpg.admin");
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        if (admin && "modifier".equals(op) && args.length >= 4) { // P2-8 test hook: next challenge run (any map) uses this rule
            entry.forceModifier("clear".equalsIgnoreCase(args[3]) ? null : maps.modifier(args[3])); // D235 → EmberEntryService
            s.sendMessage(P + "下一局规则（挑战或精选图普通版，仅一次，测试用）= " + (entry.forcedModifier() == null ? "按周" : entry.forcedModifier().id));
            return true;
        }
        if (admin && "variety".equals(op) && args.length >= 4) { // D138/D171/D181/D189/D196: blazing|…|frost|mortar|molten|venom|jailer|arcane|firechain|timed|crystal|escort|event[:rN]|clear
            session.forceVariety("clear".equalsIgnoreCase(args[3]) ? null : args[3].toLowerCase(Locale.ROOT)); // D237 → EmberSessionService
            s.sendMessage(P + "下一局普通版花样（仅一次，测试用）= " + (session.forcedVariety() == null ? "按种子" : session.forcedVariety()));
            return true;
        }
        if (admin && "extra".equals(op) && args.length >= 4) {
            session.forceExtra("clear".equalsIgnoreCase(args[3]) ? null : EmberRunRules.Extra.parse(args[3])); // D237 → EmberSessionService
            s.sendMessage(P + "下一局额外事件（仅一次，测试用）= " + (session.forcedExtra() == null ? "按种子" : session.forcedExtra().id));
            return true;
        }
        if (admin && "marks".equals(op) && args.length >= 6) { // test grant: runs marks <玩家> <阶> <±n>
            Player t = Bukkit.getPlayerExact(args[3]);
            if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
            int tier, n;
            try { tier = Integer.parseInt(args[4]); n = Integer.parseInt(args[5]); }
            catch (NumberFormatException ex) { s.sendMessage(P + "阶与数量需为整数"); return true; }
            PlayerData d = data(t.getUniqueId());
            d.addPeriodCount(C_MARK + tier, "all", Math.max(n, -marks(d, tier))); // econ-ok: admin test hook
            plugin.getDataStore().flushMutation(t.getUniqueId());
            s.sendMessage(P + t.getName() + " T" + tier + " 锻造印记 = " + marks(d, tier));
            log().info("[P1 run] admin " + s.getName() + " marks " + t.getName() + " T" + tier + " " + n + " → " + marks(d, tier));
            return true;
        }
        if (admin && "season".equals(op) && season != null) { // D116/D117 test hooks
            // runs season preview · runs season award <玩家> <id> · runs season badges <玩家> <n> · runs season goal <玩家> <goal> <n>
            String w = args.length >= 4 ? args[3].toLowerCase(Locale.ROOT) : "preview";
            if ("preview".equals(w)) { season.adminPreview(s); return true; }
            Player t = args.length >= 5 ? Bukkit.getPlayerExact(args[4]) : null;
            if (t == null) { s.sendMessage(P + "/corerpg p1 runs season preview|award|badges|goal <在线玩家> ..."); return true; }
            PlayerData td = data(t.getUniqueId());
            if ("award".equals(w) && args.length >= 6) {
                EmberCosmetics.Cosmetic c = EmberCosmetics.byId(args[5]);
                if (c == null || !c.id.startsWith("season_")) { s.sendMessage(P + "没有这个赛季奖励：" + args[5]); return true; }
                season.adminAward(t, c.id);
            } else if ("badges".equals(w) && args.length >= 6) {
                td.addPeriodCount(EmberSeason.C_BADGE, "all", Integer.parseInt(args[5])); // econ-ok: admin test hook
                flushData(t.getUniqueId());
            } else if ("goal".equals(w) && args.length >= 7) {
                season.addGoal(t.getUniqueId(), td, args[5], Integer.parseInt(args[6]));
                flushData(t.getUniqueId());
            } else { s.sendMessage(P + "参数不对"); return true; }
            s.sendMessage(P + "ok · " + t.getName() + " 余烬徽 " + EmberSeason.badges(td) + " · 周目标 " + season.goalsDone(td) + "/" + season.goalCount());
            log().info("[P1 season] admin " + s.getName() + " " + String.join(" ", args));
            return true;
        }
        if (admin && "weaken".equals(op) && args.length >= 4) { // test hook: runs weaken <玩家> — every live mob of that run → 1 HP
            Player t = Bukkit.getPlayerExact(args[3]);
            EmberRunSession x = t == null ? null : openSessionOf(t.getUniqueId());
            EmberRunDirector d = x == null || x.world == null ? null : byWorld.get(x.world);
            if (d == null) { s.sendMessage(P + "该玩家没有进行中的主线本"); return true; }
            // optional ratio (0 < r < 1): every live mob → r × max HP instead of 1 HP (phase / kill-time tests, e.g. 0.55)
            double ratio = 0;
            if (args.length >= 5) { try { ratio = Double.parseDouble(args[4]); } catch (NumberFormatException e) { ratio = 0; } }
            if (ratio <= 0 || ratio >= 1) ratio = 0;
            int n = 0;
            for (EmberRunDirector.Tracked m : d.mobs.values()) if (!m.le.isDead()) {
                double to = ratio > 0 ? Math.max(1.0, ratio * m.le.getMaxHealth()) : 1.0;
                m.le.setHealth(Math.min(to, m.le.getMaxHealth()));
                n++;
            }
            s.sendMessage(P + x.runId + " 削弱 " + n + " 只" + (ratio > 0 ? "（到 " + Math.round(ratio * 100) + "% 生命）" : "") + "（测试用；击杀仍须由玩家完成，结算照常）");
            log().info("[P1 run] admin " + s.getName() + " weaken " + x.runId + " n=" + n + (ratio > 0 ? " ratio=" + ratio : ""));
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
        if (admin && "abyssbest".equals(op) && args.length >= 5) { // D302 smoke / ops: set p2_abyss_best
            Player t = Bukkit.getPlayerExact(args[3]);
            if (t == null) { s.sendMessage(P + "玩家不在线"); return true; }
            int n;
            try { n = Integer.parseInt(args[4]); } catch (NumberFormatException e) { s.sendMessage(P + "层数须为整数"); return true; }
            n = Math.max(0, Math.min(maps.abyss.size(), n));
            PlayerData d = data(t.getUniqueId());
            int cur = abyss.best(d);
            d.addPeriodCount(EmberAbyssService.C_ABYSS_BEST, "all", n - cur);
            plugin.getDataStore().flushMutation(t.getUniqueId());
            s.sendMessage(P + t.getName() + " p2_abyss_best=" + abyss.best(d) + "（可开 1～" + abyss.maxStart(d) + "）");
            return true;
        }
        if (admin && "firstclear".equals(op) && args.length >= 5) {
            Player t = Bukkit.getPlayerExact(args[3]);
            EmberRunMaps.MapDef m = maps.byKey(args[4]);
            if (t == null || m == null) { s.sendMessage(P + "玩家不在线或地图未知"); return true; }
            PlayerData d = data(t.getUniqueId());
            boolean clear = args.length >= 6 && "clear".equalsIgnoreCase(args[5]);
            boolean skillCue = !clear && !EmberFirstClear.fact(d, m.key); // D459 smoke/admin: announce on rising edge
            EmberFirstClear.setBoth(d, m.key, m.contentVersion, !clear); // D205: fact @all + package @ver
            plugin.getDataStore().flushMutation(t.getUniqueId());
            if (skillCue) EmberSkillUnlock.announce(t, m.key);
            s.sendMessage(P + t.getName() + " " + m.key + "@" + m.contentVersion + " first_clear=" + !clear
                    + (skillCue && EmberSkillUnlock.unlockLine(m.key) != null ? " §a· 技能解锁已宣告" : ""));
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
            s.sendMessage(P + "/corerpg p1 runs list | unlock <玩家> <q02..q05> [clear] | firstclear <玩家> <q01..> [clear] | abyssbest <玩家> <层> | starter <玩家> [reset] | marks <玩家> <阶> <±n> | extra <none|treasure|elite|chest|clear>");
            return true;
        }
        Player p = (Player) s;
        PlayerData d = data(p.getUniqueId());
        for (EmberRunMaps.MapDef m : maps.maps.values()) {
            p.sendMessage(P + "§e" + m.key.toUpperCase(Locale.ROOT) + " " + m.name + " §7" + stateLabel(d, m) + " · " + maps.cost
                    + " 体力 · 掉落 " + m.dropLabel + "（偏向 " + EmberRunMaps.lootLabel(m) + "）· 首通：" + m.firstClearLabel());
        }
        String t = target(d);
        p.sendMessage(P + "挑战版（七图）：" + (challengeOpen(d) ? "§a已开放 §7· 冒险页「挑战版」选图 · 掉落 T3"
                : "§7需本人首通 " + (maps.challenge == null ? "Q07" : maps.challenge.requires.toUpperCase(Locale.ROOT))));
        raid.menuButtons(p, d); // D233 → EmberRaidService
        p.sendMessage(P + "§e每日委托 §7" + bountyLabel(d)); // P2-7
        p.sendMessage(P + "本周精选：§b" + featuredLabel(d) + " §7（前 " + maps.rotationWeeklyCap + " 次精选通关各多 1 枚印记：挑战版给 T3，Q01–Q06 首通后的普通版重打给本图阶）");
        if (!maps.modifiers.isEmpty()) p.sendMessage(P + "本周规则（精选图的挑战版" + (normalRule() ? "和首通后的普通版重打；首通不受影响" : "；普通版不变") + "；奖励不变）：§b" + modifierLabel());
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

    /** P2-1: featured challenge map key of the week containing {@code day} (map order = the runs yml order). */
    public String featured(java.time.LocalDate day) {
        return EmberRunRules.featuredChallenge(new ArrayList<String>(maps.maps.keySet()), day);
    }

    /** P2-1 label, e.g. 「Q03 残誓地窖 · 加成剩 2/3 · 周一 0 点轮换」 */
    public String featuredLabel(PlayerData d) {
        java.time.LocalDate today = java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone());
        EmberRunMaps.MapDef f = maps.byKey(featured(today));
        if (f == null || maps.rotationBonusMarks <= 0) return "无";
        int left = Math.max(0, maps.rotationWeeklyCap - d.periodCount(C_ROTATION, EmberRunRules.rotationWeekKey(today)));
        EmberRunMaps.Modifier mod = maps.modifierFor(today);
        boolean late = progressFlag(d, "q07"); // E-review #7
        boolean normalToo = f.tier < 3 && maps.rotationNormalBonusMarks > 0 && !late; // D108
        return f.key.toUpperCase(Locale.ROOT) + " " + f.name + (mod == null ? "" : " · 规则「" + mod.name + "」")
                + " · 本周还能多拿 " + left + " 次印记（" + (normalToo ? "首通后重打给 T" + f.tier + "，挑战版给 T3，共用 " + maps.rotationWeeklyCap + " 次"
                : late && f.tier < 3 ? "挑战版，T3；重打普通版不占名额" : "挑战版，T3") + "）· 周一 0 点轮换";
    }

    /** D98: the real family odds on this map for this player (EmberRunRules.familyProbability), one short tail */
    String lootOdds(PlayerData d, EmberRunMaps.MapDef m) {
        if (m == null || m.raid || m.lootFamily == null) return "";
        EmberRunRules.LootBias lb = maps.lootBias(m);
        String t = target(d);
        if (t == null) return " §8（没选目标族：偏向族 50%，另两族各 25%）";
        int pt = (int) Math.round(100 * EmberRunRules.familyProbability(t, lb, t));
        if (t.equals(m.lootFamily)) return " §8（目标族 " + EmberItemData.familyName(t) + " 约 " + pt + "%）";
        int pm = (int) Math.round(100 * EmberRunRules.familyProbability(t, lb, m.lootFamily));
        return " §8（目标族 " + EmberItemData.familyName(t) + " 约 " + pt + "%，偏向族约 " + pm + "%）";
    }

    /** D97 hub board: top {@code n} rows of the abyss board or this week's featured board (display only) */
    public java.util.List<EmberLeaderboard.Row> topRows(boolean abyssBoard, int n) {
        if (season != null) { // F-review #2 (D121): one leaderboard set — the season page's week boards
            java.util.List<EmberLeaderboard.Row> out = new ArrayList<EmberLeaderboard.Row>();
            for (EmberSeason.Row r : season.weekTop(abyssBoard ? "abyss" : "featured", n)) out.add(new EmberLeaderboard.Row(r.name, r.value, r.at));
            return out;
        }
        if (top == null) return new ArrayList<EmberLeaderboard.Row>();
        return top.top(abyssBoard, EmberRunRules.rotationWeekKey(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone())), n);
    }

    /** E-review #10: the join greeting follows progress — null = the config lines (new players, before the Q01 first clear) */
    /** F-review #3: 「本周目标 1/4 · 余烬徽 15」 */
    public String goalsShort(PlayerData d) {
        return season == null ? "" : "本周目标 §f" + season.goalsDone(d) + "/" + season.goalCount() + "§7 · 余烬徽 §f" + EmberSeason.badges(d) + "§7";
    }

    /** F-review #3: extra join buttons after graduation (null = the default three) */
    public String[][] joinButtons(PlayerData d) {
        if (d == null || !progressFlag(d, "q07")) return null;
        return new String[][]{{"[主菜单]", "/ember", "冒险 · 装备 · 工坊 · 帮助都在这里", "GOLD"},
                {"[冒险页]", "/ember_p1_adventure", "挑战版 / 深渊 / 团本", "GREEN"},
                {"[赛季 · 周目标]", "/ember_p1_season", "本周目标、排行榜、赛季奖励", "LIGHT_PURPLE"},
                {"[外观商店]", "/corerpg p1 cosmetic", "打开外观商店页（余烬徽 / 币 / 印记都能付，只做展示）", "AQUA"}}; // recheck #5: D121 says the join message opens the shop page
    }

    public List<String> joinLines(PlayerData d) {
        if (d == null || (!progressFlag(d, "q01") && !progressFlag(d, "q07"))) return null;
        List<String> out = new ArrayList<String>();
        if (progressFlag(d, "q07")) {
            out.add("§6§l[余烬服] §e欢迎回来 · 本周精选 §f" + featuredLabel(d));
            out.add("§7" + EmberSeason.abyssMine(abyssBest(d)) + "（可开第 1～" + abyssMaxStart(d) + " 层）"
                    + (season != null && season.goalsOn() ? "§7 · " + goalsShort(d) : ""));
            out.add("§7外观商店：点下面的 [外观商店]（或主菜单「赛季 · 排行 · 周目标」→ 外观商店）；余烬徽、余烬币或多出来的印记都能付，只做展示");
        } else {
            out.add("§6§l[余烬服] §e欢迎回来 · 下一步：§f" + ChatColor.stripColor(nextStep(d)));
            out.add("§7本周精选 §f" + featuredShort() + "§7 · 右键门吏 · 灰钥或点下面的 [冒险页] 进本");
        }
        return out;
    }

    /** D306: weekly featured bonus clears remaining (0 when no rotation). */
    public int featuredLeft(PlayerData d) {
        if (maps == null || maps.rotationBonusMarks <= 0 || maps.rotationWeeklyCap <= 0 || d == null) return 0;
        java.time.LocalDate today = java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone());
        return Math.max(0, maps.rotationWeeklyCap - d.periodCount(C_ROTATION, EmberRunRules.rotationWeekKey(today)));
    }

    /** D97 hub text, e.g. 「Q03 残誓地窖 · 规则「逆行」」 */
    public String featuredShort() {
        java.time.LocalDate today = java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone());
        EmberRunMaps.MapDef f = maps.byKey(featured(today));
        if (f == null) return "无";
        EmberRunMaps.Modifier mod = maps.modifierFor(today);
        return f.key.toUpperCase(Locale.ROOT) + " " + f.name + (mod == null ? "" : " · 规则「" + mod.name + "」");
    }

    /** P2-10 (D84) /corerpg p1 top: both boards, top 10 */
    public boolean topCommand(org.bukkit.command.CommandSender p) {
        if (p instanceof Player && season != null) { // F-review #2 (D121): one leaderboard set — open the season page
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "trmenu open ember_p1_season " + p.getName());
            return true;
        }
        if (top == null) { p.sendMessage(P + "排行榜未加载"); return true; }
        String wk = EmberRunRules.rotationWeekKey(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone()));
        java.util.List<EmberLeaderboard.Row> a = top.top(true, wk, 10), f = top.top(false, wk, 10);
        p.sendMessage(P + "§6排行榜 §7（只做展示）");
        p.sendMessage(P + "§5深渊最高层：" + (a.isEmpty() ? "§7暂无" : ""));
        for (int i = 0; i < a.size(); i++) p.sendMessage(P + "§f" + (i + 1) + ". " + a.get(i).name + " §7第 " + a.get(i).value + " 层");
        EmberRunMaps.MapDef fm = maps.byKey(featured(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone())));
        p.sendMessage(P + "§b本周精选挑战通关" + (fm == null ? "" : "（" + fm.key.toUpperCase(Locale.ROOT) + " " + fm.name + "）") + "：" + (f.isEmpty() ? "§7暂无" : ""));
        for (int i = 0; i < f.size(); i++) p.sendMessage(P + "§f" + (i + 1) + ". " + f.get(i).name + " §7" + f.get(i).value + " 次");
        if (p instanceof Player) { // D102: where am I
            Player me = (Player) p;
            int[] ra = top.rankOf(me.getUniqueId(), true, wk), rf = top.rankOf(me.getUniqueId(), false, wk);
            p.sendMessage(P + "§e你：§f深渊 " + (ra == null ? "未上榜（最高第 " + abyssBest(data(me.getUniqueId())) + " 层）" : "第 " + ra[1] + " 层 · 第 " + ra[0] + " 名")
                    + " §7｜ §f精选 " + (rf == null ? "本周还没有挑战通关" : rf[1] + " 次 · 第 " + rf[0] + " 名"));
            p.sendMessage(P + "§8同分时先达到的人排前面 · 测试号不上榜");
            if (season != null) { // D116
                p.sendMessage(P + "§d" + season.seasonLabel() + " §7· 赛季榜加了团本通关和最快通关");
                town.sunshine.corerpg.ConfirmTokens.sendButtons(me, P, new String[]{"[赛季榜]", "/corerpg p1 season season", "4 周一季，季末前 3 名得称号", "LIGHT_PURPLE"},
                        new String[]{"[本周榜]", "/corerpg p1 season week", "", "YELLOW"}, new String[]{"[周目标]", "/corerpg p1 goals", "", "GREEN"});
            }
        }
        return true;
    }

    /** D94: does this week's rule also apply to repeat normal runs of the featured map? */
    public boolean normalRule() {
        EmberRunMaps.Modifier mod = maps.modifierFor(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone()));
        return mod != null && mod.normal;
    }

    /** D94 %corerpg_p1_rule_<map>%: "" unless the map is featured this week and its rule reaches normal runs */
    public String ruleLine(PlayerData d, String key) {
        java.time.LocalDate today = java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone());
        if (!key.equals(featured(today))) return "";
        EmberRunMaps.MapDef m = maps.byKey(key);
        if (m == null) return "";
        EmberRunMaps.Modifier mod = normalRule() ? maps.modifierFor(today) : null;
        boolean on = firstCleared(d, m);
        if (m.tier < 3 && maps.rotationNormalBonusMarks > 0 && progressFlag(d, "q07")) // E-review #7: late players
            return "§b· 本周精选图" + (mod == null ? "" : " · 规则「" + mod.name + "」") + "§8（你已首通 Q07：重打普通版不多给印记，也不占挑战版的 " + maps.rotationWeeklyCap + " 次名额）";
        if (m.tier < 3 && maps.rotationNormalBonusMarks > 0) { // D108: the featured T1/T2 map pays a mark on repeat clears
            int left = Math.max(0, maps.rotationWeeklyCap - d.periodCount(C_ROTATION, EmberRunRules.rotationWeekKey(today)));
            return "§b· 本周精选：重打通关 +" + maps.rotationNormalBonusMarks + " 枚 T" + m.tier + " 印记（本周剩 " + left + "/" + maps.rotationWeeklyCap + "）"
                    + (mod == null ? "" : " · 规则「" + mod.name + "」") + (on ? "" : "§8（首通后才有，首通不受规则影响）");
        }
        if (mod == null) return "";
        // D101 (midgame #9): Q07 normal (T3) gets no extra mark — the extra marks are on the challenge version
        return "§b· 本周规则「" + mod.name + "」§8（" + (on ? "" : "只影响首通后的重打，") + "只加难度；额外印记在 Q07 后的挑战版）";
    }

    /** P2-8 %corerpg_p1_modifier%: this week's featured-map rule, e.g. 「限药：本局最多喝 3 瓶回复药」 */
    public String modifierLabel() {
        EmberRunMaps.Modifier mod = maps.modifierFor(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone()));
        return mod == null ? "无" : mod.name + "：" + mod.text;
    }

    /**
     * P2-8 potion cap: true when {@code u} is in a run whose weekly rule caps heal potions and has used them all
     * (the caller cancels the drink). Counts come from the run's own potion tally.
     */
    public boolean potionCapped(UUID u) {
        if (maps == null) return false;
        EmberRunSession s = openSessionOf(u);
        if (s == null) return false;
        EmberRunMaps.Modifier mod = maps.modifier(s.modifier);
        if (mod == null || mod.potionCap <= 0) return false;
        Integer used = s.potions.get(u);
        return used != null && used >= mod.potionCap;
    }

    public int potionCap(UUID u) {
        EmberRunSession s = maps == null ? null : openSessionOf(u);
        EmberRunMaps.Modifier mod = s == null ? null : maps.modifier(s.modifier);
        return mod == null ? 0 : mod.potionCap;
    }

    public String stateLabel(PlayerData d, EmberRunMaps.MapDef m) {
        if (firstClearDone(d, m)) return "已首通";
        if (unlocked(d, m)) return "已解锁";
        EmberRunMaps.MapDef req = maps.byKey(m.requires);
        return "未解锁（需 " + (req == null ? m.requires : req.key.toUpperCase(Locale.ROOT)) + " 首通）";
    }

    // ------------------------------------------------------------------ PAPI %corerpg_p1_*%

    /** D240 / ARCH S3-11: sections live in {@link EmberRunPapi} (same keys / values / first-match order). */
    public String placeholder(Player p, String key) { return papi.placeholder(p, key); }

    /** D240: PAPI section accessors (read-only). */
    Object[] passOf(UUID u) { return passes.get(u); }

    EmberLeaderboard top() { return top; }

    EmberRunPapi papi() { return papi; }
}
