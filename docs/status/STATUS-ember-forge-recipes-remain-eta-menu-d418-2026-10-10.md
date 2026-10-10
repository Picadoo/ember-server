# 状态 · D418：工坊速览挂 remain/eta（菜单号 · ≠关观察 ≠抬日表）

**日期：** 2026-10-10（上海时间）  
**上游：** tip+DESIGN `9559ea01` · D405 remain_line live · D413 ActionBar · D416§5.5 后置升主 · 总控批 A·M  
**裁决：** **已批 A · 方案 M · D418** · W1a A 格挂 remain_line · W1b V/Open 半行 · **零 jar** · **≠关观察** · **≠抬日表** · **≠开 gate_daily** · **≠动 Stage2** · **≠交 sx12** · **未派测**  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

配方速览「挂机打满对照」格末可见「今日挂机：还差/约满」（D405 真键只读）。仓内 C / 还差 G / 静态约对照 **保留**。打开菜单半行 +「回挂机庭」半行提示「速览可看今日还差/约满」。不改产量、不新 Layout、不假写固定分钟、不暗示坐满必够升阶。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_forge_recipes.yml` | A lore 末挂 remain_line + 诚实半行；V +「速览可看今日还差/约满」；Open +同半行；顶注 D418 |
| 本 STATUS | 菜单号结案 |
| DESIGN / tip / backlog / 旁注 | 批 A·M 勾选 + 挂盘已落回填 |

## 验收（本号 · 菜单纪律 · 宣称可测通 ≠ 整本 PASS）

| ID | 结果 | 备注 |
|----|------|------|
| V1 未满可见 remain_line | **PASS（菜单）** | A 挂 `%corerpg_p1_afk_remain_line%`；非假写固定分钟 |
| V2 进度可变（口径） | **可测通（菜单）** | 键同挂机战况；整本进度推进另号抽测 |
| V3 满额诚实态 | **可测通（菜单）** | 依赖 remain_line 满额文案；本号未假喊还能挂出日量 |
| V4 仓差/静态对照仍在 | **PASS（菜单）** | C / G / 静态约 A 表保留；与 remain 分工 |
| V5 回挂机庭 | **PASS（菜单）** | V → `ember_p1_afk`；半行对齐 |
| V6 文案纪律 | **PASS（菜单）** | 无「保证今天升阶」；无旧日常窟；无 sx12；无教裸指令 |
| 零假写 / 零 jar / Layout | **PASS** | 仅 PAPI；未改 jar；未新主格；A 钉死勿与 V 重复长块 |
| trmenu reload | **PASS** | `良好 | 73 个菜单已加载 (112 ms)`（**2026-10-10 09:19:56 CST**） |
| 派测 | **未派** | 测试另号 · 宣称可测通 ≠ 整本 PASS |

## 不动

- jar / daily_kills / afk.tiers / UpgradeRules / Stage2 三开关 / gate_daily / ×0.97  
- 挂机庭 remain / ActionBar / 仓差板主交付（保留）  
- sx12 / next_farm 塞速览 / 假写 ETA

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支；未 stash/reset/checkout |

---

*D418 · 工坊速览挂 remain/eta · 上游 tip `9559ea01` · ≠关观察≠抬日表≠sx12 · 未派测。*
