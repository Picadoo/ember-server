package town.sunshine.corerpg.p1;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D298 方案 M：玩法可感轻量遥测（周 periodCount + OP 只读 + 全服周合计；可选结算 JSON 日志）。
 * 零玩法改；非玩家面看板。
 */
public final class EmberPlayfeelTelemetry {

    public static final String C_RUNS = "p1_pf_runs";
    public static final String C_WALL = "p1_pf_wall";
    public static final String C_WHIFF = "p1_pf_whiff";
    public static final String C_BREAK = "p1_pf_break";
    public static final String C_EVT_ROLL = "p1_pf_evt_roll";
    public static final String C_EVT_OK = "p1_pf_evt_ok";
    public static final String C_SIG_WEAR = "p1_pf_sig_wear";
    public static final String C_SIG_ALT = "p1_pf_sig_alt";
    public static final String C_VB_HIT = "p1_pf_vb_hit";

    private static final String[] KEYS = {
            C_RUNS, C_WALL, C_WHIFF, C_BREAK, C_EVT_ROLL, C_EVT_OK, C_SIG_WEAR, C_SIG_ALT, C_VB_HIT
    };

    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> SERVER =
            new ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>>();
    private static final AtomicInteger sinceSnap = new AtomicInteger(0);

    private EmberPlayfeelTelemetry() {}

    public static String weekKey() {
        return EmberRunRules.rotationWeekKey(LocalDate.now(town.sunshine.corerpg.DailyService.zone()));
    }

    public static String lastWeekKey() {
        return EmberRunRules.rotationWeekKey(LocalDate.now(town.sunshine.corerpg.DailyService.zone()).minusWeeks(1));
    }

    public static boolean eligible(EmberRunSession s, EmberRunMaps.MapDef m) {
        if (s == null || m == null) return false;
        if (s.abyss > 0 || m.raid || m.rush || m.event) return false;
        String k = s.mapKey == null ? "" : s.mapKey.toLowerCase(Locale.ROOT);
        return k.matches("q0[1-7]");
    }

    public static boolean excludeFromServer(UUID u, String name) {
        if (EmberMode.boardExcluded(name)) return true;
        EmberMode mode = EmberMode.get();
        if (mode == null || u == null) return false;
        List<String> ids = mode.list("telemetry.exclude_uuids");
        if (ids == null || ids.isEmpty()) return false;
        String id = u.toString();
        for (String x : ids) if (x != null && id.equalsIgnoreCase(x.trim())) return true;
        return false;
    }

    public static boolean jsonLogEnabled() {
        EmberMode mode = EmberMode.get();
        return mode == null || mode.b("telemetry.json_log", true);
    }

    /**
     * W1a (+ optional R): write week counters; update server snap; optional JSON log.
     */
    public static void record(EmberRunSession s, EmberRunMaps.MapDef m, UUID u, PlayerData pd,
                              boolean win, boolean vbHit, Logger log, File dataFolder) {
        if (!eligible(s, m) || pd == null || u == null) return;
        String wk = weekKey();
        int wall = Math.max(0, s.wallHits);
        int whiff = Math.max(0, s.whiffHits);
        int brk = Math.max(0, s.breakHits);
        boolean evtRoll = s.eventRoom != null && !s.eventRoom.isEmpty();
        boolean evtOk = s.eventDone;
        boolean sigWear = false, sigAlt = false;
        EmberGrowthService g = EmberGrowthService.get();
        EmberRunService runs = EmberRunService.get();
        if (g != null && runs != null && runs.loadouts() != null) {
            Player online = Bukkit.getPlayer(u);
            EmberLoadout lo = online != null ? runs.loadouts().get(online) : null;
            if (lo != null) {
                List<EmberSignature.Def> sg = g.signatures(pd, lo);
                if (sg != null && !sg.isEmpty()) {
                    sigWear = true;
                    for (EmberSignature.Def sd : sg) {
                        if (g.sigAlt(pd, sd)) { sigAlt = true; break; }
                    }
                }
            }
        }
        pd.addPeriodCount(C_RUNS, wk, 1);
        if (wall > 0) pd.addPeriodCount(C_WALL, wk, wall);
        if (whiff > 0) pd.addPeriodCount(C_WHIFF, wk, whiff);
        if (brk > 0) pd.addPeriodCount(C_BREAK, wk, brk);
        if (evtRoll) pd.addPeriodCount(C_EVT_ROLL, wk, 1);
        if (evtOk) pd.addPeriodCount(C_EVT_OK, wk, 1);
        if (sigWear) pd.addPeriodCount(C_SIG_WEAR, wk, 1);
        if (sigAlt) pd.addPeriodCount(C_SIG_ALT, wk, 1);
        if (vbHit) pd.addPeriodCount(C_VB_HIT, wk, 1);

        OfflinePlayer op = Bukkit.getOfflinePlayer(u);
        String name = op.getName() != null ? op.getName() : u.toString();
        if (!excludeFromServer(u, name)) {
            bumpServer(wk, C_RUNS, 1);
            bumpServer(wk, C_WALL, wall);
            bumpServer(wk, C_WHIFF, whiff);
            bumpServer(wk, C_BREAK, brk);
            if (evtRoll) bumpServer(wk, C_EVT_ROLL, 1);
            if (evtOk) bumpServer(wk, C_EVT_OK, 1);
            if (sigWear) bumpServer(wk, C_SIG_WEAR, 1);
            if (sigAlt) bumpServer(wk, C_SIG_ALT, 1);
            if (vbHit) bumpServer(wk, C_VB_HIT, 1);
            if (sinceSnap.incrementAndGet() >= 25) {
                sinceSnap.set(0);
                logServerSnap(log, dataFolder, wk, false);
            }
        }

        if (jsonLogEnabled() && log != null) {
            String shortU = u.toString().replace("-", "");
            if (shortU.length() > 8) shortU = shortU.substring(0, 8);
            log.info(String.format(Locale.ROOT,
                    "[P1 pf] {\"wk\":\"%s\",\"map\":\"%s\",\"win\":%d,\"ch\":%d,\"wall\":%d,\"whiff\":%d,\"break\":%d,"
                            + "\"evt\":%d,\"evt_ok\":%d,\"sig\":%d,\"alt\":%d,\"vb\":%d,\"u\":\"%s\"}",
                    wk, s.mapKey == null ? "" : s.mapKey.toLowerCase(Locale.ROOT), win ? 1 : 0, s.challenge ? 1 : 0,
                    wall, whiff, brk, evtRoll ? 1 : 0, evtOk ? 1 : 0, sigWear ? 1 : 0, sigAlt ? 1 : 0, vbHit ? 1 : 0, shortU));
        }
    }

