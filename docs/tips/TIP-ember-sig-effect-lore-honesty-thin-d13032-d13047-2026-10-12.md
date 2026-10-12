# tip · TIP-BH 签名传奇效果持件 lore 诚实（可施工薄 · CoreRpg）· D13032–D13047

> **状态：** **CLOSED live `1.65.797-d13047`** · feat `ce5a4b66b` · 批 A·M · 2026-10-12 23:30 Asia/Shanghai  
> **上游门禁：** TIP-BG CLOSED live `1.65.796-d13031` · feat `0bdcfa1ea` · docs `8298116c5` · RESEARCH [`RESEARCH-ember-sig-effect-lore-honesty-2026-10-12.md`](../design/RESEARCH-ember-sig-effect-lore-honesty-2026-10-12.md) · DESIGN [`DESIGN-ember-sig-effect-lore-honesty-2026-10-12.md`](../design/DESIGN-ember-sig-effect-lore-honesty-2026-10-12.md) · TIP-AE 烙印扩甲 live · TIP-AJ 签名页摘要 live · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 离开 SF4 接线链 → **签名传奇行为效果持件/甲 lore 诚实**（**非 densify · 非伤乘 · 非 SF4 · 非 Anvil · 非 soft/开孔**）  
> **施工岗（批后派单）：** **余烬-插件**（CoreRpg · `EmberItems.applyLore` · `EmberSixSlot.armorLore` · `EmberGearLib` 同构；零改 DEFS.mods/ALTS/STAMP）；**不**派余烬-物品（无 TrMenu 必改）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零抬 skill_mult · 零伤乘 · 零 Affix densify · 零抬表 · 零 glance twin（禁新 PAPI）** · **零 ForgeChoice densify · 零 sx20 · 零粒子 densify** · **零改 DEFS.mods / ALTS** · **零 Anvil densify · 零 topUpTo · 零开 L13 施工 tip · 零再扫甲闸 · 零扩 stampable · 零 SetChase 六槽 densify · 零 soft-latch·CostSoft·pity·soft-glance·firstsoft·cook·life-buy soft 再叠 · 零 xp-boost · 零 GemHole/enhance+5 再叠 · 零灰印/聚火夹带 · 零 ConvertCap densify · 零 SF4 U2U3 打包 · 零 U1 kit 形缓 cue twin**

---

## 0. 一句话

签名已改打法，持件/甲 lore 把 **既有 good（+原版 bad）** 写出来；不改功率、不碰 SF4/Anvil/soft。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **签名传奇效果持件 lore 诚实** | **采纳** | 离开 SF4；可感变强途径；SoT 字已在 Def；持件静默真缺口 |
| SF4 U1 形缓 kit/cue 再叠 | **否** | 总控离链；刚 live 禁 twin |
| SF4 U2 / U3 同窗 | **否** | 硬禁打包 / 旁债冻 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost 再叠 | **否** | 链已齐 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 tip | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| Affix / 伤乘 / 抬表 / skill_mult / sx20 | **否** | 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 方案 M（定稿荐 · docs 本窗 · 批后插件）

| ID | 项 | 验收 |
|----|-----|------|
| D13032 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D13033 | power-paths 进度戳：**TIP-BG CLOSED live**；**TIP-BH OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D13034 | 文首钉死：离开 SF4；传奇效果展示诚实；零改 mods | 可核 |
| D13035 | 证据链：applyLore 无签 · armorLore 无签 · GearLib 粘连 · 页 g_/b_ 已齐 | RESEARCH §2 |
| D13036 | SoT 锁：`byCode(sigCode)` · `good` · 原版 `bad`；禁 PD/sigAlt 进 applyLore | DESIGN §2.1 |
| D13037 | **插件：** `EmberItems.applyLore` 刃/护符签名块 ≤3 行 | 代码+单测 |
| D13038 | **插件：** `EmberSixSlot.armorLore` 同构 | 可核 |
| D13039 | **插件：** `EmberGearLib` 替换粘连行为同构分隔 | 可核 |
| D13040 | 调律 live bad：仍签名页；持件只钉原版 bad | 可核 |
| D13041 | v1 无 sigCode：不发明；fold 后重刷自然带 | 可核 |
| D13042 | 玩家口径固定（§2 下） | 可核 |
| D13043 | NON 核：`git show` 无 mods/ALTS/STAMP/Affix/伤乘/skill_mult/SF4/Anvil/soft/开孔/L11/L13 | PASS |
| D13044 | HOLD：L13；Anvil densify；topUpTo；soft/开孔再叠；灰印/聚火；U2/U3；甲闸再扫；扩 stampable；SetChase 六槽；Affix/伤乘/抬表/glance twin/sx20/ForgeChoice densify | 文内硬句 |
| D13045 | 批后派单句：**余烬-插件**；策划 docs-only；**不**派物品 | 派单句 |
| D13046 | 单测最小包（DESIGN §2.3） | PASS |
| D13047 | 缓冲：错链/错戳/lore 过长；禁借号改 mods/开 L13/Anvil densify/SF4 U2U3 | 超额新 tip |

### 玩家口径（固定）

- 看着刃/护符/甲就能读：签名名、它改了什么打法、原版代价。  
- 调律当前代价仍去签名传奇页看。  
- 本窗不改伤害、掉率、开孔、Anvil、烬斩数字。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-sig-effect-lore-honesty-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-sig-effect-lore-honesty-2026-10-12.md` |
| 持件 lore | `CoreRpg/.../p1/EmberItems.java` `applyLore` |
| 甲 lore | `CoreRpg/.../p1/EmberSixSlot.java` `armorLore` |
| 仓 lore | `CoreRpg/.../p1/EmberGearLib.java` |
| SoT 只读 | `EmberSignature.Def.good` / `bad` / `byCode` |
| 旁轨 HOLD | L13 · Anvil soft densify · 灰印/聚火 · SF4 U2U3 · soft/开孔再叠 |

---

## 4. 明确不做

- 任何 DEFS.mods / ALTS / STAMP_RATE / stampable / 甲闸 / SetChase densify  
- Affix · 伤乘 · skill_mult · sx20 · glance twin · 新 PAPI  
- SF4 U2/U3 · U1 kit 形缓 cue twin · 灰印/聚火  
- soft·开孔·Anvil densify · topUpTo · 再降开孔门槛  
- 开 L13 施工 tip · 派物品岗改 TrMenu  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [x] 文首 HOLD / 选题否决表可核  
- [x] 批后才派 **余烬-插件**；策划本窗 **无**业务 jar  
- [x] `git show` docs-only（本 commit）；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [x] 总控批 **A · 方案 M** · 已批 · 已派余烬-插件  
- 选题：TIP-BG 后 · **离开 SF4** · **签名效果持件 lore 诚实**（可感变强途径 · 零改 mods）  
- **批后派岗：** 余烬-插件；不派物品  
- **HOLD：** L13 施工；Anvil densify；topUpTo；soft·开孔再叠；灰印·聚火；SF4 U2U3；甲闸再扫；扩 stampable；SetChase 六槽 densify  

## STATUS: CLOSED live `1.65.797-d13047` · feat `ce5a4b66b` · 批 A·M · Enabling+Done 5.6s · 2026-10-12 23:30 Asia/Shanghai
