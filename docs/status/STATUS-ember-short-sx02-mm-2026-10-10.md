# STATUS · 短征 sx02 MM · 锈灯栈道（D392）

- 时间：2026-10-10 05:21 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-short-dungeon-sx02-2026-10-10.md` §2.2/§2.4；总控批 A·M @`5bb2c41c`
- 对照源：`EmberSx01` 数值档 + `EmberFrostNovaCast` / `EmberSx01SentryCast` 范式；刷点坐标沿用 **Q01 / sx01** 三房惯例
- 未做：旧七线 Daily 零改；**sx01 零改**；体力 / SoftHit 本体 / ×0.97 / 观察 / S41 经济 / 菜单 / `ember-v1-runs` short.sx02 键（交 DP+键号）

## 人话

锈灯栈道四只怪和灯塔扫线读条已进 MythicMobs；DP `EmberSx02` 仍走 CoreRpg 导演空壳（不重蹈 D391 DP wave FAIL）；刷点意向表按 Q01 坐标挂好，等键号写入 `short.sx02`。

## 改动

### 1) 新建 `plugins/MythicMobs/Mobs/EmberSx02.yml`

| ID | Display | Type | HP | 攻 | 房 | 备注 |
|----|---------|------|----|----|----|------|
| `EmberSx02Guard` | `&e锈灯巡卫` | ZOMBIE | **48** | **4** | R1 | SoftHit；死掉 shard；对照 sx01 45 略厚 |
| `EmberSx02Archer` | `&e栈道弩手` | SKELETON | **32** | **4** | R1 | BOW 远程；死掉 bone_dust；对照 sx01 35 略薄 |
| `EmberSx02Elite` | `&e栈道督卫` | HUSK | **120** | **3** | R2 | SoftHit；mmxp elite；shard 0.6 |
| `EmberSx02Warden` | `&e灯塔守吏` | ZOMBIE | **180** | **3** | R3 | SoftHit + **BeaconCast @80**；mmxp；core_fragment；shard 0.5；**无装备**；未抬过日更 Boss 210 |

### 2) Skill `EmberSx02BeaconCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【锈灯】灯塔扫线落点——侧移拉开！」`@PlayersInRadius{r=24}` |
| 蓄力（扫线示意） | `flame` 窄幅 + `lava` 中幅 + `crit` 宽幅（1.12.2 可用；示扫线铺开） |
| 音效 | `minecart.base` volume 0.65 pitch 0.85 |
| 读条 | `delay 25` |
| 结算（落点圈） | `damage 1.2` `@PlayersInRadius{r=5}`；**无 SLOW** |
| 结算粒子 | `flame` + `lava` |
| Cooldown | **8** |
| 挂接 | `EmberSx02Warden` → `skill{s=EmberSx02BeaconCast} @self ~onTimer:80` |

设计「扫线或落点圈」：1.12 无可靠方向扫线伤害原语时，用**宽幅蓄力粒子示扫线 + r5 落点圈结算**；message 标明侧移。

### 3) DP / 地图 / 刷点

| 项 | 状态 |
|----|------|
| `plugins/DungeonPlus/dungeon/EmberSx02/` | 已有骨架（option/monster idle/obstacle）；本号更新 `monster.yml` 注释挂表 |
| `plugins/DungeonPlus/config.yml` | `ember_short_sx02` ↔ `EmberSx02`（地图岗已登记；本 commit 一并入库） |
| `plugins/DungeonPlus/map/ember_short_sx02/` | 本地已有（自 rail_v1 拷；**gitignore**，不入 git） |
| 出生 | `0,64,5`（option 已写） |

**意向刷点（Q01/sx01 惯例 · 键号写入 short.sx02）：**

| 房 | 组 | MM ID | Display | 坐标（本地） | 数量 |
|----|----|-------|---------|--------------|------|
| R1 | wave1 | `EmberSx02Guard` | 锈灯巡卫×3 | (-6,64,29)×2 · (0,64,29)×1 | 3 |
| R1 | wave1 | `EmberSx02Archer` | 栈道弩手×2 | (6,64,29)×1 · (9,64,28)×1 | 2 |
| R2 | wave2 | `EmberSx02Elite` | 栈道督卫×1 | (30,64,39) | 1 |
| R3 | boss | `EmberSx02Warden` | 灯塔守吏×1 | (0,64,102) | 1 |

`mobs` 槽建议：`melee=Guard` · `ranged=Archer` · `elite=Elite` · `boss.mm=Warden`。  
**禁** DP `$mob` wave 自驱（D391 FAIL：Director 不进 FIGHTING / S41 不触发）。

### 4) 明确零改

- 霜/锈/庭/焦/潮/断/窖 七线 Daily Mobs/Cast
- `EmberSx01.yml` / `EmberSx01SentryCast` / EmberSx01 DP
- 体力 / SoftHit 本体 / afk / gate_daily / ×0.97 / 观察

## 热更

| 命令 | 时间（CST） | 结果 |
|------|-------------|------|
| FIFO `mm reload` | 05:21:45 | **121** 怪（+4）· **31** 技能（+1 Cast）· 94 ms |
| `mm mobs info EmberSx02Warden` | 05:21:51 | Display 灯塔守吏 · HP180 · Damage3 · 文件 EmberSx02.yml |
| `mm skills info EmberSx02BeaconCast` | 05:21:53 | 命中 Skills/EmberDungeonDiffSkills.yml |
| FIFO `dp reload` | 05:21:55 | **插件重载完毕** · **`EmberSx02` 地牢内容初始化完毕** · `ember_short_sx02` 地图导入成功 |

- `plugins/` 与 `server-runtime/plugins/` **同 inode**（无需另拷）
- `login-runtime/ops.json` = `[]`；play ops 既有测试号 `D388Life`（本档未改）
- 服务保持可玩；未长停服；无 Sx02 相关 ERROR

## 测岗建议（交键号 short.sx02 后）

`corerpg p1 enter sx02`：栈台清 → 督卫 → 灯塔守吏 Cast「侧移拉开」先于 r5 伤；圈内 ≈1.2；无 SLOW；七线 + sx01 零回归。

## 残留

- `ember-v1-runs.yml#short.sx02` rooms/boss / S41 / 菜单双本选页 → **DP+键号 / 经济号 / 菜单号**
- 地图主题 WE（抬升栈道/分叉桥/灯塔垫）→ 地图岗
