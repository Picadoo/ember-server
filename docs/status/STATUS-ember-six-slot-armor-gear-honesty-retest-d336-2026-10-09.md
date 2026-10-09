# 余烬 · D336 装备页护甲入口诚实 · 复测（priority 倒置 · 2026-10-09）

- **性质：观察期复测 · 显示 only · 未改菜单 / 未动开关 / 未关观察**
- 上游 FAIL：[`STATUS-ember-six-slot-armor-gear-honesty-retest-d335-2026-10-09.md`](STATUS-ember-six-slot-armor-gear-honesty-retest-d335-2026-10-09.md) @ `038f42f8`（sa/sb PAPI 对，被 on 压过）
- 修复 tip：[`STATUS-ember-six-slot-armor-set-icon-priority-d336-2026-10-09.md`](STATUS-ember-six-slot-armor-set-icon-priority-d336-2026-10-09.md) @ **`6ed4ca8d`**
  （sa=1 / sb=2 / on=10；stash=3；`trmenu reload` 已载入）
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **580488** · jar **`1.65.101-d335.local`** sha `2bd11b90…`（本号菜单 only · jar 未动）
- 测号：`D336Retest` + 补采 `D336Inact`（经代理 25565）；**未动真人档**
- 证据：`/workspace/tmp/d336-priority-retest/` + `/workspace/tmp/d336-priority-retest.tgz`
- 执行：2026-10-09 20:06–20:09 CST
- **日历：** 观察起点 2026-10-08 17:40 CST；**满窗仍须 ≥2026-10-10 17:40 CST**
- **结论：PASS（H1 三态 icons 切换正确；H2 已激活静态 §a受伤 −3% 出现；进行中/未激活不谎称 −3%；H3/H4 过）。**

## 人话

D336 把三态 priority 倒过来（数字小优先）：`sa=1` 压过 `on=10`，`sb=2` 也压过 `on`。进服后装备页「护甲」入口与护甲页「四件套」格终于跟 PAPI 对齐：已激活出「已激活」+ 静态 `§a受伤 −3%`；进行中出「进行中」且不谎称 −3%；刃/护符不同族时出「未激活 / 先让刃与护符同族」。点入口仍进护甲页。开关全程未动。前次 FAIL 根因（双命中时 on 压过 sa/sb）坐实已消。

## 验收表

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| **H1** | 三态 icons 与护甲页 S 一致（已激活/进行中/未激活） | **PASS** | busy→`busy_icon`；active→`active_icon`；pure inactive→`armor_on_icon`。装备页 M 与护甲页 S 分支对齐。`v2-busy.json` / `v2-active.json` / `v2-inactive-pure.json` / `99-results.json` |
| **H2** | 已激活出静态 `§a受伤 −3%`；进行中/未激活不谎称 | **PASS** | 已激活 gear+armor 均见静态行 `§a受伤 −3%`（`static:true`）。进行中/未激活 lore **无** −3% 谎称。`v2-active.json` / `v2-busy.json` / `v2-inactive-pure.json` |
| **H3** | 无「后续开放」 | **PASS** | 静态 yml + 三态 dump。`00-static.json` |
| **H4a** | 点开仍进护甲页 | **PASS** | title `余烬 · 护甲`。`v2-click-armor.json` |
| **H4b** | 开关 / bv / play 本号未改 | **PASS** | enabled/migrate/set_bonus 全 true · bv=62 · play **580488**。`00-precheck.json` / `99-results.json` |

## 静态扫（tip 核对）

| 项 | 结果 |
|----|------|
| gear M：sa=1 / sb=2 / stash=3 / on=10 | PASS @ `6ed4ca8d` |
| armor S：sa=1 / sb=2 / on=10 | PASS |
| 条件仍为 `check papi … == 1`（未改 contains） | PASS |
| 全文无「后续开放」 | PASS |
| jar 仍 `1.65.101-d335.local` · tip `6ed4ca8d` | PASS |

## PAPI 对照（bot `/papi parse me`）

| 态 | set | active | busy | **sa** | **sb** | on | 菜单分支 |
|----|-----|--------|------|--------|--------|-----|----------|
| 进行中 1/2 | `…1/2 · 需同族…` | 0 | 1 | **0** | **1** | 1 | **busy_icon**（应 busy） |
| 已激活 2/2 | `…2/2 · 受伤 −3%` | 1 | 0 | **1** | **0** | 1 | **active_icon**（应 active） |
| 未激活（异族护符） | `先让刃与护符同族` | 0 | 0 | **0** | **0** | 1 | **armor_on_icon**（应未激活） |

→ **双命中时 sa/sb 已压过 on；单纯 on=1 仍落未激活。**

## 手法

- mineflayer `D336Retest` + 补采 `D336Inact`；`lib/proxy-login` / `lib/console`；`/trmenu open ember_p1_gear|ember_p1_armor` dump
- 态：进行中（焚烬刃+护符 + 1 靴）/ 已激活（≥2 同族 T2+）/ 未激活（烬爆护符 + 焚烬刃错配 + 1 盔）
- 主跑 inactive 曾因护符未选上落成 busy；补采 `v2-inactive-pure.json` 坐实 armor_on
- **优先拆件与菜单换装**；**未**关 `set_bonus`；**未**改 yml / jar / bv / 菜单
- 收尾：`clear` + `deop`；`list` 0

## 建议

1. **维持观察**（bv62 / set_bonus=true）；满窗 ≥2026-10-10 17:40 CST  
2. D329/D334/D335 显示债本号 **PASS** 闭环；观察期绿证据可记一笔  
3. 本号 **PASS 不代替** 关观察签字；未达 R 级关 `set_bonus` 阈值

---

*D336 复测 · tip `6ed4ca8d` · jar `1.65.101-d335.local` · 测号 D336Retest / D336Inact · 证据 `/workspace/tmp/d336-priority-retest/` · 未改开关。*
