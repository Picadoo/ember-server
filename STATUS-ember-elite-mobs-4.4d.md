# STATUS · EmberEliteWeekly 平衡四调（阶段 4.4d 怪物岗）

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：测试 4.4c（全本 **383s** FAIL；Boss TTK **68s PASS**；结束 **31% PASS**；复活 3/3）
- 总控估算：有效 DPS≈80/s；目标全本≈540s → 波次再 +≈157s → 波血池 **×1.5**；波伤不抬（已濒死）
- 未改：Boss 5200/12、Pulse100；Paper / DP / CoreRpg；Abyss / Afk；ops（始终 `[]`）

## 文件

`plugins/MythicMobs/Mobs/EmberEliteWeekly.yml`

## 改前 → 改后

| MM ID | Health | Damage |
|-------|--------|--------|
| EmberEliteZombie | 3500 → **5250** | **7** 保持 |
| EmberEliteSkeleton | 2200 → **3300** | **6** 保持 |
| EmberEliteMix | 1800 → **2700** | **7** 保持 |
| EmberEliteBrute | 7000 → **10500** | **8** 保持 |
| EmberEliteBoss | **5200** | **12** |
| BossPulse onTimer | **100** | — |

Brute 10500 < maxHealth 20000；无 LM health。

## 加载

- 游玩服短重启；MM **41** 怪；`ops.json` = `[]`

## 请测试复测

无 buff：全本是否落入 480～720s；Boss / 剩血应仍 PASS。
