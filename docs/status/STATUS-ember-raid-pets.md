# STATUS · 团本 / 使魔 / 图录

**日期：** 2026-09-12（Asia/Shanghai）  
**约束：** 未改 Paper；**未覆写** `CoreRpg.jar`；**未改**既有 `EmberAbyss` DP（仅新增 `EmberRaid`）

---

## 团本 EmberRaid

| 项 | 值 |
|----|-----|
| 命令 | `/dp start EmberRaid` |
| 人数 / 复活 / 时限 | **3～5** / 5 / **2400s**（约 40 分） |
| 门票 | `ticket_ember_raid` · 显示名 **余烬团本票** · 周 1 |
| 地图 | `ember_arena` · 出生 `-40,65,270`（同日周/深渊占位） |
| 波次 | 深渊僵尸×6 → 混响 → `EmberAbyssBrute`+添头 → **`EmberCalamityBoss`** |
| `$kill` | 余烬深渊僵尸/骷髅/蛮兵 · 余烬灾厄使 |
| 通关箱 | 核心×4 · 晶×2 · 碎片10 · 骨尘6 · 孔石锋利+稳固 · `mat_calamity_ember`×1 |

套装饰品 / 称号进度：**未接**（菜单与结算文案占位）。

**如何测：**

```
/ni give <玩家> ticket_ember_raid 1
# 需 3～5 人组队后再
/dp start EmberRaid
# 或 /ember → 团本 → 开始协作
```

重载：`/dp reload` · `/ni reload` · `/trmenu reload`

---

## 使魔 / 图录（菜单壳）

| 入口 | 菜单 | 说明 |
|------|------|------|
| hub **D** 使魔 | `ember_pet.yml` | 图鉴 / 出战 / 魂尘说明 · 命令占位 |
| hub **F** 图录 | `ember_bestiary.yml` | 怪物 / 装备 / 使魔册 · 阶段奖 tell |

逻辑、魂尘 NI、登记：**未接**（见 `docs/design/DESIGN-ember-pet-bestiary.md`）。

---

## 路径清单

| 路径 | 说明 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberRaid/option.yml` | 团本 DP 入口 / 扣票 / 通关箱 |
| `plugins/DungeonPlus/dungeon/EmberRaid/monster.yml` | 波次 · Abyss + CalamityBoss |
| `plugins/DungeonPlus/dungeon/EmberRaid/obstacle.yml` | 空障碍壳 |
| `plugins/DungeonPlus/dungeon/EmberRaid/task/timeout.yml` | 2400s 失败 |
| `plugins/NeigeItems/Items/ember-dungeon-tickets.yml` | **追加** `ticket_ember_raid` |
| `plugins/TrMenu/menus/ember_raid.yml` | 团本子菜单 |
| `plugins/TrMenu/menus/ember_pet.yml` | 使魔壳 |
| `plugins/TrMenu/menus/ember_bestiary.yml` | 图录壳 |
| `plugins/TrMenu/menus/ember_hub.yml` | D/F/K 接线，去掉「即将点燃」 |
| `docs/ember-hub-copy.md` | 使魔/图录/团本文案同步 |
| `docs/design/DESIGN-ember-pet-bestiary.md` | 短设计 |
| `docs/status/STATUS-ember-raid-pets.md` | 本文 |
| `plugins/DungeonPlus/dungeon/EmberAbyss/*` | **未改**（插件 bot 已有） |
| `plugins/CoreRpg.jar` | **未覆写** |

---

## 验收对照

| # | 标准 | 本期 |
|---|------|------|
| 1 | hub 团本无「即将点燃」，可 `menu: ember_raid` → `dp start EmberRaid` | **是** |
| 2 | 团本 3～5 人、周票扣次 YAML | **是**（需真服组队验） |
| 3 | 怪用 EmberAbyssBrute / EmberCalamityBoss 风格 | **是**（复用现 MM ID） |
| 4 | hub 使魔/图录开壳、无「即将点燃」 | **是** |
| 5 | 不破坏 EmberAbyss / 日周本 / CoreRpg.jar | **是** |

未重启服务端。
