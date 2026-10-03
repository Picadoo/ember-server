package town.sunshine.coregacha;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Hikari pool on CoreRpg's database. Credentials are read at runtime from CoreRpg's config ({@code mysql:} section of
 * plugins/CoreRpg/config.yml) — CoreGacha's own files never hold a password, and nothing here logs it.
 */
public final class Db {
    static final String[] SCHEMA = {
        "CREATE TABLE IF NOT EXISTS gacha_wallet (uuid CHAR(36) PRIMARY KEY, name VARCHAR(16) NULL, tickets INT NOT NULL DEFAULT 0,"
            + " shards INT NOT NULL DEFAULT 0, welcomed TINYINT NOT NULL DEFAULT 0,"
            + " updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
        "CREATE TABLE IF NOT EXISTS gacha_pity (uuid CHAR(36) NOT NULL, pity_group VARCHAR(32) NOT NULL, pity5 INT NOT NULL DEFAULT 0,"
            + " pity4 INT NOT NULL DEFAULT 0, PRIMARY KEY (uuid, pity_group)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
        "CREATE TABLE IF NOT EXISTS gacha_banner_state (uuid CHAR(36) NOT NULL, banner VARCHAR(32) NOT NULL, spark INT NOT NULL DEFAULT 0,"
            + " last_legend VARCHAR(64) NULL, pulls INT NOT NULL DEFAULT 0, settled TINYINT NOT NULL DEFAULT 0,"
            + " PRIMARY KEY (uuid, banner)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
        "CREATE TABLE IF NOT EXISTS gacha_owned (uuid CHAR(36) NOT NULL, item VARCHAR(64) NOT NULL, source VARCHAR(16) NOT NULL,"
            + " batch_id CHAR(36) NULL, obtained_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3), PRIMARY KEY (uuid, item))"
            + " ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
        "CREATE TABLE IF NOT EXISTS gacha_wear (uuid CHAR(36) NOT NULL, kind VARCHAR(16) NOT NULL, item VARCHAR(64) NOT NULL,"
            + " PRIMARY KEY (uuid, kind)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
        "CREATE TABLE IF NOT EXISTS gacha_pull (pull_id BIGINT AUTO_INCREMENT PRIMARY KEY, batch_id CHAR(36) NOT NULL, uuid CHAR(36) NOT NULL,"
            + " name VARCHAR(16) NULL, banner VARCHAR(32) NOT NULL, pity_group VARCHAR(32) NOT NULL, seq TINYINT NOT NULL, tier VARCHAR(8) NOT NULL,"
            + " item VARCHAR(64) NOT NULL, dup TINYINT NOT NULL, shards INT NOT NULL, pity5_before INT NOT NULL, pity5_after INT NOT NULL,"
            + " pity4_before INT NOT NULL, pity4_after INT NOT NULL, spark_after INT NOT NULL, rule VARCHAR(8) NOT NULL, cost INT NOT NULL,"
            + " status VARCHAR(12) NOT NULL, created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),"
            + " KEY idx_uuid (uuid, pull_id), KEY idx_batch (batch_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
        "CREATE TABLE IF NOT EXISTS gacha_ledger (id BIGINT AUTO_INCREMENT PRIMARY KEY, uuid CHAR(36) NOT NULL, name VARCHAR(16) NULL,"
            + " d_tickets INT NOT NULL, d_shards INT NOT NULL, tickets_after INT NULL, shards_after INT NULL, reason VARCHAR(32) NOT NULL,"
            + " ref VARCHAR(64) NULL, created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3), KEY idx_uuid (uuid, id))"
            + " ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
        "CREATE TABLE IF NOT EXISTS gacha_daily (uuid CHAR(36) NOT NULL, day CHAR(10) NOT NULL, k VARCHAR(32) NOT NULL, v INT NOT NULL DEFAULT 0,"
            + " PRIMARY KEY (uuid, day, k)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
    };

    private HikariDataSource ds;

    public boolean open(File serverRoot, ConfigurationSection own, Logger log) {
        String path = own == null ? "plugins/CoreRpg/config.yml" : own.getString("corerpg_config", "plugins/CoreRpg/config.yml");
        File f = new File(path);
        if (!f.isAbsolute()) f = new File(serverRoot, path);
        ConfigurationSection m = YamlConfiguration.loadConfiguration(f).getConfigurationSection("mysql");
        if (m == null) { log.severe("[db] no mysql: section in " + f + " — CoreGacha disabled"); return false; }
        String host = m.getString("host", "127.0.0.1"), db = m.getString("database", "ember");
        int port = m.getInt("port", 3306);
        String params = m.getString("jdbc-params", "useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8&serverTimezone=UTC");
        try {
            HikariConfig c = new HikariConfig();
            c.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + db + (params.isEmpty() ? "" : "?" + params));
            c.setUsername(m.getString("username", "ember"));
            c.setPassword(m.getString("password", ""));
            c.setMaximumPoolSize(Math.max(2, own == null ? 4 : own.getInt("pool-size", 4)));
            c.setMinimumIdle(1);
            c.setPoolName("CoreGacha-Hikari");
            c.setConnectionTimeout(8000L);
            c.setInitializationFailTimeout(1L);
            c.setDriverClassName("com.mysql.cj.jdbc.Driver");
            ds = new HikariDataSource(c);
            try (Connection con = ds.getConnection(); Statement st = con.createStatement()) {
                for (String s : SCHEMA) st.executeUpdate(s);
            }
            log.info("[db] MySQL connected: " + host + ":" + port + "/" + db + " (CoreRpg credentials, tables gacha_*)");
            return true;
        } catch (Throwable t) {
            log.log(Level.SEVERE, "[db] MySQL init FAILED: " + t.getMessage());
            close();
            return false;
        }
    }

    public Connection get() throws SQLException { return ds.getConnection(); }

    public void close() { if (ds != null) { try { ds.close(); } catch (Throwable ignored) { } ds = null; } }
}
