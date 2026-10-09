# 状态 · D412：短征 sx09 批 A·M 与骨架启动（烬渠跳石 · ≠关观察 ≠抬日表 ≠开 gate_daily）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-short-dungeon-sx09-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-short-dungeon-sx09-need-design-2026-10-10.md) @ `a35ae9bd` · DESIGN [`DESIGN-ember-short-dungeon-sx09-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx09-2026-10-10.md) · 旁注批 A 已关  
**裁决：** **已批 A · 批 M · D412** · 总控采纳方案 M（P1 短征 sx09 烬渠跳石）· tip 旁注已关 · backlog → **已批·施工中·骨架已落** · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠放开 gate_daily / EmberDaily*** · **≠开 R / K3**  
**版本：** docs + TrMenu 九本选页 + **本地** map 目录（`map/` gitignore，**未** `git add -f`）+ DP EmberSx09 轻量壳 · jar / 三开关 / bv / ×0.97 / afk **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

批了「烬渠跳石」第九条短征规格。仓里已有九本选页和 DP 本壳；地图在磁盘上从 **深渊白盒**（`ember_abyss`）复制了一份（**非** sx01–sx08 已占的 daily_v1/rail/frost/ash/crypt/spire/tide/elite 同观感），渠岸庭/离散跳石渡渠/对岸终厅还要等 WorldEdit。进本命令、通关发奖（S48）、日帽键 `%corerpg_p1_sx09_day_line%` / 首通 `%corerpg_p1_sx09_fc%` 还没接——并行岗接着做。选页第九格日帽/首通行**注释预留**（禁假写个人 n/3）。

## 拆号（并行）

| 号 | 内容 | 本号 D412 | 阻塞 / 下一步 |
|----|------|-----------|----------------|
| **地图号** | `ember_short_sx09` 渠岸/跳石渡渠/对岸烬台 + 出生/Boss 坐标 | **本地骨架已启**：`cp -a ember_abyss → ember_short_sx09`（~2.9M）；说明见 [`docs/maps/ember_short_sx09-README.md`](../maps/ember_short_sx09-README.md) | **阻塞 WE**：仓/play **无** WorldEdit/FAWE jar；**禁** `git add -f` map。烬渠主题换皮 → 地图岗进服 WE 另号 |
| **MM 号** | 渠卫/跳石弩/对岸守吏 + `EmberSx09…Cast` | **并行已交** @`58f0e791`（本菜单/地图号对齐 DP 壳注释；不重交 MM） | CanalCast 已就绪 |
| **DP+键号** | `EmberSx09` option/monster + `ember-v1-runs` `sx09`（**须 rooms×3+boss**）+ `p1 enter` + 体力30 + 日帽 | **壳已落**（与 MM 号同构 @`58f0e791`）+ `config.yml` 登记；**键/S48/rooms 未写** | 插件键号另号 |
| **插件日帽/首通号** | `%corerpg_p1_sx09_day_line%` / `_fc` + 合计扩第九本 | **注释预留**（菜单未挂假键） | 键号+挂盘另号 |
| **菜单号** | `ember_p1_short` 九本选页 + hub/adventure 半指 | **已落**：Title「短征 · 选本」· A/C/E/G/K/M/O/Q 保留 · **T=sx09** · adventure `H` + Open · hub Open/冒险格半指含「跳石」· **day_line/fc 注释预留** | sx01–sx09 选页；全链路另派 |
| **经济号** | S48 发放 + REG/Economy/source-map +（荐）p1sim | **未动** | 另号金样；草案 80币+4碎+3骨尘；首通另加 100/6/1；**禁**抬挂机表 |

## 地图进度（诚实）

| 项 | 状态 |
|----|------|
| 目录 `plugins/DungeonPlus/map/ember_short_sx09/` | **磁盘已有**（gitignore，不入本 commit） |
| 模板来源 | **`ember_abyss`**（非 sx01–sx08 同观感） |
| 结构 | 继承深渊白盒体量，**非空板**；可步行出生暂写 `0,64,5` |
| 烬渠跳石主题情绪 / 真离散跳石渡渠 | **未换皮**（待 WE） |
| 三区 R1→R2→R3 叙事钉坐标 | **未重切**；README 写路径描述（渠岸→跳石→对岸） |
| 宣称 PASS / 可测通 | **否** — 仅骨架启动 |

