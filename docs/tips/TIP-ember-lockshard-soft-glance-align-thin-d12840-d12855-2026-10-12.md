# tip · TIP-AV LOCK LockShard soft 预览/glance 诚实对齐（可施工薄 · 可感 UX）· D12840–D12855

> **状态：** **CLOSED** · live `1.65.787-d12855` · feat `901632f09` · 2026-10-12 21:56 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-lockshard-soft-glance-align-2026-10-12.md`](../design/RESEARCH-ember-lockshard-soft-glance-align-2026-10-12.md) · DESIGN [`DESIGN-ember-lockshard-soft-glance-align-2026-10-12.md`](../design/DESIGN-ember-lockshard-soft-glance-align-2026-10-12.md) · TIP-AU CLOSED live `1.65.786-d12839` · feat `53a9fe3b8` · TIP-AT CLOSED live `1.65.785-d12823` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **显式旁债** — LOCK LockShard soft glance（AS/AT 曾误写「无 soft」；**RerollLockShardSoft 已 live**，对齐 peek）  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberForgeHeldNext` lockRerollCost soft · LOCK canPay/softTail · `EmberSinkGlance` LOCK soft lack · GrowthService 菜单 lock/邀请 ls soft · 复用 `RerollLockShardSoft`/`RerollCoinSoft`/`SOFT_TAIL` + 单测）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 MULT/经济表/日闩 · 零再叠 AS/AT densify · 零 pity-remain 再叠 · 零发明第二套 LockShard soft**

---

## 0. 一句话

日首锁定洗练锁碎减负待用时，手持下一确定 / Sink 缺料 / 菜单锁定邀请按半锁碎并明示减负，「料够」跟真付费一致；不改减负表、不发明 soft、不叠 twin。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **LOCK LockShard soft glance 诚实对齐** | **采纳** | 总控显式 AU 旁债；soft **已 live**；同构 AS/AT peek；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| 「真无 soft」换轴他债 | **否** | RESEARCH E1–E6：付费已 soft、glance 仍全额 |
| 发明新 Lock CostSoft | **否** | 复用 RerollLockShardSoft |
| AS/AT / pity / Hope 再扫 | **否** | 链已齐 |
| 甲闸再扫 / SetChase 六槽 | **否** | 硬禁 |
| TrMenu hub lore | **否** | 禁加 lore |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| 观察停窗 only | **否** | 总控点名旁债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12840 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12841 | DESIGN / tip power-paths 进度戳：**TIP-AU CLOSED live**；**TIP-AV OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12842 | 错位钉死：E1–E10 soft live + 付费预览已 soft vs HeldNext/Sink/菜单全额（RESEARCH §1–2）；AT「无 soft」误判 | 可核 |
| D12843 | 「对齐」钉死：lockRerollCost(tier,pd) soft lock_shard（+ coin）；canPay 同 peek；`SOFT_TAIL`；零改 MULT；零新 PAPI；禁发明第二 soft（DESIGN §2） | 可核 |
| D12844 | **插件：** `ForgeHeldNext.lockRerollCost(tier, pd)` soft peek；旧一元 ≡ 全额 | 代码+单测 |
| D12845 | **插件：** `ForgeHeldNext.line` LOCK soft canPay/shortfall + softTail；甲早退不动 | 可核 |
| D12846 | **插件：** `SinkGlance` goal LOCK soft lack | 可核 |
| D12847 | **插件：** GrowthService 菜单 `"lock"` + 非锁邀请按钮 ls soft peek（付费预览不改） | 可核 |
| D12848 | **插件：** 单测最小包（DESIGN §2.3）+ AT/AS/AK 回归 | PASS |
| D12849 | AS/AT 他轨 / pity / Hope / TrMenu lore / 甲闸 / SetChase：**本窗不改**；禁加 lore | lore≤3 |
| D12850 | NON 核：`git show` 无 MULT/经济表/日闩/C11 改；无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸/SetChase densify；无新 PAPI；无发明第二 soft；无 AS/AT densify 再叠 | PASS |
| D12851 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽；发明第二 LockShard soft；AS/AT densify 再叠；pity-remain 再叠 | 文内硬句 |
| D12852 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12853 | 玩家口径：减负待用 → 半锁碎（+半币若币 soft）+「今日减负待用」+ 料够按半；已用/甲早退/表价/MULT 不变 | 可核 |
| D12854–D12855 | 缓冲：错链/错戳/文案过长压行；禁借号抬表或开 L13 施工或扩 stampable/SetChase 六槽/甲闸再扫/发明 soft/AS·AT densify | 超额新 tip |

### 玩家口径（固定）

- 今日锁定洗练锁碎减负还没用：手持下一确定、Sink 缺料、菜单锁定邀请按 **半锁碎**（币位若日首洗练币减负待用则半币）并标 **今日减负待用**；料够按半锁碎算。  
- 今日减负已用：跟以前全额一样。  
- 减负仍是 ×0.5、一日一次；C11 锁碎/币表本体本窗不动。  
- 护甲：仍早退「护甲随护符」。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-lockshard-soft-glance-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-lockshard-soft-glance-align-2026-10-12.md` |
| SoT soft | `CoreRpg/.../EmberRerollLockShardSoft.java`（+ `EmberRerollCoinSoft` 币位） |
| SoT 手持 | `CoreRpg/.../EmberForgeHeldNext.java` lockRerollCost / LOCK 分支 |
| SoT Sink | `CoreRpg/.../EmberSinkGlance.java` goal LOCK |
| SoT 菜单 | `CoreRpg/.../EmberGrowthService.java` `"lock"` / 邀请按钮（付费预览已 soft） |
| 复用 | `EmberGearNextHint.SOFT_TAIL` / softTail |
| 已齐对照 | GrowthService 锁洗付费+预览已 soft · TIP-AS/AT CostSoft glance · TIP-AU Hope 花费 |
| 旁轨 HOLD | TIP-AI L13 FAIL · 甲闸勿再扫 · SetChase 六槽禁 · 扩 stampable 禁 · AS/AT densify 勿再叠 · pity-remain 再叠禁 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 改 `MULT` / `C_USED` / C11 表价 / 锁洗付费序（已诚实）  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint **甲闸**  
- SetChase 六槽 densify；TrMenu 加 lore  
- 发明第二套 LockShard soft；AS/AT densify 再叠；pity nudge remain 再叠  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [x] 批后插件：D12844–D12848；D12850 `git show` 干净  
- [x] 菜单默认不动；lore≤3  
- [x] 甲早退回归；soft 已用全额回归；live `1.65.787-d12855` · feat `901632f09`；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AU 后 · **总控显式旁债** — LOCK LockShard soft glance 诚实（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify**）  
- **批注：** AS/AT 曾写 LOCK 无 soft（误）；`RerollLockShardSoft` D9937 已 live，GrowthService 锁洗付费+预览已 soft，但 HeldNext/Sink/菜单 lock 邀请仍全额 → 「还差碎」假负、另加多报；soft peek +「今日减负待用」+ canPay 同 peek；零改 MULT/C11；零新 PAPI；**不发明 soft**；甲早退不动；批后派 **余烬-插件**；**L13 施工 HOLD**；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 / AS·AT densify 再叠 / pity-remain 再叠 仍冻  

## STATUS: CLOSED · live 1.65.787-d12855 · 901632f09 · 2026-10-12 21:56 CST
