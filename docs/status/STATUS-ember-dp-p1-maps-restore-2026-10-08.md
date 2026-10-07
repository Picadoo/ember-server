# 状态 · P1 副本地图模板恢复（2026-10-08 · 运维 · 零数值变动）

**日期：** 2026-10-08（上海时间）  
**版本：** CoreRpg **1.65.97** · bv **60** 未动；零 Java / 玩法 yml / NI / TrMenu 改动

## 人话

开服日志里一直有两行 Multiverse 报错（`ember_daily_ash_v1` / `ember_daily_crypt_v1` could NOT be loaded）。顺着查下去发现更大的问题：**P1 全部副本用的地图模板是错的**。`plugins/DungeonPlus/map/` 下 7 个 `*_v1` 模板（Q01–Q07 主线，团本 R01–R03、连战 B1/B2 也共用）不是真文件夹，而是指向旧 S2 壳图（`ember_daily`、`ember_daily_ash`…）的软链。玩家进 Q01–Q07 / 团本，进的是旧图：怪刷在墙里（CoreRpg 取消了窒息伤害才没被算成击杀）、部分房间根本没有区块。已从 Release `maps-v2-2026-10-03` 原样恢复 7 个模板，重启游玩服后 12 个副本缓存全部换成 v2 图，`check-dp-spawns.py` 13 个 P1 副本里 12 个由 FAIL 变 ok（剩下的 `EmberQ0F1` 是国庆活动本，10-08 00:00 已结束，模板本来就不在）。

## 证据

| 项 | 结果 |
|---|---|
| 修前 `check-dp-spawns.py` | EmberQ01–Q07、Q0R1–R3、Q0B1、Q0B2、Q0F1 **13 个 FAIL**（头在实心方块里 / 无区块）；旧日图 EmberDaily* 等 13 个 ok |
| 同一检查换成 Release 模板（`DP_MAP_ROOT`） | 只剩 EmberQ0F1 FAIL |
| 现网模板 vs Release `SHA256SUMS.txt` | 修前 35/35 文件不同；修后全部一致 |
| 修前日志 | 10-08 01:09 / 01:12 测试号进 Q01，`MM mob 'EmberQ01Melee' was suffocating … at 0,64,34` 等 23 条 → 软链在重启（04:11）之前就存在 |
| 来源推断 | `ember-binaries-20261001` 包里**没有** `*_v1` 模板（只有旧日图）；某次换机 / 恢复只解了这个包，再用软链把 `_v1` 指回旧图凑数。哪次换机做的没法确认（日志在 09-30 → 10-07 15:36 之间有断档） |
| Multiverse 两行报错 | `server-runtime/ember_daily_ash_v1`、`ember_daily_crypt_v1` 是旧的编辑副本世界，在 `worlds.yml` 里登记成 `environment: null`（且 uid.dat 与 `ember_daily_ash` / `ember_daily_crypt` 重复）→ 建世界时空指针。玩法不经过这两个世界（副本走 DP 模板 → dungeon-caches → `dungeon_EmberQ…` 实例） |

## 做了什么（05:45–05:46，0 人在线）

1. `mv remove ember_daily_ash_v1` / `mv remove ember_daily_crypt_v1`：只是从 Multiverse 登记里去掉，世界文件夹保留（要恢复：`mv import <名> normal`）。
2. 停游玩服 → 删除 7 个软链 → 从 `ember-p1-maps-v2-2026-10-03.zip`（zip sha256 与 Release 说明一致，SHA256SUMS 自检通过）拷入 7 个真模板 → 启游玩服。
3. 验证：`Done (6.527s)`、CoreRpg `MySQL connected`、0 条 ERROR、Multiverse 13 个世界加载无异常；DP 7 个模板「地牢地图导入成功」；12 个 `dungeon-caches/dungeon_EmberQ…` 的 region 与 v2 模板逐字节一致；`check-dp-spawns.py` 仅 EmberQ0F1 FAIL。

未做真人 / bot 实跑；Release 说明里 v2 发布时已有 Q01 / Q04 / Q06 bot 生存通关记录。

## 防再犯（本号入库）

- 新 `scripts/dp-maps-v2-verify.sh`：按 Release SHA256SUMS 校验 7 个模板；`--restore-broken` 只补缺失 / 软链，`--restore` 全部换回 v2；游玩服在跑时拒绝恢复；被替换的放进 `/workspace/maps-v2/replaced-<时间>/`。
- `scripts/ember-recover-after-rebuild.sh` 加第 7 步：游玩服未启动时自动 `--restore-broken`，在跑时只报告。

## 不动

数值 / 掉落 / 体力 / 刷怪点 / `ember-v1-runs.yml` / DP 副本配置 / TrMenu；旧日图 `ember_daily*`（非 v1）和 `server-runtime/ember_daily_*` 编辑世界文件夹都原样保留；国庆活动图 `ember_fest_gq26_v1` 未恢复（活动已结束）。

## 补记 · 06:55 模板被写回软链 → 二次恢复 + 取消跟踪

- **发现：** 余烬-测试复跑（[`STATUS-ember-dp-p1-maps-rerun-test-2026-10-08.md`](STATUS-ember-dp-p1-maps-rerun-test-2026-10-08.md)，`75d86c4f`）时 `dp-maps-v2-verify.sh` 又报 7 个 `*_v1` 全 BROKEN（软链，时间 05:48）。
- **原因：** 这 7 个软链虽在 `.gitignore` 的 `plugins/DungeonPlus/map/` 之下，却在 `c6196b43`（D302，10-08 00:11）被一起提交进了 git。05:47–05:48 推 `704dcebd` 时我用 `git stash` / `git stash pop` 绕开脏工作区做 rebase，stash 把「软链被删」当改动收走、又把 git 里的软链写回，盖掉了 v2 文件夹。当时在跑的游玩服用的是启动时复制的 v2 缓存，所以测试本身跑在 v2 上；但下次重启就会回到旧图。
- **处理（06:55，0 人在线）：** 停游玩服 → `git rm --cached` 7 个软链（git 里不再跟踪，`plugins/DungeonPlus/map/` 下已无任何被跟踪文件）→ `.gitignore` 加注释和 `plugins/DungeonPlus/map/*_v1` → `dp-maps-v2-verify.sh --restore-broken`（7 个恢复，复检 exit 0，`SHA256SUMS` 全部一致，7 个都是真文件夹）→ 启游玩服：`Done (6.531s)`、CoreRpg `MySQL connected`、0 条 ERROR；12 个 `dungeon_EmberQ…` 缓存的全部 region 与 v2 逐字节一致；`check-dp-spawns.py` 只剩已结束的 EmberQ0F1。
- **脚本复核：** 模板目录缺失时 verify 报 `BROKEN … missing`（exit 1），`--restore-broken` 补齐后 exit 0；`ember-recover-after-rebuild.sh --check` 正常。两个脚本都不依赖 git 跟踪状态。
- **规矩：** `/workspace/minecraft` 里运行时文件是故意不提交的脏文件——**不要再用 `git stash`、`git reset --hard`、`git checkout -- .`**；提交只 `git add` 指定文件；`git pull --rebase --autostash` 一样会走 stash，也不要用。远端已前进时：`git fetch` 后在单独的 `git worktree`（干净目录）里 rebase / cherry-pick 再推，或 `git merge --ff-only` 能快进时直接快进。
