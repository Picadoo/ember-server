# 状态 · D357：主菜单装备格导语 Stage2 护甲诚实（批 A·M · TrMenu 落地）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-hub-equip-blurb-honesty-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-hub-equip-blurb-honesty-need-design-2026-10-09.md) @ `4d497f65` · DESIGN [`DESIGN-ember-six-slot-hub-equip-blurb-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-hub-equip-blurb-honesty-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D357** · 总控授权同号 TrMenu 施工（显示 only）  
**版本：** **docs + `ember_hub.yml` only** · jar / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / CoreRpg live yml / gear / help / forge **未动**

## 人话

主菜单装备格导语现在写「套装、觉醒与护甲」，和同格下面的四件套态一致。觉醒/护甲 PAPI 行和点进去的动作都没动。数值和观察开关都没动；观察不关。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_hub.yml` | 装备格 `o` 导语：`套装与觉醒` → `套装、觉醒与护甲` |
| DESIGN / tip / backlog | 批 A·M · tip 旁注已关 · `B-six-slot-hub-equip-blurb-honesty` → 已批 A · 已施工 |
| `%corerpg_p1_awaken%` / `%corerpg_p1_armor_set%` / actions → gear | **未动** |
| jar / CoreRpg yml / bv / set_bonus | **未动** |

## 文案摘要

| 位置 | 内容 |
|------|------|
| hub `o` 导语（旧） | `§7主手刃、已选护符、实际属性、套装与觉醒` |
| hub `o` 导语（新） | `§7主手刃、已选护符、实际属性、套装、觉醒与护甲` |
| 同格突破/签名/awaken/armor_set/副手/actions | **保留不动** |

## 验收（静态）

| # | 项 | 结果 |
|---|----|------|
| U1 | hub `o` 导语含「护甲」；不再裸「套装与觉醒」独占 | `rg` · PASS |
| U2 | 同格 `%armor_set%` / awaken / actions 仍在 | PASS |
| U3 | 开关/bv 本号未改 | PASS |
| YAML | 仅替换导语一行 | PASS |

## 热重载

已用 `scripts/console.sh play "trmenu reload"` 执行；日志：`良好 | 70 个菜单已加载 (72 ms)`（约 21:51 CST）。玩家重开主菜单装备格即可见新导语。**未**执行 `/corerpg reload`。

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · K3 施工 · 关观察 · CoreRpg live yml · gear `S`/`P`/`f` · help · forge · hub 工坊 · adventure / set / armor · 样本 R / Pack6 / 天赋 / 灰印 · 主仓切分支

## 总控旁注

**≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）。不抢 K3。活窗薄抽（打开主菜单装备格可见）交测试另号。

---

*D357 批 A·M · tip `4d497f65` · 显示 only · ≠关观察 · 观察期薄窗。*
