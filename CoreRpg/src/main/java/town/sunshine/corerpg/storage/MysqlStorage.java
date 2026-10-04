package town.sunshine.corerpg.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

/**
 * HikariCP pool + schema for CoreRpg MySQL storage.
 * Shade relocates com.zaxxer.hikari → town.sunshine.corerpg.lib.hikari
 * and com.mysql → town.sunshine.corerpg.lib.mysql at build time.
 */
public final class MysqlStorage {
    public static final String SCHEMA_PLAYERS =
            "CREATE TABLE IF NOT EXISTS cr_players ("
                    + "uuid CHAR(36) PRIMARY KEY,"
                    + "name VARCHAR(16) NULL,"
                    + "data LONGTEXT NOT NULL,"
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

    public static final String SCHEMA_GUILDS =
            "CREATE TABLE IF NOT EXISTS cr_guilds ("
                    + "id VARCHAR(64) PRIMARY KEY,"
                    + "data LONGTEXT NOT NULL,"
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

    public static final String SCHEMA_AUCTION_BLOB =
            "CREATE TABLE IF NOT EXISTS cr_auction_blob ("
                    + "id INT PRIMARY KEY,"
                    + "data LONGTEXT NOT NULL,"
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";


    public static final String SCHEMA_WAREHOUSE =
            "CREATE TABLE IF NOT EXISTS cr_warehouse ("
                    + "uuid CHAR(36) PRIMARY KEY,"
                    + "slots_unlocked INT NOT NULL DEFAULT 8,"
                    + "slots_json MEDIUMTEXT,"
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

    public static final String SCHEMA_MAIL =
            "CREATE TABLE IF NOT EXISTS cr_mail ("
                    + "uuid CHAR(36) NOT NULL,"
                    + "mail_id VARCHAR(64) NOT NULL,"
                    + "data LONGTEXT NOT NULL,"
                    + "PRIMARY KEY (uuid, mail_id)"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

    private final JavaPlugin plugin;
    private HikariDataSource dataSource;
    private boolean active;

    public MysqlStorage(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isActive() {
        return active && dataSource != null && !dataSource.isClosed();
    }

    /**
     * Open pool + create schema. Returns false on failure (caller falls back to yaml).
     */
    public boolean tryInit(ConfigurationSection mysqlSection) {
        active = false;
        if (mysqlSection == null) {
            plugin.getLogger().severe("[storage] mysql section missing; falling back to yaml");
            return false;
        }
        String host = mysqlSection.getString("host", "127.0.0.1");
        int port = mysqlSection.getInt("port", 3306);
        String database = mysqlSection.getString("database", "ember");
        String username = mysqlSection.getString("username", "ember");
        String password = mysqlSection.getString("password", "changeme");
        int poolSize = Math.max(1, mysqlSection.getInt("pool-size", 10));
        String jdbcParams = mysqlSection.getString("jdbc-params",
                "useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8&serverTimezone=UTC");

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database
                + (jdbcParams == null || jdbcParams.isEmpty() ? "" : "?" + jdbcParams);

        try {
            HikariConfig cfg = new HikariConfig();
            cfg.setJdbcUrl(jdbcUrl);
            cfg.setUsername(username);
            cfg.setPassword(password == null ? "" : password);
            cfg.setMaximumPoolSize(poolSize);
            cfg.setMinimumIdle(Math.min(2, poolSize));
            cfg.setPoolName("CoreRpg-Hikari");
            cfg.setConnectionTimeout(8000L);
            cfg.setValidationTimeout(3000L);
            cfg.setInitializationFailTimeout(1L);
            cfg.addDataSourceProperty("cachePrepStmts", "true");
            cfg.addDataSourceProperty("prepStmtCacheSize", "250");
            cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            // String rewritten by shade relocation com.mysql → town.sunshine.corerpg.lib.mysql
            cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");

            dataSource = new HikariDataSource(cfg);
            try (Connection c = dataSource.getConnection(); Statement st = c.createStatement()) {
                st.executeUpdate(SCHEMA_PLAYERS);
                st.executeUpdate(SCHEMA_GUILDS);
                st.executeUpdate(SCHEMA_AUCTION_BLOB);
                st.executeUpdate(SCHEMA_MAIL);
                st.executeUpdate(SCHEMA_WAREHOUSE);
            }
            active = true;
            plugin.getLogger().info("[storage] MySQL connected: " + host + ":" + port + "/" + database
                    + " pool=" + poolSize);
            return true;
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE,
                    "[storage] MySQL init FAILED — falling back to yaml. Cause: " + t.getMessage(), t);
            closeQuiet();
            active = false;
            return false;
        }
    }

    public void close() {
        closeQuiet();
        active = false;
    }

    private void closeQuiet() {
        if (dataSource != null) {
            try {
                dataSource.close();
            } catch (Throwable ignored) {
            }
            dataSource = null;
        }
    }

    public Connection getConnection() throws SQLException {
        if (!isActive()) throw new SQLException("MySQL storage not active");
        return dataSource.getConnection();
    }

