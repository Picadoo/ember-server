# RESEARCH · 短征结算「下一环」菜单化（settle 聊天钮去指令化 · ≠路径追同题 · ≠日差同题 · ≠今日推/周目标复读 · ≠半行 · ≠庭内旁路 · ≠抬表）· 2026-10-14

> **状态：** **CLOSED live** · feat `8c2dc7d95` · tip `374a142fd` · PASS `72e8e6a4e` · jar `1.65.821-d13328b.local` · 批 sha `e73f5847e` · TIP-CW · 2026-10-14 07:47 Asia/Shanghai · 结算下一环同题勿再交
> **起因：** 总控【连开·TIP-CW / 拟 D13328】上游 TIP-CV CLOSED live `f3defd0bb`（feat `c31c780a9` · tip `a206b9893` · jar `1.65.819-d13327.local` · PASS `f9e4ca402` · 批 sha `ac2889160` · 短征选页「路径追」齐 · **路径追同题勿再交**）。请立刻交下一档硬债 docs-only 待批 A · 荐 M。**优先强烈偏向：短征日更/乐趣系统**（真可点或房内可感）。庭内工坊旁路已近饱和——**默认勿再交庭内旁路**。宝石仍须庭面先亮 gem gap。日差软目标发奖 / weekly_goals 写入须总控另批——**本窗默认不做**。settle 聊天钮去指令化可另窗——**本窗采纳**（路径追已菜单化、实扫仍有裸指令聊天钮）。Cast+BossAid+EliteAid+todayRot+variety(W1a–d)+庭内全弧+今日推/冲一下/周短征/三冲+日差本可冲+路径追 **已齐勿复读**。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-CV CLOSED live `f3defd0bb` / feat `c31c780a9` / tip `a206b9893` / PASS `f9e4ca402` / jar `1.65.819-d13327.local` / 批 `ac2889160` · D13327
> **硬禁：** **零** 半行独批 · **零** Cast粒子主交付 · **零** Aid/variety再拧 · **零** todayRot/W1e独批 · **零** 庭内旁路再拧 · **零** 日差同题 · **零** 路径追同题 · **零** sx20 · **零** 抬日帽/S/targets · **零** Pack6 · **零** 开R/K3 · **零** 关观察 · **零** 灾厄/fest重开（须另批）· **零** 教裸指令 · **零** densify · **零** stage脏runtime · **零** 改 ShortPath resolve 算法

---

## 0. 一句话

庭内旁路近饱和；短征战斗/花样/导航冲口（今日推·周目标·三冲·日差·路径追）已齐。D13327§6⑥ 明文后置「settle 聊天钮去指令化」——实扫 `EmberShortService.settle` 有奖支仍 ConfirmTokens 走 `/corerpg p1 shortpath` / `forge`，并教 `/corerpg p1 shortpath day`。本债=结算 **下一环真可点菜单**，**不是**路径追同题，**不是**日差同题，**不是**半行，**不是**庭内旁路。

---

## 1. 选题扫描（TIP-CV 后 · 必须真动作 · 偏向短征）

| 候选 | 裁决 | 理由 |
|------|------|------|
| Cast 粒子打磨 / Aid再拧 / todayRot/W1e / variety 再拧 / 完整 variety 经济 | **否** | 硬禁；刚 CLOSED |
| 半行独批 | **否** | REJECT `d00ed84bc` 保持 |
| 庭内旁路再拧 / 宝石 | **否** | 近饱和 / 庭面 gap 未亮 |
| 路径追同题 / 改 resolve | **否** | 派单钉死勿再交；已 CLOSED |
| 日差本可冲同题 / 日差软目标发奖 | **否** | 派单钉死勿再交；经济须另批 |
| 今日推 / 周目标 / 三冲 / D406 首通同屏复读 | **否** | 已齐勿复读 |
| 短征花样结算回看 / variety 经济发核心 | **否 · 本窗** | D13327§6③；易触 variety再拧观感 |
| fest / 灾厄 / sx20 | **否** | 硬禁或另批 |
| **短征结算「下一环」菜单化（荐）** | **采纳** | D13327§6⑥；settle 仍裸指令轨；路径追菜单已齐但结算未接；真可点 |

