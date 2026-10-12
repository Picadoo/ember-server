# RESEARCH · 主线/短征解锁「进过即开」菜单诚实（MG2 · 离开转化保留链）· 2026-10-13

> **状态：** **CLOSED live** · TIP-BR · `1.65.801-d13207` · feat `a5e88aa98` · items `8c8edc1f1` · TrMenu reload 77 · 2026-10-13 00:41 Asia/Shanghai  
> **起因：** TIP-BQ CLOSED live 后总控「离开转化保留/代价 · 形缓 · 升阶保留链 · 开下一可感变强途径」。prefer **非**再叠 TrMenu 保留/代价半行；扫 AH…BQ / power-paths：**MG2「进过即开 / 清过也开」**仍缺口——服务端 `gemPathUnlocked` / `endgameUnlocked` 已是首进∨首通，短征/挑战/冒险灰锁文案与条件仍写「首通/通关」。  
> **上游：** TIP-BQ CLOSED live items `a5c3f49d7` · docs `6040733da` · jar 未改 `1.65.800-d13143` · power-paths Batch2 MG2 · `EmberSkillKit.gemPathUnlocked` / `endgameUnlocked` / `mapAccessUnlocked` · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零改 WEEKLY_CAP/MARK_TICKS/RATE/upgradeCost · 零抬 talent/gem/副手 flat · 零 ConvertCap densify · 零改 unlock 数学（C_*_ENTER / firstCleared）· 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留再叠 · 零调律 live bad 进持件 · 零并轨 B · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 · 零 lore>3**

---

## 0. 一句话

短征/挑战/冒险灰锁口对齐现网「**进过即开 · 清过也开**」；不改解锁条件，只修菜单谎称与过严条件。

---

## 1. 选题扫描（离开转化保留 + 形缓 + 升阶保留 + 幅度半行族）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **主线/短征解锁「进过即开」菜单诚实（MG2）** | **采纳** | 总控 prefer 主线解锁 cue；power-paths MG2 未齐；SoT 已首进∨首通、菜单仍「首通/通关」；**非**保留/代价半行叠；**零改**解锁数学 |
| 转化保留/代价 / 形缓 Icon A / 升阶保留再叠 | **否** | 刚 CLOSED / 硬禁离链 |
| 成色/精工「确定成功」再压 | **否** | BQ 旁债 #12 弱；hope 已齐 |
| 升阶「印记另一条路」短指针 | **否** | 升阶邻链 · prefer 再离 |
| 形状格 D/E/F 形缓半行 | **否** | 形缓链 · hard leave |
| cook 供应价 / AFK 表 / attune soft 再叠 | **否** | soft/xp-boost/假耗链已齐或硬禁抬表 |
| 签名 lore / 套装 blurb / 宝石 / 天赋 / 烙纹 / 副手 / 强化率再叠 | **否** | 硬禁 |
| Anvil densify / soft·开孔 / topUpTo / SF4 U2U3 | **否** | 硬禁 |
| 灰印 / 聚火 / L13 施工 | **否** | 仍冻 / HOLD |
| Affix densify / ConvertCap densify / 伤乘 / skill_mult | **否** | 硬禁 |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 效果长墙 / lore>3 | **否** | densify / 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `gemPathUnlocked` = `C_GEM_ENTER` 计数∨ `firstClearedKey(q01)` | `EmberSkillKit` D5281 | **Q01 进过即开宝石/短征门** |
| E2 | `attuneUnlocked` / `endgameUnlocked` = `C_ATTUNE_ENTER`∨ Q07 首通 | 同文件 D2293/D5345 | **Q07 进过即开终局/挑战** |
| E3 | `mapAccessUnlocked("q01")` → gemPath；`"q07"` → endgame | D5409 | **短征进本命令跟 SoT** |
| E4 | `challengeOpen` = `attuneUnlocked`（首进∨首通） | `EmberRunService` D2419 | **挑战资格已跟 endgame** |
| E5 | PAPI `q01done`/`q07done` = **仅** `progressFlag` 首通 | `EmberRunPapi.gate` | **菜单条件过严于 SoT** |
| E6 | 短征 sx01–13 灰锁：`%q01done% < 1` + 文案「需本人 Q01 **首通**」「先**通**主线」 | `ember_p1_short.yml` | **文案谎 + 条件过严** |
| E7 | 短征 sx14+ 灰锁：`%q01_fc% != 已领取` +「通关后本格解锁」 | 同文件 | **更严（领奖态）· 与 sx01–13 不一致** |
| E8 | 冒险「短征」灰锁：`q01done`；后图 X 等：`q07done` +「**首通** Q07 后开放」 | `ember_p1_adventure.yml` | **同谎** |
| E9 | 挑战七格：「队员需各自**首通** Q07」 | `ember_p1_challenge.yml` | **与 challengeOpen 首进∨首通冲突** |
| E10 | 聊天/赛季已有「首进或首通」口径 | `EmberSeason` / `EmberRunPapi` forge_t* / challenge 字串 | **SoT 文案已散落；菜单未统一** |
| E11 | power-paths **MG2**：入口文案统一「进过即开 / 清过也开」；**零改**解锁条件 | DESIGN power-paths §3.3 | **本窗对位** |

### SoT 解锁（只读 · 禁改数学）

| 门 | 真条件 | 菜单应写 |
|----|--------|----------|
| 短征 / 宝石路径 | Q01 **进过∨首通** | 进过即开 · 清过也开 |
| 挑战 / 深渊 / 团本入口 | Q07 **进过∨首通** | 同上 |
| 禁 | 改 C_*_ENTER / firstCleared / 体力 / 日帽 / 掉率 | — |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **插件：** 新 PAPI `access_q01` / `access_q07` → `"1"`/`"0"`，分别绑 `gemPathUnlocked` / `endgameUnlocked`；零改解锁数学。  
2. **物品：** 短征/冒险灰锁条件 `q01done`/`q01_fc`/`q07done` → `access_q01`/`access_q07`；文案统一「进过或首通 / 进过即开」。  
3. **挑战页：** 七格队员口径改「进过或首通 Q07」。  
4. **施工岗：** 批后 **余烬-插件 + 余烬-物品**；策划 docs-only。  
5. **零改** Affix / soft / 开孔 / Anvil / SF4 / 转化保留 / 形缓 / 升阶保留 / 幅度半行族。

---

## 4. 明确不做

- 改 `gemPathUnlocked` / `attuneUnlocked` / `mapAccessUnlocked` / firstCleared / C_*_ENTER 语义  
- 抬体力日帽 / 掉率 / clear-gate / Affix densify / ConvertCap densify  
- soft·开孔·Anvil densify · SF4 U2U3 · 灰印/聚火 · L13 施工  
- 转化保留 / 形缓 / 升阶保留 / 签名·套装·宝石·天赋·烙纹·副手·强化率再叠  
- 效果长墙 · lore>3 · 并轨 B

## STATUS: CLOSED live `1.65.801-d13207` · TIP-BR · feat `a5e88aa98` · items `8c8edc1f1` · TrMenu reload 77 · 2026-10-13 00:41 Asia/Shanghai
