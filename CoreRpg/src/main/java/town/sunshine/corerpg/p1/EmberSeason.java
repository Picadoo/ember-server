package town.sunshine.corerpg.p1;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;

/**
 * D116 seasons + D117 weekly goals (display / cosmetic only, book §23.3).
 * <ul>
 * <li>Weekly boards (period "w&lt;week&gt;", reset Monday 00:00 Asia/Shanghai): best abyss tier cleared, featured
 * challenge clears, raid clears, fastest R01 / R02 clear. Season boards (period "s&lt;n&gt;"): the same over the
 * season ({@code season.weeks} weeks from {@code season.anchor}).</li>
 * <li>Season end: the top {@code season.top} of the abyss / featured / raid boards, of either raid-time board, and every
 * player who cleared abyss tier {@code season.deep_tier} inside the season get season titles; #1 of any board also the
 * ❖ nameplate frame. Results go to p1-runs/season-archive/S&lt;n&gt;.yml. Offline winners get them at the next login.</li>
 * <li>Weekly goals (own Q07 first clear): targets from {@code weekly_goals.targets}; each pays {@code reward} 余烬徽,
 * all of them {@code bonus} more. 余烬徽 only buys cosmetic-shop items.</li>
 * </ul>
 * State: p1-runs/season.yml (runtime, gitignored). Accounts in leaderboard_exclude never rank and never get awards.
 */
public final class EmberSeason {

    public static final String C_GOAL = "p3_goal_";       // + goal id, period = week key: progress
    public static final String C_GOALPAY = "p3_goalpay_"; // + goal id | "all", period = week key: paid flag
    public static final String C_BADGE = "p3_badge";      // period "all": 余烬徽 balance
    public static final String C_GRAD = "p3_grad";        // period "all": epoch day of the own Q07 first clear (D134)
    public static final String C_AWARD = "p3_season_";    // + award id, period "all": times earned
    public static final String C_AWARD_LAST = "p3_seasonlast_"; // + award id, period "all": last season number
    public static final List<String> GOALS = Collections.unmodifiableList(Arrays.asList("featured", "abyss", "raid", "bounty"));
    public static final List<String> BOARDS = Collections.unmodifiableList(Arrays.asList("abyss", "featured", "raids", "time_r01", "time_r02"));
    private static final String P = "§6[余烬] §7";

    public static final class Row {
        public final String name;
        public final int value;
        public final long at;
        /** F-review #4 (D123): abyss board only — fastest clear (seconds) at the best tier, 0 = unknown; ties go to the faster */
        public final int secs;
        Row(String name, int value, long at) { this(name, value, at, 0); }
        Row(String name, int value, long at, int secs) { this.name = name; this.value = value; this.at = at; this.secs = secs; }
    }

    private final EmberRunService runs;
    private final Logger log;
    private final File file, archiveDir;
    /** period → board → uuid → row */
    private final Map<String, Map<String, Map<String, Row>>> boards = new LinkedHashMap<String, Map<String, Map<String, Row>>>();
    /** uuid → award ids still to hand out ("season_abyss:3" = earned in season 3) */
    private final Map<String, List<String>> pending = new LinkedHashMap<String, List<String>>();
    /** season number → archived lines (top per board) */
    private final Map<Integer, List<String>> archive = new LinkedHashMap<Integer, List<String>>();
    private int finalized = -1;

    public EmberSeason(EmberRunService runs, File dataFolder, Logger log) {
        this.runs = runs;
        this.log = log;
        this.file = new File(new File(dataFolder, "p1-runs"), "season.yml");
        this.archiveDir = new File(new File(dataFolder, "p1-runs"), "season-archive");
        load();
        if (finalized < 0) { finalized = seasonOf(today()) - 1; save(); } // first start: nothing from before to settle
    }

    // ------------------------------------------------------------------ calendar

    static LocalDate today() { return LocalDate.now(town.sunshine.corerpg.DailyService.zone()); }

    private EmberRunMaps maps() { return runs.maps(); }

    long anchorWeek() {
        LocalDate a;
        try { a = LocalDate.parse(maps().seasonAnchor); } catch (Exception e) { a = LocalDate.of(2026, 9, 28); }
        return EmberRunRules.weekIndex(a);
    }

