# RESEARCH · TrialPath.offerPick / applyAndReply「试炼路径选打」菜单化 · 拟 D13341 · TIP-DJ

> **状态：** **待批 A** · 荐方案 **M** · 拟 D13341 · TIP-DJ · 勿勾 CLOSED · 2026-10-14 12:05 Asia/Shanghai
> **起因：** 总控【派·TIP-DJ 选题】默认钉 TrialPath.offerPick「试炼路径选打」菜单化 · 拟 D13341。祖先 TIP-DI 抽测 PASS `22aed762e` · feat `724f53462` · jar `1.65.833-d13340` · CLOSED live `326f7d3bd`。DESIGN-DI §6⑦ 明文后置 TrialPath 等逐支另窗。选题优先级：①挂机庭资源环加厚（须新硬债；旁路近饱和·gem gap 未亮则跳过）②短征日更乐趣（非sx20；非已齐路径/选打 TIP-DH）③已齐勿复读：各 settle 下一环（CW–DD）+ 精选·硬本·团本·短征·深渊选打 DE–DI ④默认钉 TrialPath.offerPick（可改题须证据压过本支）⑤签律/破绽/步态 / legacy evacuate 后置。**禁：** 半行独批 · 已齐 settle/深渊选打同题复读 · 抬伤害/CD表/掉落/体力价 · sx20 / densify / Pack6 / 开 K3 / 关观察 · variety 完整经济 / Cast·Aid 再拧 / 庭内旁路 · 教裸指令主 UI · 全族 Path 大扫 · 零改 `ember_p1_abyss_path` / ShortPath/RaidPath/FeaturedPath/ChallengePath。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-DI CLOSED live `326f7d3bd` · feat `724f53462` · tip `39e38c8b1` · PASS `22aed762e` · jar `1.65.833-d13340.local` · 批 sha `39e38c8b1` · menu tip `27541e2cb` · D13340

---

## 1. 选题扫表

| 候选 | 结论 | 理由 |
|------|------|------|
| ① 挂机庭资源环加厚 / 宝石 | **跳过** | 旁路 TIP-BY–CT **CLOSED**；afk I/Open **零** `recipe_gap_gem`/`forge_gap_gem`；宝石同构未过 |
| ② 短征日更乐趣 | **跳过** | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/选打 TIP-DH **已齐**；sx20 硬禁 |
| ③ 已齐 settle + 选打 DE–DI | **跳过** | CW–DD settle + TIP-DE–DI 选打 **全齐勿复读** |
| ④ 签律/破绽/步态等 *Path | **后置本窗** | 派单/DESIGN-DI §6⑦ 明文后置；逐支另窗；禁全族大扫 |
| ⑤ legacy evacuate | **后置** | 须另窗证据 |
| 工坊升阶下一环 / 图装下一环 | **后置本窗** | 非本派明文优先 |
| **TrialPath.offerPick / applyAndReply（荐）** | **采纳** | DESIGN-DI §6⑦；选打仍 ConfirmTokens 教 trialpath；**无**选打专用薄页；镜像 TIP-DE–DI；≠ 深渊选打同题 |

**择优结论：** ①②无新薄债 → ③已齐勿复读 → 默认钉 DESIGN-DI §6⑦ 明文后置本支。实扫试炼路径 `offerPick`/`applyAndReply`/无参/周要约仍 ConfirmTokens 教裸 `trialpath`、TrMenu **无** `ember_p1_trial_path`——内容闭环断在**试炼路径选打**（≠ TIP-DI 深渊选打）。故交本支；**证据表见 §2**；**为何不是半行见 E12**；**改题有证据**（未改题：无更硬挂机/短日常 fun 债压过本支；签律/破绽/步态后置）。

---

## 2. 证据表

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E1 | 总控 TIP-DI CLOSED · 派 TIP-DJ 默认钉 TrialPath.offerPick | 派单 | 合法开窗 |
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · backlog TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债+选打 TIP-DH 已齐；sx20 禁 | tips CV–DH | **短征无薄窗新硬债** |
| E4 | settle CW–DD + TIP-DE–DI 选打 **已齐** | tips | **禁止同题复读** |
| E5 | DESIGN-DI §6⑦ 明文后置 TrialPath / 签律·破绽·步态逐支 | DESIGN-DI §6 | **合法未 CLOSED 后置** |
| E6 | `offerPick` ConfirmTokens 四钮 → `/corerpg p1 trialpath cautious\|aggressive\|pressure\|clear` | EmberTrialPath.offerPick | **选打仍教裸指令** |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ06` → `offerPick` | EmberTrialPath | **周要约入口同债** |
| E8 | `applyAndReply` 下一步 ConfirmTokens→进本 / `pledgepath` / `counterpath` / `trialpath` | EmberTrialPath.applyAndReply | **选后下一步仍聊天钮** |
| E9 | 命令无参 → glance + `offerPick`；用法仍教 `cautious\|aggressive\|pressure\|clear`；nudge 改路径仍教手打 | EmberCommand trialpath | **无参入口无开薄页** |
| E10 | TrMenu **无** `ember_p1_trial_path`（亦无其他 trial 选打页） | menus/ | **缺选打专用薄页** |
| E11 | 签律/破绽/步态等 *Path.offerPick 仍教裸指令 | 命令族 | **后置本窗（§6）** |
| E12 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E13 | ≠ 深渊选打同题；≠ 短征/团本/硬本/精选选打同题；≠ 全族 Path 大扫 | 本 RESEARCH | 边界 |

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `offerPick` + 无参 `trialpath` + 周要约 + `applyAndReply` 下一步→开 `/ember_p1_trial_path`；清 ConfirmTokens 教指令为主 UI；可选 `trial_path_*` PAPI；单测
2. **菜单：** 新薄页 `ember_p1_trial_path`：稳慎/猛开/加压/清空/冒险/可选工坊·进本；lore 挂路径
3. **经济：** **零** 改伤害 / CD 表 / 掉落 / 体力价 / 试炼语义
4. **Open/半行：** 若需辅指针仅同批且不可独批
5. **零改** abyss_path / 其他选打·settle 薄页 / ShortPath/RaidPath/FeaturedPath/ChallengePath / Cast / Aid / variety / 庭内 / soft-reward；**签律/破绽/步态 / legacy evacuate 后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无薄页 | 弱于真动作；默认不批 |
| W 抬表/sx20/已齐同题复读/variety再拧/庭内/深渊选打同题/全族Path大扫/签律·破绽同窗/legacy evacuate 大扫 | 硬禁 |

---

*RESEARCH · TrialPath.offerPick / applyAndReply 试炼路径选打菜单化 · **待批 A** · 荐方案 **M** · TIP-DJ / D13341 · 半行 REJECT `d00ed84bc` 保持 · ancestor TIP-DI CLOSED · ≠深渊选打同题 · 签律/破绽/步态后置 · 2026-10-14 12:05 Asia/Shanghai.*

## STATUS: **待批 A** · 荐方案 **M** · 勿勾 CLOSED · L不批 · 拟 D13341 · TIP-DJ · 2026-10-14 12:05 Asia/Shanghai
