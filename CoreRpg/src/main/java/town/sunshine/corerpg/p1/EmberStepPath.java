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
 * D491: step-dir combat path — forward / back.
 * Real play change (身法方向 via {@link EmberSkillKit#setStepDir}); chat buttons only.
 * Zero step distance / CD table / AFK / sx change. Not forge chase / enter-card / ActionBar twin.
 */
public final class EmberStepPath {

    public static final String C_PATH = "p1_step_path";
    public static final String C_OFFER = "p1_step_path_offer";

    public static final int NONE = 0;
    public static final int FORWARD = 1; // → DIR_FORWARD
    public static final int BACK = 2;    // → DIR_BACK

    private EmberStepPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == FORWARD || id == BACK;
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
        if ("forward".equals(s) || "front".equals(s) || "前冲".equals(s) || "前".equals(s) || "1".equals(s)) return FORWARD;
        if ("back".equals(s) || "backward".equals(s) || "backstep".equals(s)
                || "后撤".equals(s) || "后".equals(s) || "2".equals(s)) return BACK;
        return -1;
    }

    public static int toDirId(int pathId) {
        return pathId == BACK ? EmberSkillKit.DIR_BACK : EmberSkillKit.DIR_FORWARD;
    }

    public static String label(int id) {
        if (id == FORWARD) return "前冲";
        if (id == BACK) return "后撤";
        return "未选";
    }

    public static String tip(int id) {
        if (id == FORWARD) return "潜行+Q 向前突进 · 追击/过刀";
        if (id == BACK) return "潜行+Q 向后拉开 · 躲预警/风筝";
        return "点选身法方向（真改潜行+Q）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8身法路径：首通 Q05 后可选";
        if (!valid(id)) return "§8身法路径：未选 · /corerpg p1 steppath";
        return "§e身法路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static String enterCmd(int id) {
        if (id == FORWARD) return "/corerpg p1 enter q05";
        if (id == BACK) return "/corerpg p1 enter q02";
        return "/corerpg p1 runs";
    }

    public static String enterLabel(int id) {
        if (id == FORWARD) return "[去练 Q05 前冲]";
        if (id == BACK) return "[去练 Q02 后撤]";
        return "[打开冒险]";
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选身法方向路径（真改潜行+Q · 回城可换 · 不改距离/CD 表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[前冲]", "/corerpg p1 steppath forward", tip(FORWARD), "GREEN"},
                new String[]{"[后撤]", "/corerpg p1 steppath back", tip(BACK), "YELLOW"},
                new String[]{"[取消]", "/corerpg p1 steppath clear", "清空路径（方向回前冲）", "GRAY"});
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

    /** After Q05 first clear — step unlock. Delay past ModePath outpost offer. */
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
        if (!EmberSkillKit.stepVariantUnlocked(d, pl.getEmberRuns())) {
            p.sendMessage(EmberRunService.P + "§c身法方向需本人首通 Q05。");
            return;
        }
        if (EmberSkillKit.inDungeon(p, pl.getEmberRuns())) {
            p.sendMessage(EmberRunService.P + "§c副本里不能换身法方向，请回城后再选。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            applyDir(p, EmberSkillKit.DIR_FORWARD);
            p.sendMessage(P + "已清空身法路径（方向回前冲）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        applyDir(p, toDirId(id));
        p.sendMessage(P + "§a身法路径 → §f" + label(id) + " §7· " + tip(id) + " §8（已写入潜行+Q 方向）");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{enterLabel(id), enterCmd(id), "进图体感这种身法", "GREEN"},
                new String[]{"[技能组]", "trmenu open ember_skill_kit", "看按键与身法", "GOLD"},
                new String[]{"[换一条]", "/corerpg p1 steppath", "重选", "GRAY"});
    }

    /** Delegate to SkillService so flex CD behaviour stays single-sourced. */
    private static void applyDir(Player p, int dirId) {
        CoreRpgPlugin pl = plugin();
        if (pl == null) return;
        SkillService sk = pl.getSkillService();
        if (sk == null) return;
        sk.cmdStepDir(p, EmberSkillKit.stepDirKey(dirId));
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
