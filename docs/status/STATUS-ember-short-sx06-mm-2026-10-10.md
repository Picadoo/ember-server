# STATUS · 短征 sx06 MM · 烬环廊（D403）

- 时间：2026-10-10 06:37 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-short-dungeon-sx06-2026-10-10.md` §2.2/§2.4；总控批 A·M · D403
- 对照源：`EmberSx05` 数值档 + `EmberSx05WindCast` / `EmberSx01SentryCast` 范式；刷点坐标沿用 **Q01 / sx01–sx05** 三房惯例
- 未做：旧七线 Daily 零改；**sx01–sx05 零改**；体力 / SoftHit 本体 / ×0.97 / 观察 / S45 经济 REG / 菜单六本选页接线 / jar（`short.sx06` 键交键号并行）；地图 WE 主题（骨架已本地拷 crypt_v1）；复杂向心 pull（1.12 jar 不稳，优先可读伤害圈）

## 人话

烬环廊四只怪和环扫斩读条已进 MythicMobs；DP `EmberSx06` 仍走 CoreRpg 导演空壳（不重蹈 D391 DP wave FAIL）；刷点意向表按 Q01 坐标挂好，键名与 `short.sx06` 占位一致（Guard / Crossbow / Elite / Warden）。环扫不挂复杂 pull，靠 message「退进外环龛或离开弧带」+ r5 弧伤可读可躲。

## 改动

### 1) 新建 `plugins/MythicMobs/Mobs/EmberSx06.yml`

| ID | Display | Type | HP | 攻 | 房 | 备注 |
|----|---------|------|----|----|----|------|
| `EmberSx06Guard` | `&c环卫` | ZOMBIE | **48** | **4** | R1 | SoftHit；死掉 shard；对照 sx05 Guard |
| `EmberSx06Crossbow` | `&c环廊弩` | SKELETON | **32** | **4** | R1 | BOW 远程；键名 **Crossbow** 对齐设计「环廊弩」与 short.sx06 ranged |
| `EmberSx06Elite` | `&c环廊督卫` | HUSK | **120** | **3** | R2 | SoftHit；mmxp elite；shard 0.6 |
| `EmberSx06Warden` | `&c环心守吏` | ZOMBIE | **180** | **3** | R3 | SoftHit + **RingCast @80**；mmxp；core_fragment；shard 0.5；**无装备**；未抬过日更 Boss 210 |

### 2) Skill `EmberSx06RingCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【烬环】环扫斩——退进外环龛或离开弧带！」`@PlayersInRadius{r=24}` |
| 蓄力（环心粒子弧） | `flame` 环心弧 + `lava` 烬感 + `crit` 扫线（1.12.2 可用） |
| 音效 | `minecart.base` volume 0.65 pitch 0.85 |
| 读条 | `delay 25` |
| 结算（弧伤） | `damage 1.2` `@PlayersInRadius{r=5}`；**无 SLOW**；**无 pull**（优先可读圈） |
| 结算粒子 | `flame` + `lava` + `crit` |
| Cooldown | **8** |
| 挂接 | `EmberSx06Warden` → `skill{s=EmberSx06RingCast} @self ~onTimer:80` |

设计「环扫斩/向心拉扯」：1.12 无可靠 pull 原语时，用**环心粒子弧 + r5 环结算**；message 标明退进外环龛或离开弧带。

### 3) DP / 地图 / 刷点

| 项 | 状态 |
|----|------|
| `plugins/DungeonPlus/dungeon/EmberSx06/` | 自 EmberSx05 同构新建；`monster.yml` idle + 意向挂表 Guard/Crossbow/Elite/Warden |
| `plugins/DungeonPlus/config.yml` | `ember_short_sx06` ↔ `EmberSx06` |
| `plugins/DungeonPlus/map/ember_short_sx06/` | 本地自 `ember_daily_crypt_v1` 拷（**gitignore**，不入 git；环廊气质骨架） |
| 出生 | `0,64,5`（option 已写） |

**意向刷点（Q01/sx01–sx05 惯例 · 键号 short.sx06）：**

| 房 | 组 | MM ID | Display | 坐标（本地） | 数量 |
|----|----|-------|---------|--------------|------|
| R1 | wave1 | `EmberSx06Guard` | 环卫×3 | (-6,64,29)×2 · (0,64,29)×1 | 3 |
| R1 | wave1 | `EmberSx06Crossbow` | 环廊弩×2 | (6,64,29)×1 · (9,64,28)×1 | 2 |
| R2 | wave2 | `EmberSx06Elite` | 环廊督卫×1 | (30,64,39) | 1 |
| R3 | boss | `EmberSx06Warden` | 环心守吏×1 | (0,64,102) | 1 |

`mobs` 槽：`melee=Guard` · `ranged=Crossbow` · `elite=Elite` · `boss.mm=Warden`。  
**禁** DP `$mob` wave 自驱（D391 FAIL：Director 不进 FIGHTING / S45 不触发）。

### 4) 明确零改

- 霜/锈/庭/焦/潮/断/窖 七线 Daily Mobs/Cast
- `EmberSx01.yml` … `EmberSx05.yml` / 对应 Cast / DP
- 体力 / SoftHit 本体 / afk / gate_daily / ×0.97 / 观察
- `ember-v1-runs.yml` / economy / 菜单（键号/经济号/菜单号另交）

## 热更

| 命令 | 时间（CST） | 结果 |
|------|-------------|------|
| FIFO `mm reload` | 06:36:45 | **137** 怪（+4）· **35** 技能（+1 Cast）· 94 ms |
| `mm mobs info EmberSx06Warden` | 06:36:50 | Display 环心守吏 · HP180 · Damage3 · 文件 EmberSx06.yml |
| `mm skills info EmberSx06RingCast` | 06:36:52 | 命中 Skills/EmberDungeonDiffSkills.yml |
| `mm mobs info EmberSx06Guard` / `Crossbow` / `Elite` | 06:36:53–56 | 环卫 HP48 · 环廊弩 HP32 · 环廊督卫 HP120 |
| FIFO `dp reload` | 06:36:59 | **插件重载完毕** · **`EmberSx06` 地牢内容初始化完毕** · `ember_short_sx06` 地图导入成功 |

- `plugins/` 与 `server-runtime/plugins/` 同路径（symlink）
- `login-runtime/ops.json` = `[]`；play ops 既有测试号（本档未改）
- 服务保持可玩；未长停服；无 Sx06 相关 ERROR（TrMenu `updateCommands` 既有 1.12 噪音，非本号）

## 测岗建议（交键号 short.sx06 落地后）

`corerpg p1 enter sx06`：外环庭清 → 督卫 → 环心守吏 Cast「退进外环龛或离开弧带」先于 r5 伤；圈内 ≈1.2；无 SLOW；无强拉；七线 + sx01–sx05 零回归。

## 残留

- `ember-v1-runs.yml#short.sx06` rooms×3+boss / S45 / 菜单六本选页接线 → **DP+键号 / 经济号 / 菜单号**
- 地图主题 WE（外环庭/双环廊绕行/环心烬台，封心深坑可读）→ 地图岗
