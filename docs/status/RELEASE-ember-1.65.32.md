# 发布凭证 · CoreRpg 1.65.32（2026-10-05）

D194 半血招冷却重锚：带半血门槛的首领顶层招第一次放完后，下次 = 现在 + every，不再沿出生起的旧网格（09:44 合批冒烟发现 Q07 第 2 次炉心聚爆约 7 秒后就来）。设计：`docs/design/DESIGN-ember-gated-move-cadence-2026-10-05.md`。

## 线上状态（部署后核对于 2026-10-05 10:23 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.32**（`Enabling CoreRpg v1.65.32` 10:21:58）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `fcd1dbecc2280420aa26ecc598a89b3e42b612d7a60c54c35f1b8df3940c520d` |
| balance_version | **55**（rule_version g04-1/b55）|
| 回滚包 | `/workspace/backup/CoreRpg-1.65.31-pre-1.65.32.jar`（sha256 `3cbebba2…` = 1.65.31）|
| 服务器 PID | **1480377**（was 1435738；`server-runtime/stop.sh` 优雅停 → `start.sh`）|
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0（停服日志也无 SEVERE）|
| 部署文件 | `plugins/CoreRpg.jar`、`plugins/CoreRpg/ember-v1-runs.yml`（只改 balance_version 与注释；config.yml 未动）|
| 源码对账 | jar 由提交 `e156266` 的干净 worktree（/workspace/d194-wt）构建，部署后 /workspace/minecraft 快进到同一提交 |

## 内容

- `EmberRunDirector`：`gateOpened[]` + `gatedNext`；带 below ≤ 1.0 的顶层招首次施放后 nextAt = now + every，之后照旧走网格；无门槛招 / follow / 余烬连战不变
- 受影响：Q02 门廊突刺、Q03 焦冲、Q06 霜潮汲取、Q07 炉心聚爆、R02 砸地
- 奖励 / 掉落 / 次数 / 资产路径 / 团本人数规则：**不动**

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package`（JDK 8）| **298 / 0**（+1 `gatedSkillReanchorsOnFirstCast`）|
| p1sim | 模拟器 break 招本就按新节奏（D193 `breakmove.py` 门禁 MAX_ABS_DPP 1.6 不变）；其它半血招线上次数 ≤ 旧代码、与模拟频率一致 → **within range** |
| 冒烟 | 合批 deferred（新一批第 1 个；下批核对 Q06/Q07 两次蓄力之间 ≥ 25 秒）|
| persist-roundtrip | 不需要（无资产路径变化）|
