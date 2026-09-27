# STATUS · P5 灾厄祭坛盆地 + 团本大厅通道

**日期：** 2026-09-27 21:58–22:14（Asia/Shanghai）
**岗：** 余烬-插件岗执行器
**依据：** `docs/design-ember-calamity-raid-maps-p5.md`；参考 P3/P4 `WeeklyCorridorService` / `EliteCorridorService` / `DailyCourtyardService`
**Verdict：** **✅ P5 地图完成**（盆地/大厅通道可感知结构、落点≠Boss、开放格、菜单不教打指令、数值未改、ops=[]、CoreRpg **1.15.9**）

---

## 0. 改造前债务（施工前）

| 内容 | 状态 |
|------|------|
| `ember_event` | 祭坛区偏平；MV spawn ≈ Boss 脚 (-96.5,64,266.5) → **玩法可测，观感未完成** |
| `ember_raid` | 可玩区约 x -46..-34 × z 264..276 小房；文案分路无真左右空间 → **玩法可测，观感未完成** |

施工后勾选 §5 验收，方可宣称「祭坛盆地 / 通道团本」完成（本 STATUS）。

---

## 1. 备份路径

| 备份 | 内容 |
|------|------|
| `server-runtime/config-backups/ember_event-p5-20260927-215831/` | `ember_event` region + level.dat；`calamity.yml`；`worlds.yml`；`ember_calamity.yml` 菜单 |
| `server-runtime/config-backups/ember_raid-p5-20260927-215831/` | `map/ember_raid`；`EmberRaid` / `EmberGuildBoss` dungeon；`ember_raid.yml` 菜单 |

---

## 2. 灾厄 · `ember_event` 祭坛盆地

**施工：** `/corerpg calamitybuild`（`CalamityBasinService`，CoreRpg **1.15.9**）→ 在线世界分批建造 · blocks≈**56990**
**另名：** `/corerpg eventbuild`

| 锚 | 坐标 | 说明 |
|----|------|------|
| **Boss 台中心** | **(-96.5, 64, 266.5)** | `calamity.yml` **未改数值**，仅注释；脚下石英台 |
| **玩家奔赴 / MV spawn** | **(-96.5, 65, 282.5) yaw 180** | Boss **南 ~16**；面向北朝祭坛 |
| 战斗环脚 Y | 62 | 相对祭坛抬高 2 |
| 看台/外沿 | 环阶 +1～2；外沿相对环升 3～6 | 石英/石砖 + 灵魂沙/地狱岩 + 火盆（glowstone） |

**告示：** `回枢纽 · 打开枢纽菜单`；`灾厄使 · 中央台` / `灾厄祭坛 · 南入口` — **不写** `/hub` `/mvtp` `/dp`。
**单门：** 正式仍 `/ember` → 灾厄 → 奔赴（`corerpg calamity join` → MV spawn）；DP `EmberCalamity` 菜单钮标「仅管理/测试」，**不当正式门**。
**未改：** 窗时、缩放、日箱、MM 血攻。

**离线 anvil：** player/boss/ring 开放格 OK；环采样 soul_sand×70 + netherrack×77 + quartz/stonebrick。
**Live：** `mvtp ember_event` 落地 (-96.5,65,282.5) 脚下 stonebrick；Boss 台 quartz_block；告示可见。

---

## 3. 团本 · DP `ember_raid` 大厅→通道→终厅

**施工：** `/corerpg raidbuild`（`RaidHallService`）→ 临时世界 `ember_raid_build` → 导出 `plugins/DungeonPlus/map/ember_raid` · blocks≈**51351**

| 区段 | 范围 / 点 | 材质观感 |
|------|-----------|----------|
| 大厅 ~28×28 | x=-14..13, z=-14..13；出生 **(0,65,0)** | 亮石砖 + 旗（羊毛柱）+ 海晶灯 |
| 主通道 | x=14..27, z=-4..4；左右龛 z=±8 | 偏暗圆石 + 地狱岩 |
| 终厅 ~24×24 | x=28..51, z=-12..11；Boss 垫 **(40,66,0)** | 石英垫（厅布局，非盆地） |

### 刷点表（EmberRaid · 数量/条件未改）

| 波 | 坐标 | 说明 |
|----|------|------|
| spawn / teleport | **0,65,0** | `$setspawn` / `$teleport` |
| wave1 左道 | **18,65,-7** | 卫兵×6 · −Z 龛 |
| wave2 右道 | 卫兵 **18,65,-7**×3 · 射手 **22,65,7**×4 | +Z 龛真空间 |
| wave3 汇合 | **30,65,0** | 精英×1 + 卫兵×3 |
| boss | **40,66,0** | 终厅垫中央开放格 |

### EmberGuildBoss（同 map · **不改公会数值**）

| 点 | 新坐标 |
|----|--------|
| spawn/teleport | 0,65,0 |
| wave1 | 4,65,-6 与 4,65,6 |
| wave2 | 20,65,0 |
| boss | 40,66,0 |

