# RESEARCH · PledgePath.offerPick / applyAndReply「誓约路径选打」菜单化 · 拟 D13342 · TIP-DK

> **状态：** **CLOSED live** · feat `42fc81f49` · tip `6ee232766` · PASS `14a003c56` · jar `1.65.835-d13342.local` · 批 sha `6ee232766` · TIP-DK · 2026-10-14 12:41 Asia/Shanghai · 誓约路径选打菜单化同题勿再交 · 下一窗：破绽 CounterPath / 步态 StridePath 等 *Path.offerPick 逐支（禁全族大扫）/ 挂机若新硬债 / legacy evacuate 后置；誓约路径选打本支已齐
> **起因：** 总控【派下一窗 tip · ancestor TIP-DJ CLOSED】。祖先 TIP-DJ CLOSED live `f63d095b2` · feat `845a99920` · jar `1.65.834-d13341.local` · PASS `61e500086` · menu `9dabb653e` · APPROVED `dbbc499b0`。DESIGN-DJ §6⑦ 明文后置签律/破绽/步态等 *Path.offerPick 逐支另窗。选题闸：①挂机庭资源环加厚（须新硬债；旁路近饱和·gem gap 未亮则跳过）②短征日更乐趣（非sx20；非已齐路径）③若无新硬债：只钉一支剩余路径选打（Pledge/Counter/Stride 等，以 ConfirmTokens 教裸指令证据选一支）· 镜像 DE–DJ。**禁：** 半行独批 · 已齐 settle/选打同题复读 · 抬伤害/CD表/掉落/体力价 · sx20 / densify / Pack6 / 开 K3 / 关观察 · variety 完整经济 / Cast·Aid 再拧 / 庭内旁路 · 教裸指令主 UI · 全族 Path 大扫。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-DJ CLOSED live `f63d095b2` · feat `845a99920` · tip `dbbc499b0` · PASS `61e500086` · jar `1.65.834-d13341.local` · 批 sha `dbbc499b0` · menu tip `9dabb653e` · D13341

---

## 1. 选题扫表

| 候选 | 结论 | 理由 |
|------|------|------|
| ① 挂机庭资源环加厚 / 宝石 | **跳过** | 旁路 TIP-BY–CT **CLOSED**；afk I/Open **零** `recipe_gap_gem`/`forge_gap_gem`（仅 enhance/upgrade/refine gap）；宝石同构未过 |
| ② 短征日更乐趣 | **跳过** | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/选打 TIP-DH **已齐**；sx20 硬禁 |
| ③ 已齐 settle + 选打 DE–DJ | **跳过** | CW–DD settle + TIP-DE–DJ 选打 **全齐勿复读** |
| ④ CounterPath 破绽 / StridePath 步态 | **后置本窗** | 派单/DESIGN-DJ §6⑦ 明文逐支；本窗只钉一支 |
| ⑤ legacy evacuate | **后置** | 须另窗证据 |
| 工坊升阶下一环 / 图装下一环 | **后置本窗** | 非本派明文优先 |
| **PledgePath.offerPick / applyAndReply（荐）** | **采纳** | DESIGN-DJ §6⑦ 签律首位；选打仍 ConfirmTokens 教 pledgepath；**无**选打专用薄页；镜像 TIP-DE–DJ；≠ 试炼选打同题 |

**择优结论：** ①②无新薄债 → ③已齐勿复读 → 落 DESIGN-DJ §6⑦ 后置桶 · **只钉一支**。三支实扫均硬（Pledge/Counter/Stride 皆 ConfirmTokens 教裸指令、皆无对应 `*_path` 选打薄页）；**择 Pledge**：①签律/誓约列表首位 ②与刚 CLOSED Trial 内容相邻 ③周要约+Q06 全链镜像 Trial ④既有 `ember_p1_pledge` 是规则开关≠选打——缺 `ember_p1_pledge_path`。故交本支；**证据表见 §2**；**为何不是半行见 E12**；**改题有证据**（未改题：无更硬挂机/短日常 fun 债压过本支；破绽/步态后置）。

