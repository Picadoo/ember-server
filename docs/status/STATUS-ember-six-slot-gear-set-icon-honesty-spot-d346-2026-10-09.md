# 余烬 · D346：装备页套装格 Stage2 四件套叙事诚实薄抽（G1–G4）· 线上活菜单验收

- **性质：观察期薄抽验 · 显示/菜单可见性 · 未改菜单 / 未动开关 / 未换 jar / 未关观察**
- 上游施工：[`STATUS-ember-six-slot-gear-set-icon-honesty-d346-2026-10-09.md`](STATUS-ember-six-slot-gear-set-icon-honesty-d346-2026-10-09.md) · 菜单 tip **`d51b28a2`** · `trmenu reload` 已做
- DESIGN：[`DESIGN-ember-six-slot-gear-set-icon-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-gear-set-icon-honesty-2026-10-09.md) §2.3
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **580488** · jar **`1.65.101-d335.local`**
- 测号：`D346Spot`（经代理 joinPlay）；**未动真人档**；**未抢** D345Spot / `/workspace/tmp/d345-spot/`
- 证据：`/workspace/tmp/d346-spot/`（主结果 `99-results.json`；活菜单 `20-gear.json` / `21-set-icon.json`）
- 执行：2026-10-09 21:18–21:19 CST
- **结论：PASS（G1–G4 全绿）**

## 人话

测号进服打开装备页套装星：已见「护甲四件套：先让刃与护符同族」+「详情：护甲页…」（PAPI 解析非空）；族觉醒 I/II/III 与 `%awaken%` 名仍在；未激活态无静态「受伤 −3%」谎称。三开关与 bv62 抽前抽后未动；D345 证据目录未碰。观察不关。

## 验收表（DESIGN §2.3）

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| **G1** | gear `S` lore 有 `%corerpg_p1_armor_set%`；仍有 awaken 与觉醒档 | **PASS** | src+live 一致；有觉醒 I/II/III；无静态 −3%。`00-static.json` |
| **G2** | 打开装备页套装星：活菜单可见护甲四件套态 | **PASS** | name「套装：未成套」；lore `护甲四件套：先让刃与护符同族` + 护甲页详情；无 `%…%` 残留。`21-set-icon.json` / `20-gear.json` |
| **G3** | 未激活态：无静态「受伤 −3%」谎称（−3% 仅随 PAPI 已激活） | **PASS** | 值侧「先让刃与护符同族」无 −3%；无独立静态 −3% 行。`21-set-icon.json` |
| **G4** | 本号未拧开关；未抢 D345 测号/证据目录 | **PASS** | 抽前/后 enabled/migrate/set_bonus true · bv62；测号 `D346Spot`；d345 `99-results.json` 仍在。`00-precheck.json` / `90-endstate.json` |

## 手法与注记

- mineflayer `D346Spot` + `lib/proxy-login`；脚本 `/workspace/tmp/d346-spot/d346-spot.js`
- 开菜单：`/trmenu open ember_p1_gear` → dump 套装格（name 含「套装」）lore
- **未**改 yml / jar / set_bonus / bv / 文案；**未**切分支 / stash / reset / `checkout -- .`
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）；**不抢 K3**；**不抢 D345**

## 结束态

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 62 |
| play PID | 580488 |
| jar | 1.65.101-d335.local |
| 菜单 tip（D346 施工） | d51b28a2 |
| 抽时 HEAD | 91b1973f |
| 分支 | main |
| D345 证据 | `/workspace/tmp/d345-spot/99-results.json` 完整（user=D345Spot） |

## 总控签字栏（待签）

| 项 | 签认 |
|----|------|
| **D346 薄抽 PASS** | [ ] |
| G1–G4 | [ ] PASS（测试） |
| 活菜单装备页套装星护甲四件套可见 | [ ] |
| 未激活不静态谎称 −3% | [ ] |
| 零改 yml·开关·jar·bv；未抢 D345 | [ ] |
| ≠关观察（满窗仍须 ≥2026-10-10 17:40 CST） | [ ] |
| 不抢 K3 | [ ] |
| 签字 / 日期 | |

---

*D346 薄抽 · G1–G4 PASS · 测号 D346Spot · 未改开关 · ≠关观察 · 证据 `/workspace/tmp/d346-spot/`。*
