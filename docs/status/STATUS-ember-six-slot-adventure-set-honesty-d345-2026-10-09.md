# 状态 · D345：冒险页「我的进度」Stage2 四件套叙事诚实（批 A·M · TrMenu 落地）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-adventure-set-honesty-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-adventure-set-honesty-need-design-2026-10-09.md) @ `0d47e761` · DESIGN [`DESIGN-ember-six-slot-adventure-set-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-adventure-set-honesty-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D345** · 总控授权同号 TrMenu 施工（显示 only）  
**版本：** **docs + `ember_p1_adventure.yml` only** · jar / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / hub / help / set / gear / armor / CoreRpg live yml **未动**

## 人话

冒险页「我的进度」现在和主菜单装备格一样，能看到护甲四件套态（只读 PAPI）。族觉醒 / 刃护符套装进度行都保留；没有静态写「受伤 −3%」。数值和观察开关都没动；观察不关。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_adventure.yml` | 图标 `K`「我的进度」：在 `set_progress` 后、`pending` 前插入 `§8护甲四件套：§f%corerpg_p1_armor_set%`（对齐 hub D339） |
| DESIGN / tip / backlog | 批 A·M · tip 旁注已关 · `B-six-slot-adventure-set-honesty` → 已批 A · 已施工 |
| jar / CoreRpg yml / bv / set_bonus / hub / help / set / gear / armor | **未动** |

## 文案摘要

| 位置 | 内容 |
|------|------|
| adventure `K`（保留） | `%awaken%` / `awaken_next` / `set_progress` |
| adventure `K`（新增） | `§8护甲四件套：§f%corerpg_p1_armor_set%` |
| 静态「受伤 −3%」 | **无**（−3% 只随 PAPI 已激活分支） |

## 验收（静态）

| # | 项 | 结果 |
|---|----|------|
| A1 | adventure `K` lore 有 `%corerpg_p1_armor_set%`；仍有 awaken / set_progress | `rg` · PASS |
| A3 | 无静态「受伤 −3%」谎称行 | 对照 · PASS |
| A4 | ×0.97 / set_bonus / jar / bv / CoreRpg live yml 本号未改 | 本号未触 · PASS |
| YAML | lore 插入位正确（set_progress → armor_set → pending） | PASS |

## 热重载

已用 `scripts/console.sh play "trmenu reload"` 执行；日志：`良好 | 70 个菜单已加载 (115 ms)`（约 21:12 CST）。玩家重开冒险页「我的进度」即可见新行。**未**执行 `/corerpg reload`。

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · K3 施工 · 关观察 · CoreRpg live yml · hub / help / set / gear / armor · 样本 R / Pack6 / 天赋 / 灰印 · 主仓切分支

## 总控旁注

**≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）。不抢 K3。薄抽 A2（打开冒险页可见）交测试另号。

---

*D345 批 A·M · tip `0d47e761` · 显示 only · ≠关观察 · 观察期薄窗。*