---

## 2. 证据表

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E1 | 总控 TIP-DJ CLOSED · 派下一窗：签律/破绽/步态逐支；只钉一支 | 派单 | 合法开窗 |
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · backlog TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债+选打 TIP-DH 已齐；sx20 禁 | tips CV–DH | **短征无薄窗新硬债** |
| E4 | settle CW–DD + TIP-DE–DJ 选打 **已齐** | tips | **禁止同题复读** |
| E5 | DESIGN-DJ §6⑦ 明文后置签律/破绽/步态逐支 | DESIGN-DJ §6 | **合法未 CLOSED 后置** |
| E6 | `offerPick` ConfirmTokens 四钮 → `/corerpg p1 pledgepath lean\|reverse\|both\|clear` | EmberPledgePath.offerPick L92–100 | **选打仍教裸指令** |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ06` → `offerPick` | EmberPledgePath | **周要约/Q06 入口同债** |
| E8 | `applyAndReply` 下一步 ConfirmTokens→`pledge` / `modes` / `pledgepath` | EmberPledgePath.applyAndReply L151–154 | **选后下一步仍聊天钮** |
| E9 | 命令无参 → glance + `offerPick`；用法仍教 `lean\|reverse\|both\|clear`；nudge 改路径仍教手打 | EmberCommand pledgepath | **无参入口无开薄页** |
| E10 | TrMenu **有** `ember_p1_pledge`（toggle lean/reverse）· **无** `ember_p1_pledge_path` | menus/ | **缺选打专用薄页** |
| E11 | CounterPath.offerPick→counterpath wall\|whiff\|break\|clear；StridePath.offerPick→stridepath push\|bail\|bare\|clear；均无对应选打薄页 | Java · menus | **后置本窗（§6）** |
| E12 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E13 | ≠ 试炼选打同题；≠ 深渊/短征/团本/硬本/精选选打同题；≠ 全族 Path 大扫；≠ 复开 D481 | 本 RESEARCH | 边界 |
| E14 | 为何钉 Pledge 非 Counter/Stride | 见 §1 择优 | 最硬一支 |

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `offerPick` + 无参 `pledgepath` + 周要约/Q06 + `applyAndReply` 下一步→开 `/ember_p1_pledge_path`；清 ConfirmTokens 教指令为主 UI；可选 `pledge_path_*` PAPI；单测
2. **菜单：** 新薄页 `ember_p1_pledge_path`：限药/逆行/双挂/清空/誓约页/进阶/冒险/可选工坊；lore 挂路径
3. **经济：** **零** 改伤害 / CD 表 / 掉落 / 体力价 / 誓约规则语义
4. **Open/半行：** 若需辅指针仅同批且不可独批
5. **零改** trial_path / 其他选打·settle 薄页 / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath / Cast / Aid / variety / 庭内 / soft-reward / `ember_p1_pledge` Layout 主交；**破绽/步态 / legacy evacuate 后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无薄页 | 弱于真动作；默认不批 |
| W 抬表/sx20/已齐同题复读/variety再拧/庭内/试炼选打同题/全族Path大扫/破绽·步态同窗/legacy evacuate 大扫/复开 D481 | 硬禁 |

---

*RESEARCH · PledgePath.offerPick / applyAndReply 誓约路径选打菜单化 · **CLOSED live** · `42fc81f49` / `6ee232766` · PASS `14a003c56` · TIP-DK / D13342 · 半行 REJECT `d00ed84bc` 保持 · 誓约路径选打菜单化同题勿再交 · 下一窗：破绽 CounterPath / 步态 StridePath 等 *Path.offerPick 逐支（禁全族大扫）/ 挂机若新硬债 / legacy evacuate 后置；誓约路径选打本支已齐.*

## STATUS: **CLOSED live** · `42fc81f49` / `6ee232766` · PASS `14a003c56` · D13342 · TIP-DK · 2026-10-14 12:41 Asia/Shanghai
