# STATUS · EmberEliteWeekly 平衡三调（阶段 4.4c 怪物岗）

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`STATUS-ember-elite-weekly-test-4.4b.md` FAIL（无 buff 烬刃+7 Lv42：全本 **193s** 仍短 ~2.5×；Boss TTK **65s PASS**；结束剩血 **16%** / minHp≈0.55；复活 2/2）
- 目标：全本 8～12 分、Boss TTK 45～75s（已 PASS 保持）、结束剩血 25～55%
- 未改：Paper / DP / CoreRpg；`EmberAbyss` / `EmberAfk`；Boss Health/Damage；Display / 掉落；ops（始终 `[]`）

## 文件

`plugins/MythicMobs/Mobs/EmberEliteWeekly.yml`

## 改前 → 改后

| MM ID | Health | Damage | 备注 |
|-------|--------|--------|------|
| EmberEliteZombie | 1400 → **3500** | 9 → **7** | 波1 加池略降伤 |
| EmberEliteSkeleton | 900 → **2200** | 8 → **6** | 同上 |
| EmberEliteMix | 700 → **1800** | 9 → **7** | 波2 |
| EmberEliteBrute | 2800 → **7000** | 10 → **8** | 波2 |
| EmberEliteBoss | **5200** 保持 | **12** 保持 | TTK PASS |
| BossPulse onTimer | 80 → **100** | — | 略收爆发 |

无 LevelModifiers health（Brute 7000 / Boss 5200 均 < maxHealth 20000）。

## 加载

- 游玩服短重启
- MythicMobs：成功加载 **41** 个怪物
- `ops.json` = `[]`

## 请测试复测

无 buff 烬刃 T2+6～+8：全本时长、Boss TTK、结束剩血 / 复活次数。
