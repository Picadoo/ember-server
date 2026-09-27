# STATUS · EmberEliteWeekly 平衡二调（阶段 4.4b 怪物岗）

- 时间：2026-09-27（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`STATUS-ember-elite-weekly-test-4.4.md` CONDITIONAL PASS（功能过；有 resistance I+strength I 时全本 **66s**、Boss TTK **37s**、近满血；无 buff +4 曾团灭）
- 目标：单人烬刃 T2+6～+8、**无 op buff** → 全本 8～12 分、Boss TTK 45～75s、结束剩血 25～55%
- 未改：Paper / DP / CoreRpg；`EmberAbyss` / `EmberAfk`；Display / 掉落表结构；ops（始终 `[]`）
- 无 LevelModifiers health（避免叠爆 maxHealth 20000）

## 文件

- `plugins/MythicMobs/Mobs/EmberEliteWeekly.yml`（主改）
- `plugins/MythicMobs/Skills/EmberEliteSkills.yml`（仅注释；脉冲本体未改）

## 改前 → 改后

| MM ID | Health | Damage | 其它 |
|-------|--------|--------|------|
| EmberEliteZombie | 160 → **1400** | 6 → **9** | — |
| EmberEliteSkeleton | 110 → **900** | 5 → **8** | — |
| EmberEliteMix | 180 → **700** | 6 → **9** | — |
| EmberEliteBrute | 700 → **2800** | 5 → **10** | — |
| EmberEliteBoss | 2800 → **5200** | 4 → **12** | BossPulse `~onTimer` **120 → 80**（约 6s→4s） |

## 加载

- 游玩服短重启
- MythicMobs：成功加载 **41** 个怪物 / **22** 个技能（数量不变）
- `ops.json` = `[]`

## 请测试复测

烬刃 T2+6～+8、**无** resistance/strength op buff，跑通 `EmberEliteWeekly`：全本时长、Boss TTK、结束剩血。
