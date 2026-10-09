# 状态 · D354：装备页「成套进度」Stage2 四件套叙事诚实（批 A·M · 含软化 · TrMenu 落地）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-gear-progress-honesty-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-gear-progress-honesty-need-design-2026-10-09.md) @ `f1887dd2` · DESIGN [`DESIGN-ember-six-slot-gear-progress-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-gear-progress-honesty-2026-10-09.md)  
**裁决：** **已批 A · 批 M · 含软化 · D354** · 总控授权同号 TrMenu 施工（显示 only）  
**版本：** **docs + `ember_p1_gear.yml` only** · jar / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / adventure / hub / help / set / armor / 同页 `S`/`M` / CoreRpg live yml **未动**

## 人话

装备页「成套进度」现在和同页套装格 / 冒险进度一样，能看到护甲四件套态（只读 PAPI）。「主线的终点」改成了「刃·护符终点」，避免读成否认四件套。没有静态写「受伤 −3%」。同页套装星 / 护甲入口 / 冒险页都没动。数值和观察开关都没动；观察不关。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_gear.yml` | 图标 `P`：`set_progress` 后 +1 行 `%corerpg_p1_armor_set%`；「主线的终点」→「刃·护符终点」 |
| DESIGN / tip / backlog | 批 A·M · **含软化** · tip 旁注已关 · `B-six-slot-gear-progress-honesty` → 已批 A · 已施工 |
| jar / CoreRpg yml / bv / set_bonus / `S` / `M` / adventure / hub / help / set | **未动** |

## 文案摘要

| 位置 | 内容 |
|------|------|
| gear `P`（保留） | name `§6成套进度` · `%corerpg_p1_set_progress%` · T3+10 / 成色卓越 / 追极品 / 深渊称号句 |
| gear `P`（新增） | `§8护甲四件套：§f%corerpg_p1_armor_set%` |
| gear `P`（软化） | `§8刃·护符终点 = 同族 T3 两件 +9（觉醒 III）`（原「主线的终点」） |
| 静态「受伤 −3%」 | **无**（−3% 只随 PAPI 已激活分支） |
| 同页 `S` / `M` / adventure | **未改** |

## 验收（静态）

| # | 项 | 结果 |
|---|----|------|
| P1 | gear `P` lore 有 `%corerpg_p1_armor_set%`；仍有 `set_progress`；终点句已软化 | `rg` · PASS |
| P3 | 无静态「受伤 −3%」谎称行 | 对照 · PASS |
| P4 | ×0.97 / set_bonus / jar / bv / CoreRpg live yml 本号未改；未误改 `S`/`M`/adventure | 本号未触 · PASS |
| YAML | 追加位在 `set_progress` 后、空行与终点句前 | PASS |

## 热重载

已用 `scripts/console.sh play "trmenu reload"` 执行；日志：`良好 | 70 个菜单已加载 (73 ms)`（约 21:39 CST）。玩家重开装备页「成套进度」即可见新行。**未**执行 `/corerpg reload`。

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · K3 施工 · 关观察 · CoreRpg live yml · 同页套装格 `S` · 护甲入口 `M` · adventure / hub / help / set / armor · 样本 R / Pack6 / 天赋 / 灰印 · 主仓切分支

## 总控旁注

**≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）。不抢 K3。薄抽 P2（打开装备页成套进度可见）交测试另号（≠ D345/D346 薄抽号）。

---

*D354 批 A·M · **含软化** · tip `f1887dd2` · 显示 only · ≠关观察 · 观察期薄窗。*
