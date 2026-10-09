# 状态 · 下一档硬债选定 · 需策划（K3 worktree ↔ 主仓路径/cwd/docs 禁混用防呆 · docs-only · ≠开闸 · ≠关观察）

> **上游结案：** Stage2 观察续（绿出口仍 ≥**2026-10-10 17:40 CST**，**勿交关窗 · 勿勾 §2.4**）。D367 满窗另号签字启动前置已落 @ `8d09075c`（**≠关观察 · 勿复述**）。D360–D366 已落（**勿复述**）。禁开 R / 开 K3 live / 关观察 / 菜单诚实复扫 / 附录空转 / Pack6 / 天赋 / 灰印 / 改 ×0.97 / 改 ×0.1 / 翻 set_bonus / 改日历 / 改 ignore。**零玩法 · 零开开关。**

**日期：** 2026-10-09（上海时间）  
**本窗性质：** tip **待批 A** · **零 jar / 零开关 / 零价表 / 零部署** · **≠关观察** · **≠开闸** · **≠开 `k3_refine`** · **≠ stage 脏 runtime**  
**硬规格（STATUS=待批 A · 荐方案 M）：** [`DESIGN-ember-six-slot-k3-worktree-cwd-mix-2026-10-09.md`](../design/DESIGN-ember-six-slot-k3-worktree-cwd-mix-2026-10-09.md) · backlog `B-six-slot-k3-worktree-cwd-mix`  
**打开理由：** D359 钉了 worktree **已就位**与禁主仓切分支；D344 钉了 live 防冲；D362 钉了「已就位≠开闸」短语——**缺**操作员每次动 shell 前「我在哪棵树」的 cwd/toplevel 闸，以及主仓 docs 真源 vs wt 陈旧 docs 的指针防混。本机核验：wt `@9ff421a8` **无** D359/D367 DESIGN，且自带 `plugins/CoreRpg/ember-v1.yml`（≠观察服 live 真源）。本债只收口**路径/cwd/docs 禁混用**，**不**开闸、**不**复述 D360–D367。

---

## 0. 局势一句话

两棵树都在盘上，缺「先认 cwd 再动手」与「观察期 docs 只认主仓」——写成禁混用薄单；本窗不开闸。

---

## 1. 选题

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 开 R / 开 K3 live / 关观察 / 改 ×0.97 / 改 ×0.1 / 翻 set_bonus / 改日历 / 改 ignore | **否** | 硬禁 |
| B | 复述 D360–D367（旁注/注记/禁误读/值班/防 stage/指标读法/另号启动） | **否** | 硬禁；刚落 |
| C | 附录空转 / 菜单诚实复扫 / 已 PASS 薄抽再交 | **否** | 硬禁 |
| D | 禁误读短语 / 注记清单 / 值班勾选 / commit 黑名单 / 报告读法 / 另号启动前置（同类空转） | **否** | 总控禁再交同类 |
| E | 观察期「禁手改保护键」一页纸 | **否** | 与 D344 §2.2 / OPS §1.5 **大半重复** |
| F | 本轮无可交别轨薄债 | **否** | 有真缺口 H（路径/cwd/docs 禁混；D367 tip 曾标后置，现升主） |
| **H** | **K3 worktree ↔ 主仓路径/cwd/docs 禁混用防呆** | **采纳 · 需策划** | 证据见下 |

**证据：**
- D359 §2.2 = worktree **就位表** + 禁主仓切分支 + 禁 wt→live 盖 yml——**无**每次 shell 前 `show-toplevel`/`pwd` 闸，**无**「观察期 docs 真源=主仓」钉死。
- D344 / OPS §1.5 = live 防冲 + skip-worktree——**不**管「人进了错树」。
- D362 = 「已就位≠开闸」**短语**——**不是** cwd/路径作业单。
- D365 = commit 侧脏 runtime 黑名单——默认假设已在主仓；**不**防在 wt 里改观察 docs / 误认 wt `plugins` 为 live。
- D367 tip 选题表 E 行曾标本项 **后置**（「D359/D344/D362 已覆盖大半；易空转」）——扫后确认**未覆盖**：本机 `git worktree list` → 主仓 `/workspace/minecraft` @ `main` · wt `/workspace/minecraft-wt-d341` @ `feat/d341-k3-refine-offline` `9ff421a8`；wt **有** `plugins/CoreRpg/ember-v1.yml`；wt **无** `DESIGN-ember-six-slot-k3-offline-prep-2026-10-09.md` / `DESIGN-ember-six-slot-green-exit-alt-sign-kickoff-2026-10-09.md`（docs 分叉陈旧）。
- 风险：在 wt 里写观察 STATUS、引用 wt docs 当现态真源、或对 wt `plugins` 做「值班只读」误当观察服。

**荐方案 M：** docs-only **路径/cwd/docs 禁混用薄清单**——双树身份卡 + shell 前 toplevel 闸 + 动作路由表 + docs 真源=主仓 + 禁混红线 + 可选 OPS §1.4 半行指针；**批 A ≠ 开闸 ≠ 关观察 ≠ 开 `k3_refine` ≠ 改 live**。

**为何别轨 / 为何不重复 D360–D367：** 本债是 **「我在哪棵树 / docs 认哪份」操作闸**，不是样本旁注、白板注记、K3/关窗禁误读短语、值班勾选、commit 防 stage、指标读法、或满窗另号启动前置。零玩法、零开开关；不复述刚落正文。与 D359 分工：D359=预备怎么就位；本债=就位后如何不混用。

---

## 2. 派单句

见 DESIGN 同 slug · STATUS=待批 A · 荐方案 M。

---

## 3. 暂不做什么

不交关观察签字；不勾选 D338 §2.4；不开 R；不开 K3 live / 不部署 / 不开 `k3_refine`；不改 ×0.97 / ×0.1 / set_bonus / 日历 / ignore；不复述 D360–D367 正文；不空转绿出口附录；不菜单诚实复扫；不重写 D344/D359/D362 正文；**本 tip 窗零代码、且本号自身不得 stage 脏 runtime**。

---

*选题 H · tip 待批 A · 荐 M · K3 worktree↔主仓 cwd/docs 禁混用 · ≠开闸 · ≠关观察。*
