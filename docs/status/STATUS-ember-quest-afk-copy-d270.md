# 状态 · D270：主线 quest 挂机庭僵尸文案 → P1 自动战斗

**日期：** 2026-10-07（上海时间）  
**上游：** D269（`fc3699d2`）全息假掉落  
**范围：** `plugins/CoreRpg/quest.yml` + `CoreRpg/src/main/resources/quest.yml`（同源）

## 债证（修前）

| 位置 | 原文 | 为何假/脏 |
|---|---|---|
| L43 desc | `在挂机庭击败 挂机庭僵尸` | P1 挂机为自动战斗，层怪 MM 为 `EmberQ01Melee` 等，非「挂机庭僵尸」展示 |
| L50–51 | `僵尸或骷髅` / `骷髅会放箭…` | 同假；且 `mobs: EmberAfkZombie/Skeleton` 在 P1 自动战斗下**不计杀**（实装 `t.mm`） |
| L44 hint | `往西偏北五十来格的草坡是怪区` | 旧落点指引；现行为站层内自动开打 |
| L302–305 | `EmberAfk4*` / 旧 hint | ④ 层实装 `EmberQ07Melee` |

## 改后（玩家文案 + 计杀 MM）

- 灰烛：灰坡能自动打怪  
- 击杀 1：`EmberQ01Melee` ×6 · desc/hint 自动战斗口径  
- 击杀 2：`EmberQ01Melee` ×4 · 去掉骷髅箭 hint  
- ④ 层：`EmberQ07Melee` ×12 · 自动战斗 hint  

零改奖励数 / 章节结构。未开六格。

## 验收

- `corerpg reload`（读 live `plugins/CoreRpg/quest.yml`）
- rg：无「挂机庭僵尸」「僵尸或骷髅」「EmberAfkZombie」
