# 状态 · D329：六槽装备页护甲入口诚实（批 A·M · TrMenu 落地）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-armor-gear-honesty-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-armor-gear-honesty-need-design-2026-10-09.md) @ `6d8b6565` · DESIGN [`DESIGN-ember-six-slot-armor-gear-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-armor-gear-honesty-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D329** · 总控授权同号 TrMenu 施工（显示 only）  
**版本：** **docs + `ember_p1_gear.yml` only** · jar / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / `ember_p1_armor.yml` **未动**

## 人话

装备页护甲入口现在和护甲页一样，能看出四件套是已激活 / 进行中 / 未激活；已激活才写「受伤 −3%」，不会在未凑齐时谎称减伤。点进去仍是护甲页，数值和开关都没动。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_gear.yml` | 护甲入口 `M`：拆 `armor_set_active` / `armor_set_busy` / `armor_on` 三条件 icons（priority 5/4/3）+ 保留 stash；lore 追加套装态；actions 仍 `menu: ember_p1_armor` |
| DESIGN / tip / backlog | 批 A·M · tip 旁注已关 · `B-six-slot-armor-gear-honesty` → 已批 A · 已施工 |
| jar / CoreRpg yml / bv / set_bonus | **未动** |

## lore 三态摘要

| 态 | 条件 | 追加行 |
|----|------|--------|
| 已激活 | `armor_set_active==1` | `%corerpg_p1_armor_set%` · `§a受伤 −3%` · `§7另 2 甲位可穿异族追成色` |
| 进行中 | `armor_set_busy==1` | `%corerpg_p1_armor_set%` · `§7还缺：任意同族掉落阶 T2+ 护甲` |
| 未激活 | `armor_on==1`（默认） | `%corerpg_p1_armor_set%` · `§8先让刃与护符同族` |

保留：四部位 PAPI、stash 行、F/成色精工说明、「打开护甲页」。**无「后续开放」。**

## 验收（静态）

| # | 项 | 结果 |
|---|----|------|
| H2 | 未激活/进行中不写 −3%；仅 active 写 | 条件分支 · PASS（静态） |
| H3 | 无「后续开放」于该入口 | `rg` · PASS |
| H4 | ×0.97 / set_bonus / bv / jar 未改 | 本号未触 · PASS |
| YAML | 可解析 | PASS |

## 热重载

已用 `scripts/console.sh play "trmenu reload"` 执行；日志：`良好 | 70 个菜单已加载 (124 ms)`（约 19:33 CST）。玩家重开装备页即可见三态。

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · K3 施工 · `ember_p1_armor.yml` 已过线三态 · 样本 R / Pack6 / 天赋 / 灰印

---

*D329 批 A·M · tip `6d8b6565` · 显示 only · 观察期薄窗。*
