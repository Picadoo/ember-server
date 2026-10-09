# 状态 · D404 派测：仓内还差（vault 四材 + 代表档 gap · 同屏）

**日期：** 2026-10-10（上海时间）  
**号：** D404Spot · 证据 `/workspace/tmp/d404-mat-gap/`  
**上游：** jar `1.65.113-d404.local` · 键 tip [`a6385f12`](https://github.com/Picadoo/ember-server/commit/a6385f12) · 菜单壳 [`d5afa0a2`](https://github.com/Picadoo/ember-server/commit/d5afa0a2) · 挂键 [`94450758`](https://github.com/Picadoo/ember-server/commit/94450758) · DESIGN [`DESIGN-ember-afk-forge-mat-gap-visible-2026-10-10.md`](../design/DESIGN-ember-afk-forge-mat-gap-visible-2026-10-10.md) · PAPI STATUS [`STATUS-ember-afk-forge-mat-gap-papi-d404-2026-10-10.md`](STATUS-ember-afk-forge-mat-gap-papi-d404-2026-10-10.md) · 挂键 STATUS [`STATUS-ember-afk-forge-mat-gap-menu-hang-d404-2026-10-10.md`](STATUS-ember-afk-forge-mat-gap-menu-hang-d404-2026-10-10.md)  
**范围：** vault 四材真数 · enhance1 / upgrade_t2 拼接 / refine1 · 速览同屏 · 挂机/仓库半行 · **≠关观察** · **未改** afk.tiers / daily_kills / UpgradeRules / Stage2 三开关 / gate_daily · **未切分支** · **未反复停服**（沿用已装 d404 jar）  
**总结果：** **PASS**

## 人话

配方速览「仓内现有 / 代表档·还差」同屏挂真仓 PAPI：空仓见碎片/骨尘/核心/胚料都是 0，强化+1 还差 4、升阶 T1→T2 `碎差60·核差12·胚差6`、精工 `胚差3·骨差5`。注入材料后键即时变（如碎片 24 → enhance1=0、升阶 `碎差36·核差2·胚差0`）。挂机「去哪花」和仓库半行都指「配方速览可看仓内还差」。afk 日表 / UpgradeRules / Stage2 / gate_daily 一行没拧。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| S0 | 静态：C/G 挂 `%corerpg_p1_vault_*%` / `recipe_gap_*`；无「键未 live」预留；afk/storage 半行 | **PASS** | `10-static-checks.json` |
| V1 | `/papi parse me` 四材数字 + enhance1/upgrade_t2/refine1 与 `max(0,need−have)` 一致（空仓） | **PASS** | `20-papi-me-base.json` · `40-recipes-base.json` |
| L0 | 速览同屏：仓内现有 + 还差真数（非字面量/假写） | **PASS** | `40-recipes-base.json` |
| V2/V3 | 注入碎片等后键即时：shard↑ · enhance1→0 · upgrade/refine 拼接随仓变 | **PASS** | `21/22-papi-me-*.json` · `run-v2.log` |
| L3 | 挂机「去哪花」+ 仓库半行「配方速览可看仓内还差」 | **PASS** | `50-afk-window.json` · `51-storage-window.json` |
| V4 | `%corerpg_gate_daily%` = `no`（旧七线仍拒） | **PASS** | `60-gate-daily.json` |
| Sw | six_slot enabled/migrate/set_bonus=true · daily_kills=2400 · UpgradeRules git diff 空 | **PASS** | `00-switches-pre.json` · `90-endstate.json` |

## 关键证据摘要

- tip 键 `a6385f12` · 菜单壳 `d5afa0a2` · 挂键 `94450758` · jar `1.65.113-d404.local` sha256 `b62819ed0844503396036c6ffb4fb3ec58b89377c0a10a8d7c4e705861e28978`
- 测号 `D404MatGap` · `/papi parse me`（需临时 op）+ `trmenu open ember_p1_forge_recipes`
- 空仓同屏：碎片/骨尘/核心/胚料 `0` · enhance1 `4` · upgrade `碎差60·核差12·胚差6` · refine `胚差3·骨差5`
- 注入后键：碎片 `24` / 核 `10` / 胚 `8` / 骨 `6` → enhance1 `0` · upgrade `碎差36·核差2·胚差0` · refine `胚差0·骨差0`
- md5：recipes `2975d718…` · UpgradeRules `b0561533…`（git clean vs a6385f12）

## 旁注

- 注入后同会话立刻重开菜单，TrMenu 格 lore 可能仍显示开窗缓存（C/G **无** `update:`）；**键 parse 即时正确**。本抽 **勿改配置**，不在本号补 update。
- 并行 D403 换 jar 窗口已过；本号未停服，沿用 play 已装 `1.65.113-d404.local`。

## 不动确认

afk.tiers / daily_kills · EmberUpgradeRules · Stage2 三开关 / ×0.97 · gate_daily · K3 · 关观察 · 切分支 · login/proxy/MariaDB

## 总控旁注

**≠关观察**。D404 仓内还差派测 PASS（同屏真数 + 注入后键公式）。

---

*D404Spot · PASS · tip `a6385f12`+`94450758` · 证据 `/workspace/tmp/d404-mat-gap/` · ≠关观察。*
