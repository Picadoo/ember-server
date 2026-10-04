package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import town.sunshine.corerpg.CoreRpgPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * 1.64.1 (review round 2 #1) material-warehouse movement log, MySQL {@code cr_vault_log}. Every warehouse mutation
 * (pickup / settlement auto-deposit / 一键存入 stash / withdraw / spend from the warehouse) and every whitelisted
 * material consumed straight out of the backpack ({@code spend_inv}) is recorded per player, NI id and reason. Rows
 * are aggregated for ~2 s (row time = the LAST event of the bucket, so a bucket never looks older than it is).
 * InvSnapService reads the sums since a snapshot to net out materials that left the backpack after it, so a restore
 * cannot hand them out a second time. Without MySQL this is a no-op (restore then relies on the snapshot's vault
 * baseline only).
 */
public final class EmberVaultLog {

    public static final String SCHEMA = "CREATE TABLE IF NOT EXISTS cr_vault_log ("
            + "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
            + "player_uuid CHAR(36) NOT NULL,"
            + "ni_id VARCHAR(64) NOT NULL,"
            + "delta BIGINT NOT NULL,"
            + "reason VARCHAR(16) NOT NULL,"
            + "created_at BIGINT NOT NULL,"
            + "KEY idx_vault_log_player (player_uuid, created_at)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

    /** inventory → warehouse (一键存入): these left the backpack */
    public static final String STASH = "stash";
    /** consumed straight from the backpack by a P1 cost (forge, festival buy …) */
    public static final String SPEND_INV = "spend_inv";
    public static final String PICKUP = "pickup", AUTO = "auto", WITHDRAW = "withdraw", SPEND_VAULT = "spend_vault";

    private static volatile EmberVaultLog instance;
    public static EmberVaultLog get() { return instance; }

    private final CoreRpgPlugin plugin;
    /** "uuid|ni|reason" → {sum, lastAt} */
    private final Map<String, long[]> buf = new LinkedHashMap<String, long[]>();
    private final ExecutorService exec = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "CoreRpg-vaultlog");
        t.setDaemon(true);
        return t;
    });
    private volatile boolean schemaOk;

    public EmberVaultLog(CoreRpgPlugin plugin) {
        this.plugin = plugin;
        instance = this;
        exec.submit(this::ensureSchema);
        Bukkit.getScheduler().runTaskTimer(plugin, this::flush, 40L, 40L);
    }

    private boolean db() { return plugin.isMysqlActive(); }

    private void ensureSchema() {
        if (!db() || schemaOk) return;
        try (Connection c = plugin.getMysqlStorage().getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate(SCHEMA);
            schemaOk = true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "[vaultlog] schema failed", e);
        }
    }

    /** main thread */
    public synchronized void record(UUID u, String niId, long delta, String reason) {
        if (u == null || niId == null || delta == 0) return;
        long[] v = buf.computeIfAbsent(u + "|" + niId + "|" + reason, k -> new long[2]);
        v[0] += delta;
        v[1] = System.currentTimeMillis();
    }

    /** drains the buffer into one async batch insert (main thread / disable) */
    public void flush() {
        final List<Object[]> rows = new ArrayList<Object[]>();
        synchronized (this) {
            for (Map.Entry<String, long[]> e : buf.entrySet()) {
                if (e.getValue()[0] == 0) continue;
                String[] k = e.getKey().split("\\|", 3);
                rows.add(new Object[]{k[0], k[1], e.getValue()[0], k[2], e.getValue()[1]});
            }
            buf.clear();
        }
        if (rows.isEmpty() || !db()) return;
        exec.submit(() -> {
            ensureSchema();
            if (!schemaOk) return;
            try (Connection c = plugin.getMysqlStorage().getConnection();
                 PreparedStatement ps = c.prepareStatement("INSERT INTO cr_vault_log (player_uuid,ni_id,delta,reason,created_at) VALUES (?,?,?,?,?)")) {
                for (Object[] r : rows) {
                    ps.setString(1, (String) r[0]); ps.setString(2, (String) r[1]); ps.setLong(3, (Long) r[2]);
                    ps.setString(4, (String) r[3]); ps.setLong(5, (Long) r[4]); ps.addBatch();
                }
                ps.executeBatch();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "[vaultlog] insert " + rows.size() + " rows failed", e);
            }
        });
    }

    /** result of {@link #since}: reason → ni id → summed delta; {@code ok} false when the DB could not be read */
    public static final class Sums {
        public final Map<String, Map<String, Long>> byReason = new HashMap<String, Map<String, Long>>();
        public boolean ok;
        public long get(String reason, String niId) {
            Map<String, Long> m = byReason.get(reason);
            Long v = m == null ? null : m.get(niId);
            return v == null ? 0 : v;
        }
    }

    /** flushes, then sums rows with created_at ≥ {@code since}; {@code cb} runs on the main thread */
    public void since(final UUID u, final long since, final Consumer<Sums> cb) {
        flush();
        exec.submit(() -> {
            final Sums s = new Sums();
            if (db()) {
                ensureSchema();
                if (schemaOk) {
                    try (Connection c = plugin.getMysqlStorage().getConnection();
                         PreparedStatement ps = c.prepareStatement("SELECT reason, ni_id, SUM(delta) FROM cr_vault_log WHERE player_uuid=? AND created_at>=? GROUP BY reason, ni_id")) {
                        ps.setString(1, u.toString()); ps.setLong(2, since);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) s.byReason.computeIfAbsent(rs.getString(1), k -> new HashMap<String, Long>()).put(rs.getString(2), rs.getLong(3));
                        }
                        s.ok = true;
                    } catch (SQLException e) {
                        plugin.getLogger().log(Level.WARNING, "[vaultlog] read", e);
                    }
                }
            }
            Bukkit.getScheduler().runTask(plugin, () -> cb.accept(s));
        });
    }

    /** plugin disable */
    public void shutdown() {
        flush();
        exec.shutdown();
        try { exec.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }
}
