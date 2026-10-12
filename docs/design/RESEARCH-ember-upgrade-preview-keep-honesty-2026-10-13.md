# RESEARCH · 升阶预览「确定·保留」菜单诚实（工坊 TrMenu · 离开强化预览成功率）· 2026-10-13

> **状态：** APPROVED · TIP-BO · 已派余烬-物品 · 不派插件 · 2026-10-13 00:18 Asia/Shanghai  
> **起因：** TIP-BN CLOSED live 后总控「离开强化预览成功率链 · 开下一可感变强途径」。BN DESIGN 旁债 #9 已点名「成色/精工/升阶预览再压 WS2 同构」。扫 AH…BN / power-paths：强化 E/e 已有成功率；成色/精工已有花费·进度·点预览；**升阶 U/u 仅「本次+缺料」、无「确定·保留」、无点预览/确认 cue**——聊天 Plan.note 已写「强化/成色/精工/绑定全部保留」。  
> **上游：** TIP-BN CLOSED live `1.65.800-d13143` · feat `059891b38` · items `284e5dc7c` · docs `2e0dd355e` · `EmberUpgradeRules.upgrade` Plan.note · `EmberGearNextHint.upgradeLine` · `ember_p1_forge.yml` U/u · power-paths WS2 · TIP-AI CLOSED `STAGE1_L13_OFFLINE=FAIL`（**L13 施工 HOLD**）  
> **硬禁：** **零 Affix densify · 零伤乘 · 零抬表 · 零抬 RATE/MAX_TRIES/cost · 零抬 upgradeCost/C04 · 零抬 talent/gem/副手 flat · 零 soft·开孔再叠 · 零 Anvil densify · 零 topUpTo · 零 SF4 U2U3 · 零灰印/聚火 · 零签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量数字 / 强化率再叠 · 零调律 live bad 进持件 · 零并轨 B · 零 SetRules/Formula 抬 · 零扩 stampable · 零再扫甲闸 · 零 SetChase 六槽 densify · 零 ForgeChoice densify · 零 ConvertCap densify · 零 sx20 · 零 skill_mult · 零开 L13 施工 tip · 零效果长墙 · 零 lore>3**

---

## 0. 一句话

升阶预览/确认格用 **既有 Plan.note SoT** 写出「确定 · 保留强化/成色/精工」与点预览 cue；不改升阶价、不改门禁。

---

## 1. 选题扫描（离开强化预览成功率 + 幅度半行族 + soft/开孔 + SF4）

| 候选 | 裁决 | 理由 |
|------|------|------|
| **升阶预览「确定·保留」菜单诚实** | **采纳** | 离开强化率；BN 旁债 #9；聊天已保留口径、菜单无；成色/精工 WS2 已齐、升阶未齐；**非 TrMenu 幅度半行**；**非抬 upgradeCost** |
| 强化预览成功率再叠 | **否** | 刚 CLOSED / 硬禁离链 |
| 成色/精工再压 WS2 | **否** | 菜单已有花费·进度·点预览；弱于升阶缺口 |
| 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量再叠 | **否** | 刚 CLOSED / 硬禁 |
| pin 预览气泡 | **否** | 烙纹链 · prefer 再离 |
| SF4 U1 菜单形缓 / U2U3 | **否** | 离接线链 / U2U3 硬禁 |
| Anvil soft densify / topUpTo | **否** | 硬禁仍冻 |
| soft / 开孔 / xp-boost / GemHole / enhance+5 再叠 | **否** | 链已齐 / 硬禁 |
| 灰印 / 聚火 | **否** | 旁债仍冻 |
| L13 施工 | **否** | TIP-AI FAIL；HOLD |
| 甲闸再扫 / 扩 stampable / SetChase 六槽 | **否** | 硬禁 |
| Affix densify / 抬 RATE / 抬 talent/gem flat | **否** | 硬禁 |
| 调律 live bad 进持件 | **否** | 旁债仍冻 |
| 效果长墙 / lore>3 | **否** | densify / 硬禁 |
| 观察停窗 only | **否** | 有可施工诚实薄片 |

