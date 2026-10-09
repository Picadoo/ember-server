# 状态 · 下一档硬债选定 · 需策划（commit 前脏 runtime 防误 stage 薄清单 · docs-only · ≠关观察）

> **上游结案：** Stage2 观察续（绿出口仍 ≥**2026-10-10 17:40 CST**，**勿交关窗**）。D364 值班只读清单已落 @ `0e75b7aa`（**≠关观察 · 勿复述**）。D363 勿提前关窗 / D362 K3 禁误读 / D361 白板注记 / D360 样本旁注已落（**勿复述**）。禁开 R / 开 K3 live / 关观察 / 菜单诚实复扫 / 附录空转 / Pack6 / 天赋 / 灰印 / 改 ×0.97 / 改 ×0.1。**零改公式 · 零开开关。**
>
> **选题说明：** 扫后「短语对照 / 注记 / 值班清单」同类再交易空转。本债换类：D344 覆盖 live 真源与 resources→live / 主仓切分支，**未**钉 commit 侧「哪些脏 runtime 不得 `git add`」的可执行清单；现网工作区常驻 `ladder.yml` / `calamity-state.yml` / `p1-six/` 等脏迹，STATUS 仅散落写「未 stage 脏 runtime」。

**日期：** 2026-10-09（上海时间）  
**本窗性质：** tip **打开 · STATUS=待批 A** · **零 jar / 零开关 / 零价表 / 零部署** · **≠关观察** · **≠开闸** · **≠改配置** · **≠ stage 脏 runtime**  
**硬规格（待批 A · 荐 M · ≠关观察）：** [`DESIGN-ember-six-slot-commit-dirty-runtime-guard-2026-10-09.md`](../design/DESIGN-ember-six-slot-commit-dirty-runtime-guard-2026-10-09.md) · backlog `B-six-slot-commit-dirty-runtime-guard`  
**打开理由：** 观察期 docs/功能提交高频；被跟踪的 runtime 文件（`ladder.yml`、`calamity-state.yml` 等）与未跟踪 `p1-six/` 常驻脏。`git add -A` / 宽路径 `add plugins/CoreRpg` 易把脏态入仓。D344 防的是**冲掉 live 保护键**；D364 D6 防的是**resources→live 覆盖**——二者都**不是** commit 前「勿 stage 哪些路径」清单。本债只收口该清单，**不**改 ignore、**不**改 live、**不**关观察。

---

## 0. 局势一句话

观察期提交要有 commit 前脏 runtime 黑名单——防误 stage，≠改配置、≠关窗。

---

## 1. 选题

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 开 R / 开 K3 live / 关观察 / 改 ×0.97 / 改 ×0.1 | **否** | 硬禁 |
| B | D360–D364 复述 / 附录空转 / 短语对照再交 | **否** | 硬禁；刚落 |
| C | 菜单诚实复扫 / 已 PASS 薄抽再交 | **否** | D358 后停 |
| D | 重写 D344 live 防冲 / 改 skip-worktree 政策 | **否** | 已批；本债只补 commit 侧 |
| E | Pack6 / 天赋 / 灰印 / 样本 R | **否** | HOLD / 硬禁 |
| F | 立刻改 `.gitignore` / 取消跟踪 ladder 等 | **否（本窗）** | 过厚；易碰运维策略；另号 |
| **H** | **commit 前脏 runtime 防误 stage 薄清单** | **采纳 · 需策划** | 真缺口；证据见下 |

**证据：**
- D344 / OPS §1.5：真源、禁 resources→live、禁主仓切分支、skip-worktree——**无**「commit 前勿 stage 路径表」。
- D344 验收 A4 仅写「无误 stage 脏 runtime」——**无**文件名单与命令节奏。
- D364 D6：班次查「脏 yml 未盖 live」——运行时覆盖面，**≠** `git add` 黑名单。
- 现网（本机扫）：`git status` 常现 `M plugins/CoreRpg/ladder.yml`、`M plugins/CoreRpg/calamity-state.yml`、`?? plugins/CoreRpg/p1-six/`、MythicMobs `SavedData` 等——docs-only 提交若不显式路径 add 易误入。
- 多份 STATUS（D346/D348/D363 等）反复自检「未 stage 脏 runtime」——口径散落、无统一勾选。

**荐方案 M：** docs-only **commit 前脏 runtime 防误 stage 薄清单**——黑名单路径 + 推荐 `git add` 节奏（显式路径、禁宽 add）+ 误 stage 回退命令 + 与 D344/D364 分工表；可选 OPS §1.5 半行指针；**批 A ≠ 改 ignore ≠ 关观察 ≠ 开闸 ≠ 改 live**。

**为何别轨 / 为何不重复 D360–D364：** 本债是 **git commit 侧路径防呆**，不是样本旁注、白板注记、K3/关窗禁误读短语、或值班只读现态核对。零玩法、零开开关；不复述刚落正文。

---

## 2. 派单句

见 DESIGN 同 slug · STATUS=待批 A · 荐 M。

---

## 3. 暂不做什么

不交关观察签字；不勾选 D338 §2.4；不开 R；不开 K3 live / 不部署 / 不开 `k3_refine`；不改 ×0.97 / ×0.1 / set_bonus / 日历门槛；不复述 D360–D364 正文；不空转绿出口附录；不菜单诚实复扫；不改 `.gitignore` / 不取消跟踪（另号）；**本 tip 窗零代码、且本号自身不得 stage 脏 runtime**。

---

*选题 H · tip 打开 · 待批 A · 荐 M · commit 脏 runtime 防误 stage · ≠关观察 · ≠改配置 · ≠开闸。*
