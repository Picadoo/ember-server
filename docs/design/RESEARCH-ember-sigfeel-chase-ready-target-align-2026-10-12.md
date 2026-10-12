# RESEARCH · SigFeel / Hope / remain 追烙就绪目标件对齐 · 2026-10-12

> **状态：** OPEN · TIP-AO · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AN CLOSED live 后追烙 **ConfirmTokens ready cue / held-ready** 已认防具侧目标件；总控下一窗继续 **可感 UX / 变强途径（非 densify · 非再扫甲闸 · 非扩 stampable）**。扫 hub / 希望 / remain 摘要：`EmberHopeMeter.sigChase` · `EmberSigFeel.menuLine` · `EmberSigChase.remainLine` 徽记满时仍只写「满可烙印 / 徽记 n/n / 可烙」，**不写实际目标槽** → 与 AN 按钮「烙到穿着头盔」**摘要说谎**。  
> **上游：** TIP-AN CLOSED live `1.65.779-d12727` · TIP-AJ CLOSED live `1.65.775-d12663` · TIP-AH CLOSED live `1.65.774-d12631` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）· power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md)  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI / 新 remain 族）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩掉落 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽扩容 densify · 零 TrMenu 加 lore**

---

## 0. 一句话

徽记够时，枢纽手感行 / 希望追烙轨 / remain 摘要必须跟 AN 同一目标件槽名；今天按钮诚实、摘要仍糊。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | TIP-AN：`readyCueSlotWord` / `imprintWornTarget` / `sameSlotPiece` live | `EmberSigChase` · `1.65.779-d12727` | **按钮半行**已认甲 |
| E2 | TIP-AJ：`imprint_hope` 阶走 `imprintWornTarget` | live `1.65.775-d12663` | 花费摘要已诚实 |
| E3 | `HopeMeter.sigChase(def, marks)` 满档：`追烙就绪 n/n · … · 满可烙印` **无槽名** | `EmberHopeMeter` ~105–114 | hub 希望轨 / `progressLine` **糊** |
| E4 | `SigFeel.menuLine` 追烙枝：恒 `追烙 id 名 · 徽记 m/need`；满档也不写「可烙头/符」 | `EmberSigFeel` ~107–111 | **SigFeel leftover** |
| E5 | `remainLine(def, marks)` 满：`徽记够 · 可烙` **无槽**；MapFarmCue `[追烙]` tooltip 吃此串 | `EmberSigChase` ~103–109 · `EmberMapFarmCue` ~338 | 图装下一环旁注仍糊 |
| E6 | `maybeReadyCue` 已用 `readyCueSlotWord`，但同瞬 `progressLine`/`remainLine` 仍无 lo | `EmberSigChase` ~367–379 | **同瞬两口径** |
| E7 | `GrowthService.sigFeelLine` **有 lo**，却未传入 menuLine | Growth ~1077–1083 | 接线成本低 |
| E8 | 甲闸 AK→AL→AM 已齐；GearNextHint 全局甲闸 / SetChase glance twin / 扩 stampable **禁** | TIP-AM/AN RESEARCH | **本债=摘要对齐，非甲闸/非 densify** |
| E9 | TIP-J/T：Hope/SetChase/gem **glance twin** 禁；本债是 **改既有显示串** 跟 AN SoT，不新开 PAPI | power-paths | 正交可感 |

---

## 2. 错位场景（玩家面）

| 场景 | AN ready 钮 | 今日 hub 手感 / 希望轨 / remain | 体感 |
|------|-------------|--------------------------------|------|
| 追 L02 · 穿同族头盔 · 徽记满 | 「烙到穿着头盔」 | 「满可烙印」/「徽记 5/5」/「可烙」 | **摘要假糊** |
| 追 L02 · 徽记满 · 无护符无甲 | 回落 def.slot「护符」或穿着件 | 仍写「可烙」像已有目标 | **缺件不说** |
| 追 L01 · 穿刃 · 徽记满 | 烙刃 | 摘要无槽也可接受；对齐后写「可烙刃」 | OK / 更清 |
| 徽记未满 | （无 ready） | 进度 m/need | **不变** |

---

## 3. 「对齐」定义（钉死）

1. **复用** AN：`EmberSigChase.readyCueSlotWord(lo, def)` / `imprintWornTarget(lo, def)`；**禁**第二套 prio。  
2. `remainLine` / `HopeMeter.sigChase` / `SigFeel.menuLine`：仅在 **marks ≥ imprintMarks** 时追加目标信息。  
3. 有 lo 且 `imprintWornTarget` 非空 → `可烙{slotName(target)}`（与 ready 钮同词）。  
4. 有 lo 且目标空 → `缺可烙穿着件`（禁假装「可烙护符」当身上无可烙件）。  
5. lo==null（纯测 / 无负载）→ 保持旧满档串 **或** 仅用 `slotName(def.slot)` 回落；**禁**发明甲槽。  
6. 徽记未满文案 **零改**；徽记数 / imprint 价 / STAMP / stampable / DEFS mods **零改**。  
7. **不**新 PAPI / glance 族 / remain 族；**不** TrMenu 加 lore；**不**再扫甲闸；**不**扩 SetChase 图鉴为六槽。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫 / GearNextHint 全局甲闸  
- Hope/SetChase/gem **新** glance twin；ForgeChoice densify  
- SetChase focusCodex 扩四甲；签名四甲格 densify / 甲待命半行  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify  

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **SigFeel / Hope / remain 追烙就绪目标件对齐（可施工薄）** | **采纳** — AN 后硬 leftover；改既有串；零功率表；**非 densify · 非甲闸 · 非扩 stampable** |
| GearNextHint 全局甲闸 | **否** — 总控禁再扫甲闸 |
| SetChase 六槽 / 图鉴扩甲 | **否** — 内容扩容 · 易 densify |
| Hope/SetChase/gem glance 再 twin | **否** — TIP-J/T 停窗 |
| 扩 stampable 甲 | **否** — 硬禁 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |
| TrMenu hub lore densify | **否** — 禁加 lore |
| 观察停窗 only | **否** — 有可感诚实债 |