    private static void bumpServer(String wk, String key, int n) {
        if (n <= 0) return;
        ConcurrentHashMap<String, AtomicInteger> m = SERVER.get(wk);
        if (m == null) {
            ConcurrentHashMap<String, AtomicInteger> created = new ConcurrentHashMap<String, AtomicInteger>();
            m = SERVER.putIfAbsent(wk, created);
            if (m == null) m = created;
        }
        AtomicInteger a = m.get(key);
        if (a == null) {
            AtomicInteger created = new AtomicInteger(0);
            a = m.putIfAbsent(key, created);
            if (a == null) a = created;
        }
        a.addAndGet(n);
    }

    private static int serverGet(String wk, String key) {
        ConcurrentHashMap<String, AtomicInteger> m = SERVER.get(wk);
        if (m == null) return 0;
        AtomicInteger a = m.get(key);
        return a == null ? 0 : a.get();
    }

    public static void logServerSnap(Logger log, File dataFolder, String wk, boolean forceFile) {
        if (wk == null) wk = weekKey();
        String line = formatServerLine(wk);
        if (log != null) log.info("[P1 pf] server-week " + line);
        if (dataFolder == null) return;
        File dir = new File(dataFolder, "p1-telemetry");
        if (!dir.exists() && !dir.mkdirs()) return;
        File f = new File(dir, wk + ".yml");
        YamlConfiguration y = new YamlConfiguration();
        y.set("week", wk);
        y.set("updated", System.currentTimeMillis());
        y.set("note", "D298 W1c in-memory aggregate since last restart; excludes leaderboard_exclude + telemetry.exclude_uuids");
        for (String k : KEYS) y.set("counts." + k, serverGet(wk, k));
        try { y.save(f); } catch (IOException ignored) { /* best-effort */ }
        if (forceFile && log != null) log.info("[P1 pf] wrote " + f.getPath());
    }

    static String formatServerLine(String wk) {
        int runs = serverGet(wk, C_RUNS);
        return String.format(Locale.ROOT,
                "%s runs=%d wall=%d whiff=%d break=%d evt_roll=%d evt_ok=%d sig_wear=%d sig_alt=%d vb=%d | "
                        + "wall/run=%.2f evt_rate=%.2f evt_ok=%.2f sig=%.2f alt/sig=%.2f",
                wk, runs, serverGet(wk, C_WALL), serverGet(wk, C_WHIFF), serverGet(wk, C_BREAK),
                serverGet(wk, C_EVT_ROLL), serverGet(wk, C_EVT_OK), serverGet(wk, C_SIG_WEAR),
                serverGet(wk, C_SIG_ALT), serverGet(wk, C_VB_HIT),
                per(serverGet(wk, C_WALL), runs), per(serverGet(wk, C_EVT_ROLL), runs),
                per(serverGet(wk, C_EVT_OK), Math.max(1, serverGet(wk, C_EVT_ROLL))),
                per(serverGet(wk, C_SIG_WEAR), runs),
                per(serverGet(wk, C_SIG_ALT), Math.max(1, serverGet(wk, C_SIG_WEAR))));
    }