**择优结论：** 总控钉死短征乐趣；路径追同题禁交；战斗层勿复读。实扫发现 **有奖 settle 下一环仍 chat ConfirmTokens + 裸指令文案**——日更「通关→再冲/改追/花料」乐趣断在去指令化。故交结算下一环；**证据表见 §2**；**为何不是半行见 E8**；**改题有证据**。

---

## 2. 证据（现网只读 · 2026-10-14）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 总控 TIP-CV CLOSED · 连开 TIP-CW · **强烈偏向短征日更/乐趣**；庭内默认勿再交；**路径追同题勿再交** | live `f3defd0bb` · 派单 | 选题边界 |
| E2 | D13327 DESIGN §6⑥ 明文后置「settle 聊天钮去指令化大扫（可另窗）」 | DESIGN-path-chase | **合法未 CLOSED 后置** |
| E3 | 今日推/周目标/日差/三冲/路径追冲口 **已齐** | short.yml · short_path.yml | ≠再交同轨 |
| E4 | `EmberShortService.settle` 有奖：`[再进·KEY]`→`/corerpg p1 enter KEY`；`[工坊]`→`/corerpg p1 forge`；`[短征路径]`→`/corerpg p1 shortpath` | ShortService.java L135–144 | **结算下一环仍走 corerpg 裸指令轨** |
| E5 | 日奖满支同样挂 `[短征路径]`→`shortpath`；首通尽时文案「可改 /corerpg p1 shortpath day」 | ShortService L141–160 | **教裸指令硬证** |
| E6 | `EmberShortRules.spendChaseButtonCmd()` = `/corerpg p1 forge`；另发 `[去工坊]` 聊天钮 | ShortRules L226–228 · ShortService L169–172 | 工坊亦 chat 指令 |
| E7 | D13327 已交付 `ember_p1_short_path` + `enter short-path`；**结算「短征路径」钮仍开 chat shortpath，未开薄路径页** | tip CLOSED · ShortService | **菜单齐但结算未接 = 本债** |
| E8 | TrMenu **无** `ember_p1_short_settle`；`EmberTodayRotFeel.settleCue` 仍 chat `[再进今日推]`→`/corerpg p1 enter short-today` | menus ls · TodayRotFeel L89–96 | 缺结算聚合页 |
| E9 | **为何不是半行：** 主交付=薄 settle 页（再进/选页/路径追/工坊真开菜单）+ 插件改 settle 下一环入口；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E10 | Cast/Aid/todayRot/variety + 庭内全弧 + 日差 + 路径追齐 | D13304–27 | 勿复读主交 |
| E11 | 半行 REJECT `d00ed84bc` 保持否决 | backlog | 否决只口号 |

**一句话缺口：** 玩家选页已能点路径追，但 **通关结算仍把「下一环」钉在聊天 ConfirmTokens + 裸指令文案**——乐趣断在去指令化。

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** 有奖 settle 下一环改为打开薄页命令（或按钮目标改为 `/ember_p1_short_settle` / `/ember_p1_short` / `/ember_p1_short_path` / `/ember_p1_forge`）；清「可改 /corerpg p1 shortpath day」类裸指令文案；`spendChaseButtonCmd` 改菜单开；todayRot settleCue 同批改菜单/enter 别名不教裸打  
2. **菜单：** 薄页 `ember_p1_short_settle`：再进本图（真 enter 当前本）/ 回选页 / 路径追（开 `ember_p1_short_path`）/ 去工坊（开 forge 菜单）；灰态诚实（日奖满·体力不足）  
3. **Open/半行：** 本窗 **不做**选页半行独档；若需辅指针仅同批且不可独批  
4. **零改**日帽 / S40–S58 / targets.short / Cast / Aid / variety / 庭内 / 日差 L / 今日推 D / 周目标 F / 路径追薄页逻辑 / ShortPath resolve 算法

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮命令无薄页聚合 | 弱于真动作；默认不批 |
| W 抬表/sx20/路径追同题/日差同题/庭内/Cast·Aid·variety再拧 | 硬禁 |

---

*RESEARCH · 短征结算下一环菜单化 · **CLOSED live** · `8c2dc7d95` / `374a142fd` · PASS `72e8e6a4e` · TIP-CW / D13328 · 半行 REJECT `d00ed84bc` 保持 · 结算下一环同题勿再交。*

## STATUS: **CLOSED live** · `8c2dc7d95` / `374a142fd` · PASS `72e8e6a4e` · D13328 · TIP-CW · 2026-10-14 07:47 Asia/Shanghai
