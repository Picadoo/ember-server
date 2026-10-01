package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import town.sunshine.corerpg.CoreRpgPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * MySQL mirror for P1 item identity and loadout state. Creates two NEW tables with
 * CREATE TABLE IF NOT EXISTS (cr_p1_item, cr_p1_loadout) the first time P1 needs them; existing tables are
 * never altered. All SQL runs on one ordered background thread; callbacks return to the main thread.
 * When storage is not MySQL the store is a no-op and P1 falls back to signed NBT only.
 */
public final class EmberItemStore {

    public static final String SCHEMA_ITEM =
            "CREATE TABLE IF NOT EXISTS cr_p1_item ("
                    + "item_uid CHAR(32) NOT NULL PRIMARY KEY,"
                    + "owner_uuid CHAR(36) NULL,"
                    + "ni_id VARCHAR(64) NOT NULL,"
                    + "family VARCHAR(16) NOT NULL,"
                    + "slot VARCHAR(8) NOT NULL,"
                    + "tier TINYINT NOT NULL,"
                    + "quality TINYINT NOT NULL,"
                    + "craft TINYINT NOT NULL,"
                    + "enhance TINYINT NOT NULL,"
                    + "pity SMALLINT NOT NULL,"
                    + "bound TINYINT NOT NULL,"
                    + "source VARCHAR(16) NOT NULL,"
                    + "data_version SMALLINT NOT NULL,"
                    + "rev INT NOT NULL,"
                    + "state VARCHAR(16) NOT NULL DEFAULT 'active',"
                    + "created_at BIGINT NOT NULL,"
                    + "updated_at BIGINT NOT NULL,"
                    + "KEY idx_p1_item_owner (owner_uuid)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

    public static final String SCHEMA_LOADOUT =
            "CREATE TABLE IF NOT EXISTS cr_p1_loadout ("
                    + "player_uuid CHAR(36) NOT NULL PRIMARY KEY,"
                    + "mainhand_uid CHAR(32) NULL,"
                    + "charm_uid CHAR(32) NULL,"
                    + "active_set VARCHAR(16) NOT NULL DEFAULT 'none',"
                    + "awakening TINYINT NOT NULL DEFAULT 0,"
                    + "heal_cd_until BIGINT NOT NULL DEFAULT 0,"
                    + "skill_cd_until BIGINT NOT NULL DEFAULT 0,"
                    + "last_hp DOUBLE NULL,"
                    + "last_world VARCHAR(64) NULL,"
                    + "updated_at BIGINT NOT NULL"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

    /** DB view of one item, enough for the trust check. */
    public static final class Row {
        public final String owner;
        public final int rev;
        public final String state;
        public Row(String owner, int rev, String state) { this.owner = owner; this.rev = rev; this.state = state; }
    }

    private final CoreRpgPlugin plugin;
    private ExecutorService exec;
    private volatile boolean schemaQueued;
    private volatile boolean schemaOk;

    public EmberItemStore(CoreRpgPlugin plugin) {
        this.plugin = plugin;
    }

    /** MySQL storage live (not whether our schema exists yet). */
    public boolean usable() { return plugin.isMysqlActive(); }

    public boolean schemaOk() { return schemaOk; }

