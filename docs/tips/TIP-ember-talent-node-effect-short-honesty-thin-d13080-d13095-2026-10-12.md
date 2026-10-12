# tip · TIP-BK 天赋节点效果短诚实（TrMenu 悬停 · 可施工薄 · 可感变强途径）· D13080–D13095

> **状态：** **CLOSED live** items `4c2c38485` · 批 A·M · TrMenu reload 77 · jar 未改 `1.65.799-d13079` · 2026-10-12 23:52 Asia/Shanghai  
> **上游门禁：** TIP-BJ CLOSED live `1.65.799-d13079` · feat `72cba2db4` · items `bc1e63407` · docs `05a2676fd` · RESEARCH [`RESEARCH-ember-talent-node-effect-short-honesty-2026-10-12.md`](../design/RESEARCH-ember-talent-node-effect-short-honesty-2026-10-12.md) · DESIGN [`DESIGN-ember-talent-node-effect-short-honesty-2026-10-12.md`](../design/DESIGN-ember-talent-node-effect-short-honesty-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 离开宝石短效果链 → **天赋节点效果短诚实**（**非 densify · 非抬 talent stats · 非 soft/开孔再叠 · 非 SF4 · 非 Anvil · 非签名/套装/宝石再叠 · 非效果长墙 · 非 lore>3**）  
> **施工岗（批后派单）：** **余烬-物品**（`ember_talent.yml` 13 格消耗行短效果）；**不派插件**（零 jar · 禁改 `talent.yml`）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零抬 talent.yml stats/cost/requires · 零 skill_mult · 零 Affix densify · 零伤乘 · 零抬表 · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore 再叠 · 零调律 live bad 进持件 · 零套装 blurb 再叠 · 零宝石短效果再叠 · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零开 L13 施工 tip · 零效果长墙 densify · 零 lore>3 · 零一层中段节点塞菜单**

---

## 0. 一句话

天赋菜单悬停别只写「消耗」；用 `talent.yml` 写回短效果（合进消耗行，守 lore≤3）。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **天赋节点效果短诚实（TrMenu）** | **采纳** | 离开宝石链；D12200 误删 B2.15 效果；SoT 已有；可感变强途径 |
| 宝石短效果 / 持件孔位长墙再叠 | **否** | 刚 CLOSED / 硬禁 |
| 套装 blurb / 签名 lore 再叠 | **否** | 刚 CLOSED / 硬禁 |
| SF4 U2U3 / U1 cue twin | **否** | 硬禁 / 离链 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole 再叠 | **否** | 链已齐 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 tip | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| 烙纹定向幅度诚实 | **否（本窗）** | Affix 旁近；另 tip |
| 一层中段进 TrMenu / 效果长墙 | **否** | densify / 硬禁 |
| 抬 talent stats / skill_mult | **否** | 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 方案 M（定稿荐 · docs 本窗 · 批后物品）

| ID | 项 | 验收 |
|----|-----|------|
| D13080 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D13081 | power-paths 进度戳：**TIP-BJ CLOSED live**；**TIP-BK OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D13082 | 文首钉死：离开宝石短效果；天赋短效果；零抬 talent stats；lore≤3 | 可核 |
| D13083 | 证据链：talent.yml SoT · B2.15 人话 · D12200 删效果 · 现网无效果行 | RESEARCH §2 |
| D13084 | SoT 锁：短效果由 talent.yml stats/skill_id 格式化；禁改数值 | DESIGN §2.1–2.2 |
| D13085 | **物品：** `ember_talent.yml` 根三 + 烬斩 + 二层九 = 13 格消耗行 | 可核 |
| D13086 | 合行模板：`§7{层} · 消耗 N · {短效果}`；前置/点击保留；每格 ≤3 | 可核 |
| D13087 | 不扩一层中段节点进菜单；V/U/Z/5/B/j 不动 | 可核 |
| D13088 | 玩家口径固定（§2 下） | 可核 |
| D13089 | NON 核：`git show` 无 talent.yml stats/cost 改 / soft/开孔/Anvil/SF4/签名/套装/宝石再叠 / Affix/伤乘/skill_mult / CoreRpg 业务 jar | PASS |
| D13090 | HOLD：L13；Anvil densify；topUpTo；soft/开孔再叠；灰印/聚火；U2/U3；调律 live bad；签名 lore；套装 blurb；宝石短效果再叠；甲闸再扫；扩 stampable；SetChase 六槽；效果长墙；Affix/伤乘/抬表/sx20/ForgeChoice densify | 文内硬句 |
| D13091 | 批后派单句：**余烬-物品**；**不派插件**；策划 docs-only | 派单句 |
| D13092 | 验收：13 格短效果可见；无裸键 `phys_damage` 等；解锁格 lore≤3 | PASS |
| D13093 | 冒烟记：打开天赋页悬停余烬脉见「物攻 +1」；根燃锋见「物攻 +1」（待恢复服） | 记 |
| D13094 | B2.15 口径复用（物攻/暴伤/暴击率/移速/攻速/击中减速/受伤/生命/物防） | 可核 |
| D13095 | 缓冲：错链/错戳/短句漂移/lore>3；禁借号抬 talent stats / 开 L13 / Anvil densify / soft 再叠 / 效果长墙 / 一层中段塞菜单 | 超额新 tip |

### 玩家口径（固定）

- 天赋菜单悬停能看见这格 **加什么、加多少**（或解锁哪招）。  
- 本窗不提高天赋数值、不改点数/洗点、不改开孔/宝石。  
- 仍守 lore≤3；一层中段节点不借本窗塞进菜单。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-talent-node-effect-short-honesty-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-talent-node-effect-short-honesty-2026-10-12.md` |
| SoT 数值 | `CoreRpg/src/main/resources/talent.yml`（**只读**） |
| 菜单 | `plugins/TrMenu/menus/ember_talent.yml` |
| 口径上游 | `docs/design/design-ember-talent-attr-copy.md`（B2.15） |
| 旁轨 HOLD | L13 · Anvil soft densify · 灰印/聚火 · SF4 U2U3 · 调律 live bad · 签名 lore · 套装 blurb · 宝石短效果再叠 · soft/开孔再叠 |

---

## 4. 明确不做

- 任何 `talent.yml` stats / cost / requires / unlock command 实参改值  
- lore>3 · 效果长墙 · 一层中段节点进 TrMenu densify  
- Affix · 伤乘 · skill_mult · sx20 · ForgeChoice densify  
- SF4 U2/U3 · 灰印/聚火 · soft·开孔·Anvil densify · topUpTo  
- 套装 blurb / 签名 lore / 宝石短效果 / 调律 live bad 再叠  
- 开 L13 施工 tip · 派 CoreRpg 业务 jar · K3  

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [x] 文首 HOLD / 选题否决表可核  
- [x] 批后才派 **余烬-物品**；**不派插件**；策划本窗 **无**业务 jar  
- [x] `git show` docs-only（本 commit）；L13 施工仍 HOLD  

---

## 6. 批注（总控填）

- **批：** **A · 方案 M** · 已批 · 已派余烬-物品 · 不派插件  
- **批注：** 离开宝石短效果 · 压回 D12200 误删 · talent.yml SoT 合进消耗行 · lore≤3 · 零抬 talent stats  
- **HOLD：** L13 施工；Anvil densify；topUpTo；soft·开孔再叠；灰印·聚火；SF4 U2U3；调律 live bad 进持件；签名 lore 再叠；套装 blurb 再叠；宝石短效果再叠；甲闸再扫；扩 stampable；SetChase 六槽 densify；效果长墙；一层中段塞菜单  

## STATUS: CLOSED live items `4c2c38485` · 批 A·M · TrMenu reload 77 · jar 未改 · 2026-10-12 23:52 Asia/Shanghai
