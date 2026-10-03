# STATUS · 游玩服 maxHealth 上限抬升（派活 4.3·Paper）

**日期：** 2026-09-27 16:48（Asia/Shanghai）  
**执行岗：** 余烬-Paper  
**依据：** 总控派活 4.3；`docs/status/STATUS-ember-abyss-ttk-layer2.md` 附加发现（MM Health 2200 > 上限 2048）

---

## 变更

| 项 | 值 |
|----|-----|
| 文件 | `server-runtime/spigot.yml` |
| 键 | `settings.attribute.maxHealth.max` |
| 改前 | **2048.0** |
| 改后 | **20000.0** |
| 未改 | `movementSpeed` / `attackDamage` 仍为 2048.0；`paper-custom.jar` / NMS **未动**；login-runtime **未动** |

## 重启

- 已短重启游玩服：`./stop.sh` → `./start.sh custom`
- 启动成功（PID 见当时 `server.pid`）；**当前配置已生效，一般无需再启**
- 之后若有人改回该文件且未重启，才需再 `stop`/`start custom`

## 回滚

1. 将 `settings.attribute.maxHealth.max` 改回 `2048.0`
2. `cd server-runtime && ./stop.sh && ./start.sh custom`  
备份目录：`server-runtime/config-backups/maxhealth-20260927/`（含 `ROLLBACK.txt`、改后副本）

## 后续

怪物岗可继续调 `EmberAbyssWatcherDeep` 等 MM Health；上限 20000 足够容纳当前与后续抬血，不应再出现 “Mob HP is greater than server's maxHealth setting”。
