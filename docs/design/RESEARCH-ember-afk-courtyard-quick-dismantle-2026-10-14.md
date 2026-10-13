# RESEARCH · 挂机庭「顺手分解」（产→拆可点 · ≠第五件花料 · ≠半行空转 · ≠抬表）· 2026-10-14

> **状态：** **打开 · 待批 A · 荐 M · 拟 D13316 · TIP-CK（重开）** · docs-only · 2026-10-14 Asia/Shanghai  
> **起因：** 总控【TIP-CK REJECT · 重开真债】`d00ed84bc`：D13316「选页三房可躲半行」**否决**——纯 TrMenu lore/Open 半行、无新动作 = **菜单诚实空转硬禁**。请另开 tip+DESIGN（仍可用 TIP-CK / 拟 D13316），STATUS=待批 A · 荐 M · docs-only。必须有**玩家可点/可进/房内可感**的新动作或机制。  
> **取代：** 被否的半行题 `DESIGN/TIP-ember-short-select-dodge-half-line` · **勿勾 CLOSED**  
> **上游：** TIP-CJ CLOSED live `fe0d3890d` · D13315 推荐花 live；挂机庭四花+推荐花齐；今日推/冲一下/周短征/三冲/Cast 齐  
> **硬禁：** **零** sx20 · **零** L13 · **零** K3 · **零** Pack6/天赋灰印/R · **零抬** AFK 日表/`daily_kills`/`afk.tiers`/`short:5`/伤乘 · **零** Affix densify · **零** sure/pity/soft 诚实半行再叠 · **零** 菜单诚实/observe 空转 · **零** 假开 `gate_daily` · **零** 重开 calamity · **零** 新地图硬开 · **零** 挂机庭第五件花料 · **零** P 格兼确认 · **零** 复开 D423 · **零** 教裸指令 · **零** 撤分格 · **零** Cast 粒子纯打磨当主交付

---

## 0. 一句话

挂机庭已能顺手强化/升阶/精工/成色/推荐花，但**拆装备回料**仍只能去工坊——庭里打满/换装时，手持废件或甲（K0 只能拆）还要跳页。本债=庭内 **D「顺手分解」真 command**（预览→聊天确认，与工坊同链路），**不是**第五件花料，**不是**半行口号。

---

## 1. 选题扫描（REJECT 后 · 必须真动作）

| 候选 | 裁决 | 理由 |
|------|------|------|
| 短征选页「可躲」半行再交 | **否** | 刚 REJECT；诚实空转硬禁 |
| P 格兼确认 | **否** | 本窗硬禁；须另批 |
| Cast 粒子二次打磨当主交付 | **否** | 派单明文禁；非新动作主债 |
| 工坊再叠推荐花镜像 | **否** | ≠推荐花复读 |
| 第五件花料 / 抬产 | **否** | 硬禁 |
| fest / 灾厄 / sx20 | **否** | 硬禁或另批 |
| **挂机庭「顺手分解」真格（荐）** | **采纳 · 改题** | 工坊 D/d + `p1 dismantle` + `held_dismantle_yield` 已 live；庭内 **无** D；甲 K0 只能拆却庭内无入口；=可点真动作；≠第五花料（拆≠花） |

**择优结论：** 半行题已废。扫线发现庭内产→花齐、**产→拆断链**——真 command 可复用工坊，TrMenu-only 薄可施工。故 **改题** 交挂机庭顺手分解；**证据表见 §2**；**为何不是半行见 E7**。

---

## 2. 证据（现网只读 · 2026-10-14）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 总控 REJECT `d00ed84bc`：无新动作纯 lore = 诚实空转 | tip/DESIGN 旁注 | 必须真动作 |
| E2 | `ember_p1_afk` Icons：P/E/e/U/u/R/r/Q/q/S/V/B/I/F/H… **无 D/d** | TrMenu live | 庭内无拆入口 |
| E3 | `ember_p1_forge` Icon D：`command: corerpg p1 dismantle` · lore `%held_dismantle_yield%` · 聊天确认 | forge.yml | 真动作链路已在盘 |
| E4 | `%corerpg_p1_held_dismantle_yield%` live（含日首+1 peek · TIP-BX） | D13287/D13288 | 零新 PAPI 即可诚实预览 |
| E5 | 甲 K0 只能分解；庭内 E/U/R/Q 对甲灰 —— 拆是甲主路径 | forge 诚实 / D333 | 庭内缺甲可点事 |
| E6 | 硬禁「第五件花料」= 强化/升阶/精工/成色之外再发明花命令；**分解=拆回料，非花料** | 派单口径 | 本债合法 |
| E7 | **为何不是半行：** 主交付=新菜单格 **真 `command: corerpg p1 dismantle`**（可点→预览→聊天确认）；Open 半行仅同批指针，**不可独批** | 本 RESEARCH | 过硬禁 |

**一句话缺口：** 庭里能花不能拆——资源环「多一事」在拆侧断了。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **TrMenu-only：** `ember_p1_afk` 空位落 **D**「顺手分解」· lore 挂 `%held_dismantle_yield%` · actions `corerpg p1 dismantle`（与工坊同；聊天确认）  
2. **可选同批 d** 指引格（聊天确认说明）· 若布局挤可并入 D lore  
3. **Open / 去哪花** 同批半行「本页也可顺手拆」——**仅辅，不可独批**  
4. **零改** dismantleYield 表 / YieldBoost / UpgradeRules / afk.tiers / daily_kills  
5. **施工岗：** 批后 **余烬-物品**（TrMenu）；默认不派插件/怪物/地图

---

## 4. 明确不做

- 只 Open/lore 半行无 D 格 · 第五件花料 · P 兼确认 · 抬产/改拆表 · 静默拆无聊天确认  
- sx20 · Pack6/天赋/灰印/R/L13/K3 · 诚实/observe 空转 · 重开 calamity · 复开 D423 · Cast 粒子主交付 · 教裸指令  

## STATUS: 打开 · 待批 A · 荐 M · 拟 D13316 · TIP-CK（重开·取代半行否决题）· 2026-10-14 Asia/Shanghai
