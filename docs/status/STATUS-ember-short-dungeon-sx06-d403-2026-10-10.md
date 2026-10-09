# 状态 · D403：短征 sx06 批 A·M 与骨架启动（烬环廊 · ≠关观察 ≠抬日表 ≠开 gate_daily）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-short-dungeon-sx06-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-short-dungeon-sx06-need-design-2026-10-10.md) @ `33001f7a` · DESIGN [`DESIGN-ember-short-dungeon-sx06-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-sx06-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D403** · 总控采纳方案 M（P1 短征 sx06 烬环廊）· tip 旁注已关 · backlog → **已批·施工中** · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠放开 gate_daily / EmberDaily*** · **≠开 R / K3**  
**版本：** docs + TrMenu 六本选页 + **本地** map 目录（`map/` gitignore，**未** `git add -f`）+ DP EmberSx06 轻量壳 · jar / 三开关 / bv / ×0.97 / afk **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

批了「烬环廊」第六条短征规格。仓里已有六本选页和 DP 本壳；地图在磁盘上从 **日更断塔白盒**（`ember_daily_spire_v1`）复制了一份（**非** sx01/`ember_daily_v1`、**非** sx02/`ember_daily_rail_v1`、**非** sx03/`ember_daily_frost_v1`、**非** sx04/`ember_daily_ash_v1`、**非** sx05/`ember_daily_crypt_v1` 同观感），外环庭/双环廊/环心还要等 WorldEdit。进本命令、刷怪波次、通关发奖（S45）、日帽键 `%corerpg_p1_sx06_day_line%` 还没接——并行岗接着做。选页第六格日帽行以注释预留，禁假写个人 n/3。

## 拆号（并行）

| 号 | 内容 | 本号 D403 | 阻塞 / 下一步 |
|----|------|-----------|----------------|
| **地图号** | `ember_short_sx06` 外环庭/双环廊/环心烬台 + 出生/Boss 坐标 | **本地骨架已启**：`cp -a ember_daily_spire_v1 → ember_short_sx06`（~1.1M）；说明见 [`docs/maps/ember_short_sx06-README.md`](../maps/ember_short_sx06-README.md) | **阻塞 WE**：仓/play **无** WorldEdit/FAWE jar；**禁** `git add -f` map。烬环主题换皮 → 地图岗进服 WE 另号 |
| **MM 号** | 环卫/环廊弩/环心守吏 + `EmberSx06…Cast` | **已 push** tip `7d251df4`（本号不重交） | 刷点坐标钉死后挂 runs/groups |
| **DP+键号** | `EmberSx06` option/monster + `ember-v1-runs` `sx06`（**须 rooms×3+boss**）+ `p1 enter` + 体力30 + 日帽 | **壳已落**：`dungeon/EmberSx06/option.yml`（`$setmap` + `p1_pass_sx06` 门闩）+ `config.yml` 登记；runs/jar 键插件并行中 | 插件岗：`p1 enter sx06` · pass · `p1_sx06_day` · stamina 30 · rooms/boss 同构（防 D391 FAIL） |
| **插件日帽号** | `%corerpg_p1_sx06_day_line%` + 合计扩第六本 | **未动** | 另号；菜单已注释预留，禁假写 n/3 |
| **菜单号** | `ember_p1_short` 六本选页 + hub/adventure 半指 | **已落**：Title「短征 · 选本」· A/C/E/G/K 保留 · **M=sx06** · adventure `H` + Open · hub Open/冒险格半指含「烬环」 | sx06 点进本=键未就绪时服务端拒（预期）；sx01–sx05 仍可进 |
| **经济号** | S45 发放 + REG/Economy/source-map +（荐）p1sim | **未动** | 另号金样；草案 80币+4碎+3骨尘；首通另加 130/6/1；**禁**抬挂机表 |

## 地图进度（诚实）

