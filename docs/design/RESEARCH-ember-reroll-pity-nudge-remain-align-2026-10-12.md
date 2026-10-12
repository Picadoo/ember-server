# RESEARCH · 洗练保底日首加码 remain/希望串诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-AQ · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AP CLOSED live 后宝石加码 remain 已齐；总控下一窗继续 **可感 UX / 变强途径（非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify）**。扫 HopeMeter 非宝石/非追烙 · mapid · GearNextHint · TrMenu · 主线下一环 · AP RESEARCH HOLD：发现 **`EmberRerollPityNudge` 日首 +1 已 live**（GrowthService `plan`），但 `remainLine` / `attemptsUntilCap` / HopeMeter 洗练轨 **不认「今日加码待用」** → 「含本次≤n 次必出上限档」可多报 1 次（SoT 说谎 · **同构 AP 宝石债**）。  
> **上游：** TIP-AP CLOSED live `1.65.781-d12759` · feat `c5c7ae391` · TIP-E/D1248 reroll remain · D10977 `EmberRerollPityNudge` · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 reroll.pity / BONUS 表**

---

## 0. 一句话

日首洗练保底加码已生效时，确认页/希望轨「含本次≤n 次必出」必须跟真实下次 pity 推进一致；今天加码静默、摘要多报。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberRerollPityNudge.BONUS=1` · 非 forced 洗后 `consumeExtra` → `pityAfter + BONUS` | `EmberRerollPityNudge` · `EmberGrowthService.plan` ~2367–2374 | **加码真生效** |
| E2 | `remainLine(pity,need)` / `attemptsUntilCap` 只看 pity vs need；**不读** `RerollPityNudge.ready` | `EmberRerollPity` ~30–47 | **摘要不知加码** |
| E3 | 确认预览 / ActionBar / 档位占位串 → **`remainLine(pity, reroll.pity)`**（无 ready） | `EmberGrowthService` ~2258–2265 · ~2679–2681 | **工坊一眼说谎** |
| E4 | HopeMeter 洗练轨 → **`EmberRerollPity.glance(pity,need)`**（亦不认 nudge） | `EmberHopeMeter.priorityTracks` ~447–449 | **希望轨同谎** |
| E5 | `pity=3 · need=5`：无加码 → 本次后 pity=4（再 1 次未 forced）；加码待用 → 本次后 pity=5 → **下次必 forced**；remain 仍写「含本次≤**2**次」 | plan + remain 对照 | **可复现多报** |
| E6 | `pity=need-1`：`attemptsUntilCap=1`；加码不改下限 1（仍 1），但仍应标「今日加码待用」 | 同上 | 数字可不变；须明示加码 |
| E7 | `afterRemainLine` 吃 settle 后 pityAfter（已含本次 +BONUS）→ **事后串诚实**；债在 **事前** remain/glance | GrowthService ~2485 | 本窗主改事前 SoT |
| E8 | TIP-AP 宝石加码 remain **已齐**；AN→AO 追烙 / AK→AM 甲闸 / AH attune / AJ sig page **已齐**；SetChase 六槽 / 扩 stampable / 甲闸再扫 **禁** | TIP-AP/AO/AM… | **本债=洗练保底 UX 诚实，续 AP 同构轴** |
| E9 | TIP-J/T：禁 **新** glance twin / 新 PAPI；本债 = **改既有 remain 串**（及 Hope 洗练轨改吃 remain）跟 nudge SoT | power-paths | 正交可感 |
| E10 | DropCraft / DropQuality 亦有日首 Nudge 而 `remainLine(pd)` 不认 ready | `EmberDropCraftPity` · `EmberDropQualityPity` | **旁债 HOLD 另 tip**（本窗单主题洗练） |

---

## 2. 错位场景（玩家面）

| 场景 | 真结算（本次非 forced 洗） | 今日 remain / 希望轨 | 体感 |
|------|---------------------------|---------------------|------|
| pity=3 · need=5 · 加码未用 | pityAfter+1 → **5**；下次必出上限 | 「含本次≤**2**次」 | **多报 1 · 假** |
| pity=2 · need=5 · 加码未用 | → 4；再 1 次未 forced | 「含本次≤**3**次」真有效 **2** | **多报 1 · 假** |
| pity=4 · need=5 · 加码未用 | → 5(+BONUS 仍≥need) | 「含本次≤1次」 | 次数对；**不说加码可用** |
| pity=3 · 今日加码已用 | → 4 | 「含本次≤2次」 | **对** |
| pity≥need | 本次 forced | 「这次必出上限档」 | **不变** |

---

## 3. 「对齐」定义（钉死）

1. **复用** `EmberRerollPityNudge.ready(pd)` / `BONUS`；**禁**第二套日闩或抬 `reroll.pity`。  
2. `attemptsUntilCap(pity, need, nudgeReady)`：旧 `incl`；`ready && incl>1` → `incl' = incl - 1`（因本次非 forced 后 +BONUS）；`incl==1` 或已 forced 保持。  
3. `remainLine(pity, need, nudgeReady)`：用有效 incl；`ready && pity < need` 尾缀 **`今日加码待用`**（短；禁 celebratetwin / 禁新 PAPI）。  
4. `remainLine(pity, need)` / `attemptsUntilCap(pity, need)` 保留 = 无加码口径（单测 / 无 pd）。  
5. HopeMeter 洗练轨：改吃 **`remainLine(pity, need, ready)`**（与宝石轨同族诚实）；**禁**新 `%corerpg_p1_*`；`glance(pity,need)` 二元可留回归。  
6. GrowthService 确认预览 / 占位 remain：**喂 ready**；`afterRemainLine` **本窗可不改**（已吃 settle 后计数）。  
7. **零**改 `reroll.pity` / `BONUS` / `plan` 结算序；**零** TrMenu 加 lore；**零** SetChase / mapid 六槽；**零**甲闸再扫；**零** Affix 表 / ForgeChoice densify。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫 / GearNextHint 全局甲闸  
- SetChase 六槽 densify / mapid haveNeed 扩甲槽  
- DropCraft / DropQuality 加码 remain（**旁债另 tip**）  
- Hope/gem **新** glance twin；ForgeChoice densify  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify  
- 抬洗练币价 / lock_shard / RerollCoin densify  

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **洗练保底日首加码 remain/希望串诚实对齐（可施工薄）** | **采纳** — 续 AP 同构；SoT 改既有串；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| DropCraft / DropQuality 加码 remain | **否本窗** — 旁债另 tip；单主题 |
| SigFeel / 追烙 leftover | **否** — AN→AO 已齐 |
| mapid / MapIdentity 扩六槽 | **否** — SetChase densify 硬禁 |
| GearNextHint 甲闸 | **否** — 总控禁再扫 |
| TrMenu hub lore | **否** — 禁加 lore |
| HopeMeter 其它 densify / glance twin | **否** — TIP-J/T 停窗 |
| 主线下一环施工 | **否** — 无本窗可感诚实债证据优先于本同构债 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |
| 观察停窗 only | **否** — 有可感诚实债 |
