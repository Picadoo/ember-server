# 状态 · D391：短征 sx01 批 A·M 与骨架启动（烬门哨岗 · ≠关观察 ≠抬日表 ≠开 gate_daily）

**日期：** 2026-10-10（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-short-dungeon-p1-reachable-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-short-dungeon-p1-reachable-need-design-2026-10-10.md) @ `234dea63` · DESIGN [`DESIGN-ember-short-dungeon-p1-reachable-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-p1-reachable-2026-10-10.md)  
**裁决：** **已批 A · 批 M · D391** · 总控采纳方案 M（P1 原生短征 sx01）· tip 旁注已关 · backlog → **已批·施工中** · **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠放开 gate_daily / EmberDaily*** · **≠开 R / K3**  
**版本：** docs + DP 壳 + TrMenu 骨架 + **本地** map 目录（`map/` gitignore，**未** `git add -f`）· jar / 三开关 / bv / ×0.97 / afk **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

批了「烬门哨岗」短征规格。仓里已有短征菜单壳和 DP 本壳；地图在磁盘上从 Q01 白盒复制了一份（有墙有门有垫，不是空板），主题换皮还要等 WorldEdit。进本命令、刷怪波次、通关发奖还没接——并行岗接着做。

## 拆号（并行）

| 号 | 内容 | 本号 D391 | 阻塞 / 下一步 |
|----|------|-----------|----------------|
| **地图号** | `ember_short_sx01` 三区墙门垫 + 出生/Boss 垫坐标 | **本地骨架已启**：`cp -a ember_daily_v1 → ember_short_sx01`（2.1M · region×4）；说明见 [`docs/maps/ember_short_sx01-README.md`](../maps/ember_short_sx01-README.md) | **阻塞 WE**：仓/play **无** WorldEdit/FAWE jar；**禁** `git add -f` map（`.gitignore`：Release assets only）。主题换皮（烬门哨墙+内院+抬高垫）→ 地图岗进服 WE 另号 |
| **MM 号** | 小怪/精英/Boss + `EmberSx01SentryCast` | **未本号 stage**（工作区已有 `EmberSx01.yml` / Skills 草稿，交怪物岗） | 刷点坐标钉死后挂 `monster.yml` groups |
| **DP+键号** | `EmberSx01` option/monster + `ember-v1-runs` `sx01` + `p1 enter` + 体力30 + 日帽 | **壳已落**：`dungeon/EmberSx01/option.yml`（`$setmap` + `p1_pass_sx01` 门闩）+ `config.yml` 登记；**无** runs/jar 键 | 插件岗：`p1 enter sx01` · pass · 日帽计数 · stamina 30 |
| **菜单号** | `ember_p1_short` + hub/adventure 入口 | **已落**：`ember_p1_short.yml`（灰态 Q01/体力 + 日帽 lore + `corerpg p1 enter sx01`）· adventure `H` + Open 半行 · hub Open/冒险格半指 | 键齐后热更抽测；禁假可达旧日常 |
| **经济号** | S40 发放 + REG/Economy/source-map +（荐）p1sim | **未动** | 另号金样；草案 80币+4碎+3骨尘；**禁**抬挂机表 |

## 地图进度（诚实）

| 项 | 状态 |
|----|------|
| 目录 `plugins/DungeonPlus/map/ember_short_sx01/` | **磁盘已有**（gitignore，不入本 commit） |
| 结构 | 继承 Q01/`ember_daily_v1` 白盒：石砖/石墙体量，**非空板**；可步行出生约 `0,64,5` |
| 烬门哨岗主题情绪 | **未换皮**（待 WE） |
| 三区 R1→R2→R3 叙事钉坐标 | **未重切**；README 写意向表 |
| 宣称 PASS / 可测通 | **否** — 仅骨架启动 |

## 菜单是否落盘

| 文件 | 落盘 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | **是** |
| `ember_p1_adventure.yml` Layout+`H`+Open | **是** |
| `ember_hub.yml` Open/冒险半指 | **是** |
| trmenu reload | 本号 commit 后可热更（进本键未就绪时点进本=服务端拒，预期） |

## 验收对照（骨架窗 · 非整本 PASS）

| ID | 结果 | 备注 |
|----|------|------|
| V1 可达 | **PENDING** | 菜单壳在；`p1 enter sx01` / pass **未** |
| V2 旧日常仍关 | **PASS（纪律）** | 本号未改 gate_daily / EmberDaily* 玩家入口 |
| V3 三房+Cast+非空板 | **PARTIAL** | 地图非空板本地有；Cast/刷点/主题 WE **未** |
| V4 经济+未抬日表 | **PASS（纪律）** | 零改 afk.tiers/daily_kills；S40 未接线 |
| V5 日帽诚实 | **PARTIAL** | 菜单纯文案；计数器未接线 |
| V6 S40 登记 | **PENDING** | 经济另号 |

## 改动清单（本号 · 入仓）

| 文件 | 改动 |
|------|------|
| DESIGN / tip / backlog | 勾批 A·M · tip 关 · **已批·施工中** |
| 本 STATUS + `docs/maps/ember_short_sx01-README.md` | 拆号 · 地图进度 · WE 阻塞 |
| `plugins/DungeonPlus/dungeon/EmberSx01/{option,monster,obstacle}.yml` | DP 壳 |
| `plugins/DungeonPlus/config.yml` | 登记 `ember_short_sx01`→`EmberSx01` |
| `plugins/TrMenu/menus/ember_p1_short.yml` | 新菜单 |
| `ember_p1_adventure.yml` / `ember_hub.yml` | 入口半指 |
| `plugins/DungeonPlus/map/ember_short_sx01/**` | **仅本地** · **未 stage**（gitignore） |
| jar / ember-v1.yml / afk / MM 工作区草稿 | **未 stage / 未改 live 开关** |

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity/p1-six/MM 脏迹（常态） |
| C2 | **PASS** | cached 仅授权 docs/DP壳/TrMenu/config |
| C3 | **PASS** | 显式路径 add；未 `add -A`；**未** `add -f` map |
| C4 | **PASS** | 未碰 live ember-v1 Stage2 开关；未切分支；toplevel main |
| C5 | **PASS** | 脏 runtime / MM 草稿未入暂存 |

## 不动

关观察 · 勾 §2.4 · 抬日表 / afk.tiers · 放开 gate_daily · 开 R · 开 K3 · 改 ×0.97 · 假宣传旧日常开放 · 空板宣称 PASS · `git add -f` map · stage 脏 runtime

---

*D391 批 A·M · tip `234dea63` · 短征 sx01 骨架启动 · 地图本地有墙门垫 · WE/进本键/MM/S40 并行 · ≠关观察≠抬日表≠开旧日常闸。*
