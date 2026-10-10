# 状态 · D415：挂机战况挂仓差（菜单号 · ≠关观察 ≠抬日表）

**日期：** 2026-10-10（上海时间）  
**上游：** tip+DESIGN `34a1366d` · D404 vault/gap live（速览已挂）· 同批并行 D414 日帽挂盘 / D413 W1c  
**裁决：** **已批 A · 方案 M · D415** · W1a 战况 I 挂仓内+还差（含精工可选）· W1b Open/去哪花半行 · **零 jar** · **≠关观察** · **≠抬日表** · **≠开 gate_daily** · **≠动 Stage2** · **≠交 sx11** · **未派测**  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

挂机庭战况同屏可见仓内碎/骨/核/胚与强化+1、升阶 T2、精工 0→1 还差（D404 真键只读挂载）。remain / 下一层养签 / 层差 / 短征剩余有奖 **保留**。打开菜单半行提示「战况可看仓内还差」；「去哪花」改为「战况/速览均可看仓差」。不改产量、不重开配方速览主表、不新 Layout。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_afk.yml` | I：仓内一行 + 还差（enhance1/upgrade_t2/refine1）+ 细看半行；Open +「战况可看仓内还差」；去哪花半行对齐；顶注 D415 |
| 本 STATUS | 菜单号结案 |
| DESIGN / tip / backlog | 批 A·M 勾选 + 挂盘已落回填 |

## 验收（本号 · 菜单）

| ID | 结果 | 备注 |
|----|------|------|
| V1 战况仓内 | **PASS（菜单）** | I 挂 vault_shard/bone/core/blank |
| V2 战况还差 | **PASS（菜单）** | enhance1 + upgrade_t2 + refine1 |
| 保留旧行 | **PASS** | remain_line / next_farm / 短征 left_sum / 层差一览仍在 |
| 零假写 | **PASS** | 仅 PAPI；无写死余额 |
| 零 jar / Layout | **PASS** | 未改 jar；未新主格 |
| trmenu reload | **PASS** | `良好 \| 73 个菜单已加载 (116 ms)`（**2026-10-10 08:48:16 CST**）；afk 自动重载 6ms |
| 派测 | **未派** | 测试另号 |

## 不动

- jar / daily_kills / afk.tiers / UpgradeRules / Stage2 三开关 / gate_daily / ×0.97  
- `ember_p1_forge_recipes` 速览主表（保留）  
- ActionBar 塞仓差 / sx11

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支；未 stash/reset/checkout |

---

*D415 · 挂机战况挂仓差 · 上游 tip `34a1366d` · ≠关观察≠抬日表≠交 sx11 · 未派测。*
