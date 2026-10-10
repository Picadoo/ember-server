package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D503: short-expedition chase path — first-clear / daily-reward / late-band.
 * NEW acquisition: sticky bias over which sx to farm next (resolve live).
 * Not combo sticky-path, not variety/Extra roll bias, not prep auto-buy twin,
 * not forge spend-chase, not ActionBar twin. Chat buttons only. Zero AFK/sx20/K3.
 */
public final class EmberShortPath {

    public static final String C_PATH = "p1_short_path";
    public static final String C_OFFER = "p1_short_path_offer";

    public static final int NONE = 0;
    /** Chase unlocked shorts with unpaid career first-clear */
    public static final int FC = 1;
    /** Chase unlocked shorts with day reward left */
    public static final int DAY = 2;
    /** Prefer late band sx14–sx19 (FC then day inside band) */
    public static final int LATE = 3;

    private EmberShortPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == FC || id == DAY || id == LATE;
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
        if ("fc".equals(s) || "first".equals(s) || "首通".equals(s) || "1".equals(s)) return FC;
        if ("day".equals(s) || "daily".equals(s) || "有奖".equals(s) || "日帽".equals(s) || "2".equals(s)) return DAY;
        if ("late".equals(s) || "后段".equals(s) || "末段".equals(s) || "3".equals(s)) return LATE;
        return -1;
    }

    public static String label(int id) {
        if (id == FC) return "短征·首通追";
        if (id == DAY) return "短征·有奖追";
        if (id == LATE) return "短征·后段追";
        return "未选";
    }

    public static String tip(int id) {
        if (id == FC) return "优先刷未领生涯首通包的短征";
        if (id == DAY) return "优先刷今日还有有奖次数的短征";
        if (id == LATE) return "优先刷 sx14–sx19 后段短征（先首通再有奖）";
        return "点选短征追猎偏好（改获取路线）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8短征路径：首通 Q01 后可选";
        if (!valid(id)) return "§8短征路径：未选 · /corerpg p1 shortpath";
        return "§e短征路径：§f" + label(id) + " §8· " + tip(id);
    }

    static boolean unlocked(EmberRunService runs, PlayerData d, EmberRunMaps.MapDef m) {
        if (runs == null || d == null || m == null || !m.shortExpedition) return false;
        String req = m.requires == null || m.requires.isEmpty() ? EmberShortRules.REQUIRES : m.requires;
        return runs.progressFlag(d, req);
    }

    static boolean unpaidFc(PlayerData d, EmberRunMaps.MapDef m) {
        if (d == null || m == null) return false;
        String ver = m.contentVersion == null ? "v1" : m.contentVersion;
        return !EmberFirstClear.paid(d, m.key, ver);
    }

    static boolean dayLeft(EmberRunService runs, PlayerData d, EmberRunMaps.MapDef m) {
        if (runs == null || d == null || m == null) return false;
        EmberShortService sh = runs.shortExpedition();
        if (sh == null) return false;
        return sh.rewardedToday(d, m) < sh.dailyCap(m);
    }

    static boolean inLateBand(EmberRunMaps.MapDef m) {
        if (m == null || m.key == null || m.key.length() < 4 || !m.key.startsWith("sx")) return false;
        try {
            int n = Integer.parseInt(m.key.substring(2));
            return n >= 14 && n <= 19;
        } catch (NumberFormatException e) { return false; }
    }

    static List<EmberRunMaps.MapDef> orderedShorts(EmberRunMaps maps) {
        List<EmberRunMaps.MapDef> out = new ArrayList<EmberRunMaps.MapDef>();
        if (maps == null || maps.shortMaps == null) return out;
        out.addAll(maps.shortMaps.values());
        out.sort(Comparator.comparing(m -> m.key == null ? "" : m.key));
        return out;
    }

    /**
     * Resolve the next short to chase. Null when none match (all FC paid / day full / band empty).
     * Bukkit-free aside from MapDef / PlayerData.
     */
    public static EmberRunMaps.MapDef resolve(EmberRunService runs, PlayerData d, int path) {
        if (runs == null || d == null || !valid(path)) return null;
        EmberRunMaps maps = runs.maps();
        List<EmberRunMaps.MapDef> all = orderedShorts(maps);
        if (path == FC) {
            for (EmberRunMaps.MapDef m : all) {
                if (unlocked(runs, d, m) && unpaidFc(d, m)) return m;
            }
            return null;
        }
        if (path == DAY) {
            for (EmberRunMaps.MapDef m : all) {
                if (unlocked(runs, d, m) && dayLeft(runs, d, m)) return m;
            }
            return null;
        }
        // LATE: unpaid FC in band, then day-left in band
        EmberRunMaps.MapDef dayHit = null;
        for (EmberRunMaps.MapDef m : all) {
            if (!inLateBand(m) || !unlocked(runs, d, m)) continue;
            if (unpaidFc(d, m)) return m;
            if (dayHit == null && dayLeft(runs, d, m)) dayHit = m;
        }
        return dayHit;
    }

    public static String resolveLine(EmberRunMaps.MapDef m) {
        if (m == null) return "§7当前没有可追的短征（首通已清 / 今日有奖已满 / 后段未开）";
        String n = m.name == null || m.name.isEmpty() ? m.key : m.name;
        return "§e下一本：§f" + m.key.toUpperCase(Locale.ROOT) + " " + n;
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选短征追猎（改刷哪本 · 可随时改 · 不抬日表/不改奖励）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[首通追]", "/corerpg p1 shortpath fc", tip(FC), "GREEN"},
                new String[]{"[有奖追]", "/corerpg p1 shortpath day", tip(DAY), "GOLD"},
                new String[]{"[后段追]", "/corerpg p1 shortpath late", tip(LATE), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 shortpath clear", "清空偏好", "DARK_GRAY"});
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

    /** After Q01 — delay past prep (165L). */
    public static void scheduleOfferAfterQ01(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            maybeOfferWeekly(p, d, EmberPlayfeelTelemetry.weekKey());
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 190L);
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
        if (!runs.progressFlag(d, "q01")) {
            p.sendMessage(EmberRunService.P + "§c短征路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空短征路径");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a短征路径 → §f" + label(id) + " §7· " + tip(id));
        EmberRunMaps.MapDef next = resolve(runs, d, id);
        p.sendMessage(P + resolveLine(next));
        if (next != null) {
            String cmd = "/corerpg p1 enter " + next.key;
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[进 " + next.key.toUpperCase(Locale.ROOT) + "]", cmd, "按路径进下一本", "GREEN"},
                    new String[]{"[短征选页]", "/ember_p1_short", "自己挑", "AQUA"},
                    new String[]{"[换一条]", "/corerpg p1 shortpath", "重选", "GRAY"});
        } else {
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[短征选页]", "/ember_p1_short", "自己挑", "AQUA"},
                    new String[]{"[换一条]", "/corerpg p1 shortpath", "重选", "GRAY"});
        }
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
