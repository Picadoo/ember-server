# 状态 · D421：短征 sx13 批 A·M 与骨架启动（烬衡悬梁 · ≠关观察 ≠抬日表 ≠开 gate_daily）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-short-dungeon-sx13-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-short-dungeon-sx13-need-design-2026-10-10.md) @ `d61e0d88` · DESIGN [`DESIGN-ember-short-dungeon-sx13-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx13-2026-10-10.md) · 旁注批 A 已关  
**裁决：** **已批 A · 批 M · D421** · 总控采纳方案 M（P1 短征 sx13 烬衡悬梁）· tip 旁注已关 · backlog → **已批·施工中·骨架启动** · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠放开 gate_daily / EmberDaily*** · **≠开 R / K3** · **未派测** · **本号不写 jar/MM**  
**版本：** docs + TrMenu 十三本选页 + **本地** map 目录（`map/` gitignore，**未** `git add -f`）· jar / MM / DP / 三开关 / bv / ×0.97 / afk **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

批了「烬衡悬梁」第十三条短征规格。仓里已有十三本选页；地图在磁盘上从 **团本白盒**（`ember_raid`）复制了一份（**本地独立文件夹**；主题须 WE 重切衡门庭/悬梁衡廊（≥3配重衡梁）/衡冠，禁窄桥/跳石/转枢/递闸/对廊等气质复读）。进本命令、通关发奖（S52）、日帽键 `%corerpg_p1_sx13_day_line%` / 首通 `%corerpg_p1_sx13_fc%` **注释预留**（键号另挂；禁假写 n/3）。选页第十三格 **W=sx13**。

## 拆号（并行）

