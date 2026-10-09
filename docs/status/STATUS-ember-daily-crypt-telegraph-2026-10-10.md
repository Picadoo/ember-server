# STATUS · 日更第三拍扩线 · 残誓地窖守墓读条（D379）

- 时间：2026-10-10 03:22 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-daily-crypt-telegraph-2026-10-10.md`；总控批 A·M @`b3c025f1`
- 未做：霜/锈/庭院/焦骨/潮蚀/断塔零改；体力/掉落/DP/Health/×0.97/观察/假平面零动；点名+SLOW~160 / SoftHit 零改

## 改动

### 1) Skill `EmberCryptOathCast`

文件：`plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`（文末追加）

| 步 | 内容 |
|----|------|
| 提示 | `message`「【残誓】守墓蓄力——拉开！」`@PlayersInRadius{r=24}` |
| 蓄力粒子 | `cloud` + `crit`（1.12.2 现网已验证；地窖暗色氛围） |
| 音效 | `minecart.base` volume 0.6 pitch 0.65 |
| 读条 | `delay 25` |
| 结算 | `damage 1.2` `@PlayersInRadius{r=5}`；**无 SLOW**（学锈轨/断塔；点名自带 SLOW 已保留） |
| 结算粒子 | `crit` |
| Cooldown | **8** |

### 2) Boss `EmberDailyCryptWarden`

文件：`plugins/MythicMobs/Mobs/EmberDailyCrypt.yml`

- **删** `damage{amount=0.4} @PlayersInRadius{r=6} ~onTimer:50`（静默环）
- **增** `skill{s=EmberCryptOathCast} @self ~onTimer:80`
- **保留** 点名 `message` + `SLOW` `~onTimer:160`；SoftHit；全部 `~onDeath` mmxp/mmgive；Health **210** / Damage **3**

### 3) 明确零改

- 霜晶 / 锈轨 / 庭院 / 焦骨 / 潮蚀 / 断塔既有 Cast 与 Boss
- 七线 DP `monster.yml` / 体力 / SoftHit 概率 / Display / Equipment

## 热更

- FIFO `scripts/console.sh play "mm reload"`（03:22:08 CST）
- MythicMobs：**113** 怪 · **29** 技能（+1 Cast）· 重载完毕 **163** ms
- 无 CryptOathCast 相关 ERROR；既有无关 Invalid location / ExampleItems WARN 未新增
- `server-runtime/ops.json` = `[]`；`login-runtime/ops.json` = `[]`

## 测岗建议（复用霜/锈/D374 五条）

残誓地窖进本打 Boss：提示先于该次半径伤；圈内 ≈1.2；拉开 drop=0；点名 `~160` 仍触发且带 SLOW；SoftHit / 掉落 / DP / 体力 -30 零 diff；六线 Cast 零回归。
