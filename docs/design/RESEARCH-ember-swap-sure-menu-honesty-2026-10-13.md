# RESEARCH · 强化轨道互换「免费·确定」菜单诚实（工坊 TrMenu · 离开成色/精工确定成功链）· 2026-10-13

> **状态：** **CLOSED live** · TIP-BT · items `683011255` · TrMenu reload 77 · jar 未改 `1.65.801-d13207` · 2026-10-13 00:51 Asia/Shanghai  
> **起因：** TIP-BS CLOSED live 后总控「离开成色/精工确定成功链 · 开下一可感变强途径」。硬禁含成色/精工确定成功再叠 · access_* · 升阶保留 · 强化率 · 转化 · 形缓 · 签名/套装/宝石/天赋/烙纹/副手再叠。扫 AH…BS / power-paths / BS 旁债：**互换聊天已写「免费，确定」+「预览直办」**，菜单 S/s 仅花费口径+动作说明，**无免费确定/预览直办半行**。  
> **上游：** TIP-BS CLOSED live items `eba75771b` · docs `e35ee2b81` · TrMenu reload 77 · jar 未改 `1.65.801-d13207` · `EmberForgeService.swapSlots` · `EmberGearNextHint.swapLine` · `ember_p1_forge.yml` S/s · power-paths WS2 · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零改解锁数学 · 零 ConvertCap densify · 零改 WEEKLY_CAP/MARK_TICKS · 零抬 RATE/upgradeCost · 零抬成色/精工 cost · 零抬 talent/gem/副手 flat · 零 Anvil densify / soft·开孔再叠 / topUpTo · 零 SF4 U2U3 · 零灰印·聚火 · 零签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量 / 强化率 / 升阶保留 / 形缓 Icon A / 转化保留 / access_* / 成色·精工确定成功再叠 · 零调律 live bad · 零并轨 B · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 · 零 lore>3**

---

## 0. 一句话

互换预览·确认格补上「**免费确定 · 预览直办**」半行，对齐聊天 SoT；不改价（本就免费）、不改互换数学。

---

## 1. 选题扫描（离开成色/精工确定成功 + 已齐诚实半行族）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **强化轨道互换「免费·确定」菜单诚实** | **采纳** | 离开 Q/R 确定成功链；聊天「免费，确定」+「预览直办」菜单无；**非**成色/精工再叠；**非**升阶保留/强化率；零新 PAPI；forge 稳线旁环 |
| 成色/精工「确定成功」再叠 | **否** | 刚 CLOSED · 硬禁离链 |
| forgeroll 普通格成色权重半行 | **否** | BS 旁债 #10 · prefer 再离赌线；禁 ForgeChoice densify 边 |
| 升阶「印记另一条路」短指针 | **否** | 升阶邻链 · prefer 再离 |
| 形状格 D/E/F 形缓半行 | **否** | 形缓链 · prefer 再离 |
| hub/raid/abyss access 扫尾 | **否** | access_* 再叠硬禁 |
| 转化保留 / 强化率 / 升阶保留再叠 | **否** | 硬禁 |
| 签名/套装/宝石/天赋/烙纹/副手再叠 | **否** | 硬禁 |
| cook/AFK/attune soft 再叠 | **否** | 链已齐或硬禁 |
| Anvil densify / soft·开孔 / SF4 U2U3 | **否** | 硬禁 |
| 灰印 / 聚火 / L13 施工 | **否** | 仍冻 / HOLD |
| Affix densify / ConvertCap densify / 伤乘 | **否** | 硬禁 |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 效果长墙 / lore>3 | **否** | densify / 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 互换预览聊天：`强化轨道互换（免费，确定）:` | `EmberForgeService.swapSlots` ~D781 | **免费确定 SoT 已有** |
| E2 | ActionBar：`互换预览 · +X↔+Y · 直办` | 同文件 ~D786 | **玩家面已喊直办** |
| E3 | D4481：免费确定 → **预览直办**（少 ConfirmTokens） | 同文件 ~D788–790 | **预览=结果；免费** |
| E4 | 菜单互换 S：`held_swap_cost` + 动作说明 — **无「免费确定/预览直办」字面** | `ember_p1_forge.yml` S | **花费口径≠确定感字面；菜单缺口** |
| E5 | 菜单互换 s：同构花费 + 交换说明 — **无确定/直办** | 同文件 s | **同缺口** |
| E6 | `held_swap_cost` → `EmberGearNextHint.swapLine()` = `免费 · 交换强化等级+失败计数` | `EmberRunPapi` / GearNextHint | **既有免费口径；零新 PAPI** |
| E7 | 成色/精工 Q/q/R/r 已有「确定成功」（TIP-BS）；强化 E 有率（BN）；升阶 U 有保留（BO） | forge.yml | **离开成色/精工确定成功链 · 离开强化率/升阶保留** |
| E8 | 互换 Cost.NONE · 禁抬价 | `swapSlots` commit | **零抬 cost（本就免费）** |
| E9 | power-paths WS2：预览三态 = 花费 · 成功率或 n/N **或确定感** · 点确认 | DESIGN power-paths §3.2 | **互换缺「确定」字面** |
| E10 | BS 旁债 #9/#10 prefer 再离升阶印记/forgeroll；互换未列但仍为稳线旁环诚实缺口 | BS DESIGN §4 | **本窗选互换，不压 Q/R / 赌线 / 升阶邻** |

### SoT 免费确定（只读 · 禁改数学）

| 键 | 口径 |
|----|------|
| 可互换 | `免费确定`（预览直办；与聊天一致） |
| 花费行 | 继续 `%corerpg_p1_held_swap_cost%`（免费 · 交换强化等级+失败计数） |
| 禁 | 改 swap 数学 · 收费化 · 抬 Cost · soft MULT · 新发明价表 |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **TrMenu only**：`ember_p1_forge` Icon `S`/`s` 第二行 → `§a免费确定 §8· §f预览直办`；花费行与点预览/确认行保留；守 lore≤3。  
2. **零新 PAPI**；`held_swap_cost` / 甲灰显不动。  
3. **施工岗**：批后 **余烬-物品**；**不派插件**。  
4. **零改** Affix / soft / 开孔 / Anvil / SF4 / access / 成色精工确定成功 / 升阶保留 / 强化率 / 转化 / 形缓再叠。

---

## 4. 明确不做

- 改 swap 收费 / Cost · quality/refine cost · hope · RATE · upgradeCost · C04/C05/C06  
- Affix densify · 伤乘 · 抬表 · soft·开孔·Anvil densify · SF4 U2U3 · 灰印/聚火 · L13 施工  
- 成色/精工确定成功再叠 · access_* 再叠 · 升阶保留 / 强化率 / 转化保留 / 形缓 Icon A / 签名·套装·宝石·天赋·烙纹·副手再叠  
- forgeroll 权重半行 densify · ForgeChoice densify  
- 效果长墙 · lore>3 · 并轨 B · 新 PAPI

## STATUS: OPEN · TIP-BT · 荐批 A·M · 2026-10-13 00:48 Asia/Shanghai
