# DESIGN · MySQL + 登录服 + 单游玩服

**日期：** 2026-09-13（Asia/Shanghai）  
**用户定：** 插件上 MySQL；网络拓扑 **登录服 + 一台游玩服** 即可（不做大群服 / 多玩法岛）。

---

## 1. 拓扑

```
客户端
  │
  ▼
BungeeCord / Velocity（可选，推荐）
  ├─ login   … 认证 / 排队 / 选角壳（AuthMe）
  └─ ember   … 唯一游玩服（现 Paper 1.12.2 余烬服）
         │
         ▼
   MariaDB（本机 127.0.0.1）
   ├─ authme.*     AuthMe 账号
   └─ ember.*      CoreRpg / 后续共享玩法数据
```

| 节点 | 作用 | 说明 |
|------|------|------|
| **login** | 登录服 | 只放 AuthMe + 限插件；无刷怪/副本 |
| **ember** | 游玩服 | 现有全套 Core* / DP / MM / NI / TrMenu |
| **MariaDB** | 共享库 | 账号与 RPG 进度跨服一致；日后扩服仍可读同一库 |

无代理时也可：先进 login，认证成功后 `server`/`connect` 踢到 ember（仍建议上 Bungee，省二次进服）。

---

## 2. 本机库（已建）

| 项 | 值 |
|----|-----|
| 引擎 | MariaDB 11.8 |
| 主机 | `127.0.0.1` |
| 库 | `ember` · `authme` |
| 用户 | `ember` |
| 密码 | 见 `secrets/mysql-ember.env`（勿进公开文档） |

插件 JDBC 示例：

```yaml
storage: mysql
mysql:
  host: 127.0.0.1
  port: 3306
  database: ember
  username: ember
  password: ${from env}
  pool-size: 10
```

DungeonPlus 已有 Hikari `datasource.yml`，可指向同实例不同库/表；CoreRpg 优先迁 `players/`。

---

## 3. `ember` 库表（CoreRpg 首期）

| 表 | 内容 |
|----|------|
| `cr_players` | UUID PK；原 `players/*.yml` 扁平列 + `extra_json` |
| `cr_guilds` | 盟约 |
| `cr_guild_members` | 成员 |
| `cr_auction` | 寄售行 |
| `cr_mail` | 邮件 |
| `cr_friends` | 好友边 |

首期允许：**主表列 + JSON 过渡列**，YAML `storage: yaml` 可回退。

`authme` 库：AuthMe 默认表结构，不与 CoreRpg 混表。

---

## 4. 迁库原则

1. 配置 `storage: mysql` 才写库；默认仍可 yaml 开发。  
2. 进服 load / 退出与定时 save；禁止热路径每击杀同步刷盘。  
3. 登录服 **不写** CoreRpg 玩法表（只 AuthMe）。  
4. 游玩服是唯一 CoreRpg 写者（单服无冲突；日后多服再上锁/分区）。

---

## 5. 落地顺序

1. ✅ MariaDB + `ember`/`authme` 库  
2. ⏳ CoreRpg MySQL storage（插件岗）  
3. ⏳ 起 login 纸片服 + AuthMe（MySQL）  
4. ⏳ Bungee/Velocity 串 login→ember（可选同期）  
5. ⏳ DP / 其它需持久插件按需接同一 MariaDB  

副本内容 / Multiverse 公共世界可并行，**不阻塞** MySQL 主线。

---

## 6. 相关

- CoreRpg 现状：`plugins/CoreRpg/players/*.yml`  
- DP：`plugins/DungeonPlus/datasource.yml`  
- 成长曲线：`DESIGN-ember-growth-curve.md`
