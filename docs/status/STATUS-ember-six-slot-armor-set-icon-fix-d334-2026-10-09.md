# 余烬 · D334 四件套三态 icon 条件修复（菜单-only · 2026-10-09）

- **性质：观察期薄修 · TrMenu only · 未动 jar / set_bonus / bv / 玩法数值**
- 上游 FAIL：[`STATUS-ember-six-slot-armor-gear-honesty-spot-d329-2026-10-09.md`](STATUS-ember-six-slot-armor-gear-honesty-spot-d329-2026-10-09.md) @ `ac3477fa`（H1）
- 上游施工：[`STATUS-ember-six-slot-armor-gear-honesty-d329-2026-10-09.md`](STATUS-ember-six-slot-armor-gear-honesty-d329-2026-10-09.md)
- 现态目标：enabled/migrate/set_bonus 全 true · bv62（本号未改）
- 执行：2026-10-09 ~19:46 CST
- **结论：条件已改并用 `trmenu reload` 载入；请测试复测 H1（装备页 M + 护甲页 S 三态 icon）**

## 人话

PAPI 里 `armor_set_active` / `armor_set_busy` 已经是 `1`，菜单 lore 里的 `%corerpg_p1_armor_set%` 也能正确显示「受伤 −3% / 需同族…」，但 TrMenu 的 `check papi %…_set_active|busy% == 1` 两边都不命中，一直落到「先让刃与护符同族」。本号不改 jar，改成对**已验证能显示的** `%corerpg_p1_armor_set%` 做字符串分支。

## 根因（与 D329 抽验一致）

| 项 | 证据 |
|----|------|
| `/papi parse` active/busy 正确 | `v3-repro.json`：active=`1` busy=`0`；busy 态同理 |
| lore 行 `%corerpg_p1_armor_set%` 正确 | 已激活：`焚烬族 护甲 4/2 · 受伤 −3%`；进行中：`… · 需同族掉落阶 T2+` |
| `check papi %…_set_active% == 1` / `_set_busy%` 不命中 | 装备入口 + 护甲页 S 均停在 `armor_on` 支 |
| 疑点 | 同前缀占位 `%corerpg_p1_armor_set%`（长文案）与 `_active`/`_busy` 在 TrMenu 条件求值冲突，或条件解析对短标识异常 |

## 改法（零 jar）

| 文件 | 变更 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_armor.yml` S 格 | active / busy 条件改 contains |
| `plugins/TrMenu/menus/ember_p1_gear.yml` M 格 | 同上 |

最终条件原文：

```yaml
# priority：active > busy > on（未改）
- condition: 'check papi %corerpg_p1_armor_set% contains 受伤'   # 已激活（EmberSixPapi：… · 受伤 −3%）
- condition: 'check papi %corerpg_p1_armor_set% contains 需同族' # 进行中（… · 需同族掉落阶 T2+）
- condition: 'check papi %corerpg_p1_armor_on% == 1'           # 未激活（文案仍「先让刃与护符同族」）
```

- 保留各支原有 lore（激活仍有静态 `§a受伤 −3%`；busy/未激活不谎称 −3%）
- actions 不变（装备页仍 `menu: ember_p1_armor`；护甲页仍 `corerpg p1 armor set`）
- 未采用 `_set_active% == 1` 旧写法；若线上 `contains` 仍不命中，备选见委派（`check %…% contains` / js indexOf / 短别名需插件——后者先报告勿自改 jar）

## Reload

- `scripts/console.sh play "trmenu reload"` → **`[TrMenu] 良好 | 70 个菜单已加载 (87 ms)`**（~19:46:16 CST）
- 改文件时 watcher 亦已自动：`ember_p1_armor.yml` / `ember_p1_gear.yml`
- 已知噪声：`NoSuchMethodError: Player.updateCommands()`（1.12 既有，与本改无关；菜单仍载入成功）
- **未**关 `set_bonus`；**未**改 bv / jar

## 静态确认

- 两文件无 `_set_active% == 1` / `_set_busy% == 1`
- 两文件均有 `contains 受伤` / `contains 需同族`

## 请测试

复测 D329 H1（及 H2 静态 −3% 行是否随 active icon 出现）：

1. 已激活 → 装备入口 / 护甲页 S 应为「已激活」支（名或语义），非「先让刃与护符同族」
2. 进行中 → 「进行中」支
3. 未齐套 → 「未激活 / 先让刃与护符同族」
4. 开关 / bv 全程未动

证据目录可续用 `/workspace/tmp/d329-armor-gear-honesty-spot/` 或另开 `d334-*`。

## 红线

- 观察期；满窗仍须 ≥2026-10-10 17:40 CST
- 本号不关观察、不关 `set_bonus`

---

*D334 菜单-only · 请测试复测 H1 · 未动 set_bonus/bv/jar。*
