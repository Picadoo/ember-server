# tip · TIP-BO 升阶预览「确定·保留」菜单诚实（工坊 TrMenu · 可施工薄 · 可感变强途径）· D13144–D13159

> **状态：** APPROVED · 批 **A · 方案 M** · 已派 **余烬-物品** · **不派插件** · 2026-10-13 00:18 Asia/Shanghai  
> **上游门禁：** TIP-BN CLOSED live `1.65.800-d13143` · feat `059891b38` · items `284e5dc7c` · TrMenu reload 77 · docs `2e0dd355e` · RESEARCH [`RESEARCH-ember-upgrade-preview-keep-honesty-2026-10-13.md`](../design/RESEARCH-ember-upgrade-preview-keep-honesty-2026-10-13.md) · DESIGN [`DESIGN-ember-upgrade-preview-keep-honesty-2026-10-13.md`](../design/DESIGN-ember-upgrade-preview-keep-honesty-2026-10-13.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 离开强化预览成功率链 → **升阶预览「确定·保留」菜单诚实**（**非抬 upgradeCost · 非 Affix densify · 非伤乘 · 非抬表 · 非 soft/开孔再叠 · 非 SF4 · 非 Anvil · 非签名/套装/宝石/天赋/烙纹/副手/强化率再叠 · 非效果长墙 · 非 lore>3 · 非并轨 B**）  
> **施工岗（批后派单）：** **余烬-物品**（`ember_p1_forge` Icon `U`/`u` lore）；**不派插件**（零新 PAPI · 零 jar）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零抬 RATE/MAX_TRIES/cost · 零抬 upgradeCost/C04 · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore 再叠 · 零调律 live bad 进持件 · 零套装 blurb 再叠 · 零宝石短效果再叠 · 零天赋悬停再叠 · 零烙纹档幅度再叠 · 零副手微量数字再叠 · 零强化率再叠 · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 densify · 零 lore>3 · 零并轨方案 B**

---

## 0. 一句话

升阶别只见花费；预览/确认前写出确定成功且保留强化/成色/精工。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **升阶预览「确定·保留」菜单诚实** | **采纳** | 离开强化率；BN 旁债 #9；聊天有保留菜单无；成色/精工已齐；**非幅度半行**；**非抬价** |
| 强化预览成功率再叠 | **否** | 刚 CLOSED / 硬禁 |
| 成色/精工再压 WS2 | **否** | 已齐；弱于升阶缺口 |
| 签名/套装/宝石/天赋/烙纹/副手幅度再叠 | **否** | 刚 CLOSED / 硬禁 |
| pin 预览气泡 | **否** | 烙纹链 · prefer 再离 |
| SF4 U1 菜单形缓 / U2U3 | **否** | 离接线链 / 硬禁 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole 再叠 | **否** | 链已齐 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 tip | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| Affix densify / 抬 RATE / 抬 upgradeCost | **否** | 硬禁 |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 效果长墙 / lore>3 | **否** | densify / 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 方案 M（定稿荐 · docs 本窗 · 批后物品）

| ID | 项 | 验收 |
|----|-----|------|
| D13144 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D13145 | power-paths 进度戳：**TIP-BN CLOSED live**；**TIP-BO OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D13146 | 文首钉死：离开强化率；升阶确定保留；**非抬 upgradeCost**；零新 PAPI | 可核 |
| D13147 | 证据链：Plan.note 保留 · upgradeLine 已有 T→T · 菜单 U/u 无保留/无点预览 · BN 旁债 #9 | RESEARCH §2 |
| D13148 | SoT 锁：菜单保留半行 = Plan.note 短写；禁改 C04/upgradeCost/firstClear | DESIGN §2.1 |
| D13149 | **物品：** forge `U`/`u` lore → 本次 / 确定·保留 / 点预览\|确认；≤3 | 可核 |
| D13150 | 缺料：允许第二行尾挂 `held_upgrade_lack`（空则无字）；甲灰显本窗可不改 | 可核 |
| D13151 | 玩家口径固定（§2 下） | 可核 |
| D13152 | NON 核：`git show` 无 upgradeCost/C04/RATE 改 / soft/开孔/Anvil/SF4/签名/套装/宝石/天赋/烙纹/副手/强化率再叠 / Affix densify/伤乘/skill_mult / CoreRpg 业务 jar | PASS |
| D13153 | HOLD：L13；Anvil densify；topUpTo；soft/开孔再叠；灰印/聚火；U2/U3；调律 live bad；签名 lore；套装 blurb；宝石短效果；天赋悬停；烙纹档幅度；副手微量；强化率再叠；甲闸再扫；扩 stampable；SetChase 六槽；效果长墙；Affix densify/伤乘/抬表/sx20/ForgeChoice densify；抬 upgradeCost | 文内硬句 |
| D13154 | 批后派单句：**余烬-物品**；**不派插件**；策划 docs-only | 派单句 |
| D13155 | 验收：预览/确认可见确定·保留；有点预览/确认 cue；lore≤3 | PASS |
| D13156 | 冒烟记：手持可升阶 T1 件预览见「确定 · 保留强化/成色/精工」与 T1→T2 花费（待恢复服） | 记 |
| D13157 | 强化 E/e / 成色 / 精工 / 稳赌 / 旁路：**本窗不改** | 可核 |
| D13158 | 印记「另一条路」长文：**本窗不进菜单**（旁债） | 可核 |
| D13159 | 缓冲：错链/错戳/文案漂移；禁借号抬 upgradeCost / 开 L13 / Anvil densify / soft 再叠 / Affix densify / 效果长墙 / 强化率再叠 | 超额新 tip |

### 玩家口径（固定）

- 点升阶预览/确认前能看见 **确定成功且保留强化/成色/精工**，以及本次档位与花费。  
- 本窗不改升阶材料价、不改首通门禁、不改日首减负。  
- 仍守 lore≤3。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-upgrade-preview-keep-honesty-2026-10-13.md` |
| DESIGN | `docs/design/DESIGN-ember-upgrade-preview-keep-honesty-2026-10-13.md` |
| SoT 保留 | `CoreRpg/.../p1/EmberUpgradeRules.java` `upgrade()` Plan.note（**只读**） |
| SoT 花费行 | `CoreRpg/.../p1/EmberGearNextHint.java` `upgradeLine`（**只读**） |
| PAPI | `held_upgrade_cost` / `held_upgrade_lack`（**既有 · 不改**） |
| 菜单 | `plugins/TrMenu/menus/ember_p1_forge.yml` Icons `U`/`u` |
| 旁轨 HOLD | L13 · Anvil soft densify · 灰印/聚火 · SF4 U2U3 · 调律 live bad · 签名 lore · 套装 blurb · 宝石短效果 · 天赋悬停 · 烙纹档幅度 · 副手微量 · 强化率再叠 · soft/开孔再叠 · Affix densify · 抬 upgradeCost |

---

## 4. 明确不做

- 任何 `upgradeCost` / C04 / firstClear / UpgradeCostSoft MULT / RATE / MAX_TRIES / enhance cost 改值  
- Affix densify · 伤乘 · sx20 · ForgeChoice densify · 抬表  
- lore>3 · 效果长墙 · 印记另一条路长文进菜单  
- SF4 U2/U3 · 灰印/聚火 · soft·开孔·Anvil densify · topUpTo  
- 强化率 / 天赋悬停 / 宝石短效果 / 套装 blurb / 签名 lore / 烙纹档幅度 / 副手微量再叠  
- 开 L13 施工 tip · 成色/精工本窗再叠 · 新 PAPI

---

## 5. 验收清单

- [x] 总控批 **A · 方案 M**  
- [x] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [x] 文首 HOLD / 选题否决表可核  
- [ ] 批后才派 **余烬-物品**；**不派插件**；策划本窗 **无**业务 jar  
- [x] `git show` docs-only（本 commit）；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- **批：** **A · 方案 M** · 已批 · 已派余烬-物品 · 不派插件  
- **选题：** TIP-BN 后 · **离开强化预览成功率** · **升阶预览「确定·保留」菜单诚实**（可感变强途径 · **非抬 upgradeCost** · 对位 BN 旁债 #9 / WS2）  
- **批后派岗：** 余烬-物品；不派插件  
- **HOLD：** L13 施工；Anvil densify；topUpTo；soft·开孔再叠；灰印·聚火；SF4 U2U3；调律 live bad 进持件；签名 lore 再叠；套装 blurb 再叠；宝石短效果再叠；天赋悬停再叠；烙纹档幅度再叠；副手微量数字再叠；强化率再叠；甲闸再扫；扩 stampable；SetChase 六槽 densify；效果长墙；**Affix densify**；**抬 upgradeCost**  

## STATUS: APPROVED · 批 A·M · 已派余烬-物品 · 不派插件 · 2026-10-13 00:18 Asia/Shanghai