    public int seasonOf(LocalDate d) { return seasonOf(anchorWeek(), maps().seasonWeeks, d); }

    /** Monday 00:00 that starts season n */
    public LocalDate seasonStart(int n) { return seasonStart(anchorWeek(), maps().seasonWeeks, n); }

    /** 1-based week inside the current season */
    public int weekInSeason(LocalDate d) { return weekInSeason(anchorWeek(), maps().seasonWeeks, d); }

    static int seasonOf(long anchorWeek, int weeks, LocalDate d) { return (int) Math.floorDiv(EmberRunRules.weekIndex(d) - anchorWeek, (long) weeks) + 1; }

    static LocalDate seasonStart(long anchorWeek, int weeks, int n) { return LocalDate.ofEpochDay((anchorWeek + (long) (n - 1) * weeks) * 7 - 3); }

    static int weekInSeason(long anchorWeek, int weeks, LocalDate d) { return (int) Math.floorMod(EmberRunRules.weekIndex(d) - anchorWeek, (long) weeks) + 1; }

    static String weekKey(LocalDate d) { return "w" + EmberRunRules.weekIndex(d); }

    static String seasonKey(int n) { return "s" + n; }

    static boolean lowerBetter(String board) { return board.startsWith("time_"); }

    // ------------------------------------------------------------------ recording (called from settlement)

    void onAbyss(UUID u, String name, int tier) { onAbyss(u, name, tier, 0); }

    /** F-review #4 (D123): best tier, then the fastest clear at that tier (a later, faster clear moves you up) */
    void onAbyss(UUID u, String name, int tier, int secs) { record(u, name, "abyss", tier, MAX, secs); }

    void onFeatured(UUID u, String name) { record(u, name, "featured", 1, ADD); }

    void onRaid(UUID u, String name, String raid, int seconds) {
        record(u, name, "raids", 1, ADD);
        if (seconds > 0 && ("r01".equals(raid) || "r02".equals(raid))) record(u, name, "time_" + raid, seconds, MIN);
    }

    private static final int MAX = 0, ADD = 1, MIN = 2;

    private void record(UUID u, String name, String board, int v, int mode) { record(u, name, board, v, mode, 0); }

    private synchronized void record(UUID u, String name, String board, int v, int mode, int secs) {
        if (u == null || name == null) return;
        if (EmberMode.boardExcluded(name)) { log.info("[P1 season] " + name + " " + board + "=" + v + " not ranked (leaderboard_exclude)"); return; }
        LocalDate d = today();
        for (String period : new String[]{weekKey(d), seasonKey(seasonOf(d))}) {
            Map<String, Row> b = boards.computeIfAbsent(period, k -> new LinkedHashMap<String, Map<String, Row>>())
                    .computeIfAbsent(board, k -> new LinkedHashMap<String, Row>());
            Row r = b.get(u.toString());
            int nv = r == null ? v : mode == ADD ? r.value + v : mode == MAX ? Math.max(r.value, v) : Math.min(r.value, v);
            long at = r != null && r.value == nv ? r.at : System.currentTimeMillis();
            int ns = bestSecs(r, nv, v, secs);
            if (r != null && r.value == nv && ns != r.secs && ns > 0) at = System.currentTimeMillis();
            b.put(u.toString(), new Row(name, nv, at, ns));
        }
        save();
    }

    /** the time kept for the row: a new best tier takes its own time; the same tier keeps the faster one */
    static int bestSecs(Row old, int newValue, int v, int secs) {
        int os = old == null ? 0 : old.secs;
        if (old == null || newValue > old.value) return v == newValue ? Math.max(0, secs) : 0;
        if (v < newValue || secs <= 0) return os;
        return os <= 0 ? secs : Math.min(os, secs);
    }

    /** board order: value, then (abyss) the faster clear with unknown times last, then who got there first */
    static int compareRows(String board, Row a, Row b) {
        boolean low = lowerBetter(board);
        if (a.value != b.value) return low ? Integer.compare(a.value, b.value) : Integer.compare(b.value, a.value);
        if ("abyss".equals(board) && a.secs != b.secs) {
            if (a.secs <= 0) return 1;
            if (b.secs <= 0) return -1;
            return Integer.compare(a.secs, b.secs);
        }
        return a.at != b.at ? Long.compare(a.at, b.at) : a.name.compareTo(b.name);
    }

