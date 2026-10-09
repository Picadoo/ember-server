# 状态 · D400：短征 sx05 批 A·M 与骨架启动（裂谷风廊 · ≠关观察 ≠抬日表 ≠开 gate_daily）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-short-dungeon-sx05-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-short-dungeon-sx05-need-design-2026-10-10.md) @ `109e02b3` · DESIGN [`DESIGN-ember-short-dungeon-sx05-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx05-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D400** · 总控采纳方案 M（P1 短征 sx05 裂谷风廊）· tip 旁注已关 · backlog → **已批·施工中** · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠放开 gate_daily / EmberDaily*** · **≠开 R / K3**  
**版本：** docs + TrMenu 五本选页 + **本地** map 目录（`map/` gitignore，**未** `git add -f`）+ DP EmberSx05 轻量壳 · jar / 三开关 / bv / ×0.97 / afk **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

批了「裂谷风廊」第五条短征规格。仓里已有五本选页和 DP 本壳；地图在磁盘上从 **日更残誓/深廊白盒**（`ember_daily_crypt_v1`）复制了一份（**非** sx01/`ember_daily_v1`、**非** sx02/`ember_daily_rail_v1`、**非** sx03/`ember_daily_frost_v1`、**非** sx04/`ember_daily_ash_v1` 同观感），崖口/窄桥过隙/对岸风心还要等 WorldEdit。进本命令、刷怪波次、通关发奖（S44）、日帽键 `%corerpg_p1_sx05_day_line%` 还没接——并行岗接着做。选页第五格日帽行以注释预留，禁假写个人 n/3。

## 拆号（并行）

| 号 | 内容 | 本号 D400 | 阻塞 / 下一步 |
|----|------|-----------|----------------|
| **地图号** | `ember_short_sx05` 崖口台/窄桥过隙/对岸风心 + 出生/Boss 坐标 | **本地骨架已启**：`cp -a ember_daily_crypt_v1 → ember_short_sx05`（~2.0M）；说明见 [`docs/maps/ember_short_sx05-README.md`](../maps/ember_short_sx05-README.md) | **阻塞 WE**：仓/play **无** WorldEdit/FAWE jar；**禁** `git add -f` map。裂谷主题换皮 → 地图岗进服 WE 另号 |
| **MM 号** | 崖卫/风廊弩/风心守吏 + `EmberSx05…Cast` | **磁盘已有未入本 commit**（`Mobs/EmberSx05.yml` + WindCast；怪物岗并行，本号不重交） | 刷点坐标钉死后挂 runs/groups；MM tip 另 push |
| **DP+键号** | `EmberSx05` option/monster + `ember-v1-runs` `sx05`（**须 rooms×3+boss**）+ `p1 enter` + 体力30 + 日帽 | **壳已落**：`dungeon/EmberSx05/option.yml`（`$setmap` + `p1_pass_sx05` 门闩）+ `config.yml` 登记；**无** runs/jar 键 | 插件岗：`p1 enter sx05` · pass · `p1_sx05_day` · stamina 30 · rooms/boss 同构（防 D391 FAIL） |
| **插件日帽号** | `%corerpg_p1_sx05_day_line%` + 合计扩第五本 | **未动** | 另号；菜单已注释预留，禁假写 n/3 |
| **菜单号** | `ember_p1_short` 五本选页 + hub/adventure 半指 | **已落**：Title「短征 · 选本」· A/C/E/G 保留 · **K=sx05** · adventure `H` + Open · hub Open/冒险格半指含「裂谷」 | sx05 点进本=键未就绪时服务端拒（预期）；sx01–sx04 仍可进 |
| **经济号** | S44 发放 + REG/Economy/source-map +（荐）p1sim | **未动** | 另号金样；草案 80币+4碎+3骨尘；首通另加 140/6/1；**禁**抬挂机表 |

## 地图进度（诚实）

