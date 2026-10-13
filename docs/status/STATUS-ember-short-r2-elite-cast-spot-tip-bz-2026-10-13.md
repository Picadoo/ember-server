# 状态 · TIP-BZ 验收：短征 R2 EliteCast（sx01–19）

**日期：** 2026-10-13（上海时间）  
**上游 tip：** `d39c32bb6` · D13305 MM EliteCast  
**测岗：** 余烬-测试岗 · 证据 `/workspace/tmp/tip-bz-elite-cast/`  
**裁决：** **PASS**

## 人话

sx01–19 Elite 均挂轻压 Cast（dmg0.52 / delay35 / r4 / CD10 / ~onTimer:100），弱于本本 Boss Cast（0.85）。抽 sx01+sx07 进 R2：有精英变体时实见蓄力喊话，可侧移拉开。

## 验收

| ID | 结果 | 证据 |
|----|------|------|
| V1 静态 Skills sx01/19 EliteCast | **PASS** | `10-static-full.json` · 19/19 dmg0.52 delay35 r4 |
| V1 Elite ~onTimer:100 | **PASS** | 19/19 mobs 挂接；SoftHit/掉落保留 |
| V1 SoftHit/Boss Cast/HP/掉落未改 | **PASS** | tip diff 仅 +1 EliteCast 行 + Skills 追加 |
| V1 伤低于 Boss | **PASS** | 0.52&lt;0.85 |
| V2 sx01 R2 蓄力喊话 | **PASS** | `【烬门】伍长哨岗轻扫——拉开！` · `72-sx01-retest.json` |
| V2 sx07 R2 蓄力喊话 | **PASS** | `【烬塔】督卫塔冠轻坠——退进侧窗廊龛或下撤半层！` · `52-r2-sx07.json` |
| V2 可躲窗口 | **PASS（规格+喊话）** | delay35 + 粒子 YAML；mineflayer 不可视粒子 |
| V8 Stage2/afk/gate_daily | **PASS** | 三开 true · daily_kills=2400 · gate_daily=no |

## 旁注（不挡绿）

- sx01/sx07 R2 有 a/b 变体；b=`melee3+ranged1` **无精英** → 无 EliteCast（首抽 sx01 命中 b，复测 a 绿）
- 粒子包 bot 不可视；可读性以喊话+delay+YAML 粒子为准
- TIP-BY 并行；本号独立 bot，未为本抽停服

## 不动

Stage2 三开关 · afk.tiers/daily_kills · gate_daily · ×0.97 · 观察窗 · login/proxy/MariaDB

*TIP-BZ · tip d39c32bb6 · PASS*
