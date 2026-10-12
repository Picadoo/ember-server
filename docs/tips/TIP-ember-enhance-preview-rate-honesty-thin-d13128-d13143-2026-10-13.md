# tip · TIP-BN 强化预览成功率菜单诚实（工坊 TrMenu · 可施工薄 · 可感变强途径）· D13128–D13143

> **状态：** **OPEN** · 荐批 **A · 方案 M** · 2026-10-13 00:05 Asia/Shanghai  
> **上游门禁：** TIP-BM CLOSED live items `91300ad65` · TrMenu reload 77 · docs `cfbfacadb` · jar 未改 `1.65.799-d13079` · RESEARCH [`RESEARCH-ember-enhance-preview-rate-honesty-2026-10-13.md`](../design/RESEARCH-ember-enhance-preview-rate-honesty-2026-10-13.md) · DESIGN [`DESIGN-ember-enhance-preview-rate-honesty-2026-10-13.md`](../design/DESIGN-ember-enhance-preview-rate-honesty-2026-10-13.md) · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **选题：** 离开副手购/炼数字链 → **强化预览成功率菜单诚实**（**非抬 RATE · 非 Affix densify · 非伤乘 · 非抬表 · 非 soft/开孔再叠 · 非 SF4 · 非 Anvil · 非签名/套装/宝石/天赋/烙纹/副手幅度再叠 · 非效果长墙 · 非 lore>3 · 非并轨 B**）  
> **施工岗（批后派单）：** **余烬-插件**（`held_enhance_rate` PAPI · `EmberRunPapi` + 单测；零改 RATE/MAX_TRIES/cost）+ **余烬-物品**（`ember_p1_forge` Icon `E`/`e` lore）  
> **策划本窗：** docs-only commit（零业务 jar）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零抬 RATE/MAX_TRIES/cost · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore 再叠 · 零调律 live bad 进持件 · 零套装 blurb 再叠 · 零宝石短效果再叠 · 零天赋悬停再叠 · 零烙纹档幅度再叠 · 零副手微量数字再叠 · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 densify · 零 lore>3 · 零并轨方案 B**

---

## 0. 一句话

强化别只见花费和保底；预览/确认前用现网 RATE 写出下一档成功率。

---

## 1. 选题裁决

| 候选 | 裁决 | 理由 |
|------|------|------|
| **强化预览成功率菜单诚实** | **采纳** | 离开副手数字；WS2 旁债；聊天有率菜单无；**非抬 RATE**；离开幅度半行叠层 |
| 副手微量 / 烙纹档幅度 / 天赋悬停 / 宝石短效果 / 套装 blurb / 签名 lore 再叠 | **否** | 刚 CLOSED / 硬禁 |
| pin 预览气泡 | **否** | 烙纹链 · prefer 再离 |
| SF4 U1 菜单形缓 / U2U3 | **否** | 离接线链 / 硬禁 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole 再叠 | **否** | 链已齐 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 tip | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| Affix densify / 抬 RATE / 抬 talent/gem flat | **否** | 硬禁 |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 效果长墙 / lore>3 | **否** | densify / 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 方案 M（定稿荐 · docs 本窗 · 批后插件+物品）

| ID | 项 | 验收 |
|----|-----|------|
| D13128 | 本 tip + RESEARCH + DESIGN（上文链） | 路径可点 |
| D13129 | power-paths 进度戳：**TIP-BM CLOSED live**；**TIP-BN OPEN**；旁注 **L13 施工 HOLD** | 戳可见 |
| D13130 | 文首钉死：离开副手数字；强化预览率；**非抬 RATE**；零改 cost/pity 数学 | 可核 |
| D13131 | 证据链：RATE[] · 聊天预览有率 · 菜单 E/e 无率 · 无 rate PAPI · WS2 | RESEARCH §2 |
| D13132 | SoT 锁：菜单率 = `round(RATE[t]*100)`；必成口径对齐聊天；禁改 RATE/MAX_TRIES/cost | DESIGN §2.1 |
| D13133 | **插件：** `held_enhance_rate` → `N%` / `必成` / 空位；登记 + 单测 | 可核 |
| D13134 | **物品：** forge `E`/`e` lore → 花费 / 成功率+保底 / 点预览|确认；≤3 | 可核 |
| D13135 | 甲灰显子 icon 本窗可不加率；勿第四行 | 可核 |
| D13136 | 玩家口径固定（§2 下） | 可核 |
| D13137 | NON 核：`git show` 无 RATE/MAX_TRIES/cost 改 / soft/开孔/Anvil/SF4/签名/套装/宝石/天赋/烙纹/副手幅度再叠 / Affix densify/伤乘/skill_mult | PASS |
| D13138 | HOLD：L13；Anvil densify；topUpTo；soft/开孔再叠；灰印/聚火；U2/U3；调律 live bad；签名 lore；套装 blurb；宝石短效果；天赋悬停；烙纹档幅度；副手微量数字再叠；甲闸再扫；扩 stampable；SetChase 六槽；效果长墙；Affix densify/伤乘/抬表/sx20/ForgeChoice densify；抬 RATE | 文内硬句 |
| D13139 | 批后派单句：**余烬-插件** + **余烬-物品**；策划 docs-only | 派单句 |
| D13140 | 验收：预览/确认可见成功率或必成；与聊天同档；lore≤3 | PASS |
| D13141 | 冒烟记：手持 +4 件预览见成功率 70%；保底将成见必成（待恢复服） | 记 |
| D13142 | afk / 成色 / 精工 / 升阶 / 稳赌：**本窗不改** | 可核 |
| D13143 | 缓冲：错链/错戳/率漂移；禁借号抬 RATE / 开 L13 / Anvil densify / soft 再叠 / Affix densify / 效果长墙 | 超额新 tip |

