# tip · TIP-BR 主线/短征解锁「进过即开」菜单诚实（MG2 · 可施工薄 · 可感变强途径）· D13192–D13207

> **状态：** **OPEN** · 荐批 **A · 方案 M** · 2026-10-13 00:33 Asia/Shanghai  
> **上游门禁：** TIP-BQ CLOSED live items `a5c3f49d7` · TrMenu reload 77 · docs `6040733da` · jar 未改 `1.65.800-d13143` · RESEARCH [`RESEARCH-ember-map-enter-unlock-cue-honesty-2026-10-13.md`](../design/RESEARCH-ember-map-enter-unlock-cue-honesty-2026-10-13.md) · DESIGN [`DESIGN-ember-map-enter-unlock-cue-honesty-2026-10-13.md`](../design/DESIGN-ember-map-enter-unlock-cue-honesty-2026-10-13.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 离开转化保留/代价 · 形缓 · 升阶保留链 → **主线/短征解锁「进过即开」菜单诚实（MG2）**（**非 densify · 非改解锁数学 · 非 Affix densify · 非伤乘 · 非抬表 · 非 soft/开孔再叠 · 非 SF4 · 非 Anvil · 非签名/套装/宝石/天赋/烙纹/副手/强化率/升阶保留/形缓/转化保留再叠 · 非效果长墙 · 非 lore>3 · 非并轨 B**）  
> **施工岗（批后派单）：** **余烬-插件**（PAPI `access_q01`/`access_q07` + 单测）；**余烬-物品**（short / adventure / challenge 灰锁条件+文案）；策划本窗 **docs-only**  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零改解锁数学（C_*_ENTER / firstCleared / mapAccessUnlocked 语义）· 零改 MARK_TICKS · 零抬 RATE/upgradeCost · 零抬 talent/gem/副手 flat · 零改 WEEKLY_CAP · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore 再叠 · 零调律 live bad 进持件 · 零套装 blurb 再叠 · 零宝石短效果再叠 · 零天赋悬停再叠 · 零烙纹档幅度再叠 · 零副手微量数字再叠 · 零强化率再叠 · 零升阶保留再叠 · 零形缓 Icon A 再叠 · 零转化保留再叠 · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 densify · 零 lore>3 · 零并轨方案 B**

---

## 0. 一句话

短征/挑战/冒险灰锁对齐「进过即开 · 清过也开」；进本资格跟 SoT，不改门闩数学。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **主线/短征解锁「进过即开」菜单诚实（MG2）** | **采纳** | 离开转化保留链；总控 prefer 主线解锁 cue；SoT 已首进∨首通、菜单仍「首通/通关」+ `q01done`/`q01_fc` 过严；**非**保留/代价半行；**零改**解锁数学 |
| 转化保留 / 形缓 Icon A / 升阶保留再叠 | **否** | 刚 CLOSED / 硬禁 |
| 成色/精工「确定成功」再压 | **否** | 弱；hope 已齐 |
| 升阶「印记另一条路」/ 形状格形缓半行 | **否** | 邻链 · prefer 再离 |
| cook 供应价 / AFK 表抬 / attune soft 再叠 | **否** | 链已齐或硬禁 |
| 签名/套装/宝石/天赋/烙纹/副手/强化率再叠 | **否** | 硬禁 |
| SF4 U2U3 / Anvil densify / soft·开孔再叠 | **否** | 硬禁 |
| ConvertCap densify / Affix densify / 伤乘 | **否** | 硬禁 |
| 灰印 / 聚火 / L13 施工 | **否** | 仍冻 / HOLD |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 效果长墙 / lore>3 | **否** | densify / 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 方案 M（定稿荐 · docs 本窗 · 批后插件+物品）

| ID | 项 | 验收 |
|----|-----|------|
| D13192 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D13193 | power-paths 进度戳：**TIP-BQ CLOSED live**；**TIP-BR OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D13194 | 文首钉死：离开转化保留/形缓/升阶保留；MG2 进过即开；**非 densify · 非改解锁数学** | 可核 |
| D13195 | 证据链：gemPath/endgame/mapAccess/challengeOpen SoT · q01done 过严 · short 首通文案 · sx14+ q01_fc · adventure/challenge 谎称 | RESEARCH §2 |
| D13196 | SoT 锁：access_* = 现网解锁布尔；禁改 C_*_ENTER/firstCleared | DESIGN §2.1 |
| D13197 | **插件：** PAPI `access_q01`/`access_q07` → 1/0 + 单测 | 可核 |
| D13198 | **物品：** short 灰锁条件→access_q01；文案「进过或首通」；sx14+ 与前段统一 | 可核 |
| D13199 | **物品：** adventure H/后图灰锁→access_*；challenge「进过或首通」 | 可核 |
| D13200 | 玩家口径固定（§2 下） | 可核 |
| D13201 | NON 核：`git show` 无解锁数学改 / soft/开孔/Anvil/SF4 / 转化·形缓·升阶·幅度再叠 / Affix densify/伤乘/skill_mult / 业务数值表 | PASS |
| D13202 | HOLD：L13；Anvil densify；topUpTo；soft/开孔再叠；灰印/聚火；U2/U3；调律 live bad；签名 lore；套装 blurb；宝石；天赋；烙纹；副手；强化率；升阶保留；形缓 Icon A；转化保留再叠；甲闸再扫；扩 stampable；SetChase 六槽；效果长墙；Affix densify/伤乘/抬表/sx20/ForgeChoice densify；**ConvertCap densify**；**改解锁数学** | 文内硬句 |
| D13203 | 批后派单句：**余烬-插件 + 余烬-物品**；策划 docs-only | 派单句 |
| D13204 | 验收：仅进过 Q01 可见短征开；文案无「通关后才开」谎；挑战无「各自首通」谎 | PASS |
| D13205 | 冒烟记：进 Q01 未通 → 短征灰锁消；进 Q07 未通 → 挑战/后图按 SoT（待恢复服） | 记 |
| D13206 | convert / skill_kit A / forge U·E / 副手·烙纹·天赋·宝石·套装·签名：**本窗不改** | 可核 |
| D13207 | 缓冲：错链/错戳/文案漂移；禁借号改解锁数学 / 开 L13 / Anvil densify / soft 再叠 / Affix densify / ConvertCap densify / 转化·形缓·升阶保留再叠 | 超额新 tip |

### 玩家口径（固定）

- 短征/挑战/终局入口：**进过对应主线本即开，清过也开**。  
- 本窗不改体力、日帽、掉率、解锁计数数学。  
- 首通奖励仍要通关才领；进本资格 ≠ 首通奖。  
- 仍守 lore≤3。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-map-enter-unlock-cue-honesty-2026-10-13.md` |
| DESIGN | `docs/design/DESIGN-ember-map-enter-unlock-cue-honesty-2026-10-13.md` |
| SoT 解锁 | `CoreRpg/.../p1/EmberSkillKit.java` `gemPathUnlocked` / `endgameUnlocked` / `mapAccessUnlocked`（**只读**） |
| SoT 挑战 | `CoreRpg/.../p1/EmberRunService.java` `challengeOpen`（**只读**） |
| PAPI 改 | `CoreRpg/.../p1/EmberRunPapi.java` GATE + 单测 |
| 菜单主改 | `plugins/TrMenu/menus/ember_p1_short.yml` · `ember_p1_adventure.yml` · `ember_p1_challenge.yml` |
| 旁轨 HOLD | L13 · Anvil soft densify · 灰印/聚火 · SF4 U2U3 · 调律 live bad · 转化保留 · 形缓 Icon A · 升阶保留 · 强化率 · 签名 lore · 套装 blurb · 宝石 · 天赋 · 烙纹 · 副手 · soft/开孔再叠 · ConvertCap densify · Affix densify · skill_mult · **改解锁数学** |

---

## 4. 明确不做

- 任何 C_*_ENTER / firstCleared / mapAccessUnlocked / gemPathUnlocked / attuneUnlocked **语义改值**  
- Affix densify · 伤乘 · sx20 · ForgeChoice densify · ConvertCap densify · 抬表 · 抬体力日帽/掉率  
- lore>3 · 效果长墙 · 发明第二套解锁系统  
- SF4 U2/U3 · 灰印/聚火 · soft·开孔·Anvil densify · topUpTo  
- 转化保留 / 形缓 Icon A / 升阶保留 / 强化率 / 天赋悬停 / 宝石短效果 / 套装 blurb / 签名 lore / 烙纹档幅度 / 副手微量再叠  
- 开 L13 施工 tip

---

## 5. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 文首 HOLD / 选题否决表可核  
- [ ] 批后才派 **余烬-插件 + 余烬-物品**；策划本窗 **无**业务 jar  
- [ ] `git show` docs-only（本 commit）；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- **批：** **A · 方案 M** · 待总控批 · 批后派余烬-插件+物品  
- **选题：** TIP-BQ 后 · **离开转化保留/代价 · 形缓 · 升阶保留** · **主线/短征解锁「进过即开」菜单诚实（MG2）**（可感变强途径 · **非 densify · 非改解锁数学** · 对位 gemPath/endgame SoT / power-paths MG2）  
- **批后派岗：** 余烬-插件 + 余烬-物品  
- **HOLD：** L13 施工；Anvil densify；topUpTo；soft·开孔再叠；灰印·聚火；SF4 U2U3；调律 live bad 进持件；签名 lore 再叠；套装 blurb 再叠；宝石短效果再叠；天赋悬停再叠；烙纹档幅度再叠；副手微量数字再叠；强化率再叠；升阶保留再叠；形缓 Icon A 再叠；转化保留再叠；甲闸再扫；扩 stampable；SetChase 六槽 densify；效果长墙；**Affix densify**；**ConvertCap densify**；**改解锁数学**  

## STATUS: OPEN · 荐批 A·M · 2026-10-13 00:33 Asia/Shanghai