    private static double per(int n, int d) { return d <= 0 ? 0.0 : (double) n / (double) d; }

    public static List<String> summaryLines(String label, Map<String, Integer> c) {
        List<String> out = new ArrayList<String>();
        int runs = get(c, C_RUNS);
        out.add("§6[遥测] §f" + label + " §7· 合格重打 §f" + runs + " §7局");
        out.add(String.format(Locale.ROOT, "§7破绽累加 §f墙%d §8/ §f落空%d §8/ §f破招%d §7· 局均 §f%.2f/%.2f/%.2f",
                get(c, C_WALL), get(c, C_WHIFF), get(c, C_BREAK),
                per(get(c, C_WALL), runs), per(get(c, C_WHIFF), runs), per(get(c, C_BREAK), runs)));
        int er = get(c, C_EVT_ROLL), eo = get(c, C_EVT_OK);
        out.add(String.format(Locale.ROOT, "§7事件 §f出房%d §7(%.0f%%) §8· §f成功%d §7(占出房 %.0f%%)",
                er, 100.0 * per(er, runs), eo, 100.0 * per(eo, Math.max(1, er))));
        int sw = get(c, C_SIG_WEAR), sa = get(c, C_SIG_ALT);
        out.add(String.format(Locale.ROOT, "§7签名 §f佩戴%d §7(%.0f%%) §8· §f调律%d §7(占佩戴 %.0f%% · 占局 %.0f%%)",
                sw, 100.0 * per(sw, runs), sa, 100.0 * per(sa, Math.max(1, sw)), 100.0 * per(sa, runs)));
        out.add(String.format(Locale.ROOT, "§7花样进度局 §f%d §7(%.0f%%)", get(c, C_VB_HIT), 100.0 * per(get(c, C_VB_HIT), runs)));
        return out;
    }

    private static int get(Map<String, Integer> c, String k) {
        Integer v = c == null ? null : c.get(k);
        return v == null ? 0 : Math.max(0, v.intValue());
    }

    public static Map<String, Integer> readWeek(PlayerData pd, String wk) {
        Map<String, Integer> m = new LinkedHashMap<String, Integer>();
        if (pd == null || wk == null) return m;
        for (String k : KEYS) m.put(k, Integer.valueOf(pd.periodCount(k, wk)));
        return m;
    }

    public static boolean cmd(CommandSender s, String[] args, CoreRpgPlugin plugin) {
        if (!s.hasPermission("corerpg.admin")) {
            s.sendMessage(ChatColor.RED + "需要 corerpg.admin");
            return true;
        }
        String P = ChatColor.GOLD + "[余烬·遥测] " + ChatColor.GRAY;
        if (args.length >= 3 && ("server".equalsIgnoreCase(args[2]) || "全服".equals(args[2]))) {
            String wk = weekKey();
            logServerSnap(plugin.getLogger(), plugin.getDataFolder(), wk, true);
            s.sendMessage(P + "全服本周（内存·已排除测试号）：§f" + formatServerLine(wk));
            return true;
        }
        OfflinePlayer target;
        if (args.length >= 3) {
            target = Bukkit.getOfflinePlayer(args[2]);
            if (target == null || (target.getName() == null && !target.hasPlayedBefore())) {
                s.sendMessage(P + "找不到玩家 §f" + args[2]);
                return true;
            }
        } else if (s instanceof Player) {
            target = (Player) s;
        } else {
            s.sendMessage(P + "用法：/corerpg p1 telemetry [玩家|server]");
            return true;
        }
        PlayerData pd = plugin.getDataStore().get(target.getUniqueId());
        if (pd == null) { s.sendMessage(P + "数据未加载"); return true; }
        String name = target.getName() != null ? target.getName() : target.getUniqueId().toString();
        String wk = weekKey();
        String prev = lastWeekKey();
        for (String line : summaryLines(name + " · 本周 " + wk, readWeek(pd, wk))) s.sendMessage(line);
        Map<String, Integer> last = readWeek(pd, prev);
        if (get(last, C_RUNS) > 0) {
            for (String line : summaryLines(name + " · 上周 " + prev, last)) s.sendMessage(line);
        } else {
            s.sendMessage("§8（上周无合格重打记录）");
        }
        s.sendMessage(P + "§8只读 · 非玩家面 · D298");
        return true;
    }
}
