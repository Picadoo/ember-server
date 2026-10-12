# RESEARCH · SinkGlance 护甲诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-AM · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AL CLOSED live 后 CommitNext / HeldNext / 签名摘要甲闸已齐；**开工会话消耗一瞥**（`EmberSinkGlance.tellHeld` · `/corerpg p1 forge` 入口）对手持护甲仍拼 `GearNextHint.qualityLine`/`craftLine`/`upgradeLine` + 成色/精工 `lackHalf`——点下去 K0 拒绝，一瞥却假推费用。总控下一窗：**可感 UX / 变强途径（非 densify）**；SinkGlance **可作候选但勿 densify**（诚实/清晰 OK，禁 glance twin / 功率 densify）。  
> **上游：** TIP-AL CLOSED live `1.65.777-d12695` · TIP-AK CLOSED live `1.65.776-d12679` · TIP-AJ CLOSED live `1.65.775-d12663` · TIP-AH CLOSED live `1.65.774-d12631` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）· D680/D681 SinkGlance · D318 K0 `ARMOR_REFUSE` · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md)  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin · 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice·wear·pity·awkDelta densify · 零抬 STAMP · 零改 L11/L13 · 零扩掉落 stampable · 零开 L13 施工 tip · 零 SinkGlance 功率 densify / 新 remainLine 族**

---

## 0. 一句话

手持护甲开工会话时「消耗一瞥」不得再假装能强化/成色/精工；与 K0 / AK HeldNext / AL CommitNext 同句诚实。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | D318 K0：甲无 enhance/upgrade/craft/quality/swap；`ARMOR_REFUSE` | `EmberUpgradeRules.forgeable` | **真规则**甲不可锻 |
| E2 | TIP-AK：`ForgeHeldNext.line` 甲早退 ≡ `ARMOR_REFUSE` | live `1.65.776-d12679` | 聊天「下一确定」已诚实 |
| E3 | TIP-AL：`CommitNext.quality`/`refine` 甲→null；到账/胚料/零头甲诚实包 | live `1.65.777-d12695` | 到账下一环已诚实 |
| E4 | `EmberSinkGlance.tellHeld`：**无** `isArmor` 闸；恒拼 `enhanceLine`/`qualityLine`/`craftLine`/`upgradeLine` | `EmberSinkGlance.java` | **消耗一瞥 SoT 缺甲闸** |
| E5 | `GearNextHint.qualityLine`/`craftLine`/`upgradeLine` 只看档位 int，**不**调 `forgeable` | `EmberGearNextHint` | 甲仍出假成色/精工/升阶费用行 |
| E6 | SinkGlance 另加 `qualityCost`/`refineCost` → `lackHalf`（D1176） | 同文件 ~74–83 | 甲再叠假「成色升缺料 / 精工缺料」 |
| E7 | `/corerpg p1 forge` 开入口：先 `tellHeld`，再恒发 `[强化预览][成色预览][精工预览]` 三钮 | `EmberCommand` ~4059–4070 | **同瞬**预览钮对甲也假 |
| E8 | `enhanceLine(armor)` 经 `enhanceCheck`→`forgeable` 已返 `ARMOR_REFUSE` | UpgradeRules · GearNextHint | 强化行半诚实；**成色/精工/升阶行仍假** → 一瞥整体仍假 |
| E9 | HopeMeter / SixPapi 甲轨已 `emptySlot` / `heldArmorLine` | HopeMeter · SixPapi | 希望/菜单已齐；缺口在 SinkGlance |
| E10 | AL RESEARCH §4/§5：SinkGlance 甲闸 **另债**（本窗 SoT 曾=CommitNext） | AL docs | 本窗接棒 |
| E11 | TIP-J 停窗：Hope/SetChase/gem glance 再 twin **禁**；本债是 **甲闸补洞** 非 densify 新钮 | power-paths / TIP-J | 正交可感诚实 |
| E12 | `BlankLoop.followHint` 仍写 `[去成色]|[去精工]|[去升阶]`（AL 后残留文案） | `EmberBlankLoop` | 轻触另半行；非主轴 |

