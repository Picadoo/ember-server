# DESIGN · SinkGlance 护甲诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** OPEN · TIP-AM · docs + 可施工 · 2026-10-12  
> **上游 RESEARCH：** [`RESEARCH-ember-sinkglance-armor-honesty-2026-10-12.md`](RESEARCH-ember-sinkglance-armor-honesty-2026-10-12.md)  
> **对接 live：** TIP-AL CLOSED live `1.65.777-d12695`（CommitNext 甲诚实）· TIP-AK CLOSED live `1.65.776-d12679`（HeldNext 甲闸）· D318 K0 `ARMOR_REFUSE` · D680/D681 SinkGlance  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI `STAGE1_L13_OFFLINE=FAIL` · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零 STAMP 抬 · 零扩掉落 · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/**glance twin**/深化 densify/sx20/ForgeChoice densify · **零 SinkGlance 功率 densify**

---

## 0. 一句话

让 `EmberSinkGlance.tellHeld` 对手持护甲早退诚实句；开工会话不再假推成色/精工费用。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| K0 / HeldNext / CommitNext | 甲 → `ARMOR_REFUSE` / 甲诚实包 | 保持 |
| `SinkGlance.tellHeld` | 甲仍拼成色/精工/升阶费用+缺料 | **甲早退诚实** |
| forge 开入口预览三钮 | 甲也发 enhance/quality/refine | **甲改诚实短包** |
| 刃/护符一瞥 | 已 SHIP | **不变** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（一处修好）

`EmberSinkGlance.tellHeld`：

```
after resolve held EmberItemData d:
  if (d != null && d.isArmor()) {
    // chat: ARMOR_REFUSE (± optional dismantle half-line / tenths progress)
    // actionbar: 消耗 · 护甲随护符 | 分解零头
    return;  // never qualityLine/craftLine/upgradeLine / quality·refine lackHalf
  }
  // blade/charm: existing bits + goal lack (unchanged)
```

- 复用 `EmberUpgradeRules.ARMOR_REFUSE` 字面；可选 `dismantleYieldLine` / 零头进度半行（**不**新造费用表）。  
- **禁止**对甲输出「成色 …（胚N…）」/「精工 …」/「升阶 T…」费用行。  
- **禁止**新 PAPI / 新 glance 族 / remainLine twin。  
- 刃/护符 + forgeGoal（ENHANCE/REFINE/ROLL/CONVERT/BRAND）枝 **原样**。

### 2.2 调用方 / 轻触

| 点 | 动作 |
|----|------|
| `EmberCommand` forge 开入口 (~4059–4070) | `tellHeld` 已吃 SoT；**轻触**手持预览三钮：若手持甲 → 勿发 enhance/quality/refine 预览；改发分解/成套追/工坊（命令现网存在；可对齐 `EmberForgeCommitNext.armorHonestPack` 标签） |
| `BlankLoop.followHint` | **可选**：去掉假成色串（如改「分解/成套/工坊」或「见下一环钮」） |
| `GearNextHint` | **本窗不改**（消费者闸） |
| TrMenu | **禁**加 lore |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| 抽纯助手（荐）：`armorGlanceLine(d)` 或 package 可见甲早退串 | 含 `ARMOR_REFUSE`；**不含**「成色 …→」/「精工 …→」/「升阶 T」费用形 |
| 刃 `tellHeld` 逻辑回归（若可测 bits 组装） | 仍含成色/精工近档形（与现网一致） |
| `git show` | 无 STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无 ForgeChoice densify；无新 hope/glance PAPI |

> 若 `tellHeld` 强依赖 Bukkit Player：抽 `armorHonestMessage(d)` / `shouldSkipForgeCosts(d)` 纯函数测串；勿为测强起服务器。

### 2.4 玩家口径（固定）

- 手里拿着护甲再开 `/corerpg p1 forge`：「消耗一瞥」会说 **护甲随护符成长**（或分解零头），**不会**列出成色/精工本次费用。  
- 同瞬预览钮不再诱点「成色预览/精工预览」去撞拒绝。  
- 刃/护符消耗一瞥本窗不动；价钱、掉落、数字不动。

---

## 3. 验收

- [ ] 手持甲开 forge：一瞥无成色/精工/升阶费用行；可见 ARMOR_REFUSE（±分解半行）  
- [ ] 手持甲：开入口预览钮无 enhance/quality/refine 命令  
- [ ] 手持刃：一瞥与预览回归（费用行仍在）  
- [ ] 无新 PAPI / 无 glance twin / lore≤3 不加成行  
- [ ] L13 施工仍 HOLD；TIP-AD exclude 保留  

---

## 4. 明确不做

- 抬表 / 扩 stampable / 开 L13 施工 tip  
- ForgeChoice densify / K3 / 希望 celebration / glance twin / SinkGlance 功率 densify  
- 签名页甲待命半行 / 四甲格  
- 改 `ARMOR_REFUSE` 为可锻；全局改 `GearNextHint` 成色串  

---

## 5. 施工指针（真路径）

| 岗 | 路径 |
|----|------|
| 插件主责 | `CoreRpg/.../EmberSinkGlance.java` — `tellHeld` 甲早退 |
| 轻触 | `EmberCommand` forge 开入口预览三钮甲闸 |
| 可选 | `EmberBlankLoop.followHint` 文案 |
| 已诚实对照 | TIP-AK `EmberForgeHeldNext` · TIP-AL `EmberForgeCommitNext` · SixPapi `heldArmorLine` · K0 `ARMOR_REFUSE` |
| 单测 | 新 `EmberSinkGlanceTest` 或扩现有纯助手测（最小包） |
| 菜单 | 本窗默认不改 TrMenu |
