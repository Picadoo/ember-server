# STATUS · P5 灾厄祭坛盆地 + 团本大厅通道 · 测试验收

**日期：** 2026-09-27 22:19–22:28 CST（Asia/Shanghai）  
**岗：** 余烬-测试岗执行器  
**依据：** `docs/design/design-ember-calamity-raid-maps-p5.md` · `docs/status/STATUS-ember-calamity-raid-maps-p5.md`  
**脚本/痕迹：** `/tmp/p5-anvil-accept.py` · `/tmp/p5-anvil-accept-result.json` · `/tmp/p5-accept-live.js` · `/tmp/p5-accept-live-result.json` · `/tmp/p5-cmd-probe.js` · `/tmp/p5-cmd-probe-result.json`  
**OP：** `P5OpBot`（uuid `79e0d242-0578-3d9a-bee2-d31aa1ae4c87`，临时；测完 deop + LP unset）· 玩家 `P5t_3502`  
**Verdict：** **✅ P5 地图验收通过**（灾厄 live + anvil；团本 anvil/结构 PASS，live 进本 **SKIP**；命令 table 路由存在；CoreRpg **1.15.9**；ops=[]；数值/MM 未改）

---

## 总评

| # | 验收项 | 结果 | 证据摘要 |
|---|--------|------|----------|
| 1 | 灾厄 `ember_event` 盆地·落点≠Boss·可站立·告示不教指令 | **PASS** | live `mvtp` 落地 **(-96.5,65,282.5)** `stonebrick`；Boss **(-96.5,64,266.5)** `quartz_block`；环 soul_sand×271 + netherrack×281；外沿相对中央顶差 **3**；告示无 `/hub` `/mvtp` `/dp` |
| 2 | 团本 `ember_raid` 大厅→通道→终厅·左右道真空间·刷点开放 | **PASS*** | *live 进本 SKIP（组队 1/3～5）；离线 anvil 全刷点 `open`；大厅 stone_bricks×525+wool×16；通道 cobble×364+netherrack×27；终厅 quartz×128；左右 Z 差 **14** |
| 3 | admin 命令 `calamitybuild`/`eventbuild`/`raidbuild` 路由存在 | **PASS** | dry `/corerpg … table`（**未** build）；坐标表回显；源码 `CoreRpgPlugin` L773–774 |
| 4 | 团本 live 组队未齐 → SKIP；`ember_calamity` 测图未扩不算 FAIL | **SKIP / N/A** | `dp start-console` →「团本人数 3～5，当前 (1)」；测图未纳入本轮 FAIL 条件 |
| 5 | CoreRpg 1.15.9 · ops=[] · 不改数值/MM | **PASS** | jar+日志 1.15.9；双侧 ops `[]`；`calamity.yml` Boss 仍 (-96.5,64,266.5)；MM mtime **14:04 CST**（施工前） |

---

## 1) 灾厄 · `ember_event`

### Live（`P5t_3502` · OP `mvtp ember_event`）

| 点 | 实测 | 脚下 | 站立 |
|----|------|------|------|
| 玩家 / MV spawn | **(-96.5, 65, 282.5)** | `stonebrick` | 脚/头 `air` · open |
| Boss 台 | **(-96.5, 64, 266.5)** | `quartz_block` | open |
| 高度差（外沿 r≈24 顶 Y66 − 中央顶 Y63） | **drop=3** | — | 非整盘同 Y 平板 |
| 环区采样（中心约 -97,62,266 · r20） | soul_sand **271** · netherrack **281** · quartz_block 台区 **90** · stonebrick **1753** · glow **13** | — | 盆地主题可辨 |

**告示（扫到，无教打指令）：**
- `灾厄祭坛 / 南入口平台 / 面向中央台 / 盆地·仪式场`（-99,65,282）
- `回枢纽 / 打开枢纽菜单 / 返回 / 不教打指令`（-95,65,282）
- `灾厄使 / 中央台 / 窗内苏醒 / 公共祭坛`（-91,64,266）

**配置只读：** `plugins/CoreRpg/calamity.yml` `boss` = **(-96.5, 64.0, 266.5)**（与设计/施工一致，未改数值逻辑）。  
**MV：** `worlds.yml` spawnLocation **x=-96.5 y=65.0 z=282.5 yaw=180**。

**菜单：** `ember_calamity.yml` lore/tell **无** `/hub` `/mvtp` `/dp` 教学；正式钮 `corerpg calamity join`；测试钮标「仅管理/测试」。  
（本轮 `calamity join` 因 ensureLevel 缺 `corerpg.admin` 未抬级 → 聊天拦在 Lv.10；落点以 `mvtp` 等同 spawn 验收，不挡地图项。）

