# 状态 · D422：冒险页短征入口数字化（批 A·M 勾选旁注 + 菜单挂盘 · ≠关观察 ≠抬日表 ≠sx14）

**日期：** 2026-10-10（上海时间）  
**上游 tip：** `f206dcf9` · STATUS-ember-next-hard-debt-adventure-short-day-fc-visible-need-design · DESIGN-ember-adventure-short-day-fc-visible  
**裁决：** **已批 A · 方案 M · D422** · W1a 冒险 **H** 解锁态挂 `%corerpg_p1_sx_day_left_sum%` + `%corerpg_p1_sx_fc_left%`/`sx_fc_pending_line` 必做 · W1b Open 半行同批 · **零 jar** · **钉死 H 解锁态** · 灰锁禁个人数字 · **≠关观察** · **≠抬日表** · **≠开 gate_daily** · **≠动 Stage2** · **≠交 sx14** · **未派测**  
**toplevel：** `/workspace/minecraft` @ `main`

## 批 A 勾选（旁注）

- [x] **方案 M**（W1a 冒险 H 挂日帽合计+首通合计必做 + W1b Open 半行同批）
- [x] 否决 A（只口号）/ L（抬表·改包·假承诺）/ W（sx14/复述/假写）
- [x] 批注：总控批 A·M · D422 · W1a+W1b

## 人话

打开冒险页看短征入口，解锁态可见「今日剩余有奖」合计与「生涯首通未领」合计（真 PAPI）。灰锁（未通 Q01）不挂个人数字。打开菜单半行提示「短征入口可看今日剩余有奖/首通未领」。不改日帽/首通包/体力/S40–S52、不新 Layout、不假写固定 N。

## 改动

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_adventure.yml` | H 解锁态 lore 挂 day_left_sum + fc_left + pending_line + 诚实半行；Open +「短征入口可看今日剩余有奖/首通未领」；顶注 D422；灰锁未挂个人数字 |
| 本 STATUS | 批旁注 + 菜单号结案 |
| DESIGN / tip / backlog | 批 A·M 勾选 + 挂盘已落回填 |

## 验收（本号 · 菜单纪律 · 宣称可测通 ≠ 整本 PASS）

| ID | 结果 | 备注 |
|----|------|------|
| V1 未满可见 day_left_sum | **PASS（菜单）** | H 挂 `%corerpg_p1_sx_day_left_sum%`；非假写固定数 |
| V2 有未领首通可见 fc | **PASS（菜单）** | H 挂 `sx_fc_left` + `pending_line` |
| V3 满额诚实态 | **可测通（菜单）** | 依赖 PAPI 满额文案；本号未假喊还能领今日有奖 |
| V4 未通 Q01 灰态 | **PASS（菜单）** | 灰锁 lore 无个人 day_left/fc 数字 |
| V5 点 H | **PASS（菜单）** | H → `ember_p1_short`；未新 Layout 主格 |
| V6 文案纪律 | **PASS（菜单）** | 无「保证今天升阶」；无旧日常窟；无 sx14；无挂机产魂尘；无教裸指令 |
| 零假写 / 零 jar / Layout / 钉死 H | **PASS** | 仅 PAPI；未改 jar；未新主格；静态规则行保留 |
| trmenu reload | **PASS** | `良好 | 73 个菜单已加载 (77 ms)`（**2026-10-10 09:56:47 CST**） |
| 派测 | **未派** | 测试另号 · 宣称可测通 ≠ 整本 PASS |

## 不动

- jar / daily_kills / afk.tiers / 日帽 / S40–S52 / 体力30 / 首通包 / Stage2 三开关 / gate_daily / ×0.97  
- 选页 day_line/fc / 枢纽可追 / 仓差·remain·ActionBar·next_farm 主交付（保留）  
- sx14 / Pack6 / talent / ash / R  

## 指针

| 项 | 路径 |
|----|------|
| DESIGN | [`DESIGN-ember-adventure-short-day-fc-visible-2026-10-10.md`](../design/DESIGN-ember-adventure-short-day-fc-visible-2026-10-10.md) |
| tip（旁注已关） | [`STATUS-ember-next-hard-debt-adventure-short-day-fc-visible-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-adventure-short-day-fc-visible-need-design-2026-10-10.md) |
| backlog | `B-adventure-short-day-fc-visible`（**已批·挂盘已落**） |

## 本号 commit 自检（D365）

| ID | 结果 |
|----|------|
| C1–C5 | **PASS** · 显式路径 add；未 `add -A`；未碰 live Stage2；未切分支；未 stash/reset/checkout |

---

*D422 · 冒险页短征入口数字化 · 上游 tip `f206dcf9` · ≠关观察≠抬日表≠sx14 · 未派测。*
