# DESIGN · 签名传奇效果持件 lore 诚实（薄窗 · 可施工）· 2026-10-12

> **状态：** OPEN · TIP-BH · 待批 A·M · 2026-10-12 23:24 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-sig-effect-lore-honesty-2026-10-12.md`](RESEARCH-ember-sig-effect-lore-honesty-2026-10-12.md)  
> **对接 live：** TIP-BG CLOSED live `1.65.796-d13031` · TIP-AE 烙印扩甲 · TIP-AJ 签名页摘要 · `EmberItems.applyLore` · `EmberSixSlot.armorLore` · `EmberGearLib` · `EmberSignature.DEFS`  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；Anvil soft densify / topUpTo / 灰印·聚火 / SF4 U2U3 **仍冻**  
> **硬禁：** 零功率表 · 零改 DEFS.mods/ALTS · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火**

---

## 0. 一句话

让背包持件 / 甲 / 仓库在写出「有签名」时，同时写出 **既有 good（行为）**（可选原版 bad）；不改战斗数字。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| 战斗 / mods | 签名行为已生效 | **不动** |
| 签名页 g_/b_ | 得/代价齐全 | **不动（SoT 展示源）** |
| 刃/护符 applyLore | 无签名效果行 | **sigCode>0 → 名+得（+代价）** |
| 甲 armorLore | 无签名行 | **同构** |
| GearLib 仓 | 名+good 粘连 · 无代价 | **与持件同构分隔** |
| DEFS / STAMP / 甲闸 | — | **零改** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改展示串 · 禁改 mods）

```
// 既有 — 零改 API 语义 / mods / ALTS / STAMP
EmberSignature.byCode(code) → Def { name, boss, good, bad }
EmberItemData.sigCode          // v2 权威；v1 本窗不发明 PD 读

EmberItems.applyLore (刃/护符) 伪码:
  … 既有 shortLabel / 成长 / 孔 / 来源 / map …
  if (d.sigCode > 0) {
    Def sg = byCode(d.sigCode);
    if (sg != null) {
      lore.add("§6签名 · §f" + sg.name + " §7（" + sg.boss + "）");
      lore.add("§a得 · §f" + sg.good);
      if (sg.bad != null && !sg.bad.isEmpty() && !"无".equals(sg.bad))
        lore.add("§c代价 · §7" + sg.bad);  // 原版字；调律 live 仍看签名页
    }
  }
  // 行数：+2～3；禁新 PAPI；禁读 PlayerData / sigAlt

EmberSixSlot.armorLore:
  同构（短标后插签名块）；禁改共鸣/成色公式

EmberGearLib 签名行:
  拆成与上同构 2～3 行（替换原粘连一行）；禁加第四教学墙
```

- **复用** `Def.good` / `Def.bad` 字面；**禁止**改 `mods` / `ALTS` / 烙印价 / STAMP。  
- **禁止**新 `%corerpg_p1_*`；**禁止** SigFeel.menuLine 效果墙 densify。  
- 调律 **live bad** 仍签名页；持件钉原版 bad（好处不变）。  
- SF4 / soft / 开孔 / Anvil / 灰印 **本窗不接线**。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `EmberItems.applyLore` | 刃/护符签名块 |
| `EmberSixSlot.armorLore` | 甲签名块 |
| `EmberGearLib` 仓 lore | 同构替换粘连行 |
| imprint / createNi 刷新 | 既有 `applyLore` 路径自然带上 |
| `EmberSignature` DEFS/ALTS | **本窗不改** |
| SigFeel / skill kit / SF4 | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| v2 刃 sigCode=L07 · applyLore | lore 含「潮闸长杆」+ good 含「直线」类字；无 mods 数字改 |
| v2 甲 sigCode>0 · armorLore | 含签名名 + good |
| sigCode=0 | **无**签名块 |
| bad=「无」 | **无**代价行 |
| GearLib | 不再名+good 无分隔粘连 |
| `git show` | 无 DEFS/mods/ALTS/STAMP/C12/loot/Affix/skill_mult/SF4/Anvil/soft 改；无新 PAPI；无甲闸/SetChase densify |

### 2.4 玩家口径（固定）

- 看着件就能读：**签名名 · 它改了什么打法（得）· 原版代价**。  
- 调律版当前代价仍在 **签名传奇页** 看。  
- 不改伤害表、不改掉率、不加新签名。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后插件：applyLore / armorLore / GearLib + 单测；**不**派物品  
- L13 施工仍 HOLD；Anvil / soft / SF4 U2U3 / 灰印仍冻  

## 4. 旁债（下一 prefer · 非本窗）

| # | 债 | 态 |
|---|----|----|
| 1 | Anvil soft densify / topUpTo | **仍冻** |
| 2 | 灰印 / 聚火 | **仍冻** |
| 3 | SF4 U2 / U3 | **仍冻** |
| 4 | L13 施工 | **HOLD** |
| 5 | 调律 live bad 进持件（须 PD） | 另 tip · 非本窗 |

## STATUS: OPEN · TIP-BH · 待批 · 2026-10-12
