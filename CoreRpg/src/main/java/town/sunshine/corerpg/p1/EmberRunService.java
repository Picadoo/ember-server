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
    /** P2-1: weekly challenge rotation bonus clears, period = rotation week key */
    static final String C_ROTATION = "p2_rotation";
    static final String C_UNLOCK = "p1_unlock_";
    static final String C_FIRST = EmberForgeService.FLAG_PREFIX; // D205: fact p1_first_clear_<map>@all (see EmberFirstClear); package paid = p1_fcpay_<map>@<ver>

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
    CoreRpgPlugin plugin() { return plugin; }

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
        return enter(leader, mapKey, challenge, 0, 0L);
    }

    // ------------------------------------------------------------------ P2-2 abyss (book §18.3, D70)

    static final String C_ABYSS_BEST = "p2_abyss_best";
    static final String C_RAID = "p2_raid_";
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

    /** P2-5: settled clears of this raid in the current Monday-based week */
    public int raidWeek(PlayerData d, EmberRunMaps.MapDef m) {
        return d.periodCount(C_RAID + capKey(m), EmberRunRules.rotationWeekKey(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone())));
    }

    /** P2-6 (D78): the weekly counter of a raid = its cap_group, else its own key */
    static String capKey(EmberRunMaps.MapDef m) { return m.capGroup == null || m.capGroup.isEmpty() ? m.key : m.capGroup; }

    public String raidLabel(PlayerData d, EmberRunMaps.MapDef m) {
        if (!progressFlag(d, m.requires)) return "需本人首通 " + m.requires.toUpperCase(Locale.ROOT);
        return "本周 " + raidWeek(d, m) + "/" + m.weeklyCap + (capKey(m).equals(m.key) ? "" : "（团本合计）") + " · " + maps.partyMin(m) + "～" + maps.partyMax(m) + " 人 · " + maps.cost(m) + " 体力";
    }

    public boolean abyssOpen(PlayerData d) {
        return maps.challenge != null && !maps.abyss.isEmpty() && progressFlag(d, maps.abyssRequires);
    }

    /** highest fully cleared abyss tier of this character (0 = none) */
    public int abyssBest(PlayerData d) { return d.periodCount(C_ABYSS_BEST, "all"); }

    /** highest tier this character may start now (best + 1, capped by the table) */
    public int abyssMaxStart(PlayerData d) { return Math.min(maps.abyss.size(), abyssBest(d) + 1); }

    /**
     * One abyss segment = one new entry (book §18.3): the seeded map at challenge values × tier factors, 30 stamina +
     * the tier fee reserved now and refunded like stamina, settled only when its boss dies.
     */
    public boolean tryEnterAbyss(final Player leader, int tier) {
        if (maps.abyssTier(tier) == null) { leader.sendMessage(P + ChatColor.RED + "深渊层数 1～" + maps.abyss.size()); return true; }
        long seed = rnd.nextLong();
        EmberRunMaps.MapDef m = maps.abyssMap(seed);
        if (m == null) { leader.sendMessage(P + "深渊未配置。"); return true; }
        return enter(leader, m.key, true, tier, seed);
    }

    private boolean enter(final Player leader, String mapKey, final boolean challenge, final int abyss, long presetSeed) {
        if (!EmberMode.active()) {
            leader.sendMessage(P + "新模式（ember-v1.0-P1）尚未开启，主线 Q 本暂不可进入。");
            return true;
        }
        final EmberRunMaps.MapDef m = maps.byKey(mapKey);
        if (m == null) { leader.sendMessage(P + "未知主线本 " + mapKey); return true; }
        if (challenge && maps.challenge == null) { leader.sendMessage(P + "挑战版未配置。"); return true; }
        final EmberRunMaps.AbyssTier at = abyss > 0 ? maps.abyssTier(abyss) : null;
        if (abyss > 0 && at == null) { leader.sendMessage(P + "深渊未配置。"); return true; }
        if (!EmberRunBridges.teamLeader(leader)) { leader.sendMessage(P + "组队时由队长开本。"); return true; }
        List<UUID> ids = EmberRunBridges.teamMembers(leader);
        List<Player> party = new ArrayList<Player>();
        List<String> problems = new ArrayList<String>();
        for (UUID u : ids) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline()) { problems.add("队员不在线：" + nameOf(u)); continue; } // D101: a name, not a uuid
            party.add(p);
        }
        final int cost = maps.cost(m);
        if (party.size() < maps.partyMin(m) || party.size() > maps.partyMax(m))
            problems.add("人数 " + maps.partyMin(m) + "～" + maps.partyMax(m) + "，当前 " + party.size());
        if (m.raid && (challenge || abyss > 0)) problems.add("团本没有挑战 / 深渊版本");
        if (m.event && (challenge || abyss > 0)) problems.add("活动本没有挑战 / 深渊版本");
        if (m.rush && (challenge || abyss > 0)) problems.add(m.rushLabel + "没有挑战 / 深渊版本");
        StaminaService st = plugin.getStaminaService();
        if (st == null) problems.add("体力服务未就绪");
        for (Player p : party) {
            PlayerData d = data(p.getUniqueId());
            if (at != null) {
                if (!abyssOpen(d)) problems.add(p.getName() + " 未开放深渊（需本人首通 " + maps.abyssRequires.toUpperCase(Locale.ROOT) + "）");
                else if (abyss > abyssMaxStart(d)) problems.add(p.getName() + " 深渊最高只能开第 " + abyssMaxStart(d) + " 层（先完整通关第 " + abyssBest(d) + " 层）");
                int fee0 = feeFor(p, at.fee); // D142 深渊行者
                if (d.getCoin() < fee0 && feeMarks(d, fee0) == 0)
                    problems.add(p.getName() + " 余烬币不足（这一层 " + fee0 + "，当前 " + d.getCoin() + "）"
                            + (maps.abyssFeeMarkCoin > 0 ? "，多出来的 T3 印记也不够抵（1 枚抵 " + maps.abyssFeeMarkCoin + " 币，留 " + EmberCosmetics.MARK_RESERVE + " 枚）" : ""));
            } else if (challenge && !challengeOpen(d)) {
                problems.add(p.getName() + " 未开放挑战版（需本人首通 " + maps.challenge.requires.toUpperCase(Locale.ROOT) + "）");
            } else if (m.event) { // D139: festival window, own first clear of `requires`, daily entries
                String why = festival == null ? "活动未加载" : festival.entryProblem(p, d);
                if (why != null) problems.add(why);
            } else if (m.rush) { // D144: own Q07 first clear; D160: no weekly entry limit (the reward is claimed once a week)
                if (!progressFlag(d, m.requires)) problems.add(p.getName() + " 未开放" + m.rushLabel + "（需本人首通 " + m.requires.toUpperCase(Locale.ROOT) + "）");
                else for (String ck : m.chainKeys) if (!progressFlag(d, ck)) { problems.add(p.getName() + " 还没首通 " + ck.toUpperCase(Locale.ROOT) + "（" + m.rushLabel + "只打已首通的图的首领）"); break; } // D174 stage 2b
            } else if (m.raid) { // P2-5: own Q07 first clear + weekly cap of settled clears
                if (!progressFlag(d, m.requires)) problems.add(p.getName() + " 未开放团本（需本人首通 " + m.requires.toUpperCase(Locale.ROOT) + "）");
                else if (m.weeklyCap > 0 && raidWeek(d, m) >= m.weeklyCap) problems.add(p.getName() + " 本周团本次数已满（" + raidWeek(d, m) + "/" + m.weeklyCap + "，周一 0 点重置）");
            } else if (!challenge && !unlocked(d, m)) {
                EmberRunMaps.MapDef req = maps.byKey(m.requires);
                problems.add(p.getName() + " 未解锁（需先首通 " + (req == null ? m.requires : req.key.toUpperCase(Locale.ROOT) + " " + req.name) + "）");
            }
            if (openSessionOf(p.getUniqueId()) != null) problems.add(p.getName() + " 已在另一局主线本中");
            if (p.getWorld().getName().startsWith("dungeon_")) problems.add(p.getName() + " 仍在副本内");
            if (st != null && st.staminaOf(p) < cost) problems.add(p.getName() + " 体力不足（需 " + cost + "，当前 " + st.staminaOf(p) + "）");
        }
        if (!problems.isEmpty()) {
            for (Player p : party) for (String s : problems) p.sendMessage(P + ChatColor.RED + s);
            if (!party.contains(leader)) for (String s : problems) leader.sendMessage(P + ChatColor.RED + s);
            return true;
        }
        // D96: a first attempt at Q02+ without a T1 blade in hand or a selected T1 charm is a near-certain death (p1sim
        // 0 % even at dodge 0.5) that costs a third of the day's stamina: warn once, entering stays the player's choice.
        if (!challenge && abyss == 0 && !m.raid && !m.event && !m.rush && !forcedReady.remove(leader.getUniqueId())) {
            EmberLoadoutService ls = plugin.getEmberLoadouts();
            EmberRunMaps.MapDef q1 = maps.maps.isEmpty() ? null : maps.maps.values().iterator().next();
            List<String> warn = new ArrayList<String>();
            boolean noCharm = false;
            if (ls != null && q1 != null && !m.key.equals(q1.key)) for (Player p : party) {
                if (firstClearDone(data(p.getUniqueId()), m)) continue;
                EmberLoadout l = ls.refresh(p);
                // D98: Q01 first clear now gives the T1 charm; the T1 blade comes from Q01 drops (Q01 leans to blades)
                if (l.blade == null || l.blade.tier < 1) warn.add(p.getName() + " 主手还没有 T1 刃：回 Q01 多打几局（Q01 偏向掉刃），拿到会自动放到快捷栏第 1 格");
                if (l.charm == null || l.charm.tier < 1) {
                    warn.add(p.getName() + " 还没有生效的 T1 护符（生命只有一半）：先领 Q01 首通自选的护符");
                    if (p.equals(leader)) noCharm = true;
                }
            }
            if (!warn.isEmpty()) {
                // D101 (midgame recheck #1): say the whole rule — both T1 pieces come from Q01; the Q02 first-clear blade
                // is a second, chosen-family blade (to match the charm), not the way in
                leader.sendMessage(P + "§e" + m.key.toUpperCase(Locale.ROOT) + " 首通推荐：T1 刃 + T1 护符，两件都来自 Q01"
                        + "（护符 = Q01 首通自选，刃 = Q01 掉落）。" + (m.key.equals("q02") ? "Q02 首通送一次免费定向兑换（自选族和部位的 T1 件），用来补齐同族的那一件。" : ""));
                for (String w : warn) leader.sendMessage(P + ChatColor.YELLOW + "⚠ " + w);
                leader.sendMessage(P + "§7这样首通 " + m.key.toUpperCase(Locale.ROOT) + " 几乎打不过，倒下不退体力。");
                List<String[]> btn = new ArrayList<String[]>();
                if (noCharm) btn.add(new String[]{"[领 Q01 首通护符]", "/corerpg p1 firstclear", "领取 Q01 首通自选的 T1 护符（选族）", "GREEN"});
                btn.add(new String[]{"[回 Q01]", "/corerpg p1 enter " + q1.key, "开一局 Q01（30 体力），刷 T1 刃", "GREEN"});
                btn.add(new String[]{"[仍然进入]", "/corerpg p1 enter " + m.key + " force", "本次不再提醒，直接开本", "RED"});
                town.sunshine.corerpg.ConfirmTokens.sendButtons(leader, P, btn.toArray(new String[0][]));
                return true;
            }
        }
        // D104 (midgame #2): the challenge is tuned for T3 — warn once per server session when someone still has a T2 blade
        if (challenge && abyss == 0 && !forcedReady.remove(leader.getUniqueId()) && warnedT3.add(leader.getUniqueId())) {
            EmberLoadoutService ls = plugin.getEmberLoadouts();
            List<String> low = new ArrayList<String>();
            if (ls != null) for (Player p : party) {
                EmberLoadout l = ls.refresh(p);
                if (l.blade == null || l.blade.tier < 3) low.add(p.getName());
            }
            if (!low.isEmpty()) {
                leader.sendMessage(P + "§e挑战版按 T3 装备来调；" + String.join("、", low) + " 主手还不是 T3 刃。");
                leader.sendMessage(P + "§7先用 8 枚 T3 印记兑换（或升阶）一把 T3 刃，再到工坊「互换」免费把强化挪过去，通关率会高很多。");
                town.sunshine.corerpg.ConfirmTokens.sendButtons(leader, P,
                        new String[]{"[印记兑换]", "/corerpg p1 marks", "看看能兑换什么", "GREEN"},
                        new String[]{"[仍然进入]", "/corerpg p1 enter " + m.key + " challenge force", "本次不再提醒，直接开本", "RED"});
                return true;
            }
        }
        // create the session first (seed, snapshot, extra event fixed now — never re-rolled on reconnect)
        final EmberRunSession s = new EmberRunSession();
        s.runId = m.key + (abyss > 0 ? "a" + abyss : challenge ? "c" : "") + "-" + Long.toString(System.currentTimeMillis(), 36) + "-" + Integer.toString(rnd.nextInt(36 * 36 * 36), 36);
        s.mapKey = m.key;
        s.dungeon = m.dungeon;
        s.mapVersion = m.mapVersion;
        s.ruleVersion = maps.ruleVersion;
        s.contentVersion = m.contentVersion;
        s.challenge = challenge;
        s.abyss = abyss;
        s.tier = challenge ? maps.challenge.tier : m.tier;
        if (challenge && abyss == 0) { // P2-8 weekly rule, fixed at entry
            java.time.LocalDate today = java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone());
            EmberRunMaps.Modifier mod = m.key.equals(featured(today)) ? maps.modifierFor(today) : null;
            if (forcedModifier != null) {
                log().info("[P1 run] " + s.runId + " modifier forced " + forcedModifier.id + " (admin test)");
                mod = forcedModifier;
                forcedModifier = null;
            }
            s.modifier = mod == null ? "" : mod.id;
        } else if (!challenge && abyss == 0 && !m.raid && !m.event && !m.rush) { // D94: repeat normal runs of the featured map get the rule too
            java.time.LocalDate today = java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone());
            EmberRunMaps.Modifier mod = m.key.equals(featured(today)) ? maps.modifierFor(today) : null;
            if (mod != null && !mod.normal) mod = null;                 // 术者换防 stays challenge-only (model: +6～+17 points)
            if (mod != null) for (Player p : party) if (!firstCleared(data(p.getUniqueId()), m)) { mod = null; break; } // first clears stay canonical
            if (forcedModifier != null) {
                log().info("[P1 run] " + s.runId + " modifier forced " + forcedModifier.id + " (admin test, normal run)");
                mod = forcedModifier;
                forcedModifier = null;
            }
            s.modifier = mod == null ? "" : mod.id;
            if (mod == null) { // D174 stage 2b 自选誓约: the leader's pledge, only where the weekly rule is off and everyone has the first clear
                String pk = pledgeKey(leader, m, party);
                if (pk != null) {
                    s.modifier = pk;
                    log().info("[P1 run] " + s.runId + " pledge " + pk + " by " + leader.getName());
                }
            }
        }
        s.seed = presetSeed != 0L ? presetSeed : rnd.nextLong();
        s.created = System.currentTimeMillis();
        s.leader = leader.getUniqueId();
        s.extra = EmberRunRules.rollExtra(new java.util.Random(EmberRunRules.subSeed(s.seed, "extra")).nextDouble());
        if (forcedExtra != null) { // admin test hook, one shot
            log().info("[P1 run] " + s.runId + " extra forced " + s.extra.id + " → " + forcedExtra.id + " (admin test)");
            s.extra = forcedExtra;
            forcedExtra = null;
        }
        if (!challenge && abyss == 0 && !m.raid && !m.rush && maps.variety.on()) { // D138: repeat-run variety, first clears stay canonical
            boolean all = true;
            for (Player p : party) if (!firstCleared(data(p.getUniqueId()), m)) { all = false; break; }
            if (all || forcedVariety != null) {
                String[] v = maps.variety.roll(EmberRunRules.subSeed(s.seed, "variety"));
                if (forcedVariety != null) { // admin test hook, one shot: "regen:r1" / "crystal:r2" / "event:r1" / "escort:r2"
                    String[] fv = forcedVariety.split(":");
                    String id = fv[0], room = fv.length > 1 ? fv[1] : "r1";
                    if (EmberRunMaps.Variety.KNOWN.contains(id)) { v[1] = id; v[0] = room; }
                    else if ("event".equals(id) || "timed".equals(id)) { v[2] = room; if (v.length > 3) v[3] = "timed"; }
                    else if (EmberRunMaps.Variety.EVENTS.contains(id)) { v[2] = room; if (v.length > 3) v[3] = id; }
                    log().info("[P1 run] " + s.runId + " variety forced " + forcedVariety + " (admin test)");
                    forcedVariety = null;
                }
                s.affixRoom = v[0];
                s.affix = v[1];
                s.eventRoom = v[2];
                s.eventKind = v.length > 3 && v[3] != null ? v[3] : (!v[2].isEmpty() ? "timed" : "");
            }
        }
        for (Player p : party) {
            s.participants.add(p.getUniqueId());
            String t = target(data(p.getUniqueId()));
            s.target.put(p.getUniqueId(), t == null ? "" : t);
        }
        // reserve
        List<Player> reserved = new ArrayList<Player>();
        for (Player p : party) {
            StaminaService.ConsumeResult r = st.reserveFlat(p, cost);
            if (!r.ok) {
                for (Player q : reserved) release(s, q.getUniqueId(), "预留失败回滚");
                for (Player q : party) q.sendMessage(P + ChatColor.RED + (r.failMessage == null ? "体力不足" : r.failMessage));
                return true;
            }
            s.cost.put(p.getUniqueId(), r.cost);
            ledgerRow(p.getUniqueId(), s.runId, "cost", "stamina:" + r.cost, EmberRunRules.ST_RESERVED);
            reserved.add(p);
            final int pfee = at == null ? 0 : feeFor(p, at.fee); // D142 深渊行者: own fee per player
            if (at != null && pfee > 0) { // P2-2: the segment fee rides with the stamina reservation
                PlayerData pd = data(p.getUniqueId());
                int fm = pd.getCoin() < pfee ? feeMarks(pd, pfee) : 0; // F-review #5: surplus T3 marks pay when coins cannot
                if (fm > 0) {
                    pd.addPeriodCount(C_MARK + 3, "all", -fm);
                    s.fee.put(p.getUniqueId(), 0);
                    ledgerRow(p.getUniqueId(), s.runId, "cost_coin", "mark:3:" + fm, EmberRunRules.ST_RESERVED);
                    p.sendMessage(P + "§7这一层的费用 " + pfee + " 币用 §f" + fm + " 枚 T3 印记§7抵了（余烬币不够；1 枚抵 " + maps.abyssFeeMarkCoin
                            + " 币，剩 " + marks(pd, 3) + " 枚）· 没打成会和体力一起退回");
                    log().info("[P1 run] " + s.runId + " abyss fee " + p.getName() + ": " + fm + " T3 mark(s) for " + pfee + " coin");
                } else if (!pd.takeCoin(pfee)) {
                    for (Player q : reserved) release(s, q.getUniqueId(), "预留失败回滚");
                    for (Player q : party) q.sendMessage(P + ChatColor.RED + p.getName() + " 余烬币不足（这一层 " + pfee + "）");
                    return true;
                } else {
                    s.fee.put(p.getUniqueId(), pfee);
                    ledgerRow(p.getUniqueId(), s.runId, "cost_coin", "coin:" + pfee, EmberRunRules.ST_RESERVED);
                }
                plugin.getDataStore().flushMutation(p.getUniqueId());
            }
        }
        sessions.put(s.runId, s);
        store.save(s);
        long until = System.currentTimeMillis() + maps.passSeconds * 1000L;
        for (Player p : party) {
            passes.put(p.getUniqueId(), new Object[]{m.key, until});
            p.sendMessage(P + (at != null ? "§5深渊 · 余烬层 第 " + abyss + " 层 §7→ §e" + m.key.toUpperCase(Locale.ROOT) + " " + m.name
                    + " §7正在创建实例……（已预留体力 " + cost + (at.fee > 0 ? " · 余烬币 " + at.fee : "") + "）"
                    : m.rush ? "§c" + m.rushLabel + " §7正在创建实例……（不耗体力 · " + rushRuleText(m) + " · "
                            + (EmberRunRules.rushPaysReward(rushWeek(data(p.getUniqueId()), p.getUniqueId(), m), m.rushWeekly) ? "§a你本周奖励未领完§7" : "§e你本周已领完，这局是练习（无奖励）§7") + "）"
                    : m.event ? "§c国庆活动本 §6" + m.name + " §7正在创建实例……（不耗体力 · 今日第 " + (festival.entriesToday(data(p.getUniqueId())) + 1) + "/" + festival.dailyEntries + " 次）"
                    : "§e" + (m.raid ? "团本 " : "") + m.key.toUpperCase(Locale.ROOT) + " " + m.name + (challenge ? " §c挑战版" : "") + " §7正在创建实例……（已预留体力 " + cost + "）"));
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
        EmberRunMaps.MapDef vm = maps.byKey(s.mapKey);
        s.hpFactor = EmberRunMaps.hpFactor(vm, s.partySize);
        s.dmgFactor = EmberRunMaps.dmgFactor(vm, s.partySize); // P2-5 raids only (1.0 elsewhere)
        store.save(s);
        if (s.abyss > 0) {
            EmberRunMaps.AbyssTier t = maps.abyssTier(s.abyss);
            if (t != null) tellRun(s, "§5深渊第 " + s.abyss + " 层 §7· 敌方生命 ×" + String.format(Locale.ROOT, "%.2f", t.hp) + " 伤害 ×"
                    + String.format(Locale.ROOT, "%.2f", t.dmg) + "（在挑战版之上）· 掉落成色 " + qualityLabel(t.quality) + " · 打完首领才结算，失败只丢这一层的花费");
        }
        potionCheck(s);
        if (vm != null && vm.event && festival != null) { // D139: the day's entry counts once the player is inside
            for (UUID u : s.committed) { PlayerData pd = data(u); if (pd != null) { festival.countEntry(pd); flushData(u); } }
            tellRun(s, "§c国庆 · " + vm.name + " §7· " + s.partySize + " 人（敌方生命 ×" + String.format(Locale.ROOT, "%.2f", s.hpFactor)
                    + "）· 小怪和首领掉" + festival.coinName + " · 不发余烬币和装备 · 首次通关得限时称号 · 倒下即失败");
            return;
        }
        if (vm != null && vm.rush) { // D144: chain bosses × rush HP / damage on top of the party HP factor
            s.hpFactor = s.hpFactor * vm.rushHp;
            s.dmgFactor = vm.rushDmg;
            store.save(s);
            tellRun(s, "§c" + vm.rushLabel + " §7· " + s.partySize + " 人 · " + rushChainText(vm) + " · 首领生命 ×" + String.format(Locale.ROOT, "%.2f", s.hpFactor)
                    + " 伤害 ×" + String.format(Locale.ROOT, "%.2f", s.dmgFactor) + (vm.chain.size() > 1 ? " · 每打倒一个休息 " + Math.round(vm.rushBreak) + " 秒、站着的人回复 "
                    + Math.round(vm.rushHeal * 100) + "% 生命" : "") + " · 倒下观战，没有复活 · 只发" + rushRewardText(vm));
            return;
        }
        if (vm != null && vm.raid) {
            tellRun(s, "§6团本开始 §7· " + s.partySize + " 人 · 掉落 T3 · 敌方生命 ×" + String.format(Locale.ROOT, "%.2f", s.hpFactor)
                    + " 伤害 ×" + String.format(Locale.ROOT, "%.2f", s.dmgFactor) + " · 倒下后观战队友，下一个房间开打、首领转阶段时自动复活（50% 生命），首领最后 20% 生命再复活一次 · 走进前方房间开战 · 首领死后统一结算");
            if (!vm.partyHint.isEmpty()) tellRun(s, "§e" + vm.partyHint); // D166
            return;
        }
        EmberRunMaps.Modifier mod = maps.modifier(s.modifier);
        int npl = EmberRunMaps.pledgeIds(s.modifier).size();
        if (mod != null && npl > 0) tellRun(s, "§d自选誓约「" + mod.name + "」§7" + mod.text + " · 通关结算每人 +" + npl + " 枚本图首领徽记（掉落不变）");
        else if (mod != null) tellRun(s, "§b本周规则「" + mod.name + "」§7" + mod.text + "（奖励不变" + (s.challenge ? "" : "；本周精选图首通后的重打") + "）");
        tellRun(s, (s.abyss > 0 ? "§5深渊 §7· 掉落 T3 · " : s.challenge ? "§c挑战版 §7· 掉落 T3 · " : "§7") + "主线本开始 · " + s.partySize + " 人（敌方生命 ×" + String.format(Locale.ROOT, "%.2f", s.hpFactor)
                + "）· 走进前方房间开战 · 击败首领后统一结算");
    }

    private void commit(EmberRunSession s, UUID u) {
        if (!s.committed.add(u)) return;
        EmberRunRules.Ledger l = store.ledger(u);
        l.mark(s.runId, "cost", EmberRunRules.ST_COMMITTED, System.currentTimeMillis());
        store.saveLedger(u, Collections.singletonList(l.get(s.runId, "cost")));
        if (l.get(s.runId, "cost_coin") != null) {
            l.mark(s.runId, "cost_coin", EmberRunRules.ST_COMMITTED, System.currentTimeMillis());
            store.saveLedger(u, Collections.singletonList(l.get(s.runId, "cost_coin")));
        }
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
        releaseFee(s, u, l);
        log().info("[P1 run] " + s.runId + " release " + u + " (" + why + ")");
    }

    /** F-review #5 (D124): T3 marks that would pay this fee (0 = off / not enough surplus above the exchange reserve) */
    int feeMarks(PlayerData d, int fee) {
        if (fee <= 0 || maps.abyssFeeMarkCoin <= 0 || d == null) return 0;
        int need = (fee + maps.abyssFeeMarkCoin - 1) / maps.abyssFeeMarkCoin;
        return marks(d, 3) - EmberCosmetics.MARK_RESERVE >= need ? need : 0;
    }

    /**
     * Endgame #6 (D128): the first failed challenge / abyss run of the stamina day gives back runs.yml fail_refund of the
     * stamina it cost (no loot, no coins, the abyss fee stays spent). One ledger row per (player, day) like the death
     * refund: record() never overwrites, so a second fail, a relog or a restart pays nothing more.
     * Returns the stamina given back, 0 = already used today, -1 = not eligible (off / no committed cost).
     */
    private int failRefund(UUID u, EmberRunSession s) {
        Integer c = s.cost.get(u);
        int back = EmberRunRules.failRefundAmount(c == null ? 0 : c, maps.failRefund);
        StaminaService st = plugin.getStaminaService();
        if (back <= 0 || st == null) return -1;
        EmberRunRules.Ledger l = store.ledger(u);
        EmberRunRules.Row cost = l.get(s.runId, "cost");
        if (cost == null || EmberRunRules.ST_RELEASED.equals(cost.status)) return -1;
        String day = town.sunshine.corerpg.DailyService.today();
        boolean[] created = new boolean[1];
        EmberRunRules.Row r = l.record(EmberRunRules.failRefundRun(day), EmberRunRules.FAIL_REFUND_KEY,
                "stamina:" + back + ":" + s.runId, EmberRunRules.ST_DELIVERED, System.currentTimeMillis(), created);
        if (!created[0]) {
            log().info("[P1 run] fail refund " + u + " " + day + ": already used today (" + r.result + ")");
            return 0;
        }
        store.saveLedger(u, Collections.singletonList(r));
        st.releaseFlat(u, back);
        log().info("[P1 run] fail refund " + u + " " + day + " run " + s.runId + ": " + back + " stamina (cost " + c + ")");
        return back;
    }

    /** D128 PAPI p1_failrefund: whether today's failed-challenge refund is still there */
    String failRefundLabel(UUID u) {
        if (maps.failRefund <= 0) return "";
        int pct = (int) Math.round(maps.failRefund * 100);
        boolean used = store.ledger(u).get(EmberRunRules.failRefundRun(town.sunshine.corerpg.DailyService.today()), EmberRunRules.FAIL_REFUND_KEY) != null;
        return used ? "§8今天的失败退还已用过（明天 0 点再有）" : "§a每天第一次失败退还 " + pct + "% 体力（" + EmberRunRules.failRefundAmount(maps.cost, maps.failRefund) + " 点；不给掉落和币）";
    }

    /** P2-2: the abyss fee goes back together with the stamina, once (ledger "cost_coin" status). */
    private void releaseFee(EmberRunSession s, UUID u, EmberRunRules.Ledger l) {
        EmberRunRules.Row f = l.get(s.runId, "cost_coin");
        if (f == null || EmberRunRules.ST_RELEASED.equals(f.status)) return;
        Integer fee = s.fee.get(u);
        EmberRunRules.Grant paid = EmberRunRules.Grant.decode("cost_coin", f.result);
        if (paid != null && paid.kind == EmberRunRules.Kind.MARK) { // F-review #5: marks go back as marks
            data(u).addPeriodCount(C_MARK + paid.id, "all", paid.amount);
            plugin.getDataStore().flushMutation(u);
        } else if (fee != null && fee > 0) {
            data(u).addCoin(fee);
            plugin.getDataStore().flushMutation(u);
        }
        l.mark(s.runId, "cost_coin", EmberRunRules.ST_RELEASED, System.currentTimeMillis());
        store.saveLedger(u, Collections.singletonList(f));
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

    void onRoomStarted(EmberRunSession s, EmberRunMaps.Room r, boolean b, int spawned, int planned, String comp) {
        if (EmberRunSession.ENTERED.equals(s.state)) s.state = EmberRunSession.FIGHTING;
        if (s.fightStart == 0L) s.fightStart = System.currentTimeMillis(); // D116 raid clear time
        store.save(s);
        // D89: say what is in the room (the A/B variant letter meant nothing to players; it stays in the log)
        tellRun(s, "§e" + r.label + " §7· 敌人 " + spawned + (spawned < planned ? "/" + planned : "") + "：" + comp);
        if (!r.hint.isEmpty()) tellRun(s, "§e提示：§f" + r.hint); // D166
        log().info("[P1 run] " + s.runId + " " + r.id + " variant " + (b ? "B" : "A") + " " + comp.replaceAll("§.", ""));
        reviveFallen(s, "新房间开打"); // D106
    }

    void onRoomCleared(EmberRunSession s, EmberRunMaps.Room r, boolean last) {
        store.save(s);
        tellRun(s, "§a" + r.label + " 已清空" + (r.door != null ? " · 门已打开" : "") + (last ? "" : ""));
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
        tellRun(s, "§c首领 " + b.name + (s.challenge ? "（挑战）" : "") + " §7现身 · 招式都有预警，看清地面火线再躲");
        reviveFallen(s, "首领现身"); // D106
    }

    // ------------------------------------------------------------------ D106 raid falls: watch a teammate, revive later

    void onBossPhase(EmberRunSession s, String why) { reviveFallen(s, why); }

    boolean isRaid(EmberRunSession s) { return raidRun(s); }

    private boolean raidRun(EmberRunSession s) {
        EmberRunMaps.MapDef m = maps.byKey(s.mapKey);
        return m != null && m.raid;
    }

    /** committed members standing in the instance (not fallen, not left, not spectating) */
    private List<Player> livingIn(EmberRunSession s) {
        List<Player> out = new ArrayList<Player>();
        for (UUID u : s.committed) {
            if (s.died.contains(u) || s.left.contains(u)) continue;
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline() || p.isDead() || p.getGameMode() == GameMode.SPECTATOR) continue;
            if (s.world != null && !s.world.equals(p.getWorld().getName())) continue;
            out.add(p);
        }
        return out;
    }

    private Player nearestLiving(EmberRunSession s, Player from) {
        Player best = null;
        double bd = Double.MAX_VALUE;
        for (Player o : livingIn(s)) {
            double d = o.getWorld() == from.getWorld() ? o.getLocation().distanceSquared(from.getLocation()) : Double.MAX_VALUE / 2;
            if (best == null || d < bd) { best = o; bd = d; }
        }
        return best;
    }

    private String nextReviveText(EmberRunSession s) {
        EmberRunDirector d = s.world == null ? null : byWorld.get(s.world);
        String n = d == null ? null : d.nextRevive();
        return n == null ? "本局没有复活点了，等队友打完（首领死后照常结算）" : n + "自动复活（50% 生命）";
    }

    /** a fallen raid member: spectator mode, camera on a living teammate, plus the [观战队友] button and next revive */
    private void watchTeammate(Player p, EmberRunSession s, boolean tell) {
        p.setGameMode(GameMode.SPECTATOR);
        Player t = nearestLiving(s, p);
        if (t != null) {
            if (p.getWorld() != t.getWorld() || p.getLocation().distanceSquared(t.getLocation()) > 4) p.teleport(t.getLocation());
            try { p.setSpectatorTarget(t); } catch (Throwable ignored) { }
        }
        if (tell) {
            p.sendMessage(P + "§c你已倒下§7：观战队友" + (t == null ? "" : " §f" + t.getName()) + "§7 · 下一次复活：§e" + nextReviveText(s));
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, new String[]{"[观战队友]", "/corerpg p1 watch", "换下一个还站着的队友", "AQUA"});
        }
    }

    /** D106: revive every fallen raid member still in the instance at 50 % HP next to a living teammate. */
    void reviveFallen(EmberRunSession s, String why) {
        if (!s.open() || s.died.isEmpty() || !raidRun(s)) return;
        List<Player> alive = livingIn(s);
        if (alive.isEmpty()) return;
        List<String> names = new ArrayList<String>();
        for (UUID u : new ArrayList<UUID>(s.died)) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline() || s.left.contains(u) || p.isDead()) continue;
            if (s.world == null || !s.world.equals(p.getWorld().getName())) continue;
            Player a0 = nearestLiving(s, p);
            final Player a = a0 == null ? alive.get(0) : a0;
            try { p.setSpectatorTarget(null); } catch (Throwable ignored) { }
            // DP keeps a fallen raider in its own "dead" state (revive=true, number=0): clear it first, or DP would
            // count the revived player as dead and end the dungeon when the last DP-alive member falls.
            try { Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "dp revive " + p.getName() + " true true"); }
            catch (RuntimeException ex) { log().warning("[P1 run] dp revive " + p.getName() + ": " + ex); }
            s.died.remove(u);
            names.add(p.getName());
            final String world = s.world;
            Bukkit.getScheduler().runTaskLater(plugin, () -> { // after DP's own respawn teleport
                if (!p.isOnline() || !world.equals(p.getWorld().getName())) return;
                Location to = a.isOnline() && a.getWorld() == p.getWorld() ? a.getLocation() : p.getLocation();
                p.teleport(to);
                p.setGameMode(GameMode.ADVENTURE);
                double max = EmberHeal.maxHp(p);
                p.setHealth(Math.max(1.0, Math.min(max, max * 0.5)));
                EmberHeal.rebase(p); // sanctioned HP change: the B2.144 guard must not revert it
                p.setFireTicks(0);
                p.setFallDistance(0f);
                p.sendMessage(P + "§a" + why + "：你已复活（50% 生命），回到 §f" + a.getName() + " §a身边");
            }, 3L);
            Bukkit.getScheduler().runTaskLater(plugin, () -> { // DP may put the player back on the death point a bit later
                if (!p.isOnline() || !a.isOnline() || a.getWorld() != p.getWorld() || !world.equals(p.getWorld().getName())) return;
                if (p.getLocation().distanceSquared(a.getLocation()) > 64) p.teleport(a.getLocation());
            }, 20L);
        }
        if (names.isEmpty()) return;
        store.save(s);
        tellRun(s, "§a" + why + " · 复活：§f" + String.join("、", names));
        log().info("[P1 run] " + s.runId + " raid revive (" + why + "): " + names);
    }

    private final Map<UUID, Long> leashTold = new java.util.concurrent.ConcurrentHashMap<UUID, Long>();

    /** D106: once a second — a fallen raid member who drifts more than 24 blocks from every living teammate (or out of
     *  the instance world) is put back on a teammate's camera. */
    void leashFallen(EmberRunDirector d) {
        EmberRunSession s = d.s;
        if (!s.open() || s.died.isEmpty()) return;
        for (UUID u : s.died) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline() || p.getGameMode() != GameMode.SPECTATOR || s.left.contains(u)) continue;
            if (p.getWorld() != d.w) continue; // left the instance: handled by onChangedWorld (counts as left)
            if (p.getSpectatorTarget() != null) continue;
            Player t = nearestLiving(s, p);
            if (t == null) continue;
            if (t.getLocation().distanceSquared(p.getLocation()) > 24 * 24) {
                watchTeammate(p, s, false);
                Long last = leashTold.get(u); // F-review #7: once every 10 s, not every second
                long now = System.currentTimeMillis();
                if (last == null || now - last >= 10_000L) {
                    leashTold.put(u, now);
                    p.sendMessage(P + "§7倒下时只能在队友身边 24 格内观战 · 下一次复活：§e" + nextReviveText(s));
                }
            }
        }
    }

    /** D106: spectator-menu teleports out of the raid instance are blocked for fallen members. */
    @EventHandler(ignoreCancelled = true)
    public void onSpectateTeleport(org.bukkit.event.player.PlayerTeleportEvent e) {
        if (e.getCause() != org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.SPECTATE || e.getTo() == null) return;
        Player p = e.getPlayer();
        EmberRunDirector d = byWorld.get(p.getWorld().getName());
        if (d == null || !d.def.raid || !d.s.died.contains(p.getUniqueId())) return;
        if (e.getTo().getWorld() != d.w) {
            e.setCancelled(true);
            p.sendMessage(P + "§7倒下时只能观战本局队友。");
        }
    }

    private final Map<UUID, Long> leaveAsked = new HashMap<UUID, Long>();

    /** D106: a fallen raid member cannot walk out of the instance — /dp leave is held once; a second /dp leave within
     *  10 s is a deliberate give-up (counts as leaving: no revive, no settlement). Hub commands are blocked by QuestService. */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFallenLeave(org.bukkit.event.player.PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        EmberRunDirector d = byWorld.get(p.getWorld().getName());
        if (d == null || !d.def.raid || !d.s.open() || !d.s.died.contains(p.getUniqueId())) return;
        String[] a = e.getMessage().replaceFirst("^/", "").trim().toLowerCase(Locale.ROOT).split("\\s+");
        String root = a[0].contains(":") ? a[0].substring(a[0].indexOf(':') + 1) : a[0];
        if ((root.equals("dp") || root.startsWith("dungeon")) && a.length >= 2 && "revive".equals(a[1])) { // F-review #7
            e.setCancelled(true);
            p.sendMessage(P + "§7团本里倒下后不能自己复活 · 下一次复活：§e" + nextReviveText(d.s));
            if (plugin.getQuestService() != null) plugin.getQuestService().quietHint(p.getUniqueId()); // no second "不可用" line
            return;
        }
        if (!(root.equals("dp") || root.startsWith("dungeon")) || a.length < 2 || !"leave".equals(a[1])) return;
        Long t = leaveAsked.get(p.getUniqueId());
        long now = System.currentTimeMillis();
        if (t != null && now - t < 10000L) { leaveAsked.remove(p.getUniqueId()); return; } // confirmed give-up
        leaveAsked.put(p.getUniqueId(), now);
        e.setCancelled(true);
        if (plugin.getQuestService() != null) plugin.getQuestService().quietHint(p.getUniqueId());
        p.sendMessage(P + "§c倒下后不能离开团本§7：下一次复活 §e" + nextReviveText(d.s)
                + "§7；本局结束会自动送回。§c确实要放弃本局结算§7：10 秒内再输一次 /dp leave");
    }

    /** /corerpg p1 watch — a fallen raid member cycles the camera through the living teammates. */
    private boolean cmdWatch(CommandSender sender) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        EmberRunDirector d = byWorld.get(p.getWorld().getName());
        if (d == null || !d.s.died.contains(p.getUniqueId()) || !d.s.open()) { p.sendMessage(P + "只有团本里倒下的人可以观战队友。"); return true; }
        List<Player> alive = livingIn(d.s);
        if (alive.isEmpty()) { p.sendMessage(P + "没有还站着的队友。"); return true; }
        Entity cur = p.getSpectatorTarget();
        int i = 0;
        for (int k = 0; k < alive.size(); k++) if (alive.get(k).equals(cur)) { i = (k + 1) % alive.size(); break; }
        Player t = alive.get(i);
        p.setGameMode(GameMode.SPECTATOR);
        try { p.setSpectatorTarget(null); } catch (Throwable ignored) { }
        p.teleport(t.getLocation());
        try { p.setSpectatorTarget(t); } catch (Throwable ignored) { }
        p.sendMessage(P + "§7正在观战 §f" + t.getName() + " §7· 下一次复活：§e" + nextReviveText(d.s));
        return true;
    }

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
    private void potionCheck(EmberRunSession s) {
        EmberSupplyService sup = plugin.getEmberSupplies();
        if (sup == null) return;
        for (UUID u : s.participants) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline()) continue;
            int slot = sup.potionSlot(p);
            if (slot < 0) p.sendMessage(P + "§e你没带回复药：本局只能靠躲技能。出本后右键补给官 · 灰粮或在装备页购买（每瓶 " + EmberSupplyService.price() + " 余烬币）");
            else if (slot == 9) p.sendMessage(P + "§e回复药在背包里：按 E 拖到快捷栏，危险时按数字键切过去、按住右键喝");
            else p.sendMessage(P + "§7回复药在快捷栏第 5–9 格：按数字键切过去，按住右键喝（回复 20%，15 秒冷却）");
        }
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
        boolean chFail = s.challenge && s.fightStarted(); // read before the state flips to FAILED
        s.state = EmberRunSession.FAILED;
        s.reason = why;
        store.save(s);
        EmberRunMaps.MapDef fm = maps.byKey(s.mapKey);
        if (!chFail && fm != null && fm.raid && fm.weeklyCap > 0) { // recheck #3 (D133): the weekly raid count only moves on a clear
            for (UUID u : s.participants) {
                Player p = Bukkit.getPlayer(u);
                if (p == null || !p.isOnline()) continue;
                int used = raidWeek(data(u), fm);
                p.sendMessage(P + ChatColor.RED + "本局失败：" + why + "（已开战不退体力；未结算的额外奖励作废）");
                p.sendMessage(P + "§a本周团本次数没有扣§7：还是 " + used + "/" + fm.weeklyCap + (capKey(fm).equals(fm.key) ? "" : "（团本合计）")
                        + (used < fm.weeklyCap ? "，体力够就可以再来（只有通关才算一次）" : ""));
            }
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
                    ? "§a · 今天第一次挑战失败：退还 " + back + " 体力（花费的 " + Math.round(maps.failRefund * 100) + "%，每天一次；不给掉落和币" + (s.abyss > 0 ? "，层费不退" : "") + "）"
                    : back == 0 ? "§c（今天的失败退还已经用过，明天再有；未结算的额外奖励作废）"
                    : "§c（已开战不退体力；未结算的额外奖励作废）"));
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

    // ------------------------------------------------------------------ D144 余烬连战 (weekly boss rush)

    static final String C_RUSH = "p4_rush";          // period = week key: rush attempts this week (stats only since D160)
    static final String C_RUSH_CLAIM = "p4_rush_claim"; // D160: period = week key: weekly rush REWARD claims (settled clears that paid)
    static final int RUSH_WEEKLY = 1;                 // D160: weekly reward claims (attempts are unlimited and free)

    private static String rushWeekKey() {
        return EmberRunRules.rotationWeekKey(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone()));
    }

    /** D160: reward claims this week; a pre-D160 clear this week (on the weekly time board) counts as the claim. */
    int rushWeek(PlayerData d, UUID u) {
        if (d == null) return 0;
        boolean legacy = d.periodCount(C_RUSH, rushWeekKey()) > 0 && season != null && u != null && season.weekRank(u, "time_rush") != null;
        return EmberRunRules.rushClaims(d.periodCount(C_RUSH_CLAIM, rushWeekKey()), legacy);
    }

    /** D174 stage 2b: claims this week on the entry's own counter (the D144 rush keeps its legacy rule) */
    int rushWeek(PlayerData d, UUID u, EmberRunMaps.MapDef m) {
        if (m == null || m.mainRush()) return rushWeek(d, u);
        return d == null ? 0 : Math.max(0, d.periodCount(m.rushClaim, rushWeekKey()));
    }

    static String rushRuleText(EmberRunMaps.MapDef m) {
        return m.rushWeekly <= 1 ? "每周首通领奖，失败可无限重试" : "每周前 " + m.rushWeekly + " 次通关领奖" + (m.mainRush() ? "" : "（同类共用）") + "，失败可无限重试";
    }

    static String rushRewardText(EmberRunMaps.MapDef m) {
        StringBuilder b = new StringBuilder();
        if (m.rushMarks > 0) b.append(" T").append(m.rushMarkTier).append(" 印记 +").append(m.rushMarks);
        if (m.rushSig > 0) b.append(b.length() == 0 ? "" : " ·").append(" ").append(m.chainKeys.size() > 1 ? "每张图" : m.chainKeys.get(0).toUpperCase(Locale.ROOT)).append("首领徽记 +").append(m.rushSig);
        if (m.rushBadges > 0) b.append(b.length() == 0 ? "" : " ·").append(" 余烬徽 +").append(m.rushBadges);
        if (!m.rushTitle.isEmpty()) b.append(b.length() == 0 ? "" : " ·").append(" 首通称号");
        return b.toString();
    }

    static String rushChainText(EmberRunMaps.MapDef m) {
        StringBuilder b = new StringBuilder();
        for (EmberRunMaps.Boss x : m.chain) b.append(b.length() == 0 ? "" : " → ").append(x.name);
        return b.toString();
    }

    void onRushStart(EmberRunSession s, EmberRunMaps.MapDef m, EmberRunMaps.Boss first) {
        tellRun(s, "§c" + m.rushLabel + " §7· 5 秒后 §f" + first.name + " §7现身（1/" + m.chain.size() + "）· " + rushRuleText(m) + "；领完后是练习（无奖励）");
    }

    /** D144: chain boss {@code done} (0-based) fell; heal everyone standing, say who is next. */
    void onRushStage(EmberRunDirector d, int done, EmberRunMaps.Boss was, EmberRunMaps.Boss next, long secs) {
        EmberRunSession s = d.s;
        int healed = 0;
        for (UUID u : s.committed) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline() || p.isDead() || s.died.contains(u) || !p.getWorld().equals(d.w)) continue;
            double max = EmberHeal.maxHp(p);
            p.setHealth(Math.max(1.0, Math.min(max, p.getHealth() + max * d.def.rushHeal)));
            EmberHeal.rebase(p); // sanctioned HP change (B2.144 guard)
            p.setFireTicks(0);
            healed++;
        }
        tellRun(s, "§a" + was.name + " 倒下 §7（" + (done + 1) + "/" + d.def.chain.size() + "，" + secs + " 秒）· 休息 " + Math.round(d.def.rushBreak)
                + " 秒，站着的人回复 " + Math.round(d.def.rushHeal * 100) + "% 生命 · 下一个：§c" + next.name);
        log().info("[P1 run] " + s.runId + " rush stage " + (done + 1) + " " + was.name + " down after " + secs + " s, healed " + healed);
    }

    /** D144 烬核同心: a clean 烬核 stack (called by the director; counted at the raid settlement, once per run) */
    void onCleanShare(EmberRunSession s, List<Player> inside) {
        int before = s.coreClean.size();
        for (Player p : inside) s.coreClean.add(p.getUniqueId());
        if (s.coreClean.size() > before) log().info("[P1 run] " + s.runId + " clean 烬核 stack n=" + inside.size());
    }

    /** D144: the rush settlement — T3 marks (ledger), 余烬徽, the first-clear title, the weekly time board. */
    private void rushSettle(EmberRunSession s, EmberRunMaps.MapDef m, UUID u) {
        PlayerData pd = data(u);
        EmberRunRules.Ledger l = store.ledger(u);
        final boolean fresh = l.get(s.runId, "rush_mark") == null && l.get(s.runId, "rush_practice") == null && l.get(s.runId, "rush_paid") == null;
        // D160: only the week's first settled clear pays; later clears this week are practice (time board only)
        // D174 stage 2b: echo / outpost entries pay their first `weekly` clears on their own counter
        final boolean pays = fresh ? EmberRunRules.rushPaysReward(rushWeek(pd, u, m), m.rushWeekly) : l.get(s.runId, "rush_mark") != null || l.get(s.runId, "rush_paid") != null;
        List<EmberRunRules.Row> changed = new ArrayList<EmberRunRules.Row>();
        boolean[] created = new boolean[1];
        if (fresh && !pays) { // D160 practice: one marker row so a duplicate settle of this run stays a no-op
            EmberRunRules.Row r = l.record(s.runId, "rush_practice", new EmberRunRules.Grant("rush_practice", EmberRunRules.Kind.MARK, "3", 0, null).encode(),
                    EmberRunRules.ST_DELIVERED, System.currentTimeMillis(), created);
            if (created[0]) changed.add(r);
        }
        if (pays && m.rushMarks > 0) {
            EmberRunRules.Row r = l.record(s.runId, "rush_mark", new EmberRunRules.Grant("rush_mark", EmberRunRules.Kind.MARK, String.valueOf(m.rushMarkTier), m.rushMarks, null).encode(),
                    EmberRunRules.ST_PENDING, System.currentTimeMillis(), created);
            if (created[0]) changed.add(r);
        }
        if (pays && !m.mainRush()) { // D174 stage 2b: claim marker (idempotent re-settle) + insignia of each chain map (ledger, delivered by deliver())
            EmberRunRules.Row r = l.record(s.runId, "rush_paid", new EmberRunRules.Grant("rush_paid", EmberRunRules.Kind.MARK, String.valueOf(m.rushMarkTier), 0, null).encode(),
                    EmberRunRules.ST_DELIVERED, System.currentTimeMillis(), created);
            if (created[0]) changed.add(r);
            if (m.rushSig > 0) for (String ck : m.chainKeys) {
                if (!EmberSignature.hasMap(ck)) continue;
                EmberRunRules.Row g = l.record(s.runId, "rush_sig_" + ck, new EmberRunRules.Grant("rush_sig_" + ck, EmberRunRules.Kind.SIGMARK, ck, m.rushSig, null).encode(),
                        EmberRunRules.ST_PENDING, System.currentTimeMillis(), created);
                if (created[0]) changed.add(g);
            }
        }
        Player p = Bukkit.getPlayer(u);
        long t0 = s.fightStart > 0 ? s.fightStart : s.created;
        int secs = t0 > 0 ? (int) Math.max(1, (System.currentTimeMillis() - t0) / 1000L) : 0;
        if (fresh && pays) {
            pd.addPeriodCount(m.mainRush() ? C_RUSH_CLAIM : m.rushClaim, rushWeekKey(), 1); // D160: the week's reward is claimed
            if (m.rushBadges > 0) pd.addPeriodCount(EmberSeason.C_BADGE, "all", m.rushBadges);
            if (cosmetics != null && m.mainRush()) cosmetics.onRaidClear(p, pd, m.key); // first clear → the 连战不息 title (counts clears)
        }
        if (fresh && m.mainRush() && season != null && secs > 0) season.onRush(u, Bukkit.getOfflinePlayer(u).getName(), secs); // D160: practice clears count on the time board too
        store.saveLedger(u, changed);
        plugin.getDataStore().flushMutation(u);
        log().info("[P1 run] " + s.runId + " rush settle " + u + " rows+" + changed.size() + " secs=" + secs + (fresh ? (pays ? " claim" : " practice") : " (repeat event)"));
        if (p != null && p.isOnline()) {
            String t = (secs / 60) + " 分 " + String.format(Locale.ROOT, "%02d", secs % 60) + " 秒";
            if (fresh && pays && m.mainRush()) p.sendMessage(P + "§c余烬连战通关 §7· 用时 §f" + t
                    + " §7· 本周奖励：T3 印记 +" + m.rushMarks + " · 余烬徽 +" + m.rushBadges + "（共 " + EmberSeason.badges(pd) + "）· 之后本周再打是练习（无奖励）· 本周连战榜看 /corerpg p1 rush");
            else if (fresh && m.mainRush()) p.sendMessage(P + "§c余烬连战通关（练习）§7· 用时 §f" + t + " §7· 本周奖励已经领过，这局不发奖励；成绩照样计入本周最快榜 · 周一 0 点重置");
            else if (fresh && pays) p.sendMessage(P + "§c" + m.rushLabel + "通关 §7· 用时 §f" + t + " §7· 奖励：" + rushRewardText(m)
                    + " §7· 本周已领 " + rushWeek(pd, u, m) + "/" + m.rushWeekly + "（周一 0 点重置）");
            else if (fresh) p.sendMessage(P + "§c" + m.rushLabel + "通关（练习）§7· 用时 §f" + t + " §7· 本周 " + m.rushWeekly + " 次奖励已领完，这局不发奖励 · 周一 0 点重置");
            deliver(p);
        }
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
        if (d.bossSpawnedAt > 0) log().info(String.format(Locale.ROOT, "[P1 run] %s boss killed after %.1f s", s.runId, (System.currentTimeMillis() - d.bossSpawnedAt) / 1000.0));
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
            if (m.event) { if (festival != null) festival.onClear(s, u, cosmetics); } // D139: no main-line settlement
            else if (m.rush) rushSettle(s, m, u); // D144: marks / 余烬徽 / title only
            else settleFor(s, m, u);
        }
        s.state = EmberRunSession.COMPLETE;
        store.save(s);
        tellRun(s, "§a" + d.bossDef().name + " 已击败 · 结算完成，实例稍后关闭");
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
        in.firstClear = s.challenge || m.raid || firstCleared(pd, m) ? null : m.firstClear;
        if (m.raid && maps.challenge != null) in.qualityWeights = maps.challenge.quality; // P2-5: no exclusive drop table
        if (s.challenge && maps.challenge != null) in.qualityWeights = maps.challenge.quality;
        EmberRunMaps.AbyssTier abT = s.abyss > 0 ? maps.abyssTier(s.abyss) : null;
        if (abT != null) in.qualityWeights = abT.quality; // P2-2 tier quality table
        in.loot = m.raid ? null : maps.lootBias(m); // P2-9 (D81) map loot identity (abyss segments: the segment map)
        List<EmberRunRules.Grant> grants = new ArrayList<EmberRunRules.Grant>(EmberRunRules.settle(in));
        // P2-1 weekly challenge rotation: featured map, first 3 challenge clears of the week → +1 mark of the run tier
        java.time.LocalDate today = java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone());
        String week = EmberRunRules.rotationWeekKey(today);
        boolean rotation = s.challenge && s.abyss == 0 && m.key.equals(featured(today)) && maps.rotationBonusMarks > 0
                && pd.periodCount(C_ROTATION, week) < maps.rotationWeeklyCap;
        // D108: a repeat NORMAL clear of the featured T1/T2 map (Q01–Q06) → +normal_bonus_marks of the map tier; one
        // weekly counter with the challenge bonus (C_ROTATION), so the week's featured bonus stays 3 clears in total
        boolean featNormal = !s.challenge && s.abyss == 0 && !m.raid && in.firstClear == null && s.tier < 3
                && m.key.equals(featured(today)) && maps.rotationNormalBonusMarks > 0
                && !progressFlag(pd, "q07") // E-review #7: after the own Q07 first clear a normal repeat neither pays nor uses a challenge slot
                && pd.periodCount(C_ROTATION, week) < maps.rotationWeeklyCap;
        if (featNormal) rotation = true;
        final int rotMarks = s.challenge ? maps.rotationBonusMarks : maps.rotationNormalBonusMarks;
        if (m.raid) { // P2-5 + P2-9 (D82): one targeted T3 roll (floor 精良) + 1 T3 mark; titles / trail are cosmetic
            grants.add(EmberRunRules.raidItem(in, "raid_item", m.lootFamily, maps.raidItemQualityFloor)); // P2-9 (D82)
            grants.add(new EmberRunRules.Grant("raid_mark", EmberRunRules.Kind.MARK, String.valueOf(s.tier), 1, null));
        }
        if (rotation) grants.add(new EmberRunRules.Grant("rot_mark", EmberRunRules.Kind.MARK, String.valueOf(s.tier), rotMarks, null));
        // D174 签名传奇: repeat NORMAL clear of a signature map → 1 insignia + maybe a signature stamp on the base item;
        // the map's first clear → the first-clear insignia, once per map (not per content version, C_FC)
        boolean sigRun = !s.challenge && s.abyss == 0 && !m.raid && !m.event && !m.rush && EmberSignature.hasMap(m.key);
        if (sigRun && in.firstClear == null && progressFlag(pd, m.key)) {
            EmberRunRules.Grant base = null;
            for (EmberRunRules.Grant g : grants) if ("base_item".equals(g.key)) base = g;
            grants.addAll(EmberRunRules.signatureGrants(m.key, in, base));
        }
        final int pledged = pledgeCount(s.modifier);
        if (sigRun && in.firstClear == null && progressFlag(pd, m.key) && pledged > 0) // D174 stage 2b 自选誓约: +1 insignia per pledged rule
            grants.add(new EmberRunRules.Grant("pledge_sigmark", EmberRunRules.Kind.SIGMARK, m.key, pledged, null));
        final boolean sigFc = sigRun && in.firstClear != null && pd.periodCount(EmberSignature.C_FC + m.key, "all") <= 0;
        if (sigFc) grants.add(new EmberRunRules.Grant("fc_sigmark", EmberRunRules.Kind.SIGMARK, m.key, EmberSignature.FC_MARKS, null));
        if (!s.challenge && s.abyss == 0 && !m.raid) // D138 repeat-run variety (rolled only when every member had the first clear)
            grants.addAll(EmberRunRules.varietyGrants(in.firstClear != null, s.affixDone, maps.variety.affixShard, s.eventDone, maps.variety.eventCore, s.eventKind));
        EmberRunRules.Ledger l = store.ledger(u);
        // P2-7 daily bounty (D79): the n-th settled clear of the stamina day; counted once per run (fresh = no base row yet)
        final boolean fresh = l.get(s.runId, "base_coin") == null;
        final String bDay = town.sunshine.corerpg.DailyService.today();
        final List<EmberRunRules.BountyTier> tiers = bountyTiers();
        final int bountyW = m.raid ? 2 : 1; // D105: a raid clear (50 stamina) counts as 2 runs toward the daily bounty
        final int bountyPrev = fresh ? pd.periodCount(C_BOUNTY, bDay) : 0;
        final int bountyN = fresh ? bountyPrev + bountyW : 0;
        List<EmberRunRules.Grant> bountyPaid = fresh ? EmberRunRules.bountyGrants(tiers, bountyPrev, bountyN) : Collections.<EmberRunRules.Grant>emptyList();
        grants.addAll(bountyPaid);
        // D144 花样委托: repeat normal runs only (where the variety rolls); counted once per run (fresh)
        final boolean varRun = fresh && !s.challenge && s.abyss == 0 && !m.raid && !m.event && in.firstClear == null;
        final List<EmberRunRules.VarietyBounty> vbs = varRun ? varietyBounties() : Collections.<EmberRunRules.VarietyBounty>emptyList();
        final List<String> vbDone = new ArrayList<String>();
        boolean vbMoved = false;
        for (EmberRunRules.VarietyBounty vb : vbs) {
            boolean hit = "affix".equals(vb.kind) ? s.affixDone : s.eventDone;
            if (!hit) continue;
            int prev = pd.periodCount(C_VBOUNTY + vb.kind, bDay);
            List<EmberRunRules.Grant> g = EmberRunRules.varietyBountyGrants(vb, prev, true);
            if (prev < vb.count) { pd.addPeriodCount(C_VBOUNTY + vb.kind, bDay, 1); vbMoved = true; }
            if (!g.isEmpty()) { grants.addAll(g); vbDone.add(vb.label() + "（" + vb.rewardText() + "）"); }
        }
        EmberGrowthService growth = EmberGrowthService.get(); // D142 余烬勋记: settlement coin % / +shards (own ledger rows)
        Player gp = Bukkit.getPlayer(u);
        if (growth != null && gp != null) {
            int coins = 0;
            for (EmberRunRules.Grant g : grants) if (g.kind == EmberRunRules.Kind.COIN) coins += g.amount;
            int hc = growth.honorCoin(gp, coins), hs = growth.honorShard(gp);
            if (hc > 0) grants.add(new EmberRunRules.Grant("honor_coin", EmberRunRules.Kind.COIN, null, hc, null));
            if (hs > 0) grants.add(new EmberRunRules.Grant("honor_shard", EmberRunRules.Kind.MAT, EmberUpgradeRules.MAT_SHARD, hs, null));
        }
        List<EmberRunRules.Row> changed = new ArrayList<EmberRunRules.Row>();
        long now = System.currentTimeMillis();
        boolean[] created = new boolean[1];
        for (EmberRunRules.Grant g : grants) {
            String st = g.kind == EmberRunRules.Kind.CHOICE ? EmberRunRules.ST_AWAIT : EmberRunRules.ST_PENDING;
            EmberRunRules.Row r = l.record(s.runId, g.key, g.encode(), st, now, created);
            if (created[0]) changed.add(r);
            if (created[0] && "rot_mark".equals(g.key)) pd.addPeriodCount(C_ROTATION, week, 1); // counted once per run (ledger key)
            if (created[0] && "raid_mark".equals(g.key)) pd.addPeriodCount(C_RAID + capKey(m), week, 1); // P2-5 weekly cap (P2-6: per cap_group)
            if (created[0] && "fc_sigmark".equals(g.key)) pd.addPeriodCount(EmberSignature.C_FC + m.key, "all", 1); // D174: once per map
        }
        if (fresh) pd.addPeriodCount(C_BOUNTY, bDay, bountyW);
        if (fresh && m.raid && cosmetics != null) cosmetics.onRaidClear(Bukkit.getPlayer(u), pd, m.key); // P2-9 (D83)
        if (fresh && season != null && s.coreClean.contains(u)) season.addGoal(u, pd, "core", 1); // D144 烬核同心 (optional goal)
        if (s.challenge && s.abyss == 0 && pd.periodCount(EmberGrowthService.C_CHAL + m.key, "all") == 0)
            pd.addPeriodCount(EmberGrowthService.C_CHAL + m.key, "all", 1); // D141: challenge first clear per map (talent point / honors)
        if (in.firstClear != null) {
            EmberFirstClear.record(pd, m.key, m.contentVersion); // §9.4 + D205: package once per content version; fact @all never deleted
            if (cosmetics != null) cosmetics.onFirstClear(Bukkit.getPlayer(u), m.key); // D103 milestone titles
        }
        boolean newBest = s.abyss > 0 && s.abyss > abyssBest(pd);
        int oldBest = abyssBest(pd);
        if (newBest) pd.addPeriodCount(C_ABYSS_BEST, "all", s.abyss - abyssBest(pd)); // P2-2: opens tier + 1
        if (newBest && cosmetics != null) cosmetics.onAbyssBest(Bukkit.getPlayer(u), oldBest, s.abyss); // P2-9 (D83)
        if (growth != null && gp != null) growth.refreshHonors(gp); // D142: one-time unlock notice
        if (top != null && (s.abyss > 0 || fresh)) { // P2-10 (D84); abyss: idempotent, also lists older records
            String nm = Bukkit.getOfflinePlayer(u).getName();
            if (s.abyss > 0) top.abyssBest(u, nm, abyssBest(pd));
            if (fresh && s.challenge && s.abyss == 0 && m.key.equals(featured(today))) top.featuredClear(u, nm, week);
        }
        if (season != null && fresh) { // D116 season boards + D117 weekly goals (display / cosmetic currency only)
            String nm = Bukkit.getOfflinePlayer(u).getName();
            if (s.abyss > 0) { // F-review #4: the clear time breaks ties on the abyss board
                long a0 = s.fightStart > 0 ? s.fightStart : s.created;
                season.onAbyss(u, nm, s.abyss, a0 > 0 ? (int) Math.max(1, (System.currentTimeMillis() - a0) / 1000L) : 0);
                season.addGoal(u, pd, "abyss", 1);
            }
            if (s.challenge && s.abyss == 0 && m.key.equals(featured(today))) { season.onFeatured(u, nm); season.addGoal(u, pd, "featured", 1); }
            if (m.raid) {
                long t0 = s.fightStart > 0 ? s.fightStart : s.created;
                season.onRaid(u, nm, m.key, t0 > 0 ? (int) Math.max(1, (System.currentTimeMillis() - t0) / 1000L) : 0);
                season.addGoal(u, pd, "raid", 1);
            }
            int topClears = tiers.isEmpty() ? 0 : tiers.get(tiers.size() - 1).clears;
            if (topClears > 0 && bountyPrev < topClears && bountyN >= topClears) season.addGoal(u, pd, "bounty", 1);
        }
        store.saveLedger(u, changed);
        plugin.getDataStore().flushMutation(u);
        log().info("[P1 run] " + s.runId + " settle " + u + " rows+" + changed.size() + (in.firstClear != null ? " (first clear)" : "")
                + (s.abyss > 0 ? " (abyss " + s.abyss + ")" : s.challenge ? " (challenge T" + s.tier + ")" : ""));
        Player p = Bukkit.getPlayer(u);
        if (p != null && p.isOnline() && s.abyss > 0) {
            p.sendMessage(P + "§5深渊第 " + s.abyss + " 层 已完整通关" + (newBest ? " §a· 新纪录，开放第 " + abyssMaxStart(pd) + " 层" : "")
                    + " §7（下一层需重新确认与付费：冒险页 → 深渊）");
        }
        if (p != null && p.isOnline() && rotation) {
            p.sendMessage(P + "§b本周精选" + (s.challenge ? "挑战" : "重打") + " §f" + m.name + "§b：额外 T" + s.tier + " 锻造印记 +" + rotMarks
                    + "§7（本周 " + pd.periodCount(C_ROTATION, week) + "/" + maps.rotationWeeklyCap + "）");
        }
        if (p != null && p.isOnline() && vbMoved) // D144
            p.sendMessage(P + "§e花样委托 §7" + (vbDone.isEmpty() ? "" : "§a完成：" + String.join("、", vbDone) + " §7· ") + varietyBountyLine(pd));
        if (p != null && p.isOnline() && fresh && !tiers.isEmpty()) {
            p.sendMessage(P + "§e每日委托 §7" + (bountyW > 1 ? "§7团本算 " + bountyW + " 局 · " : "") + (bountyPaid.isEmpty() ? "" : "§a完成第 " + bountyN + " 局档 §7· ")
                    + EmberRunRules.bountyLine(tiers, bountyN));
        }
        if (p != null && p.isOnline()) {
            deliver(p);
            if (in.firstClear != null && maps.challenge != null && m.key.equals(maps.challenge.requires)) endOfP1(p);
        }
    }

    /** Batch 3: the Q07 first clear ends the P1 main line (§2 table, §18.1): say what it opened, once. */
    private void endOfP1(Player p) {
        p.sendMessage(P + "§6§l余烬主线完结§r §7— 已首通最后一张主线图 Q07。");
        p.sendMessage(P + "§a已开放：§fT3 定向锻造§7（工坊）· §fT2→T3 升阶§7 · §f七图挑战版、深渊、团本§7（冒险页，掉落 T3 与 T3 印记）");
        p.sendMessage(P + "§7长线目标：同族 T3 两件套 +9 = 觉醒III（装备页看成套进度）");
        // D104 (midgame #2): the challenge is tuned for T3 — say how to get there before the first attempt
        p.sendMessage(P + "§e挑战版按 T3 装备来调。§7刚首通：先用 T3 印记兑换（或升阶）把刃换到 T3，再到工坊「互换」免费把强化挪过去；"
                + "七张挑战图强度相同，只是掉落偏向的族 / 部位不同。");
        PlayerData d = data(p.getUniqueId());
        if (season != null) { season.markGraduated(d); flushData(p.getUniqueId()); } // D134: the bounty goal is prorated in the graduation week
        if (season != null && season.goalsOn()) {
            // F-review #8: today's daily bounty counts for the week even when it was finished before the Q07 clear
            List<EmberRunRules.BountyTier> tiers = bountyTiers();
            int topClears = tiers.isEmpty() ? 0 : tiers.get(tiers.size() - 1).clears;
            if (topClears > 0 && d.periodCount(C_BOUNTY, town.sunshine.corerpg.DailyService.today()) >= topClears && season.progress(d, "bounty") == 0)
                season.addGoal(p.getUniqueId(), d, "bounty", 1);
            // F-review #3: the weekly goals and the season are the reason to come back — say so at graduation
            p.sendMessage(P + "§d周目标和赛季已开放：§7" + goalsShort(d) + "（每周 4 个，完成得余烬徽换外观；赛季榜 4 周一季）");
        }
        town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, new String[]{"[打开冒险页]", "/ember_p1_adventure", "挑战版 / 深渊 / 团本都在这里", "GREEN"},
                new String[]{"[赛季 · 周目标]", "/ember_p1_season", "本周目标、排行榜、赛季奖励、外观商店", "LIGHT_PURPLE"});
        p.playSound(p.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
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
            return true;
        }
        java.util.List<Integer> can = EmberCodex.claimable(d);
        if (can.isEmpty()) { p.sendMessage(P + "§7没有可领取的图录阶段奖励（" + EmberCodex.count(d) + "/" + EmberCodex.ENTRIES.size() + "）"); return true; }
        UUID u = p.getUniqueId();
        for (int i : can) {
            d.addPeriodCount(EmberCodex.C_CLAIM + i, "all", 1); // the counter is the once-only guard (ledger rows get pruned)
            ledgerRow(u, EmberCodex.LEDGER_RUN, "stage" + i, "coin:" + EmberCodex.STAGE_COIN[i], EmberRunRules.ST_PENDING);
            log().info("[P1 codex] " + p.getName() + " stage " + EmberCodex.STAGE_AT[i] + " -> coin " + EmberCodex.STAGE_COIN[i]);
        }
        deliver(p);
        return true;
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
        boolean[] hbEmpty = new boolean[9];
        for (int i = 0; i < 9; i++) { ItemStack x = p.getInventory().getItem(i); hbEmpty[i] = x == null || x.getType() == org.bukkit.Material.AIR; }
        // D208 (ARCH S1-4): a settlement signature is written INTO the piece when the piece is created (v2 sig_code on
        // cr_p1_item + NBT, inside the signed data) — no p1_sig_ counter. SIG rows settle after the ITEM rows below.
        Map<String, Integer> sigFor = new HashMap<String, Integer>();
        java.util.Set<String> itemOpen = new java.util.HashSet<String>(), stamped = new java.util.HashSet<String>(), itemsGiven = new java.util.HashSet<String>();
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
                        if (ps != null) ps.grantFlatEmberXp(p, g.amount, "余烬主线");
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
                    // D216 / ARCH S2-3: mark grants with a known REG source go through grantMark
                    String src = EmberEconomy.sourceForGrant(g.key, r.runId);
                    if (src != null) {
                        if (!EmberEconomy.grantMark(d, src, tier, g.amount))
                            log().warning("[P1 run] economy grantMark refused " + src + " T" + tier + " " + g.amount + " for " + p.getName());
                    } else {
                        d.addPeriodCount(C_MARK + tier, "all", g.amount);
                    }
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
                    if (ni != null && vlt != null && vlt.autoDeposit(p, g.id, g.amount)) {
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
                    EmberVault vlt = EmberVault.get();
                    if (ni != null && vlt != null && vlt.creditBound(p, g.id, g.amount)) {
                        got.add("挂机 " + ni.displayName(g.id) + " ×" + g.amount + "（进仓库 · 账号绑定）");
                        done = true;
                    } else waiting++; // stays pending (vault off / at cap) — retried on the next delivery
                    break;
                }
                case SIGMARK: { // D174 首领徽记: an account counter like the forge marks
                    d.addPeriodCount(EmberSignature.C_MARK + g.id, "all", g.amount);
                    got.add(g.id.toUpperCase(Locale.ROOT) + " 首领徽记 " + g.amount + "（共 " + d.periodCount(EmberSignature.C_MARK + g.id, "all") + "）");
                    if ("fc_sigmark".equals(r.key)) { // D174: the first clear of a signature map also announces its new unlock
                        String un = EmberSignature.IMPRINT_UNLOCK.equals(g.id) ? "烬炉烙印（用徽记把签名烙到自己的件上）"
                                : EmberSignature.DUAL_UNLOCK.equals(g.id) ? "双签名（刃 + 护符两条签名同时生效）"
                                : EmberSignature.ALT_UNLOCK.equals(g.id) ? "签名调律（7 件签名多一个换代价的调律版，" + EmberSignature.ALT_MARKS + " 枚那张图的徽记解锁）+ Q07 签名"
                                : "签名传奇（重打这张图掉它首领的签名件和徽记）";
                        town.sunshine.corerpg.ConfirmTokens.sendButton(p, P + "§6新解锁：§e" + un + " ", "[打开签名页]", "/corerpg p1 sig menu", "签名图鉴、首领徽记、烙印、开关签名");
                        p.sendMessage(P + "§7不用打命令：主菜单 → 装备 → 签名传奇"); // stage 1.5 one-line hint
                        String mode = modeUnlock(g.id); // D174 stage 2b: Q04 / Q05 / Q06 first clears also open a mode
                        if (mode != null) {
                            town.sunshine.corerpg.ConfirmTokens.sendButton(p, P + "§6新模式：§e" + mode + " ", "[打开进阶模式]", "/corerpg p1 modes", "首领残响 / 连战·前哨 / 自选誓约");
                            p.sendMessage(P + "§7不用打命令：主菜单 → 冒险 → 进阶模式");
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
                    String res = giveItem(p, g, sc == null ? 0 : sc);
                    if (res != null) {
                        got.add(res); done = true; itemsGiven.add(g.id);
                        if (sc != null && !res.endsWith("（已在背包）")) stamped.add(g.id);
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
        if (!got.isEmpty() && !quietDeliver) p.sendMessage(P + "§a结算到账：§f" + String.join("§7、§f", got));
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
    private String giveItem(Player p, EmberRunRules.Grant g, int sigCode) {
        EmberRunRules.ItemRoll it = g.item;
        String src = g.key.startsWith("fc_") || g.key.startsWith("starter_") ? "quest" : "drop";
        EmberItemData d = new EmberItemData(g.id, EmberItemData.templateId(it.tier == 0 ? "none" : it.family, it.slot, it.tier),
                it.tier == 0 ? "none" : it.family, it.slot, it.tier, it.quality, it.craft, 0, 0, true, src,
                EmberItemData.DATA_VERSION, 0, 0, 0, sigCode, 0); // D208: the settlement signature lives in the signed data
        for (ItemStack s : p.getInventory().getContents()) {
            if (s == null || !loadouts.items().hasData(s)) continue;
            EmberItems.Read r = loadouts.items().read(s);
            if (r != null && r.data != null && g.id.equals(r.data.uid)) return d.shortLabel() + "（已在背包）";
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
            if (!p.isOnline() || blocksLegacy(p.getWorld())) return;
            deliver(p);
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

    /** D142 深渊行者: this player's segment fee */
    int feeFor(Player p, int fee) {
        EmberGrowthService g = EmberGrowthService.get();
        return g == null ? fee : g.abyssFee(p, fee);
    }

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
        if (d.def.raid && d.s.open() && !livingIn(d.s).isEmpty()) { // D106
            tellRun(d.s, "§c" + p.getName() + " 倒下 §7· 下一次复活：§e" + nextReviveText(d.s));
            watchLater(p, d.s, 10L, 3); // DP keeps the fallen raider in the instance (dead state) — put the camera on a teammate
        }
    }

    /** D106: once the fallen raider is back on their feet (DP auto-respawn), switch to watching a teammate. */
    private void watchLater(final Player p, final EmberRunSession rs, long delay, final int tries) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline() || !rs.open() || !rs.died.contains(p.getUniqueId()) || rs.world == null
                    || !rs.world.equals(p.getWorld().getName())) return;
            if (p.isDead()) { if (tries > 0) watchLater(p, rs, 20L, tries - 1); return; }
            watchTeammate(p, rs, true);
        }, delay);
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
            "target", "marks", "firstclear", "claim", "run", "runs", "enter", "abyss", "recruit", "watch", "season", "goals", "equip", "route", "fest", "国庆", "rush", "pledge", "modes"));

    public boolean cmd(CommandSender s, String sub, String[] args) {
        switch (sub) {
            case "fest":
            case "国庆":
                if (festival == null) { s.sendMessage(P + "活动未加载"); return true; }
                return festival.command(s, args); // D139
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
                if (args.length >= 4 && "force".equalsIgnoreCase(args[args.length - 1])) forcedReady.add(((Player) s).getUniqueId()); // D96
                return tryEnter((Player) s, args[2].toLowerCase(Locale.ROOT), args.length >= 4 && isChallengeWord(args[3]));
            case "abyss": return cmdAbyss(s, args);
            case "recruit": return cmdRecruit(s, args); // D104 (midgame #5)
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
            case "pledge": return cmdPledge(s, args); // D174 stage 2b
            case "modes": // D174 stage 2b: the 进阶模式 page (首领残响 / 连战·前哨 / 自选誓约)
                if (s instanceof Player) openMenuFor((Player) s, "ember_p1_modes");
                return true;
            default:
                return cmdRuns(s, args);
        }
    }

    // ------------------------------------------------------------------ D174 stage 2b 自选誓约 (Q06)

    static final String C_PLEDGE = "p1_pledge_"; // + rule id, period all: 1 = the player pledges this rule on the repeat normal runs they lead
    static final String PLEDGE_UNLOCK = "q06";

    /** D174 stage 2b: the mode a map's first clear opens (null = none yet live) */
    static String modeUnlock(String mapKey) {
        if (PLEDGE_UNLOCK.equals(mapKey)) return "自选誓约（重打已首通的 Q01–Q07 普通版时自己挂规则，每条 +1 本图徽记）";
        if ("q04".equals(mapKey)) return "首领残响（每周 3 次单独再打 Q01–Q04 的强化首领，每次 2 枚那张图的徽记）";
        if ("q05".equals(mapKey)) return "连战·前哨（Q01→Q02→Q03 三首领连战，每周领一次：每张图 2 枚徽记 + 1 枚 T2 印记）";
        return null;
    }

    private void openMenuFor(Player p, String menu) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (p.isOnline()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "trmenu open " + menu + " " + p.getName());
        });
    }

    /** the pledged rules that count: still in the pool (normal: true), config order */
    List<EmberRunMaps.Modifier> pledged(PlayerData d) {
        List<EmberRunMaps.Modifier> out = new ArrayList<EmberRunMaps.Modifier>();
        if (d == null || maps == null) return out;
        for (EmberRunMaps.Modifier m : maps.pledgePool()) if (d.periodCount(C_PLEDGE + m.id, "all") > 0) out.add(m);
        return out;
    }

    /** settlement: how many pledged rules the session carried (pool members only — a rule dropped from the pool pays nothing) */
    int pledgeCount(String modifier) {
        int n = 0;
        if (maps == null) return 0;
        for (String id : EmberRunMaps.pledgeIds(modifier)) for (EmberRunMaps.Modifier m : maps.pledgePool()) if (m.id.equals(id)) n++;
        return n;
    }

    /**
     * the session modifier for the leader's pledge, or null: leader's own Q06 first clear, a repeat NORMAL run of a
     * signature map (Q01–Q07 — the pledge pays that map's insignia), every member already first-cleared it.
     */
    String pledgeKey(Player leader, EmberRunMaps.MapDef m, List<Player> party) {
        PlayerData d = data(leader.getUniqueId());
        if (d == null || !progressFlag(d, PLEDGE_UNLOCK) || !EmberSignature.hasMap(m.key)) return null;
        List<EmberRunMaps.Modifier> l = pledged(d);
        if (l.isEmpty()) return null;
        for (Player p : party) if (!firstCleared(data(p.getUniqueId()), m)) {
            leader.sendMessage(P + "§7自选誓约这局不生效：" + p.getName() + " 还没首通 " + m.key.toUpperCase(Locale.ROOT) + "（首通保持原样）");
            return null;
        }
        StringBuilder b = new StringBuilder(EmberRunMaps.PLEDGE);
        for (int i = 0; i < l.size(); i++) b.append(i == 0 ? "" : "+").append(l.get(i).id);
        return b.toString();
    }

    /** /corerpg p1 pledge [toggle &lt;id&gt; | off | list] — bare = the menu */
    private boolean cmdPledge(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        PlayerData d = data(p.getUniqueId());
        String op = args.length > 2 ? args[2].toLowerCase(Locale.ROOT) : "";
        if (op.isEmpty()) { openMenuFor(p, "ember_p1_pledge"); return true; }
        if (d == null) return true;
        if ("list".equals(op)) {
            p.sendMessage(P + "§d自选誓约 §7— " + pledgeHead(d));
            for (EmberRunMaps.Modifier m : maps.pledgePool())
                p.sendMessage(P + (d.periodCount(C_PLEDGE + m.id, "all") > 0 ? "§a● " : "§8○ ") + "§f" + m.name + " §7" + m.text);
            return true;
        }
        if (!progressFlag(d, PLEDGE_UNLOCK)) { p.sendMessage(P + ChatColor.RED + "自选誓约需本人首通 Q06 霜封哨所。"); return true; }
        if ("off".equals(op)) {
            for (EmberRunMaps.Modifier m : maps.pledgePool()) if (d.periodCount(C_PLEDGE + m.id, "all") > 0) d.addPeriodCount(C_PLEDGE + m.id, "all", -d.periodCount(C_PLEDGE + m.id, "all"));
            flushData(p.getUniqueId());
            p.sendMessage(P + "§7自选誓约已全部取消。");
            return true;
        }
        if ("toggle".equals(op) && args.length > 3) {
            String id = args[3].toLowerCase(Locale.ROOT);
            EmberRunMaps.Modifier pick = null;
            for (EmberRunMaps.Modifier m : maps.pledgePool()) if (m.id.equals(id)) pick = m;
            if (pick == null) { p.sendMessage(P + ChatColor.RED + "没有这条可挂的规则：" + id); return true; }
            int cur = d.periodCount(C_PLEDGE + id, "all");
            d.addPeriodCount(C_PLEDGE + id, "all", cur > 0 ? -cur : 1);
            flushData(p.getUniqueId());
            p.sendMessage(P + (cur > 0 ? "§7已取消誓约「" + pick.name + "」" : "§d已挂上誓约「" + pick.name + "」§7" + pick.text) + " · " + pledgeHead(d));
            return true;
        }
        p.sendMessage(P + "用法：/corerpg p1 pledge（打开誓约页）· toggle <规则> · off · list");
        return true;
    }

    /** %corerpg_p1_pledge_head|ok|s_&lt;id&gt;|n_&lt;id&gt;|t_&lt;id&gt;% for ember_p1_pledge / ember_p1_modes */
    String pledgePapi(PlayerData d, String k) {
        if (d == null) return "";
        if ("head".equals(k)) return pledgeHead(d);
        if ("ok".equals(k)) return progressFlag(d, PLEDGE_UNLOCK) ? "1" : "0";
        if (k.length() > 2 && k.charAt(1) == '_') {
            EmberRunMaps.Modifier m = null;
            for (EmberRunMaps.Modifier x : maps.pledgePool()) if (x.id.equals(k.substring(2))) m = x;
            if (m == null) return "";
            boolean on = d.periodCount(C_PLEDGE + m.id, "all") > 0;
            switch (k.charAt(0)) {
                case 's': return on ? "§a● 已挂上（左键取消）" : "§8○ 未挂（左键挂上）";
                case 'n': return m.name;
                case 't': return m.text;
                default: return "";
            }
        }
        return "";
    }

    String pledgeHead(PlayerData d) {
        List<EmberRunMaps.Modifier> l = pledged(d);
        if (!progressFlag(d, PLEDGE_UNLOCK)) return "§8首通 Q06 后开放";
        if (l.isEmpty()) return "§7现在没挂规则（重打按原样）";
        StringBuilder b = new StringBuilder();
        for (EmberRunMaps.Modifier m : l) b.append(b.length() == 0 ? "" : "+").append(m.name);
        return "§d已挂：" + b + " §7· 你当队长重打已首通的 Q01–Q07 普通版时生效，每条 +1 本图徽记（本周精选图的周规则那天优先）";
    }

    /** D144 /corerpg p1 rush [go] — the rule, this week's entry, the weekly fastest board; go = enter. */
    private boolean cmdRush(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        EmberRunMaps.MapDef m = maps.rush.isEmpty() ? null : maps.rush.values().iterator().next();
        if (args.length >= 3 && maps.rush.containsKey(args[2].toLowerCase(Locale.ROOT))) { // D174 stage 2b: /corerpg p1 rush <entry> [go]
            m = maps.rush.get(args[2].toLowerCase(Locale.ROOT));
            String[] a = new String[args.length - 1];
            a[0] = args[0]; a[1] = args[1];
            System.arraycopy(args, 3, a, 2, args.length - 3);
            args = a;
        }
        if (m == null) { p.sendMessage(P + "余烬连战未配置。"); return true; }
        if (args.length >= 3 && ("go".equalsIgnoreCase(args[2]) || "enter".equalsIgnoreCase(args[2]))) return tryEnter(p, m.key);
        PlayerData d = data(p.getUniqueId());
        if (!m.mainRush()) { // D174 stage 2b 首领残响 / 连战·前哨: the rule + this week's claims (no time board)
            p.sendMessage(P + "§c" + m.rushLabel + " §7— " + rushChainText(m) + (m.chain.size() > 1 ? "，同一个大厅连打" : "，单独在大厅里打") + "（招式和各自主线图一样）");
            p.sendMessage(P + "§7首领生命 ×" + fmt2(m.rushHp) + "、伤害 ×" + fmt2(m.rushDmg) + "（组队另按人数加生命）· 1～" + maps.partyMax(m) + " 人 · 不耗体力 · 倒下观战，没有复活");
            p.sendMessage(P + "§e" + rushRuleText(m) + " §7· 奖励：" + rushRewardText(m) + " · 不给余烬币、装备、碎片，也不算每日委托");
            if (!progressFlag(d, m.requires)) { p.sendMessage(P + "§c需本人首通 " + m.requires.toUpperCase(Locale.ROOT)); return true; }
            int used = rushWeek(d, p.getUniqueId(), m);
            p.sendMessage(P + (EmberRunRules.rushPaysReward(used, m.rushWeekly) ? "§a本周已领 " : "§8本周已领 ") + used + "/" + m.rushWeekly);
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, new String[]{EmberRunRules.rushPaysReward(used, m.rushWeekly) ? "[开始]" : "[练习一局]",
                    "/corerpg p1 rush " + m.key + " go", "组队时由队长开；失败不扣任何东西，可以无限重试", EmberRunRules.rushPaysReward(used, m.rushWeekly) ? "RED" : "GRAY"});
            return true;
        }
        p.sendMessage(P + "§c余烬连战 §7— " + rushChainText(m) + "，同一个大厅连打（招式和各自主线图一样）");
        p.sendMessage(P + "§7首领生命 ×" + fmt2(m.rushHp) + "、伤害 ×" + fmt2(m.rushDmg) + "（组队另按人数加生命）· 1～" + maps.partyMax(m) + " 人 · 不耗体力"
                + " · 每打倒一个休息 " + Math.round(m.rushBreak) + " 秒、回复 " + Math.round(m.rushHeal * 100) + "% 生命 · 倒下观战，没有复活");
        p.sendMessage(P + "§e每周首通领奖，失败可无限重试；已领奖后可练习（无奖励）§7 · 失败不扣体力、不算次数");
        p.sendMessage(P + "§7本周奖励：T3 印记 +" + m.rushMarks + " · 余烬徽 +" + m.rushBadges + " · 第一次通关得称号「" + (EmberCosmetics.byId(m.rushTitle) == null ? m.rushTitle : EmberCosmetics.byId(m.rushTitle).label)
                + "§7」· 不给余烬币、装备、碎片，也不算每日委托 · 本周最快榜（练习局也算成绩；赛季末前 3 名得「赛季疾行者」）");
        if (!progressFlag(d, m.requires)) { p.sendMessage(P + "§c需本人首通 " + m.requires.toUpperCase(Locale.ROOT)); return true; }
        int used = rushWeek(d, p.getUniqueId());
        p.sendMessage(P + (EmberRunRules.rushPaysReward(used, RUSH_WEEKLY) ? "§a" : "§8") + EmberRunRules.rushWeekText(used, RUSH_WEEKLY));
        if (season != null) {
            List<EmberSeason.Row> rows = season.weekTop("time_rush", 5);
            StringBuilder b = new StringBuilder("§e本周最快§7：");
            if (rows.isEmpty()) b.append("暂无");
            for (int i = 0; i < rows.size(); i++) b.append(i == 0 ? "" : " ｜ ").append("§f").append(i + 1).append(". ").append(rows.get(i).name).append(" §7").append(EmberSeason.rowText("time_rush", rows.get(i)));
            int[] me = season.weekRank(p.getUniqueId(), "time_rush");
            b.append(me != null ? " §8（你：第 " + me[0] + " 名）" : "");
            p.sendMessage(P + b);
        }
        town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, EmberRunRules.rushPaysReward(used, RUSH_WEEKLY)
                ? new String[]{"[开始连战]", "/corerpg p1 rush go", "组队时由队长开；失败不扣任何东西，可以无限重试", "RED"}
                : new String[]{"[练习一局]", "/corerpg p1 rush go", "本周奖励已领：这局无奖励，成绩计入本周最快榜", "GRAY"});
        return true;
    }

    private static String fmt2(double v) { return String.format(Locale.ROOT, "%.2f", v); }

    /** /corerpg p1 abyss [层] — without a tier: the table and this character's state; with one: start that segment. */
    private boolean cmdAbyss(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        PlayerData d = data(p.getUniqueId());
        if (maps.abyss.isEmpty() || maps.challenge == null) { p.sendMessage(P + "深渊 · 余烬层未配置。"); return true; }
        if (args.length >= 3) {
            int t;
            try { t = Integer.parseInt(args[2]); } catch (NumberFormatException e) { t = "next".equalsIgnoreCase(args[2]) ? abyssMaxStart(d) : -1; }
            return tryEnterAbyss(p, t);
        }
        p.sendMessage(P + "§5深渊 · 余烬层 §7— 每层 = 随机一张主线图打一局（3 房 + 首领），打赢第 N 层开放第 N+1 层；共 "
                + maps.abyss.size() + " 层封顶");
        p.sendMessage(P + "§7每层单独确认：" + maps.cost + " 体力 + 层费（余烬币）；没开打就退出（含服务器重启）全额退还；打完首领才结算，失败只丢这一层的花费（每天第一次失败退一半体力，层费不退），不掉装备不降强化");
        if (!abyssOpen(d)) { p.sendMessage(P + "§c需本人首通 " + maps.abyssRequires.toUpperCase(Locale.ROOT)); return true; }
        p.sendMessage(P + "最高通关 第 " + abyssBest(d) + " 层 · 可开 1～" + abyssMaxStart(d) + " 层 · 余烬币 " + d.getCoin());
        for (EmberRunMaps.AbyssTier t : maps.abyss) p.sendMessage(P + abyssLine(d, t));
        p.sendMessage(P + "§7开始：冒险页 → 深渊，点要下潜的层");
        return true;
    }

    /** D101: a player's name for messages (offline players too), never a raw uuid */
    static String nameOf(UUID u) {
        org.bukkit.OfflinePlayer op = Bukkit.getOfflinePlayer(u);
        String n = op == null ? null : op.getName();
        return n == null ? "（未知玩家）" : n;
    }

    String abyssLine(PlayerData d, EmberRunMaps.AbyssTier t) {
        String st = t.index <= abyssBest(d) ? "§a已通关" : t.index <= abyssMaxStart(d) ? "§e可开" : "§8未开放";
        return "§d第 " + t.index + " 层 " + st + " §7· 生命 ×" + String.format(Locale.ROOT, "%.2f", t.hp) + " 伤害 ×"
                + String.format(Locale.ROOT, "%.2f", t.dmg) + " · 掉落成色 " + qualityLabel(t.quality) + " · 费 " + t.fee + " 币"
                + (t.index == 1 ? " §8（= 挑战版强度：挑战版能过就能下）" : t.index == maps.abyss.size() ? " §8（两件 T3 +10、卓越以上再来）" : "");
    }

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
                EmberItemData.DATA_VERSION, 0);
        final EmberPay.Price price = EmberPay.Price.marks(tier, EmberRunRules.MARKS_PER_EXCHANGE);
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

    /** D96: leaders who clicked [仍然进入] skip the readiness warning once */
    private final Map<UUID, Long> recruitAt = new java.util.concurrent.ConcurrentHashMap<UUID, Long>();

    /**
     * D104 (midgame #5): /corerpg p1 recruit <r01|r02|r03> — the leader (a DP team is created if needed) sends every online
     * player with their own Q07 first clear a clickable call; /corerpg p1 recruit join <leader> sends the DP join request
     * and gives the leader a clickable [同意]. 60 s cooldown per leader.
     */
    private boolean cmdRecruit(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        if (args.length >= 4 && "join".equalsIgnoreCase(args[2])) {
            Player l = Bukkit.getPlayerExact(args[3]);
            if (l == null || l.equals(p)) { p.sendMessage(P + ChatColor.RED + "队长不在线。"); return true; }
            if (EmberRunBridges.hasTeam(p)) { p.sendMessage(P + ChatColor.RED + "你已经在一支队伍里了，先退出再申请。"); return true; }
            // E-review #5: DP's own line already carries [同意] [拒绝] — no second [同意] from us
            p.performCommand("dungeon-team request join " + l.getName());
            return true;
        }
        if (args.length >= 3 && "list".equalsIgnoreCase(args[2])) { showRecruits(p, true); return true; }
        String key = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "r01";
        EmberRunMaps.MapDef m = maps.byKey(key);
        if (m == null || !m.raid) { p.sendMessage(P + "/corerpg p1 recruit <r01|r02|r03> — 全服招募团本队员"); return true; }
        if (!progressFlag(data(p.getUniqueId()), m.requires)) { p.sendMessage(P + ChatColor.RED + "先首通 " + m.requires.toUpperCase(Locale.ROOT) + " 才能开团本。"); return true; }
        if (!EmberRunBridges.teamLeader(p)) { p.sendMessage(P + ChatColor.RED + "只有队长能招募。"); return true; }
        long now = System.currentTimeMillis();
        Long last = recruitAt.get(p.getUniqueId());
        if (last != null && now - last < 60_000L) { p.sendMessage(P + ChatColor.RED + "招募 60 秒内只能发一次（还剩 " + (60 - (now - last) / 1000) + " 秒）。"); return true; }
        if (!EmberRunBridges.hasTeam(p)) p.performCommand("dungeon-team create");
        recruitAt.put(p.getUniqueId(), now);
        List<UUID> team = EmberRunBridges.teamMembers(p);
        int need = Math.max(0, maps.partyMin(m) - team.size());
        String line = P + "§6" + p.getName() + " §f招 §e" + m.name + " §f队员（现在 " + team.size() + " 人"
                + (need > 0 ? "，还差 " + need + " 人开本" : "") + "；人越多越稳，最多 5 人）" + (m.partyHint.isEmpty() ? "" : " §e" + m.partyHint) + " ";
        recruits.put(p.getUniqueId(), new Recruit(p.getUniqueId(), p.getName(), m.key, m.name, now)); // E-review #5: the board
        int sent = 0;
        for (Player o : Bukkit.getOnlinePlayers()) {
            if (o.equals(p) || team.contains(o.getUniqueId())) continue;
            if (!progressFlag(data(o.getUniqueId()), m.requires)) continue;
            town.sunshine.corerpg.ConfirmTokens.sendButtons(o, line,
                    new String[]{"[申请入队]", "/corerpg p1 recruit join " + p.getName(), "向队长申请；队长同意后入队", "GREEN"});
            sent++;
        }
        p.sendMessage(P + (sent > 0 ? "§a已向 " + sent + " 位已首通 " + m.requires.toUpperCase(Locale.ROOT) + " 的在线玩家发出招募；有人申请时会出现 [同意] [拒绝]。"
                : "§7现在没有其他已首通 " + m.requires.toUpperCase(Locale.ROOT) + " 的玩家在线。") + "§7招募挂在冒险页团本图标上 10 分钟，之后上线的人也看得到。");
        return true;
    }

    // ------------------------------------------------------------------ E-review #5: recruit board (10 minutes)

    static final long RECRUIT_TTL = 10 * 60_000L;

    private static final class Recruit {
        final UUID leader; final String name, raid, raidName; final long at;
        Recruit(UUID leader, String name, String raid, String raidName, long at) { this.leader = leader; this.name = name; this.raid = raid; this.raidName = raidName; this.at = at; }
    }
    private final Map<UUID, Recruit> recruits = new java.util.concurrent.ConcurrentHashMap<UUID, Recruit>();

    /** live board entries: younger than 10 min, leader online, still leading a team that is not full and not in a run */
    private List<Recruit> liveRecruits() {
        long now = System.currentTimeMillis();
        List<Recruit> out = new ArrayList<Recruit>();
        for (Recruit r : new ArrayList<Recruit>(recruits.values())) {
            Player l = Bukkit.getPlayer(r.leader);
            boolean ok = now - r.at < RECRUIT_TTL && l != null && l.isOnline() && !blocksLegacy(l.getWorld())
                    && EmberRunBridges.hasTeam(l) && EmberRunBridges.teamLeader(l) && EmberRunBridges.teamMembers(l).size() < 5;
            if (ok) out.add(r); else recruits.remove(r.leader);
        }
        out.sort((a, b) -> Long.compare(b.at, a.at));
        return out;
    }

    private String recruitText(Recruit r) {
        Player l = Bukkit.getPlayer(r.leader);
        int n = l == null ? 1 : EmberRunBridges.teamMembers(l).size();
        EmberRunMaps.MapDef m = maps.byKey(r.raid);
        int min = m == null ? 3 : maps.partyMin(m);
        long mins = (System.currentTimeMillis() - r.at) / 60_000L;
        return r.raid.toUpperCase(Locale.ROOT) + " · " + r.name + " · " + n + "/" + min + (n >= min ? "（可开本）" : "") + " · " + (mins == 0 ? "刚刚" : mins + " 分钟前");
    }

    /** %corerpg_p1_recruits%: up to two board entries for the adventure icons */
    public String recruitsLabel() {
        List<Recruit> live = liveRecruits();
        if (live.isEmpty()) return "暂无（右键发一个）";
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < Math.min(2, live.size()); i++) b.append(i == 0 ? "" : " ｜ ").append(recruitText(live.get(i)));
        return b + (live.size() > 2 ? " 等 " + live.size() + " 个" : "");
    }

    /** chat list with [申请入队] (players with their own Q07 first clear, not already in a team) */
    void showRecruits(Player p, boolean tellEmpty) {
        List<Recruit> live = liveRecruits();
        if (live.isEmpty()) { if (tellEmpty) p.sendMessage(P + "现在没有团本在招人。冒险页团本图标右键可以自己发一个。"); return; }
        p.sendMessage(P + "§6团本招募板§7（挂 10 分钟）：");
        for (Recruit r : live) {
            if (r.leader.equals(p.getUniqueId())) { p.sendMessage(P + "  §7" + recruitText(r) + "（你的招募）"); continue; }
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P + "  §f" + recruitText(r) + " ",
                    new String[]{"[申请入队]", "/corerpg p1 recruit join " + r.name, "向队长申请；队长同意后入队", "GREEN"});
        }
    }

    /** E-review #5: players with a Q07 first clear see open recruits when they log in */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoinRecruits(org.bukkit.event.player.PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        if (season != null) Bukkit.getScheduler().runTaskLater(plugin, () -> { if (p.isOnline()) season.apply(p); }, 60L); // D116
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!p.isOnline() || !progressFlag(data(p.getUniqueId()), "q07") || liveRecruits().isEmpty()) return;
            showRecruits(p, false);
        }, 100L);
    }

    /** E-review #5: DP says nothing to a leader who refused an application ([拒绝] runs request unaccept <name>) */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRequestRefused(org.bukkit.event.player.PlayerCommandPreprocessEvent e) {
        String[] a = e.getMessage().replaceFirst("^/", "").trim().split("\\s+");
        if (a.length < 4 || !a[0].toLowerCase(Locale.ROOT).endsWith("dungeon-team") || !"request".equalsIgnoreCase(a[1])
                || !"unaccept".equalsIgnoreCase(a[2])) return;
        e.getPlayer().sendMessage(P + "已拒绝 " + a[3] + " 的入队申请。");
    }

    private final java.util.Set<UUID> warnedT3 = java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<UUID, Boolean>()); // D104
    private final java.util.Set<UUID> forcedReady = java.util.Collections.newSetFromMap(new java.util.concurrent.ConcurrentHashMap<UUID, Boolean>());

    /** admin test hook: the next started run uses this extra event instead of the seeded roll (one shot). */
    private volatile EmberRunRules.Extra forcedExtra;
    /** P2-8 admin test hook: the next challenge run uses this weekly rule (one shot). */
    private volatile EmberRunMaps.Modifier forcedModifier;
    /** D138 admin test hook: the next normal run gets this affix / event ("blazing:r2", "event:r1"; one shot). */
    private volatile String forcedVariety;

    private boolean cmdRuns(CommandSender s, String[] args) {
        boolean admin = s.hasPermission("corerpg.admin");
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        if (admin && "modifier".equals(op) && args.length >= 4) { // P2-8 test hook: next challenge run (any map) uses this rule
            forcedModifier = "clear".equalsIgnoreCase(args[3]) ? null : maps.modifier(args[3]);
            s.sendMessage(P + "下一局规则（挑战或精选图普通版，仅一次，测试用）= " + (forcedModifier == null ? "按周" : forcedModifier.id));
            return true;
        }
        if (admin && "variety".equals(op) && args.length >= 4) { // D138/D171/D181/D189/D196: blazing|…|frost|mortar|molten|venom|jailer|arcane|firechain|timed|crystal|escort|event[:rN]|clear
            forcedVariety = "clear".equalsIgnoreCase(args[3]) ? null : args[3].toLowerCase(Locale.ROOT);
            s.sendMessage(P + "下一局普通版花样（仅一次，测试用）= " + (forcedVariety == null ? "按种子" : forcedVariety));
            return true;
        }
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
                td.addPeriodCount(EmberSeason.C_BADGE, "all", Integer.parseInt(args[5]));
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
        if (admin && "firstclear".equals(op) && args.length >= 5) {
            Player t = Bukkit.getPlayerExact(args[3]);
            EmberRunMaps.MapDef m = maps.byKey(args[4]);
            if (t == null || m == null) { s.sendMessage(P + "玩家不在线或地图未知"); return true; }
            PlayerData d = data(t.getUniqueId());
            boolean clear = args.length >= 6 && "clear".equalsIgnoreCase(args[5]);
            EmberFirstClear.setBoth(d, m.key, m.contentVersion, !clear); // D205: fact @all + package @ver
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
                    + " 体力 · 掉落 " + m.dropLabel + "（偏向 " + EmberRunMaps.lootLabel(m) + "）· 首通：" + m.firstClearLabel());
        }
        String t = target(d);
        p.sendMessage(P + "挑战版（七图）：" + (challengeOpen(d) ? "§a已开放 §7· 冒险页「挑战版」选图 · 掉落 T3"
                : "§7需本人首通 " + (maps.challenge == null ? "Q07" : maps.challenge.requires.toUpperCase(Locale.ROOT))));
        for (EmberRunMaps.MapDef rm : maps.raids.values())
            town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P + "§6团本 " + rm.key.toUpperCase(Locale.ROOT) + " " + rm.name + " §7" + raidLabel(d, rm) + " ",
                    new String[]{"[开本]", "/corerpg p1 enter " + rm.key, "队长点：全队需各自首通 Q07，3～5 人", "GOLD"});
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

    public String placeholder(Player p, String key) {
        if (p == null || maps == null) return "";
        PlayerData d = data(p.getUniqueId());
        if (key.startsWith("pass_")) return hasPass(p.getUniqueId(), key.substring(5)) ? "yes" : "no";
        if ("target".equals(key)) { String t = target(d); return t == null ? "未选择" : EmberItemData.familyName(t); }
        if (key.startsWith("marks_t")) {
            try { return String.valueOf(marks(d, Integer.parseInt(key.substring(7)))); } catch (NumberFormatException e) { return "0"; }
        }
        if ("pending".equals(key)) { // D93: a first-clear choice waiting for a click is not "stuck in storage"
            int n = 0;
            for (EmberRunRules.Row r : store.ledger(p.getUniqueId()).open()) if (!EmberRunRules.ST_AWAIT.equals(r.status)) n++;
            return String.valueOf(n);
        }
        if ("active".equals(key)) return EmberMode.active() ? "yes" : "no";
        if ("vbounty".equals(key)) { String l = varietyBountyLine(d); return l.isEmpty() ? "—" : l; } // D144 花样委托
        if ("rush".equals(key)) { // D144 余烬连战 menu line
            if (maps.rush.isEmpty()) return "未配置";
            if (!progressFlag(d, "q07")) return "§8需本人首通 Q07";
            int used = rushWeek(d, p == null ? null : p.getUniqueId()); // D160
            return EmberRunRules.rushPaysReward(used, RUSH_WEEKLY) ? "§a本周奖励未领 · 失败可无限重试" : "§7本周奖励已领 · 可练习（无奖励）";
        }
        if (key.startsWith("pledge_")) return pledgePapi(d, key.substring(7)); // D174 stage 2b 自选誓约
        if (key.startsWith("rush_") && maps.rush.containsKey(key.substring(5))) { // D174 stage 2b: %corerpg_p1_rush_<entry>% menu line
            EmberRunMaps.MapDef rm = maps.rush.get(key.substring(5));
            if (!progressFlag(d, rm.requires)) return "§8需本人首通 " + rm.requires.toUpperCase(Locale.ROOT);
            for (String ck : rm.chainKeys) if (!progressFlag(d, ck)) return "§8需本人首通 " + ck.toUpperCase(Locale.ROOT);
            int used = rushWeek(d, p.getUniqueId(), rm);
            return EmberRunRules.rushPaysReward(used, rm.rushWeekly) ? "§a本周已领 " + used + "/" + rm.rushWeekly + " · 失败可无限重试" : "§7本周 " + rm.rushWeekly + " 次已领完 · 可练习（无奖励）";
        }
        if (key.startsWith("passd_")) { // D174 stage 2b: entries sharing one DP hall dungeon check the pass by dungeon
            Object[] ps = passes.get(p.getUniqueId());
            EmberRunMaps.MapDef pm = ps == null || System.currentTimeMillis() > (Long) ps[1] ? null : maps.byKey((String) ps[0]);
            return pm != null && pm.dungeon.equalsIgnoreCase(key.substring(6)) ? "yes" : "no";
        }
        if (key.startsWith("sign_")) { EmberSignService g = EmberSignService.get(); return g == null ? "" : g.signPapi(p, d, key.substring(5)); } // D180
        if (key.startsWith("online_")) { EmberSignService g = EmberSignService.get(); return g == null ? "" : g.onlinePapi(p, d, key.substring(7)); } // D180
        if (key.startsWith("afk_")) { EmberAfkService a = EmberAfkService.get(); return a == null ? "" : a.papi(p, d, key.substring(4)); } // D177
        if (key.startsWith("sig_")) { EmberGrowthService g = EmberGrowthService.get(); return g == null ? "" : g.sigPapi(p, d, key.substring(4)); } // D174 stage 1.5
        if (key.startsWith("reroll_")) { EmberGrowthService g = EmberGrowthService.get(); return g == null ? "" : g.rerollPapi(p, d, key.substring(7)); } // D143
        if (key.startsWith("honor_")) { EmberGrowthService g = EmberGrowthService.get(); return g == null ? "" : g.honorPapi(p, d, key.substring(6)); } // D142
        if (key.startsWith("spec_")) { EmberGrowthService g = EmberGrowthService.get(); return g == null ? "" : g.papi(p, d, key.substring(5)); } // D141
        if ("forge_t2".equals(key)) return progressFlag(d, "q04") ? "已开放" : "需本人首通 Q04";
        if ("forge_t3".equals(key)) return progressFlag(d, "q07") ? "已开放" : "需本人首通 Q07";
        if ("challenge".equals(key)) return challengeOpen(d) ? "已开放" : "需本人首通 Q07";
        if ("q07done".equals(key)) return progressFlag(d, "q07") ? "1" : "0"; // D99 menu condition (post-Q07 icons)
        if (key.length() == 7 && key.startsWith("q0") && key.endsWith("done")) return progressFlag(d, key.substring(0, 3)) ? "1" : "0"; // D174 stage 2b: q04done / q05done / q06done
        if ("featured".equals(key)) return featuredLabel(d); // P2-1
        if ("modifier".equals(key)) return modifierLabel(); // P2-8
        if (key.startsWith("rule_")) return ruleLine(d, key.substring(5)); // D94
        if (key.startsWith("top_abyss_") || key.startsWith("top_featured_")) { // P2-10 %corerpg_p1_top_abyss_1%
            boolean ab = key.startsWith("top_abyss_");
            int i;
            try { i = Integer.parseInt(key.substring(ab ? 10 : 13)); } catch (NumberFormatException e) { return ""; }
            if (i < 1 || i > 10) return "";
            if (season != null) { // F-review #2: the season week board (same rows as the season page)
                java.util.List<EmberSeason.Row> sr = season.weekTop(ab ? "abyss" : "featured", i);
                return sr.size() < i ? "—" : sr.get(i - 1).name + " · " + EmberSeason.rowText(ab ? "abyss" : "featured", sr.get(i - 1));
            }
            if (top == null) return "";
            java.util.List<EmberLeaderboard.Row> rows = top.top(ab, EmberRunRules.rotationWeekKey(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone())), i);
            if (rows.size() < i) return "—";
            EmberLeaderboard.Row r = rows.get(i - 1);
            return r.name + " · " + (ab ? "第 " + r.value + " 层" : r.value + " 次");
        }
        if (season != null && (key.startsWith("goal") || key.startsWith("season") || key.startsWith("sboard_") || key.startsWith("srank_") || "badges".equals(key))) {
            String v = season.papi(p == null ? null : p.getUniqueId(), d, key); // D116 / D117
            if (v != null) return v;
        }
        if (key.startsWith("shop") && !key.startsWith("shop_") && cosmetics != null) { // F-review #2 (D121)
            String v = cosmetics.papi(p == null ? null : p.getUniqueId(), d, key);
            if (v != null) return v;
        }
        if (key.startsWith("shop_") && cosmetics != null) return cosmetics.shopLabel(p == null ? null : p.getUniqueId(), d, key.substring(5)); // D119
        if (key.startsWith("fest_") && festival != null) { String v = festival.papi(p, d, key.substring(5)); return v == null ? "" : v; } // D139
        if ("title".equals(key)) return cosmetics == null ? "" : cosmetics.titleMenuText(d); // P2-9 %corerpg_p1_title% (D103: never empty)
        if ("honors".equals(key)) return cosmetics == null ? "0/0" : cosmetics.earnedCount(d) + "/" + EmberCosmetics.ALL.size();
        if (key.startsWith("loot_")) { EmberRunMaps.MapDef lm = maps.byKey(key.substring(5)); if (lm == null) lm = maps.raids.get(key.substring(5)); return EmberRunMaps.lootLabel(lm) + lootOdds(d, lm); }
        if ("abyss_best".equals(key)) return String.valueOf(abyssBest(d)); // P2-2
        if ("bounty".equals(key)) return bountyLabel(d); // P2-7 %corerpg_p1_bounty%
        if ("next".equals(key)) return nextStep(d, p.getUniqueId()); // new-player polish %corerpg_p1_next%
        if (key.startsWith("raid_")) { // P2-5 %corerpg_p1_raid_r01%
            EmberRunMaps.MapDef rm = maps.raids.get(key.substring(5));
            return rm == null ? "" : raidLabel(d, rm);
        }
        if ("abyss_state".equals(key)) return maps.abyss.isEmpty() ? "未配置" : !abyssOpen(d) ? "需本人首通 " + maps.abyssRequires.toUpperCase(Locale.ROOT)
                : "最高第 " + abyssBest(d) + " 层 · 可开 1～" + abyssMaxStart(d) + " 层";
        if (key.startsWith("abyss_t")) {
            try {
                EmberRunMaps.AbyssTier t = maps.abyssTier(Integer.parseInt(key.substring(7)));
                return t == null ? "" : abyssLine(d, t);
            } catch (NumberFormatException e) { return ""; }
        }
        if ("recruits".equals(key)) return recruitsLabel(); // E-review #5
        if ("failrefund".equals(key)) return failRefundLabel(p.getUniqueId()); // D128
        if ("featured_key".equals(key)) return String.valueOf(featured(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone())));
        if ("awaken_route".equals(key)) { // D120: cheapest real route (first line)
            List<String> r;
            if (Bukkit.isPrimaryThread()) r = breakthroughRoutes(p, null);
            else try { final Player fp = p; r = Bukkit.getScheduler().callSyncMethod(plugin, () -> breakthroughRoutes(fp, null)).get(750, java.util.concurrent.TimeUnit.MILLISECONDS); }
            catch (Exception e) { return ""; }
            return r.isEmpty() ? "" : "§7路线：" + r.get(0) + (r.size() > 1 ? " §8（还有 " + (r.size() - 1) + " 条，点开看）" : "");
        }
                if ("awaken".equals(key) || "awaken_next".equals(key) || "set_progress".equals(key) || "stats".equals(key)
                || "ehp".equals(key) || "blade".equals(key) || "charm".equals(key)) {
            EmberLoadoutService ls = plugin.getEmberLoadouts();
            EmberLoadout l = ls == null ? null : ls.get(p);
            if (l == null && ls != null) l = ls.refresh(p);
            if (l == null) return "";
            switch (key) {
                case "awaken": return l.setLabel();
                case "set_progress": return l.setProgress();
                // B2.174 §19.1 装备页: actual B / H / D (formula output, §19.2), main hand + selected charm
                case "stats": return String.format(Locale.ROOT, "攻击 %.1f · 生命 %.0f · 防御 %.0f（承伤 ×%.2f）· Lv%d",
                        l.b, l.h, l.d, l.m, l.level);
                case "ehp": return String.format(Locale.ROOT, "%.0f", l.ehp());
                case "blade": return l.blade == null ? "主手没拿余烬刃" : l.blade.shortLabel();
                case "charm": return l.charm == null ? "未选定护符" : l.charm.shortLabel();
                default: return l.nextAwakeningHint();
            }
        }
        if (key.startsWith("codex")) { // B2.180 图录 · 装备 (display only, §19.5)
            EmberLoadoutService ls = plugin.getEmberLoadouts();
            if (ls != null) ls.backfillCodex(p);
            if ("codex_count".equals(key)) return EmberCodex.count(d) + "/" + EmberCodex.ENTRIES.size();
            if (key.startsWith("codex_stage_")) {
                try {
                    int i = Integer.parseInt(key.substring(12));
                    return i >= 0 && i < EmberCodex.STAGE_AT.length ? EmberCodex.stageLabel(d, i) : "";
                } catch (NumberFormatException e) { return ""; }
            }
            if (key.startsWith("codex_")) return EmberCodex.has(d, key.substring(6)) ? "§a已登记" : "§8未获得";
            return "";
        }
        int us = key.indexOf('_');
        if (us > 0) {
            EmberRunMaps.MapDef m = maps.byKey(key.substring(0, us));
            String f = key.substring(us + 1);
            if (m != null) {
                switch (f) {
                    case "state": return stateLabel(d, m);
                    case "open": return unlocked(d, m) ? "yes" : "no";
                    case "cleared": return firstClearDone(d, m) ? "yes" : "no";
                    case "name": return m.name;
                    case "cost": return maps.cost(m) + " 体力";
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
