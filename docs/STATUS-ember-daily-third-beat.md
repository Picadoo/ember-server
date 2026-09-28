# STATUS · 日常第三拍方案 A · 霜晶霜暴读条

- 时间：2026-09-28（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design-ember-daily-third-beat.md` §5/§12；tip `0aa0266` / 设计 `c48850e`
- 未做：DP monster/option/门/prep/体力/掉落池/他线 MM；Boss HP 210 零改；SoftHit / onDeath 掉落零改
- **未 commit/push**（总控代推后派测）

## 改动

### 1) Skill `EmberFrostNovaCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【霜厅】霜核蓄力——拉开！」`@PlayersInRadius{r=24}` |
| 蓄力粒子 | `effect:particles{p=cloud;…}`（**未用** `snowballpoof`，1.12 不安全） |
| 音效 | `minecart.base` |
| 读条 | `delay 25`（≈1.25s） |
| 结算 | `damage 1.2` + `SLOW` 40tick `@PlayersInRadius{r=5}` |
| 结算粒子 | `crit` |
| Cooldown | **8** |

### 2) Boss `EmberDailyFrostBrute`

文件：`plugins/MythicMobs/Mobs/EmberDailyFrost.yml`

- **删** `~onTimer:45` 的 `potion SLOW r6` 与 `damage 0.4 r5`
- **增** `skill{s=EmberFrostNovaCast} @self ~onTimer:80`
- SoftHit / mmxp / core / gear×3 / shard：**未动**；Health **210** / Damage **3**：**未动**

## 热更

- FIFO `console.in` → `mm reload`（11:14:25 CST）
- MythicMobs：**59** 怪 · **23** 技能（+1）· 重载完毕 108 ms
- 仅既有 ExampleItems `KingsCrown` WARN；无 Frost 相关报错
- `ops.json` = `[]`

## 测岗建议

霜晶进本打 Boss：提示须先于结算伤；delay 内站旁不吃该次霜暴；拉开 r5 外免伤；勿抬 HP。
