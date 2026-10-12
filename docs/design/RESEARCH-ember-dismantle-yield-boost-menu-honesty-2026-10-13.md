# RESEARCH · 分解日首得胚+1（YieldBoost）菜单/预览诚实（离开签名盖章 pity · SUR 表面化）· 2026-10-13

> **状态：** OPEN · TIP-BX · 荐批 A·M · 2026-10-13 01:14 Asia/Shanghai  
> **起因：** TIP-BW CLOSED live 后总控「离开签名盖章软保底「连空 n/N」菜单诚实链 · 开下一可感变强途径」。硬禁含 sig stamp pity n/N 再叠 · sure/keep/直办半行族再叠 · Affix densify · STAMP densify。扫 AH…BW / power-paths / BW 旁债：**`EmberDismantleYieldBoost` 已 live（非甲日首拆 +1 胚 · Forge 预览聊天已吃 boost）**，菜单 `%held_dismantle_yield%` / `GearNextHint.dismantleYieldLine` **只报表额得胚、不报日首+1**——打开工坊看错「今天拆能拿多少」。  
> **上游：** TIP-BW CLOSED live `1.65.802-d13287` · feat `2bdb153ff` · items `4118a3ab6` · docs `5fa1aea22` · TrMenu 77 · `EmberDismantleYieldBoost` · `EmberForgeService.dismantle` · `EmberGearNextHint.dismantleYieldLine` · `ember_p1_forge.yml` D/d · power-paths（变强途径表面化 · 非 densify）· TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零改 BONUS/C_USED · 零改 dismantleYield 表 · 零 Affix densify · 零伤乘 · 零抬表 · 零抬 STAMP_RATE · 零扩 stampable · 零 SigStamp densify · 零签名效果 lore / 盖章 pity n/N 再叠 · 零烙纹直办/forge/洗练 sure·keep·直办半行再叠 · 零 soft·开孔再叠 · 零 Anvil densify · 零 SF4 U2U3 · 零灰印·聚火 · 零 UndoWindow densify · 零 DeathRefund densify · 零调律 live bad · 零并轨 B · 零效果长墙 · 零 lore>3 · 零 ConvertCap densify · 零 forgeroll 权重 densify · 零改解锁数学 · 零开 L13 施工 tip · 零新 hope celebration**

---

## 0. 一句话

工坊分解预览/菜单与聊天预览同口径：日首非甲拆待用时一眼 **得胚×(表额+1) · 今日首拆+1待用**；不改增产表。

---

