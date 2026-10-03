# STATUS · EmberEliteWeekly 承伤下调（阶段 4.4e 怪物岗）

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：4.4d FAIL（约 459s 进 Boss、时长近目标；死亡 4 / 复活 3 耗尽，约 529s 未通关）
- 策略：波血保持 4.4d；波伤压至约 2/3；Boss 5200/12、Pulse100 **禁止改**；不抬伤
- 未改：Paper / DP / 票据；Abyss / Afk；ops（始终 `[]`）

## 文件

`plugins/MythicMobs/Mobs/EmberEliteWeekly.yml`

## 改前 → 改后

| MM ID | Health | Damage | Ignite ~onAttack |
|-------|--------|--------|------------------|
| EmberEliteZombie | **5250** 保持 | 7 → **5** | 0.28 → **0.20** |
| EmberEliteSkeleton | **3300** 保持 | 6 → **5** | 0.12 → **0.09** |
| EmberEliteMix | **2700** 保持 | 7 → **5** | 0.15 → **0.11** |
| EmberEliteBrute | **10500** 保持 | 8 → **6** | 0.18 → **0.13** |
| EmberEliteBoss | **5200** | **12** | Pulse **100**（未动） |

Rush `~onTimer:200` 未加快。

## 加载

- 游玩服短重启；MM **41** 怪；`ops.json` = `[]`

## 请测试复测

无 buff：能否通关且复活有余；全本仍宜落在 8～12 分窗。
