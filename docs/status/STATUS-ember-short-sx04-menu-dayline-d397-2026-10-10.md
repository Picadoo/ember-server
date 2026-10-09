# 状态 · D397：短征选页挂 sx04 day_line（菜单号 · ≠关观察）

**日期：** 2026-10-10（上海时间）  
**上游：** tip 键 `8bd534fe` · jar `1.65.109-d397.local` · MM `a2b3fe8b` · 骨架 `52eeaeb7` · 本窗前选页仍注释预留  
**裁决：** **菜单挂日帽已落** · `%corerpg_p1_sx04_day_line%` 同构挂 G 格 + I 合计半行 · **≠关观察** · **≠抬日表** · **≠开 gate_daily** · **≠动 Stage2 三开关**  
**toplevel：** `/workspace/minecraft` @ `main`

## 人话

第四本烬井螺旋的日有奖帽键已 live，选页去掉「键未 live」注释预留，按前三本同构挂个人 day_line；说明格合计改为四本 `sx_day_left_sum`。金额文案仍对齐实发：有奖 80/4/3 · 首通 150/6/1 · 体力 30 · 日帽 3 分开计。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | 顶注脚对齐 d397 键；I 挂烬井 day_line + 四本合计；G 灰态/体力不足/主 lore 挂 day_line；去掉进本键另号拒提示 |
| 本 STATUS | 菜单号结案短注 |

## 验收（本号 · 菜单）

| ID | 结果 | 备注 |
|----|------|------|
| V8 选页挂 day_line | **PASS（菜单）** | A/C/E/G 均挂 `%corerpg_p1_sx0N_day_line%`；I 四行 + left_sum |
| 金额诚实 | **PASS** | S43 80/4/3 · 首通 150/6/1 · 体力 30 |
| 禁假写 n/3 | **PASS** | 仅 PAPI，无写死个人次数 |
| trmenu reload | 见 commit 旁注 / 控制台 | 本号热更 |

## 不动

- jar / ember-v1.yml Stage2 三开关 / afk.tiers / daily_kills / gate_daily  
- map（未 `git add -f`）· MM · runs 源（插件岗已交）  
- 本号**不派测**（测试另派）

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支 |
