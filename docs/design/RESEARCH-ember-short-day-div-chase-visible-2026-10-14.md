# RESEARCH · 短征选页「日差本可冲」（日更换本真进本 · ≠今日推复读 · ≠本周短征可冲复读 · ≠半行 · ≠庭内旁路 · ≠抬表）· 2026-10-14

> **状态：** **已批 A · 方案 M** · 拟 D13326 · TIP-CU · 全弧非 L · 派插件+物品 · 勿勾 CLOSED · 2026-10-14 03:52 Asia/Shanghai
> **起因：** 总控【连开·TIP-CU / 拟 D13326】上游 TIP-CT CLOSED live `ea265a3ed`（feat `2705550cd` · tip `07738f8bb` · jar `1.65.817-d13325.local` · PASS `2b24a1647` · 挂机庭顺手随机锻齐 · 宝石仍后置）。请立刻交下一档硬债 docs-only 待批 A · 荐 M。**优先强烈偏向：短征日更/乐趣系统**（真可点或房内可感）。庭内工坊旁路入口已近饱和（四花+推荐+拆+换+洗+烙+转+随机锻）——**默认勿再交庭内旁路**。宝石仍须庭面先亮 gem gap。Cast+BossAid+EliteAid+todayRot+variety(W1a–d)+庭内全弧+今日推/冲一下/周短征/三冲 **已齐勿复读**。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-CT CLOSED live `ea265a3ed` / feat `2705550cd` / tip `07738f8bb` / PASS `2b24a1647` / jar `1.65.817-d13325.local` / 批 `08e77d55d` · D13325
> **硬禁：** **零** 半行独批 · **零** 第五花/推荐花复读 · **零** 洗练/烙纹/转化/随机锻再拧 · **零** densify · **零** Cast粒子打磨 · **零** Aid再拧 · **零** todayRot/W1e独批 · **零** 抬 event_rate/event_core/affix_shard · **零** 新 variety kind · **零** 完整 variety 经济 · **零** sx20 · **零** 抬日表/伤表/S表 · **零** Pack6 · **零** 开R/K3 · **零** 关观察 · **零** 灾厄重开 · **零** 教裸指令 · **零** stage脏runtime · **零** 改 convert 周帽/价 · **零** 改 forgeroll 价/权重

---

## 0. 一句话

庭内旁路 **近饱和**；短征战斗/花样层（Cast/Aid/todayRot/variety）**已齐**。导航层有今日推（单本）与周目标 chase（计数），**无**「今日已有奖通关不同本」换本冲口。本债=选页 **L「日差本可冲」真进本**，**不是**今日推复读，**不是**周目标复读，**不是**半行，**不是**庭内旁路，**不是** Cast/Aid/variety 再拧。

---

## 1. 选题扫描（TIP-CT 后 · 必须真动作 · 偏向短征）

| 候选 | 裁决 | 理由 |
|------|------|------|
| Cast 粒子打磨 / Aid再拧 / todayRot/W1e / variety 再拧 / 完整 variety 经济 | **否** | 硬禁；刚 CLOSED |
| 半行独批（选页/Open 口号） | **否** | REJECT `d00ed84bc` 保持 |
| 庭内旁路再拧 / 第五花 / 推荐花 / 洗练·烙纹·转化·随机锻再拧 | **否** | 近饱和 / 硬禁 |
| 今日推 / 本周短征可冲 / 三冲 / FC 路径复读 | **否** | 已齐勿复读 |
| fest / 灾厄 / sx20 | **否** | 硬禁或另批 |
| 庭内顺手宝石 | **否 · 仍后置** | 庭面 gap 未亮 |
| 短征花样结算回看 | **后置** | 偏 variety 结算辅；弱于新导航真动作；易触「variety再拧」观感 |
| **短征选页「日差本可冲」（荐）** | **采纳** | 日更差异化硬缺口；真 enter；Layout 有空位；对位 GoalShortChase 范式 |

**择优结论：** 总控钉死回短征乐趣；庭内默认勿交；战斗层勿复读。扫线发现导航 **日差换本断链**（有日帽合计与单本今日推，无「今日未领日奖的另一本」一键）。故交日差本可冲；**证据表见 §2**；**为何不是半行见 E8**；**改题有证据**（庭内饱和 + 短征导航缺日差轨）。

---

## 2. 证据（现网只读 · 2026-10-14）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 总控 TIP-CT CLOSED · 连开 TIP-CU · **强烈偏向短征日更/乐趣**；庭内默认勿再交；宝石须庭面 gap | live `ea265a3ed` · 派单 | 选题边界 |
| E2 | 今日推 D13309 菜单 + D13320 门内 cue 齐；=**单本**轮换身份 | tip-CD/CO CLOSED | ≠跨本有奖分散 |
| E3 | 本周短征可冲 D13312：`goal_short_chase*` + F + `enter short-goal`；=**周计数** short:5 | EmberGoalShortChase · short.yml | ≠日维度换本 |
| E4 | FC/`shortpath`：追未领生涯首通；≠今日有奖不同本 | EmberShortPath | ≠日差 |
| E5 | `ember_p1_short`：**无**「日差」Icon / 无 `short-div` / 无 `short_div*` PAPI；Layout `'#PRSJDF #'` 尾空可落 **L** | short.yml · RunPapi | **缺口 + 可落格** |
| E6 | 每本 `rewardedToday` / `dayLeft` / `sx_day_left_sum` live；无 distinct 进度键 | EmberShortService · RunPapi | 计数原料在盘；缺聚合+chase |
| E7 | 庭内：四花+P+D+W/w+L+G+C+O（随机锻）齐；宝石仍无庭面 gap | afk.yml · D13315–25 | 勿回庭内；不交宝石 |
| E8 | **为何不是半行：** 主交付=新菜单格 **真 `enter short-div`** → 进另一本有日奖且今日未有奖通关图；Open 半行仅同批辅，**不可独批** | 本 RESEARCH | 过硬禁 |
| E9 | Cast/Aid/todayRot/variety + 导航三冲齐 | D13304–21 · D13313 | 勿复读主交 |
| E10 | 半行 REJECT `d00ed84bc` 保持否决 | backlog | 否决只口号 |

**一句话缺口：** 玩家看得见「今日还有日奖合计」，也能冲「今日推单本」与「周目标」，却 **不能一键跳到「今天还没领过日奖的另一本」**——日更换本乐趣断在选页动作。

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `short_div_n` / `_line` / `_chase*` / `_chase_ok` / `_done` + `enter short-div`（对位 GoalShortChase；解锁谓词用 `EmberShortPath.unlocked`）  
2. **chase：** 已解锁 ∧ dayLeft>0 ∧ rewardedToday==0；稳定序；可选跳过已有奖的今日推本  
3. **TrMenu：** 选页 **L**「日差本可冲」· lore 挂进度+chase · 真 enter · 灰态诚实  
4. **Open 辅半行同批：** 「日差本可冲见本页 L」· **不可独批**  
5. **零改**日帽 cap / S40–S58 / `weekly_goals.targets.short` / Cast / Aid / variety / 庭内菜单 / forgeroll·convert 价  

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅 Open 无进本 | 弱于真动作；默认不批 |
| W 抬表/sx20/复读已齐/庭内旁路 | 硬禁 |

---

*RESEARCH · 短征日差本可冲 · **待批 A** · 荐 **M** · TIP-CU / 拟 D13326 · ancestor TIP-CT CLOSED `ea265a3ed` · 半行 REJECT `d00ed84bc` 保持。*
