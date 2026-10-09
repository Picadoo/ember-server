# 状态 · D366：Stage2 观察指标报告读法口径（批 A·M · 已落字 · ≠关观察 ≠改系数）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-observe-metrics-report-read-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-observe-metrics-report-read-need-design-2026-10-09.md) @ `b85b72ba` · DESIGN [`DESIGN-ember-six-slot-observe-metrics-report-read-2026-10-09.md`](../design/DESIGN-ember-six-slot-observe-metrics-report-read-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D366** · 总控采纳方案 M · 观察指标报告读法口径已批 · tip 旁注已关 · OPS 半行已落 · **≠关观察** · **≠改 ×0.97** · **≠开闸** · **≠开 R** · **≠勾选 §2.4** · **≠改 live**  
**版本：** **docs-only**（DESIGN 勾批 · tip 关 · backlog · OPS §1.4 半行 · 本 STATUS）· jar / 三开关 / bv / ×0.97 / ×0.1 / TrMenu / 日历门槛 / `.gitignore` **未动**

## 人话

档 C / set_bonus / ×0.97 / D325 日志在报告里怎么读——钉口径；系数不动、≠关窗。

## 口径已批（方案 M 摘要）

### 与已有清单分工

| 项 | D326 §2 必抽 | D342 / D364 | D363 | **本口径（报告读法）** |
|----|--------------|-------------|------|-------------------------|
| 问题 | 测什么才算 PASS | 班次/签字复跑查现态 | 关窗短语勿误读 | **写/读报告时指标各字段表示什么、禁止推出什么** |
| 时机 | 观察抽测号 | 班次 / 满窗前预检 | 任何「像关窗」措辞 | **每次写观察相关 STATUS / 读证据包** |
| 动作 | 跑 M1–M9 | 只读勾选 | 短语对照 | **按 §2.2–2.3 读字段；禁 §2.3 推断** |
| 出口 | PASS/FAIL | 红报或不拧 | ≠关观察 | 报告语义正确；**≠**改 live |

### 读什么（合法含义 · 摘要）

| 报告常见写法 | **合法读成** |
|--------------|--------------|
| 档 C / Stage2 set bonus | 激活受伤侧减伤方案；现网观察中 |
| `set_bonus=true` | 线上开关开着；仍须满足激活条件 |
| ×0.97 / −3% | 激活且条件满足时该次受伤乘区；钉死值 |
| D325 日志行 | 该次受伤套装乘区已应用；与同事件 `[P1伤害]` 至多一条 |
| `final 0.78` | 采样世界证据样例；仅证据 |
| bv=62 / 三开关全 true | 构建钉点 / 观察维持现态 |
| M1–M9 整轮 PASS | 必抽 ≥1 轮过；**不是**已关观察 |

### 不读成什么（禁推断 · 摘要）

| 见到… | **不等于** |
|-------|------------|
| ×0.97 / −3% | ≠授权拧数字 |
| M2 PASS / 见 D325 行 | ≠关观察 ≠勾选 §2.4 |
| `set_bonus=true` | ≠开 K3 ≠开样本 R |
| `final 0.78` | ≠玩家面 KPI |
| 本页批 A | ≠关观察 ≠改系数 ≠开闸 |

真源：DESIGN §2.1–2.4。**批 A ≠ 关观察 ≠ 改系数 ≠ 开闸。**

## 验收自检 G1–G3

| ID | 结果 | 备注 |
|----|------|------|
| G1 | **PASS** | tip/DESIGN/本 STATUS 含分工、读什么、不读成、写报纪律；批 A 已勾 |
| G2 | **PASS** | 明文批 A ≠ 关观察 ≠ 改 ×0.97 ≠ 开闸 ≠ 开 R |
| G3 | **PASS** | 本号未改 jar / 开关 / ×0.97 / ×0.1 / 日历门槛 / `.gitignore` / TrMenu |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-six-slot-observe-metrics-report-read-2026-10-09.md` | STATUS→已批 A·M·D366；勾批 A；总控批注；变更记录 |
| tip `…-observe-metrics-report-read-need-design-…` | 旁注已关 · 硬规格→已批 A·D366 |
| `design-ember-content-backlog.md` | `B-six-slot-observe-metrics-report-read` → **已关 · 口径已采纳 · OPS 半行已落 · ≠关观察 ≠改系数** |
| `OPS-ember-six-slot-migration.md` §1.4 | +半行：观察指标报告读法 → DESIGN/STATUS D366（≠改系数 ≠关观察） |
| 本 STATUS | 口径摘要 · G1–G3 |
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

关观察 · 勾选 §2.4 · 改 ignore · 开闸 · 开 R · 改 ×0.97 / ×0.1 · 复述 D360–D365 · 空转绿出口附录 · 脏 runtime stage · 改 live

---

*D366 批 A·M · tip `b85b72ba` · 观察指标报告读法口径已采纳 · OPS §1.4 半行已落 · ≠关观察≠改系数≠开闸 · 零玩法。*
