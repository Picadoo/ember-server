# 状态 · 下一档硬债选定 · 需策划（新短本房 · P1 可达 + 经济回盘 · docs-only · ≠关观察 · ≠开闸 · ≠开 R）

> **旁注（已关 · 批 A · 批 M · D391）：** tip `234dea63` 已关；总控已批 A·M；STATUS [`STATUS-ember-short-dungeon-sx01-d391-2026-10-10.md`](STATUS-ember-short-dungeon-sx01-d391-2026-10-10.md) · 骨架启动（地图本地+DP壳+菜单）· 进本键/MM/S40 并行 · **≠关观察 ≠抬日表 ≠开 R ≠放开 gate_daily**。

> **【已关 · 已批 A · 批 M · D391】** tip+DESIGN 已批 · 骨架启动 · **≠关观察 ≠抬日表 ≠开 R ≠开 K3 ≠假可达 ≠空跳转 ≠又一个 PAPI 同屏当主交付 ≠复述 D373–D389 为主交付** · **总控点名主题①**

> **上游结案：** 总控批 A·M · D390 内容展示波 exhausted @`9d9dfd04` → **点名主题①：新短本房（含 P1 可达 + 经济回盘）**；暂不②挂机第5层 / ③Stage4。Stage2 观察照续（≥**2026-10-10 17:40 CST**，**≠关窗 ≠改×0.97**）。

**日期：** 2026-10-10（上海时间）  
**本窗性质：** tip **已关** · **已批 A·M·D391** · **零 jar / 零开关 / 零价表 / 零部署** · **≠关观察** · **≠开闸** · **≠开 `k3_refine`** · **≠抬挂机日表** · **≠ stage 脏 runtime** · **内容真债（够施工拆号）**  
**硬规格（已批 A · 批 M · D391 · 骨架启动 · ≠关观察 ≠抬日表 ≠开 R ≠放开 gate_daily）：** [`DESIGN-ember-short-dungeon-p1-reachable-2026-10-10.md`](../design/DESIGN-ember-short-dungeon-p1-reachable-2026-10-10.md) · backlog `B-short-dungeon-p1-reachable`  
**打开理由：** 展示波已 exhausted；旧七线日更在 P1 下 **LegacyGate 故意不可达**（D198/D202）；玩家缺一条「今天能打、5–8 分钟、打完有收获」的短本循环。总控点名本主题。

---

## 0. 局势一句话

旧日更打不通；主线/团本偏长或门槛高——需要一条 **P1 原生短征**（新入口 + 薄经济），不是重开 `EmberDaily*` 假宣传。

---

## 1. 选题

| # | 候选 | 裁决 | 理由（证据） |
|---|------|------|--------------|
| A | 把 `ember_daily` 挂回 P1 枢纽 / 放开 `gate_daily` | **否** | D198 S0-1/2 关旧日常；旧掉落未进 p1sim；假可达=假平面；help 已写「旧日常窟已收起」 |
| B | 第八条 `EmberDaily*` + 只改菜单文案 | **否** | 仍走 LegacyGate/`TicketEntryService` DAILY；经济未建模；与点名「P1 可达+经济回盘」不符 |
| C | 挂机第 5 层 / Stage4 整包 | **否本窗** | 总控暂不点名；抬 tiers / 大包另派 |
| D | 又一个 PAPI 同屏 / 空跳转 / 复述 D373–D389 | **否** | 展示波刚 exhausted；硬禁 |
| E | 抬 `daily_kills` / afk.tiers「补偿短本」 | **否** | 硬禁抬日表 |
| **F** | **P1 原生「短征」新本（sx01）· 可达 + 薄经济回盘** | **采纳 · 需策划** | 见 DESIGN；对齐点名① |

### 1.1 扫证（本机 · 2026-10-10）

- `LegacyGate` / D198：`%corerpg_gate_daily%` P1=`no`；`TicketEntryService` 拒旧 DAILY kind；D202 普通玩家打不开 `ember_daily`。
- `ember_daily.yml` + `EmberDaily*`：七线 Cast/可躲齐，但入口仅 legacy；help「旧日常窟已收起」。
- P1 进本真路径：`ember_hub` → `ember_p1_adventure` → `corerpg enter q0x` / `corerpg p1 enter r0x`；体力主线 **30** / 团本 **50**。
- 经济：S01 主线基线；S22 挂机 `daily_kills=2400` + tiers；工坊 C03–C06 吃碎片/骨尘/胚料/币——短本奖励应入 **vault 同账户**，**不**抬挂机表。
- 无现成「短征 / sx / EmberSx」DP/菜单草稿；map-plan 有日更真实地形约束可指针。
- backlog：D390 已点名本主题；Stage4 / T5 暂不。

**荐方案 M：** docs-only **P1 短征 sx01 规格**——独立入口（非重开旧日常）+ 体力对齐 + 多房 5–8 分 + 真实地形 + 薄表 S40 回盘工坊。**批 A ≠ 施工 ≠ 关观察 ≠ 抬日表 ≠ 开 R。**

**方案 W（否决）：** 假可达宣传旧七线 / 只加空菜单 / 抬挂机日表当奖励 / 又一个 PAPI 当主交付。

---

## 2. 派单句

见 DESIGN 同 slug · STATUS=待批 A · 方案 M（新短本房 P1 可达 + 经济回盘 · 短征 sx01）。

---

## 3. 暂不做什么

不交关观察签字；不勾选 D338 §2.4；不开 R；不开 K3 live；不抬 `daily_kills`/afk.tiers；不改 ×0.97；不放开 `gate_daily` / 不重开 `EmberDaily*` 玩家入口；不复述 D373–D389 为新主交付；不以 PAPI 同屏当本债主交付；**本 tip 窗零代码、且本号自身不得 stage 脏 runtime**。

---

## 4. 建议（批 A 后）

1. **施工拆号（见 DESIGN §4）：** 地图 → MM/Cast → DP+进本键 → TrMenu → 经济 S40/金样（可另号数值模拟）。  
2. **满窗路径：** ≥**2026-10-10 17:40 CST** → 仍按 D367 另号绿出口（**≠**本号关窗）。  
3. **禁：** 借批 A 偷开样本 R / Pack6 / 天赋灰印 / 改 ×0.97 / 开 K3 / 抬挂机表。

---

*选题 F · tip 已关 · 批 A·M · D391 骨架启动 · ≠关观察 · ≠开闸 · ≠抬日表 · ≠放开旧日常闸。*
