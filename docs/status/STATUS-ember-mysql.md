# STATUS · CoreRpg MySQL storage (1.4.0)

**日期：** 2026-09-13（Asia/Shanghai）  
**版本：** CoreRpg **1.4.0**（自 1.3.11）  
**拓扑：** 登录服 + 单游玩服；CoreRpg DB = `ember`（AuthMe 另库 `authme`）

## 默认存储

- **`storage: yaml`**（生产 / 本机 plugins 配置保持 yaml，不自动切 mysql）
- `storage: mysql` 时用 HikariCP 4.0.3 + mysql-connector-java 8.0.33（shade 进 jar）
- 连接失败：**FAIL-OPEN → yaml**，打 severe 日志，游玩服仍可启动

## 凭据

见 `secrets/mysql-ember.env`（host/port/user/pass/db）。示例：`CoreRpg/config-mysql.example.yml`、`plugins/CoreRpg/config-mysql.example.yml`。

## 配置键

```yaml
storage: yaml   # yaml | mysql
mysql:
  host: 127.0.0.1
  port: 3306
  database: ember
  username: ember
  password: "…"   # 见 secrets
  pool-size: 10
  jdbc-params: "useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8&serverTimezone=UTC"
```

## DDL（`CoreRpg/sql/schema.sql`）

```sql
CREATE TABLE IF NOT EXISTS cr_players (
  uuid CHAR(36) PRIMARY KEY,
  name VARCHAR(16) NULL,
  data LONGTEXT NOT NULL,  -- YamlConfiguration.saveToString() of PlayerData
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cr_guilds (
  id VARCHAR(64) PRIMARY KEY,
  data LONGTEXT NOT NULL,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cr_auction_blob (
  id INT PRIMARY KEY,  -- always 1
  data LONGTEXT NOT NULL,  -- next_id + listings.* YAML
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS cr_mail (
  uuid CHAR(36) NOT NULL,
  mail_id VARCHAR(64) NOT NULL,  -- 'inbox' = full mail/<uuid>.yml
  data LONGTEXT NOT NULL,
  PRIMARY KEY (uuid, mail_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

## 数据映射

| 原 YAML | MySQL |
|---------|--------|
| `players/<uuid>.yml` | `cr_players.data`（同字段 YAML 文本 roundtrip） |
| `guilds/<id>.yml` | `cr_guilds.data` |
| `auction.yml` listings | `cr_auction_blob` id=1 |
| `mail/<uuid>.yml` | `cr_mail` (uuid, mail_id='inbox') |

## Shade relocate

- `com.zaxxer.hikari` → `town.sunshine.corerpg.lib.hikari`
- `com.mysql` → `town.sunshine.corerpg.lib.mysql`

## 指令

- `/corerpg storage` — 显示有效模式 + MySQL ping
- `/corerpg admin migrate-yaml-to-mysql`（`corerpg.admin`）— 一次性导入 players/guilds/auction/mail → MySQL（需已 `storage: mysql` 且连接成功）

## 生命周期

- enable：若 mysql → 建池 + CREATE IF NOT EXISTS；失败回退 yaml
- disable：flush all + close pool
- 定时 save（原有）在 mysql 路径写库

## 本机 DB

- MariaDB 已有库 `ember` / 用户 `ember`（见 secrets）
- 默认 **不** 把 live `plugins/CoreRpg/config.yml` 切到 mysql

## 构建产物

- `plugins/CoreRpg.jar`（shaded）约 **4.5M**（4696271 bytes）
- 本机：`ember` 库已建表；smoke insert/select `cr_players` OK
- 游玩服已重启：`CoreRpg 1.4.0 enabled (storage=yaml)`

## 迁移指令

```
/corerpg admin migrate-yaml-to-mysql
```
（需 `corerpg.admin` 且当前已 `storage: mysql` 连接成功）

## 游玩服切库（总控 2026-09-13）

- `plugins/CoreRpg/config.yml` → `storage: mysql`（凭据对齐 secrets）
- 启服日志：`[storage] MySQL connected: 127.0.0.1:3306/ember`
- `/corerpg admin migrate-yaml-to-mysql` → players=8 guilds=1 mail=1 auction=ok
- `/corerpg storage` → mysql ping 1ms
