# 余烬 · D355：装备页工坊入口 Stage2 护甲分解诚实薄抽（F1–F4）· 线上活菜单验收

- **性质：观察期薄抽验 · 显示/菜单可见性 · 未改菜单 / 未动开关 / 未换 jar / 未关观察**
- 上游施工：[`STATUS-ember-six-slot-gear-forge-entry-honesty-d355-2026-10-09.md`](STATUS-ember-six-slot-gear-forge-entry-honesty-d355-2026-10-09.md) · 菜单 tip **`f64ebbf2`** · `trmenu reload` 已做
- DESIGN：[`DESIGN-ember-six-slot-gear-forge-entry-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-gear-forge-entry-honesty-2026-10-09.md) §2.4
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **580488** · jar **`1.65.101-d335.local`**
- 测号：`D355Spot`（经代理 joinPlay）；**未动真人档**；**未抢** D354 测号/证据（`/workspace/tmp/d354-*` mtime 未变）
- 证据：`/workspace/tmp/d355-spot/`（主结果 `99-results.json`；活菜单 `20-gear.json` / `21-forge-entry.json`）
- 执行：2026-10-09 21:46 CST
- **结论：PASS（F1–F4 全绿）**

## 人话

测号进服打开装备页工坊入口：除「手持要处理的刃或护符再打开」外，已见「手持护甲：仅可分解（白板零头）；养成随护符」。无暗示甲可强化/升阶/精工。本号未改 forge 页内（仍 D333 `ee5217d6`）与 hub 工坊。三开关与 bv62 抽前抽后未动。未碰 d354-spot。观察不关。

## 验收表（DESIGN §2.4）

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| **F1** | gear `f` lore 有护甲仅分解 / 白板零头；仍有刃护符行 | **PASS** | src+live 一致；有「手持护甲：仅可分解（白板零头）」；仍有「刃或护符」行；无甲养成谎称。`00-static.json` |
| **F2** | 打开装备页工坊格：可见甲只能分解短述 | **PASS** | name「工坊 · 强化 / 升阶 / 精工 / 成色 / 互换 / 分解」；lore 四行含甲仅分解+白板零头+打开工坊。`21-forge-entry.json` / `20-gear.json` |
| **F3** | 工坊页内 / hub 工坊本号未改 | **PASS** | D355 提交未触 `ember_p1_forge.yml` / `ember_hub.yml`；forge tip 仍 `ee5217d6`；src=live md5。`00-f3-static.json` |
| **F4** | 本号未拧开关；未抢 d354-spot | **PASS** | 抽前/后 enabled/migrate/set_bonus true · bv62；测号 D355Spot；证据仅 `/workspace/tmp/d355-spot/`；d354 `99-results.json` mtime 未变。`00-precheck.json` / `90-endstate.json` |

## 手法与注记

- mineflayer `D355Spot` + `lib/proxy-login`；脚本 `/workspace/tmp/d355-spot/d355-spot.js`
- 开菜单：`/trmenu open ember_p1_gear` → dump 工坊入口 lore
- **未**改 yml / jar / set_bonus / bv / 文案；**未**切分支 / stash / reset / `checkout -- .`
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）；**不抢 K3**；**不抢 D354**

## 结束态

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 62 |
| play PID | 580488 |
| jar | 1.65.101-d335.local |
| 菜单 tip（D355 施工） | f64ebbf2 |
| forge tip（未改） | ee5217d6 |
| 抽时 HEAD | 593d8792 |
| 分支 | main |

## 总控签字栏（待签）

| 项 | 签认 |
|----|------|
| **D355 薄抽 PASS** | [ ] |
| F1–F4 | [ ] PASS（测试） |
| 活菜单装备页工坊入口可见甲仅分解/白板零头 | [ ] |
| 仍有刃护符行；无甲养成谎称 | [ ] |
| 未改 forge 页内 / hub 工坊 | [ ] |
| 零改 yml·开关·jar·bv；未抢 d354-spot | [ ] |
| ≠关观察（满窗仍须 ≥2026-10-10 17:40 CST） | [ ] |
| 不抢 K3 | [ ] |
| 签字 / 日期 | |

---

*D355 薄抽 · F1–F4 PASS · 测号 D355Spot · 未改开关 · ≠关观察 · 证据 `/workspace/tmp/d355-spot/`。*
