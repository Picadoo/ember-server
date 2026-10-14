# RESEARCH · BlazePath.offerPick / applyAndReply「避焰路径选打」菜单化 · 拟 D13353 · TIP-DV

> **状态：** **待批 A** · 荐 M · 2026-10-14 15:54 Asia/Shanghai · 勿勾 CLOSED
> **起因：** 总控【下一窗 TIP-DV · 只钉一支 · 禁全族大扫】。祖先 TIP-DU **CLOSED live** `79985fd2a` · feat `d21666151` · tip `093e5a641` · menu `74f28d8e5` · jar `1.65.845-d13352.local` · PASS `09e0e1e77` · 证据 `/workspace/tmp/tip-du-spin-path/`。**避旋路径选打菜单化同题勿再交**。选题钉死：**BlazePath.offerPick**「避焰路径选打」菜单化 → TrMenu `ember_p1_blaze_path`。总控实扫：offerPick ConfirmTokens→`blazepath auto|ask|mute|clear`；applyAndReply 下一步 ConfirmTokens→`/corerpg p1`/`blazepath`；glance 教手打；局内「炽热焰圈」战斗钮≠选打薄页；TrMenu **无** `ember_p1_blaze_path`；与刚 CLOSED Spin 同构；DESIGN-DU§6⑦ Spin 后下一位。**禁：** 挂机 gem gap 未亮/短征已齐 → 勿交 gem/sx20；DE–DU 选打勿复；PacePath（本仓无）后置；禁全族 *Path.offerPick 大扫；半行 REJECT `d00ed84bc`；零抬伤害/CD表/掉落/体力价；零改 spin_path / snap_path 等已齐；勿勾 CLOSED。
> **上游：** TIP-DU CLOSED live `79985fd2a` · feat `d21666151` · tip `093e5a641` · PASS `09e0e1e77` · jar `1.65.845-d13352.local` · 批 sha `093e5a641` · menu tip `74f28d8e5` · D13352

---

## 1. 选题扫表

| 候选 | 结论 | 理由 |
|------|------|------|
| ① 挂机庭资源环加厚 / 宝石 | **跳过** | 旁路 TIP-BY–CT **CLOSED**；afk **零** gem gap；总控 gem gap 未亮 → 勿交 gem |
| ② 短征日更乐趣 | **跳过** | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/选打 TIP-DH **已齐**；sx20 硬禁 |
| ③ 已齐 settle + 选打 DE–DU | **跳过** | CW–DD settle + TIP-DE–DU 选打（含避旋）**全齐勿复读** |
| ④ PacePath | **后置** | 本仓无 EmberPacePath；派单明文后置 |
| ⑤ 其他仍 ConfirmTokens 的 *Path | **后置** | 禁全族大扫；本窗只钉一支；逐支另窗 |
| ⑥ legacy evacuate | **后置** | 须另窗证据 |
| 工坊升阶下一环 / 图装下一环 | **后置本窗** | 非本派明文优先 |
| **BlazePath.offerPick / applyAndReply（荐）** | **采纳** | 总控钉死；ConfirmTokens 全链；**无**选打专用薄页；局内炽热焰圈钮≠选打页；镜像 TIP-DE–DU；≠ 避旋选打同题 |

**择优结论：** ①②无新薄债 → ③已齐勿复读 → 总控钉死 Blaze（DESIGN-DU§6⑦ Spin 后下一位）→ 实扫残留 *Path.offerPick 仍教裸指令且无薄页者：Blaze 与 Spin 同构 → **钉 BlazePath**（offerPick ConfirmTokens→blazepath auto|ask|mute|clear；applyAndReply 下一步 ConfirmTokens 教裸 `blazepath`/`p1`；glance 未选教手打；用法教 `auto|ask|mute|clear`；TrMenu **无** `ember_p1_blaze_path`）。**局内炽热焰圈预警「避焰」+「改路径」战斗钮**（ASK 路径提醒）**存在但不消解选打硬债**——选打链仍 ConfirmTokens。PacePath（本仓无）后置。故交本支；**证据表见 §2**；**为何不是半行见 E13**；**改题有证据**（未改题：无更硬挂机/短日常 fun 债压过本支；总控选题已钉死）。

