# STATUS · 短征 sx08 MM · 错层烬庭（D410）

- 时间：2026-10-10 07:23 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-short-dungeon-sx08-2026-10-10.md` §2.2/§2.4；总控批 A·M · D410 · DESIGN tip `2f4ff19d`
- 对照源：`EmberSx07` 数值档 + `EmberSx07TowerCast` / `EmberSx01SentryCast` 范式；刷点坐标沿用 **Q01 / sx01–sx07** 三房惯例
- 未做：旧七线 Daily 零改；**sx01–sx07 零改**；afk / Stage2 / gate_daily / 体力 / SoftHit 本体 / ×0.97 / 观察 / S47 经济 REG / 菜单八本选页接线 / jar（`short.sx08` 键交键号并行）；地图 WE 主题（骨架已本地拷 daily_v1 庭院）

## 人话

错层烬庭四只怪和层间砸落读条已进 MythicMobs；DP `EmberSx08` 仍走 CoreRpg 导演空壳（不重蹈 D391 DP wave FAIL）；刷点意向表按 Q01 坐标挂好，键名与 `short.sx08` 占位一致（Guard / Crossbow / Elite / Warden）。砸落无 SLOW，靠 message「退进下层栏或侧龛 / 下撤半层」+ r5 柱/环伤可读可躲。

## 改动

### 1) 新建 `plugins/MythicMobs/Mobs/EmberSx08.yml`

| ID | Display | Type | HP | 攻 | 房 | 备注 |
|----|---------|------|----|----|----|------|
| `EmberSx08Guard` | `&c庭卫` | ZOMBIE | **48** | **4** | R1 | SoftHit；死掉 shard；对照 sx07 Guard；设计「庭卫尸」 |
| `EmberSx08Crossbow` | `&c错层弩` | SKELETON | **32** | **4** | R1 | BOW 远程；键名 **Crossbow** 对齐设计「错层弩手」与 short.sx08 ranged |
| `EmberSx08Elite` | `&c错层督卫` | HUSK | **120** | **3** | R2 | SoftHit；mmxp elite；shard 0.6；设计 R2「途中 1 波错层怪」 |
| `EmberSx08Warden` | `&c上层守吏` | ZOMBIE | **180** | **3** | R3 | SoftHit + **LayerCast @80**；mmxp；core_fragment；shard 0.5；**无装备**；未抬过日更 Boss 210 |

**新 MM id**，禁复用 EmberSx01–07 本壳。

### 2) Skill `EmberSx08LayerCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【错层】层间砸落——退进下层栏或侧龛 / 下撤半层！」`@PlayersInRadius{r=24}` |
| 蓄力（上层垫/镂空边） | `flame` 竖柱 + `lava` 镂空边感 + `crit` 扫层（1.12.2 可用） |
| 音效 | `minecart.base` volume 0.7 pitch 0.6 |
| 读条 | `delay 25` |
| 结算（柱/环伤） | `damage 1.2` `@PlayersInRadius{r=5}`；**无 SLOW** |
| 结算粒子 | `flame` + `lava` + `crit` |
| Cooldown | **8** |
| 挂接 | `EmberSx08Warden` → `skill{s=EmberSx08LayerCast} @self ~onTimer:80` |

设计「层间砸落/扫层斩」：用**上层垫/镂空边粒子 + r5 环结算**；message 标明退进下层栏或侧龛 / 下撤半层。

### 3) DP / 地图 / 刷点

| 项 | 状态 |
|----|------|
| `plugins/DungeonPlus/dungeon/EmberSx08/` | 自 EmberSx07 同构新建；`monster.yml` idle + 意向挂表 Guard/Crossbow/Elite/Warden |
| `plugins/DungeonPlus/config.yml` | `ember_short_sx08` ↔ `EmberSx08` |
| `plugins/DungeonPlus/map/ember_short_sx08/` | 本地自 `ember_daily_v1` 拷（**gitignore**，不入 git；庭院气质骨架） |
| 出生 | `0,64,5`（option 已写） |

**意向刷点（Q01/sx01–sx07 惯例 · 键号 short.sx08）：**

| 房 | 组 | MM ID | Display | 坐标（本地） | 数量 |
|----|----|-------|---------|--------------|------|
| R1 | wave1 | `EmberSx08Guard` | 庭卫×3 | (-6,64,29)×2 · (0,64,29)×1 | 3 |
| R1 | wave1 | `EmberSx08Crossbow` | 错层弩×2 | (6,64,29)×1 · (9,64,28)×1 | 2 |
| R2 | wave2 | `EmberSx08Elite` | 错层督卫×1 | (30,64,39) | 1 |
| R3 | boss | `EmberSx08Warden` | 上层守吏×1 | (0,64,102) | 1 |

`mobs` 槽：`melee=Guard` · `ranged=Crossbow` · `elite=Elite` · `boss.mm=Warden`。  
**禁** DP `$mob` wave 自驱（D391 FAIL：Director 不进 FIGHTING / S47 不触发）。

### 4) 明确零改

- 霜/锈/庭/焦/潮/断/窖 七线 Daily Mobs/Cast
- `EmberSx01.yml` … `EmberSx07.yml` / 对应 Cast / DP
- 体力 / SoftHit 本体 / **afk** / **gate_daily** / **Stage2** / ×0.97 / 观察
- `ember-v1-runs.yml` / economy / 菜单（键号/经济号/菜单号另交）

## 热更

| 命令 | 时间（CST） | 结果 |
|------|-------------|------|
| FIFO `mm reload` | 07:23:12 | **145** 怪（+4）· **37** 技能（+1 Cast）· 97 ms |
| `mm mobs info EmberSx08Warden` | 07:23:16 | Display 上层守吏 · HP180 · Damage3 · 文件 EmberSx08.yml |
| `mm skills info EmberSx08LayerCast` | 07:23:18 | 命中 Skills/EmberDungeonDiffSkills.yml |
| `mm mobs info EmberSx08Guard` / `Crossbow` / `Elite` | 07:23:20–23 | 庭卫 HP48 · 错层弩 HP32 · 错层督卫 HP120 |
| FIFO `dp reload` | 07:23:25 | **插件重载完毕** · **`EmberSx08` 地牢内容初始化完毕** · `ember_short_sx08` 地图导入/初始化成功 |

- `plugins/` 与 `server-runtime/plugins/` 同路径（symlink）
- `login-runtime/ops.json` = `[]`；play ops 既有测试号（本档未改）
- 服务保持可玩；未长停服；无 Sx08 相关 ERROR（TrMenu `updateCommands` WARN 为 1.12 既有，非本档）

## 测岗建议（交键号 short.sx08 落地后）

`corerpg p1 enter sx08`：下层庭清 → 督卫 → 上层守吏 Cast「退进下层栏或侧龛 / 下撤半层」先于 r5 伤；圈内 ≈1.2；无 SLOW；七线 + sx01–sx07 零回归。

## 残留

- `ember-v1-runs.yml#short.sx08` rooms×3+boss / S47 / 菜单八本选页接线 → **DP+键号 / 经济号 / 菜单号**
- 地图主题 WE（下层庭/错层换层净高差 ≥1 整层/上层栏心烬台，下层栏或侧龛/半层下撤平台）→ 地图岗