    public synchronized List<Row> top(String period, String board, int n) {
        List<Row> rows = sorted(period, board);
        return rows.size() > n ? new ArrayList<Row>(rows.subList(0, n)) : rows;
    }

    private List<Row> sorted(String period, String board) {
        Map<String, Map<String, Row>> p = boards.get(period);
        List<Row> rows = new ArrayList<Row>(p == null || p.get(board) == null ? Collections.<Row>emptyList() : p.get(board).values());
        rows.removeIf(r -> EmberMode.boardExcluded(r.name));
        rows.sort((a, b) -> compareRows(board, a, b));
        return rows;
    }

    /** the current week's board (the one leaderboard set: hub board, PAPI top_* and /corerpg p1 top all read it) */
    public List<Row> weekTop(String board, int n) { return top(weekKey(today()), board, n); }

    public int[] weekRank(UUID u, String board) { return rankOf(u, weekKey(today()), board); }

    /** {rank, value} or null */
    public synchronized int[] rankOf(UUID u, String period, String board) {
        Map<String, Map<String, Row>> p = boards.get(period);
        Row mine = p == null || p.get(board) == null ? null : p.get(board).get(u.toString());
        if (mine == null) return null;
        int i = sorted(period, board).indexOf(mine);
        return i < 0 ? null : new int[]{i + 1, mine.value};
    }

    public static String boardName(String b) {
        switch (b) {
            case "abyss": return "深渊最高层";
            case "featured": return "精选挑战通关";
            case "raids": return "团本通关";
            case "time_r01": return "R01 最快通关";
            case "time_r02": return "R02 最快通关";
            default: return b;
        }
    }

    /** F-review #4: abyss rows show the time that breaks the tie (「第 10 层 · 6 分 05 秒」) */
    public static String rowText(String b, Row r) {
        return valueText(b, r.value) + ("abyss".equals(b) && r.secs > 0 ? " · " + (r.secs / 60) + " 分 " + String.format(Locale.ROOT, "%02d", r.secs % 60) + " 秒" : "");
    }

    public static String valueText(String b, int v) {
        if (lowerBetter(b)) return (v / 60) + " 分 " + String.format(Locale.ROOT, "%02d", v % 60) + " 秒";
        return "abyss".equals(b) ? "第 " + v + " 层" : v + " 次";
    }

    // ------------------------------------------------------------------ season end

    /** every minute: settle each season that has ended (normally at most one), prune old weekly boards */
    public void tick() {
        LocalDate d = today();
        int cur = seasonOf(d);
        int guard = 0;
        while (finalized < cur - 1 && guard++ < 4) finalizeSeason(finalized + 1);
        long wk = EmberRunRules.weekIndex(d);
        boolean pruned = false;
        synchronized (this) {
            for (String k : new ArrayList<String>(boards.keySet())) {
                if (k.startsWith("w")) {
                    try { if (Long.parseLong(k.substring(1)) < wk - 1) { boards.remove(k); pruned = true; } } catch (NumberFormatException ignored) { }
                }
            }
        }
        if (pruned) save();
    }

    static final String[][] AWARD_OF = {{"abyss", "season_abyss"}, {"featured", "season_featured"}, {"raids", "season_raids"},
            {"time_r01", "season_fast"}, {"time_r02", "season_fast"}};

    synchronized void finalizeSeason(int n) { finalizeSeason(n, null); }

