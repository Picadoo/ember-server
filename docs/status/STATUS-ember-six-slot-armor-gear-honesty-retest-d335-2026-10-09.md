# 余烬 · D335 装备页护甲入口诚实 · 复测（armor_sa/sb · 2026-10-09）

- **性质：观察期复测 · 显示 only · 未改菜单 / 未动开关 / 未关观察**
- 上游 FAIL：[`STATUS-ember-six-slot-armor-gear-honesty-retest-d329-2026-10-09.md`](STATUS-ember-six-slot-armor-gear-honesty-retest-d329-2026-10-09.md) @ `238ba2f2`（contains 不命中）· spot @ `ac3477fa`（active/busy 不命中）
- 修复 tip：jar [`STATUS-ember-six-slot-armor-sa-sb-d335-2026-10-09.md`](STATUS-ember-six-slot-armor-sa-sb-d335-2026-10-09.md) @ **`985942be`** · 菜单 [`STATUS-ember-six-slot-armor-set-icon-menu-d335-2026-10-09.md`](STATUS-ember-six-slot-armor-set-icon-menu-d335-2026-10-09.md) @ **`3f827bd1`**
  （条件改为 `check papi %corerpg_p1_armor_sa% == 1` / `armor_sb% == 1`；`trmenu reload` 已载入）
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **580488** · jar **`1.65.101-d335.local`** sha `2bd11b90…`
- 测号：`D335Retest`（经代理 25565）；**未动真人档**
- 证据：`/workspace/tmp/d335-armor-sa-sb-retest/` + `/workspace/tmp/d335-armor-sa-sb-retest.tgz`
- 执行：2026-10-09 19:59–20:01 CST
- **日历：** 观察起点 2026-10-08 17:40 CST；**满窗仍须 ≥2026-10-10 17:40 CST**
- **结论：FAIL（H1 三态 icons 仍未切换；H2 静态 §a受伤 −3% 行未出现）。H2 内容诚实（进行中/未激活不谎称 −3%）/ H3 / 点击进护甲页 / 开关未动过。**
- **建议：维持观察；本号不改菜单；`check papi %armor_sa|sb% == 1` 仍不命中（与 D329/D334 同病：条件求值层）→ 另派。**

## 人话

D335 把条件换成了短别名 `armor_sa` / `armor_sb`，jar 与菜单 tip 都对上了，也 reload 了。PAPI 解析本身完全正确：已激活时 `sa=1 sb=0`，进行中 `sa=0 sb=1`。但进服后装备页入口和护甲页「四件套」格**仍停在「未激活 / 先让刃与护符同族」**。静态 `§a受伤 −3%` 分支行因为 icon 没命中所以不会出现；PAPI 展开行在已激活时能看到「受伤 −3%」。进行中不会谎称 −3%。点「护甲」仍进护甲页。开关全程未动。

## 验收表

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| **H1** | 三态 icons 与护甲页 S 一致（已激活/进行中/未激活） | **FAIL** | 三态 gear/armor 分支均为 `armor_on_icon`；护甲页名均「四件套 · 未激活」。已激活时 PAPI `sa=1` 且 set=`…受伤 −3%`，仍落 armor_on。`v2-active.json` / `v2-busy.json` / `99-results.json` |
| **H2** | 已激活出静态 `§a受伤 −3%`；进行中/未激活不谎称 | **静态行 FAIL / 内容诚实 PASS** | **静态行未出现**（`static:false`）。PAPI 行在已激活可见「受伤 −3%」。进行中/对照 lore **无** −3% 谎称。`v2-active.json` / `v2-busy.json` |
| **H3** | 无「后续开放」 | **PASS** | 静态 yml + 三态 dump。`00-static.json` |
| **H4a** | 点开仍进护甲页 | **PASS** | title `余烬 · 护甲`。`v2-click-armor.json` |
| **H4b** | 开关 / bv / play 本号未改 | **PASS** | enabled/migrate/set_bonus 全 true · bv=62 · play **580488**。`00-precheck.json` / `99-results.json` |

## 静态扫（tip 核对）

| 项 | 结果 |
|----|------|
| gear/armor 条件为 `armor_sa/sb == 1` | PASS @ `3f827bd1` |
| 无 `contains 受伤|需同族` / `_set_active% == 1` 条件 | PASS |
| 全文无「后续开放」 | PASS |
| jar `1.65.101-d335.local` · tip `985942be` | PASS |
| tip 后 `trmenu reload`（菜单落地号已做） | 沿用 · 本号未再 reload |

## PAPI 对照（bot `/papi parse me`）

| 态 | set | active | busy | **sa** | **sb** | on | 菜单分支 |
|----|-----|--------|------|--------|--------|-----|----------|
| 进行中 1/2 | `…1/2 · 需同族…` | 0 | 1 | **0** | **1** | 1 | armor_on（应 busy） |
| 已激活 2/2 | `…2/2 · 受伤 −3%` | 1 | 0 | **1** | **0** | 1 | armor_on（应 active） |
| 对照 | `…1/2 · 需同族…` | 0 | 1 | **0** | **1** | 1 | armor_on |

→ **短别名数值正确；TrMenu `check papi … == 1` 仍不切分支。**

## 手法

- mineflayer `D335Retest` + `lib/proxy-login` / `lib/console`；`/trmenu open ember_p1_gear|ember_p1_armor` dump
- 态：进行中（焚烬刃+护符 + 1 靴）/ 已激活（≥2 同族 T2+）/ 对照（异族护符错配）
- **优先拆件与菜单换装**；**未**关 `set_bonus`；**未**改 yml / jar / bv / 菜单
- 收尾：`clear` + `deop`；`list` 0

## 根因备忘（不施工）

- lore 中 `%corerpg_p1_armor_set%` **能展开**；`/papi parse` 对 `armor_sa`/`armor_sb` **返回正确 1/0**
- 条件 `check papi %corerpg_p1_armor_sa% == 1` / `armor_sb% == 1` **仍不命中**（与前次 `== 1` on long keys、`contains` 同病：TrMenu 条件求值层）
- 装备页 M 与护甲页 S **同步失败** → 非单页问题
- **本号不改菜单、不改 jar**

## 建议

1. **维持观察**（bv62 / set_bonus=true）；满窗 ≥2026-10-10 17:40 CST  
2. **另派**：TrMenu 条件求值备选（js / 非 check-papi 路径 / 其它语法）——勿在本复测号继续改菜单  
3. 本号 **FAIL 不代替** 关观察签字；未达 R 级关 `set_bonus` 阈值

---

*D335 复测 · tip 菜单 `3f827bd1` · jar `985942be` · 测号 D335Retest · 证据 `/workspace/tmp/d335-armor-sa-sb-retest/` · 未改开关。*
