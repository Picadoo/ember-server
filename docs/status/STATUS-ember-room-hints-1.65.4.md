# CoreRpg 1.65.4：Q02–Q07 战术房提示（D170）+ 装备结构分阶段计划定稿（D169）

上线：**2026-10-04 15:53 CST**（PID 684468，0 人在线时 `server-runtime/stop.sh` / `start.sh`）。协调：`/workspace/COORD-routine-1547.txt`。

## 内容

| 裁决 | 改了什么 |
|---|---|
| D169 文档 | `docs/design/DESIGN-ember-gear-staged-plan-2026-10-04.md` 定稿 6 槽分阶段计划；`COORD-gear-structure-hold.txt` → SETTLED PLAN，CoreRpg 结构代码仍 HOLD。不占 D167/D168 |
| D170 文案 | Q02–Q07 第一房短中文 `hint`；Q04 r3 落差提醒；Q01 r1 门口提示保留。两份 `ember-v1-runs.yml` 同步 |

不改：战斗数值、variety、技能、`balance_version`（29）、非 hint 键、tools/p1sim、槽位/套装/锻造 Java。

### 提示文案

| 图 | 房 | hint |
|---|---|---|
| Q01 | r1 | （D166 保留）进门后退到门口打，别站进怪堆… |
| Q02 | r1 焦梁廊 | 炉边怪多，先清近战再打侧炉 |
| Q03 | r1 上层碑厅 | 走廊窄，别把怪拉进拐角堆叠 |
| Q04 | r1 双渠平台 | 注意水边落差，别掉出房间（靠里站） |
| Q04 | r3 闸前检修场 | 靠内侧实心地面打，别贴东墙水边 |
| Q05 | r1 第一层环院 | 环廊风筝，别被夹在内外圈 |
| Q06 | r1 霜旗外院 | 开阔地风筝，远程优先 |
| Q07 | r1 装卸场 | 货箱掩体，别站死角 |

## 构建与部署

- `JAVA_HOME=/workspace/minecraft/tools/jdk8u504-b01`，Maven 离线；单测 **230 / 0**（含 `textHints_D170`）；class major **52**；jar `plugin.yml` 1.65.4。
- 回滚包：`/workspace/backup/CoreRpg-1.65.3-pre-1654.jar`（1.65.3，sha256 `3a22e108…`）。新 jar 备份 `/workspace/backup/CoreRpg-1.65.4.jar`（sha256 `21feda2d…`）。
- `latest.log`：`Enabling CoreRpg v1.65.4`、`[CoreRpg] [storage] MySQL connected`、`[CoreGacha] [db] MySQL connected`、`Done (15.659s)`、SEVERE 0。ops.json `[]`。

## 实机冒烟（1.65.4，15:54–15:58 CST，日志 `/workspace/scratch/d1654/`）

| 号 | 做法 | 结果 |
|---|---|---|
| FreshQ107 | `MOVE=kite` realgear Q02，T1+0 烬爆，Lv10 | 进本并清完 R1–R3；Boss 倒下（FAIL，可接受短冒烟）。未在脚本截断聊天里抓到提示行 |
| FreshQ108 | 进 Q02 → 走进 r1 触发 | **PASS**：聊天出现「提示：炉边怪多，先清近战再打侧炉」 |

下一测号 **FreshQ109**。下一裁决号留给 forge-random / gear8 / Stage 0 工作者（D167/D168 不由本窗占用）；本窗已用 D169/D170。
