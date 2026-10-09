# 地图骨架 · `ember_short_sx03`（短征 · 霜雾闸廊 · D393）

**日期：** 2026-10-10 Asia/Shanghai  
**规格：** [`DESIGN-ember-short-dungeon-sx03-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx03-2026-10-10.md) §2.5  
**STATUS：** [`STATUS-ember-short-sx03-mm-2026-10-10.md`](../status/STATUS-ember-short-sx03-mm-2026-10-10.md)

## 惯例（查证）

| 层 | 路径 / 约定 |
|----|-------------|
| DP map 模板 | `plugins/DungeonPlus/map/<name>/`（`level.dat` + `region/*.mca`） |
| Git | **整目录 gitignore**（Release assets；`scripts/dp-maps-v2-verify.sh`）；**禁止** `git add -f` |
| 登记 | `plugins/DungeonPlus/config.yml` → map 名 ↔ dungeon id |
| 本壳 | `plugins/DungeonPlus/dungeon/<Id>/option.yml` · `$setmap{name=…}` + `$setspawn` |
| 模板选择 | **优先非 sx01/sx02 同观感**：本号用 `ember_daily_frost_v1`（霜气质），**不用** `ember_short_sx01/sx02` |
| WE | DESIGN-ember-map-plan：WorldEdit/FAWE 后期；未装则占位模板 |

## 本号动作

```text
cp -a plugins/DungeonPlus/map/ember_daily_frost_v1 \
      plugins/DungeonPlus/map/ember_short_sx03
```

- **目的：** 继承已有墙/道体量，**禁空板宣称完成**；与 sx01（daily_v1）/ sx02（rail_v1）观感分离。
- **非目的：** 不宣称霜雾闸廊主题完工；不宣称雾廊/双闸/霜窖垫已按 R1/R2/R3 重切。

## 意向本地坐标（WE 后钉死 · 写入 option/monster）

| 区 | 意向 | 备注 |
|----|------|------|
| 出生 | `0,64,5`（暂与短征范式同） | option 已暂写 |
| R1 雾廊入口 | 出生北侧窄廊+雾氛围 | 1～2 波巡卒/闸弩 |
| R2 双闸室 | **真两道闸门形体**顺序开闸 | 禁假画两条线 |
| R3 霜窖垫 | 终厅 · Cast 雾楔/冻圈 | `EmberSx03Warden` + `EmberSx03FrostCast` |
| 体量目标 | 约 30×30～36×36 · 高 10～14 | 主题：deepslate / packed_ice / 铁闸 + blue_ice/snow 点缀 |

## 阻塞

1. **无 WorldEdit/FAWE** 于 play/plugins → 主题换皮与三区重切须地图岗进服另号。  
2. **map 二进制不入 git** → 发布走 Release/同步流程（对齐 maps-v2）；本 README 为仓内真源说明。  
3. 进本键 / S42 结算 / 菜单三本选页 **并行号**；本骨架+MM 可热更，完整测通依赖键号落地。

## 禁

空板 PASS · 覆盖其它 `ember_*` map 不备份 · 长期共用 `ember_short_sx01/sx02` 目录交差 · 放开旧日常 map 当短征 · 抬挂机表当奖励 · 纯换皮宣称第三条短征完工
