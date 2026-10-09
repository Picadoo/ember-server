# 状态 · D393：短征 sx03 批 A·M 与骨架启动（霜雾闸廊 · ≠关观察 ≠抬日表 ≠开 gate_daily）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-short-dungeon-sx03-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-short-dungeon-sx03-need-design-2026-10-10.md) @ `088fd3b7` · DESIGN [`DESIGN-ember-short-dungeon-sx03-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx03-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D393** · 总控采纳方案 M（P1 短征 sx03 霜雾闸廊）· tip 旁注已关 · backlog → **已批·施工中** · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠放开 gate_daily / EmberDaily*** · **≠开 R / K3**  
**版本：** docs + TrMenu 三本选页 + **本地** map 目录（`map/` gitignore，**未** `git add -f`）· DP/config 壳已由并行 `71e03569` 入仓（本号 README/菜单对齐）· jar / 三开关 / bv / ×0.97 / afk **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

批了「霜雾闸廊」第三条短征规格。仓里已有三本选页和 DP 本壳；地图在磁盘上从 **日更霜雾白盒**（`ember_daily_frost_v1`）复制了一份（**非** sx01/`ember_daily_v1`、**非** sx02/`ember_daily_rail_v1` 同观感），主题雾廊/双闸/霜窖还要等 WorldEdit。进本命令、刷怪波次、通关发奖（S42）还没接——并行岗接着做。顺手清了 sx02「结算另号 / 进本键另号就绪」陈旧半行。

## 拆号（并行）

| 号 | 内容 | 本号 D393 | 阻塞 / 下一步 |
|----|------|-----------|----------------|
| **地图号** | `ember_short_sx03` 雾廊/双闸/霜窖垫 + 出生/Boss 坐标 | **本地骨架已启**：`cp -a ember_daily_frost_v1 → ember_short_sx03`（1.1M · region×4）；说明见 [`docs/maps/ember_short_sx03-README.md`](../maps/ember_short_sx03-README.md) | **阻塞 WE**：仓/play **无** WorldEdit/FAWE jar；**禁** `git add -f` map。霜雾主题换皮 → 地图岗进服 WE 另号 |
| **MM 号** | 巡卒/闸弩/守吏 + `EmberSx03…Cast` | **并行已有**：怪物岗 tip `71e03569`（MM+FrostCast+DP壳；本号不重交） | 刷点坐标钉死后挂 runs/groups |
| **DP+键号** | `EmberSx03` option/monster + `ember-v1-runs` `sx03`（**须 rooms×3+boss**）+ `p1 enter` + 体力30 + 日帽 | **壳已落**（并行 `71e03569` 入仓）：`dungeon/EmberSx03/option.yml`（`$setmap` + `p1_pass_sx03` 门闩）+ `config.yml` 登记；**无** runs/jar 键 | 插件岗：`p1 enter sx03` · pass · `p1_sx03_day` · stamina 30 · rooms/boss 同构（防 D391 FAIL） |
| **菜单号** | `ember_p1_short` 三本选页 + hub/adventure 半指 | **已落**：Title「短征 · 选本」· A=sx01 · C=sx02 · **E=sx03** · 清 sx02「结算另号/进本键另号就绪」陈旧半行 · adventure `H` + Open · hub Open/冒险格半指「烬门 / 锈灯 / 霜雾」 | sx03 点进本=键未就绪时服务端拒（预期）；sx01/sx02 仍可进 |
| **经济号** | S42 发放 + REG/Economy/source-map +（荐）p1sim | **未动** | 另号金样；草案 70币+5碎+3骨尘；**禁**抬挂机表 |

## 地图进度（诚实）

| 项 | 状态 |
|----|------|
| 目录 `plugins/DungeonPlus/map/ember_short_sx03/` | **磁盘已有**（gitignore，不入本 commit） |
| 模板来源 | **`ember_daily_frost_v1`**（优先非 sx01/sx02 同观感；对齐 DESIGN E6） |
| 结构 | 继承霜雾白盒体量，**非空板**；可步行出生暂写 `0,64,5` |
| 霜雾闸廊主题情绪 / 真双闸形体 | **未换皮**（待 WE） |
| 三区 R1→R2→R3 叙事钉坐标 | **未重切**；README 写意向表 |
| 宣称 PASS / 可测通 | **否** — 仅骨架启动 |

## 菜单是否落盘

| 文件 | 落盘 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | **是**（三本选页 A/C/E） |
| `ember_p1_adventure.yml` Layout+`H`+Open | **是**（半指「烬门 / 锈灯 / 霜雾」） |
| `ember_hub.yml` Open/冒险半指 | **是** |
| trmenu reload | 本号热更 |

## 验收对照（骨架窗 · 非整本 PASS）

| ID | 结果 | 备注 |
|----|------|------|
| V1 可达 | **PENDING** | 菜单壳在；`p1 enter sx03` / pass **未** |
| V2 sx01/sx02 仍可进 · 旧日常仍关 | **PASS（纪律）** | 本号未改 gate_daily / EmberDaily*；sx01/sx02 进本键保留 |
| V3 双闸+Cast+非空板 | **PARTIAL** | 地图非空板本地有；Cast/刷点/主题 WE **未** |
| V4 经济+未抬日表 | **PASS（纪律）** | 零改 afk.tiers/daily_kills；S42 未接线 |
| V5 日帽诚实分开 | **PARTIAL** | 菜单纯文案；`p1_sx03_day` 计数器未接线 |
| V6 S42 登记 | **PENDING** | 经济另号 |
| V7 rooms×3+boss | **PENDING** | 键号首航硬提醒；本号未写 runs |

## 改动清单（本号 · 入仓）

| 文件 | 改动 |
|------|------|
| DESIGN / tip / backlog | 勾批 A·M · tip 关 · **已批·施工中** |
| 本 STATUS + `docs/maps/ember_short_sx03-README.md` | 拆号 · 地图进度 · WE 阻塞 |
| `plugins/DungeonPlus/dungeon/EmberSx03/{option,monster,obstacle}.yml` | DP 壳（并行 `71e03569` 已入仓；本号不重交） |
| `plugins/DungeonPlus/config.yml` | 登记 `ember_short_sx03`→`EmberSx03`（并行已入仓） |
| `plugins/TrMenu/menus/ember_p1_short.yml` | 三本选页 + 清 sx02 陈旧半行 |
| `ember_p1_adventure.yml` / `ember_hub.yml` | 入口半指 |
| `plugins/DungeonPlus/map/ember_short_sx03/**` | **仅本地** · **未 stage**（gitignore） |
| jar / ember-v1.yml / afk / MM | **未 stage / 未改 live 开关** |

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity/p1-six/MM 脏迹（常态） |
| C2 | **PASS** | cached 仅授权 docs/DP壳/TrMenu/config |
| C3 | **PASS** | 显式路径 add；未 `add -A`；**未** `add -f` map |
| C4 | **PASS** | 未碰 live ember-v1 Stage2 开关；未切分支；toplevel main |
| C5 | **PASS** | 脏 runtime / MM 草稿未入暂存 |

## 不动

关观察 · 勾 §2.4 · 抬日表 / afk.tiers · 放开 gate_daily · 开 R · 开 K3 · 改 ×0.97 · 假宣传旧日常开放 · 空板宣称 PASS · `git add -f` map · stage 脏 runtime · 同号写 jar/MM/完整地形

---

*D393 批 A·M · tip `088fd3b7` · 短征 sx03 骨架启动 · 地图本地 frost 壳 · WE/进本键/MM/S42 并行 · ≠关观察≠抬日表≠开旧日常闸。*
