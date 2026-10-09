# 地图骨架 · `ember_short_sx04`（短征 · 烬井螺旋 · D397）

**日期：** 2026-10-10 Asia/Shanghai  
**规格：** [`DESIGN-ember-short-dungeon-sx04-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx04-2026-10-10.md) §2.5  
**STATUS：** [`STATUS-ember-short-dungeon-sx04-d397-2026-10-10.md`](../status/STATUS-ember-short-dungeon-sx04-d397-2026-10-10.md)

## 惯例（查证）

| 层 | 路径 / 约定 |
|----|-------------|
| DP map 模板 | `plugins/DungeonPlus/map/<name>/`（`level.dat` + `region/*.mca`） |
| Git | **整目录 gitignore**（Release assets；`scripts/dp-maps-v2-verify.sh`）；**禁止** `git add -f` |
| 登记 | `plugins/DungeonPlus/config.yml` → map 名 ↔ dungeon id |
| 本壳 | `plugins/DungeonPlus/dungeon/<Id>/option.yml` · `$setmap{name=…}` + `$setspawn` |
| 模板选择 | **优先非 sx01/sx02/sx03 同观感**：本号用 `ember_daily_ash_v1`（焦骨灰/赤灰气质），**不用** `ember_short_sx01` / `ember_short_sx02` / `ember_short_sx03` / `ember_daily_v1` / `ember_daily_rail_v1` / `ember_daily_frost_v1` |
| WE | DESIGN-ember-map-plan：WorldEdit/FAWE 后期；未装则占位模板 |

## 本号动作

```text
cp -a plugins/DungeonPlus/map/ember_daily_ash_v1 \
      plugins/DungeonPlus/map/ember_short_sx04
```

- **目的：** 继承已有墙/道体量，**禁空板宣称完成**；与 sx01（daily_v1）/ sx02（rail_v1）/ sx03（frost_v1）观感分离，对齐烬井赤灰主题。
- **非目的：** 不宣称烬井螺旋主题完工；不宣称井口台/螺旋下降/炉心垫已按 R1/R2/R3 重切。

## 意向本地坐标（WE 后钉死 · 写入 option/monster）

| 区 | 意向 | 备注 |
|----|------|------|
| 出生 | `0,64,5`（暂与短征范式同） | option 已暂写 |
| R1 井口台 | 半露天井缘 + 下井口可读 | 1～2 波井卫/螺旋弩 |
| R2 螺旋下井 | **真螺旋台阶/坡道下降**（净降建议 ≥8～12） | 禁假画「看起来像螺旋」的平坦廊 |
| R3 井底炉心 | 终厅 · Cast 井心喷焰/灰柱 | `EmberSx04…Warden/FurnaceCast` |
| 体量目标 | 约 28×28～34×34 · 高差可读 | 主题：stonebrick / cobble / netherrack·火盆 / 少量 magma 点缀井底 |

## 阻塞

1. **无 WorldEdit/FAWE** 于 play/plugins → 主题换皮与三区重切须地图岗进服 WE 另号。  
2. **map 二进制不入 git** → 发布走 Release/同步流程（对齐 maps-v2）；本 README 为仓内真源说明。  
3. 进本键 / MM groups / S43 结算 **并行号**，本骨架不可玩家测通。

## 禁

空板 PASS · 覆盖其它 `ember_*` map 不备份 · 长期共用 `ember_short_sx01/02/03` 目录交差 · 放开旧日常 map 当短征 · 抬挂机表当奖励 · 纯换皮宣称第四条短征完工
