# DESIGN · ForgeCommitNext 护甲诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** **CLOSED live** `1.65.777-d12695.local` · TIP-AL 批 A·M · feat `9b4824398` · chore `6be39c642` · 2026-10-12 20:26 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-forge-commit-armor-honesty-2026-10-12.md`](RESEARCH-ember-forge-commit-armor-honesty-2026-10-12.md)  
> **对接 live：** TIP-AK CLOSED live `1.65.776-d12679`（HeldNext 甲闸）· D318 K0 `ARMOR_REFUSE` · D2128 `afterGearDeliver` · D2101 `afterBlanks` · D2156 `afterTenths`  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI `STAGE1_L13_OFFLINE=FAIL` · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零 STAMP 抬 · 零扩掉落 · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance/深化 densify/sx20/ForgeChoice densify

---

## 0. 一句话

让 `EmberForgeCommitNext` 对护甲到账/持甲花胚不再推强化成色钮；与 K0 / AK HeldNext 同句诚实。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| K0 / 菜单 / HeldNext | 甲 → `ARMOR_REFUSE` | 保持 |
| `quality`/`refine` 助手 | 只看档位 int，甲仍出钮 | **甲 → null** |
| `afterGearDeliver` | 甲也 `[去强化][去成色]…` | **甲诚实包** |
| `afterBlanks` / `afterTenths` | 持甲假推花胚锻 | **甲诚实包 / 吃 null** |
| 刃/护符 commit | 已 SHIP | **不变** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（一处修好）

`EmberForgeCommitNext`：

```
quality(d) / refine(d):
  if (d != null && d.isArmor()) return null;
  // 其余原样（cost 档）

// 甲诚实三钮（命名可微调，命令须现网存在）
armorHonestPack():
  a = ["[分解零头]", "/corerpg p1 dismantle", "护甲随护符·可分解零头", ...]
       或 scrap 入口若与 dismantle 同页——以现网 forge 甲可点入口为准
  b = setchase()
  c = forge()

afterGearDeliver(piece):
  if (piece != null && piece.isArmor()) { send3(..., armorHonestPack); return; }
  // 原样 enhance + quality/refine/lock

afterBlanks(held):
  if (held != null && held.isArmor()) { send3(胚料文案, armorHonestPack 或 scrap+setchase+forge); return; }
  // 原样
```

- **禁止**对甲返回含「去强化 / 去成色 / 去精工 / 去升阶」且指向 `/corerpg p1 enhance|quality|refine|upgrade` 的可执行养成钮。  
- `enhance()` 无件参 API **保留**（刃路径）；有件路径必须经甲闸。  
- **禁止**改 pity / RATE / cost / ForgeChoice。

### 2.2 调用方

| 点 | 动作 |
|----|------|
| `EmberDelivery` D2128 | **不改**（吃 `afterGearDeliver`） |
| `EmberBlankLoop.afterDismantle` | **不改**（吃 `afterBlanks`） |
| `EmberBlankLoop.afterTenths` | 小改：第二钮勿在 `quality`/`refine` 皆 null 时硬编码 `[去升阶]` 顶甲；改 `setchase`/`forge`/再分解 |
| TrMenu | **禁**加 lore |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| `quality(armor q0)` / `refine(armor c0)` | **null** |
| `quality(blade q0)` / `refine(blade c0)` | 仍非 null（回归） |
| `afterGearDeliver` 甲（可测纯助手若抽 `armorButtons`；或测 quality/refine null + 文档验收 Delivery） | 无 enhance/quality/refine/upgrade 命令串 |
| `enhance()` 无件 | 仍指向 `/corerpg p1 enhance`（回归） |
| `git show` | 无 STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无 ForgeChoice densify |

### 2.4 玩家口径（固定）

- 掉落护甲进背包：「到账下一环」会指 **分解零头 / 成套追 / 工坊**，**不会**假装能强化成色。  
- 手里拿着护甲再分解得胚/零头：花法同样不推甲锻。  
- 刃/护符养成下一环本窗不动；价/掉落/数字不动。

---

## 3. 验收

- [ ] 甲到账：下一环无 enhance/quality/refine/upgrade 可执行钮  
- [ ] 持甲 afterBlanks / afterTenths：同上  
- [ ] 刃 quality/refine/enhance 回归 PASS  
- [ ] 无新 PAPI / 无新 glance 族 / lore≤3 不加成行  
- [ ] L13 施工仍 HOLD；TIP-AD exclude 保留  

---

## 4. 明确不做

- 抬表 / 扩 stampable / 开 L13 施工 tip  
- ForgeChoice densify / K3 / 希望 celebration / glance twin  
- 签名页甲待命半行 / 四甲格  
- 改 `ARMOR_REFUSE` 为可锻；SinkGlance 大扫（另债）  

---

## 5. 施工指针（真路径）

| 岗 | 路径 |
|----|------|
| 插件主责 | `CoreRpg/.../EmberForgeCommitNext.java` — quality/refine 甲闸 + afterGearDeliver/afterBlanks 甲包 |
| 轻触 | `EmberBlankLoop.afterTenths` — 第二钮勿升阶顶甲 |
| 吃 SoT | `EmberDelivery` · `EmberBlankLoop.afterDismantle` |
| 已诚实对照 | TIP-AK `EmberForgeHeldNext` · SixPapi `heldArmorLine` · K0 `ARMOR_REFUSE` |
| 单测 | `EmberForgeCommitNextTest`（+甲 null 用例） |
| 菜单 | 本窗默认不改 TrMenu |
