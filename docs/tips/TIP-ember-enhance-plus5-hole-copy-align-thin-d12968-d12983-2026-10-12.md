# tip · TIP-BD 强化「+5开孔」硬文案诚实对齐（可施工薄 · 可感 UX）· D12968–D12983

> **状态：** **OPEN** · 荐批 **A · 方案 M** · 2026-10-12 22:55 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-enhance-plus5-hole-copy-align-2026-10-12.md`](../design/RESEARCH-ember-enhance-plus5-hole-copy-align-2026-10-12.md) · DESIGN [`DESIGN-ember-enhance-plus5-hole-copy-align-2026-10-12.md`](../design/DESIGN-ember-enhance-plus5-hole-copy-align-2026-10-12.md) · TIP-BC CLOSED live `1.65.794-d12967` · feat `b13c47c69` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控显式旁债 **enhance「+5开孔」硬文案** — soft 门槛已 live、强化预览/Hope/菜单仍硬唯一（**诚实 ≠ densify**）  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberForgeService.enhance` 预览 ready 开孔尾 · 可选 Hope enhance 同构 · GemSocket 锁孔→强化按钮副标 soft/硬 · 可选 legacy `EnhanceService` info 附注 · 单测；零改 DELTA/C_USED/sockUnlocked/unlock_at）；**余烬-物品**（TrMenu `ember_p1_gem.yml` / `ember_socket.yml` 门槛句双写替换 · **禁**加 lore 行）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零改 DELTA/C_USED/sockUnlocked/unlock_at · 零 ConvertCap densify · 零 CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠 · 零 soft-latch 假耗再叠 · 零 xp-boost cue 再叠 · 零 GemHole Open/拒线再叠 · 零 LifeCap/UndoWindow/FestivalEntry/AnvilRepair densify · 零 topUpTo 扩 soft densify · 零再降开孔门槛 densify**

---

## 0. 一句话

强化预览/菜单写开孔时同时说清表额与日首−1；预览不消耗 soft。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **强化「+5开孔」硬文案诚实对齐** | **采纳** | BC 旁债；菜单/预览硬唯一；零改 DELTA/硬表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase · 非 soft 假耗/Open 再叠** |
| 再降 DELTA / 改 unlock_at | **否** | densify |
| AnvilRepair Soft 假耗 | **否** | 硬禁 densify |
| topUpTo 接 soft | **否** | 扩 soft densify |
| GemHole Open/拒线再叠 | **否** | BC 已齐 |
| soft-latch / CostSoft / pity / soft-glance / firstsoft / cook·life-buy soft / xp-boost cue 再叠 | **否** | 链已齐 / 硬禁 |
| mapid 扩甲 / HopeMeter densify / 甲闸再扫 | **否** | 硬禁 |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12968 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12969 | DESIGN / tip power-paths 进度戳：**TIP-BC CLOSED live**；**TIP-BD OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12970 | 错位钉死：E1–E9 预览/菜单硬唯一（RESEARCH §1–2） | 可核 |
| D12971 | 「对齐」钉死：预览 ready 尾；零 `unlocked()`；零改 DELTA/硬表/unlock_at；零新 PAPI；TrMenu 双写替换（DESIGN §2） | 可核 |
| D12972 | **插件：** `EmberForgeService.enhance` 预览 · ready∩锁孔 → UnlockSoft.CUE/等价 | 代码+单测 |
| D12973 | **插件：** 可选 Hope enhance 轨同构 soft 尾；零改保底数字 | 可核 |
| D12974 | **插件：** GemSocket 锁孔→强化按钮副标 ready→soft 档 / used→表额 | 可核 |
| D12975 | **插件：** 可选 legacy `EnhanceService` info 锁孔行 ready 附注；禁改 unlock_at | 可核 |
| D12976 | **插件：** 单测最小包（DESIGN §2.3）+ 预览不 latch | PASS |
| D12977 | **物品：** `ember_p1_gem.yml` / `ember_socket.yml` 门槛句 → 表额+日首−1 双写；**禁**加 lore 行 | lore≤3 · diff 仅替换 |
| D12978 | soft-latch / CostSoft / pity / Cap / firstsoft / cook·life-buy / xp-boost / GemHole Open·拒线 / 甲闸 / SetChase / Anvil / topUpTo：**本窗不改** | 可核 |
| D12979 | NON 核：`git show` 无 DELTA/C_USED/sockUnlocked/unlock_at 改；无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸/SetChase densify；无新 PAPI；无 soft-latch/CostSoft/pity/Cap/firstsoft densify；无 Anvil/Festival/LifeCap/topUpTo soft densify；无 GemHole Open/拒线再叠；无 xp-boost 再叠 | PASS |
| D12980 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；ConvertCap densify；CostSoft·pity·soft-glance·firstsoft·cook soft·life-buy soft 再叠；soft-latch 假耗再叠；xp-boost cue 再叠；GemHole Open/拒线再叠；LifeCap/UndoWindow/FestivalEntry/Anvil densify；topUpTo 扩 soft；再降开孔门槛 densify | 文内硬句 |
| D12981 | 批后派单句：**余烬-插件** + **余烬-物品**（TrMenu）；策划 docs-only | 派单句 |
| D12982 | 玩家口径：ready∩锁孔 → 预览/菜单承认日首−1；表额仍可见；soft 过锁才消耗；DELTA/硬表不动 | 可核 |
| D12983 | 缓冲：错链/错戳/文案过长压行；禁借号抬/降 DELTA 或改 unlock_at 或开 L13 施工或扩 stampable/SetChase/甲闸再扫/soft 假耗再叠/再降门槛；Anvil soft 另 tip（仍冻 densify） | 超额新 tip |

