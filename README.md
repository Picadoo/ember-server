# 余烬服（Ember）· Minecraft 1.12.2 私服工程

余烬服是一套基于 **Paper 1.12.2（自定义核心 `paper-custom.jar`）** 的 RPG 私服：

| 服务 | 目录 | 端口 | 说明 |
|------|------|------|------|
| 代理（公网入口） | `proxy-runtime/` | **25565** | Waterfall（Java 21），新连接先到登录服，AuthMe 登录后送往游玩服；见 `docs/proxy-20260926.md` |
| 登录服 | `login-runtime/` | 127.0.0.1:**25566** | AuthMe 登录/注册（MySQL 库 `authme`），`bungeecord: true` |
| 游玩服 | `server-runtime/` | 127.0.0.1:**25567** | 主玩法：CoreRpg + Core* 系列插件、NeigeItems、MythicMobs、DungeonPlus 等 |
| 数据库 | MariaDB | 3306 | 库 `ember`（CoreRpg）+ 库 `authme`（AuthMe），用户 `ember` |

> 本仓库只含**源码 + 配置 + 文档**。世界存档、所有 jar、SQL 转储在 GitHub Release
> **`v2026.10.01`** 的资产 `ember-binaries-20261001.tar.xz` 里；数据库密码等机密**不在**仓库中。

## 仓库结构

```
Core*/                     自研插件源码（CoreAnvil CoreBrew CoreCombat CoreCraft CoreEnchant
                           CoreFish CoreRpg CoreSmelt CoreWorldRules），Maven/pom，无构建产物
Paper/                     自定义 Paper 1.12.2 核心：Spigot-*-Patches 补丁、Paper-API / Paper-Server 源码
                           （不含 .git 与 work/）；ember-custom-Paper-Server.diff = 相对上游补丁的本地改动
plugins/                   游玩服插件配置（YAML/JSON/脚本），游玩服 server-runtime/plugins 应为指向此处的符号链接
server-runtime/            游玩服配置 + start.sh / stop.sh / env.sh
login-runtime/             登录服配置 + plugins/（AuthMe、ProtocolLib、Vault 配置）
mineflayer-tests/          mineflayer 自动化冒烟测试（npm install 后运行）
ember-stack-display/       客户端堆叠显示 mod 源码（Forge 1.12.2 / Gradle）
dungeons/ scripts/         副本设计与搭建脚本文本
docs/                      设计/规格文档（分类见下方「文档地图」与 docs/README.md）
secrets/mysql-ember.env.example   数据库凭据模板（真实 secrets/mysql-ember.env 被 .gitignore 忽略）
```

## 文档地图

根目录只保留本 README 与少量工程文档（`HOOKS.md`、`ROADMAP-core-mod.md`、`PERF-CHANGELOG-paper-nms.md`）；其余文档都在 `docs/` 下：

| 目录 | 内容 |
|------|------|
| `docs/handoff/` | 交接文档。**当前交接：`docs/handoff/HANDOFF-ember-2026-10-02.md`**；早期总接手说明 `docs/handoff/HANDOFF.md`；设计交接模板 `TEMPLATE-ember-design-handoff.md` |
| `docs/design/` | 所有 `DESIGN-*.md` / `design-*.md` 设计稿。**设计书（P1）：`docs/design/design-ember-v1.0-P1.md`**；来源开关/决策表：`docs/design/DESIGN-ember-v1.0-P1-source-table.md`；**P2 草案：`docs/design/design-ember-v1.1-P2-draft.md`** |
| `docs/status/` | 所有 `STATUS*.md` 进度/验收记录（总览 `docs/status/STATUS.md`；gameplay 套件报告 `docs/status/STATUS-gameplay-suite.md`） |
| `docs/reviews/` | 试玩、评审、复盘：`PLAYTEST-*`、`review-*`（如 `review-recheck-2026-10-03.md`）、`RETRO-*`、`LESSONS-*`、`critic-*`、`audit-*` |
| `docs/tests/` | 单项测试报告 `TEST-*` 与冒烟记录 `smoke-*` |
| `docs/`（根） | 规格与参考：`ember-master-plan.md`、`ember-*-spec.md`、`如何创建-Grok-Bot专岗.md`（重建 bot 产线指南）等 |

新写的 STATUS / 设计稿请直接放进对应子目录（`docs/status/STATUS-<主题>.md`、`docs/design/design-<主题>.md`）。

## 在新机器上恢复

1. **克隆仓库**（私有仓库，需要有权限的 GitHub 账号）：
   ```bash
   gh repo clone Picadoo/ember-server /workspace/minecraft
   cd /workspace/minecraft
   ```
   > 脚本（env.sh、start.sh）里写死了 `/workspace/minecraft/...` 路径，建议就克隆到这个位置，否则需改 `env.sh`。
2. **下载 Release 资产并解压到仓库根目录**（世界 + jar + SQL）：
   ```bash
   gh release download v2026.10.01 --repo Picadoo/ember-server
   tar -xJf ember-binaries-20261001.tar.xz -C .
   ```
   会放好：`server-runtime/{world*,ember_hub,ember_afk,ember_event,ember_daily_*,ember_*_build}`、`login-runtime/world*`、
   `plugins/DungeonPlus/map/`、所有插件 jar、`server-runtime/paper-custom.jar`、`login-runtime/paper-custom.jar`、
   `paper/paper-1.12.2-1620.jar`（原版备用核心）、`sql/ember.sql`、`sql/authme.sql`。
