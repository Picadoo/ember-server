# STATUS · 日常第三拍扩线 · 锈轨矿监砸地读条

- 时间：2026-09-28（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/design-ember-daily-rail-third-beat.md`；tip `d3fac2b` / 设计 `f15d151`
- 未做：DP/门/prep/体力/他线/抬 HP；SoftHit / 逼近 message / onDeath 掉落零改
- **未 commit/push**（总控代推后派测）

## 改动

### 1) Skill `EmberRailSlamCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【锈轨】矿监蓄力砸地——拉开！」`@PlayersInRadius{r=24}` |
| 蓄力粒子 | `lava` + `crit`（1.12 现网已验证；未替换） |
| 音效 | `minecart.base` pitch 0.55 |
| 读条 | `delay 25` |
| 结算 | `damage 1.2` `@PlayersInRadius{r=5}`；**无 SLOW**（异于霜晶） |
| 结算粒子 | `flame` |
| Cooldown | **8** |

### 2) Boss `EmberDailyRailWarden`

文件：`plugins/MythicMobs/Mobs/EmberDailyRail.yml`

- **删** `damage{amount=0.5} @PlayersInRadius{r=4} ~onTimer:40`
- **增** `skill{s=EmberRailSlamCast} @self ~onTimer:80`
- **保留** SoftHit、逼近 `message ~onTimer:170`、全部 `~onDeath`；Health **210** / Damage **3** 零改

## 热更

- FIFO `console.in` → `mm reload`（11:24:30 CST）
- MythicMobs：**59** 怪 · **24** 技能（+1）· 重载完毕 71 ms
- 仅既有 ExampleItems `KingsCrown` WARN；无 Rail 相关报错
- `ops.json` = `[]`

## 测岗建议

锈轨进本打 Boss：提示先于结算；delay 内不吃该次砸地伤；拉开 r5 外免伤；无减速（与霜晶体感差）。
