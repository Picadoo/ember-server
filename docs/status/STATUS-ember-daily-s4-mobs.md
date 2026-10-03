# STATUS · S4 日常线 F/G 怪物 MM 草案

- 时间：2026-09-28（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/design-ember-daily-s4.md` §4.4 / §5.4；总控派工 S4（设计已批准 `b4a1b21`）
- 未做：DP / 地图 / 菜单 / Paper / 体力数值；S1～S3 日怪未改；**未 commit/push**
- 口径：对齐 S3 日常量级一次估好（通关约 5～8 分钟）；掉落 `corerpg mmgive` 轻量日常级

## 新文件

| 文件 | 线 |
|------|-----|
| `plugins/MythicMobs/Mobs/EmberDailyFrost.yml` | F 霜晶裂隙 |
| `plugins/MythicMobs/Mobs/EmberDailyRail.yml` | G 锈轨矿道 |

## 规格

### 线 F（冻台友好 · 霜厅慢压）

| MM ID | Display（去色） | Type | Health | Damage | 备注 |
|-------|-----------------|------|--------|--------|------|
| EmberDailyFrostZombie | 霜晶尸 | ZOMBIE | 51 | 4 | SoftHit + 轻减速 |
| EmberDailyFrostSkeleton | 霜矢骷 | SKELETON | 40 | 4 | **弓**可贴脸；移速 0.27；`PreventRandomEquipment` |
| EmberDailyFrostBrute | 霜核蛮兵 | HUSK | 210 | 3 | 冻步减速光环 + 轻伤；`mmxp elite` |

### 线 G（巷道 · 机房压迫）

| MM ID | Display（去色） | Type | Health | Damage | 备注 |
|-------|-----------------|------|--------|--------|------|
| EmberDailyRailZombie | 锈轨尸 | ZOMBIE | 50 | 4 | SoftHit |
| EmberDailyRailSkeleton | 矿矢骷 | SKELETON | 40 | 4 | **弓**可贴脸；`PreventRandomEquipment` |
| EmberDailyRailWarden | 锈轨矿监 | ZOMBIE | 210 | 3 | 铁镐近战；SoftHit 偏高 + 近距光环；`mmxp elite` |

对照 S3：潮蚀 52/4·41/4·205/3；断塔 50/4·39/4·215/3 → 本草案同档。

## Display / ID 撞名

- 去色明文不撞：庭院 / 焦骨 / 窖卫·誓印·残誓 / 潮蚀·浪矢·潮闸 / 断塔·裂隙·守望
- ID 前缀 `EmberDailyFrost*` / `EmberDailyRail*`
- 色码：F `&b` · G `&6` · 潮蚀 `&3` · 断塔 `&7` · 庭院 `&a` · 焦骨 `&c` · 地窖 `&8`

## 加载

- 游玩服短重启；MythicMobs 成功加载 **59** 个怪物（+6，相对 S3 的 53）
- `ops.json` = `[]`

## 交总控

可接地图刷点 / DP `$kill`（Display 去色对齐上表）/ 菜单 F/G。勿改体力表。本岗未 push。
