# STATUS · 短征 sx10 MM · 烬闸递室（D414）

- 时间：2026-10-10 08:07 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-short-dungeon-sx10-2026-10-10.md` §2.2/§2.4；总控批 A·M · D414 · DESIGN tip `e052caa9`
- 对照源：`EmberSx09` 数值档 + `EmberSx09CanalCast` / `EmberSx01SentryCast` 范式；刷点坐标沿用 **Q01 / sx01–sx09** 三房惯例
- 未做：旧七线 Daily 零改；**sx01–sx09 零改**；afk / Stage2 / gate_daily / 体力 / SoftHit 本体 / ×0.97 / 观察 / S49 经济 REG / 菜单十本选页接线 / jar（`short.sx10` 键交键号并行）；地图 WE 主题（骨架已本地拷，外闸/递闸/末室 WE 另号）

## 人话

烬闸递室四只怪和闸心压浪读条已进 MythicMobs；DP `EmberSx10` 仍走 CoreRpg 导演空壳（不重蹈 D391 DP wave FAIL）；刷点意向表按 Q01 坐标挂好，键名与 `short.sx10` 占位一致（Guard / Crossbow / Elite / Warden）。压浪无 SLOW，靠 message「退进室侧龛或回撤闸后」+ r5 柱/环伤可读可躲。

## 改动

### 1) 新建 `plugins/MythicMobs/Mobs/EmberSx10.yml`

| ID | Display | Type | HP | 攻 | 房 | 备注 |
|----|---------|------|----|----|----|------|
| `EmberSx10Guard` | `&c闸卫` | ZOMBIE | **48** | **4** | R1 | SoftHit；死掉 shard；对照 sx09 Guard；设计「闸卫尸」 |
| `EmberSx10Crossbow` | `&c闸室弩` | SKELETON | **32** | **4** | R1 | BOW 远程；键名 **Crossbow** 对齐设计「闸室弩手」与 short.sx10 ranged |
| `EmberSx10Elite` | `&c闸室督卫` | HUSK | **120** | **3** | R2 | SoftHit；mmxp elite；shard 0.6；设计 R2「途中 1 波闸室怪或闸弩」 |
| `EmberSx10Warden` | `&c末室守吏` | ZOMBIE | **180** | **3** | R3 | SoftHit + **GateCast @80**；mmxp；core_fragment；shard 0.5；**无装备**；未抬过日更 Boss 210 |

**新 MM id**，禁复用 EmberSx01–09 本壳。

### 2) Skill `EmberSx10GateCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【烬闸】闸心压浪——退进室侧龛或回撤闸后！」`@PlayersInRadius{r=24}` |
| 蓄力（闸心/室缘） | `cloud` 压浪雾 + `lava` 烬感 + `crit` 扫室（1.12.2 可用） |
| 音效 | `minecart.base` volume 0.7 pitch 0.5 |
| 读条 | `delay 25` |
| 结算（柱/环伤） | `damage 1.2` `@PlayersInRadius{r=5}`；**无 SLOW** |
| 结算粒子 | `cloud` + `lava` + `crit` |
| Cooldown | **8** |
| 挂接 | `EmberSx10Warden` → `skill{s=EmberSx10GateCast} @self ~onTimer:80` |

设计「闸心压浪/扫室斩」：用**闸心/室缘粒子 + r5 环结算**；message 标明退进室侧龛或回撤上一道闸后平台。

### 3) DP / 地图 / 刷点

| 项 | 状态 |
|----|------|
| `plugins/DungeonPlus/dungeon/EmberSx10/` | 自 EmberSx09 同构（并行地图岗已入仓；本号对齐 MM id 注释） |
| `plugins/DungeonPlus/config.yml` | `ember_short_sx10` ↔ `EmberSx10`（与并行地图岗登记对齐） |
| `plugins/DungeonPlus/map/ember_short_sx10/` | 本地骨架自 **ember_raid**（**gitignore**，不入 git；外闸/递闸/末室 WE 另号） |
| 出生 | `0,64,5`（option 已写） |

**意向刷点（Q01/sx01–sx09 惯例 · 键号 short.sx10）：**

| 房 | 组 | MM ID | Display | 坐标（本地） | 数量 |
|----|----|-------|---------|--------------|------|
| R1 | wave1 | `EmberSx10Guard` | 闸卫×3 | (-6,64,29)×2 · (0,64,29)×1 | 3 |
| R1 | wave1 | `EmberSx10Crossbow` | 闸室弩×2 | (6,64,29)×1 · (9,64,28)×1 | 2 |
| R2 | wave2 | `EmberSx10Elite` | 闸室督卫×1 | (30,64,39) | 1 |
| R3 | boss | `EmberSx10Warden` | 末室守吏×1 | (0,64,102) | 1 |

`mobs` 槽：`melee=Guard` · `ranged=Crossbow` · `elite=Elite` · `boss.mm=Warden`。  
**禁** DP `$mob` wave 自驱（D391 FAIL：Director 不进 FIGHTING / S49 不触发）。

### 4) 明确零改

- 霜/锈/庭/焦/潮/断/窖 七线 Daily Mobs/Cast
- `EmberSx01.yml` … `EmberSx09.yml` / 对应 Cast / DP
- 体力 / SoftHit 本体 / **afk** / **gate_daily** / **Stage2** / ×0.97 / 观察
- `ember-v1-runs.yml` / economy / 菜单（键号/经济号/菜单号另交）

## 热更

| 命令 | 时间（CST） | 结果 |
|------|-------------|------|
| FIFO `mm reload` | 08:07:36 | **153** 怪（+4）· **39** 技能（+1 Cast）· 109 ms |
| `mm mobs info EmberSx10Warden` | 08:07:41 | Display 末室守吏 · HP180 · Damage3 · 文件 EmberSx10.yml |
| `mm skills info EmberSx10GateCast` | 08:07:43 | 命中 Skills/EmberDungeonDiffSkills.yml |
| `mm mobs info EmberSx10Guard` / `Crossbow` / `Elite` | 08:07:45–48 | 闸卫 HP48 · 闸室弩 HP32 · 闸室督卫 HP120 |
| FIFO `dp reload` | 08:07:49 | **插件重载完毕** · **`EmberSx10` 地牢内容初始化完毕** · `ember_short_sx10` 地图导入/初始化成功 |
| `login-runtime/ops.json` | 08:07 | `[]`（本档未改） |

## 残留

- `ember-v1-runs.yml#short.sx10` rooms×3+boss / S49 / 菜单十本选页接线 → **DP+键号 / 经济号 / 菜单号**
- 地图主题 WE（外闸庭 / ≥3 道重闸+密封室体可读 / 末室栏心烬台 + 室侧龛或回撤闸后平台）→ 地图岗
