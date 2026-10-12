# RESEARCH · 宝石效果短诚实（合成/镶嵌/石物 lore）· 2026-10-12

> **状态：** APPROVED · TIP-BJ · 已派余烬-插件+余烬-物品 · 2026-10-12 23:44 Asia/Shanghai  
> **起因：** TIP-BI CLOSED live 后总控「离开套装 blurb 链 · 开下一可感变强途径」。BI DESIGN 旁债已点名「宝石镶嵌效果持件/菜单细述」另 tip。扫 AH…BI / power-paths：宝石 **数值 SoT 已有**（`EmberGems` 常量），合成/镶嵌/NI 石物仍写「微量××」或只写族名，**锋利 T2+ 门闩几乎不可见**——玩家合成/镶嵌前不知真正变强点。  
> **上游：** TIP-BI CLOSED live `1.65.798-d13063` · feat `de89241c2` · items `2cb3917e7` · docs `1040ffb39` · `EmberGems` / `EmberGemSeatLive.effectLine` · `ember_p1_gem.yml` · `ember-enhance-gems.yml` · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零抬 `SHARP_ATK`/`STEADY_DEF`/`DRAIN_LS`/`GALE_MS`/CAP_* · 零 gem flat densify · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore 再叠 · 零调律 live bad 进持件 · 零套装 blurb 再叠 · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 densify**

---

## 0. 一句话

宝石合成/镶嵌/石物 lore 用 **既有 `EmberGems` 常量** 写 **≤1 行短效果**（含锋利刃 T2+），不抬宝石数值、不写字墙。

---

## 1. 选题扫描（离开套装 blurb + soft/开孔 + SF4 + 签名 lore）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **宝石效果短诚实（合成/镶嵌/石物）** | **采纳** | BI 旁债点名；SoT 常量已有未上菜单/石物；锋利 T2+ 隐瞒；可感变强途径；非 densify |
| 套装 blurb 再叠 | **否** | 刚 CLOSED；离链 |
| 签名 lore 再叠 / 调律 live bad 进持件 | **否** | 硬禁 / 旁债冻 |
| SF4 U2U3 / U1 cue twin | **否** | 硬禁 / 离链 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole Open 再叠 | **否** | 链已齐 / 硬禁 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| 主线 Q unlock / MG2 大 DESIGN / EM1-B | **否** | MG1/MG2 已有 mapid 线；大 unlock 非薄 |
| AFK remain/ETA densify | **否** | 近饱和 / 易 twin |
| 宝石效果 **长墙** / 持件孔位逐孔详述 densify | **否** | 本窗只短行；禁字墙 |
| 抬 gem flat / CAP | **否** | 硬禁 gem flat |
| Affix / 伤乘 / skill_mult / sx20 | **否** | 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `SHARP_ATK=0.1` · `STEADY_DEF=0.1` · `DRAIN_LS=0.01` · `GALE_MS=0.02` · CAP 0.2/0.2/0.05/0.08 · `SHARP_MIN_TIER=2` | `EmberGems.java` | **战斗 SoT 已有** |
| E2 | `EmberGemSeatLive.effectLine` 仅「锋利 · 计入攻击（刃 T2+）」等，**无幅度**；且只在镶后 one-shot cue | `EmberGemSeatLive.java` | **预购/预镶不可见** |
| E3 | NI 石物 lore =「镶嵌石 · 微量攻击/防御/吸血/移速」 | `ember-enhance-gems.yml` | **无数字 · 锋利无 T2 门** |
| E4 | 合成格 lore「碎8+骨4+币40 → 锋利」**无效果句** | `ember_p1_gem.yml` 合成四格 | **合成前不知变强点** |
| E5 | 孔0/孔1 镶入格多无效果行（仅材料/门槛） | 同文件镶入格 | **镶前不知效果** |
| E6 | 旧 `ember_socket.yml` 写「微量攻击」等 | `ember_socket.yml` | **同构模糊** |
| E7 | 持件 `socketLoreLine` =「孔：〔锋利石〕」名-only | `EmberItemData.socketLoreLine` | **可保留；本窗不扩成长墙** |
| E8 | 抬 `SHARP_ATK` 等 = gem flat densify | 总控硬禁 | **诚实 ≠ 抬表** |
| E9 | TIP-AP/BC/BD 已齐 pity / 开孔 soft / +5 开孔文案 | 链 CLOSED | **本窗不叠 soft/开孔** |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **SoT 短句**：在 `EmberGems` 或 `EmberGemSeatLive` 增/改 `shortEffect(code)`，从常量拼 ≤1 行（含锋利 T2+）；座椅 cue 复用同句。  
2. **插件**：短句 + 单测；可选只读 PAPI（非 remain twin）供菜单，或菜单直接抄 DESIGN 定稿短句（须与常量一致）。  
3. **物品**：`ember-enhance-gems.yml` 四石 lore 第 1 行 → 短效果；`ember_p1_gem.yml` 合成/镶入加半行；`ember_socket.yml` 对齐（若仍入口）。  
4. **零改** 数值常量 / CAP / soft / 开孔门槛 / pity / Anvil / SF4 / 套装 / 签名。

---

## 4. 明确不做

- 抬 `SHARP_*` / `STEADY_*` / `DRAIN_*` / `GALE_*` / CAP_*  
- 效果长墙 · 持件逐孔详述 densify · 新 gem 种类  
- soft·开孔·Anvil densify · SF4 U2U3 · 灰印/聚火 · L13 施工  
- 套装 blurb / 签名 lore / 调律 live bad 再叠  
- Affix / 伤乘 / skill_mult / sx20 / glance remain twin 族再叠  

---

## 5. 对照

| 成熟做法 | 余烬本窗 |
|----------|----------|
| PoE 通货/珠宝悬停写真实 mod 幅度 | 石物/合成写 SoT 短幅度 |
| D3 宝石槽悬停写加成 | 镶入格半行短效果 |
| 禁止「微量」糊弄构筑选择 | 禁微量；锋利明示 T2+ |

