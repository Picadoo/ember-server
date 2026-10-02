# 余烬服 · 交接（2026-10-02 23:35 CST 更新）

这份文档写给接手的人，读完就能接着做。设计正文在 `docs/design-ember-v1.0-P1.md`（下文称「书」）。P2 草案在 `docs/design-ember-v1.1-P2-draft.md`。所有裁决记在源表 `docs/DESIGN-ember-v1.0-P1-source-table.md`（§13.x，D01–D87）。

## 1 当前状态

| 项 | 值 |
|---|---|
| 仓库 | `Picadoo/ember-server`，main 分支。工作树 `/workspace/ember-p1`（分支 p1-g01，推到 main）。服务器在 `/workspace/minecraft` 跑，这是 main 的检出 |
| CoreRpg | **1.39.1**（`CoreRpg/pom.xml` 第 8 行和 `plugin.yml` `version:` 一起改） |
| balance_version | 10（`ember-v1-runs.yml`，规则 g04-1/b10） |
| 模式 | P1 是默认模式（D61–D65），旧玩法藏在 `ember_hub_legacy`。**不要关 P1** |
| 菜单 | TrMenu 共 47 个，`trmenu reload` 就能重载 |
| 套件 | `mineflayer-tests` gameplay 9/9（1.39.1；钓鱼偶尔随机失败，重跑即过） |
| 玩家 | 没有老玩家，**不做数据迁移**。测试号 P1Fox、RaidA–E、NewbieQ、FreshA1–C1（全新号） |
| 内容 | 七张主线图 Q01–Q07 + 挑战版。P2-1 每周挑战轮换。P2-2 深渊·余烬层（10 层）。P2-5/6 团本 R01 锈轨矿道·团 + R02 霜封哨所·团（3～5 人，两本合计每周 3 次）。P2-7 每日委托。P2-8 精选图周规则（限药 / 术者换防 / 逆行）。P2-9 每图掉落偏向、团本额外装备按目标族定向（保底精良）、称号和团本足迹（只做展示）。P2-10 排行榜（`/corerpg p1 top`）。新手提示：回复药放快捷栏、生命低提醒、「下一步」（1.34.1–1.36.1）。新手第一周（1.39.x，D85–D87）：护符自动生效、T1 自动顶替起步件、Q01 普通版减压、首通自选 / 目标族改成可点按钮 |

## 2 启停

- **全栈**：`scripts/ember-up.sh` 和 `scripts/ember-down.sh`（登录服、游戏服、代理）。
- **只重启游戏服**：`server-runtime/stop.sh; sleep 2; server-runtime/start.sh`，等大约 40 秒。日志 `server-runtime/logs/stdout.log` 里要看到「Enabling CoreRpg vX」「47 个菜单已加载」「Done」。
- **控制台**：`scripts/console.sh play "<cmd>" [等待秒数]`。
- **构建**：
  ```
  cd CoreRpg && JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01 /workspace/minecraft/tools/apache-maven-3.9.16/bin/mvn -q -o package
  ```
  构建会跑单元测试。**先看输出里有没有 `ERROR]`**（测试失败时 target 里还是旧 jar，照样能复制过去），没有再 `cp target/CoreRpg.jar /workspace/minecraft/plugins/CoreRpg.jar` 并重启。
- **每次部署**：
  1. 升版本号，备份 jar 到 `/workspace/backup/CoreRpg-<ver>.jar`。
  2. 冒烟，再跑套件 `cd mineflayer-tests && timeout 600 npm run gameplay`，结果看 `PASS=`。
  3. 跑 `scripts/db-dump.sh`，把最新的 `…-manual.sql` 改名为 `…-after-<ver>.sql`。
  4. 套件会改写 live 里的 `STATUS-gameplay-suite.md`：复制到工作树提交，然后在 live 里 `git checkout --` 还原。

## 3 架构地图（P1/P2）

