# 余烬 · D340：D339 hub/help 落地薄抽（S0–S6）· 线上活菜单验收

- **性质：观察期薄抽验 · 显示/菜单可见性 · 未改菜单 / 未动开关 / 未换 jar / 未关观察**
- 上游施工：[`STATUS-ember-six-slot-hub-help-set-honesty-d339-2026-10-09.md`](STATUS-ember-six-slot-hub-help-set-honesty-d339-2026-10-09.md) · 菜单 tip **`2ba73e97`** · `trmenu reload` 已做
- DESIGN：[`DESIGN-ember-six-slot-d339-hub-help-spot-2026-10-09.md`](../design/DESIGN-ember-six-slot-d339-hub-help-spot-2026-10-09.md) §2 · 批 A tip **`6369b8c1`** · DESIGN tip **`86e0f813`**
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **580488** · jar **`1.65.101-d335.local`** sha256 `2bd11b903c…850f`
- 测号：`D340Spot`（经代理 25565）；**未动真人档**；login/proxy/MariaDB 未动
- 证据：`/workspace/tmp/d340-d339-hub-help-spot/`（主结果 `99-results.json`；另有 `d340-d339-hub-help-spot.tgz`）
- 执行：2026-10-09 20:49–20:51 CST
- **结论：PASS（S0–S6 全绿）**

## 人话

测号进服打开 `/ember`：装备格已见「护甲四件套」且 PAPI 解析为「先让刃与护符同族」（非空、无「后续开放」、未激活不静态谎称 −3%）；工坊格有「手持护甲：仅可分解（白板零头）」且刃/护符养成句仍在。帮助「怎么玩」「三套装」两层叙事（族觉醒 vs 四件套受伤 −3%）齐全。点击装备→「余烬 · 装备」、工坊→「余烬 · 锻造」、帮助可关。抽后三开关/bv/play PID 未动。

## 验收表（DESIGN §2.2）

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| **S0** | 基线三开关 + bv62 + jar tip | **PASS** | `true/true/true` · bv=62 · jar `1.65.101-d335.local` · PID 580488 · `00-precheck.json` |
| **S1** | hub 装备格：护甲四件套 + armor_set 解析；无后续开放；未激活不静态谎称 −3% | **PASS** | lore `§8护甲四件套：§f§7先让刃与护符同族`；无 `%…%` 残留；无「后续开放」；值侧无 −3%。`10-hub.json` |
| **S2** | hub 工坊格：「手持护甲：仅可分解（白板零头）」；刃护符养成句仍在 | **PASS** | 甲句 + `手持刃或护符：强化 / 升阶 / …`。`10-hub.json` |
| **S3** | help 怎么玩：族觉醒 + 四件套 −3% 两层 | **PASS** | `刃+护符同族 = 族觉醒；再穿同族 T2+ 甲≥2 = 四件套（受伤 −3%）`。`30-help.json` |
| **S4** | help 三套装：四件套 −3%/不进 B/H + 指向套装/装备/护甲 | **PASS** | 焚烬/烬爆/炽愈保留；`受伤 −3%，不进面板 B/H`；`详情：主菜单 → 套装 / 装备 / 护甲`。`30-help.json` |
| **S5** | 点击烟：装备→gear；工坊→forge；帮助可关 | **PASS** | 标题「余烬 · 装备」「余烬 · 锻造」「余烬 · 帮助」；帮助可关。`20-gear-click.json` / `21-forge-click.json` / `30-help.json` |
| **S6** | 不动复核：本号未改开关/文案 | **PASS** | 抽后仍 `true/true/true` · bv62 · PID 580488；hub/help 目标句仍在。`90-endstate.json` |

## 手法与注记

- mineflayer `D340Spot` + `lib/proxy-login`；脚本 `d340-spot.js`（`menu-lore-dump.js` 思路：`/ember` dump → 点装备/工坊/帮助）
- 静态 `rg` 辅证：`00-static.txt`（与 live 一致）
- **未**改 yml / jar / set_bonus / bv；login/proxy/MariaDB 未动
- 可选 D339 抽验并入本号正式验收；证据目录用 `d340-d339-hub-help-spot/`（非旧 `d339-hub-help-spot/`）
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）；**不抢 K3**

## 结束态

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 62 |
| play PID | 580488 |
| jar | 1.65.101-d335.local · sha256 `2bd11b903c703163ec201075793b34b53c9d9e04a524a6ac85f2461bee9e850f` |
| 菜单 tip（D339） | 2ba73e97 |
| 批 A tip（D340 docs） | 6369b8c1 |
| DESIGN tip | 86e0f813 |

---

*D340 薄抽验 PASS · 测号 D340Spot · 证据 `/workspace/tmp/d340-d339-hub-help-spot/` · 未改开关 · ≠关观察。*
