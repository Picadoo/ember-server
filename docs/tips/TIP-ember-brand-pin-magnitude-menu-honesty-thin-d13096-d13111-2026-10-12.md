# tip · TIP-BL 烙纹定向幅度菜单诚实（TrMenu 类型格 · 可施工薄 · 可感变强途径）· D13096–D13111

> **状态：** **OPEN** · 荐批 **A · 方案 M** · 2026-10-12 23:54 Asia/Shanghai  
> **上游门禁：** TIP-BK CLOSED live items `4c2c38485` · TrMenu reload 77 · docs `4f0794c15` · jar 未改 `1.65.799-d13079` · RESEARCH [`RESEARCH-ember-brand-pin-magnitude-menu-honesty-2026-10-12.md`](../design/RESEARCH-ember-brand-pin-magnitude-menu-honesty-2026-10-12.md) · DESIGN [`DESIGN-ember-brand-pin-magnitude-menu-honesty-2026-10-12.md`](../design/DESIGN-ember-brand-pin-magnitude-menu-honesty-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 离开天赋悬停链 → **烙纹定向幅度菜单诚实**（**非 Affix densify · 非伤乘 · 非抬表 · 非 soft/开孔再叠 · 非 SF4 · 非 Anvil · 非签名/套装/宝石/天赋再叠 · 非效果长墙 · 非 lore>3**）  
> **施工岗（批后派单）：** **余烬-物品**（`ember_p1_brand.yml` 五类型格 ± 说明半行）；**不派插件**（零 jar · 禁改 growth affix values/weights）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify（禁改 values/weights/pool/pity/rollable）· 零伤乘 · 零抬表 · 零抬 talent/gem flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore 再叠 · 零调律 live bad 进持件 · 零套装 blurb 再叠 · 零宝石短效果再叠 · 零天赋悬停再叠 · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 densify · 零 lore>3 · 零一层中段塞菜单**

---

## 0. 一句话

烙纹定向别只写「有伤害」；用现网四档 values 写出幅度区间，点前就知道大概加多少。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **烙纹定向幅度菜单诚实** | **采纳** | 离开天赋悬停；BK 旁债 #8；SoT values 已有；可感变强途径；**非 Affix densify** |
| 天赋悬停 / 宝石短效果 / 套装 blurb / 签名 lore 再叠 | **否** | 刚 CLOSED / 硬禁 |
| SF4 U2U3 / U1 cue twin | **否** | 硬禁 / 离链 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole 再叠 | **否** | 链已齐 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 tip | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| Affix densify（改 values/权重/池） | **否** | 硬禁 |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 副手「微量」数字诚实 | **否（本窗）** | 另 tip |
| pin 预览气泡补区间 | **否（本窗）** | 另 tip；本窗只菜单 |
| 效果长墙 / lore>3 / 一层中段塞菜单 | **否** | densify / 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 方案 M（定稿荐 · docs 本窗 · 批后物品）

| ID | 项 | 验收 |
|----|-----|------|
| D13096 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D13097 | power-paths 进度戳：**TIP-BK CLOSED live**；**TIP-BL OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D13098 | 文首钉死：离开天赋悬停；烙纹幅度菜单；**非 Affix densify**；零改 values | 可核 |
| D13099 | 证据链：growth values · menu 定性-only · signedPct · pctList 已有未上菜单 | RESEARCH §2 |
| D13100 | SoT 锁：五格幅度由 values→signedPct 定稿；禁改 growth affix 表 | DESIGN §2.1 |
| D13101 | **物品：** `ember_p1_brand.yml` Icons `1`–`5` lore → 定稿短幅度 | 可核 |
| D13102 | 合行模板：`§7{定性} · 档 {低}～{高}（注）` + 点击行；每格 ≤3 | 可核 |
| D13103 | 可选 `N` 说明半行：「档位仍随机 · 成色上限截断 · 本窗不改表」 | 可核 |
| D13104 | 猎缀/抗缀保留「仅主线重打」；减伤格用负号幅度 | 可核 |
| D13105 | 玩家口径固定（§2 下） | 可核 |
| D13106 | NON 核：`git show` 无 growth affix values/weights/pity/rollable 改 / soft/开孔/Anvil/SF4/签名/套装/宝石/天赋再叠 / Affix densify/伤乘/skill_mult / CoreRpg 业务 jar | PASS |
| D13107 | HOLD：L13；Anvil densify；topUpTo；soft/开孔再叠；灰印/聚火；U2/U3；调律 live bad；签名 lore；套装 blurb；宝石短效果；天赋悬停再叠；甲闸再扫；扩 stampable；SetChase 六槽；效果长墙；Affix densify/伤乘/抬表/sx20/ForgeChoice densify | 文内硬句 |
| D13108 | 批后派单句：**余烬-物品**；**不派插件**；策划 docs-only | 派单句 |
| D13109 | 验收：五格可见档区间；无「微量」糊弄；lore≤3 | PASS |
| D13110 | 冒烟记：打开烙纹页悬停猎缀见 +4%～+16%；余烬纹见 +0.25%～+1%（待恢复服） | 记 |
| D13111 | 缓冲：错链/错戳/幅度漂移；禁借号改 values / 开 L13 / Anvil densify / soft 再叠 / Affix densify / 效果长墙 | 超额新 tip |

### 玩家口径（固定）

- 烙纹定向页点类型前能看见 **档位大概加/减多少**。  
- 本窗不改词缀数值、不改花费/次数、不改开孔/宝石。  
- 档位仍随机、成色截断；守 lore≤3。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-brand-pin-magnitude-menu-honesty-2026-10-12.md` |
| DESIGN | `docs/design/DESIGN-ember-brand-pin-magnitude-menu-honesty-2026-10-12.md` |
| SoT 数值 | `CoreRpg/src/main/resources/ember-v1-growth.yml`（affix · **只读**）· runtime `plugins/CoreRpg/ember-v1-growth.yml` |
| 展示算法 | `EmberAffix.Def.text` · `EmberGrowth.signedPct`（只读对照） |
| 菜单 | `plugins/TrMenu/menus/ember_p1_brand.yml` |
| 旁轨 HOLD | L13 · Anvil soft densify · 灰印/聚火 · SF4 U2U3 · 调律 live bad · 签名 lore · 套装 blurb · 宝石短效果 · 天赋悬停再叠 · soft/开孔再叠 · Affix densify |

---

## 4. 明确不做

- 任何 affix `values` / `tier_weights` / `pity` / `rollable` / pool 改值  
- Affix densify · 伤乘 · skill_mult · sx20 · ForgeChoice densify  
- lore>3 · 效果长墙 · 一层中段塞菜单  
- SF4 U2/U3 · 灰印/聚火 · soft·开孔·Anvil densify · topUpTo  
- 天赋悬停 / 宝石短效果 / 套装 blurb / 签名 lore / 调律 live bad 再叠  
- 开 L13 施工 tip · 派 CoreRpg 业务 jar · K3 · pin 预览气泡（另 tip）

---

## 5. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 文首 HOLD / 选题否决表可核  
- [ ] 批后才派 **余烬-物品**；**不派插件**；策划本窗 **无**业务 jar  
- [ ] `git show` docs-only（本 commit）；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- **荐批：** **A · 方案 M**  
- **选题：** TIP-BK 后 · **离开天赋悬停** · **烙纹定向幅度菜单诚实**（可感变强途径 · **非 Affix densify** · 零改 values）  
- **批后派岗：** 余烬-物品；**不派插件**  
- **HOLD：** L13 施工；Anvil densify；topUpTo；soft·开孔再叠；灰印·聚火；SF4 U2U3；调律 live bad 进持件；签名 lore 再叠；套装 blurb 再叠；宝石短效果再叠；天赋悬停再叠；甲闸再扫；扩 stampable；SetChase 六槽 densify；效果长墙；**Affix densify**；一层中段塞菜单  

## STATUS: OPEN · 荐批 A·M · 2026-10-12 23:54 Asia/Shanghai
