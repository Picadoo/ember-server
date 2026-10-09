# 状态 · D398 薄抽：工坊配方速览（静态+活扫）

**日期：** 2026-10-10（上海时间）  
**号：** D398Spot · 证据 `/workspace/tmp/d398-forge-recipes/`  
**上游施工：** tip [`7443866f`](https://github.com/Picadoo/ember-server/commit/7443866f) · STATUS [`STATUS-ember-afk-forge-recipe-visible-d398-2026-10-10.md`](STATUS-ember-afk-forge-recipe-visible-d398-2026-10-10.md) · DESIGN [`DESIGN-ember-afk-forge-recipe-visible-2026-10-10.md`](../design/DESIGN-ember-afk-forge-recipe-visible-2026-10-10.md)  
**范围：** 静态对照 UpgradeRules + live `trmenu open` 工坊→点 P→配方速览 + 挂机「去哪花」半行 · **≠关观察** · **≠开 K3** · **未改** 配置 / afk.tiers / UpgradeRules / Stage2 三开关 · **未切分支**  
**总结果：** **PASS**

## 人话

工坊底栏 P「配方速览」能打开薄页，未持件也能看见强化代表档（+1/+4/+7/+10）和升阶/精工/成色静态数；挂机「去哪花」半行指到速览。afk 日表 / UpgradeRules / Stage2 三开关一行没拧。

## 验收表

| ID | 要点 | 结果 | 证据 |
|----|------|------|------|
| A1 | forge Layout `# A P bB#` · Icons.P「配方速览」→ `menu: ember_p1_forge_recipes` | **PASS** | `10-static-checks.json` · `30-forge-window.json`（P slot 49） |
| A2 | 薄页强化代表档 + 升阶/精工/成色静态数；与 `EmberUpgradeRules` 抽样一致；甲不可用 / 极品不可养成诚实 | **PASS** | `40-recipes-window.json` · rules 对照 |
| A3 | 挂机 S「去哪花」半行「工坊有「配方速览」看 ×N」+ +1 一例 | **PASS** | `50-afk-window.json` |
| A4 | tip 未动 `EmberUpgradeRules` / `ember-v1.yml` afk.tiers / daily_kills；Stage2 三开关仍 true；daily_kills=2400 | **PASS** | tip --name-only · `90-endstate.json` |
| L1 | live 打开工坊 · 见「配方速览」并点进薄页 Title「工坊 · 配方速览」 | **PASS** | `run.log` · `30/40-*.json` |
| L2 | live lore：+1 碎×4币×40 · +10 碎×72核×6币×1080 · T1→T2 1500 · 精工0→1 · 不可养成 · 「约」灰坡 | **PASS** | `40-recipes-window.json` |
| L3 | live 挂机「去哪花」含配方速览半行 | **PASS** | `50-afk-window.json` |
| Sw | six_slot enabled/migrate/set_bonus=true · bv67 · daily_kills=2400 | **PASS** | `00-switches-pre.json` · `90-endstate.json` |

## 关键证据摘要

- tip `7443866f`（`7443866f6575a05eec9b8dd90ef8e530bc2cac22`）· 分支 `main`
- md5：recipes `dbba957e…` · forge `c136bb1c…` · afk `d93669c2…` · UpgradeRules `b0561533…`（git clean）· live TrMenu 与仓同 md5 · 72 菜单已载
- 测号 `D398Recipe` · console `trmenu open` · 点 P 进薄页（click 事务旁注可忽略，窗口已开）
- 本号未切分支 / 未 stash / 未 reset / 未改 live yml 开关 / 未关观察

## 不动确认

afk.tiers / daily_kills · EmberUpgradeRules · 工坊扣费逻辑 · ×0.97 · set_bonus/enabled/migrate · bv · jar · K3 · 关观察 · 切分支

## 总控旁注

**≠关观察**。配方速览薄抽结案（静态+活扫 PASS）；与 D397 分报。

---

*D398Spot · A1–A4/L1–L3 PASS · tip `7443866f` · 证据 `/workspace/tmp/d398-forge-recipes/` · ≠关观察。*
