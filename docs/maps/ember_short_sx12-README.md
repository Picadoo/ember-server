# 地图骨架 · `ember_short_sx12`（短征 · 烬枢转厅 · D419）

**日期：** 2026-10-10 Asia/Shanghai  
**规格：** [`DESIGN-ember-short-dungeon-sx12-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx12-2026-10-10.md) §2.5  
**STATUS：** [`STATUS-ember-short-dungeon-sx12-d419-2026-10-10.md`](../status/STATUS-ember-short-dungeon-sx12-d419-2026-10-10.md)

## 惯例（查证）

| 层 | 路径 / 约定 |
|----|-------------|
| DP map 模板 | `plugins/DungeonPlus/map/<name>/`（`level.dat` + `region/*.mca`） |
| Git | **整目录 gitignore**（Release assets）；**禁止** `git add -f` |
| 登记 | `plugins/DungeonPlus/config.yml` → map 名 ↔ dungeon id（键/DP 另号） |
| 本壳 | DP `EmberSx12` option（**本号不写**；MM/DP 另号）· `$setmap{name=…}` + `$setspawn` |
| 模板选择 | 本号用 `ember_raid`（本地**独立**文件夹；与 sx11 同模板源仅占位，**须** WE 重切入枢庭/环枢侧厢/枢冠；**禁止**长期共用 `ember_short_sx01..11` 目录交差；**禁**环廊/对廊/递闸/霜雾廊/跳石/错层/塔升/风廊气质复读） |

## 本号动作

```text
cp -a plugins/DungeonPlus/map/ember_raid \
      plugins/DungeonPlus/map/ember_short_sx12
```

- **目的：** 继承已有墙/道体量，**禁空板宣称完成**。
- **非目的：** 不宣称烬枢转厅主题完工；不宣称入枢庭/环枢侧厢/枢冠终厅已按 R1/R2/R3 重切。

## 路径描述（入枢庭→环枢侧厢→枢冠终厅）

| 区 | 意向 | 备注 |
|----|------|------|
| 出生 | `0,64,5`（暂与短征范式同） | option 另号暂写 |
| R1 **入枢庭** | 灰砖/安山入枢庭可读；中枢转心可读「须清侧厢后转枢」；可看见≥2 放射侧厢入口 | 1～2 波入枢卫 |
| R2 **环枢侧厢** | 须**清≥2 放射侧厢**后转枢对齐、中枢廊通向枢冠；每厢净空建议 ≥4×8；途中每厢 1 波 | **禁**单廊换皮、**禁**环廊复读 sx06、**禁**对廊复读 sx11、**禁**递闸串室复读 sx10 |
| R3 **枢冠终厅** | 枢冠栏心烬台 · Cast 枢扫斩/转面压浪 | `EmberSx12…Cast`（另号）· 退进已对齐侧厢龛或枢冠侧台 |
| 体量目标 | 约 30×30～36×36 · 放射更方 | stonebrick / cobble / andesite + 少量 iron_bars/piston 气质；禁整图草板 |

## 阻塞

1. **无 WorldEdit/FAWE** 于 play/plugins → 主题换皮与三区重切须地图岗进服 WE 另号。  
2. **map 二进制不入 git** → 发布走 Release/同步流程；本 README 为仓内真源说明。  
3. 进本键 / MM groups / S51 结算 **并行号**，本骨架不可玩家测通。

## 禁

空板 PASS · 覆盖其它 `ember_*` map 不备份 · 长期共用 `ember_short_sx01..11` 目录交差 · 放开旧日常 map 当短征 · 抬挂机表当奖励 · 纯换皮宣称第十二条短征完工 · 把本债写成「环廊 / 对廊 / 递闸 / 霜雾廊 / 跳石 / 错层 / 塔升 / 风廊」· **`git add -f` map**

## D446
- 环枢侧厢≥2 · `@d446`
