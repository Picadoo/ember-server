package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * D141–D143 horizontal growth (P2 draft §5za): 天赋专精 (talent rows), 余烬勋记 (permanent account honors) and 词条洗练
 * (affix reroll). Pure rules over the parsed {@code ember-v1-growth.yml}; no Bukkit, unit-tested offline. The model
 * (tools/p1sim/growth.py) reads the same file and combines modifiers the same way ({@link Mods#combine}).
 */
public final class EmberGrowth {

    public static final String FILE = "ember-v1-growth.yml";

    /** additive keys (default 0); dodge_secs keeps the max, dodge_icd the min; share_w adds over a base of 1 */
    static final Set<String> ADD = new HashSet<String>(java.util.Arrays.asList(
            "dodge_secs", "dodge_heal", "dodge_icd", "burn_ticks", "burst_every", "sustain_every", "shard_bonus", "burn_spread", "dodge_burst", "spread_icd"));

    // ------------------------------------------------------------------ modifiers

    /** Immutable combined modifiers; {@link #get} returns the neutral value for an absent key. */
    public static final class Mods {
        public static final Mods NONE = new Mods(Collections.<String, Double>emptyMap());
        final Map<String, Double> m;
        Mods(Map<String, Double> m) { this.m = m; }
        public double get(String k) {
            Double v = m.get(k);
            if (v != null) return v;
            if ("share_w".equals(k)) return 1.0;
            if ("dodge_icd".equals(k)) return 6.0;
            return ADD.contains(k) ? 0.0 : 1.0;
        }
        public boolean has(String k) { return m.containsKey(k); }
        public boolean isEmpty() { return m.isEmpty(); }
        public Map<String, Double> view() { return Collections.unmodifiableMap(m); }

        public static Mods combine(List<Map<String, Double>> parts) {
            Map<String, Double> out = new LinkedHashMap<String, Double>();
            for (Map<String, Double> p : parts) {
                if (p == null) continue;
                for (Map.Entry<String, Double> e : p.entrySet()) {
                    String k = e.getKey();
                    double v = e.getValue();
                    Double cur = out.get(k);
                    if ("dodge_secs".equals(k)) out.put(k, cur == null ? v : Math.max(cur, v));
                    else if ("dodge_icd".equals(k) || "spread_icd".equals(k)) out.put(k, cur == null ? v : Math.min(cur, v));
                    else if (ADD.contains(k)) out.put(k, (cur == null ? 0.0 : cur) + v);
                    else if ("share_w".equals(k)) out.put(k, (cur == null ? 1.0 : cur) + (v - 1.0));
                    else out.put(k, (cur == null ? 1.0 : cur) * v);
                }
            }
            return out.isEmpty() ? NONE : new Mods(out);
        }

        @Override public String toString() { return m.toString(); }
    }

    // ------------------------------------------------------------------ talents (D141)

    public static final class PointSrc {
        public final String id, kind, arg, name;
        PointSrc(String id, String kind, String arg, String name) { this.id = id; this.kind = kind; this.arg = arg; this.name = name; }
    }

    public static final class Row {
        public final int row, points, coin;
        public final String name, theme;
        Row(int row, String name, int points, int coin, String theme) { this.row = row; this.name = name; this.points = points; this.coin = coin; this.theme = theme; }
    }

    public static final class Node {
        public final String id, name, family, set, good, bad;
        public final int row;
        public final Map<String, Double> mods;
        Node(String id, int row, String name, String family, String set, Map<String, Double> mods, String good, String bad) {
            this.id = id; this.row = row; this.name = name; this.family = family; this.set = set; this.mods = mods; this.good = good; this.bad = bad;
        }
        /** set-gated nodes only work while that set is active */
        public boolean activeWith(String activeSet) { return set == null || set.isEmpty() || set.equals(activeSet); }
    }

    public static final class Talents {
        public final int respecCoin;
        public final List<PointSrc> points;
        public final List<Row> rows;
        public final List<Node> nodes;
        Talents(int respecCoin, List<PointSrc> points, List<Row> rows, List<Node> nodes) {
            this.respecCoin = respecCoin; this.points = points; this.rows = rows; this.nodes = nodes;
        }
        public Node node(String id) {
            if (id == null) return null;
            for (Node n : nodes) if (n.id.equalsIgnoreCase(id)) return n;
            return null;
        }
        public Row row(int r) { for (Row x : rows) if (x.row == r) return x; return null; }
        public List<Node> inRow(int r) {
            List<Node> l = new ArrayList<Node>();
            for (Node n : nodes) if (n.row == r) l.add(n);
            return l;
        }
        public int maxPoints() { return points.size(); }
    }

    /** Pick state: the active node id per row (null = empty). */
    public static int pointsUsed(Talents t, Map<Integer, String> picks) {
        int n = 0;
        for (Map.Entry<Integer, String> e : picks.entrySet()) {
            if (e.getValue() == null) continue;
            Row r = t.row(e.getKey());
            if (r != null) n += r.points;
        }
        return n;
    }

    /** @return null when {@code nodeId} may be picked now (row empty, enough free points), else the reason (Chinese) */
    public static String canPick(Talents t, Map<Integer, String> picks, String nodeId, int pointsEarned) {
        Node n = t.node(nodeId);
        if (n == null) return "没有这个天赋：" + nodeId;
        String cur = picks.get(n.row);
        if (n.id.equals(cur)) return "已经点了「" + n.name + "」";
        Row r = t.row(n.row);
        if (r == null) return "天赋配置缺第 " + n.row + " 排";
        if (cur != null) return "第 " + n.row + " 排已经点了「" + t.node(cur).name + "」（每排只能点一个；想换先重置）";
        if (n.row > 1 && t.row(n.row - 1) != null && picks.get(n.row - 1) == null)
            return "先点第 " + (n.row - 1) + " 排（天赋树从上往下点）";
        int free = pointsEarned - pointsUsed(t, picks);
        if (free < r.points) return "专精点不够：第 " + n.row + " 排要 " + r.points + " 点，现在空 " + Math.max(0, free) + " 点";
        return null;
    }

    /** Talent modifiers for the active set (set-gated nodes drop out when the set does not match). */
    public static List<Map<String, Double>> talentParts(Talents t, Map<Integer, String> picks, String activeSet) {
        List<Map<String, Double>> l = new ArrayList<Map<String, Double>>();
        if (t == null) return l;
        for (String id : picks.values()) {
            Node n = t.node(id);
            if (n != null && n.activeWith(activeSet)) l.add(n.mods);
        }
        return l;
    }

    /** First respec free, then {@code respecCoin} each time. */
    public static int respecCost(Talents t, int resetsUsed) { return resetsUsed <= 0 ? 0 : t.respecCoin; }

    // ------------------------------------------------------------------ parsing (snakeyaml map)

    public static Talents parseTalents(Map<?, ?> root) {
        Object o = root == null ? null : root.get("talents");
        if (!(o instanceof Map)) return null;
        Map<?, ?> m = (Map<?, ?>) o;
        List<PointSrc> pts = new ArrayList<PointSrc>();
        for (Map<?, ?> x : maps(m.get("points"))) pts.add(new PointSrc(s(x, "id"), s(x, "kind"), s(x, "arg"), s(x, "name")));
        List<Row> rows = new ArrayList<Row>();
        for (Map<?, ?> x : maps(m.get("rows"))) rows.add(new Row(i(x, "row"), s(x, "name"), i(x, "points"), i(x, "coin"), s(x, "theme")));
        List<Node> nodes = new ArrayList<Node>();
        for (Map<?, ?> x : maps(m.get("nodes"))) nodes.add(new Node(s(x, "id"), i(x, "row"), s(x, "name"), s(x, "family"), s(x, "set"),
                mods(x.get("mods")), s(x, "good"), s(x, "bad")));
        return new Talents(i(m, "respec_coin"), Collections.unmodifiableList(pts), Collections.unmodifiableList(rows), Collections.unmodifiableList(nodes));
    }

    static List<Map<?, ?>> maps(Object o) {
        List<Map<?, ?>> l = new ArrayList<Map<?, ?>>();
        if (o instanceof List) for (Object x : (List<?>) o) if (x instanceof Map) l.add((Map<?, ?>) x);
        return l;
    }

    static Map<String, Double> mods(Object o) {
        Map<String, Double> out = new LinkedHashMap<String, Double>();
        if (o instanceof Map) for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet())
            if (e.getValue() instanceof Number) out.put(String.valueOf(e.getKey()), ((Number) e.getValue()).doubleValue());
        return Collections.unmodifiableMap(out);
    }

    static String s(Map<?, ?> m, String k) { Object v = m.get(k); return v == null ? null : String.valueOf(v); }

    static int i(Map<?, ?> m, String k) {
        Object v = m.get(k);
        if (v instanceof Number) return ((Number) v).intValue();
        try { return v == null ? 0 : Integer.parseInt(String.valueOf(v).trim()); } catch (NumberFormatException e) { return 0; }
    }

    static double d(Map<?, ?> m, String k, double def) {
        Object v = m.get(k);
        if (v instanceof Number) return ((Number) v).doubleValue();
        try { return v == null ? def : Double.parseDouble(String.valueOf(v).trim()); } catch (NumberFormatException e) { return def; }
    }

    /** one-line text of a modifier set, e.g. for logs / admin */
    public static String describe(Map<String, Double> m) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Double> e : m.entrySet()) sb.append(sb.length() == 0 ? "" : " ").append(e.getKey()).append('=')
                .append(String.format(Locale.ROOT, "%.3f", e.getValue()));
        return sb.toString();
    }

    private EmberGrowth() {}
}
