# 余烬服 · Grok Bot 交接（给 GPT 协助策划）

Date: 2026-10-01 13:38 Asia/Shanghai  
From: 余烬-总控（Grok Bot）  
To: GPT（策划顾问）/ 下一任总控  
Repo: https://github.com/Picadoo/ember-server（私有）· branch `main`  
Workspace（Grok Bot 机器）: `/workspace/minecraft`

## 0. 一句话

文案薄窗流水线已跑到 **B2.111 PASS · 勾销**（close `86f655c`），全部已 push，工作区干净。流水线**暂停**，等用户带回 GPT 的策划意见。没有新意见时，默认下一窗是 **B2.113**。

## 1. 请 GPT 帮什么

用户希望 GPT 帮忙做策划判断，Grok Bot 这边按结论派单执行。最需要的几件事：

1. **方向**：继续清体力/门票口径的文案债（§4 队列），还是转去做内容和平衡（§6）？
2. **队列取舍**：§4 里哪些值得做、哪些可以合并或砍掉，顺序要不要调整。
3. **新内容**：如果转内容，给出 1～3 个可以拆成「一文件一窗」薄窗的具体提案，写清目标文件、玩家能看到的变化、数值依据。
4. **口径裁决**：§5 里几个悬而未决的点。

提案请遵守 §3 的硬规则，否则流水线没法执行。

## 2. 服务器与机制速览（截至 HEAD）

- Paper 1.12.2 自定义核心。玩法插件 CoreRpg（Java，含 TicketEntryService / StaminaService / EliteService / QuestService / ProgressService）、TrMenu 3 菜单、DungeonPlus（DP）副本、MythicMobs 4.11.0、NeigeItems（NI）物品。
- **体力制已替代票制（玩家观感上）**：日回体力，`base_max 90`。按玩法扣费，数值以 `plugins/CoreRpg/cash.yml` 为准：日常 30、周本 45、深渊 30、团本 50、精英 40。
- **周首免**：周本、精英、团本的本周第一次进本免体力；深渊没有首免。OP 进本「管理免扣」，并跳过等级门。
- **精英试炼**：每人每周限**通关** 1 次；失败或超时不算通关，同周可以再来（之后每次扣 40）。
- 进本由菜单执行 `corerpg enter <type>`。扣费、拒绝、首免、退还的结果都由插件私聊告诉玩家，所以菜单 tell 只写中性句（「尝试进入……」），不写扣费数字。
- PAPI 占位符可用：`%corerpg_stamina_cost_weekly|abyss|raid|elite%`、`%corerpg_stamina%`、`stamina_blocked_*`。
- 箱子上没有运行中的游戏服和 DB，验收全部是**静态**的（git diff、js-yaml、rg、代码核对）。reload 实测统一记为「待恢复服后实测」，不挡 PASS。

## 3. 流水线与硬规则

**每窗流程**：
1. 策划写 tip（`docs/design/design-*.md`），本地 commit。
2. 总控核对、push，批 A（改 tip STATUS 和 backlog），再 push。
3. 插件、物品或怪物岗施工，本地 commit；总控核对后 push。
4. 测试岗做静态验收，写 `docs/tests/TEST-B2.xx-*.md`，本地 commit；总控 push。
5. 总控 close：tip 改为 PASS · 勾销，新建 `docs/status/STATUS-*-close.md`，更新 backlog，push。
6. 交下一窗。

**硬规则（勿破）**：
- **一文件一窗**。同一文件里相邻的几行可以并成一窗；不捆厚窗。
- 专岗不 push，只有总控 push。提交身份 `Picadoo <Picadoo@users.noreply.github.com>`。
- secrets、jar、世界存档、日志、玩家数据不进 git；`ops.json` 必须是 `[]`。
- **勿宣称**：B0.1 已清、票已废、战令/勋阶/活动/深渊·灾厄已完整落地、地图已正式。
- NI 票物品保留（`ticket_convert` 要用）；精英预览壳不硬开。
- 玩家不打 slash，一律走 TrMenu。自定义物品用 NI ID 匹配，不按显示名。
- 明确不做（除非用户新令）：四件甲、誓约主动大改、锻炉重做、骨饰双上、位移和保命双上、无证据重开 B0.1。

权威 backlog：`docs/design/design-ember-content-backlog.md`，顶部是 Progress snapshot，L20 是当前窗，约 L350 是队列，约 L560 是表格。

## 4. 已排队列（默认顺序，均未出稿）

