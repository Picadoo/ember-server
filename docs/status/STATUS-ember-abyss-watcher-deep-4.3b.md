# STATUS · EmberAbyssWatcherDeep 二调（阶段 4.3b 怪物岗）

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/status/STATUS-ember-abyss-ttk-4.3.md` FAIL（20000/7 → TTK≈205s、剩血≈100%；bot DPS≈98/s）
- 目标：`docs/design/design-stage4-abyss-9-12.md` §2.3（10层 50～80s / 剩血 25～50%；12层 55～90s / 25～55%）
- 未改：Paper / DP / CoreRpg；Gaze / 掉落 / Display；`EmberAbyssWatcher`（1500/3）；1～8；ops（始终 `[]`）

## 文件

`plugins/MythicMobs/Mobs/EmberAbyss.yml` → **`EmberAbyssWatcherDeep`**

## 改前 → 改后

| 项 | 改前（4.3） | 改后（4.3b） |
|----|-------------|--------------|
| Health | 20000 | **7500** |
| Damage | 7 | **18** |
| LevelModifiers health | 0 | **0**（保持） |
| LevelModifiers damage | 1.4 | 1.4 |
| Display / Gaze / 掉落 | — | 不变 |

粗算：7500 ÷ 98 DPS ≈ **76s**（贴近 10/12 窗上沿～中段）；Damage 18 抬威胁，压低站桩满血通关。

## 加载

- 游玩服短重启；MM 启动加载
- `ops.json` = `[]`

## 请测试复测

满二层烬刃参照档复测第 10 / 12 层 TTK 与结束剩血。
