# 状态 · D346：装备页套装格 Stage2 四件套叙事诚实（批 A·M · TrMenu 落地）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-gear-set-icon-honesty-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-gear-set-icon-honesty-need-design-2026-10-09.md) @ `05dfa135` · DESIGN [`DESIGN-ember-six-slot-gear-set-icon-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-gear-set-icon-honesty-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D346** · 总控授权同号 TrMenu 施工（显示 only）  
**版本：** **docs + `ember_p1_gear.yml` only** · jar / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / adventure / hub / help / set / armor / CoreRpg live yml **未动** · D345 薄抽证据 **未碰**

## 人话

装备页「套装」星现在和护甲入口 / 冒险进度一样，能看到护甲四件套态（只读 PAPI），并有一行指向护甲页。族被动、觉醒档、`%awaken%` 都保留；没有静态写「受伤 −3%」。护甲入口三态没动。数值和观察开关都没动；观察不关。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_gear.yml` | 图标 `S`：觉醒说明块末 +空行 + `%corerpg_p1_armor_set%` + 护甲页详情行 |
| DESIGN / tip / backlog | 批 A·M · tip 旁注已关 · `B-six-slot-gear-set-icon-honesty` → 已批 A · 已施工 |
| jar / CoreRpg yml / bv / set_bonus / armor `M` / adventure / hub / help / set | **未动** |
| `/workspace/tmp/d345-*` | **未碰** |

## 文案摘要

| 位置 | 内容 |
|------|------|
| gear `S`（保留） | name `%awaken%` · 族被动三行 · 觉醒 I/II/III · 「按较低那件算」 |
| gear `S`（新增） | `§8护甲四件套：§f%corerpg_p1_armor_set%` |
| gear `S`（新增） | `§8详情：护甲页 · 两件套已激活 + 同族掉落阶 T2+ 甲≥2` |
| 静态「受伤 −3%」 | **无**（−3% 只随 PAPI 已激活分支） |
| 护甲入口 `M` | **未改** |

## 验收（静态）

| # | 项 | 结果 |
|---|----|------|
| G1 | gear `S` lore 有 `%corerpg_p1_armor_set%`；仍有 awaken 与觉醒档 | `rg` · PASS |
| G3 | 无静态「受伤 −3%」谎称行 | 对照 · PASS |
| G4 | ×0.97 / set_bonus / jar / bv / CoreRpg live yml 本号未改；未抢 D345 证据 | 本号未触 · PASS |
| YAML | 追加位在觉醒块末、`N` 前 | PASS |

## 热重载

已用 `scripts/console.sh play "trmenu reload"` 执行；日志：`良好 | 70 个菜单已加载 (94 ms)`（约 21:15 CST）。玩家重开装备页套装格即可见新行。**未**执行 `/corerpg reload`。

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · K3 施工 · 关观察 · CoreRpg live yml · 护甲入口 `M` 三态 · adventure / hub / help / set / armor · D345 薄抽 · 样本 R / Pack6 / 天赋 / 灰印 · 主仓切分支

## 总控旁注

**≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）。不抢 K3。薄抽 G2（打开装备页套装格可见）交测试另号（≠ D345 adventure 薄抽号）。

---

*D346 批 A·M · tip `05dfa135` · 显示 only · ≠关观察 · 观察期薄窗。*
