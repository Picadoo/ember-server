# 状态 · D351：Stage2 次入口「三套装」叙事诚实（批 A·M · TrMenu 落地）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-secondary-set-copy-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-secondary-set-copy-need-design-2026-10-09.md) @ `570a99ed` · DESIGN [`DESIGN-ember-six-slot-secondary-set-copy-2026-10-09.md`](../design/DESIGN-ember-six-slot-secondary-set-copy-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D351** · 总控授权同号 TrMenu 施工（显示 only）  
**版本：** **docs + TrMenu only**（`ember_p1_adventure.yml` / `ember_hub.yml` / `ember_p1_codex.yml`）· jar / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / CoreRpg live yml / help 正文图标 / hub_legacy / gear / set / armor **未动**

## 人话

团本旁注不再写「三套装不变」，改成「战斗规则不变」并点明族觉醒/护甲四件套同主线；主菜单和图录进帮助的捷径也改成「套装（含四件套）」。数值和观察开关都没动；观察不关。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_adventure.yml` | 团本×3 lore：旧「战斗规则和三套装不变；…」→ 双行「战斗规则不变；…」+「族觉醒 / 护甲四件套规则同主线（见装备·帮助）」 |
| `plugins/TrMenu/menus/ember_hub.yml` | 帮助入口 `i`：`三套装` → `套装（含四件套）` |
| `plugins/TrMenu/menus/ember_p1_codex.yml` | 帮助捷径 `H`：同上 |
| DESIGN / tip / backlog | 批 A·M · tip 旁注已关 · `B-six-slot-secondary-set-copy` → 已批 A · 已施工 |
| jar / CoreRpg yml / bv / set_bonus / help 图标名 / hub_legacy | **未动** |

## 文案摘要

| 位置 | 内容 |
|------|------|
| adventure 团本×3（新） | `§7战斗规则不变；通关得专属称号与足迹（只做展示）` |
| adventure 团本×3（新） | `§8族觉醒 / 护甲四件套规则同主线（见装备·帮助）` |
| hub 帮助 `i` | `§7怎么玩 · 操作 · 套装（含四件套） · 变强 · 收益与体力` |
| codex 帮助 `H` | `§7怎么玩 / 操作 / 套装（含四件套）` |
| 静态「受伤 −3%」 | **无**（本债新增行未写 −3%） |
| 「三套装不变」现状句 | **无** |

## 验收（静态）

| # | 项 | 结果 |
|---|----|------|
| T1 | adventure：无「三套装不变」现状句；有战斗规则不变 + 四件套指针 | `rg` · PASS |
| T2 | hub `i` + codex `H`：捷径含「套装（含四件套）」 | `rg` · PASS |
| T3 | 本债新增行无静态「受伤 −3%」谎称 | 对照 · PASS |
| T4 | ×0.97 / set_bonus / jar / bv / CoreRpg live yml 本号未改 | 本号未触 · PASS |

## 热重载

已用 `scripts/console.sh play "trmenu reload"` 执行；日志：`良好 | 70 个菜单已加载 (67 ms)`（约 21:28 CST）。玩家重开冒险团本格 / 主菜单帮助 / 图录帮助即可见新文案。**未**执行 `/corerpg reload`。

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · K3 施工 · 关观察 · CoreRpg live yml · help 页「三套装」图标改名（后置 L）· hub_legacy · 主路径已收页（gear/set/armor/adventure 进度/hub 装备）· 样本 R / Pack6 / 天赋 / 灰印 · 主仓切分支

## 总控旁注

**≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）。不抢 K3。薄抽（测号打开团本/帮助捷径可见）交测试另号可选。

---

*D351 批 A·M · tip `570a99ed` · 显示 only · ≠关观察 · 观察期薄窗。*
