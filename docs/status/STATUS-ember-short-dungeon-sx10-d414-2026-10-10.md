# 状态 · D414：短征 sx10 批 A·M 与骨架启动（烬闸递室 · ≠关观察 ≠抬日表 ≠开 gate_daily）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-short-dungeon-sx10-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-short-dungeon-sx10-need-design-2026-10-10.md) @ `e052caa9` · DESIGN [`DESIGN-ember-short-dungeon-sx10-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx10-2026-10-10.md) · 旁注批 A 已关  
**裁决：** **已批 A · 批 M · D414** · 总控采纳方案 M（P1 短征 sx10 烬闸递室）· tip 旁注已关 · backlog → **已批·施工中·骨架已落** · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠放开 gate_daily / EmberDaily*** · **≠开 R / K3**  
**版本：** docs + TrMenu 十本选页 + **本地** map 目录（`map/` gitignore，**未** `git add -f`）+ DP EmberSx10 轻量壳 · jar / 三开关 / bv / ×0.97 / afk **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

批了「烬闸递室」第十条短征规格。仓里已有十本选页和 DP 本壳；地图在磁盘上从 **团本白盒**（`ember_raid`）复制了一份（**非** sx01–sx09 已占的 daily_v1/rail/frost/ash/crypt/spire/tide/elite/abyss 同观感），外闸庭/递闸密封室/末室终厅还要等 WorldEdit。进本命令、通关发奖（S49）、日帽键 `%corerpg_p1_sx10_day_line%` / 首通 `%corerpg_p1_sx10_fc%` **键 live tip `9c8fb353` · 菜单挂盘已落**。选页第十格 V=sx10；禁假写个人 n/3；禁连续霜雾廊复读 sx03。

## 拆号（并行）

