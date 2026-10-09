# 状态 · D356：帮助「变强」分解行 Stage2 护甲零头诚实（批 A·M · TrMenu 落地）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-help-dismantle-honesty-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-help-dismantle-honesty-need-design-2026-10-09.md) @ `593d8792` · DESIGN [`DESIGN-ember-six-slot-help-dismantle-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-help-dismantle-honesty-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D356** · 总控授权同号 TrMenu 施工（显示 only）  
**版本：** **docs + `ember_help.yml` only** · jar / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / gear `f` / forge / hub 工坊 / CoreRpg live yml / `/workspace/tmp/d354-*` `/workspace/tmp/d355-*` **未动**

## 人话

帮助「变强」分解说明现在区分：刃/护符拆成胚料（精工·成色·升阶用）；甲只拆成白板零头，见工坊。没有暗示甲可拿去强化/升阶/精工。装备页工坊入口、工坊页内、主菜单工坊都没动。数值和观察开关都没动；观察不关。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_help.yml` | 图标「变强」分解行：由「多余的掉落件换胚料…」改为荐单行（刃/护符胚料 + 甲白板零头） |
| DESIGN / tip / backlog | 批 A·M · tip 旁注已关 · `B-six-slot-help-dismantle-honesty` → 已批 A · 已施工 |
| gear `f` / `ember_p1_forge.yml` / hub 工坊 | **未动** |
| jar / CoreRpg yml / bv / set_bonus / d354·d355-spot | **未动** |

## 文案摘要

| 位置 | 内容 |
|------|------|
| help「变强」分解行（旧） | `§f分解§7：多余的掉落件换胚料（精工 / 成色 / 升阶用）` |
| help「变强」分解行（新） | `§f分解§7：刃/护符→胚料（精工·成色·升阶用）；甲→白板零头（仅分解，见工坊）` |
| 同格强化/升阶/印记/互换/四条路/工坊指针/推荐打法 | **保留不动** |

## 验收（静态）

| # | 项 | 结果 |
|---|----|------|
| H1 | help「变强」lore 分解行含甲/白板零头；仍有刃护符胚料义 | `rg` · PASS |
| H2 | 同格其它行未误删 | 强化/升阶/印记/互换/四条路/工坊指针/推荐打法仍在 · PASS |
| H3 | 开关/bv；D354/D355 薄抽目录本号未改；未抢 spot | 本号未触 · PASS |
| YAML | 仅替换分解一行；actions 保留 | PASS |

## 热重载

已用 `scripts/console.sh play "trmenu reload"` 执行；日志：`良好 | 70 个菜单已加载 (94 ms)`（约 21:46 CST）。玩家重开帮助「变强」格即可见新行。**未**执行 `/corerpg reload`。

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · K3 施工 · 关观察 · CoreRpg live yml · gear `f` · `ember_p1_forge.yml` · hub 工坊 · gear `S`/`P`/`M` · adventure / set / armor · `/workspace/tmp/d354-*` `/workspace/tmp/d355-*` · 样本 R / Pack6 / 天赋 / 灰印 · 主仓切分支

## 总控旁注

**≠关观察**（满窗仍须 ≥2026-10-10 17:40 CST）。不抢 K3。活窗薄抽（打开帮助「变强」可见）交测试另号（≠ D354/D355 薄抽号）。

---

*D356 批 A·M · tip `593d8792` · 显示 only · ≠关观察 · 观察期薄窗。*