### 离线 anvil（`server-runtime/ember_event/region`）

spawn/Boss 开放格 OK；soul_sand×271 + netherrack×281；sign 块×3；与 live 一致。

---

## 2) 团本 · DP map `ember_raid`

### Live 进本 · **SKIP**

| 尝试 | 结果 |
|------|------|
| `/dp start-console <player> EmberRaid` | 聊天：`你成功创建了新的队伍` · `团本人数 3～5，当前 (1)` · 仍在枢纽 (-18.5,58,110.5) |
| `/corerpg enter raid` | 等级门（本轮玩家未抬到 35） |
| 组队 3～5 | **未齐** → 按总控：**SKIP**，改 anvil/结构验收 |

### 离线 anvil（`plugins/DungeonPlus/map/ember_raid/region`）

| 点 | 坐标 | under | open |
|----|------|-------|------|
| 大厅 spawn | **(0,65,0)** | stone_bricks | ✅ |
| wave1 左道 | **(18,65,-7)** | cobblestone | ✅ |
| wave2 射手 | **(22,65,7)** | cobblestone | ✅ |
| wave3 汇合 | **(30,65,0)** | cobblestone | ✅ |
| Boss 终厅 | **(40,66,0)** | quartz_block | ✅ |
| Guild w1A/B | (4,65,-6)/(4,65,6) | stone_bricks | ✅ |
| Guild w2 | (20,65,0) | cobblestone | ✅ |

- 左右道 Z 差 **14**（真空间，非同点换文案）  
- 大厅 stone_bricks×525 + wool×16；通道 cobble×364 + netherrack×27；终厅 quartz×128  
- 告示块×3（地图内；文案以施工 STATUS / 菜单为准）

### 配置只读对齐

- `EmberRaid/option.yml`：`$setspawn` / `$teleport` = **0,65,0**  
- `EmberRaid/monster.yml`：w1 **18,65,-7** · w2 射手 **22,65,7** · w3 **30,65,0** · boss **40,66,0**  
- `EmberGuildBoss`：spawn 0,65,0 · w1 4,65,±6 · w2 20,65,0 · boss 40,66,0  
- `ember_raid.yml`：Title「团本 · 大厅通道」；lore 无 `/dp` `/hub` `/mvtp` 教学；进本 `corerpg enter raid`

---

## 3) 命令路由（admin · **未重建**）

授予临时 `corerpg.admin` 后只跑 **`table`**：

| 命令 | 回显（摘要） |
|------|----------------|
| `/corerpg calamitybuild table` | Boss feet=(-96.5,64,266.5) · playerSpawn=(-96.5,65,282.5) yaw=180 · ringFeetY=62 … |
| `/corerpg eventbuild table` | 同上（同服务） |
| `/corerpg raidbuild table` | spawn=(0,65,0) · hall/corr/bossHall 范围 · w1L=(18,65,-7) w2R=(22,65,7) boss=(40,66,0) |

源码路由：`CoreRpgPlugin.java` — `raidbuild` → `RaidHallService`；`calamitybuild`/`eventbuild` → `CalamityBasinService`。  
测后：`/lp user P5OpBot permission unset corerpg.admin`。

---

## 4) 版本 / ops / 未改项

| 项 | 值 |
|----|-----|
| CoreRpg jar | **1.15.9** |
| 日志 | `CoreRpg 1.15.9 enabled`（本轮 22:22 / 22:26 实例） |
| `server-runtime/ops.json` | **`[]`** |
| `login-runtime/ops.json` | **`[]`** |
| MM `EmberRaid.yml` / `EmberCalamity.yml` mtime | **2026-09-27 14:04:25 CST**（P5 施工前；本轮未改） |
| 数值/票/波次 | 未改（只读抽检坐标与菜单） |

---

## 5) 收尾

- 测中临时 OP：`P5OpBot`（短重启 play 加载 ops；LP 临时 `corerpg.admin` 仅用于 table）  
- 测后：`/deop P5OpBot` · LP unset · 双侧 `ops.json=[]`  
- play 仍在跑（`./server-runtime/start.sh custom`，JAVA_HOME=jdk8u504-b01）；代理 25565 / login 25566 / play 25567  
- **未**执行 `calamitybuild`/`raidbuild` 的 build 分支；**未**改 region/数值/MM/票

**结论：** P5 灾厄祭坛盆地 + 团本大厅通道地图验收 **PASS**（团本 live 进本 SKIP，anvil 补齐）。
