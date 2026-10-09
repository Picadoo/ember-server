# 余烬 · D345：冒险页「我的进度」四件套叙事诚实薄抽（A1–A4）· 线上活菜单验收

- **性质：观察期薄抽验 · 显示/菜单可见性 · 未改菜单 / 未动开关 / 未换 jar / 未关观察**
- 上游施工：[`STATUS-ember-six-slot-adventure-set-honesty-d345-2026-10-09.md`](STATUS-ember-six-slot-adventure-set-honesty-d345-2026-10-09.md) · 菜单 tip **`49f401b1`** · `trmenu reload` 已做
- DESIGN：[`DESIGN-ember-six-slot-adventure-set-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-adventure-set-honesty-2026-10-09.md) §2.4
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **580488** · jar **`1.65.101-d335.local`**
- 测号：`D345Spot`（经代理 joinPlay）；**未动真人档**；login/proxy/MariaDB 未动
- 证据：`/workspace/tmp/d345-spot/`（主结果 `99-results.json`；活菜单 `20-adventure.json` / `21-progress-item.json`）
- 执行：2026-10-09 21:15–21:16 CST
- **结论：PASS（A1–A4 全绿）**

## 人话

测号进服打开冒险页「我的进度」：已见「护甲四件套：先让刃与护符同族」（PAPI 解析非空）；族觉醒 / 成套进度行仍在；未激活态无静态「受伤 −3%」谎称。三开关与 bv62 抽前抽后未动。观察不关。

## 验收表（DESIGN §2.4）

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| **A1** | `ember_p1_adventure.yml` 图标 K lore 有 `%corerpg_p1_armor_set%`；仍有 awaken / set_progress；顺序 set_progress→armor_set→pending | **PASS** | src+live 一致；`00-static.json` |
| **A2** | 打开冒险「我的进度」活菜单可见护甲四件套态；PAPI 解析非空 | **PASS** | lore `护甲四件套：先让刃与护符同族`；无 `%…%` 残留。`21-progress-item.json` / `20-adventure.json` |
| **A3** | 未激活态：无静态「受伤 −3%」谎称（−3% 仅随 PAPI 已激活） | **PASS** | 值侧「先让刃与护符同族」无 −3%；无独立静态 −3% 行。`21-progress-item.json` |
| **A4** | 本号未拧开关；读确认 enabled/migrate/set_bonus true · bv62 | **PASS** | 抽前/后均 true/true/true · bv62；yml 未变。`00-precheck.json` / `90-endstate.json` |

## 手法与注记

- mineflayer `D345Spot` + `lib/proxy-login`；脚本 `/workspace/tmp/d345-spot/d345-spot.js`
- 开菜单：`/trmenu open ember_p1_adventure` → dump 图标「我的进度」lore
- **未**改 yml / jar / set_bonus / bv；**未**切分支 / stash / reset / `checkout -- .`
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）；**不抢 K3**

## 结束态

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 62 |
| play PID | 580488 |
| jar | 1.65.101-d335.local |
| 菜单 tip（D345 施工） | 49f401b1（HEAD 祖先；抽时 HEAD 已前进含 D346） |
| 分支 | main |

## 总控签字栏（待签）

| 项 | 签认 |
|----|------|
| **D345 薄抽 PASS** | [ ] |
| A1–A4 | [ ] PASS（测试） |
| 活菜单「我的进度」护甲四件套可见 | [ ] |
| 未激活不静态谎称 −3% | [ ] |
| 零改 yml·开关·jar·bv | [ ] |
| ≠关观察（满窗仍须 ≥2026-10-10 17:40 CST） | [ ] |
| 不抢 K3 | [ ] |
| 签字 / 日期 | |

---

*D345 薄抽 · A1–A4 PASS · 测号 D345Spot · 未改开关 · ≠关观察 · 证据 `/workspace/tmp/d345-spot/`。*
