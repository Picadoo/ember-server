# 状态 · D314：六槽 Stage1 T0 总控签字 → 回 HOLD（docs-only）

**日期：** 2026-10-08（上海时间）  
**上游：** D312 已批 B（[`STATUS-ember-six-slot-stage1-decision-d312.md`](STATUS-ember-six-slot-stage1-decision-d312.md)）→ D313 T0/T0'（[`STATUS-ember-six-slot-stage1-t0-d313.md`](STATUS-ember-six-slot-stage1-t0-d313.md) · 报告 [`tools/p1sim/out-six-slot-stage1-t0.md`](../../tools/p1sim/out-six-slot-stage1-t0.md) · `4f91a300`）  
**版本：** CoreRpg **1.65.97** 未动 · bv **60** 未动 · **零 Java / yml / NI / TrMenu / 部署** · p1sim `SIX` 默认仍 None

## 人话

六槽护甲的离线复算做完了：单人、刷图、挂机都能调到线内，**但团本不过**——3～4 人随机组队通关率比现在低 3.6～4.7 个点，换哪种取整、哪种比例都一样。原因是打团本那段时间护甲普遍落后护符一截，这是六槽结构本身的缺口，不是这一轮允许调的几个参数能补的。所以六槽**回到搁置**，线上什么都没变。

## 总控签字

> T0/T0' 未过（G8 团本结构性缺口）→ 回 HOLD，线上零变化。重开条件：策划另交护甲追赶/团本甲来源设计稿并经总控批，再另号 T0″。T1–T3 不开。

| 项 | 结果 |
|---|---|
| G8 团本（T0' 变体 A） | 3/9：R01 3 人 −4.5 [−6.8, −2.2]、R02 4 人 −4.7 [−6.5, −3.0]、R03 4 人 −3.6 [−4.9, −2.3] pp ✗ |
| 浮点原方案 / 变体 B | 同样 3/9 → 与取整、比例无关 |
| 其余门禁（T0' A） | G0–G7 + G8 挂机过（动态 21/21、挂机 21/21） |
| G6 备忘 | 将来若进 T1：Java 迁移须走定点 / 同档短路（same-tier carry-over），golden 单测逐位相等（已记入报告 §7） |

## 本号改动（只 docs）

| 文件 | 改动 |
|---|---|
| `docs/status/STATUS-ember-six-slot-stage1-t0-d313.md` | 签字框：回 HOLD [x] + 签字原文 + G6 备忘 |
| `tools/p1sim/out-six-slot-stage1-t0.md` | §7 签字框：回 HOLD [x] + G6 Java 备忘行 + 签字原文（只改签字框，数据不动） |
| `docs/design/DESIGN-ember-six-slot-stage1-decision-2026-10-08.md` | STATUS 改「已批 B → T0 未过 · 回 HOLD · D314」；§4 加 D314 签字批注；§7 变更记录；文末 |
| `docs/design/design-ember-content-backlog.md` | `B-six-slot-stage1-decision` 指针 → HOLD（T0 未过 · 重开条件） |
| 本文件 | 新增 |

## 不动

任一 R 窗 · D308 表（当前全表不得开）· Pack6 · 天赋 row1 / 灰印 HOLD · D309 副手方案 A · 刃 / 护符掉率 · 价表 · event_rate · 体力 · 怪物数值 · p1sim 默认行为 · T1–T3（不开）

## 重开条件

策划另交「护甲追赶 / 团本甲来源」设计稿（例如团本 / 挑战另掉甲、甲升阶补差），经总控批 → 另号 T0″ 重跑 G0–G9（重点 G8 团本）。在此之前六槽不进任何施工号。
