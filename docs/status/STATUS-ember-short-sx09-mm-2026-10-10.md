# STATUS · 短征 sx09 MM · 烬渠跳石（D412）

- 时间：2026-10-10 07:48 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-short-dungeon-sx09-2026-10-10.md` §2.2/§2.4；总控批 A·M · D412 · DESIGN tip `a35ae9bd`
- 对照源：`EmberSx08` 数值档 + `EmberSx08LayerCast` / `EmberSx01SentryCast` 范式；刷点坐标沿用 **Q01 / sx01–sx08** 三房惯例
- 未做：旧七线 Daily 零改；**sx01–sx08 零改**；afk / Stage2 / gate_daily / 体力 / SoftHit 本体 / ×0.97 / 观察 / S48 经济 REG / 菜单九本选页接线 / jar（`short.sx09` 键交键号并行）；地图 WE 主题（骨架已本地拷，渠岸/跳石/对岸 WE 另号）

## 人话

烬渠跳石四只怪和渠心溅浪读条已进 MythicMobs；DP `EmberSx09` 仍走 CoreRpg 导演空壳（不重蹈 D391 DP wave FAIL）；刷点意向表按 Q01 坐标挂好，键名与 `short.sx09` 占位一致（Guard / Crossbow / Elite / Warden）。溅浪无 SLOW，靠 message「退进岸侧龛或回撤跳石」+ r5 柱/环伤可读可躲。

## 改动

### 1) 新建 `plugins/MythicMobs/Mobs/EmberSx09.yml`

| ID | Display | Type | HP | 攻 | 房 | 备注 |
|----|---------|------|----|----|----|------|
| `EmberSx09Guard` | `&c渠卫` | ZOMBIE | **48** | **4** | R1 | SoftHit；死掉 shard；对照 sx08 Guard；设计「渠卫尸」 |
| `EmberSx09Crossbow` | `&c跳石弩` | SKELETON | **32** | **4** | R1 | BOW 远程；键名 **Crossbow** 对齐设计「跳石弩手」与 short.sx09 ranged |
| `EmberSx09Elite` | `&c跳石督卫` | HUSK | **120** | **3** | R2 | SoftHit；mmxp elite；shard 0.6；设计 R2「途中 1 波跳石怪或岸弩」 |
| `EmberSx09Warden` | `&c对岸守吏` | ZOMBIE | **180** | **3** | R3 | SoftHit + **CanalCast @80**；mmxp；core_fragment；shard 0.5；**无装备**；未抬过日更 Boss 210 |

**新 MM id**，禁复用 EmberSx01–08 本壳。

### 2) Skill `EmberSx09CanalCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【烬渠】渠心溅浪——退进岸侧龛或回撤跳石！」`@PlayersInRadius{r=24}` |
| 蓄力（渠心/岸缘） | `cloud` 溅浪雾 + `lava` 烬渠感 + `crit` 扫岸（1.12.2 可用） |
| 音效 | `minecart.base` volume 0.7 pitch 0.75 |
| 读条 | `delay 25` |
| 结算（柱/环伤） | `damage 1.2` `@PlayersInRadius{r=5}`；**无 SLOW** |
| 结算粒子 | `cloud` + `lava` + `crit` |
| Cooldown | **8** |
| 挂接 | `EmberSx09Warden` → `skill{s=EmberSx09CanalCast} @self ~onTimer:80` |

设计「渠心溅浪/扫岸斩」：用**渠心/岸缘粒子 + r5 环结算**；message 标明退进岸侧龛或回撤最后一枚跳石平台。

### 3) DP / 地图 / 刷点

| 项 | 状态 |
|----|------|
| `plugins/DungeonPlus/dungeon/EmberSx09/` | 自 EmberSx08 同构新建；`monster.yml` idle（禁 wave 自驱） |
| `plugins/DungeonPlus/config.yml` | `ember_short_sx09` ↔ `EmberSx09`（与并行地图岗登记对齐） |
| `plugins/DungeonPlus/map/ember_short_sx09/` | 本地骨架（**gitignore**，不入 git；渠岸/跳石/对岸 WE 另号） |
| 出生 | `0,64,5`（option 已写） |

**意向刷点（Q01/sx01–sx08 惯例 · 键号 short.sx09）：**

| 房 | 组 | MM ID | Display | 坐标（本地） | 数量 |
|----|----|-------|---------|--------------|------|
| R1 | wave1 | `EmberSx09Guard` | 渠卫×3 | (-6,64,29)×2 · (0,64,29)×1 | 3 |
| R1 | wave1 | `EmberSx09Crossbow` | 跳石弩×2 | (6,64,29)×1 · (9,64,28)×1 | 2 |
| R2 | wave2 | `EmberSx09Elite` | 跳石督卫×1 | (30,64,39) | 1 |
| R3 | boss | `EmberSx09Warden` | 对岸守吏×1 | (0,64,102) | 1 |

`mobs` 槽：`melee=Guard` · `ranged=Crossbow` · `elite=Elite` · `boss.mm=Warden`。  
**禁** DP `$mob` wave 自驱（D391 FAIL：Director 不进 FIGHTING / S48 不触发）。

### 4) 明确零改

- 霜/锈/庭/焦/潮/断/窖 七线 Daily Mobs/Cast
- `EmberSx01.yml` … `EmberSx08.yml` / 对应 Cast / DP
- 体力 / SoftHit 本体 / **afk** / **gate_daily** / **Stage2** / ×0.97 / 观察
- `ember-v1-runs.yml` / economy / 菜单（键号/经济号/菜单号另交）

## 热更

| 命令 | 时间（CST） | 结果 |
|------|-------------|------|
| FIFO `mm reload` | 07:48:28 | **149** 怪（+4）· **38** 技能（+1 Cast）· 131 ms |
| `mm mobs info EmberSx09Warden` | 07:48:33 | Display 对岸守吏 · HP180 · Damage3 · 文件 EmberSx09.yml |
| `mm skills info EmberSx09CanalCast` | 07:48:35 | 命中 Skills/EmberDungeonDiffSkills.yml |
| `mm mobs info EmberSx09Guard` / `Crossbow` / `Elite` | 07:48:37–40 | 渠卫 HP48 · 跳石弩 HP32 · 跳石督卫 HP120 |
| FIFO `dp reload` | 07:48:41 | **插件重载完毕** · **`EmberSx09` 地牢内容初始化完毕** · `ember_short_sx09` 地图导入/初始化成功 |
| `login-runtime/ops.json` | 07:48 | `[]`（本档未改） |

## 残留

- `ember-v1-runs.yml#short.sx09` rooms×3+boss / S48 / 菜单九本选页接线 → **DP+键号 / 经济号 / 菜单号**
- 地图主题 WE（渠岸庭 / ≥3 枚离散跳石 + 渠面可读 / 对岸栏心烬台 + 岸侧龛或回撤跳石平台）→ 地图岗
