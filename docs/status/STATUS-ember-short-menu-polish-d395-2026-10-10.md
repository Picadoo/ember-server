# 状态 · D395：短征三本菜单假平面清零（§2.1）· 日帽挂键等插件

**日期：** 2026-10-10（上海时间）  
**上游：** DESIGN [`DESIGN-ember-short-menu-polish-2026-10-10.md`](../design/DESIGN-ember-short-menu-polish-2026-10-10.md) · tip [`STATUS-ember-next-hard-debt-short-menu-polish-need-design-2026-10-10.md`](STATUS-ember-next-hard-debt-short-menu-polish-need-design-2026-10-10.md) @ `7b43f741` · 旁注 D394 tip `cf12a560`（金额已对齐）  
**裁决：** **已批 A · 批 M · D395** · 本号交 §2.1 TrMenu 假平面清零（零 jar）· **日帽挂键等插件**（§2.2/§2.3）· **≠关观察** · **≠抬日表** · **≠改 S40–S42** · **≠同号写 Java** · **≠假写个人 n/3** · **≠假开旧日常**  
**版本：** TrMenu + docs · jar / CoreRpg / MM / DP / economy / runs **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`

## 人话

短征选页三本奖励数字与实发一致，不再写「草案/另号未就绪」。今日有奖还剩几次要等插件补键后再挂到菜单上——本号不写假数字。

## 落地摘要（方案 M · §2.1）

| 窗 | 本号 |
|----|------|
| 旁注 D394 `cf12a560` | E/A/C 通关 80/4/3 + 首通薄包已齐；草案 70/5/3 / 另号就绪已去 |
| D395 实扫 | 玩家面 **零**「草案 / 另号 / 未就绪 / 币70」 |
| I 说明 | +「奖励以结算为准 · 日有奖各 3 次分开计」；静态「最多 3 次/日」 |
| Open | +「奖励以结算为准」 |
| A/C/E 日帽行 | **仍静态**「最多 3 次/日」（禁挂不存在 `%corerpg_p1_sx0N_day*%`） |
| update | A/C/E/I/S/B 已 `update: 20` |
| YAML 顶注 | 约定键名备忘（仅 `#` 注释，不进 lore） |
| actions / 门闩 / 体力 / 经济表 | **未动** |

## 日帽同屏（未挂真键）

| 项 | 口径 |
|----|------|
| jar | `1.65.107-d393.local` · `EmberRunPapi` / `CorePapi` **无** `p1_sx0N_day*` |
| 约定键（DESIGN §2.2） | `%corerpg_p1_sx01_day_line%` · `…sx02…` · `…sx03…`（及可选 `_day` / `_left_sum`） |
| 挂点（等插件 live） | A/C/E 主 lore+灰态 · I 说明 |
| 本号纪律 | **勿写不存在的 `%…%`** · 静态日帽合法临时态 |

## 验收自检（静态）

| ID | 结果 | 备注 |
|----|------|------|
| 假平面 | **PASS** | 玩家面无草案/另号/未就绪/70 |
| 金额 | **PASS** | S40/S41/S42 与 D394 实发一致 |
| 假写日帽 | **PASS** | 无个人 n/3 字面；无假 `%…%` |
| jar/开关/afk | **PASS** | 本号未触 |
| 日帽同屏 | **待** | 等插件 §2.2 → 菜单 §2.3 |

**活测（测岗 · §2.1）：** 开 `ember_p1_short` → 霜雾悬停无另号 → 通关预告 80/4/3。日帽 n/3 变数等插件后另号。

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `plugins/TrMenu/menus/ember_p1_short.yml` | 顶注 D395；I/Open 诚实半行；日帽仍静态 |
| `DESIGN-ember-short-menu-polish-2026-10-10.md` | 勾批 A·M · §7 落字 |
| `STATUS-ember-next-hard-debt-short-menu-polish-need-design-2026-10-10.md` | tip **已关** |
| `design-ember-content-backlog.md` | `B-short-menu-polish` → 已批·§2.1 已落·日帽等插件 |
| 本 STATUS | 落地摘要 |
| Java / jar / economy / runs / afk / 三开关 | **未动 / 未 stage** |

## 热更

- play：`scripts/console.sh play "trmenu reload"`（见落字后执行日志）
- **未** `/corerpg reload`（零 jar / 零开关）

## 下一号

1. **插件岗：** 注册 `%corerpg_p1_sx0N_day*%`（DESIGN §2.2）+ 单测；装 play。  
2. **菜单另号：** 挂 `day_line` 到 A/C/E/I；灰态同步。  
3. **测岗：** E 无草案；金额=S42；三本日帽随结算变；gate_daily 仍拒。

## 不动

关观察 · 抬 afk / daily_kills · 开 gate_daily · 改 S40–S42 / daily_cap · 同号写 Java · 假写个人日帽 · 假挂旧日常 · sx04 本窗主债 · stage 脏 runtime · 复述 D373–D393 主规格

---

*D395 批 A·M · §2.1 假平面清零 · 日帽挂键等插件 · tip 上游 `7b43f741` / D394 `cf12a560` · ≠关观察 ≠抬日表。*
