# 状态 · D368：K3 worktree ↔ 主仓路径/cwd/docs 禁混用防呆（批 A·M · 已落字 · ≠开闸 ≠关观察）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-k3-worktree-cwd-mix-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-k3-worktree-cwd-mix-need-design-2026-10-09.md) @ `b66697fc` · DESIGN [`DESIGN-ember-six-slot-k3-worktree-cwd-mix-2026-10-09.md`](../design/DESIGN-ember-six-slot-k3-worktree-cwd-mix-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D368** · 总控采纳方案 M · cwd/docs 禁混用清单已批 · tip 旁注已关 · OPS 半行已落 · **本号 ≠ 开闸 ≠ 关观察 ≠ 开 `k3_refine` ≠ 改 live ≠ 开 R ≠ 改 ignore**  
**版本：** **docs-only**（DESIGN 勾批 · tip 关 · backlog · OPS §1.4 半行 · 本 STATUS）· jar / 三开关 / bv / ×0.97 / ×0.1 / TrMenu / 日历门槛 / `.gitignore` **未动**  
**toplevel（落字时）：** `/workspace/minecraft` @ `main`（C0 合格）

## 人话

worktree 已就位，缺「先认树再动手」。禁混用清单已批；**不开闸、不关观察、不改 live。**

## 清单已批（方案 M 摘要）

### 与已有包分工

| 项 | D359 离线预备 | D344 live 防冲 | D362 禁误读 | D365 commit 黑名单 | **本清单（cwd/docs 禁混）** |
|----|---------------|----------------|-------------|--------------------|------------------------------|
| 问题 | worktree **怎么就位** | live **别被盖** | 「已就位」等**短语**勿当开闸 | **误 stage** 脏 runtime | **人在错树动手** / **docs 认错份** |
| 出口 | wt 已就位 ≠开闸 | live 保护键完好 | ≠开闸声明 | diff 无黑名单 | 动作落在正确树；观察 docs 只出自主仓 |

### 双树身份（摘要）

| 树 | 路径 | 角色 |
|----|------|------|
| **主仓** | `/workspace/minecraft` @ `main` | Stage2 观察真源 · live yml · 观察期 docs 真源 |
| **K3 wt** | `/workspace/minecraft-wt-d341` @ `feat/d341-k3-refine-offline` | 闸关离线 Java/单测 · **≠** 观察 live · **≠** 观察期 docs 真源 |

### Shell 前闸 C0–C3 + 路由（摘要）

C0 `show-toplevel` → C1 分支对照 → C2 自报 cwd → C3 按路由表动手。观察 tip/DESIGN/STATUS/OPS → **主仓**；K3 Java/单测 → **wt**；禁 wt→live 盖 yml；禁主仓为 K3 切分支。

真源：DESIGN §2.1–2.6。**批 A ≠ 开闸 ≠ 关观察 ≠ 改 live ≠ 开 R。**

## 验收自检 G1–G3

| ID | 结果 | 备注 |
|----|------|------|
| G1 | **PASS** | tip/DESIGN/本 STATUS 含分工、身份卡、C0–C3、路由、docs 真源、禁做；批 A 已勾 |
| G2 | **PASS** | 明文批 A ≠ 开闸 ≠ 关观察 ≠ 改 live；docs 真源=主仓 |
| G3 | **PASS** | 本号未改 jar / 开关 / ×0.97 / ×0.1 / 日历门槛 / `.gitignore` / TrMenu / live |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-six-slot-k3-worktree-cwd-mix-2026-10-09.md` | STATUS→已批 A·M·D368；勾批 A；总控批注；变更记录 |
| tip `…-k3-worktree-cwd-mix-need-design-…` | 旁注已关 · 硬规格→已批 A·D368 |
| `design-ember-content-backlog.md` | `B-six-slot-k3-worktree-cwd-mix` → **已关 · 清单已采纳 · OPS 半行已落 · ≠开闸 ≠关观察** |
| `OPS-ember-six-slot-migration.md` §1.4 | +半行：K3 wt↔主仓 cwd/docs 禁混用 → DESIGN/STATUS D368（≠开闸） |
| 本 STATUS | 清单摘要 · G1–G3 · **明文本号 ≠ 开闸 ≠ 关观察** |
| jar / 三开关 / bv / ×0.97 / ×0.1 / 日历门槛 / TrMenu / `.gitignore` | **未动** |
| `ladder.yml` / 脏 runtime / `p1-six/` / MM SavedData / 未授权 ember-v1 | **未 stage / 未碰** |

## 本号 commit 自检（按 D365）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权 docs 路径 |
| C3 | **PASS** | 显式 `git add` 各 docs 路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live / resources；未切分支；toplevel=`/workspace/minecraft` |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

开闸 · 开 `k3_refine` · 关观察 · 勾选 §2.4 · 改日历 · 改 ignore · 开 R · 改 ×0.97 / ×0.1 · 复述 D360–D367 · 空转绿出口附录 · 脏 runtime stage · 改 live · 进 wt 写观察 docs 当真源

---

*D368 批 A·M · tip `b66697fc` · K3 wt↔主仓 cwd/docs 禁混用已采纳 · OPS §1.4 半行已落 · ≠开闸≠关观察≠改 live · 零玩法。*