| 层 | 位置 | 说明 |
|---|---|---|
| 模式和战斗 | `CoreRpg/.../p1/EmberMode`、`EmberCombatListener`、`EmberFormula`、`EmberHeal`、`EmberDamageTrace` | P1 世界判定、统一伤害、回复只能走 HealLedger、伤害追踪 |
| 装备 | `EmberItems`、`EmberItemData`、`EmberItemStore`（MySQL）、`EmberLoadout(Service)`、`EmberForgeService`、`EmberUpgradeRules`、`EmberCodex`、`EmberCompare` | T0–T3、成色和精工、强化和升阶、图鉴 |
| 套装 | `EmberSetRules`、`EmberSetEngine`、`EmberSetService`、`EmberBurnBook` | 烬爆 / 焚烬 / 炽愈；动作栏 HUD（生命低时显示喝药提示） |
| 副本 | `EmberRunService`（入口、结算、账本）、`EmberRunSession` / `EmberRunStore`、`EmberRunDirector`（房间和首领；顶层招式可按 `below` 分阶段）、`EmberRunMaps`（读图、挑战、深渊、团本）、`EmberRunRules`（纯规则）、`EmberRunBridges`（反射调 DungeonPlus） | 体力预留和退还、断线重连、重启后中止并退还 |
| 补给 | `EmberSupplyService` | 回复药商店、起步药（放进快捷栏右侧）、喝药提示 |
| 地图建造 | `p1/map/P1MapBuilder`、`P1MapLayout`；`resources/p1-book-maps.yml` | 按书里的章节生成白盒地图 |
| 命令 | `EmberCommand`（`/corerpg p1 …`） | 玩家：enter / run / abyss / shop / charm / title / trail。管理：runs firstclear / weaken [比例] / modifier <id>（下一局挑战强制周规则）/ heal |
| 参数源 | `plugins/CoreRpg/ember-v1.yml`、`plugins/CoreRpg/ember-v1-runs.yml`。`CoreRpg/src/main/resources/` 下各有一份，**两份要一起改** | 图（含 `loot:`）、`loot_bias:`、`raid_item:`、挑战、`rotation:`（含 `modifiers:`）、`abyss:`、`raids:`（`cap_group`）；`ember-v1.yml` 里有 `bounty:` |
| 怪物 | `plugins/MythicMobs/Mobs/EmberP1Main.yml` | 生成时的生命以 MM 为准 |
| 副本实例 | `plugins/DungeonPlus/dungeon/EmberQ01..Q07`、`EmberQ0R1` / `EmberQ0R2`（团本）；`config.yml` 里的 precache | DP 队伍：`/dp team` GUI，接受用 `/dungeon-team request accept <名>` |
| 菜单 | `plugins/TrMenu/menus/ember_hub.yml`（主菜单）、`ember_p1_adventure` / `_challenge` / `_abyss` / `_codex` / `_codex_gear` / `_forge` / `_gear` | 进本一律走 `/corerpg p1 enter <key>`；`/corerpg enter raid` 是旧团本 |
| PAPI | `CoreRpgExpansion`：`p1_*`（`p1_pass_<key>`、`p1_raid_r01/r02`、`p1_abyss_*`、`p1_bounty`、`p1_next`、`p1_modifier`、`p1_loot_<图>`、`p1_title`、`p1_honors` …） | 菜单和 DP 进入条件都用它 |

## 4 工具

| 工具 | 用途 |
|---|---|
| `tools/p1sim/p1sim.py`、`selfcheck.py` | 单人从 Q01 推到 Q07 的节奏模型，读真实配置。selfcheck 应该 0 failed |
| `tools/p1sim/p2econ.py` | 60 人 8 周经济模型，开关 `--abyss` / `--trade` / `--raid` / `--mods` / `--no-loot` / `--old-raid-item` / `--no-swap` / `--no-bounty`。**凡是发奖励的改动都要先跑这个** |
| `tools/p1sim/p1party.py` | 3～5 人团本模型，`--raid r01|r02`。参数 `--pool --weeks --dodge --trials --boss-hp --atk --k --j` |
| `tools/p1map/book.py`、`gen.py` | 解析书里的地图章节，生成地图 |
| `tools/p1map/chal-smoke.sh`、`abyss-smoke.sh`、`raid-smoke.sh LEADER "成员…" [q07|q06]`、`boss-test.sh LEADER "成员…" QMAP [比例]`（首领不 weaken 或只削到比例，看阶段和击杀用时） | 机器人冒烟：按书里的路线走，每个房间先 admin weaken，再由机器人击杀，结算是真的 |
| `tools/p1map/norm-smoke.sh NAME qNN`（普通版，每个房间削弱）、`newbie-run.sh NAME qNN`（不削弱、生存模式、喝药，死了就停）；`fight.sh` 加环境变量 `DRINK=1`，生命低于 11 时喝快捷栏的药 | 新手视角冒烟 |
| `scripts/check-dp-spawns.py`、`dp_fix_spawns.py`、`dp_map_reach.py` | 检查 DP 刷怪点（卡墙、走不到） |
| `scripts/db-dump.sh` | MySQL 备份到 `/workspace/backup/` |
| `mineflayer-tests/tmp-p1/b.sh` | 机器人：join / quit / eval / chat。另有 `walk.sh`、`fight.sh` |

