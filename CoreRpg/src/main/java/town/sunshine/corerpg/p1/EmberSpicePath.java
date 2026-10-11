package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D500: encounter spice path — blaze / control / objective.
 * NEW play change: biases D138 repeat-run variety rolls (affix type or room event),
 * not a combo of existing sticky paths. Chat buttons only.
 * Zero dmg/CD / AFK / sx / stamp / pin price. Not forge spend / enter-card / ActionBar twin.
 */
public final class EmberSpicePath {

    public static final String C_PATH = "p1_spice_path";
    public static final String C_OFFER = "p1_spice_path_offer";

    public static final int NONE = 0;
    /** Prefer pressure affixes (点燃/投弹/亡爆/火链/冲锋) */
    public static final int BLAZE = 1;
    /** Prefer control affixes (凝霜/禁锢/毒十字/旋光/再生/护盾/分裂) */
    public static final int CONTROL = 2;
    /** Prefer room objectives (砸晶/占点/护灯/传火/护兔/裂隙/连斩/无伤) over bare timed */
    public static final int OBJECTIVE = 3;

    static final Set<String> BLAZE_AFFIX = setOf(
            "blazing", "mortar", "molten", "firechain", "charge");
    static final Set<String> CONTROL_AFFIX = setOf(
            "frost", "jailer", "venom", "arcane", "regen", "shield", "split");
    static final Set<String> OBJECTIVE_EVENT = setOf(
            "crystal", "hold", "beacon", "relay", "escort", "breach", "chain", "unscathed");

    private EmberSpicePath() {}

