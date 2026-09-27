# STATUS · S3 日常线 D/E 怪物 MM 草案

- 时间：2026-09-28（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design-ember-daily-s3.md` §4.3 / §5.3；总控派工 S3
- 未做：DP / 地图 / 菜单 / Paper / 体力数值；S1/S2 日怪未改
- 口径：对齐 S2 日常量级一次估好（通关约 5～8 分钟）；掉落 `corerpg mmgive` 轻量日常级

## 新文件

| 文件 | 线 |
|------|-----|
| `plugins/MythicMobs/Mobs/EmberDailyTide.yml` | D 潮蚀水道 |
| `plugins/MythicMobs/Mobs/EmberDailySpire.yml` | E 断塔回廊 |

## 规格

### 线 D（桥面友好 · 近战可清）

| MM ID | Display（去色） | Type | Health | Damage | 备注 |
|-------|-----------------|------|--------|--------|------|
| EmberDailyTideZombie | 潮蚀尸 | ZOMBIE | 52 | 4 | SoftHit + 轻减速 |
| EmberDailyTideSkeleton | 浪矢骷 | SKELETON | 41 | 4 | **弓**可射；移速 0.27 + SoftHit **可贴脸**；`PreventRandomEquipment` |
| EmberDailyTideBrute | 潮闸蛮兵 | ZOMBIE | 205 | 3 | 减速光环 + 轻伤光环；`mmxp elite` |

### 线 E（环廊 · 顶台推离）

| MM ID | Display（去色） | Type | Health | Damage | 备注 |
|-------|-----------------|------|--------|--------|------|
| EmberDailySpireZombie | 断塔卫尸 | ZOMBIE | 50 | 4 | SoftHit |
| EmberDailySpireSkeleton | 裂隙箭骷 | SKELETON | 39 | 4 | **弓**环廊对射；可贴脸；`PreventRandomEquipment` |
| EmberDailySpireWarden | 断塔守望 | SKELETON | 215 | 3 | 木剑近战 Boss；弱 throw 推离 + 压迫光环；`mmxp elite` |

对照 S2：焦骨 50/4·40/4·200/3；地窖 48/4·38/4·210/3 → 本草案同档微调，禁多轮盲调。

## Display / ID 撞名

- 去色明文与 `灰烬庭院*` / `焦骨*` / `燃矢*` / `窖卫*` / `誓印*` / `残誓*` / `余烬地窟*` **不撞**
- ID 前缀 `EmberDailyTide*` / `EmberDailySpire*`，不撞 Ash / Crypt / Abyss / Afk
- 色码：D `&3` · E `&7` · 庭院 `&a` · 焦骨 `&c` · 地窖 `&8`

## 加载

- 游玩服短重启；MythicMobs 成功加载 **53** 个怪物（+6，相对 S2 的 47）
- `ops.json` = `[]`

## 交总控

可接地图刷点 / DP `$kill`（Display 去色对齐上表）/ 菜单 D/E。勿改体力表。
