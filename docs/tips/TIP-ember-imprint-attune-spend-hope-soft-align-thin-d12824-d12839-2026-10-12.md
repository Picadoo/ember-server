# tip · TIP-AU 烙印/调律花费 Hope 诚实对齐（可施工薄 · 可感 UX）· D12824–D12839

> **状态：** **OPEN** · 待总控批 A·M · 2026-10-12 21:38 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-imprint-attune-spend-hope-soft-align-2026-10-12.md`](../design/RESEARCH-ember-imprint-attune-spend-hope-soft-align-2026-10-12.md) · DESIGN [`DESIGN-ember-imprint-attune-spend-hope-soft-align-2026-10-12.md`](../design/DESIGN-ember-imprint-attune-spend-hope-soft-align-2026-10-12.md) · TIP-AT CLOSED live `1.65.785-d12823` · feat `5d29316a6` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **可感 UX / 变强途径** — **离开 CostSoft / pity-remain 族**（AS+AT / AP–AR 已齐）；HopeMeter 烙印/调律花费 leftover  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `GrowthService` imprint_hope/attune_hope/impPapi cost + 签名页门槛文案 · `EmberSigChase` remain/readyCue soft need · `autoAttuneUnlock` soft 扣费 · 复用 Imprint*Soft/AttuneUnlockSoft/`GearNextHint.softTail` + 单测）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/经济表/日闩 · 零再叠 CostSoft glance · 零 pity-remain 再叠 · 零 LOCK LockShard soft 本窗**

---

## 0. 一句话

烙印/调律日首减负待用时，签名页花费希望、烙印页花费行、追烙就绪口按半门槛并明示减负，「够烙/够解锁」跟真付费一致；不改减负表、不叠 twin。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **烙印/调律花费 Hope + 烙印页 cost 诚实对齐** | **采纳** | 离开 CostSoft/pity；HopeMeter other + SigFeel 花费 leftover；SoT 改既有串；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| LOCK LockShard soft glance | **否** | 仍费用 soft 族；旁债 |
| mapid / 图鉴扩甲 | **否** | 扩 stampable / SetChase 邻域 |
| CostSoft / pity 再扫 | **否** | AS+AT / AP–AR 已齐 |
| 甲闸再扫 / SetChase 六槽 | **否** | 硬禁 |
| TrMenu hub lore | **否** | 禁加 lore |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| 观察停窗 only | **否** | 有可感诚实债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12824 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12825 | DESIGN / tip power-paths 进度戳：**TIP-AT CLOSED live**；**TIP-AU OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12826 | 错位钉死：E1–E10 付费+check 已 soft vs hope/cost/remain/readyCue/autoAttune 全额（RESEARCH §1–2） | 可核 |
| D12827 | 「对齐」钉死：imprint/attune soft peek；cost 同 needM/needC；readyCue soft need；autoAttune soft 扣；`SOFT_TAIL`；零改 MULT/表价/STAMP；零新 PAPI（DESIGN §2） | 可核 |
| D12828 | **插件：** `sigPapi` imprint_hope / attune_hope soft peek + softTail | 代码+单测 |
| D12829 | **插件：** `impPapi cost` 用 needM/needC + softTail；签名页烙印/调律按钮门槛 soft | 可核 |
| D12830 | **插件：** SigChase remain/progress/readyCue/tip 展示 soft need（有 pd）；pickUnderMarkForMap 可仍全额 | 可核 |
| D12831 | **插件：** `autoAttuneUnlock` `AttuneUnlockSoft.need` consume 扣费 | 可核 |
| D12832 | **插件：** 单测最小包（DESIGN §2.3）+ AT/AO/AK 回归 | PASS |
| D12833 | CostSoft glance / pity remain / TrMenu lore / 甲闸 / SetChase 六槽 / LOCK soft：**本窗不改**；禁加 lore | lore≤3 |
| D12834 | NON 核：`git show` 无 MULT/经济表/日闩/IMPRINT_MARKS/ALT_MARKS/币表/STAMP 改；无 C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸/SetChase densify；无新 PAPI；无 CostSoft/pity 再叠；无 LOCK soft | PASS |
| D12835 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；CostSoft glance 再叠；pity-remain 再叠；LOCK LockShard soft 本窗 | 文内硬句 |
| D12836 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12837 | 玩家口径：减负待用 → 半门槛 +「今日减负待用」+ 够烙按半；已用/表价/MULT/STAMP 不变 | 可核 |
| D12838–D12839 | 缓冲：错链/错戳/文案过长压行；禁借号抬表或开 L13 施工或扩 stampable/SetChase 六槽/甲闸再扫/CostSoft·pity 再叠/LOCK soft | 超额新 tip |

### 玩家口径（固定）

- 今日烙印减负还没用：希望/花费行按 **半徽记+半币**（各 soft 独立）并标 **今日减负待用**；够烙按半门槛。  
- 今日调律解锁减负还没用：希望/按钮按 **半徽记** 并标减负。  
- 减负已用：跟以前全额一样。  
- 减负仍是 ×0.5、一日一次；徽记/币/STAMP 表本体本窗不动。  
- 追烙就绪：徽记达今日可烙门槛即 cue（与真付费同）。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-imprint-attune-spend-hope-soft-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-imprint-attune-spend-hope-soft-align-2026-10-12.md` |
| SoT Hope | `CoreRpg/.../EmberHopeMeter.java` imprintSpend / attuneUnlock（调用方喂 soft 数） |
| SoT 接线 | `CoreRpg/.../EmberGrowthService.java` sigPapi / impPapi / 签名页文案 / autoAttuneUnlock |
| SoT 追烙展示 | `CoreRpg/.../EmberSigChase.java` remain/progress/readyCue |
| 复用 | `EmberImprintCoinSoft` · `EmberImprintMarkSoft` · `EmberAttuneUnlockSoft` · `EmberGearNextHint.softTail` |
| 已齐对照 | GrowthService 付费+check 已 soft · TIP-AT CostSoft glance · TIP-AO chase 目标件 · TIP-AH attune 预览 |
| 旁轨 HOLD | TIP-AI L13 FAIL · 甲闸勿再扫 · SetChase 六槽禁 · 扩 stampable 禁 · CostSoft/pity 再叠禁 · LOCK LockShard soft 旁债 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 改 `MULT` / `C_USED` / `IMPRINT_MARKS` / `ALT_MARKS` / 币表本体 / 手点付费序（已诚实）  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint **甲闸**  
- SetChase 六槽 densify；TrMenu 加 lore  
- CostSoft glance 再叠；pity nudge remain 再叠；LOCK LockShard soft 本窗  

---

## 5. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 批后插件：D12828–D12832；D12834 `git show` 干净  
- [ ] 菜单默认不动；lore≤3  
- [ ] soft 已用全额回归；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AT 后 · **离开 CostSoft/pity** — 烙印/调律花费 Hope 诚实（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify**）  
- **批注：** AS+AT CostSoft glance 已齐；ImprintCoin/Mark/AttuneUnlock Soft 付费+check 已 live，但 imprint_hope/attune_hope/impPapi cost/签名页门槛/SigChase remain·readyCue/autoAttune 仍全额 → 「币差/徽记不够」假负、就绪晚；soft peek +「今日减负待用」+ readyCue/autoAttune 同 peek；零改 MULT/表价/STAMP；零新 PAPI；批后派 **余烬-插件**；**L13 施工 HOLD**；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 / CostSoft 再叠 / pity-remain 再叠 / LOCK soft 仍冻  

## STATUS: OPEN · TIP-AU · D12824–D12839 · 2026-10-12 21:38 CST
