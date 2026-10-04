# CoreRpg 1.65.3：拆分 → 破甲（D164）、裂身纹退池（D165）、范围与新手提示（D166）

上线：**2026-10-04 13:41 CST**（PID 587771，0 人在线时 `server-runtime/stop.sh` / `start.sh`）。包含未单独部署的 1.65.2（D163：B01 + 开局药格文案 5–9 格）。协调：`/workspace/COORD-corerpg-1653.txt`（routine 已让出；吸收了 COORD-q01-hint 和 1.65.2 待部署）。

## 内容

| 裁决 | 改了什么 |
|---|---|
| D164 破甲 | `t2c`「拆分」→「破甲」：`dmg_affix_shield` 1.60 / `dmg_affix_blazing` 0.80 / `dmg_affix_split` 0.80（分身也算分裂）。`EmberGrowthService.classMult(m, cls, affix)` 用 `EmberRunService.mobAffix(target)` 取目标词缀类型。节点 id 不变，已点拆分的人自动变破甲。growth.yml `version: 3` |
| D165 裂身纹退池 | `b_split` `rollable: false`：`EmberAffix.pool()` 只给可洗词条；锁定退役词条被忽略 / 拒绝，洗练页不显示锁定按钮；词条池最后列「裂身纹：已移出洗练池（洗不出来；已有的照常生效）」。刃池 3 → 2 |
| D166 文案 | 第二排主题 + t2a/t2b/t2c 正面文案、猎缀纹 / 裂身纹 / 抗缀纹 `note`：「仅主线重打生效」；图鉴词缀精英条目；Q01 r1 `hint`；`EmberRunRules.PRE_BOSS_HINT`（清完最后一房等首领时，所有主线）；R01 `party_hint`（招募广播、开本、冒险页图标） |
| 菜单 | `ember_p1_reroll.yml`（刃 2 选 1、护符 3 选 1；期望次数 刃 不锁约 11 / 锁定约 6，护符约 16 / 6）、`ember_p1_adventure.yml`（R01 炽愈提示）、`ember_p1_codex.yml`（第二排范围） |

不改：数值表、奖励、`balance_version`（29）、ember-v1-runs 其它内容。

## 模拟（上线前）

`tools/p1sim/check_d164.py` → `tools/p1sim/out-d164-armorbreak-check.md`。rules sha256 `f23d20ef0ce2779e`，n=3000，躲避 0.5，种子 4243 配对；容差 14 情境 × 3 躲避 × 种子 7/11/13 × 600 局。破甲 vs 1.65.2 拆分：

| 厚甲精英击杀用时 | 焚烬 | 烬爆 | 炽愈 |
|---|---|---|---|
| Q04 重打 | −34.8% | −34.0% | −36.2% |
| Q07 重打 | −31.7% | −30.5% | −34.6% |

- 分裂精英：慢 +1.8 ～ +10.6%；炽热精英：与拆分相同（都比无成长慢 +14 ～ +20%）。
- 重打通关率 +0.2 ～ +3.2pp（相对拆分），不超过无成长；Q07 挑战不变。
- 首通 / 挑战 42 格：无成长 / 拆分 / 破甲三组逐局相同（词缀精英只在重打出现）。
- 洗练（rerollsim，刃，指定极品）：不锁 15.9 → 10.5 次，锁定 6.2 → 5.6 次。

## 构建与部署

- `JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01`，Maven 离线；单测 **229 / 0**；class major **52**；jar `plugin.yml` 1.65.3。
- 回滚包：`/workspace/backup/CoreRpg-1.65.1-pre-1653.jar`（上线前的 1.65.1，sha256 `f415f897…`）。新 jar 备份 `/workspace/backup/CoreRpg-1.65.3.jar`（sha256 `3a22e108…`）。
- `latest.log`：`Enabling CoreRpg v1.65.3`、`[CoreRpg] [storage] MySQL connected`、`[CoreGacha] [db] MySQL connected`、`Done (5.638s)`、SEVERE 0；`[P1 growth] talents 9 nodes / 6 points, honors 7, reroll 6 affixes`。无 `CORERPG_TEST_FAULTS`；两份 ops.json 均为 `[]`。

## 实机冒烟（1.65.3，13:42–13:50 CST，日志 `/workspace/scratch/d1653/`）

| 号 | 做法 | 管理员辅助 | 结果 |
|---|---|---|---|
| FreshQ100 | `MOVE=kite` realgear Q01，T0 装、Lv10 | 无 | **PASS**：通关 151 s，0 死，喝 2 瓶；聊天出现「提示：进门后退到门口打，别站进怪堆…」和「首领前留 2 瓶药…」 |
| FreshQ101 | `smoke-armor.sh` + kite newbie-run Q01 | firstclear q01–q07、5000 币、`runs variety shield:r1` 强制厚甲精英、控制台伤害追踪（之后关掉） | **PASS**：点了稳桩 + 破甲，文案正确；厚甲精英日志 `[厚甲] 灰烬卒 … D141 天赋 ×1.600 → 96.77`，普通怪无倍率、首领 ×0.990（稳桩）；通关结算；两条提示都出现。洗练页：「刃 2 选 1」，猎缀纹 / 抗缀纹带「（仅主线重打生效）」，「裂身纹：已移出洗练池」；天赋页第二排主题带「仅主线重打生效」 |
| FreshQ102 | firstclear q01–q07 后在线，FreshQ101 `/corerpg p1 recruit r01` | firstclear | **PASS**：收到「…招 锈轨矿道·团 队员 … 建议队伍里有炽愈：首领半血时加怪、单下伤害高，要有人回血 [申请入队]」 |

结束后 bot 全部下线（`/list` 0 人）。

**没覆盖：** R01 开本时那一行（要 3 人开本；单测 + 招募广播 + 冒险页覆盖）；真人数据（只有模拟 + bot）；没在实机比较击杀用时（模拟给的）。厚甲精英受到少量原版护甲减伤（−0.87），本版之前就有，不归本改动。

## 下一个

下一测号 **FreshQ103**，下一裁决 **D167**。B02 剩余：第三排套装专精、余烬纹、定身纹（设计文档 §7 其它提案）。
