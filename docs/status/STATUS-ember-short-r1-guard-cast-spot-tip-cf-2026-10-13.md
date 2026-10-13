# 状态 · TIP-CF 验收：短征 R1 GuardCast（sx01–19）

**日期：** 2026-10-13（上海时间）  
**上游 tip：** `1c90911ef` · D13311 MM GuardCast  
**测岗：** 余烬-测试岗 · 证据 `/workspace/tmp/tip-cf-guard-cast/`  
**裁决：** **PASS**

## 人话

sx01–19 R1 **近战**均挂轻压 GuardCast（dmg0.32 / delay35 / r4 / CD12 / ~onTimer:140），弱于本本 EliteCast（0.52/t100）再弱于 Boss Cast（0.85）。抽 sx01+sx07+sx13 进 R1 实见蓄力喊话；sx19 地图壳缺失，同技能实例内 spawn 实见喊话。远程 Crossbow/Skeleton **无** GuardCast。

## 验收

| ID | 结果 | 证据 |
|----|------|------|
| V1 静态 Skills ×19 GuardCast | **PASS** | `10-static-full.json` · 19/19 dmg0.32 delay35 r4 CD12 粒子+喊话 |
| V1 ~onTimer:140 仅近战 | **PASS** | Zombie/Patrol/Guard；Crossbow/Skeleton/Archer 零挂 |
| V1 SoftHit / Elite·Boss Cast / HP·掉落未拧 | **PASS** | tip diff 仅 +1 GuardCast 行 + Skills 追加；Elite 仍 0.52/t100 |
| V1 弱于 Elite 弱于 Boss | **PASS** | 0.32 &lt; 0.52 &lt; 0.85 |
| V2 sx01 R1 蓄力喊话 | **PASS** | `【烬门】哨卒轻扫——侧移！` · `60-sx01.json` / `51-r1-sx01.json` |
| V2 sx07 R1 蓄力喊话 | **PASS** | `【烬塔】塔卫轻坠——侧移！` · `60-sx07.json` |
| V2 sx13 R1 蓄力喊话（中高段） | **PASS** | `【衡梁】衡卫轻斩——侧移！` · `60-sx13.json` |
| V2 sx19 GuardCast 喊话 | **PASS** | `【烬弦】弦卫轻斩——侧移！` · `75-sx19-inst-spawn.json`（地图缺失→实例内 spawn） |
| V2 可躲窗口 | **PASS（规格+喊话）** | delay35 + 粒子 YAML；mineflayer 不可视粒子 |
| V4 零抬日表/sx20 | **PASS** | tip 仅 MM Skills+mobs |
| V8 Stage2/afk/gate_daily | **PASS** | 三开 true · daily_kills=2400 · gate_daily=no · `70-gate-retest.json` |

## 旁注（不挡绿）

- 粒子包 bot 不可视；可读性以喊话+delay35+YAML 粒子为准
- sx19 地图 `ember_short_sx19` 不存在（WE/壳 PARTIAL）；同技能 `EmberSx19Guard` 在 sx01 实例内 `mm mobs spawn` 实见 Cast，不挡绿
- ≥3 本：sx01（首）+ sx07（中）+ sx19（尾技能）；另补 sx13 原生 R1 绿
- 未改 Stage2 / afk / gate_daily；≠关观察；未停服

## 不动

Stage2 三开关 · afk.tiers/daily_kills · gate_daily · ×0.97 · 观察窗 · login/proxy/MariaDB

*TIP-CF · tip 1c90911ef · PASS*
