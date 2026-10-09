# 状态 · D332：D12-② 掉落甲分解验收清单（批 A·M · 授权测试抽测）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-d12-armor-dismantle-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-d12-armor-dismantle-need-design-2026-10-09.md) @ `09a387a3` · DESIGN [`DESIGN-ember-six-slot-d12-armor-dismantle-2026-10-09.md`](../design/DESIGN-ember-six-slot-d12-armor-dismantle-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D332** · 总控采纳清单 · **授权另号测试**执行 D12-②-A…G  
**版本：** **docs only**（DESIGN · tip 旁注 · backlog · 本 STATUS）· jar / 价表 / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / TrMenu / 玩法公式 **未动** · **本号未跑实机**

## 人话

D12-① 结算掉甲线上已 PASS；掉落甲工坊分解（×0.1 阶→胚零头、迁移件拒绝）还缺一份可执行验收清单。本号只批清单、授权测试另号抽测 A–G；不改任何分解公式或价表，也不挡 Stage2 绿出口日历，不抢 K3。

## 批注要点

| 项 | 口径 |
|----|------|
| 采纳 | DESIGN §2 验收清单为唯一真源（方案 M） |
| 执行 | **另号测试**跑 A–G；测服优先，线上可选管理号；**不碰真人档** |
| 公式 | **零改** `armorDismantleTenths` / `dismantleCheck` / 价表 / ×0.97 |
| 日历 | **不挡** Stage2 绿出口（D324：D12-② 可后续补） |
| K3 | **不抢**（施工仍等绿出口） |
| 注记 | 报告须写「白板经济模型未覆盖」（D318） |

## 改动（本号）

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-six-slot-d12-armor-dismantle-2026-10-09.md` | STATUS→已批 A·M·D332；勾批 A；总控批注；变更记录 |
| tip `…-d12-armor-dismantle-need-design-…` | 旁注已关 · 硬规格→已批 A·D332 |
| backlog `B-six-slot-d12-armor-dismantle` | → **已批 A · 待测试执行** |
| 本 STATUS | 批 A · 授权测试抽测 |
| jar / CoreRpg 公式 / 价表 / 开关 / bv | **未动** |

## 派给测试（另号）

执行 DESIGN §2.2：

| ID | 焦点 |
|----|------|
| A | drop 甲分解预览 ×0.1 阶 |
| B | 确认分解：件作废 + 零头 |
| C | 零头跨 10 → 整胚 +1 |
| D | migrate 拒绝 |
| E | 其它非 drop 拒绝（缺样可 NA） |
| F | K0：强化/精工/成色仍拒，仅分解可用 |
| G | `p1 audit` 无非预期 ACTIVE_NOT_HELD |

**绿出口（测试号）：** A–G 全 PASS（E 可 NA）+ 报告含「白板经济模型未覆盖」→ 总控另号签 D12-② PASS。  
**中止：** 误拆 migrate、×1.0 整胚、资产双失 → 停抽测、报总控；**不**自动改代码。

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · 分解公式 / 价表 · K3 施工 · 样本 R / Pack6 / 天赋 / 灰印 · 本号实机

---

*D332 批 A·M · tip `09a387a3` · docs-only · 授权另号测试 · 观察期薄窗。*
