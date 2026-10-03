# STATUS · EmberAbyssWatcherDeep 三调（阶段 4.3c 怪物岗）

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/status/STATUS-ember-abyss-ttk-4.3b.md` FAIL（7500/18 → 第10 TTK≈97.9s/剩血≈61%；第12≈90.9s/剩血≈100%；Damage 18 已能逼复活）
- 目标：`docs/design/design-stage4-abyss-9-12.md` §2.3（10层 50～80s / 剩血 25～50%；12层 55～90s / 25～55%）
- 未改：Paper / DP / CoreRpg；Gaze / 掉落 / Display；`EmberAbyssWatcher`（1500/3）；1～8；ops（始终 `[]`）

## 文件

`plugins/MythicMobs/Mobs/EmberAbyss.yml` → **`EmberAbyssWatcherDeep`**

## 改前 → 改后

| 项 | 改前（4.3b） | 改后（4.3c） |
|----|--------------|--------------|
| Health | 7500 | **6000** |
| Damage | 18 | **18**（保持） |
| LevelModifiers health | 0 | **0**（保持） |
| LevelModifiers damage | 1.4 | 1.4 |
| Display / Gaze / 掉落 | — | 不变 |

粗算：6000 ÷ 98 DPS ≈ **61s**（落在 10 层窗中段；12 层略紧上沿）。只削血、不抬伤，保留复活压力。

## 加载

- 游玩服短重启（`./stop.sh` + `./start.sh custom`）
- MythicMobs：成功加载 **36** 个怪物
- `ops.json` = `[]`

## 请测试复测

满二层烬刃参照档复测第 10 / 12 层 TTK 与结束剩血。
