package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D486: abyss combat path — push next floor vs farm cleared floor.
 * Real progression + quality-table choice; chat buttons only.
 * Zero abyss HP/dmg/fee / AFK / sx change.
 */
public final class EmberAbyssPath {

    public static final String C_PATH = "p1_abyss_path";
    public static final String C_OFFER = "p1_abyss_path_offer";

    public static final int NONE = 0;
    public static final int PUSH = 1; // 冲层 · maxStart / next
    public static final int FARM = 2; // 刷层 · best cleared (or 1)

    private EmberAbyssPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == PUSH || id == FARM;
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
        if ("push".equals(s) || "冲层".equals(s) || "next".equals(s) || "1".equals(s)) return PUSH;
        if ("farm".equals(s) || "刷层".equals(s) || "farming".equals(s) || "2".equals(s)) return FARM;
        return -1;
    }

    public static String label(int id) {
        if (id == PUSH) return "冲层";
        if (id == FARM) return "刷层";
        return "未选";
    }

    public static String tip(int id) {
        if (id == PUSH) return "开最高可开层 · 冲纪录 · 层费随层涨";
        if (id == FARM) return "刷已通关层 · 控费 · 成色仍按该层表";
        return "点选本周深渊打法";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8深渊路径：首通 Q07 后可选";
        if (!valid(id)) return "§8深渊路径：未选 · /corerpg p1 abysspath";
        return "§e深渊路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** Resolve enter tier hint (Bukkit-free when best/maxStart passed). */
    public static int enterTier(int id, int best, int maxStart) {
        if (id == PUSH) return Math.max(1, maxStart);
        if (id == FARM) return Math.max(1, best > 0 ? best : 1);
        return 1;
    }

    public static String enterCmd(int id, int best, int maxStart) {
        if (id == PUSH) return "/corerpg p1 abyss next";
        if (id == FARM) return "/corerpg p1 abyss " + enterTier(id, best, maxStart);
        return "/corerpg p1 abyss";
    }

    public static String enterLabel(int id, int best, int maxStart) {
        if (id == PUSH) return "[冲第 " + enterTier(id, best, maxStart) + " 层]";
        if (id == FARM) return "[刷第 " + enterTier(id, best, maxStart) + " 层]";
        return "[打开深渊]";
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选本周深渊打法（冲层/刷层 · 可随时改 · 不改层费/强度）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[冲层]", "/corerpg p1 abysspath push", tip(PUSH), "RED"},
                new String[]{"[刷层]", "/corerpg p1 abysspath farm", tip(FARM), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 abysspath clear", "清空路径", "GRAY"});
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

    /** After Q07 first clear — abyss unlocks. Delay past raidpath offer. */
    public static void scheduleOfferAfterQ07(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            maybeOfferWeekly(p, d, EmberPlayfeelTelemetry.weekKey());
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 100L);
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
        EmberAbyssService abyss = pl.getEmberRuns().abyss();
        if (abyss == null || !abyss.open(d)) {
            p.sendMessage(EmberRunService.P + "§c深渊需本人首通 Q07 断塔回廊。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空深渊路径");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        int best = abyss.best(d);
        int maxStart = abyss.maxStart(d);
        p.sendMessage(P + "§a深渊路径 → §f" + label(id) + " §7· " + tip(id)
                + " §8（最高通关 " + best + " · 可开至 " + maxStart + "）");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{enterLabel(id, best, maxStart), enterCmd(id, best, maxStart), "开深渊层（体力+层费）", "GREEN"},
                new String[]{"[深渊表]", "/corerpg p1 abyss", "看各层状态", "GOLD"},
                new String[]{"[换一条]", "/corerpg p1 abysspath", "重选", "GRAY"});
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
