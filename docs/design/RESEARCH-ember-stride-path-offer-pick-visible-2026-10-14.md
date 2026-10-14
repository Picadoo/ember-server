# RESEARCH · StridePath.offerPick / applyAndReply「步态路径选打」菜单化 · 拟 D13344 · TIP-DM

> **状态：** **待批 A** · 荐 M · 拟 D13344 · TIP-DM · 2026-10-14 13:03 Asia/Shanghai · 勿勾 CLOSED
> **起因：** 总控【派下一窗 tip · ancestor TIP-DL CLOSED】。祖先 TIP-DL CLOSED live `999b7a383` · feat `2b7617600` · jar `1.65.836-d13343.local` · PASS `2b54c0d0b` · menu `c2b3b37bd` · APPROVED `7490a03eb`。DESIGN-DL §6⑦ 明文后置步态 StridePath.offerPick 等 *Path.offerPick 逐支另窗。选题闸：①挂机庭资源环加厚（须新硬债；旁路近饱和·gem gap 未亮则跳过）②短征日更乐趣（非sx20；非已齐路径）③若无新硬债：只钉 **StridePath 步态选打**（或实扫仍残留的一支 *Path.offerPick）· 镜像 DE–DL。**禁：** 半行独批 · 已齐 settle/选打同题复读 · 抬伤害/CD表/掉落/体力价 · sx20 / densify / Pack6 / 开 K3 / 关观察 · variety 完整经济 / Cast·Aid 再拧 / 庭内旁路 · 教裸指令主 UI · 全族 Path 大扫。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-DL CLOSED live `999b7a383` · feat `2b7617600` · tip `7490a03eb` · PASS `2b54c0d0b` · jar `1.65.836-d13343.local` · 批 sha `7490a03eb` · menu tip `c2b3b37bd` · D13343

---

## 1. 选题扫表

| 候选 | 结论 | 理由 |
|------|------|------|
| ① 挂机庭资源环加厚 / 宝石 | **跳过** | 旁路 TIP-BY–CT **CLOSED**；afk I/Open **零** `recipe_gap_gem`/`forge_gap_gem`（仅 enhance/upgrade/refine/brand/convert/roll gap）；宝石同构未过 |
| ② 短征日更乐趣 | **跳过** | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/选打 TIP-DH **已齐**；sx20 硬禁 |
| ③ 已齐 settle + 选打 DE–DL | **跳过** | CW–DD settle + TIP-DE–DL 选打 **全齐勿复读** |
| ④ PacePath | **后置** | 本仓无 EmberPacePath；派单明文后置 |
| ⑤ FlexPath / StepPath / SidestepPath | **后置本窗** | DESIGN-DL §6⑧ 明文逐支另窗；本窗只钉一支 |
| ⑥ legacy evacuate | **后置** | 须另窗证据 |
| 工坊升阶下一环 / 图装下一环 | **后置本窗** | 非本派明文优先 |
| **StridePath.offerPick / applyAndReply（荐）** | **采纳** | DESIGN-DL §6⑦ 明文钉步态；选打仍 ConfirmTokens 教 stridepath；**无**选打专用薄页；镜像 TIP-DE–DL；≠ 破绽选打同题 |

**择优结论：** ①②无新薄债 → ③已齐勿复读 → 落 DESIGN-DL §6⑦ 后置桶 · **只钉一支 = StridePath**。实扫 `offerPick` ConfirmTokens→stridepath push|bail|bare|clear；`applyAndReply` 下一步仍 ConfirmTokens→enter/flexpath/steppath/stridepath；无参/周要约/Q05 同入口；TrMenu **无** `ember_p1_stride_path`。故交本支；**证据表见 §2**；**为何不是半行见 E12**；**改题有证据**（未改题：无更硬挂机/短日常 fun 债压过本支；轻技/身法/闪冲后置）。

---

## 2. 证据表

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E1 | 总控 TIP-DL CLOSED · 派下一窗：步态逐支；只钉一支 | 派单 | 合法开窗 |
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · backlog TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债+选打 TIP-DH 已齐；sx20 禁 | tips | **短征无薄窗新硬债** |
| E4 | settle CW–DD + TIP-DE–DL 选打 **已齐** | tips | **禁止同题复读** |
| E5 | DESIGN-DL §6⑦ 明文后置步态逐支 | DESIGN-DL §6 | **合法未 CLOSED 后置** |
| E6 | `offerPick` ConfirmTokens 四钮 → `/corerpg p1 stridepath push\|bail\|bare\|clear` | EmberStridePath.offerPick L107–115 | **选打仍教裸指令** |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ05` → `offerPick` | EmberStridePath L118–140 | **周要约/Q05 入口同债** |
| E8 | `applyAndReply` 下一步 ConfirmTokens→`enterCmd` / `flexpath` / `steppath` / `stridepath` | EmberStridePath.applyAndReply L192–196 | **选后下一步仍聊天钮** |
| E9 | 命令无参 → glance + `offerPick`；用法仍教 `push\|bail\|bare\|clear`；glance 未选仍教手打 | EmberCommand L3434–3457 · glance L91 | **无参入口无开薄页** |
| E10 | TrMenu **无** `ember_p1_stride_path`（亦无步态选打页） | menus/ | **缺选打专用薄页** |
| E11 | 本仓无 EmberPacePath；Flex/Step/Sidestep 仍 ConfirmTokens | Java · menus | **后置本窗（§6）** |
| E12 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E13 | ≠ 破绽选打同题；≠ 誓约/试炼/深渊/短征/团本/硬本/精选选打同题；≠ 全族 Path 大扫；≠ 复开 D499 | 本 RESEARCH | 边界 |
| E14 | 为何钉 Stride | 见 §1 择优 | 最硬一支 |

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `offerPick` + 无参 `stridepath` + 周要约/Q05 + `applyAndReply` 下一步→开 `/ember_p1_stride_path`；清 ConfirmTokens 教指令为主 UI；可选 `stride_path_*` PAPI；单测
2. **菜单：** 新薄页 `ember_p1_stride_path`：突进踏/后撤踏/卸轻技/清空/进本/冒险/可选工坊；lore 挂路径
3. **经济：** **零** 改伤害 / CD 表 / 掉落 / 体力价 / 步态规则语义
4. **Open/半行：** 若需辅指针仅同批且不可独批
5. **零改** counter_path / 其他选打·settle 薄页 / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath/PledgePath/CounterPath / Cast / Aid / variety / 庭内 / soft-reward；**轻技/身法/闪冲 / Pace / legacy evacuate 后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无薄页 | 弱于真动作；默认不批 |
| W 抬表/sx20/已齐同题复读/variety再拧/庭内/破绽选打同题/全族Path大扫/轻技·身法·闪冲同窗/legacy evacuate 大扫/复开 D499 | 硬禁 |

---

*RESEARCH · StridePath.offerPick / applyAndReply 步态路径选打菜单化 · **待批 A** · 荐 M · TIP-DM / D13344 · 半行 REJECT `d00ed84bc` 保持 · 勿勾 CLOSED.*

## STATUS: **待批 A** · 荐 M · 拟 D13344 · TIP-DM · 2026-10-14 13:03 Asia/Shanghai · 勿勾 CLOSED
