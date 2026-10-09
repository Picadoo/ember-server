# 状态 · 下一档硬债选定 · 需策划（D12-② 掉落甲分解验收清单 · docs-only）

> **上游：** D330 权威/OPS 落字 @ `422cc0f3`；D331 套装页诚实 **另轨施工中 · 本窗勿抢 `ember_set.yml`**。D12-① 结算掉甲线上 PASS（`b2cfc017`）；**D12-② 掉落甲分解 ×0.1 未跑**（D323/D324 可后续补）。Stage2 观察中；K3 施工等绿出口。禁 Pack6 / 天赋 / 灰印 / 改 ×0.97 / 拧 set_bonus / 动 F / 样本 R / K3 部署。

**日期：** 2026-10-09（上海时间）  
**本窗性质：** **只 docs tip + DESIGN** · **零代码 / 零价表 / 零 live jar/yml** · **不改 ember_set**  
**硬规格（待批 A）：** [`DESIGN-ember-six-slot-d12-armor-dismantle-2026-10-09.md`](../design/DESIGN-ember-six-slot-d12-armor-dismantle-2026-10-09.md) · backlog `B-six-slot-d12-armor-dismantle`  
**打开理由：** T3 残余 D12-② 仍缺可执行验收清单；观察期可钉 docs，抽测另号；不挡绿出口日历，但补齐资产路径诚实。

---

## 0. 局势一句话

结算掉甲已验；**分解路径**仍是观察期可补的硬缺口。本 tip 只交验收清单，不改玩法。

---

## 1. 选题

| # | 候选 | 裁决 |
|---|------|------|
| 套装页再拧 | **否** | D331 另轨，勿抢 ember_set |
| 权威/OPS 再勘误 | **否** | D330 已落 |
| **H · D12-② 分解验收清单** | **采纳** | D323 明确未跑；T3 §4.2 / T1 §1.4 已有口径 |

**证据：** live Q01 STATUS「D12-② 未跑」；T3 rollout §4.2「掉落甲分解 + 迁移件拒绝」；`armorDismantleTenths` = tier 十分位；`dismantleCheck` 非 `drop`（含 migrate）拒绝；单测已有、实机缺。

**荐 M：** docs-only 验收清单（步骤 / 预期 / 中止 / 岗位）；测服或线上抽测**另号**；本窗不改代码价表。

---

## 2. 派单句

见 DESIGN 同 slug · STATUS=待批 A · 荐 M。

---

## 3. 暂不做什么

不改代码 / 价表 / ×0.97 / set_bonus / jar；不部署 K3；**不改 ember_set.yml**；本 tip 窗不跑实机（批后另号测试）。

---

*选题 H · 荐 M · 零 live · 不抢 D331/K3。*
