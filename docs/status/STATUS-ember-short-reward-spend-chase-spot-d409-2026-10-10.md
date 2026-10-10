# 余烬 · D409：短征有奖→工坊可追 · 线上薄抽

- **性质：观察期薄抽 · `ember_p1_short` 满态追工坊（W1b/c）+ W1d 右挂机 + W1a 结算半句 · 未改配置 / 未动开关 / 未关观察**
- 上游施工：jar tip **`20cd1318`**（W1a）· 菜单 tip **`1c782cbc`**（W1b/c+W1d）· DESIGN **`9a22f1a0`** · 勾毕 **`feb70949`** · 施工 STATUS [`STATUS-ember-short-reward-spend-chase-w1a-d409-2026-10-10.md`](STATUS-ember-short-reward-spend-chase-w1a-d409-2026-10-10.md) / [`STATUS-ember-short-reward-spend-chase-d409-menu-2026-10-10.md`](STATUS-ember-short-reward-spend-chase-d409-menu-2026-10-10.md)
- 现态（抽测窗）：抽测起 jar **`1.65.117-d409.local`** → 并行叠档至 **`1.65.118-d410` / `1.65.119-d411` / `1.65.119-d412.local`**（W1a `spendChase*` 字符串仍在 live jar）· 菜单 D409 满态块仍在（后挂 sx08/sx09 day_line，md5≠tip 原文）· Stage2 三开 · `daily_kills=2400`
- 测号：`D409Spend`；经代理 joinPlay；**未动真人档**
- 证据：`/workspace/tmp/d409-reward-spend/`（`10-menu-static` · `12-unit-test` · `00-switches-pre` · `20-short-unfull` · `live-menu-unfull` / `live-open-unfull` · `56d-settle` · `60-w1a-ABORT-still-full` · `99-final-summary` · `cold-papi` · logs）
- 执行：2026-10-10 **07:14–08:04 CST**（`trmenu reload`；并行 D410/D411/D412 换 jar；**未**为 D409 故意反复停服）
- **结论：PARTIAL** — 静态菜单满态 Open/I/B+W1d、未满勿误亮、W1a 单测/源路径、无奖不假喊 live、纪律项 **绿**；**`sx_day_left_sum==0` 满态 live CTA** 与 **有奖 settle 追工坊 tell live** 因日帽计数器离线注入不可靠（新键/改值 join 后仍读旧内存态；叠本后 sum 被 sx08+ 未帽底托住）**未绿证** → 记 PARTIAL，**停止** DB inject / 清本循环

## 人话

短征有奖打满后，选本页应直接说「去工坊花」；「去哪花」满态左键工坊、右键挂机。有奖通关结算应多一句「材料已进仓 · 工坊可花」。本抽：菜单面与单测对齐 tip；未满时 live 不开满态口号；无奖通关不假喊。满态同屏与有奖追句因日帽注入不稳未能在测号上绿证。

## 验收表

| ID | 项 | 结果 | 证据 |
|----|----|------|------|
| **V0** | 菜单 tip `1c782cbc` 祖先 · 满态 Open/I/B/W1d 块仍在 live YAML | **PASS** | `10-menu-static.json` · tip `1c782cbc` · live `ember_p1_short.yml`（Open tell / I 满态+挂机半行 / B→forge / 右→afk） |
| **V0b** | jar tip `20cd1318` W1a · `spendChaseTell` / 文案仍在叠档 jar | **PASS** | `10-menu-static.jarHas` · `javap` EmberShortRules · live `1.65.119-d412.local` 仍含「材料已进仓」「本本今日有奖已满」 |
| **U1** | `EmberShortRulesTest` 48/48（含 spendChase / 无奖分支不调 chase） | **PASS** | `12-unit-test.txt` |
| **S1** | 静态：Open `sx_day_left_sum<=0` →「今日短征有奖已满 · 材料在仓 · 去工坊花」；未满 deny 现网句 | **PASS** | `10-menu-static` openFull* / openDenyUnfull |
| **S2** | 静态：I 满态名「今日有奖已满 · 去工坊花」+ 体力>0 挂机半行 | **PASS** | `10-menu-static` iFull* / iAfkHalf |
| **S3** | 静态：B 满态「去工坊花材料」左 `ember_p1_forge` · 右 `ember_p1_afk`（W1d） | **PASS** | `10-menu-static` bFull* / bLeftForge / bRightAfk |
| **S4** | 静态：未满 B 仍「去哪花」· 默认无满态口号 | **PASS** | `10-menu-static` bUnfullName / bDefaultNotFullSlogan |
| **L1** | live 未满（sum>0）：Open **无**「今日短征有奖已满」· I≠满态名 · B=「去哪花」· 左仍工坊 | **PASS** | `live-final.log` L_unfull_* · `live-open-unfull.json` · `live-unfull-left-forge.json` |
| **L2** | live 无奖通关 **不**假喊「材料已进仓」 | **PASS** | `56d-settle.json`（no-reward · chase=false · day=3/3） |
| **L3** | live 有奖通关 tell「材料已进仓 · 工坊可花」(+ 可选本本满) | **PARTIAL** | 单测/源路径绿（U1/V0b）；live 造「未满→有奖通关」依赖日帽注入 → `60-w1a-ABORT-still-full.json` / `cold-papi.json`（DB 写 1/3 后 join 仍读 3；sx08 新键写后 join 仍 0）**未绿证** |
| **L4** | live `sx_day_left_sum==0` 满态 Open/I/B + 左工坊 + 右挂机 | **PARTIAL** | 静态 S1–S3 绿；live sum 抽窗最低见 **3**（sx01–sx07 满、sx08 日帽 0→left 托住合计；离线灌 sx08=3 不进内存）· `99-final-summary` F_sum0/F_open/F_I/F_B/F_right_afk 红 · **未**再开清本循环 |
| **V5** | 零改：`daily_kills=2400` · Stage2 `enabled/migrate/set_bonus=true` · 短征有奖量级 80/4/3 未拧 · ≠关观察 | **PASS** | `00-switches-pre.json` · 抽测末 `ember-v1.yml` 扫描 · economy S40/S45–S47 clear.coin=80 |

