# 状态 · D337：D12-②-C 跨 10 整胚补测清单（批 A·M · 授权测试抽测）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-d12-c-cross10-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-d12-c-cross10-need-design-2026-10-09.md) @ `ccebafea` · DESIGN [`DESIGN-ember-six-slot-d12-c-cross10-2026-10-09.md`](../design/DESIGN-ember-six-slot-d12-c-cross10-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D337** · 总控采纳补测清单 · **授权另号测试**执行 C0–C4  
**版本：** **docs only**（DESIGN · tip 旁注 · backlog · 本 STATUS）· jar / 价表 / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 / TrMenu / 玩法公式 **未动** · **本号未跑实机**

## 人话

D332 主路径 A/B/D/E/F/G 已 PASS，但 **C=NA**（样件不足未跨 10 十分位）。本号只批跨 10 补测清单、授权测试另号跑 C0–C4；**禁** admin/`p1 give` 冒充 drop；**不重跑** A–G；不改分解公式或价表；不挡 Stage2 观察日历；不抢 K3。

## 批注要点

| 项 | 口径 |
|----|------|
| 采纳 | DESIGN §2 补测清单为唯一真源（方案 M） |
| 执行 | **另号测试**跑 C0–C4（C5 可选）；测服优先，线上可选测号/管理号；**不碰真人档** |
| 样件 | **仅** `source=drop` 甲；**禁** admin / `p1 give` / migrate / quest 冒充 drop |
| 范围 | **只补 C**；**不重跑** A/B/D/E/F/G（偶发回归可记一笔） |
| 公式 | **零改** `armorDismantleTenths` / `addTenths` / 价表 / ×0.97 |
| 日历 | **不挡**观察（满窗仍须 ≥2026-10-10 17:40 CST） |
| K3 | **不抢**（施工仍等绿出口） |
| 注记 | 报告首行须写「白板经济模型未覆盖」（D318） |

## 改动（本号）

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-six-slot-d12-c-cross10-2026-10-09.md` | STATUS→已批 A·M·D337；勾批 A；总控批注；变更记录 |
| tip `…-d12-c-cross10-need-design-…` | 旁注已关 · 硬规格→已批 A·D337 |
| backlog `B-six-slot-d12-c-cross10` | → **已批 A · 待测试执行** |
| 本 STATUS | 批 A · 授权测试抽测 |
| jar / CoreRpg 公式 / 价表 / 开关 / bv | **未动** |

## 派给测试（另号）

执行 DESIGN §2.2（仅 C 族）：

| ID | 焦点 |
|----|------|
| C0 | 基线：整胚 `B0`、零头十分位 `T0` |
| C1 | 跨界规划：`need = 10 - (T0 % 10)`（若 T0%10=0 则 need=10）；只选 drop 甲 |
| C2 | 连续分解至累计十分位跨过下一个整十 |
| C3 | 跨 10 记账：`B1`/`T1` 与 `addTenths` 一致（通常恰好多 1 整胚） |
| C4 | 审计短抽：无非预期 ACTIVE_NOT_HELD 大面积 |
| C5（可选） | 再跨一次整十 |

**绿出口（测试号）：** C0–C4 PASS + 报告含「白板经济模型未覆盖」→ 总控另号签 **D12-②-C PASS**（可旁注回写 D332 STATUS / backlog）。  
**中止：** ×1.0 整胚、资产双失、非 drop 冒充、错账 → 停抽测、报总控；**不**自动改代码。

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · 分解公式 / 价表 · K3 施工 · 样本 R / Pack6 / 天赋 / 灰印 · 本号实机 · 重跑 A–G

---

*D337 批 A·M · tip `ccebafea` · docs-only · 授权另号测试 C0–C4 · 观察期薄窗。*
