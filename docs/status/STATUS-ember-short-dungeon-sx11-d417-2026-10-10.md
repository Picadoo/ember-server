# 状态 · D417：短征 sx11 批 A·M 与骨架启动（烬镜对廊 · ≠关观察 ≠抬日表 ≠开 gate_daily）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-short-dungeon-sx11-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-short-dungeon-sx11-need-design-2026-10-10.md) @ `fc951892` · DESIGN [`DESIGN-ember-short-dungeon-sx11-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx11-2026-10-10.md) · 旁注批 A 已关  
**裁决：** **已批 A · 批 M · D417** · 总控采纳方案 M（P1 短征 sx11 烬镜对廊）· tip 旁注已关 · backlog → **已批·施工中·骨架已落** · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠放开 gate_daily / EmberDaily*** · **≠开 R / K3**  
**版本：** docs + TrMenu 十一本选页 + **本地** map 目录（`map/` gitignore，**未** `git add -f`）+ DP EmberSx11 轻量壳（MM 并行 @`1b81b415`）· jar / 三开关 / bv / ×0.97 / afk **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

批了「烬镜对廊」第十一条短征规格。仓里已有十一本选页和 DP 本壳；地图在磁盘上从 **团本白盒**（`ember_raid`）复制了一份（**本地独立文件夹**；主题须 WE 重切对称双廊，禁环廊/递闸/霜雾廊/跳石/错层/塔升/风廊气质复读），对廊前厅/左右双廊/合镜终厅还要等 WorldEdit。进本命令、通关发奖（S50）、日帽键 `%corerpg_p1_sx11_day_line%` / 首通 `%corerpg_p1_sx11_fc%` **注释预留**（键号另挂）。选页第十一格 X=sx11；禁假写个人 n/3。

## 拆号（并行）

