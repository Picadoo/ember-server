# 余烬 · D406：短征生涯首通同屏 · 线上薄抽

- **性质：观察期薄抽 · `ember_p1_short` 六本格+I 挂 `sx0N_fc` · I/Open 合计 `sx_fc_left`/`pending_line` · day_line 仍在 · 未改配置 / 未动开关 / 未关观察**
- 上游施工：菜单 fc tip **`3e8cff26`** · 合计键 tip **`af408d9e`** · 合计挂菜单 tip **`5a2ea665`** · jar **`1.65.115-d406.local`** · DESIGN [`DESIGN-ember-short-firstclear-visible-2026-10-10.md`](../design/DESIGN-ember-short-firstclear-visible-2026-10-10.md) · 菜单 STATUS [`STATUS-ember-short-firstclear-visible-menu-d406-2026-10-10.md`](STATUS-ember-short-firstclear-visible-menu-d406-2026-10-10.md) · 合计 STATUS [`STATUS-ember-short-firstclear-fc-aggregate-d406-2026-10-10.md`](STATUS-ember-short-firstclear-fc-aggregate-d406-2026-10-10.md)
- 现态（抽测窗）：bv**70** · `enabled/migrate/set_bonus=true` · jar **`1.65.115-d406.local`** sha256 `d9a16337…` · 菜单 **73** · `daily_kills=2400` · `ember_p1_short.yml` md5 `d6386674…`（= tip `5a2ea665`）
- 测号：`D406Fc`；经代理 joinPlay；**未动真人档**
- 证据：`/workspace/tmp/d406-short-fc/`（`10-static-checks` · `20-papi-bot` · `22-papi-claim-flip-bot` · `40/41/42/43-menu-*` · `30-open-chat` · `90-endstate` · `99-results-final` · `run.log` / `run-papi-retest.log`）
- 执行：2026-10-10 **07:01–07:07 CST**（施工合计 jar/菜单挂上后复测；`trmenu reload` 热更；**未**停服）
- **结论：PASS** — 六本格+I 未领=包摘要 / 已领=已领取；I/Open 合计行真数（6→5）；day_line 仍在；无静态「另加：币」lore；日帽/S40–S45/首通包/afk/Stage2 零改；gate_daily=no

## 人话

打开短征选本，六本悬停「生涯首通」跟真键走：未领看「碎片/胚料/币」摘要，领过变「已领取」。说明格六行可扫，并见「生涯首通未领：N 本」+「还有 N 本生涯首通未领」；Open 旁注同步。日帽行照旧。admin 标 sx01 首通后同屏即时变 5。afk 日表 / Stage2 / gate_daily 一行没拧。

## 验收表

| ID | 项 | 结果 | 证据 |
|----|----|------|------|
| **V0** | tip `3e8cff26`（fc）+ `5a2ea665`（合计挂）+ live=agg tip | **PASS** | `00-md5.txt` · `10-static-checks.json` |
| **V1** | 六本格 A/C/E/G/K/M 挂 `sx0N_fc`（槽+I 各 ≥1） | **PASS** | `10-static` · live `M1` |
| **V2** | 无 lore 静态谎称「另加：币 / 另加薄包」（注释提及不计） | **PASS** | `10-static` V2 |
| **V3** | `day_line` 六本仍在 + `sx_day_left_sum` 保留 | **PASS** | `10-static` · live day 行 |
| **V4** | I/Open 挂合计：`sx_fc_left` + `pending_line`（非假写字面） | **PASS** | `10-static` V4 · `M0`/`M4` |
| **L1** | PAPI 六本 fc：未领=包摘要 | **PASS** | `20-papi-bot.json`（bot `/papi parse me`） |
| **L2** | PAPI 六本 `day_line` | **PASS** | `20-papi-bot.json` |
| **L3** | PAPI 合计 left=6 / pending「还有 6 本…」与未领数一致 | **PASS** | `20-papi-bot.json` |
| **L4** | `%corerpg_gate_daily%` = `no` | **PASS** | `20-papi-bot.json` |
| **L5/L6** | 标 sx01 首通 → fc「已领取」· left 6→5 · pending「还有 5 本…」 | **PASS** | `22-papi-claim-flip-bot.json` |
| **M1** | 六本同屏：fc 摘要 + day_line · 无假另加 | **PASS** | `41-menu-live.json` |
| **M2** | I 六行首通摘要 + day | **PASS** | `41-menu-live.json` |
| **M0/M4** | Open tell + I 合计同屏（未领 6） | **PASS** | `30-open-chat.json` · `41-menu-live.json` |
| **M3/M5** | 领后同屏：烬门「已领取」· I 未领 5 / 还有 5 本 | **PASS** | `42/43-menu-after-claim*.json` |
| **S9** | 零改 afk/Stage2/runs/yml（抽窗 md5 不变）· daily_kills=2400 | **PASS** | `00-switches-pre` · `90-endstate` |

