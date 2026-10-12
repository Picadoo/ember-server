# tip · TIP-BC 宝石开孔日首门槛 Soft 预告/拒线诚实对齐（可施工薄 · 可感 UX）· D12952–D12967

> **状态：** **CLOSED live `1.65.794-d12967`** · feat `b13c47c69` · 批 A·M · 2026-10-12 22:52 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-gemhole-unlock-soft-cue-align-2026-10-12.md`](../design/RESEARCH-ember-gemhole-unlock-soft-cue-align-2026-10-12.md) · DESIGN [`DESIGN-ember-gemhole-unlock-soft-cue-align-2026-10-12.md`](../design/DESIGN-ember-gemhole-unlock-soft-cue-align-2026-10-12.md) · TIP-BB CLOSED live `1.65.793-d12951` · feat `6f7d01e91` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **离开 soft-latch / xp-boost cue** — **可感 UX / 变强途径**；GemHoleUnlockSoft **已 live**、Open/拒线撒谎或静默（**诚实 ≠ densify**）  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberGemSocket` Open ready 尾 + insert 拒线 soft/硬对齐 · 可选 UnlockSoft 旁 CUE 常量 · 单测；零改 DELTA/C_USED/sockUnlocked 硬表）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 DELTA/C_USED/sockUnlocked 硬表 · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠 · 零 soft-latch 假耗再叠 · 零 xp-boost cue 再叠 · 零 LifeCap/UndoWindow/FestivalEntry/AnvilRepair densify · 零 topUpTo 扩 soft densify · 零再降开孔门槛 densify**

---

## 0. 一句话

日首开孔门槛−1待用时，宝石页先说清楚；拒线跟 ready 对齐；打开列表不消耗。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **宝石开孔日首门槛 Soft 预告/拒线诚实对齐** | **采纳** | soft 已 live（D10785）；Open 静默、拒线硬表撒谎；零改 DELTA；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase · 非 soft-latch/xp-boost 再叠** |
| 再降 DELTA / 扩 soft 孔 | **否** | densify |
| AnvilRepair Soft 假耗 | **否** | 硬禁 densify |
| topUpTo 接 soft | **否** | 扩 soft densify |
| soft-latch / CostSoft / pity / soft-glance / firstsoft / cook·life-buy soft / xp-boost cue 再叠 | **否** | 链已齐 / 硬禁 |
| mapid 扩甲 / HopeMeter densify / TrMenu lore / 甲闸再扫 | **否** | 硬禁 |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12952 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12953 | DESIGN / tip power-paths 进度戳：**TIP-BB CLOSED live**；**TIP-BC OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12954 | 错位钉死：E1–E8 Open 无 ready 尾、拒线硬表（RESEARCH §1–2） | 可核 |
| D12955 | 「对齐」钉死：ready 尾；Open 零 `unlocked()`；零改 DELTA/硬表；零新 PAPI（DESIGN §2） | 可核 |
| D12956 | **插件：** `EmberGemSocket` Open/list · UnlockSoft.ready →「今日开孔门槛−1待用（刃二孔≥4·护符≥2）」 | 代码+单测 |
| D12957 | **插件：** insert 拒线 ready → soft 门槛串；已用 → 硬表 ≥5/≥3 | 可核 |
| D12958 | **插件：** 可选 UnlockSoft 旁短 CUE 常量；可选 soft-ok&&ready ActionBar 短标；禁永久 lore / TrMenu | 可核 |
| D12959 | **插件：** 单测最小包（DESIGN §2.3）+ Open 不 latch | PASS |
| D12960 | soft-latch / CostSoft / pity / Cap / firstsoft / cook·life-buy / xp-boost / TrMenu lore / 甲闸 / SetChase / Anvil / topUpTo：**本窗不改**；禁加 lore | lore≤3 |
| D12961 | NON 核：`git show` 无 DELTA/C_USED/sockUnlocked 硬表改；无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸/SetChase densify；无新 PAPI；无 soft-latch/CostSoft/pity/Cap/firstsoft densify；无 Anvil/Festival/LifeCap/topUpTo soft densify；无 xp-boost 再叠 | PASS |
| D12962 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；ConvertCap densify；CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠；soft-latch 假耗再叠；xp-boost cue 再叠；LifeCap/UndoWindow/FestivalEntry/Anvil densify；topUpTo 扩 soft；再降开孔门槛 densify | 文内硬句 |
| D12963 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12964 | 玩家口径：ready → 预告门槛−1；soft 过锁才消耗；已用无尾；DELTA/硬表不动 | 可核 |
| D12965–D12967 | 缓冲：错链/错戳/文案过长压行；禁借号抬 DELTA 或开 L13 施工或扩 stampable/SetChase/甲闸再扫/soft 假耗再叠/再降门槛；Anvil soft / enhance「+5开孔」硬文案另 tip | 超额新 tip |

