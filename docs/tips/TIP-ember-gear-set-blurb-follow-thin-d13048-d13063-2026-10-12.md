# tip · TIP-BI 装备页套装效果行跟随成套族诚实（可施工薄 · 可感变强途径）· D13048–D13063

> **状态：** APPROVED · 批 **A · 方案 M** · 已派 **余烬-插件** + **余烬-物品** · 2026-10-12 23:34 Asia/Shanghai  
> **上游门禁：** TIP-BH CLOSED live `1.65.797-d13047` · feat `ce5a4b66b` · docs `82f86182a` · RESEARCH [`RESEARCH-ember-gear-set-blurb-follow-2026-10-12.md`](../design/RESEARCH-ember-gear-set-blurb-follow-2026-10-12.md) · DESIGN [`DESIGN-ember-gear-set-blurb-follow-2026-10-12.md`](../design/DESIGN-ember-gear-set-blurb-follow-2026-10-12.md) · TIP-P 觉醒白话 live · D325 四件套 live · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 离开签名 lore 链 → **装备页套装效果行跟随成套族诚实**（**非 densify · 非伤乘 · 非签名 lore 再叠 · 非 SF4 · 非 Anvil · 非 soft/开孔**）  
> **施工岗（批后派单）：** **余烬-插件**（`familyBlurb` 修正 · `EmberRunPapi` `set_blurb` · 单测）；**余烬-物品**（`ember_p1_gear.yml` `S` 效果行接线）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零抬 SetRules/Formula/`sustain_hp_mult` · 零伤乘 · 零 Affix densify · 零抬表 · 零签名 lore 再叠 · 零调律 live bad 进持件 · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零改四件套 −3%**

---

## 0. 一句话

装备页套装格标题已跟族，效果行别再写死炽愈；跟 `familyBlurb`，并修掉 ×1.12 谎称。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **装备页套装效果行跟随成套族诚实** | **采纳** | 离开签名 lore；可感变强途径；SoT `familyBlurb` 已有；装备页写死炽愈+×1.12 谎 |
| 签名 lore 再叠 / 调律 live bad 进持件 | **否** | 硬禁 / 旁债冻 |
| SF4 U2U3 / U1 cue twin | **否** | 硬禁 / 离链 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost 再叠 | **否** | 链已齐 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 tip | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| 主线 unlock 大 DESIGN / EM1-B | **否** | 非薄 / BLOCKED |
| AFK densify / 宝石效果长墙 | **否** | 近饱和 / 另 tip |
| Affix / 伤乘 / 抬表 / skill_mult / sx20 | **否** | 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 方案 M（定稿荐 · docs 本窗 · 批后插件+物品）

| ID | 项 | 验收 |
|----|-----|------|
| D13048 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D13049 | power-paths 进度戳：**TIP-BH CLOSED live**；**TIP-BI OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D13050 | 文首钉死：离开签名 lore；套装效果行跟族；零改 SetRules/Formula | 可核 |
| D13051 | 证据链：gear `S` 写死炽愈 · familyBlurb 未接线 · sustain ×1.12 vs D293 1.00 | RESEARCH §2 |
| D13052 | SoT 锁：`familyBlurb(activeSet)`；sustain 对齐 ×1.00；禁改 coef 表 | DESIGN §2.1 |
| D13053 | **插件：** `familyBlurb("sustain")` 去 ×1.12 谎 | 代码+单测 |
| D13054 | **插件：** `EmberRunPapi` `set_blurb` → blurb 或未成套短提示 | 可核 |
| D13055 | **物品：** `ember_p1_gear.yml` `S` 效果行 → `%corerpg_p1_set_blurb%` | 可核 |
| D13056 | 保留 `%corerpg_p1_armor_set%`；零改 −3% | 可核 |
| D13057 | 玩家口径固定（§2 下） | 可核 |
| D13058 | NON 核：`git show` 无 SetRules/Formula/sustain_hp_mult/签名 lore/soft/Anvil/SF4/Affix/伤乘/skill_mult | PASS |
| D13059 | HOLD：L13；Anvil densify；topUpTo；soft/开孔再叠；灰印/聚火；U2/U3；调律 live bad；签名 lore 再叠；甲闸再扫；扩 stampable；SetChase 六槽；Affix/伤乘/抬表/sx20/ForgeChoice densify | 文内硬句 |
| D13060 | 批后派单句：**余烬-插件** + **余烬-物品**；策划 docs-only | 派单句 |
| D13061 | 单测最小包（DESIGN §2.3） | PASS |
| D13062 | 成套焚烬/烬爆打开装备页 → 效果行含本族、非炽愈主句（冒烟记待恢复服） | 记 |
| D13063 | 缓冲：错链/错戳/PAPI 键名；禁借号改 SetRules/抬表/开 L13/Anvil densify/签名 lore 再叠 | 超额新 tip |

### 玩家口径（固定）

- 装备页套装格：标题是哪套，下面效果就说那套。  
- 炽愈生命乘按现网 ×1.00，不再写 ×1.12。  
- 本窗不改套装伤害/回血系数表、不开孔、不碰签名持件 lore。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-gear-set-blurb-follow-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-gear-set-blurb-follow-2026-10-12.md` |
| SoT 文案 | `CoreRpg/.../p1/EmberItemData.java` `familyBlurb` |
| PAPI | `CoreRpg/.../p1/EmberRunPapi.java` loadout `set_blurb` |
| 菜单 | `plugins/TrMenu/menus/ember_p1_gear.yml` 图标 `S` |
| 旁轨 HOLD | L13 · Anvil soft densify · 灰印/聚火 · SF4 U2U3 · 调律 live bad · 签名 lore 再叠 · soft/开孔再叠 |

---

## 4. 明确不做

- 任何 SetRules 数组 / Formula / `sustain_hp_mult` 数值改  
- 签名 applyLore / 调律 live bad 进持件再叠  
- Affix · 伤乘 · skill_mult · sx20 · ForgeChoice densify  
- SF4 U2/U3 · 灰印/聚火 · soft·开孔·Anvil densify · topUpTo  
- 开 L13 施工 tip · 四件套 −3% 改值 · K3  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [x] 文首 HOLD / 选题否决表可核  
- [ ] 批后才派 **余烬-插件** + **余烬-物品**；策划本窗 **无**业务 jar  
- [x] `git show` docs-only（本 commit）；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- [x] 总控批 **A · 方案 M** · 已批 · 已派插件+物品  
- 选题：TIP-BH 后 · **离开签名 lore** · **装备页套装效果行跟成套族诚实**（可感变强途径 · 零改套装系数）  
- **批后派岗：** 余烬-插件 + 余烬-物品  
- **HOLD：** L13 施工；Anvil densify；topUpTo；soft·开孔再叠；灰印·聚火；SF4 U2U3；调律 live bad 进持件；签名 lore 再叠；甲闸再扫；扩 stampable；SetChase 六槽 densify  

## STATUS: APPROVED · 批 A·M · 已派余烬-插件+余烬-物品 · 2026-10-12 23:34 Asia/Shanghai
