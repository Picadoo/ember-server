# RESEARCH · ForgeCommitNext 护甲诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-AL · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AK CLOSED live 后 `ForgeHeldNext.line` 甲闸已齐；**到账 / 胚料 / 零头「下一环」按钮包**（`EmberForgeCommitNext`）仍对手持或到账 **护甲** 推 `[去强化]/`[去成色]`/`[去精工]`/`[去升阶]`——点下去服务端 K0 拒绝，预览却假。总控下一窗：**可感 UX / 变强途径（非 densify）**。  
> **上游：** TIP-AK CLOSED live `1.65.776-d12679` · TIP-AJ CLOSED live `1.65.775-d12663` · TIP-AH CLOSED live `1.65.774-d12631` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）· D2128 `afterGearDeliver` · D2101 `afterBlanks` · D2156 `afterTenths` · D318 K0 `ARMOR_REFUSE` · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md)  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin · 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice·wear·pity·awkDelta densify · 零抬 STAMP · 零改 L11/L13 · 零扩掉落 stampable · 零开 L13 施工 tip**

---

## 0. 一句话

护甲到账或手持甲时「下一环」按钮不得再假装能强化/成色；与 K0 / AK HeldNext 同句诚实。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | D318 K0：甲无 enhance/upgrade/craft/quality/swap；`ARMOR_REFUSE` | `EmberUpgradeRules.forgeable` | **真规则**甲不可锻 |
| E2 | TIP-AK：`ForgeHeldNext.line` 甲早退 ≡ `ARMOR_REFUSE`（±分解半行） | live `1.65.776-d12679` | **聊天「下一确定」已诚实** |
| E3 | 菜单 `held_is_armor` 灰养成格；RunPapi/SixPapi 甲走 `heldArmorLine` | `ember_p1_forge.yml` · SixPapi | **菜单面已诚实** |
| E4 | `ForgeCommitNext.quality(d)` / `refine(d)` 只看 `qualityCost`/`refineCost` **整数档**，**不**判 `isArmor` | `EmberForgeCommitNext` | 甲 q&lt;2 / craft&lt;3 → **仍出成色/精工钮** |
| E5 | `enhance()` **无件参**，恒返回 `[去强化]` | 同文件 | 甲到账默认第一钮 = 假强化 |
| E6 | `afterGearDeliver(piece)`：恒 `enhance()` + `quality(piece)`/`refine`/`lock` | Delivery D2128 | **掉甲到背包 → 说谎下一环** |
| E7 | `afterBlanks(held)`：`quality`→`refine`→`upgrade`；持甲时假推花胚 | BlankLoop D2101 | **分解得胚后持甲 → 假** |
| E8 | `BlankLoop.afterTenths`：`quality(held)`/`refine(held)`/`upgrade`；刚拆甲仍持甲 → 假 | BlankLoop D2156 | **零头下一环假** |
| E9 | AK RESEARCH E6：HopeDial/GearNextHint 甲轨希望一眼已齐；E8 甲待命/四甲格 **另债**（易 densify） | AK docs | 本窗不抢签名格 densify |
| E10 | TIP-J 停窗：Hope/SetChase/gem glance 再 twin **禁**；WS1 commit 族已 CLOSED，本债是 **甲闸补洞** 非 densify 新钮 | power-paths / TIP-J | 正交可感诚实 |

---

## 2. 错位场景（玩家面）

| 场景 | K0 / HeldNext / 菜单 | 今日 CommitNext 下一环 | 体感 |
|------|---------------------|------------------------|------|
| 掉落头盔到账 | 甲不可锻 | **[去强化][去成色][成套追]** | **假** |
| 分解得胚 · 仍持胸甲 | HeldNext 拒绝 | **[去成色][去精工][去升阶]** | **假** |
| 护甲零头入账 · 仍持甲 | 零头进度真 | 第二钮成色/精工/升阶 | **假** |
| 刃/护符到账或养成后 | 可锻 | enhance/quality/refine | OK（不变） |

---

## 3. 「对齐」定义（钉死）

1. `quality(d)` / `refine(d)`：若 `d != null && d.isArmor()` → **return null**（与 `forgeable` 同口径）。  
2. 新增（或内联）甲诚实钮包，供到账/胚料/零头用，例如：  
   - `[分解零头]` → `/corerpg p1 dismantle`（或现网 scrap 等同入口）· 半行 ≡ 分解/ARMOR_REFUSE  
   - 次钮：`setchase()` 或 `forge()`  
   - **禁止**对甲发出含 quality/refine/enhance/upgrade 可执行养成命令的钮。  
3. `afterGearDeliver`：`piece.isArmor()` → 甲诚实包；否则 **原样**。  
4. `afterBlanks`：`held.isArmor()` → 甲诚实包（可保留「胚已入仓」文案，花法不推甲锻）；否则原样。  
5. `BlankLoop.afterTenths`：吃修好的 `quality`/`refine` null；第二钮勿再硬编码 upgrade 顶甲；可改走甲诚实次钮。  
6. `enhance()` 无件参 API **保留**（刃路径大量调用）；**有件**路径必须经甲闸。  
7. **不**新开 glance / HopeMeter / remainLine 族；**不**改 ForgeChoice densify；TrMenu **禁**加 lore。  
8. 零功率表 / 零 STAMP / 零 L11·L13 mods。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**；TIP-AI FAIL）  
- ForgeChoice·wear·pity·awkDelta densify；K3；抬 forgeroll / enhance 表  
- 签名页甲待命半行 / 四甲格；扩甲 stampable  
- Hope/SetChase/gem glance twin；SinkGlance 大扫（可另债；本窗 SoT=CommitNext）  
- 改 `ARMOR_REFUSE` 语义为「甲可锻」

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **ForgeCommitNext 护甲诚实对齐（可施工薄）** | **采纳** — AK 后下一真说谎口；SoT 一处；零功率表；正交 L13 HOLD |
| SinkGlance 甲闸 | **否本窗主轴** — status 一瞥；CommitNext 到账更可感；可列另债 |
| 甲待命半行 / 签名四甲格 | **否** — AK/AJ 显式另债；易 densify |
| Hope/SetChase/gem glance 再 twin | **否** — TIP-J/T 停窗 |
| L13 施工 tip | **否** — PASS≠开工；显式开工前 **HOLD** |
| 观察停窗 only | **否** — 有可感可施工诚实债 |
