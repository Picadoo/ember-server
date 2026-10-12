# RESEARCH · 宝石通关保底加码 remain/希望串诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-AP · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AO CLOSED live 后 AN→AO 追烙摘要链已齐；总控下一窗继续 **可感 UX / 变强途径（非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify）**，**优先离开签名追烙族**。扫 HopeMeter 非追烙 / mapid / gem pity / GearNextHint / TrMenu：发现 **`EmberGemPityNudge` 日首 +1 已 live**，但 `remainLine` / hub `%corerpg_p1_gem_pity_hope%` **不认「今日加码待用」** → 「再通关 n 次得石」可多报 1 次（SoT 说谎）。  
> **上游：** TIP-AO CLOSED live `1.65.780-d12743` · TIP-E CLOSED live gem glance · TIP-Q drop-craft pity · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 PITY_NEED / BONUS 表**

---

## 0. 一句话

日首宝石保底加码已生效时，枢纽/聊天「再通关几次得石」必须跟真实下次结算一致；今天加码静默、摘要多报。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberGemPityNudge.BONUS=1` · 日首通关 settle 时 `consumeExtra` → pity +1 额外 | `EmberGemPityNudge` · `EmberGemAcquire.onClear` ~82–88 | **加码真生效** |
| E2 | `remainLine(int cur)` / `remainLine(pd)` 只看 `cur` vs `PITY_NEED=5`；**不读** `GemPityNudge.ready` | `EmberGemAcquire` ~52–61 | **摘要不知加码** |
| E3 | hub `%corerpg_p1_gem_pity_hope%` → **`remainLine(pd)`**（非 hopeLine） | `EmberRunPapi` ~801–803 · TIP-E | 枢纽一眼吃 remain |
| E4 | `HopeMeter.priorityTracks` gem 轨 / `MapIdentity.methodLine` 宝石半行 / `GemSocket` 聊天 → 同吃 `remainLine` | HopeMeter ~460 · MapIdentity ~134 · GemSocket | **多口同谎** |
| E5 | `clearsLeft(3)=2`；加码待用时下次 settle 实际 `+1+1` → 满档发石 → **真剩 1 通** | onClear + remain 对照 | **可复现多报** |
| E6 | `clearsLeft(4)=1`；加码不改「1 通」下限（仍 1） | 同上 | 满档前一档数字不变，但仍应标「今日加码待用」 |
| E7 | `hopeLine` → `HopeMeter.gemPity(cur,5)` 亦不认 nudge；主路径已是 remain，但须同口径 | GemAcquire ~41–42 | 接线一致性 |
| E8 | AN→AO 追烙链 / 甲闸 AK→AL→AM / AH attune / AJ sig page **已齐**；SetChase 六槽 / 扩 stampable / 甲闸再扫 **禁** | TIP-AO/AN/AM… | **本债=宝石保底 UX 诚实，换轴离开追烙** |
| E9 | TIP-J/T：禁 **新** gem glance twin；本债 = **改既有 remain 串** 跟 nudge SoT，不新开 PAPI | power-paths | 正交可感 |

---

## 2. 错位场景（玩家面）

| 场景 | 真结算（下次通关） | 今日 remain / hub | 体感 |
|------|-------------------|-------------------|------|
| pity=3 · 今日加码未用 | +2 → 满 5 发石 | 「再通关**2**次得石」 | **多报 1 · 假** |
| pity=2 · 加码未用 | +2 → 4；再 +1 → 发 | 「再通关**3**次」真 **2** | **多报 1 · 假** |
| pity=4 · 加码未用 | +1（或 +2）→ 发 | 「再通关1次」 | 次数对；**不说加码可用** |
| pity=3 · 今日加码已用 | +1 → 4 | 「再通关2次」 | **对** |
| pity≥5 | 下次发石 | 「已满 · 下次通关得石」 | **不变** |

---

## 3. 「对齐」定义（钉死）

1. **复用** `EmberGemPityNudge.ready(pd)` / `BONUS`；**禁**第二套日闩或抬 `PITY_NEED`。  
2. `remainLine(PlayerData)`：以 `pity` + `ready` 算 **有效剩余通关数**；`ready && left>1` → `left' = left - 1`（因下次 +BONUS）；`left==1` 保持 1。  
3. `ready && cur < PITY_NEED`：尾缀 **`今日加码待用`**（短；禁 celebratetwin / 禁新 PAPI）。  
4. `remainLine(int cur)` 保留 = 无加码口径（单测 / 无 pd）；或显式 `remainLine(cur, nudgeReady)`。  
5. `clearsLeft`：可加重载 `clearsLeft(cur, nudgeReady)`；旧一元不变。  
6. `hopeLine(pd)`：与 remain **同有效剩余口径**（或委托 remain）；禁改 `gemPity(cur,need)` 纯函数语义除非测试同步。  
7. **零**改 `PITY_NEED` / `BONUS` / onClear 结算序；**零**新 `%corerpg_p1_*`；**零** TrMenu 加 lore；**零** SetChase / mapid 六槽；**零**甲闸再扫。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫 / GearNextHint 全局甲闸  
- SetChase 六槽 densify / mapid haveNeed 扩甲槽  
- Hope/SetChase/gem **新** glance twin；ForgeChoice densify  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify  
- 再抬宝石战力 / 改合成价 / GemCraftCost densify  

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **宝石通关保底加码 remain/希望串诚实对齐（可施工薄）** | **采纳** — 换轴离开追烙；SoT 改既有串；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| SigFeel 非追烙 leftover（active/wornOff） | **否** — AO 后枝已够；无新说谎证据 |
| mapid / MapIdentity 扩六槽 | **否** — SetChase/图鉴 densify 硬禁 |
| GearNextHint 甲闸 | **否** — 总控禁再扫 |
| TrMenu hub lore | **否** — 禁加 lore |
| HopeMeter 其它非追烙 densify | **否** — TIP-J/T 停窗 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |
| 观察停窗 only | **否** — 有可感诚实债 |
