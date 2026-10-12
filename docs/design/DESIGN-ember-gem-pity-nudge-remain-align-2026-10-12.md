# DESIGN · 宝石通关保底加码 remain/希望串诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** OPEN · TIP-AP · docs-only 待批 · 2026-10-12  
> **上游 RESEARCH：** [`RESEARCH-ember-gem-pity-nudge-remain-align-2026-10-12.md`](RESEARCH-ember-gem-pity-nudge-remain-align-2026-10-12.md)  
> **对接 live：** TIP-AO CLOSED live `1.65.780-d12743` · TIP-E gem glance · `EmberGemPityNudge` D10673 · `EmberGemAcquire` D608/D1258  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零改 PITY_NEED/BONUS · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 TrMenu 加 lore**

---

## 0. 一句话

让「再通关几次得石」在日首加码待用时少报虚次，并明示今日加码可用。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| `onClear` 结算 | 日首 `+1+BONUS` 真加 | 保持 |
| `remainLine` / hub gem_pity_hope | 只看 cur/5 | **认 ready；有效剩余；尾缀加码** |
| `hopeLine` | 不认 nudge | 与 remain 同口径 |
| `PITY_NEED` / `BONUS` | 5 / 1 | **零改** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
// 有效剩余（加码只影响「下一次」+BONUS，非整段 /2）
clearsLeft(cur, nudgeReady):
  left = clearsLeft(cur)           // 旧：max(0, NEED-cur)；满 → 0
  if nudgeReady && left > 1: return left - 1
  return left

remainLine(cur, nudgeReady):
  if cur >= NEED: return "通关保底已满 · 下次通关得石"
  left = clearsLeft(cur, nudgeReady)
  base = "通关保底 " + cur + "/" + NEED + " · 再通关" + left + "次得石"
  if nudgeReady && cur < NEED: return base + " · 今日加码待用"
  return base

remainLine(int cur): remainLine(cur, false)          // 单测 / 无 pd
remainLine(PlayerData d): remainLine(pity(d), EmberGemPityNudge.ready(d))

hopeLine(PlayerData d):
  // 与 remain 同有效口径；可委托 remainLine(d) 或 gemPity 后拼尾
  // 禁新 PAPI；禁改 glance() 全局语义到其它轨
```

- **复用** `EmberGemPityNudge.ready` / `BONUS` 常量语义；**禁止**改 consume / 日闩键 / NEED。  
- `onClear` 结算序 **本窗不改**（仍先 +1 再 consumeExtra）。  
- **禁止**新 `%corerpg_p1_*` / 新 remain 族 / TrMenu 加 lore / 抬表。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `EmberGemAcquire.remainLine(PlayerData)` | 喂 ready → 上式 |
| `EmberRunPapi` `gem_pity_hope` | **不改**（已吃 remainLine(pd)） |
| `HopeMeter.priorityTracks` gem 轨 | **不改**（已吃 remainLine(pd)） |
| `MapIdentity.methodLine` / GemSocket 聊天 | **不改**（吃 SoT） |
| `remainLine(int)` / `clearsLeft(int)` | 保留旧语义（无加码） |
| TrMenu / 甲闸 / GearNextHint / SetChase / MapIdentity haveNeed | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| cur=3 · nudgeReady=true | remain 含「再通关**1**次」+「今日加码待用」 |
| cur=2 · nudgeReady=true | 「再通关**2**次」+「今日加码待用」 |
| cur=4 · nudgeReady=true | 「再通关**1**次」+「今日加码待用」 |
| cur=3 · nudgeReady=false | 「再通关**2**次」；**无**加码尾 |
| cur=0 · false | 旧「再通关5次」（回归） |
| cur≥5 | 旧已满串 |
| `remainLine(int)` / `clearsLeft(int)` | 与 TIP-E/D1258 旧断言一致 |
| `hopeLine(pd)` 有 ready | 与 remain 有效剩余一致（或同串） |
| `git show` | 无 PITY_NEED/BONUS 常量改；无 STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无新 PAPI；无甲闸/SetChase densify |

### 2.4 玩家口径（固定）

- 今日宝石通关保底加码还没用：枢纽/聊天会写 **今日加码待用**，且「再通关几次」按 **下次会多计 1 次** 少报虚次。  
- 今日加码已用：数字与旧口径一致，无加码尾。  
- 保底阈值 5、加码 +1、掉落/合成价/石头战力 **本窗不动**。

---

## 3. 验收

- [ ] pity=3 + 加码待用：remain/hub 写再通关 1 + 今日加码待用  
- [ ] 加码已用 / 无 pd 一元重载回归  
- [ ] 无新 PAPI / lore≤3 不加成行  
- [ ] 未改 PITY_NEED / BONUS / onClear / stampable / L11·L13 / 甲闸 / SetChase  
- [ ] L13 施工仍 HOLD  

---

## 4. 批后派单

**余烬-插件**（CoreRpg · `EmberGemAcquire.remainLine`/`clearsLeft`/`hopeLine` + 单测；吃 SoT：RunPapi / HopeMeter / MapIdentity / GemSocket）

策划本窗：docs-only（零业务 jar）

---

## 5. HOLD

- **L13 施工 HOLD**（TIP-AI FAIL · 显式开工前不开施工 tip）  
- 刃 L11/L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 densify / glance twin / 深化 densify / ForgeChoice densify / Affix / 伤乘 / 抬表 / sx20 / skill_mult / STAMP  

## STATUS: OPEN · TIP-AP · 2026-10-12
