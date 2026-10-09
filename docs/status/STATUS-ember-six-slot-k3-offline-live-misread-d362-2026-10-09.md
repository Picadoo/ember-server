# 状态 · D362：K3 offline/live 禁误读短语对照（批 A·M · 已落字 · ≠开闸≠部署）

**日期：** 2026-10-09（上海时间）  
**上游：** tip [`STATUS-ember-next-hard-debt-six-slot-k3-offline-live-misread-need-design-2026-10-09.md`](STATUS-ember-next-hard-debt-six-slot-k3-offline-live-misread-need-design-2026-10-09.md) @ `70a4efb1` · DESIGN [`DESIGN-ember-six-slot-k3-offline-live-misread-2026-10-09.md`](../design/DESIGN-ember-six-slot-k3-offline-live-misread-2026-10-09.md)  
**裁决：** **已批 A · 批 M · D362** · 总控采纳方案 M · 禁误读对照已批 · tip 旁注已关 · OPS 半行已落 · **≠开闸** · **≠部署** · **≠开 k3_refine** · **≠关观察** · **≠开 R**  
**版本：** **docs-only**（DESIGN 勾批 · tip 关 · backlog · OPS §1.4 半行 · D359/D341 旁注指针 · 本 STATUS）· jar / 三开关 / bv / ×0.97 / ×0.1 / TrMenu / worktree 内容 **未动**

## 人话

「已就位 / PASS / 批 A」写多了像绿灯——钉一张「这些词 ≠ 开闸」单；清单、开关、闸条件都不动。

## 对照已批（方案 M 摘要）

### 短语 → 不等于（运维一眼）

| 文档常见短语 | **不等于** | 真源 |
|--------------|------------|------|
| worktree **已就位** / `feat/d341-k3-refine-offline` | ≠开闸 ≠部署 ≠开 `k3_refine` | D359 · D344 |
| **T0‴ PASS** / `blank-probe` PASS | ≠施工令 ≠部署 | D328 · D341 §2.3 |
| **D341 / D359 / 本页 批 A** | ≠开闸；仅采纳文档 | 各文首 |
| OPS「施工/部署**等**绿出口」 | ≠绿出口已签 | OPS §1.4 · D338 G1 |
| D338 **R 门闩「再议 K3」** | ≠部署；须回 D341 §2.1 | D341 §2.5 |

### 开闸唯一真源（重申 · 不改条件）

| 路径 | 条件 |
|------|------|
| **A（默认）** | Stage2 **绿出口已签**「观察结束 · 维持 bv62 / set_bonus」 |
| **B（例外）** | 总控**另签**「观察期并行施工 · K3」独立 STATUS |

二者皆无 → **闸关**。本对照**不**增加第三条开闸路径。

真源：DESIGN §2.1–2.5。

## 验收自检 K1–K3

| ID | 结果 | 备注 |
|----|------|------|
| K1 | **PASS** | tip/DESIGN/本 STATUS 含 §2.1 对照、§2.2 开闸真源、何时引用、禁做；批 A 已勾 |
| K2 | **PASS** | 明文批 A ≠ 开闸 ≠ 部署 ≠ 开 `k3_refine` |
| K3 | **PASS** | 本号未改 jar / 开关 / ×0.97 / ×0.1 / TrMenu / worktree |

## 改动清单（本号）

| 文件 | 改动 |
|------|------|
| `DESIGN-ember-six-slot-k3-offline-live-misread-2026-10-09.md` | STATUS→已批 A·M·D362；勾批 A；总控批注；变更记录 |
| tip `…-k3-offline-live-misread-need-design-…` | 旁注已关 · 硬规格→已批 A·D362 |
| `design-ember-content-backlog.md` | `B-six-slot-k3-offline-live-misread` → **已关 · 对照已采纳 · OPS 半行已落 · ≠开闸** |
| `OPS-ember-six-slot-migration.md` §1.4 | +半行：禁误读短语（已就位/PASS/批A ≠开闸）→ DESIGN/STATUS D362 |
| D359 STATUS / D341 STATUS | 旁注一行指针本表（仍 ≠开闸） |
| 本 STATUS | 对照摘要 · K1–K3 |
| jar / 三开关 / bv / ×0.97 / ×0.1 / `k3_refine` / TrMenu / worktree | **未动** |
| `ladder.yml` / 脏 runtime / `p1-six/` | **未 stage / 未碰** |

## 不动

开闸 · 部署 K3 jar · 开 `k3_refine` · 关观察 · 开 R · 改 ×0.97 / ×0.1 · 复述 D359 预备逐步 · 重写 D341 §2.1 · 脏 runtime stage

---

*D362 批 A·M · tip `70a4efb1` · K3 offline/live 禁误读对照已采纳 · OPS §1.4 半行已落 · ≠开闸≠部署≠开 k3_refine · 零玩法。*
