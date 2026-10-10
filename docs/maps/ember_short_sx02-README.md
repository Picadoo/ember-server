# 地图骨架 · `ember_short_sx02`（短征 · 锈灯栈道 · D392）

**日期：** 2026-10-10 Asia/Shanghai  
**规格：** [`DESIGN-ember-short-dungeon-sx02-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx02-2026-10-10.md) §2.5  
**STATUS：** [`STATUS-ember-short-dungeon-sx02-d392-2026-10-10.md`](../status/STATUS-ember-short-dungeon-sx02-d392-2026-10-10.md)

## 惯例（查证）

| 层 | 路径 / 约定 |
|----|-------------|
| DP map 模板 | `plugins/DungeonPlus/map/<name>/`（`level.dat` + `region/*.mca`） |
| Git | **整目录 gitignore**（Release assets；`scripts/dp-maps-v2-verify.sh`）；**禁止** `git add -f` |
| 登记 | `plugins/DungeonPlus/config.yml` → map 名 ↔ dungeon id |
| 本壳 | `plugins/DungeonPlus/dungeon/<Id>/option.yml` · `$setmap{name=…}` + `$setspawn` |
| 模板选择 | **优先非 sx01 同观感**：本号用 `ember_daily_rail_v1`（锈轨气质），**不用** `ember_short_sx01` / `ember_daily_v1` |
| WE | DESIGN-ember-map-plan：WorldEdit/FAWE 后期；未装则占位模板 |

## 本号动作

```text
cp -a plugins/DungeonPlus/map/ember_daily_rail_v1 \
      plugins/DungeonPlus/map/ember_short_sx02
```

- **目的：** 继承已有墙/道体量，**禁空板宣称完成**；与 sx01（`ember_daily_v1` 源）观感分离。
- **非目的：** 不宣称锈灯栈道主题完工；不宣称分叉灯桥/灯塔垫已按 R1/R2/R3 重切。

## 意向本地坐标（WE 后钉死 · 写入 option/monster）

| 区 | 意向 | 备注 |
|----|------|------|
| 出生 | `0,64,5`（暂与短征范式同） | option 已暂写 |
| R1 抬升栈台 | 出生北侧抬高木/铁栈 | 1～2 波巡卫 |
| R2 分叉灯桥 | **真分叉形体**左右汇合 | 禁假画两条线 |
| R3 灯塔垫 | 终厅 · Cast 扫线/落点 | `EmberSx02…Warden/Cast` |
| 体量目标 | 约 30×30～36×36 · 高 12～16 | 主题：spruce/锈铁栏杆 + 灯笼 + 氧化铜点缀 |

## 阻塞

1. **无 WorldEdit/FAWE** 于 play/plugins → 主题换皮与三区重切须地图岗进服另号。  
2. **map 二进制不入 git** → 发布走 Release/同步流程（对齐 maps-v2）；本 README 为仓内真源说明。  
3. 进本键 / MM groups / S41 结算 **并行号**，本骨架不可玩家测通。

## 禁

空板 PASS · 覆盖其它 `ember_*` map 不备份 · 长期共用 `ember_short_sx01` 目录交差 · 放开旧日常 map 当短征 · 抬挂机表当奖励 · 纯换皮宣称第二条短征完工

## D443 真地图
- Y68 分叉灯桥 · `@d443`
