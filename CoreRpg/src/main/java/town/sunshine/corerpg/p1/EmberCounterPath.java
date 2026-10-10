package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D480: counterplay combat path — wall / whiff / break.
 * Real fight-style choice (which boss window to chase); chat buttons only — no ActionBar HUD spam.
 * Zero boss dmg / CD / AFK / stamp change.
 */
public final class EmberCounterPath {

    public static final String C_PATH = "p1_counter_path";
    public static final String C_OFFER = "p1_counter_path_offer";

    public static final int NONE = 0;
    /** 撞墙破绽 · Q02 焦冲 / Q07 冲撞 */
    public static final int WALL = 1;
    /** 落空破绽 · Q01–Q05 重招 */
    public static final int WHIFF = 2;
    /** 破招通道 · Q06 霜潮 / Q07 炉心 */
    public static final int BREAK = 3;

    private EmberCounterPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == WALL || id == WHIFF || id == BREAK;
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
        if ("wall".equals(s) || "撞墙".equals(s) || "wall_stun".equals(s) || "1".equals(s)) return WALL;
        if ("whiff".equals(s) || "落空".equals(s) || "whiff_stun".equals(s) || "2".equals(s)) return WHIFF;
        if ("break".equals(s) || "破招".equals(s) || "break_hp".equals(s) || "3".equals(s)) return BREAK;
        return -1;
    }

    public static String label(int id) {
        if (id == WALL) return "撞墙破绽";
        if (id == WHIFF) return "落空破绽";
        if (id == BREAK) return "破招通道";
        return "未选";
    }

    public static String tip(int id) {
        if (id == WALL) return "冲撞贴墙打断 · 练 Q02/Q07";
        if (id == WHIFF) return "预警内站开让重招落空 · 练 Q01–Q05";
        if (id == BREAK) return "半血读条圈内猛打破招 · 练 Q06/Q07";
        return "点选本周破绽练法";
    }

    public static String glance(int id) {
        if (!valid(id)) return "§8破绽路径：未选 · /corerpg p1 counterpath";
        return "§e破绽路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** Suggested enter command after path set. */
    public static String enterCmd(int id) {
        if (id == WALL) return "/corerpg p1 enter q02";
        if (id == WHIFF) return "/corerpg p1 enter q01";
        if (id == BREAK) return "/corerpg p1 enter q06";
        return "/corerpg p1 runs";
    }

    public static String enterLabel(int id) {
        if (id == WALL) return "[去练 Q02 撞墙]";
        if (id == WHIFF) return "[去练 Q01 落空]";
        if (id == BREAK) return "[去练 Q06 破招]";
        return "[打开冒险]";
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选破绽玩法路径（练哪类首领窗口 · 可随时改 · 不改数值）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[撞墙]", "/corerpg p1 counterpath wall", tip(WALL), "GOLD"},
                new String[]{"[落空]", "/corerpg p1 counterpath whiff", tip(WHIFF), "AQUA"},
                new String[]{"[破招]", "/corerpg p1 counterpath break", tip(BREAK), "LIGHT_PURPLE"},
                new String[]{"[取消]", "/corerpg p1 counterpath clear", "清空路径", "GRAY"});
    }

    /** Once per ISO week when unset. */
    public static boolean maybeOfferWeekly(Player p, PlayerData d, String week) {
        if (p == null || d == null || week == null || week.isEmpty()) return false;
        if (valid(get(d))) return false;
        if (d.periodCount(C_OFFER, week) > 0) return false;
        d.addPeriodCount(C_OFFER, week, 1);
        flush(p);
        offerPick(p);
        return true;
    }

    /** After Q02 first clear — wall_stun awareness unlocks this path offer. */
    public static void scheduleOfferAfterQ02(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            String wk = EmberPlayfeelTelemetry.weekKey();
            maybeOfferWeekly(p, d, wk);
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 50L);
    }

    public static void applyAndReply(Player p, int id) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) { p.sendMessage(EmberRunService.P + "数据未就绪"); return; }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空破绽路径");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a破绽路径 → §f" + label(id) + " §7· " + tip(id));
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{enterLabel(id), enterCmd(id), "进图练这种破绽", "GREEN"},
                new String[]{"[换一条]", "/corerpg p1 counterpath", "重选", "GRAY"});
    }

    private static PlayerData dataOf(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (!(pl instanceof town.sunshine.corerpg.CoreRpgPlugin)) return null;
        return ((town.sunshine.corerpg.CoreRpgPlugin) pl).getDataStore().get(p.getUniqueId());
    }

    private static void flush(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl instanceof town.sunshine.corerpg.CoreRpgPlugin && p != null)
            ((town.sunshine.corerpg.CoreRpgPlugin) pl).getDataStore().flushMutation(p.getUniqueId());
    }
}
