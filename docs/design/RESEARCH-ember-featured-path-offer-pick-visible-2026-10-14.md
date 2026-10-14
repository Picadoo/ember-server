# RESEARCH · FeaturedPath.offerPick / applyAndReply「精选路径选打」菜单化 · 拟 D13336 · TIP-DE

> **状态：** **CLOSED live** · feat `1465d7622` · tip `c72e8a964` · PASS `687033a51` · jar `1.65.829-d13336.local` · 批 sha `c72e8a964` · TIP-DE · 2026-10-14 10:57 Asia/Shanghai · 精选路径选打菜单化同题勿再交 · 下一窗：内容优先（挂机若新硬债 / 短征若新薄债 / 其他菜单去指令残留如 ChallengePath.offerPick 等另窗逐支 / legacy evacuate 另窗；精选 settle+选打本支已齐）
> **起因：** 总控【派·TIP-DE / 拟 D13336】上游 TIP-DD CLOSED live `36ecc1b43`（jar `1.65.828-d13335` · feat `b07074070` · tip `b7160ee19` · PASS `f864e36b9` · 批 `b7160ee19`）。请交下一薄窗（内容优先）。选题优先级：①挂机庭资源环加厚（须新硬债）②短征日更乐趣（非sx20；非已齐路径）③其余 settle 聊天钮：D673/D794 **已齐勿复读** ④legacy evacuate 后置。若①②无新薄债：扫其他真可点乐趣硬债（工坊/图装/日常短循环/菜单去指令残留等）。**禁：** 半行独批 · 已齐 settle 同题复读 · 抬掉落/日帽/体力价 · sx20 / densify / Pack6 / 开 K3 / 关观察 · variety 完整经济 / Cast·Aid 再拧 / 庭内旁路 · 教裸指令主 UI。半行 REJECT `d00ed84bc` **保持否决**。
> **上游：** TIP-DD CLOSED live `36ecc1b43` · feat `b07074070` · tip `b7160ee19` · PASS `f864e36b9` · jar `1.65.828-d13335.local` · 批 sha `b7160ee19` · D13335

---

## 1. 选题扫表

| 候选 | 结论 | 理由 |
|------|------|------|
| ① 挂机庭资源环加厚 / 宝石 | **跳过** | 旁路 TIP-BY–CT **CLOSED**；afk I/Open **零** `recipe_gap_gem`/`forge_gap_gem`；宝石同构未过 |
| ② 短征日更乐趣 | **跳过** | Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看 **已齐**；sx20 硬禁 |
| ③ 其余 settle 聊天钮 | **跳过** | D673/D794 + 短征/团本/挑战/深渊 settle **全齐勿复读** |
| ④ legacy evacuate | **后置** | 派单默认后置；须另窗证据 |
| 工坊升阶下一环 `EmberUpgradeLoop` | **后置本窗** | ConfirmTokens 仍在，但 forge 页已有强化/成色/精工真点；非 DESIGN-DD 明文后置 |
| 图装下一环 `EmberMapFarmCue` | **后置本窗** | 有债；冒险已有 mapfarm resume；非 §6⑤ |
| **FeaturedPath.offerPick / applyAndReply（荐）** | **采纳** | DESIGN-DD §6⑤ 明文后置；选打面仍 ConfirmTokens；**无**选打薄页；≠ settle 同题 |

**择优结论：** ①②无新薄债 → ③ settle 已齐 → 扫菜单去指令残留。DESIGN-DD §6⑤ 明文后置 offerPick/glance；实扫 `offerPick`/`applyAndReply`/冒险 Open 仍教指令、TrMenu **无** `ember_p1_featured_path`——内容闭环断在**精选路径选打**（≠ TIP-DD 结算下一环）。故交本支；**证据表见 §2**；**为何不是半行见 E10**；**改题有证据**。

---

## 2. 证据表

