# STATUS · 短征 sx11 MM · 烬镜对廊（D417）

- 时间：2026-10-10 09:01 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-short-dungeon-sx11-2026-10-10.md` §2.2/§2.4；总控批 A·M · D417 · DESIGN tip `fc951892`
- 对照源：`EmberSx10` 数值档 + `EmberSx10GateCast` / `EmberSx01SentryCast` 范式；刷点坐标沿用 **Q01 / sx01–sx10** 三房惯例
- 未做：旧七线 Daily 零改；**sx01–sx10 零改**；afk / Stage2 / gate_daily / 体力 / SoftHit 本体 / ×0.97 / 观察 / S50 经济 REG / 菜单十一本选页接线 / jar（`short.sx11` 键交键号并行）；地图 WE 主题（骨架已本地拷，对廊前厅/左右双廊/合镜终厅 WE 另号）

## 人话

烬镜对廊四只怪和镜面扫线读条已进 MythicMobs；DP `EmberSx11` 仍走 CoreRpg 导演空壳（不重蹈 D391 DP wave FAIL）；刷点意向表按 Q01 坐标挂好，键名与 `short.sx11` 占位一致（Guard / Crossbow / Elite / Warden）。扫线无 SLOW，靠 message「退进合镜侧龛或回撤侧廊」+ r5 条/环伤可读可躲。

## 改动

### 1) 新建 `plugins/MythicMobs/Mobs/EmberSx11.yml`

| ID | Display | Type | HP | 攻 | 房 | 备注 |
|----|---------|------|----|----|----|------|
| `EmberSx11Guard` | `&c对廊卫` | ZOMBIE | **48** | **4** | R1 | SoftHit；死掉 shard；对照 sx10 Guard；设计「对廊卫」 |
| `EmberSx11Crossbow` | `&c侧廊弩` | SKELETON | **32** | **4** | R1 | BOW 远程；键名 **Crossbow** 对齐设计「侧廊影侍」与 short.sx11 ranged |
| `EmberSx11Elite` | `&c镜廊督卫` | HUSK | **120** | **3** | R2 | SoftHit；mmxp elite；shard 0.6；设计 R2「途中每侧 1 波」 |
| `EmberSx11Warden` | `&c合镜守吏` | ZOMBIE | **180** | **3** | R3 | SoftHit + **MirrorCast @80**；mmxp；core_fragment；shard 0.5；**无装备**；未抬过日更 Boss 210 |

**新 MM id**，禁复用 EmberSx01–10 本壳。

### 2) Skill `EmberSx11MirrorCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【烬镜】镜面扫线——退进合镜侧龛或回撤侧廊！」`@PlayersInRadius{r=24}` |
| 蓄力（中轴/双廊镜像） | `crit` 扫线 + `cloud` 镜像雾 + `lava` 烬感（1.12.2 可用） |
| 音效 | `minecart.base` volume 0.7 pitch 0.55 |
| 读条 | `delay 25` |
| 结算（条/环伤） | `damage 1.2` `@PlayersInRadius{r=5}`；**无 SLOW** |
| 结算粒子 | `crit` + `cloud` + `lava` |
| Cooldown | **8** |
| 挂接 | `EmberSx11Warden` → `skill{s=EmberSx11MirrorCast} @self ~onTimer:80` |

设计「镜面扫线/对折斩」：用**中轴/双廊镜像粒子 + r5 环结算**；message 标明退进合镜侧龛或回撤任一已通侧廊末端平台。

### 3) DP / 地图 / 刷点

| 项 | 状态 |
|----|------|
| `plugins/DungeonPlus/dungeon/EmberSx11/` | 自 EmberSx10 同构 idle 壳（option / monster 空 groups / obstacle） |
| `plugins/DungeonPlus/config.yml` | `ember_short_sx11` ↔ `EmberSx11` |
| `plugins/DungeonPlus/map/ember_short_sx11/` | 本地骨架自 **ember_raid**（**gitignore**，不入 git；对廊/双廊/合镜 WE 另号） |
| 出生 | `0,64,5`（option 已写） |

**意向刷点（Q01/sx01–sx10 惯例 · 键号 short.sx11）：**

| 房 | 组 | MM ID | Display | 坐标（本地） | 数量 |
|----|----|-------|---------|--------------|------|
| R1 | wave1 | `EmberSx11Guard` | 对廊卫×3 | (-6,64,29)×2 · (0,64,29)×1 | 3 |
| R1 | wave1 | `EmberSx11Crossbow` | 侧廊弩×2 | (6,64,29)×1 · (9,64,28)×1 | 2 |
| R2 | wave2 | `EmberSx11Elite` | 镜廊督卫×1 | (30,64,39) | 1 |
| R3 | boss | `EmberSx11Warden` | 合镜守吏×1 | (0,64,102) | 1 |

`mobs` 槽：`melee=Guard` · `ranged=Crossbow` · `elite=Elite` · `boss.mm=Warden`。  
**禁** DP `$mob` wave 自驱（D391 FAIL：Director 不进 FIGHTING / S50 不触发）。

### 4) 明确零改

- 霜/锈/庭/焦/潮/断/窖 七线 Daily Mobs/Cast
- `EmberSx01.yml` … `EmberSx10.yml` / 对应 Cast / DP
- 体力 / SoftHit 本体 / **afk** / **gate_daily** / **Stage2** / ×0.97 / 观察
- `ember-v1-runs.yml` / economy / 菜单（键号/经济号/菜单号另交）

## 热更

| 命令 | 时间（CST） | 结果 |
|------|-------------|------|
| FIFO `mm reload` | 09:01:27 | **157** 怪（+4）· **40** 技能（+1 Cast）· 135 ms |
| `mm mobs info EmberSx11Warden` | 09:01:32 | Display 合镜守吏 · HP180 · Damage3 · 文件 EmberSx11.yml |
| `mm skills info EmberSx11MirrorCast` | 09:01:34 | 命中 Skills/EmberDungeonDiffSkills.yml |
| `mm mobs info EmberSx11Guard` / `Crossbow` / `Elite` | 09:01:35–38 | 对廊卫 HP48 · 侧廊弩 HP32 · 镜廊督卫 HP120 |
| FIFO `dp reload` | 09:01:41 | **插件重载完毕** · **`EmberSx11` 地牢内容初始化完毕** · `ember_short_sx11` 地图导入/初始化成功 |
| `login-runtime/ops.json` | 09:01 | `[]`（本档未改） |

## 残留

- `ember-v1-runs.yml#short.sx11` rooms×3+boss / S50 / 菜单十一本选页接线 → **DP+键号 / 经济号 / 菜单号**
- 地图主题 WE（对廊前厅 / 左右对称双廊 + 中轴合镜门 / 合镜终厅栏心烬台 + 侧龛或侧廊回撤平台）→ 地图岗
