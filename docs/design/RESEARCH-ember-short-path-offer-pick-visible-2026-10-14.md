# RESEARCH · ShortPath.offerPick / applyAndReply「短征路径选打」菜单化 · 拟 D13339 · TIP-DH

> **状态：** **已批 A · 方案 M** · APPROVED A·M · 拟 D13339 · TIP-DH · 可施工 · 勿勾 CLOSED · 2026-10-14 11:32 Asia/Shanghai
> **起因：** 总控【派·TIP-DH 选题】默认钉 ShortPath.offerPick「短征路径选打」菜单化 · 拟 D13339。祖先 TIP-DG 抽测 PASS `7ec45e0a1` · feat `c81c9e7e2` · jar `1.65.831-d13338` · CLOSED 收口。DESIGN-DG §6⑦ 后置其他 *Path 逐支另窗。选题优先级：①挂机庭资源环加厚（须新硬债；旁路近饱和·gem gap 未亮则跳过）②短征日更乐趣（非sx20；非已齐路径追页/settle；**offerPick 入口仍教指令**）③已齐勿复读：各 settle 下一环（CW–DD）+ 精选·硬本·团本选打 DE–DG ④默认钉 ShortPath.offerPick（可改题须证据压过本支）⑤AbyssPath/TrialPath / legacy evacuate 后置。**禁：** 半行独批 · 已齐 settle/选打/路径追同题复读 · 抬日表/掉落/体力价 · sx20 / densify / Pack6 / 开 K3 / 关观察 · variety 完整经济 / Cast·Aid 再拧 / 庭内旁路 · 教裸指令主 UI · 全族 Path 大扫 · 零改 `ember_p1_short_settle` / RaidPath/FeaturedPath/ChallengePath。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-DG CLOSED live `f8fef8025` · feat `c81c9e7e2` · tip `fd80fd696` · PASS `7ec45e0a1` · jar `1.65.831-d13338.local` · 批 sha `fd80fd696` · menu tip `b9b8f688f` · D13338

---

## 1. 选题扫表

| 候选 | 结论 | 理由 |
|------|------|------|
| ① 挂机庭资源环加厚 / 宝石 | **跳过** | 旁路 TIP-BY–CT **CLOSED**；afk I/Open **零** `recipe_gap_gem`/`forge_gap_gem`；宝石同构未过 |
| ② 短征日更乐趣（Cast/Aid/variety/日差/路径追页/settle/soft/回看） | **跳过已齐支** | 上述薄债 **已齐**；sx20 硬禁；**但 offerPick 入口仍硬债**（见下） |
| ③ 已齐 settle + 精选·硬本·团本选打 | **跳过** | CW–DD settle + TIP-DE/DF/DG **全齐勿复读** |
| ④ AbyssPath.offerPick / TrialPath.offerPick | **后置本窗** | 派单明文后置；逐支另窗；禁全族大扫；内容优先短征 |
| ⑤ legacy evacuate | **后置** | 须另窗证据 |
| 工坊升阶下一环 / 图装下一环 | **后置本窗** | 非本派明文优先 |
| **ShortPath.offerPick / 周要约 / 无参 shortpath（荐）** | **采纳** | 薄页已在 TIP-CV；settle「换一条」已指菜单；**offerPick/周要约/无参/选页 Open 仍 ConfirmTokens 教 shortpath**；镜像 DE/DF/DG；≠路径追同题；≠ settle 同题 |

**择优结论：** ①无新硬债 → ②已齐路径追页/settle 等，但 **短征选打入口仍教指令**（内容优先压过试炼）→ ③已齐勿复读 → **默认钉 ShortPath.offerPick**。未改题：无更硬挂机/短日常 fun 债压过本支（试炼/深渊选打后置；挂机 gem gap 未亮）。**证据表见 §2**；**为何不是半行见 E11**。

---

