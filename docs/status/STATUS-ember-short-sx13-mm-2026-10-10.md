# STATUS · 短征 sx13 MM · 烬衡悬梁（D421）

- 时间：2026-10-10 09:43 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-short-dungeon-sx13-2026-10-10.md` §2.2/§2.4；总控批 A·M · D421
- 对照源：`EmberSx12` 数值档 + `EmberSx12PivotCast` / `EmberSx01SentryCast` 范式；刷点坐标沿用 **Q01 / sx01–sx12** 三房惯例
- 未做：旧七线 Daily 零改；**sx01–sx12 零改**；afk / Stage2 / gate_daily / 体力 / SoftHit 本体 / ×0.97 / 观察 / S52 经济 REG / 菜单十三本选页接线 / jar（`short.sx13` 键交键号并行）；地图 WE 主题（骨架已本地拷，衡门庭/悬梁衡廊/衡冠终厅 WE 另号）

## 人话

烬衡悬梁四只怪和衡扫斩读条已进 MythicMobs；DP `EmberSx13` 仍走 CoreRpg 导演空壳（不重蹈 D391 DP wave FAIL）；刷点意向表按 Q01 坐标挂好，键名与 `short.sx13` 占位一致（Guard / Crossbow / Elite / Warden）。衡扫无 SLOW，靠 message「退进衡冠侧龛或已过衡梁侧台」+ r5 条/扇形伤可读可躲。Display 用衡门/梁廊/衡廊/衡冠文案，**未**复读窄桥/跳石/转枢/递闸/对廊/环廊/塔升/错层/风廊/霜雾廊气质。

## 改动

### 1) 新建 `plugins/MythicMobs/Mobs/EmberSx13.yml`

| ID | Display | Type | HP | 攻 | 房 | 备注 |
|----|---------|------|----|----|----|------|
| `EmberSx13Guard` | `&c衡门卫` | ZOMBIE | **48** | **4** | R1 | SoftHit；死掉 shard；对照 sx12 Guard；设计「衡门卫」 |
| `EmberSx13Crossbow` | `&c梁廊弩` | SKELETON | **32** | **4** | R1 | BOW 远程；键名 **Crossbow** 对齐设计「梁廊衡侍」与 short.sx13 ranged |
| `EmberSx13Elite` | `&c衡廊督卫` | HUSK | **120** | **3** | R2 | SoftHit；mmxp elite；shard 0.6；设计 R2「途中 1 波梁卫或梁弩」 |
| `EmberSx13Warden` | `&c衡冠守吏` | ZOMBIE | **180** | **3** | R3 | SoftHit + **BalanceCast @80**；mmxp；core_fragment；shard 0.5；**无装备**；未抬过日更 Boss 210 |

**新 MM id**，禁复用 EmberSx01–12 本壳。

### 2) Skill `EmberSx13BalanceCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【烬衡】衡扫斩——退进衡冠侧龛或已过衡梁侧台！」`@PlayersInRadius{r=24}` |
| 蓄力（衡梁倾势） | `crit` 倾梁条 + `cloud` 配重倾势 + `lava` 烬感（1.12.2 可用） |
| 音效 | `minecart.base` volume 0.7 pitch 0.45 |
| 读条 | `delay 25` |
| 结算（条/扇形伤 · r5 圈近似） | `damage 1.2` `@PlayersInRadius{r=5}`；**无 SLOW** |
| 结算粒子 | `crit` + `cloud` + `lava` |
| Cooldown | **8** |
| 挂接 | `EmberSx13Warden` → `skill{s=EmberSx13BalanceCast} @self ~onTimer:80` |

设计「衡扫斩/倾梁压浪」：用**衡梁倾势粒子 + r5 环结算**近似条/扇形；message 标明退进衡冠侧龛或已过衡梁侧台。

### 3) DP / 地图 / 刷点

| 项 | 状态 |
|----|------|
| `plugins/DungeonPlus/dungeon/EmberSx13/` | 自 EmberSx12 同构 idle 壳（option / monster 空 groups / obstacle） |
| `plugins/DungeonPlus/config.yml` | `ember_short_sx13` ↔ `EmberSx13` |
| `plugins/DungeonPlus/map/ember_short_sx13/` | 本地骨架自 **ember_raid**（**gitignore**，不入 git；衡门庭/悬梁衡廊/衡冠 WE 另号） |
| 出生 | `0,64,5`（option 已写） |

**意向刷点（Q01/sx01–sx12 惯例 · 键号 short.sx13）：**

| 房 | 组 | MM ID | Display | 坐标（本地） | 数量 |
|----|----|-------|---------|--------------|------|
| R1 | wave1 | `EmberSx13Guard` | 衡门卫×3 | (-6,64,29)×2 · (0,64,29)×1 | 3 |
| R1 | wave1 | `EmberSx13Crossbow` | 梁廊弩×2 | (6,64,29)×1 · (9,64,28)×1 | 2 |
| R2 | wave2 | `EmberSx13Elite` | 衡廊督卫×1 | (30,64,39) | 1 |
| R3 | boss | `EmberSx13Warden` | 衡冠守吏×1 | (0,64,102) | 1 |

`mobs` 槽：`melee=Guard` · `ranged=Crossbow` · `elite=Elite` · `boss.mm=Warden`。  
**禁** DP `$mob` wave 自驱（D391 FAIL：Director 不进 FIGHTING / S52 不触发）。

### 4) 明确零改

- 霜/锈/庭/焦/潮/断/窖 七线 Daily Mobs/Cast
- `EmberSx01.yml` … `EmberSx12.yml` / 对应 Cast / DP
- 体力 / SoftHit 本体 / **afk** / **gate_daily** / **Stage2** / ×0.97 / 观察
- `ember-v1-runs.yml` / economy / 菜单（键号/经济号/菜单号另交）

## 热更

| 命令 | 时间（CST） | 结果 |
|------|-------------|------|
| FIFO `mm reload` | 09:43:10 | **165** 怪（+4）· **42** 技能（+1 Cast）· 148 ms |
| `mm mobs info EmberSx13Warden` | 09:43:16 | Display 衡冠守吏 · HP180 · Damage3 · 文件 EmberSx13.yml |
| `mm skills info EmberSx13BalanceCast` | 09:43:18 | 命中 Skills/EmberDungeonDiffSkills.yml |
| `mm mobs info EmberSx13Guard` / `Crossbow` / `Elite` | 09:43:19–22 | 衡门卫 HP48 · 梁廊弩 HP32 · 衡廊督卫 HP120 |
| FIFO `dp reload` | 09:43:23 | **插件重载完毕** · **`EmberSx13` 地牢内容初始化完毕** · `ember_short_sx13` 地图导入/初始化成功 |
| `login-runtime/ops.json` | 09:43 | `[]`（本档未改；server-runtime 有测账号 op，未动） |

## 残留

- `ember-v1-runs.yml#short.sx13` rooms×3+boss / S52 / 菜单十三本选页接线 → **DP+键号 / 经济号 / 菜单号**
- 地图主题 WE（衡门庭 / 悬梁衡廊 ≥3 配重衡梁 + 梁下虚空/深坑 / 衡冠终厅栏心烬台 + 侧龛或侧台）→ 地图岗
