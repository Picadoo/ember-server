# 余烬 · D408：挂机满额追短征 · W1c 复测

- **性质：观察期复测 · 对 FAIL `f45cc48f` · 修 tip `7e06b745` · live W1c 体力&lt;30 诚实 · 未改配置 / 未动开关 / 未关观察**
- 上游 FAIL：spot tip **`f45cc48f`** · STATUS [`STATUS-ember-afk-full-short-chase-spot-d408-2026-10-10.md`](STATUS-ember-afk-full-short-chase-spot-d408-2026-10-10.md)
- 上游修：fix tip **`7e06b745`** · STATUS [`STATUS-ember-afk-full-short-chase-fix-d408-2026-10-10.md`](STATUS-ember-afk-full-short-chase-fix-d408-2026-10-10.md) · 菜单 `ember_p1_afk.yml` md5 `6497cd78…`
- 上游施工：菜单 tip **`36f38d1b`** · DESIGN tip **`191b845f`**
- 现态（复测窗）：bv**72** · `enabled/migrate/set_bonus=true` · jar **`1.65.118-d410.local`**（并行 D410 已装；本号只验菜单）· 菜单 **73** · `daily_kills=2400` · `ember_p1_afk.yml` md5 `6497cd78992a7ce983b37e08ca770fd0`（= tip `7e06b745`）
- 测号：`D408Afk`（未满）· `D405AfkEta`（满额 `p1_afk_kill@2026-10-10` REPLACE 2400）；经代理 joinPlay；**未动真人档**
- 证据：`/workspace/tmp/d408-afk-full-short-retest/`（`10-menu-static` · `20/40/51-afk-*` · `42/44/52/53-click-*` · `30/50-papi-*` · `90-endstate` · `99-summary` · `run.log`）
- 执行：2026-10-10 **07:30–07:31 CST**（`trmenu reload` 热更；**未**停服；等 D409 复测退出后再占 play）
- **结论：PASS** — 旧 FAIL M5/M6 已消；满额双路径 / Open·I 半行 / D285 左键 / 未满 F / 零改表 **全绿**

## 人话

修号把「满额+体力不足」从复合 `and` 图标改成：满额只留一档，右键嵌套单条件判体力。复测里体力=12 时 lore 常驻「短征需 30 · 当前 12」，右键只 tell、**不开**短征选页；体力够仍开选页；左键仍进冒险。

## 验收表

| ID | 项 | 结果 | 证据 |
|----|----|------|------|
| **V0** | tip `7e06b745` ancestor · md5 live=tip | **PASS** | `00-tip.txt` · `10-menu-static.json` |
| **S1–S5** | 无 and 复合 · 嵌套 stamina+deny · 左冒险 · Open/I 半行静态 | **PASS** | `10-menu-static.json` |
| **R6** | trmenu reload 73 菜单 | **PASS** | `11-trmenu-reload.json`（**07:30:07 CST** · 98 ms） |
| **L0** | 未满 `afk_full=0` | **PASS** | unfull 段 |
| **L2** | 满额 `afk_full=1` · stamina≥30 | **PASS** | `30-papi-full.json`（满额 2400 · 体 90） |
| **M0** | Open「也可追短征」· I「挂满可追短征」 | **PASS** | `41-open-chat-full` · `40-afk-full-hi` |
| **M1** | 未满 F=自动战斗 | **PASS** | `20-afk-unfull.json` |
| **M2** | 满额 F lore 含短征可追 + 短征需 + 剩余有奖 | **PASS** | `40-afk-full-hi.json`（剩余有奖 **24**） |
| **M3** | 满额左键 → `余烬 · 冒险` | **PASS** | `42-left-adventure.json` |
| **M4** | 满额右键（体≥30）→ `短征 · 选本` | **PASS** | `44-right-short.json`（八本可见） |
| **M5** | 满额+体力&lt;30 → F lore「短征需…当前」 | **PASS**（旧 FAIL 消） | `51-afk-full-low.json`（当前 **12**） |
| **M6** | 满额+体力&lt;30 右键 tell·**不开**选页 | **PASS**（旧 FAIL 消） | `52-right-low-stam.json` · tell「短征需 30 体力 · 当前 12」· `openedShort=false` · 仍挂机庭 |
| **M7** | 低体力左键仍进冒险（D285） | **PASS** | `53-left-adventure-low.json` |
| **V5** | gate_daily=no · daily_kills/tiers/Stage2/jar·菜单 md5 抽窗零改 | **PASS** | `60-gate-daily` · `90-endstate` |