| 窗 | 对象 | 问题 | 类型 |
|----|------|------|------|
| B2.113 | TrMenu `ember_hub.yml` L215/L266/L280 | lore 写死体力 45/30/50 → 占位符 | YAML |
| B2.118 | TrMenu `ember_raid.yml` L22/L109/L118 | 写死 50 → `%corerpg_stamina_cost_raid%` | YAML |
| B2.120 | TrMenu `ember_abyss.yml` L49/L54/L130/L139 | 写死 30 → `%corerpg_stamina_cost_abyss%` | YAML |
| B2.121 | TrMenu `ember_abyss.yml` L132 | 「硬顶建议 ≤2/日（免费+购）」是票制旧口径，深渊没有免费次数 | YAML，改写方式待定 |
| B2.115 | DP `EmberWeekly/task/timeout.yml` L6 | 「周常超时失败（体力不返还）」在首免和 OP 情况下不准 | YAML |
| B2.116 | DP `EmberRaid/task/timeout.yml` L7 | 同上（团本） | YAML |
| B2.117 | CoreRpg `quest.yml` L377 | 「试炼每周一次」→ 每人每周限通关口径 | YAML |
| B2.103 | `CoreRpgPlugin.cmdAbyss` | L1356 fallback 文案、背包票数显示 | Java，需 build |
| B2.104 | `EliteService` | status 输出、L37-38/L61 注释 | Java，需 build |
| B2.105 | `set.yml` L27 | 旧口径 | YAML |
| B2.106 | `cash.yml` L86、L92 段首 | `hard_cap` 没有消费方（键保留） | YAML 注释 |
| B2.107 | src 模板 `cash.yml:92`、`quest.yml:389` | 与 live 不同步；live 存在时不会被覆盖 | 挂起，可不排 |
| B2.108 | `CoreRpgExpansion` L51 | 旧口径 | Java，需 build |
| B2.119 | `TicketEntryService` 退还提示 | 发起者 40 tick 内下线既不退也不提示（:189）；5.5s 内重试退了但不提示（:209）；首免退还仍写「体力已退还」；DP 人数不足拒绝时报「缓存冷却」 | Java，需 build |
| B2.109 | mineflayer 测试脚本 | 一批脚本还在断言旧文案或用旧写法；还要补精英超时用例、团本和深渊菜单点击用例、hub lore 断言 | 测试 |

只记不排：Daily:7 和 Abyss:8 的 timeout 文案；level_gates 缺 elite 键时阈值为 0；`corerpg enter` 与 `corerpg elite start` 命名不统一；七个日常本的「已消耗体力」；quest.yml L134、L73、L243；菜单灰显（raid 的 blocked_raid、abyss 的 L41）不区分 OP；`ember_abyss` L78 撤离句与 DP L23 重复。

## 5. 待裁决的口径问题

1. **B2.121**：深渊「硬顶 ≤2/日」现在还成立吗？如果成立，该怎么表述（每日上限？体力自然约束？），还是直接删掉？
2. **灰显不区分 OP**：要不要开 build 窗新增 `stamina_blocked_abyss`，并让 OP 不被灰显？这只影响管理员，优先级可能很低。
3. **B2.119 退还提示**：值不值得为几种边角情况改 Java、重新 build？
4. **B2.109 mineflayer**：测试脚本大面积过期，要不要整体重写一批，而不是逐个修？
5. **周首免文案**：timeout 失败提示要不要明确说「本周首次免费那次失败了不返还」？

## 6. 内容/平衡方向的旧候选（来自上一份交接，未立项）

- NI `ember-dungeon-tickets.yml` 的显示名仍是「余烬日票」等（迁移期可兑换；改名需要设计）。
- 玩法债：断塔近阶偶发掉底厅；霜、锈两线没有 Boss 前压；AFK 二档通胀。
- 平衡要先算再做：以 `base_max 90` / 日回量 / 各玩法单价为基础，给出每日、每周可玩次数和产出预期。

## 7. 本轮已结窗（2026-09-30 ~ 10-01）

B2.91–B2.102、B2.112、B2.114、B2.110、B2.111 全部 PASS · 勾销。每窗都有 `docs/status/STATUS-*-close.md` 和 `docs/tests/TEST-B2.xx-*.md`。最近四窗：

| 窗 | 改动 | close |
|----|------|-------|
| B2.112 | 精英 timeout L6 →「这次不算通关，本周还能再来」 | `e63340f` |
| B2.114 | quest.yml L389 hint →「精英试炼（每人每周限通关 1 次）」 | `46de54d` |
| B2.110 | ember_raid L76-77 →「[团本] 尝试进入……」「人数 3～5」 | `1530bc4` |
| B2.111 | ember_abyss L77 →「[深渊] 尝试下潜……（需余烬 Lv.25）」 | `86f655c` |

## 8. 专岗（Grok Bot 侧 agent id）

| 岗 | id |
|----|----|
| 余烬-总控 | Grok Bot（用户主 bot） |
| 余烬-策划 | `140d4710-e7d4-45ca-ad4a-dba75abe0af3` |
| 余烬-插件 | `78749aa1-2fd7-4843-9173-642238b4f65f` |
| 余烬-物品 | `3c51611a-c47b-43d4-911b-b123b055f60b` |
| 余烬-怪物 | `8957a58f-aaea-430d-8314-334ec6ee544d` |
| 余烬-测试 | `a46f07ef-0c32-4594-82f3-1ed3fcd46b3b` |
| 余烬-Paper | `8724bc4b-4240-4e72-a307-cdb04a7a9c7b` |
| 余烬-挑刺玩家 | `2fa6b735-502d-4d68-afa2-aa7d350dce80` |

## 9. 恢复方式

用户把 GPT 的意见贴回 Grok Bot 聊天后，总控按意见调整 backlog 顺序，再交策划出下一窗。没有意见时，从 B2.113 继续。