3. **游玩服插件目录软链**：`ln -sfn ../plugins server-runtime/plugins`（start.sh 缺失时也会自动建）。
4. **填写机密**：
   ```bash
   cp secrets/mysql-ember.env.example secrets/mysql-ember.env && chmod 600 secrets/mysql-ember.env
   # 编辑填入真实密码
   ```
   然后把以下文件中的 `CHANGE_ME` 替换为同一数据库密码（提交前这些位置已被脱敏）：
   - `login-runtime/plugins/AuthMe/config.yml`（`mySQLPassword`）
   - `plugins/CoreRpg/config.yml`（`mysql.password`）
   - `plugins/CoreRpg/config-mysql.example.yml`
   - `CoreRpg/config-mysql.example.yml`
   - `CoreRpg/src/main/resources/config-mysql.example.yml`
5. **恢复数据库**（MariaDB 10.x/11.x）：
   ```bash
   source secrets/mysql-ember.env
   sudo mariadb -e "CREATE DATABASE IF NOT EXISTS ember CHARACTER SET utf8mb4;
     CREATE DATABASE IF NOT EXISTS authme CHARACTER SET utf8mb4;
     CREATE USER IF NOT EXISTS '$MYSQL_USER'@'localhost' IDENTIFIED BY '$MYSQL_PASSWORD';
     CREATE USER IF NOT EXISTS '$MYSQL_USER'@'127.0.0.1' IDENTIFIED BY '$MYSQL_PASSWORD';
     GRANT ALL ON ember.* TO '$MYSQL_USER'@'localhost'; GRANT ALL ON authme.* TO '$MYSQL_USER'@'localhost';
     GRANT ALL ON ember.* TO '$MYSQL_USER'@'127.0.0.1'; GRANT ALL ON authme.* TO '$MYSQL_USER'@'127.0.0.1';"
   sudo mariadb ember  < sql/ember.sql
   sudo mariadb authme < sql/authme.sql
   ```
6. **Java 8**：需要 JDK 8（原环境为 Temurin `jdk8u504-b01`，放在 `tools/`，未入库）。
   解压到 `tools/jdk8u504-b01`，或 `export JAVA_HOME=/path/to/jdk8` 后再启动（env.sh 会优先用已有的 `JAVA_HOME`）。
7. **启动顺序**：
   1. MariaDB
   2. 一键：`scripts/ember-up.sh`（登录服 127.0.0.1:25566 → 游玩服 127.0.0.1:25567 → 代理 0.0.0.0:25565）
      代理需要 Java 11+（原环境为 `tools/jdk-21.0.12.1+1`，可用 `PROXY_JAVA=/path/to/java` 覆盖）；Waterfall jar 放在 `proxy-runtime/waterfall.jar`。
   停止：`scripts/ember-down.sh`（先代理，再游玩服，再登录服）。后端只绑 127.0.0.1，内网穿透只能指向 25565。
   **控制台**：三个 `start.sh` 都把服务端 stdin 接到 `<runtime>/console.fifo`（由常驻 `sleep infinity` 持有写端，pid 在 `console-holder.pid`；FIFO 已 gitignore），所以可以随时下控制台命令：
   `scripts/console.sh play "list"`、`scripts/console.sh login "authme reload"`、`scripts/console.sh proxy "bungee"`（打印该命令产生的日志行）。
   `stop.sh` 先经 FIFO 发 `stop`（代理为 `end`）正常停服，超时才 SIGTERM/SIGKILL。代理不再因 stdin=/dev/null 空转 100% CPU。
8. （可选）重新构建：`Core*/` 用 Maven（`mvn package`），Paper 核心见 `Paper/` 与 `PERF-CHANGELOG-paper-nms.md`、`HOOKS.md`；
   mineflayer 测试 `cd mineflayer-tests && npm install`。

## 未入库 / 需注意

- `secrets/`、所有 `*.env`（仅保留 `.example`）。
- 世界、jar、插件 sqlite 数据、SQL 转储 → 见 Release 资产。
- `tools/`（JDK、Maven、mca-venv 等）、日志、缓存、`node_modules`、`Paper/work/` 与 Paper 的 `.git` 历史。
- `plugins/_downloads/`（插件备用版本 jar）未打包。
- `tunnel/` 原目录只有日志（bore / playit），没有脚本，未入库；内网穿透需在新机器重新配置。

## 相关文档

- `docs/如何创建-Grok-Bot专岗.md`：换账号后如何重建余烬服多 bot 产线（提示词模板）。
- `docs/status/STATUS.md`：当前进度总览；`docs/design/DESIGN-ember-data-protection.md`：玩家数据保护（每小时备份、恢复脚本、数据库断线守卫、背包快照 `/corerpg invsnap`）；`docs/design/DESIGN-ember-mysql-network.md`：登录服/游玩服/MySQL 拓扑。
