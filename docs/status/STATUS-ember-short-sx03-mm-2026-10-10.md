# STATUS · 短征 sx03 MM · 霜雾闸廊（D393）

- 时间：2026-10-10 05:38 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-short-dungeon-sx03-2026-10-10.md` §2.2/§2.4；总控批 A·M @`088fd3b7`
- 对照源：`EmberSx02` 数值档 + `EmberFrostNovaCast` 范式；刷点坐标沿用 **Q01 / sx01 / sx02** 三房惯例
- 未做：旧七线 Daily 零改；**sx01/sx02 零改**；体力 / SoftHit 本体 / ×0.97 / 观察 / S42 经济 REG / 菜单三本选页 / jar（`short.sx03` 键草稿在 CoreRpg/src 由键号并行）

## 人话

霜雾闸廊四只怪和霜雾楔/冻圈读条已进 MythicMobs；DP `EmberSx03` 仍走 CoreRpg 导演空壳（不重蹈 D391 DP wave FAIL）；刷点意向表按 Q01 坐标挂好，键名与 `short.sx03` 占位一致（Patrol / Crossbow / Elite / Warden）。

## 改动

### 1) 新建 `plugins/MythicMobs/Mobs/EmberSx03.yml`

| ID | Display | Type | HP | 攻 | 房 | 备注 |
|----|---------|------|----|----|----|------|
| `EmberSx03Patrol` | `&b霜廊巡卒` | ZOMBIE | **48** | **4** | R1 | SoftHit；死掉 shard；对照 sx02 Guard |
| `EmberSx03Crossbow` | `&b闸弩` | SKELETON | **32** | **4** | R1 | BOW 远程；键名 **Crossbow** 对齐设计「闸弩」与 short.sx03 ranged（**禁** Archer，避 sx02 Crossbow≠Archer） |
| `EmberSx03Elite` | `&b霜闸督卫` | HUSK | **120** | **3** | R2 | SoftHit；mmxp elite；shard 0.6 |
| `EmberSx03Warden` | `&b霜窖守吏` | ZOMBIE | **180** | **3** | R3 | SoftHit + **FrostCast @80**；mmxp；core_fragment；shard 0.5；**无装备**；未抬过日更 Boss 210 |

### 2) Skill `EmberSx03FrostCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【霜雾】霜雾楔冻圈——侧移拉开！」`@PlayersInRadius{r=24}` |
| 蓄力（雾楔） | `cloud` 宽幅雾压 + `crit` 落点示意（1.12.2 可用） |
| 音效 | `minecart.base` volume 0.6 pitch 0.7 |
| 读条 | `delay 25` |
| 结算（冻圈） | `damage 1.2` `@PlayersInRadius{r=5}`；**轻 SLOW** duration40 level0（对齐 FrostNova） |
| 结算粒子 | `cloud` + `crit` |
| Cooldown | **8** |
| 挂接 | `EmberSx03Warden` → `skill{s=EmberSx03FrostCast} @self ~onTimer:80` |

设计「霜雾楔/冻圈」：1.12 无可靠方向楔形伤害原语时，用**雾粒子蓄力 + r5 冻圈结算 + 轻减速**；message 标明侧移。

### 3) DP / 地图 / 刷点

| 项 | 状态 |
|----|------|
| `plugins/DungeonPlus/dungeon/EmberSx03/` | 骨架已有（地图岗 / 本号更新 `monster.yml` 挂表） |
| `plugins/DungeonPlus/config.yml` | `ember_short_sx03` ↔ `EmberSx03`（已登记） |
| `plugins/DungeonPlus/map/ember_short_sx03/` | 本地自 `ember_daily_frost_v1` 拷（**gitignore**，不入 git） |
| 出生 | `0,64,5`（option 已写） |

**意向刷点（Q01/sx01/sx02 惯例 · 键号 short.sx03）：**

| 房 | 组 | MM ID | Display | 坐标（本地） | 数量 |
|----|----|-------|---------|--------------|------|
| R1 | wave1 | `EmberSx03Patrol` | 霜廊巡卒×3 | (-6,64,29)×2 · (0,64,29)×1 | 3 |
| R1 | wave1 | `EmberSx03Crossbow` | 闸弩×2 | (6,64,29)×1 · (9,64,28)×1 | 2 |
| R2 | wave2 | `EmberSx03Elite` | 霜闸督卫×1 | (30,64,39) | 1 |
| R3 | boss | `EmberSx03Warden` | 霜窖守吏×1 | (0,64,102) | 1 |

`mobs` 槽：`melee=Patrol` · `ranged=Crossbow` · `elite=Elite` · `boss.mm=Warden`。  
**禁** DP `$mob` wave 自驱（D391 FAIL：Director 不进 FIGHTING / S42 不触发）。

### 4) 明确零改

- 霜/锈/庭/焦/潮/断/窖 七线 Daily Mobs/Cast
- `EmberSx01.yml` / `EmberSx02.yml` / 对应 Cast / DP
- 体力 / SoftHit 本体 / afk / gate_daily / ×0.97 / 观察

## 热更

| 命令 | 时间（CST） | 结果 |
|------|-------------|------|
| FIFO `mm reload` | 05:38:42 | **125** 怪（+4）· **32** 技能（+1 Cast）· 130 ms |
| `mm mobs info EmberSx03Warden` | 05:38:45 | Display 霜窖守吏 · HP180 · Damage3 · 文件 EmberSx03.yml |
| `mm skills info EmberSx03FrostCast` | 05:38:47 | 命中 Skills/EmberDungeonDiffSkills.yml |
| FIFO `dp reload` | 05:38:49 | **插件重载完毕** · **`EmberSx03` 地牢内容初始化完毕** · `ember_short_sx03` 地图导入成功 |

- `plugins/` 与 `server-runtime/plugins/` **同 inode**（无需另拷）
- `login-runtime/ops.json` = `[]`；play ops 既有测试号 `D388Life`（本档未改）
- 服务保持可玩；未长停服；无 Sx03 相关 ERROR

## 测岗建议（交键号 short.sx03 落地后）

`corerpg p1 enter sx03`：雾廊清 → 督卫 → 霜窖守吏 Cast「侧移拉开」先于 r5 伤；圈内 ≈1.2 + 轻 SLOW；七线 + sx01/sx02 零回归。

## 残留

- `ember-v1-runs.yml#short.sx03` rooms/boss / S42 / 菜单三本选页 → **DP+键号 / 经济号 / 菜单号**（src 草稿已见 Patrol/Crossbow 占位，与本 MM 键名对齐）
- 地图主题 WE（雾廊/双闸/霜窖垫）→ 地图岗
