# RESEARCH · 转化周次容错 CapSoft 预览/weekLine 诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-AW · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AV CLOSED live 后 AS→AT→AV **CostSoft glance 链（含 LOCK）已齐** · AU Hope soft 已上；总控下一窗继续 **可感 UX / 变强途径**，**优先离开 soft-glance / pity-remain / 甲闸 / 追烙摘要族**。扫 mapid / HopeMeter other / GearNextHint 非 soft / TrMenu / 主线下一环 / wear cue leftover / boss telegraph leftover：发现 **`EmberConvertCapSoft` 周次容错已 live（D9249）**，但 `convertHeld` **在 `!confirm` 预览路径就 `consume`**，且 `weekLine` / 预览「本周剩余」/ ConvertSure 直办在 `used>=WEEKLY_CAP` 时仍写「环已完成 / 剩余 0」→ 容错待用时假「用完」、确认时 soft 已吞导致 **二次确认拒办（SoT 说谎 + peek 误吞）**。  
> **上游：** TIP-AV CLOSED live `1.65.787-d12855` · feat `901632f09` · ConvertCapSoft D9249 · ConvertCostSoft（TIP-AT 已齐费用 soft）· ConvertPath.weekLine D732 · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）· post-convertcap STOP densify（本债=诚实对齐 · **非**抬 cap / 非再发明 soft）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 WEEKLY_CAP / C_CONV / CapSoft EXTRA（无 EXTRA 常量 · 禁发明第二容错）· 零再叠 CostSoft glance · 零 pity-remain 再叠 · 零 soft-glance 费用族再叠 · 零 ConvertCap densify（抬次数/改 week 键）**

---

## 0. 一句话

周转化次数已满且周次容错待用时，weekLine/预览必须明示「还可容错 1 次」，且容错只在确认成功时消耗——预览不得吞 soft。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberConvertCapSoft`：`ready`→`consume` 周闩一次；**无 peek API**（仅 ready/consume） | `EmberConvertCapSoft.java` | **容错真存在 · 无安全 peek** |
| E2 | `convertHeld`：`refuse != null && used >= WEEKLY_CAP` 时 **先 `consume`**，再 `refusal(..., used-1, …)` | GrowthService ~3044–3053 | **预览与确认同一吞 soft 路径** |
| E3 | `if (!confirm)` 在 consume **之后**：`weekLeft = WEEKLY_CAP - used`（仍 0）拼「本周剩余 0/1」 | GrowthService ~3078–3111 | **预览已吞 soft 仍报剩余 0** |
| E4 | 玩家点确认 → 再进 `convertHeld(..., true)`：`used` 仍 ≥CAP · CapSoft 已 used → `consume` 失败 → **refuse 拒办** | 同路径 | **预览→确认链路上容错几乎不可达** |
| E5 | ConvertSure 直办：`!confirm` 内 `return convertHeld(..., true)`；若先走过 E2 则 soft 已吞，直办同拒 | GrowthService ~3118–3121 | **直办与容错互踩** |
| E6 | `ConvertPath.weekLine`：`left<=0` →「本周转化环已完成 · 下周重置」；**不看 CapSoft.ready** | ConvertPath ~89–95 | **枢纽/命令 weekLine 假完成** |
| E7 | `afterSet` AB「转化环 · 余 N」同 `WEEKLY_CAP-used`；菜单「周 1 次」tip 不知容错 | ConvertPath ~108–117 | **路径设置口同谎** |
| E8 | AS→AT→AV CostSoft glance **已齐**；AP–AR pity remain **已齐**；AK–AM 甲闸 **已齐**；AN–AO 追烙 **已齐**；AU Hope soft **已齐** | TIP-AV…AU | **本债=周次 cap soft 诚实；离开费用 soft-glance / pity / 甲闸 / 追烙** |
| E9 | post-convertcap：**STOP ConvertCap densify** | RESEARCH-post-convertcap | **禁抬 WEEKLY_CAP / 禁再发明 soft；本窗只修接线诚实** |
| E10 | TIP-J/T：禁 **新** glance twin；本债 = **改既有 weekLine/预览串 + consume 时机** | power-paths | 正交可感 |

---

## 2. 错位场景（玩家面）

| 场景 | 真付费/真能力 | weekLine / 预览口 | 体感 |
|------|---------------|-------------------|------|
| 本周已转化 1 次 · CapSoft ready · 点预览 | 应还能容错转 1 次 | 预览吞 soft · 仍写「剩余 0」· 点确认 **拒办** | **假完成 + 容错白吞** |
| 同上 · ConvertSure 直办 | 本应 confirm 扣料成功 | soft 在进入直办前已吞 → 拒办 | **直办踩容错** |
| CapSoft ready · 只看 weekLine / 转化路径 | 还可容错 1 次 | 「环已完成 · 下周重置」 | **假完成 · 拦人** |
| CapSoft 已用 / used&lt;CAP | 跟表一致 | 跟表一致 | **对** |

---

## 3. 「对齐」定义（钉死）

1. **复用** `ConvertCapSoft.ready` / `consume`；**禁**抬 `WEEKLY_CAP`、改 `C_CONV` / `C_USED` 周键、发明第二套容错或 EXTRA 次数。  
2. **预览 `!confirm`：** 仅 `ready` peek；**禁止** `consume`。`used>=CAP && ready` → 视作「可继续预览/出确认钮」；`weekLeft` 展示口径 = **容错待用（还可 1 次）**（文案钉 DESIGN）。  
3. **确认 `confirm=true`：** 仅当 `used>=CAP` 且需容错时 **`consume` 成功** 才放行；consume 失败 → 诚实拒办。  
4. **`ConvertPath.weekLine` / afterSet 余次：** `used>=CAP && ready` → 明示容错待用（非「环已完成」）；已用 / 无 pd → 旧口径。  
5. **ConvertSure 直办：** 走确认路径（只在 confirm 吞 soft）；预览不得先吞。  
6. **零**新 `%corerpg_p1_*`；TrMenu **不加 lore**；**零**再叠 CostSoft glance / pity-remain / 甲闸 / 追烙摘要 densify。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫 / SetChase 六槽  
- ConvertCap **densify**（抬 WEEKLY_CAP / 再发明 soft / 改 week 闩语义为多送）  
- CostSoft glance 再叠（AS–AV 已齐）· pity nudge remain 再叠  
- mapid 扩甲 / TrMenu hub lore / 主线下一环大 DESIGN  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify / ForgeChoice densify  

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **转化周次容错 CapSoft 预览/weekLine 诚实对齐（可施工薄）** | **采纳** — 离开 soft-glance 费用族 / pity / 甲闸 / 追烙；SoT 改既有串 + consume 时机；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| gearlibHope 撤销窗 +5 分展示 | **否** — 欠报非假负；UndoWindow densify STOP；优先级低于本债 |
| MapFarmCue imprint soft 下一环 | **否** — 追烙摘要邻域；总控荐离开 chase-summary |
| mapid 扩甲 haveNeed | **否** — 扩 stampable / SetChase 邻域硬禁 |
| TrMenu hub lore | **否** — 禁加 lore |
| 主线下一环 / boss telegraph | **否** — 无本窗级说谎证据优先 |
| CostSoft / pity / 甲闸再扫 | **否** — 链已齐 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |
| 观察停窗 only | **否** — 有可感诚实债（含 peek 误吞） |
| ConvertCap densify | **否** — post STOP |

## STATUS: OPEN · TIP-AW · 2026-10-12
