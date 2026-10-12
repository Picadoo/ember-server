# RESEARCH · ForgeHeldNext 手持护甲诚实对齐 · 2026-10-12

> **状态：** OPEN · TIP-AK · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-AJ CLOSED live 后签名摘要已齐；工坊 **菜单 PAPI**（`held_next_*` / `held_is_armor`）对手持护甲已走 `EmberSixPapi.heldArmorLine` → `ARMOR_REFUSE`，但 **聊天 / ActionBar「下一确定」**（`EmberForgeHeldNext.line` · forge helpLines · `/corerpg p1 status` 残留）**不判 `isArmor`** → 手持甲仍推成色/精工/强化，与 K0「甲不可锻」**说谎**。总控下一窗：**可感 UX / 变强途径（非 densify）**。  
> **上游：** TIP-AJ CLOSED live `1.65.775-d12663` · TIP-AH CLOSED live `1.65.774-d12631` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）· D1943/D1944 ForgeHeldNext · D318 K0 ARMOR_REFUSE · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md)  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin · 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice·wear·pity·awkDelta densify · 零抬 STAMP · 零改 L11/L13 · 零扩掉落 stampable · 零开 L13 施工 tip**

---

## 0. 一句话

手持护甲时「下一确定」必须与工坊拒绝口径同句；今天菜单诚实、聊天/抬头假推养成。

---

## 1. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | D318 K0：护甲 **无** enhance/upgrade/craft/quality/swap 轨；`ARMOR_REFUSE = "护甲随护符成长；成色和精工看掉落"` | `EmberUpgradeRules` · forge refuse | **真规则**甲不可锻 |
| E2 | TrMenu / RunPapi：`held_next_q`/`held_next_c` 若 `isArmor` → `EmberSixPapi.heldArmorLine`；`held_is_armor` 闸菜单 | `EmberRunPapi#heldNext` · `ember_p1_forge.yml` | **菜单面已诚实** |
| E3 | `EmberForgeHeldNext.line` 注释写「held blade/charm」；**无** `isArmor` 早退；对甲件仍算 quality→refine→enhance… | `EmberForgeHeldNext.java` | **SoT 缺甲闸** |
| E4 | forge `helpLines` / forgegoal 后 ActionBar：`heldNextLine` → 直接 `ForgeHeldNext.line`（不经 RunPapi） | `EmberForgeService` ~134–172 | 开工会话 **聊天说谎** |
| E5 | `/corerpg p1 status`（或成长摘要）手持焦点：`EmberCommand` ~4466–4494 同调 `ForgeHeldNext.line` | `EmberCommand` p1 | 状态页 **同样假推** |
| E6 | HopeDial / GearNextHint 甲轨：`EmberHopeMeter` 已 `emptySlot("甲不可强"|"甲看掉落")`；`heldForgeLine` 甲走 SixPapi | HopeMeter · RunPapi | 希望一眼 **已对齐**；缺口只在 HeldNext SoT |
| E7 | 装备页工坊入口 lore 已写「手持护甲：仅可分解…」 | `ember_p1_gear.yml` f | 入口文案 OK；**会话反馈仍假** |
| E8 | AJ RESEARCH 可选「甲待命」半行 / 签名四甲格 | AJ docs | **另债**；本窗不抢（禁六槽大改 densify） |

---

## 2. 错位场景（玩家面）

| 场景 | 菜单 `held_next_*` | 今日聊天/AB「下一确定」 | 体感 |
|------|-------------------|------------------------|------|
| 手持刃/护符 · 料够成色 | 成色近档 | 成色 · 料够 | OK |
| 手持护甲 · 开工会话 | `§8护甲随护符成长…` | **成色/精工/强化假推** | **假** |
| 手持护甲 · `/corerpg p1 status` | — | 同上假推 | **假** |
| 手持护甲 · 点强化确认 | 服务端 refuse | （预览若走 HeldNext 则更假） | 点了才发现 |

---

## 3. 「对齐」定义（钉死）

1. `EmberForgeHeldNext.line(d,…)`：若 `d != null && d.isArmor()` → **立即**返回诚实句（复用 `ARMOR_REFUSE` 口径；可选半行分解零头提示，**不**发明新 pity/费用）。  
2. 刃/护符行为 **不变**（含 forgeGoal 优先序）。  
3. 空手句可保持「手持刃/护符」；甲件不走空手套话。  
4. **不**新开 glance / remainLine / HopeMeter 族；**不**改 ForgeChoice / 稳赌 / commit densify。  
5. RunPapi / SixPapi 已诚实 → **本窗可不改**（SoT 修好后聊天与菜单同源）。  
6. TrMenu **禁**加 lore 行；可选头注一句「HeldNext 甲闸与菜单同句」（非必须）。  
7. 零功率表 / 零 STAMP / 零 L11·L13 mods。

---

## 4. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**；TIP-AI FAIL）  
- ForgeChoice·wear·pity·awkDelta densify；K3；抬 forgeroll / enhance 表  
- 扩甲 stampable；签名页四甲格；甲待命半行 densify  
- 希望 celebration / 新 hope PAPI；glance twin  
- 改 ARMOR_REFUSE 语义为「甲可锻」（硬否）

---

## 5. 荐本窗

| 候选 | 裁决 |
|------|------|
| **ForgeHeldNext 手持护甲诚实对齐（可施工薄）** | **采纳** — 真说谎；SoT 一处修好聊天+status；零功率表；正交 L13 HOLD / AJ 签名轴 |
| 甲待命半行 / 签名四甲格 | **否** — AJ 显式另债；易 densify |
| Hope/SetChase/gem glance 再 twin | **否** — TIP-J/T 停窗 |
| L13 施工 tip | **否** — PASS≠开工；显式开工前 **HOLD** |
| 观察停窗 only | **否** — 有可感可施工诚实债 |
