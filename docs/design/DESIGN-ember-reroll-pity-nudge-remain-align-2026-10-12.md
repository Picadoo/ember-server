# DESIGN · 洗练保底日首加码 remain/希望串诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** APPROVED · TIP-AQ 批 A·M · 已派插件 · 2026-10-12 21:07 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-reroll-pity-nudge-remain-align-2026-10-12.md`](RESEARCH-ember-reroll-pity-nudge-remain-align-2026-10-12.md)  
> **对接 live：** TIP-AP CLOSED live `1.65.781-d12759` · D10977 `EmberRerollPityNudge` · D1248 `EmberRerollPity` · HopeMeter 洗练轨  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**；DropCraft/DropQuality 加码 remain **旁债另 tip**  
> **硬禁：** 零功率表 · 零改 reroll.pity/BONUS · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 TrMenu 加 lore**

---

## 0. 一句话

让「含本次≤n 次必出上限档」在日首洗练加码待用时少报虚次，并明示今日加码可用。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| `plan` 结算 | 日首非 forced 后 `pityAfter+BONUS` 真加 | 保持 |
| `remainLine` / 确认预览 | 只看 pity/need | **认 ready；有效 incl；尾缀加码** |
| HopeMeter 洗练轨 | `glance(pity,need)` 不认 nudge | **改吃 remainLine(…, ready)** |
| `reroll.pity` / `BONUS` | 表值 / 1 | **零改** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
attemptsUntilCap(pity, need, nudgeReady):
  if need <= 0: return 0
  if pity >= need: return 1
  incl = need - pity                    // 旧一元
  if nudgeReady && incl > 1: return incl - 1
  return incl

remainLine(pity, need, nudgeReady):
  if need <= 0: return "保底未配置"
  if pity >= need: return "这次必出上限档"
  incl = attemptsUntilCap(pity, need, nudgeReady)
  base = "第" + pity + "/" + need + " · 含本次≤" + incl + "次必出上限档"
  if nudgeReady && pity < need: return base + " · 今日加码待用"
  return base

attemptsUntilCap(pity, need): attemptsUntilCap(pity, need, false)
remainLine(pity, need): remainLine(pity, need, false)

afterRemainLine(pityAfter, need):  // 本窗可不动（settle 后已含 BONUS）
glance(pity, need):                // 二元保留回归；Hope 轨改吃 remain
```

- **复用** `EmberRerollPityNudge.ready` / `BONUS`；**禁止**改 consume / 日闩键 / `reroll.pity`。  
- `plan` 结算序 **本窗不改**（仍非 forced 后 consumeExtra）。  
- **禁止**新 `%corerpg_p1_*` / 新 remain 族 / TrMenu 加 lore / 抬表。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `EmberRerollPity.remainLine` / `attemptsUntilCap` | 三元重载 + 旧二元委托 false |
| `EmberGrowthService` 确认预览 / 占位 remain | `remainLine(pity, need, EmberRerollPityNudge.ready(d))` |
| `EmberHopeMeter.priorityTracks` 洗练轨 | **改**吃 `remainLine(rerollPity, rerollNeed, EmberRerollPityNudge.ready(pd))` |
| `afterRemainLine` / `glance(pity,need)` | **本窗可不改**（事后诚实；glance 留测） |
| TrMenu / 甲闸 / GearNextHint / SetChase / mapid | **本窗不改** |
| DropCraft / DropQuality remain | **本窗不改**（旁债） |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| pity=3 · need=5 · nudgeReady=true | remain 含「含本次≤**1**次」+「今日加码待用」 |
| pity=2 · need=5 · nudgeReady=true | 「含本次≤**2**次」+「今日加码待用」 |
| pity=4 · need=5 · nudgeReady=true | 「含本次≤**1**次」+「今日加码待用」 |
| pity=3 · need=5 · false | 「含本次≤**2**次」；**无**加码尾 |
| pity=0 · need=3 · false | 旧「含本次≤3次」（回归 D1248） |
| pity≥need | 旧「这次必出上限档」 |
| `remainLine(pity,need)` / `attemptsUntilCap` 二元 | 与 `EmberRerollPityTest` 旧断言一致 |
| Hope 洗练轨（有 ready） | 进度串与 remain 有效 incl 一致（或同串） |

---

## 3. 验收

- [ ] pity=3+加码待用：确认/希望写含本次≤1 + 今日加码待用  
- [ ] 加码已用 / 满档：旧口径  
- [ ] `git show` 无 reroll.pity / BONUS / Affix / STAMP / L11·L13 / 甲闸 / SetChase densify / 新 PAPI  
- [ ] L13 施工仍 HOLD  

---

## 4. 批后派单

**余烬-插件**（CoreRpg · `EmberRerollPity` remain/attempts 三元 · GrowthService 预览喂 ready · HopeMeter 洗练轨改吃 remain · 单测扩）

---

## 5. HOLD

- **L13 施工 HOLD**（TIP-AI FAIL · 显式开工前不开施工 tip）  
- DropCraft / DropQuality 加码 remain 另 tip  
- 甲闸再扫 / 扩 stampable / SetChase 六槽 densify / glance twin / ForgeChoice densify / Affix / 抬表  
