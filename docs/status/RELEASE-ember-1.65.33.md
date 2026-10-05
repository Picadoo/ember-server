# 发布凭证 · CoreRpg 1.65.33（2026-10-05）

D195 团本破绽 Raid Counterplay Pack 1：三个团本首领各继承来源首领已有的那一个破绽（R01 冲撞撞墙眩晕 1.5 秒 / R02 半血砸地、R03 斧刃横扫全员躲开踉跄 0.5 秒），这三条招伤害 79 → 94 / 88 / 84 抵消。设计：`docs/design/DESIGN-ember-raid-counterplay-2026-10-05.md`。

## 线上状态（部署后核对于 2026-10-05 10:56 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.33**（`Enabling CoreRpg v1.65.33` 10:54:57）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `4f37b4ebacecd297fba36d15c6a39c01546c2e35ab59acde1e5374cd0d6d9cc2` |
| balance_version | **56**（rule_version g04-1/b56）|
| 回滚包 | `/workspace/backup/CoreRpg-1.65.32-pre-1.65.33.jar`（sha256 `fcd1dbec…` = 1.65.32）|
| 服务器 PID | **1500928**（was 1480377；`server-runtime/stop.sh` 优雅停 → `start.sh`）|
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 部署文件 | `plugins/CoreRpg.jar`、`plugins/CoreRpg/ember-v1-runs.yml`（三条团本招）、`plugins/TrMenu/menus/ember_p1_codex.yml`（三个团本格）；config.yml 未动 |
| 源码对账 | jar 由提交 `81de952` 的干净 worktree（/workspace/d195-wt）构建，jar 内 ember-v1-runs.yml 与提交逐字节一致；部署后 /workspace/minecraft 快进到同一提交 |

## 内容

- R01 炉锁巨卫·团 冲撞：`wall_stun 1.5`，dmg 79 → 94
- R02 霜封统领·团 半血砸地：`whiff_stun 0.5`，dmg 79 → 88（2 秒后的横扫不变）
- R03 断塔斧卫·团 斧刃横扫：`whiff_stun 0.5`，dmg 79 → 84（烬核不变）
- Java 逻辑零改动；奖励 / 掉落 / 周上限 / 复活 / 人数倍率 / 资产路径：**不动**

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package`（JDK 8）| **299 / 0**（+1 `raidBossesInheritCounterplay_D195`；D188 / D192 团本断言改为其余团本）|
| p1sim | `tools/p1sim/raidwin.py`（p1party `RAID_STUN`）1,500 局 × 躲避 0.3 / 0.5 / 0.7 × 3 / 4 / 5 人：MAX_ABS_DPP **2.5**（≤ 3.0）→ **within range**；无伤害抵消时 6.2 被否；报告 `tools/p1sim/out-raidwin-d195.md` |
| 冒烟 | 合批 deferred（新一批第 2 个：1.65.32 D194 + 1.65.33 D195）。下批冒烟：3 个 FreshQ 进 R01 背墙引冲撞 → latest.log `wall stun`；R03 全员躲横扫 → `whiff stun 斧刃横扫`；R02 半血砸地全员出圈 → `whiff stun 砸地` |
| persist-roundtrip | 不需要（无资产路径变化）|
