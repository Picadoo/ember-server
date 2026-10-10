# 状态 · D421：短征选页挂 sx13 day_line + fc（菜单号 · ≠关观察）

**日期：** 2026-10-10（上海时间）  
**上游：** tip 键 `2688b0b2` · jar `1.65.124-d421.local` · bv **77** · 骨架 tip `77c06810`（等价 `f980b4a9`）· MM tip `0ecf4134` · DESIGN tip `d61e0d88`  
**裁决：** **菜单挂日帽/首通已落** · `%corerpg_p1_sx13_day_line%` + `%corerpg_p1_sx13_fc%` 同构挂 W 格 + I 合计半行 · **≠关观察** · **≠抬日表** · **≠开 gate_daily** · **≠动 Stage2 三开关** · **≠改 jar / ×0.97** · hub/adventure 已「十三本/衡梁」本号不动  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

第十三本烬衡悬梁的日有奖帽与生涯首通键已 live（插件 tip `2688b0b2`），选页去掉「键号另挂 / 注释预留」，按前十二本同构挂个人 day_line / fc；说明格合计改为「十三本」`sx_day_left_sum`（插件称已含第十三本）。枢纽/冒险半指已写「衡梁」，本号不动。金额文案对齐实发：有奖 80/4/3 · 首通由 fc 真键（60/6/1）· 体力 30 · 日帽 3 分开计。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | 顶注脚对齐 d421 键 live；I 挂烬衡 day_line + fc + 十三本合计；W 灰态/体力不足/主 lore 挂 day_line + 主 lore 挂 fc；去掉静态「键号另挂/预留」假字 |
| 本 STATUS | 菜单号结案短注 |
| DESIGN / backlog / tip / d421 骨架 STATUS | 轻量回填「挂盘已落」 |

## 验收（本号 · 菜单）

| ID | 结果 | 备注 |
|----|------|------|
| V8 选页挂 day_line/fc | **PASS（菜单）** | A/C/E/G/K/M/O/Q/T/V/X/Y/W 均挂 day_line；W+I 挂 sx13_fc；I 十三行 + left_sum（十三本） |
| 金额诚实 | **PASS** | S52 80/4/3 · 首通由 fc · 体力 30 · 日帽 3 分开 |
| 禁假写 n/3 | **PASS** | 仅 PAPI，无写死个人次数 |
| hub/adventure | **不动** | 骨架号已「衡梁」+ 十三本半指 |
| trmenu reload | **PASS** · `良好 | 73 个菜单已加载 (93 ms)`（**2026-10-10 09:49:33 CST**） |

## 不动

- jar / ember-v1.yml Stage2 三开关 / afk.tiers / daily_kills / gate_daily / ×0.97  
- map（未 `git add -f`）· MM · runs 源（插件岗已交 tip `2688b0b2`）  
- 本号**不派测**（测试另派）

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支；未 stash/reset/checkout |

---

*D421 菜单号 · sx13 day_line/fc 挂盘 · 上游键 tip `2688b0b2` · 骨架 tip `77c06810` · MM `0ecf4134` · ≠关观察≠抬日表≠开旧日常闸。*
