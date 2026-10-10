# 地图骨架 · `ember_short_sx11`（短征 · 烬镜对廊 · D417）

**日期：** 2026-10-10 Asia/Shanghai  
**规格：** [`DESIGN-ember-short-dungeon-sx11-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx11-2026-10-10.md) §2.5  
**STATUS：** [`STATUS-ember-short-dungeon-sx11-d417-2026-10-10.md`](../status/STATUS-ember-short-dungeon-sx11-d417-2026-10-10.md) · MM [`STATUS-ember-short-sx11-mm-2026-10-10.md`](../status/STATUS-ember-short-sx11-mm-2026-10-10.md)

## 惯例（查证）

| 层 | 路径 / 约定 |
|----|-------------|
| DP map 模板 | `plugins/DungeonPlus/map/<name>/`（`level.dat` + `region/*.mca`） |
| Git | **整目录 gitignore**（Release assets）；**禁止** `git add -f` |
| 登记 | `plugins/DungeonPlus/config.yml` → map 名 ↔ dungeon id |
| 本壳 | `plugins/DungeonPlus/dungeon/EmberSx11/option.yml` · `$setmap{name=…}` + `$setspawn` |
| 模板选择 | 本号用 `ember_raid`（本地**独立**文件夹；与 sx10 同模板源仅占位，**须** WE 重切对称双廊；**禁止**长期共用 `ember_short_sx01..10` 目录交差；**禁**环廊/递闸/霜雾廊/跳石/错层/塔升/风廊气质复读） |

## 本号动作

```text
cp -a plugins/DungeonPlus/map/ember_raid \
      plugins/DungeonPlus/map/ember_short_sx11
```

- **目的：** 继承已有墙/道体量，**禁空板宣称完成**。
- **非目的：** 不宣称烬镜对廊主题完工；不宣称对廊前厅/左右双廊/合镜终厅已按 R1/R2/R3 重切。

## 路径描述（对廊前厅→左右对称双廊→合镜终厅）

| 区 | 意向 | 备注 |
|----|------|------|
| 出生 | `0,64,5`（暂与短征范式同） | option 已暂写 |
| R1 **对廊前厅** | 灰砖/安山前厅可读；中轴合镜门可读「须两侧各通」；可看见左右廊入口 | 1～2 波对廊卫/侧廊弩 |
| R2 **左右对称双廊** | 须左廊与右廊各走通/清场后中轴合镜门开；每侧廊净空 ≥4 宽×10 长；途中每侧 1 波 | **禁**单廊换皮、**禁**环走复读 sx06、**禁**递闸串室复读 sx10 |
| R3 **合镜终厅** | 合镜栏心烬台 · Cast 镜面扫线/对折斩 | `EmberSx11…Warden/Cast` · 退进合镜侧龛或回撤侧廊末端平台 |
| 体量目标 | 约 28×24～36×28 · 对称轴向更宽 | stonebrick / cobble / andesite + 少量 glass/iron_bars 镜面气质；禁整图草板 |

## 阻塞

1. **无 WorldEdit/FAWE** 于 play/plugins → 主题换皮与三区重切须地图岗进服 WE 另号。  
2. **map 二进制不入 git** → 发布走 Release/同步流程；本 README 为仓内真源说明。  
3. 进本键 / MM groups / S50 结算 **并行号**，本骨架不可玩家测通。

## 禁

空板 PASS · 覆盖其它 `ember_*` map 不备份 · 长期共用 `ember_short_sx01..10` 目录交差 · 放开旧日常 map 当短征 · 抬挂机表当奖励 · 纯换皮宣称第十一条短征完工 · 把本债写成「单廊 / 环走 / 递闸 / 霜雾廊」
