# RESEARCH · 装备页套装效果行跟随成套族诚实 · 2026-10-12

> **状态：** CLOSED live `1.65.798-d13063` · TIP-BI · feat `de89241c2` · 2026-10-12 23:38 Asia/Shanghai  
> **起因：** TIP-BH CLOSED live 后总控「离开签名 lore 链 · 开下一可感变强途径」。扫 AH…BH / power-paths / DESIGN 剩债：装备页套装格 **标题已跟成套族**，**效果行写死炽愈**——成套焚烬/烬爆玩家读到错族说明；`familyBlurb` 自称与装备页同口径却未接线，且炽愈仍写 ×1.12（D293 live `sustain_hp_mult=1.00`）。  
> **上游：** TIP-BH CLOSED live `1.65.797-d13047` · feat `ce5a4b66b` · docs `82f86182a` · TIP-P 觉醒升档白话 live · D325 四件套 live · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零 SetRules/Formula 改系数 · 零改 sustain_hp_mult · 零签名 lore 再叠 · 零调律 live bad 进持件 · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip**

---

## 0. 一句话

装备页套装格效果行必须 **跟随当前成套族**（读既有 `familyBlurb` / 现网表口径），并修正炽愈 ×1.12 谎称；不抬套装功率。

---

## 1. 选题扫描（离开签名 lore + SF4 + soft/开孔）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **装备页套装效果行跟随成套族诚实** | **采纳** | 真缺口；标题动态、效果写死炽愈；familyBlurb SoT 已有未接线；×1.12 vs D293 1.00 谎；离开签名 lore / SF4 / soft |
| 签名 lore 再叠 / 调律 live bad 进持件 | **否** | 刚 CLOSED / 旁债硬冻 |
| SF4 U2U3 / U1 cue twin | **否** | 硬禁 / 离链 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole 再叠 | **否** | 链已齐 / 硬禁 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| 主线 Q01–Q07 大 unlock DESIGN / EM1-B | **否** | COORD 2c 大设计 / EM1-B BLOCKED；非本薄窗 |
| 传奇效果抬 mods / 新签名 | **否** | 硬禁抬表 |
| AFK remain/ETA densify | **否** | 近饱和 / 易 twin |
| 宝石镶嵌效果持件 densify 长墙 | **否** | 本窗优先装备页套装错族；宝石另 tip |
| Affix / 伤乘 / skill_mult / sx20 | **否** | 硬禁 |

---

## 2. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 装备页 `S` name=`%corerpg_p1_awaken%`（动态「焚烬 觉醒II」等） | `ember_p1_gear.yml` ~85 | **标题已跟族** |
| E2 | 同格 lore **写死** `§a炽愈：生命 ×1.00，每 5 次普攻…` | 同文件 ~87 | **焚烬/烬爆玩家读错族效果** |
| E3 | `EmberItemData.familyBlurb` 注释：「chat / help / **gear page** 同口径」；实现三族各一句 | `EmberItemData.familyBlurb` | **SoT 已有 · 装备页未接线** |
| E4 | `familyBlurb("sustain")` 仍写 **生命 ×1.12** | 同上 | **与 D293 live `sustain_hp_mult: 1.00` 冲突** |
| E5 | `ember-v1.yml` `sustain_hp_mult: 1.00`；Formula 注释 D293 | `ember-v1.yml` · `EmberFormula` | **现网无 ×1.12 生命乘** |
| E6 | TIP-P `awkDeltaLine` / `set_run` 已有升档白话与本局短 cue | `EmberSetFeel` · gear `P` 格 | **突破瞬间/本局半行已有；装备主格效果行仍错** |
| E7 | 四件套 `%corerpg_p1_armor_set%` 已诚实三态 | gear `S` 第二行 · D345 | **保留；本窗不改 −3%** |
| E8 | 改 SetRules coef / Formula / sustain_hp_mult = 抬表 | 总控硬禁 | **诚实 ≠ densify** |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **修正** `familyBlurb("sustain")`：去掉 ×1.12 谎称，对齐 D293 / 装备页已写的 ×1.00 或省略生命乘只写回血句。  
2. **插件** 暴露只读 PAPI（例 `set_blurb`）=`familyBlurb(activeSet)`；未成套 → 固定短提示（刃+护符同族启用）。  
3. **物品** `ember_p1_gear.yml` `S` 效果行改 `%corerpg_p1_set_blurb%`；保留四件套行。  
4. **零改** SetRules 数组 / Formula / sustain_hp_mult 数值 / 签名 lore / soft / Anvil / SF4。

---

## 4. 明确不做

- 签名 lore 再叠 · 调律 live bad 进持件  
- SetRules / Formula / `sustain_hp_mult` 抬/降  
- 四件套 −3% 改值 · K3 · 档 S  
- soft·开孔·Anvil densify · SF4 U2U3 · 灰印/聚火 · L13 施工  
- Affix / 伤乘 / skill_mult / sx20 / glance remain twin 族再叠  

---

## 5. 对照

| 成熟做法 | 余烬本窗 |
|----------|----------|
| PoE 装备页被动随当前树/簇更新 | 套装效果行随 `activeSet` |
| D3 套装悬停写本套效果 | familyBlurb → 装备格 |
| 禁止用错套说明误导构筑 | 禁写死炽愈 |

