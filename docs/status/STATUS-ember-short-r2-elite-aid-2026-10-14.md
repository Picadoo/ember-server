# 状态 · D13319：短征 R2 Elite 半血轻添头（MM-only）

**日期：** 2026-10-14（上海时间）  
**上游：** TIP-CN APPROVED A·M · tip `29495eb92` · DESIGN `DESIGN-ember-short-r2-elite-aid-readable-2026-10-14.md` §3 · SPEC `TIP-ember-short-r2-elite-aid-readable-thin-d13319-2026-10-14.md`  
**裁决：** sx01–19 全线 Elite 半血喊「精英添头」并 summon **1×本本 R1 近战** · Cooldown **9999** 一场一次 · 挂钩 **`~onDamaged`**（未用 fallback `~onTimer:120`）· **零改** EliteCast/Guard/Archer/BossCast/BossAid/Warden · **零改** HP/掉落/Type/Equipment/地图/DP · **≠**插件 · **≠**afk/Stage2/gate_daily/旧七线 · **≠**sx20 · **禁** summon≥2 / Elite/Boss  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

中房 Elite 打到约半血会喊「精英添头——先清！」并在身边刷出 1 只本本近战小怪；同一场只会添一次。EliteCast 与终厅 BossAid 不动。

## 挂钩方式

| 项 | 口径 |
|----|------|
| 技能条件（现网） | `Conditions: - health{h=<60}` — 对齐 BossAid tip `ee36f5af9`：MM 4.11 **无** `healthpercent`；现网条件名=`health`，字段 `h`。全线 Elite `Health: 120` → `<60` ≈ 半血 |
| tip 原文 | `health{h=<60}`（已对齐绝对值） |
| Cooldown | **9999**（一场一次） |
| Elite 挂接 | `- skill{s=EmberSxNNEliteAid} @self ~onDamaged` |
| SoftHit + EliteCast | **保留**（SoftHit `~onAttack` · EliteCast `~onTimer:100`） |
| fallback | **未启用**（半血条件沿用 BossAid 现网 `health{h=<N}` 范式；未挂 `~onTimer:120`；禁静默双挂） |

## R1 召唤表（19/19）

| 本 | EliteAid | 主题括号 | summon R1 | 挂接 Elite |
|----|----------|----------|-----------|------------|
| sx01 | EmberSx01EliteAid | 【烬门】 | EmberSx01Zombie | EmberSx01Elite |
| sx02 | EmberSx02EliteAid | 【锈灯】 | EmberSx02Guard | EmberSx02Elite |
| sx03 | EmberSx03EliteAid | 【霜雾】 | EmberSx03Patrol | EmberSx03Elite |
| sx04 | EmberSx04EliteAid | 【烬井】 | EmberSx04Guard | EmberSx04Elite |
| sx05 | EmberSx05EliteAid | 【裂谷】 | EmberSx05Guard | EmberSx05Elite |
| sx06 | EmberSx06EliteAid | 【环廊】 | EmberSx06Guard | EmberSx06Elite |
| sx07 | EmberSx07EliteAid | 【烬塔】 | EmberSx07Guard | EmberSx07Elite |
| sx08 | EmberSx08EliteAid | 【错层】 | EmberSx08Guard | EmberSx08Elite |
| sx09 | EmberSx09EliteAid | 【烬渠】 | EmberSx09Guard | EmberSx09Elite |
| sx10 | EmberSx10EliteAid | 【密封闸】 | EmberSx10Guard | EmberSx10Elite |
| sx11 | EmberSx11EliteAid | 【合镜】 | EmberSx11Guard | EmberSx11Elite |
| sx12 | EmberSx12EliteAid | 【环枢】 | EmberSx12Guard | EmberSx12Elite |
| sx13 | EmberSx13EliteAid | 【衡梁】 | EmberSx13Guard | EmberSx13Elite |
| sx14 | EmberSx14EliteAid | 【烬裂】 | EmberSx14Guard | EmberSx14Elite |
| sx15 | EmberSx15EliteAid | 【烬旋】 | EmberSx15Guard | EmberSx15Elite |
| sx16 | EmberSx16EliteAid | 【烬影】 | EmberSx16Guard | EmberSx16Elite |
| sx17 | EmberSx17EliteAid | 【烬潮】 | EmberSx17Guard | EmberSx17Elite |
| sx18 | EmberSx18EliteAid | 【烬晶】 | EmberSx18Guard | EmberSx18Elite |
| sx19 | EmberSx19EliteAid | 【烬弦】 | EmberSx19Guard | EmberSx19Elite |

