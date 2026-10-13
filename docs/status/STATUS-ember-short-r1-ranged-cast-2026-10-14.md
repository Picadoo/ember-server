# 状态 · D13314：短征 R1 远程卫可读轻 Cast（MM-only）

**日期：** 2026-10-14（上海时间）  
**上游：** TIP-CI APPROVED A·M · tip `1b05cef46` · DESIGN `DESIGN-ember-short-r1-ranged-cast-readable-2026-10-14.md` §3  
**裁决：** sx01–19 全线 R1 **远程**挂轻压主题 ArcherCast · **弱于**本本 GuardCast · **零改** HP/掉落/Type/Equipment/地图/DP/Guard·Elite·Boss Cast · **≠**插件 · **≠**afk/Stage2/gate_daily/旧七线 · **禁**挂近战 Guard/Zombie/Patrol · **≠**sx20  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

短征第一房远程弓/弩卫也会「蓄力→可躲」：喊话+主题粒子 → delay 35 → r4 结算伤 0.25。比 GuardCast（0.32 / timer140 / CD12）更轻、更慢；比 EliteCast（0.52）/ Boss Cast（0.85）再低。近战卫**不**挂本窗技能。

## 数值钉死（全线同形）

| 项 | Boss Cast | EliteCast | GuardCast（近战） | **ArcherCast（本窗·远程）** |
|----|-----------|-----------|-------------------|------------------------------|
| Timer | `~onTimer:70` | `~onTimer:100` | `~onTimer:140` | **`~onTimer:180`** |
| 结算伤 | 0.85 | 0.52 | 0.32 | **0.25** |
| 蓄力 delay | 30 | 35 | 35 | **35** |
| 半径 | r5 | r4 | r4 | **r4** |
| CD | 8 | 10 | 12 | **15** |
| SLOW | 仅 sx03 Boss | 全线无 | 全线无 | **全线无** |

梯队：**0.25 &lt; 0.32 &lt; 0.52 &lt; 0.85**；Timer **180 ≠ 140**（禁同 Timer 双压）。

## 覆盖表（19/19）

| 本 | ArcherCast | 主题 | 挂接远程 ID | SoftHit 保留 |
|----|------------|------|-------------|--------------|
| sx01 | EmberSx01ArcherCast | 哨弓轻矢 | EmberSx01Skeleton | 是 |
| sx02 | EmberSx02ArcherCast | 栈弩轻矢 | EmberSx02Archer | 是 |
| sx03 | EmberSx03ArcherCast | 闸弩轻楔矢 | EmberSx03Crossbow | 是 |
| sx04 | EmberSx04ArcherCast | 井弩轻焰矢 | EmberSx04Crossbow | 是 |
| sx05 | EmberSx05ArcherCast | 崖弩轻剪矢 | EmberSx05Crossbow | 是 |
| sx06 | EmberSx06ArcherCast | 环弩轻弧矢 | EmberSx06Crossbow | 是 |
| sx07 | EmberSx07ArcherCast | 塔弩轻坠矢 | EmberSx07Crossbow | 是 |
| sx08 | EmberSx08ArcherCast | 层弩轻砸矢 | EmberSx08Crossbow | 是 |
| sx09 | EmberSx09ArcherCast | 渠弩轻溅矢 | EmberSx09Crossbow | 是 |
| sx10 | EmberSx10ArcherCast | 闸弩轻压矢 | EmberSx10Crossbow | 是 |
| sx11 | EmberSx11ArcherCast | 镜弩轻扫矢 | EmberSx11Crossbow | 是 |
| sx12 | EmberSx12ArcherCast | 枢弩轻斩矢 | EmberSx12Crossbow | 是 |
| sx13 | EmberSx13ArcherCast | 衡弩轻斩矢 | EmberSx13Crossbow | 是 |
| sx14 | EmberSx14ArcherCast | 裂弩轻斩矢 | EmberSx14Crossbow | 是 |
| sx15 | EmberSx15ArcherCast | 旋弩轻斩矢 | EmberSx15Crossbow | 是 |
| sx16 | EmberSx16ArcherCast | 影弩轻斩矢 | EmberSx16Crossbow | 是 |
| sx17 | EmberSx17ArcherCast | 潮弩轻斩矢 | EmberSx17Crossbow | 是 |
| sx18 | EmberSx18ArcherCast | 晶弩轻斩矢 | EmberSx18Crossbow | 是 |
| sx19 | EmberSx19ArcherCast | 弦弩轻斩矢 | EmberSx19Crossbow | 是 |

粒子对齐本本 GuardCast 主题（1.12.2 已用集：flame/lava/crit/cloud/smoke/waterdrop/splash/explode/spell_witch/happyVillager/fireworkSpark/note）；粒子量约 Guard 七成。喊话「…——侧移！」；**禁**原样复读 Guard/Elite/Boss 文案。

## 改动文件

| 文件 | 改动 |
|------|------|
| `plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml` | 追加 19× `EmberSxNNArcherCast`（零删改既有 Guard/Elite/Boss） |
| `plugins/MythicMobs/Mobs/EmberSx01.yml` … `EmberSx19.yml` | 远程 Skills 各 +1 行 `skill{s=EmberSxNNArcherCast} @self ~onTimer:180`；SoftHit 保留 |
| 本 STATUS | 结案 |

## 热更

| 项 | 时间 (CST) | 结果 |
|----|------------|------|
| FIFO `mm reload` | 00:16:11 | **189** 怪（不变）· **105** 技能（**86→105 · +19 ArcherCast**）· 303 ms |
| `login-runtime/ops.json` | 00:16 | `[]` |
| `server-runtime/ops.json` | 00:16 | 测试 bot ops 仍在（8 条 · 他线 TIP 测岗遗留 · **本窗未改**）；login 已空 |

## 不动

- GuardCast / EliteCast / Boss Cast 本体数值 / timer  
- Guard/Elite/Boss/远程 HP·Damage·Type·Equipment·掉落·mmxp  
- 地图 / DP / afk / Stage2 / gate_daily / 旧七线 Daily  
- sx20 · 插件 jar · 选页文案（W1d 非主交付）

## 验收自检

| ID | 结果 |
|----|------|
| V1 蓄力可读（规格） | **PASS（配置）** — 19 Cast 均 message+粒子+delay35+结算；实战抽测另派 |
| V2 伤梯队 | **PASS** — 0.25&lt;0.32&lt;0.52&lt;0.85 · timer180&gt;140 · CD15≥12 · r4=r4 |
| V3 仅远程 / 近战仍 GuardCast | **PASS** — 仅 Skeleton/Archer/Crossbow；Zombie/Patrol/Guard 无 ArcherCast |
| V4 无抬表/无 sx20 | **PASS** — diff 仅 Skills 追加 + 远程一行挂接 |
| V5 SoftHit/掉落保留 | **PASS** |
| V6 无裸指令 | **PASS** — 玩家面仅喊话 |

*D13314 · 短征 R1 ArcherCast · sx01–19 全线 · tip 见 commit。*
