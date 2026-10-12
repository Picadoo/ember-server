# RESEARCH · 强化预览成功率菜单诚实（工坊 TrMenu · 离开副手购/炼数字）· 2026-10-13

> **状态：** CLOSED live `1.65.800-d13143` · TIP-BN · feat `059891b38` · items `284e5dc7c` · 2026-10-13 00:14 Asia/Shanghai  
> **起因：** TIP-BM CLOSED live 后总控「离开副手购/炼数字链 · 开下一可感变强途径」。扫 AH…BM / power-paths：幅度半行族（签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量）已齐；**WS2 预览三态「花费 · 成功率或 n/N · 点确认」未齐**——聊天预览已写「成功率 N%」，工坊菜单仅花费+保底。  
> **上游：** TIP-BM CLOSED live items `91300ad65` · TrMenu reload 77 · docs `cfbfacadb` · jar 未改 `1.65.799-d13079` · `EmberUpgradeRules.RATE` / `EmberForgeService.enhance` 预览聊天 · `ember_p1_forge.yml` E/e · power-paths [`DESIGN-ember-power-paths-variety-2026-10-12.md`](DESIGN-ember-power-paths-variety-2026-10-12.md) §3.2 WS2 · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零抬 RATE/MAX_TRIES/cost · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量数字再叠 · 零调律 live bad 进持件 · 零并轨 B · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 · 零 lore>3**

---

## 0. 一句话

工坊强化预览/确认格用 **既有 RATE SoT** 写出下一档成功率；不改率、不改花费、不改保底数学。

---

## 1. 选题扫描（离开副手购/炼数字 + 幅度半行族 + soft/开孔 + SF4）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **强化预览成功率菜单诚实** | **采纳** | 离开副手数字；WS2 旁债未齐；聊天已有率、菜单无；**非抬 RATE**；换轴离开 TrMenu 幅度半行叠层 |
| 副手微量数字 / 烙纹档幅度 / 天赋悬停 / 宝石短效果 / 套装 blurb / 签名 lore 再叠 | **否** | 刚 CLOSED / 硬禁离链 |
| pin 预览气泡补档区间 | **否** | 烙纹链 · prefer 再离 |
| SF4 U1 菜单形缓展示 / U2U3 | **否** | 离 SF4 接线链 / U2U3 硬禁 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole / enhance+5 再叠 | **否** | 链已齐 / 硬禁 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| Affix densify / 抬 talent/gem/副手 flat | **否** | 硬禁 |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 效果长墙 / lore>3 | **否** | densify / 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `RATE[]` = `{0,1,1,1,0.85,0.70,0.55,0.40,0.30,0.22,0.15}`（目标 +1…+10） | `EmberUpgradeRules.java` | **成功率 SoT 已有** |
| E2 | `MAX_TRIES[]` = `{0,1,1,1,3,4,5,6,8,10,12}` | 同文件 | **保底 SoT 已有** |
| E3 | 预览聊天：`强化预览: +N → +T  成功率 X%（…保底…）` | `EmberForgeService.enhance` ~413–419 | **聊天已诚实** |
| E4 | 菜单强化预览 lore：花费 + 保底 + 点预览 — **无成功率** | `ember_p1_forge.yml` Icon `E` | **点格前看不见率** |
| E5 | 菜单强化确认同构：花费 + 保底 + 点确认 — **无成功率** | Icon `e` | **确认前仍缺率** |
| E6 | PAPI 有 `held_enhance_cost` / `held_enhance_pity` / `held_enhance_lack` — **无 rate/odds** | `EmberRunPapi` | **缺菜单率位** |
| E7 | power-paths **WS2**：预览三态 = 花费 · **成功率或 n/N** · 点确认；零改 cost | `DESIGN-ember-power-paths-variety` §3.2 | **本窗对位 WS2 未齐片** |
| E8 | Batch3 TIP-S 做了稳/赌「选的是」；**未**把成功率写进强化格 | TIP-S CLOSED | **WS2 成功率半行仍缺** |
| E9 | 成色/精工预览为确定升档（hope 已含「下次确定」）— **非本窗** | `EmberHopeMeter.quality/refine` | **本窗只强化随机档** |
| E10 | 甲条件灰显格：花费+缺料 — 本窗可不扩率（甲不可强） | forge E/e armor icons | **刃/符主路径优先** |

### SoT 档率（只读 · 禁改）

| 目标档 | 成功率 | 保底次数 |
|--------|--------|----------|
| +1…+3 | 100% | 1 |
| +4 | 85% | 3 |
| +5 | 70% | 4 |
| +6 | 55% | 5 |
| +7 | 40% | 6 |
| +8 | 30% | 8 |
| +9 | 22% | 10 |
| +10 | 15% | 12 |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **新 PAPI** `held_enhance_rate`（或等价 `held_enhance_odds`）：手持可强时回 `N%`；本次必成回 `必成`；不可强/空手回短空位。数字 = `round(RATE[target]*100)`，与聊天预览一致。  
2. **TrMenu**：`ember_p1_forge` Icon `E`/`e` 第二行合写 `成功率：… · 保底：…`（或等价紧凑），守 lore≤3；花费行与点预览/确认行保留。  
3. **施工岗**：批后 **余烬-插件**（PAPI + 单测，零改 RATE/MAX_TRIES/cost）+ **余烬-物品**（只改 forge 强化两格 lore）。  
4. **零改** Affix / soft / 开孔 / Anvil / SF4 / 签名·套装·宝石·天赋·烙纹·副手幅度再叠；零抬 RATE。

---

## 4. 明确不做

- 改 `RATE` / `MAX_TRIES` / enhance cost / pity 数学 / CostSoft MULT  
- Affix densify · 伤乘 · 抬表 · soft·开孔·Anvil densify · SF4 U2U3 · 灰印/聚火 · L13 施工  
- 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量数字 / pin 气泡再叠  
- 效果长墙 · lore>3 · 成色/精工/升阶本窗扩面（另 tip）  
- ForgeChoice densify · 稳/赌文案再叠  

---

## 5. 对照

| 成熟做法 | 余烬本窗 |
|----------|----------|
| 强化 UI 悬停写下一档成功率 | 工坊格写 N% |
| 已有聊天预览率 → 菜单对齐 | 与 `EmberForgeService` 预览同 SoT |
| WS2 三态：花费·率/保底·确认 | 补齐缺的「率」半行 |
| 禁止只见保底不见率 | 禁菜单只 pity-only |

## STATUS: CLOSED live `1.65.800-d13143` · TIP-BN · feat `059891b38` · items `284e5dc7c` · 2026-10-13 00:14 Asia/Shanghai
