# RESEARCH · 成色/精工「确定成功」菜单诚实（工坊 TrMenu · 离开 MG2 进过即开链）· 2026-10-13

> **状态：** **CLOSED live** · TIP-BS · items `eba75771b` · TrMenu reload 77 · jar 未改 `1.65.801-d13207` · 2026-10-13 00:47 Asia/Shanghai  
> **起因：** TIP-BR CLOSED live 后总控「离开 MG2 进过即开 / 转化 / 形缓 / 升阶保留链 · 开下一可感变强途径」。硬禁含 **access_* 再叠** → 不扫 hub/raid/abyss 解锁尾。扫 AH…BR / power-paths / BR 旁债 #8：**成色/精工聊天已写「确定成功，预览即结果」**，菜单 Q/q·R/r 仅花费+希望进度，**无确定成功半行**。  
> **上游：** TIP-BR CLOSED live `1.65.801-d13207` · feat `a5e88aa98` · items `8c8edc1f1` · docs `b5f1113ce` · TrMenu reload 77 · `EmberForgeService.simple` 成色/精工预览 · `ember_p1_forge.yml` Q/q/R/r · power-paths WS2 · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零改解锁数学 · 零 ConvertCap densify · 零改 WEEKLY_CAP/MARK_TICKS · 零抬 RATE/upgradeCost · 零抬 talent/gem/副手 flat · 零抬成色/精工 cost · 零 Anvil densify / soft·开孔再叠 / topUpTo · 零 SF4 U2U3 · 零灰印·聚火 · 零签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留 / access_* 再叠 · 零调律 live bad · 零并轨 B · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 · 零 lore>3**

---

## 0. 一句话

成色/精工预览·确认格补上「**确定成功**」半行，对齐聊天 SoT；不改价、不改希望数学。

---

## 1. 选题扫描（离开 MG2 / 转化 / 形缓 / 升阶保留 + access_* + 幅度半行族）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **成色/精工「确定成功」菜单诚实** | **采纳** | 离开 MG2/access；BR 旁债 #8；聊天「确定成功，预览即结果」菜单无；**非**升阶保留再叠；**非**强化率再叠；零新 PAPI；forge quality 轴 |
| hub/raid/abyss「通关才开」扫尾（access_q07 再绑） | **否** | **access_* 再叠**硬禁；MG2 链 leave |
| 转化保留 / 形缓 Icon A / 升阶保留 / 强化率再叠 | **否** | 刚 CLOSED / 硬禁离链 |
| 签名 lore / 套装 blurb / 宝石 / 天赋 / 烙纹 / 副手再叠 | **否** | 硬禁 |
| cook 供应价 / AFK 表抬 / attune soft 再叠 | **否** | soft/假耗/xp-boost 链已齐或硬禁抬表 |
| forgeroll 成色权重半行 densify | **否** | 下限格已有「必≥精良」+加扣；弱于稳线确定成功缺口；禁 ForgeChoice densify |
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
| E1 | 成色/精工预览聊天：`之后: …（确定成功，预览即结果）` | `EmberForgeService.simple` ~D511 | **确定成功 SoT 已有** |
| E2 | ActionBar：`确定成功 · 成色/精工 · {花费}` | 同文件 ~D540 | **玩家面已喊确定成功** |
| E3 | D4417：成色/精工 → **预览直办**（升阶默认仍确认） | 同文件 ~D552–554 | **无随机失败；预览=结果** |
| E4 | 菜单成色 Q/q：花费 · 进度 hope · 点预览/确认 — **无「确定成功」** | `ember_p1_forge.yml` | **希望≠确定成功；菜单谎称缺口** |
| E5 | 菜单精工 R/r：同构花费 · 进度 · 点预览/确认 — **无「确定成功」** | 同文件 | **同缺口** |
| E6 | 升阶 U/u 已有「确定 · 保留…」（TIP-BO）；强化 E/e 已有成功率（TIP-BN） | forge.yml | **离开升阶保留 / 强化率链** |
| E7 | `held_next_q` / `held_next_c` / `held_quality_hope` / `held_refine_hope` 既有 | `EmberRunPapi` | **零新 PAPI** |
| E8 | 成色/精工 cost / hope 数学 — **本窗只读** | `EmberUpgradeRules` / HopeMeter | **零抬价 · 零改 pity** |
| E9 | BO/BQ 曾否「成色/精工再压 WS2」因花费·进度·cue 已齐；BR 旁债 #8 单点 **「确定成功」** 仍可选 | BO RESEARCH · BR DESIGN §4 | **本窗只补确定成功半行，非再压整链** |
| E10 | power-paths WS2：预览三态 = 花费 · 成功率或 n/N **或确定感** · 点确认 | DESIGN power-paths §3.2 | **成色/精工缺「确定」字面** |

### SoT 确定成功（只读 · 禁改数学）

| 键 | 口径 |
|----|------|
| 可成色/精工 | `确定成功`（预览即结果；与聊天一致） |
| 希望进度 | 继续 `%held_quality_hope%` / `%held_refine_hope%`（保底旁路可见；**非**成功率） |
| 禁 | 改 quality/refine cost · hope MAX · RATE · soft MULT |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **TrMenu only**：`ember_p1_forge` Icon `Q`/`q`/`R`/`r` 第二行 → `§a确定成功 §8· §e进度：§f%{hope}%`；花费与点预览/确认行保留；守 lore≤3。  
2. **零新 PAPI**；cost/hope/lack 既有键不动。  
3. **施工岗**：批后 **余烬-物品**；**不派插件**。  
4. **零改** Affix / soft / 开孔 / Anvil / SF4 / access / 升阶保留 / 强化率 / 转化 / 形缓再叠。

---

## 4. 明确不做

- 改 quality/refine cost · hope 数学 · RATE · upgradeCost · C04/C05/C06  
- Affix densify · 伤乘 · 抬表 · soft·开孔·Anvil densify · SF4 U2U3 · 灰印/聚火 · L13 施工  
- access_* 再叠 · hub/raid/abyss 解锁尾 · 升阶保留 / 强化率 / 转化保留 / 形缓 Icon A 再叠  
- 效果长墙 · lore>3 · 并轨 B · 新 PAPI

## STATUS: OPEN · TIP-BS · 荐批 A·M · 2026-10-13 00:43 Asia/Shanghai
