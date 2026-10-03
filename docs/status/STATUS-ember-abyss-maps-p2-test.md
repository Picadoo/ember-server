# STATUS · P2 ember_abyss 下行竖井地形验收

**日期：** 2026-09-27 21:07–21:14 CST（Asia/Shanghai）  
**岗位：** 余烬-测试岗  
**依据：** `docs/status/STATUS-ember-abyss-maps-p2.md`（CoreRpg 1.15.4）；对照 `docs/status/STATUS-ember-hub-maps-p1b-test.md` 格式  
**范围：** 菜单进本 / 井顶→F12 层高与站立 / 西壁梯子 / 告示语义 / 上浮撤离 / MM 血伤抽检；**未改**玩法数值、怪血伤/掉落、票价、菜单文案以外配置。

## 总评：PASS

六项均有实测证据；菜单优先进本与撤离；两侧 `ops.json=[]`；MM 血伤与 4.3 结案一致。

## 分项结果

| # | 项目 | 结果 | 实测 / 证据 |
|---|---|---|---|
| 1 | 从菜单进深渊 · 井顶 → F1 台面 | **PASS** | 账号 `AbP2a_2805`：`/ember` →「深渊」→「开始下潜」成功（聊天「深渊已开启…本期可下潜至第 12 层」「—— 第 1 层 —— 潮尸×3」）。同本脚本井顶自然落点（`AbP2w_3368` `/dp start` 同 `$teleport`）：**(-40, 90, 274)** 脚下 **`quartz_block`**，头/脚空，下方 dy12=`air`（可下望）。≈3.3s 后 F1：**(-38, 85, 274)** 脚下 **`stonebrick`**，below 有 `air`，非空岛/平板。菜单路径 `AbP2e_9045` 亦自然落到 F1 **(-41.5, 85, 273.9)** `stonebrick`（井顶帧因菜单转场延迟未采到，但 spawn/地形与 `/dp` 同脚本）。井顶告示可见：「上浮撤离 / 打开深渊菜单…」「余烬深渊 / 下行竖井…」。 |
| 2 | F1→F9 层高下降 · 西壁梯子 | **PASS** | op `/tp` 逐层：F1 Y≈86 → F2 80 → F3 75 → F4 70 → F5 65 → F6 60 → F7 55 → F8 50 → F9 45（步长≈5）；各层脚下实心（`stonebrick`）、不卡墙/不虚空。西壁梯子 `x=-49`：F5 采到 `ladder` 于 (-49,62..64,270) 等；F9 同样贯通（各 ≥12 格）。 |
| 3 | F10→12 开阔看守区 | **PASS** | F10 **(-41.5, 38, 266.5)** `nether_brick`；F11 **(-37.5, 33, 274.5)** `nether_brick`；F12 **(-41.5, 28, 266.5)** `nether_brick`。Y 与上层（≥45）分离；材质带下界砖，F12 告示「看守深域 / 第10～12层 / 上浮撤离 / 打开深渊菜单」。 |
| 4 | 刷点对齐 · 进层不掉虚空/卡墙 | **PASS** | `option.yml` spawn/teleport **(-40,90,274)**；`monster.yml` 各 floor 主刷点与 STATUS 表一致（抽检 F1/F5/F9/F10/F11/F12 全 `okStand`）。菜单进本与 `/tp` 落点均脚下实心、头空。 |
| 5 | 上浮撤离（菜单） | **PASS** | 本内 `/ember` 开「深渊 · 无尽层」→ 点「上浮撤离」；聊天「结算完成 · 层 5 → 碎片×12…」「上浮撤离 · 前往 hub」；落点 **(-18.5, 58, 110.5)**（ember_hub）。告示文案含「上浮撤离 / 打开深渊菜单」，**无** `/hub` `/dp` `/corerpg` 教打指令。未用手打 evacuate 指令作主路径。 |
| 6 | MM 血伤抽检 · ops=[] | **PASS** | 见下表；`server-runtime/ops.json=[]` · `login-runtime/ops.json=[]`。本轮未改 `EmberAbyss.yml`（mtime 仍 17:25）。 |

## 关键坐标摘要

| 点 | 实测 pos | 脚下 | 备注 |
|---|---|---|---|
| 井顶 spawn | **(-40, 90, 274)** | quartz_block | 自然进本 dt≈0.3s |
| F1 | **(-38, 85, 274)** / 菜单 (-41.5,85,273.9) | stonebrick | below 有 air |
| F5 | (-37.5, 65, 274.5) | stonebrick | 侧壁撤离牌 |
| F9 | (-37.5, 45, 274.5) | stonebrick | |
| F10 | (-41.5, 38, 266.5) | nether_brick | 开阔看守 |
| F12 | (-41.5, 28, 266.5) | nether_brick | 井底牌 |
| 撤离回枢纽 | **(-18.5, 58, 110.5)** | — | 菜单「上浮撤离」 |

西壁梯子：`x=-49` ladder 贯通（F5/F9 抽检）。

## option / monster 对照（只读）

| 来源 | 坐标 |
|---|---|
| `option.yml` `$setspawn` / `$teleport` | (-40,90,274) |
| `monster.yml` floor1…9 主点 | (-38,85,274)…(-38,45,274) 步长 Y-5；偶层偏 -Z |
| floor10/11/12 | (-42,38,266) / (-38,33,274) / (-42,28,266) |

## MM 血伤抽检（只读）

| Mob | Health | Damage | 对照 |
|---|---|---|---|
| EmberAbyssZombie | 110 | 5 | OK |
| EmberAbyssMix | 130 | 5 | OK |
| EmberAbyssSkeleton | 80 | 4 | OK |
| EmberAbyssBrute | 450 | 4 | OK |
| EmberAbyssWatcher | 1500 | 3 | OK |
| EmberAbyssWatcherDeep | **6000** | **18** | 与 4.3 结案一致 |

## 环境 / 版本证据

- 日志：`CoreRpg 1.15.4 enabled`（本轮多次短重启后均确认）。
- 进本路径：**菜单优先**（`AbP2a_2805`）；井顶自然帧补采用同脚本 `/dp start`（`AbP2w_3368`），报告已标明。
- 痕迹：`/tmp/abyss-p2-accept.js` · `/tmp/abyss-p2-accept-result.json` · `/tmp/abyss-p2-enter-sample-result.json` · `/tmp/abyss-p2-welltop-result.json`。
- 本轮未改玩法数值、怪血伤/掉落、票价、TrMenu 文案、地图 region。

## 收尾 / ops

- 测中临时 OP：`RpgBot`（uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`，level 4）。
- 测后：`/deop RpgBot`；`server-runtime/ops.json`：**`[]`**；`login-runtime/ops.json`：**`[]`**。
- play 仍在跑（`./start.sh custom`，JAVA_HOME=jdk8u504-b01）；代理 25565 / login 25566 / play 25567。

**结论：** P2 ember_abyss 下行竖井地形验收 **PASS**。
