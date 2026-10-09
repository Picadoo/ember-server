# 状态 · 下一档硬债选定 · 需策划（六槽工坊手持甲菜单诚实 · K0 只能分解 · docs-only）

> **旁注（已关 · 批 A · 批 M · D333）：** tip `5564ea55` 已关；总控已批 A·M；施工 STATUS [`STATUS-ember-six-slot-forge-armor-honesty-d333-2026-10-09.md`](STATUS-ember-six-slot-forge-armor-honesty-d333-2026-10-09.md)。

> **上游结案：** D332 已批 A·M（D12-② 验收清单 · 测试另号）@ `fd9fd732`；D329 装备页入口诚实、D330 权威/OPS、D331 套装页诚实均已落。Stage2 观察中（绿出口不早于 **2026-10-10 17:40 CST**）；K3 T0‴ PASS · 施工等绿出口。禁 Pack6 / 天赋 / 灰印 / 改 ×0.97 / 拧 set_bonus / 观察期部署 K3 / 动 F / 样本 R。**零 live jar 玩法施工（本稿只 docs）。**

**日期：** 2026-10-09（上海时间）  
**本窗性质：** tip 已关 · 施工见 D333 STATUS · 勿抢 D332 测试 / K3  
**硬规格（已批 A · 批 M · D333）：** [`DESIGN-ember-six-slot-forge-armor-honesty-2026-10-09.md`](../design/DESIGN-ember-six-slot-forge-armor-honesty-2026-10-09.md) · backlog `B-six-slot-forge-armor-honesty`  
**打开理由：** K0「甲只能分解」代码/PAPI 已诚实，但 **工坊菜单** 强化/升阶/精工/成色/互换格仍按刃护符视觉可点；分解静态 lore 仍写「胚料 1/2/3」，与甲 ×0.1 零头口径不一致——观察期可钉显示规格，施工另号。

---

## 0. 局势一句话

玩家手持甲进工坊：按钮看起来还能养成，点了才被拒；分解旁还写整胚 1/2/3。要让菜单与 K0 / D318 白板口径**同屏诚实**（≠ forge R 改价）。

---

## 1. 选题

| # | 候选 | 裁决 | 理由 |
|---|------|------|------|
| A | 样本 R / Pack6 / 天赋 / 灰印 / 改 ×0.97 / 部署 K3 | **否** | 硬禁 / HOLD / 观察期禁 |
| B | 重复 D329–D332 | **否** | 已落 / 测试另号 |
| C | 护甲候选 lore 套装态 | **否（本窗）** | 候选已有 `armor_*_fam`；非最尖 |
| D | 过时 tip 批量关 / backlog 清扫 | **否（本窗主交付）** | 维护薄债；可后置 |
| E | 绿出口前必抽复跑 checklist 薄页 | **否（本窗）** | D326 §2 + D327 M1–M9 已有；非新缺口 |
| **H** | **工坊手持甲菜单诚实（K0 只能分解）** | **采纳 · 需策划** | 证据见下 |

**证据：**
- `ember_p1_forge.yml`：无手持甲 icons 分支；E/U/R/Q/S 材质与确认键仍常驻可点；强化 lore「手持刃或护符」但甲时仍点命令；分解静态句「只限普通掉落件 → 胚料 1/2/3」**未**写白板零头。
- `EmberUpgradeRules.ARMOR_REFUSE` / K0：甲强化/精工/成色拒；分解仅 `src=drop`；`armorDismantleTenths` = tier 十分位。
- `EmberRunPapi.heldForgeLine` → `EmberSixPapi.heldArmorLine`：非分解返回 ARMOR_REFUSE；分解返回「白板胚 x.y」——**PAPI 已诚实，菜单壳未跟**。
- D332 清单 D12-②-F 已钉「K0 对照 · 仅分解可用」；本债补**玩家面菜单**对齐，不抢测试抽测号。

**荐方案 M：** docs-only 钉菜单补丁规格——手持甲时养成格灰显/短拒因；分解 lore 对齐 ×0.1 零头（可只改 TrMenu + 必要时只读 `held_is_armor` PAPI）；零改公式/价表/K0；施工另号。

---

## 2. 派单句

见 DESIGN 同 slug · STATUS=待批 A · 荐 M。

---

## 3. 暂不做什么

不改分解公式 / 价表 / ×0.97 / set_bonus / F / jar 玩法；不部署 K3；不抢 D332 测试号；不改 `ember_set.yml`；本 tip 窗零 live。

---

*选题 H · 荐 M · 零 live · 不重复 D329–D332。*
