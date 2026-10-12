# DESIGN · SigChase 追烙就绪防具侧对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** APPROVED · TIP-AN 批 A·M · 已派插件 · 2026-10-12 20:37 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-sigchase-defense-ready-align-2026-10-12.md`](RESEARCH-ember-sigchase-defense-ready-align-2026-10-12.md)  
> **对接 live：** TIP-AM CLOSED live `1.65.778-d12711`（甲闸链齐）· TIP-AE/AJ/AH 防具侧烙印/预览/希望已齐 · D3937 held-ready · D470 ready cue  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零 STAMP 抬 · 零扩掉落 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸**

---

## 0. 一句话

让追烙「徽记够可烙」提示认防具侧目标件，与烙印页 / `imprintWornTarget` 同序。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| 烙印页 / imprint_hope / 战斗 | `fitsDefense` + `imprintWornTarget` | 保持 |
| `sameSlotPiece` / held-ready | 严格槽 + 只扫刃/符 | **fitsDefense + 防具侧穿着序** |
| ready cue 按钮槽名 | `slotName(def.slot)` | **实际目标件槽名** |
| 刃追烙 | 已 OK | **不变** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（一处修好）

`EmberSigChase`：

```
sameSlotPiece(p, d, lo, def):
  // held first
  held = mainhand P1 piece
  if (held != null && EmberSignature.fitsDefense(def, held.slot, held.family)) return held
  // worn: mirror GrowthService.imprintWornTarget
  if (blade-def) return lo.blade
  if (charm-def) {
    if (lo.charm fitsDefense) return lo.charm
    for armor i in 0..3: if lo.armor(i) fitsDefense return it
  }
  return null

maybeHeldReadyTip:
  target = sameSlotPiece(...)
  worn = (target != null && target.uid equals imprintWornTarget(lo, def).uid)
  // where/how copy: 穿着 | 主手 | 同部位 — unchanged shape

maybeReadyCue ConfirmTokens half:
  target = imprintWornTarget-equivalent(lo, def)  // may be null
  slotWord = target != null ? slotName(target.slot) : slotName(def.slot) // or 「穿着件」
  // "烙到穿着{slotWord}" — never force 护符 when armor is real target
```

- 优先抽 **package-visible static** `imprintWornTarget(lo, def)`（可放 `EmberSigChase` 或提升 GrowthService 为 static 同实现），**禁止**两套 prio。  
- 复用 `EmberSignature.fitsDefense`；**禁止**调用 `fitsStamp` 当烙印认件。  
- **禁止**新 PAPI / glance 族 / remainLine twin。  
- 徽记阈值、imprint 价、STAMP、stampable、DEFS mods **零改**。

### 2.2 调用方 / 轻触

| 点 | 动作 |
|----|------|
| `maybeReadyCue` / `maybeHeldReadyTip` | 吃 SoT；文案槽名跟目标件 |
| GrowthService `imprintWornTarget` | **可**改为委托同一 static（可选轻触，防漂移） |
| TrMenu | **禁**加 lore |
| ForgeHeldNext / CommitNext / SinkGlance / GearNextHint | **本窗不改**（甲闸已齐） |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| charm-def（L02）+ 同族头盔穿着 · 无护符 | `sameSlotPiece` → 该头盔 |
| charm-def + 主手胸甲（测手持枝；Player 可 null 则只测 worn） | worn/held 认 `fitsDefense` |
| blade-def（L01）+ 刃 | 仍返回刃（回归） |
| charm-def + 无护符无甲 | null（回归） |
| 文案助手（若抽）：ready 槽名对头盔含「头」类，**不含**强制「护符」当目标是甲 | 可核 |
| `git show` | 无 STAMP/C12/loot/DEFS mods/L11/L13/Affix/skill_mult；无甲闸文件；无新 hope/glance PAPI |

### 2.4 玩家口径（固定）

- 追烙徽记够、身上穿着可烙的防具：会提示 **烙印就绪 → 穿着该防具**，不会假装只能烙护符。  
- 手里拿着可烙同签防具：手持就绪提示会认，不再静默。  
- 刃追烙、价钱、掉落、徽记数、签名表 **本窗不动**。

---

## 3. 验收

- [ ] L02 类追烙 + 穿同族甲：held-ready / ready 文案认甲槽  
- [ ] L01 刃路径回归  
- [ ] 无新 PAPI / 无 glance twin / lore≤3 不加成行  
- [ ] 未改 stampable / STAMP / L11·L13 / 甲闸三文件  
- [ ] L13 施工仍 HOLD  

---

## 4. 明确不做

- 扩 stampable 甲 / 抬 STAMP / 开 L13 施工 tip  
- Hope/SetChase/gem glance densify / ForgeChoice densify  
- 再扫甲闸（HeldNext/CommitNext/SinkGlance/GearNextHint 全局）  
- 签名四甲格 / 甲待命半行  

---

## 5. 施工指针（真路径）

| 岗 | 路径 |
|----|------|
| 插件主责 | `CoreRpg/.../EmberSigChase.java` — `sameSlotPiece` · `maybeHeldReadyTip` · `maybeReadyCue` |
| 复用 | `EmberSignature.fitsDefense` · GrowthService `imprintWornTarget` 序（charm→四甲） |
| 单测 | `EmberSigHeldReadyTipTest` 扩防具侧 + 刃回归 |
| 可选轻触 | GrowthService 委托同一 static |
