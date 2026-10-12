# RESEARCH · 签名传奇效果持件 lore 诚实 · 2026-10-12

> **状态：** OPEN · TIP-BH · docs + 可施工薄窗 · 2026-10-12  
> **起因：** TIP-BG CLOSED live 后总控「离开 SF4 接线链 · 开下一可感变强途径」。扫 AH…BG / power-paths / DESIGN 剩债：签名 **行为效果已改战斗**，持件 / 甲 lore **静默**——传奇效果展示诚实缺口（**非 densify · 非伤乘 · 非 SF4 · 非 Anvil**）。  
> **上游：** TIP-BG CLOSED live `1.65.796-d13031` · feat `0bdcfa1ea` · TIP-AE 烙印扩甲 live · TIP-AJ 签名页防具摘要 live · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 glance twin（禁新 PAPI）· 零深化 densify · 零 sx20 · 零 skill_mult · 零 ForgeChoice densify · 零抬 STAMP · 零扩 stampable · 零改 L11/L13 mods · 零开 L13 施工 tip · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零再降开孔门槛 · 零 SF4 U2U3 打包 · 零灰印/聚火夹带 · 零改 DEFS.mods / ALTS**

---

## 0. 一句话

签名传奇的 `good`（行为）已在战斗生效，签名页 / 图鉴有完整得/代价；**背包持件与甲 lore 不写效果**——玩家看件不知道「这招牌改打法」。本窗只把 **既有 SoT 字** 铺到持件展示，不改功率。

---

## 1. 选题扫描（离开 SF4）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **签名传奇效果持件 lore 诚实** | **采纳** | 真缺口；行为已 live；展示静默；零改 mods；离开 SF4/soft/开孔 |
| SF4 U1 kit/形缓 cue densify | **否** | 刚接线；总控离开 SF4 链；禁 twin |
| SF4 U2 微疗 / U3 微抗 | **否** | 硬禁同窗打包 / 旁债冻 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost 再叠 | **否** | 链已齐 / 硬禁 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 | **否** | TIP-AI FAIL；HOLD |
| 防具签名再扫甲闸 / 扩 stampable | **否** | 硬禁 |
| SetChase 六槽 densify | **否** | 硬禁 |
| COORD 2c 大 DESIGN 再开 | **否** | AA/AB/AE 已走最小切片；本窗非结构扩 |
| EM1-B 每图新方法 | **否** | BLOCKED 缺业主表 |
| AFK auto-combat 再叠 remain/ETA | **否** | 近饱和 / 易 twin |
| Affix / 伤乘 / 抬表 / skill_mult | **否** | 硬禁 |

---

## 2. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `EmberSignature.Def.good` / `bad` = 行为与代价 SoT；`mods` 战斗真生效 | `EmberSignature.DEFS` | **效果改打法 · 非伤乘表抬** |
| E2 | 签名页 PAPI `g_`/`b_` / imprint 聊天已写「得/代价」 | `EmberGrowthService.sigPapi` · imprint tell | **图鉴/烙印路径诚实** |
| E3 | `EmberItems.applyLore`（刃/护符）写 shortLabel/成长/孔/来源/map · **无 sigCode → good/bad** | `EmberItems.applyLore` | **持件静默** |
| E4 | `EmberSixSlot.armorLore` 五行走标签/共鸣/成色/阶/绑定 · **无签名行** | `EmberSixSlot.armorLore` | **甲可烙（TIP-AE）仍静默** |
| E5 | 仓库 `EmberGearLib` 有一行 `§6签名：name（boss）`+`good`（无分隔 · 无代价） | `EmberGearLib` ~1070 | **仓有残句 · 持件/甲无；格式不齐** |
| E6 | 分解预览只点名「签名传奇·name」· 不复述效果 | `EmberForgeService` dismantle | **旁路提示弱** |
| E7 | `EmberSigFeel.menuLine` 故意只挂 **名字**（禁 mods 墙） | `EmberSigFeel` | **枢纽半行不承担效果全文 · 效果应在件上** |
| E8 | v2 件 `sigCode` 在 NBT；`EmberItemKeys.sig` 权威 | `EmberItemData.sigCode` · `EmberItemKeys` | **applyLore 可读 d.sigCode · 零新 PAPI** |
| E9 | 改 `mods` / 抬 STAMP / 扩 stampable / 甲闸再扫 = densify/硬禁 | 总控 | **诚实 ≠ densify** |

---

## 3. 错位场景（玩家面）

| 场景 | 真能力 | 现行展示 | 体感 |
|------|--------|----------|------|
| 刃已烙 L07（直线穿刺） | 烬斩几何已变 | 持件 lore 无形状句 | **不知招牌改打法** |
| 甲已烙 defense 签（TIP-AE） | 防具侧生效 | armorLore 无签名 | **当普通甲** |
| 仓内看同件 | GearLib 有 good 残句 | 背包打开同件无 | **仓/持件打架** |
| 签名页对照 | g_/b_ 齐全 | 须开菜单才知 | **变强途径藏菜单** |
| 调律版 | bad 变 · good 不变 | 件上无任何签 | **连原版效果都看不见** |

---

## 4. 「对齐」定义（钉死）

1. **复用** `EmberSignature.byCode(d.sigCode)` · `Def.good` · `Def.bad`（原版代价字）；**禁**改 `DEFS`/`mods`/`ALTS`/`STAMP`。  
2. **`EmberItems.applyLore`（刃/护符）：** `sigCode>0` → ≤2 行：签名名（+boss 短）· **得：good**；可选第二行 **代价：bad**（`bad` 为「无」则省）。  
3. **`EmberSixSlot.armorLore`：** 同构（甲可烙后必须可见）。  
4. **`EmberGearLib`：** 与持件同构分隔（名 / 得；可选代价）；禁加第三信息墙。  
5. **调律 live bad：** 仍以签名页 `b_` / `blade_o` 为准（须 PD）；持件只钉 **原版 bad + good**（调律「好处不变」已文档化）——**禁**为调律新 PAPI / 禁 PD 进 applyLore。  
6. **v1 无 sigCode：** 不发明；fold 后重刷 lore 自然带上。  
7. **禁** SF4 / soft / 开孔 / Anvil / 灰印 / L13 / Affix / glance twin。

---

## 5. 非目标

- L13 施工 / 改 L11·L13 mods（**HOLD**）  
- 扩 stampable / 抬 STAMP / 甲闸再扫 / SetChase 六槽 densify  
- 改 DEFS.mods / ALTS / skill_mult / 伤乘 / Affix  
- SF4 U2U3 · U1 kit cue twin · 灰印/聚火  
- soft·开孔·Anvil densify · topUpTo · 再降开孔门槛  
- 新 `%corerpg_p1_*` · SigFeel menuLine 效果墙 densify  

---

## 6. 荐本窗

| 候选 | 裁决 |
|------|------|
| **签名效果持件/甲 lore 诚实（可施工薄）** | **采纳** — 离开 SF4；可感变强途径；SoT 已有字；零改 mods |
| SF4 U1 菜单形缓再叠 | **否** — 离链 |
| Anvil / soft / 开孔 / 灰印 / L13 | **否** — 冻 / HOLD |

## STATUS: OPEN · TIP-BH · 2026-10-12
