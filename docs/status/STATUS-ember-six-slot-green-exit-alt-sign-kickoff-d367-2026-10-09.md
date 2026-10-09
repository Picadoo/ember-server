# 状态 · D367：Stage2 满窗另号签字启动前置模板（批 A·M · 已落字 · ≠关观察 ≠勾 §2.4）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-green-exit-alt-sign-kickoff-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-green-exit-alt-sign-kickoff-need-design-2026-10-09.md) @ `43f429fa` · DESIGN [`DESIGN-ember-six-slot-green-exit-alt-sign-kickoff-2026-10-09.md`](../design/DESIGN-ember-six-slot-green-exit-alt-sign-kickoff-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D367** · 总控采纳方案 M · 满窗另号签字启动前置模板已批 · tip 旁注已关 · OPS 半行已落 · **本号 ≠ 关观察 ≠ 勾选 §2.4 ≠ 开闸 ≠ 开 R ≠ 改日历 ≠ 改 live**  
**版本：** **docs-only**（DESIGN 勾批 · tip 关 · backlog · OPS §1.4 半行 · 本 STATUS）· jar / 三开关 / bv / ×0.97 / ×0.1 / TrMenu / 日历门槛 / `.gitignore` **未动**

## 人话

签字包（D338）= 签什么；本号 = 满窗后另号怎么起。模板已批；**现在不关窗、不勾 §2.4。**

## 模板已批（方案 M 摘要）

### 与已有包分工

| 项 | D338 签字包 | D342 现态复跑 | D364 值班 | D363 禁误读 | **本模板（另号启动）** |
|----|-------------|---------------|-----------|-------------|------------------------|
| 问题 | 签**什么** | 签字前查现态 | 观察期内班次薄检 | 「像关窗」短语勿误读 | **到点后另号怎么起** |
| 时机 | 满窗另号正文 | 满窗当日或签字前 ≤6h | 观察维持班次 | 任何「像关窗」措辞 | **时钟 ≥2026-10-10 17:40 CST 之后**开另号时 |
| 出口 | §2.4 三选一 | 粘贴关观察号 | 班次 OK / 红报 | ≠关观察 | 另号已合法启动；**本采纳号 ≠ 已关观察** |

### 硬前置 H1–H4（摘要）

| # | 项 | 合格 |
|---|----|------|
| H1 | 日历 | 上海时间 ≥ **2026-10-10 17:40 CST** |
| H2 | 模板真源 | D338 §2 仍为签字正文真源 |
| H3 | 声明 | 另号首行含满窗绿出口另号声明 |
| H4 | 禁同号偷关 | **本观察窗 / 本 tip 批 A 号**不得勾选 §2.4 |

### 启动顺序 S0–S7（摘要）

S0 时钟核 → S1 新开关观察另号 → S2 复制 D338 §2 → S3 D342 复跑 → S4 勾 G1–G5 → S5 确认 M1–M9 → S6 **仅此时**勾 §2.4 → S7 可选 R 门闩（不部署）。

真源：DESIGN §2.1–2.4。**批 A ≠ 关观察 ≠ 现在勾 §2.4 ≠ 开闸 ≠ 改日历。**

## 验收自检 G1–G3

| ID | 结果 | 备注 |
|----|------|------|
| G1 | **PASS** | tip/DESIGN/本 STATUS 含分工、H1–H4、S0–S7、骨架、禁做；批 A 已勾 |
| G2 | **PASS** | 明文批 A ≠ 关观察 ≠ 现在勾 §2.4 ≠ 改日历；正文真源仍 D338 |
| G3 | **PASS** | 本号未改 jar / 开关 / ×0.97 / ×0.1 / 日历门槛 / `.gitignore` / TrMenu |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-six-slot-green-exit-alt-sign-kickoff-2026-10-09.md` | STATUS→已批 A·M·D367；勾批 A；总控批注；变更记录 |
| tip `…-green-exit-alt-sign-kickoff-need-design-…` | 旁注已关 · 硬规格→已批 A·D367 |
| `design-ember-content-backlog.md` | `B-six-slot-green-exit-alt-sign-kickoff` → **已关 · 模板已采纳 · OPS 半行已落 · ≠关观察 ≠勾 §2.4** |
| `OPS-ember-six-slot-migration.md` §1.4 | +半行：满窗另号签字启动前置 H1–H4/S0–S7 → DESIGN/STATUS D367（≠现在关窗） |
| 本 STATUS | 模板摘要 · G1–G3 · **明文本号 ≠ 关观察 ≠ 勾 §2.4** |
| jar / 三开关 / bv / ×0.97 / ×0.1 / 日历门槛 / TrMenu / `.gitignore` | **未动** |
| `ladder.yml` / 脏 runtime / `p1-six/` / MM SavedData / 未授权 ember-v1 | **未 stage / 未碰** |

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权 docs 路径 |
| C3 | **PASS** | 显式 `git add` 各 docs 路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live / resources；未切分支 |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

关观察 · **勾选 §2.4** · 改日历 · 改 ignore · 开闸 · 开 R · 改 ×0.97 / ×0.1 · 复述 D360–D366 · 空转绿出口附录 · 脏 runtime stage · 改 live

---

*D367 批 A·M · tip `43f429fa` · 满窗另号签字启动前置模板已采纳 · OPS §1.4 半行已落 · ≠关观察≠勾§2.4≠改日历 · 零玩法。*
