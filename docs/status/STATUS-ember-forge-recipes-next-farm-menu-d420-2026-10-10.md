# 状态 · D420：工坊速览回挂机挂 next_farm（菜单号 · ≠关观察 ≠抬日表）

**日期：** 2026-10-10（上海时间）  
**上游：** tip+DESIGN `d5846638` · D411 next_farm live · D418 速览 A remain · D418§5.3 后置升主 · 总控批 A·M  
**裁决：** **已批 A · 方案 M · D420** · W1a V 格挂 next_farm · W1b Open 半行 · **零 jar** · **钉死 V** · **≠关观察** · **≠抬日表** · **≠开 gate_daily** · **≠动 Stage2** · **≠交 sx13** · **未派测**  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

配方速览「回挂机庭」格可见「下一层养签」（D411 真键只读）。A 格 remain / 仓内 C / 还差 G / 静态约对照 **保留**。打开菜单半行提示「回挂机庭可看下一层养签」。不改产量、不新 Layout、不假写固定层名、不暗示换层必赚更多。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_forge_recipes.yml` | V lore 挂 `next_farm` + 诚实半行；Open +「回挂机庭可看下一层养签」；顶注 D420 |
| 本 STATUS | 菜单号结案 |
| DESIGN / tip / backlog / 旁注 | 批 A·M 勾选 + 挂盘已落回填 |

## 验收（本号 · 菜单纪律 · 宣称可测通 ≠ 整本 PASS）

| ID | 结果 | 备注 |
|----|------|------|
| V1 未满可见 next_farm | **PASS（菜单）** | V 挂 `%corerpg_p1_afk_next_farm%`；非假写固定层名 |
| V2 打稳可试态（口径） | **可测通（菜单）** | 键同挂机战况；整本进度推进另号抽测 |
| V3 满额诚实态 | **可测通（菜单）** | 依赖 next_farm 满额文案；本号未假喊还能挂出日量 |
| V4 remain/仓差仍在 | **PASS（菜单）** | A remain / C / G / 静态约保留；与 next_farm 分工（remain=还差多久，next_farm=坐哪层） |
| V5 回挂机庭 | **PASS（菜单）** | V → `ember_p1_afk`；换层仍点上排①–④ |
| V6 文案纪律 | **PASS（菜单）** | 无「保证换层必赚」；无旧日常窟；无 sx13；无教裸指令；无自动传送 |
| 零假写 / 零 jar / Layout / 钉死 V | **PASS** | 仅 PAPI；未改 jar；未新主格；next_farm 在 V 勿与 A remain 抢长块 |
| trmenu reload | **PASS** | `良好 | 73 个菜单已加载 (91 ms)`（**2026-10-10 09:37:38 CST**） |
| 派测 | **未派** | 测试另号 · 宣称可测通 ≠ 整本 PASS |

## 不动

- jar / daily_kills / afk.tiers / UpgradeRules / Stage2 三开关 / gate_daily / ×0.97  
- 挂机庭 next_farm / A remain / ActionBar / 仓差板主交付（保留）  
- sx13 / next_farm 塞 A / 假写养签

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支；未 stash/reset/checkout |

---

*D420 · 工坊速览回挂机挂 next_farm · 上游 tip `d5846638` · ≠关观察≠抬日表≠sx13 · 未派测。*
