# DESIGN · LOCK LockShard 日首 soft 预览/glance 诚实对齐（薄窗 · 可施工）· 2026-10-12

> **状态：** OPEN · TIP-AV · 2026-10-12  
> **上游 RESEARCH：** [`RESEARCH-ember-lockshard-soft-glance-align-2026-10-12.md`](RESEARCH-ember-lockshard-soft-glance-align-2026-10-12.md)  
> **对接 live：** TIP-AU CLOSED live `1.65.786-d12839` · RerollLockShardSoft D9937 · RerollCoinSoft D9745 · GearNextHint SOFT_TAIL（TIP-AS）· ForgeHeldNext/SinkGlance soft peek 模式（TIP-AS/AT）  
> **HOLD 旁注：** **L13 施工 HOLD**（TIP-AI FAIL · PASS≠开工）；TIP-AD exclude 刃 L11/L13 **保留**  
> **硬禁：** 零功率表 · 零改 MULT/经济表/日闩键 · 零 STAMP 抬 · 零扩 stampable · 零 L11/L13 mods · 零 Affix/伤乘/skill_mult/glance twin/深化 densify/sx20/ForgeChoice densify · **零再扫甲闸 · 零 SetChase 六槽 densify · 零新 PAPI · 零 TrMenu 加 lore · 零再叠 AS/AT densify · 零 pity-remain 再叠 · 零发明第二套 LockShard soft**

---

## 0. 一句话

让手持下一确定 / Sink 缺料 / 洗练菜单锁定邀请在日首锁碎减负待用时按半锁碎（+半币若币 soft ready）并明示减负，且「料够/还差」跟真付费一致。

---

## 1. 问题陈述

| 层 | 现状 | 应然 |
|----|------|------|
| GrowthService 锁洗付费 + `!confirm` 预览 | 已吃 LockShard soft（+ Coin soft） | 保持 |
| ForgeHeldNext LOCK canPay/shortfall | 全额 lockRerollCost | **soft peek；ready 可标减负尾** |
| SinkGlance goal LOCK | 无专支 / 非 soft | **LOCK soft lack（同构 BRAND）** |
| 菜单 `"lock"` / 邀请按钮 ls | 全额 | **soft peek + 可选 softTail** |
| MULT / C_USED / C11 表 | 0.5 / 日闩 | **零改** |
| 甲路径 | AK–AM 早退 | **本窗不动** |

---

## 2. 方案 M（定稿荐）

### 2.1 SoT（改既有串 · 禁新族）

```
// peek only — never consume from glance/menu
lockRerollCost(tier):                 // 旧一元 ≡ 全额（保留）
lockRerollCost(tier, pd):
  shard = C11.tN.shard          // 基础碎 · 全额（无 soft）
  lock  = C11.tN.lock_shard
  coin  = C11.tN.coin
  if pd != null:
    lock = RerollLockShardSoft.apply(pd, lock, false)
    coin = RerollCoinSoft.apply(pd, coin, false)   // 同付费路径 leftover
  return Cost(shard + lock, 0, 0, 0, coin)

ForgeHeldNext.line(..., pd):
  // 甲早退不变（TIP-AK）
  lock = lockRerollCost(tier, pd)   // was full-only
  // canPay/shortfall 用 soft Cost
  // LOCK「料够」ready → + softTail(RerollLockShardSoft.ready(pd)
  //            || RerollCoinSoft.ready(pd))

SinkGlance goal LOCK:
  cost = lockRerollCost(held.tier, pd)
  lack = lackHalf(lackingFor(cost))
  focus = "锁定洗练"

GrowthService rerollPapi "lock" / 非锁邀请按钮:
  ls = RerollLockShardSoft.apply(d, lockShardFor(tier), false)
  文案写 ls + softTail(RerollLockShardSoft.ready(d))
  // 锁洗付费+!confirm 预览本窗不改
```

- **复用** 既有 soft `ready` / `apply(..., false)`；**禁止**改 MULT / C_USED / 付费 `consume=true`。  
- **禁止**新 `%corerpg_p1_*` / 新 remain 族 / TrMenu 加 lore / 抬表 / 发明第二 soft 类。

### 2.2 调用方接线

| 点 | 动作 |
|----|------|
| `ForgeHeldNext.lockRerollCost(tier, pd)` | soft peek lock_shard（+ coin） |
| `ForgeHeldNext.line` | LOCK soft canPay/shortfall + softTail |
| `SinkGlance.tellHeld` | goal LOCK soft lack |
| GrowthService 菜单 lock / 邀请按钮 | ls soft peek |
| GrowthService 锁洗付费+预览 | **本窗不改**（已诚实） |
| AS/AT softCost 他轨 / 甲闸 / SetChase / TrMenu lore | **本窗不改** |

### 2.3 单测（最小包）

| 用例 | 期望 |
|------|------|
| LockShard soft ready · 袋料仅够「基础碎+半锁碎」(+半币若测币) | HeldNext LOCK →「料够」；可含「今日减负待用」 |
| soft 已用 / 无 pd | 「还差」/全额回归；无 softTail |
| Sink goal LOCK soft ready | lack 按半锁碎 Cost |
| 菜单 lock 串 soft ready | 另加半锁碎 + 可含减负尾 |
| 甲 | ARMOR_REFUSE 回归（TIP-AK/AM） |
| AT BRAND soft / AS 锻四软 | 回归不破 |
| `git show` | 无 MULT/经济表/STAMP/C12/loot/DEFS/L11/L13/Affix/skill_mult；无新 PAPI；无甲闸/SetChase densify |

### 2.4 玩家口径（固定）

- 今日锁定洗练锁碎减负还没用：手持下一确定、Sink 缺料、菜单锁定邀请按 **半锁碎**（币位若日首洗练币减负待用则半币）并标 **今日减负待用**；料够按半锁碎算。  
- 今日减负已用：跟以前全额一样。  
- 减负仍是 ×0.5、一日一次；C11 `lock_shard` / 币表本体本窗不动。  
- 普通洗练（非 LOCK）菜单碎/币串：本窗可不扩（旁债）；锁洗命令预览已诚实。  
- 护甲：仍早退「护甲随护符」。

---

## 3. 验收

- [ ] soft ready：HeldNext LOCK「料够」跟半锁碎一致；可含今日减负待用  
- [ ] Sink LOCK / 菜单 lock 邀请 soft ready：缺料/另加按半锁碎  
- [ ] soft 已用 / 无 pd 全额回归  
- [ ] 甲早退回归；无新 PAPI / lore≤3 不加成行  
- [ ] 未改 MULT / C11 / 付费序 / stampable / L11·L13 / 甲闸 / SetChase / pity / AS·AT densify  
- [ ] L13 施工仍 HOLD  

---

## 4. 批后派单

**余烬-插件**（CoreRpg · `EmberForgeHeldNext` lockRerollCost soft peek · LOCK canPay/softTail · `EmberSinkGlance` LOCK soft lack · GrowthService 菜单 lock/邀请 ls soft · 复用 `RerollLockShardSoft`/`RerollCoinSoft`/`SOFT_TAIL` + 单测）

策划本窗：docs-only（零业务 jar）

---

## 5. HOLD

- **L13 施工 HOLD**（TIP-AI FAIL · 显式开工前不开施工 tip）  
- 刃 L11/L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 densify / glance twin / 深化 densify / ForgeChoice densify / Affix / 伤乘 / 抬表 / sx20 / skill_mult / STAMP  
- AS/AT CostSoft 再 densify；pity-remain 再叠；发明第二 LockShard soft / 抬 MULT  

## STATUS: OPEN · TIP-AV · 2026-10-12
