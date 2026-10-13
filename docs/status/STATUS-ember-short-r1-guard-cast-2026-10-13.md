# 状态 · D13311：短征 R1 近战卫可读轻 Cast（MM-only）

**日期：** 2026-10-13（上海时间）  
**上游：** TIP-CF APPROVED A·M · tip `00f3fd0f9` · DESIGN `DESIGN-ember-short-r1-guard-cast-readable-2026-10-13.md` §3  
**裁决：** sx01–19 全线 R1 **近战**挂轻压主题 GuardCast · **弱于**本本 EliteCast · **零改** HP/掉落/Type/Equipment/地图/DP/Elite·Boss Cast · **≠**插件 · **≠**afk/Stage2/gate_daily/旧七线 · **禁**挂 Crossbow/Skeleton · **≠**sx20  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

短征第一房近战卫也会「蓄力→可躲」：喊话+主题粒子 → delay 35 → r4 结算伤 0.32。比 EliteCast（0.52 / timer100 / CD10）更轻、更慢；比 Boss Cast（0.85 / timer70）再低一档。远程弩/弓手**不**挂。

## 数值钉死（全线同形）

| 项 | Boss Cast | EliteCast（D13305） | **GuardCast（本窗）** |
|----|-----------|---------------------|------------------------|
| Timer | `~onTimer:70` | `~onTimer:100` | **`~onTimer:140`** |
| 结算伤 | 0.85 | 0.52 | **0.32** |
| 蓄力 delay | 30 | 35 | **35** |
| 半径 | r5 | r4 | **r4** |
| CD | 8 | 10 | **12** |
| SLOW | 仅 sx03 Boss | 全线无 | **全线无** |

## 覆盖表（19/19）

| 本 | GuardCast | 主题 | 挂接近战 ID | SoftHit 保留 |
|----|-----------|------|-------------|--------------|
| sx01 | EmberSx01GuardCast | 哨卒轻扫 | EmberSx01Zombie | 是 |
| sx02 | EmberSx02GuardCast | 栈卫轻落点 | EmberSx02Guard | 是 |
| sx03 | EmberSx03GuardCast | 闸卫轻楔 | **EmberSx03Patrol**（本本近战 id≠Guard） | 是 |
| sx04 | EmberSx04GuardCast | 井卫轻焰 | EmberSx04Guard | 是 |
| sx05 | EmberSx05GuardCast | 崖卫轻剪 | EmberSx05Guard | 是 |
| sx06 | EmberSx06GuardCast | 环卫轻弧 | EmberSx06Guard | 是 |
| sx07 | EmberSx07GuardCast | 塔卫轻坠 | EmberSx07Guard | 是 |
| sx08 | EmberSx08GuardCast | 层卫轻砸 | EmberSx08Guard | 是 |
| sx09 | EmberSx09GuardCast | 渠卫轻溅 | EmberSx09Guard | 是 |
| sx10 | EmberSx10GuardCast | 闸卒轻压 | EmberSx10Guard | 是 |
| sx11 | EmberSx11GuardCast | 镜卫轻扫 | EmberSx11Guard | 是 |
| sx12 | EmberSx12GuardCast | 枢卫轻斩 | EmberSx12Guard | 是 |
| sx13 | EmberSx13GuardCast | 衡卫轻斩 | EmberSx13Guard | 是 |
| sx14 | EmberSx14GuardCast | 裂卫轻斩 | EmberSx14Guard | 是 |
| sx15 | EmberSx15GuardCast | 旋卫轻斩 | EmberSx15Guard | 是 |
| sx16 | EmberSx16GuardCast | 影卫轻斩 | EmberSx16Guard | 是 |
| sx17 | EmberSx17GuardCast | 潮卫轻斩 | EmberSx17Guard | 是 |
| sx18 | EmberSx18GuardCast | 晶卫轻斩 | EmberSx18Guard | 是 |
| sx19 | EmberSx19GuardCast | 弦卫轻斩 | EmberSx19Guard | 是 |

粒子对齐本本 EliteCast（1.12.2 已用集：flame/lava/crit/cloud/smoke/waterdrop/splash/explode/spell_witch/happyVillager/fireworkSpark/note）；粒子量约 Elite 六成。

**旁注：** sx03 R1 近战 live id 为 `EmberSx03Patrol`（非 `Guard`）；按「仅近战主怪」挂接，技能名仍用 `EmberSx03GuardCast`。Crossbow/Skeleton/Archer **零挂**。

## 改动文件

| 文件 | 改动 |
|------|------|
| `plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml` | 追加 19× `EmberSxNNGuardCast` |
| `plugins/MythicMobs/Mobs/EmberSx01.yml` … `EmberSx19.yml` | 近战 Skills 各 +1 行 `skill{s=EmberSxNNGuardCast} @self ~onTimer:140`；SoftHit 保留 |
| 本 STATUS | 结案 |

## 热更

| 项 | 时间 (CST) | 结果 |
|----|------------|------|
| FIFO `mm reload` | 23:11:44 | **189** 怪（不变）· **86** 技能（**67→86 · +19 GuardCast**）· 242 ms |
| `login-runtime/ops.json` | 23:11 | `[]` |
| `server-runtime/ops.json` | 23:11 | `[]` |

## 不动

- EliteCast / Boss Cast 本体数值 / timer  
- Guard/Elite/Boss HP·Damage·Type·Equipment·掉落·mmxp  
- 地图 / DP / afk / Stage2 / gate_daily / 旧七线 Daily  
- sx20 · 插件 jar · 选页文案（W1d 非主交付）· R1 远程 Cast

## 验收自检

| ID | 结果 |
|----|------|
| V1 蓄力可读（规格） | **PASS（配置）** — 19 Cast 均 message+粒子+delay35+结算；实战测另派 |
| V2 弱于 Elite | **PASS** — 0.32&lt;0.52 · timer140&gt;100 · CD12≥10 · r4=r4 |
| V3 仅近战 / 无远程 | **PASS** — 仅 Zombie/Patrol/Guard；Crossbow/Skeleton 无 GuardCast |
| V4 无抬表/无 sx20 | **PASS** — diff 仅 Skills 追加 + 近战一行挂接 |
| V5 SoftHit/掉落保留 | **PASS** |
| V6 无裸指令 | **PASS** — 玩家面仅喊话 |

*D13311 · 短征 R1 GuardCast · sx01–19 全线 · tip 见 commit。*
