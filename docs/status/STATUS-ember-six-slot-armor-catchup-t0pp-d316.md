# 状态 · D316：六槽护甲追赶 T0″ 离线复验（tools/docs only）

**日期：** 2026-10-08（上海时间）  
**上游：** D315 已批 F → 另号 T0″（[`STATUS-ember-six-slot-armor-catchup-d315.md`](STATUS-ember-six-slot-armor-catchup-d315.md)）· 设计稿 [`DESIGN-ember-six-slot-armor-catchup-2026-10-08.md`](../design/DESIGN-ember-six-slot-armor-catchup-2026-10-08.md)  
**报告：** [`tools/p1sim/out-six-slot-armor-catchup-t0pp.md`](../../tools/p1sim/out-six-slot-armor-catchup-t0pp.md) · 原始输出 [`tools/p1sim/out-six-slot-armor-catchup-t0pp/`](../../tools/p1sim/out-six-slot-armor-catchup-t0pp/)  
**版本：** CoreRpg **1.65.97** 未动 · bv **60** 未动 · **零 Java / yml / NI / TrMenu / 部署** · p1sim `SIX` 默认仍 None · 不开任何 R · T1–T3 未授权

## 人话

「护甲共鸣」（护甲的阶级和强化跟着护符走）在离线复算里**全部过线**：三到五人团本的通关率和现在两槽只差 0～1.6 个点（原来差 3.6～4.7 个点），单人、挂机、成长速度也都更接近现在。对照的「只跟强化」方案在单人 Q05 那一段会难 3 个点，不过线。线上什么都没动，下一步要不要进 T1 由总控签字决定。

顺带查清了一件事：团本那段护甲真正落后的是**成色和精工**，不是强化。旧规则下新掉的甲从 +0 开始，比不过身上强化过的旧甲，玩家就一直穿着成色差的老甲。共鸣之后甲只按成色和精工挑，这个问题自然就没了。

## 门禁（F）

> G4 完整 W30 仍在跑，补齐后更新本页。

| 门禁 | F | 判定 |
|---|---|---|
| G0 默认逐位 | selfcheck 0 失败；默认 / `--ref` 逐字节相同；T0' A 复现与 D313 逐位相同 | 过 |
| G1 静态 | 42/42 × 3，stats 与 2 槽逐位相同 | 过 |
| G2/G7 动态（F 天然整数口径） | 21/21 · 0 ✗ · 最贴边 0.5 Q07 +0.6 [+0.4, +0.8] | 过 |
| G3 刷图 W30 | −0.03 / −0.03 | 过 |
| G4 完整 W30 | 运行中 | 待补 |
| G5 / G6 | 0 漂移 / 101,376 逐位相等 | 过 |
| **G8 团本** | **9/9**（最差 R02 4 人 −1.6 [−2.7, −0.6]）· T0' A 复现 3/9 | **过** |
| G8 挂机 | 21/21（最大 +0.4） | 过 |
| G10 逐人落后 | 有效低阶件数 / 强化差全 0；H 中位 −0.46%（T0' A −1.24%）；剩余差来自甲精工 +1.77 档 | 过 |

E（对照）：G8 团本 6/9 点估、9/9 含噪声；**G2 0.5 Q05 −3.0 [−3.3, −2.6] ✗** → 不过。

## 本号改动

| 文件 | 改动 |
|---|---|
| `tools/p1sim/p1sim.py` | `SIX['follow']`（`all` / `enh`）：`hp_def()`、`invest()`；首通记录补精工字段。缺省逐位不变 |
| `tools/p1sim/six_t0.py` | raid 每池建一次池（结果逐位同 D313）、逐人状态入 JSON、G10 表、噪声内判定列 |
| `tools/p1sim/selfcheck.py` | +2 条 follow 断言 |
| `tools/p1sim/out-six-slot-armor-catchup-t0pp.md` + 目录 | 报告 + 原始输出（不含 pkl） |
| 本文件 | 新增 |

## 总控签字（空）

| 项 | 签认 |
|---|---|
| T0″ 过线（F）→ 可另号派 T1 规格修订（T1 仍需签字） | [ ] |
| 选 E | [ ] |
| 回 HOLD | [ ] |
| D169 Stage1 修订由暂认转正式 | [ ] |
| 签字 / 日期 | ________ |

## 不动

任一 R 窗 · D308 表 · Pack6 · 天赋 row1 / 灰印 HOLD · D309 副手方案 A · 刃 / 护符掉率 · 价表 · event_rate · 体力 · 怪物数值 · 团本产出 · T1–T3
