# 余烬 · D408：挂机满额追短征 · 线上薄抽

- **性质：观察期薄抽 · `ember_p1_afk` 满额 F 双路径 / Open·I 半行 / 体力&lt;30 诚实 · 未改配置 / 未动开关 / 未关观察**
- 上游施工：菜单 tip **`36f38d1b`** · DESIGN tip **`191b845f`** · STATUS 施工 [`STATUS-ember-afk-full-short-chase-d408-2026-10-10.md`](STATUS-ember-afk-full-short-chase-d408-2026-10-10.md) · DESIGN [`DESIGN-ember-afk-full-short-chase-2026-10-10.md`](../design/DESIGN-ember-afk-full-short-chase-2026-10-10.md)
- 现态（抽测窗）：bv**71** · `enabled/migrate/set_bonus=true` · jar 抽测中并行换至 **`1.65.117-d409.local`**（菜单 md5 仍 = tip）· 菜单 **73** · `daily_kills=2400` · `ember_p1_afk.yml` md5 `b577523c…`（= tip `36f38d1b`）
- 测号：`D408Afk`（未满）· `D405AfkEta`（满额 2400，既有 `p1_afk_kill` 键 REPLACE）；经代理 joinPlay；**未动真人档**
- 证据：`/workspace/tmp/d408-afk-full-short/`（`10-menu-static` · `20/40/51-afk-*` · `42/44/52/53-click-*` · `30/50-papi-*` · `90-endstate` · `99-summary` · `run.log` / `run-v2.log`）
- 执行：2026-10-10 **07:14–07:24 CST**（`trmenu reload` 热更；**未**停服）
- **结论：FAIL** — 满额双路径 / Open·I 半行 / D285 左键 / 未满 F / 零改表 **绿**；**W1c 体力&lt;30 诚实 live 红**（复合 `and` 条件未命中，右键仍开短征选页）

## 人话

挂机打满后，F 格变成「去冒险/短征」：左键进冒险（D285 还在），右键进短征选本，lore 挂今日剩余有奖真数。Open / 战况 I 都能看见「也可追短征 / 挂满可追短征」。未满时 F 仍是自动战斗开关。

**红点：** 体力故意压到 12 后，F 仍显示「右键 ➥ 短征选本」（满额≥30 档），**没有**「短征需 30·当前 N」档；右键照样打开短征选页，没有诚实 tell。同号同体力下 `ember_p1_short` 单条件 `stamina < 30` 灰锁正常——问题收在本菜单 **`afk_full>=1 and stamina<30` 复合条件未生效**。

## 验收表

| ID | 项 | 结果 | 证据 |
|----|----|------|------|
| **V0** | tip `36f38d1b` live=md5 · tip ancestor | **PASS** | `00-tip.txt` · `10-menu-static.json` |
| **S1** | 未满 F 仍自动战斗 · 无强制短征主钮 | **PASS** | `10-menu-static` · `20-afk-unfull.json` |
| **S2** | 满额 F 静态：左冒险 / 右短征 / lore `sx_day_left_sum` | **PASS** | `10-menu-static.json` |
| **S3** | 菜单面 W1c 低体力 tell·无 short（YAML） | **PASS（静态）** | `10-menu-static` S3 |
| **S4** | Open/I 满额半行 + remain_line 保留 | **PASS** | `10-menu-static` · live M0/M2 |
| **L0** | 未满 `afk_full=0` | **PASS** | `22-papi-unfull` / v2 L0 |
| **L2** | 满额 `afk_full=1` · stamina≥30 | **PASS** | `30-papi-full.json` |
| **M0** | Open tell「也可追短征」· I「挂满可追短征」+ remain | **PASS** | `21-open-chat-*` · `40-afk-full-hi` |
| **M1** | 未满 F=自动战斗 | **PASS** | `20-afk-unfull.json` |
| **M2** | 满额 F lore 含短征可追 + `剩余有奖` 真数 | **PASS** | `40-afk-full-hi.json`（例：剩余有奖 **21**） |
| **M3** | 满额左键 → `余烬 · 冒险` | **PASS** | `42-left-adventure.json` |
| **M4** | 满额右键 → `短征 · 选本` | **PASS** | `44-right-short.json` |
| **M5** | 满额+体力&lt;30 → F 低体力档 lore | **FAIL** | `51-afk-full-low.json` · 仍为「右键 ➥ 短征选本」无「短征需」 |
| **M6** | 满额+体力&lt;30 右键 tell·**不开**选页 | **FAIL** | `52-right-low-stam.json` · 打开了短征选页 · tell 空 |
| **M7** | 低体力左键仍进冒险（D285） | **PASS** | `53-left-adventure-low.json` |
| **V5** | gate_daily=no · daily_kills/tiers/Stage2/jar 菜单 md5 抽窗零改 | **PASS** | `60-gate-daily` · `90-endstate` |

