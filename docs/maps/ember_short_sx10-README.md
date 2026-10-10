# 地图骨架 · `ember_short_sx10`（短征 · 烬闸递室 · D414）

**日期：** 2026-10-10 Asia/Shanghai  
**规格：** [`DESIGN-ember-short-dungeon-sx10-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx10-2026-10-10.md) §2.5  
**STATUS：** [`STATUS-ember-short-dungeon-sx10-d414-2026-10-10.md`](../status/STATUS-ember-short-dungeon-sx10-d414-2026-10-10.md)

## 惯例（查证）

| 层 | 路径 / 约定 |
|----|-------------|
| DP map 模板 | `plugins/DungeonPlus/map/<name>/`（`level.dat` + `region/*.mca`） |
| Git | **整目录 gitignore**（Release assets；`scripts/dp-maps-v2-verify.sh`）；**禁止** `git add -f` |
| 登记 | `plugins/DungeonPlus/config.yml` → map 名 ↔ dungeon id |
| 本壳 | `plugins/DungeonPlus/dungeon/EmberSx10/option.yml` · `$setmap{name=…}` + `$setspawn` |
| 模板选择 | **优先非前九本同观感**：本号用 `ember_raid`（未用于 sx01–sx09 的 daily_*_v1 / elite / abyss 系列），**不用** `ember_short_sx01..09` / `ember_daily_v1` / `ember_daily_rail_v1` / `ember_daily_frost_v1` / `ember_daily_ash_v1` / `ember_daily_crypt_v1` / `ember_daily_spire_v1` / `ember_daily_tide_v1` / `ember_elite` / `ember_abyss` |
| WE | DESIGN-ember-map-plan：WorldEdit/FAWE 后期；未装则占位模板 |

## 本号动作

```text
cp -a plugins/DungeonPlus/map/ember_raid \
      plugins/DungeonPlus/map/ember_short_sx10
```

- **目的：** 继承已有墙/道体量，**禁空板宣称完成**；与前九本观感分离（前九本已占 daily_v1/rail/frost/ash/crypt/spire/tide/elite/abyss）。
- **非目的：** 不宣称烬闸递室主题完工；不宣称外闸庭/递闸密封室/末室终厅已按 R1/R2/R3 重切。

## 路径描述（外闸庭→递闸密封室→末室终厅）

| 区 | 意向 | 备注 |
|----|------|------|
| 出生 | `0,64,5`（暂与短征范式同） | option 已暂写 |
| R1 **外闸庭** | 灰砖/安山外庭可读；首道重闸/封门可读「要闯闸换室」；地面非空板；可看见下一密封室轮廓 | 1～2 波闸卫/闸室弩 |
| R2 **递闸密封室** | 须沿 ≥3 道**重闸/封门**依次进下一密封室到末室入口；闸间为独立室体、封闭可读；途中 1 波闸室怪或闸弩 | **禁**连续霜雾廊复读 sx03、**禁**同高假「过闸无室」、**禁**跳石/错层/塔升/环廊/风廊气质 |
| R3 **末室终厅** | 末室栏心烬台 · Cast 闸心压浪/扫室斩 | `EmberSx10…Warden/Cast` · 退进室侧龛或回撤上一道闸后平台 |
| 体量目标 | 约 24×24～32×32 · **闸室可读**（每密封室净空建议 ≥6×6；重闸 ≥3；主走道宽 2～3） | 主题：stonebrick / cobble / andesite + iron_bars/door 气质闸门 + stairs/slab/栏杆；禁整图草板/冰/灯笼栈/井心岩浆/连续裂谷窄桥/封心环廊/塔冠窗廊/双层错层庭/渠岸跳石当主识别 |

## 阻塞

1. **无 WorldEdit/FAWE** 于 play/plugins → 主题换皮与三区重切须地图岗进服 WE 另号。  
2. **map 二进制不入 git** → 发布走 Release/同步流程（对齐 maps-v2）；本 README 为仓内真源说明。  
3. 进本键 / MM groups / S49 结算 **并行号**，本骨架不可玩家测通。

## 禁

空板 PASS · 覆盖其它 `ember_*` map 不备份 · 长期共用 `ember_short_sx01..09` 目录交差 · 放开旧日常 map 当短征 · 抬挂机表当奖励 · 纯换皮宣称第十条短征完工 · 把本债写成「sx03 连续霜雾廊」无密封递闸 · 连续霜雾廊假闯闸