| # | 证据 | 源 | 结论 |
|---|------|-----|------|
| E1 | 总控 TIP-DD CLOSED · 内容优先连开 | 派单 | 合法开窗 |
| E2 | 挂机庭：强化/升阶/精工/成色/推荐花/拆/换/洗练/烙纹/转化/随机锻 **CLOSED**；afk.yml I/Open **零** `recipe_gap_gem`/`forge_gap_gem`/「宝石」 | afk.yml · backlog TIP-BY–CT | **挂机无新硬债；宝石同构未过** |
| E3 | 短征：Cast/Aid/todayRot/variety/日差/路径追/settle/soft/回看 **CLOSED**；sx20 硬禁 | tips CW–CY 等 | **短征无薄窗新硬债** |
| E4 | D673/D794 + 短征/团本/挑战/深渊 settle 下一环 **已齐** | tips CW–DD | **禁止 settle 同题复读** |
| E5 | DESIGN-DD §6⑤ 明文后置 offerPick / glance 全链 | DESIGN-DD §6 | **合法未 CLOSED 后置** |
| E6 | `offerPick` ConfirmTokens → featurepath challenge/normal/clear | EmberFeaturedPath L99–107 | **选打仍教指令** |
| E7 | `applyAndReply` ConfirmTokens → 裸 enter / runs / featurepath | L171–179 | **选后下一步仍教指令** |
| E8 | 命令无参 + 冒险 Open `corerpg p1 featurepath` → offerPick | EmberCommand L3787–3790 · adventure.yml L50 | **入口无菜单页** |
| E9 | TrMenu **无** `ember_p1_featured_path`；仅有 `ember_p1_featured_settle`（结算面，≠选打面） | menus/ | **缺选打薄页** |
| E10 | **为何不是半行：** 主交付=插件改入口 + 薄页真态；半行不可独批 | 本 RESEARCH | 过硬禁 |
| E11 | ≠ 精选 settle 同题；≠ 全族 Path 大扫 | 本 RESEARCH | 边界 |

---

## 3. 方案 M 摘要（对接 DESIGN）

1. **插件：** `offerPick` + 无参 `featurepath` + `applyAndReply` 下一步→开 `/ember_p1_featured_path`；清 ConfirmTokens 教指令为主 UI；可选 PAPI `featured_path_*`；单测
2. **菜单：** 新薄页 `ember_p1_featured_path`：挑战/普通/清空/进本周精选/冒险/可选工坊；lore 挂图名+路径
3. **经济：** **零** 改掉落 / 体力价 / 周帽 / 精选语义
4. **Open/半行：** 若需辅指针仅同批且不可独批
5. **零改** featured_settle / 其他 settle 薄页 / Cast / Aid / variety / 庭内 / soft-reward / 其他 *Path.offerPick；**legacy evacuate 后置**；**工坊升阶/图装下一环后置**

---

## 4. 否决摘要

| 否决 | 理由 |
|------|------|
| A 只半行 | REJECT 保持 |
| L 仅改一条聊天钮目标无薄页 | 弱于真动作；默认不批 |
| W 抬表/sx20/已齐settle同题复读/variety再拧/庭内/精选settle同题/全族Path大扫/legacy evacuate 大扫 | 硬禁 |

---

*RESEARCH · FeaturedPath.offerPick / applyAndReply 精选路径选打菜单化 · **CLOSED live** · `1465d7622` / `c72e8a964` · PASS `687033a51` · TIP-DE / D13336 · 半行 REJECT `d00ed84bc` 保持 · 精选路径选打菜单化同题勿再交 · 下一窗：内容优先（挂机若新硬债 / 短征若新薄债 / 其他菜单去指令残留如 ChallengePath.offerPick 等另窗逐支 / legacy evacuate 另窗；精选 settle+选打本支已齐）。*

## STATUS: **CLOSED live** · `1465d7622` / `c72e8a964` · PASS `687033a51` · D13336 · TIP-DE · 2026-10-14 10:57 Asia/Shanghai