| 项 | 状态 |
|----|------|
| 目录 `plugins/DungeonPlus/map/ember_short_sx05/` | **磁盘已有**（gitignore，不入本 commit） |
| 模板来源 | **`ember_daily_crypt_v1`**（优先非前四本同观感；对齐 DESIGN E5/E6 · 深板岩裂谷气质） |
| 结构 | 继承残誓白盒体量，**非空板**；可步行出生暂写 `0,64,5` |
| 裂谷风廊主题情绪 / 真窄桥过隙 | **未换皮**（待 WE） |
| 三区 R1→R2→R3 叙事钉坐标 | **未重切**；README 写意向表 |
| 宣称 PASS / 可测通 | **否** — 仅骨架启动 |

## 菜单是否落盘

| 文件 | 落盘 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | **是**（五本选页 A/C/E/G/K；sx05 day_line **注释预留**） |
| `ember_p1_adventure.yml` Layout+`H`+Open | **是**（半指含裂谷） |
| `ember_hub.yml` Open/冒险半指 | **是** |
| trmenu reload | 本号热更 |

## 验收对照（骨架窗 ≠ 非整本 PASS）

| ID | 结果 | 备注 |
|----|------|------|
| V1 可达 | **PENDING** | 菜单壳在；`p1 enter sx05` / pass **未** |
| V2 sx01–sx04 仍可进 · 旧日常仍关 | **PASS（纪律）** | 本号未改 gate_daily / EmberDaily*；前四本进本键保留 |
| V3 窄桥+Cast+非空板 | **PARTIAL** | 地图非空板本地有；Cast/MM **未**；主题 WE **未** |
| V4 经济+未抬日表 | **PASS（纪律）** | 零改 afk.tiers/daily_kills；S44 未接线 |
| V5 日帽诚实分开 | **PARTIAL** | 菜单纯文案「最多 3 · 分开计」；`p1_sx05_day` / day_line **未**接线（注释预留） |
| V6 S44 登记 | **PENDING** | 经济另号 |
| V7 rooms×3+boss | **PENDING** | 键号首航硬提醒；本号未写 runs |
| V8 选页挂 day_line | **PENDING** | 键未 live → 注释预留；禁假写个人 n/3 |

## 改动清单（本号 · 入仓）

| 文件 | 改动 |
|------|------|
| DESIGN / tip / backlog | 勾批 A·M · tip 关 · **已批·施工中** |
| 本 STATUS + `docs/maps/ember_short_sx05-README.md` | 拆号 · 地图进度 · WE 阻塞 |
| `plugins/DungeonPlus/dungeon/EmberSx05/{option,monster,obstacle}.yml` | DP 轻量壳 |
| `plugins/DungeonPlus/config.yml` | 登记 `ember_short_sx05`→`EmberSx05` |
| `plugins/TrMenu/menus/ember_p1_short.yml` | 五本选页 + sx05 day_line 注释预留 |
| `ember_p1_adventure.yml` / `ember_hub.yml` | 入口半指含裂谷 |
| `plugins/DungeonPlus/map/ember_short_sx05/**` | **仅本地** · **未 stage**（gitignore） |
| jar / ember-v1.yml / afk / MM | **未 stage / 未改** |

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity/p1-six/MM 脏迹（常态） |
| C2 | **PASS** | cached 仅授权 docs/TrMenu/DP 壳 |
| C3 | **PASS** | 显式路径 add；未 `add -A`；**未** `add -f` map |
| C4 | **PASS** | 未碰 live ember-v1 Stage2 开关；未切分支；toplevel main |
| C5 | **PASS** | 脏 runtime / MM 草稿未入暂存 |

## 不动

关观察 · 勾 §2.4 · 抬日表 / afk.tiers · 放开 gate_daily · 开 R · 开 K3 · 改 ×0.97 · 假宣传旧日常开放 · 空板宣称 PASS · `git add -f` map · stage 脏 runtime · 同号写 jar/完整地形 · 假写个人日帽 n/3

---

*D400 批 A·M · tip `109e02b3` · 短征 sx05 骨架启动 · 地图本地 crypt 壳 · WE/进本键/S44/日帽键/MM 并行 · ≠关观察≠抬日表≠开旧日常闸。*
