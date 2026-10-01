package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

/**
 * Mode flag {@value #MODE_ID} (source table §6.1). Default OFF: when inactive every gate in the legacy
 * services short-circuits on a single volatile read and the legacy pipeline is untouched.
 *
 * <p>Scope is per world: the new mode applies only to players/entities standing in a world listed in
 * {@code ember-v1.yml scope.worlds} or matching {@code scope.world_prefixes} (DP instance worlds of the
 * new-mode maps, test worlds). Admins can add/remove worlds and flip the master switch at runtime
 * ({@code /corerpg p1 ...}); runtime changes are not written back to the file and reset on restart.</p>
 */
public final class EmberMode {

    public static final String MODE_ID = "ember-v1.0-P1";
    public static final String FILE = "ember-v1.yml";

    private static volatile EmberMode instance;

    private final JavaPlugin plugin;
    private volatile boolean configEnabled;
    /** null = follow config; TRUE/FALSE = admin runtime override */
    private volatile Boolean runtimeEnabled;
    private volatile boolean blocked;
    private volatile String blockedReason = "";
    private volatile Set<String> cfgWorlds = Collections.emptySet();
    private volatile List<String> cfgPrefixes = Collections.emptyList();
    private final Set<String> rtAdded = Collections.synchronizedSet(new LinkedHashSet<String>());
    private final Set<String> rtRemoved = Collections.synchronizedSet(new LinkedHashSet<String>());
    private volatile EmberTables tables = EmberTables.defaults();
    private volatile FileConfiguration cfg = new YamlConfiguration();

    public EmberMode(JavaPlugin plugin) {
        this.plugin = plugin;
        instance = this;
    }

    // ---------------------------------------------------------------- static fast paths (gates)

    public static EmberMode get() { return instance; }

    /** Master switch effective (config or runtime override) and not blocked. */
    public static boolean active() {
        EmberMode m = instance;
        return m != null && m.isActive();
    }

    public static boolean isP1World(World w) {
        EmberMode m = instance;
        return m != null && w != null && m.isActive() && m.worldInScope(w.getName());
    }

    /** True when the entity (player or mob) stands in a P1 world while the mode is active. */
    public static boolean isP1(Entity e) {
        EmberMode m = instance;
        return m != null && e != null && m.isActive() && m.worldInScope(e.getWorld().getName());
    }

    public static EmberTables tables() {
        EmberMode m = instance;
        return m == null ? EmberTables.defaults() : m.tables;
    }

    // ---------------------------------------------------------------- state

    public boolean isActive() {
        if (blocked) return false;
        Boolean rt = runtimeEnabled;
        return rt != null ? rt : configEnabled;
    }

    public boolean isConfigEnabled() { return configEnabled; }
    public Boolean getRuntimeOverride() { return runtimeEnabled; }
    public boolean isBlocked() { return blocked; }
    public String getBlockedReason() { return blockedReason; }
    public FileConfiguration config() { return cfg; }

    public void setRuntimeEnabled(Boolean v) { runtimeEnabled = v; }

    public boolean worldInScope(String name) {
        if (name == null) return false;
        String n = name.toLowerCase(Locale.ROOT);
        if (rtRemoved.contains(n)) return false;
        if (rtAdded.contains(n) || cfgWorlds.contains(n)) return true;
        for (String p : cfgPrefixes) if (n.startsWith(p)) return true;
        return false;
    }

    public void addWorld(String name) {
        String n = name.toLowerCase(Locale.ROOT);
        rtRemoved.remove(n);
        rtAdded.add(n);
    }

    public void removeWorld(String name) {
        String n = name.toLowerCase(Locale.ROOT);
        rtAdded.remove(n);
        rtRemoved.add(n);
    }

    public List<String> describeScope() {
        List<String> out = new ArrayList<String>();
        out.add("config worlds=" + cfgWorlds + " prefixes=" + cfgPrefixes);
        synchronized (rtAdded) { out.add("runtime +" + rtAdded); }
        synchronized (rtRemoved) { out.add("runtime -" + rtRemoved); }
        return out;
    }

    // ---------------------------------------------------------------- config

    public void reload() {
        File f = new File(plugin.getDataFolder(), FILE);
        if (!f.exists()) {
            try { plugin.saveResource(FILE, false); } catch (Throwable ignored) {}
        }
        YamlConfiguration y = f.exists() ? YamlConfiguration.loadConfiguration(f) : new YamlConfiguration();
        try {
            java.io.InputStream in = plugin.getResource(FILE);
            if (in != null) {
                y.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8)));
            }
        } catch (Throwable ignored) {}
        cfg = y;
        configEnabled = y.getBoolean("enabled", false);
        Set<String> ws = new LinkedHashSet<String>();
        for (String s : y.getStringList("scope.worlds")) if (s != null && !s.trim().isEmpty()) ws.add(s.trim().toLowerCase(Locale.ROOT));
        List<String> ps = new ArrayList<String>();
        for (String s : y.getStringList("scope.world_prefixes")) if (s != null && !s.trim().isEmpty()) ps.add(s.trim().toLowerCase(Locale.ROOT));
        cfgWorlds = Collections.unmodifiableSet(ws);
        cfgPrefixes = Collections.unmodifiableList(ps);
        try {
            ConfigurationSection t = y.getConfigurationSection("tables");
            tables = EmberTables.of(
                    nums(t, "weapon_a"), nums(t, "charm_h"), nums(t, "charm_d"),
                    nums(t, "quality"), nums(t, "craft"), nums(t, "enhance"),
                    y.getDouble("crit.rate", 0.10), y.getDouble("crit.mult", 1.5),
                    y.getDouble("defense.k", 40.0), y.getDouble("defense.floor", 0.5),
                    y.getDouble("sustain_hp_mult", 1.12));
        } catch (IllegalArgumentException ex) {
            tables = EmberTables.defaults();
            plugin.getLogger().log(Level.WARNING, "[" + MODE_ID + "] bad tables in " + FILE + ", using book defaults: " + ex.getMessage());
        }
        checkConflicts();
        plugin.getLogger().info("[" + MODE_ID + "] config enabled=" + configEnabled + " worlds=" + cfgWorlds
                + " prefixes=" + cfgPrefixes + (blocked ? " BLOCKED: " + blockedReason : ""));
    }

    /** P1 refuses to run next to a second attribute engine (source table A23). */
    public void checkConflicts() {
        if (Bukkit.getPluginManager().isPluginEnabled("AttributePlus")) {
            if (!blocked) plugin.getLogger().warning("[" + MODE_ID + "] AttributePlus is enabled: P1 mode blocked");
            blocked = true;
            blockedReason = "AttributePlus is enabled (would double-apply attributes)";
        } else {
            blocked = false;
            blockedReason = "";
        }
    }

    private static List<Number> nums(ConfigurationSection s, String key) {
        if (s == null || !s.isList(key)) return null;
        List<Number> out = new ArrayList<Number>();
        for (Object o : s.getList(key)) {
            if (o instanceof Number) out.add((Number) o);
            else {
                try { out.add(Double.valueOf(String.valueOf(o))); }
                catch (NumberFormatException e) { throw new IllegalArgumentException("tables." + key + " has non-number " + o); }
            }
        }
        return out;
    }

    // typed config helpers
    public double d(String path, double def) { return cfg.getDouble(path, def); }
    public int i(String path, int def) { return cfg.getInt(path, def); }
    public boolean b(String path, boolean def) { return cfg.getBoolean(path, def); }
    public String s(String path, String def) { return cfg.getString(path, def); }
    public List<String> list(String path) { return cfg.getStringList(path); }
}
