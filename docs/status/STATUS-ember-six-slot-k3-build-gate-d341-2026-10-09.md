# 状态 · D341：K3 施工闸等待页（批 A·M · 采纳闸页 · ≠开闸≠部署）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-k3-build-gate-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-k3-build-gate-need-design-2026-10-09.md) @ `d3f6e77d` · DESIGN [`DESIGN-ember-six-slot-k3-build-gate-2026-10-09.md`](../design/DESIGN-ember-six-slot-k3-build-gate-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D341** · 总控采纳施工闸等待页 · **批 A ≠ 开闸 ≠ 部署**  
**版本：** **docs only**（DESIGN · tip 旁注 · backlog · 本 STATUS）· jar / ×0.97 / `set_bonus` / enabled / migrate / bv / K3 玩法 / TrMenu / 玩法公式 **未动** · **本号未开闸、未部署**

## 人话

> **旁注（D359）：** 离线预备清单已批 · [`STATUS-ember-six-slot-k3-offline-prep-d359-2026-10-09.md`](STATUS-ember-six-slot-k3-offline-prep-d359-2026-10-09.md) · worktree **已就位** `/workspace/minecraft-wt-d341` → `feat/d341-k3-refine-offline` @ `9ff421a8` · **仍 ≠开闸**。

T0‴ 已 PASS（D328），但缺一页说清「何时能动 K3 码/换 jar」。本号只批等待闸真源：开闸条件、闸关可预备、禁项、与 D338 衔接。**批 A 只采纳闸页**；开闸须 D338 绿出口已签（路径 A）或总控另签并行（路径 B）。闸关可做离线预备，**禁 live**。

## 批注要点

| 项 | 口径 |
|----|------|
| 采纳 | DESIGN §2 施工闸等待页为开闸前真源（方案 M） |
| 批 A | **仅采纳闸页** · **≠开闸** · **≠部署** · 不代替 §2.1 |
| 开闸 | 须 **D338 绿出口已签** **或** 另签「观察期并行施工 · K3」 |
| 闸关允许 | 读规则/证据 · 隔离分支+单测 · docs 指针 · **不**装线上 play |
| 闸关禁止 | live/共享测服换 K3 jar · 开 `k3_refine` · 借 PASS/本页当施工令 · 改 ×0.97 / 拧 set_bonus · 提前关观察 · Pack6/天赋/灰印/样本 R |
| 观察 | **不**提前关；关观察走 D338；本号观察维持 |
| live | **零** jar / yml / 开关 / 菜单 / 价表 |

## 改动（本号）

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-six-slot-k3-build-gate-2026-10-09.md` | STATUS→已批 A·M·D341；勾批 A；总控批注；变更记录 |
| tip `…-k3-build-gate-need-design-…` | 旁注已关 · 硬规格→已批 A·D341 |
| backlog `B-six-slot-k3-build-gate` | → **已批 A · 闸页已采纳 · ≠开闸≠部署** |
| backlog `B-six-slot-k3-same-slot-refine` / D327 tip 行 | 旁注闸页 D341（≠开闸） |
| 本 STATUS | 批 A · 闸页采纳声明 |
| jar / CoreRpg yml / 开关 / bv / TrMenu | **未动** |

## 给开闸号（另号 · 须满足 §2.1）

| 路径 | 条件 |
|------|------|
| A（默认） | Stage2 绿出口已签「观察结束 · 维持 bv62 / set_bonus」（D338 §2；日历 ≥**2026-10-10 17:40 CST**） |
| B（例外） | 总控另签「观察期并行施工 · K3」（独立 STATUS；写范围与回滚；仍禁默改 ×0.97 / 关 Stage1） |

开闸后另号施工：引用本页 §2.1 + D327 §2 规则；建议 `k3_refine` 默认关；回滚=关开关/必要时回退 jar。**本号不勾选开闸。**

## 不动

×0.97 · set_bonus / enabled / migrate · bv · jar · K3 施工/部署 · 样本 R / Pack6 / 天赋 / 灰印 · 关 Stage2 观察 · 关 Stage1 · 本号实机 / live

---

*D341 批 A·M · tip `d3f6e77d` · docs-only · 采纳 K3 施工闸等待页 · ≠开闸≠部署 · 观察期薄窗。*