---

## 2. 证据表

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E1 | 总控 TIP-DU CLOSED · 派下一窗：只钉 BlazePath；禁全族大扫 | 派单 | 合法开窗 |
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · backlog TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债+选打 TIP-DH 已齐；sx20 禁 | tips | **短征无薄窗新硬债** |
| E4 | settle CW–DD + TIP-DE–DU 选打 **已齐** | tips | **禁止同题复读** |
| E5 | DESIGN-DU §6⑦ / Spin 旁注：Blaze 为 Spin 后下一位；本仓无 PacePath | DESIGN-DU §6 · TIP-DU | **合法未 CLOSED 后置** |
| E6 | `offerPick` ConfirmTokens 四钮 → `/corerpg p1 blazepath auto\|ask\|mute\|clear` | EmberBlazePath.offerPick L149–157 | **选打仍教裸指令** |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ01` → `offerPick` | EmberBlazePath L160–177 | **周要约/Q01 入口同债** |
| E8 | `applyAndReply` 下一步 ConfirmTokens→`/corerpg p1` / `blazepath` | EmberBlazePath.applyAndReply L212–214 | **选后下一步仍聊天钮** |
| E9 | 命令无参 → glance + `offerPick`；用法仍教 `auto\|ask\|mute\|clear`；glance 未选仍教手打 | EmberCommand L5536–5569 · glance L87–89 | **无参入口无开薄页** |
| E10 | TrMenu **无** `ember_p1_blaze_path`（已有 spin/snap/veil/free/dodge/…_path） | menus/ | **缺选打专用薄页** |
| E11 | 局内炽热焰圈有「避焰」+「改路径」ConfirmTokens | EmberBlazePath L122–146 | **局内战斗钮≠选打薄页；选打硬债仍成立** |
| E12 | PacePath 本仓无；其他剩支若有则另窗 | Java | **后置（§6）** |
| E13 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E14 | ≠ 避旋选打同题；≠ 断链/避毒/解锢/闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态选打同题；≠ 全族 Path 大扫；≠ 复开 D576 | 本 RESEARCH | 边界 |

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `offerPick` + 无参 `blazepath` + 周要约/Q01 + `applyAndReply` 下一步→开 `/ember_p1_blaze_path`；清 ConfirmTokens 教指令为主 UI；可选 `blaze_path_*` PAPI；单测；局内避焰战斗钮语义零改
2. **菜单：** 新薄页 `ember_p1_blaze_path`：自动/提醒/静默/清空/技能组/冒险/可选工坊；lore 挂路径；TrMenu 点击 + `corerpg p1 from/back`
3. **经济：** **零** 改伤害 / CD 表 / 掉落 / 体力价 / 避焰规则语义
4. **Open/半行：** 若需辅指针仅同批且不可独批
5. **零改** spin_path / snap_path / veil_path / free_path / dodge_path / sidestep_path / step_path / flex_path / stride_path / 其他选打·settle 薄页 / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath/PledgePath/CounterPath/StridePath/FlexPath/StepPath/SidestepPath/DodgePath/FreePath/VeilPath/SnapPath/SpinPath / Cast / Aid / variety / 庭内 / soft-reward；**Pace / legacy evacuate 后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无薄页 | 弱于真动作；默认不批 |
| W 抬表/sx20/已齐同题复读/variety再拧/庭内/避旋选打同题/全族Path大扫/Pace同窗/legacy evacuate 大扫/复开 D576 | 硬禁 |

---

*RESEARCH · BlazePath.offerPick / applyAndReply 避焰路径选打菜单化 · **待批 A** · 荐 M · TIP-DV / D13353 · 半行 REJECT `d00ed84bc` 保持 · PacePath（本仓无）/挂机若新硬债/legacy evacuate 后置 · ancestor TIP-DU CLOSED `79985fd2a` · 2026-10-14 15:54 Asia/Shanghai · 勿勾 CLOSED.*

## STATUS: **待批 A** · 荐 M · 勿勾 CLOSED · L不批 · D13353 · TIP-DV · 2026-10-14 15:54 Asia/Shanghai