| 号 | 内容 | 本号 D421 | 阻塞 / 下一步 |
|----|------|-----------|----------------|
| **地图号** | `ember_short_sx13` 衡门庭/悬梁衡廊/衡冠烬台 + 出生/Boss 坐标 | **本地骨架已启**：`cp -a ember_raid → ember_short_sx13`（~2.9M）；说明见 [`docs/maps/ember_short_sx13-README.md`](../maps/ember_short_sx13-README.md) | **阻塞 WE**：仓/play **无** WorldEdit/FAWE jar；**禁** `git add -f` map。烬衡主题换皮 → 地图岗进服 WE 另号 |
| **MM 号** | 衡门卫/梁廊衡侍/衡冠守吏 + `EmberSx13…Cast` | **本号不写** | 另号 |
| **DP+键号** | `EmberSx13` option/monster + `ember-v1-runs` `sx13`（**须 rooms×3+boss**）+ `p1 enter` + 体力30 + 日帽 | **本号不写** | 插件号并行 |
| **插件日帽/首通号** | `%corerpg_p1_sx13_day_line%` / `_fc` + 合计扩第十三本 | **注释预留**（键号另挂） | 键 live 后同批挂盘 |
| **菜单号** | `ember_p1_short` 十三本选页 + hub/adventure 半指 | **已落**：Title「短征 · 选本」· A/C/E/G/K/M/O/Q/T/V/X/Y/**W** · adventure `H` + Open · hub「衡梁」· **day_line/fc 注释预留** | 键号齐后挂盘；全链路另派 |
| **经济号** | S52 发放 + REG/Economy/source-map +（荐）p1sim | **未动** | 另号金样；草案 80币+4碎+3骨尘；首通另加 60/6/1；**禁**抬挂机表 |

## 地图进度（诚实）

| 项 | 状态 |
|----|------|
| 目录 `plugins/DungeonPlus/map/ember_short_sx13/` | **磁盘已有**（gitignore，不入本 commit） |
| 模板来源 | **`ember_raid`**（本地独立文件夹；主题 WE 另切） |
| 结构 | 继承团本白盒体量，**非空板**；可步行出生暂写 `0,64,5` |
| 烬衡悬梁主题情绪 / 真配重衡梁 | **未换皮**（待 WE） |
| 三区 R1→R2→R3 叙事钉坐标 | **未重切**；README 写路径描述（衡门庭→悬梁衡廊→衡冠终厅） |
| 宣称 PASS / 可测通 | **否** — 仅骨架启动 |

## 菜单是否落盘

| 文件 | 落盘 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | **是**（十三本选页 A/C/E/G/K/M/O/Q/T/V/X/Y/**W**；sx13 day_line/fc **注释预留**） |
| `ember_p1_adventure.yml` Layout+`H`+Open | **是**（半指含衡梁/烬衡） |
| `ember_hub.yml` Open/冒险半指 | **是** |
| trmenu reload | **PASS** · `良好 \| 73 个菜单已加载 (104 ms)`（**09:47:22 CST**） |
| DP `EmberSx13` / MM | **本号不写** |

## 验收对照（骨架窗 ≠ 非整本 PASS）

| ID | 结果 | 备注 |
|----|------|------|
| V1 可达 | **PENDING** | 菜单壳在；`p1 enter sx13` / pass **未**（键/DP 另号） |
| V2 sx01–sx12 仍可进 · 旧日常仍关 | **PASS（纪律）** | 本号未改 gate_daily / EmberDaily*；前十二本进本键保留 |
| V3 悬梁衡廊+Cast+非空板 | **PARTIAL** | 地图非空板本地有；Cast/MM **未**；主题 WE **未**；**禁**窄桥/跳石/转枢/递闸/对廊复读 |
| V4 经济+未抬日表 | **PASS（纪律）** | 零改 afk.tiers/daily_kills；S52 未接线 |
| V5 日帽诚实分开 | **PASS（菜单纪律）** | W/I 预留；禁假写 n/3；合计文案「十三本合计键另挂」 |
| V6 S52 登记 | **PENDING** | 经济另号 |
| V7 rooms×3+boss | **PENDING** | 键号首航硬提醒；本号未写 runs |
| V8 选页挂 day_line/fc | **PENDING（预留）** | 键号另挂后挂盘 |

## 改动清单（本号 · 入仓）

| 文件 | 改动 |
|------|------|
| DESIGN / tip / backlog | 勾批 A·M · tip 关 · **已批·施工中·骨架启动** |
| 本 STATUS + `docs/maps/ember_short_sx13-README.md` | 拆号 · 地图进度 · WE 阻塞 · STATUS 指针 |
| `plugins/TrMenu/menus/ember_p1_short.yml` | 十三本选页 W=sx13 + sx13 day_line/fc **注释预留** |
| `ember_p1_adventure.yml` / `ember_hub.yml` | 入口半指含衡梁 |
| `plugins/DungeonPlus/map/ember_short_sx13/**` | **仅本地** · **未 stage**（gitignore） |
| jar / MM / DP / ember-v1.yml / afk | **未 stage / 未改** |

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有常态脏迹 · 本号显式路径 |
| C2 | **PASS** | cached 仅授权 docs/TrMenu |
| C3 | **PASS** | 显式路径 add；未 `add -A`；**未** `add -f` map |
| C4 | **PASS** | 未碰 live ember-v1 Stage2 开关；未切分支；toplevel main |
| C5 | **PASS** | 脏 runtime / 未跟踪 dungeon 副本目录未入暂存 |

## 不动

关观察 · 勾 §2.4 · 抬日表 / afk.tiers · 放开 gate_daily · 开 R · 开 K3 · 改 ×0.97 · 假宣传旧日常开放 · 空板宣称 PASS · `git add -f` map · stage 脏 runtime · 同号写 jar/MM/完整地形 · 假写个人日帽 n/3 · 窄桥/跳石/转枢/递闸/对廊/环廊/塔升/错层/风廊/霜雾廊复读 · 派测

---

*D421 批 A·M · 上游 tip `d61e0d88` · 短征 sx13 骨架启动 · 地图本地 raid 壳 · 十三本选页 W=sx13 · WE/进本键/S52/日帽键/MM/DP 并行 · ≠关观察≠抬日表≠开旧日常闸 · 未派测。*
