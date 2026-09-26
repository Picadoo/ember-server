# DESIGN · 余烬副本地图规划（独立 map）

**日期：** 2026-09-13（Asia/Shanghai）  
**约束：** 不改 Paper；不覆盖并行岗已落盘的 `plugins/DungeonPlus/map/ember_*`；本文件只定意图与 WE 施工约定。  
**工具（后期）：** WorldEdit / FAWE（未装则先占位；施工后再 `//copy` 进各 map 世界并保存）。  
**现状同步：** `map/` 已有 `ember_daily` / `ember_weekly` / `ember_abyss` / `ember_calamity` / `ember_raid` / `ember_arena`（并行岗克隆模板，level.dat 同规格）；`config.yml` 已登记；各 `option.yml` 仍共用占位出生 `-40,65,270`。

---

## 1. 目标

每本 **独立 map 文件夹 + 独立视觉主题**，避免日/周/深渊/灾厄/团本共用同一石砖小馆观感。  
挂机庭与主城 hub 仍在主世界 `world`，不进 DungeonPlus map。

| 内容 | DP 本 | map 名 | 主题 | 体量（约） | 布局意象 |
|------|-------|--------|------|------------|----------|
| 日常 | EmberDaily | `ember_daily` | **灰烬庭院** | 32×32×12 | 露天/半露天庭院 + 四面回廊 + 中央 Boss 垫 |
| 周常 | EmberWeekly | `ember_weekly` | **深核廊** | 64×20×14 | 长廊串联 3 室（前厅→中核→深室） |
| 深渊 | EmberAbyss | `ember_abyss` | **下行竖井** | 20×20 平面 × 多层 | 竖井平台层层下潜，层间楼梯/落差 |
| 灾厄 | EmberCalamity | `ember_calamity` | **开放祭坛** | 48×48×16 | 开阔祭坛广场，边缘观众台/火盆 |
| 团本 | EmberRaid | `ember_raid` | **大厅+通道** | 大厅 28×28 + 通道总长 ~80 | 集结大厅 → 三岔通道 → 终厅 |
| 竞技/回退 | ember_arena | `ember_arena` | 竞技场（既有） | 维持现状 | 共享回退 / 1v1·2v2 壳 |

---

## 2. 出生点与本地坐标系（施工约定）

**原则：** 每个 map 内用 **本地原点** 设计；DP `$setspawn` / `$teleport` / `$mob location` 与本地一致。  
并行岗落地前可继续用占位 `-40,65,270`；WE 完工后按表改 `option.yml` + `monster.yml`（本规划不直接改 YAML，交给插件岗一次对齐）。

| map | 建议玩家出生（本地） | Boss / 终区垫 | 备注 |
|-----|----------------------|---------------|------|
| `ember_daily` | `0,65,0` | `0,65,12` | 庭院中轴；回廊内侧可刷小怪 |
| `ember_weekly` | `0,65,0` | `0,65,48` | 廊长向 +Z；三室中心约 z=12 / 30 / 48 |
| `ember_abyss` | `0,90,0` | `0,50,0` | 出生在井顶平台；下潜至井底 Boss |
| `ember_calamity` | `0,65,-16` | `0,65,0` | 出生在祭坛南侧入口；Boss 站中央 |
| `ember_raid` | `0,65,0`（大厅） | `40,65,0`（终厅） | 大厅集结；通道 +X；可扩 ±Z 支路 |
| `ember_arena` | 保持现坐标 | — | 不改除非竞技重做 |

**主世界 hub（非 DP map）：** 建议在主城 spawn 旁另辟平台，例如相对现演示窟偏移：  
`hub_platform ≈ (-20, 66, 250)`（远离 `-40,65,270` 演示/刷怪点，避免踩怪）。  
本轮 **未施工**（见 STATUS：RpgBot 长循环占线，风险跳过）。

---

## 3. 主题与方块调性（WE 指引）

### 3.1 日 · 灰烬庭院 `ember_daily`

- **情绪：** 短本、明亮灰调、可一眼看清路径。  
- **主材：** `stonebrick` / `cobblestone` / `gravel` / 少量 `netherrack` 点缀；地面 `stone` + 角落火盆（`netherrack`+`fire` 或红石灯）。  
- **结构：** 外墙 32×32，高 4～5；中央 8×8 Boss 垫抬高 1；四面开口回廊宽 3。  
- **刷怪：** 回廊节点 → 中央垫（对齐现有 wave1/2/Boss 逻辑）。

### 3.2 周 · 深核廊 `ember_weekly`

