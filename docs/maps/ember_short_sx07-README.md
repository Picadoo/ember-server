# 地图骨架 · `ember_short_sx07`（短征 · 烬塔回升 · D407）

**日期：** 2026-10-10 Asia/Shanghai  
**规格：** [`DESIGN-ember-short-dungeon-sx07-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx07-2026-10-10.md) §2.5  
**STATUS：** [`STATUS-ember-short-dungeon-sx07-d407-2026-10-10.md`](../status/STATUS-ember-short-dungeon-sx07-d407-2026-10-10.md)

## 惯例（查证）

| 层 | 路径 / 约定 |
|----|-------------|
| DP map 模板 | `plugins/DungeonPlus/map/<name>/`（`level.dat` + `region/*.mca`） |
| Git | **整目录 gitignore**（Release assets；`scripts/dp-maps-v2-verify.sh`）；**禁止** `git add -f` |
| 登记 | `plugins/DungeonPlus/config.yml` → map 名 ↔ dungeon id |
| 本壳 | `plugins/DungeonPlus/dungeon/EmberSx07/option.yml` · `$setmap{name=…}` + `$setspawn` |
| 模板选择 | **优先非前六本同观感**：本号用 `ember_daily_tide_v1`（未用于 sx01–sx06），**不用** `ember_short_sx01..06` / `ember_daily_v1` / `ember_daily_rail_v1` / `ember_daily_frost_v1` / `ember_daily_ash_v1` / `ember_daily_crypt_v1` / `ember_daily_spire_v1` |
| WE | DESIGN-ember-map-plan：WorldEdit/FAWE 后期；未装则占位模板 |

## 本号动作

```text
cp -a plugins/DungeonPlus/map/ember_daily_tide_v1 \
      plugins/DungeonPlus/map/ember_short_sx07
```

- **目的：** 继承已有墙/道体量，**禁空板宣称完成**；与前六本观感分离（前六本已占 daily_v1/rail/frost/ash/crypt/spire）。
- **非目的：** 不宣称烬塔回升主题完工；不宣称塔基庭/折返上行/塔冠终厅已按 R1/R2/R3 重切。

## 意向本地坐标（WE 后钉死 · 写入 option/monster）

| 区 | 意向 | 备注 |
|----|------|------|
| 出生 | `0,64,5`（暂与短征范式同） | option 已暂写 |
| R1 塔基庭 | 塔身入口/首层阶可读「要往上走」；地面非空板 | 1～2 波塔卫/窗廊弩 |
| R2 折返/螺旋上行 | **须爬升 ≥2 整层可读高差**（净爬升建议 ≥12～20；护栏/窗洞可读） | 禁假坡/同高假「上楼」 |
| R3 塔冠终厅 | 塔冠烬台 · Cast 塔冠坠焰/扫顶柱 | `EmberSx07…Warden/TowerCast` · 退进侧窗廊龛或下撤半层平台 |
| 体量目标 | 约 24×24～32×32 · 塔高可读 | 主题：stonebrick / cobble / andesite + stairs/slab；禁整图冰/灯笼/井心岩浆/裂谷窄桥/封心环廊 |

## 阻塞

1. **无 WorldEdit/FAWE** 于 play/plugins → 主题换皮与三区重切须地图岗进服 WE 另号。  
2. **map 二进制不入 git** → 发布走 Release/同步流程（对齐 maps-v2）；本 README 为仓内真源说明。  
3. 进本键 / MM groups / S46 结算 **并行号**，本骨架不可玩家测通。

## 禁

空板 PASS · 覆盖其它 `ember_*` map 不备份 · 长期共用 `ember_short_sx01..06` 目录交差 · 放开旧日常 map 当短征 · 抬挂机表当奖励 · 纯换皮宣称第七条短征完工 · 把本债写成「断塔日常重开」

## D444
- 塔升≥2层 · `@d444`
