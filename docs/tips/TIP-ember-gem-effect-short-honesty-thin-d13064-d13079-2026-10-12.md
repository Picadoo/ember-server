# tip · TIP-BJ 宝石效果短诚实（合成/镶嵌/石物 lore · 可施工薄 · 可感变强途径）· D13064–D13079

> **状态：** **CLOSED live `1.65.799-d13079`** · feat `72cba2db4` · items `bc1e63407` · 批 A·M · 2026-10-12 23:46 Asia/Shanghai  
> **上游门禁：** TIP-BI CLOSED live `1.65.798-d13063` · feat `de89241c2` · items `2cb3917e7` · docs `1040ffb39` · RESEARCH [`RESEARCH-ember-gem-effect-short-honesty-2026-10-12.md`](../design/RESEARCH-ember-gem-effect-short-honesty-2026-10-12.md) · DESIGN [`DESIGN-ember-gem-effect-short-honesty-2026-10-12.md`](../design/DESIGN-ember-gem-effect-short-honesty-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 离开套装 blurb 链 → **宝石效果短诚实**（**非 densify · 非抬 gem flat · 非 soft/开孔再叠 · 非 SF4 · 非 Anvil · 非签名 lore · 非套装 blurb 再叠 · 非效果长墙**）  
> **施工岗（批后派单）：** **余烬-插件**（`shortEffect` / `effectLine` 幅度对齐 · 座椅 cue · 单测）；**余烬-物品**（`ember-enhance-gems.yml` · `ember_p1_gem.yml` · 可选 `ember_socket.yml`）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零抬 SHARP_ATK/STEADY_DEF/DRAIN_LS/GALE_MS/CAP_* · 零 gem flat densify · 零 Affix densify · 零伤乘 · 零抬表 · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore 再叠 · 零调律 live bad 进持件 · 零套装 blurb 再叠 · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 densify**

---

## 0. 一句话

合成/镶嵌/石物别再写「微量」；用现网宝石常量写一行短效果，锋利标明刃 T2+。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **宝石效果短诚实（合成/镶嵌/石物）** | **采纳** | 离开套装 blurb；BI 旁债点名；SoT 常量已有；锋利 T2+ 隐瞒；可感变强途径 |
| 套装 blurb / 签名 lore 再叠 | **否** | 刚 CLOSED / 硬禁 |
| SF4 U2U3 / U1 cue twin | **否** | 硬禁 / 离链 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole 再叠 | **否** | 链已齐 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 tip | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| 主线 unlock 大 DESIGN / EM1-B | **否** | 非薄 / BLOCKED |
| AFK densify / 效果长墙 / 抬 gem flat | **否** | 近饱和 / 硬禁 |
| Affix / 伤乘 / 抬表 / skill_mult / sx20 | **否** | 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 方案 M（定稿荐 · docs 本窗 · 批后插件+物品）

| ID | 项 | 验收 |
|----|-----|------|
| D13064 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D13065 | power-paths 进度戳：**TIP-BI CLOSED live**；**TIP-BJ OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D13066 | 文首钉死：离开套装 blurb；宝石短效果；零抬 gem flat/CAP | 可核 |
| D13067 | 证据链：EmberGems 常量 · NI「微量」· 合成无效果 · seat effectLine 无幅度 · 锋利 T2+ | RESEARCH §2 |
| D13068 | SoT 锁：`shortEffect(code)` 由常量格式化；sharp 含 T2+；禁改数值 | DESIGN §2.1 |
| D13069 | **插件：** `shortEffect` / 升级 `effectLine`；座椅 cue 复用 | 代码+单测 |
| D13070 | **物品：** NI 四石 lore 第 1 行 → 短效果 | 可核 |
| D13071 | **物品：** `ember_p1_gem.yml` 合成+镶入各 ≤1 行短效果；可选 socket.yml 对齐 | 可核 |
| D13072 | 持件 `socketLoreLine` 默认不动；禁扩孔位字墙 | 可核 |
| D13073 | 玩家口径固定（§2 下） | 可核 |
| D13074 | NON 核：`git show` 无 SHARP_ATK 等赋值改 / soft/开孔/Anvil/SF4/签名/套装 SetRules/Affix/伤乘/skill_mult | PASS |
| D13075 | HOLD：L13；Anvil densify；topUpTo；soft/开孔再叠；灰印/聚火；U2/U3；调律 live bad；签名 lore；套装 blurb 再叠；甲闸再扫；扩 stampable；SetChase 六槽；效果长墙；Affix/伤乘/抬表/sx20/ForgeChoice densify | 文内硬句 |
| D13076 | 批后派单句：**余烬-插件** + **余烬-物品**；策划 docs-only | 派单句 |
| D13077 | 单测最小包（DESIGN §2.3） | PASS |
| D13078 | 冒烟记：打开合成页四石可见短效果；锋利含 T2+（待恢复服） | 记 |
| D13079 | 缓冲：错链/错戳/短句漂移；禁借号抬 gem flat/开 L13/Anvil densify/soft 再叠/效果长墙 | 超额新 tip |

### 玩家口径（固定）

- 合成、镶嵌、石物上能看见这颗石 **加什么、加多少**。  
- 锋利石写明：**刃达到 T2 才计入攻击**。  
- 本窗不提高宝石数值、不改开孔、不改保底。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-gem-effect-short-honesty-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-gem-effect-short-honesty-2026-10-12.md` |
| SoT 数值 | `CoreRpg/.../p1/EmberGems.java` |
| SoT 短句 / cue | `CoreRpg/.../p1/EmberGemSeatLive.java` |
| 石物 | `plugins/NeigeItems/Items/ember-enhance-gems.yml` |
| 菜单 | `plugins/TrMenu/menus/ember_p1_gem.yml` · 可选 `ember_socket.yml` |
| 旁轨 HOLD | L13 · Anvil soft densify · 灰印/聚火 · SF4 U2U3 · 调律 live bad · 签名 lore · 套装 blurb 再叠 · soft/开孔再叠 |

---

## 4. 明确不做

- 任何 `SHARP_ATK` / `STEADY_DEF` / `DRAIN_LS` / `GALE_MS` / `CAP_*` / `SHARP_MIN_TIER` 改值  
- 效果长墙 · 持件逐孔详述 densify · 新宝石种类  
- Affix · 伤乘 · skill_mult · sx20 · ForgeChoice densify  
- SF4 U2/U3 · 灰印/聚火 · soft·开孔·Anvil densify · topUpTo  
- 套装 blurb / 签名 lore / 调律 live bad 再叠  
- 开 L13 施工 tip · K3  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [x] 文首 HOLD / 选题否决表可核  
- [x] 批后才派 **余烬-插件** + **余烬-物品**；策划本窗 **无**业务 jar  
- [x] `git show` docs-only（本 commit）；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [x] 总控批 **A · 方案 M** · 已批 · 已派插件+物品  
- 选题：TIP-BI 后 · **离开套装 blurb** · **宝石效果短诚实**（可感变强途径 · 零抬 gem flat）  
- **批后派岗：** 余烬-插件 + 余烬-物品  
- **HOLD：** L13 施工；Anvil densify；topUpTo；soft·开孔再叠；灰印·聚火；SF4 U2U3；调律 live bad 进持件；签名 lore 再叠；套装 blurb 再叠；甲闸再扫；扩 stampable；SetChase 六槽 densify；效果长墙  

## STATUS: CLOSED live `1.65.799-d13079` · feat `72cba2db4` · items `bc1e63407` · 批 A·M · Enabling+Done 5.2s · 2026-10-12 23:46 Asia/Shanghai