- **情绪：** 压迫、纵深、比日本更长。  
- **主材：** `obsidian` 包边 + `stonebrick` 主体 + `redstone_block`/`glowstone` 作「核心」灯。  
- **结构：** 宽 7～9 的主廊；三室：前厅（清杂）→ 中核（双波）→ 深室（蛮兵/双怪）。  
- **门：** 可用铁栏杆 / 活塞门占位（脚本后再接障碍物）。

### 3.3 深渊 · 下行竖井 `ember_abyss`

- **情绪：** 垂直进度感 = 层数叙事。  
- **主材：** `bedrock` 外圈（防穿）+ 内侧 `cobblestone`/`stone`；每层木/石平台。  
- **结构：** 井筒 16～20 边长；层高差 8～10；螺旋梯或对角落水（小心摔死，出生给缓降或栏杆）。  
- **刷怪：** 每层一波；井底 Brute；与层数榜文案一致。

### 3.4 灾厄 · 开放祭坛 `ember_calamity`

- **情绪：** 开阔、仪式感、适合大型 Boss。  
- **主材：** `quartz`/`stonebrick` 祭坛台；周围 `soul_sand`/`netherrack` 环；四角柱 + 火。  
- **结构：** 中央 11×11 抬高 2 的祭坛；外环战斗区半径 ~20；边缘 1～2 级看台。  
- **刷怪：** 仅中央 Boss（现 `EmberCalamityBoss`）。

### 3.5 团本 · 大厅+通道 `ember_raid`

- **情绪：** 协作集结 → 分路压力 → 终厅。  
- **主材：** 大厅偏 `stonebrick`+旗/地毯；通道偏暗 `cobble`；终厅用灾厄同系祭坛缩小版。  
- **结构：**  
  1. 大厅 28×28（出生、回城牌、简短说明）  
  2. 主通道向 +X，长 ~40，可分 ±Z 支路做小怪  
  3. 终厅 24×24 放大 Boss  
- **人数：** 3～5；出生点要有站位空间。

---

## 4. 施工流程（WorldEdit 后期）

1. 主世界或专用 `we_edit` 世界按主题建造（或直接进对应 map 世界建造）。  
2. `//schematic save ember_<type>` 备份。  
3. 确认 map 文件夹 region 覆盖建造区块；拷贝进 `plugins/DungeonPlus/map/<name>/`。  
4. 一次对齐该本 `option.yml` 的 `$setspawn` / `$teleport` 与 `monster.yml` 的 `location=`。  
5. `/dp reload`（或重启）后 smoke：`/dp start EmberDaily` 等。  
6. **禁止** 覆盖并行岗 map 目录时不备份；改前先 `cp -a map map.bak-YYYYMMDD`。

未装 WorldEdit 时：保持模板 void/克隆图；脚本坐标继续用占位 `-40,65,270` 即可跑通逻辑。

---

## 5. 与现网脚本的关系

| 文件 | 现状 | 规划后 |
|------|------|--------|
| `dungeon/*/option.yml` `$setmap` | 已指向独立 map 名 | 保持 |
| `$setspawn` / `$teleport` | 全员 `-40,65,270` | WE 后改本地表 |
| `monster.yml` locations | 同簇约 `-40..-42,65,273..278` | 随主题垫平移 |
| `DungeonPlus/config.yml` map 登记 | 已有五本+arena | 不改名 |
| `map/ember_*` | 并行岗已建目录+region | **不覆盖，只文档同步** |

---

## 6. 主城 Hub 平台（可选，本轮跳过）

- **意图：** 主世界一小块石砖/石英平台 + 告示牌「余烬枢纽」+ 指向 `/ember`。  
- **坐标建议：** `(-20,66,250)` 一带，避开演示窟与挂机刷怪。  
- **手段：** mineflayer RpgBot `/setblock`·`/fill`（需 OP）。  
- **本轮：** **SKIP** — 服务器虽在线，但 `RpgBot` 正被 `rpg-loop.js` 长占并反复死亡/刷怪，再建易冲突；见 `STATUS-ember-map-plan.md`。

---

## 7. 验收清单（地图岗）

- [ ] 五本 map 目录在、DP `config.yml` 登记正确（已满足骨架）  
- [ ] WE 后视觉可辨：庭院 / 廊 / 竖井 / 祭坛 / 大厅通道  
- [ ] 出生与刷怪坐标同本地系，进本不掉虚空、不卡墙  
- [ ] 未误改 Paper；未无备份覆盖并行 map  
- [ ] 主城 hub 平台（可选）与演示窟分离  

---

## 8. 相关文档

- `DESIGN-dungeon-daily-weekly.md` — 日周循环与次数  
- `DESIGN-ember-dungeon.md` / `dungeons/ember-crypt/` — 演示窟  
- `docs/ember-hub-copy.md` — 菜单文案  
- `STATUS-ember-map-plan.md` — 本轮执行状态  
