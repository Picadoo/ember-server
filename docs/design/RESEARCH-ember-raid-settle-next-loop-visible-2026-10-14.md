# RESEARCH · 团本结算「下一环」菜单化（settle 聊天钮去指令化 · ≠短征settle下一环同题 · ≠短征settle回看同题 · ≠variety再拧 · ≠半行 · ≠庭内旁路 · ≠抬周帽）· 2026-10-14

> **状态：** **待批 A** · 荐 **M** · 拟 D13331 · TIP-CZ · docs-only · 2026-10-14 08:40 Asia/Shanghai
> **起因：** 总控【派·TIP-CZ / D13331】上游 TIP-CY CLOSED live `bdf88e272`（jar `1.65.823-d13330` · feat `e5cb2ddd6` · tip `a1f5a2537` · PASS `176e2fe03` · 批 `afeb9a210`）。请交下一薄窗。方向：内容优先拉长在线——挂机加厚须新硬债证据；短征再一趣味薄窗；或 DESIGN §6 仍缓可施工项。**禁：** variety再拧/完整经济 · soft-reward · settle回看 · settle下一环（短征）· 路径追 · 日差同题 · 庭内旁路再拧。Cast+BossAid+EliteAid+todayRot+variety(W1a–d)+庭内全弧+今日推/冲一下/周短征/三冲+日差本可冲+路径追+settle下一环+soft-reward+settle花样回看 **已齐勿复读**。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-CY CLOSED live `bdf88e272` · feat `e5cb2ddd6` · tip `a1f5a2537` · PASS `176e2fe03` · jar `1.65.823-d13330.local` · 批 sha `afeb9a210` · D13330
> **硬禁：** **零** 半行独批 · **零** Cast粒子主交付 · **零** Aid/variety再拧 · **零** todayRot/W1e独批 · **零** 庭内旁路再拧 · **零** soft-reward同题 · **零** 短征settle下一环同题 · **零** 短征settle回看同题 · **零** 路径追同题 · **零** 日差本同题 · **零** sx20 · **零** 抬周帽/weeklyCap/loot · **零** Pack6 · **零** 开R/K3 · **零** 关观察 · **零** 灾厄/fest重开 · **零** 教裸指令 · **零** densify · **零** stage脏runtime · **零** 挑战/深渊同窗大扫

---

## 0. 一句话

短征 settle 下一环已菜单化，**团本周清仍钉在 ConfirmTokens 教 `/corerpg p1 raidpath|recruit|enter`**——§6⑥ 后置「全服 settle 去指令化」可另窗。本债=**RaidLoop 改开薄页 + `ember_p1_raid_settle` 真可点**，**不是**短征同题复读，**不是**半行。

---

## 1. 选题扫描（TIP-CY 后 · 必须真动作 · 内容优先）

| 候选 | 裁决 | 理由 |
|------|------|------|
| Cast 粒子打磨 / Aid再拧 / todayRot/W1e / variety 再拧 / 完整 variety 经济 | **否** | 硬禁；刚齐 / 仍缓 |
| 半行独批 | **否** | REJECT `d00ed84bc` 保持 |
| 庭内旁路再拧 / 宝石 | **否** | 近饱和；庭面 gem gap 未挂 `recipe_gap_gem`；无新消耗/产出硬债 |
| soft-reward / settle回看 / 短征settle下一环 / 路径追 / 日差本同题 | **否** | 已齐勿再交 |
| 今日推 / 周目标 / 三冲 复读 | **否** | 已齐勿复读 |
| fest / 灾厄 / sx20 | **否** | 硬禁或须另批 |
| 挂机区资源环加厚 | **否 · 本窗** | 实扫无新硬债（非第五花/已齐旁路镜像）；宝石仍缺庭面 gap |
| 挑战/深渊 settle 同窗大扫 | **后置** | §6；本窗先钉团本一支（对齐 TIP-CW 只钉短征） |
| **团本结算下一环菜单化（荐）** | **采纳** | §6⑥；RaidLoop ConfirmTokens；无薄页；真可点 |

