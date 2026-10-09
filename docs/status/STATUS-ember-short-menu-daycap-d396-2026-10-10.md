# 状态 · D396：短征选页挂日帽同屏（§2.3 · TrMenu-only · 已施工）

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-short-menu-polish-2026-10-10.md`](../design/DESIGN-ember-short-menu-polish-2026-10-10.md) **§2.3** · tip [`STATUS-ember-next-hard-debt-short-menu-polish-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-short-menu-polish-need-design-2026-10-10.md) @ `7b43f741` · §2.1 D395 tip 菜单侧 · §2.2 插件 D395 tip `ee765f56` · jar `1.65.108-d395.local`  
**裁决：** **已批 A · 批 M · D396** · 本号交 `ember_p1_short` A/C/E/I 挂 `%corerpg_p1_sx0N_day_line%`（I 另挂 `sx_day_left_sum`）· **≠关观察** · **≠抬日表** · **≠改 S40–S42** · **≠同号写 Java** · **≠假写个人 n/3** · **≠假开旧日常**  
**版本：** TrMenu + docs · jar / CoreRpg / MM / DP / economy / runs **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`

## 人话

打开短征选页，悬停烬门/锈灯/霜雾就能看见「今日有奖 n/3」；说明格汇总三本剩余。刷完有奖后约 1 秒同屏会跟着变，不用关菜单猜还剩几次。

## 落地摘要（方案 M · §2.3）

| 窗 | 本号 |
|----|------|
| A/C/E 主 lore | 原静态「最多 3 次/日」→ `%corerpg_p1_sx0N_day_line%`（保留「第4+无结算 / 分开计」旁注） |
| A/C/E 灰态（未解锁 / 体力不足） | 同步挂对应 `day_line`；去静态「日有奖帽 3」重复句 |
| I 说明 | 三行各本 `day_line` + `%corerpg_p1_sx_day_left_sum%`；删「悬停见最多 3 次」假指引 |
| Open tell | +半行「今日有奖见各本悬停」 |
| update | A/C/E/I/S/B 已 `update: 20`（沿用） |
| actions / 门闩 / 体力 / 经济表 | **未动** |

## 挂键清单

| Placeholder | 挂点 |
|-------------|------|
| `%corerpg_p1_sx01_day_line%` | A 主 lore · A 灰态×2 · I |
| `%corerpg_p1_sx02_day_line%` | C 主 lore · C 灰态×2 · I |
| `%corerpg_p1_sx03_day_line%` | E 主 lore · E 灰态×2 · I |
| `%corerpg_p1_sx_day_left_sum%` | I（可选合计） |

未挂：单本 `day` / `day_left`（`day_line` 已含 n/3；禁写不存在键）。

## 验收自检（静态）

| ID | 结果 | 备注 |
|----|------|------|
| 真键 | **PASS** | 仅挂 D395 已交付键；玩家面无「最多 3 次」静态重复 |
| 假写 | **PASS** | 无字面个人「还能刷 3」 |
| 金额 | **PASS** | S40/S41/S42 与 D394 实发一致（本号未改） |
| jar/开关/afk | **PASS** | 本号未触 |

**活测（测岗）：** 开 `ember_p1_short` → 悬停 A/C/E 见 `今日有奖 n/3`；通关有奖后 stay-open≤1s 看 line 变；I 合计 remaining；gate_daily 仍拒。

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | A/C/E/I 挂 day_line；I 挂 left_sum；去静态最多3次 |
| `DESIGN-ember-short-menu-polish-2026-10-10.md` | 勾 §2.3 已落 · STATUS→D396 |
| `design-ember-content-backlog.md` | `B-short-menu-polish` → 已关 · D395+D396 |
| `STATUS-ember-short-daycap-papi-d395-2026-10-10.md` | 下一号指针→D396 已交 |
| 本 STATUS | 落地摘要 · 挂键 · 静态验收 |
| Java / jar / economy / runs / afk / 三开关 | **未动 / 未 stage** |

## 热更

- play **已热更**：`scripts/console.sh play "trmenu reload"` → 日志 `良好 | 71 个菜单已加载 (172 ms)`（**05:55:54 CST**）
- jar 仍 `1.65.108-d395.local` · **未** `/corerpg reload`

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 runtime 脏迹（常态） |
| C2 | **PASS** | cached 仅授权路径（TrMenu/docs） |
| C3 | **PASS** | 显式 `git add` 各路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live ember-v1；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

关观察 · 抬 afk / daily_kills · 开 gate_daily · 改 S40–S42 / daily_cap · 同号写 Java · 假写个人日帽 · 假挂旧日常 · sx04 本窗主债 · stage 脏 runtime · 复述 D373–D395 主规格

---

*D396 批 A·M · §2.3 菜单挂日帽 · tip 上游 `7b43f741` / 插件 `ee765f56` · ≠关观察 ≠抬日表。*