    private synchronized ExecutorService exec() {
        if (exec == null || exec.isShutdown()) {
            exec = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "CoreRpg-P1-store");
                t.setDaemon(true);
                return t;
            });
        }
        return exec;
    }

    /** Idempotent; called when P1 becomes active. Queues the two CREATE TABLE IF NOT EXISTS. */
    public void ensureSchema() {
        if (!usable() || schemaQueued) return;
        schemaQueued = true;
        exec().submit(() -> {
            try (Connection c = plugin.getMysqlStorage().getConnection(); Statement st = c.createStatement()) {
                st.executeUpdate(SCHEMA_ITEM);
                st.executeUpdate(SCHEMA_LOADOUT);
                schemaOk = true;
                plugin.getLogger().info("[" + EmberMode.MODE_ID + "] MySQL tables cr_p1_item / cr_p1_loadout ready");
            } catch (Throwable t) {
                schemaQueued = false;
                plugin.getLogger().log(Level.WARNING, "[" + EmberMode.MODE_ID + "] schema init failed: " + t.getMessage());
            }
        });
    }

    private void run(String what, SqlTask task) {
        if (!usable()) return;
        ensureSchema();
        exec().submit(() -> {
            try (Connection c = plugin.getMysqlStorage().getConnection()) {
                task.run(c);
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "[" + EmberMode.MODE_ID + "] " + what + " failed: " + t.getMessage());
            }
        });
    }

    private interface SqlTask { void run(Connection c) throws SQLException; }

    private void sync(Runnable r) {
        if (plugin.isEnabled()) Bukkit.getScheduler().runTask(plugin, r);
    }

    public void upsertItem(final EmberItemData d, final UUID owner, final String state) {
        run("upsert item " + d.uid, c -> {
            long now = System.currentTimeMillis();
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO cr_p1_item (item_uid,owner_uuid,ni_id,family,slot,tier,quality,craft,enhance,pity,bound,source,data_version,rev,state,created_at,updated_at)"
                            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
                            + " ON DUPLICATE KEY UPDATE owner_uuid=VALUES(owner_uuid),ni_id=VALUES(ni_id),family=VALUES(family),slot=VALUES(slot),"
                            + "tier=VALUES(tier),quality=VALUES(quality),craft=VALUES(craft),enhance=VALUES(enhance),pity=VALUES(pity),"
                            + "bound=VALUES(bound),source=VALUES(source),data_version=VALUES(data_version),rev=VALUES(rev),state=VALUES(state),"
                            + "updated_at=VALUES(updated_at)")) {
                int i = 1;
                ps.setString(i++, d.uid);
                if (owner == null) ps.setNull(i++, Types.CHAR); else ps.setString(i++, owner.toString());
                ps.setString(i++, d.ni);
                ps.setString(i++, d.family);
                ps.setString(i++, d.slot);
                ps.setInt(i++, d.tier);
                ps.setInt(i++, d.quality);
                ps.setInt(i++, d.craft);
                ps.setInt(i++, d.enhance);
                ps.setInt(i++, d.pity);
                ps.setInt(i++, d.bound ? 1 : 0);
                ps.setString(i++, d.source);
                ps.setInt(i++, d.version);
                ps.setInt(i++, d.rev);
                ps.setString(i++, state == null ? "active" : state);
                ps.setLong(i++, now);
                ps.setLong(i, now);
                ps.executeUpdate();
            }
        });
    }

    public void loadOwnerItems(final UUID owner, final Consumer<Map<String, Row>> cb) {
        run("load items " + owner, c -> {
            final Map<String, Row> out = new HashMap<String, Row>();
            try (PreparedStatement ps = c.prepareStatement("SELECT item_uid,owner_uuid,rev,state FROM cr_p1_item WHERE owner_uuid=?")) {
                ps.setString(1, owner.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) out.put(rs.getString(1), new Row(rs.getString(2), rs.getInt(3), rs.getString(4)));
                }
            }
            sync(() -> cb.accept(out));
        });
    }

    /** Single-row lookup; callback gets null when the uid is unknown. */
    public void lookupItem(final String uid, final Consumer<Row> cb) {
        run("lookup item " + uid, c -> {
            Row row = null;
            try (PreparedStatement ps = c.prepareStatement("SELECT owner_uuid,rev,state FROM cr_p1_item WHERE item_uid=?")) {
                ps.setString(1, uid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) row = new Row(rs.getString(1), rs.getInt(2), rs.getString(3));
                }
            }
            final Row r = row;
            sync(() -> cb.accept(r));
        });
    }

    public void saveState(final UUID player, final EmberPlayerState s) {
        final String charm = s.charmUid, main = s.mainhandUid, set = s.activeSet, world = s.lastWorld;
        final int awk = s.awakening;
        final long heal = s.healCdUntil, skill = s.skillCdUntil;
        final double hp = s.lastHp;
        run("save loadout " + player, c -> {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO cr_p1_loadout (player_uuid,mainhand_uid,charm_uid,active_set,awakening,heal_cd_until,skill_cd_until,last_hp,last_world,updated_at)"
                            + " VALUES (?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE mainhand_uid=VALUES(mainhand_uid),charm_uid=VALUES(charm_uid),"
                            + "active_set=VALUES(active_set),awakening=VALUES(awakening),heal_cd_until=VALUES(heal_cd_until),"
                            + "skill_cd_until=VALUES(skill_cd_until),last_hp=VALUES(last_hp),last_world=VALUES(last_world),updated_at=VALUES(updated_at)")) {
                ps.setString(1, player.toString());
                if (main == null) ps.setNull(2, Types.CHAR); else ps.setString(2, main);
                if (charm == null) ps.setNull(3, Types.CHAR); else ps.setString(3, charm);
                ps.setString(4, set == null ? "none" : set);
                ps.setInt(5, awk);
                ps.setLong(6, heal);
                ps.setLong(7, skill);
                if (Double.isNaN(hp)) ps.setNull(8, Types.DOUBLE); else ps.setDouble(8, hp);
                if (world == null) ps.setNull(9, Types.VARCHAR); else ps.setString(9, world);
                ps.setLong(10, System.currentTimeMillis());
                ps.executeUpdate();
            }
        });
    }

    /** Fills {@code into} from the DB (main thread callback); leaves defaults when there is no row. */
    public void loadState(final UUID player, final EmberPlayerState into, final Runnable done) {
        run("load loadout " + player, c -> {
            String charm = null, main = null, set = "none", world = null;
            int awk = 0; long heal = 0, skill = 0; double hp = Double.NaN;
            boolean found = false;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT mainhand_uid,charm_uid,active_set,awakening,heal_cd_until,skill_cd_until,last_hp,last_world FROM cr_p1_loadout WHERE player_uuid=?")) {
                ps.setString(1, player.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        found = true;
                        main = rs.getString(1); charm = rs.getString(2); set = rs.getString(3); awk = rs.getInt(4);
                        heal = rs.getLong(5); skill = rs.getLong(6);
                        double v = rs.getDouble(7);
                        hp = rs.wasNull() ? Double.NaN : v;
                        world = rs.getString(8);
                    }
                }
            }
            final boolean f = found;
            final String fc = charm, fm = main, fs = set, fw = world;
            final int fa = awk; final long fh = heal, fk = skill; final double fhp = hp;
            sync(() -> {
                if (f) {
                    // keep anything changed in memory meanwhile (later of the two cooldowns wins)
                    if (into.charmUid == null) into.charmUid = fc;
                    if (into.mainhandUid == null) into.mainhandUid = fm;
                    into.activeSet = fs;
                    into.awakening = fa;
                    into.healCdUntil = Math.max(into.healCdUntil, fh);
                    into.skillCdUntil = Math.max(into.skillCdUntil, fk);
                    if (Double.isNaN(into.lastHp)) into.lastHp = fhp;
                    if (into.lastWorld == null) into.lastWorld = fw;
                }
                into.loaded = true;
                if (done != null) done.run();
            });
        });
    }

    /** Flush queued writes on disable (bounded wait). */
    public void shutdown() {
        ExecutorService e = exec;
        if (e == null) return;
        e.shutdown();
        try { e.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
    }
}
