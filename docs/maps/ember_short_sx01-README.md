# 地图骨架 · `ember_short_sx01`（短征 · 烬门哨岗 · D391）

**日期：** 2026-10-10 Asia/Shanghai  
**规格：** [`DESIGN-ember-short-dungeon-p1-reachable-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-p1-reachable-2026-10-10.md) §2.4  
**STATUS：** [`STATUS-ember-short-dungeon-sx01-d391-2026-10-10.md`](../status/STATUS-ember-short-dungeon-sx01-d391-2026-10-10.md)

## 惯例（查证）

| 层 | 路径 / 约定 |
|----|-------------|
| DP map 模板 | `plugins/DungeonPlus/map/<name>/`（`level.dat` + `region/*.mca`） |
| Git | **整目录 gitignore**（Release assets；`scripts/dp-maps-v2-verify.sh`）；**禁止** `git add -f` |
| 登记 | `plugins/DungeonPlus/config.yml` → map 名 ↔ dungeon id |
| 本壳 | `plugins/DungeonPlus/dungeon/<Id>/option.yml` · `$setmap{name=…}` + `$setspawn` |
| 主线近邻 | Q01 用 `ember_daily_v1` · spawn `0,64,5`（可步行白盒） |
| WE | DESIGN-ember-map-plan：WorldEdit/FAWE 后期；未装则占位模板 |

## 本号动作

```text
cp -a plugins/DungeonPlus/map/ember_daily_v1 \
      plugins/DungeonPlus/map/ember_short_sx01
```

- **目的：** 继承已有墙/门/垫体量，**禁空板宣称完成**。
- **非目的：** 不宣称烬门哨岗主题完工；不宣称三房叙事已按 R1/R2/R3 重切。

## 意向本地坐标（WE 后钉死 · 写入 option/monster）

| 区 | 意向 | 备注 |
|----|------|------|
| 出生 | `0,64,5`（现与 Q01 白盒同） | option 已暂写 |
| R1 院角 | 出生北侧短院 | 1～2 波小怪 |
| R2 甬道 | 串联走廊 | 精英 |
| R3 Boss 垫 | 抬高垫 · Cast r≈5 | `EmberSx01Warden` |
| 体量目标 | 约 28×28～32×32 · 高 10～14 | 主题：石砖哨墙 + 内院 + 火盆点缀 |

## 阻塞

1. **无 WorldEdit/FAWE** 于 play/plugins → 主题换皮与三区重切须地图岗进服另号。  
2. **map 二进制不入 git** → 发布走 Release/同步流程（对齐 maps-v2）；本 README 为仓内真源说明。  
3. 进本键 / MM groups / S40 结算 **并行号**，本骨架不可玩家测通。

## 禁

空板 PASS · 覆盖其它 `ember_*` map 不备份 · 放开旧日常 map 当短征 · 抬挂机表当奖励

## D443 真地图
- 线性哨岗廊 · `@d443` · d443 脚本
