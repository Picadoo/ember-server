# 状态 · D416：短征选页挂仓差（菜单号 · ≠关观察 ≠抬日表）

**日期：** 2026-10-10（上海时间）  
**上游：** tip+DESIGN `a0e51f9f` · D404 vault/gap live · D415 战况仓差 `85e8d28a` · 总控批 A·M  
**裁决：** **已批 A · 方案 M · D416** · W1a 选页 I 挂仓内+还差（含精工可选）· W1b Open 半行 · **零 jar** · **≠关观察** · **≠抬日表** · **≠开 gate_daily** · **≠动 Stage2** · **≠交 sx11** · **未派测**  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

短征选本说明同屏可见仓内碎/骨/核/胚与强化+1、升阶 T2、精工 0→1 还差（D404 真键只读挂载）。满态「今日有奖已满 · 去工坊花」主 CTA **保留**；日帽/首通/周钩/满态追工坊 **保留**。打开菜单半行提示「选本说明可看仓内还差」。不改产量、不重开挂机战况/速览仓差主表、不新 Layout。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | I 三分支（满态有体/满态无体/未满）：仓内一行 + 还差 enhance1/upgrade_t2/refine1 + 细看半行；Open +「选本说明可看仓内还差」；顶注 D416 |
| 本 STATUS | 菜单号结案 |
| DESIGN / tip / backlog | 批 A·M 勾选 + 挂盘已落回填 |

## 验收（本号 · 菜单）

| ID | 结果 | 备注 |
|----|------|------|
| V1 选页仓内 | **PASS（菜单）** | I 三分支挂 vault_shard/bone/core/blank |
| V2 选页还差 | **PASS（菜单）** | enhance1 + upgrade_t2 + refine1 |
| V3 满态 CTA | **PASS（菜单）** | `name: §e今日有奖已满 · 去工坊花` 两满态分支仍在；仓差挂在日帽块下，不盖主 CTA |
| 保留旧行 | **PASS** | day_line / fc / fc_left / 周钩 / 满态追工坊 / B 去哪花仍在 |
| 零假写 | **PASS** | 仅 PAPI；无写死余额 |
| 零 jar / Layout | **PASS** | 未改 jar；未新主格 |
| trmenu reload | **PASS** | `良好 | 73 个菜单已加载 (87 ms)`（**2026-10-10 08:55:36 CST**） |
| 派测 | **未派** | 测试另号 |

## 不动

- jar / daily_kills / afk.tiers / UpgradeRules / Stage2 三开关 / gate_daily / ×0.97  
- `ember_p1_afk` 战况仓差 / `ember_p1_forge_recipes` 速览主表（保留）  
- sx11 / ActionBar 塞仓差

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支；未 stash/reset |

---

*D416 · 短征选页挂仓差 · 上游 tip `a0e51f9f` · ≠关观察≠抬日表≠交 sx11 · 未派测。*
