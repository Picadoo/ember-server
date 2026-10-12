# RESEARCH · 烙纹定向幅度菜单诚实（TrMenu 类型格 · 离开天赋悬停）· 2026-10-12

> **状态：** APPROVED · TIP-BL · 已派余烬-物品 · 不派插件 · 2026-10-12 23:56 Asia/Shanghai  
> **起因：** TIP-BK CLOSED live 后总控「离开天赋悬停链 · 开下一可感变强途径」。BK DESIGN 旁债 #8 已点名「烙纹定向幅度菜单诚实」。扫 AH…BK / power-paths：`ember-v1-growth.yml` affix **values SoT 已有**；烙纹页五格只写定性「对词缀精英伤害」等，**无档位幅度区间**——点类型前不知变强多少。  
> **上游：** TIP-BK CLOSED live items `4c2c38485` · TrMenu reload 77 · docs `4f0794c15` · jar 未改 `1.65.799-d13079` · `ember-v1-growth.yml` affix defs · `ember_p1_brand.yml` · `EmberAffix.Def.text` / `signedPct` · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify（禁改 values/weights/pool/pity/rollable）· 零伤乘 · 零抬表 · 零抬 talent/gem flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore 再叠 · 零调律 live bad 进持件 · 零套装 blurb 再叠 · 零宝石短效果再叠 · 零天赋悬停再叠 · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 densify · 零 lore>3 · 零一层中段塞菜单**

---

## 0. 一句话

烙纹定向菜单五类型格用 **既有 affix `values[]`** 写 **≤1 行档位幅度**（`signedPct` 口径）；不改词缀表、不抬功率。

---

## 1. 选题扫描（离开天赋悬停 + soft/开孔 + SF4 + 签名/套装/宝石）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **烙纹定向幅度菜单诚实** | **采纳** | BK 旁债 #8；SoT values 已有未上菜单；点前不知幅度；**非 Affix densify**（只展示）；离开天赋链 |
| 天赋悬停 / 宝石短效果 / 套装 blurb / 签名 lore 再叠 | **否** | 刚 CLOSED / 硬禁离链 |
| SF4 U2U3 / U1 cue twin | **否** | 硬禁 / 离链 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole 再叠 | **否** | 链已齐 / 硬禁 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| Affix densify（改 values/权重/池） | **否** | 硬禁；本窗零改表 |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 一层中段 / 效果长墙 / lore>3 | **否** | densify / 硬禁 |
| 副手「微量」守势数字诚实 | **否（本窗）** | 另 tip；本窗优先 BK 旁债 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | affix defs 含 `values:` 四档（如 `b_affix` `[1.04,1.08,1.12,1.16]`） | `ember-v1-growth.yml` · `plugins/CoreRpg/ember-v1-growth.yml` | **战斗 SoT 已有** |
| E2 | `EmberAffix.Def.text(tier)` = desc + `signedPct(v)` + note | `EmberAffix.java` | **展示口径已有** |
| E3 | `EmberGrowth.signedPct`：1.04→`+4%` · 1.0025→`+0.25%` · 0.96→`-4%` | `EmberGrowth.java` | **幅度字算法已有** |
| E4 | 烙纹菜单五格 lore 仅定性（「对词缀精英伤害」等）+「点击预览」 | `ember_p1_brand.yml` 1–5 | **点前无幅度** |
| E5 | pin 预览聊天才写类型名；「档位仍随机，成色上限截断」——**仍无区间** | `EmberGrowthService.brandPin` | **预览也不报范围** |
| E6 | `pctList(Def)` 已能列出四档 signedPct | 同文件 private | **可复用；本窗菜单可静态对齐 SoT** |
| E7 | 改 `values` / weights / pool = Affix densify | 总控硬禁 | **诚实 ≠ densify** |
| E8 | TIP-AT/AU 等已齐 CostSoft / Hope；本窗不叠 soft | 链 CLOSED | **零 soft 再叠** |

### SoT 四档（只读 · 禁改）

| id | 名 | 定性 | signedPct 四档（低→高） |
|----|----|------|-------------------------|
| `b_affix` | 猎缀纹 | 对词缀精英伤害 | +4% · +8% · +12% · +16%（仅主线重打） |
| `b_set` | 余烬纹 | 套装事件伤害 | +0.25% · +0.5% · +0.75% · +1% |
| `c_tele` | 定身纹 | 受预警招伤害 | −0.5% · −1% · −1.5% · −2% |
| `c_share` | 分核纹 | 烬核自己那份 | −1% · −2% · −3% · −4% |
| `c_affix` | 抗缀纹 | 受词缀精英伤害 | −4% · −8% · −12% · −16%（仅主线重打） |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **定稿短幅度半行**：按上表 `signedPct`；合进定性行或独立一行；每格 lore≤3。  
2. **施工岗**：批后 **余烬-物品**（只改 `ember_p1_brand.yml` 五类型格 ± 说明格半行）；**不派插件**（零 jar · 禁改 growth affix values/weights）。  
3. **可选旁注（非本窗必做）：** pin 预览聊天气泡补「档区间」——另 tip，防本窗扩插件面。  
4. **零改** affix 表 · soft/开孔 · Anvil · SF4 · 天赋/宝石/签名/套装展示再叠。

---

## 4. 明确不做

- 改 `ember-v1-growth.yml` affix `values` / `tier_weights` / `pity` / `rollable` / pool  
- Affix densify · 伤乘 · 抬表 · skill_mult · sx20  
- soft·开孔·Anvil densify · SF4 U2U3 · 灰印/聚火 · L13 施工  
- 天赋悬停 / 宝石短效果 / 套装 blurb / 签名 lore / 调律 live bad 再叠  
- 效果长墙 · lore>3 · 一层中段塞菜单  

---

## 5. 对照

| 成熟做法 | 余烬本窗 |
|----------|----------|
| PoE 通货悬停写 mod 幅度区间 | 烙纹类型格写四档区间 |
| D3 传奇悬停写数值范围 | 合进定性行 · 守 lore≤3 |
| 禁止「有伤害」无数字糊弄选择 | 禁无幅度定性-only |

