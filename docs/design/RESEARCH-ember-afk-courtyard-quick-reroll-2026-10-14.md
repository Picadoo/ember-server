# RESEARCH · 挂机庭「顺手洗练」（产→洗炼轨可点 · ≠第五件花料 · ≠推荐花复读 · ≠半行 · ≠抬表）· 2026-10-14

> **状态：** **勾毕 CLOSED live** · live `e099c02b7` · feat `a342f34cf` · tip `6804ed53a` · PASS `16998530c` · jar `1.65.814-d13322.local` · 批 sha `33d94eb40` · TIP-CQ · 2026-10-14 02:57 Asia/Shanghai
> **起因：** 总控【连开·TIP-CQ / 拟 D13322】上游 TIP-CP CLOSED live `d87c3d89d`（feat `64f08f47b` · jar `1.65.813-d13321.local` · PASS `1696ccd17` · 短征轻量 variety 房内可感齐 · W1e 仍关）。请立刻交下一档硬债 docs-only 待批 A · 荐 M。**优先短征日更 / 乐趣系统**（真可点或房内可感）；挂机庭资源环可扩（≠顺手镜像/第五花/推荐花复读）。Cast+BossAid+EliteAid+todayRot+轻量 variety(W1a–d) **已齐** · 勿复读。半行 REJECT `d00ed84bc` **保持否决**。  
> **上游：** TIP-CP CLOSED live `d87c3d89d` · feat `64f08f47b` · PASS `1696ccd17` · 批 sha `e976fe6d8` · D13321；Cast 全波 + BossAid + EliteAid + todayRot cue + 短征轻量 variety(W1a–d) 齐；庭内四花+推荐+拆+换齐；今日推/冲一下/周短征/三冲齐  
> **硬禁：** **零** 半行独批 · **零** W1e todayRot偏置独批 · **零** Cast粒子打磨 · **零** Aid再拧 · **零** todayRot cue再拧 · **零** 抬 event_rate/event_core/affix_shard · **零** 新 variety kind · **零** 完整 variety 经济发核心 · **零** sx20 · **零** 抬日表/伤表/S表 · **零** Pack6 · **零** 开R/K3 · **零** 关观察 · **零** 灾厄重开 · **零** 教裸指令 · **零** stage脏runtime · **零** 第五件花料 · **零** 推荐花复读 · **零** P格兼确认

---

## 0. 一句话

短征 Cast/Aid/todayRot/variety(W1a–d) **已饱和**；挂机庭能花能拆能换轨，但 **词条洗练** 仍无庭内可点入口——日满/工坊目标已会喊「碎够锁定洗 · /corerpg p1 reroll from forge」，玩家却要从庭跳工坊再点洗练。本债=庭内 **L「顺手洗练」真 command** 进既有洗练页（返回挂机庭），**不是**第五花料，**不是**推荐花复读，**不是**四花顺手镜像，**不是**半行口号。

---

## 1. 选题扫描（TIP-CP 后 · 必须真动作）

| 候选 | 裁决 | 理由 |
|------|------|------|
| Cast 粒子打磨 / Aid再拧 / todayRot cue再拧 / W1e 独批 | **否** | 硬禁；刚 CLOSED |
| 新 variety kind / 抬 event_core / 完整 variety 经济 | **否** | 硬禁 |
| 半行独批（选页/Open 口号） | **否** | REJECT `d00ed84bc` 保持 |
| 庭内再叠第五花 / 推荐花复读 / P兼确认 | **否** | 硬禁 / 已齐 |
| 导航三冲 / 今日推 / 冲一下复读 | **否** | 已 CLOSED |
| fest / 灾厄 / sx20 | **否** | 硬禁或另批 |
| **挂机庭「顺手洗练」真入口（荐）** | **采纳** | D13317§6③ 明文后置；工坊洗练入口 live；庭内无入口；AfkForgePath 已喊 reroll；=产→洗炼轨真扩；≠花料/推荐花 |

