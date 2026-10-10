package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.SkillService;

/**
 * D490: slash-shape combat path — fan / line / ring.
 * Real play change (烬斩 geometry via {@link EmberSkillKit#setShape}); chat buttons only.
 * Zero slash dmg / CD table / AFK / sx change. Not forge chase / enter-card / ActionBar twin.
 */
public final class EmberShapePath {

    public static final String C_PATH = "p1_shape_path";
    public static final String C_OFFER = "p1_shape_path_offer";

    public static final int NONE = 0;
    public static final int FAN = 1;   // → SHAPE_FAN
    public static final int LINE = 2;  // → SHAPE_LINE
    public static final int RING = 3;  // → SHAPE_RING

    private EmberShapePath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == FAN || id == LINE || id == RING;
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
        if ("fan".equals(s) || "扇形".equals(s) || "扇".equals(s) || "1".equals(s)) return FAN;
        if ("line".equals(s) || "直线".equals(s) || "穿刺".equals(s) || "2".equals(s)) return LINE;
        if ("ring".equals(s) || "环".equals(s) || "环斩".equals(s) || "3".equals(s)) return RING;
        return -1;
    }

    public static int toShapeId(int pathId) {
        if (pathId == LINE) return EmberSkillKit.SHAPE_LINE;
        if (pathId == RING) return EmberSkillKit.SHAPE_RING;
        return EmberSkillKit.SHAPE_FAN;
    }

    public static String label(int id) {
        if (id == FAN) return "扇形";
        if (id == LINE) return "直线穿刺";
        if (id == RING) return "原地环斩";
        return "未选";
    }

    public static String tip(int id) {
        if (id == FAN) return "近距扇面清杂 · 默认手感";
        if (id == LINE) return "中距直线点名 · 走廊点杀";
        if (id == RING) return "环身近战 · 被围时扫一圈";
        return "点选烬斩几何（真改出招）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8符文路径：首通 Q04 后可选";
        if (!valid(id)) return "§8符文路径：未选 · /corerpg p1 shapepath";
        return "§e符文路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static String enterCmd(int id) {
        if (id == FAN) return "/corerpg p1 enter q01";
        if (id == LINE) return "/corerpg p1 enter q02";
        if (id == RING) return "/corerpg p1 enter q04";
        return "/corerpg p1 runs";
    }

    public static String enterLabel(int id) {
        if (id == FAN) return "[去练 Q01 扇面]";
        if (id == LINE) return "[去练 Q02 直线]";
        if (id == RING) return "[去练 Q04 环斩]";
        return "[打开冒险]";
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选烬斩符文路径（真改出招几何 · 回城可换 · 不改伤害表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[扇形]", "/corerpg p1 shapepath fan", tip(FAN), "GOLD"},
                new String[]{"[直线穿刺]", "/corerpg p1 shapepath line", tip(LINE), "AQUA"},
                new String[]{"[原地环斩]", "/corerpg p1 shapepath ring", tip(RING), "LIGHT_PURPLE"},
                new String[]{"[取消]", "/corerpg p1 shapepath clear", "清空路径（符文回扇形）", "GRAY"});
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

    /** After Q04 first clear — shape unlock. Delay past ModePath/EchoPath. */
    public static void scheduleOfferAfterQ04(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            maybeOfferWeekly(p, d, EmberPlayfeelTelemetry.weekKey());
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 75L);
    }

    public static void applyAndReply(Player p, int id) {
        if (p == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null || pl.getEmberRuns() == null) {
            p.sendMessage(EmberRunService.P + "主线本服务未加载");
            return;
        }
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        if (d == null) { p.sendMessage(EmberRunService.P + "数据未就绪"); return; }
        if (!EmberSkillKit.shapeUnlocked(d, pl.getEmberRuns())) {
            p.sendMessage(EmberRunService.P + "§c烬斩符文需本人首通 Q04。");
            return;
        }
        if (EmberSkillKit.inDungeon(p, pl.getEmberRuns())) {
            p.sendMessage(EmberRunService.P + "§c副本里不能换符文，请回城后再选。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            applyShape(p, EmberSkillKit.SHAPE_FAN);
            p.sendMessage(P + "已清空符文路径（烬斩回扇形）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        applyShape(p, toShapeId(id));
        p.sendMessage(P + "§a符文路径 → §f" + label(id) + " §7· " + tip(id) + " §8（已写入烬斩几何）");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{enterLabel(id), enterCmd(id), "进图体感这种几何", "GREEN"},
                new String[]{"[技能组]", "trmenu open ember_skill_kit", "看按键与符文", "GOLD"},
                new String[]{"[换一条]", "/corerpg p1 shapepath", "重选", "GRAY"});
    }

    /** Delegate to SkillService so CD/X1 behaviour stays single-sourced. */
    private static void applyShape(Player p, int shapeId) {
        CoreRpgPlugin pl = plugin();
        if (pl == null) return;
        SkillService sk = pl.getSkillService();
        if (sk == null) return;
        sk.cmdShape(p, EmberSkillKit.shapeKey(shapeId));
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
