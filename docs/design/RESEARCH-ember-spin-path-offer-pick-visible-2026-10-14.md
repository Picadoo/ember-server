# RESEARCH · SpinPath.offerPick / applyAndReply「避旋路径选打」菜单化 · 拟 D13352 · TIP-DU

> **状态：** **已批 A · 方案 M** · APPROVED A·M · 拟 D13352 · TIP-DU · 可施工 · 勿勾 CLOSED · 2026-10-14 15:37 Asia/Shanghai
> **起因：** 总控【下一窗 TIP-DU · 只钉一支 · 禁全族大扫】。祖先 TIP-DT **CLOSED live** `3fe104477` · feat `b9d06e01a` · tip `f5cea1e1d` · menu `4070a04f7` · jar `1.65.844-d13351.local` · PASS `6f69cb256` · 证据 `/workspace/tmp/tip-dt-snap-path/`。**断链路径选打菜单化同题勿再交**。选题钉死：**SpinPath.offerPick**「避旋路径选打」菜单化 → TrMenu `ember_p1_spin_path`。总控实扫：offerPick ConfirmTokens→`spinpath auto|ask|mute|clear`；applyAndReply 下一步 ConfirmTokens→`/corerpg p1`/`spinpath`；glance 教手打；局内「旋光光束」战斗钮≠选打薄页；TrMenu **无** `ember_p1_spin_path`；与刚 CLOSED Snap 同构；DESIGN-DQ§6⑦ Snap 后下一位。**禁：** 挂机 gem gap 未亮/短征已齐 → 勿交 gem/sx20；DE–DT 选打勿复；Blaze **后置**；禁全族 *Path.offerPick 大扫；半行 REJECT `d00ed84bc`；零抬伤害/CD表/掉落/体力价；零改 snap_path / veil_path 等已齐；勿勾 CLOSED。
> **上游：** TIP-DT CLOSED live `3fe104477` · feat `b9d06e01a` · tip `f5cea1e1d` · PASS `6f69cb256` · jar `1.65.844-d13351.local` · 批 sha `f5cea1e1d` · menu tip `4070a04f7` · D13351

---

## 1. 选题扫表

| 候选 | 结论 | 理由 |
|------|------|------|
| ① 挂机庭资源环加厚 / 宝石 | **跳过** | 旁路 TIP-BY–CT **CLOSED**；afk **零** gem gap；总控 gem gap 未亮 → 勿交 gem |
| ② 短征日更乐趣 | **跳过** | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看/选打 TIP-DH **已齐**；sx20 硬禁 |
| ③ 已齐 settle + 选打 DE–DT | **跳过** | CW–DD settle + TIP-DE–DT 选打（含断链）**全齐勿复读** |
| ④ PacePath | **后置** | 本仓无 EmberPacePath；派单明文后置 |
| ⑤ Blaze 等仍 ConfirmTokens 的 *Path | **后置** | 禁全族大扫；本窗只钉一支；逐支另窗 |
| ⑥ legacy evacuate | **后置** | 须另窗证据 |
| 工坊升阶下一环 / 图装下一环 | **后置本窗** | 非本派明文优先 |
| **SpinPath.offerPick / applyAndReply（荐）** | **采纳** | 总控钉死；ConfirmTokens 全链；**无**选打专用薄页；局内旋光光束钮≠选打页；镜像 TIP-DE–DT；≠ 断链选打同题 |

**择优结论：** ①②无新薄债 → ③已齐勿复读 → 总控钉死 Spin（DESIGN-DQ§6⑦ Snap 后下一位）→ 实扫残留 *Path.offerPick 仍教裸指令且无薄页者：Spin 与 Snap 同构 → **钉 SpinPath**（offerPick ConfirmTokens→spinpath auto|ask|mute|clear；applyAndReply 下一步 ConfirmTokens 教裸 `spinpath`/`p1`；glance 未选教手打；用法教 `auto|ask|mute|clear`；TrMenu **无** `ember_p1_spin_path`）。**局内旋光光束预警「退开」战斗钮**（ASK 路径提醒）**存在但不消解选打硬债**——选打链仍 ConfirmTokens。Blaze 等同构剩支后置。故交本支；**证据表见 §2**；**为何不是半行见 E13**；**改题有证据**（未改题：无更硬挂机/短日常 fun 债压过本支；总控选题已钉死）。

---

