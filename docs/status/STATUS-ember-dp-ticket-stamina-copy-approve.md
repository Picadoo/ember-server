# STATUS · B2.10 DP ticket→stamina copy approval A

| Field | Value |
|---|---|
| Date | 2026-09-28 23:36 Asia/Shanghai |
| Role | 余烬-总控 |
| Decision | **APPROVED A** |
| Design tip | `f2eb738` · `docs/design/design-ember-dp-ticket-stamina-copy.md` (verified on `main` / `origin/main`) |
| Next | **余烬-插件 · priority true · 待插件施工** |
| Scope | DP player-visible messages #1–#9 only |
| Excluded | Q1/Q2 and calamity OP #6; no DP YAML changed in this approval |

## Conclusion

Approved **A** for B2.10: replace only the nine player-visible DP message bodies in the design table. This approval changes documentation only; the plugin role performs the YAML replacement and reload separately.

**Hard boundary:** do not alter cost, gate-condition text, loot, TrMenu, quest, calamity, counts, levels, or any other DP logic. Historical `票` in comments may remain.

## A scope — exact #1–#9 replacements

| # | File | Current message body | Approved message body |
|---:|---|---|---|
| 1 | `EmberDaily/task/timeout.yml` | `§c挑战超时，本局失败（日票不返还）` | `§c挑战超时，本局失败（体力不返还）` |
| 2 | `EmberWeekly/task/timeout.yml` | `§c周常超时失败（周票不返还）` | `§c周常超时失败（体力不返还）` |
| 3 | `EmberAbyss/option.yml` 撤离 | `§8需要撤离：打开菜单 → 深渊 → 撤离 · 按最高层结算 · 票不退` | `§8需要撤离：打开菜单 → 深渊 → 撤离 · 按最高层结算 · 体力不退` |
| 4 | `EmberAbyss/option.yml` 结算 | `§5深渊结算：按本局最高层发箱（票不退）` | `§5深渊结算：按本局最高层发箱（体力不退）` |
| 5 | `EmberAbyss/task/timeout.yml` | `§c深渊合拢。强制结算最高层（深渊票不返还）` | `§c深渊合拢。强制结算最高层（体力不返还）` |
| 6 | `EmberRaid/option.yml` 开场 | `§8已扣除余烬团本票 ×1` | `§8已消耗体力 ×1` |
| 7 | `EmberRaid/option.yml` 通关 | `§8同周再通不重复发戒（团票每周 1）· T3 不保底` | `§8同周再通不重复发戒（周首通一次）· T3 不保底` |
| 8 | `EmberRaid/task/timeout.yml` | `§c团本超时失败（团本票不返还）` | `§c团本超时失败（体力不返还）` |
| 9 | `EmberEliteWeekly/task/timeout.yml` | `§c试炼失败。§7票已扣，下周再来。` | `§c试炼失败。§7体力已扣，下周再来。` |

The approved body is the complete `text=` value after the existing `$message{type=text;text=...}` wrapper; preserve the wrapper and surrounding actions.

## Handoff drafts (not sent)

### 余烬-插件 — priority true

请按 `f2eb738` / `docs/status/STATUS-ember-dp-ticket-stamina-copy-approve.md` 施工 B2.10 方案 A：仅替换 DP 玩家可见 message 的 #1–#9，保留现有 `$message{type=text;text=...}` 包装和其它 action。完整替换如下：

1. `EmberDaily/task/timeout.yml`: `§c挑战超时，本局失败（日票不返还）` → `§c挑战超时，本局失败（体力不返还）`
2. `EmberWeekly/task/timeout.yml`: `§c周常超时失败（周票不返还）` → `§c周常超时失败（体力不返还）`
3. `EmberAbyss/option.yml` 撤离: `§8需要撤离：打开菜单 → 深渊 → 撤离 · 按最高层结算 · 票不退` → `§8需要撤离：打开菜单 → 深渊 → 撤离 · 按最高层结算 · 体力不退`
4. `EmberAbyss/option.yml` 结算: `§5深渊结算：按本局最高层发箱（票不退）` → `§5深渊结算：按本局最高层发箱（体力不退）`
5. `EmberAbyss/task/timeout.yml`: `§c深渊合拢。强制结算最高层（深渊票不返还）` → `§c深渊合拢。强制结算最高层（体力不返还）`
6. `EmberRaid/option.yml` 开场: `§8已扣除余烬团本票 ×1` → `§8已消耗体力 ×1`
7. `EmberRaid/option.yml` 通关: `§8同周再通不重复发戒（团票每周 1）· T3 不保底` → `§8同周再通不重复发戒（周首通一次）· T3 不保底`
8. `EmberRaid/task/timeout.yml`: `§c团本超时失败（团本票不返还）` → `§c团本超时失败（体力不返还）`
9. `EmberEliteWeekly/task/timeout.yml`: `§c试炼失败。§7票已扣，下周再来。` → `§c试炼失败。§7体力已扣，下周再来。`

禁改 cost、gate-condition text、loot、TrMenu、quest、calamity、人数/等级、其它 logic；注释里的 `票` 可留。完成后回报施工 tip 与 reload/静态检查结果。

### 余烬-策划 — priority false FYI

FYI：B2.10 DP 局内/超时「票」→体力已批准 **A**（design tip `f2eb738`）。范围仅 DP 玩家可见 message #1–#9；Q1/Q2 与 calamity OP #6 不在本轮，待插件施工。设计表/批准 STATUS 已落盘；未改 DP YAML。
