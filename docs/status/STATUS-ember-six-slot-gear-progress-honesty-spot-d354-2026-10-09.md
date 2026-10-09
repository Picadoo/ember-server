# 余烬 · D354：装备页「成套进度」Stage2 四件套叙事诚实薄抽（P1–P4）· 线上活菜单验收

- **性质：观察期薄抽验 · 显示/菜单可见性 · 未改菜单 / 未动开关 / 未换 jar / 未关观察**
- 上游施工：[`STATUS-ember-six-slot-gear-progress-honesty-d354-2026-10-09.md`](STATUS-ember-six-slot-gear-progress-honesty-d354-2026-10-09.md) · 菜单 tip **`7538bbc9`** · `trmenu reload` 已做
- DESIGN：[`DESIGN-ember-six-slot-gear-progress-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-gear-progress-honesty-2026-10-09.md) §2.3
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **580488** · jar **`1.65.101-d335.local`**
- 测号：`D354Spot`（经代理 joinPlay）；**未动真人档**；**未抢** D345/D346 测号/证据
- 证据：`/workspace/tmp/d354-spot/`（主结果 `99-results.json`；活菜单 `20-gear.json` / `21-progress-icon.json`）
- 执行：2026-10-09 21:43 CST
- **结论：PASS（P1–P4 全绿）**

## 人话

测号进服打开装备页「成套进度」：已见「护甲四件套：先让刃与护符同族」（PAPI 解析非空）；`set_progress` 仍在；终点句为「刃·护符终点」（非「主线的终点」）；未激活态无静态「受伤 −3%」谎称。同页套装星 `S` 与 adventure 的 armor_set 未误改。三开关与 bv62 抽前抽后未动。观察不关。

**旁注（D352 FYI · 非单开）：** help 图标 `S` name=`套装说明`（src+live 静态确认 @`f2ce7a07`）· PASS。

## 验收表（DESIGN §2.3）

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| **P1** | gear `P` lore 有 `%corerpg_p1_armor_set%`；仍有 `set_progress`；终点句已软化 | **PASS** | src+live 一致；有「刃·护符终点」；无「主线的终点」；无静态 −3%。`00-static.json` |
| **P2** | 打开装备页「成套进度」：活菜单可见护甲四件套态 | **PASS** | name「成套进度」；lore `护甲四件套：先让刃与护符同族` + set_progress 已解析；无 `%…%` 残留。`21-progress-icon.json` / `20-gear.json` |
| **P3** | 未激活态：无静态「受伤 −3%」谎称（−3% 仅随 PAPI 已激活） | **PASS** | 值侧「先让刃与护符同族」无 −3%；无独立静态 −3% 行。`21-progress-icon.json` |
| **P4** | 本号未拧开关；未误改同页 `S` / adventure | **PASS** | 抽前/后 enabled/migrate/set_bonus true · bv62；`S` 仍有 armor_set+awaken；adventure 仍有 armor_set。`00-precheck.json` / `90-endstate.json` / `22-set-icon-S.json` |

## 手法与注记

- mineflayer `D354Spot` + `lib/proxy-login`；脚本 `/workspace/tmp/d354-spot/d354-spot.js`
- 开菜单：`/trmenu open ember_p1_gear` → dump「成套进度」lore
- **未**改 yml / jar / set_bonus / bv / 文案；**未**切分支 / stash / reset / `checkout -- .`
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）；**不抢 K3**；**不抢 D345/D346**

## 结束态

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 62 |
| play PID | 580488 |
| jar | 1.65.101-d335.local |
| 菜单 tip（D354 施工） | 7538bbc9 |
| 抽时 HEAD | f64ebbf2 |
| 分支 | main |
| D352 help `S` name（FYI） | `套装说明`（静态 PASS @ f2ce7a07） |

## 总控签字栏（待签）

| 项 | 签认 |
|----|------|
| **D354 薄抽 PASS** | [ ] |
| P1–P4 | [ ] PASS（测试） |
| 活菜单装备页成套进度护甲四件套可见 | [ ] |
| 未激活不静态谎称 −3% | [ ] |
| 零改 yml·开关·jar·bv；未误改 S/adventure | [ ] |
| ≠关观察（满窗仍须 ≥2026-10-10 17:40 CST） | [ ] |
| 不抢 K3 | [ ] |
| 签字 / 日期 | |

---

*D354 薄抽 · P1–P4 PASS · 测号 D354Spot · 未改开关 · ≠关观察 · 证据 `/workspace/tmp/d354-spot/`。*
