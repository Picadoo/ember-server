# STATUS · EmberEliteWeekly 阶段 4.4f 验收（测试岗）

- 时间：2026-09-27 19:33:59～19:44:18（Asia/Shanghai）
- 执行：余烬-测试岗
- 依据：`STATUS-ember-elite-mobs-4.4f.md`
- 对照：`STATUS-ember-elite-weekly-test-4.4e.md`
- 脚本：`/tmp/elite-weekly-4.4f-clear.js`（`FORCE_BUFF=false`）
- 原始 JSON：`/tmp/elite44f-out.json`
- 账号：`ElF8838`（新号）
- **未修改玩法数值、Boss 5200/12、Pulse100 或怪物配置**

## 总评

**PASS**。

4.4f 无战斗 buff、Lv42、烬刃 T2+7 参照档下，hub 菜单成功进本并扣除 1 张 `ticket_ember_elite`；全本 **510s**、Boss TTK **70s**、通关前一瞬 HP **13.605/40（34%）**，全部落在验收窗口。全程 **1 死 / 1 次成功复活**，未发生最后一死，通关前 HP 快照有效，不是复活灌血污染。

## 必测结果

| 指标 | 结果 | 实测 |
|---|---|---:|
| hub「精英试炼」进本 | **PASS** | 菜单流日志含队伍创建、`精英试炼开启`、试炼一；点击端有 transaction timeout，但进本已成功 |
| `ticket_ember_elite` 扣票×1 | **PASS** | 进本前 1，进本后 0 |
| 全本通关时长 480～720s | **PASS** | **510s** |
| Boss TTK 45～75s | **PASS** | Boss @439s，通关/击杀检测 @509s，**70s** |
| 结束剩血 25～55% | **PASS** | 通关检测瞬间 **13.605/40 = 34%**；死亡后已成功复活，之后无再死亡 |
| 死亡 / 成功复活 | **PASS** | **1 / 1**；复活后剩余复活次数 2 |
| minHp | 记录 | **1.332/40（约3.3%）** |
| 窒息 | **PASS** | 0 |
| 战斗 buff | **PASS** | 测前 `/effect clear`；`buff=false`；未给予 resistance/strength/任何 op 战斗 buff |

## 账号与装备

- 等级：**Lv42**
- 主武器：烬刃 T2 **+7**；护符 **+4**；sharpness **3**
- 誓约：`blaze`；天赋：`blaze_root` / `blaze_crit1` / `blaze_leech1`
- 最大生命：40.0
- hits / skill casts：**656 / 57**
- 测前 HP：13.851/40；进入准备观测 13.916/40（无战斗 buff）

## 波次与对照

- 波1：0s
- 波2：247s
- Boss：439s
- 通关：509s

| 指标 | 4.4e | 4.4f |
|---|---:|---:|
| 全本 | 529s 通关 | **510s 通关（PASS）** |
| Boss TTK | 68s（PASS） | **70s（PASS）** |
| 通关前 HP | 93%，复活灌血不可计 | **34%，快照有效（PASS）** |
| 死亡 / 复活 | 3 / 3 | **1 / 1** |
| minHp | 0.691/40 | **1.332/40** |
| 窒息 | 0 | **0** |

4.4f 将死亡从 4.4e 的 3 次降至 1 次；全本与 Boss TTK 仍在窗口内，且通关前一瞬 HP 已可有效验收。测试岗未改怪物/玩法数值。

## 测后状态

- 临时 `RpgBot`（UUID `148ec8b6-253f-36f4-bcd7-bf2395b8df80`）已 deop。
- `server-runtime/ops.json`：`[]`
- `login-runtime/ops.json`：`[]`
- 游玩服端口 **25567**、proxy **25565** 仍运行。