---

## 2. 错位场景（玩家面）

| 场景 | K0 / HeldNext / CommitNext | 今日 SinkGlance + forge 预览钮 | 体感 |
|------|---------------------------|--------------------------------|------|
| 手持头盔开 `/corerpg p1 forge` | 甲不可锻；下一确定诚实 | **成色/精工/升阶费用+缺料一瞥** + 三预览钮 | **假** |
| 手持刃 · 料差成色 | 可锻 | 成色行+缺料 | OK（不变） |
| 手持甲 · 点成色预览 | 服务端 refuse | 一瞥已假推费用 | 点了才发现 |
| 空手开工会话 | — | 「手持余烬件可看…」提示 | OK（可微调甲口径，非必须） |

---

## 3. 「对齐」定义（钉死）

1. `EmberSinkGlance.tellHeld(…)`：若读到手持件 `d.isArmor()` → **早退诚实句**（复用 `ARMOR_REFUSE`；可选半行分解零头/`dismantleYieldLine`/`armorDismantleTenths` 进度，**不**发明新 pity/费用）。ActionBar 同口径（如「消耗 · 护甲随护符」或「分解零头」）。  
2. 甲路径 **禁止**再拼 `qualityLine`/`craftLine`/`upgradeLine` 费用行，**禁止**再跑成色/精工 `lackHalf`。  
3. `enhanceLine` 对甲虽已返 refuse，甲早退后 **不必**再拼强化行（避免一瞥堆 refuse+假成色）。  
4. 刃/护符行为 **不变**（含 forgeGoal 聚焦缺料、ROLL/CONVERT/BRAND 目标枝）。  
5. **轻触（同主题）：** `EmberCommand` forge 开入口手持预览三钮 — 若手持甲，**勿**发 enhance/quality/refine 预览命令钮；改发甲诚实短包（分解/成套追/工坊，命令须现网存在；可复用 CommitNext `armorHonestPack` 思路或等价）。  
6. **可选轻触：** `BlankLoop.followHint` 文案去假成色串（非玩家主路径）。  
7. **不**改 `GearNextHint.qualityLine` 全局（其它调用方 + 单测钉死刃串；甲闸放在 SinkGlance / forge 开入口消费者）。  
8. **不**新开 glance / HopeMeter / remainLine 族；**不** ForgeChoice densify；TrMenu **禁**加 lore。  
9. 零功率表 / 零 STAMP / 零 L11·L13 mods。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**；TIP-AI FAIL）  
- ForgeChoice·wear·pity·awkDelta densify；K3；抬 forgeroll / enhance 表  
- SinkGlance **功率 densify**（新目标枝 / 新 PAPI twin / 抬费用可见度当成长）  
- Hope/SetChase/gem glance twin；签名页甲待命半行 / 四甲格  
- 改 `ARMOR_REFUSE` 语义为「甲可锻」  
- 全局改 `GearNextHint` 成色/精工串（本窗不抢）

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **SinkGlance 护甲诚实对齐（可施工薄）** | **采纳** — AL 显式另债；开工会话可感说谎；SoT 一处；零功率表；正交 L13 HOLD；**非 densify** |
| forge 开入口预览三钮甲闸 | **纳入同主题轻触** — 与 tellHeld 同瞬 |
| GearNextHint 全局甲闸 | **否本窗主轴** — 单测/多调用方面广；消费者闸更薄 |
| BlankLoop.followHint 文案 | **可选轻触** |
| L13 施工 tip | **否** — PASS≠开工；**HOLD** |
| Hope/SetChase/gem glance 再 twin | **否** — TIP-J/T 停窗 |
| 甲待命半行 / 签名四甲格 | **否** — AJ/AK 另债；易 densify |
| 观察停窗 only | **否** — 有可感可施工诚实债 |