    private static Set<String> setOf(String... ids) {
        Set<String> s = new HashSet<String>();
        Collections.addAll(s, ids);
        return Collections.unmodifiableSet(s);
    }

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == BLAZE || id == CONTROL || id == OBJECTIVE;
    }

    public static boolean set(PlayerData d, int id) {
        if (d == null) return false;
        if (id != NONE && !valid(id)) return false;
        int cur = d.periodCount(C_PATH, "all");
        if (cur == id) return false;
        d.addPeriodCount(C_PATH, "all", id - cur);
        return true;
    }

    public static int parse(String raw) {
        if (raw == null) return -1;
        String s = raw.toLowerCase(Locale.ROOT).trim();
        if ("clear".equals(s) || "none".equals(s) || "off".equals(s) || "取消".equals(s)) return NONE;
        if ("blaze".equals(s) || "pressure".equals(s) || "压火".equals(s) || "火力".equals(s) || "1".equals(s)) return BLAZE;
        if ("control".equals(s) || "ctrl".equals(s) || "控制".equals(s) || "控场".equals(s) || "2".equals(s)) return CONTROL;
        if ("objective".equals(s) || "obj".equals(s) || "目标".equals(s) || "事件".equals(s) || "3".equals(s)) return OBJECTIVE;
        return -1;
    }

    public static String key(int id) {
        if (id == BLAZE) return "blaze";
        if (id == CONTROL) return "control";
        if (id == OBJECTIVE) return "objective";
        return "none";
    }

    public static String label(int id) {
        if (id == BLAZE) return "火力花样";
        if (id == CONTROL) return "控场花样";
        if (id == OBJECTIVE) return "目标花样";
        return "未选";
    }

    public static String tip(int id) {
        if (id == BLAZE) return "重打本偏向点燃/投弹/亡爆/火链/冲锋词缀精英";
        if (id == CONTROL) return "重打本偏向凝霜/禁锢/毒十字/旋光/再生等控场词缀";
        if (id == OBJECTIVE) return "重打本偏向砸晶/占点/护灯/传火等房间目标事件";
        return "点选本周花样遭遇偏好（真改重打本卷轴）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8花样路径：首进/首通 Q02 后可选（重打本花样）";
        if (!valid(id)) return "§8花样路径：未选 · /corerpg p1 spicepath";
        return "§e花样路径：§f" + label(id) + " §8· " + tip(id);
    }

    /**
     * Bias a rolled variety quadruple {@code [affixRoom, affix, eventRoom, eventKind]}.
     * Deterministic for {@code seed}. Never invents ids outside the live pools.
     * Bukkit-free for unit tests.
     */
    public static String[] applyBias(String[] rolled, int spice, List<String> affixPool, List<String> eventPool, long seed) {
        String[] v = new String[4];
        v[0] = rolled != null && rolled.length > 0 && rolled[0] != null ? rolled[0] : "";
        v[1] = rolled != null && rolled.length > 1 && rolled[1] != null ? rolled[1] : "";
        v[2] = rolled != null && rolled.length > 2 && rolled[2] != null ? rolled[2] : "";
        v[3] = rolled != null && rolled.length > 3 && rolled[3] != null ? rolled[3] : (!v[2].isEmpty() ? "timed" : "");
        if (!valid(spice)) return v;
        Random r = new Random(seed);
        if (spice == BLAZE || spice == CONTROL) {
            Set<String> want = spice == BLAZE ? BLAZE_AFFIX : CONTROL_AFFIX;
            List<String> cand = intersect(affixPool, want);
            if (cand.isEmpty()) return v;
            String pick = cand.get(Math.floorMod(r.nextInt(), cand.size()));
            // D2945: always land preferred-family affix when pool intersects (real scroll bias)
            v[0] = v[0].isEmpty() ? roomPick(r) : v[0];
            v[1] = pick;
            return v;
        }
        // OBJECTIVE
        List<String> cand = intersect(eventPool, OBJECTIVE_EVENT);
        if (cand.isEmpty()) cand = eventPool == null ? Collections.<String>emptyList() : eventPool;
        if (cand == null || cand.isEmpty()) return v;
        String pick = cand.get(Math.floorMod(r.nextInt(), cand.size()));
        // D2945: always land objective-family event when pool intersects
        v[2] = v[2].isEmpty() ? roomPick(r) : v[2];
        v[3] = pick;
        return v;
    }

    static List<String> intersect(List<String> pool, Set<String> want) {
        List<String> out = new ArrayList<String>();
        if (pool == null || want == null) return out;
        for (String id : pool) if (id != null && want.contains(id) && !out.contains(id)) out.add(id);
        return out;
    }

    static String roomPick(Random r) {
        String[] rooms = {"r1", "r2", "r3"};
        return rooms[Math.floorMod(r.nextInt(), 3)];
    }


    public static void scheduleOfferAfterEnter(Player p) { scheduleOfferAfterQ02(p); }

    public static boolean matchesAffix(int spice, String affix) {
        if (affix == null || affix.isEmpty()) return false;
        if (spice == BLAZE) return BLAZE_AFFIX.contains(affix);
        if (spice == CONTROL) return CONTROL_AFFIX.contains(affix);
        return false;
    }

    public static boolean matchesEvent(int spice, String eventKind) {
        if (spice != OBJECTIVE || eventKind == null || eventKind.isEmpty()) return false;
        return OBJECTIVE_EVENT.contains(eventKind);
    }

    /** D2926: spice-align cue (no Glow twin — affix already glows). */
    public static void maybeAlignCue(EmberRunSession s, EmberRunService runs, String kind, String id) {
        if (s == null || runs == null || id == null || id.isEmpty()) return;
        for (java.util.UUID u : s.committed) {
            PlayerData d = runs.dataOf(u);
            int spice = get(d);
            if (!valid(spice)) continue;
            boolean ok = "affix".equals(kind) ? matchesAffix(spice, id) : matchesEvent(spice, id);
            if (!ok) continue;
            org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(u);
            if (p == null || !p.isOnline()) continue;
            try {
                p.sendMessage(EmberRunService.P + "§a花样对齐 · §f" + label(spice) + " §7· " + id);
                p.sendActionBar("§a花样对齐 · §f" + label(spice));
                p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.35f);
            } catch (Throwable ignored) { }
        }
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选本周花样遭遇（真改重打本词缀/事件卷轴 · 可随时改 · 不改数值表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[火力花样]", "/corerpg p1 spicepath blaze", tip(BLAZE), "RED"},
                new String[]{"[控场花样]", "/corerpg p1 spicepath control", tip(CONTROL), "AQUA"},
                new String[]{"[目标花样]", "/corerpg p1 spicepath objective", tip(OBJECTIVE), "GOLD"},
                new String[]{"[取消]", "/corerpg p1 spicepath clear", "清空偏好（回纯随机）", "GRAY"});
    }

    public static boolean maybeOfferWeekly(Player p, PlayerData d, String week) {
        if (p == null || d == null || week == null || week.isEmpty()) return false;
        if (valid(get(d))) return false;
        if (d.periodCount(C_OFFER, week) > 0) return false;
        d.addPeriodCount(C_OFFER, week, 1);
        flush(p);
        offerPick(p);
        return true;
    }

    /** After Q02 — imprint unlock / variety-relevant. Delay past counterpath. */
    public static void scheduleOfferAfterQ02(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            maybeOfferWeekly(p, d, EmberPlayfeelTelemetry.weekKey());
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 85L);
    }

    public static void applyAndReply(Player p, int id) {
        if (p == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null || pl.getEmberRuns() == null) {
            p.sendMessage(EmberRunService.P + "主线本服务未加载");
            return;
        }
        EmberRunService runs = pl.getEmberRuns();
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        if (d == null) { p.sendMessage(EmberRunService.P + "数据未就绪"); return; }
        if (!EmberSkillKit.spicePathUnlocked(d, runs)) {
            p.sendMessage(EmberRunService.P + "§c花样路径需本人首进或首通 Q02。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空花样路径（重打本卷轴回纯随机）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a花样 → §f" + label(id) + " §7· " + tip(id));
        p.sendMessage(P + "§8队长偏好 · 仅本人已首通图的重打普通本生效（挑战/深渊/团本不改）");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[去重打 Q01]", "/corerpg p1 enter q01", "已首通后重打才出花样", "GREEN"},
                new String[]{"[破绽路径]", "/corerpg p1 counterpath", "练破绽窗口", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 spicepath", "重选", "GRAY"});
    }

    private static PlayerData dataOf(Player p) {
        CoreRpgPlugin pl = plugin();
        return pl == null || p == null ? null : pl.getDataStore().get(p.getUniqueId());
    }

    private static void flush(Player p) {
        CoreRpgPlugin pl = plugin();
        if (pl != null && p != null) pl.getDataStore().flushMutation(p.getUniqueId());
    }

    private static CoreRpgPlugin plugin() {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        return pl instanceof CoreRpgPlugin ? (CoreRpgPlugin) pl : null;
    }
}
