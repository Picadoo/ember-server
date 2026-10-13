# tip · TIP-CJ 挂机庭「顺手推荐花」聚合（四花智路由 · ≠第五件花料 · ≠抬表）· 拟 D13315

> **状态：** CLOSED live · 批 **A · 方案 M** · jar `1.65.811-d13315.local` · 插件 `09fd8d29e` · 物品 `f85daf617` · TrMenu reload 77 · PASS STATUS `843dfee65` · 2026-10-14 00:43 Asia/Shanghai · **勾毕** · 策划旁注 00:45
> **DESIGN** [`DESIGN-ember-afk-courtyard-recommend-flower-2026-10-14.md`](../design/DESIGN-ember-afk-courtyard-recommend-flower-2026-10-14.md) · RESEARCH 同日 · backlog `B-afk-courtyard-recommend-flower`  
> **施工岗（批后）：** 余烬-插件（薄 `afk_rec_flower*` + enter `afk-rec-flower`）+ 余烬-物品（TrMenu P 格）· **不派怪物/地图**  
> **硬禁：** 零第五件花料命令 · 零抬日表/价 · 零撤 E/U/R/Q · 零一键静默 confirm · 零 sx20/L13/K3 · 零诚实空转

## 一句话
挂机庭一眼推荐仓够的强化/升阶/精工/成色，一点进既有预览；确认仍走原确认格。

## 布局钉死
- 庭内第二行空位落 **P**（荐 `#P1 2 3 #`；保留 1/2/3/U/S/4/Q/V/R/E 等）
- Icon **P**：名 `§a顺手推荐花`；lore 挂 `%corerpg_p1_afk_rec_flower_name%` + kind；`enter afk-rec-flower`
- `_ok=0` 灰态 → 工坊；**不撤** E/e/U/u/R/r/Q/q
- W1c：Open「仓够推荐见本页 P」
- update:20

## jar 钉死（W1a）
- 优先序：enhance 仓够 → upgrade 仓够 → refine 仓够 → quality 仓够 → empty
- 护甲跳过 upgrade/refine/quality
- `%corerpg_p1_afk_rec_flower%` / `_name` / `_kind` / `_ok`
- enter `afk-rec-flower` → 既有预览命令（非 confirm）
- **零改** UpgradeRules / afk.tiers / daily_kills / 价

## 验收（批后施工）
- [x] 总控批 A·M · 2026-10-14 00:28 Asia/Shanghai
- [x] 插件交 afk_rec_flower* + enter + 单测 · 1.65.811-d13315.local · 2026-10-14 00:35
- [x] 物品交 P 格 + TrMenu reload · `f85daf617` · reload 77
- [x] 测岗 PASS `843dfee65` · enhance优先 · 灰跳工坊 · EeUuRrQq · 零抬产

## STATUS: CLOSED live · jar `1.65.811-d13315.local` · menu `f85daf617` · PASS `843dfee65` · 2026-10-14 00:43 Asia/Shanghai
