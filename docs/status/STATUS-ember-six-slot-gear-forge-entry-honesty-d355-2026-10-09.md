# 状态 · D355：装备页工坊入口 Stage2 护甲分解诚实（批 A·M · TrMenu 落地）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-gear-forge-entry-honesty-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-gear-forge-entry-honesty-need-design-2026-10-09.md) @ `8518e275` · DESIGN [`DESIGN-ember-six-slot-gear-forge-entry-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-gear-forge-entry-honesty-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D355** · 总控授权同号 TrMenu 施工（显示 only）  
**版本：** **docs + `ember_p1_gear.yml` only** · jar / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / forge 页内 / hub 工坊 / CoreRpg live yml / `/workspace/tmp/d354-*` **未动**

## 人话

装备页点工坊时，除了「手持刃或护符」外，现在也能看到「手持护甲：仅可分解（白板零头）；养成随护符」，与主菜单工坊口径一致。没有暗示甲可强化/升阶/精工。工坊页内和 hub 工坊都没动。数值和观察开关都没动；观察不关。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_gear.yml` | 图标 `f`：在「手持要处理的刃或护符再打开」后 +1 行甲仅分解（对齐 hub D339） |
| DESIGN / tip / backlog | 批 A·M · tip 旁注已关 · `B-six-slot-gear-forge-entry-honesty` → 已批 A · 已施工 |
| `ember_p1_forge.yml` / hub 工坊 / gear `S`/`P`/`M` / adventure | **未动** |
| jar / CoreRpg yml / bv / set_bonus / d354-spot | **未动** |

## 文案摘要

| 位置 | 内容 |
|------|------|
| gear `f`（保留） | name `§6工坊 · 强化 / 升阶 / 精工 / 成色 / 互换 / 分解` · `§7手持要处理的刃或护符再打开` · `§e➥ §f打开工坊` · `menu: ember_p1_forge` |
| gear `f`（新增） | `§8手持护甲：仅可分解（白板零头）；养成随护符` |
| forge 页内 / hub 工坊 | **未改**（D333 / D339 已收） |

## 验收（静态）

| # | 项 | 结果 |
|---|----|------|
| F1 | gear `f` lore 有护甲仅分解 / 白板零头；仍有刃护符行 | `rg` · PASS |
| F3 | 工坊页内 / hub 工坊本号未改 | 本号未触 · PASS |
| F4 | ×0.97 / set_bonus / jar / bv / CoreRpg live yml 本号未改；未抢 d354-spot | 本号未触 · PASS |
| YAML | 新行在刃护符行后、空行与打开工坊行前；actions 保留 | PASS |

## 热重载

已用 `scripts/console.sh play "trmenu reload"` 执行；日志：`良好 | 70 个菜单已加载 (92 ms)`（约 21:42 CST）。玩家重开装备页工坊格即可见新行。**未**执行 `/corerpg reload`。

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · K3 施工 · 关观察 · CoreRpg live yml · `ember_p1_forge.yml` · hub 工坊 · gear `S`/`P`/`M` · adventure / help / set / armor · `/workspace/tmp/d354-*` · 样本 R / Pack6 / 天赋 / 灰印 · 主仓切分支

## 总控旁注

**≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）。不抢 K3。薄抽 F2（打开装备页工坊格可见）交测试另号（≠ D354 薄抽号）。

---

*D355 批 A·M · tip `8518e275` · 显示 only · ≠关观察 · 观察期薄窗。*
