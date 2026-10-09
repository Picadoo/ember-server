# 余烬 · D351：次入口「三套装」叙事诚实薄抽（T1–T4）· 线上活菜单验收

- **性质：观察期薄抽验 · 显示/菜单可见性 · 未改菜单 / 未动开关 / 未换 jar / 未关观察**
- 上游施工：[`STATUS-ember-six-slot-secondary-set-copy-d351-2026-10-09.md`](STATUS-ember-six-slot-secondary-set-copy-d351-2026-10-09.md) · 菜单 tip **`b8bccabf`** · `trmenu reload` 已做
- DESIGN：[`DESIGN-ember-six-slot-secondary-set-copy-2026-10-09.md`](../design/DESIGN-ember-six-slot-secondary-set-copy-2026-10-09.md) §2.6
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · jar **`1.65.101-d335.local`**
- 测号：`D351Spot`（经代理 joinPlay；控制台仅标 Q01–Q07 首通以露出团本格；**未动真人档**）
- 证据：`/workspace/tmp/d351-spot/`（主结果 `99-results.json`；团本 `21-raid-items.json`；hub 帮助 `31-hub-help.json`；codex 帮助 `41-codex-help.json`）
- 执行：2026-10-09 21:34–21:36 CST
- **结论：PASS（T1–T4 全绿）**

## 人话

团本旁注不再写「三套装不变」，三张团本 lore 均见「战斗规则不变」+「族觉醒 / 护甲四件套规则同主线」；主菜单帮助捷径与图录帮助捷径均写「套装（含四件套）」。本债新增行无静态「受伤 −3%」。三开关与 bv62 抽前抽后未动。观察不关。

## 验收表（DESIGN §2.6）

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| **T1** | adventure：无「三套装不变」现状句；有战斗规则不变 + 四件套/主线指针（静态 rg + 活菜单团本 lore） | **PASS** | src+live 无旧句；三张团本活 lore 均含「战斗规则不变」与「护甲四件套规则同主线」。`00-static.json` / `21-raid-items.json` |
| **T2** | hub 帮助捷径 i + codex H：含四件套/套装说明 | **PASS** | 静态「套装（含四件套）」；活菜单 hub 帮助 / codex 帮助同文案。`31-hub-help.json` / `41-codex-help.json` |
| **T3** | 本债新增行无静态「受伤 −3%」谎称 | **PASS** | 新增行与活团本/帮助捷径均无「受伤 −3%」。`00-static.json` / `99-results.json` |
| **T4** | 开关/bv 未拧（三 true · bv62） | **PASS** | 抽前/后 enabled/migrate/set_bonus true · bv62；yml 未变。`00-precheck.json` / `90-endstate.json` |

## 手法与注记

- mineflayer `D351Spot` + `lib/proxy-login`；脚本 `/workspace/tmp/d351-spot/d351-spot.js`
- 团本格需 `%corerpg_p1_q07done%=1`：控制台 `corerpg p1 runs firstclear D351Spot q01..q07`（仅测号进度，非改开关/文案）
- 开菜单：`/trmenu open ember_p1_adventure`（dump 三团本）· `/ember`（hub 帮助 i）· `/ember_p1_codex`（codex 帮助 H）
- **未**改 yml / jar / set_bonus / bv；**未**切分支 / stash / reset / `checkout -- .`
- **≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）；**不抢 K3**

## 结束态

| 项 | 值 |
|----|-----|
| set_bonus / enabled / migrate | true / true / true |
| balance_version | 62 |
| jar | 1.65.101-d335.local |
| 菜单 tip（D351 施工） | b8bccabf（HEAD 祖先；抽时 HEAD 已前进） |
| 分支 | main |

## 总控签字栏（待签）

| 项 | 签认 |
|----|------|
| **D351 薄抽 PASS** | [ ] |
| T1–T4 | [ ] PASS（测试） |
| 团本×3 无「三套装不变」+ 有四件套指针 | [ ] |
| hub i / codex H 含「套装（含四件套）」 | [ ] |
| 本债新增行无静态 −3% 谎称 | [ ] |
| 零改 yml·开关·jar·bv | [ ] |
| ≠关观察（满窗仍须 ≥2026-10-10 17:40 CST） | [ ] |
| 不抢 K3 | [ ] |
| 签字 / 日期 | |

---

*D351 薄抽 · T1–T4 PASS · 测号 D351Spot · 未改开关 · ≠关观察 · 证据 `/workspace/tmp/d351-spot/`。*
