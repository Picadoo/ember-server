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

    /** 1.62 装备库: per-item lock / favourite flags of the owner's stored gear (the item row itself is cr_p1_item, state 'stored'). */
    public static final String SCHEMA_GEARLIB =
            "CREATE TABLE IF NOT EXISTS cr_p1_gearlib ("
                    + "item_uid CHAR(32) NOT NULL PRIMARY KEY,"
                    + "owner_uuid CHAR(36) NOT NULL,"
                    + "locked TINYINT NOT NULL DEFAULT 0,"
                    + "fav TINYINT NOT NULL DEFAULT 0,"
                    + "stored_at BIGINT NOT NULL DEFAULT 0,"
                    + "KEY idx_p1_gearlib_owner (owner_uuid)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

    /**
     * D162 (review A01): durable delivery records. A gear-library / forge transaction that owes the player something
     * (materials back from a dismantle, a withdrawn piece, a refund, a debit) writes the owed lines here IN THE SAME DB
     * transaction as the item state change. Unique (owner, request, idx): a replayed request never owes twice. Delivered
     * on callback when online, else at the next join; exactly once through a marker in the player row (see EmberDelivery).
     * status: pending → delivered (or void when the owed piece no longer exists); attempts / reason kept for the admin.
     */
    public static final String SCHEMA_DELIVERY =
            "CREATE TABLE IF NOT EXISTS cr_p1_delivery ("
                    + "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                    + "owner_uuid CHAR(36) NOT NULL,"
                    + "request_id VARCHAR(96) NOT NULL,"
                    + "idx INT NOT NULL DEFAULT 0,"
                    + "kind VARCHAR(16) NOT NULL,"
                    + "item VARCHAR(64) NOT NULL,"
                    + "amount BIGINT NOT NULL,"
                    + "status VARCHAR(12) NOT NULL DEFAULT 'pending',"
                    + "attempts INT NOT NULL DEFAULT 0,"
                    + "reason VARCHAR(160) NULL,"
                    + "note VARCHAR(160) NULL,"
                    + "created_at BIGINT NOT NULL,"
                    + "updated_at BIGINT NOT NULL,"
                    + "UNIQUE KEY uq_p1_delivery (owner_uuid,request_id,idx),"
                    + "KEY idx_p1_delivery_owner (owner_uuid,status)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

    /** one owed line: kind {@code mat} (item = NI id, amount &gt; 0 credit, &lt; 0 debit) or {@code gear} (item = uid, amount 1) */
    public static final class Owed {
        public final String kind, item, note;
        public final long amount;
        public Owed(String kind, String item, long amount, String note) { this.kind = kind; this.item = item; this.amount = amount; this.note = note; }
        public static Owed mat(String ni, long n, String note) { return new Owed("mat", ni, n, note); }
        public static Owed gear(String uid, String note) { return new Owed("gear", uid, 1, note); }
    }

    /** a pending delivery row */
    public static final class Delivery {
        public final long id;
        public final String request, kind, item, note;
        public final int idx, attempts;
        public final long amount;
        Delivery(long id, String request, int idx, String kind, String item, long amount, int attempts, String note) {
            this.id = id; this.request = request; this.idx = idx; this.kind = kind; this.item = item; this.amount = amount;
            this.attempts = attempts; this.note = note;
        }
    }

    // D162 (review A03): write health of the P1 store — consecutive ERROR results pause asset mutations for a minute
    private static volatile int errStreak;
    private static volatile long lastErrAt;
    public static boolean writesPaused() { return errStreak >= 3 && System.currentTimeMillis() - lastErrAt < 60000L; }
    public static int errStreak() { return errStreak; }
    private static void health(TxnStatus st) {
        if (st == TxnStatus.ERROR) { errStreak++; lastErrAt = System.currentTimeMillis(); }
        else errStreak = 0;
    }

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
                st.executeUpdate(SCHEMA_GEARLIB);
                st.executeUpdate(SCHEMA_DELIVERY);
                addColumnIfMissing(c, "cr_p1_loadout", "burst_cd_ms", "INT NOT NULL DEFAULT 0");
                addColumnIfMissing(c, "cr_p1_loadout", "sustain_cd_ms", "INT NOT NULL DEFAULT 0");
                schemaOk = true;
                plugin.getLogger().info("[" + EmberMode.MODE_ID + "] MySQL tables cr_p1_item / cr_p1_loadout / cr_p1_txn / cr_p1_run / cr_p1_reward / cr_p1_gearlib / cr_p1_delivery ready");
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

    /** B2.180 codex backfill: distinct (family, slot, tier) of every row this player ever owned, test items excluded. */
    public void loadOwnerKinds(final UUID owner, final Consumer<List<String[]>> cb) {
        run("load kinds " + owner, c -> {
            final List<String[]> out = new java.util.ArrayList<String[]>();
            try (PreparedStatement ps = c.prepareStatement(
                    "SELECT DISTINCT family,slot,tier FROM cr_p1_item WHERE owner_uuid=? AND source<>'admin'")) {
                ps.setString(1, owner.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) out.add(new String[] {rs.getString(1), rs.getString(2), String.valueOf(rs.getInt(3))});
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
        /** DB state the row must be in (default active; the gear library moves stored ↔ active, undo dismantled → stored) */
        public final String expectState;
        public TxnItem(EmberItemData before, EmberItemData after, String retireState) {
            this(before, after, retireState, "active");
        }
        public TxnItem(EmberItemData before, EmberItemData after, String retireState, String expectState) {
            this.before = before; this.after = after; this.retireState = retireState;
            this.expectState = expectState == null ? "active" : expectState;
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
        commitTxn(rid, kind, owner, items, costJson, note, null, cb);
    }

    /**
     * D162: same, plus the owed lines written to cr_p1_delivery inside the same DB transaction (null/empty = none).
     * Test env only (CORERPG_TEST_FAULTS=1): an armed fault makes it fail before COMMIT, or drop the callback after it.
     */
    public void commitTxn(final String rid, final String kind, final UUID owner, final List<TxnItem> items,
                          final String costJson, final String note, final List<Owed> owed, final Consumer<TxnResult> cb) {
        if (!usable()) { cb.accept(new TxnResult(TxnStatus.ERROR, "MySQL 不可用")); return; }
        ensureSchema();
        final boolean failBefore = EmberFaults.fire(owner, EmberFaults.BEFORE_COMMIT);
        final boolean dropCb = !failBefore && EmberFaults.fire(owner, EmberFaults.AFTER_COMMIT);
        exec().submit(() -> {
            TxnResult res;
            Connection c = null;
            try {
                c = plugin.getMysqlStorage().getConnection();
                c.setAutoCommit(false);
                res = doTxn(c, rid, kind, owner, items, costJson, note);
                if (res.status == TxnStatus.OK && owed != null && !owed.isEmpty()) insertOwed(c, owner, rid, owed);
                if (res.status == TxnStatus.OK && failBefore) throw new SQLException("TEST FAULT before_commit");
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
            health(r.status);
            if (dropCb && r.status == TxnStatus.OK) { // the window between COMMIT and the callback, widened to 10 s
                plugin.getLogger().warning("[" + EmberMode.MODE_ID + "] TEST FAULT after_commit: txn " + kind + " " + rid + " committed, callback held 10 s");
                if (plugin.isEnabled()) Bukkit.getScheduler().runTaskLater(plugin, () -> cb.accept(r), 200L);
                return;
            }
            if (plugin.isEnabled()) Bukkit.getScheduler().runTask(plugin, () -> cb.accept(r));
        });
    }

    private static void insertOwed(Connection c, UUID owner, String rid, List<Owed> owed) throws SQLException {
        insertOwed(c, owner, rid, owed, "pending");
    }

    private static int insertOwed(Connection c, UUID owner, String rid, List<Owed> owed, String status) throws SQLException {
        return insertOwed(c, owner, rid, owed, status, 0);
    }

    private static int insertOwed(Connection c, UUID owner, String rid, List<Owed> owed, String status, int base) throws SQLException {
        long now = System.currentTimeMillis();
        int n = 0;
        try (PreparedStatement ps = c.prepareStatement("INSERT IGNORE INTO cr_p1_delivery"
                + " (owner_uuid,request_id,idx,kind,item,amount,status,attempts,note,created_at,updated_at) VALUES (?,?,?,?,?,?,'" + status + "',0,?,?,?)")) {
            for (int i = 0; i < owed.size(); i++) {
                Owed o = owed.get(i);
                ps.setString(1, owner.toString());
                ps.setString(2, rid.length() > 96 ? rid.substring(0, 96) : rid);
                ps.setInt(3, base + i);
                ps.setString(4, o.kind);
                ps.setString(5, o.item);
                ps.setLong(6, o.amount);
                String nt = o.note == null ? null : (o.note.length() > 160 ? o.note.substring(0, 160) : o.note);
                if (nt == null) ps.setNull(7, Types.VARCHAR); else ps.setString(7, nt);
                ps.setLong(8, now);
                ps.setLong(9, now);
                ps.addBatch();
            }
            for (int x : ps.executeBatch()) if (x > 0 || x == java.sql.Statement.SUCCESS_NO_INFO) n++;
        }
        return n;
    }

    /** D162: owed lines without an item transaction (e.g. a stash that failed after the player left). cb(ok) on the main thread. */
    public void insertDeliveries(final UUID owner, final String rid, final List<Owed> owed, final Consumer<Boolean> cb) {
        if (!usable()) { if (cb != null) cb.accept(false); return; }
        ensureSchema();
        exec().submit(() -> {
            boolean ok = false;
            try (Connection c = plugin.getMysqlStorage().getConnection()) {
                insertOwed(c, owner, rid, owed);
                ok = true;
            } catch (Throwable t) {
                plugin.getLogger().log(Level.SEVERE, "[" + EmberMode.MODE_ID + "] delivery insert " + rid + " for " + owner + " FAILED: " + t.getMessage());
            }
            health(ok ? TxnStatus.OK : TxnStatus.ERROR);
            final boolean r = ok;
            if (cb != null) sync(() -> cb.accept(r));
        });
    }

    /** D162: the owner's pending delivery rows, oldest first; cb on the main thread (null when the read failed) */
    public void pendingDeliveries(final UUID owner, final Consumer<List<Delivery>> cb) {
        if (!usable()) { cb.accept(null); return; }
        ensureSchema();
        exec().submit(() -> {
            List<Delivery> out = new java.util.ArrayList<Delivery>();
            boolean ok = false;
            try (Connection c = plugin.getMysqlStorage().getConnection();
                 PreparedStatement ps = c.prepareStatement("SELECT id,request_id,idx,kind,item,amount,attempts,note FROM cr_p1_delivery"
                         + " WHERE owner_uuid=? AND status='pending' ORDER BY id")) {
                ps.setString(1, owner.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) out.add(new Delivery(rs.getLong(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5),
                            rs.getLong(6), rs.getInt(7), rs.getString(8)));
                }
                ok = true;
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "[" + EmberMode.MODE_ID + "] pending deliveries " + owner + ": " + t.getMessage());
            }
            final List<Delivery> r = ok ? out : null;
            sync(() -> cb.accept(r));
        });
    }

    /**
     * D162: close a delivery row ({@code status} delivered / void) or, with status null, just count a failed attempt and
     * keep it pending with the reason. cb(true) only when the row really changed.
     */
    public void ackDelivery(final long id, final String status, final String reason, final Consumer<Boolean> cb) {
        if (!usable()) { if (cb != null) cb.accept(false); return; }
        exec().submit(() -> {
            boolean ok = false;
            try (Connection c = plugin.getMysqlStorage().getConnection();
                 PreparedStatement ps = c.prepareStatement(status == null
                         ? "UPDATE cr_p1_delivery SET attempts=attempts+1,reason=?,updated_at=? WHERE id=? AND status='pending'"
                         : "UPDATE cr_p1_delivery SET status=?,attempts=attempts+1,reason=?,updated_at=? WHERE id=? AND status='pending'")) {
                int i = 1;
                if (status != null) ps.setString(i++, status);
                String rs = reason == null ? null : (reason.length() > 160 ? reason.substring(0, 160) : reason);
                if (rs == null) ps.setNull(i++, Types.VARCHAR); else ps.setString(i++, rs);
                ps.setLong(i++, System.currentTimeMillis());
                ps.setLong(i, id);
                ok = ps.executeUpdate() == 1;
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "[" + EmberMode.MODE_ID + "] delivery ack " + id + ": " + t.getMessage());
            }
            final boolean r = ok;
            if (cb != null) sync(() -> cb.accept(r));
        });
    }

    /**
     * D162 (review A02): auto-stash of a new piece as ONE transaction (item row stored + create ledger row + library flags),
     * run synchronously so the caller only reports "已存入装备库" after COMMIT. A few ms on the main thread, once per drop.
     * false = nothing written (or the commit is unknown and the row is not there) → the caller gives a backpack stack.
     */
    public boolean autoStashNow(EmberItemData d, UUID owner, String note, long storedAt) {
        if (!usable() || !schemaOk) return false;
        Connection c = null;
        boolean ok = false;
        try {
            c = plugin.getMysqlStorage().getConnection();
            c.setAutoCommit(false);
            long now = System.currentTimeMillis();
            try (PreparedStatement ps = c.prepareStatement("SELECT state FROM cr_p1_item WHERE item_uid=? FOR UPDATE")) {
                ps.setString(1, d.uid);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) { c.rollback(); return false; } } // re-delivery of a known uid: not ours to stash
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO cr_p1_item (item_uid,owner_uuid,ni_id,family,slot,tier,quality,craft,enhance,pity,bound,source,data_version,rev,state,created_at,updated_at)"
                            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,'stored',?,?)")) {
                int i = 1;
                ps.setString(i++, d.uid); ps.setString(i++, owner.toString()); ps.setString(i++, d.ni); ps.setString(i++, d.family);
                ps.setString(i++, d.slot); ps.setInt(i++, d.tier); ps.setInt(i++, d.quality); ps.setInt(i++, d.craft);
                ps.setInt(i++, d.enhance); ps.setInt(i++, d.pity); ps.setInt(i++, d.bound ? 1 : 0); ps.setString(i++, d.source);
                ps.setInt(i++, d.version); ps.setInt(i++, d.rev); ps.setLong(i++, now); ps.setLong(i, now);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT IGNORE INTO cr_p1_txn (request_id,kind,owner_uuid,uid_a,uid_b,before_json,after_json,cost_json,result,note,created_at)"
                            + " VALUES (?,'create',?,?,NULL,'[null]',?,NULL,'ok',?,?)")) {
                ps.setString(1, "create:" + d.uid); ps.setString(2, owner.toString()); ps.setString(3, d.uid);
                ps.setString(4, json(d));
                String nt = note == null ? null : (note.length() > 250 ? note.substring(0, 250) : note);
                if (nt == null) ps.setNull(5, Types.VARCHAR); else ps.setString(5, nt);
                ps.setLong(6, now);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO cr_p1_gearlib (item_uid,owner_uuid,locked,fav,stored_at) VALUES (?,?,0,0,?)"
                    + " ON DUPLICATE KEY UPDATE owner_uuid=VALUES(owner_uuid),locked=0,fav=0,stored_at=VALUES(stored_at)")) {
                ps.setString(1, d.uid); ps.setString(2, owner.toString()); ps.setLong(3, storedAt);
                ps.executeUpdate();
            }
            if (EmberFaults.fire(owner, EmberFaults.BEFORE_COMMIT)) throw new SQLException("TEST FAULT before_commit (auto-stash)");
            c.commit();
            ok = true;
        } catch (Throwable t) {
            try { if (c != null) c.rollback(); } catch (Throwable ignored) {}
            plugin.getLogger().log(Level.WARNING, "[" + EmberMode.MODE_ID + "] auto-stash " + d.uid + " not committed: " + t.getMessage());
        } finally {
            if (c != null) {
                try { c.setAutoCommit(true); } catch (Throwable ignored) {}
                try { c.close(); } catch (Throwable ignored) {}
            }
        }
        health(ok ? TxnStatus.OK : TxnStatus.ERROR);
        return ok;
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
                    if (!it.expectState.equals(rs.getString(3))) return new TxnResult(TxnStatus.CONFLICT, "DB 状态 " + rs.getString(3));
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
        // D162 (A03): a cost paid up front for this request is settled by the commit → its refund hold is void
        try (PreparedStatement ps = c.prepareStatement("UPDATE cr_p1_delivery SET status='void',reason='committed',updated_at=?"
                + " WHERE owner_uuid=? AND request_id=? AND status='hold'")) {
            ps.setLong(1, now);
            ps.setString(2, owner.toString());
            ps.setString(3, holdId(rid));
            ps.executeUpdate();
        }
        return new TxnResult(TxnStatus.OK, null);
    }

    static String holdId(String rid) { String h = "refund:" + rid; return h.length() > 96 ? h.substring(0, 96) : h; }

    /**
     * D162 (A03): durable op record for a cost paid BEFORE the item transaction (forge materials / coins). Written as
     * status 'hold' (request {@code refund:<rid>}) before the cost is taken; the item transaction voids it in the same
     * COMMIT; a failed / replayed transaction releases it (→ pending → refunded exactly once by EmberDelivery); a crash
     * in between is settled by {@link #reconcileHolds}: committed request → void, else → pending (refund).
     */
    public void insertHolds(final UUID owner, final String rid, final List<Owed> owed, final Consumer<Boolean> cb) {
        if (!usable()) { cb.accept(false); return; }
        ensureSchema();
        exec().submit(() -> {
            boolean ok = false;
            try (Connection c = plugin.getMysqlStorage().getConnection()) {
                // one attempt in flight per request; a retry after a refunded attempt gets the next block of 16 idx
                int base = 0;
                boolean inFlight = false;
                try (PreparedStatement ps = c.prepareStatement("SELECT status,idx FROM cr_p1_delivery WHERE owner_uuid=? AND request_id=?")) {
                    ps.setString(1, owner.toString());
                    ps.setString(2, holdId(rid));
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) { if ("hold".equals(rs.getString(1))) inFlight = true; base = Math.max(base, (rs.getInt(2) / 16 + 1) * 16); }
                    }
                }
                ok = !inFlight && insertOwed(c, owner, holdId(rid), owed, "hold", base) == owed.size();
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "[" + EmberMode.MODE_ID + "] refund hold " + rid + ": " + t.getMessage());
            }
            health(ok ? TxnStatus.OK : TxnStatus.ERROR);
            final boolean r = ok;
            sync(() -> cb.accept(r));
        });
    }

    /** D162: nothing was taken (pay failed / not durable) → the hold is void, never refunded */
    public void voidHolds(final UUID owner, final String rid) {
        run("void hold " + rid, c -> {
            try (PreparedStatement ps = c.prepareStatement("UPDATE cr_p1_delivery SET status='void',reason='not paid',updated_at=?"
                    + " WHERE owner_uuid=? AND request_id=? AND status='hold'")) {
                ps.setLong(1, System.currentTimeMillis());
                ps.setString(2, owner.toString());
                ps.setString(3, holdId(rid));
                ps.executeUpdate();
            }
        });
    }

    /** D162: the transaction did not happen → the held refund becomes a pending delivery. cb on the main thread. */
    public void releaseHolds(final UUID owner, final String rid, final Runnable cb) {
        run("release hold " + rid, c -> {
            try (PreparedStatement ps = c.prepareStatement("UPDATE cr_p1_delivery SET status='pending',reason='txn not committed',updated_at=?"
                    + " WHERE owner_uuid=? AND request_id=? AND status='hold'")) {
                ps.setLong(1, System.currentTimeMillis());
                ps.setString(2, owner.toString());
                ps.setString(3, holdId(rid));
                ps.executeUpdate();
            }
            if (cb != null) sync(cb);
        });
    }

    /** D162: holds older than {@code olderThan} left by a crash: committed request → void, otherwise → pending (refund) */
    public void reconcileHolds(final UUID owner, final long olderThan, final Runnable cb) {
        if (!usable()) { if (cb != null) cb.run(); return; }
        ensureSchema();
        exec().submit(() -> {
            try (Connection c = plugin.getMysqlStorage().getConnection();
                 PreparedStatement ps = c.prepareStatement("UPDATE cr_p1_delivery d LEFT JOIN cr_p1_txn t ON t.request_id=SUBSTRING(d.request_id,8)"
                         + " SET d.status=IF(t.request_id IS NULL,'pending','void'),"
                         + " d.reason=IF(t.request_id IS NULL,'reconciled: txn missing → refund','reconciled: committed'),d.updated_at=?"
                         + " WHERE d.owner_uuid=? AND d.status='hold' AND d.created_at<?")) {
                ps.setLong(1, System.currentTimeMillis());
                ps.setString(2, owner.toString());
                ps.setLong(3, olderThan);
                int n = ps.executeUpdate();
                if (n > 0) plugin.getLogger().warning("[" + EmberMode.MODE_ID + "] reconciled " + n + " refund hold(s) of " + owner);
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "[" + EmberMode.MODE_ID + "] reconcile holds " + owner + ": " + t.getMessage());
            }
            if (cb != null) sync(cb);
        });
    }

    // ------------------------------------------------------------------ D172: durable payment for undo / mark redeem / reroll

    /**
     * D172: refund holds for several requests in ONE DB transaction (a batch undo pays once for all its pieces): all
     * inserted, or none (any request with a hold still in flight → nothing). cb(ok) on the main thread.
     */
    public void insertHoldsAll(final UUID owner, final Map<String, List<Owed>> owed, final Consumer<Boolean> cb) {
        if (!usable()) { cb.accept(false); return; }
        ensureSchema();
        exec().submit(() -> {
            boolean ok = false;
            Connection c = null;
            try {
                c = plugin.getMysqlStorage().getConnection();
                c.setAutoCommit(false);
                boolean good = true;
                for (Map.Entry<String, List<Owed>> e : owed.entrySet()) {
                    if (e.getValue() == null || e.getValue().isEmpty()) continue;
                    int base = 0;
                    boolean inFlight = false;
                    try (PreparedStatement ps = c.prepareStatement("SELECT status,idx FROM cr_p1_delivery WHERE owner_uuid=? AND request_id=? FOR UPDATE")) {
                        ps.setString(1, owner.toString());
                        ps.setString(2, holdId(e.getKey()));
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) { if ("hold".equals(rs.getString(1))) inFlight = true; base = Math.max(base, (rs.getInt(2) / 16 + 1) * 16); }
                        }
                    }
                    if (inFlight || insertOwed(c, owner, holdId(e.getKey()), e.getValue(), "hold", base) != e.getValue().size()) { good = false; break; }
                }
                if (good) { c.commit(); ok = true; } else c.rollback();
            } catch (Throwable t) {
                try { if (c != null) c.rollback(); } catch (Throwable ignored) {}
                plugin.getLogger().log(Level.WARNING, "[" + EmberMode.MODE_ID + "] refund holds " + owed.keySet() + ": " + t.getMessage());
            } finally {
                if (c != null) {
                    try { c.setAutoCommit(true); } catch (Throwable ignored) {}
                    try { c.close(); } catch (Throwable ignored) {}
                }
            }
            health(ok ? TxnStatus.OK : TxnStatus.ERROR);
            final boolean r = ok;
            sync(() -> cb.accept(r));
        });
    }

    private interface TxnBody { TxnResult run(Connection c, long now) throws SQLException; }

    /** commitTxn's frame (faults, COMMIT / rollback, Duplicate → replay, main-thread callback) around another body */
    private void runTxn(final String rid, final String kind, final UUID owner, final TxnBody body, final Consumer<TxnResult> cb) {
        if (!usable()) { cb.accept(new TxnResult(TxnStatus.ERROR, "MySQL 不可用")); return; }
        ensureSchema();
        final boolean failBefore = EmberFaults.fire(owner, EmberFaults.BEFORE_COMMIT);
        final boolean dropCb = !failBefore && EmberFaults.fire(owner, EmberFaults.AFTER_COMMIT);
        exec().submit(() -> {
            TxnResult res;
            Connection c = null;
            try {
                c = plugin.getMysqlStorage().getConnection();
                c.setAutoCommit(false);
                try (PreparedStatement ps = c.prepareStatement("SELECT note FROM cr_p1_txn WHERE request_id=?")) {
                    ps.setString(1, rid);
                    try (ResultSet rs = ps.executeQuery()) { res = rs.next() ? new TxnResult(TxnStatus.REPLAY, rs.getString(1)) : null; }
                }
                if (res == null) res = body.run(c, System.currentTimeMillis());
                if (res.status == TxnStatus.OK && failBefore) throw new SQLException("TEST FAULT before_commit");
                if (res.status == TxnStatus.OK) c.commit(); else c.rollback();
            } catch (Throwable t) {
                try { if (c != null) c.rollback(); } catch (Throwable ignored) {}
                String m = t.getMessage() == null ? t.getClass().getSimpleName() : t.getMessage();
                res = m.contains("Duplicate") ? new TxnResult(TxnStatus.REPLAY, "并发重复请求") : new TxnResult(TxnStatus.ERROR, m);
                plugin.getLogger().log(Level.WARNING, "[" + EmberMode.MODE_ID + "] txn " + kind + " " + rid + " failed: " + m);
            } finally {
                if (c != null) {
                    try { c.setAutoCommit(true); } catch (Throwable ignored) {}
                    try { c.close(); } catch (Throwable ignored) {}
                }
            }
            final TxnResult r = res;
            health(r.status);
            if (dropCb && r.status == TxnStatus.OK) {
                plugin.getLogger().warning("[" + EmberMode.MODE_ID + "] TEST FAULT after_commit: txn " + kind + " " + rid + " committed, callback held 10 s");
                if (plugin.isEnabled()) Bukkit.getScheduler().runTaskLater(plugin, () -> cb.accept(r), 200L);
                return;
            }
            if (plugin.isEnabled()) Bukkit.getScheduler().runTask(plugin, () -> cb.accept(r));
        });
    }

    private static void ledger(Connection c, String rid, String kind, UUID owner, String uidA, String before, String after,
                               String costJson, String note, long now) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO cr_p1_txn (request_id,kind,owner_uuid,uid_a,uid_b,before_json,after_json,cost_json,result,note,created_at)"
                        + " VALUES (?,?,?,?,NULL,?,?,?,'ok',?,?)")) {
            ps.setString(1, rid); ps.setString(2, kind); ps.setString(3, owner.toString()); ps.setString(4, uidA);
            ps.setString(5, before); ps.setString(6, after);
            if (costJson == null) ps.setNull(7, Types.VARCHAR); else ps.setString(7, costJson);
            String nt = note == null ? null : (note.length() > 250 ? note.substring(0, 250) : note);
            if (nt == null) ps.setNull(8, Types.VARCHAR); else ps.setString(8, nt);
            ps.setLong(9, now);
            ps.executeUpdate();
        }
    }

    private static void voidHold(Connection c, UUID owner, String rid, long now) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE cr_p1_delivery SET status='void',reason='committed',updated_at=?"
                + " WHERE owner_uuid=? AND request_id=? AND status='hold'")) {
            ps.setLong(1, now);
            ps.setString(2, owner.toString());
            ps.setString(3, holdId(rid));
            ps.executeUpdate();
        }
    }

    /**
     * D172 (forge review X1 / X4): a NEW item paid up front (mark redemption) — item row (active, owner), ledger row,
     * the owed lines (the piece itself as a gear delivery) and the void of the refund hold in ONE DB transaction.
     */
    public void commitCreate(final String rid, final String kind, final UUID owner, final EmberItemData d, final String costJson,
                             final String note, final List<Owed> owed, final Consumer<TxnResult> cb) {
        runTxn(rid, kind, owner, (c, now) -> {
            try (PreparedStatement ps = c.prepareStatement("SELECT state FROM cr_p1_item WHERE item_uid=? FOR UPDATE")) {
                ps.setString(1, d.uid);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return new TxnResult(TxnStatus.CONFLICT, "物品编号已存在 " + d.uid.substring(0, 8)); }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO cr_p1_item (item_uid,owner_uuid,ni_id,family,slot,tier,quality,craft,enhance,pity,bound,source,data_version,rev,state,created_at,updated_at)"
                            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,'active',?,?)")) {
                int i = 1;
                ps.setString(i++, d.uid); ps.setString(i++, owner.toString()); ps.setString(i++, d.ni); ps.setString(i++, d.family);
                ps.setString(i++, d.slot); ps.setInt(i++, d.tier); ps.setInt(i++, d.quality); ps.setInt(i++, d.craft);
                ps.setInt(i++, d.enhance); ps.setInt(i++, d.pity); ps.setInt(i++, d.bound ? 1 : 0); ps.setString(i++, d.source);
                ps.setInt(i++, d.version); ps.setInt(i++, d.rev); ps.setLong(i++, now); ps.setLong(i, now);
                ps.executeUpdate();
            }
            ledger(c, rid, kind, owner, d.uid, "[null]", json(d), costJson, note, now);
            if (owed != null && !owed.isEmpty()) insertOwed(c, owner, rid, owed);
            voidHold(c, owner, rid, now);
            return new TxnResult(TxnStatus.OK, null);
        }, cb);
    }

    /**
     * D172 (X15): a payment that buys no item change by itself (affix reroll with shards: the result lives in PlayerData)
     * — ledger row + void of the refund hold in one DB transaction. The row is what makes the paid roll "owed".
     */
    public void commitPlain(final String rid, final String kind, final UUID owner, final String uidA, final String costJson,
                            final String note, final Consumer<TxnResult> cb) {
        runTxn(rid, kind, owner, (c, now) -> {
            ledger(c, rid, kind, owner, uidA, "[null]", "[null]", costJson, note, now);
            voidHold(c, owner, rid, now);
            return new TxnResult(TxnStatus.OK, null);
        }, cb);
    }

    /** D172: "committed" (ledger row exists) / "hold" (its refund hold still open) / "none"; null when unreadable */
    public void txnState(final UUID owner, final String rid, final Consumer<String> cb) {
        if (!usable()) { cb.accept(null); return; }
        ensureSchema();
        exec().submit(() -> {
            String st = null;
            try (Connection c = plugin.getMysqlStorage().getConnection()) {
                try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM cr_p1_txn WHERE request_id=?")) {
                    ps.setString(1, rid);
                    try (ResultSet rs = ps.executeQuery()) { if (rs.next()) st = "committed"; }
                }
                if (st == null) try (PreparedStatement ps = c.prepareStatement(
                        "SELECT COUNT(*) FROM cr_p1_delivery WHERE owner_uuid=? AND request_id=? AND status='hold'")) {
                    ps.setString(1, owner.toString());
                    ps.setString(2, holdId(rid));
                    try (ResultSet rs = ps.executeQuery()) { st = rs.next() && rs.getInt(1) > 0 ? "hold" : "none"; }
                }
            } catch (Throwable t) {
                st = null;
                plugin.getLogger().log(Level.WARNING, "[" + EmberMode.MODE_ID + "] txn state " + rid + ": " + t.getMessage());
            }
            final String r = st;
            sync(() -> cb.accept(r));
        });
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

    // ------------------------------------------------------------------ 1.62 ledger: create rows, history, gear library

    /**
     * Ledger row for a newly created item (drop / quest / admin / auto-stash). Request id {@code create:<uid>}, INSERT
     * IGNORE so a re-delivery of the same reward uid never writes twice. {@code state} is the state the row starts in.
     */
    public void logCreate(final EmberItemData d, final UUID owner, final String state, final String note) {
        if (d == null || owner == null) return;
        final String after = json(d), nt = note == null ? null : (note.length() > 250 ? note.substring(0, 250) : note);
        run("ledger create " + d.uid, c -> {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT IGNORE INTO cr_p1_txn (request_id,kind,owner_uuid,uid_a,uid_b,before_json,after_json,cost_json,result,note,created_at)"
                            + " VALUES (?,?,?,?,NULL,'[null]',?,NULL,?,?,?)")) {
                ps.setString(1, "create:" + d.uid);
                ps.setString(2, "create");
                ps.setString(3, owner.toString());
                ps.setString(4, d.uid);
                ps.setString(5, after);
                ps.setString(6, "ok");
                if (nt == null) ps.setString(7, d.source + (state == null || "active".equals(state) ? "" : " → " + state));
                else ps.setString(7, nt);
                ps.setLong(8, System.currentTimeMillis());
                ps.executeUpdate();
            }
        });
    }

    /** One cr_p1_txn row for the admin item history. */
    public static final class TxnRow {
        public final String rid, kind, owner, uidA, uidB, cost, note; public final long at;
        TxnRow(String rid, String kind, String owner, String uidA, String uidB, String cost, String note, long at) {
            this.rid = rid; this.kind = kind; this.owner = owner; this.uidA = uidA; this.uidB = uidB; this.cost = cost; this.note = note; this.at = at;
        }
    }

    /** Newest first: by owner ({@code uidPrefix} null) or by item uid prefix (≥ 6 hex chars, any owner). */
    public void history(final UUID owner, final String uidPrefix, final int limit, final Consumer<List<TxnRow>> cb) {
        run("history", c -> {
            final List<TxnRow> out = new java.util.ArrayList<TxnRow>();
            String sql = uidPrefix != null
                    ? "SELECT request_id,kind,owner_uuid,uid_a,uid_b,cost_json,note,created_at FROM cr_p1_txn WHERE uid_a LIKE ? OR uid_b LIKE ? ORDER BY created_at DESC LIMIT ?"
                    : "SELECT request_id,kind,owner_uuid,uid_a,uid_b,cost_json,note,created_at FROM cr_p1_txn WHERE owner_uuid=? ORDER BY created_at DESC LIMIT ?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                int i = 1;
                if (uidPrefix != null) { ps.setString(i++, uidPrefix + "%"); ps.setString(i++, uidPrefix + "%"); }
                else ps.setString(i++, owner.toString());
                ps.setInt(i, Math.max(1, Math.min(200, limit)));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) out.add(new TxnRow(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                            rs.getString(5), rs.getString(6), rs.getString(7), rs.getLong(8)));
                }
            }
            sync(() -> cb.accept(out));
        });
    }

    /** A stored (gear library) or recently dismantled item of one owner, with the library flags. */
    public static final class LibRow {
        public final EmberItemData data; public final String state; public final boolean locked, fav; public final long storedAt, updatedAt;
        /** 1.64.1: txn note (recent dismantles only; carries the bulk batch tag) */
        public String note;
        LibRow(EmberItemData data, String state, boolean locked, boolean fav, long storedAt, long updatedAt) {
            this.data = data; this.state = state; this.locked = locked; this.fav = fav; this.storedAt = storedAt; this.updatedAt = updatedAt;
        }
    }

    /** Every row of {@code owner} in {@code state} (stored = gear library; dismantled = undo candidates, newer than {@code since}). */
    public void loadByState(final UUID owner, final String state, final long since, final Consumer<List<LibRow>> cb) {
        run("load " + state + " " + owner, c -> {
            final List<LibRow> out = new java.util.ArrayList<LibRow>();
            try (PreparedStatement ps = c.prepareStatement("SELECT i.item_uid,i.ni_id,i.family,i.slot,i.tier,i.quality,i.craft,i.enhance,i.pity,"
                    + "i.bound,i.source,i.data_version,i.rev,i.state,COALESCE(g.locked,0),COALESCE(g.fav,0),COALESCE(g.stored_at,i.updated_at),i.updated_at"
                    + " FROM cr_p1_item i LEFT JOIN cr_p1_gearlib g ON g.item_uid=i.item_uid WHERE i.owner_uuid=? AND i.state=? AND i.updated_at>=?")) {
                ps.setString(1, owner.toString());
                ps.setString(2, state);
                ps.setLong(3, since);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        EmberItemData d = new EmberItemData(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5),
                                rs.getInt(6), rs.getInt(7), rs.getInt(8), rs.getInt(9), rs.getInt(10) != 0, rs.getString(11), rs.getInt(12), rs.getInt(13));
                        out.add(new LibRow(d, rs.getString(14), rs.getInt(15) != 0, rs.getInt(16) != 0, rs.getLong(17), rs.getLong(18)));
                    }
                }
            }
            sync(() -> cb.accept(out));
        });
    }

    /** Gear library flags (upsert); {@code storedAt} 0 keeps the current value. */
    public void saveLibFlags(final String uid, final UUID owner, final boolean locked, final boolean fav, final long storedAt) {
        run("gearlib flags " + uid, c -> {
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO cr_p1_gearlib (item_uid,owner_uuid,locked,fav,stored_at) VALUES (?,?,?,?,?)"
                    + " ON DUPLICATE KEY UPDATE owner_uuid=VALUES(owner_uuid),locked=VALUES(locked),fav=VALUES(fav),"
                    + "stored_at=IF(VALUES(stored_at)>0,VALUES(stored_at),stored_at)")) {
                ps.setString(1, uid);
                ps.setString(2, owner.toString());
                ps.setInt(3, locked ? 1 : 0);
                ps.setInt(4, fav ? 1 : 0);
                ps.setLong(5, storedAt);
                ps.executeUpdate();
            }
        });
    }

    /**
     * Soft-undo candidates: items of {@code owner} whose dismantle (kind dismantle / glibdis, not reroll) committed at or
     * after {@code since} and that are still dismantled. Newest first.
     */
    public void recentDismantles(final UUID owner, final long since, final Consumer<List<LibRow>> cb) {
        run("recent dismantles " + owner, c -> {
            final List<LibRow> out = new java.util.ArrayList<LibRow>();
            try (PreparedStatement ps = c.prepareStatement("SELECT i.item_uid,i.ni_id,i.family,i.slot,i.tier,i.quality,i.craft,i.enhance,i.pity,"
                    + "i.bound,i.source,i.data_version,i.rev,i.state,t.created_at,t.note FROM cr_p1_txn t JOIN cr_p1_item i ON i.item_uid=t.uid_a"
                    + " WHERE t.owner_uuid=? AND t.kind IN ('dismantle','glibdis') AND t.created_at>=? AND i.state='dismantled'"
                    + " AND i.owner_uuid=? ORDER BY t.created_at DESC LIMIT 500")) {
                ps.setString(1, owner.toString());
                ps.setLong(2, since);
                ps.setString(3, owner.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        EmberItemData d = new EmberItemData(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5),
                                rs.getInt(6), rs.getInt(7), rs.getInt(8), rs.getInt(9), rs.getInt(10) != 0, rs.getString(11), rs.getInt(12), rs.getInt(13));
                        LibRow lr = new LibRow(d, rs.getString(14), false, false, rs.getLong(15), rs.getLong(15));
                        lr.note = rs.getString(16);
                        out.add(lr);
                    }
                }
            }
            sync(() -> cb.accept(out));
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
