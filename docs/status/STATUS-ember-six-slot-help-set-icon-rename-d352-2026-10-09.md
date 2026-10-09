# 状态 · D352：帮助页套装入口图标名 Stage2 诚实（批 A·M · TrMenu 落地）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-help-set-icon-rename-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-help-set-icon-rename-need-design-2026-10-09.md) @ `28e5585f` · DESIGN [`DESIGN-ember-six-slot-help-set-icon-rename-2026-10-09.md`](../design/DESIGN-ember-six-slot-help-set-icon-rename-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D352** · 总控授权同号 TrMenu 施工（显示 only）  
**版本：** **docs + TrMenu only**（`ember_help.yml` 图标 `S` name 一行）· jar / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / CoreRpg live yml / adventure / hub / codex / gear / set / armor / hub_legacy / D351 薄抽证据 **未动**

## 人话

帮助页套装入口头顶不再挂「三套装」牌，改成「套装说明」；下面的族被动与四件套说明一字未动。数值和观察开关都没拧；观察不关；没抢 D351 薄抽轨。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_help.yml` | 图标 `S`：`name: '§d三套装'` → `name: '§d套装说明'`；lore/actions **未动** |
| DESIGN / tip / backlog | 批 A·M · tip 旁注已关 · `B-six-slot-help-set-icon-rename` → 已批 A · 已施工 |
| jar / CoreRpg yml / bv / set_bonus / D351 spot | **未动** |

## 文案摘要

| 位置 | 内容 |
|------|------|
| help `S` name（新） | `§d套装说明` |
| help `S` lore | 仍含焚烬/烬爆/炽愈 + 觉醒档 +「另：护甲四件套…受伤 −3%」+ 详情指针（D339） |
| 裸「三套装」name | **无**（本债范围内） |

## 验收（静态 · N1–N3）

| # | 项 | 结果 |
|---|----|------|
| N1 | `rg` help `S` name：非裸「三套装」；为 `§d套装说明` | `rg` · PASS |
| N2 | 同格 lore 仍有四件套句；本号未删 | 对照 · PASS |
| N3 | ×0.97 / set_bonus / jar / bv / CoreRpg live yml 本号未改；未抢 `/workspace/tmp/d351-spot` | 本号未触 · PASS |

## 部署

已用 `scripts/console.sh play "trmenu reload"` 执行；玩家重开帮助页即可见新图标名。**未**执行 `/corerpg reload`。

## 边界

- **≠关观察**（绿出口仍 ≥2026-10-10 17:40 CST）  
- **≠开 K3**  
- **≠抢 D351 薄抽**  
- Pack6 / 天赋 / 灰印 / 样本 R / 改 ×0.97：**未动**

---

*D352 · 批 A·M · help `S` → 套装说明 · 显示 only · ≠关观察 · 不抢 D351 薄抽。*
