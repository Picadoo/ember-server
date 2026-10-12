# DESIGN · DropCraft/Q 日首加码 remain/希望串诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** APPROVED · TIP-AR 批 A·M · 已派插件 · 2026-10-12 21:14 Asia/Shanghai  
> **上游 RESEARCH：** [`RESEARCH-ember-drop-craft-q-pity-nudge-remain-align-2026-10-12.md`](RESEARCH-ember-drop-craft-q-pity-nudge-remain-align-2026-10-12.md)  
> **对接 live：** TIP-AQ CLOSED live `1.65.782-d12775` · TIP-AP gem 加码 remain · TIP-Q craft settle · D10737 `EmberDropCraftNudge` · D10721 `EmberDropQualityNudge` · D6817/D611 pity  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零改 NEED/FLOOR/BONUS / CRAFT_WEIGHTS / QUALITY_WEIGHTS · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 TrMenu 加 lore · 零新开精工 Hope 轨**

---

## 0. 一句话

让「再连出几次触发精工1/精良底」在日首加码待用时少报虚次，并明示今日加码可用。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| `applyToGrants` 结算 | 日首 craft0/标准 tick `+1+BONUS` 真加 | 保持 |
| `DropCraftPity.remainLine` / settle chat·AB | 只看 cur/5 | **认 ready；有效剩余；尾缀加码** |
| `DropQualityPity.remainLine` / settle | 只看 cur/5 | **同上** |
| `DropQualityPity.hope` / Hope `drop_q` / RunPapi | glance 不认 nudge | **改吃 remain（认 ready）** |
| `NEED` / `FLOOR` / `BONUS` | 5 / 1 / 1 | **零改** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT — DropCraft（改既有串 · 禁新族）

```
clearsLeft(cur):                    // 可新加对称 Q；或内联
  c = max(0, cur); if c >= NEED: return 0; return NEED - c

clearsLeft(cur, nudgeReady):
  left = clearsLeft(cur)
  if nudgeReady && left > 1: return left - 1
  return left

remainLine(cur, nudgeReady):
  if cur >= NEED: return "精工软保底已满 · 下次主线装必≥精工1"
  left = clearsLeft(cur, nudgeReady)
  base = "精工软保底 " + cur + "/" + NEED + " · 再连出精工0×" + left + "触发精工1底"
  if nudgeReady && cur < NEED: return base + " · 今日加码待用"
  return base

remainLine(int cur): remainLine(cur, false)
remainLine(PlayerData d): remainLine(get(d), EmberDropCraftNudge.ready(d))
```

- **复用** `EmberDropCraftNudge.ready` / `BONUS`；**禁止**改 consume / 日闩键 / NEED/FLOOR。  
- `applyToGrants` 结算序 **本窗不改**。  
- SettleService **不改接线**（已吃 `remainLine(pd)`）。  
- **禁止**新开精工 HopeMeter 轨 / 新 `%corerpg_p1_*` / TrMenu 加 lore。

### 2.2 SoT — DropQuality（同构）

```
clearsLeft(cur, nudgeReady):        // 旧一元保留
  left = clearsLeft(cur)
  if nudgeReady && left > 1: return left - 1
  return left

remainLine(cur, nudgeReady):
  if cur >= NEED: return "成色软保底已满 · 下次主线装必≥精良"
  left = clearsLeft(cur, nudgeReady)
  base = "成色软保底 " + cur + "/" + NEED + " · 再连出标准" + left + "次触发精良底"
  if nudgeReady && cur < NEED: return base + " · 今日加码待用"
  return base

remainLine(int cur): remainLine(cur, false)
remainLine(PlayerData d): remainLine(get(d), EmberDropQualityNudge.ready(d))

hope(PlayerData d): return remainLine(d)   // 与 gem hopeLine→remain 同族；禁新 PAPI
```

- HopeMeter `drop_q` / RunPapi `drop_q_hope`：**吃 SoT 不改**（hope→remain 后自动诚实）。  
- `HopeMeter.dropQuality(cur,need)` 二元 glance **可留回归/其它调用**；本窗主路径改吃 remain。  
- SettleService 成色枝 **不改接线**。

### 2.3 调用方接线

| 点 | 动作 |
|----|------|
| `EmberDropCraftPity.remainLine` / `clearsLeft` | 二元/三元重载 + 旧一元委托 false；pd 喂 CraftNudge.ready |
| `EmberDropQualityPity.remainLine` / `clearsLeft` / `hope` | 同上；hope→remainLine(pd) |
| `EmberSettleService` C/Q remain 枝 | **不改**（吃 SoT） |
| `EmberHopeMeter.priorityTracks` drop_q | **不改**（吃 hope→remain） |
| `EmberRunPapi` `drop_q_hope` | **不改**（吃 hope） |
| TrMenu / 甲闸 / GearNextHint / SetChase / mapid | **本窗不改** |
| 新开精工 hub 轨 | **禁止** |

### 2.4 单测（最小包）

| 用例 | 期望 |
|------|------|
| C/Q cur=3 · nudgeReady=true | remain 含「再连出×**1**」+「今日加码待用」 |
| C/Q cur=2 · true | 「再连出×**2**」+「今日加码待用」 |
| C/Q cur=4 · true | 「再连出×**1**」+「今日加码待用」 |
| C/Q cur=3 · false | 「再连出×**2**」；**无**加码尾 |
| C/Q cur=0 · false | 旧「再连出×5」（回归 TIP-Q / D1323） |
| C/Q cur≥NEED | 旧已满串 |
| `remainLine(int)` / `clearsLeft(int)` | 与旧断言一致 |
| Q `hope(pd)` 有 ready | 与 remain 有效剩余一致（或同串） |
| `git show` | 无 NEED/FLOOR/BONUS/权重常量改；无 STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无新 PAPI；无甲闸/SetChase densify |

### 2.5 玩家口径（固定）

- 今日掉落精工/成色保底加码还没用：结算（与成色枢纽）会写 **今日加码待用**，且「再连出几次」按下次会多计一次来算，不再多报。  
- 今日加码已用或保底已满：跟以前一样。  
- 保底仍要满 5、加码仍是 +1；掉落权重 / NEED / FLOOR **本窗不动**。

---

## 3. 验收

- [ ] cur=3 + 加码待用：C/Q remain（及 Q hope/hub）写再连出 1 + 今日加码待用  
- [ ] 加码已用 / 一元重载回归  
- [ ] 无新 PAPI / 无新精工 Hope 轨 / lore≤3 不加成行  
- [ ] 未改 NEED/FLOOR/BONUS/权重 / stampable / L11·L13 / 甲闸 / SetChase  
- [ ] L13 施工仍 HOLD  

---

## 4. 批后派单

**余烬-插件**（CoreRpg · `EmberDropCraftPity` / `EmberDropQualityPity` remain·clearsLeft·hope + 单测扩；Settle / HopeMeter / RunPapi 吃 SoT 不改或仅 hope→remain）

策划本窗：docs-only（零业务 jar）

---

## 5. HOLD

- **L13 施工 HOLD**（TIP-AI FAIL · 显式开工前不开施工 tip）  
- 刃 L11/L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 densify / glance twin / 深化 densify / ForgeChoice densify / Affix / 伤乘 / 抬表 / sx20 / skill_mult / STAMP  
- 新开精工 HopeMeter 轨 / 抬掉落权重  

## STATUS: OPEN · TIP-AR · 2026-10-12
