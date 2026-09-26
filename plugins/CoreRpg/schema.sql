-- CoreRpg 1.4.5 MySQL / MariaDB schema (DB: ember)
-- Auto-applied on enable when storage: mysql

CREATE TABLE IF NOT EXISTS cr_players (
  uuid CHAR(36) PRIMARY KEY,
  name VARCHAR(16) NULL,
  data LONGTEXT NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cr_guilds (
  id VARCHAR(64) PRIMARY KEY,
  data LONGTEXT NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Single-row listings blob (id=1); next_id + listings.* as YamlConfiguration text
CREATE TABLE IF NOT EXISTS cr_auction_blob (
  id INT PRIMARY KEY,
  data LONGTEXT NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Per-player inbox: mail_id='inbox' holds full messages YAML (same as mail/<uuid>.yml)
CREATE TABLE IF NOT EXISTS cr_mail (
  uuid CHAR(36) NOT NULL,
  mail_id VARCHAR(64) NOT NULL,
  data LONGTEXT NOT NULL,
  PRIMARY KEY (uuid, mail_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Material warehouse (DESIGN-ember-material-warehouse)
CREATE TABLE IF NOT EXISTS cr_warehouse (
  uuid CHAR(36) PRIMARY KEY,
  slots_unlocked INT NOT NULL DEFAULT 8,
  slots_json MEDIUMTEXT,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

