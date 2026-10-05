# 发布凭证 · CoreRpg 1.65.30（2026-10-05）

D192 Boss Moves Pack 4 落空破绽：Q01–Q05 首领主重招全员躲开 → 首领踉跄 0.5 秒。设计：`docs/design/DESIGN-ember-boss-moves-pack4-2026-10-05.md`。

## 线上状态（部署后核对于 2026-10-05 08:35 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.30**（`Enabling CoreRpg v1.65.30` 08:34:38）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `17b430e7bf751c8c559f2234639cb22f338862b10dce3a052391d07b99703e90` |
| balance_version | **53** |
| 回滚包 | `/workspace/backup/CoreRpg-1.65.29-pre-1.65.30.jar`（sha256 `d8195915…c8eede6e` = 1.65.29）|
| 服务器 PID | **1415948**（was 1392729；`server-runtime/stop.sh` 优雅停 → `start.sh`）|
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 部署文件 | `plugins/CoreRpg.jar`、`plugins/CoreRpg/ember-v1-runs.yml`、`plugins/TrMenu/menus/ember_p1_codex.yml`（config.yml 未动）|
| 源码对账 | 从提交 `095a06f` 的 tree `git archive` 重建 jar，与线上 jar 2360 个条目逐字节一致 |

## 内容

- `ember-v1-runs.yml`：Q01 重斩 / Q02 砸地 / Q03 誓斩 / Q04 冲击圈 / Q05 斧刃横扫 `whiff_stun: 0.5`；Q04 冲击圈普通 dmg 22 → 25；balance_version 52 → 53
- `EmberRunMaps.Skill.whiffStun`：非 charge、非 share，钳 ≤ 2 秒；复制构造保留（挑战 / 深渊 / 连战沿用）
- `EmberRunDirector`：预警开始时有人在形状内才装备（`anyoneInside`），右移二段不装备；`execute` 返回命中人数；落空 → `whiffStun`（同 D188 `stunUntil`：不放招、普攻取消、定身、follow 顺延）；提示「全员躲开它会踉跄 0.5 秒」/「落空！… 踉跄 0.5 秒 · 破绽，趁现在输出」；日志 `[P1 run] <run> whiff stun <招> 0.5s`
- 图鉴：Q01–Q05 该招一行 +「破绽」一行；Q04 冲击圈伤害 25；连战页斧刃横扫注「躲空踉跄 0.5 秒」
- p1sim：`Fight.hurt` 躲开返回 True；`WHIFF_STUN` / `WHIFF_ARM`；门禁 `tools/p1sim/whiffstun.py` → `tools/p1sim/out-whiffstun-d192.md`
- 奖励 / 掉落 / 闸门 / 词缀 / 事件 / 装备结构 / AFK / 签到 / 化妆品 / 团本：**不动**

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package`（JDK 8）| **294 / 0**（+3：`whiffStunParsesOffChargeAndShareAndIsCapped_D192`、`whiffOnlyWhenArmedAndNobodyHit_D192`、`earlyBossesStaggerOnAWhiffedHeavyMove_D192`）|
| p1sim 门禁 | 五图 × 普通 / 挑战 × 躲避 0.3/0.5/0.7：Q01 2.0 / Q02 2.9 / Q03 2.0 / Q04 2.3 / Q05 1.8 → MAX_ABS_DPP **2.9**（cap 3.0）；1.0 秒初稿 5.9 否决；连战 rushsim 全格 ±1 → **within range** |
| 冒烟 | 合批 deferred（新一批第 2 个：1.65.29 D191 + 1.65.30 D192；下批须含 D188 撞墙定点 + D192 落空踉跄定点）|
| persist-roundtrip | 不需要（无资产路径变化）|