**择优结论：** 短征日更玩法层（Cast/Aid/todayRot/variety）已齐，§6①②③④⑤ 多触硬禁。扫线发现庭资源环 **洗炼轨断链**——与「乐趣/资源环真扩」落差即真债。故交庭内顺手洗练；**证据表见 §2**；**为何不是半行见 E8**；**改题有证据**（短征饱和 + D13317§6③ + 现网缺口）。

---

## 2. 证据（现网只读 · 2026-10-14）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 总控 TIP-CP CLOSED · 连开 TIP-CQ · 短征已齐可转向资源环真扩 | live `d87c3d89d` · 派单 | 选题边界 |
| E2 | D13317§6③ 明文后置「庭内顺手洗炼/reroll（若另证真债）」 | DESIGN-swap | 合法接力 |
| E3 | `ember_p1_afk`：E/U/R/Q/P/D/W/w/S… **无** 洗练 Icon / 无 `p1 reroll` | TrMenu live | 庭内无洗炼入口 |
| E4 | `ember_p1_forge` Icon W：`corerpg p1 reroll from forge` + `%reroll_blade_cur%` / `%reroll_charm_cur%` live | forge.yml | 真动作链路已在盘 |
| E5 | `REROLL_FROM` 仅 `gear`/`hub`/`forge` · **无** `afk`→`ember_p1_afk` | `EmberGrowthService` static | 返回源缺挂机庭；薄插件可补 |
| E6 | `EmberAfkForgePath` forgeGoal=LOCK 时喊「碎够锁定洗 · /corerpg p1 reroll from forge」 | Java live | 系统已认定洗练是挂机产料消耗点，但无菜单闭环 |
| E7 | 洗练≠强化/升阶/精工/成色（花料）≠推荐花≠拆≠换轨 —— **独立洗炼轨** | 口径 | ≠第五花/推荐花复读/顺手镜像 |
| E8 | **为何不是半行：** 主交付=新菜单格 **真 `command: corerpg p1 reroll from afk`** → 打开既有洗练页可花碎/锁洗；Open 半行仅同批辅，**不可独批** | 本 RESEARCH | 过硬禁 |

**一句话缺口：** 挂机产料已可喂洗练，庭内却点不到——资源环洗炼侧断在入口。

---

## 3. 方案方向（荐 M · 详 DESIGN）

1. **插件 W1a（薄）：** `REROLL_FROM.put("afk", "ember_p1_afk")` + 返回文案「返回挂机庭」（对齐 forge 返回工坊）  
2. **TrMenu W1b：** `ember_p1_afk` 空位落 **L**「顺手洗练」· lore 挂刃/护符 cur（既有 PAPI）· `command: corerpg p1 reroll from afk`  
3. **TrMenu W1c（同批辅）：** Open / 去哪花 +半行「本页也可顺手洗练」——**不可独批**  
4. **零改** reroll 价/pity/pool/shard·币表 · afk.tiers · daily_kills · 伤表 · Cast/Aid/variety  
5. **施工岗：** 批后 **余烬-插件**（W1a）+ **余烬-物品**（W1b/c）；不派怪物/地图

---

## 4. 明确不做

- 只 Open/lore 半行无 L 格 · 第五件花料 · 推荐花复读 · P兼确认 · 抬产/改洗练价 · densify forgeroll 权重  
- Cast/Aid/todayRot/variety 再拧 · W1e 独批 · sx20 · Pack6/灰印/R/L13/K3 · 灾厄 · 教裸指令 · stage 脏 runtime

## STATUS: **勾毕 CLOSED live** · live `e099c02b7` · feat `a342f34cf` · tip `6804ed53a` · PASS `16998530c` · jar `1.65.814-d13322.local` · 批 `33d94eb40` · D13322 · TIP-CQ · 2026-10-14 02:57 Asia/Shanghai
