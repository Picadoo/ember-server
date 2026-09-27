# STATUS · P3 ember_weekly 深核廊 + ember_elite 试炼短廊验收

**日期：** 2026-09-27 21:25–21:51 CST（Asia/Shanghai）  
**岗位：** 余烬-测试岗  
**依据：** `docs/STATUS-ember-weekly-elite-maps-p3.md`；对照 `STATUS-ember-abyss-maps-p2-test.md` 格式  
**范围：** 菜单/进本 spawn · 三室可辨 · 精英≠周本 · 廊道折点抽检 · 撤离回枢纽 · DP config 绑定 · CoreRpg/ops · MM 血伤只读；**未改**玩法数值、怪血伤/掉落、票价、菜单进本 action。

## 总评：PASS

地图/绑定/主题/三室站立/撤离/ops/MM 均有实测证据。精英 **未** 误落 weekly。  
**已知风险已验证：** 波末 `$teleport` 跳过廊道（玩法不依赖走廊）；折角廊坐标可到达、主题材质正确——部分抽检 Y 估点落在实心块内（3×3 站立需微调脚高），不阻塞通关。

> **版本备注：** 任务期望 CoreRpg **1.15.5**；验收过程中 jar 被并发岗换为 **1.15.7**（日志 `CoreRpg 1.15.7 enabled`，mtime 21:39）。P3 地图能力在 1.15.7 上复验通过；若硬卡字面 1.15.5 仅版本号不符。

## 分项结果

| # | 项目 | 结果 | 实测 / 证据 |
|---|---|---|---|
| 1 | 周常 EmberWeekly → map ember_weekly · spawn≈(-40,65,270) · 三室石砖/下界 | **PASS** | 账号 `P3b_4924`：进本落地 **(-40, 65, 270)** 脚下 **`stonebrick`**，邻域 stonebrick×102 + nether×13，非空平板。三室 op `/tp`：wave1 **(-40.9,65,281.6)** `stonebrick`；wave2 **(-39.5,68,308.5)** `stonebrick`；wave3 **(-39.5,63,340.5)** `nether_brick`；均 `openOk` + 3×3 stand。告示「回枢纽…」「深核·周 / 前厅→中核→深室…」，**无**教打 `/dp`/`/hub`/`/corerpg`。 |
| 2 | 精英 EmberEliteWeekly → map ember_elite · spawn≈(-35,70,270) · **禁止仍 weekly** | **PASS** | 落地 **(-35, 70, 270)** 脚下 **`gold_block`**，邻域 quartz×122 gold×8；**不是** (-40,65,270)。厅一 (-31,70,269) `quartz_block`；厅二 (-3.5,72,270.5) `gold_block`；厅三 (22.5,68,270.5) `gold_block`。告示「精英试炼 / 三厅短廊 / 石英·金砖中轴」。另：同会话前序 `P3ac_6569` 菜单点「精英试炼」聊天「精英试炼开启…试炼一」（菜单路径可进，随后被炽尸秒杀；本轮地形采样用 `/dp start` + creative）。 |
| 3 | 刷点不虚空不卡墙 · 撤离回枢纽 · 告示/菜单不教打指令 | **PASS** | 两图三室均脚下实心、头脚空。`/dp leave` 后均回 **(-18.5, 58, 110.5)** ember_hub。本内告示无教打指令；TrMenu hub 周常/精英 lore 无 `/dp start` 教打句（进本 action 保留）。 |
| 4 | DP config：weekly 仅 EmberWeekly；elite 仅 EmberEliteWeekly | **PASS** | `config.yml`：`ember_weekly: EmberWeekly:1`；`ember_elite: EmberEliteWeekly:1`。option：`$setmap{ember_weekly}` spawn `-40,65,270`；`$setmap{ember_elite}` spawn `-35,70,270`。波末 teleport：周本 → `(-40,68,308)` / `(-40,63,340)`；精英 → `(-4,72,270)` / `(22,68,270)`（**跳过廊道**）。 |
| 5 | CoreRpg · ops=[] · MM 血伤未改 | **PASS*** | 日志：`CoreRpg 1.15.7 enabled`（*期望 1.15.5，见上注）。`server-runtime/ops.json=[]` · `login-runtime/ops.json=[]`。MM 只读抽检见下；mtime 周本 14:04 / 精英 19:28，本轮未改。 |

## 关键坐标摘要

