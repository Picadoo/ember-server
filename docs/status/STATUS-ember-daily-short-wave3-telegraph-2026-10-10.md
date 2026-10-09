# STATUS · 日更第三拍扩线 · 庭院/焦骨/潮蚀/断塔预警环伤（D374）

- 时间：2026-10-10 02:57 CST（Asia/Shanghai）
- 执行：余烬-怪物
- 依据：`docs/design/DESIGN-ember-daily-short-wave3-telegraph-2026-10-10.md`；总控批 A·M @`a944ed30`
- 未做：霜晶/锈轨零改；残誓地窖后置；体力/掉落/DP/Health/×0.97/观察/假平面零动

## 改动

### 1) 四个 Cast Skill（追加 `plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml`）

| Skill | 线 | 提示文案 | 蓄力粒子 | 结算 | SLOW | CD |
|-------|----|----------|----------|------|------|----|
| `EmberYardPulseCast` | 庭院 | 【庭院】蛮兵蓄力——拉开！ | `flame`+`crit` | dmg **1.2** r5 → `flame` | **无** | 8 |
| `EmberAshBurstCast` | 焦骨 | 【焦骨】焦核蓄力焰爆——拉开！ | `lava`+`crit` | dmg **1.2** r5 → `flame` | **无** | 8 |
| `EmberTideCrashCast` | 潮蚀 | 【潮蚀】潮闸蓄力拍水——拉开！ | `cloud`+`crit` | dmg **1.2** r5 → `cloud` | **有** `SLOW` 40t/lv0 | 8 |
| `EmberSpireSlamCast` | 断塔 | 【断塔】守望蓄力砸石——拉开！ | `crit`+`cloud` | dmg **1.2** r5 → `crit` | **无** | 8 |

共性：`message` → 粒子 → `minecart.base` → **`delay 25`** → 结算；数值同档霜/锈，未抬超霜晶。粒子均为 1.12.2 现网已用族（cloud/crit/lava/flame），本窗未发现无效需换。

### 2) 四线 Boss

| Boss | 文件 | 删 | 增 | 保留 |
|------|------|----|----|------|
| `EmberDailyBrute` | `EmberDaily.yml` | `damage 0.5 r6 ~onTimer:40` | `skill{s=EmberYardPulseCast} @self ~onTimer:80` | HP **180** / 全部 `~onDeath` mmxp·mmgive |
| `EmberAshBrute` | `EmberDailyAsh.yml` | `damage 0.5 r5 ~onTimer:40` | `skill{s=EmberAshBurstCast} @self ~onTimer:80` | SoftHit / Ignite / HP **200** / 掉落 |
| `EmberDailyTideBrute` | `EmberDailyTide.yml` | `damage 0.4 r5 ~onTimer:40` **及** `SLOW r5 ~onTimer:50` | `skill{s=EmberTideCrashCast} @self ~onTimer:80` | SoftHit / HP **205** / 掉落（SLOW 改由 Cast 结算带） |
| `EmberDailySpireWarden` | `EmberDailySpire.yml` | `damage 0.45 r5 ~onTimer:45` | `skill{s=EmberSpireSlamCast} @self ~onTimer:80` | SoftHit / throw / 逼近 `message ~onTimer:180` / HP **215** / 掉落 |

### 3) 明确零改

- 霜晶 `EmberFrostNovaCast` / `EmberDailyFrostBrute`
- 锈轨 `EmberRailSlamCast` / `EmberDailyRailWarden`
- 地窖 `EmberDailyCryptWarden`（后置）
- 七线 DP `monster.yml` / 体力 / SoftHit 概率 / Display

## 热更

- FIFO `scripts/console.sh play "mm reload"`（02:57:37 CST）
- MythicMobs：**113** 怪 · **28** 技能（+4 Cast）· 重载完毕 **126** ms
- 无 Yard/Ash/Tide/Spire Cast 相关 ERROR；既有无关 WARN 未新增
- `server-runtime/ops.json` = `[]`；`login-runtime/ops.json` = `[]`

## 测岗建议（复用霜/锈五条）

每线短抽：提示先于该次半径伤；圈内 ≈1.2（潮蚀另验轻 SLOW）；拉开 drop=0；DP/掉落/SoftHit/逼近（断塔）零 diff；无 `$kill-any`。
