package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D521: weekly season-goal focus path — featured / abyss / raid / bounty / flex.
 * NEW acquisition: sticky focus for 余烬徽 weekly goals (chase line + goals panel pin).
 * Distinct from DailyPath (daily clear count), ShortPath (sx), FeaturedPath (featured enter buttons).
 * Chat only. Zero goal rewards / AFK / sx. Not combo sticky, not forge/enter/ActionBar cue.
 */
public final class EmberGoalPath {

    public static final String C_PATH = "p1_goal_path";
    public static final String C_OFFER = "p1_goal_path_offer";

    public static final int NONE = 0;
    public static final int FEATURED = 1;
    public static final int ABYSS = 2;
    public static final int RAID = 3;
    public static final int BOUNTY = 4;
    public static final int FLEX = 5;

    private EmberGoalPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == FEATURED || id == ABYSS || id == RAID || id == BOUNTY || id == FLEX;
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
        if ("clear".equals(s) || "none".equals(s) || "取消".equals(s)) return NONE;
        if ("featured".equals(s) || "精选".equals(s) || "1".equals(s)) return FEATURED;
        if ("abyss".equals(s) || "深渊".equals(s) || "2".equals(s)) return ABYSS;
        if ("raid".equals(s) || "团本".equals(s) || "3".equals(s)) return RAID;
        if ("bounty".equals(s) || "委托".equals(s) || "日委".equals(s) || "4".equals(s)) return BOUNTY;
        if ("flex".equals(s) || "随意".equals(s) || "off".equals(s) || "5".equals(s)) return FLEX;
        return -1;
    }

    public static String key(int id) {
        if (id == FEATURED) return "featured";
        if (id == ABYSS) return "abyss";
        if (id == RAID) return "raid";
        if (id == BOUNTY) return "bounty";
        if (id == FLEX) return "flex";
        return "none";
    }

    /** Season goal id for path, or null when flex/none. Bukkit-free. */
    public static String focusGoal(int path) {
        if (path == FEATURED) return "featured";
        if (path == ABYSS) return "abyss";
        if (path == RAID) return "raid";
        if (path == BOUNTY) return "bounty";
        return null;
    }

    public static String label(int id) {
        if (id == FEATURED) return "周标·精选";
        if (id == ABYSS) return "周标·深渊";
        if (id == RAID) return "周标·团本";
        if (id == BOUNTY) return "周标·委托";
        if (id == FLEX) return "周标·随意";
        return "未选";
    }

    public static String tip(int id) {
        if (id == FEATURED) return "本周优先追精选挑战目标（余烬徽）";
        if (id == ABYSS) return "本周优先追深渊通关目标（余烬徽）";
        if (id == RAID) return "本周优先追团本通关目标（余烬徽）";
        if (id == BOUNTY) return "本周优先追每日委托满档目标（余烬徽）";
        if (id == FLEX) return "不设周标焦点，四条目标平等";
        return "点选本周目标焦点（改追猎 · 不改徽奖励）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8周标路径：首通 Q07 后可选";
        if (!valid(id)) return "§8周标路径：未选 · /corerpg p1 goalpath";
        return "§e周标路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** Chase line for focus; null when flex/none or goal closed. Bukkit-free vs season API. */
    public static String chaseLine(int path, int progress, int target, String goalName) {
        if (focusGoal(path) == null || target <= 0) return null;
        if (progress >= target) return "§a周标焦点达标 ✔ §7（" + label(path) + "）";
        String gn = goalName == null ? focusGoal(path) : goalName;
        return "§e周标焦点 §f" + progress + "/" + target + " §7· " + gn + "（" + label(path) + "）";
    }

    public static void maybeGlance(Player p, EmberRunService runs, String activity) {
        if (p == null || runs == null || activity == null) return;
        PlayerData d = runs.dataOf(p.getUniqueId());
        if (d == null || !runs.progressFlag(d, "q07")) return;
        int path = get(d);
        String g = focusGoal(path);
        if (g == null || !g.equals(activity)) return;
        EmberSeason season = runs.season();
        if (season == null || !season.goalsOn()) return;
        int t = season.target(d, g);
        String line = chaseLine(path, season.progress(d, g), t, EmberSeason.goalName(g));
        if (line != null) p.sendMessage(EmberRunService.P + line);
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选本周目标焦点（改追猎 · 可随时改 · 不改徽奖励）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[精选]", "/corerpg p1 goalpath featured", tip(FEATURED), "GOLD"},
                new String[]{"[深渊]", "/corerpg p1 goalpath abyss", tip(ABYSS), "DARK_PURPLE"},
                new String[]{"[团本]", "/corerpg p1 goalpath raid", tip(RAID), "RED"},
                new String[]{"[委托]", "/corerpg p1 goalpath bounty", tip(BOUNTY), "GREEN"},
                new String[]{"[随意]", "/corerpg p1 goalpath flex", tip(FLEX), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 goalpath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 210L); // after challenge ~195
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
        if (!runs.progressFlag(d, "q07")) {
            p.sendMessage(EmberRunService.P + "§c周标路径需本人首通 Q07。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空周标路径（无焦点）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a周标 → §f" + label(id) + " §7· " + tip(id));
        EmberSeason season = runs.season();
        if (season != null && season.goalsOn()) {
            String g = focusGoal(id);
            if (g != null) {
                String line = chaseLine(id, season.progress(d, g), season.target(d, g), EmberSeason.goalName(g));
                if (line != null) p.sendMessage(P + line);
            }
        }
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[周目标]", "/corerpg p1 goals", "看本周进度", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 goalpath", "重选", "GRAY"});
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
