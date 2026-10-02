package town.sunshine.corerpg.p1;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * P2-10 (D84) leaderboards, display only: abyss best tier (all time) and featured-map challenge clears this rotation
 * week. Kept in p1-runs/leaderboard.yml (runtime, gitignored), written on change; offline players stay listed.
 */
public final class EmberLeaderboard {

    public static final class Row {
        public final String name;
        public final int value;
        /** D102: when this value was first reached (epoch ms) — ties go to whoever got there first */
        public final long at;
        Row(String name, int value, long at) { this.name = name; this.value = value; this.at = at; }
    }

    private final File file;
    private final Logger log;
    private final Map<String, Row> abyss = new LinkedHashMap<String, Row>();
    private final Map<String, Row> featured = new LinkedHashMap<String, Row>();
    private String week = "";

    public EmberLeaderboard(File dataFolder, Logger log) {
        this.file = new File(new File(dataFolder, "p1-runs"), "leaderboard.yml");
        this.log = log;
        load();
    }

    private void load() {
        if (!file.isFile()) return;
        YamlConfiguration y = YamlConfiguration.loadConfiguration(file);
        week = y.getString("featured_week", "");
        read(y.getConfigurationSection("abyss"), abyss);
        read(y.getConfigurationSection("featured"), featured);
        purgeExcluded();
    }

    /** D102: drop test / bot accounts (EmberMode leaderboard_exclude) from the stored boards; true if any went */
    public synchronized boolean purgeExcluded() {
        boolean gone = false;
        for (Map<String, Row> m : java.util.Arrays.asList(abyss, featured))
            for (java.util.Iterator<Row> it = m.values().iterator(); it.hasNext(); )
                if (EmberMode.boardExcluded(it.next().name)) { it.remove(); gone = true; }
        if (gone) save();
        return gone;
    }

    private static void read(ConfigurationSection sec, Map<String, Row> into) {
        if (sec == null) return;
        for (String k : sec.getKeys(false)) into.put(k, new Row(sec.getString(k + ".name", "?"), sec.getInt(k + ".value"), sec.getLong(k + ".at", 0L)));
    }

    private void save() {
        YamlConfiguration y = new YamlConfiguration();
        y.set("featured_week", week);
        for (Map.Entry<String, Row> e : abyss.entrySet()) put(y, "abyss." + e.getKey(), e.getValue());
        for (Map.Entry<String, Row> e : featured.entrySet()) put(y, "featured." + e.getKey(), e.getValue());
        try {
            file.getParentFile().mkdirs();
            y.save(file);
        } catch (Exception ex) {
            log.warning("[P1 top] save failed: " + ex.getMessage());
        }
    }

    private static void put(YamlConfiguration y, String k, Row r) { y.set(k + ".name", r.name); y.set(k + ".value", r.value); y.set(k + ".at", r.at); }

    public synchronized void abyssBest(UUID u, String name, int best) {
        if (EmberMode.boardExcluded(name)) return; // D102
        Row r = abyss.get(u.toString());
        if (r != null && r.value >= best && r.name.equals(name)) return;
        int v = Math.max(best, r == null ? 0 : r.value);
        long at = r != null && r.value == v ? r.at : System.currentTimeMillis();
        abyss.put(u.toString(), new Row(name == null ? "?" : name, v, at));
        save();
    }

    public synchronized void featuredClear(UUID u, String name, String weekKey) {
        if (!weekKey.equals(week)) { week = weekKey; featured.clear(); }
        if (EmberMode.boardExcluded(name)) return; // D102
        Row r = featured.get(u.toString());
        featured.put(u.toString(), new Row(name == null ? "?" : name, (r == null ? 0 : r.value) + 1, System.currentTimeMillis()));
        save();
    }

    public synchronized List<Row> top(boolean abyssBoard, String currentWeek, int n) {
        List<Row> rows = sorted(abyssBoard, currentWeek);
        return rows.size() > n ? new ArrayList<Row>(rows.subList(0, n)) : rows;
    }

    /** D102: value desc, then whoever reached it first; excluded accounts never listed */
    private List<Row> sorted(boolean abyssBoard, String currentWeek) {
        List<Row> rows = new ArrayList<Row>();
        if (abyssBoard) rows.addAll(abyss.values());
        else if (currentWeek.equals(week)) rows.addAll(featured.values());
        rows.removeIf(r -> EmberMode.boardExcluded(r.name));
        Collections.sort(rows, (a, b) -> b.value != a.value ? Integer.compare(b.value, a.value)
                : a.at != b.at ? Long.compare(a.at, b.at) : a.name.compareTo(b.name));
        return rows;
    }

    /** D102「你：第 N 名」: {rank (1-based), value}, or null when the player is not on this board */
    public synchronized int[] rankOf(UUID u, boolean abyssBoard, String currentWeek) {
        Row mine = (abyssBoard ? abyss : currentWeek.equals(week) ? featured : java.util.Collections.<String, Row>emptyMap()).get(u.toString());
        if (mine == null) return null;
        List<Row> rows = sorted(abyssBoard, currentWeek);
        int i = rows.indexOf(mine);
        return i < 0 ? null : new int[] { i + 1, mine.value };
    }
}
