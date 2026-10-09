# 地图骨架 · `ember_short_sx08`（短征 · 错层烬庭 · D410）

**日期：** 2026-10-10 Asia/Shanghai  
**规格：** [`DESIGN-ember-short-dungeon-sx08-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx08-2026-10-10.md) §2.5  
**STATUS：** [`STATUS-ember-short-dungeon-sx08-d410-2026-10-10.md`](../status/STATUS-ember-short-dungeon-sx08-d410-2026-10-10.md)

## 惯例（查证）

| 层 | 路径 / 约定 |
|----|-------------|
| DP map 模板 | `plugins/DungeonPlus/map/<name>/`（`level.dat` + `region/*.mca`） |
| Git | **整目录 gitignore**（Release assets；`scripts/dp-maps-v2-verify.sh`）；**禁止** `git add -f` |
| 登记 | `plugins/DungeonPlus/config.yml` → map 名 ↔ dungeon id |
| 本壳 | `plugins/DungeonPlus/dungeon/EmberSx08/option.yml` · `$setmap{name=…}` + `$setspawn` |
| 模板选择 | **优先非前七本同观感**：本号用 `ember_elite`（未用于 sx01–sx07 的 daily_*_v1 系列），**不用** `ember_short_sx01..07` / `ember_daily_v1` / `ember_daily_rail_v1` / `ember_daily_frost_v1` / `ember_daily_ash_v1` / `ember_daily_crypt_v1` / `ember_daily_spire_v1` / `ember_daily_tide_v1` |
| WE | DESIGN-ember-map-plan：WorldEdit/FAWE 后期；未装则占位模板 |

## 本号动作

```text
cp -a plugins/DungeonPlus/map/ember_elite \
      plugins/DungeonPlus/map/ember_short_sx08
```

- **目的：** 继承已有墙/道体量，**禁空板宣称完成**；与前七本观感分离（前七本已占 daily_v1/rail/frost/ash/crypt/spire/tide）。
- **非目的：** 不宣称错层烬庭主题完工；不宣称下层庭/错层换层/上层终厅已按 R1/R2/R3 重切。

## 路径描述（塔基类 · 下层→换层→上层）

| 区 | 意向 | 备注 |
|----|------|------|
| 出生 | `0,64,5`（暂与短征范式同） | option 已暂写 |
| R1 **下层庭** | 灰砖/安山双层壁可读；地面非空板；可从镂空看见上层轮廓；错层入口/首级台阶可读「要换到上层」 | 1～2 波庭卫/错层弩 |
| R2 **错层换层** | 须沿台阶或短桥换到上层入口；**净高差 ≥1 整层可读**（建议 ≥6～10 方块）；层间镂空/栏杆可读 | **禁**同高假「换层」、**禁**纯塔升无下层庭、**禁**纯下井无上层终厅 |
| R3 **上层终厅** | 上层栏心烬台 · Cast 层间砸落/扫层斩 | `EmberSx08…Warden/Cast` · 退进下层栏或侧龛 / 下撤半层平台 |
| 体量目标 | 约 24×24～32×32 · **双层须可读** | 主题：stonebrick / cobble / andesite + stairs/slab/栏杆；禁整图草板/冰/灯笼栈/井心岩浆/裂谷窄桥/封心环廊/塔冠窗廊当主识别 |

## 阻塞

1. **无 WorldEdit/FAWE** 于 play/plugins → 主题换皮与三区重切须地图岗进服 WE 另号。  
2. **map 二进制不入 git** → 发布走 Release/同步流程（对齐 maps-v2）；本 README 为仓内真源说明。  
3. 进本键 / MM groups / S47 结算 **并行号**，本骨架不可玩家测通。

## 禁

空板 PASS · 覆盖其它 `ember_*` map 不备份 · 长期共用 `ember_short_sx01..07` 目录交差 · 放开旧日常 map 当短征 · 抬挂机表当奖励 · 纯换皮宣称第八条短征完工 · 把本债写成「sx04/sx07 半截」无双层错层
