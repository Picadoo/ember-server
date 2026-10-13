# 状态 · D13305：短征 R2 精英可读 Cast（MM-only）

**日期：** 2026-10-13（上海时间）  
**上游：** TIP-BZ APPROVED A·M · tip `d688ed5af` · DESIGN `DESIGN-ember-short-r2-elite-cast-readable-2026-10-13.md` §3  
**裁决：** sx01–19 全线 Elite 挂轻压主题 Cast · **弱于**本本 Boss Cast · **零改** HP/掉落/Type/Equipment/地图/DP/Boss Cast · **≠**插件 · **≠**afk/Stage2/gate_daily/旧七线  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

短征第二房精英也会「蓄力→可躲」：喊话+主题粒子 → delay 35 → r4 结算伤 0.52。比 Boss Cast（0.85 / delay 30 / r5 / timer70）更轻、更慢。sx03 霜雾 Elite **不加** SLOW（Boss 有轻减速，Elite 再降一档）。

## 数值钉死（全线同形）

| 项 | Boss Cast（对照 live） | EliteCast（本窗） |
|----|------------------------|-------------------|
| Timer | `~onTimer:70` | **`~onTimer:100`** |
| 结算伤 | 0.85 | **0.52** |
| 蓄力 delay | 30 | **35** |
| 半径 | r5 | **r4** |
| CD | 8 | **10** |
| SLOW | 仅 sx03 Boss | **全线无** |

## 覆盖表（19/19）

| 本 | EliteCast | 主题 | Elite ID | 已挂 SoftHit+Cast |
|----|-----------|------|----------|-------------------|
| sx01 | EmberSx01EliteCast | 哨岗轻扫 | EmberSx01Elite | 是 |
| sx02 | EmberSx02EliteCast | 锈灯轻落点 | EmberSx02Elite | 是 |
| sx03 | EmberSx03EliteCast | 霜雾轻楔 | EmberSx03Elite | 是 |
| sx04 | EmberSx04EliteCast | 井心轻喷 | EmberSx04Elite | 是 |
| sx05 | EmberSx05EliteCast | 风剪轻条 | EmberSx05Elite | 是 |
| sx06 | EmberSx06EliteCast | 环扫轻弧 | EmberSx06Elite | 是 |
| sx07 | EmberSx07EliteCast | 塔冠轻坠 | EmberSx07Elite | 是 |
| sx08 | EmberSx08EliteCast | 层间轻砸 | EmberSx08Elite | 是 |
| sx09 | EmberSx09EliteCast | 渠心轻溅 | EmberSx09Elite | 是 |
| sx10 | EmberSx10EliteCast | 闸心轻压 | EmberSx10Elite | 是 |
| sx11 | EmberSx11EliteCast | 镜面轻扫 | EmberSx11Elite | 是 |
| sx12 | EmberSx12EliteCast | 枢扫轻斩 | EmberSx12Elite | 是 |
| sx13 | EmberSx13EliteCast | 衡扫轻斩 | EmberSx13Elite | 是 |
| sx14 | EmberSx14EliteCast | 裂扫轻斩 | EmberSx14Elite | 是 |
| sx15 | EmberSx15EliteCast | 旋扫轻斩 | EmberSx15Elite | 是 |
| sx16 | EmberSx16EliteCast | 影扫轻斩 | EmberSx16Elite | 是 |
| sx17 | EmberSx17EliteCast | 潮扫轻斩 | EmberSx17Elite | 是 |
| sx18 | EmberSx18EliteCast | 晶扫轻斩 | EmberSx18Elite | 是 |
| sx19 | EmberSx19EliteCast | 弦扫轻斩 | EmberSx19Elite | 是 |

粒子对齐本本 Boss Cast（1.12.2 已用集：flame/lava/crit/cloud/smoke/waterdrop/splash/explode/spell_witch/happyVillager/fireworkSpark/note）；粒子量约 Boss 六成。

## 改动文件

| 文件 | 改动 |
|------|------|
| `plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml` | 追加 19× `EmberSxNNEliteCast` |
| `plugins/MythicMobs/Mobs/EmberSx01.yml` … `EmberSx19.yml` | Elite Skills 各 +1 行 `skill{s=EmberSxNNEliteCast} @self ~onTimer:100`；SoftHit 保留 |
| 本 STATUS | 结案 |

## 热更

| 项 | 时间 (CST) | 结果 |
|----|------------|------|
| FIFO `mm reload` | 21:34:53 | **189** 怪（不变）· **67** 技能（**48→67 · +19 EliteCast**）· 147 ms |
| `login-runtime/ops.json` | 21:34 | `[]` |
| `server-runtime/ops.json` | 21:34 | `[]` |

## 不动

- Boss Cast 本体数值 / timer  
- Elite/Boss HP·Damage·Type·Equipment·掉落·mmxp  
- 地图 / DP / afk / Stage2 / gate_daily / 旧七线 Daily  
- sx20 · 插件 jar · 选页文案（W1d 非主交付）

## 验收自检

| ID | 结果 |
|----|------|
| V1 蓄力可读（规格） | **PASS（配置）** — 19 Cast 均 message+粒子+delay35+结算；实战测另派 |
| V2 弱于 Boss | **PASS** — 0.52&lt;0.85 · timer100&gt;70 · delay35≥30 · r4≤r5 · CD10≥8 |
| V3 无抬表/无 sx20 | **PASS** — diff 仅 Skills 追加 + Elite 一行挂接 |
| V4 SoftHit/掉落保留 | **PASS** |
| V5 无裸指令 | **PASS** — 玩家面仅喊话 |

*D13305 · 短征 R2 EliteCast · sx01–19 全线 · tip 见 commit。*