## 1. 选题扫描（离开签名盖章 pity n/N + AH…BW 已齐诚实族）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **分解日首得胚+1（YieldBoost）菜单/预览诚实** | **采纳** | 离开 sure/keep/pity-n/N TrMenu 半行族；SoT boost 已 live · 菜单 PAPI 说谎；同构 BB 日首加验 / BD 日首门槛表面化；**非** densify · **非** 仅「确定」字面（BW 旁债 #12 弱项） |
| 签名盖章 pity n/N / 签名效果 lore 再叠 | **否** | 刚 CLOSED / 硬禁离链 |
| forge EQRUS / 洗练直留 / 烙纹直办 / 互换 / 成色精工确定成功再叠 | **否** | sure·keep·直办半行族硬禁离链 |
| 分解菜单「确定」字面 alone | **否** | 弱；本窗做更强 YieldBoost 对齐 |
| 升阶「印记另一条路」短指针 | **否** | 升阶邻链 · BO 已满 lore≤3 |
| forgeroll 普通格成色权重半行 | **否** | 禁 ForgeChoice densify 边 |
| AFK remain/层差 dwell 再叠 | **否** | D383/D405/D411 已齐 · BW #14 可选但 prefer 换轴 |
| hub/raid/abyss access / methodLine / hope glance 再叠 | **否** | access_* / MG/HD 硬禁或停窗 |
| cook/AFK/attune soft / life-buy / supply 再叠 | **否** | 链已齐或 soft densify 硬禁 |
| UndoWindowSoft / DeathRefund 再 densify | **否** | 硬禁 densify；本窗不抬撤销窗 |
| Anvil densify / soft·开孔 / SF4 U2U3 / 灰印 / 聚火 / L13 | **否** | 硬禁 / HOLD |
| Affix densify / ConvertCap densify / 伤乘 / 抬 STAMP | **否** | 硬禁 |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 效果长墙 / lore>3 / 并轨 B | **否** | 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberDismantleYieldBoost`：`BONUS=1` · `ready`→非甲日首 `apply(+1)` · `C_USED` 日闩 | `EmberDismantleYieldBoost.java` | **增产已 live · 零改表** |
| E2 | `ForgeService.dismantle` 预览 `!go`：`apply(pd, blanks, false)` → 聊天/AB 报 **boost 后** 得胚；成交才 `consume` +「分解增产 · 胚 +1」 | `EmberForgeService` ~600–625 | **聊天预览已诚实** |
| E3 | `GearNextHint.dismantleYieldLine(d)`：**只** `得胚×dismantleYield(d)` · **不吃** YieldBoost · **无** PlayerData | `EmberGearNextHint.java` | **菜单 SoT 说谎** |
| E4 | `RunPapi.heldForgeLine(…,"dismantle")` → E3；已有 softPd 在手却未传入 | `EmberRunPapi` ~heldForgeLine | **接线缺口** |
| E5 | 工坊 Icon `D` lore：`%held_dismantle_yield%` = 表额；Icon `d`：「确认在聊天栏」· **无得胚 · 无日首+1** | `ember_p1_forge.yml` D/d | **菜单缺口** |
| E6 | 甲分解走 `SixPapi.heldArmorLine` 零头 · YieldBoost **不**作用于甲 | `EmberSixPapi` / ForgeService | **本窗甲线不动** |
| E7 | D9633 tip 已 SHIP YieldBoost；D1324 AB「得胚×」；**无** 菜单日首尾对齐 tip | tip D9633 / D1321 | **表面化债未收** |
| E8 | power-paths：已有保底/日首表面化 > densify；禁 UndoWindow densify | DESIGN power-paths | **本窗 = 表面化 · 非抬 BONUS** |

### SoT 展示（只读 · 禁改 BONUS）

| 态 | 菜单/预览口径（对齐聊天 boost 后数字） |
|----|--------------------------------------|
| 非甲 · ready | `得胚×(表额+1) · 今日首拆+1待用`（或等价短句） |
| 非甲 · 已用 | `得胚×表额`（同现网） |
| 不可分解 | 既有 `dismantleCheck` 短句 |
| 甲 | 既有零头句 · **本窗不改** |
| 禁 | 抬 BONUS · 改 dismantleYield(T) · 甲也加日首 · celebration 墙 · 新 PAPI 轨（优先修既有 line） |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **插件：** `dismantleYieldLine(d, pd)`（或等价）ready∩非甲 → 表额+BONUS 并尾「今日首拆+1待用」；peek 不 consume；`heldForgeLine` 传入 softPd；单测三态。  
2. **物品：** `ember_p1_forge` Icon `D` 吃对齐后 PAPI；Icon `d` lore 写入得胚行 +「聊天栏点确认」短句（≤3）；可选 Open 半行指针。  
3. **零** sure/keep/直办半行再叠；零改 BONUS；零 UndoWindow densify。  
4. **施工岗：** 批后 **余烬-插件** + **余烬-物品**。

---

## 4. 明确不做

- 改 `BONUS` / `C_USED` 周期 / `dismantleYield` 表 · Affix densify · 伤乘 · 抬表 · STAMP densify · 扩 stampable  
- soft·开孔·Anvil densify · SF4 U2U3 · 灰印/聚火 · L13 施工 · UndoWindow/DeathRefund densify  
- 签名盖章 pity n/N / 签名效果 lore / forge EQRUS·洗练·烙纹 sure 半行再叠 · access_* · 升阶印记指针 · forgeroll 权重  
- 效果长墙 · lore>3 · 并轨 B · 发明第二套 dismantle hope PAPI（优先修既有 `held_dismantle_yield`）

## STATUS: OPEN · TIP-BX · 荐批 A·M · 2026-10-13 01:14 Asia/Shanghai
