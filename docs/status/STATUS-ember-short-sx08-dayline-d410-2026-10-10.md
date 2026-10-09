# 状态 · D410：短征选页挂 sx08 day_line + fc（菜单号 · ≠关观察）

**日期：** 2026-10-10（上海时间）  
**上游：** tip 键 `24ffa712` · jar `1.65.118-d410.local` · 骨架 tip `50e5c79c` · MM tip `4044d460` · DESIGN tip `2f4ff19d`  
**裁决：** **菜单挂日帽/首通已落** · `%corerpg_p1_sx08_day_line%` + `%corerpg_p1_sx08_fc%` 同构挂 Q 格 + I 合计半行 · **≠关观察** · **≠抬日表** · **≠开 gate_daily** · **≠动 Stage2 三开关** · **≠改 jar / ×0.97**  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

第八本错层烬庭的日有奖帽与生涯首通键已 live（插件 tip `24ffa712`），选页去掉「键未 live」注释预留，按前七本同构挂个人 day_line / fc；说明格合计改为八本 `sx_day_left_sum`（插件称已含第八本）。枢纽/冒险半指已用合计键且写「错层」，本号不动。金额文案对齐实发：有奖 80/4/3 · 首通由 fc 真键 · 体力 30 · 日帽 3 分开计。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | 顶注脚对齐 d410 键 live；I 挂错层 day_line + fc + 八本合计；Q 灰态/体力不足/主 lore 挂 day_line + 主 lore 挂 fc；去掉静态「键号后出」假字 |
| 本 STATUS | 菜单号结案短注 |
| DESIGN / backlog / tip / d410 STATUS | 轻量回填「挂盘已落」 |

## 验收（本号 · 菜单）

| ID | 结果 | 备注 |
|----|------|------|
| V8 选页挂 day_line/fc | **PASS（菜单）** | A/C/E/G/K/M/O/Q 均挂 day_line；Q+I 挂 sx08_fc；I 八行 + left_sum |
| 金额诚实 | **PASS** | S47 80/4/3 · 首通由 fc · 体力 30 · 日帽 3 分开 |
| 禁假写 n/3 | **PASS** | 仅 PAPI，无写死个人次数 |
| hub/adventure | **不动** | 已有短征半指 + left_sum（自然含第八本） |
| trmenu reload | **PASS** | `良好 \| 73 个菜单已加载 (131 ms)`（**2026-10-10 07:29:26 CST**） |

## 不动

- jar / ember-v1.yml Stage2 三开关 / afk.tiers / daily_kills / gate_daily / ×0.97  
- map（未 `git add -f`）· MM · runs 源（插件岗已交）  
- 本号**不派测**（测试另派）

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支 |

---

*D410 菜单号 · sx08 day_line/fc 挂盘 · 上游键 tip `24ffa712` · ≠关观察≠抬日表≠开旧日常闸。*
