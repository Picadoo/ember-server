# RESEARCH · AbyssPath.offerPick / applyAndReply「深渊路径选打」菜单化 · 拟 D13340 · TIP-DI

> **状态：** **待批 A** · 荐 M · L不批 · 勿勾 CLOSED · TIP-DI · 2026-10-14 11:50 Asia/Shanghai
> **起因：** 总控【派·TIP-DI 选题】默认钉 AbyssPath.offerPick「深渊路径选打」菜单化 · 拟 D13340。祖先 TIP-DH 抽测 PASS `191f0be8c` · feat `ec5264ef9` · jar `1.65.832-d13339` · CLOSED live `016bfef1f`。DESIGN-DH §6⑦ 明文后置 Abyss/Trial 等逐支另窗。选题优先级：①挂机庭资源环加厚（须新硬债；旁路近饱和·gem gap 未亮则跳过）②短征日更乐趣（非sx20；非已齐路径/选打 TIP-DH）③已齐勿复读：各 settle 下一环（CW–DD）+ 精选·硬本·团本·短征选打 DE–DH ④默认钉 AbyssPath.offerPick（可改题须证据压过本支）⑤TrialPath / legacy evacuate 后置。**禁：** 半行独批 · 已齐 settle/短征选打同题复读 · 抬层费/强度/掉落/体力价/层表 · sx20 / densify / Pack6 / 开 K3 / 关观察 · variety 完整经济 / Cast·Aid 再拧 / 庭内旁路 · 教裸指令主 UI · 全族 Path 大扫 · 零改 `ember_p1_abyss_settle` / ShortPath/RaidPath/FeaturedPath/ChallengePath。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-DH CLOSED live `016bfef1f` · feat `ec5264ef9` · tip `fbe2cce5c` · PASS `191f0be8c` · jar `1.65.832-d13339.local` · 批 sha `fbe2cce5c` · menu tip `9a81a840e` · D13339

---

## 1. 选题扫表

| 候选 | 结论 | 理由 |
|------|------|------|
| ① 挂机庭资源环加厚 / 宝石 | **跳过** | 旁路 TIP-BY–CT **CLOSED**；afk I/Open **零** `recipe_gap_gem`/`forge_gap_gem`；宝石同构未过 |
| ② 短征日更乐趣 | **跳过** | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/选打 TIP-DH **已齐**；sx20 硬禁 |
| ③ 已齐 settle + 选打 DE–DH | **跳过** | CW–DD settle + TIP-DE–DH 选打 **全齐勿复读** |
| ④ TrialPath.offerPick | **后置本窗** | 派单/DESIGN-DH §6⑦ 明文后置；逐支另窗；禁全族大扫 |
| ⑤ legacy evacuate | **后置** | 须另窗证据 |
| 工坊升阶下一环 / 图装下一环 | **后置本窗** | 非本派明文优先 |
| **AbyssPath.offerPick / applyAndReply（荐）** | **采纳** | DESIGN-DH §6⑦；选打仍 ConfirmTokens 教 abysspath；**无**选打专用薄页；镜像 TIP-DE–DH；≠ settle 同题；≠ 短征选打同题 |

**择优结论：** ①②无新薄债 → ③已齐勿复读 → 默认钉 DESIGN-DH §6⑦ 明文后置本支。实扫深渊路径 `offerPick`/`applyAndReply`/无参/周要约仍 ConfirmTokens 教裸 `abysspath`、TrMenu **无** `ember_p1_abyss_path`——内容闭环断在**深渊路径选打**（≠ TIP-DB 结算下一环；≠ TIP-DH 短征选打）。故交本支；**证据表见 §2**；**为何不是半行见 E12**；**改题有证据**（未改题：无更硬挂机/短日常 fun 债压过本支；Trial 后置）。

---

## 2. 证据表

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E1 | 总控 TIP-DH CLOSED · 派 TIP-DI 默认钉 AbyssPath.offerPick | 派单 | 合法开窗 |
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · backlog TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债+选打 TIP-DH 已齐；sx20 禁 | tips CV–DH | **短征无薄窗新硬债** |
| E4 | settle CW–DD + TIP-DE–DH 选打 **已齐** | tips | **禁止同题复读** |
| E5 | DESIGN-DH §6⑦ 明文后置 AbyssPath/TrialPath 逐支 | DESIGN-DH §6 | **合法未 CLOSED 后置** |
| E6 | `offerPick` ConfirmTokens 三钮 → `/corerpg p1 abysspath push\|farm\|clear` | EmberAbyssPath L94–101 | **选打仍教裸指令** |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ07` → `offerPick` | L104–126 | **周要约入口同债** |
| E8 | `applyAndReply`「换一条」→ `/corerpg p1 abysspath`；进本 ConfirmTokens→`abyss next`/`abyss N`；深渊表→`/corerpg p1 abyss` | L167–172 | **选后下一步仍聊天钮** |
| E9 | 命令无参 → glance + `offerPick`；用法仍教 `push\|farm\|clear` | EmberCommand L3813–3843 | **无参入口无开薄页** |
| E10 | TrMenu **有** `ember_p1_abyss` / `ember_p1_abyss_settle`；**无** `ember_p1_abyss_path` | menus/ | **缺选打专用薄页** |
| E11 | TrialPath.offerPick 仍教 trialpath；**无** `ember_p1_trial_path` | EmberTrialPath · menus/ | **后置本窗（§6）** |
| E12 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E13 | ≠ 深渊 settle 同题；≠ 短征/团本/硬本/精选选打同题；≠ 全族 Path 大扫 | 本 RESEARCH | 边界 |

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `offerPick` + 无参 `abysspath` + 周要约 + `applyAndReply` 下一步→开 `/ember_p1_abyss_path`；清 ConfirmTokens 教指令为主 UI；可选复用 `abyss_path_*` PAPI；单测
2. **菜单：** 新薄页 `ember_p1_abyss_path`：冲层/刷层/清空/深渊表/冒险/可选工坊；lore 挂路径
3. **经济：** **零** 改层费 / 强度 / 掉落 / 体力价 / 层表 / 深渊语义
4. **Open/半行：** 若需辅指针仅同批且不可独批
5. **零改** abyss_settle / 其他 settle 薄页 / ShortPath/RaidPath/FeaturedPath/ChallengePath / Cast / Aid / variety / 庭内 / soft-reward；**TrialPath / legacy evacuate 后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无薄页 | 弱于真动作；默认不批 |
| W 抬表/sx20/已齐同题复读/variety再拧/庭内/短征选打同题/深渊settle同题/全族Path大扫/Trial同窗/legacy evacuate 大扫 | 硬禁 |

---

*RESEARCH · AbyssPath.offerPick / applyAndReply 深渊路径选打菜单化 · **待批 A** · 荐 M · TIP-DI / 拟 D13340 · 半行 REJECT `d00ed84bc` 保持 · ancestor TIP-DH CLOSED `016bfef1f`.*

## STATUS: **待批 A** · 荐 M · L不批 · 勿勾 CLOSED · 拟 D13340 · TIP-DI · 2026-10-14 11:50 Asia/Shanghai