**告示：** `左道卫兵 · 右道射手 · 汇合后终厅`；`回枢纽 · 打开枢纽菜单`。
**离线 anvil：** spawn/w1/w2/w3/boss/guild 点全 `open=True`；大厅 stonebrick×257+wool；通道 cobble×195；终厅 quartz×85；左右龛 Z 差 ≥14。
**Live 进本：** 需 DP 组队 3～5（`/dungeon-team …`）；本轮冒烟组队未齐（人数显示 1）→ 未进实例；**地图以 anvil + 导出验收**。玩法票/波次/奖励未改。

---

## 4. 改动文件列表

| 路径 | 变更 |
|------|------|
| `CoreRpg/.../CalamityBasinService.java` | **新建** 灾厄盆地建造 |
| `CoreRpg/.../RaidHallService.java` | **新建** 团本大厅通道建造 + 导出 |
| `CoreRpg/.../CoreRpgPlugin.java` | 路由 `raidbuild` / `calamitybuild` / `eventbuild` |
| `CoreRpg/pom.xml` · `plugin.yml` | 版本 **1.15.8 → 1.15.9** |
| `plugins/CoreRpg.jar` | 重编换入 |
| `server-runtime/ember_event/region/*.mca` | 祭坛盆地地形 |
| `plugins/Multiverse-Core/worlds.yml` | ember_event spawn → (-96.5,65,282.5) yaw 180 |
| `plugins/CoreRpg/calamity.yml`（及 resources） | Boss 坐标保持；P5 落点注释 |
| `plugins/DungeonPlus/map/ember_raid/region/*.mca` | 大厅/通道/终厅 |
| `plugins/DungeonPlus/dungeon/EmberRaid/option.yml` · `monster.yml` | spawn/刷点对齐新图 |
| `plugins/DungeonPlus/dungeon/EmberGuildBoss/option.yml` · `monster.yml` | 刷点随图重对齐 |
| `plugins/TrMenu/menus/ember_calamity.yml` | 奔赴文案；Layout 露出测试钮 T |
| `plugins/TrMenu/menus/ember_raid.yml` | 标题/大厅通道 lore（无 `/dp` 教学） |
| `docs/STATUS-ember-calamity-raid-maps-p5.md` | 本文件 |

---

## 5. 验收 checklist（设计 §5）

### 5.1 灾厄
- [x] 盆地/环阶/中央抬高台可辨
- [x] 玩家落点 ≠ Boss 脚；面向祭坛
- [x] 入口回枢纽菜单语义牌（不教打指令）
- [x] 正式入口仅菜单奔赴；DP 测本不进主路径
- [x] Boss 坐标/窗/缩放/日箱未改

### 5.2 团本
- [x] 大厅 / 通道 / 终厅三段可辨（anvil 材质采样）
- [x] 左右道真实空间差（w1 z=-7 / w2 z=+7）
- [x] 与日庭院、周本廊观感可区分
- [x] 刷点开放格、Boss 不卡边环（anvil）
- [x] 团菜单无玩家可见 `/dp start` 教学
- [x] 票/人数/奖励/波次数未改

### 5.3 债务
- [x] 改造前「观感未完成」已写；改造后勾选 5.1/5.2

### 自检命令
```
/corerpg calamitybuild   # 或 eventbuild
/corerpg raidbuild
/corerpg calamity join   # 落地应 ≈ (-96.5,65,282.5)
# 团本：3～5 人组 DP 队后 /ember → 团本 → 一点进（corerpg enter raid）
```

---

## 6. 版本与 ops

| 项 | 值 |
|----|-----|
| CoreRpg | **1.15.9**（日志 `CoreRpg 1.15.9 enabled`） |
| `ops.json` | **`[]`**（测中临时 RpgBot，已清） |
| git | **本 STATUS 随 P5 sync 提交**；push 结果见提交记录 |

痕迹：`/tmp/p5-build-result.json` · `/tmp/p5-verify-result.json`（灾厄 live）· 离线 anvil 采样见施工记录。

---

## 7. 风险 / 未做

1. **团本 live 进本冒烟** — DP 队伍须 `/dungeon-team request join <队长>`；本轮冒烟人数门仍报 (1)，未进实例。地图开放格已离线验收；建议验收岗用三人 `/dungeon-team` 再进一次看落地 (0,65,0)。
2. **DP 实例缓存** — 大改后若见旧小房，清 `dungeon-caches` 中 EmberRaid/GuildBoss 或重启 play。
3. **`ember_calamity` DP 测图** — 按稿不扩成完整盆地；菜单保持测试灰钮。
4. **并行重启** — 施工期间他岗多次重启 proxy/play；以 region mtime 与 anvil 为准。
5. **未改** 窗时刻表、参战缩放、日箱、团票/波次/奖励、MM 血攻、P3/P4 图。
