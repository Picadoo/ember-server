# STATUS · 短征 sx07 MM · 烬塔回升（D407）

- 时间：2026-10-10 07:05 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-short-dungeon-sx07-2026-10-10.md` §2.2/§2.4；总控批 A·M · D407
- 对照源：`EmberSx06` 数值档 + `EmberSx06RingCast` / `EmberSx01SentryCast` 范式；刷点坐标沿用 **Q01 / sx01–sx06** 三房惯例
- 未做：旧七线 Daily 零改；**sx01–sx06 零改**；体力 / SoftHit 本体 / ×0.97 / 观察 / S46 经济 REG / 菜单七本选页接线 / jar（`short.sx07` 键交键号并行）；地图 WE 主题（骨架已本地拷 spire_v1）

## 人话

烬塔回升四只怪和塔冠坠焰读条已进 MythicMobs；DP `EmberSx07` 仍走 CoreRpg 导演空壳（不重蹈 D391 DP wave FAIL）；刷点意向表按 Q01 坐标挂好，键名与 `short.sx07` 占位一致（Guard / Crossbow / Elite / Warden）。坠焰无 SLOW，靠 message「退进侧窗廊龛或下撤半层」+ r5 柱/环伤可读可躲。

## 改动

### 1) 新建 `plugins/MythicMobs/Mobs/EmberSx07.yml`

| ID | Display | Type | HP | 攻 | 房 | 备注 |
|----|---------|------|----|----|----|------|
| `EmberSx07Guard` | `&c塔卫` | ZOMBIE | **48** | **4** | R1 | SoftHit；死掉 shard；对照 sx06 Guard |
| `EmberSx07Crossbow` | `&c窗廊弩` | SKELETON | **32** | **4** | R1 | BOW 远程；键名 **Crossbow** 对齐设计「窗廊弩」与 short.sx07 ranged |
| `EmberSx07Elite` | `&c塔廊督卫` | HUSK | **120** | **3** | R2 | SoftHit；mmxp elite；shard 0.6 |
| `EmberSx07Warden` | `&c塔冠守吏` | ZOMBIE | **180** | **3** | R3 | SoftHit + **TowerCast @80**；mmxp；core_fragment；shard 0.5；**无装备**；未抬过日更 Boss 210 |

### 2) Skill `EmberSx07TowerCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【烬塔】塔冠坠焰——退进侧窗廊龛或下撤半层！」`@PlayersInRadius{r=24}` |
| 蓄力（塔心/顶缘） | `flame` 竖柱 + `lava` 坠焰感 + `crit` 扫顶（1.12.2 可用） |
| 音效 | `minecart.base` volume 0.7 pitch 0.55 |
| 读条 | `delay 25` |
| 结算（柱/环伤） | `damage 1.2` `@PlayersInRadius{r=5}`；**无 SLOW** |
| 结算粒子 | `flame` + `lava` + `crit` |
| Cooldown | **8** |
| 挂接 | `EmberSx07Warden` → `skill{s=EmberSx07TowerCast} @self ~onTimer:80` |

设计「塔冠坠焰/扫顶柱」：用**塔心/顶缘粒子竖柱 + r5 环结算**；message 标明退进侧窗廊龛或下撤半层。

### 3) DP / 地图 / 刷点

| 项 | 状态 |
|----|------|
| `plugins/DungeonPlus/dungeon/EmberSx07/` | 自 EmberSx06 同构新建；`monster.yml` idle + 意向挂表 Guard/Crossbow/Elite/Warden |
| `plugins/DungeonPlus/config.yml` | `ember_short_sx07` ↔ `EmberSx07` |
| `plugins/DungeonPlus/map/ember_short_sx07/` | 本地自 `ember_daily_spire_v1` 拷（**gitignore**，不入 git；塔升气质骨架） |
| 出生 | `0,64,5`（option 已写） |

**意向刷点（Q01/sx01–sx06 惯例 · 键号 short.sx07）：**

| 房 | 组 | MM ID | Display | 坐标（本地） | 数量 |
|----|----|-------|---------|--------------|------|
| R1 | wave1 | `EmberSx07Guard` | 塔卫×3 | (-6,64,29)×2 · (0,64,29)×1 | 3 |
| R1 | wave1 | `EmberSx07Crossbow` | 窗廊弩×2 | (6,64,29)×1 · (9,64,28)×1 | 2 |
| R2 | wave2 | `EmberSx07Elite` | 塔廊督卫×1 | (30,64,39) | 1 |
| R3 | boss | `EmberSx07Warden` | 塔冠守吏×1 | (0,64,102) | 1 |

`mobs` 槽：`melee=Guard` · `ranged=Crossbow` · `elite=Elite` · `boss.mm=Warden`。  
**禁** DP `$mob` wave 自驱（D391 FAIL：Director 不进 FIGHTING / S46 不触发）。

### 4) 明确零改

- 霜/锈/庭/焦/潮/断/窖 七线 Daily Mobs/Cast
- `EmberSx01.yml` … `EmberSx06.yml` / 对应 Cast / DP
- 体力 / SoftHit 本体 / afk / gate_daily / ×0.97 / 观察
- `ember-v1-runs.yml` / economy / 菜单（键号/经济号/菜单号另交）

## 热更

| 命令 | 时间（CST） | 结果 |
|------|-------------|------|
| FIFO `mm reload` | 07:04:29 | **141** 怪（+4）· **36** 技能（+1 Cast）· 97 ms |
| `mm mobs info EmberSx07Warden` | 07:04:37 | Display 塔冠守吏 · HP180 · Damage3 · 文件 EmberSx07.yml |
| `mm skills info EmberSx07TowerCast` | 07:04:40 | 命中 Skills/EmberDungeonDiffSkills.yml |
| `mm mobs info EmberSx07Guard` / `Crossbow` / `Elite` | 07:04:43–47 | 塔卫 HP48 · 窗廊弩 HP32 · 塔廊督卫 HP120 |
| FIFO `dp reload` | 07:04:49 | **插件重载完毕** · **`EmberSx07` 地牢内容初始化完毕** · `ember_short_sx07` 地图导入/初始化成功 |

- `plugins/` 与 `server-runtime/plugins/` 同路径（symlink）
- `login-runtime/ops.json` = `[]`；play ops 既有测试号（本档未改）
- 服务保持可玩；未长停服；无 Sx07 相关 ERROR

## 测岗建议（交键号 short.sx07 落地后）

`corerpg p1 enter sx07`：塔基庭清 → 督卫 → 塔冠守吏 Cast「退进侧窗廊龛或下撤半层」先于 r5 伤；圈内 ≈1.2；无 SLOW；七线 + sx01–sx06 零回归。

## 残留

- `ember-v1-runs.yml#short.sx07` rooms×3+boss / S46 / 菜单七本选页接线 → **DP+键号 / 经济号 / 菜单号**
- 地图主题 WE（塔基庭/折返上行 ≥2 整层/塔冠烬台，侧窗廊龛或半层下撤平台）→ 地图岗