    /** preview != null: admin dry run — same ranking, nothing awarded / archived / removed, lines go to the sender only */
    synchronized List<String> finalizeSeason(int n, org.bukkit.command.CommandSender preview) {
        String sk = seasonKey(n);
        Map<String, Set<String>> awards = new LinkedHashMap<String, Set<String>>();
        Map<String, String> names = new LinkedHashMap<String, String>();
        List<String> lines = new ArrayList<String>();
        int topN = maps().seasonTop;
        for (String[] ba : AWARD_OF) {
            List<Row> rows = sorted(sk, ba[0]);
            Map<String, Row> raw = boards.containsKey(sk) && boards.get(sk).get(ba[0]) != null ? boards.get(sk).get(ba[0]) : Collections.<String, Row>emptyMap();
            StringBuilder line = new StringBuilder(boardName(ba[0]) + "：");
            if (rows.isEmpty()) line.append("—");
            for (int i = 0; i < Math.min(topN, rows.size()); i++) {
                Row r = rows.get(i);
                String uid = uidOf(raw, r);
                if (uid == null) continue;
                names.put(uid, r.name);
                awards.computeIfAbsent(uid, k -> new LinkedHashSet<String>()).add(ba[1]);
                if (i == 0) awards.get(uid).add("season_crown");
                line.append(i == 0 ? "" : " ｜ ").append(i + 1).append(". ").append(r.name).append(" ").append(rowText(ba[0], r));
            }
            lines.add(line.toString());
        }
        Map<String, Row> deep = boards.containsKey(sk) && boards.get(sk).get("abyss") != null ? boards.get(sk).get("abyss") : Collections.<String, Row>emptyMap();
        int deepN = 0;
        for (Map.Entry<String, Row> e : deep.entrySet()) {
            if (e.getValue().value >= maps().seasonDeepTier && !EmberMode.boardExcluded(e.getValue().name)) {
                awards.computeIfAbsent(e.getKey(), k -> new LinkedHashSet<String>()).add("season_deep");
                names.put(e.getKey(), e.getValue().name);
                deepN++;
            }
        }
        lines.add("深渊第 " + maps().seasonDeepTier + " 层及以上：" + deepN + " 人");
        if (preview != null) {
            List<String> who = new ArrayList<String>();
            for (Map.Entry<String, Set<String>> e : awards.entrySet()) who.add(names.get(e.getKey()) + "=" + e.getValue());
            preview.sendMessage(P + "§d第 " + n + " 赛季结算预览§7（不发奖、不归档）：" + String.join("；", lines));
            preview.sendMessage(P + "§7会得奖：" + (who.isEmpty() ? "无" : String.join("；", who)));
            return lines;
        }
        for (Map.Entry<String, Set<String>> e : awards.entrySet()) {
            List<String> l = pending.computeIfAbsent(e.getKey(), k -> new ArrayList<String>());
            for (String id : e.getValue()) l.add(id + ":" + n);
        }
        archive.put(n, lines);
        // archive file (one per season, kept forever)
        try {
            archiveDir.mkdirs();
            YamlConfiguration y = new YamlConfiguration();
            y.set("season", n);
            y.set("from", seasonStart(n).toString());
            y.set("to", seasonStart(n + 1).minusDays(1).toString());
            y.set("settled_at", System.currentTimeMillis());
            y.set("lines", lines);
            Map<String, Map<String, Row>> sb = boards.get(sk);
            if (sb != null) for (Map.Entry<String, Map<String, Row>> b : sb.entrySet()) {
                List<Row> rows = sorted(sk, b.getKey());
                List<String> top = new ArrayList<String>();
                for (int i = 0; i < Math.min(10, rows.size()); i++) top.add((i + 1) + ". " + rows.get(i).name + " " + rows.get(i).value);
                y.set("boards." + b.getKey(), top);
            }
            for (Map.Entry<String, Set<String>> e : awards.entrySet()) y.set("awards." + e.getKey(), new ArrayList<String>(e.getValue()));
            for (Map.Entry<String, String> e : names.entrySet()) y.set("names." + e.getKey(), e.getValue());
            y.save(new File(archiveDir, "S" + n + ".yml"));
        } catch (Exception ex) {
            log.warning("[P1 season] archive S" + n + " failed: " + ex.getMessage());
        }
        boards.remove(sk);
        finalized = n;
        save();
        log.info("[P1 season] S" + n + " settled: " + awards.size() + " players awarded · " + String.join(" / ", lines));
        Bukkit.broadcastMessage(P + "§d第 " + n + " 赛季结算§7：" + String.join("；", lines) + " §8（只做展示，奖励是称号 / 名牌框）");
        for (Player p : Bukkit.getOnlinePlayers()) apply(p);
        return lines;
    }

    private static String uidOf(Map<String, Row> raw, Row r) {
        for (Map.Entry<String, Row> e : raw.entrySet()) if (e.getValue() == r) return e.getKey();
        return null;
    }

