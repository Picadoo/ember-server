package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.FlexSkillService;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.SkillService;

/**
 * D499: combat stride path — push / bail / bare.
 * Real play change: writes flex equip + step direction together.
 * Chat buttons only. Zero dmg/CD tables / AFK / sx. Not forge / enter-card / ActionBar twin.
 */
public final class EmberStridePath {

    public static final String C_PATH = "p1_stride_path";
    public static final String C_OFFER = "p1_stride_path_offer";

    public static final int NONE = 0;
    /** 装配踏步 + 前冲 */
    public static final int PUSH = 1;
    /** 装配踏步 + 后撤 */
    public static final int BAIL = 2;
    /** 卸下轻技（身法方向回前冲） */
    public static final int BARE = 3;

    private EmberStridePath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == PUSH || id == BAIL || id == BARE;
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
        if ("push".equals(s) || "突进".equals(s) || "前冲踏".equals(s) || "1".equals(s)) return PUSH;
        if ("bail".equals(s) || "后撤".equals(s) || "后撤踏".equals(s) || "2".equals(s)) return BAIL;
        if ("bare".equals(s) || "卸下".equals(s) || "空手".equals(s) || "3".equals(s)) return BARE;
        return -1;
    }

    public static String label(int id) {
        if (id == PUSH) return "突进踏";
        if (id == BAIL) return "后撤踏";
        if (id == BARE) return "卸轻技";
        return "未选";
    }

    public static String tip(int id) {
        if (id == PUSH) return "装配踏步 + 前冲 · 潜行+Q 追击过刀";
        if (id == BAIL) return "装配踏步 + 后撤 · 潜行+Q 拉开躲预警";
        if (id == BARE) return "卸下轻技 · 潜行+Q 不放身法（方向回前冲）";
        return "点选步态（一次写入轻技+身法方向）";
    }

    public static int flexId(int id) {
        if (id == BARE) return EmberFlexPath.OFF;
        if (id == PUSH || id == BAIL) return EmberFlexPath.ON;
        return EmberFlexPath.NONE;
    }

    public static int stepId(int id) {
        if (id == BAIL) return EmberStepPath.BACK;
        return EmberStepPath.FORWARD; // PUSH / BARE default forward
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8步态路径：首通 Q05 后可选（需轻技+身法）";
        if (!valid(id)) return "§8步态路径：未选 · /corerpg p1 stridepath";
        return "§e步态路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static String enterCmd(int id) {
        if (id == BAIL) return "/corerpg p1 enter q02";
        if (id == PUSH) return "/corerpg p1 enter q05";
        return "/corerpg p1 runs";
    }

    public static String enterLabel(int id) {
        if (id == BAIL) return "[去练 Q02 后撤]";
        if (id == PUSH) return "[去练 Q05 前冲]";
        return "[打开冒险]";
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选步态玩法（一次写入轻技装配+身法方向 · 回城可换 · 不改伤害/CD 表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[突进踏]", "/corerpg p1 stridepath push", tip(PUSH), "GREEN"},
                new String[]{"[后撤踏]", "/corerpg p1 stridepath bail", tip(BAIL), "YELLOW"},
                new String[]{"[卸轻技]", "/corerpg p1 stridepath bare", tip(BARE), "GRAY"},
                new String[]{"[取消]", "/corerpg p1 stridepath clear", "清空步态（装配/方向未自动回滚）", "DARK_GRAY"});
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

    /** After Q05 — step unlock. Delay past posturepath (95L). */
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
        Bukkit.getScheduler().runTaskLater(pl, r, 130L);
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
        boolean stepOk = EmberSkillKit.stepVariantUnlocked(d, runs);
        if (!runs.progressFlag(d, "q01") || !stepOk) {
            p.sendMessage(EmberRunService.P + "§c步态需本人首通 Q01（轻技）与 Q05（身法）。"
                    + (!runs.progressFlag(d, "q01") ? " §8· 缺 Q01" : "")
                    + (!stepOk ? " §8· 缺 Q05" : ""));
            return;
        }
        if (EmberSkillKit.inDungeon(p, runs)) {
            p.sendMessage(EmberRunService.P + "§c副本里不能改步态，请回城后再选。");
            return;
        }
        FlexSkillService flex = pl.getFlexSkillService();
        SkillService sk = pl.getSkillService();
        if (flex == null || sk == null) {
            p.sendMessage(EmberRunService.P + "轻技/技能服务未加载");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空步态路径（当前轻技装配与身法方向未改）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        EmberFlexPath.set(d, flexId(id));
        EmberStepPath.set(d, stepId(id));
        flush(p);
        if (flexId(id) == EmberFlexPath.ON) flex.cmdEquip(p, FlexSkillService.PILOT_ID);
        else flex.cmdUnequip(p);
        sk.cmdStepDir(p, EmberSkillKit.stepDirKey(
                stepId(id) == EmberStepPath.BACK ? EmberSkillKit.DIR_BACK : EmberSkillKit.DIR_FORWARD));
        p.sendMessage(P + "§a步态 → §f" + label(id) + " §7· " + tip(id)
                + " §8（轻技=" + EmberFlexPath.label(flexId(id))
                + " · 身法=" + EmberStepPath.label(stepId(id)) + "）");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{enterLabel(id), enterCmd(id), "进图体感这套步态", "GREEN"},
                new String[]{"[轻技路径]", "/corerpg p1 flexpath", "单改装配", "GOLD"},
                new String[]{"[身法路径]", "/corerpg p1 steppath", "单改方向", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 stridepath", "重选", "GRAY"});
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
