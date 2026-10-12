# DESIGN · 宝石开孔日首门槛 Soft 预告/拒线诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** APPROVED · TIP-BC 批 A·M · 已派插件 · 2026-10-12 22:49 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-gemhole-unlock-soft-cue-align-2026-10-12.md`](RESEARCH-ember-gemhole-unlock-soft-cue-align-2026-10-12.md)  
> **对接 live：** TIP-BB CLOSED live `1.65.793-d12951` · feat `6f7d01e91` · GemHoleUnlockSoft D10785 · `EmberGemSocket` Open/insert · `EmberItemData.sockUnlocked` 硬表  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零改 DELTA/C_USED/sockUnlocked 硬表 · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 TrMenu 加 lore · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠 · 零 soft-latch 假耗再叠 · 零 xp-boost cue 再叠 · 零 LifeCap / UndoWindow / FestivalEntry / AnvilRepair densify · 零 topUpTo 扩 soft · 零再降开孔门槛 densify**

---

## 0. 一句话

让宝石页在日首开孔门槛−1仍待用时说清楚「刃二孔≥4 / 护符≥2」；拒线跟 ready 对齐；打开列表不得 consume。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| GemHoleUnlockSoft | insert 真 soft（DELTA=1） | **Open ready 预告；consume 仍仅 soft 过锁** |
| Open/list ActionBar | 硬 `sockUnlocked`→「锁」 | **ready 尾预告；可选 soft-ok 短标** |
| insert 拒线 | 固定 ≥5/≥3 | **ready → soft 门槛文案；已用 → 硬表** |
| DELTA / 硬表 | 1 / ≥5·≥3 | **零改** |
| soft-latch / Anvil / xp-boost | BA/BB 已齐 / 硬禁 | **本窗不改** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
// 既有 — 零改 API 语义 / DELTA
EmberGemHoleUnlockSoft.ready(d) / usedToday(d) / unlocked(item,i,d)  // unlocked 仍仅 insert 门禁

EmberGemSocket Open/list (伪码):
  … shortLabel · socketLoreLine …
  if UnlockSoft.ready(pd):
    msg += " · 今日开孔门槛−1待用（刃二孔≥4·护符≥2）"  // 禁调用 unlocked()
  ActionBar 可保留硬锁标；可选 soft-ok&&ready →「可软开」短尾（禁永久 lore）

insert 拒线 (伪码):
  if !sockUnlocked && !unlocked(...):  // unlocked 仍 SoT
    if UnlockSoft.ready(pd):  // 注意：拒 = soft 亦未过，ready 仍 true
      "该孔未解锁（今日门槛−1：刃二孔≥4 · 护符≥2；表额≥5/≥3）"
    else:
      "该孔未解锁（刃第二孔需强化≥5 · 护符孔需强化≥3）"
```

- **复用** 既有 UnlockSoft；**禁止**改 `DELTA`/`C_USED`/`sockUnlocked`。  
- **禁止**新 `%corerpg_p1_*` / TrMenu 加 lore / 改永久 `socketLoreLine` 语义（本窗不强制改 lore 硬锁字符）。  
- soft-latch / Anvil / Cap / CostSoft / pity / xp-boost / Supply **本窗不改接线**。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `EmberGemSocket` Open/list | ready 尾；零 `unlocked()` |
| insert 拒线 | ready → soft 门槛串；已用 → 硬表串 |
| `EmberGemHoleUnlockSoft` | **本窗可不改类**（仅 read ready）；可选 `CUE` 短常量便于单测 |
| soft-latch / Anvil / Cap / CostSoft / pity / xp-boost / 甲闸 / SetChase / TrMenu | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| ready · Open 文案含「开孔门槛−1待用」或「≥4」 | `usedToday=false`（未 unlocked） |
| ready · 刃+3 insert 拒 | 拒串含 soft 门槛（≥4）且仍 ready |
| 已用 · Open | **无**门槛−1待用尾 |
| 已用 · 拒 | 硬表 ≥5/≥3 |
| `git show` | 无 DELTA/C_USED/sockUnlocked 硬表改；无 STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无新 PAPI；无甲闸/SetChase densify；无 soft-latch/CostSoft/pity/Cap/firstsoft densify；无 Anvil/topUpTo soft densify；无 xp-boost 再叠 |

### 2.4 玩家口径（固定）

- 今日还没软开过孔：宝石页可见 **今日开孔门槛−1待用（刃二孔≥4·护符≥2）**；真正 soft 过锁才消耗。  
- 拒线在 ready 时报 soft 门槛（可附硬表对照）；已用报硬表 ≥5/≥3。  
- 已用：不再显示待用尾。  
- DELTA 仍是 1、一日一次；硬表 `sockUnlocked` 本窗不动。

---

## 3. 验收

- tip + RESEARCH + 本 DESIGN + power-paths 戳可点  
- 批后插件：Open 预告 + 拒线对齐 + 单测；`git show` 干净  
- 菜单默认不动；lore≤3；禁改永久 socketLore 硬语义为本窗必做  
- Open 不吞 soft；仅 unlocked 过锁 consume；L13 施工仍 HOLD  

## 4. 旁债（下一 prefer · 非本窗）

1. AnvilRepair Soft 假耗（硬禁 densify 未解冻前不开）  
2. topUpTo 是否接 soft（须另批 · 现禁 densify）  
3. 主线解锁 DESIGN / stampable 空 / L13 HOLD 观察  
4. enhance 预览「冲到+5开孔」硬门槛文案（若仍撒谎 · 另 tip 诚实）

## STATUS: APPROVED · TIP-BC · 2026-10-12 22:49 Asia/Shanghai · 已派余烬-插件