## 2. 证据表

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E1 | 总控 TIP-DG CLOSED · 派 TIP-DH 默认钉 ShortPath.offerPick | 派单 | 合法开窗 |
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · backlog TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征 Cast/Aid/todayRot/variety/日差/路径追页/settle/soft/回看已齐；sx20 禁 | tips CV–CY 等 | **已齐支勿复读**；入口仍另债 |
| E4 | settle CW–DD + TIP-DE/DF/DG 选打 **已齐** | tips | **禁止同题复读** |
| E5 | DESIGN-DG §6⑦ 后置其他 *Path 逐支；内容优先短征 | DESIGN-DG §6 · 派单 | **合法未 CLOSED 后置支** |
| E6 | `offerPick` ConfirmTokens 四钮 → `/corerpg p1 shortpath fc\|day\|late\|clear` | EmberShortPath L184–192 | **选打仍教裸指令** |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ01` → `offerPick` | L195–216 | **周要约入口同债** |
| E8 | 命令无参 → glance + `offerPick`；用法仍教 `fc\|day\|late\|clear` | EmberCommand L3283–3313 | **无参入口无开薄页** |
| E9 | `applyAndReply`「换一条」已 `SETTLE_PATH_CMD=/ember_p1_short_path`；「进本」仍 ConfirmTokens→裸 `enter` | EmberShortPath L263–272 · EmberShortRules L232 | **换一条已菜单；进本仍可清主 UI** |
| E10 | TrMenu **有** `ember_p1_short_path`（TIP-CV）；选页 Icon 0 已开薄页；选页 Open 仍 `command: corerpg p1 shortpath` → 再弹 ConfirmTokens | menus/ember_p1_short_path.yml · ember_p1_short.yml Open | **薄页已在；入口旁路未清** |
| E11 | **为何不是半行：** 主交付=插件改入口开已有薄页 + 清教指令主路径；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E12 | AbyssPath.offerPick 仍教 abysspath；TrialPath 仍教 trialpath；**无** `ember_p1_abyss_path`/`ember_p1_trial_path` | EmberAbyssPath · EmberTrialPath · menus/ | **后置本窗（§6）** |
| E13 | ≠ 路径追同题（TIP-CV）；≠ 短征 settle 同题；≠ 团本/硬本/精选选打同题；≠ 全族 Path 大扫 | 本 RESEARCH | 边界 |

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `offerPick` + 无参 `shortpath` + 周要约 → 开 `/ember_p1_short_path`；清 ConfirmTokens 教 `shortpath`/`enter` 为主 UI；可选复用 `short_path_*` PAPI；单测
2. **菜单：** 既有薄页接线/补洞（非必新建）；清选页 Open `corerpg p1 shortpath` 旁路
3. **经济：** **零** 改日表 / 掉落 / 体力价 / ShortPath resolve
4. **Open/半行：** 若需辅指针仅同批且不可独批
5. **零改** short_settle / 其他 settle / RaidPath/FeaturedPath/ChallengePath / Cast / Aid / variety / 庭内 / soft-reward；**AbyssPath/TrialPath / legacy evacuate 后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无开薄页 | 弱于真动作；默认不批 |
| W 抬表/sx20/已齐同题复读/variety再拧/庭内/路径追同题/团本·硬本·精选选打同题/全族Path大扫/Abyss·Trial同窗/legacy evacuate 大扫 | 硬禁 |

---

*RESEARCH · ShortPath.offerPick / applyAndReply 短征路径选打菜单化 · **已批 A · 方案 M** · APPROVED A·M · TIP-DH / D13339 · 半行 REJECT `d00ed84bc` 保持 · ancestor TIP-DG CLOSED · ≠路径追同题/short_settle · Abyss/Trial 后置 · 2026-10-14 11:32 Asia/Shanghai.*

## STATUS: **已批 A · 方案 M** · APPROVED A·M · 可施工 · 勿勾 CLOSED · L不批 · D13339 · TIP-DH · 2026-10-14 11:32 Asia/Shanghai
