# 状态 · D403：短征选页挂 sx06 day_line（菜单号 · ≠关观察）

**日期：** 2026-10-10（上海时间）  
**上游：** tip 键 `d96b0dd3` · jar `1.65.112-d403.local` · MM `7d251df4` · 骨架已就位 · 本窗前选页 sx06 仍注释预留  
**裁决：** **菜单挂日帽已落** · `%corerpg_p1_sx06_day_line%` 同构挂 M 格 + I 合计半行 · **≠关观察** · **≠抬日表** · **≠开 gate_daily** · **≠动 Stage2 三开关**  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

第六本烬环廊的日有奖帽键已 live，选页去掉「键未 live」注释预留，按前五本同构挂个人 day_line；说明格合计改为六本 `sx_day_left_sum`。金额文案仍对齐实发：有奖 80/4/3 · 首通 130/6/1 · 体力 30 · 日帽 3 分开计。枢纽半指未改（已有短征日帽合计）。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | 顶注脚对齐 d403 键；I 挂烬环 day_line + 六本合计；M 灰态/体力不足/主 lore 挂 day_line；去掉「日帽键另号」静态假字 |
| 本 STATUS | 菜单号结案短注 |

## 验收（本号 · 菜单）

| ID | 结果 | 备注 |
|----|------|------|
| V8 选页挂 day_line | **PASS（菜单）** | A/C/E/G/K/M 均挂 `%corerpg_p1_sx0N_day_line%`；I 六行 + left_sum |
| 金额诚实 | **PASS** | S45 80/4/3 · 首通 130/6/1 · 体力 30 · 日帽 3 分开 |
| 禁假写 n/3 | **PASS** | 仅 PAPI，无写死个人次数 |
| hub | **不动** | 已有短征半指 + left_sum |
| trmenu reload | 见 commit 旁注 / 控制台 | 本号热更 |

## 不动

- jar / ember-v1.yml Stage2 三开关 / afk.tiers / daily_kills / gate_daily  
- map（未 `git add -f`）· MM · runs 源（插件岗已交）  
- 本号**不派测**（测试另派）

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支 |
