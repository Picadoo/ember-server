# STATUS · 短征 sx01 MM · 烬门哨岗（D391）

- 时间：2026-10-10 04:18 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-short-dungeon-p1-reachable-2026-10-10.md` §2.3；总控批 A·M @`234dea63`
- 对照源：`EmberDailyFrost` / `EmberDaily`（庭院）数值档 + `EmberFrostNovaCast` 范式
- 未做：旧七线 Daily 零改；DP / 体力 / 菜单 / S40 经济 / 地图实体零动；装备掉落未加（设计 §2.5 首航默认不发）

## 改动

### 1) 新建 `plugins/MythicMobs/Mobs/EmberSx01.yml`

| ID | Display | Type | HP | 攻 | 房 | 备注 |
|----|---------|------|----|----|----|------|
| `EmberSx01Zombie` | `&6哨岗尸` | ZOMBIE | **45** | **4** | R1 | SoftHit；死掉 `mat_ember_shard` |
| `EmberSx01Skeleton` | `&6哨弓骷` | SKELETON | **35** | **4** | R1 | BOW 远程；死掉 `mat_ember_bone_dust` |
| `EmberSx01Elite` | `&6哨岗伍长` | HUSK | **120** | **3** | R2 | SoftHit；mmxp elite；shard 0.6 |
| `EmberSx01Warden` | `&6烬门哨长` | ZOMBIE | **180** | **3** | R3 | SoftHit + **Cast @80**；mmxp elite；core_fragment；shard 0.5；**无装备** |

### 2) Skill `EmberSx01SentryCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【烬门】哨长蓄力——拉开！」`@PlayersInRadius{r=24}` |
| 蓄力粒子 | `flame` + `crit`（烬门主题；1.12.2） |
| 音效 | `minecart.base` volume 0.6 pitch 0.75 |
| 读条 | `delay 25` |
| 结算 | `damage 1.2` `@PlayersInRadius{r=5}`；**无 SLOW**（设计未钉 SLOW） |
| 结算粒子 | `flame` |
| Cooldown | **8** |
| 挂接 | `EmberSx01Warden` → `skill{s=EmberSx01SentryCast} @self ~onTimer:80` |

### 3) 坐标占位

- 地图 id：**`ember_short_sx01`**（设计钉）
- 本档 **只 MM**，未写 DP `monster.yml` / `option.yml` 刷点
- **待地图号**钉 R1/R2/R3 出生与 Boss 垫本地坐标后，由 **DP+键号** 挂怪 ID：
  - R1：`EmberSx01Zombie` + `EmberSx01Skeleton`（1～2 波）
  - R2：`EmberSx01Elite`
  - R3：`EmberSx01Warden`

### 4) 明确零改

- 霜/锈/庭/焦/潮/断/窖 七线 Mobs 与既有 Cast
- 体力 / SoftHit 本体 / 无关掉落池 / ×0.97 / 观察

## 热更

- FIFO `scripts/console.sh play "mm reload"`（04:18:19 CST）
- MythicMobs：**117** 怪（+4）· **30** 技能（+1 Cast）· 重载完毕 **112** ms
- `mm mobs info EmberSx01Warden` / `mm skills info EmberSx01SentryCast` 均命中
- 无 Sx01 相关 ERROR
- `login-runtime/ops.json` = `[]`；`server-runtime/ops.json` 既有 `D388Life`（本档未改 ops）

## 测岗建议（交地图/DP 号后）

进 `EmberSx01` 打终厅：Cast 提示先于半径伤；圈内 ≈1.2；拉开 drop=0；无 SLOW；小怪/精英掉落薄材料；七线 Daily 零回归。
