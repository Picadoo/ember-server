# 发布凭证 · CoreRpg 1.65.27（2026-10-05）

D188 Boss Moves Pack 3 撞墙破绽：Q02 守炉蛮兵「焦冲」（半血后）/ Q07 炉锁巨卫「冲撞」被实心墙截短时，首领眩晕 1.5 秒（不放招、普攻取消、定身）。设计：`docs/design/DESIGN-ember-boss-moves-pack3-2026-10-05.md`。

## 线上状态（部署后核对于 2026-10-05 06:02 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.27** |
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `0b3792b5a5f5642d2c8f1c53374e7e69cef3409f8d6be4690c99aaa300aa28a6` |
| balance_version | **50** |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.26-pre-1.65.27.jar` |
| 服务器 PID | **1326994**（was 1306094）|
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |

## 内容

- yml 新键 `wall_stun`（只对 `type: charge`，≤ 3 秒）：Q02 焦冲 1.5、Q07 冲撞 1.5；Q07 冲撞普通伤害 36 → 40（挑战 / 深渊走 heavy 覆盖不变；连战 ×1.45 → 58）
- `EmberRunDirector`：蓄力时 `crashGrid` 判定终点后一步是否实心墙；冲完 `wallStun()` → `recoverUntil` / follow 推后、SLOW 10、暴击星 + 铁砧声、全队一行、日志 `[P1 run] … wall stun <招名> 1.5s`；蓄力提示加「让它撞上墙会晕 1.5 秒」
- `EmberCombatListener` + `EmberRunService.bossStunned`：眩晕中首领普攻取消（招式伤害不拦）
- 图录 `ember_p1_codex.yml`：Q02 / Q07 两格加「撞上墙会晕 1.5 秒」与「破绽」打法行；Q07 冲撞伤害 40
- 团本 r01 冲撞不动（单测断言）；奖励 / 掉落 / 装备结构 / AFK / 签到 / 化妆品：**不动**

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn test package`（JDK 8）| **284 / 0**（+3：crashGrid 墙 / 跑满 / 悬崖 / 空地边界；wall_stun 只认 charge 且 ≤3；bundled Q02 / Q07 = 1.5、r01 = 0）|
| p1sim 门禁 | `tools/p1sim/wallstun.py q02 q07`：**MAX_ABS_DPP 1.1**（撞墙比例 = 躲避 × 0.5）；上界（每次都撞墙）7.6 仅参考；余烬连战 rushsim 全格 ±1 点。见 `tools/p1sim/out-wallstun-d188.md` |
| 开服 | Enabling CoreRpg v1.65.27；双 MySQL；SEVERE 0 |
| 冒烟 | **DEFERRED batch**（POLICY 01:53；新批次第 2 包：1.65.26 D187 + 1.65.27 D188）。批量冒烟要看：Q07 / Q02 半血后冲撞撞墙时日志出现 `wall stun`，撞空地不出现 |
| 资产回归 | 无物品资产路径变化 → 未跑 persist-roundtrip |
