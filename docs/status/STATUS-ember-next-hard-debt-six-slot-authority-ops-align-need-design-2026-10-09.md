# 状态 · 下一档硬债选定 · 需策划（六槽权威文档 + OPS 对齐 · docs-only）

> **旁注（已关 · 批 A · 批 M · D330）：** tip `80d41c5c` 已关；总控已批 A·M；施工 STATUS [`STATUS-ember-six-slot-authority-ops-align-d330-2026-10-09.md`](STATUS-ember-six-slot-authority-ops-align-d330-2026-10-09.md)。

> **上游结案：** D329 装备页护甲入口诚实 **已落地** @ `7f1b165e`（TrMenu + reload · 零数值）。Stage2 观察中（绿出口不早于 **2026-10-10 17:40 CST** · 必抽 PASS）。K3 T0‴ 已签 PASS（`b085e945`）· **施工等绿出口 · 本窗不抢签**。护甲页三态 / 候选 `_fam` 已诚实。样本 R 全表不得开；天赋/灰印 HOLD；Pack6 硬禁；禁改 ×0.97 / 拧 set_bonus / 观察期部署 K3。

**日期：** 2026-10-09（上海时间）  
**本窗性质：** **只 docs tip + 硬规格** · **未改** yml / jar / 菜单 · **零 live**  
**硬规格（已批 A · 批 M · D330）：** [`DESIGN-ember-six-slot-authority-ops-align-2026-10-09.md`](../design/DESIGN-ember-six-slot-authority-ops-align-2026-10-09.md) · backlog `B-six-slot-authority-ops-align`  
**打开理由：** 观察期要继续推进；玩家面菜单刚收一刀（D329），下一刀证据最硬的是 **权威文档仍写「六槽未上线」** + **OPS 仍写「线上 1.65.97 / 功能没有」且无 `set_bonus` 节**——运维误读风险高；纯 docs，观察期可批 A。

---

## 0. 局势一句话

D329 已落地，**不要再做装备页护甲入口**。护甲页候选套装态已有 `_fam`。下一档若开 R / 改 ×0.97 / 部署 K3 = 假活。改采纳 **六槽权威文档 + OPS 对齐勘误（docs-only）**。

---

## 1. 选题（扫描后裁决）

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| 1 | 护甲页候选缺套装态 | **否** | 已有 `%corerpg_p1_armor_*_fam%`（核实） |
| 2 | 工坊甲灰显「后续开放」 | **否** | `ARMOR_REFUSE`=「护甲随护符成长；成色和精工看掉落」诚实 |
| 3 | D12-② 分解 checklist | **否（本窗主交付）** | 可做，偏测残余；本窗优先权威/OPS 误读 |
| **H** | **权威文档 + OPS 对齐** | **采纳** | 见 §证据 |
| 5=H 内 | OPS 缺 set_bonus | **纳入 H** | 同交付 |
| 6 | 其它薄诚实 | — | D329 刚收装备入口 |

**证据（硬）：**
1. `DESIGN-ember-gear-structure-2026-10-06.md` §1：「6 槽 / 8 槽 **未上线**… Stage 1 未开」——与线上 **bv62 · enabled+migrate+set_bonus** 矛盾。  
2. 同文 §9：仍写 Stage1「代码待显式开工」。  
3. `OPS-ember-six-slot-migration.md` 文首：「线上目前仍跑 **1.65.97**，这里所有功能线上都没有」；§1 仅两开关，**无** `gear.six_slot.set_bonus`。  
4. `docs/design/staged/d318-six-slot/README.md`：仍写四件套「**后续开放**」。

**荐方案 M：** 勘误清单页——改 gear-structure §1/§9 指针、OPS 文首+增补 set_bonus 观察/回滚段（引用 D326）、staged README 一句；backlog 过时六槽指针旁注；**零 jar/yml 玩法**；批 A 后另号 docs 施工。

---

## 2. 一句话问题

线上已是 F+Stage2 C，但权威装备文档与 OPS 仍按「未部署 / 无四件套开关」写——观察期运维与策划会误判。

---

## 3. 派单句

见 [`DESIGN-ember-six-slot-authority-ops-align-2026-10-09.md`](../design/DESIGN-ember-six-slot-authority-ops-align-2026-10-09.md) · **已批 A · 批 M · D330**（原荐 M）。

---

## 4. 暂不做什么

不改 ×0.97 / set_bonus / jar / bv；不部署 K3；不开 R；不重做 D329 装备入口；不改价表 / F / 护符×1.0。本 tip **零 live**。

---

## 5. 指针

- D329 @ `7f1b165e` · `B-six-slot-armor-gear-honesty` 已落地  
- D326 观察 · D325 live bv62 · D328 T0‴ PASS（K3 施工等绿出口）  
- 真源：gear-structure · OPS · staged README

---

*选题 H · 已批 A·M · D330 已落字 · 纯 docs · 禁偷开 R / K3 部署 / 改 ×0.97。*