### 玩家口径（固定）

- 日首 soft 仍待用且手持有锁孔：强化预览标 **今日开孔门槛−1待用（刃二孔≥4·护符≥2）**；真正 soft 过锁才消耗。  
- 菜单写 **表额≥5/≥3** 与 **日首−1≥4/≥2**；已用后仍知硬门槛。  
- DELTA 仍是 1；硬表 / unlock_at 本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-enhance-plus5-hole-copy-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-enhance-plus5-hole-copy-align-2026-10-12.md` |
| Soft SoT | `CoreRpg/.../EmberGemHoleUnlockSoft.java`（本窗只读 ready/CUE） |
| 预览 | `CoreRpg/.../EmberForgeService.java` enhance go=false |
| Hope | `CoreRpg/.../EmberHopeMeter.java` enhance 轨（可选） |
| 按钮 | `CoreRpg/.../EmberGemSocket.java` 锁孔→强化 ConfirmTokens 副标 |
| 硬表 | `EmberItemData.sockUnlocked` · `enhance.yml unlock_at`（本窗只读） |
| 菜单 | `plugins/TrMenu/menus/ember_p1_gem.yml` · `ember_socket.yml` |
| Legacy | `EnhanceService` info（可选） |
| 对照已齐 | TIP-BC GemSocket Open/拒线 · UnlockSoft D10785 |
| 旁轨 HOLD | TIP-AI L13 FAIL · 甲闸勿再扫 · SetChase 六槽禁 · 扩 stampable 禁 · soft-latch/CostSoft/pity/Cap/firstsoft/xp-boost/Open 再叠禁 · Anvil/topUpTo soft densify 禁 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 改 `DELTA` / `C_USED` / `sockUnlocked` / `unlock_at`；再降开孔门槛  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint **甲闸**  
- SetChase 六槽 densify；TrMenu **加** lore 行（只允许替换门槛句）  
- soft-latch 假耗再叠；ConvertCap densify；CostSoft glance 再叠；pity nudge remain 再叠；soft-glance 工坊费用族再叠；firstsoft 再叠；cook soft / life-buy soft 再叠；xp-boost cue 再叠；GemHole Open/拒线再叠；LifeCap / UndoWindow / FestivalEntry / AnvilRepair densify；topUpTo 扩 soft  

---

## 5. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 批后插件：D12972–D12976；物品 D12977；D12979 `git show` 干净  
- [ ] TrMenu 仅替换门槛句；lore 行数不增  
- [ ] 预览不吞 soft；仅 unlocked 过锁 consume；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-BC 后 · **enhance「+5开孔」硬文案诚实**（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify · 非 soft 假耗再叠 · 非再降 DELTA · 非 Open/拒线再叠**）  
- **批注：** soft 已 live（刃≥4/护符≥2），强化预览静默、TrMenu/legacy 硬写 ≥5/≥3 → 变强途径撒谎；预览 ready∩锁孔 → CUE；菜单双写表额+日首−1；零改 DELTA/硬表/unlock_at；零新 PAPI；批后派 **余烬-插件** + **余烬-物品**；**L13 施工 HOLD**；Anvil soft densify 旁债仍冻；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 / soft-latch·CostSoft·pity·soft-glance·firstsoft·cook·life-buy soft 再叠 / xp-boost cue 再叠 / GemHole Open 再叠 / Anvil densify / topUpTo 扩 soft / 再降开孔门槛 仍冻  

## STATUS: OPEN · tip D12968–D12983 · 2026-10-12 22:55 Asia/Shanghai · 荐批 A·M
