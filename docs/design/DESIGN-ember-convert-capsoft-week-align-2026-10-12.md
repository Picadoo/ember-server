# DESIGN · 转化周次容错 CapSoft 预览/weekLine 诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** CLOSED · live `1.65.788-d12871` · feat `07923c868` · 2026-10-12 22:05 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-convert-capsoft-week-align-2026-10-12.md`](RESEARCH-ember-convert-capsoft-week-align-2026-10-12.md)  
> **对接 live：** TIP-AV CLOSED live `1.65.787-d12855` · feat `901632f09` · ConvertCapSoft D9249 · ConvertPath.weekLine D732 · ConvertCostSoft（TIP-AT）· ConvertSure D5857  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零改 WEEKLY_CAP / C_CONV / CapSoft C_USED 周键 · 零发明第二容错 · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 TrMenu 加 lore · 零再叠 CostSoft glance · 零 pity-remain 再叠 · 零 ConvertCap densify · 零 soft-glance 费用族再叠**

---

## 0. 一句话

让周转化剩余口与转化预览在周次容错待用时明示「还可容错 1 次」，且容错只在确认成功时消耗。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| CapSoft | live · ready/consume · **预览即 consume** | **预览只 peek；确认才 consume** |
| `convertHeld` 预览 weekLeft | `WEEKLY_CAP - used`（满周=0） | **满周且 ready → 容错待用口径** |
| `ConvertPath.weekLine` / afterSet 余次 | 满周「环已完成」 | **满周且 ready → 明示容错待用** |
| ConvertSure 直办 | 可先吞 soft 再 confirm 拒办 | **confirm 路径才吞；与预览同 SoT** |
| WEEKLY_CAP / C_CONV | 1 / 周闩 | **零改** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
// CapSoft — 可加纯 peek 助手（禁第二套日/周闩）
ready(pd)           // 已有
consume(pd)         // 已有 · 仅确认成功路径调用
// 可选纯函数（推荐，便于单测）：
effectiveExtra(pd, used):
  if used < WEEKLY_CAP: return 0
  return ready(pd) ? 1 : 0

ConvertPath.weekLine(d):
  used = periodCount(C_CONV, week)
  left = max(0, WEEKLY_CAP - used)
  if left > 0: 旧「本周转化剩余 left/CAP」
  else if CapSoft.ready(d):
    return "§e本周转化 · 周次容错待用 · 还可 1 次"   // 钉文案；可 +「下周重置」短尾
  else: 旧「本周转化环已完成 · 下周重置」

convertHeld(..., confirm):
  refuse = refusal(t, toFam, used, gate)
  if refuse != null && used >= WEEKLY_CAP:
    if !confirm:
      // peek only
      if CapSoft.ready(d):
        refuse = refusal(t, toFam, used - 1, gate)   // 允许出预览/确认钮
      // else keep refuse
    else:
      if CapSoft.consume(d):
        refuse = refusal(t, toFam, used - 1, gate)
        // 成功放行时再喊「转化周次容错 · 额外 1 次」
      // else keep refuse
  if refuse != null: 拒办 return
  // CostSoft 等既有路径不动（TIP-AT）
  if !confirm:
    extra = (used >= WEEKLY_CAP && CapSoft.ready(d))   // 预览未吞 · ready 仍真
    weekLeftDisplay:
      if used < WEEKLY_CAP: WEEKLY_CAP - used
      else if extra: 「容错待用 · 还可 1 次」（禁写 0/1 假完成）
      else: 0 + 已完成口径
    // 出确认钮 / ConvertSure 直办走 confirm=true（此时才 consume）
```

- **复用** 既有 CapSoft；**禁止**改 `WEEKLY_CAP` / `C_CONV` / `C_USED` 周键；**禁止**发明第二容错类或抬「额外次数」。  
- ConvertCostSoft 费用 peek（TIP-AT）**本窗不改接线**。  
- **禁止**新 `%corerpg_p1_*` / TrMenu 加 lore / ConvertCap densify。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `EmberConvertCapSoft` | 保持 ready/consume；可选 `effectiveExtra` 纯助手 |
| `EmberConvertPath.weekLine` / afterSet 余次 AB | 满周+ready → 容错待用文案 |
| `GrowthService.convertHeld` | 预览 peek；确认 consume；weekLeft 展示对齐 |
| ConvertSure 直办 | 仅 confirm 路径吞 soft |
| CostSoft / pity / 甲闸 / 追烙 / TrMenu lore | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| used=CAP · CapSoft ready · `!confirm` 预览 | 不 consume；可出确认钮；文案含容错待用；`ready` 仍 true |
| 同上 · `confirm=true` | consume 一次；转化可办；再 confirm 无二次容错 |
| used=CAP · CapSoft 已用 · 预览/确认 | 拒办「本周转化次数已用完」 |
| used&lt;CAP | weekLine/预览旧口径；不碰 CapSoft |
| ConvertSure 直办 · used=CAP · ready | 一次成功 consume；不出现「预览吞→确认拒」 |
| weekLine ready vs used | 文案分叉可核 |
| `git show` | 无 WEEKLY_CAP/C_CONV 改；无 STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无新 PAPI；无甲闸/SetChase densify；无 CostSoft/pity 再叠 |

### 2.4 玩家口径（固定）

- 本周转化次数已用完、且本周容错还没用：weekLine / 转化预览写 **周次容错待用 · 还可 1 次**；点确认才消耗容错并成功转化。  
- 预览（未确认）**不消耗**容错。  
- 容错已用或本周未满帽：跟以前表口径一样。  
- 每周转化次数上限仍是 1；容错仍是 **额外 1 次 / 每周一次**；不改费用表、不改 CostSoft。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后插件：§2.1–2.3；NON 核 `git show` 干净  
- 菜单默认不动；lore≤3  
- L13 施工仍 HOLD；ConvertCap densify / CostSoft glance 再叠 / pity-remain / 甲闸 / SetChase 六槽仍冻  

## STATUS: CLOSED · live 1.65.788-d12871 · 07923c868 · 2026-10-12 22:05 CST
