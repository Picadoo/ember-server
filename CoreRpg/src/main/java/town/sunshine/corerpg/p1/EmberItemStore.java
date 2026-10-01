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
import java.util.List;
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
                    + "burst_cd_ms INT NOT NULL DEFAULT 0,"
                    + "sustain_cd_ms INT NOT NULL DEFAULT 0,"
                    + "updated_at BIGINT NOT NULL"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

    /** G03 idempotency key + audit of every P1 item mutation (enhance/swap/upgrade/refine/quality/dismantle). */
    public static final String SCHEMA_TXN =
            "CREATE TABLE IF NOT EXISTS cr_p1_txn ("
                    + "request_id VARCHAR(64) NOT NULL PRIMARY KEY,"
                    + "kind VARCHAR(16) NOT NULL,"
                    + "owner_uuid CHAR(36) NOT NULL,"
                    + "uid_a CHAR(32) NOT NULL,"
                    + "uid_b CHAR(32) NULL,"
                    + "before_json TEXT NOT NULL,"
                    + "after_json TEXT NOT NULL,"
                    + "cost_json VARCHAR(255) NULL,"
                    + "result VARCHAR(16) NOT NULL,"
                    + "note VARCHAR(255) NULL,"
                    + "created_at BIGINT NOT NULL,"
                    + "KEY idx_p1_txn_uid_a (uid_a),"
                    + "KEY idx_p1_txn_owner (owner_uuid)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

    /** G04 §20.3 RunSession mirror (authoritative copy: plugins/CoreRpg/p1-runs/runs/<run>.yml). */
    public static final String SCHEMA_RUN =
            "CREATE TABLE IF NOT EXISTS cr_p1_run ("
                    + "run_id VARCHAR(48) NOT NULL PRIMARY KEY,"
                    + "map_key VARCHAR(16) NOT NULL,"
                    + "map_version VARCHAR(64) NOT NULL,"
                    + "rule_version VARCHAR(32) NOT NULL,"
                    + "world VARCHAR(64) NULL,"
                    + "seed BIGINT NOT NULL,"
                    + "participants VARCHAR(255) NOT NULL,"
                    + "targets VARCHAR(255) NOT NULL,"
                    + "extra VARCHAR(16) NOT NULL,"
                    + "extra_done TINYINT NOT NULL,"
                    + "party_size TINYINT NOT NULL,"
                    + "hp_factor DOUBLE NOT NULL,"
                    + "state VARCHAR(16) NOT NULL,"
                    + "reason VARCHAR(255) NULL,"
                    + "created_at BIGINT NOT NULL,"
                    + "updated_at BIGINT NOT NULL"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

    /** G04 §20.3 RewardLedger mirror: one row per (player, run_id, reward_key); the result is never updated. */
    public static final String SCHEMA_REWARD =
            "CREATE TABLE IF NOT EXISTS cr_p1_reward ("
                    + "player_uuid CHAR(36) NOT NULL,"
                    + "run_id VARCHAR(48) NOT NULL,"
                    + "reward_key VARCHAR(48) NOT NULL,"
                    + "result VARCHAR(160) NOT NULL,"
                    + "status VARCHAR(16) NOT NULL,"
                    + "created_at BIGINT NOT NULL,"
                    + "updated_at BIGINT NOT NULL,"
                    + "PRIMARY KEY (player_uuid, run_id, reward_key),"
                    + "KEY idx_p1_reward_run (run_id)"
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
                st.executeUpdate(SCHEMA_TXN);
                st.executeUpdate(SCHEMA_RUN);
                st.executeUpdate(SCHEMA_REWARD);
                addColumnIfMissing(c, "cr_p1_loadout", "burst_cd_ms", "INT NOT NULL DEFAULT 0");
                addColumnIfMissing(c, "cr_p1_loadout", "sustain_cd_ms", "INT NOT NULL DEFAULT 0");
                schemaOk = true;
                plugin.getLogger().info("[" + EmberMode.MODE_ID + "] MySQL tables cr_p1_item / cr_p1_loadout / cr_p1_txn / cr_p1_run / cr_p1_reward ready");
            } catch (Throwable t) {
                schemaQueued = false;
                plugin.getLogger().log(Level.WARNING, "[" + EmberMode.MODE_ID + "] schema init failed: " + t.getMessage());
            }
        });
    }

    /** Our own P1 tables only: adds a column introduced after the table may already exist (G02 cooldowns). */
    static void addColumnIfMissing(Connection c, String table, String column, String ddl) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM information_schema.COLUMNS"
                + " WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME=? AND COLUMN_NAME=?")) {
            ps.setString(1, table);
            ps.setString(2, column);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) return;
            }
        }
        try (Statement st = c.createStatement()) {
            st.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + column + " " + ddl);
        }
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

    /** G04: mirror one run session (upsert; state / world / reason / extra_done move forward). */
    public void mirrorRun(final EmberRunSession s) {
        final String id = s.runId, map = s.mapKey, mv = s.mapVersion == null ? "" : s.mapVersion,
                rv = s.ruleVersion == null ? "" : s.ruleVersion, world = s.world, extra = s.extra.id, state = s.state,
                reason = s.reason == null ? "" : s.reason;
        final long seed = s.seed, created = s.created;
        final boolean done = s.extraDone;
        final int party = s.partySize;
        final double hpf = s.hpFactor;
        StringBuilder ps = new StringBuilder(), ts = new StringBuilder();
        for (UUID u : s.participants) {
            if (ps.length() > 0) { ps.append(','); ts.append(','); }
            ps.append(u);
            String t = s.target.get(u);
            ts.append(t == null || t.isEmpty() ? "-" : t);
        }
        final String parts = ps.length() > 255 ? ps.substring(0, 255) : ps.toString();
        final String targets = ts.length() > 255 ? ts.substring(0, 255) : ts.toString();
        run("mirror run " + id, c -> {
            long now = System.currentTimeMillis();
            try (PreparedStatement p = c.prepareStatement(
                    "INSERT INTO cr_p1_run (run_id,map_key,map_version,rule_version,world,seed,participants,targets,extra,extra_done,party_size,hp_factor,state,reason,created_at,updated_at)"
                            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE world=VALUES(world),participants=VALUES(participants),"
                            + "extra_done=VALUES(extra_done),party_size=VALUES(party_size),hp_factor=VALUES(hp_factor),state=VALUES(state),reason=VALUES(reason),updated_at=VALUES(updated_at)")) {
                int i = 1;
                p.setString(i++, id);
                p.setString(i++, map);
                p.setString(i++, mv);
                p.setString(i++, rv);
                if (world == null) p.setNull(i++, Types.VARCHAR); else p.setString(i++, world);
                p.setLong(i++, seed);
                p.setString(i++, parts);
                p.setString(i++, targets);
                p.setString(i++, extra);
                p.setInt(i++, done ? 1 : 0);
                p.setInt(i++, party);
                p.setDouble(i++, hpf);
                p.setString(i++, state);
                p.setString(i++, reason.length() > 255 ? reason.substring(0, 255) : reason);
                p.setLong(i++, created);
                p.setLong(i, now);
                p.executeUpdate();
            }
        });
    }

    /** G04: mirror one ledger row; INSERT keeps the first result, a later call only moves the status. */
    public void mirrorReward(final UUID player, final EmberRunRules.Row r) {
        final String run = r.runId, key = r.key, result = r.result.length() > 160 ? r.result.substring(0, 160) : r.result,
                status = r.status;
        final long created = r.created, updated = r.updated;
        run("mirror reward " + run + "/" + key, c -> {
            try (PreparedStatement p = c.prepareStatement(
                    "INSERT INTO cr_p1_reward (player_uuid,run_id,reward_key,result,status,created_at,updated_at) VALUES (?,?,?,?,?,?,?)"
                            + " ON DUPLICATE KEY UPDATE status=VALUES(status),updated_at=VALUES(updated_at)")) {
                p.setString(1, player.toString());
                p.setString(2, run);
                p.setString(3, key);
                p.setString(4, result);
                p.setString(5, status);
                p.setLong(6, created);
                p.setLong(7, updated);
                p.executeUpdate();
            }
        });
    }

    public void saveState(final UUID player, final EmberPlayerState s) {
        final String charm = s.charmUid, main = s.mainhandUid, set = s.activeSet, world = s.lastWorld;
        final int awk = s.awakening;
        final long heal = s.healCdUntil, skill = s.skillCdUntil;
        final double hp = s.lastHp;
        final int burstMs = (int) Math.max(0, Math.min(Integer.MAX_VALUE, s.burstCdRemainMs));
        final int sustainMs = (int) Math.max(0, Math.min(Integer.MAX_VALUE, s.sustainCdRemainMs));
        run("save loadout " + player, c -> {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO cr_p1_loadout (player_uuid,mainhand_uid,charm_uid,active_set,awakening,heal_cd_until,skill_cd_until,last_hp,last_world,burst_cd_ms,sustain_cd_ms,updated_at)"
                            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE mainhand_uid=VALUES(mainhand_uid),charm_uid=VALUES(charm_uid),"
                            + "active_set=VALUES(active_set),awakening=VALUES(awakening),heal_cd_until=VALUES(heal_cd_until),"
                            + "skill_cd_until=VALUES(skill_cd_until),last_hp=VALUES(last_hp),last_world=VALUES(last_world),"
                            + "burst_cd_ms=VALUES(burst_cd_ms),sustain_cd_ms=VALUES(sustain_cd_ms),updated_at=VALUES(updated_at)")) {
                ps.setString(1, player.toString());
                if (main == null) ps.setNull(2, Types.CHAR); else ps.setString(2, main);
                if (charm == null) ps.setNull(3, Types.CHAR); else ps.setString(3, charm);
                ps.setString(4, set == null ? "none" : set);
                ps.setInt(5, awk);
                ps.setLong(6, heal);
                ps.setLong(7, skill);
                if (Double.isNaN(hp)) ps.setNull(8, Types.DOUBLE); else ps.setDouble(8, hp);
                if (world == null) ps.setNull(9, Types.VARCHAR); else ps.setString(9, world);
                ps.setInt(10, burstMs);
                ps.setInt(11, sustainMs);
                ps.setLong(12, System.currentTimeMillis());
                ps.executeUpdate();
            }
        });
    }

    /** Fills {@code into} from the DB (main thread callback); leaves defaults when there is no row. */
    public void loadState(final UUID player, final EmberPlayerState into, final Runnable done) {
        run("load loadout " + player, c -> {
            String charm = null, main = null, set = "none", world = null;
            int awk = 0; long heal = 0, skill = 0; double hp = Double.NaN; long bcd = 0, scd = 0;
            boolean found = false;
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT mainhand_uid,charm_uid,active_set,awakening,heal_cd_until,skill_cd_until,last_hp,last_world,burst_cd_ms,sustain_cd_ms FROM cr_p1_loadout WHERE player_uuid=?")) {
                ps.setString(1, player.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        found = true;
                        main = rs.getString(1); charm = rs.getString(2); set = rs.getString(3); awk = rs.getInt(4);
                        heal = rs.getLong(5); skill = rs.getLong(6);
                        double v = rs.getDouble(7);
                        hp = rs.wasNull() ? Double.NaN : v;
                        world = rs.getString(8);
                        bcd = rs.getLong(9); scd = rs.getLong(10);
                    }
                }
            }
            final boolean f = found;
            final String fc = charm, fm = main, fs = set, fw = world;
            final int fa = awk; final long fh = heal, fk = skill; final double fhp = hp; final long fb = bcd, fsu = scd;
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
                    into.burstCdRemainMs = Math.max(into.burstCdRemainMs, fb);
                    into.sustainCdRemainMs = Math.max(into.sustainCdRemainMs, fsu);
                }
                into.loaded = true;
                if (done != null) done.run();
            });
        });
    }

    // ------------------------------------------------------------------ G03 transactions

    public enum TxnStatus { OK, REPLAY, CONFLICT, ERROR }

    public static final class TxnResult {
        public final TxnStatus status;
        /** CONFLICT/ERROR: reason; REPLAY: the stored note of the original request */
        public final String detail;
        public TxnResult(TxnStatus status, String detail) { this.status = status; this.detail = detail; }
    }

    /** One locked item row: {@code before} must match the DB (owner, rev, active); {@code after} null = retire it. */
    public static final class TxnItem {
        public final EmberItemData before, after;
        public final String retireState;
        public TxnItem(EmberItemData before, EmberItemData after, String retireState) {
            this.before = before; this.after = after; this.retireState = retireState;
        }
    }

    static String json(EmberItemData... ds) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < ds.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(ds[i] == null ? "null" : "\"" + ds[i].canonical().replace("\\", "\\\\").replace("\"", "\\\"") + "\"");
        }
        return sb.append(']').toString();
    }

    /**
     * Single DB transaction: replay check by request id → SELECT … FOR UPDATE every row (uid order) → owner/rev/state
     * check → UPDATE … WHERE rev=? → INSERT the ledger row → COMMIT. Callback on the main thread.
     */
    public void commitTxn(final String rid, final String kind, final UUID owner, final List<TxnItem> items,
                          final String costJson, final String note, final Consumer<TxnResult> cb) {
        if (!usable()) { cb.accept(new TxnResult(TxnStatus.ERROR, "MySQL 不可用")); return; }
        ensureSchema();
        exec().submit(() -> {
            TxnResult res;
            Connection c = null;
            try {
                c = plugin.getMysqlStorage().getConnection();
                c.setAutoCommit(false);
                res = doTxn(c, rid, kind, owner, items, costJson, note);
                if (res.status == TxnStatus.OK) c.commit(); else c.rollback();
            } catch (Throwable t) {
                try { if (c != null) c.rollback(); } catch (Throwable ignored) {}
                String m = t.getMessage() == null ? t.getClass().getSimpleName() : t.getMessage();
                // a concurrent insert of the same request id loses on the PRIMARY KEY → treat as replay
                res = m.contains("Duplicate") ? new TxnResult(TxnStatus.REPLAY, "并发重复请求") : new TxnResult(TxnStatus.ERROR, m);
                plugin.getLogger().log(Level.WARNING, "[" + EmberMode.MODE_ID + "] txn " + kind + " " + rid + " failed: " + m);
            } finally {
                if (c != null) {
                    try { c.setAutoCommit(true); } catch (Throwable ignored) {}
                    try { c.close(); } catch (Throwable ignored) {}
                }
            }
            final TxnResult r = res;
            if (plugin.isEnabled()) Bukkit.getScheduler().runTask(plugin, () -> cb.accept(r));
        });
    }

    private TxnResult doTxn(Connection c, String rid, String kind, UUID owner, List<TxnItem> items,
                            String costJson, String note) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT note FROM cr_p1_txn WHERE request_id=?")) {
            ps.setString(1, rid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new TxnResult(TxnStatus.REPLAY, rs.getString(1));
            }
        }
        List<TxnItem> ordered = new java.util.ArrayList<TxnItem>(items);
        java.util.Collections.sort(ordered, (x, y) -> x.before.uid.compareTo(y.before.uid)); // fixed lock order
        for (TxnItem it : ordered) {
            try (PreparedStatement ps = c.prepareStatement("SELECT owner_uuid,rev,state FROM cr_p1_item WHERE item_uid=? FOR UPDATE")) {
                ps.setString(1, it.before.uid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return new TxnResult(TxnStatus.CONFLICT, "DB 无记录 " + it.before.uid.substring(0, 8));
                    if (!owner.toString().equals(rs.getString(1))) return new TxnResult(TxnStatus.CONFLICT, "DB 所有者不符");
                    if (rs.getInt(2) != it.before.rev) return new TxnResult(TxnStatus.CONFLICT, "DB rev " + rs.getInt(2) + " != " + it.before.rev);
                    if (!"active".equals(rs.getString(3))) return new TxnResult(TxnStatus.CONFLICT, "DB 状态 " + rs.getString(3));
                }
            }
        }
        long now = System.currentTimeMillis();
        for (TxnItem it : ordered) {
            int n;
            if (it.after == null) {
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE cr_p1_item SET state=?,rev=rev+1,updated_at=? WHERE item_uid=? AND rev=?")) {
                    ps.setString(1, it.retireState == null ? "retired" : it.retireState);
                    ps.setLong(2, now);
                    ps.setString(3, it.before.uid);
                    ps.setInt(4, it.before.rev);
                    n = ps.executeUpdate();
                }
            } else {
                EmberItemData d = it.after;
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE cr_p1_item SET ni_id=?,family=?,slot=?,tier=?,quality=?,craft=?,enhance=?,pity=?,bound=?,rev=?,updated_at=?"
                                + " WHERE item_uid=? AND rev=?")) {
                    int i = 1;
                    ps.setString(i++, d.ni); ps.setString(i++, d.family); ps.setString(i++, d.slot);
                    ps.setInt(i++, d.tier); ps.setInt(i++, d.quality); ps.setInt(i++, d.craft);
                    ps.setInt(i++, d.enhance); ps.setInt(i++, d.pity); ps.setInt(i++, d.bound ? 1 : 0);
                    ps.setInt(i++, d.rev); ps.setLong(i++, now);
                    ps.setString(i++, it.before.uid); ps.setInt(i, it.before.rev);
                    n = ps.executeUpdate();
                }
            }
            if (n != 1) return new TxnResult(TxnStatus.CONFLICT, "并发修改 " + it.before.uid.substring(0, 8));
        }
        EmberItemData[] before = new EmberItemData[items.size()], after = new EmberItemData[items.size()];
        for (int i = 0; i < items.size(); i++) { before[i] = items.get(i).before; after[i] = items.get(i).after; }
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO cr_p1_txn (request_id,kind,owner_uuid,uid_a,uid_b,before_json,after_json,cost_json,result,note,created_at)"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?)")) {
            ps.setString(1, rid);
            ps.setString(2, kind);
            ps.setString(3, owner.toString());
            ps.setString(4, items.get(0).before.uid);
            if (items.size() > 1) ps.setString(5, items.get(1).before.uid); else ps.setNull(5, Types.CHAR);
            ps.setString(6, json(before));
            ps.setString(7, json(after));
            if (costJson == null) ps.setNull(8, Types.VARCHAR); else ps.setString(8, costJson);
            ps.setString(9, "ok");
            String nt = note == null ? null : (note.length() > 250 ? note.substring(0, 250) : note);
            if (nt == null) ps.setNull(10, Types.VARCHAR); else ps.setString(10, nt);
            ps.setLong(11, now);
            ps.executeUpdate();
        }
        return new TxnResult(TxnStatus.OK, null);
    }

    /** Full DB row as item data (+ owner/state), for re-syncing a stale NBT copy. */
    public static final class FullRow {
        public final EmberItemData data;
        public final String owner, state;
        FullRow(EmberItemData data, String owner, String state) { this.data = data; this.owner = owner; this.state = state; }
    }

    public void lookupFull(final String uid, final Consumer<FullRow> cb) {
        run("lookup full " + uid, c -> {
            FullRow row = null;
            try (PreparedStatement ps = c.prepareStatement("SELECT ni_id,family,slot,tier,quality,craft,enhance,pity,bound,source,"
                    + "data_version,rev,owner_uuid,state FROM cr_p1_item WHERE item_uid=?")) {
                ps.setString(1, uid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        EmberItemData d = new EmberItemData(uid, rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4),
                                rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8), rs.getInt(9) != 0, rs.getString(10),
                                rs.getInt(11), rs.getInt(12));
                        row = new FullRow(d, rs.getString(13), rs.getString(14));
                    }
                }
            }
            final FullRow r = row;
            sync(() -> cb.accept(r));
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