### 玩家口径（固定）

- 点强化预览/确认前能看见 **下一档成功率**（或本次必成）与保底。  
- 本窗不改成功率表、不改花费、不改保底次数。  
- 仍守 lore≤3。

---

## 3. 对接文件（真 ID · 禁发明表）

| 层 | 路径 |
|----|------|
| RESEARCH | `docs/design/RESEARCH-ember-enhance-preview-rate-honesty-2026-10-13.md` |
| DESIGN | `docs/design/DESIGN-ember-enhance-preview-rate-honesty-2026-10-13.md` |
| SoT 率 | `CoreRpg/.../p1/EmberUpgradeRules.java` `RATE` / `MAX_TRIES`（**只读**） |
| 聊天对照 | `CoreRpg/.../p1/EmberForgeService.java` enhance 预览 |
| PAPI | `CoreRpg/.../p1/EmberRunPapi.java` |
| 菜单 | `plugins/TrMenu/menus/ember_p1_forge.yml` Icons `E`/`e` |
| 旁轨 HOLD | L13 · Anvil soft densify · 灰印/聚火 · SF4 U2U3 · 调律 live bad · 签名 lore · 套装 blurb · 宝石短效果 · 天赋悬停 · 烙纹档幅度 · 副手微量数字 · soft/开孔再叠 · Affix densify · 抬 RATE |

---

## 4. 明确不做

- 任何 `RATE` / `MAX_TRIES` / enhance cost / pity 数学 / CostSoft MULT 改值  
- Affix densify · 伤乘 · sx20 · ForgeChoice densify · 抬表  
- lore>3 · 效果长墙  
- SF4 U2/U3 · 灰印/聚火 · soft·开孔·Anvil densify · topUpTo  
- 天赋悬停 / 宝石短效果 / 套装 blurb / 签名 lore / 烙纹档幅度 / 副手微量数字 / 调律 live bad 再叠  
- 开 L13 施工 tip · 成色/精工/升阶本窗扩面 · pin 预览气泡

---

## 5. 验收清单

- [ ] 总控批 **A · 方案 M**  
- [ ] tip + RESEARCH + DESIGN + power-paths 戳可点  
- [ ] 文首 HOLD / 选题否决表可核  
- [ ] 批后才派 **余烬-插件** + **余烬-物品**；策划本窗 **无**业务 jar  
- [ ] `git show` docs-only（本 commit）；L13 施工仍 HOLD  

---

## 6. 批注（荐）

- **批：** **A · 方案 M**（待总控）  
- **选题：** TIP-BM 后 · **离开副手购/炼数字** · **强化预览成功率菜单诚实**（可感变强途径 · **非抬 RATE** · 对位 WS2）  
- **批后派岗：** 余烬-插件 + 余烬-物品  
- **HOLD：** L13 施工；Anvil densify；topUpTo；soft·开孔再叠；灰印·聚火；SF4 U2U3；调律 live bad 进持件；签名 lore 再叠；套装 blurb 再叠；宝石短效果再叠；天赋悬停再叠；烙纹档幅度再叠；副手微量数字再叠；甲闸再扫；扩 stampable；SetChase 六槽 densify；效果长墙；**Affix densify**；**抬 RATE**  

## STATUS: OPEN · 荐批 A·M · 2026-10-13 00:05 Asia/Shanghai
