# RESEARCH · ChallengePath.offerPick / applyAndReply「硬本路径选打」菜单化 · 拟 D13337 · TIP-DF

> **状态：** **已批 A · 方案 M** · APPROVED A·M · 拟 D13337 · TIP-DF · 可施工 · 勿勾 CLOSED · 2026-10-14 10:59 Asia/Shanghai
> **起因：** 总控【派·TIP-DF / 拟 D13337】上游 TIP-DE 抽测 PASS `687033a51`（feat `1465d7622` · menu `c161fff0d` · tip `c72e8a964` · jar `1.65.829-d13336` · 证据 `/workspace/tmp/tip-de-featured-path/`）。总控盖 CLOSED；连开写稿。选题优先级：①挂机庭资源环加厚（须新硬债；旁路近饱和·gem gap 未亮则跳过）②短征日更乐趣（非sx20；非已齐路径）③已齐勿复读：各 settle 下一环（CW–DD）+ 精选路径选打（TIP-DE）④菜单去指令残留：可扫 ChallengePath.offerPick / 其他 *Path.offerPick（**逐支另窗，禁全族大扫**）⑤legacy evacuate 后置。若①②无新薄债：优先钉一条有证据的路径选打残留（如硬本路径 offerPick），镜像 TIP-DE。**禁：** 半行独批 · 已齐 settle/精选选打同题复读 · 抬掉落/日帽/体力价 · sx20 / densify / Pack6 / 开 K3 / 关观察 · variety 完整经济 / Cast·Aid 再拧 / 庭内旁路 · 教裸指令主 UI · 全族 Path 大扫。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-DE CLOSED live `678cd409a` · feat `1465d7622` · tip `c72e8a964` · PASS `687033a51` · jar `1.65.829-d13336.local` · 批 sha `c72e8a964` · menu tip `c161fff0d` · D13336

---

## 1. 选题扫表

| 候选 | 结论 | 理由 |
|------|------|------|
| ① 挂机庭资源环加厚 / 宝石 | **跳过** | 旁路 TIP-BY–CT **CLOSED**；afk I/Open **零** `recipe_gap_gem`/`forge_gap_gem`；宝石同构未过 |
| ② 短征日更乐趣 | **跳过** | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看 **已齐**；sx20 硬禁 |
| ③ 已齐 settle + 精选选打 | **跳过** | CW–DD settle + TIP-DE FeaturedPath 选打 **全齐勿复读** |
| ④ legacy evacuate | **后置** | 派单默认后置；须另窗证据 |
| RaidPath.offerPick | **后置本窗** | 同构 ConfirmTokens→raid_settle；逐支另窗；禁同窗大扫 |
| 工坊升阶下一环 / 图装下一环 | **后置本窗** | 非本派明文优先；forge/mapfarm 已有菜单主路径 |
| **ChallengePath.offerPick / applyAndReply（荐）** | **采纳** | DESIGN-DE §6⑦；选打仍借 settle ConfirmTokens；**无**选打专用薄页；镜像 TIP-DE；≠ settle 同题 |

**择优结论：** ①②无新薄债 → ③已齐勿复读 → 扫菜单去指令残留。DESIGN-DE §6⑦ 明文后置其他 *Path.offerPick；实扫硬本路径 `offerPick`/`applyAndReply`/无参/冒险 Open 仍 ConfirmTokens 借 `ember_p1_challenge_settle`、TrMenu **无** `ember_p1_challenge_path`——内容闭环断在**硬本路径选打**（≠ TIP-DA 结算下一环；≠ TIP-DE 精选选打）。故交本支；RaidPath 写 §6；**证据表见 §2**；**为何不是半行见 E11**；**改题有证据**。

---

## 2. 证据表

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E1 | 总控 TIP-DE CLOSED · 内容优先连开 | 派单 | 合法开窗 |
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · backlog TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债已齐；sx20 禁 | tips CW–CY 等 | **短征无薄窗新硬债** |
| E4 | settle CW–DD + TIP-DE 精选选打 **已齐** | tips | **禁止同题复读** |
| E5 | DESIGN-DE §6⑦ 明文后置其他 *Path.offerPick | DESIGN-DE §6 | **合法未 CLOSED 后置** |
| E6 | `offerPick` ConfirmTokens → `/ember_p1_challenge_settle` | EmberChallengePath L103–111 | **选打借结算面** |
| E7 | `applyAndReply` ConfirmTokens → 同 settle | L177–181 | **选后下一步仍借 settle** |
| E8 | 命令无参 + 冒险 Open `corerpg p1 challengepath` → offerPick；用法教 prefer|easy|follow|clear | EmberCommand L2705–2728 · adventure.yml L48 | **入口无选打专用页** |
| E9 | TrMenu **无** `ember_p1_challenge_path`；仅有 `ember_p1_challenge_settle`（结算面混装路径格） | menus/ | **缺选打专用薄页** |
| E10 | RaidPath.offerPick 同构 → raid_settle | EmberRaidPath L99–107 | **后置另窗** |
| E11 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E12 | ≠ 挑战 settle 同题；≠ 精选选打同题；≠ 全族 Path 大扫 | 本 RESEARCH | 边界 |

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `offerPick` + 无参 `challengepath` + `applyAndReply` 下一步→开 `/ember_p1_challenge_path`；清 ConfirmTokens 借 settle/教指令为主 UI；可选 PAPI；单测
2. **菜单：** 新薄页 `ember_p1_challenge_path`：偏挑战/偏普通/随令/清空/冒险/可选选图·工坊；lore 挂路径
3. **经济：** **零** 改掉落 / 体力价 / 硬本语义 / resolve
4. **Open/半行：** 若需辅指针仅同批且不可独批
5. **零改** challenge_settle / 其他 settle 薄页 / FeaturedPath / Cast / Aid / variety / 庭内 / soft-reward / RaidPath.offerPick；**legacy evacuate 后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无薄页 | 弱于真动作；默认不批 |
| W 抬表/sx20/已齐同题复读/variety再拧/庭内/精选选打同题/挑战settle同题/全族Path大扫/RaidPath同窗/legacy evacuate 大扫 | 硬禁 |

---

*RESEARCH · ChallengePath.offerPick / applyAndReply 硬本路径选打菜单化 · **已批 A · 方案 M** · APPROVED A·M · TIP-DF / D13337 · 半行 REJECT `d00ed84bc` 保持 · ≠ challenge_settle 同题 · ≠ featured_path 选打同题 · ≠图身份/深渊/短征/团本/精选settle同题 · RaidPath 后置 · 2026-10-14 10:59 Asia/Shanghai.*

## STATUS: **已批 A · 方案 M** · APPROVED A·M · 可施工 · 勿勾 CLOSED · L不批 · D13337 · TIP-DF · 2026-10-14 10:59 Asia/Shanghai