## 对照证据摘要

| 项 | 值 |
|----|-----|
| Open 满态 tell（YAML） | `今日短征有奖已满 · 材料在仓 · 去工坊花` |
| I 满态 + W1d | `今日有奖已满 · 去工坊花` · lore「体力还在可挂机 · …右键进挂机庭」 |
| B 满态 | 左 → `ember_p1_forge` · 右 → `ember_p1_afk` |
| 未满 live Open | 选本句 / 「材料进仓去工坊」旁注 · **无**满态口号 |
| 无奖 settle live | `短征通关（无奖）· 今日有奖已满 3/3` · **无**「材料已进仓」 |
| 有奖 chase live | **未证**（注入失败） |
| sum==0 满态 live | **未证**（注入/叠本日帽） |
| gate / afk / Stage2 | 未抬日杀 · 三开关仍 true |

## 旁注

1. **日帽离线注入不可靠（观察）：** `UPDATE cr_players.data` 改 `p1_sx0N_day@today` 后，即便冷启动后首 join，**既有键改小值**与**新键 sx08=3** 均未能让 PAPI 读到写入值（例：DB sx01=1 → PAPI day=3；DB 灌齐八本=3 → `sx08_day=0` · `sum=3`）。quit 后 save 写回内存态，冲掉注入。BukkitYamlTest（同 `paper-custom.jar`）能解析注入 YAML——问题在 live 读档/缓存路径，**非**本抽修复范围。故满态 CTA / 有奖 chase **不以 inject 强证**。
2. 抽测窗并行 D410 sx08 → D411/D412 换 jar；`SHORT_KEYS` 含 sx08+，未帽底本会托住 `sx_day_left_sum`。自然通关填满八本可证满态，但总控收束令 **停止新清本循环**。
3. mineflayer `clickWindow` 偶报 transaction 超时，窗口仍切 —— 以 title/dump 为准（未满左键 `余烬 · 锻造` 已证）。
4. 菜单 tip 原文七本口径；live 已挂八/九本 day_line，**D409 满态条件与跳转块保留**。

## tip / 产物

| 项 | 值 |
|----|-----|
| 本 STATUS tip | （push 后填） |
| 菜单上游 tip | **`1c782cbc`** |
| jar 上游 tip | **`20cd1318`** |
| DESIGN / 勾毕 | **`9a22f1a0`** / **`feb70949`** |
| 分支 | main |
| 证据目录 | `/workspace/tmp/d409-reward-spend/` |

## 手法与注记

- mineflayer `D409Spend` + `lib/proxy-login`；脚本 `d409-spot.js` / `d409-final.js` / `d409-live-final.js` 等
- 开菜单：console `trmenu open ember_p1_short D409Spend`；体力 `corerpg stamina set`
- **未**改 feed / afk 产量 / Stage2 / gate_daily / 日帽数值 / S40–S47 金额
- **未**切分支；**≠关观察**
- 工作区并行脏文件：**只 stage 本 STATUS**

## 结束态（抽测窗）

| 项 | 值 |
|----|-----|
| 结论 | **PARTIAL** |
| jar（结束可见） | `1.65.119-d412.local`（W1a 符号仍在） |
| Stage2 | enabled/migrate/set_bonus=true |
| daily_kills | 2400 |
