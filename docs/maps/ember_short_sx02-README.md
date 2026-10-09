# 地图骨架 · `ember_short_sx02`（短征 · 锈灯栈道 · D392）

**日期：** 2026-10-10 Asia/Shanghai  
**规格：** [`DESIGN-ember-short-dungeon-sx02-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx02-2026-10-10.md) §2.5  
**STATUS：** [`STATUS-ember-short-sx02-mm-2026-10-10.md`](../status/STATUS-ember-short-sx02-mm-2026-10-10.md)

## 惯例

| 层 | 路径 / 约定 |
|----|-------------|
| DP map 模板 | `plugins/DungeonPlus/map/ember_short_sx02/`（`level.dat` + `region/*.mca`） |
| Git | **整目录 gitignore**；禁止 `git add -f` |
| 登记 | `plugins/DungeonPlus/config.yml` → `ember_short_sx02` ↔ `EmberSx02` |
| 本壳 | `plugins/DungeonPlus/dungeon/EmberSx02/option.yml` · `$setmap` + `$setspawn` |
| 与 sx01 | **独立文件夹**；本壳自 `ember_daily_rail_v1` 拷（非 sx01 同观感） |

## 本号动作

地图岗已本地 `cp` 骨架；MM 号只钉怪/Cast/刷点对照，**不**宣称栈道主题 WE 完工。

## 意向本地坐标（沿用 Q01/sx01 三房惯例 · WE 后可重钉）

| 区 | 坐标 | MM |
|----|------|-----|
| 出生 | `0,64,5` | — |
| R1 栈台 | (-6/0/6,64,29) 等 | `EmberSx02Guard` / `EmberSx02Archer` |
| R2 灯桥汇合 | `(30,64,39)` | `EmberSx02Elite` |
| R3 灯塔垫 | `(0,64,102)` | `EmberSx02Warden` + BeaconCast |

## 阻塞

主题 WE（抬升栈道 / 分叉灯桥 / 灯塔垫）→ 地图岗另号；键号写 `short.sx02` rooms/boss。
