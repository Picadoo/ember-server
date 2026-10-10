# 余烬 · D409：短征有奖→工坊可追 · 线上薄抽（含补验）

- **性质：观察期薄抽 · `ember_p1_short` 满态追工坊（W1b/c）+ W1d 右挂机 + W1a 结算半句 · 未改配置 / 未动开关 / 未关观察**
- 上游施工：jar tip **`20cd1318`**（W1a）· 菜单 tip **`1c782cbc`**（W1b/c+W1d）· DESIGN **`9a22f1a0`** · 勾毕 **`feb70949`** · 施工 STATUS [`STATUS-ember-short-reward-spend-chase-w1a-d409-2026-10-10.md`](STATUS-ember-short-reward-spend-chase-w1a-d409-2026-10-10.md) / [`STATUS-ember-short-reward-spend-chase-d409-menu-2026-10-10.md`](STATUS-ember-short-reward-spend-chase-d409-menu-2026-10-10.md)
- 现态（补验窗）：live jar **`1.65.119-d412.local`**（W1a `spendChase*` 字符串仍在）· 菜单 D409 满态块仍在 · Stage2 三开 · `daily_kills=2400`
- 测号：`D409Spend`；经代理 joinPlay；**未动真人档**；**禁离线 inject 日帽**（总控硬令）
- 证据：`/workspace/tmp/d409-reward-spend/`（初抽）· `/workspace/tmp/d409-reward-spend-reverify/`（补验自然清本）
- 执行：初抽 2026-10-10 **07:14–08:04 CST** → PARTIAL @`8773b3ae`；补验 **08:08–08:34 CST**（自然清本 sx08×3 + sx09×3；中途 Paper Watchdog 重启 play，login 亦曾掉线后自拉起）
- **结论：PASS** — 自然有奖通关把 `sx_day_left_sum` 打到 **0** 后，满态 Open/I/B live CTA→工坊全绿；有奖 settle tell「材料已进仓 · 工坊可花」(+ 本本满半句) live 多趟绿证

## 人话

短征有奖打满后，选本页说「去工坊花」；有奖通关结算多一句「材料已进仓 · 工坊可花」。初抽因禁/不可靠 inject 未能绿证满态与 chase；补验用自然清本填满 sx08/sx09 日帽后两项均绿。

## 验收表

| ID | 项 | 结果 | 证据 |
|----|----|------|------|
| **V0** | 菜单 tip `1c782cbc` 祖先 · 满态 Open/I/B/W1d 块仍在 live YAML | **PASS** | 初抽 `10-menu-static.json` · live `ember_p1_short.yml` |
| **V0b** | jar tip `20cd1318` W1a · `spendChaseTell` 文案仍在叠档 jar | **PASS** | live `1.65.119-d412.local` Enabling；settle 活证 chase 行 |
| **U1** | `EmberShortRulesTest` 48/48（含 spendChase） | **PASS** | 初抽 `12-unit-test.txt` |
| **S1–S4** | 静态满态/未满 Open/I/B/W1d | **PASS** | 初抽 `10-menu-static` |
| **L1** | live 未满：无满态口号 | **PASS** | 初抽 `live-open-unfull` / `live-menu-unfull` |
| **L2** | live 无奖通关不假喊 chase | **PASS** | 初抽 `56d-settle.json` |
| **L3** | live 有奖 settle「材料已进仓 · 工坊可花」(+ 可选本本满) | **PASS** | 补验 `sx08-c1-settle.json` / `sx08-c3-settle.json`（含「本本今日有奖已满」）/ `sx09-f1-settle.json` / `sx09-f2-settle.json` |
| **L4** | live `sx_day_left_sum==0` 满态 Open/I/B + 左工坊 | **PASS** | 补验 `follow-days-post.json` sum=0 · `follow-open-chat-a1.json` · `follow-menu-full-a1.json` · `follow-b-left-forge.json`（title「余烬 · 锻造」） |
| **V5** | 零改 daily_kills/Stage2/日帽数值/S40–S48 · ≠关观察 | **PASS** | 补验 `follow-99-summary` V5_unchanged · 全程无 inject |

## 补验对照

| 项 | 值 |
|----|-----|
| 日帽手法 | **自然有奖通关** sx08×3 + sx09×2（DB 起 sx09=1）打满；**零** `cr_players` / YAML inject |
| days pre→post | sum **6→0**（sx01–sx07 已满；sx08 0→3；sx09 1→3） |
| chase 样例 | `[余烬] 材料已进仓 · 工坊可花（强化/精工/成色）` |
| 本本满样例 | `…工坊可花（强化/精工/成色） · 本本今日有奖已满`（sx08-c3 / sx09-f2） |
| Open 满态 | `今日短征有奖已满 · 材料在仓 · 去工坊花` |
| I 满态 | `今日有奖已满 · 去工坊花` |
| B 满态 | `今日有奖已满 · 去工坊花材料` → 左键 **余烬 · 锻造** |
| W1d 旁注 | 补验菜单窗 B lore 走「体力已尽」支（`/papi parse me %corerpg_stamina%`=80 时 TrMenu 条件仍亮尽态）；左键工坊已绿；右键挂机仍靠静态 YAML priority1。不挡补验 PASS |

## 旁注

1. 初抽 PARTIAL @`8773b3ae`：离线 inject 日帽不可靠（写库后 join 仍读旧内存）；总控收束禁 inject → 改自然清本。
2. 补验中途 Paper Watchdog 因长清本卡顿触发 `./start.sh` 重启；play/login 曾短时不可达，拉起后续通。
3. 选页 lore 已叠至「十本」口径（并行 sx10 挂盘）；D409 满态条件与跳转块保留。
4. mineflayer `clickWindow` 偶报 transaction 超时，窗口仍切 —— 以 title/dump 为准。

## tip / 产物

| 项 | 值 |
|----|-----|
| 本 STATUS tip | **`25c58406`** |
| 上游 PARTIAL tip | **`8773b3ae`** |
| 菜单上游 tip | **`1c782cbc`** |
| jar 上游 tip | **`20cd1318`** |
| DESIGN / 勾毕 | **`9a22f1a0`** / **`feb70949`** |
| 分支 | main |
| 证据目录 | `/workspace/tmp/d409-reward-spend-reverify/`（主）· `/workspace/tmp/d409-reward-spend/`（初抽） |

## 手法与注记

- mineflayer `D409Spend` + `lib/proxy-login`；脚本 `d409-reverify.js` / `d409-reverify-sx09.js`
- 开菜单：console `trmenu open ember_p1_short D409Spend`；体力 `corerpg stamina set`（非日帽 inject）
- **未**改 feed / afk 产量 / Stage2 / gate_daily / 日帽数值 / S40–S48 金额
- **未**切分支；**≠关观察**；**未**离线 inject 日帽
- 工作区并行脏文件：**只 stage 本 STATUS**

## 结束态（补验窗）

| 项 | 值 |
|----|-----|
| 结论 | **PASS** |
| jar | `1.65.119-d412.local`（W1a 符号仍在） |
| Stage2 | enabled/migrate/set_bonus=true |
| daily_kills | 2400 |
| D409Spend sum | 0（sx08/sx09 day=3/3） |
