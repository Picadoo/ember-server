# 地图骨架 · `ember_short_sx09`（短征 · 烬渠跳石 · D412）

**日期：** 2026-10-10 Asia/Shanghai  
**规格：** [`DESIGN-ember-short-dungeon-sx09-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx09-2026-10-10.md) §2.5  
**STATUS：** [`STATUS-ember-short-dungeon-sx09-d412-2026-10-10.md`](../status/STATUS-ember-short-dungeon-sx09-d412-2026-10-10.md)

## 惯例（查证）

| 层 | 路径 / 约定 |
|----|-------------|
| DP map 模板 | `plugins/DungeonPlus/map/<name>/`（`level.dat` + `region/*.mca`） |
| Git | **整目录 gitignore**（Release assets；`scripts/dp-maps-v2-verify.sh`）；**禁止** `git add -f` |
| 登记 | `plugins/DungeonPlus/config.yml` → map 名 ↔ dungeon id |
| 本壳 | `plugins/DungeonPlus/dungeon/EmberSx09/option.yml` · `$setmap{name=…}` + `$setspawn` |
| 模板选择 | **优先非前八本同观感**：本号用 `ember_abyss`（未用于 sx01–sx08 的 daily_*_v1 / elite 系列），**不用** `ember_short_sx01..08` / `ember_daily_v1` / `ember_daily_rail_v1` / `ember_daily_frost_v1` / `ember_daily_ash_v1` / `ember_daily_crypt_v1` / `ember_daily_spire_v1` / `ember_daily_tide_v1` / `ember_elite` |
| WE | DESIGN-ember-map-plan：WorldEdit/FAWE 后期；未装则占位模板 |

## 本号动作

```text
cp -a plugins/DungeonPlus/map/ember_abyss \
      plugins/DungeonPlus/map/ember_short_sx09
```

- **目的：** 继承已有墙/道体量，**禁空板宣称完成**；与前八本观感分离（前八本已占 daily_v1/rail/frost/ash/crypt/spire/tide/elite）。
- **非目的：** 不宣称烬渠跳石主题完工；不宣称渠岸庭/离散跳石渡渠/对岸终厅已按 R1/R2/R3 重切。

## 路径描述（渠岸→跳石渡渠→对岸）

| 区 | 意向 | 备注 |
|----|------|------|
| 出生 | `0,64,5`（暂与短征范式同） | option 已暂写 |
| R1 **渠岸庭** | 灰砖/安山渠壁可读；地面非空板；可看见对岸轮廓与渠面；渠口/首枚跳石可读「要过渠」 | 1～2 波渠卫/跳石弩 |
| R2 **跳石渡渠** | 须沿 ≥3 枚**离散**石墩/堤桩过渠到对岸入口；石间空隙/落水/灰汤风险可读；渠面净宽建议 ≥6～10 | **禁**连续平板窄桥复读 sx05、**禁**同高假「过渠」、**禁**错层/塔升/环廊气质 |
| R3 **对岸终厅** | 对岸栏心烬台 · Cast 渠心溅浪/扫岸斩 | `EmberSx09…Warden/Cast` · 退进岸侧龛或回撤最后一枚跳石平台 |
| 体量目标 | 约 24×24～32×32 · **渠宽与跳石须可读** | 主题：stonebrick / cobble / andesite + stairs/slab/栏杆 + 水面/灰汤；禁整图草板/冰/灯笼栈/井心岩浆/连续裂谷窄桥/封心环廊/塔冠窗廊/双层错层庭当主识别 |

## 阻塞

1. **无 WorldEdit/FAWE** 于 play/plugins → 主题换皮与三区重切须地图岗进服 WE 另号。  
2. **map 二进制不入 git** → 发布走 Release/同步流程（对齐 maps-v2）；本 README 为仓内真源说明。  
3. 进本键 / MM groups / S48 结算 **并行号**，本骨架不可玩家测通。

## 禁

空板 PASS · 覆盖其它 `ember_*` map 不备份 · 长期共用 `ember_short_sx01..08` 目录交差 · 放开旧日常 map 当短征 · 抬挂机表当奖励 · 纯换皮宣称第九条短征完工 · 把本债写成「sx05 连续窄桥」无离散跳石 · 连续窄桥假渡渠
