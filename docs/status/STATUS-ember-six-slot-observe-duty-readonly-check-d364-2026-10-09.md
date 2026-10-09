# 状态 · D364：Stage2 观察值班只读核对薄清单（批 A·M · 已落字 · ≠关观察）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-observe-duty-readonly-check-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-observe-duty-readonly-check-need-design-2026-10-09.md) @ `f1031f8a` · DESIGN [`DESIGN-ember-six-slot-observe-duty-readonly-check-2026-10-09.md`](../design/DESIGN-ember-six-slot-observe-duty-readonly-check-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D364** · 总控采纳方案 M · 值班只读核对薄清单已批 · tip 旁注已关 · OPS 半行已落 · **≠关观察** · **≠改配置** · **≠开闸** · **≠开 R** · **≠勾选 §2.4**  
**版本：** **docs-only**（DESIGN 勾批 · tip 关 · backlog · OPS §1.4 半行 · 本 STATUS）· jar / 三开关 / bv / ×0.97 / ×0.1 / TrMenu / 日历门槛 **未动**

## 人话

满窗前值班要有班次只读单——核对≠关窗，异常只报不拧；不替代签字前整表复跑。

## 清单已批（方案 M 摘要）

### 与 D342 分工

| 项 | D342 现态复跑 | **本清单（值班）** |
|----|---------------|-------------------|
| 时机 | 满窗当日或签字前 ≤6h（另号） | 观察维持期内班次（开班/交接） |
| 目的 | 关观察号 G3–G5 | 确认观察仍维持；异常早报 |
| 深度 | R0–R10 全表 | 薄：D1–D10 |
| 出口 | 可签结束 / 继续观察 / 回滚评估 | **只记 STATUS**；**永不**本号勾 §2.4 |

### 班次勾选项（D1–D10 · 只读）

| ID | 核对项 | 预期 |
|----|--------|------|
| D1 | 现态三开关 | `enabled` / `migrate` / `set_bonus` **均为 true** |
| D2 | bv | **62** |
| D3 | ×0.97 / set_bonus | 档 C 未动；`set_bonus=true` |
| D4 | 绿出口日历 | 未提前勾 §2.4；本班不交关窗 |
| D5 | K3 仍 offline | `k3_refine` 关/缺省；闸关 |
| D6 | 脏 yml 未盖 live | live 真源完好（D344） |
| D7 | 白板注记指针 | 相关报告必写「模型未覆盖」（D361） |
| D8 | 禁误读指针 | 开闸→D362；关窗→D363；≠行动授权 |
| D9 | 样本 R / Pack6 | 无未授权开 R / Pack6 |
| D10 | 汇总 | 班次 OK·观察续 / 有红项已报·观察续 / 须事故评估（仍≠本号关窗） |

真源：DESIGN §2.1–2.6。**批 A ≠ 关观察 ≠ 改配置 ≠ 开闸。**

## 验收自检 G1–G3

| ID | 结果 | 备注 |
|----|------|------|
| G1 | **PASS** | tip/DESIGN/本 STATUS 含 §2.1 分工、§2.3 勾选、§2.4 输出表、禁做；批 A 已勾 |
| G2 | **PASS** | 明文批 A ≠ 关观察 ≠ 改配置 ≠ 开闸；异常只记 STATUS |
| G3 | **PASS** | 本号未改 jar / 开关 / ×0.97 / ×0.1 / 日历门槛 / TrMenu |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-six-slot-observe-duty-readonly-check-2026-10-09.md` | STATUS→已批 A·M·D364；勾批 A；总控批注；变更记录 |
| tip `…-observe-duty-readonly-check-need-design-…` | 旁注已关 · 硬规格→已批 A·D364 |
| `design-ember-content-backlog.md` | `B-six-slot-observe-duty-readonly-check` → **已关 · 清单已采纳 · OPS 半行已落 · ≠关观察** |
| `OPS-ember-six-slot-migration.md` §1.4 | +半行：班次只读核对 D1–D10 → DESIGN/STATUS D364（≠关观察） |
| 本 STATUS | 清单摘要 · G1–G3 |
| jar / 三开关 / bv / ×0.97 / ×0.1 / 日历门槛 / TrMenu | **未动** |
| `ladder.yml` / 脏 runtime / `p1-six/` | **未 stage / 未碰** |

## 不动

关观察 · 勾选 §2.4 · 改配置 · 开闸 · 开 R · 改 ×0.97 / ×0.1 · 复述 D360–D363 · 空转绿出口附录 · 脏 runtime stage · 替代 D342 签字复跑

---

*D364 批 A·M · tip `f1031f8a` · 观察值班只读核对薄清单已采纳 · OPS §1.4 半行已落 · ≠关观察≠改配置≠开闸 · 零玩法。*
