# STATUS · EmberEliteWeekly 再降致死（阶段 4.4f 怪物岗）

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：4.4e（全本 529s PASS、Boss TTK 68s PASS；死亡 3/3，结束血 93% 为复活灌血不可计）
- 目标：通关死亡 ≤2，使结束剩血可测（25～55%）；勿抬伤
- 未改：Boss 5200/12、Pulse100、Rush200；Paper / DP；Abyss / Afk；ops（始终 `[]`）

## 文件

`plugins/MythicMobs/Mobs/EmberEliteWeekly.yml`

## 改前 → 改后

| MM ID | Health | Damage | Ignite ~onAttack |
|-------|--------|--------|------------------|
| EmberEliteZombie | **5250** | 5 → **4** | 0.20 → **0.15** |
| EmberEliteSkeleton | **3300** | 5 → **4** | 0.09 → **0.07** |
| EmberEliteMix | **2700** | 5 → **4** | 0.11 → **0.08** |
| EmberEliteBrute | **10500** | 6 → **5** | 0.13 → **0.10** |
| EmberEliteBoss | **5200** | **12** | Pulse **100**（禁止改） |

## 加载

- 游玩服短重启；MM **41** 怪；`ops.json` = `[]`

## 请测试复测

无 buff：死亡次数、结束剩血（非灌血）、全本时长 / Boss TTK。
