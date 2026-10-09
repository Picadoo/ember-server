# STATUS · 短征 sx05 MM · 裂谷风廊（D400）

- 时间：2026-10-10 06:19 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-short-dungeon-sx05-2026-10-10.md` §2.2/§2.4；总控批 A·M · D400
- 对照源：`EmberSx04` 数值档 + `EmberSx01SentryCast` / `EmberSx04FurnaceCast` 范式；刷点坐标沿用 **Q01 / sx01–sx04** 三房惯例
- 未做：旧七线 Daily 零改；**sx01–sx04 零改**；体力 / SoftHit 本体 / ×0.97 / 观察 / S44 经济 REG / 菜单五本选页接线 / jar（`short.sx05` 键交键号并行）；地图 WE 主题（骨架已由 D400 地图号启）

## 人话

裂谷风廊四只怪和风剪条读条已进 MythicMobs；DP `EmberSx05` 仍走 CoreRpg 导演空壳（不重蹈 D391 DP wave FAIL）；刷点意向表按 Q01 坐标挂好，键名与 `short.sx05` 占位一致（Guard / Crossbow / Elite / Warden）。

## 改动

### 1) 新建 `plugins/MythicMobs/Mobs/EmberSx05.yml`

| ID | Display | Type | HP | 攻 | 房 | 备注 |
|----|---------|------|----|----|----|------|
| `EmberSx05Guard` | `&c崖卫` | ZOMBIE | **48** | **4** | R1 | SoftHit；死掉 shard；对照 sx04 Guard |
| `EmberSx05Crossbow` | `&c风廊弩` | SKELETON | **32** | **4** | R1 | BOW 远程；键名 **Crossbow** 对齐设计「风廊弩」与 short.sx05 ranged |
| `EmberSx05Elite` | `&c风廊督卫` | HUSK | **120** | **3** | R2 | SoftHit；mmxp elite；shard 0.6 |
| `EmberSx05Warden` | `&c风心守吏` | ZOMBIE | **180** | **3** | R3 | SoftHit + **WindCast @80**；mmxp；core_fragment；shard 0.5；**无装备**；未抬过日更 Boss 210 |

### 2) Skill `EmberSx05WindCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【裂谷】风剪条——退进侧龛或离开条带！」`@PlayersInRadius{r=24}` |
| 蓄力（桥面风条） | `cloud` 宽幅桥面风压 + `crit` 条带 + `explode` 侧斩示意（1.12.2 可用） |
| 音效 | `minecart.base` volume 0.65 pitch 1.1 |
| 读条 | `delay 25` |
| 结算（条伤） | `damage 1.2` `@PlayersInRadius{r=5}`；**无 SLOW**（风剪按设计通常无） |
| 结算粒子 | `cloud` + `crit` + `explode` |
| Cooldown | **8** |
| 挂接 | `EmberSx05Warden` → `skill{s=EmberSx05WindCast} @self ~onTimer:80` |

设计「风剪条/侧风斩」：1.12 无可靠窄条伤害原语时，用**桥面宽幅粒子条 + r5 环结算**；message 标明退进侧龛或离开条带。

### 3) DP / 地图 / 刷点

| 项 | 状态 |
|----|------|
| `plugins/DungeonPlus/dungeon/EmberSx05/` | 骨架由 D400 地图号先落；本号修正 `monster.yml` 意向挂表为 Guard/Crossbow/Elite/Warden |
| `plugins/DungeonPlus/config.yml` | `ember_short_sx05` ↔ `EmberSx05`（地图号已登记） |
| `plugins/DungeonPlus/map/ember_short_sx05/` | 本地自 `ember_daily_crypt_v1` 拷（**gitignore**，不入 git；裂谷气质骨架） |
| 出生 | `0,64,5`（option 已写） |

**意向刷点（Q01/sx01–sx04 惯例 · 键号 short.sx05）：**

| 房 | 组 | MM ID | Display | 坐标（本地） | 数量 |
|----|----|-------|---------|--------------|------|
| R1 | wave1 | `EmberSx05Guard` | 崖卫×3 | (-6,64,29)×2 · (0,64,29)×1 | 3 |
| R1 | wave1 | `EmberSx05Crossbow` | 风廊弩×2 | (6,64,29)×1 · (9,64,28)×1 | 2 |
| R2 | wave2 | `EmberSx05Elite` | 风廊督卫×1 | (30,64,39) | 1 |
| R3 | boss | `EmberSx05Warden` | 风心守吏×1 | (0,64,102) | 1 |

`mobs` 槽：`melee=Guard` · `ranged=Crossbow` · `elite=Elite` · `boss.mm=Warden`。  
**禁** DP `$mob` wave 自驱（D391 FAIL：Director 不进 FIGHTING / S44 不触发）。

### 4) 明确零改

- 霜/锈/庭/焦/潮/断/窖 七线 Daily Mobs/Cast
- `EmberSx01.yml` / `EmberSx02.yml` / `EmberSx03.yml` / `EmberSx04.yml` / 对应 Cast / DP
- 体力 / SoftHit 本体 / afk / gate_daily / ×0.97 / 观察
- `ember-v1-runs.yml` / economy / 菜单（键号/经济号/菜单号另交）

## 热更

| 命令 | 时间（CST） | 结果 |
|------|-------------|------|
| FIFO `mm reload` | 06:19:36 | **133** 怪（+4）· **34** 技能（+1 Cast）· 160 ms |
| `mm mobs info EmberSx05Warden` | 06:19:39 | Display 风心守吏 · HP180 · Damage3 · 文件 EmberSx05.yml |
| `mm skills info EmberSx05WindCast` | 06:19:41 | 命中 Skills/EmberDungeonDiffSkills.yml |
| `mm mobs info EmberSx05Guard` / `Crossbow` / `Elite` | 06:19:43–54 | 崖卫 HP48 · 风廊弩 HP32 · 风廊督卫 HP120 |
| FIFO `dp reload` | 06:19:46 | **插件重载完毕** · **`EmberSx05` 地牢内容初始化完毕** · `ember_short_sx05` 地图导入成功 |

- `plugins/` 与 `server-runtime/plugins/` 目标文件同 inode（无需另拷）
- `login-runtime/ops.json` = `[]`；play ops 既有测试号（本档未改）
- 服务保持可玩；未长停服；无 Sx05 相关 ERROR

## 测岗建议（交键号 short.sx05 落地后）

`corerpg p1 enter sx05`：崖口清 → 督卫 → 风心守吏 Cast「退进侧龛或离开条带」先于 r5 伤；圈内 ≈1.2；无 SLOW；七线 + sx01–sx04 零回归。

## 残留

- `ember-v1-runs.yml#short.sx05` rooms×3+boss / S44 / 菜单五本选页接线 → **DP+键号 / 经济号 / 菜单号**
- 地图主题 WE（崖口台/窄桥过隙/对岸风心垫，空隙可读）→ 地图岗