**择优结论：** 短征日更弧（含 settle回看）已饱和勿复读；庭内无新硬债。实扫 D13330/29/28§6⑥ 明文后置全服 settle 去指令化且短征支已齐，而 `EmberRaidLoop` 仍 ConfirmTokens 主路径、TrMenu **无** `ember_p1_raid_settle`——内容闭环断在团本结算。故交团本下一环；**证据表见 §2**；**为何不是半行见 E8**；**改题有证据**。

---

## 2. 证据（现网只读 · 2026-10-14）

| # | 事实 | 路径 | 含义 |
|---|------|------|------|
| E1 | 总控派 TIP-CZ · 内容优先 · 禁短征同题复读 · 庭内默认勿交 · §6 仍缓可施工 | 派单 | 选题边界 |
| E2 | D13330 / D13329 / D13328 DESIGN §6⑥ 皆后置「全服 settle 去指令化」；短征支 TIP-CW 已 CLOSED | 三份 DESIGN §6 · tip CW | **合法未 CLOSED 后置**（团本/挑战/深渊） |
| E3 | soft-reward / settle回看 / 短征settle下一环 / 路径追 / 日差 / 今日推 / 周目标 / 三冲 / Cast / Aid / todayRot / variety(W1a–d) / 庭内全弧 **已齐** | tips CLOSED | ≠ 同题复读 |
| E4 | `EmberRaidLoop.afterWeeklyClear`：额度满→ConfirmTokens「下一步」`/ember_p1_adventure` + `/corerpg p1 raidpath`；未满→「团本下一环」`enterCmd`/`recruit`/`raidpath` | RaidLoop.java L31–58 | **主路径仍 chat 钮** |
| E5 | `EmberRaidPath.offerPick` / `glance` 文案含 `/corerpg p1 raidpath`；路径切仍 ConfirmTokens | RaidPath.java L82–106 | **路径选择亦教指令** |
| E6 | TrMenu **无** `ember_p1_raid_settle.yml`；仅有 `ember_raid.yml` 大厅通道（S/P/R 开本，非结算下一环） | menus/ | **缺结算薄页** |
| E7 | 短征已有 `ember_p1_short_settle` 范式（TIP-CW）；可镜像团本一支，**不**复开短征同题 | short_settle.yml | 可施工范式 |
| E8 | **为何不是半行：** 主交付=插件改 RaidLoop 入口 + 薄页真开菜单去向；Open/半行不可独批 | 本 RESEARCH | 过硬禁 |
| E9 | 半行 REJECT `d00ed84bc` 保持否决 | backlog | 否决只口号 |
| E10 | 挂机庭 Layout 已塞满四花+推荐+拆+换+洗+烙+转+随机锻；`recipe_gap_gem` PAPI 有但庭面未挂 | afk.yml · RunPapi | **否决挂机加厚/宝石本窗** |
| E11 | 挑战 `EmberChallengePath.afterSettle` 仍 ConfirmTokens「硬本下一环」——另窗；本窗不扫 | ChallengePath.java L225–229 | §6 后置 |

**一句话缺口：** 团本打完「下一环」仍钉在聊天点指令，**没有**结算薄页可点——内容在线断在团本结算闭环。

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `EmberRaidLoop.afterWeeklyClear` 下一环→开 `/ember_p1_raid_settle`（额度满/未满皆可）；可选 PAPI `raid_settle_*`（周次/路径 label/map）；单测  
2. **菜单：** 新薄页 `ember_p1_raid_settle`：再进路径团本 / 选·换路径 / 冒险 / 工坊 / 招募；灰态诚实  
3. **经济：** **零** 改 weeklyCap / loot / 体力价 / 团本数值  
4. **Open/半行：** 若需辅指针仅同批且不可独批  
5. **零改**短征薄页 / Cast / Aid / variety / 庭内 / soft-reward / ShortPath resolve；**挑战/深渊后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无薄页 | 弱于真动作；默认不批 |
| W 抬周帽/sx20/短征同题复读/variety再拧/庭内/挑战深渊同窗大扫 | 硬禁 |

---

*RESEARCH · 团本结算下一环菜单化 · **待批 A** · 荐 M · TIP-CZ / D13331 · 半行 REJECT `d00ed84bc` 保持 · ≠短征settle下一环同题 · ≠短征settle回看同题。*

## STATUS: **待批 A** · 荐 **M** · 拟 D13331 · TIP-CZ · 2026-10-14 08:40 Asia/Shanghai
