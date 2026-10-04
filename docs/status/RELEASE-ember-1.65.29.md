# 发布凭证 · CoreRpg 1.65.29（2026-10-05）

D191 Room Events Pack 4：房间事件池 6 → 9，新增「裂隙」`breach`、「连斩」`chain`、「无伤」`unscathed`。设计：`docs/design/DESIGN-ember-room-events-pack4-2026-10-05.md`。

## 线上状态（部署后核对于 2026-10-05 07:58 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.29**（`Enabling CoreRpg v1.65.29` 07:57:34）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `d81959150f1207fd397bb4f830d336fac1722817774e9bd8ac52c72ac8eede6e` |
| balance_version | **52** |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.28-pre-1.65.29.jar`（sha256 `9cfdf869…02732eb` = 1.65.28）|
| 服务器 PID | **1392729**（was 1339093；`server-runtime/stop.sh` 优雅停 → `start.sh`）|
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 部署文件 | `plugins/CoreRpg.jar`、`plugins/CoreRpg/ember-v1-runs.yml`、`plugins/CoreRpg/ember-v1.yml`（仅注释）、`plugins/TrMenu/menus/ember_p1_codex.yml`、`ember_p1_adventure.yml`（config.yml 未动）|
| 源码对账 | 从提交 tree `git archive` 重建 jar，与线上 jar 全部条目逐字节一致 |

## 内容

- `ember-v1-runs.yml`：`variety.events` + `breach, chain, unscathed`；`breach: {radius 5.0, min 1.5, max 7.0, shrink 0.15, grow 0.8}`、`chain: {need 4, gap 4.0}`、`unscathed: {hits 4, per_member 2}`；balance_version 51 → 52
- `EmberRunMaps.Variety`：EVENTS 9 项、参数解析与钳位（shrink ≥ 0.01：裂隙一定会合上）、中文名 裂隙 / 连斩 / 无伤、三者无倒计时
- `EmberRunDirector`：裂隙（锚点海晶灯 + 名牌半径 + 紫圈，每秒缩，圈内击杀撑开，合上即失败）；连斩（本房击杀间隔 ≤ gap 计数，需 min(need, 本房刷怪数)）；无伤（开房按人数定预算，落地命中计数，超出即失败）；清房判定 + 清理；进房提示各一句
- `EmberRunService.onRunHitTaken`：MONITOR + ignoreCancelled + finalDamage > 0，damager 为本局怪或其投射物
- 结算源键 `var_event_breach / chain / unscathed`（同 MAT_CORE × `event_core` 1），账单前缀 裂隙 / 连斩 / 无伤；花样委托「房间事件达标」照计
- 图鉴 / 冒险页事件列表 6 → 9，图鉴一行三事件说明
- p1sim：`Fight.nhit` / 怪物死亡时间 + `event_ok` 按类建模；门禁 `tools/p1sim/eventpack4.py` → `tools/p1sim/out-eventpack4-d191.md`
- 奖励数量 / 频率 / 掉落 / 闸门 / 词缀 / 装备结构 / AFK / 签到 / 化妆品：**不动**

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package`（JDK 8）| **291 / 0**（+6：`varietyPack4RollsBreachChainUnscathed_D191`、`pack4PaysExistingCoreOnceOnly_D191`、`breachShrinksGrowsAndCollapses_D191`、`chainNeedsKillsWithinGap_D191`、`unscathedBudgetScalesPerMember_D191`、`pack4ConfigClampsAndUnknownKindsDropped_D191`）|
| p1sim 门禁 | Part A：参考带 97% / 裂隙 97% / 连斩 97% / 无伤 83%，新事件不高于参考；每次事件期望核心 0.969 → 0.952。Part B：p2econ 120 人 × 12 周新旧池各列一致（币中位 W12 −10 以内）→ **within range** |
| 冒烟 | 合批 deferred（新一批第 1 个；下批须含 D188 撞墙定点）|
| persist-roundtrip | 不需要（无资产路径变化）|
