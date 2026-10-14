# RESEARCH · RaidPath.offerPick / applyAndReply「团本路径选打」菜单化 · 拟 D13338 · TIP-DG

> **状态：** **待批 A** · 荐方案 **M** · L不批 · TIP-DG · 拟 D13338 · 2026-10-14 11:15 Asia/Shanghai · **勿勾 CLOSED**
> **起因：** 总控【派·TIP-DG 选题】RaidPath.offerPick / applyAndReply「团本路径选打」菜单化 · 拟 D13338。祖先 TIP-DF 抽测 PASS `6d98cb62a` · feat `5927286ca` · jar `1.65.830-d13337` · CLOSED 收口。DESIGN-DF §6⑦ 明文后置本支。选题优先级：①挂机庭资源环加厚（须新硬债；旁路近饱和·gem gap 未亮则跳过）②短征日更乐趣（非sx20；非已齐路径）③已齐勿复读：各 settle 下一环（CW–DD）+ 精选选打 DE + 硬本选打 DF ④默认钉 RaidPath.offerPick（可改题须证据压过本支）⑤legacy evacuate 后置。**禁：** 半行独批 · 已齐 settle/精选·硬本选打同题复读 · 抬掉落/日帽/体力价/周帽 · sx20 / densify / Pack6 / 开 K3 / 关观察 · variety 完整经济 / Cast·Aid 再拧 / 庭内旁路 · 教裸指令主 UI · 全族 Path 大扫 · 零改 `ember_p1_raid_settle` / FeaturedPath / ChallengePath。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-DF CLOSED live `6b1fc942d` · feat `5927286ca` · tip `17d0f7ddf` · PASS `6d98cb62a` · jar `1.65.830-d13337.local` · 批 sha `17d0f7ddf` · menu tip `bebeb7f17` · D13337

---

## 1. 选题扫表

| 候选 | 结论 | 理由 |
|------|------|------|
| ① 挂机庭资源环加厚 / 宝石 | **跳过** | 旁路 TIP-BY–CT **CLOSED**；afk I/Open **零** `recipe_gap_gem`/`forge_gap_gem`；宝石同构未过 |
| ② 短征日更乐趣 | **跳过** | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看 **已齐**；sx20 硬禁 |
| ③ 已齐 settle + 精选选打 + 硬本选打 | **跳过** | CW–DD settle + TIP-DE FeaturedPath + TIP-DF ChallengePath **全齐勿复读** |
| ④ legacy evacuate | **后置** | 派单默认后置；须另窗证据 |
| 工坊升阶下一环 / 图装下一环 | **后置本窗** | 非本派明文优先；forge/mapfarm 已有菜单主路径 |
| **RaidPath.offerPick / applyAndReply（荐）** | **采纳** | DESIGN-DF §6⑦；选打仍借 settle ConfirmTokens；**无**选打专用薄页；镜像 TIP-DE/DF；≠ settle 同题；≠ 硬本/精选选打同题 |

**择优结论：** ①②无新薄债 → ③已齐勿复读 → 默认钉 DESIGN-DF §6⑦ 明文后置本支。实扫团本路径 `offerPick`/`applyAndReply`/无参/冒险 Open 仍 ConfirmTokens 借 `ember_p1_raid_settle`、TrMenu **无** `ember_p1_raid_path`——内容闭环断在**团本路径选打**（≠ TIP-CZ 结算下一环；≠ TIP-DE/DF 选打）。故交本支；**证据表见 §2**；**为何不是半行见 E11**；**改题有证据**（未改题：无更硬挂机/短日常 fun 债压过本支）。

---

## 2. 证据表

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E1 | 总控 TIP-DF CLOSED · 派 TIP-DG 默认钉本支 | 派单 | 合法开窗 |
| E2 | 挂机庭旁路全弧 CLOSED；afk 零 gem gap | afk.yml · backlog TIP-BY–CT | **挂机无新硬债** |
| E3 | 短征薄债已齐；sx20 禁 | tips CW–CY 等 | **短征无薄窗新硬债** |
| E4 | settle CW–DD + TIP-DE 精选选打 + TIP-DF 硬本选打 **已齐** | tips | **禁止同题复读** |
| E5 | DESIGN-DF §6⑦ 明文后置 RaidPath.offerPick | DESIGN-DF §6 | **合法未 CLOSED 后置** |
| E6 | `offerPick` ConfirmTokens → `/ember_p1_raid_settle` | EmberRaidPath L99–107 · EmberRaidSettle L14 | **选打借结算面** |
| E7 | `applyAndReply` ConfirmTokens → 同 settle | L170–175 | **选后下一步仍借 settle** |
| E8 | 命令无参 + 冒险 Open `corerpg p1 raidpath` → offerPick；用法教 r01\|r02\|r03\|clear | EmberCommand L3851–3876 · adventure.yml L49 | **入口无选打专用页** |
| E9 | TrMenu **有** `ember_p1_raid_settle` / `ember_raid*`；**无** `ember_p1_raid_path` | menus/ | **缺选打专用薄页** |
| E10 | FeaturedPath / ChallengePath 选打已齐 | TIP-DE/DF | **零改本窗** |
| E11 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E12 | ≠ 团本 settle 同题；≠ 硬本/精选选打同题；≠ 全族 Path 大扫 | 本 RESEARCH | 边界 |

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `offerPick` + 无参 `raidpath` + `applyAndReply` 下一步→开 `/ember_p1_raid_path`；清 ConfirmTokens 借 settle/教指令为主 UI；可选复用 `raid_path_*` PAPI；单测
2. **菜单：** 新薄页 `ember_p1_raid_path`：R01锈轨/R02霜封/R03断塔/清空/冒险/可选大厅·工坊；lore 挂路径
3. **经济：** **零** 改掉落 / 体力价 / 周帽 / 团本语义 / resolve
4. **Open/半行：** 若需辅指针仅同批且不可独批
5. **零改** raid_settle / 其他 settle 薄页 / FeaturedPath / ChallengePath / Cast / Aid / variety / 庭内 / soft-reward；**legacy evacuate 后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无薄页 | 弱于真动作；默认不批 |
| W 抬表/sx20/已齐同题复读/variety再拧/庭内/硬本选打同题/精选选打同题/团本settle同题/全族Path大扫/legacy evacuate 大扫 | 硬禁 |

---

*RESEARCH · RaidPath.offerPick / applyAndReply 团本路径选打菜单化 · **待批 A** · 荐 M · TIP-DG / 拟 D13338 · 半行 REJECT `d00ed84bc` 保持 · ancestor TIP-DF CLOSED · 2026-10-14 11:15 Asia/Shanghai.*

## STATUS: **待批 A** · 荐 M · L不批 · 拟 D13338 · TIP-DG · 2026-10-14 11:15 Asia/Shanghai
