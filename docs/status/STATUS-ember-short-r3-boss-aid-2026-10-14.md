# 状态 · D13318：短征 R3 Boss 半血轻添头（MM-only）

**日期：** 2026-10-14（上海时间）  
**上游：** TIP-CM APPROVED A·M · tip `661e73acf` · DESIGN `DESIGN-ember-short-r3-boss-aid-readable-2026-10-14.md` §3 · SPEC `TIP-ember-short-r3-boss-aid-readable-thin-d13318-2026-10-14.md`  
**裁决：** sx01–19 全线 Warden 半血喊「添头」并 summon **1×本本 R1 近战** · Cooldown **9999** 一场一次 · 挂钩 **`~onDamaged`**（未用 fallback `~onTimer:160`）· **零改** BossCast/Guard/Archer/Elite Cast 数值/timer · **零改** HP/掉落/Type/Equipment/地图/DP · **≠**插件 · **≠**afk/Stage2/gate_daily/旧七线 · **≠**sx20 · **禁** summon≥2 / Elite/Boss  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

终厅 Boss 打到约半血会喊「半血添头——先清！」并在身边刷出 1 只本本近战小怪；同一场只会添一次。Cast 波不动。

## 挂钩方式

| 项 | 口径 |
|----|------|
| 技能条件（现网） | `Conditions: - health{h=<120}` — MM 4.11 **无** `healthpercent`（reload 报 Failed to load custom condition）；现网条件名=`health`（别名 `hp`），字段 `h`/`a`/`amount`，**不支持** `%`（`50%`→NumberFormatException）。全线 Warden `Health: 240` → `<120` ≈ 半血 |
| tip 原文 | `healthpercent{percent=<50}` — **未采用**（现网 jar 无此条件；按 tip「MM 4.11 语法按现网」改写） |
| Cooldown | **9999**（一场一次） |
| Warden 挂接 | `- skill{s=EmberSxNNBossAid} @self ~onDamaged` |
| SoftHit + BossCast | **保留**（SoftHit `~onAttack` · BossCast `~onTimer:70`） |
| fallback | **未启用**（半血条件已用现网 `health{h=<120}` 干净加载；未挂 `~onTimer:160`；禁静默双挂） |

## R1 召唤表（19/19）

| 本 | BossAid | 主题括号 | summon R1 | 挂接 Warden |
|----|---------|----------|-----------|-------------|
| sx01 | EmberSx01BossAid | 【烬门】 | EmberSx01Zombie | EmberSx01Warden |
| sx02 | EmberSx02BossAid | 【锈灯】 | EmberSx02Guard | EmberSx02Warden |
| sx03 | EmberSx03BossAid | 【霜雾】 | EmberSx03Patrol | EmberSx03Warden |
| sx04 | EmberSx04BossAid | 【烬井】 | EmberSx04Guard | EmberSx04Warden |
| sx05 | EmberSx05BossAid | 【裂谷】 | EmberSx05Guard | EmberSx05Warden |
| sx06 | EmberSx06BossAid | 【环廊】 | EmberSx06Guard | EmberSx06Warden |
| sx07 | EmberSx07BossAid | 【烬塔】 | EmberSx07Guard | EmberSx07Warden |
| sx08 | EmberSx08BossAid | 【错层】 | EmberSx08Guard | EmberSx08Warden |
| sx09 | EmberSx09BossAid | 【烬渠】 | EmberSx09Guard | EmberSx09Warden |
| sx10 | EmberSx10BossAid | 【密封闸】 | EmberSx10Guard | EmberSx10Warden |
| sx11 | EmberSx11BossAid | 【合镜】 | EmberSx11Guard | EmberSx11Warden |
| sx12 | EmberSx12BossAid | 【环枢】 | EmberSx12Guard | EmberSx12Warden |
| sx13 | EmberSx13BossAid | 【衡梁】 | EmberSx13Guard | EmberSx13Warden |
| sx14 | EmberSx14BossAid | 【烬裂】 | EmberSx14Guard | EmberSx14Warden |
| sx15 | EmberSx15BossAid | 【烬旋】 | EmberSx15Guard | EmberSx15Warden |
| sx16 | EmberSx16BossAid | 【烬影】 | EmberSx16Guard | EmberSx16Warden |
| sx17 | EmberSx17BossAid | 【烬潮】 | EmberSx17Guard | EmberSx17Warden |
| sx18 | EmberSx18BossAid | 【烬晶】 | EmberSx18Guard | EmberSx18Warden |
| sx19 | EmberSx19BossAid | 【烬弦】 | EmberSx19Guard | EmberSx19Warden |

喊话统一：`&X【主题】&f半血添头——&7先清！`（颜色对齐本本 BossCast 前缀）。辅：轻粒子 + `mob.zombie.metal`（非主交付）。

## 改动文件

| 文件 | 改动 |
|------|------|
| `plugins/MythicMobs/Skills/EmberDungeonDiffSkills.yml` | 追加 19× `EmberSxNNBossAid`（零删改既有 Cast） |
| `plugins/MythicMobs/Mobs/EmberSx01.yml` … `EmberSx19.yml` | Warden Skills 各 +1 行 BossAid `~onDamaged`；SoftHit/BossCast/掉落/HP 保留 |
| 本 STATUS | 结案 |

## 热更

| 项 | 时间 (CST) | 结果 |
|----|------------|------|
| FIFO `mm reload`（终态） | 01:20:37 | **189** 怪（不变）· **124** 技能（**105→124 · +19 BossAid**）· **0** Config Error / Failed to construct · 81 ms |
| 试错记录 | 01:19–01:20 | `healthpercent{percent=<50}` → Failed to load custom condition；`health{h=<50%}` → NumberFormatException `50%`；改 `health{h=<120}` 通过 |
| `login-runtime/ops.json` | 01:19 | `[]` |

## 不动

- BossCast / GuardCast / ArcherCast / EliteCast 数值与 timer  
- HP / Damage / Type / Equipment / 掉落 / mmxp / 日帽  
- 地图 / DP / afk / Stage2 / gate_daily / 旧七线 Daily  
- sx20 · 插件 jar · 选页半行 · Cast 粒子打磨主交付

## 验收自检

| ID | 结果 |
|----|------|
| V1 BossAid 19/19 + Warden 挂接 | **PASS（配置）** |
| V2 同场≤1（CD9999） | **PASS（配置）** — 实战抽测总控派 |
| V3 Cast 数值未拧 | **PASS** — diff 仅 Skills 追加 + Warden 一行 |
| V4 无抬表/无 sx20/无半行 | **PASS** |
| V5 SoftHit/掉落保留 | **PASS** |
| V6 挂钩方式记 STATUS | **PASS** — `~onDamaged` + 现网 `health{h=<120}`（无 timer fallback） |

*D13318 · TIP-CM · 短征 R3 BossAid · sx01–19 全线 · MM-only · 挂钩 ~onDamaged · 条件 health{h=<120}。*
