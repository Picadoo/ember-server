# 状态 · D414：短征选页挂 sx10 day_line + fc（菜单号 · ≠关观察）

**日期：** 2026-10-10（上海时间）  
**上游：** tip 键 `9c8fb353` · jar `1.65.121-d414.local` · bv **74** · 骨架 tip `57edf3fc` · MM tip `efb26b5d` · DESIGN tip `e052caa9`  
**裁决：** **菜单挂日帽/首通已落** · `%corerpg_p1_sx10_day_line%` + `%corerpg_p1_sx10_fc%` 同构挂 V 格 + I 合计半行 · **≠关观察** · **≠抬日表** · **≠开 gate_daily** · **≠动 Stage2 三开关** · **≠改 jar / ×0.97** · hub/adventure 已「十本/递闸」本号不动 · 同批可选 D413 W1c Open 抬头半行  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

第十本烬闸递室的日有奖帽与生涯首通键已 live（插件 tip `9c8fb353`），选页去掉「键号另挂 / 注释预留」，按前九本同构挂个人 day_line / fc；说明格合计保持十本 `sx_day_left_sum`（插件称已含第十本）。枢纽/冒险半指已写「递闸」与合计键，本号不动。金额文案对齐实发：有奖 80/4/3 · 首通由 fc 真键（90/6/1）· 体力 30 · 日帽 3 分开计。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | 顶注脚对齐 d414 键 live；I 挂烬闸 day_line + fc + 十本合计；V 灰态/体力不足/主 lore 挂 day_line + 主 lore 挂 fc；去掉静态「键号另挂/预留」假字 |
| `plugins/TrMenu/menus/ember_p1_afk.yml` | （同批可选 D413 W1c）Open +半行 tell「抬头可见还差/约满」；**不**重开菜单 remain 主块 |
| 本 STATUS | 菜单号结案短注 |
| DESIGN / backlog / tip / d414 STATUS | 轻量回填「挂盘已落」 |

## 验收（本号 · 菜单）

| ID | 结果 | 备注 |
|----|------|------|
| V8 选页挂 day_line/fc | **PASS（菜单）** | A/C/E/G/K/M/O/Q/T/V 均挂 day_line；V+I 挂 sx10_fc；I 十行 + left_sum |
| 金额诚实 | **PASS** | S49 80/4/3 · 首通由 fc · 体力 30 · 日帽 3 分开 |
| 禁假写 n/3 | **PASS** | 仅 PAPI，无写死个人次数 |
| hub/adventure | **不动** | 已有短征半指 + left_sum（自然含第十本）+「递闸」 |
| ember_p1_afk remain 主块 | **不动** | 仅 Open 半行；禁重开 D405 remain |
| trmenu reload | **PASS** | `良好 \| 73 个菜单已加载 (133 ms)`（**2026-10-10 08:45:17 CST**） |

## 不动

- jar / ember-v1.yml Stage2 三开关 / afk.tiers / daily_kills / gate_daily / ×0.97  
- map（未 `git add -f`）· MM · runs 源（插件岗已交 tip `9c8fb353`）  
- 本号**不派测**（测试另派）

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支；未 stash/reset/checkout |

---

*D414 菜单号 · sx10 day_line/fc 挂盘 · 上游键 tip `9c8fb353` · 同批 D413 W1c Open · ≠关观察≠抬日表≠开旧日常闸。*
