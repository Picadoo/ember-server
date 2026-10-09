# 状态 · D411：挂机 next_farm · 菜单 W1b/W1c（已批 A·M · 菜单侧已挂 live 键 · ≠关观察 ≠抬日表 ≠sx09）

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-afk-next-farm-visible-2026-10-10.md`](../design/DESIGN-ember-afk-next-farm-visible-2026-10-10.md) @ tip `9a936440` · tip [`STATUS-ember-next-hard-debt-afk-next-farm-visible-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-afk-next-farm-visible-need-design-2026-10-10.md) · backlog `B-afk-next-farm-visible` · 插件 STATUS [`STATUS-ember-afk-next-farm-papi-d411-2026-10-10.md`](STATUS-ember-afk-next-farm-papi-d411-2026-10-10.md) tip `3c59bab5` · jar `1.65.119-d411.local`  
**裁决：** **已批 A · 批 M · D411 · 菜单侧已落地** · 本号交 **W1b 战况挂键 + W1c Open 半行** · 插件 W1a **已 live**（`3c59bab5`）· **≠关观察** · **≠抬 afk.tiers/daily_kills** · **≠开 gate_daily / R / K3** · **≠交 sx09** · **≠动 Stage2 三开关** · **≠假写固定层名当个人态** · **零 Java** · **未派测**

**toplevel：** `/workspace/minecraft` @ `main`

## 人话

挂机战况「还差/约满」下面多了一行真 PAPI：下一层养签（满额让位 / 未解锁继续养 / 打稳可试上一层·多养真表物 / 未打稳继续打稳）。Open tell 多半行「战况有下一层养签」。D383 静态层差四行保留。不抬产量。

## 落地对照（方案 M · 本号 = W1b/W1c）

| 窗 | 结果 | 落点 |
|----|------|------|
| W1a next_farm 键 | **另号已齐** | tip `3c59bab5` · `%corerpg_p1_afk_next_farm%` · jar `1.65.119-d411.local` |
| W1b 战况挂键 | **PASS** | `ember_p1_afk` 格 I · remain_line 下挂 `§6下一层养签：%corerpg_p1_afk_next_farm%`（真键 · 禁假写） |
| W1c Open 半行 | **PASS（可选同批）** | Open tell「战况有下一层养签」 |
| W1d 仓差挂战况 / W1e sx09 | **本窗不做** | 硬禁/后置 |

## 改动清单（入仓）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_afk.yml` | I · remain_line 下 +next_farm；Open tell 半行；保留 D383 静态层差四行 |
| DESIGN / tip / backlog / 插件 STATUS | 回填：菜单已落地 |
| 本 STATUS | 菜单侧结案 |

## 验收（本号 · 菜单面）

| ID | 结果 | 备注 |
|----|------|------|
| 1 战况可见养签行 | **PASS（菜单）** | I 挂 `next_farm` live |
| 2 Open 半行 | **PASS** | 「战况有下一层养签」 |
| 3 静态层差保留 | **PASS** | D383 四行未删 |
| 4 零改表 | **PASS（纪律）** | 未 stage Java / afk.tiers / daily_kills / ember-v1 开关 / jar |
| 5 无假平面 | **PASS** | 无旧日常假开；无魂尘挂机产；无 sx09；无假写固定层名 |
| trmenu reload | **PASS** | `良好 \| 73 个菜单已加载 (114 ms)`（**07:54:04 CST**）|

## 不动

- Java / afk.tiers / daily_kills / Stage2 / gate_daily / jar（插件另号已交）
- 假写固定层名/口号当个人态
- 本号**未派全链路测**（总控另派）

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支 |

## 热更旁注

- play：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 73 个菜单已加载 (114 ms)`（**2026-10-10 07:54:04 CST**）· 旁注：1.12 `updateCommands` NoSuchMethodError 已知无害
