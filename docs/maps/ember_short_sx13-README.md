# 地图骨架 · `ember_short_sx13`（短征 · 烬衡悬梁 · D421）

**日期：** 2026-10-10 Asia/Shanghai  
**规格：** [`DESIGN-ember-short-dungeon-sx13-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx13-2026-10-10.md) §2.5  
**STATUS：** [`STATUS-ember-short-dungeon-sx13-d421-2026-10-10.md`](../status/STATUS-ember-short-dungeon-sx13-d421-2026-10-10.md)

## 惯例（查证）

| 层 | 路径 / 约定 |
|----|-------------|
| DP map 模板 | `plugins/DungeonPlus/map/<name>/`（`level.dat` + `region/*.mca`） |
| Git | **整目录 gitignore**（Release assets）；**禁止** `git add -f` |
| 登记 | `plugins/DungeonPlus/config.yml` → map 名 ↔ dungeon id（键/DP 另号） |
| 本壳 | DP `EmberSx13` option（**本号不写**；MM/DP 另号）· `$setmap{name=…}` + `$setspawn` |
| 模板选择 | 本号用 `ember_raid`（本地**独立**文件夹；与 sx12 同模板源仅占位，**须** WE 重切衡门庭/悬梁衡廊/衡冠；**禁止**长期共用 `ember_short_sx01..12` 目录交差；**禁**窄桥/跳石/转枢/递闸/对廊/环廊/塔升/错层/风廊/霜雾廊气质复读） |

## 本号动作

```text
cp -a plugins/DungeonPlus/map/ember_raid \
      plugins/DungeonPlus/map/ember_short_sx13
```

- **目的：** 继承已有墙/道体量，**禁空板宣称完成**。
- **非目的：** 不宣称烬衡悬梁主题完工；不宣称衡门庭/悬梁衡廊/衡冠终厅已按 R1/R2/R3 重切。

## 路径描述（衡门庭→悬梁衡廊→衡冠终厅）

| 区 | 意向 | 备注 |
|----|------|------|
| 出生 | `0,64,5`（暂与短征范式同） | option 另号暂写 |
| R1 **衡门庭** | 灰砖/安山衡门庭可读；可看见首道衡梁与梁下虚空/深坑；地面非空板 | 1～2 波衡门卫 |
| R2 **悬梁衡廊** | 须**过≥3道配重衡梁/跷梁**到衡冠入口；真有衡梁体+配重端+梁下虚空/深坑可读；途中 1 波梁卫或梁弩 | **禁**连续窄桥复读 sx05、**禁**离散跳石复读 sx09、**禁**转枢复读 sx12、**禁**递闸串室/对廊/环廊/塔升/错层/风廊/霜雾廊气质 |
| R3 **衡冠终厅** | 衡冠栏心烬台 · Cast 衡扫斩/倾梁压浪 | `EmberSx13…Cast`（另号）· 退进衡冠侧龛或已过衡梁侧台 |
| 体量目标 | 约 28×36～32×40 · 衡廊偏长 | stonebrick / cobble / andesite + 少量 iron_bars/chain/anvil 气质；禁整图草板 |

## 阻塞

1. **无 WorldEdit/FAWE** 于 play/plugins → 主题换皮与三区重切须地图岗进服 WE 另号。  
2. **map 二进制不入 git** → 发布走 Release/同步流程；本 README 为仓内真源说明。  
3. 进本键 / MM groups / S52 结算 **并行号**，本骨架不可玩家测通。

## 禁

空板 PASS · 覆盖其它 `ember_*` map 不备份 · 长期共用 `ember_short_sx01..12` 目录交差 · 放开旧日常 map 当短征 · 抬挂机表当奖励 · 纯换皮宣称第十三条短征完工 · 把本债写成「窄桥 / 跳石 / 转枢 / 递闸 / 对廊 / 环廊 / 塔升 / 错层 / 风廊 / 霜雾廊」· **`git add -f` map**

## D446
- 衡梁≥3 · `@d446`