    /** Ping; returns latency ms or -1 on failure. */
    public long pingMs() {
        if (!isActive()) return -1L;
        long t0 = System.currentTimeMillis();
        try (Connection c = getConnection(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT 1")) {
            if (rs.next()) return System.currentTimeMillis() - t0;
        } catch (SQLException e) {
            plugin.getLogger().warning("[storage] ping failed: " + e.getMessage());
        }
        return -1L;
    }

    public String loadPlayerYaml(UUID uuid) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT data FROM cr_players WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return null;
    }

    public void savePlayerYaml(UUID uuid, String name, String yamlData) throws SQLException {
        String sql = "INSERT INTO cr_players (uuid, name, data) VALUES (?,?,?) "
                + "ON DUPLICATE KEY UPDATE name=VALUES(name), data=VALUES(data)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, name == null || name.isEmpty() ? null : truncate(name, 16));
            ps.setString(3, yamlData);
            ps.executeUpdate();
        }
    }

    public List<UUID> listPlayerUuids() throws SQLException {
        List<UUID> out = new ArrayList<UUID>();
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT uuid FROM cr_players")) {
            while (rs.next()) {
                try {
                    out.add(UUID.fromString(rs.getString(1)));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        return out;
    }

    public Map<String, String> loadAllGuildYaml() throws SQLException {
        Map<String, String> out = new LinkedHashMap<String, String>();
        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, data FROM cr_guilds")) {
            while (rs.next()) {
                out.put(rs.getString(1), rs.getString(2));
            }
        }
        return out;
    }

    public void saveGuildYaml(String id, String yamlData) throws SQLException {
        String sql = "INSERT INTO cr_guilds (id, data) VALUES (?,?) "
                + "ON DUPLICATE KEY UPDATE data=VALUES(data)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, yamlData);
            ps.executeUpdate();
        }
    }

    public void deleteGuild(String id) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM cr_guilds WHERE id=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    public String loadAuctionBlob() throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT data FROM cr_auction_blob WHERE id=1")) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return null;
    }

    public void saveAuctionBlob(String yamlData) throws SQLException {
        String sql = "INSERT INTO cr_auction_blob (id, data) VALUES (1,?) "
                + "ON DUPLICATE KEY UPDATE data=VALUES(data)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, yamlData);
            ps.executeUpdate();
        }
    }

    public String loadMailInboxYaml(UUID uuid) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT data FROM cr_mail WHERE uuid=? AND mail_id='inbox'")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return null;
    }

    public void saveMailInboxYaml(UUID uuid, String yamlData) throws SQLException {
        String sql = "INSERT INTO cr_mail (uuid, mail_id, data) VALUES (?, 'inbox', ?) "
                + "ON DUPLICATE KEY UPDATE data=VALUES(data)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, yamlData);
            ps.executeUpdate();
        }
    }


    public static final class WarehouseRow {
        public final int slotsUnlocked;
        public final String slotsJson;
        public WarehouseRow(int slotsUnlocked, String slotsJson) {
            this.slotsUnlocked = slotsUnlocked;
            this.slotsJson = slotsJson;
        }
    }

    public WarehouseRow loadWarehouse(UUID uuid) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT slots_unlocked, slots_json FROM cr_warehouse WHERE uuid=?")) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new WarehouseRow(rs.getInt(1), rs.getString(2));
                }
            }
        }
        return null;
    }

    /**
     * D162 (review A03): the player row (counters incl. P1 delivery markers, coin, …) and the warehouse row in ONE
     * transaction, so a crash between the two statements can no longer leave a delivery marker without its materials
     * (or the materials without the marker).
     */
    public void savePlayerAndWarehouse(UUID uuid, String name, String yamlData, int slotsUnlocked, String slotsJson) throws SQLException {
        try (Connection c = getConnection()) {
            boolean auto = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO cr_players (uuid, name, data) VALUES (?,?,?) "
                        + "ON DUPLICATE KEY UPDATE name=VALUES(name), data=VALUES(data)")) {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, name == null || name.isEmpty() ? null : truncate(name, 16));
                    ps.setString(3, yamlData);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO cr_warehouse (uuid, slots_unlocked, slots_json) VALUES (?,?,?) "
                        + "ON DUPLICATE KEY UPDATE slots_unlocked=VALUES(slots_unlocked), slots_json=VALUES(slots_json)")) {
                    ps.setString(1, uuid.toString());
                    ps.setInt(2, slotsUnlocked);
                    ps.setString(3, slotsJson == null ? "[]" : slotsJson);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                try { c.rollback(); } catch (SQLException ignored) {}
                throw e;
            } finally {
                try { c.setAutoCommit(auto); } catch (SQLException ignored) {}
            }
        }
    }

    public void saveWarehouse(UUID uuid, int slotsUnlocked, String slotsJson) throws SQLException {
        String sql = "INSERT INTO cr_warehouse (uuid, slots_unlocked, slots_json) VALUES (?,?,?) "
                + "ON DUPLICATE KEY UPDATE slots_unlocked=VALUES(slots_unlocked), slots_json=VALUES(slots_json)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, slotsUnlocked);
            ps.setString(3, slotsJson == null ? "[]" : slotsJson);
            ps.executeUpdate();
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
