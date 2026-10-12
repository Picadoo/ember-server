# tip · TIP-AP 宝石通关保底加码 remain 诚实对齐（可施工薄 · 可感 UX）· D12744–D12759

> **状态：** APPROVED · 批 **A · 方案 M** · 已派 **余烬-插件** · 2026-10-12 21:00 Asia/Shanghai  
> **上游：** RESEARCH [`RESEARCH-ember-gem-pity-nudge-remain-align-2026-10-12.md`](../design/RESEARCH-ember-gem-pity-nudge-remain-align-2026-10-12.md) · DESIGN [`DESIGN-ember-gem-pity-nudge-remain-align-2026-10-12.md`](../design/DESIGN-ember-gem-pity-nudge-remain-align-2026-10-12.md) · TIP-AO CLOSED live `1.65.780-d12743` · TIP-E CLOSED live gem glance · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 总控 **可感 UX / 变强途径（非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify）** — **换轴离开追烙**；宝石日首加码 vs remain/hub 说谎  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberGemAcquire.remainLine` / `clearsLeft` / `hopeLine` + 单测）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify** · **零深化 densify · 零 sx20 · 零 skill_mult** · **零抬 STAMP/C12 · 零扩掉落 stampable · 零改 L11/L13 mods** · **零开 L13 施工 tip** · **零再扫甲闸** · **零 SetChase 六槽 densify · 零 TrMenu 加 lore · 零改 PITY_NEED/BONUS**

---

## 0. 一句话

日首宝石保底加码待用时，「再通关几次得石」少报虚次并明示加码；不加码表、不叠 twin。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **宝石通关保底加码 remain 诚实对齐** | **采纳** | 换轴离开追烙；SoT 改既有串；零功率表；**非 densify · 非甲闸 · 非扩 stampable · 非 SetChase 六槽** |
| SigFeel 非追烙 leftover | **否** | AO 后无新说谎证据 |
| mapid / 图鉴扩六槽 | **否** | SetChase densify 硬禁 |
| GearNextHint 甲闸 | **否** | 总控禁再扫 |
| TrMenu hub lore | **否** | 禁加 lore |
| HopeMeter 其它 densify / glance twin | **否** | TIP-J/T 停窗 |
| L13 施工 tip | **否** | TIP-AI FAIL；PASS≠开工；**HOLD** |
| 观察停窗 only | **否** | 有可感诚实债 |
| Affix / 伤乘 / 抬表 / sx20 / skill_mult | **否** | 硬禁 |

---

## 2. 方案 M（定稿荐）

| ID | 项 | 验收 |
|----|-----|------|
| D12744 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D12745 | DESIGN / tip power-paths 进度戳：**TIP-AO CLOSED live**；**TIP-AP OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D12746 | 错位钉死：E1–E5 加码真生效 vs remain/hub 多报（RESEARCH §1–2） | 可核 |
| D12747 | 「对齐」钉死：`ready && left>1 → left-1`；尾缀「今日加码待用」；零改 NEED/BONUS；零新 PAPI（DESIGN §2） | 可核 |
| D12748 | **插件：** `clearsLeft(cur, nudgeReady)` + 旧一元委托 | 代码+单测 |
| D12749 | **插件：** `remainLine(cur, nudgeReady)` / `remainLine(pd)` 喂 `GemPityNudge.ready` | 可核 |
| D12750 | **插件：** `hopeLine(pd)` 与 remain 同有效口径 | 可核 |
| D12751 | **插件：** 单测最小包（DESIGN §2.3）+ D1258 一元回归 | PASS |
| D12752 | RunPapi / HopeMeter / MapIdentity / GemSocket / TrMenu / 甲闸 / SetChase / haveNeed：**本窗不改吃 SoT**；禁加 lore | lore≤3 |
| D12753 | NON 核：`git show` 无 PITY_NEED/BONUS 常量改；无 STAMP/C12/loot/`DEFS` mods/L11/L13/Affix/skill_mult；无甲闸/SetChase densify；无新 PAPI | PASS |
| D12754 | HOLD：L13 施工；刃 L11/L13 mods；扩 stampable；甲闸再扫；glance twin/深化 densify；ForgeChoice densify；SetChase 六槽 densify | 文内硬句 |
| D12755 | 批后派单句：**余烬-插件**；策划 docs-only | 派单句 |
| D12756 | 玩家口径：加码待用 → 少报虚次 +「今日加码待用」；已用/满档/价/掉落/NEED 不变 | 可核 |
| D12757–D12759 | 缓冲：错链/错戳/文案过长压行；禁借号抬表或开 L13 施工或扩 stampable/SetChase 六槽 | 超额新 tip |

### 玩家口径（固定）

- 今日宝石通关保底加码还没用：枢纽和聊天会写 **今日加码待用**，且「再通关几次」按下次会多计一次来算，不再多报。  
- 今日加码已用或保底已满：跟以前一样。  
- 保底仍要满 5、加码仍是 +1；石头战力、合成价、掉落本窗不动。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-gem-pity-nudge-remain-align-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-gem-pity-nudge-remain-align-2026-10-12.md` |
| SoT | `CoreRpg/.../EmberGemAcquire.java` `remainLine` · `clearsLeft` · `hopeLine` |
| 复用 | `EmberGemPityNudge.ready` / `BONUS`（D10673） |
| 吃 SoT（本窗不改） | `EmberRunPapi` `gem_pity_hope` · `EmberHopeMeter.priorityTracks` · `EmberMapIdentity.methodLine` · `EmberGemSocket` |
| 单测 | `EmberGemAcquireRemainTest`（扩）· 可选 `EmberGemPityNudgeTest` |
| 已齐对照 | TIP-E gem glance · TIP-AO 追烙链闭合 · TIP-AM 甲闸链闭合 |
| 旁轨 HOLD | TIP-AI L13 FAIL · 甲闸勿再扫 · SetChase 六槽禁 · 扩 stampable 禁 |

---

## 4. 明确不做

- 任何功率表 / STAMP / C12 / loot / L11/L13 `DEFS` mods / Affix / skill_mult / **新** glance twin / sx20 / 深化 densify / ForgeChoice densify  
- 改 `PITY_NEED` / `BONUS` / `onClear` 结算序  
- 扩 stampable 甲；开 L13 施工 tip（**HOLD**）  
- 再扫 ForgeHeldNext / CommitNext / SinkGlance / GearNextHint 甲闸  
- SetChase 六槽 densify；mapid haveNeed 扩甲；TrMenu 加 lore  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 批后插件：D12748–D12751；D12753 `git show` 干净  
- [ ] 菜单默认不动；lore≤3  
- [ ] 插件 jar live 后策划 docs close  
- [ ] **L13 施工仍 HOLD**  

---

## 6. 批注（荐）

- [ ] 总控批 **A · 方案 M** · 荐批  
- 选题：TIP-AO 后 · **换轴可感 UX** — 宝石通关保底加码 remain 诚实（**非 densify · 非再扫甲闸 · 非扩 stampable · 非 SetChase 六槽 densify**）  
- **批注：** AN→AO 追烙链已齐；`GemPityNudge` 日首 +1 live 但 remain/hub 仍按裸 cur/5 多报「再通关 n 次」；`ready && left>1 → left-1` + 尾缀「今日加码待用」；零改 NEED/BONUS；零新 PAPI；批后派 **余烬-插件**；**L13 施工 HOLD**；深化 densify / Affix / 抬表 / glance twin / sx20 / skill_mult / STAMP / L11·L13 mods / 扩 stampable / 甲闸再扫 / SetChase 六槽 仍冻  

## STATUS: OPEN · TIP-AP · 2026-10-12
