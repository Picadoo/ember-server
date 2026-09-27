# STATUS · EmberAbyssWatcherDeep 抬血（阶段 4.3 怪物岗）

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`STATUS-ember-abyss-ttk-layer2.md` FAIL；`docs/design-stage4-abyss-9-12.md` §2.3；总控派活（Paper maxHealth.max 已 20000）
- 未改：Paper / DP / CoreRpg；1～8 层；`EmberAbyssWatcher`（1500/3）；Gaze / 掉落 / Display；ops（始终 `[]`）

## 文件

`plugins/MythicMobs/Mobs/EmberAbyss.yml` → **`EmberAbyssWatcherDeep`**

## 改前 → 改后

| 项 | 改前 | 改后 |
|----|------|------|
| Health | 2200 | **20000** |
| Damage | 4 | **7** |
| LevelModifiers health | 12 | **0**（防 level 叠血顶破 20000 上限） |
| LevelModifiers damage | 1.4 | 1.4（不变） |
| Display | `&5余烬深渊·看守·深` | 不变 |
| Gaze | `EmberAbyssWatcherGaze` ~onTimer:100 | 不变 |
| 掉落 | 核心必掉 / 孔石≈45% / 保护券 12% / 无稳定符 | 不变 |

## 加载

- 游玩服短重启（MM 启动加载）
- `spigot.yml` `attribute.maxHealth.max` = **20000.0**（Paper 岗已改，本岗只读）
- `ops.json` = `[]`

## 请测试复测

满二层烬刃参照档：第 10 层 TTK 50～80s 剩血 25～50%；第 12 层 55～90s 剩血 25～55%。若仍偏短/偏脆再微调（勿超 20000）。
