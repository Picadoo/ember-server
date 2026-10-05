# 发布凭证 · CoreRpg 1.65.31（2026-10-05）

D193 Boss Moves Pack 5 破招：Q06 / Q07 首领半血后的 3 秒蓄力大圈，蓄力期间全队打掉它 6.5% 生命即打断 + 踉跄。设计：`docs/design/DESIGN-ember-boss-moves-pack5-2026-10-05.md`。

## 线上状态（部署后核对于 2026-10-05 09:05 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.31**（`Enabling CoreRpg v1.65.31` 09:04:46）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `3cbebba26215ea5dc74634630b70e90b352185874d9de9b4580daf5c081dbf39` |
| balance_version | **54**（rule_version g04-1/b54）|
| 回滚包 | `/workspace/backup/CoreRpg-1.65.30-pre-1.65.31.jar`（sha256 `17b430e7…` = 1.65.30）|
| 服务器 PID | **1435738**（was 1415948；`server-runtime/stop.sh` 优雅停 → `start.sh`）|
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 部署文件 | `plugins/CoreRpg.jar`、`plugins/CoreRpg/ember-v1-runs.yml`、`plugins/TrMenu/menus/ember_p1_codex.yml`（config.yml 未动）|
| 源码对账 | jar 由提交 `55c529f` 的干净 worktree（/workspace/d193-wt，git status 无改动）构建，部署后 /workspace/minecraft 快进到同一提交 |

## 内容

- `ember-v1-runs.yml`：Q06 +霜潮汲取（circle r5，every 25，below 0.5，warn 3.0，dmg 30，break_hp 0.065，break_stun 0.3）；Q07 +炉心聚爆（r5.5，dmg 36，break_stun 0.5）；均排在招表最后；balance_version 53 → 54
- `EmberRunMaps.Skill.breakHp / breakStun`：非 charge、非 share；≤0.5 / ≤2 秒；复制构造保留（挑战 / 深渊沿用，伤害走 heavy 覆盖）
- `EmberRunDirector`：蓄力开始记需求 = 最大生命 × break_hp；`noteBossHurt` 累计玩家伤害；每 0.25 秒动作栏「破招 x%」；达标 → `breakCast`（不落地，解除蓄力定身，stunUntil 踉跄，follow 顺延），日志 `[P1 run] <run> break <招> <done>/<need> stun <s>s`；**余烬连战（def.rush）这两招永不到点**
- `EmberRunService.onPlayerHit`：来源（含投射物射手）是玩家时把 finalDamage 交给 `noteBossHurt`
- 图鉴：Q06 / Q07 各加招式两行 +「破招」一行；连战页注明不出这两招
- p1sim：`BREAK` / `BREAK_UPTIME` / `BREAK_STATS`；门禁 `tools/p1sim/breakmove.py` → `tools/p1sim/out-breakmove-d193.md`；`rushsim.py` 剔除 break 招
- 奖励 / 掉落 / 闸门 / 词缀 / 事件 / 装备结构 / AFK / 签到 / 化妆品 / 团本 / Q01–Q05：**不动**

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package`（JDK 8）| **297 / 0**（+3：`breakParsesOffChargeAndShareAndIsCapped_D193`、`brokenOnlyWhenArmedAndNeedReached_D193`、`lateBossesHaveOneHalfHpBreakChannel_D193`）|
| p1sim 门禁 | Q06 / Q07 × 普通 / 挑战 × 躲避 0.3/0.5/0.7：Q06 0.5 / Q07 1.6 → MAX_ABS_DPP **1.6**（cap 3.0）；Q06 踉跄 0.4 秒起 +3.5 否决；连战 rushsim 与现网基线逐格一致 → **within range** |
| 冒烟 | 合批 deferred（新一批第 3 个：1.65.29 D191 + 1.65.30 D192 + 1.65.31 D193；下批须含 D188 撞墙定点 + D192 落空踉跄定点 + D193 破招定点）|
| persist-roundtrip | 不需要（无资产路径变化）|