## 菜单是否落盘

| 文件 | 落盘 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | **是**（九本选页 A/C/E/G/K/M/O/Q/**T**；sx09 day_line/fc **注释预留**） |
| `ember_p1_adventure.yml` Layout+`H`+Open | **是**（半指含跳石/烬渠） |
| `ember_hub.yml` Open/冒险半指 | **是** |
| trmenu reload | **PASS** · `良好 \| 73 个菜单已加载 (83 ms)`（**07:49:41 CST**）；中间曾 YAML 解析失败（缩进插坏）已修；DP `EmberSx09` 初始化 OK（**07:49:35 CST**） |

## 验收对照（骨架窗 ≠ 非整本 PASS）

| ID | 结果 | 备注 |
|----|------|------|
| V1 可达 | **PENDING** | 菜单壳在；`p1 enter sx09` / pass **未** |
| V2 sx01–sx08 仍可进 · 旧日常仍关 | **PASS（纪律）** | 本号未改 gate_daily / EmberDaily*；前八本进本键保留 |
| V3 跳石渡渠+Cast+非空板 | **PARTIAL** | 地图非空板本地有；Cast/MM **未**；主题 WE **未**；**禁**连续窄桥 |
| V4 经济+未抬日表 | **PASS（纪律）** | 零改 afk.tiers/daily_kills；S48 未接线 |
| V5 日帽诚实分开 | **PARTIAL（菜单）** | T/I 注释预留；禁假写 n/3；合计文案已写「九本」（键扩另号） |
| V6 S48 登记 | **PENDING** | 经济另号 |
| V7 rooms×3+boss | **PENDING** | 键号首航硬提醒；本号未写 runs |
| V8 选页挂 day_line/fc | **PENDING（注释预留）** | 等插件键号挂盘另号 |

## 改动清单（本号 · 入仓）

| 文件 | 改动 |
|------|------|
| DESIGN / tip / backlog | 勾批 A·M · tip 关 · **已批·施工中·骨架已落** |
| 本 STATUS + `docs/maps/ember_short_sx09-README.md` | 拆号 · 地图进度 · WE 阻塞 |
| `plugins/DungeonPlus/dungeon/EmberSx09/{option,monster,obstacle}.yml` | DP 轻量壳 |
| `plugins/DungeonPlus/config.yml` | 登记 `ember_short_sx09`→`EmberSx09` |
| `plugins/TrMenu/menus/ember_p1_short.yml` | 九本选页 + sx09 day_line/fc 注释预留 |
| `ember_p1_adventure.yml` / `ember_hub.yml` | 入口半指含跳石 |
| `plugins/DungeonPlus/map/ember_short_sx09/**` | **仅本地** · **未 stage**（gitignore） |
| jar / ember-v1.yml / afk / MM | **未 stage / 未改 jar/afk/Stage2** |

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity/p1-six/MM 脏迹（常态） |
| C2 | **PASS** | cached 仅授权 docs/TrMenu/DP 壳 |
| C3 | **PASS** | 显式路径 add；未 `add -A`；**未** `add -f` map |
| C4 | **PASS** | 未碰 live ember-v1 Stage2 开关；未切分支；toplevel main |
| C5 | **PASS** | 脏 runtime / MM 草稿未入暂存 |

## 不动

关观察 · 勾 §2.4 · 抬日表 / afk.tiers · 放开 gate_daily · 开 R · 开 K3 · 改 ×0.97 · 假宣传旧日常开放 · 空板宣称 PASS · `git add -f` map · stage 脏 runtime · 同号写 jar/完整地形 · 假写个人日帽 n/3 · 假造完整 MM · 连续窄桥复读 sx05

---

*D412 批 A·M · tip `a35ae9bd` · 短征 sx09 骨架启动 · 地图本地 abyss 壳 · WE/进本键/S48/日帽键/MM 并行 · ≠关观察≠抬日表≠开旧日常闸。*