| 号 | 内容 | 本号 D414 | 阻塞 / 下一步 |
|----|------|-----------|----------------|
| **地图号** | `ember_short_sx10` 外闸庭/递闸密封室/末室烬台 + 出生/Boss 坐标 | **本地骨架已启**：`cp -a ember_raid → ember_short_sx10`（~2.9M）；说明见 [`docs/maps/ember_short_sx10-README.md`](../maps/ember_short_sx10-README.md) | **阻塞 WE**：仓/play **无** WorldEdit/FAWE jar；**禁** `git add -f` map。烬闸主题换皮 → 地图岗进服 WE 另号 |
| **MM 号** | 闸卫/闸室弩/末室守吏 + `EmberSx10…Cast` | **并行已交** tip `efb26b5d`（本号 DP 注释对齐；不重交 MM） | GateCast 已就绪 |
| **DP+键号** | `EmberSx10` option/monster + `ember-v1-runs` `sx10`（**须 rooms×3+boss**）+ `p1 enter` + 体力30 + 日帽 | **壳已落**（与 MM 号同构 @`efb26b5d`/本菜单号注释对齐）+ `config.yml` 登记；**键/S49/rooms 未接** | 插件号并行 |
| **插件日帽/首通号** | `%corerpg_p1_sx10_day_line%` / `_fc` + 合计扩第十本 | **键 live** tip `9c8fb353` · jar `1.65.121-d414` bv74 | 菜单挂盘见 dayline STATUS |
| **菜单号** | `ember_p1_short` 十本选页 + hub/adventure 半指 | **已落**：Title「短征 · 选本」· A/C/E/G/K/M/O/Q/T/**V** · adventure `H` + Open · hub「递闸」· **day_line/fc 挂盘已落**（[`STATUS-ember-short-sx10-dayline-d414-2026-10-10.md`](STATUS-ember-short-sx10-dayline-d414-2026-10-10.md)） | 全链路另派 |
| **经济号** | S49 发放 + REG/Economy/source-map +（荐）p1sim | **未动** | 另号金样；草案 80币+4碎+3骨尘；首通另加 90/6/1；**禁**抬挂机表 |

## 地图进度（诚实）

| 项 | 状态 |
|----|------|
| 目录 `plugins/DungeonPlus/map/ember_short_sx10/` | **磁盘已有**（gitignore，不入本 commit） |
| 模板来源 | **`ember_raid`**（非 sx01–sx09 同观感） |
| 结构 | 继承团本白盒体量，**非空板**；可步行出生暂写 `0,64,5` |
| 烬闸递室主题情绪 / 真密封递闸 ≥3 | **未换皮**（待 WE） |
| 三区 R1→R2→R3 叙事钉坐标 | **未重切**；README 写路径描述（外闸→递闸→末室） |
| 宣称 PASS / 可测通 | **否** — 仅骨架启动 |

## 菜单是否落盘

| 文件 | 落盘 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | **是**（十本选页 A/C/E/G/K/M/O/Q/T/**V**；sx10 day_line/fc **挂盘已落** tip `9c8fb353`） |
| `ember_p1_adventure.yml` Layout+`H`+Open | **是**（半指含递闸/烬闸） |
| `ember_hub.yml` Open/冒险半指 | **是** |
| trmenu reload | **PASS** · 骨架 `85 ms` @08:08:21 · 挂盘 `良好 \| 73 个菜单已加载 (133 ms)`（**08:45:17 CST**） |
| DP `EmberSx10` | **PASS** · 导入/初始化完毕（**08:08:26–08:08:27 CST**）；map `ember_short_sx10` 导入成功 |

## 验收对照（骨架窗 ≠ 非整本 PASS）

| ID | 结果 | 备注 |
|----|------|------|
| V1 可达 | **PENDING** | 菜单壳在；`p1 enter sx10` / pass **未** |
| V2 sx01–sx09 仍可进 · 旧日常仍关 | **PASS（纪律）** | 本号未改 gate_daily / EmberDaily*；前九本进本键保留 |
| V3 密封递闸+Cast+非空板 | **PARTIAL** | 地图非空板本地有；Cast/MM **已入仓** @`efb26b5d`；主题 WE **未**；**禁**连续霜雾廊 |
| V4 经济+未抬日表 | **PASS（纪律）** | 零改 afk.tiers/daily_kills；S49 未接线 |
| V5 日帽诚实分开 | **PASS（菜单纪律）** | V/I 真键；禁假写 n/3；合计文案「十本」 |
| V6 S49 登记 | **PENDING** | 经济另号 |
| V7 rooms×3+boss | **PENDING** | 键号首航硬提醒；本号未写 runs |
| V8 选页挂 day_line/fc | **PASS（菜单）** | 见 dayline STATUS · tip `9c8fb353` |

## 改动清单（本号 · 入仓）

| 文件 | 改动 |
|------|------|
| DESIGN / tip / backlog | 勾批 A·M · tip 关 · **已批·施工中·骨架已落** |
| 本 STATUS + `docs/maps/ember_short_sx10-README.md` | 拆号 · 地图进度 · WE 阻塞 |
| `plugins/DungeonPlus/dungeon/EmberSx10/{option,monster,obstacle}.yml` | DP 轻量壳 |
| `plugins/DungeonPlus/config.yml` | 登记 `ember_short_sx10`→`EmberSx10` |
| `plugins/TrMenu/menus/ember_p1_short.yml` | 十本选页 + sx10 day_line/fc **挂盘已落** |
| `ember_p1_adventure.yml` / `ember_hub.yml` | 入口半指含递闸 |
| `plugins/DungeonPlus/map/ember_short_sx10/**` | **仅本地** · **未 stage**（gitignore） |
| jar / ember-v1.yml / afk / MM | **未 stage / 未改 jar/afk/Stage2** |

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有常态脏迹 |
| C2 | **PASS** | cached 仅授权 docs/TrMenu/DP 壳 |
| C3 | **PASS** | 显式路径 add；未 `add -A`；**未** `add -f` map |
| C4 | **PASS** | 未碰 live ember-v1 Stage2 开关；未切分支；toplevel main |
| C5 | **PASS** | 脏 runtime / MM 草稿未入暂存 |

## 不动

关观察 · 勾 §2.4 · 抬日表 / afk.tiers · 放开 gate_daily · 开 R · 开 K3 · 改 ×0.97 · 假宣传旧日常开放 · 空板宣称 PASS · `git add -f` map · stage 脏 runtime · 同号写 jar/完整地形 · 假写个人日帽 n/3 · 假造完整 MM · 连续霜雾廊复读 sx03 · 派测

---

*D414 批 A·M · 上游 tip `e052caa9` · 短征 sx10 骨架启动 · 地图本地 raid 壳 · MM/DP @`efb26b5d` · WE/进本键/S49/日帽键并行 · ≠关观察≠抬日表≠开旧日常闸 · 未派测。*