### 玩家口径（固定）

- 今日还没软开过孔：宝石页标 **今日开孔门槛−1待用（刃二孔≥4·护符≥2）**；真正 soft 过锁才消耗。  
- 拒线在 ready 时报 soft 门槛（可附硬表）；已用报 ≥5/≥3。  
- 已用：不再显示待用尾。  
- DELTA 仍是 1；硬表 `sockUnlocked` 本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-gemhole-unlock-soft-cue-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-gemhole-unlock-soft-cue-align-2026-10-12.md` |
| SoT Soft | `CoreRpg/.../EmberGemHoleUnlockSoft.java` |
| SoT UI | `CoreRpg/.../EmberGemSocket.java` Open/insert |
| 硬表 | `CoreRpg/.../EmberItemData.sockUnlocked`（本窗只读） |
| 对照已齐 | TIP-BB xp-boost cue · D10785 UnlockSoft live · AU softTail 族（本窗离开） |
| 旁轨 HOLD | TIP-AI L13 FAIL · 甲闸勿再扫 · SetChase 六槽禁 · 扩 stampable 禁 · soft-latch/CostSoft/pity/Cap/firstsoft/xp-boost 再叠禁 · Anvil/topUpTo soft densify 禁 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 改 `DELTA` / `C_USED` / `sockUnlocked` 硬表；再降开孔门槛  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint **甲闸**  
- SetChase 六槽 densify；TrMenu 加 lore  
- soft-latch 假耗再叠；ConvertCap densify；CostSoft glance 再叠；pity nudge remain 再叠；soft-glance 工坊费用族再叠；firstsoft 再叠；cook soft / life-buy soft 再叠；xp-boost cue 再叠；LifeCap / UndoWindow / FestivalEntry / AnvilRepair densify；topUpTo 扩 soft  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [x] 批后插件：D12956–D12959；D12961 `git show` 干净  
- [x] 菜单默认不动；lore≤3  
- [x] Open 不吞 soft；仅 unlocked 过锁 consume；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [x] 总控批 **A · 方案 M** · 已批 · 已派插件  
- 选题：TIP-BB 后 · **离开 soft-latch / xp-boost cue** — GemHoleUnlockSoft 门槛预告/拒线诚实（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify · 非 soft 假耗再叠 · 非再降 DELTA**）  
- **批注：** GemHoleUnlockSoft 已 live，Open 不预告、拒线固定 ≥5/≥3 → 变强途径静默/撒谎；ready 尾「今日开孔门槛−1待用（刃二孔≥4·护符≥2）」；拒线跟 ready；零改 DELTA/硬表；零新 PAPI；批后派 **余烬-插件**；**L13 施工 HOLD**；Anvil soft / enhance「+5开孔」硬文案旁债；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 / soft-latch·CostSoft·pity·soft-glance·firstsoft·cook·life-buy soft 再叠 / xp-boost cue 再叠 / Anvil densify / topUpTo 扩 soft / 再降开孔门槛 仍冻  

## STATUS: CLOSED live `1.65.794-d12967` · tip D12952–D12967 · 2026-10-12 22:52 Asia/Shanghai · feat `b13c47c69`