喊话统一：`&X【主题】&f精英添头——&7先清！`（颜色/括号对齐本本 BossAid；文案「精英添头」与终厅「半血添头」区分）。辅：轻粒子 + `mob.zombie.metal`（非主交付）。

## 改动文件

| 文件 | 改动 |
|------|------|
| `plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml` | 追加 19× `EmberSxNNEliteAid`（零删改既有 Cast/BossAid） |
| `plugins/MythicMobs/Mobs/EmberSx01.yml` … `EmberSx19.yml` | Elite Skills 各 +1 行 EliteAid `~onDamaged`；SoftHit/EliteCast/掉落/HP 保留；Warden/BossAid 零改 |
| 本 STATUS | 结案 |

## 热更

| 项 | 时间 (CST) | 结果 |
|----|------------|------|
| FIFO `mm reload` | 01:39:26 | **189** 怪（不变）· **143** 技能（**124→143 · +19 EliteAid**）· **0** Config Error / Failed to construct · 156 ms |
| `login-runtime/ops.json` | 01:39 | `[]` |

## 不动

- EliteCast / GuardCast / ArcherCast / BossCast / BossAid 数值与 timer / 挂接  
- Warden Skills（含 BossAid）  
- HP / Damage / Type / Equipment / 掉落 / mmxp / 日帽  
- 地图 / DP / afk / Stage2 / gate_daily / 旧七线 Daily  
- sx20 · 插件 jar · 选页半行 · Cast 粒子打磨主交付

## 验收自检

| ID | 结果 |
|----|------|
| V1 EliteAid 19/19 + Elite 挂接 | **PASS（配置）** |
| V2 同场≤1（CD9999） | **PASS（配置）** — 实战抽测总控派 |
| V3 EliteCast/BossAid/Cast 数值未拧 | **PASS** — diff 仅 Skills 追加 + Elite 一行 |
| V4 无抬表/无 sx20/无半行 | **PASS** |
| V5 SoftHit/掉落保留 | **PASS** |

## 测岗联验（TIP-CN spot · 2026-10-14 CST）

| ID | 结果 | 证据 |
|----|------|------|
| V0 Stage2 三开 · daily_kills=2400 · ×0.97 | **PASS** | `00-switches-pre.json` |
| V1 EliteAid×19 · health{h=<60} · CD9999 · summon1 · Elite ~onDamaged · SoftHit+EliteCast 保留 | **PASS** | `10-static.json` |
| V1 BossAid/Warden 未拧 · Cast 数值未拧 · 零 sx20 | **PASS** | `10-static.json` · tip diff 仅 Skills 追加 + Elite +1 行 |
| V1 mm reload 189/143 | **PASS** | `05-mm-reload.json` · 01:39/复测 pin |
| V2 sx01 喊「精英添头」+场上+1 R1 · secondAid=0 · EliteCast 仍放 | **PASS** | `72-sx01-blade.json` · 喊话「【烬门】精英添头——先清！」· newTypes=[zombie] |
| V2 sx07 同口径 | **PASS** | `72-sx07-blade.json` · 「【烬塔】精英添头——先清！」· +1 zombie · CD0 · 督卫塔冠轻坠 |
| V8 gate_daily=no | **PASS** | `08-gate.json` |

**裁决：** PASS · 证据目录 `/workspace/tmp/tip-cn-elite-aid/` · RESULT.txt=PASS  
**旁注：** 握刃测须 `corerpg p1 give` 发 ember_v1 刃（`ni give gear_ember_blade` 无 P1 属性·钻石剑不吃伤）；条件口径 `health{h=<60}`（Elite HP120）对齐 BossAid `h=<120`。


## tip sha

`021d8e351`

> **总控收口：** CLOSED live · tip `021d8e351` · PASS `17f05e0f1` · 2026-10-14 01:53 Asia/Shanghai
