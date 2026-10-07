# 余烬日/周本 — 路径清单

设计：`/workspace/minecraft/docs/design/DESIGN-dungeon-daily-weekly.md`  
维护备忘：P1 现行玩家入口 = TrMenu 主枢纽进 **Q01–Q07**（`/corerpg enter q0x`）· 扣体力。旧日/周/深渊/团本/精英菜单与 NI「票」是遗留物——**S0 闸门（D198–D202）下普通玩家进不了旧本**；次数体感以体力为准，不要写成「门票」。
管理/测本（OP / 控制台）：`dp start-console <玩家> <DungeonId>` · 灾厄测本 `/dp start EmberCalamity`（仅测试）· 盟 Boss：`/corerpg guild boss`

## 地牢配置

| 路径 | 说明 |
|------|------|
| `plugins/DungeonPlus/dungeon/EmberDaily/option.yml` | 地图/出生/遗留票物入场条件/通关 ni give |
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

## 次数（遗留票物 · 现行=体力）

维护备忘：DP 1.4 无原生「每日 N 次」字段。下表 NI 是**遗留入场条件物**，不是给玩家教的「门票」；P1 现行扣次 = 体力。旧本在 S0 下对非 OP 已关，这些票不会成为玩家日常货币。

| NI ID | 显示名（遗留物名） | 规则 |
|-------|--------|------|
| `ticket_ember_daily` | 余烬日票 | 遗留 DP 入场条件物；**现行次数=体力**，勿当门票卖 |
| `ticket_ember_weekly` | 余烬周票 | 同上 |
| `ticket_ember_abyss` | 余烬深渊票 | 同上 |

物品草案：`plugins/NeigeItems/Items/ember-dungeon-tickets.yml`  
> **维护备忘：** 旧 DP `<item:显示名>` 条件仍可能引用显示名；给物/计数走 NI ID。详见 `docs/status/STATUS-ember-ticket-ni-audit.md`。  
仅管理/测试可用 `/ni give <玩家> ticket_ember_daily 3` 做物测；**不要写成玩家操作说明**。

## 菜单

- **P1 现行：** `plugins/TrMenu/menus/ember_hub.yml`（及 `ember_p1_*`）→ `/corerpg enter q01..q07`。
- **遗留旧本菜单：** `ember_daily.yml` / `ember_weekly.yml` 等仍写 `corerpg enter daily|…`——普通玩家会被 S0-2 拒绝；菜单本身也无命令绑定（S0-8）。勿再写「已改为 `dp start`」（与现文件不符）。

## 重载建议

```
/dp reload
/ni reload   # 或重载 NI
/trmenu reload
```

权限：DP 仍声明 `dungeon.user` / `dungeon.start`；P1 下旧本另受 `%corerpg_gate_*%`（S0-1）约束，勿以为有 `dungeon.start` 就能开旧本。

## 维护备忘（地图与脚本）

- 地图已按本拆分（仍为测试区切片，出生 `-40,65,270`）；近出生点有主题方块标记；正式艺术面由 WorldEdit 再调。详见 `docs/status/STATUS-ember-maps.md`。
- `$kill` 依赖 MM Display「余烬地窟僵尸/骷髅/蛮兵」；若对不上，看 DP debug 或改 monster.yml。
- 周本装备保底目前固定发刃。
