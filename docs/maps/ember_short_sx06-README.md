# 地图骨架 · `ember_short_sx06`（短征 · 烬环廊 · D403）

**日期：** 2026-10-10 Asia/Shanghai  
**规格：** [`DESIGN-ember-short-dungeon-sx06-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx06-2026-10-10.md) §2.5  
**STATUS：** [`STATUS-ember-short-dungeon-sx06-d403-2026-10-10.md`](../status/STATUS-ember-short-dungeon-sx06-d403-2026-10-10.md)

## 惯例（查证）

| 层 | 路径 / 约定 |
|----|-------------|
| DP map 模板 | `plugins/DungeonPlus/map/<name>/`（`level.dat` + `region/*.mca`） |
| Git | **整目录 gitignore**（Release assets；`scripts/dp-maps-v2-verify.sh`）；**禁止** `git add -f` |
| 登记 | `plugins/DungeonPlus/config.yml` → map 名 ↔ dungeon id |
| 本壳 | `plugins/DungeonPlus/dungeon/EmberSx06/option.yml` · `$setmap{name=…}` + `$setspawn` |
| 模板选择 | **优先非前五本同观感**：本号用 `ember_daily_spire_v1`（灰砖/断塔石廊气质），**不用** `ember_short_sx01..05` / `ember_daily_v1` / `ember_daily_rail_v1` / `ember_daily_frost_v1` / `ember_daily_ash_v1` / `ember_daily_crypt_v1` |
| WE | DESIGN-ember-map-plan：WorldEdit/FAWE 后期；未装则占位模板 |

## 本号动作

```text
cp -a plugins/DungeonPlus/map/ember_daily_spire_v1 \
      plugins/DungeonPlus/map/ember_short_sx06
```

- **目的：** 继承已有墙/道体量，**禁空板宣称完成**；与 sx01（daily_v1）/ sx02（rail_v1）/ sx03（frost_v1）/ sx04（ash_v1）/ sx05（crypt_v1）观感分离，对齐灰砖环廊气质。
- **非目的：** 不宣称烬环廊主题完工；不宣称外环庭/双环廊绕行/环心终厅已按 R1/R2/R3 重切。

## 意向本地坐标（WE 后钉死 · 写入 option/monster）

| 区 | 意向 | 备注 |
|----|------|------|
| 出生 | `0,64,5`（暂与短征范式同） | option 已暂写 |
| R1 外环庭 | 环廊外缘可读 · 中心封坑/栏可读「不能直线穿心」 | 1～2 波环卫/环廊弩 |
| R2 双环廊绕行 | **真环状走道与封心**（须环走/半环；主走道宽建议 2～4） | 禁假画「看起来像环」的直线宽廊 |
| R3 环心终厅 | 环心烬台 · Cast 环扫斩/向心拉扯 | `EmberSx06…Warden/RingCast` · 退进外环龛或离开弧带 |
| 体量目标 | 约 28×28～36×36 · 环心封坑深 ≥4～8 或实心封柱 | 主题：stonebrick / cobble / andesite + 少量铁栏；禁整图冰/灯笼/井心岩浆/裂谷窄桥 |

## 阻塞

1. **无 WorldEdit/FAWE** 于 play/plugins → 主题换皮与三区重切须地图岗进服 WE 另号。  
2. **map 二进制不入 git** → 发布走 Release/同步流程（对齐 maps-v2）；本 README 为仓内真源说明。  
3. 进本键 / MM groups / S45 结算 **并行号**，本骨架不可玩家测通。

## 禁

空板 PASS · 覆盖其它 `ember_*` map 不备份 · 长期共用 `ember_short_sx01..05` 目录交差 · 放开旧日常 map 当短征 · 抬挂机表当奖励 · 纯换皮宣称第六条短征完工

## D444
- 环廊 · `@d444`
