# 运维 · 机器重建后恢复 Ember（MariaDB 数据目录在 /workspace）

**日期：** 2026-10-08（上海时间）  
**起因：** 2026-10-08 04:11 box 被平台用新系统盘重建：`/workspace` 原样保留，系统盘（含 `mariadb-server` 软件和 `/var/lib/mysql`）全部清空，服务全停。当时从 03:18 的每小时备份导回（03:18 后无玩家活动，无数据丢失）。

## 现在的布局

| 项 | 位置 |
|---|---|
| MariaDB 数据目录 | **`/workspace/mysql-data`**（属主 mysql，在仓库外，随 /workspace 保留） |
| 指向它的配置 | `/etc/mysql/mariadb.conf.d/99-ember-datadir.cnf`（`[mysqld] datadir = /workspace/mysql-data`；在系统盘上，重建后要重写——脚本会做） |
| 旧数据目录 | `/var/lib/mysql`（迁移前的副本，已不用；重建后会消失，无所谓） |
| 每小时备份 | `/workspace/backup/db-auto/hourly/`（`scripts/ember-backup-loop.sh`，随游玩服 start.sh 启动） |

## 重建后怎么做（一条命令）

```bash
/workspace/minecraft/scripts/ember-recover-after-rebuild.sh          # 全流程
/workspace/minecraft/scripts/ember-recover-after-rebuild.sh --check  # 只看要做什么，不改任何东西
/workspace/minecraft/scripts/ember-recover-after-rebuild.sh --db-only # 只恢复数据库，不起服
```

脚本每一步先检查、已就绪就跳过（可重复跑）：

1. 没装 `mariadb-server` → `apt-get install`；
2. 写 `99-ember-datadir.cnf`；
3. `/workspace/mysql-data` 修属主为 mysql（重建后 mysql 的 uid 可能变）；目录空 → `mariadb-install-db`；
4. MariaDB 没响应 → 后台 `mysqld_safe --user=mysql`，确认跑在 `/workspace/mysql-data`；
5. 应用账号 `ember@127.0.0.1/localhost` 不存在 → 用 `plugins/CoreRpg/config.yml` 里的密码新建（已存在的账号**不改**；密码不打印、不进命令行）；
6. `ember` / `authme` 库缺表 → `scripts/db-restore.sh latest --live --yes`（最新每小时备份，先试导核对再导正式库）；
7. 登录 / 游玩 / 代理没全在跑 → `setsid nohup scripts/ember-up.sh`。

跑完核对：端口 3306 / 25565 / 25566 / 25567 在监听；`server-runtime/logs/latest.log` 有 `MySQL connected` 和 `Done (`；`login-runtime/logs/latest.log` 有 `[AuthMe] MySQL setup finished`。

**没有装开机自启**（机器没有 systemd / cron）；重建后需人工或值班 agent 跑一次上面的命令。需要免密 sudo。

## 已知遗留（本次未动）

- CoreRpg / AuthMe 配置里的数据库密码仍是仓库占位值，与 `secrets/mysql-ember.env` 不一致；库用户密码按配置设置（只允许本机登录）。统一密码需另行决定。
- `server-runtime/ops.json` 有 17 个测试号是 4 级管理员（重启前即如此），是否清理待定。
- `ember_daily_ash_v1` / `ember_daily_crypt_v1` 两张图 Multiverse 报加载失败，重启前就有。