`tools/*` 被 gitignore 了，新文件要 `git add -f`。

## 5 备份和发布

- jar 备份在 `/workspace/backup/CoreRpg-<ver>.jar`，最新是 1.39.1。
- 数据库备份是 `/workspace/backup/db-ember-authme-*-after-<ver>.sql`，最新是 after-1.39.1。
- 发布就是推到 main，然后在 live 里 `git pull --rebase --autostash`。没有单独的 release 产物。

## 6 规则（必须遵守）

1. **数值纪律（书 §23.3）**：改数先改参数源（两份），再重跑模型，登记到源表，然后升 balance_version 和插件版本。不能自行增加奖励。
2. **绝不提交**：CoreRpg `config.yml`、AuthMe 配置（含数据库密码，不能打印）、`ladder.yml`、`calamity-state.yml`、`players/**`、`worlds.yml`、jar、世界、日志、`tmp-p1/`、Adyeshach npc json、`ember-v1-item.key`、`login-runtime/*`、HolographicDisplays `database.yml`、MythicMobs SavedData、`plugins/CoreRpg/p1-runs/`（含 leaderboard.yml）。
3. `server-runtime/ops.json` 保持 `[]`。
4. **只按确切 PID 杀进程**，绝不用 `pkill -f` 或 `pgrep -f`。
5. 游戏内的击杀命令只在副本里用，而且要限定半径。机器人死了就停掉 fight.sh 或让它下线。
6. 提交署名 `Picadoo <Picadoo@users.noreply.github.com>`。推送用 `git pull --rebase --autostash -q && git push -q origin HEAD:main`。提交小、推送勤。

## 7 未完成

- **spigot 生命上限（1.36.3 已修）**：`spigot.yml` 的 `maxHealth.max` 原来是 20000，团本首领（3 人 35100）和高层深渊首领都被悄悄截断，所以之前团本通关比模型简单。现在是 1000000，scaleHealth 截断时会打警告。
- **团本 TTK**：书 §18.4 第 4 条要求模型和实测差不超过 20%。已经实测：
  - R02 第二阶段实机出现（首领削到 60%，日志 `boss phase 2 at 50% (砸地)`）。
  - 单个机器人对首领的 DPS 185，模型 190（−3%）。
  - 完整 3 人击杀用时还没测成：机器人不躲技能，1 秒一次治疗也扛不住，R01 削到 40% 时团灭了。要真人或会躲的机器人。
- **后期币堆积**：深渊层费是目前唯一的大额出口（D72）。要发币的新内容必须先过 p2econ。
- **交易**：D73 否决开市，只有规则。
- 旧玩法试玩遗留：`docs/PLAYTEST-2026-10-01-newplayer.md` 的「未解决问题」，P1 默认模式下大多已经不在主路上。
- 日志噪音很小。MythicMobs `ExampleItems.yml` 里的 `GOLDEN_HELMET` 在 1.12 不存在，没修。
- 剩下的候选：
  - 精选周规则可以再加几条，比如计时或黑暗（没做的原因见 P2 草案 §5e）。
  - 主城氛围：书 §1 只要求「一句背景」。
- 团本 DP 队伍要用 GUI 组建（`/dp team`）：队长点第 40 格建队，队员点第 10 格申请，队长 `/dungeon-team request accept <名>`。