## 对照证据摘要

| 项 | 值 |
|----|-----|
| 满额 F 名 | `体力还在 · 去冒险/短征` |
| 满额 lore | `短征还可追 · 今日剩余有奖 21` · 左冒险 / 右短征选本 |
| 左键 title | `余烬 · 冒险` |
| 右键 title | `短征 · 选本`（七本可见） |
| Open | `满后体力还能去冒险 · 也可追短征（选本页）` |
| I | `挂满可追短征 · 今日剩余有奖 21` + remain_line |
| 体力=12 时 F | **仍**满额≥30 档（无「短征需」） |
| 体力=12 右键 | **仍开** `短征 · 选本`（对照：同体力 short 本页灰锁「体力不足」正常） |
| gate_daily | `no` |
| trmenu reload | `良好 \| 73 个菜单已加载 (110 ms)`（**07:14:48 CST**） |

## 旁注

1. **W1c live FAIL 根因（观察）：** `ember_p1_afk` F 低体力档条件为复合句 `afk_full>=1 and stamina<30`。同服同号体力=12 时，`ember_p1_short` / `ember_hub` 的**单条件** `stamina < 30` 均命中；本复合条件未切档。枢纽 D399 低体力用**独立 priority 图标**而非 `and` 复合。施工 STATUS 菜单面自检未覆盖 live 低体力右键。
2. 新号 `D408Afk` 离线 INSERT `p1_afk_kill@today` 后 join 读档为 0（键被 stamina onJoin flush 冲掉）；改用既有键账号 `D405AfkEta` REPLACE 至 2400 造满额。**未**改 `daily_kills` / tiers。
3. 抽测窗并行施工将 jar 换为 `1.65.117-d409.local`；`ember_p1_afk.yml` md5 与 tip 一致；Stage2 三开 / daily_kills=2400 未变。
4. mineflayer `clickWindow` 常报 transaction 超时，但窗口仍切换——以 title/dump 为准。

## tip / 产物

| 项 | 值 |
|----|-----|
| 菜单 tip | **`36f38d1b`** |
| DESIGN tip | **`191b845f`** |
| `ember_p1_afk.yml` md5 | `b577523c4adc0d7c07df42117a460349` |
| jar（抽测结束态） | `1.65.117-d409.local` |
| 分支 | main |

## 手法与注记

- mineflayer `D408Afk` / `D405AfkEta` + `lib/proxy-login`；脚本 `d408-spot.js` + `d408-spot-v2.js`
- 开菜单：console `trmenu open ember_p1_afk <名>`；造满额：既有账号 `p1_afk_kill@2026-10-10` REPLACE 2400；体力：`corerpg stamina set`
- **未**改 feed / afk 产量 / Stage2 / gate_daily / 菜单内容（只 `trmenu reload`）
- **未**切分支 / stash / reset / `checkout -- .`；login/proxy/MariaDB 仅测号计数器 REPLACE；**未**反复停服
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）
- 工作区另有并行施工脏文件：**未 stage / 未 add -A**

## 结束态（抽测窗）

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 71 |
| daily_kills | 2400 |
| jar | 1.65.117-d409.local |
| tips | `36f38d1b` + DESIGN `191b845f` |
| 结论 | **FAIL**（W1c live） |
