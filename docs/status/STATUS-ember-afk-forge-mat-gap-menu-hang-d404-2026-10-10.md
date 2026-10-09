# 状态 · D404：配方速览挂仓内还差 PAPI（菜单号 · ≠关观察 · 勿派测）

**日期：** 2026-10-10（上海时间）  
**上游：** jar `1.65.113-d404.local` tip 键 `a6385f12` · 菜单壳 `d5afa0a2` · PAPI STATUS [`STATUS-ember-afk-forge-mat-gap-papi-d404-2026-10-10.md`](STATUS-ember-afk-forge-mat-gap-papi-d404-2026-10-10.md)  
**裁决：** **菜单挂键已落** · 去掉 vault_*/recipe_gap_* 注释预留 · 挂真 PAPI · **≠关观察** · **≠抬日表** · **≠开 gate_daily** · **≠动 Stage2 三开关** · **≠假写固定数字** · **本号不派测**  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

配方速览「仓内现有 / 代表档·还差」两格已挂真仓 PAPI：碎片/骨尘/核心/胚料仓合计，以及强化+1 碎片差、升阶 T1→T2 拼接半行、精工 0→1 拼接半行。不再写「键未 live」预留假字。

## 挂键清单

| Placeholder | 菜单落点 | 形态 |
|-------------|---------|------|
| `%corerpg_p1_vault_shard%` | C 仓内现有 · 碎片行 | 数字 |
| `%corerpg_p1_vault_bone%` | C · 骨尘行 | 数字 |
| `%corerpg_p1_vault_core%` | C · 核心行 | 数字 |
| `%corerpg_p1_vault_blank%` | C · 胚料行 | 数字 |
| `%corerpg_p1_recipe_gap_enhance1%` | G · 强化+1 碎片还差 | 单数字 |
| `%corerpg_p1_recipe_gap_upgrade_t2%` | G · 升阶 T1→T2 | 拼接 `碎差N·核差M·胚差K` |
| `%corerpg_p1_recipe_gap_refine1%` | G · 精工 0→1 | 拼接 `胚差N·骨差M` |

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_forge_recipes.yml` | C/G 去注释预留挂真 PAPI；Open/K 半行对齐；顶注脚对齐 d404 键 |
| 本 STATUS | 菜单挂键结案短注 |

## 验收（本号 · 菜单）

| ID | 结果 | 备注 |
|----|------|------|
| W1c 挂键 | **PASS（菜单）** | vault 四键 + enhance1 / upgrade_t2 / refine1 均挂 `%corerpg_…%` |
| 禁假写 | **PASS** | 无写死个人余额/gap；need 标签（×4 / 碎60…）仍静态说明 |
| 零改表 | **PASS（纪律）** | 未动 Java / afk / UpgradeRules / Stage2 / gate_daily |
| trmenu reload | 见 commit 旁注 | 本号热更 |
| 派测 | **不做** | 总控钉死本号勿派测 |

## 不动

- jar / ember-v1.yml Stage2 三开关 / afk.tiers / daily_kills / gate_daily / UpgradeRules  
- 挂机半行 / 仓库半行（壳号 `d5afa0a2` 已齐，本号不重写）  
- 本号**不派测**

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支 |