    /** hand out pending season awards (login, or right after the season settles) */
    public void apply(Player p) {
        List<String> l;
        synchronized (this) { l = pending.remove(p.getUniqueId().toString()); if (l != null) save(); }
        if (l == null || l.isEmpty()) return;
        PlayerData d = runs.dataOf(p.getUniqueId());
        for (String s : l) {
            int i = s.lastIndexOf(':');
            String id = i < 0 ? s : s.substring(0, i);
            int n = 0;
            try { n = i < 0 ? 0 : Integer.parseInt(s.substring(i + 1)); } catch (NumberFormatException ignored) { }
            d.addPeriodCount(C_AWARD + id, "all", 1);
            d.addPeriodCount(C_AWARD_LAST + id, "all", n - d.periodCount(C_AWARD_LAST + id, "all"));
            EmberCosmetics.Cosmetic c = EmberCosmetics.byId(id);
            p.sendMessage(P + "§d第 " + n + " 赛季奖励：" + (c == null ? id : (c.kind == EmberCosmetics.Kind.FLAIR ? "名牌框 " : "称号 ") + c.label)
                    + "§7（只做展示，主菜单「赛季 · 排行 · 周目标」右键装上）");
        }
        runs.flushData(p.getUniqueId());
    }

    public static int lastSeason(PlayerData d, String id) { return d == null ? 0 : d.periodCount(C_AWARD_LAST + id, "all"); }

    // ------------------------------------------------------------------ weekly goals

    public int target(String goal) {
        Integer t = maps().goalTargets.get(goal);
        return t == null ? 0 : t;
    }

    public boolean goalsOn() { return !maps().goalTargets.isEmpty(); }

    /**
     * Recheck #8 (D134): the bounty goal ("做满每日委托 N 天") in the week of the Q07 first clear only asks for the days
     * left that week, the graduation day included (Saturday → 2, Sunday → 1); later weeks ask for the full target.
     */
    public int target(PlayerData d, String goal) {
        int t = target(goal);
        if (t <= 0 || d == null || !"bounty".equals(goal)) return t;
        int gd = d.periodCount(C_GRAD, "all");
        if (gd <= 0) return t;
        LocalDate g = LocalDate.ofEpochDay(gd);
        return weekKey(g).equals(weekKey(today())) ? proratedTarget(t, g.getDayOfWeek().getValue()) : t;
    }

    /** D134: days from the graduation weekday (1 = Monday … 7 = Sunday) to Sunday, inclusive, capped by the target */
    static int proratedTarget(int target, int gradDow) {
        return Math.max(1, Math.min(target, 8 - gradDow));
    }

    /** D134: remember the graduation day once (called on the real Q07 first clear) */
    void markGraduated(PlayerData d) {
        if (d != null && d.periodCount(C_GRAD, "all") == 0) d.addPeriodCount(C_GRAD, "all", (int) today().toEpochDay());
    }

    public int progress(PlayerData d, String goal) { return d == null ? 0 : d.periodCount(C_GOAL + goal, weekKey(today())); }

    public static int badges(PlayerData d) { return d == null ? 0 : d.periodCount(C_BADGE, "all"); }

    public int goalsDone(PlayerData d) {
        int n = 0;
        for (String g : GOALS) if (target(d, g) > 0 && progress(d, g) >= target(d, g)) n++;
        return n;
    }

    public int goalCount() {
        int n = 0;
        for (String g : GOALS) if (target(g) > 0) n++;
        return n;
    }

    public static String goalName(String g) {
        switch (g) {
            case "featured": return "通关本周精选图的挑战版（带本周规则）";
            case "abyss": return "通关深渊层（任意层）";
            case "raid": return "通关团本（R01 或 R02）";
            case "bounty": return "做满每日委托（当天第 3 局）";
            default: return g;
        }
    }

    public static String goalOpen(String g) {
        switch (g) {
            case "featured": return "/ember_p1_challenge";
            case "abyss": return "/ember_p1_abyss";
            default: return "/ember_p1_adventure";
        }
    }

