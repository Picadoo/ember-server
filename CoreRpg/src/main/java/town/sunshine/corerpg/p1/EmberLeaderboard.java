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
        Row(String name, int value) { this.name = name; this.value = value; }
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
    }

    private static void read(ConfigurationSection sec, Map<String, Row> into) {
        if (sec == null) return;
        for (String k : sec.getKeys(false)) into.put(k, new Row(sec.getString(k + ".name", "?"), sec.getInt(k + ".value")));
    }

    private void save() {
        YamlConfiguration y = new YamlConfiguration();
        y.set("featured_week", week);
        for (Map.Entry<String, Row> e : abyss.entrySet()) { y.set("abyss." + e.getKey() + ".name", e.getValue().name); y.set("abyss." + e.getKey() + ".value", e.getValue().value); }
        for (Map.Entry<String, Row> e : featured.entrySet()) { y.set("featured." + e.getKey() + ".name", e.getValue().name); y.set("featured." + e.getKey() + ".value", e.getValue().value); }
        try {
            file.getParentFile().mkdirs();
            y.save(file);
        } catch (Exception ex) {
            log.warning("[P1 top] save failed: " + ex.getMessage());
        }
    }

    public synchronized void abyssBest(UUID u, String name, int best) {
        Row r = abyss.get(u.toString());
        if (r != null && r.value >= best && r.name.equals(name)) return;
        abyss.put(u.toString(), new Row(name == null ? "?" : name, Math.max(best, r == null ? 0 : r.value)));
        save();
    }

    public synchronized void featuredClear(UUID u, String name, String weekKey) {
        if (!weekKey.equals(week)) { week = weekKey; featured.clear(); }
        Row r = featured.get(u.toString());
        featured.put(u.toString(), new Row(name == null ? "?" : name, (r == null ? 0 : r.value) + 1));
        save();
    }

    public synchronized List<Row> top(boolean abyssBoard, String currentWeek, int n) {
        List<Row> rows = new ArrayList<Row>();
        if (abyssBoard) rows.addAll(abyss.values());
        else if (currentWeek.equals(week)) rows.addAll(featured.values());
        Collections.sort(rows, (a, b) -> b.value != a.value ? Integer.compare(b.value, a.value) : a.name.compareTo(b.name));
        return rows.size() > n ? new ArrayList<Row>(rows.subList(0, n)) : rows;
    }
}
