# DESIGN · 强化「+5开孔」硬文案诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** OPEN · TIP-BD · 2026-10-12 22:55 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-enhance-plus5-hole-copy-align-2026-10-12.md`](RESEARCH-ember-enhance-plus5-hole-copy-align-2026-10-12.md)  
> **对接 live：** TIP-BC CLOSED live `1.65.794-d12967` · feat `b13c47c69` · GemHoleUnlockSoft + CUE · `EmberGemSocket` Open/拒线已齐 · `EmberForgeService.enhance` 预览 · `EmberHopeMeter` · TrMenu `ember_p1_gem.yml` / `ember_socket.yml` · legacy `EnhanceService`  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零改 DELTA/C_USED/sockUnlocked/unlock_at · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠 · 零 soft-latch 假耗再叠 · 零 xp-boost cue 再叠 · 零 GemHole Open/拒线再叠 · 零 LifeCap / UndoWindow / FestivalEntry / AnvilRepair densify · 零 topUpTo 扩 soft · 零再降开孔门槛 densify**

---

## 0. 一句话

让强化预览 / Hope / 菜单在写开孔门槛时同时承认 **表额≥5/≥3** 与 **日首−1→≥4/≥2**；打开预览不得 consume。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| GemHoleUnlockSoft | insert 真 soft（DELTA=1）· BC Open/拒线已齐 | **强化侧文案跟 SoT** |
| P1 强化预览 | 无开孔门槛句 | **ready → CUE/等价半句；零 unlocked()** |
| Hope enhance | 无 soft 开孔尾 | **可选 ready 尾** |
| TrMenu gem/socket | 硬唯一 ≥5 / @+5 / ≥3 | **静态双写表额+日首−1；禁加行** |
| EnhanceService legacy | 需+N 硬表 | **可选 ready 附注；禁改 unlock_at** |
| DELTA / 硬表 | 1 / ≥5·≥3 | **零改** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
// 既有 — 零改 API 语义 / DELTA / unlock_at
EmberGemHoleUnlockSoft.ready / usedToday / CUE / softEligible
EmberItemData.sockUnlocked  // 硬表只读

EmberForgeService.enhance go=false (伪码):
  … 既有档位/成功率/材料 …
  if UnlockSoft.ready(pd) && heldHasLockedHole(d):  // 刃 hole1 锁 或 护符 hole0 锁
    msg += " · " + UnlockSoft.CUE   // 或压行「今日开孔门槛−1：刃二孔≥4·护符≥2」
  // 禁调用 unlocked()

EmberHopeMeter enhance 轨 (可选同构):
  ready && held locked → softTail；数字/保底不变

EmberGemSocket 锁孔→强化按钮副标:
  ready → "冲软开孔（≥4/≥2）" 或含 CUE 压行
  used  → "冲强化开孔（≥5/≥3）"

TrMenu 静态（物品 · 读不到 ready）:
  孔1 lore: 「表额≥5 · 日首−1≥4」类双写（替换原「强化≥5」；行数不增）
  孔0 符: 「表额≥3 · 日首−1≥2」
  socket.yml: 「孔2 表额+5 · 日首−1+4」

EnhanceService info (可选):
  锁孔行 ready → 附「日首门槛−1」；unlock_at 数组零改
```

- **复用** UnlockSoft；**禁止**改 `DELTA`/`C_USED`/`sockUnlocked`/`unlock_at`。  
- **禁止**新 `%corerpg_p1_*`；TrMenu **只替换**既有开孔门槛句 · **禁**加 lore 行。  
- GemSocket Open/拒线 / soft-latch / Anvil / Cap / CostSoft / pity / xp-boost **本窗不改接线**（BC 已齐 · 禁再叠）。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `EmberForgeService.enhance` 预览 | ready + 锁孔 → CUE 尾；零 `unlocked()` |
| `EmberHopeMeter` enhance（可选） | 同构 ready 尾 |
| `EmberGemSocket` 锁孔强化按钮副标 | ready/used 档差诚实 |
| TrMenu `ember_p1_gem.yml` / `ember_socket.yml` | **物品** 替换硬唯一句为双写 |
| `EnhanceService` info（可选） | ready 附注；禁改 yaml unlock_at |
| UnlockSoft / sockUnlocked / Open·拒线 / Anvil / Cap / CostSoft | **本窗不改 SoT / 不叠 BC** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| ready · 刃+3 锁孔1 · enhance 预览 | 文案含「开孔门槛−1」或「≥4」；`usedToday=false` |
| 已用 · 同件预览 | **无**门槛−1待用尾（或仅表额） |
| ready · 按钮副标 | 含 soft 档 ≥4/≥2 |
| TrMenu diff | 仅替换门槛句；无第四 lore 行；无 unlock_at/DELTA 改 |
| `git show` | 无 DELTA/C_USED/sockUnlocked/unlock_at 改；无 STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无新 PAPI；无甲闸/SetChase densify；无 soft-latch/CostSoft/pity/Cap/firstsoft densify；无 Anvil/topUpTo soft densify；无 GemHole Open/拒线再叠；无 xp-boost 再叠 |

### 2.4 玩家口径（固定）

- 强化预览在日首 soft 仍待用且手持有锁孔时：可见 **今日开孔门槛−1待用（刃二孔≥4·护符≥2）**；真正 soft 过锁才消耗（仍仅镶嵌路径）。  
- 菜单写清 **表额** 与 **日首−1**；已用后玩家仍知硬门槛。  
- DELTA 仍是 1；硬表 / unlock_at 本窗不动。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后插件：预览/Hope/按钮 + 单测；物品：TrMenu 双写；`git show` 干净  
- lore 行数不增；Open/拒线不重做；L13 施工仍 HOLD  

## 4. 旁债（下一 prefer · 非本窗）

1. AnvilRepair Soft 假耗（硬禁 densify 未解冻前不开）  
2. topUpTo 是否接 soft（须另批 · 现禁 densify）  
3. 主线解锁 DESIGN / stampable 空 / L13 HOLD 观察  
4. legacy EnhanceService 全页若仍玩家主感 · 可续扫（本窗可选已覆盖 info）

## STATUS: OPEN · TIP-BD · 2026-10-12 22:55 Asia/Shanghai
