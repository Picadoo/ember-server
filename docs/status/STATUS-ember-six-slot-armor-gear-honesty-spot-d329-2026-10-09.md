# 余烬 · D329 装备页护甲入口诚实 · 线上薄抽验（余烬-测试 · 2026-10-09）

- **性质：观察期薄抽验 · 显示 only · 未改菜单 / 未动开关 / 未关观察**
- 上游施工：[`STATUS-ember-six-slot-armor-gear-honesty-d329-2026-10-09.md`](STATUS-ember-six-slot-armor-gear-honesty-d329-2026-10-09.md) @ `7f1b165e`
- DESIGN：[`DESIGN-ember-six-slot-armor-gear-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-armor-gear-honesty-2026-10-09.md) §3
- 现态：bv**62** · `enabled=true` · `migrate=true` · `set_bonus=true` · play PID **520664**（全程未变）
- 测号：`D329Spot`（经代理 25565）；**未动真人档**；login/proxy/MariaDB 未动
- 证据：`/workspace/tmp/d329-armor-gear-honesty-spot/` + `/workspace/tmp/d329-armor-gear-honesty-spot.tgz`
- 执行：2026-10-09 19:35–19:42 CST
- **日历：** 观察起点 2026-10-08 17:40 CST；**满窗仍须 ≥2026-10-10 17:40 CST**
- **结论：FAIL（H1 三态 icons 未切换）。H2 内容诚实部分过 / H3 / 点击进护甲页 / 开关未动过。**
- **建议：维持观察；本号不改菜单、不关 `set_bonus`；条件分支问题另派（护甲页 S 同步复现，疑既有）。**

## 人话

装备页护甲入口点得进护甲页，也没有「后续开放」。进行中时不会写出 −3%。已激活时，套装 PAPI 那一行能看到「受伤 −3%」，但 **已激活 / 进行中的条件图标没切上**——入口一直停在「先让刃与护符同族」那一支；护甲页「四件套」格同样停在「未激活」。PAPI `armor_set_active=1` / `busy=1` 本身是对的，是 TrMenu `check papi … == 1` 没选中对应 icon。

## 验收表

| # | 项 | 结果 | 证据 |
|---|----|------|------|
| **H1** | 装备页护甲入口三态与护甲页 S 语义一致（条件 icons） | **FAIL** | 已激活时 PAPI `active=1`，装备入口仍 `armor_on` lore（「先让刃与护符同族」）；护甲页 S 名仍「四件套 · 未激活」。进行中 `busy=1` 同病。三态 icons 未切换。`v3-repro.json` / `v2-active.json` / `v2-busy.json` |
| **H2** | 已激活可见 −3%；进行中/未激活不谎称 −3% | **部分 PASS** | 已激活：`%corerpg_p1_armor_set%` 行含「受伤 −3%」（静态 `§a受伤 −3%` 行因 active icon 未命中而未出现）。进行中 lore **无** −3%。`v2-busy.json` / `v2-active.json` / `v3-repro.json` |
| **H3** | 无「后续开放」 | **PASS** | 静态 `ember_p1_gear.yml` + 三态 dump 均无。`00-static.json` |
| **H4a** | 点开仍进护甲页 | **PASS** | 装备页点「护甲」→ title `余烬 · 护甲`。`v2-click-armor.json` / `click-armor-page.json` |
| **H4b** | 开关 / bv / play 未动 | **PASS** | enabled/migrate/set_bonus 全 true · bv=62 · play **520664**。`99-results-v2.json` |

## 静态扫（施工稿复核）

| 项 | 结果 |
|----|------|
| `−3%` 仅出现在 `armor_set_active` 分支 | PASS（`ember_p1_gear.yml` L351 一处） |
| 全文无「后续开放」 | PASS |
| 三条件 actions 仍 `menu: ember_p1_armor` | PASS |

## 手法

- mineflayer `D329Spot` + `lib/proxy-login` / `lib/console`；`/trmenu open ember_p1_gear|ember_p1_armor` dump
- 态：进行中（焚烬刃+护符 + 1 靴）/ 已激活（≥2 同族 T2+）/ 对照未齐套
- **优先拆件与菜单换装**；**未**关 `set_bonus`；**未**改 yml / jar / bv
- 收尾：`clear` + `deop`；`list` 0

## 根因备忘（不施工）

- `/papi parse me %corerpg_p1_armor_set_active%` → `1` 时，TrMenu 条件 `check papi %corerpg_p1_armor_set_active% == 1` 仍不命中（busy 同理）；`armor_on == 1` 可命中
- **护甲页 S 与装备页 M 同步失败** → 非 D329 菜单独有；疑 TrMenu 条件求值 / 标识符问题（另号）
- 本抽验红线：**不改菜单、不关开关**

## 建议

1. **维持观察**（bv62 / set_bonus=true）；满窗 ≥2026-10-10 17:40 CST 后再议关观察  
2. **另派**：调查 TrMenu `check papi %corerpg_p1_armor_set_active|busy% == 1` 为何不命中（护甲页+装备页）  
3. 本号 **FAIL 不代替** 关观察签字；异常未达 R 级关 `set_bonus` 阈值

---

*D329 薄抽验 · 测号 D329Spot · 证据 `/workspace/tmp/d329-armor-gear-honesty-spot/` · 未改开关。*
