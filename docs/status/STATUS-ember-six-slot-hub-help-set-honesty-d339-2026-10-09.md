# 状态 · D339：主菜单/帮助页 Stage2 四件套叙事诚实（批 A·M · TrMenu 落地）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-hub-help-set-honesty-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-hub-help-set-honesty-need-design-2026-10-09.md) @ `027322ef` · DESIGN [`DESIGN-ember-six-slot-hub-help-set-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-hub-help-set-honesty-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D339** · 总控授权同号 TrMenu 施工（显示 only） · **D340 薄抽 PASS · 关清**  
**版本：** **docs + `ember_hub.yml` + `ember_help.yml` only** · jar / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / `ember_set` / `ember_p1_gear` / `ember_p1_armor` / `ember_hub_legacy` **未动**

## 人话

主菜单「装备」现在能看到护甲四件套态（PAPI 镜像，−3% 只随已激活文案出现）；工坊格补了「手持护甲仅可分解」。帮助「怎么玩 / 三套装」分清族觉醒与四件套受伤 −3% 两层，并指向套装/装备/护甲。数值和开关都没动；观察不关。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_hub.yml` | 装备格 `o`：保留 awaken；追加 `%corerpg_p1_armor_set%`（**无**静态未激活也 −3%）；工坊格 `p`：补「手持护甲：仅可分解（白板零头）；养成随护符」 |
| `plugins/TrMenu/menus/ember_help.yml` | L：刃+护符句改为族觉醒 + 四件套 −3%；S：追加四件套一行 + 指向套装/装备/护甲 |
| DESIGN / tip / backlog | 批 A·M · tip 旁注已关 · `B-six-slot-hub-help-set-honesty` → 已批 A · 已施工 |
| jar / CoreRpg yml / bv / set_bonus / ember_set / gear / armor / hub_legacy | **未动** |

## 文案摘要

| 位置 | 新内容要点 |
|------|------------|
| hub 装备 `o` | `§8护甲四件套：%corerpg_p1_armor_set%`（PAPI 已含 −3% 时不另静态写） |
| hub 工坊 `p` | `§8手持护甲：仅可分解（白板零头）；养成随护符` |
| help 怎么玩 `L` §74 | `刃+护符同族 = 族觉醒；再穿同族 T2+ 甲≥2 = 四件套（受伤 −3%）` |
| help 三套装 `S` | `另：护甲四件套…→ 受伤 −3%，不进面板 B/H` + `详情：主菜单 → 套装 / 装备 / 护甲` |

**无**「后续开放」。同袍 / 刃护符养成路径文案保留。

## 验收（静态）

| # | 项 | 结果 |
|---|----|------|
| H1 | hub/help 无「后续开放」；有四件套或 −3% 指针 | `rg` · PASS |
| H2 | hub 装备格可见 armor_set | 对照 · PASS |
| H3 | help L/S 两层叙事（觉醒 vs 四件套） | 对照 · PASS |
| H4 | ×0.97 / set_bonus / jar / bv / 禁触文件未改 | 本号未触 · PASS |
| YAML | 可解析 | PASS |

## 热重载

已用 `scripts/console.sh play "trmenu reload"` 执行；日志：`良好 | 70 个菜单已加载 (94 ms)`（约 20:46 CST）。玩家重开主菜单/帮助即可见新文案。

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · K3 施工 · 关观察 · `ember_set.yml` · `ember_p1_gear.yml` · `ember_p1_armor.yml` · `ember_hub_legacy.yml` · 样本 R / Pack6 / 天赋 / 灰印

## 总控旁注（2026-10-09 · D340）

**D339 薄抽 PASS · 关清**——线上活菜单 S0–S6 全绿（STATUS [`STATUS-ember-six-slot-d339-hub-help-spot-d340-2026-10-09.md`](STATUS-ember-six-slot-d339-hub-help-spot-d340-2026-10-09.md) @ `7d560c39`）。DESIGN §2.6 玩家面可见性已闭合。**不改** 本号文案 / 开关 / jar / bv。**≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）。不抢 K3。

---

*D339 批 A·M · tip `027322ef` · 显示 only · **D340 薄抽 PASS · 关清** · ≠关观察 · 观察期薄窗。*
