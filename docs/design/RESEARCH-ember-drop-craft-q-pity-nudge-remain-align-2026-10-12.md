# RESEARCH · DropCraft/Q 日首加码 remain/希望串诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-AR · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AQ CLOSED live 后洗练加码 remain 已齐；总控下一窗显式：**DropCraft/Q 加码 remain 旁债**（同构 AP 宝石 + AQ 洗练）。扫 `EmberDropCraftPity` / `EmberDropQualityPity` / settle / Hope / RunPapi：发现 **`EmberDropCraftNudge` / `EmberDropQualityNudge` 日首 +1 已 live**（`applyToGrants` craft0/标准 路径 `consumeExtra`），但 `remainLine` / `hope` / 结算 chat·AB **不认「今日加码待用」** → 「再连出精工0×n / 再连出标准 n 次」可多报 1 次（SoT 说谎 · **同构 AP/AQ**）。  
> **上游：** TIP-AQ CLOSED live `1.65.782-d12775` · feat `58e9ac299` · TIP-AP CLOSED live `1.65.781-d12759` · TIP-Q CLOSED live drop-craft settle · D10737/D10721 Nudge · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 DropCraft/Q NEED/FLOOR/BONUS / CRAFT_WEIGHTS / QUALITY_WEIGHTS**

---

## 0. 一句话

