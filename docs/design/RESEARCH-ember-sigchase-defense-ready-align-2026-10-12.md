# RESEARCH · SigChase 追烙就绪防具侧对齐 · 2026-10-12

> **状态：** OPEN · TIP-AN · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AM CLOSED live 后甲闸诚实链 **AK→AL→AM 已齐**；总控换轴 **可感 UX / 变强途径（非 densify · 非再扫甲闸）**。扫 AE+AJ 后签名烙印轨：战斗/预览/`imprint_hope` 已走 `fitsDefense` / `imprintWornTarget`，但 **追烙就绪 / 手持同部位提示**（`EmberSigChase.sameSlotPiece` · `maybeHeldReadyTip` · `maybeReadyCue` 按钮槽名）仍 **严格 `def.slot` + 只扫刃/护符** → 追 L02 类 charm-def、穿着/手持防具可烙时 **漏显或说「护符」**。  
> **上游：** TIP-AM CLOSED live `1.65.778-d12711` · TIP-AJ CLOSED live `1.65.775-d12663` · TIP-AH CLOSED live `1.65.774-d12631` · TIP-AE CLOSED live `1.65.773-d12599` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）· D3937 held-ready · D464/D470 ready cue · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md)  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin · 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice·wear·pity·awkDelta densify · 零抬 STAMP · 零扩掉落 stampable · 零改 L11/L13 · 零开 L13 施工 tip · 零再扫 ForgeHeldNext/CommitNext/SinkGlance 甲闸 · 零 Hope/SetChase/gem glance twin**

---

## 0. 一句话

追烙就绪提示与「烙到哪件」必须跟现网烙印目标（刃 / 防具侧 5 取 1）同槽；今天仍按旧 charm=护符严格槽，甲可烙时漏或说谎。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | TIP-AE：`fitsDefense` — charm-def → charm\|头\|胸\|腿\|靴；`imprintWornTarget` 同序 | `EmberSignature` · `EmberGrowthService` | **真规则**防具侧可烙 |
| E2 | TIP-AJ：`imprint_hope` / 签名页 C 已跟 `imprintWornTarget` | live `1.65.775-d12663` | 花费/摘要已诚实 |
| E3 | TIP-AH：`runPreview*` 六槽对齐 | live `1.65.774-d12631` | 进本预览已诚实 |
| E4 | `EmberSigChase.sameSlotPiece`：手持用 `def.slot.equals`；穿着只取 `lo.charm` / `lo.blade` | `EmberSigChase` ~426–450 | **不认甲槽 · 不认 fitsDefense** |
| E5 | `maybeHeldReadyTip`：`worn` 只比 charm/blade uid | 同文件 ~408–410 | 甲穿着永不「穿着」 |
| E6 | `maybeReadyCue` ConfirmTokens 文案：`slotName(def.slot)` → charm-def 恒「护符」 | 同文件 ~376–378 | **按钮槽名假**（真目标可能是头盔） |
| E7 | 烙印页 `pickTarget`：held 用 `fitsDefense`；worn 用 `imprintWornTarget` | `EmberGrowthService` ~1833–1839 | **页内已对齐；追烙 cue 未跟** |
| E8 | 单测 `EmberSigHeldReadyTipTest.sameSlotWornPreferred` 钉死 L02+无护符 → null（无甲用例） | test | 旧契约未覆盖防具侧 |
| E9 | TIP-J/T：Hope/SetChase/**gem** glance twin **禁**；本债是 **SigChase 就绪诚实** 非 glance densify | power-paths | 正交可感 |
| E10 | 甲闸链 AK→AL→AM **已闭合**；GearNextHint 全局甲闸仍面广勿抢 | TIP-AM RESEARCH | **换轴**；勿再扫甲闸 |
| E11 | stampable 甲仍空（AE 硬禁扩掉落） | `fitsStamp` / stampable | **本窗不扩 stampable**；只修 imprint 就绪 UX |

---

## 2. 错位场景（玩家面）

| 场景 | 烙印页 / imprint_hope | 今日追烙就绪 / 手持提示 | 体感 |
|------|----------------------|------------------------|------|
| 追 L02 · 穿同族头盔 · 徽记够 | 可烙到头盔 | held-ready **不响**；ready 钮写「护符」 | **漏 / 假** |
| 追 L02 · 主手持同族胸甲 · 徽记够 | held 可烙 | `sameSlotPiece` 因 slot≠charm **null** | **漏** |
| 追 L01 · 穿刃 · 徽记够 | 可烙刃 | 仍认刃 | OK（不变） |
| 追 L02 · 穿护符 | 可烙护符 | 仍认护符 | OK（不变） |

---

## 3. 「对齐」定义（钉死）

1. `sameSlotPiece`：**手持**若 `fitsDefense(def, held.slot, held.family)` 则返回 held；**穿着**镜像 `imprintWornTarget`（刃-def→blade；charm-def→charm 优先再四甲）。  
2. `maybeHeldReadyTip`：`worn` = 目标 uid 属于当前穿着烙印目标（含甲），非仅 charm/blade。  
3. `maybeReadyCue` 按钮半行：槽名取 **实际目标件** `slotName(target.slot)`（无目标时再回落 `def.slot` /「穿着件」）；**禁**对 charm-def 一律写「护符」。  
4. 复用 `EmberSignature.fitsDefense`；可抽 static 助手与 GrowthService 同序（禁两套优先级）。  
5. 刃路径 **不变**；徽记数 / imprint 价 / STAMP / stampable **不变**。  
6. **不**新开 Hope/SetChase/gem glance；**不** ForgeChoice densify；TrMenu **禁**加 lore。  
7. **不**再扫 ForgeHeldNext / CommitNext / SinkGlance 甲闸。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable 甲 / 抬 STAMP / imprint 价  
- Hope/SetChase/gem glance twin；甲闸再扫；GearNextHint 全局甲闸  
- 签名四甲格 densify / 甲待命半行  
- Affix / 伤乘 / 抬表 / sx20 / skill_mult / 深化 densify  

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **SigChase 追烙就绪防具侧对齐（可施工薄）** | **采纳** — AE/AJ 后硬缺口；SoT 一处；零功率表；**非 densify · 非甲闸** |
| GearNextHint 全局甲闸 | **否** — 甲闸链已齐；面广；总控禁再扫甲闸 |
| Hope/SetChase/gem glance 再 twin | **否** — TIP-J/T 停窗 |
| 扩 stampable 甲 | **否** — 硬禁 |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |
| 观察停窗 only | **否** — 有可感可施工诚实债 |
