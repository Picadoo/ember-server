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
 * D495: combat posture path — strike / guard / sweep.
 * Real play change: writes 烬斩符文 + 身法方向 together via SkillService.
 * Chat buttons only. Zero dmg/CD tables / AFK / sx. Not forge chase / enter-card / ActionBar twin.
 */
public final class EmberPosturePath {

    public static final String C_PATH = "p1_posture_path";
    public static final String C_OFFER = "p1_posture_path_offer";

    public static final int NONE = 0;
    /** 直线 + 前冲 */
    public static final int STRIKE = 1;
    /** 环斩 + 后撤 */
    public static final int GUARD = 2;
    /** 扇形 + 前冲 */
    public static final int SWEEP = 3;

    private EmberPosturePath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == STRIKE || id == GUARD || id == SWEEP;
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
        if ("strike".equals(s) || "突进".equals(s) || "点名".equals(s) || "1".equals(s)) return STRIKE;
        if ("guard".equals(s) || "守势".equals(s) || "风筝".equals(s) || "2".equals(s)) return GUARD;
        if ("sweep".equals(s) || "清杂".equals(s) || "扇推".equals(s) || "3".equals(s)) return SWEEP;
        return -1;
    }

    public static String label(int id) {
        if (id == STRIKE) return "突进点名";
        if (id == GUARD) return "守势风筝";
        if (id == SWEEP) return "扇面清杂";
        return "未选";
    }

    public static String tip(int id) {
        if (id == STRIKE) return "直线穿刺 + 前冲 · 走廊点杀突进";
        if (id == GUARD) return "原地环斩 + 后撤 · 贴身扫一圈再拉开";
        if (id == SWEEP) return "扇形 + 前冲 · 近距扇面推进清杂";
        return "点选战斗姿态（一次写入符文+身法）";
    }

    public static int shapeId(int id) {
        if (id == STRIKE) return EmberSkillKit.SHAPE_LINE;
        if (id == GUARD) return EmberSkillKit.SHAPE_RING;
        return EmberSkillKit.SHAPE_FAN; // SWEEP / default
    }

    public static int dirId(int id) {
        if (id == GUARD) return EmberSkillKit.DIR_BACK;
        return EmberSkillKit.DIR_FORWARD; // STRIKE / SWEEP
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8姿态路径：首通 Q05 后可选（需符文+身法）";
        if (!valid(id)) return "§8姿态路径：未选 · /corerpg p1 posturepath";
        return "§e姿态路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static String enterCmd(int id) {
        if (id == STRIKE) return "/corerpg p1 enter q02";
        if (id == GUARD) return "/corerpg p1 enter q04";
        if (id == SWEEP) return "/corerpg p1 enter q01";
        return "/corerpg p1 runs";
    }

    public static String enterLabel(int id) {
        if (id == STRIKE) return "[去练 Q02 突进]";
        if (id == GUARD) return "[去练 Q04 守势]";
        if (id == SWEEP) return "[去练 Q01 清杂]";
        return "[打开冒险]";
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选战斗姿态（一次写入烬斩符文+身法方向 · 回城可换 · 不改伤害/CD 表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[突进点名]", "/corerpg p1 posturepath strike", tip(STRIKE), "RED"},
                new String[]{"[守势风筝]", "/corerpg p1 posturepath guard", tip(GUARD), "AQUA"},
                new String[]{"[扇面清杂]", "/corerpg p1 posturepath sweep", tip(SWEEP), "GOLD"},
                new String[]{"[取消]", "/corerpg p1 posturepath clear", "清空姿态", "GRAY"});
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

    /** After Q05 — both shape (Q04) and step (Q05) unlock. Delay past steppath. */
    public static void scheduleOfferAfterQ05(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            maybeOfferWeekly(p, d, EmberPlayfeelTelemetry.weekKey());
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 95L);
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
        boolean shapeOk = EmberSkillKit.shapeUnlocked(d, runs);
        boolean stepOk = EmberSkillKit.stepVariantUnlocked(d, runs);
        if (!shapeOk || !stepOk) {
            p.sendMessage(EmberRunService.P + "§c姿态需本人首通 Q04（符文）与 Q05（身法）。"
                    + (!shapeOk ? " §8· 缺 Q04" : "") + (!stepOk ? " §8· 缺 Q05" : ""));
            return;
        }
        if (EmberSkillKit.inDungeon(p, runs)) {
            p.sendMessage(EmberRunService.P + "§c副本里不能改姿态，请回城后再选。");
            return;
        }
        SkillService sk = pl.getSkillService();
        if (sk == null) {
            p.sendMessage(EmberRunService.P + "技能服务未加载");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空姿态路径（当前符文/身法未改）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        // also mirror shapepath / steppath sticky so glances stay consistent
        EmberShapePath.set(d, shapePathId(id));
        EmberStepPath.set(d, stepPathId(id));
        flush(p);
        sk.cmdShape(p, EmberSkillKit.shapeKey(shapeId(id)));
        sk.cmdStepDir(p, EmberSkillKit.stepDirKey(dirId(id)));
        p.sendMessage(P + "§a姿态 → §f" + label(id) + " §7· " + tip(id)
                + " §8（已写入 "
                + EmberSkillKit.shapeName(shapeId(id)) + " + "
                + EmberSkillKit.stepDirName(dirId(id)) + "）");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{enterLabel(id), enterCmd(id), "进图体感这套姿态", "GREEN"},
                new String[]{"[符文路径]", "/corerpg p1 shapepath", "单改几何", "GOLD"},
                new String[]{"[身法路径]", "/corerpg p1 steppath", "单改方向", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 posturepath", "重选", "GRAY"});
    }

    static int shapePathId(int posture) {
        if (posture == STRIKE) return EmberShapePath.LINE;
        if (posture == GUARD) return EmberShapePath.RING;
        return EmberShapePath.FAN;
    }

    static int stepPathId(int posture) {
        if (posture == GUARD) return EmberStepPath.BACK;
        return EmberStepPath.FORWARD;
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