日首掉落精工/成色保底加码已生效时，结算与枢纽「再连出几次触发底」必须跟真实下次 pity 推进一致；今天加码静默、摘要多报。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberDropCraftNudge.BONUS=1` · craft0 连出 tick 时 `consumeExtra` → pity +1 额外 | `EmberDropCraftNudge` · `EmberDropCraftPity.applyToGrants` ~49–52 | **精工加码真生效** |
| E2 | `EmberDropQualityNudge.BONUS=1` · 标准连出 tick 时 `consumeExtra` → pity +1 额外 | `EmberDropQualityNudge` · `EmberDropQualityPity.applyToGrants` ~72–75 | **成色加码真生效** |
| E3 | `DropCraftPity.remainLine(cur/pd)` 只看 cur vs NEED=5；**不读** `DropCraftNudge.ready` | `EmberDropCraftPity` ~95–104 | **精工摘要不知加码** |
| E4 | `DropQualityPity.remainLine` / `clearsLeft` / `hope(pd)` → `HopeMeter.dropQuality(cur,NEED)` 亦不认 nudge | `EmberDropQualityPity` ~26–46 · HopeMeter ~165–167 | **成色摘要/希望不知加码** |
| E5 | 结算非 forced：`SettleService` chat/AB → **`remainLine(pd)`**（C 有 settle_remain 热关；Q 同枝） | `EmberSettleService` ~729–747 · TIP-Q | **通关一眼说谎** |
| E6 | HopeMeter `drop_q` 轨 / RunPapi `%corerpg_p1_drop_q_hope%` → **`hope(pd)`**（glance 族） | HopeMeter ~455–456 · RunPapi ~805–807 | **枢纽同谎（成色）** |
| E7 | `cur=3 · NEED=5`：无加码 → 下次 craft0/标准 → 4；加码待用 → 下次 +1+BONUS → **5 满档**；remain 仍写「再连出×**2**」 | apply + remain 对照 | **可复现多报** |
| E8 | `cur=NEED-1`：`left=1`；加码不改下限 1，但仍应标「今日加码待用」 | 同上 | 数字可不变；须明示加码 |
| E9 | TIP-AP/AQ 加码 remain **已齐**；TIP-Q 精工 settle remain **已齐**（不认 nudge）；AN→AO 追烙 / AK→AM 甲闸 **已齐**；SetChase 六槽 / 扩 stampable / 甲闸再扫 **禁** | TIP-AP/AQ/Q… | **本债=掉落 C/Q 加码 UX 诚实，续 AP/AQ 同构轴** |
| E10 | TIP-J/T：禁 **新** glance twin / 新 PAPI；本债 = **改既有 remain/hope 串** 跟 nudge SoT | power-paths | 正交可感 |
| E11 | DropCraft **无** HopeMeter 轨（TIP-Q HOLD 枢纽挂精工）；本窗 **不**新开 hub 轨 / 新 PAPI | TIP-Q D12416 | 精工只修 settle remain SoT |

---

## 2. 错位场景（玩家面）

| 场景 | 真结算（下次 craft0 / 标准） | 今日 remain / hope | 体感 |
|------|---------------------------|-------------------|------|
| cur=3 · NEED=5 · 加码未用 | +1+BONUS → **5** 满 | 「再连出×**2**」 | **多报 1 · 假** |
| cur=2 · 加码未用 | → 4；再 1 次未满 | 「再连出×**3**」真有效 **2** | **多报 1 · 假** |
| cur=4 · 加码未用 | → 5(+BONUS) 满 | 「再连出×1」 | 次数对；**不说加码可用** |
| cur=3 · 今日加码已用 | → 4 | 「再连出×2」 | **对** |
| cur≥NEED | 下次强制底 | 「已满 · 下次必≥底」 | **不变** |

（精工「再连出精工0×n」与成色「再连出标准 n 次」同构。）

---

## 3. 「对齐」定义（钉死）

1. **复用** `EmberDropCraftNudge.ready` / `EmberDropQualityNudge.ready` / `BONUS`；**禁**第二套日闩或抬 `NEED`/`FLOOR`。  
2. `clearsLeft(cur, nudgeReady)`（Q 已有一元；C 可加对称）：旧 `left`；`ready && left>1` → `left' = left - 1`；`left==1` 保持 1。  
3. `remainLine(cur, nudgeReady)`：用有效 left；`ready && cur < NEED` 尾缀 **`今日加码待用`**（短；禁 celebratetwin / 禁新 PAPI）。  
4. `remainLine(int)` / `clearsLeft(int)` 保留 = 无加码口径（单测 / 无 pd）；`remainLine(PlayerData)` 喂对应 `Nudge.ready`。  
5. **成色** `hope(pd)`：改吃 **`remainLine(pd)`**（与宝石 hopeLine→remain 同族）；HopeMeter `drop_q` / RunPapi `drop_q_hope` **吃 SoT 不改接线**（或显式改吃 remain，二选一以零新 PAPI 为准）。`HopeMeter.dropQuality(cur,need)` 二元 glance **可留回归**。  
6. **精工**：无 hub 轨本窗；SettleService 已吃 `remainLine(pd)` → SoT 修好即结算诚实；**禁**新开精工 Hope 轨 / 新 `%corerpg_p1_*`。  
7. **零**改 `NEED`/`FLOOR`/`BONUS`/`CRAFT_WEIGHTS`/`QUALITY_WEIGHTS` / `applyToGrants` 结算序；**零** TrMenu 加 lore；**零** SetChase / mapid 六槽；**零**甲闸再扫；**零** Affix / ForgeChoice densify。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫 / GearNextHint 全局甲闸  
- SetChase 六槽 densify / mapid haveNeed 扩甲槽  
- 新开 DropCraft HopeMeter 轨 / 新 glance twin / 新 PAPI  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify / ForgeChoice densify  
- 抬掉落权重 / 改 NEED/FLOOR / settle_remain 热关语义（可保留）

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **DropCraft/Q 日首加码 remain/希望串诚实对齐（可施工薄）** | **采纳** — 总控显式旁债；续 AP/AQ 同构；SoT 改既有串；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| 仅修 C 或仅修 Q | **否** — 总控「DropCraft/Q」同窗同构；单主题=掉落加码 remain |
| 新开精工 hub Hope 轨 | **否** — TIP-Q HOLD；禁新 PAPI / densify |
| SigFeel / 追烙 leftover | **否** — AN→AO 已齐 |
| mapid / MapIdentity 扩六槽 | **否** — SetChase densify 硬禁 |
| GearNextHint 甲闸 | **否** — 总控禁再扫 |
| TrMenu hub lore | **否** — 禁加 lore |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |
| 观察停窗 only | **否** — 有可感诚实债 |
