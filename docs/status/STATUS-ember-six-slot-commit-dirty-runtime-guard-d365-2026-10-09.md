# 状态 · D365：Stage2 commit 前脏 runtime 防误 stage 薄清单（批 A·M · 已落字 · ≠关观察）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-commit-dirty-runtime-guard-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-commit-dirty-runtime-guard-need-design-2026-10-09.md) @ `a728c2a0` · DESIGN [`DESIGN-ember-six-slot-commit-dirty-runtime-guard-2026-10-09.md`](../design/DESIGN-ember-six-slot-commit-dirty-runtime-guard-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D365** · 总控采纳方案 M · commit 前脏 runtime 防误 stage 薄清单已批 · tip 旁注已关 · OPS 半行已落 · **≠关观察** · **≠改 ignore** · **≠开闸** · **≠开 R** · **≠勾选 §2.4** · **≠改 live**  
**版本：** **docs-only**（DESIGN 勾批 · tip 关 · backlog · OPS §1.5 半行 · 本 STATUS）· jar / 三开关 / bv / ×0.97 / ×0.1 / TrMenu / 日历门槛 / `.gitignore` **未动**

## 人话

观察期提交要有脏 runtime 黑名单——不 stage ≠ 清仓；≠改 ignore、≠关窗。

## 清单已批（方案 M 摘要）

### 与 D344 / D364 分工

| 项 | D344 live 防冲 | D364 值班 D6 | **本清单（commit 侧）** |
|----|----------------|--------------|-------------------------|
| 风险 | resources/checkout **盖掉** live 保护键 | 班次发现 live 已被盖 | **误把运行时脏迹 commit 进仓** |
| 时机 | 改 yml / 切分支 / 部署前 | 班次只读 | **每次 `git add` / `commit` 前** |
| 动作 | 禁 src→live；skip-worktree | 红报；不拧 | **显式路径 add；禁宽 add；误 stage 则 unstage** |
| 出口 | live 真源完好 | STATUS 红项 | 提交 diff **不含** §2.3 黑名单 |

### 黑名单 R1–R6（观察期默认勿 stage）

| 类 | 路径（示例） | 默认 |
|----|--------------|------|
| **R1** 天梯/灾厄态 | `ladder.yml` · `calamity-state.yml` | **勿 stage** |
| **R2** 六槽迁移记录 | `p1-six/` | **勿 stage**；亦禁手改 |
| **R3** 玩家/公会实例 | `players/*.yml`（除已批例）· `guilds/*.yml` · `mail/<uuid>.yml` | **勿 stage** |
| **R4** MM 运行态 | `MythicMobs/SavedData/**` | **勿 stage** |
| **R5** 日志/临时 | `logs/` · `/workspace/tmp/**` | **勿入仓**（除非另签） |
| **R6** 保护键 live（未授权） | `ember-v1.yml` · `ember-v1-runs.yml` | **勿擅自 stage** |

### 提交前勾选 C1–C5

| ID | 项 | 预期 |
|----|----|------|
| **C1** | `git status --short` 已读 | 已知脏 runtime |
| **C2** | `git diff --cached --name-only` | **无** R1–R6（除非总控明文授权） |
| **C3** | 未使用禁宽 add | 无未审 `add -A` / `add plugins/CoreRpg` |
| **C4** | 与 D344 | 未 resources→live；未主仓危险切分支 |
| **C5** | 结论 | `脏 runtime 未入暂存 · 可 commit` |

真源：DESIGN §2.1–2.7。**批 A ≠ 关观察 ≠ 改 ignore ≠ 开闸。**

## 验收自检 G1–G3

| ID | 结果 | 备注 |
|----|------|------|
| G1 | **PASS** | tip/DESIGN/本 STATUS 含分工、黑名单、add 节奏、勾选、禁做；批 A 已勾 |
| G2 | **PASS** | 明文批 A ≠ 关观察 ≠ 改 ignore ≠ 开闸；本号自身未 stage 黑名单 |
| G3 | **PASS** | 本号未改 jar / 开关 / ×0.97 / ×0.1 / 日历门槛 / `.gitignore` / TrMenu |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-six-slot-commit-dirty-runtime-guard-2026-10-09.md` | STATUS→已批 A·M·D365；勾批 A；总控批注；变更记录 |
| tip `…-commit-dirty-runtime-guard-need-design-…` | 旁注已关 · 硬规格→已批 A·D365 |
| `design-ember-content-backlog.md` | `B-six-slot-commit-dirty-runtime-guard` → **已关 · 清单已采纳 · OPS 半行已落 · ≠关观察 ≠改 ignore** |
| `OPS-ember-six-slot-migration.md` §1.5 | +半行：commit 前脏 runtime 黑名单 R1–R6 → DESIGN/STATUS D365（≠改 ignore ≠关观察） |
| 本 STATUS | 清单摘要 · G1–G3 |
| jar / 三开关 / bv / ×0.97 / ×0.1 / 日历门槛 / TrMenu / `.gitignore` | **未动** |
| `ladder.yml` / 脏 runtime / `p1-six/` / MM SavedData / 未授权 ember-v1 | **未 stage / 未碰** |

## 本号 commit 自检（按本清单）

| ID | 结果 | 备注 |
|----|------|------|
| C1 | **PASS** | 工作区有 ladder/calamity-state/p1-six/MM SavedData 脏迹（常态） |
| C2 | **PASS** | cached 仅授权 docs 路径 |
| C3 | **PASS** | 显式 `git add` 各 docs 路径；未用 `add -A` |
| C4 | **PASS** | 未碰 live / resources；未切分支 |
| C5 | **PASS** | 脏 runtime 未入暂存 · 可 commit |

## 不动

关观察 · 勾选 §2.4 · 改 ignore / 取消跟踪 · 开闸 · 开 R · 改 ×0.97 / ×0.1 · 复述 D360–D364 · 空转绿出口附录 · 脏 runtime stage · 改 live

---

*D365 批 A·M · tip `a728c2a0` · commit 前脏 runtime 防误 stage 薄清单已采纳 · OPS §1.5 半行已落 · ≠关观察≠改 ignore≠开闸 · 零玩法。*
