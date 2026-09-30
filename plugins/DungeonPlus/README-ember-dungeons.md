# 余烬日/周本 — 路径清单

设计：`/workspace/minecraft/DESIGN-dungeon-daily-weekly.md`  
维护备忘：玩家入口=TrMenu 点击进本（日/周/深渊/团本/精英）· 体力扣次；进本门控仍挂
管理/测本：`dp start-console <玩家> <DungeonId>` · 灾厄测本 `/dp start EmberCalamity`（须标仅测试）· 盟 Boss：`/corerpg guild boss`

## 地牢配置

| 路径 | 说明 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberDaily/option.yml` | 地图/出生/入场券条件/通关 ni give |
| `plugins/DungeonPlus/dungeon/EmberDaily/monster.yml` | 三波 MM 怪 |
| `plugins/DungeonPlus/dungeon/EmberDaily/obstacle.yml` | 空障碍（必填） |
| `plugins/DungeonPlus/dungeon/EmberDaily/task/timeout.yml` | 720s 超时失败 |
| `plugins/DungeonPlus/dungeon/EmberWeekly/option.yml` | 周本同上 |
| `plugins/DungeonPlus/dungeon/EmberWeekly/monster.yml` | 四波加压 |
| `plugins/DungeonPlus/dungeon/EmberWeekly/obstacle.yml` | 空障碍 |
| `plugins/DungeonPlus/dungeon/EmberWeekly/task/timeout.yml` | 1500s 超时 |
| `plugins/DungeonPlus/dungeon/EmberAbyss/` | 维护备忘：深渊 5 层 + 通关箱 5～9 档 |
| `plugins/DungeonPlus/dungeon/EmberCalamity/` | 维护备忘：灾厄 Boss + 日箱表 |
| `plugins/DungeonPlus/map/ember_arena/` | 共享回退 / `ember_arena` 本（保留） |
| `plugins/DungeonPlus/map/ember_daily/` | EmberDaily 独立图（绿羊毛+绿宝石柱标记） |
| `plugins/DungeonPlus/map/ember_weekly/` | EmberWeekly 独立图（蓝羊毛+青金石柱） |
| `plugins/DungeonPlus/map/ember_abyss/` | EmberAbyss 独立图（黑曜石/紫陶瓦墙） |
| `plugins/DungeonPlus/map/ember_calamity/` | EmberCalamity 独立图（地狱岩/岩浆块） |
| `plugins/DungeonPlus/map/ember_raid/` | EmberRaid 独立图（石英大平台） |
| `plugins/DungeonPlus/config.yml` | `dungeon-pre-folder` 已注册各 map |

## 次数（遗留票物 / 体力）

维护备忘：DP 1.4 无原生「每日 N 次」字段，物品仅用于 DP 入场条件；现行玩家次数以体力为准。

| NI ID | 显示名 | 规则 |
|-------|--------|------|
| `ticket_ember_daily` | 余烬日票 | 进本扣 1；遗留票物 / DP 入场条件；现行次数=体力 |
| `ticket_ember_weekly` | 余烬周票 | 进本扣 1；遗留票物 / DP 入场条件；现行次数=体力 |
| `ticket_ember_abyss` | 余烬深渊票 | 进本扣 1；遗留票物 / DP 入场条件；现行次数=体力 |

物品草案：`plugins/NeigeItems/Items/ember-dungeon-tickets.yml`  
> **维护备忘（票务 NI 对齐）：** 进本扣次仍用 DP `<item:显示名>`（官方只认物品名）；给票/计数已走 NI ID。详见 `/workspace/minecraft/STATUS-ember-ticket-ni-audit.md`。  
维护备忘：自动发放未落地前，仅管理/测试可用 `/ni give <玩家> ticket_ember_daily 3` 做物测；不作为玩家操作说明。

## 菜单

`plugins/TrMenu/menus/ember_daily.yml` / `ember_weekly.yml` 进本已改为 `dp start …`

## 重载建议

```
/dp reload
/ni reload   # 或重载 NI
/trmenu reload
```

权限：玩家需 `dungeon.user` / `dungeon.start`（见 DP 文档）。

## 维护备忘（地图与脚本）

- 地图已按本拆分（仍为测试区切片，出生 `-40,65,270`）；近出生点有主题方块标记；正式艺术面由 WorldEdit 再调。详见 `STATUS-ember-maps.md`。
- `$kill` 依赖 MM Display「余烬地窟僵尸/骷髅/蛮兵」；若对不上，看 DP debug 或改 monster.yml。
- 周本装备保底目前固定发刃。
