# 状态 · D331：套装页 Stage2 叙事诚实（批 A·M · TrMenu 落地）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-set-page-honesty-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-set-page-honesty-need-design-2026-10-09.md) @ `b9c03259` · DESIGN [`DESIGN-ember-six-slot-set-page-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-set-page-honesty-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D331** · 总控授权同号 TrMenu 施工（显示 only）  
**版本：** **docs + `ember_set.yml` only** · jar / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / D329 `ember_p1_gear.yml` / D330 gear-structure·OPS·staged **未动**

## 人话

套装页现在分两轨说清楚：上面仍是「余烬同袍＝团戒+刃」击杀回能（永不进 P1 B/H）；下面补上 P1 族觉醒 + 四件套受伤 −3%，并指向装备/护甲页。旧的「护符套装后续开放」「本期不改方块甲」已删。数值和开关都没动。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_set.yml` | Open tell：同袍诚实 + P1 套装短述 + 装备/护甲指针；图标 E lore/tell：删误导，改 P1 短指针；可选 `%corerpg_p1_awaken%` / `%corerpg_p1_armor_set%` 只读镜像 |
| DESIGN / tip / backlog | 批 A·M · tip 旁注已关 · `B-six-slot-set-page-honesty` → 已批 A · 已施工 |
| jar / CoreRpg yml / bv / set_bonus / D330 文件 / D329 gear | **未动** |

## 文案摘要

| 位置 | 新内容要点 |
|------|------------|
| Open tell | 【同袍】戒+刃回能 · 永不进 B/H · 【P1 套装】觉醒 + 四件套 −3% · 详情→装备/护甲 |
| 图标 E lore | 同袍回能保留；「P1 另有：刃+护符觉醒 · 护甲四件套（受伤 −3%）」+ 见装备/护甲；PAPI 两行镜像 |
| E tell | 删「护符套装后续开放」；改指向装备/护甲的 P1 短述 |

**无**「护符套装后续开放」「不改方块甲」「日后 4 件再扩」。同袍戒+刃说明与副手 O 保留。

## 验收（静态）

| # | 项 | 结果 |
|---|----|------|
| S1 | 页内无「护符套装后续开放」「不改方块甲」 | `rg` · PASS |
| S2 | 同袍（戒+刃）说明仍在且不进 B/H 声明保留 | 对照 · PASS |
| S3 | 出现 P1 觉醒 + 四件套 −3% 短述或入口指针 | 对照 · PASS |
| S4 | ×0.97 / set_bonus / jar / 同袍数值 / D330 文件未改 | 本号未触 · PASS |
| YAML | 可解析 | PASS |

## 热重载

已用 `scripts/console.sh play "trmenu reload"` 执行；日志：`良好 | 70 个菜单已加载 (107 ms)`（约 19:38 CST）。玩家重开套装页即可见新文案。

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · K3 施工 · D329 `ember_p1_gear.yml` · D330 gear-structure / OPS / staged · 同袍回能公式 · 样本 R / Pack6 / 天赋 / 灰印

---

*D331 批 A·M · tip `b9c03259` · 显示 only · 观察期薄窗。*