| 点 | 实测 pos | 脚下 | 备注 |
|---|---|---|---|
| 周本 spawn | **(-40, 65, 270)** | stonebrick | 深核廊入口 |
| 周本 wave1 / 前厅 | (-40.9, 65, 281.6) | stonebrick | stand3x3 |
| 周本 wave2 / 中核 | (-39.5, 68, 308.5) | stonebrick | 波末 TP 目标 |
| 周本 wave3 / 深室 | (-39.5, 63, 340.5) | nether_brick | |
| 精英 spawn | **(-35, 70, 270)** | gold_block | ≠ weekly |
| 精英厅一 | (-31, 70, 269.2) | quartz_block | |
| 精英厅二 | (-3.5, 72, 270.5) | gold_block | 波末 TP |
| 精英厅三 | (22.5, 68, 270.5) | gold_block | |
| 撤离 hub | **(-18.5, 58, 110.5)** | quartz_block | 两图 leave 后 |

## 廊道抽检（已知风险）

**结论：廊道不必必须步行**（`corridor_must_walk=false`）。`monster.yml` 波末 `$teleport` 直达下一室，玩法不依赖走廊。

| 图 | 折点抽检 | 结果 |
|---|---|---|
| 周本 | 经 x≈-52：(-51.5,66,294.5) 脚下 nether_brick（估 Y 偏高卡实心）；旁点 (-51.5,55,300.5) 可站；经 x≈-28：(-27.5,64,330.5) 主题 nether；旁点 y=55 可站 | 折点可达、材质对题；精确走道 Y 需微调，**不阻塞**（有波间 TP） |
| 精英 | 经 z≈260 / z≈280：(-17.5~-19.5,70~71,260.5)、(8.5~10.5,69~70,280.5) 均为 quartz/gold 主题，估 Y 落在实心（feet=quartz/gold） | 轴/材质与周本可辨；3×3 开敞需降 1 格再探；**玩法不依赖** |

## option / config 对照（只读）

| 来源 | 值 |
|---|---|
| EmberWeekly `$setmap` / `$setspawn` | `ember_weekly` / `-40,65,270` |
| EmberEliteWeekly `$setmap` / `$setspawn` | `ember_elite` / `-35,70,270` |
| config `ember_weekly` | 仅 `EmberWeekly: 1` |
| config `ember_elite` | 仅 `EmberEliteWeekly: 1` |
| 周本波末 TP | `-40,68,308` → `-40,63,340` |
| 精英波末 TP | `-4,72,270` → `22,68,270` |

## MM 血伤抽检（只读）

| Mob | Health | Damage | 对照 |
|---|---|---|---|
| EmberWeeklyZombie | 110 | 4 | OK |
| EmberWeeklySkeleton | 70 | 3 | OK |
| EmberWeeklyBruteA | 350 | 4 | OK |
| EmberWeeklyBruteB | 1500 | 2 | OK |
| EmberEliteZombie | 5250 | 4 | OK |
| EmberEliteSkeleton | 3300 | 4 | OK |
| EmberEliteMix | 2700 | 4 | OK |
| EmberEliteBrute | 10500 | 5 | OK |
| EmberEliteBoss | 5200 | 12 | OK |

## 环境 / 版本证据

- 日志：`CoreRpg 1.15.7 enabled`（期望基线 1.15.5；jar mtime 21:39 已升）。  
- 进本：周本/精英本轮地形采样主路径 **`/dp start`**（bot 点 TrMenu「周常 · 深核」子菜单偶发未切到「开始挑战」；精英菜单路径前序账号已证实可进）。  
- 痕迹：`/tmp/p3-accept.js` · `/tmp/p3-accept2.js` · `/tmp/p3-accept-result.json` · `/tmp/p3-accept2-result.json` · `/tmp/p3-accept2.log`。  
- 本轮未改玩法数值、怪血伤/掉落、票价、地图 region、DP option/monster（只读）。

## 收尾 / ops

- 测中临时 OP：`RpgBot`（uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`，level 4）。  
- 测后：`/deop RpgBot`；`server-runtime/ops.json`：**`[]`**；`login-runtime/ops.json`：**`[]`**。  
- play 仍在跑（JAVA_HOME=jdk8u504-b01）；代理 25565 / login 25566 / play 25567。

**结论：** P3 周本深核廊 + 精英独立图验收 **PASS**（精英未误落 weekly；廊道非必步行；CoreRpg 现为 1.15.7）。
