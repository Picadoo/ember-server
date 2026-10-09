# 余烬 · D329 装备页护甲入口诚实 · 复测（D334 tip 后 · 2026-10-09）

- **性质：观察期复测 · 显示 only · 未改菜单 / 未动开关 / 未关观察**
- 上游 FAIL：[`STATUS-ember-six-slot-armor-gear-honesty-spot-d329-2026-10-09.md`](STATUS-ember-six-slot-armor-gear-honesty-spot-d329-2026-10-09.md) @ `ac3477fa`
- 修复 tip：[`STATUS-ember-six-slot-armor-set-icon-fix-d334-2026-10-09.md`](STATUS-ember-six-slot-armor-set-icon-fix-d334-2026-10-09.md) @ **`be131486`**
  （条件改为 `check papi %corerpg_p1_armor_set% contains 受伤|需同族`；`trmenu reload` 已载入）
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **568465**（D333 jar 热换后；本号未改 jar/开关）
- 测号：`D329Retest`（经代理 25565）；**未动真人档**
- 证据：`/workspace/tmp/d329-armor-gear-honesty-retest/` + `/workspace/tmp/d329-armor-gear-honesty-retest.tgz`
- 执行：2026-10-09 19:50–19:52 CST（此前一跑被 D333 jar 短重启打断，已归档 `interrupted-by-d333-jar/`）
- **日历：** 观察起点 2026-10-08 17:40 CST；**满窗仍须 ≥2026-10-10 17:40 CST**
- **结论：FAIL（H1 三态 icons 仍未切换；H2 静态 −3% 行未出现）。H2 内容诚实部分过 / H3 / 点击进护甲页 / 开关未动过。**
- **建议：维持观察；本号不改菜单；contains 仍不命中 → 另派（备选语法 / js / 短别名需插件则先报勿自改 jar）。**

## 人话

D334 把 `armor_set_active|busy == 1` 换成了对 `%corerpg_p1_armor_set%` 的 `contains 受伤|需同族`，菜单文件与 runtime 都对上了，也 reload 了。但进服后装备页入口和护甲页「四件套」格**仍停在「未激活 / 先让刃与护符同族」**。PAPI 文案本身是对的（已激活能看到「受伤 −3%」那一行），静态 `§a受伤 −3%` 分支行因为 icon 没命中所以不会出现。进行中不会谎称 −3%。点「护甲」仍进护甲页。开关全程未动。

## 验收表

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| **H1** | 三态 icons 与护甲页 S 一致（已激活/进行中/未激活） | **FAIL** | 三态 gear/armor 分支均为 `armor_on_icon`；护甲页名均「四件套 · 未激活」。已激活时 PAPI `active=1` 且 set=`…受伤 −3%`，仍落 armor_on。`v2-active.json` / `v2-busy.json` / `99-results-v2.json` |
| **H2** | 已激活见 −3% 静态行；进行中/未激活不谎称 | **部分 PASS / 静态行 FAIL** | PAPI 行在已激活可见「受伤 −3%」；**静态 `§a受伤 −3%` 未出现**（`static:false`）。进行中 lore **无** −3% 谎称。`v2-active.json` / `v2-busy.json` |
| **H3** | 无「后续开放」 | **PASS** | 静态 yml + 三态 dump。`00-static.json` |
| **H4a** | 点开仍进护甲页 | **PASS** | title `余烬 · 护甲`。`v2-click-armor.json` |
| **H4b** | 开关 / bv / play 本号未改 | **PASS** | enabled/migrate/set_bonus 全 true · bv=62 · play **568465**（D333 热换后 PID，非本号）。`00-precheck.json` / `99-results-v2.json` |

## 静态扫（tip 核对）

| 项 | 结果 |
|----|------|
| gear/armor 条件为 `contains 受伤` / `contains 需同族` | PASS @ `be131486` |
| 无 `_set_active% == 1` / `_set_busy% == 1` | PASS |
| 全文无「后续开放」 | PASS |
| tip 后 `trmenu reload`（含 D333 重启后再 reload） | PASS · 70 菜单 |

## 手法

- mineflayer `D329Retest` + `lib/proxy-login` / `lib/console`；`/trmenu open ember_p1_gear|ember_p1_armor` dump
- 态：进行中（焚烬刃+护符 + 1 靴）/ 已激活（≥2 同族 T2+）/ 对照
- **优先拆件与菜单换装**；**未**关 `set_bonus`；**未**改 yml / jar / bv
- 收尾：`clear` + `deop`；`list` 0

## 根因备忘（不施工）

- lore 中 `%corerpg_p1_armor_set%` **能展开**为含「受伤 / 需同族」的文案
- 条件 `check papi %corerpg_p1_armor_set% contains 受伤|需同族` **仍不命中**（与前次 `== 1` 同病：条件求值层）
- 装备页 M 与护甲页 S **同步失败** → 非单页问题
- D334 STATUS 已列备选：`check %…% contains` / js indexOf / 短别名需插件——**本号不改菜单、不改 jar**

## 建议

1. **维持观察**（bv62 / set_bonus=true）；满窗 ≥2026-10-10 17:40 CST  
2. **另派**：TrMenu 条件语法备选验证（勿在本复测号继续改菜单）  
3. 本号 **FAIL 不代替** 关观察签字；未达 R 级关 `set_bonus` 阈值

---

*D329 复测 · tip `be131486` · 测号 D329Retest · 证据 `/workspace/tmp/d329-armor-gear-honesty-retest/` · 未改开关。*
