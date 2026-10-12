# RESEARCH · LOCK LockShard 日首 soft 预览/glance 诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-AV · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AU CLOSED live 后总控显式下一窗 = **LOCK LockShard soft 旁债**。AS/AT 曾写「LOCK 无 soft / 不发明 soft」；AU E9 已钉 **`EmberRerollLockShardSoft` live**，HeldNext LOCK 仍全额。本窗复核：soft **已存在且付费+锁洗命令预览已吃**，说谎口在 glance/菜单邀请串 — **不是发明 soft，是对齐既有 soft**。  
> **上游：** TIP-AU CLOSED live `1.65.786-d12839` · feat `53a9fe3b8` · TIP-AT CLOSED live `1.65.785-d12823` · RerollLockShardSoft D9937 · RerollCoinSoft D9745 · TIP forge-roll-quality-lockshard · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/经济表/日闩 · 零再叠 AS/AT CostSoft densify · 零 pity-remain 再叠 · 零发明第二套 LockShard soft / 抬 MULT**

---

## 0. 一句话

日首锁定洗练锁碎减负已生效时，手持下一确定 / Sink 缺料 / 洗练菜单锁定邀请串必须跟真付费锁碎一致，并明示今日减负可用。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberRerollLockShardSoft`：`ready`→锁碎 ×`MULT=0.5`；日首一次；`C_USED=p1_reroll_lock_shard_soft` | `EmberRerollLockShardSoft.java` | **soft 已 live（非发明）** |
| E2 | GrowthService 锁洗付费+`!confirm` 预览：`apply(d, lockShard, confirm)` 已 soft；确认时「锁定洗练减负 · 锁碎 ×0.5」 | GrowthService ~2258–2292 | **命令预览诚实** |
| E3 | `ForgeHeldNext.lockRerollCost(tier)`：C11 `shard+lock_shard+coin` **全额**；`line` LOCK 分支 canPay/shortfall **无 soft peek**、无 `SOFT_TAIL` | ForgeHeldNext ~42–51 · ~95 · ~111–112 · ~180–185 | **目标 LOCK「料够/还差」可假** |
| E4 | TIP-AT RESEARCH E6「lock-reroll **无** CostSoft」**误**；AU E9 已纠正；AT 测试 `lockAndNullPd_fullPrice` 刻意断言 LOCK 无 softTail | AT RESEARCH E6 · AU RESEARCH E9 · AT GlanceTest ~124–137 | **旁债成立；本窗纠对齐** |
| E5 | SinkGlance：goal LOCK **无专支**（落入 enhance lack）；BRAND/CONVERT/ROLL 已 soft | SinkGlance ~53–74 | **LOCK 目标缺料口未对齐 soft（可补同构）** |
| E6 | 洗练菜单 `rerollPapi "lock"`：`另加 ls 碎片` **全额**；非锁预览邀请按钮同全额 ls | GrowthService ~2332–2333 · ~2714–2721 | **点前一眼多报锁碎** |
| E7 | 同路径 `RerollCoinSoft` 已吃币；HeldNext LOCK Cost.coins 仍全额 | GrowthService ~2242 · lockRerollCost | **锁轨币位同构 leftover（本窗随 Cost 一并 peek；禁抬 MULT）** |
| E8 | 无 `%corerpg_p1_forge_gap_lock%`；本窗 **禁新 PAPI** | RunPapi forge_gap_* | **只改既有串** |
| E9 | AS+AT CostSoft glance / AP–AR pity / AN→AO 追烙 / AK→AM 甲闸 / AU Hope 花费 **已齐** | TIP 链 | **本债=LOCK LockShard soft glance；非 densify 再叠他族** |
| E10 | TIP-J/T：禁 **新** glance twin；本债 = **改既有 canPay/缺料/菜单串** 跟 soft SoT | power-paths | 正交可感 |

---

## 2. 错位场景（玩家面）

| 场景 | 真付费（今日锁碎减负未用） | HeldNext / Sink / 菜单 lock | 体感 |
|------|--------------------------|----------------------------|------|
| C11 T1 lock_shard=40 · 袋内碎够「基础碎+20」· soft ready | 付锁碎20 · **料够锁洗** | 「锁定洗练 · **还差碎/币**」 | **假负 · 拦人** |
| soft ready · 菜单 Shift 锁定邀请 | 付半锁碎 | 「另加 **40** 碎片」 | **多报一倍 · 假** |
| soft 已用 / pd=null | 全额 | 全额 | **对** |
| 锁洗命令 `!confirm` 预览 | 已 soft | 已 soft | **对（本窗不改付费序）** |
| 手持甲 | ARMOR_REFUSE | 早退 | **不变（禁再扫甲闸）** |

---

## 3. 「对齐」定义（钉死）

1. **复用** `EmberRerollLockShardSoft.apply(pd, lockShard, false)`（及顺带 `RerollCoinSoft.apply(pd, coin, false)` 喂 LOCK Cost.coins）；**禁**第二套日闩或抬 `MULT` / C11 表价。  
2. `lockRerollCost(tier, pd)`（或等价）：只 soft **lock_shard 段**（+ coins 段）；**基础 shard 全额**；pd=null ≡ 旧一元全额。  
3. `ForgeHeldNext.line(..., pd)`：LOCK Cost **先 soft peek** 再 canPay/shortfall；ready 时「料够」可加 ` · 今日减负待用`（`SOFT_TAIL`）；甲早退不动。  
4. `SinkGlance`：goal **LOCK** 缺料 Cost soft peek（同构 BRAND）；甲早退不动。  
5. GrowthService 菜单 `"lock"` 串 + 非锁预览「锁定再洗」按钮文案：ls **soft peek** + 可选 softTail；**锁洗付费+`!confirm` 已 soft 本窗不改接线**。  
6. **零**新 `%corerpg_p1_*`；TrMenu **不加 lore**；**零**再叠 AS/AT 他轨 densify；**零** pity-remain；**零**发明新 soft 类。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫  
- SetChase 六槽 densify  
- AS/AT CostSoft 再 densify · pity nudge remain 再叠 · AU Hope 再叠  
- **发明**第二套 LockShard soft / 抬 MULT / 改 C11 表  
- Hope/SetChase **新** glance twin；ForgeChoice densify  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify  

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **LOCK LockShard soft glance 诚实对齐（可施工薄）** | **采纳** — 总控显式旁债；soft **已 live**；SoT 改既有串；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| 观察「真无 soft」换轴 | **否** — 证据 E1–E6 有诚实债 |
| 发明新 Lock CostSoft 类 | **否** — 复用 RerollLockShardSoft |
| AS/AT / pity / Hope 再扫 | **否** — 链已齐 |
| 甲闸 / SetChase 六槽 | **否** — 硬禁 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |

## STATUS: OPEN · TIP-AV · 2026-10-12
