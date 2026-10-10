# STATUS · 短征 sx12 MM · 烬枢转厅（D419）

- 时间：2026-10-10 09:24 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-short-dungeon-sx12-2026-10-10.md` §2.2/§2.4；总控批 A·M · D419 · DESIGN tip `a72f4d05` 上游
- 对照源：`EmberSx11` 数值档 + `EmberSx11MirrorCast` / `EmberSx01SentryCast` 范式；刷点坐标沿用 **Q01 / sx01–sx11** 三房惯例
- 未做：旧七线 Daily 零改；**sx01–sx11 零改**；afk / Stage2 / gate_daily / 体力 / SoftHit 本体 / ×0.97 / 观察 / S51 经济 REG / 菜单十二本选页接线 / jar（`short.sx12` 键交键号并行）；地图 WE 主题（骨架已本地拷，入枢庭/环枢侧厢/枢冠终厅 WE 另号）

## 人话

烬枢转厅四只怪和枢扫斩读条已进 MythicMobs；DP `EmberSx12` 仍走 CoreRpg 导演空壳（不重蹈 D391 DP wave FAIL）；刷点意向表按 Q01 坐标挂好，键名与 `short.sx12` 占位一致（Guard / Crossbow / Elite / Warden）。枢扫无 SLOW，靠 message「退进侧厢龛或枢冠侧台」+ r5 条/扇形伤可读可躲。Display 用入枢/侧厢/枢厢/枢冠文案，**未**复读环廊/对廊/递闸/霜雾廊/跳石/错层/塔升/风廊气质。

## 改动

### 1) 新建 `plugins/MythicMobs/Mobs/EmberSx12.yml`

| ID | Display | Type | HP | 攻 | 房 | 备注 |
|----|---------|------|----|----|----|------|
| `EmberSx12Guard` | `&c入枢卫` | ZOMBIE | **48** | **4** | R1 | SoftHit；死掉 shard；对照 sx11 Guard；设计「入枢庭卫」 |
| `EmberSx12Crossbow` | `&c侧厢弩` | SKELETON | **32** | **4** | R1 | BOW 远程；键名 **Crossbow** 对齐设计「侧厢枢侍」与 short.sx12 ranged |
| `EmberSx12Elite` | `&c枢厢督卫` | HUSK | **120** | **3** | R2 | SoftHit；mmxp elite；shard 0.6；设计 R2「途中每厢 1 波」 |
| `EmberSx12Warden` | `&c枢冠守吏` | ZOMBIE | **180** | **3** | R3 | SoftHit + **PivotCast @80**；mmxp；core_fragment；shard 0.5；**无装备**；未抬过日更 Boss 210 |

**新 MM id**，禁复用 EmberSx01–11 本壳。

### 2) Skill `EmberSx12PivotCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【烬枢】枢扫斩——退进侧厢龛或枢冠侧台！」`@PlayersInRadius{r=24}` |
| 蓄力（中枢转心） | `crit` 扇扫 + `cloud` 转心雾 + `lava` 烬感（1.12.2 可用） |
| 音效 | `minecart.base` volume 0.7 pitch 0.48 |
| 读条 | `delay 25` |
| 结算（条/扇形伤 · r5 圈近似） | `damage 1.2` `@PlayersInRadius{r=5}`；**无 SLOW** |
| 结算粒子 | `crit` + `cloud` + `lava` |
| Cooldown | **8** |
| 挂接 | `EmberSx12Warden` → `skill{s=EmberSx12PivotCast} @self ~onTimer:80` |

设计「枢扫斩/转面压浪」：用**中枢转心粒子 + r5 环结算**近似条/扇形；message 标明退进已对齐侧厢龛或枢冠侧台。

### 3) DP / 地图 / 刷点

| 项 | 状态 |
|----|------|
| `plugins/DungeonPlus/dungeon/EmberSx12/` | 自 EmberSx11 同构 idle 壳（option / monster 空 groups / obstacle） |
| `plugins/DungeonPlus/config.yml` | `ember_short_sx12` ↔ `EmberSx12` |
| `plugins/DungeonPlus/map/ember_short_sx12/` | 本地骨架自 **ember_raid**（**gitignore**，不入 git；入枢庭/放射侧厢/枢冠 WE 另号） |
| 出生 | `0,64,5`（option 已写） |

**意向刷点（Q01/sx01–sx11 惯例 · 键号 short.sx12）：**

| 房 | 组 | MM ID | Display | 坐标（本地） | 数量 |
|----|----|-------|---------|--------------|------|
| R1 | wave1 | `EmberSx12Guard` | 入枢卫×3 | (-6,64,29)×2 · (0,64,29)×1 | 3 |
| R1 | wave1 | `EmberSx12Crossbow` | 侧厢弩×2 | (6,64,29)×1 · (9,64,28)×1 | 2 |
| R2 | wave2 | `EmberSx12Elite` | 枢厢督卫×1 | (30,64,39) | 1 |
| R3 | boss | `EmberSx12Warden` | 枢冠守吏×1 | (0,64,102) | 1 |

`mobs` 槽：`melee=Guard` · `ranged=Crossbow` · `elite=Elite` · `boss.mm=Warden`。  
**禁** DP `$mob` wave 自驱（D391 FAIL：Director 不进 FIGHTING / S51 不触发）。

### 4) 明确零改

- 霜/锈/庭/焦/潮/断/窖 七线 Daily Mobs/Cast
- `EmberSx01.yml` … `EmberSx11.yml` / 对应 Cast / DP
- 体力 / SoftHit 本体 / **afk** / **gate_daily** / **Stage2** / ×0.97 / 观察
- `ember-v1-runs.yml` / economy / 菜单（键号/经济号/菜单号另交）

## 热更

| 命令 | 时间（CST） | 结果 |
|------|-------------|------|
| FIFO `mm reload` | 09:24:11 | **161** 怪（+4）· **41** 技能（+1 Cast）· 124 ms |
| `mm mobs info EmberSx12Warden` | 09:24:14 | Display 枢冠守吏 · HP180 · Damage3 · 文件 EmberSx12.yml |
| `mm skills info EmberSx12PivotCast` | 09:24:16 | 命中 Skills/EmberDungeonDiffSkills.yml |
| `mm mobs info EmberSx12Guard` / `Crossbow` / `Elite` | 09:24:18–21 | 入枢卫 HP48 · 侧厢弩 HP32 · 枢厢督卫 HP120 |
| FIFO `dp reload` | 09:24:22 | **插件重载完毕** · **`EmberSx12` 地牢内容初始化完毕** · `ember_short_sx12` 地图导入/初始化成功 |
| `login-runtime/ops.json` | 09:24 | `[]`（本档未改；server-runtime 同） |

## 残留

- `ember-v1-runs.yml#short.sx12` rooms×3+boss / S51 / 菜单十二本选页接线 → **DP+键号 / 经济号 / 菜单号**
- 地图主题 WE（入枢庭 / 中枢转心 + ≥2 放射侧厢 + 对齐枢廊 / 枢冠终厅栏心烬台 + 侧厢龛或枢冠侧台）→ 地图岗