## 2. 证据表

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E1 | 总控 TIP-DT CLOSED · 派下一窗：只钉 SpinPath；禁全族大扫 | 派单 | 合法开窗 |
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · backlog TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债+选打 TIP-DH 已齐；sx20 禁 | tips | **短征无薄窗新硬债** |
| E4 | settle CW–DD + TIP-DE–DT 选打 **已齐** | tips | **禁止同题复读** |
| E5 | DESIGN-DQ §6⑦ / Snap 旁注：Spin 为 Snap 后下一位；本仓无 PacePath | DESIGN-DQ §6 · TIP-DT | **合法未 CLOSED 后置** |
| E6 | `offerPick` ConfirmTokens 四钮 → `/corerpg p1 spinpath auto\|ask\|mute\|clear` | EmberSpinPath.offerPick L150–158 | **选打仍教裸指令** |
| E7 | `maybeOfferWeekly` / `scheduleOfferAfterQ01` → `offerPick` | EmberSpinPath L161–181 | **周要约/Q01 入口同债** |
| E8 | `applyAndReply` 下一步 ConfirmTokens→`/corerpg p1` / `spinpath` | EmberSpinPath.applyAndReply L213–215 | **选后下一步仍聊天钮** |
| E9 | 命令无参 → glance + `offerPick`；用法仍教 `auto\|ask\|mute\|clear`；glance 未选仍教手打 | EmberCommand L5480–5514 · glance L88–90 | **无参入口无开薄页** |
| E10 | TrMenu **无** `ember_p1_spin_path`（已有 snap/veil/free/dodge/…_path） | menus/ | **缺选打专用薄页** |
| E11 | 局内旋光光束有「退开」+「改路径」ConfirmTokens | EmberSpinPath L123–147 | **局内战斗钮≠选打薄页；选打硬债仍成立** |
| E12 | Blaze 等同构仍 ConfirmTokens；本仓无 PacePath | Java | **后置（§6）** |
| E13 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E14 | ≠ 断链选打同题；≠ 避毒/解锢/闪榴/闪冲/身法/轻技/破绽/誓约/试炼/深渊/短征/团本/硬本/精选/步态选打同题；≠ 全族 Path 大扫；≠ 复开 D575 | 本 RESEARCH | 边界 |

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `offerPick` + 无参 `spinpath` + 周要约/Q01 + `applyAndReply` 下一步→开 `/ember_p1_spin_path`；清 ConfirmTokens 教指令为主 UI；可选 `spin_path_*` PAPI；单测；局内避旋战斗钮语义零改
2. **菜单：** 新薄页 `ember_p1_spin_path`：自动/提醒/静默/清空/技能组/冒险/可选工坊；lore 挂路径；TrMenu 点击 + `corerpg p1 from/back`
3. **经济：** **零** 改伤害 / CD 表 / 掉落 / 体力价 / 避旋规则语义
4. **Open/半行：** 若需辅指针仅同批且不可独批
5. **零改** snap_path / veil_path / free_path / dodge_path / sidestep_path / step_path / flex_path / stride_path / 其他选打·settle 薄页 / ShortPath/RaidPath/FeaturedPath/ChallengePath/AbyssPath/TrialPath/PledgePath/CounterPath/StridePath/FlexPath/StepPath/SidestepPath/DodgePath/FreePath/VeilPath/SnapPath / Cast / Aid / variety / 庭内 / soft-reward；**Blaze / Pace / legacy evacuate 后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无薄页 | 弱于真动作；默认不批 |
| W 抬表/sx20/已齐同题复读/variety再拧/庭内/断链选打同题/全族Path大扫/Pace同窗/Blaze同窗/legacy evacuate 大扫/复开 D575 | 硬禁 |

---

*RESEARCH · SpinPath.offerPick / applyAndReply 避旋路径选打菜单化 · **已批 A · 方案 M** · APPROVED A·M · TIP-DU / D13352 · 半行 REJECT `d00ed84bc` 保持 · Blaze 后置 · ancestor TIP-DT CLOSED `3fe104477` · 2026-10-14 15:37 Asia/Shanghai.**待批 A** · 荐 M · TIP-DU / D13352 · 半行 REJECT `d00ed84bc` 保持 · Blaze等剩支/挂机若新硬债/legacy evacuate 后置 · ancestor TIP-DT CLOSED `3fe104477` · 2026-10-14 15:35 Asia/Shanghai · 勿勾 CLOSED.*

## STATUS: **已批 A · 方案 M** · APPROVED A·M · 可施工 · 勿勾 CLOSED · L不批 · D13352 · TIP-DU · 2026-10-14 15:37 Asia/Shanghai
