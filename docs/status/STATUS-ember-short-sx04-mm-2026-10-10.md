# STATUS · 短征 sx04 MM · 烬井螺旋（D397）

- 时间：2026-10-10 06:01 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-short-dungeon-sx04-2026-10-10.md` §2.2/§2.4；总控批 A·M @`0ffa02f2`
- 对照源：`EmberSx03` 数值档 + `EmberAshBurstCast` / `EmberSx01SentryCast` 范式；刷点坐标沿用 **Q01 / sx01–sx03** 三房惯例
- 未做：旧七线 Daily 零改；**sx01/sx02/sx03 零改**；体力 / SoftHit 本体 / ×0.97 / 观察 / S43 经济 REG / 菜单四本选页 / jar（`short.sx04` 键交键号并行）

## 人话

烬井螺旋四只怪和井心喷焰读条已进 MythicMobs；DP `EmberSx04` 仍走 CoreRpg 导演空壳（不重蹈 D391 DP wave FAIL）；刷点意向表按 Q01 坐标挂好，键名与 `short.sx04` 占位一致（Guard / Crossbow / Elite / Warden）。

## 改动

### 1) 新建 `plugins/MythicMobs/Mobs/EmberSx04.yml`

| ID | Display | Type | HP | 攻 | 房 | 备注 |
|----|---------|------|----|----|----|------|
| `EmberSx04Guard` | `&c井卫` | ZOMBIE | **48** | **4** | R1 | SoftHit；死掉 shard；对照 sx03 Patrol |
| `EmberSx04Crossbow` | `&c螺旋弩` | SKELETON | **32** | **4** | R1 | BOW 远程；键名 **Crossbow** 对齐设计「螺旋弩」与 short.sx04 ranged（风格对齐 sx03） |
| `EmberSx04Elite` | `&c井阶督卫` | HUSK | **120** | **3** | R2 | SoftHit；mmxp elite；shard 0.6 |
| `EmberSx04Warden` | `&c炉心井守` | ZOMBIE | **180** | **3** | R3 | SoftHit + **FurnaceCast @80**；mmxp；core_fragment；shard 0.5；**无装备**；未抬过日更 Boss 210 |

### 2) Skill `EmberSx04FurnaceCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【烬井】井心喷焰——踏上井沿或拉开！」`@PlayersInRadius{r=24}` |
| 蓄力（井心柱） | `lava` 窄幅竖柱 + `flame` 竖柱 + `crit` 环示意（1.12.2 可用） |
| 音效 | `minecart.base` volume 0.7 pitch 0.5 |
| 读条 | `delay 25` |
| 结算（喷涌环） | `damage 1.2` `@PlayersInRadius{r=5}`；**无 SLOW**（炉心喷涌按设计无/极轻，本档选无） |
| 结算粒子 | `lava` + `flame` |
| Cooldown | **8** |
| 挂接 | `EmberSx04Warden` → `skill{s=EmberSx04FurnaceCast} @self ~onTimer:80` |

设计「井心喷焰/灰柱」：1.12 无可靠竖柱伤害原语时，用**竖向 lava/flame 蓄力柱 + r5 环结算**；message 标明踏上井沿或拉开。

### 3) DP / 地图 / 刷点

| 项 | 状态 |
|----|------|
| `plugins/DungeonPlus/dungeon/EmberSx04/` | 新建骨架（option/monster idle/obstacle；自 Sx03 同构） |
| `plugins/DungeonPlus/config.yml` | `ember_short_sx04` ↔ `EmberSx04`（本号登记） |
| `plugins/DungeonPlus/map/ember_short_sx04/` | 本地自 `ember_daily_ash_v1` 拷（**gitignore**，不入 git；炉心气质骨架） |
| 出生 | `0,64,5`（option 已写） |

**意向刷点（Q01/sx01–sx03 惯例 · 键号 short.sx04）：**

| 房 | 组 | MM ID | Display | 坐标（本地） | 数量 |
|----|----|-------|---------|--------------|------|
| R1 | wave1 | `EmberSx04Guard` | 井卫×3 | (-6,64,29)×2 · (0,64,29)×1 | 3 |
| R1 | wave1 | `EmberSx04Crossbow` | 螺旋弩×2 | (6,64,29)×1 · (9,64,28)×1 | 2 |
| R2 | wave2 | `EmberSx04Elite` | 井阶督卫×1 | (30,64,39) | 1 |
| R3 | boss | `EmberSx04Warden` | 炉心井守×1 | (0,64,102) | 1 |

`mobs` 槽：`melee=Guard` · `ranged=Crossbow` · `elite=Elite` · `boss.mm=Warden`。  
**禁** DP `$mob` wave 自驱（D391 FAIL：Director 不进 FIGHTING / S43 不触发）。

### 4) 明确零改

- 霜/锈/庭/焦/潮/断/窖 七线 Daily Mobs/Cast
- `EmberSx01.yml` / `EmberSx02.yml` / `EmberSx03.yml` / 对应 Cast / DP
- 体力 / SoftHit 本体 / afk / gate_daily / ×0.97 / 观察
- `ember-v1-runs.yml` / economy / 菜单（键号/经济号/菜单号另交）

## 热更

| 命令 | 时间（CST） | 结果 |
|------|-------------|------|
| FIFO `mm reload` | 06:01:55 | **129** 怪（+4）· **33** 技能（+1 Cast）· 109 ms |
| `mm mobs info EmberSx04Warden` | 06:02:00 | Display 炉心井守 · HP180 · Damage3 · 文件 EmberSx04.yml |
| `mm skills info EmberSx04FurnaceCast` | 06:02:02 | 命中 Skills/EmberDungeonDiffSkills.yml |
| `mm mobs info EmberSx04Guard` / `Crossbow` | 06:02:04–05 | 井卫 HP48 · 螺旋弩 HP32 |
| FIFO `dp reload` | 06:02:07 | **插件重载完毕** · **`EmberSx04` 地牢内容初始化完毕** · `ember_short_sx04` 地图导入成功 |

- `plugins/` 与 `server-runtime/plugins/` 目标文件同 inode（无需另拷）
- `login-runtime/ops.json` = `[]`；play ops 既有测试号（本档未改）
- 服务保持可玩；未长停服；无 Sx04 相关 ERROR

## 测岗建议（交键号 short.sx04 落地后）

`corerpg p1 enter sx04`：井口清 → 督卫 → 炉心井守 Cast「踏上井沿或拉开」先于 r5 伤；圈内 ≈1.2；无 SLOW；七线 + sx01–sx03 零回归。

## 残留

- `ember-v1-runs.yml#short.sx04` rooms×3+boss / S43 / 菜单四本选页 → **DP+键号 / 经济号 / 菜单号**
- 地图主题 WE（井口台/螺旋下井/炉心垫，井深≥8～12）→ 地图岗
