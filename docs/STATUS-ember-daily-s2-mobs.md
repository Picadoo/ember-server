# STATUS · S2 日常线 B/C 怪物 MM 草案

- 时间：2026-09-28（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design-ember-daily-s2.md` §4.3 / §5.3；总控派工 S2
- 未做：DP / 地图 / 菜单 / Paper；庭院 `EmberDaily.yml` 未改
- 口径：庭院同档轻压（通关约 5～8 分钟）；掉落 `corerpg mmgive`

## 新文件

| 文件 | 线 |
|------|-----|
| `plugins/MythicMobs/Mobs/EmberDailyAsh.yml` | B 焦骨甬道 |
| `plugins/MythicMobs/Mobs/EmberDailyCrypt.yml` | C 残誓地窖 |

## 规格

### 线 B（近战优先 · 少远程）

| MM ID | Display（去色） | Type | Health | Damage | 备注 |
|-------|-----------------|------|--------|--------|------|
| EmberAshZombie | 焦骨尸 | ZOMBIE | 50 | 4 | SoftHit + 轻点燃 |
| EmberAshSkeleton | 燃矢骷 | SKELETON | 40 | 4 | **木剑近战**（无弓）；点燃更高 |
| EmberAshBrute | 焦核蛮兵 | HUSK | 200 | 3 | SoftHit + 点燃 + 0.5 灼烧光环；`mmxp elite` |

### 线 C（地窖高差 · 守墓）

| MM ID | Display（去色） | Type | Health | Damage | 备注 |
|-------|-----------------|------|--------|--------|------|
| EmberDailyCryptZombie | 窖卫尸 | ZOMBIE | 48 | 4 | SoftHit + Slow；**不用** `EmberCrypt*`（地窟已占） |
| EmberDailyCryptSkeleton | 誓印骷 | SKELETON | 38 | 4 | **弓**（中层高台节奏） |
| EmberDailyCryptWarden | 残誓守墓 | WITHER_SKELETON | 210 | 3 | 慢压光环 + ~8s 点名缓速；观感三别 |

对照庭院：僵尸 45/4、骷髅 35/4、蛮兵 180/2 → 本草案略抬血/Boss 伤，仍日常档。

## Display 撞名

去色明文与 `灰烬庭院*` / `余烬地窟*` **不撞**。色码：B `&c` · C `&8` · 庭院 `&a`。

## 加载

- 游玩服短重启；MythicMobs 成功加载 **47** 个怪物（+6）
- `ops.json` = `[]`

## 交总控

可接地图刷点 / DP `$kill`（Display 去色对齐上表）/ 菜单去灰。数值上线后可一次微调，禁多轮盲调。