    /** settlement hook: n more toward goal g this week; pays when it completes */
    void addGoal(UUID u, PlayerData d, String g, int n) {
        int t = target(d, g);
        if (d == null || t <= 0 || n <= 0 || !runs.progressFlag(d, "q07")) return;
        String wk = weekKey(today());
        int before = d.periodCount(C_GOAL + g, wk);
        if (before >= t) return;
        int after = d.addPeriodCount(C_GOAL + g, wk, Math.min(n, t - before));
        Player p = Bukkit.getPlayer(u);
        if (after >= t && d.periodCount(C_GOALPAY + g, wk) == 0) {
            d.addPeriodCount(C_GOALPAY + g, wk, 1);
            int pay = maps().goalReward;
            if (pay > 0) d.addPeriodCount(C_BADGE, "all", pay);
            if (p != null) p.sendMessage(P + "§a周目标完成：" + goalName(g) + " §7· 余烬徽 +" + pay + "（共 " + badges(d) + "，外观商店用）· 本周 "
                    + goalsDone(d) + "/" + goalCount());
            if (goalsDone(d) >= goalCount() && d.periodCount(C_GOALPAY + "all", wk) == 0) {
                d.addPeriodCount(C_GOALPAY + "all", wk, 1);
                int b = maps().goalBonus;
                if (b > 0) d.addPeriodCount(C_BADGE, "all", b);
                if (p != null) p.sendMessage(P + "§d本周目标全部完成！§7余烬徽 +" + b + "（共 " + badges(d) + "）");
            }
            Bukkit.getLogger().info("[P1 goals] " + u + " " + g + " done " + wk);
        } else if (p != null) {
            p.sendMessage(P + "§7周目标：" + goalName(g) + " §f" + after + "/" + t);
        }
    }

    public String goalLine(PlayerData d, String g) {
        int t = target(d, g);
        if (t <= 0) return "";
        int v = Math.min(t, progress(d, g));
        return (v >= t ? "§a✔ " : "§e") + goalName(g) + " §f" + v + "/" + t + (t < target(g) ? " §8（毕业这周按剩下的天数）" : "") + (v >= t ? "" : " §7· 完成得 " + maps().goalReward + " 余烬徽");
    }