## 关键证据摘要

| 项 | 值 |
|----|-----|
| 未领六本 fc（例） | sx01 `碎片 8 胚料 1 币 200` … sx06 `碎片 6 胚料 1 币 130` |
| 合计未领 | `6` · `还有 6 本生涯首通未领` |
| 标 sx01 后 | fc `已领取` · left `5` · `还有 5 本生涯首通未领` |
| Open tell | `生涯首通：各本悬停可见未领/已领取 · 还有 N 本生涯首通未领` |
| gate_daily | `no` |
| trmenu reload | `良好 \| 73 个菜单已加载 (121 ms)`（**07:05:08 CST**） |

## 旁注

1. 控制台 `papi parse <名>` 在本窗多次报 `Failed to find player`（bot 已在 play、且 `trmenu open`/`corerpg p1 runs firstclear` 可命中）；**改用 bot `/papi parse me`（临时 op）+ 菜单 dump 作权威证据**。非键坏。
2. 施工并行装 jar `1.65.115-d406.local` 时曾短暂 economy S44/S45 drift 启服失败，施工自愈后本抽开跑；本号**未**改 economy/afk/开关。
3. 合计 SKIP 已按总控补取消——键 tip `af408d9e` + 菜单 tip `5a2ea665` 均核。

## tip / 产物

| 项 | 值 |
|----|-----|
| 菜单 fc tip | **`3e8cff26`** |
| 合计键 tip | **`af408d9e`** |
| 合计挂菜单 tip | **`5a2ea665`** |
| `ember_p1_short.yml` md5 | `d63866744549302b9713300364925353` |
| jar | `1.65.115-d406.local` sha256 `d9a16337d24144406f70586119f7a3762a8c7614bf8ad0276cf7d16119f69293` |
| 分支 | main |

## 手法与注记

- mineflayer `D406Fc` + `lib/proxy-login`；脚本 `d406-spot.js` + `d406-papi-retest.js`
- 开菜单：console `trmenu open ember_p1_short <名>`；造条件：`corerpg p1 runs firstclear`（q01 / sx0N / sx0N clear）
- **未**改 feed / afk / Stage2 / gate_daily / 日帽 / S40–S45 / 首通包数额 / 菜单内容（只 `trmenu reload`）
- **未**切分支 / stash / reset / `checkout -- .`；login/proxy/MariaDB **未碰**；**未**反复停服
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）
- 工作区另有 D407 施工脏文件：**未 stage / 未 add -A**

## 结束态（抽测窗）

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 70 |
| daily_kills | 2400 |
| jar | 1.65.115-d406.local |
| tips | `3e8cff26` + `af408d9e` + `5a2ea665` |

## 总控签字栏（待签）

| 项 | 签认 |
|----|------|
| **D406 薄抽 PASS** | [ ] |
| tip `3e8cff26` + `af408d9e` + `5a2ea665` | [ ] |
| 六本 fc 未领/已领 · I/Open 合计真数 · day_line 仍在 | [ ] |
| 零改日帽·S40–S45·首通包·afk·Stage2·gate_daily | [ ] |
| ≠关观察（满窗仍须 ≥2026-10-10 17:40 CST） | [ ] |
| 签字 / 日期 | |

---

*D406 薄抽 · PASS · tip `3e8cff26`+`af408d9e`+`5a2ea665` · jar `1.65.115-d406.local` · 证据 `/workspace/tmp/d406-short-fc/` · ≠关观察。*