---

## 2. 证据（现网只读）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | `upgrade()` Plan.note = `升阶 T{n} → T{n+1}（强化/成色/精工/绑定全部保留）` | `EmberUpgradeRules.java` | **保留口径 SoT 已有** |
| E2 | `upgradeLine` → `T{n}→T{n+1} · {costShort}`（或需首通/T0/已满） | `EmberGearNextHint.upgradeLine` | **档位+花费已在 cost 行** |
| E3 | PAPI `held_upgrade_cost` = upgradeLine；`held_upgrade_lack` = 缺料半行 | `EmberRunPapi` | **无需新 PAPI** |
| E4 | 菜单升阶预览 lore：本次 + 缺料 — **无「确定·保留」、无 ➥ 点预览** | `ember_p1_forge.yml` Icon `U` | **点格前不见保留利益 / 操作 cue** |
| E5 | 菜单升阶确认：本次 + 缺料 +「与预览一致」— **无保留半行、无 ➥ 点确认** | Icon `u` | **确认前仍缺保留口径** |
| E6 | 成色/精工预览：花费 · 进度 hope · ➥ 点预览 — **WS2 已齐** | Icons `Q`/`q`/`R`/`r` | **本窗不叠成色/精工** |
| E7 | 强化预览：花费 · 成功率·保底 · ➥ 点预览 — **TIP-BN 已齐** | Icons `E`/`e` | **离开强化率链** |
| E8 | 聊天预览：Plan.note + 当前/之后 +「确定成功」+（T≥1 印记另一条路） | `EmberForgeService.simple` | **聊天已诚实；菜单未对齐保留** |
| E9 | `upgradeCost` / C04 / firstClear 门禁 — **本窗只读** | `EmberUpgradeRules` / economy | **零抬价 · 零改门禁** |
| E10 | power-paths **WS2**：预览三态 = 花费 · 成功率或 n/N · 点确认；BN 旁债 #9 升阶未齐 | DESIGN power-paths §3.2 · BN DESIGN §4 | **本窗对位升阶保留半行** |

### SoT 保留口径（只读 · 禁改数学）

| 键 | 口径 |
|----|------|
| 可升阶 | `确定 · 保留强化/成色/精工`（绑定同保留；菜单短写） |
| T0 / 已满 / 甲 | 现网 cost 行已给短句；本窗不发明第二表 |
| 禁 | 改 upgradeCost / C04 / firstClear / soft MULT |

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **TrMenu only**：`ember_p1_forge` Icon `U`/`u` lore → 本次（既有 cost）· **确定 · 保留强化/成色/精工**（+缺料空位）· ➥ 点预览|确认；守 lore≤3。  
2. **零新 PAPI**：保留文案钉死 Plan.note；cost/lack 既有键不动。  
3. **施工岗**：批后 **余烬-物品**（只改 forge 升阶两格 lore）；**不派插件**。  
4. **零改** Affix / soft / 开孔 / Anvil / SF4 / 强化率 / 签名·套装·宝石·天赋·烙纹·副手幅度再叠；零抬 upgradeCost。

---

## 4. 明确不做

- 改 `upgradeCost` / C04 / firstClear / UpgradeCostSoft MULT / RATE / MAX_TRIES  
- Affix densify · 伤乘 · 抬表 · soft·开孔·Anvil densify · SF4 U2U3 · 灰印/聚火 · L13 施工  
- 强化率 / 签名 lore / 套装 blurb / 宝石短效果 / 天赋悬停 / 烙纹档幅度 / 副手微量再叠  
- 成色/精工本窗再叠 · 效果长墙 · lore>3 · 印记「另一条路」长文进菜单（另 tip）

## STATUS: APPROVED · TIP-BO · 已派余烬-物品 · 不派插件 · 2026-10-13 00:18 Asia/Shanghai
