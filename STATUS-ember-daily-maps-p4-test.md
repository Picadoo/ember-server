# STATUS · P4 ember_daily「灰烬庭院」验收

**日期：** 2026-09-27 21:59–22:02 CST（Asia/Shanghai）  
**岗位：** 余烬-测试岗执行器  
**依据：** `docs/STATUS-ember-daily-maps-p4.md`；对照 `STATUS-ember-weekly-elite-maps-p3-test.md` 格式  
**范围：** 菜单进日常 · spawn≈(0,65,0) · 回廊+中央 Boss 垫 · 刷点开放 · 告示/菜单不教打进本指令 · 撤离回枢纽 · 票/波次/MM 只读未改 · ops=[]。**未改**数值/MM/票/波次。

## 总评：PASS

菜单路径进本落地灰烬庭院；刷点 w1/w2/boss 不窒息不虚空；Boss 垫相对回廊 +1；日菜单/本内牌无「进本命令」教学；票波次数量与 MM Health/Damage 未改；测后两侧 ops=[]。

> **版本备注：** 施工基线 CoreRpg **1.15.7**；本轮 play 日志为 **1.15.8 enabled**（22:00 重启加载）。磁盘 jar `plugin.yml` 验收中途已被并发岗换为 **1.15.9**（mtime 22:00:47），以日志运行时为准。

## 分项结果

| # | 项目 | 结果 | 实测 / 证据 |
|---|---|---|---|
| 1 | 菜单进日常 → spawn≈(0,65,0)；回廊+中央抬高 Boss 垫+拱门；非换皮小房 | **PASS** | 账号 `P4ac_7934`：`/ember`→「日常 · 余烬窟」→日菜单「日常 · 灰烬庭院」→「开始挑战」。聊天「[日] 正在进入 灰烬庭院」「[日票] 正在进入……」「灰烬庭院·日 开始！已扣除日票 ×1」。落地 **(0, 65, 0)** 脚下 **`stonebrick`**，feet/head=air。邻域 stonebrick×220 + gravel×89 + cobble×90 + netherrack×48 + glowstone；露天庭院，非小石盒、非精英石英。 |
| 2 | 刷点 w1/w2/boss 不窒息不虚空 | **PASS** | op `/tp` 实测站立：w1≈(-12.3,65.9,7.6) 可站（目标格 remote under=`gravel`）；w2a **(-12.5,65,24.5)** under=`cobblestone`；w2b **(12.5,65,24.5)** under=`stone`；boss **(0.5,66,12.5)** under=`cobblestone`。四处 remote `open=true`（under≠air，feet/head=air）。高度差：Boss 脚 Y**66** vs 西回廊 Y**65**（+1）。 |
| 3 | 日菜单无「进本命令」教学；本内牌不教打指令；撤离/结束回枢纽 | **PASS** | 日菜单「开始挑战」lore 无 `/dp`/`进本命令`；hub 日常 lore=`灰烬庭院 · 回廊清潮后上中央垫`（无 `/dp start EmberDaily`）。进本 action=`corerpg enter daily`。本内告示三块：「回枢纽/打开枢纽菜单…」「灰烬庭院/回廊清潮…」「中央垫/蛮兵在此…」——**无**教打 `/dp`/`/hub`/`/corerpg`。`/dp leave` → **(-18.5, 58, 110.5)** ember_hub quartz。 |
| 4 | 票/波次/MM 未改；ops=[] | **PASS** | monster 数量仍 僵尸×2 → 僵尸×1+骷髅×2 → 蛮兵×1（仅 location 改庭院坐标）。MM `EmberDaily.yml` mtime **14:04**（P4 施工前）；Health/Damage：Zombie **45/4** · Skeleton **35/4** · Brute **180/2**。测后 `server-runtime/ops.json=[]` · `login-runtime/ops.json=[]`。 |

## 关键坐标摘要

| 点 | 实测 pos | 脚下 | 备注 |
|---|---|---|---|
| spawn / 南拱门内 | **(0, 65, 0)** | stonebrick | 菜单进本落地 |
| w1 西回廊 | 目标 (-13,65,6) remote under=gravel；站立≈(-12.3,65.9,7.6) | gravel / netherrack旁 | open |
| w2a 北廊西 | **(-12.5, 65, 24.5)** | cobblestone | open |
| w2b 北廊东 | **(12.5, 65, 24.5)** | stone | open |
| Boss 中央垫 | **(0.5, 66, 12.5)** | cobblestone | 相对回廊 +1 |
| 撤离 hub | **(-18.5, 58, 110.5)** | quartz_block | `/dp leave` |

## 菜单路径

**主路径（本轮采用）：** `/ember` → 日常子菜单 → 「开始挑战」→ 底层 `command: corerpg enter daily`（有票 Lv≥10）。  
**未用：** 裸 `/dp start`（LP `dungeon.start=false` 可能拒）；**未需** `dp start-console` 兜底。

## MM / 波次只读对照

| 项 | 值 | 对照 |
|---|---|---|
| wave1 | EmberDailyZombie @ -13,65,6 ×**2** | 数量同备份，仅坐标改 |
| wave2 | Zombie @ -13,65,24 ×**1** + Skeleton @ 12,65,24 ×**2** | 数量同备份 |
| boss | EmberDailyBrute @ 0,66,12 ×**1** | 数量同备份 |
| EmberDailyZombie | Health **45** Damage **4** | mtime 14:04 未改 |
| EmberDailySkeleton | Health **35** Damage **4** | 未改 |
| EmberDailyBrute | Health **180** Damage **2** | 未改 |

## 环境 / 版本证据

- 日志：`[CoreRpg] Loading CoreRpg v1.15.8` · `CoreRpg 1.15.8 enabled`（22:00:07–12）。  
- 磁盘 jar 验收中途 → **1.15.9**（以运行日志为准，非本岗所改）。  
- 进本：`menu:/ember→日常→开始挑战 (corerpg enter daily)`。  
- 痕迹：`/tmp/p4-daily-accept.js` · `/tmp/p4-daily-accept-result.json` · `/tmp/p4-daily-accept.log`。  
- 本轮未改玩法数值、怪血伤、票价、地图 region、DP option/monster（只读）。

## 收尾 / ops

- 测中临时 OP：`RpgBot`（uuid `148ec8b6-253f-36f4-bcd7-bf2395b8df80`，level 4；短重启 play 写入 ops 后加载）。  
- 测后：`/deop RpgBot`；`server-runtime/ops.json`：**`[]`**；`login-runtime/ops.json`：**`[]`**。  
- play 仍在跑（JAVA_HOME=jdk8u504-b01）；代理 25565 / login 25566 / play 25567。

**结论：** P4 ember_daily 灰烬庭院验收 **PASS**（菜单路径进本；庭院主题可辨；刷点开放；无进本命令教学；票/波次/MM 未改；ops=[]）。
