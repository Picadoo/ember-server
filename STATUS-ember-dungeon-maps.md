# STATUS — Ember dungeon theme markers（轻量占位）

**日期：** 2026-09-13 15:05 Asia/Shanghai（约 UTC 07:05）  
**范围：** `plugins/DungeonPlus/map/ember_{daily,weekly,abyss,calamity,raid}`  
**未改：** Paper / CoreRpg / DP scripts (`option.yml` / `monster.yml`) / `ember_arena`

## 结论

五本独立 map 在出生点 `-40,65,270` 附近均有**可辨主题色块 + 中文告示牌 + 主题色旗帜**。非完整 WorldEdit 布景，仅作视觉占位。

## 路径

| map | 目录 |
|-----|------|
| daily | `plugins/DungeonPlus/map/ember_daily/` |
| weekly | `plugins/DungeonPlus/map/ember_weekly/` |
| abyss | `plugins/DungeonPlus/map/ember_abyss/` |
| calamity | `plugins/DungeonPlus/map/ember_calamity/` |
| raid | `plugins/DungeonPlus/map/ember_raid/` |

`server-runtime/plugins` → symlink 到同上；MCA 为硬链，改一处即两边一致。

编辑文件：各 map 的 `region/r.-1.0.mca`（chunk `-3,16`）。备份：`r.-1.0.mca.bak-theme-signs`。

## 出生 / 标记坐标

| map | 中文牌 | 平台主材 (1.12 id) | 告示牌 | 旗帜 (Base) |
|-----|--------|-------------------|--------|-------------|
| `ember_daily` | 余烬日课 | lime wool `35:5` + emerald `133` | `(-43,67,267)` | `(-37,66,267)` Base=5 lime |
| `ember_weekly` | 余烬周征 | blue wool `35:11` + lapis `22` | 同上 | Base=11 blue |
| `ember_abyss` | 余烬深渊 | obsidian `49` + purple clay `159:10` | 同上 | Base=10 purple |
| `ember_calamity` | 余烬灾厄 | netherrack `87` + magma `213` | 同上 | Base=14 red |
| `ember_raid` | 余烬团本 | quartz `155` + gold `41` 点缀 | 同上 | Base=4 yellow |

牌文案（TileEntity `minecraft:sign`）：
- Text1 = 中文主题名  
- Text2 = Daily/Weekly/Abyss/Calamity/Raid  
- Text3 = Ember  
- Text4 = map 文件夹名  

`ember_arena` 仍为 stone，未动。

## 工具

- `anvil-parser` + 直接改 1.12 `Blocks`/`Data`/`TileEntities`（因 EmptyRegion.save 为 1.13+ 格式，不可用）
- 坐标 JSON：`docs/ember-dungeon-theme-markers.json`

## 缓存注意

`plugins/DungeonPlus/dungeon-caches/dungeon_Ember*` 可能是旧拷贝（无新牌）。**新开本**会从 `map/` 模板复制；若要旧缓存也更新需删对应 cache 或 `/dp reload` 后新开实例。

## 验收

- [x] 五本 MCA 内 sign+banner TileEntity 可读回  
- [x] 平台色与 arena 区分  
- [ ] 进服肉眼确认（可选 smoke；见下方）  
- [ ] 后期 WorldEdit 正式布景（DESIGN-ember-map-plan.md）

## 相关

- `STATUS-ember-maps.md` — 分图 + 初代色块  
- `DESIGN-ember-map-plan.md` — WE 主题规划  

## Smoke（:25565）

- Bot `ThemeBot` → `/dp start EmberDaily` **成功**（扣日票、进本）
- 出生附近可见：`wool:5`（lime）、`emerald_block`、`fence`、`standing_sign` @ `(-43,67,267)`
- 位置约 `(-41.4, 65.0, 268.6)`（spawn 簇）
- 未改 DP 脚本；`/dp leave` 在本外提示属预期

**Blocker：** 无 WorldEdit；正式布景仍待后期。旧 `dungeon-caches` 可能不含新牌/旗，以 `map/` 模板为准。