## 对照证据摘要

| 项 | 值 |
|----|-----|
| 满额 F 名 | `体力还在 · 去冒险/短征` |
| 满额 lore（体 90） | `短征还可追 · 今日剩余有奖 24` · `短征需 30 体力 · 当前 90` · 左冒险 / 右短征（体力够才开） |
| 左键 title | `余烬 · 冒险` |
| 右键 title（体≥30） | `短征 · 选本`（sx01–sx08） |
| 体力=12 时 F | 同满额档 + **短征需 30 · 当前 12** |
| 体力=12 右键 | tell「短征需 30 体力 · 当前 12」· **未开**选页 · 仍 `挂机庭 · 自动战斗` |
| Open | `满后体力还能去冒险 · 也可追短征（选本页）` |
| I | `挂满可追短征 · 今日剩余有奖 24` |
| gate_daily | `no` |
| trmenu reload | `良好 \| 73 个菜单已加载 (98 ms)`（**07:30:07 CST**） |

## 旁注

1. 旧 FAIL 根因已由 fix `7e06b745` 落地：删 `afk_full … and … stamina` 独立档；右键嵌套单条件 `stamina < 30` + deny 开选页；lore 满额常驻短征需30。
2. 测号 `D405AfkEta` 离线 REPLACE `p1_afk_kill@2026-10-10=2400` 后 join 读满额；**未**改 `daily_kills` / tiers。
3. 复测窗 jar 为并行 D410 `1.65.118-d410.local` / bv72；`ember_p1_afk.yml` md5 与修 tip 一致；Stage2 三开 / daily_kills=2400 未变。
4. 等 D409 retest2 退出（0 在线）后再占 play；**未**反复停服。
5. mineflayer `clickWindow` 常报 transaction 超时，但窗口仍切换——以 title/dump/tell 为准。

## tip / 产物

| 项 | 值 |
|----|-----|
| 修 tip | **`7e06b745`** |
| FAIL 上游 | **`f45cc48f`** |
| DESIGN tip | **`191b845f`** |
| `ember_p1_afk.yml` md5 | `6497cd78992a7ce983b37e08ca770fd0` |
| jar（复测窗） | `1.65.118-d410.local` |
| 分支 | main |
| 结论 | **PASS** |

## 手法与注记

- mineflayer `D408Afk` / `D405AfkEta` + `lib/proxy-login`；脚本 `d408-retest.js`
- 开菜单：console `trmenu open ember_p1_afk <名>`；造满额：既有账号 `p1_afk_kill@2026-10-10` REPLACE 2400；体力：`corerpg stamina set`
- **未**改 feed / afk 产量 / Stage2 / gate_daily / 菜单内容（只 `trmenu reload` 确认修 tip 已挂）
- **未**切分支 / stash / reset / `checkout -- .`；login/proxy/MariaDB 仅测号计数器 REPLACE；**未**反复停服
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）
- 工作区另有并行施工脏文件：**未 stage / 未 add -A**

## 结束态（复测窗）

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 72 |
| daily_kills | 2400 |
| jar | 1.65.118-d410.local |
| tips | fix `7e06b745` · FAIL `f45cc48f` · DESIGN `191b845f` |
| 结论 | **PASS**（W1c live 旧红消） |