| 号 | 内容 | 本号 D417 | 阻塞 / 下一步 |
|----|------|-----------|----------------|
| **地图号** | `ember_short_sx11` 对廊前厅/左右双廊/合镜烬台 + 出生/Boss 坐标 | **本地骨架已启**：`cp -a ember_raid → ember_short_sx11`（~2.9M）；说明见 [`docs/maps/ember_short_sx11-README.md`](../maps/ember_short_sx11-README.md) | **阻塞 WE**：仓/play **无** WorldEdit/FAWE jar；**禁** `git add -f` map。烬镜主题换皮 → 地图岗进服 WE 另号 |
| **MM 号** | 对廊卫/侧廊弩/镜廊督卫/合镜守吏 + `EmberSx11…MirrorCast` | **并行已交** tip `1b81b415`（本号 DP 注释对齐；不重交 MM） | MirrorCast 已就绪 |
| **DP+键号** | `EmberSx11` option/monster + `ember-v1-runs` `sx11`（**须 rooms×3+boss**）+ `p1 enter` + 体力30 + 日帽 | **壳已落**（与 MM 号同构 @`1b81b415`/本菜单号注释对齐）+ `config.yml` 登记；**键/S50/rooms 未接** | 插件号并行 |
| **插件日帽/首通号** | `%corerpg_p1_sx11_day_line%` / `_fc` + 合计扩第十一本 | **未装** · 菜单**注释预留** | 键 live 后挂盘另号 |
| **菜单号** | `ember_p1_short` 十一本选页 + hub/adventure 半指 | **已落**：Title「短征 · 选本」· A/C/E/G/K/M/O/Q/T/V/**X** · adventure `H` + Open · hub「镜廊」· day_line/fc **注释预留** | 全链路另派 |
| **经济号** | S50 发放 + REG/Economy/source-map +（荐）p1sim | **未动** | 另号金样；草案 80币+4碎+3骨尘；首通另加 80/6/1；**禁**抬挂机表 |

## 地图进度（诚实）

| 项 | 状态 |
|----|------|
| 目录 `plugins/DungeonPlus/map/ember_short_sx11/` | **磁盘已有**（gitignore，不入本 commit） |
| 模板来源 | **`ember_raid`**（本地独立文件夹；主题 WE 另切） |
| 结构 | 继承团本白盒体量，**非空板**；可步行出生暂写 `0,64,5` |
| 烬镜对廊主题情绪 / 真对称双廊 | **未换皮**（待 WE） |
| 三区 R1→R2→R3 叙事钉坐标 | **未重切**；README 写路径描述（对廊前厅→左右双廊→合镜终厅） |
| 宣称 PASS / 可测通 | **否** — 仅骨架启动 |

## 菜单是否落盘

| 文件 | 落盘 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | **是**（十一本选页 A/C/E/G/K/M/O/Q/T/V/**X**；sx11 day_line/fc **注释预留**） |
| `ember_p1_adventure.yml` Layout+`H`+Open | **是**（半指含镜廊/烬镜） |
| `ember_hub.yml` Open/冒险半指 | **是** |
| trmenu reload | **PASS** · `良好 \| 73 个菜单已加载 (207 ms)`（**09:08:56 CST**） |
| DP `EmberSx11` | **PASS（MM 号）** · tip `1b81b415`；map `ember_short_sx11` 本地已有 |

## 验收对照（骨架窗 ≠ 非整本 PASS）

| ID | 结果 | 备注 |
|----|------|------|
| V1 可达 | **PENDING** | 菜单壳在；`p1 enter sx11` / pass **未** |
| V2 sx01–sx10 仍可进 · 旧日常仍关 | **PASS（纪律）** | 本号未改 gate_daily / EmberDaily*；前十本进本键保留 |
| V3 对称双廊+Cast+非空板 | **PARTIAL** | 地图非空板本地有；Cast/MM **已入仓** @`1b81b415`；主题 WE **未**；**禁**环廊/递闸/霜雾廊/跳石/错层/塔升/风廊复读 |
| V4 经济+未抬日表 | **PASS（纪律）** | 零改 afk.tiers/daily_kills；S50 未接线 |
| V5 日帽诚实分开 | **PASS（菜单纪律）** | X/I 预留；禁假写 n/3；合计文案「十一本合计键另挂」 |
| V6 S50 登记 | **PENDING** | 经济另号 |
| V7 rooms×3+boss | **PENDING** | 键号首航硬提醒；本号未写 runs |
| V8 选页挂 day_line/fc | **PENDING（注释预留）** | 真键另挂后挂盘 |

## 改动清单（本号 · 入仓）

| 文件 | 改动 |
|------|------|
| DESIGN / tip / backlog | 勾批 A·M · tip 关 · **已批·施工中·骨架已落** |
| 本 STATUS + `docs/maps/ember_short_sx11-README.md` | 拆号 · 地图进度 · WE 阻塞 · STATUS 指针 |
| `plugins/TrMenu/menus/ember_p1_short.yml` | 十一本选页 X=sx11 + day_line/fc 注释预留 |
| `ember_p1_adventure.yml` / `ember_hub.yml` | 入口半指含镜廊 |
| `plugins/DungeonPlus/map/ember_short_sx11/**` | **仅本地** · **未 stage**（gitignore；MM 号已拷） |
| DP EmberSx11 / MM / config.yml | **本号不重交**（已 @`1b81b415`） |
| jar / ember-v1.yml / afk | **未 stage / 未改 jar/afk/Stage2** |

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有常态脏迹 |
| C2 | **PASS** | cached 仅授权 docs/TrMenu |
| C3 | **PASS** | 显式路径 add；未 `add -A`；**未** `add -f` map |
| C4 | **PASS** | 未碰 live ember-v1 Stage2 开关；未切分支；toplevel main |
| C5 | **PASS** | 脏 runtime / 未跟踪 dungeon 副本目录未入暂存 |

## 不动

关观察 · 勾 §2.4 · 抬日表 / afk.tiers · 放开 gate_daily · 开 R · 开 K3 · 改 ×0.97 · 假宣传旧日常开放 · 空板宣称 PASS · `git add -f` map · stage 脏 runtime · 同号写 jar/完整地形 · 假写个人日帽 n/3 · 假造完整 MM · 环廊/递闸/霜雾廊/跳石/错层/塔升/风廊复读 · 派测

---

*D417 批 A·M · 上游 tip `fc951892` · 短征 sx11 骨架启动 · 地图本地 raid 壳 · MM/DP @`1b81b415` · 十一本选页 X=sx11 · WE/进本键/S50/日帽键并行 · ≠关观察≠抬日表≠开旧日常闸 · 未派测。*