    /** /corerpg p1 goals: progress + one [打开] button per open goal */
    public boolean goalsCommand(Player p) {
        PlayerData d = runs.dataOf(p.getUniqueId());
        if (!goalsOn()) { p.sendMessage(P + "周目标未开放。"); return true; }
        LocalDate t = today();
        int left = (int) (7 - (t.getDayOfWeek().getValue() - 1));
        p.sendMessage(P + "§6本周目标§7（周一 0 点刷新，还剩 " + left + " 天）· 完成 " + goalsDone(d) + "/" + goalCount() + " · 余烬徽 §f" + badges(d));
        if (!runs.progressFlag(d, "q07")) { p.sendMessage(P + "§8首通 Q07 后才计数（挑战版、深渊、团本都在 Q07 之后）。"); return true; }
        p.sendMessage(P + "§8首通 Q07 后才计数；首通 Q07 当天已做满的每日委托也算；毕业那周的委托目标按剩下的天数算（周六毕业要 2 天，周日 1 天）");
        for (String g : GOALS) {
            if (target(g) <= 0) continue;
            boolean done = progress(d, g) >= target(d, g);
            net.md_5.bungee.api.chat.TextComponent line = new net.md_5.bungee.api.chat.TextComponent(P + goalLine(d, g) + " ");
            if (!done) {
                net.md_5.bungee.api.chat.TextComponent b = new net.md_5.bungee.api.chat.TextComponent("§b[打开]");
                b.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, goalOpen(g)));
                b.setHoverEvent(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
                        new net.md_5.bungee.api.chat.ComponentBuilder("§7打开对应的页面").create()));
                line.addExtra(b);
            }
            p.spigot().sendMessage(line);
        }
        p.sendMessage(P + "§7全部完成再 +" + maps().goalBonus + " 余烬徽 · 余烬徽只能在外观商店换外观（1 徽 = 1 点 = 50 币标价），不加属性");
        town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P, new String[]{"[赛季与周目标页]", "/ember_p1_season", "排行榜、周目标、赛季奖励", "GOLD"},
                new String[]{"[外观商店]", "/ember_p1_shop", "用余烬徽 / 币 / 印记换外观", "AQUA"});
        return true;
    }

    // ------------------------------------------------------------------ /corerpg p1 season

    public String seasonLabel() {
        LocalDate t = today();
        int n = seasonOf(t);
        long days = seasonStart(n + 1).toEpochDay() - t.toEpochDay();
        return "第 " + n + " 赛季 · 第 " + weekInSeason(t) + "/" + maps().seasonWeeks + " 周 · 还剩 " + days + " 天";
    }

    public boolean seasonCommand(Player p, String[] args) {
        String op = args.length >= 3 ? args[2].toLowerCase(Locale.ROOT) : "";
        LocalDate t = today();
        int n = seasonOf(t);
        if ("last".equals(op)) {
            int ln = lastArchived();
            List<String> l = archive.get(ln);
            if (l == null) { p.sendMessage(P + "还没有结算过的赛季。"); return true; }
            p.sendMessage(P + "§d第 " + ln + " 赛季结果");
            for (String s : l) p.sendMessage(P + "§f" + s);
            return true;
        }
        boolean week = !"season".equals(op);
        String period = week ? weekKey(t) : seasonKey(n);
        p.sendMessage(P + "§6" + (week ? "本周榜" : "赛季榜") + " §7" + seasonLabel() + "（只做展示）");
        for (String b : BOARDS) {
            List<Row> rows = top(period, b, 5);
            StringBuilder s = new StringBuilder("§e" + boardName(b) + "§7：");
            if (rows.isEmpty()) s.append("暂无");
            for (int i = 0; i < rows.size(); i++) s.append(i == 0 ? "" : " ｜ ").append("§f").append(i + 1).append(". ").append(rows.get(i).name).append(" §7").append(rowText(b, rows.get(i)));
            int[] me = rankOf(p.getUniqueId(), period, b);
            s.append(me != null ? " §8（你：第 " + me[0] + " 名）" : " §8（你：未上榜" + ("abyss".equals(b) ? "，" + abyssMine(runs.abyssBest(runs.dataOf(p.getUniqueId()))) : "") + "）");
            p.sendMessage(P + s);
        }
        p.sendMessage(P + "§8赛季末：每榜前 " + maps().seasonTop + " 名和赛季内通关深渊第 " + maps().seasonDeepTier + " 层的人得赛季称号，各榜第 1 名再得 ❖ 名牌框 · 测试号不上榜");
        town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P,
                new String[]{week ? "[看赛季榜]" : "[看本周榜]", "/corerpg p1 season " + (week ? "season" : "week"), "切换榜单", "YELLOW"},
                new String[]{"[上赛季结果]", "/corerpg p1 season last", "", "LIGHT_PURPLE"},
                new String[]{"[周目标]", "/corerpg p1 goals", "", "GREEN"});
        return true;
    }

    /** PAPI: %corerpg_p1_sboard_<w|s>_<board>_<i>% and %corerpg_p1_srank_<w|s>_<board>% */
    public String papi(UUID u, PlayerData d, String key) {
        LocalDate t = today();
        if ("season".equals(key)) return seasonLabel();
        if ("badges".equals(key)) return String.valueOf(badges(d));
        if ("goals".equals(key)) return runs.progressFlag(d, "q07") ? goalsDone(d) + "/" + goalCount() : "首通 Q07 后开放";
        if (key.startsWith("goal_")) {
            String g = key.substring(5);
            if (!runs.progressFlag(d, "q07")) return "§8首通 Q07 后开放";
            String l = goalLine(d, g);
            return l.isEmpty() ? "—" : l;
        }
        if ("season_last".equals(key)) {
            List<String> l = archive.get(lastArchived());
            return l == null ? "还没有结算过的赛季" : String.join(" · ", l.subList(0, Math.min(2, l.size())));
        }
        if (key.startsWith("sboard_") || key.startsWith("srank_")) {
            boolean rank = key.startsWith("srank_");
            String rest = key.substring(rank ? 6 : 7);
            if (rest.length() < 3) return "";
            String period = rest.charAt(0) == 'w' ? weekKey(t) : seasonKey(seasonOf(t));
            rest = rest.substring(2);
            if (rank) {
                int[] me = u == null ? null : rankOf(u, period, rest);
                if (me == null) return "abyss".equals(rest) ? "未上榜（" + abyssMine(runs.abyssBest(d)) + "）" : "未上榜";
                return "第 " + me[0] + " 名 · " + valueText(rest, me[1]);
            }
            int us = rest.lastIndexOf('_');
            if (us < 0) return "";
            int i;
            try { i = Integer.parseInt(rest.substring(us + 1)); } catch (NumberFormatException e) { return ""; }
            String b = rest.substring(0, us);
            List<Row> rows = top(period, b, i);
            return rows.size() < i ? "—" : rows.get(i - 1).name + " · " + rowText(b, rows.get(i - 1));
        }
        return null;
    }

    private synchronized int lastArchived() {
        int m = -1;
        for (Integer k : archive.keySet()) m = Math.max(m, k);
        return m;
    }

    /** admin test: queue an award for a player (handed out at once when online) */
    void adminAward(Player p, String id) {
        synchronized (this) { pending.computeIfAbsent(p.getUniqueId().toString(), k -> new ArrayList<String>()).add(id + ":" + seasonOf(today())); save(); }
        apply(p);
    }

    // ------------------------------------------------------------------ persistence

    private synchronized void load() {
        if (!file.isFile()) return;
        YamlConfiguration y = YamlConfiguration.loadConfiguration(file);
        finalized = y.getInt("finalized", -1);
        ConfigurationSection b = y.getConfigurationSection("boards");
        if (b != null) for (String period : b.getKeys(false)) {
            ConfigurationSection ps = b.getConfigurationSection(period);
            if (ps == null) continue;
            for (String board : ps.getKeys(false)) {
                ConfigurationSection bs = ps.getConfigurationSection(board);
                if (bs == null) continue;
                Map<String, Row> m = boards.computeIfAbsent(period, k -> new LinkedHashMap<String, Map<String, Row>>())
                        .computeIfAbsent(board, k -> new LinkedHashMap<String, Row>());
                for (String u : bs.getKeys(false))
                    m.put(u, new Row(bs.getString(u + ".name", "?"), bs.getInt(u + ".value"), bs.getLong(u + ".at"), bs.getInt(u + ".secs", 0)));
            }
        }
        ConfigurationSection pe = y.getConfigurationSection("pending");
        if (pe != null) for (String u : pe.getKeys(false)) pending.put(u, new ArrayList<String>(pe.getStringList(u)));
        ConfigurationSection ar = y.getConfigurationSection("archive");
        if (ar != null) for (String k : ar.getKeys(false)) {
            try { archive.put(Integer.parseInt(k), new ArrayList<String>(ar.getStringList(k))); } catch (NumberFormatException ignored) { }
        }
    }

    private synchronized void save() {
        YamlConfiguration y = new YamlConfiguration();
        y.set("finalized", finalized);
        for (Map.Entry<String, Map<String, Map<String, Row>>> p : boards.entrySet())
            for (Map.Entry<String, Map<String, Row>> b : p.getValue().entrySet())
                for (Map.Entry<String, Row> r : b.getValue().entrySet()) {
                    String k = "boards." + p.getKey() + "." + b.getKey() + "." + r.getKey();
                    y.set(k + ".name", r.getValue().name);
                    y.set(k + ".value", r.getValue().value);
                    y.set(k + ".at", r.getValue().at);
                    if (r.getValue().secs > 0) y.set(k + ".secs", r.getValue().secs);
                }
        for (Map.Entry<String, List<String>> e : pending.entrySet()) y.set("pending." + e.getKey(), e.getValue());
        for (Map.Entry<Integer, List<String>> e : archive.entrySet()) y.set("archive." + e.getKey(), e.getValue());
        try {
            file.getParentFile().mkdirs();
            y.save(file);
        } catch (Exception ex) {
            log.warning("[P1 season] save failed: " + ex.getMessage());
        }
    }

    /** admin: preview the current season's settlement (dry run, nothing changes) */
    void adminPreview(org.bukkit.command.CommandSender to) { finalizeSeason(seasonOf(today()), to); }
    static String abyssMine(int best) {
        return best <= 0 ? "还没打过深渊" : "深渊最高第 " + best + " 层";
    }
}