| 项 | 状态 |
|----|------|
| 目录 `plugins/DungeonPlus/map/ember_short_sx06/` | **磁盘已有**（gitignore，不入本 commit） |
| 模板来源 | **`ember_daily_spire_v1`**（优先非前五本同观感；对齐 DESIGN E5/E6 · 灰砖断塔气质） |
| 结构 | 继承断塔白盒体量，**非空板**；可步行出生暂写 `0,64,5` |
| 烬环廊主题情绪 / 真环廊绕行 | **未换皮**（待 WE） |
| 三区 R1→R2→R3 叙事钉坐标 | **未重切**；README 写意向表 |
| 宣称 PASS / 可测通 | **否** — 仅骨架启动 |

## 菜单是否落盘

| 文件 | 落盘 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | **是**（六本选页 A/C/E/G/K/M；sx06 day_line **注释预留**） |
| `ember_p1_adventure.yml` Layout+`H`+Open | **是**（半指含烬环） |
| `ember_hub.yml` Open/冒险半指 | **是** |
| trmenu reload | **PASS** · `良好 \| 73 个菜单已加载 (81 ms)`（**06:37:52 CST**）；DP `EmberSx06`+`ember_short_sx06` 导入/初始化 OK（**06:38:02–03 CST**）；RegisterCommands WARN=1.12 已知噪点 |

## 验收对照（骨架窗 ≠ 非整本 PASS）

| ID | 结果 | 备注 |
|----|------|------|
| V1 可达 | **PENDING** | 菜单壳在；`p1 enter sx06` / pass **未** |
| V2 sx01–sx05 仍可进 · 旧日常仍关 | **PASS（纪律）** | 本号未改 gate_daily / EmberDaily*；前五本进本键保留 |
| V3 环廊+Cast+非空板 | **PARTIAL** | 地图非空板本地有；Cast/MM **并行**；主题 WE **未** |
| V4 经济+未抬日表 | **PASS（纪律）** | 零改 afk.tiers/daily_kills；S45 未接线 |
| V5 日帽诚实分开 | **PARTIAL** | 菜单纯文案「最多 3 · 分开计」；`p1_sx06_day` / day_line **未**接线（注释预留） |
| V6 S45 登记 | **PENDING** | 经济另号 |
| V7 rooms×3+boss | **PENDING** | 键号首航硬提醒；本号未写 runs |
| V8 选页挂 day_line | **PENDING** | 键未 live → 注释预留；禁假写个人 n/3 |

## 改动清单（本号 · 入仓）

| 文件 | 改动 |
|------|------|
| DESIGN / tip / backlog | 勾批 A·M · tip 关 · **已批·施工中**（旁注已齐） |
| 本 STATUS + `docs/maps/ember_short_sx06-README.md` | 拆号 · 地图进度 · WE 阻塞 |
| `plugins/DungeonPlus/dungeon/EmberSx06/{option,monster,obstacle}.yml` | DP 轻量壳 |
| `plugins/DungeonPlus/config.yml` | 登记 `ember_short_sx06`→`EmberSx06` |
| `plugins/TrMenu/menus/ember_p1_short.yml` | 六本选页 + sx06 day_line 注释预留 |
| `ember_p1_adventure.yml` / `ember_hub.yml` | 入口半指含烬环 |
| `plugins/DungeonPlus/map/ember_short_sx06/**` | **仅本地** · **未 stage**（gitignore） |
| jar / ember-v1.yml / afk / MM | **未 stage / 未改 jar/afk/Stage2**（MM 怪物岗另交） |

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

*D403 批 A·M · tip `33001f7a` · 短征 sx06 骨架启动 · 地图本地 spire 壳 · WE/进本键/S45/日帽键/MM 并行 · ≠关观察≠抬日表≠开旧日常闸。*

**本号 tip：** `41ac7417`（地图 README + 六本菜单 + STATUS）· 批 A 旁注 `dd00a1f7` · MM/DP 壳 `7d251df4` · DESIGN tip `33001f7a`
