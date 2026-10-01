package town.sunshine.corerpg.p1;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import town.sunshine.corerpg.CoreRpgPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * G04 persistence. Authoritative copy: small YAML files written synchronously on the main thread
 * (plugins/CoreRpg/p1-runs/runs/&lt;run&gt;.yml and p1-runs/ledger/&lt;player&gt;.yml, gitignored runtime data), so the
 * idempotency decision never waits for the database. When MySQL is live every change is mirrored into cr_p1_run /
 * cr_p1_reward (one row per player, run_id, reward_key) for audit and support queries.
 */
public final class EmberRunStore {

    private static final long KEEP_CLOSED_MS = 30L * 24 * 3600 * 1000;

    private final CoreRpgPlugin plugin;
    private final EmberItemStore mirror;
    private final File runDir, ledgerDir;
    private final Map<UUID, EmberRunRules.Ledger> ledgers = new HashMap<UUID, EmberRunRules.Ledger>();

    public EmberRunStore(CoreRpgPlugin plugin, EmberItemStore mirror) {
        this.plugin = plugin;
        this.mirror = mirror;
        File root = new File(plugin.getDataFolder(), "p1-runs");
        this.runDir = new File(root, "runs");
        this.ledgerDir = new File(root, "ledger");
    }

    // ------------------------------------------------------------------ sessions

    public void save(EmberRunSession s) {
        s.updated = System.currentTimeMillis();
        if (!runDir.isDirectory() && !runDir.mkdirs()) plugin.getLogger().warning("[P1 run] cannot create " + runDir);
        YamlConfiguration y = new YamlConfiguration();
        for (Map.Entry<String, Object> e : s.toMap().entrySet()) y.set(e.getKey(), e.getValue());
        try {
            y.save(new File(runDir, s.runId + ".yml"));
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "[P1 run] save " + s.runId + " failed", e);
        }
        if (mirror != null) mirror.mirrorRun(s);
    }

    /** Every run file whose state is still open (prepare / entered / fighting / settling). */
    public List<EmberRunSession> loadOpen() {
        List<EmberRunSession> out = new ArrayList<EmberRunSession>();
        File[] fs = runDir.listFiles((d, n) -> n.endsWith(".yml"));
        if (fs == null) return out;
        long now = System.currentTimeMillis();
        for (File f : fs) {
            try {
                YamlConfiguration y = YamlConfiguration.loadConfiguration(f);
                EmberRunSession s = EmberRunSession.fromMap(plain(y));
                if (s.runId == null || s.runId.isEmpty()) continue;
                if (s.open()) out.add(s);
                else if (now - s.updated > KEEP_CLOSED_MS && !f.delete()) plugin.getLogger().fine("[P1 run] keep " + f);
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "[P1 run] unreadable " + f.getName(), t);
            }
        }
        return out;
    }

    // ------------------------------------------------------------------ ledgers

    public EmberRunRules.Ledger ledger(UUID player) {
        EmberRunRules.Ledger l = ledgers.get(player);
        if (l != null) return l;
        File f = new File(ledgerDir, player + ".yml");
        if (f.isFile()) {
            YamlConfiguration y = YamlConfiguration.loadConfiguration(f);
            ConfigurationSection rows = y.getConfigurationSection("rows");
            l = EmberRunRules.Ledger.fromMap(rows == null ? null : plain(rows));
            l.prune(System.currentTimeMillis(), KEEP_CLOSED_MS);
        } else {
            l = new EmberRunRules.Ledger();
        }
        ledgers.put(player, l);
        return l;
    }

    /** Writes the whole ledger file and mirrors the given rows (null = none). */
    public void saveLedger(UUID player, List<EmberRunRules.Row> changed) {
        EmberRunRules.Ledger l = ledger(player);
        if (!ledgerDir.isDirectory() && !ledgerDir.mkdirs()) plugin.getLogger().warning("[P1 run] cannot create " + ledgerDir);
        YamlConfiguration y = new YamlConfiguration();
        y.set("player", player.toString());
        for (Map.Entry<String, Object> e : l.toMap().entrySet()) y.set("rows." + e.getKey().replace('/', '|'), e.getValue());
        try {
            y.save(new File(ledgerDir, player + ".yml"));
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "[P1 run] ledger save " + player + " failed", e);
        }
        if (mirror != null && changed != null) for (EmberRunRules.Row r : changed) mirror.mirrorReward(player, r);
    }

    public void unload(UUID player) { ledgers.remove(player); }

    /** ConfigurationSection → plain nested maps (MemorySection values become maps). */
    static Map<String, Object> plain(ConfigurationSection sec) {
        Map<String, Object> out = new LinkedHashMap<String, Object>();
        for (String k : sec.getKeys(false)) {
            Object v = sec.get(k);
            out.put(k, v instanceof ConfigurationSection ? plain((ConfigurationSection) v) : v);
        }
        return out;
    }
}
