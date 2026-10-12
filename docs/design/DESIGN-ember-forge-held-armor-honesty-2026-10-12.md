# DESIGN · ForgeHeldNext 手持护甲诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** APPROVED · TIP-AK 批 A·M · 已派插件 · 2026-10-12 20:11 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-forge-held-armor-honesty-2026-10-12.md`](RESEARCH-ember-forge-held-armor-honesty-2026-10-12.md)  
> **对接 live：** D318 K0 `ARMOR_REFUSE` · D1943/D1944 ForgeHeldNext · RunPapi `held_next_*` 已甲闸 · TIP-AJ CLOSED live `1.65.775-d12663`  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI `STAGE1_L13_OFFLINE=FAIL` · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零 STAMP 抬 · 零扩掉落 · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance/深化 densify/sx20/ForgeChoice densify

---

## 0. 一句话

让 `EmberForgeHeldNext.line` 对手持护甲与菜单 / K0 同句拒绝；聊天与 ActionBar 不再假推成色强化。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| K0 / 菜单 PAPI | 甲 → `ARMOR_REFUSE` / 分解零头 | 保持 |
| `ForgeHeldNext.line` | 甲仍走 quality→refine→enhance… | **甲早退诚实句** |
| forge helpLines / status 焦点 | 直接调 line → 假推 | 随 SoT 变真 |
| 刃/护符 HeldNext | 已 SHIP | **不变** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（一处修好）

`EmberForgeHeldNext.line(EmberItemData d, Mats m, boolean gateOk, int forgeGoal)` 在空桩与业务序之前：

```
if (d != null && d.isArmor()) {
  // 主句 ≡ UpgradeRules.ARMOR_REFUSE（禁止改语义成可锻）
  // 可选半行：若 dismantleCheck==null 且 armorDismantleTenths>0
  //   → "下一确定 · 护甲 · 分解零头 · /corerpg p1 dismantle"
  // 否则 → "下一确定 · 护甲 · " + ARMOR_REFUSE
  return ...;
}
```

- **禁止**对甲返回含「成色」「精工」「强化」「升阶」「料够 · /corerpg p1 quality|refine|enhance|upgrade」的可执行养成推送。  
- **禁止**改 pity / RATE / cost / ForgeChoice。  
- 刃/护符 + forgeGoal 优先序 **原样**。

### 2.2 调用方

| 点 | 动作 |
|----|------|
| `EmberForgeService#heldNextLine` | **不改**（吃 SoT） |
| `EmberCommand` status 焦点 HeldNext | **不改**（吃 SoT） |
| `EmberRunPapi#heldNext` / `heldForgeLine` | **本窗不改**（已诚实；回归即可） |
| TrMenu forge/gear | **禁**加 lore；可选头注非必须 |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| 手持盔/胸（`isArmor`）任意 mats | line 含护甲拒绝口径（`ARMOR_REFUSE` 子串或「护甲随护符」）；**不含**「料够 · /corerpg p1 quality」类假推 |
| 可分解掉落甲 | 可含「分解」提示；仍不含成色/强化推送 |
| 刃 q0 料够 | 仍「成色」「料够」「quality」（回归） |
| 空手 | 仍含「空手」 |
| `git show` | 无 STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无 ForgeChoice densify |

### 2.4 玩家口径（固定）

- 手持护甲再开工会话 / 看成长摘要：「下一确定」会说 **护甲随护符成长**（或分解零头），**不会**假装能成色/强化。  
- 刃/护符养成路径本窗不动；价/掉落/数字不动。

---

## 3. 验收

- [ ] 手持甲：forge help / status ActionBar ≡ K0 拒绝口径（或分解诚实）  
- [ ] 手持刃：HeldNext 回归用例 PASS  
- [ ] 无新 PAPI / 无新 glance 族 / lore≤3 不加成行  
- [ ] L13 施工仍 HOLD；TIP-AD exclude 保留  

---

## 4. 明确不做

- 抬表 / 扩 stampable / 开 L13 施工 tip  
- ForgeChoice densify / K3 / 希望 celebration  
- 签名页甲待命半行 / 四甲格  
- 改 `ARMOR_REFUSE` 为可锻  

---

## 5. 施工指针（真路径）

| 岗 | 路径 |
|----|------|
| 插件主责 | `CoreRpg/.../EmberForgeHeldNext.java` — `line(...)` 甲早退 |
| 复用 | `EmberItemData.isArmor` · `EmberUpgradeRules.ARMOR_REFUSE` · `dismantleCheck` / `armorDismantleTenths`（可选半行） |
| 吃 SoT | `EmberForgeService#heldNextLine` · `EmberCommand` status 焦点 |
| 已诚实对照 | `EmberRunPapi#heldNext` · `EmberSixPapi.heldArmorLine` |
| 单测 | `EmberForgeHeldNextTest`（+1 甲用例） |
| 菜单 | 本窗默认不改 TrMenu |
