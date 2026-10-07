# AUDIT · Ember 旧路径可达性审计（2026-10-05）— ARCH §6 N1

> **性质：** 离线审计快照（D197，2026-10-05）——当时只写文档、不改代码。§1–§4 的 LEAK 表保留为**历史证据**；§5 S0 条目已标 ✅ 落地状态。
> **落地状态（D250 追记，2026-10-07）：** S0-1～S0-5、S0-8 已随 CoreRpg **1.65.35–1.65.38** 上线（D198–D202）。普通玩家侧 L1–L12 入口 / 发奖纵深已关；OP / 控制台仍放行。残余提案：S0-9（世界级传送兜底）、S0-10（旧仓库写路径）。对照 ARCH §0 #2 / §3 O1–O3 / §4 R1 / §6.1。
> **基线（审计当时）：** `main` = `087fc8c`；CoreRpg **1.65.34**；P1 `enabled: true`。
> **上级文档：** `docs/design/ARCH-ember-systems-map-2026-10-05.md`（§2 数据流、§3 O1–O3、§5 S0、§6 N1）。
> **方法：** 从 `J/CoreRpgPlugin.java:939–1092 onCommand` 逐条路由到各服务的 `cmd / cmdRoot`；对照 `CoreRpg/src/main/resources/plugin.yml`、`server-runtime/permissions.yml`、`plugins/DungeonPlus/dungeon/*/option.yml`、`plugins/TrMenu/menus/*.yml`、`plugins/MythicMobs/Mobs/*.yml`、`P/*.yml`；`tools/p1sim/*.py` 用 `rg -i` 逐词确认是否建模。
> **证据等级：** 【码】源码确认 ·【配】配置确认 ·【文】只见于文档 / 历史测试记录 ·【待测】静态推断、需实服验证 · **未确认** = 没能静态确认，不猜。
> **路径缩写：** `J/` = `CoreRpg/src/main/java/town/sunshine/corerpg/`，`P/` = `plugins/CoreRpg/`，`DP/` = `plugins/DungeonPlus/dungeon/`，`TM/` = `plugins/TrMenu/menus/`，`MM/` = `plugins/MythicMobs/Mobs/`，`SR/` = `server-runtime/`（`SR/plugins` 是指向 `../plugins` 的符号链接）。
> **保密：** 本文不引用 `P/config.yml` 的数据库段、`secrets/*.env`、AuthMe 配置的任何内容；引用 `P/config.yml` 只到非敏感键的行号（`coin` / `calamity` / `afk_caps`）。

## 0. 一页结论

1. **共 12 条 LEAK（审计当时快照）**——P1 开着时普通玩家曾能拿到 p1sim / p2econ 没建模的奖励。**D250：玩家侧已由 S0 关闭**；下表保留证据，勿再当「现行开放清单」：

| # | 风险 | 一句话 | 主要证据 |
|---|---|---|---|
| L1 | 高 | **玩家可直接 `/dp start <旧本>`**，完全绕过 CoreRpg 的体力 / 周免：`dungeon.start` 对所有人 `default: true`，旧本开本条件只有人数 + 余烬等级，新角色出生就是 Lv10 = 日常门槛；可无限次刷 7 个旧日常（每局核心碎片 1 + 碎片 5 + 附魔晶 1 + 旧经验 + 小怪掉落） | `SR/permissions.yml:5–7`、`DP/EmberDaily/option.yml:12–14,22–29`、`J/PlayerData.java:36`；P1 本有 `%corerpg_p1_pass_*%` 一次性通行而旧本没有（`DP/EmberQ01/option.yml:16`）；历史实测「非 OP `/dp start EmberGuildBoss` 开本成功」（`docs/tests/smoke-gating-drops-20260926.md:100`）【配 / 文，待测】 |
| L2 | 高 | `/corerpg enter daily*/weekly/abyss/raid/elite`（及 `/corerpg elite`）不查 P1 开关；周本 / 精英 / 团本每周 1 次**不扣体力** | `J/TicketEntryService.java:113–154,271–296`、`J/StaminaService.java:208–212,300–314`【码，待测】 |
| L3 | 高 | 竞技场对战币**无次数上限**：两号排 1v1、一方立刻认输，胜 25 + 负 10 币 / 场 | `J/ArenaService.java:226,246,374–382,611–649`、`P/arena.yml:19–20`（`ArenaService` 无任何场次上限字段，grep `cap|limit` 为 0）【码 / 配，待测】 |
| L4 | 中 | `/corerpg pass free`：每天 1 封邮件 80 币 + 碎片 2 | `J/CashService.java:526–544` → `P/mail.yml:24–29`【码 / 配】 |
| L5 | 中 | `/corerpg arena claim`：每天 80 币 + 外观碎片 1，不需要打过任何一场 | `J/ArenaService.java:242–243,308–321`、`P/arena.yml:11–14`【码 / 配】 |
| L6 | 中 | **旧经验 → P1 战力**：旧本 / 旧击杀 / 灾厄发的余烬经验抬高 `emberLevel`，而 P1 公式 `B += 0.2×(L−10)`、`H0 += (L−10)`；p1sim 只累计主线结算经验 | `J/p1/EmberFormula.java:7–8,14,21–29`、`J/p1/EmberLoadoutService.java:182–189`、`P/progress.yml:35–55`、`tools/p1sim/p1sim.py:124–132,972`【码 / 配】 |
| L7 | 中 | `/corerpg calamity join`（Lv30）随时传送到 `ember_event`；每天 3 窗公共首领：参战奖 + 日箱 + MM 掉落（含 T3 旧装 / 孔石 / 核心）+ 不封顶击杀币 | `J/CoreRpgPlugin.java:1520–1533`、`J/CalamityService.java:432–462,543–590`、`P/calamity.yml:16–36`、`MM/EmberCalamity.yml`（12 条 `corerpg mmgive`）【码 / 配，待测】 |
| L8 | 中 | `/corerpg pass claim`：战令等级奖励（币 100 / 碎片 3 / 核心 / **体力药 30**）；P1 下战令经验只来自旧本的控制台 `corerpg progress`，入口本身不查 P1 | `J/ProgressService.java:279–311,423–452`、`P/progress.yml:14–27,62–124`（grep `grantPassXp` 调用点只有旧签到 / 旧悬赏 / 管理命令）【码 / 配】 |
| L9 | 中 | `/corerpg scrap` / `reforge`：把旧装（旧本掉落、`weekly_t1` / `abyss_t2` 箱、灾厄）拆成碎片 / 骨尘 / 孔石——旧装 → P1 材料的转换器 | `J/ScrapService.java:229–233,253–330`、`P/scrap.yml:2–98`、`P/loot.yml:5–13`【码 / 配】 |
| L10 | 低 | `/corerpg vip claim`：勋阶 0 也每天 20 币 | `J/CashService.java:506–520`、`P/cash.yml:99–103`【码 / 配】 |
| L11 | 低 | `/corerpg guild create/donate/boss`：5000 币建盟 + 捐 200 碎片 → 盟 Boss，全队每人碎片 8 / 核心 1 / 骨尘 4 / `guild_gem` + 余烬经验 100；材料净亏但队友白拿、经验未建模 | `J/GuildService.java:487,696–770,782–830`、`P/guild.yml:4–34`、`DP/EmberGuildBoss/option.yml:16–18,30–39`【码 / 配】 |
| L12 | 低（依赖 L1/L2/L7 才能到达） | 旧击杀币 / 旧击杀经验 / 旧悬赏进度：旧本实例和 `ember_event` 不在 `afk_caps.worlds` → **不封顶**；旧本小怪 `corerpg mmgive` 掉材料 / 旧装也不封顶 | `J/CoreRpgPlugin.java:839–877,1402–1461,1472–1494`、`P/config.yml:22,99–114`【码 / 配】 |

2. **ARCH O1–O3 在审计当时全部成立，而且比当时 ARCH 写的更松**（最大口子是 `/dp start` 直开 L1 + 新号 Lv10）。**现况：** O1/O2/O3 已 CLOSED（S0-1～S0-4）；ARCH 正文已于 D250 与实现对齐。
3. **菜单 / NPC 不是入口**：P1 主菜单 `TM/ember_hub.yml`（绑定 `ember` / `menu`，`:6–9`）及其可达子菜单里**没有**任何旧本或旧领取按钮（§2.3 的可达图）；旧入口菜单只挂在**不绑定命令**的 `TM/ember_hub_legacy.yml`（`:218/233/283/298`）；枢纽 NPC 只开 P1 菜单（`P/hub_npcs.yml:18–68`）；没有 Multiverse 传送门（`plugins/Multiverse-Portals/portals.yml` = `portals: {}`）；没有 Essentials / `/warp`（`SR/logs/latest.log` Enabling 列表）。**所有旧路径都是「手打命令」路径**——这对 S0 是好消息：一个命令层闸门就能封住绝大多数。
4. **已经封好的**：`sign / activity / bounty`（`J/CoreRpgPlugin.java:1207/1237/1287`）、`auction`（`:1062`）、旧主线引路人（`J/QuestService.java:238,465`）、挂机庭旧发奖（`J/p1/EmberAfkService.java:216–219` + `P/ember-v1.yml:101 legacy_payouts: false`）、P1 本内旧击杀币（`J/p1/EmberRunService.java:154–161`）、P1 世界里旧属性 / 旧药（`J/StatService.java:318`、`J/LifeService.java:160`）、`EmberCalamity` DP 实例（OP 才能开，`DP/EmberCalamity/option.yml:17`）。
5. **DEAD**：晶钻（`ember_crystal_cash`）没有玩家来源（只有管理员 `cash give` `J/CoreRpgPlugin.java:1656–1657` 和管理员邮件模板 `P/mail.yml:11–16`），所以 `shop buy * / monthly buy / 月卡登录币 / 战令付费轨` 对玩家是死路；`cash.yml` 各 `free_tickets` 发放函数恒返回 0（`J/TicketGrantService.java:110–113`）。
6. **p2econ / p1sim 覆盖**：`rg -i` 在 `tools/p1sim/*.py` 里对 `arena / vip / monthly / kill_reward / EmberDaily / EmberWeekly / mail / guild / calamity / scrap / reforge / socket / warehouse / auction / free_tickets` 全部 0 命中；`cash.yml` 只读了 `stamina.base_max`（`tools/p1sim/p1config.py:84,103`）。也就是说 **12 条 LEAK 没有一条在模型里**。
7. **推荐的前三个 S0 窗口（当时）→ 均已落地**：① S0-1 `%corerpg_gate_*%`（D198）；② S0-2 `TicketEntryService` 拒旧 kind（D198）；③ S0-3 路由默认拒绝 + `legacy_gate.allow`（D199）。后续 S0-4 发奖纵深（D200）、S0-5 竞技场上限（D201）、S0-8 菜单探针无需改码（D202）。

---

## 1. `/corerpg` 子命令全表

### 1.1 权限前提

- `/corerpg`（别名 `crpg` / `rpg`）本身要 `corerpg.use`（`CoreRpg/src/main/resources/plugin.yml:12–13`），`corerpg.use` 及 `enhance / socket / covenant / talent / skill / cash / shop / monthly / scrap / reforge / mail / friend / settings / pet / guild / arena / auction / warehouse` 全是 `default: true`（`plugin.yml:20–59` 各 `default: true`）；`corerpg.mail.send` 与 `corerpg.admin` 是 `default: op`（`:44–45`、`:60–61`）。
- 服上**没有权限插件**：`SR/logs/latest.log` 的 Enabling 列表是 Vault / ProtocolLib / Adyeshach / PlaceholderAPI / spark / CoreWorldRules / Multiverse-Core / Multiverse-Portals / HolographicDisplays / MythicMobs / DungeonPlus / NeigeItems / Core* / TrMenu / CoreGacha，无 LuckPerms（`plugins/LuckPerms/` 只有配置目录，无 jar）。Bukkit 自带 `SR/permissions.yml` 把 `dungeon.user / dungeon.start / dungeon.teleport / dungeon.game.*` 设为 `default: true`（`:1–22`），`op-permission-level=4`（`SR/server.properties:13`）。
- 因此下表「谁能用」只有三种：**所有人**（default true）、**OP**（`corerpg.admin` / `corerpg.mail.send` / `isOp()`）、**控制台**。许多旧命令把 `corerpg.<x>` 与 `corerpg.use` 做 OR（例：`J/CoreRpgPlugin.java:1352,1367,1573`），等于所有人。
- **P1 开关**指 `EmberMode.active()`（`J/p1/EmberMode.java:61–64`）；「P1 世界」指 `EmberMode.isP1(...)`（`:66–75`，scope = `ember_afk` + 前缀 `dungeon_EmberQ0`，`P/ember-v1.yml:8–14`）。只看「P1 世界」的判断在枢纽 / 旧本世界里**不生效**。
- **控制台调用**：DP 旧本奖励脚本、MM `~onDeath` 都以控制台身份调 `/corerpg progress / loot / abyss settle / elite weekly-first / raid grant-ring / mmgive / mmxp`，所以这些子命令在表里是 ADMIN-ONLY，但它们是 L1 / L2 / L7 的**发奖下游**。
- **副本内**：DP 只放行白名单命令（`plugins/DungeonPlus/config.yml:23–48`：`dp`、`corerpg quest/stats/skill/属性/技能/主线`、`corerpg abyss evacuate/leave`、`corerpg p1 status/run/watch` 等），其余在副本里打不出来；`QuestService.onDungeonCmdEarly` 另拦回城类命令（`J/QuestService.java:1232–1270`）。

### 1.2 全表

图例：**OK** = P1 下可达但不发 P1 外的奖励（或本来就是 P1 系统）；**LEAK** = P1 玩家可拿到未建模奖励（编号见 §0）；**ADMIN-ONLY** = 只有 OP / 控制台；**DEAD** = 玩家可调用但实际没有效果 / 没有前置来源。「p2econ」列：**否** = `tools/p1sim` 全目录 grep 0；**是** = P1 自己的来源 / 消耗已建模；**—** = 不涉及经济。

| 子命令（别名） | 路由 | 谁能用 | 查 P1？ | 发 / 扣 | p2econ | 结论 |
|---|---|---|---|---|---|---|
| `help`（无参） | `J/CoreRpgPlugin.java:943` | 所有人 | — | 无 | — | OK |
| `reload` | `:945–950` | OP | — | 重载配置 | — | ADMIN-ONLY |
| `storage` / `storage guard [status]` | `:951–953`；`J/DbGuard.java:118–121` | 所有人看状态；`release` 要 OP | 否 | 无（显示存储模式 / ping） | — | OK |
| `invsnap …` | `:955–958`；`J/InvSnapService.java:729–730` | OP | — | 快照恢复（资产） | — | ADMIN-ONLY |
| `admin migrate-yaml-to-mysql` | `:959–961`；`:477–481` | OP | — | 数据迁移 | — | ADMIN-ONLY |
| `status` | `:962`；`:1123–1143` | 所有人 | 否 | 无（显示币 / 旧活跃 / 旧悬赏） | — | OK |
| `spawn` | `:963`；`:1145–1151` | OP | — | 控制台刷怪 | — | ADMIN-ONLY |
| `coin` / `coin give` | `:964`；`:1172–1203` | 查看：所有人；`give`：OP（`:1174`） | 否 | `give` 加币 | — | OK（查看）/ ADMIN-ONLY（give） |
| `sign` | `:965`；`:1205–1211` | 所有人 | **是**（`:1207` → P1 签到 `EmberSignService.legacySign`） | P1 签到 | 是（`signin.py`） | OK |
| `xpreward` / `passxp` | `:966–967`；`J/ProgressService.java:506–507` | OP / 控制台 | — | 原版经验等级 / 战令经验 + 余烬经验 | 否 | ADMIN-ONLY（但 L6 / L8 的下游） |
| `progress <玩家> <source>` | `:968`；`J/ProgressService.java:462–497` | OP / 控制台 | 否 | 战令经验 + 余烬经验（`P/progress.yml:14–55`） | 否 | ADMIN-ONLY（旧本通关脚本调用 → L6 / L8 下游） |
| `loot <玩家> <表>` | `:969`；`J/LootService.java:34–57` | OP / 控制台 | 否 | 旧装 / 孔石 / 稳定符（`P/loot.yml`） | 否 | ADMIN-ONLY（旧本通关脚本调用 → L1 / L2 下游） |
| `stats` / `属性` | `:970`；`J/StatService.java:525–528` | 所有人（看别人要 OP） | 是（P1 世界内显示 P1，`:318/326`） | 无 | — | OK |
| `p1` / `ember …` | `:971`；`J/p1/EmberCommand.java:13–14,47–113` | 玩家子命令（status / sig / spec / reroll / sign / vault / gearlib / codex / shop / cosmetic …）所有人；其余（mapbuild / enable / give / givedup / heal / debug …）OP（`:112–113`） | 是（P1 系统本身） | P1 来源 / 消耗 | 是 | OK（玩家部分）/ ADMIN-ONLY（其余） |
| `forge` / `锻造`（旧） | `:972`；`J/ForgeService.java:76–130` | 所有人（副本内拒绝 `:80–82`） | 否 | 扣币 + 材料，旧装升阶（只认旧 `gear_ember_*` 配方 `P/forge.yml:7–10`） | 否 | OK（只是旧装消耗；P1 世界不读旧属性 `J/StatService.java:318`） |
| `part` / `部件` craft | `:973`；`J/PartService.java:66–133` | 所有人 | 否 | 扣材料出旧部件（`P/part.yml`） | 否 | OK（同上；部件在 P1 世界是否有效果：未确认） |
| `mmgiveall` / `mmgive` / `mmxp` | `:974–975`；`:1402–1461` | OP / 控制台（MM `~onDeath`） | 只看 P1 本 / 挂机庭（`:1424–1425,1448–1449`） | NI 物品 / 余烬经验 / 原版等级 | 否 | ADMIN-ONLY（旧本 / 灾厄 MM 掉落的出口 → L12） |
| `quest` / `mainline` / `主线` | `:976`；`J/QuestService.java:491–560` | `talk` 所有人（需站在 NPC 旁）；`set/reset/event/npc` OP（`:500–501`） | **是**（`onJoin :238`、`talk :465` P1 下改为打开冒险页） | P1 下不发旧主线物品 | — | OK |
| `hubbuild` / `hubambience` / `hubnpc` / `abyssbuild` / `weeklybuild` / `elitebuild` / `dailybuild`…`railbuild` / `raidbuild` / `calamitybuild`（及各别名） | `:977–991` | OP（各服务 `cmd` 首行：`J/HubPlazaService.java:36`、`J/HubAmbienceService.java:98`、`J/HubNpcService.java:141`、`J/AbyssShaftService.java:56`、`J/WeeklyCorridorService.java:62`、`J/EliteCorridorService.java:62`、`J/DailyCourtyardService.java:82`、`J/DailyAshCorridorService.java:88`、`J/DailyCryptService.java:88`、`J/DailyTideService.java:86`、`J/DailySpireService.java:88`、`J/DailyFrostService.java:91`、`J/DailyRailService.java:91`、`J/RaidHallService.java:72`、`J/CalamityBasinService.java:53`） | — | 建图 | — | ADMIN-ONLY |
| `afk [1–4 / fight / list]` / `挂机` | `:992`；`J/AfkTierService.java:284–320` | 所有人（`build` 要 OP `:366`） | 是（P1 下委托 `EmberAfkService`，`:289–293,313–318`） | 传送挂机庭；P1 按击杀发币 / 经验 / 绑定材料 | 是（`afk.py`） | OK |
| `life` / `vendor` / `补给` / `生活`（buy / cook） | `:993`；`J/LifeService.java:272–350` | 所有人（副本内拒绝 `:278–281`；`fishdebug / xp` 要 OP `:282–283`） | 部分（只在**消耗**时 P1 世界改走 P1 规则 `:160`） | 扣币换面包 / 钓竿 / 药 / 重铸石 / 稳定符 / 使魔 / 副手（`P/life.yml:16–92`） | 否 | OK（只是币消耗；`offhand_ward/vita` 副手在 P1 世界是否生效：未确认） |
| `level` / `lv` / `等级` | `:994–998` | 所有人 | 否 | 无 | — | OK |
| `activity` | `:999`；`:1235–1241` | 所有人 | **是**（`:1237`，P1 下只开在线页） | P1 下无旧宝箱 | 是（`signin.py`） | OK |
| `bounty` | `:1000`；`:1285–1290` | 所有人 | **是**（`:1287`） | P1 下无旧悬赏币 | 是（P1 委托） | OK |
| `enhance [info]` / `enhance set` | `:1001`；`:1333–1363`；`J/EnhanceService.java:364–402` | 所有人；`set` OP（`:1334–1335`） | 否 | 扣币 + 材料强化**旧装**（白名单只有 `gear_ember_*`，`P/enhance.yml:4–12`，`J/EnhanceService.java:251–255`） | 否 | OK（P1 装备不在白名单；P1 世界不读旧属性） |
| `socket list/insert/remove` | `:1002`；`:1365–1395`；`J/EnhanceService.java:471–600` | 所有人 | 否 | 旧装镶孔石 | 否 | OK*（P1 装备不在白名单、P1 世界不生效；但「旧宝石保持关闭」只靠 id 不匹配，`P/enhance.yml:2 enabled: true` + 命令开放 → S0 显式关） |
| `calamity [status]` | `:1003`；`:1518–1553` | 所有人 | 否 | 无（显示窗口） | — | OK |
| `calamity join` / `go` | `:1520–1533` | 所有人（Lv30，OP 免门 `:1525`） | **否** | 控制台 `mvtp` 到 `ember_event`（`:1529–1530`）→ 灾厄奖励 | 否 | **LEAK L7** |
| `calamity forceopen/trigger/spawn/force/forceend/end` | `:1535–1549` | OP | — | 开 / 关窗口 | — | ADMIN-ONLY |
| `abyss [status/info]`（旧） | `:1004–1006`；`J/AbyssSettleService.java:222–230` | 所有人 | 否 | 无 | — | OK |
| `abyss progress <玩家> <层>` / `abyss settle <玩家>` | `J/AbyssSettleService.java:265–268,306–316,487–489` | OP / 控制台 | 否 | 旧深渊层结算（碎片 / 骨尘 / 核心 / 孔石 / T2 旧装 / 稳定符，`:402–463`） | 否 | ADMIN-ONLY（`DP/EmberAbyss/option.yml:25,36` 调用 → L2 下游） |
| `abyss settle`（自己）/ `evacuate` / `leave` / `上浮` | `J/AbyssSettleService.java:306–356` | 所有人（副本内白名单放行 `plugins/DungeonPlus/config.yml:30–33`） | 否 | 按本局旧深渊会话层数发奖（层数只能由控制台 `progress` 抬高）；`evacuate` 另执行 `dp leave` | 否 | OK（无会话时不发；有会话 = 已在旧深渊里 → 属 L2）。P1 本内执行 `evacuate` 的副作用：未确认（§6） |
| `cmdAbyss` 兜底 | `:1556–1569` | — | — | 只在 `abyssSettleService == null` 时走到 | — | DEAD |
| `elite` / `eliteweekly` / `精英试炼` [start] | `:1008–1012`；`J/EliteService.java:63–70,110–122` | 所有人（无参即开本） | **否** | → `TicketEntryService.tryEnter(ELITE)` → 旧精英本 | 否 | **LEAK L2** |
| `elite weekly-first <玩家>` | `J/EliteService.java:76–79` | OP / 控制台 | 否 | 稳定符 ×1 / 周 | 否 | ADMIN-ONLY（`DP/EmberEliteWeekly/option.yml:35` 调用） |
| `elite status` | `J/EliteService.java:127–147` | 所有人 | 否 | 无 | — | OK |
| `covenant [set/reset]`（旧誓约） | `:1013`；`:1571–1594`；`J/CovenantService.java:185–260` | 所有人 | 否 | 选旧誓约；重置扣重置券或晶钻（`P/covenant.yml:8–11`） | 否 | OK（P1 世界不读旧属性；与 Q06「自选誓约」同名不同物，ARCH O8） |
| `talent [info/unlock/reset]`（旧天赋）/ `talent grant` | `:1014`；`:1601–1638`；`J/TalentService.java:267–462` | 所有人；`grant` OP（`:1602–1604`） | 否 | 旧天赋点（来自余烬等级）；重置每日 1 次免费，否则券 / 晶钻（`:388–420`） | 否 | OK（P1 世界不读旧属性） |
| `cash` / `cash give` | `:1015`；`:1655–1686` | 查看：所有人；`give`：OP | 否 | `give` 加晶钻并计入勋阶充值（`:1671–1675`） | 否 | OK / ADMIN-ONLY |
| `stamina [show/info]` / `体力` | `:1016–1020`；`J/StaminaService.java:535–567` | 所有人 | 否 | 无 | 是（`stamina.base_max`） | OK |
| `stamina convert` / `兑换` | `J/StaminaService.java:549–559` | 所有人 | 否 | 背包旧票 → 体力 | 否 | DEAD（旧票玩家无来源：`J/TicketGrantService.java:110–113` 恒 0，`P/cash.yml:46–96` 各 `free_tickets: 0`；付费轨票要晶钻） |
| `stamina set/give` | `J/StaminaService.java:569–590` | OP | — | 改体力 | — | ADMIN-ONLY |
| `enter` / `进本` `q01..q07 [challenge]` / `q0Nc` | `:1021–1025`；`J/TicketEntryService.java:271–292,115–119` | 所有人 | **是**（`Kind.p1()` → `EmberRunService.tryEnter`） | P1 体力 30、P1 结算 | 是 | OK |
| `enter daily / daily_ash / daily_crypt / daily_tide / daily_spire / daily_frost / daily_rail / weekly / abyss / raid / elite`（及中文 / DungeonId 别名 `J/TicketEntryService.java:58–83`） | `J/TicketEntryService.java:113–228,296` | 所有人（OP 免等级免扣 `:121`） | **否** | 扣体力（`P/cash.yml:17–28`）或**周免**（weekly / elite / raid，`J/StaminaService.java:300–314`），控制台 `dp start-console`（`:177`）→ 旧本奖励（§2） | 否 | **LEAK L2** |
| `tickets` / `ticket` | `:1026`；`:1640–1653`；`J/TicketGrantService.java:120` | 所有人 | 否 | 无（显示） | — | OK |
| `tickets consume` | `J/TicketEntryService.java:301–305` | OP / 控制台 | — | 扣旧票 | — | ADMIN-ONLY |
| `shop buy daily_ticket / pass_unlock / weekly_ticket` | `:1027`；`:1688–1711`；`J/CashService.java:356–481` | 所有人 | 否 | 扣晶钻 → 体力药 / 战令付费轨（含 300 币兜底 `:429`）/ 周体力 45 | 否 | DEAD（晶钻无玩家来源，§0 第 5 条） |
| `monthly [buy]` | `:1028`；`:1713–1725`；`J/CashService.java:561–620` | 所有人 | 否 | 扣晶钻开月卡；生效后每日登录 200 币 + 体力 30（`:279–303`，`P/cash.yml:72–80`） | 否 | DEAD（同上；若管理员给过晶钻则变 LEAK） |
| `vip` / `vip claim` | `:1029`；`:1727–1743`；`J/CashService.java:484–520` | 所有人 | **否** | 勋阶 0 每日 20 币（勋阶 ≥1 120，`P/cash.yml:99–103`） | 否 | **LEAK L10** |
| `pass` / `战令`（show / rewards） | `:1030`；`:1746–1774` | 所有人 | 否 | 无 | — | OK |
| `pass free` | `:1760–1762`；`J/CashService.java:526–544` | 所有人 | **否** | 每日邮件 `pass_track_free`：80 币 + 碎片 2（`P/mail.yml:24–29`） | 否 | **LEAK L4** |
| `pass claim` | `:1764–1766`；`J/ProgressService.java:279–311` | 所有人 | **否** | 按战令等级寄等级奖励（`P/progress.yml:62–124`；免费轨 Lv10 / Lv20 各含 `consumable_ember_stamina_30` ×1 `:73,83`） | 否 | **LEAK L8** |
| `pass season [reset]` | `:1747–1749`；`J/ProgressService.java:328–345` | 查看所有人；`reset` OP | — | 换赛季 | — | OK / ADMIN-ONLY |
| `enderchest` / `ec` / `末影箱` | `:1031`；`:1777–1786` | 所有人 | 否 | 打开自己的末影箱 | — | OK（P1 材料入末影箱由 D177 守卫管，ARCH O9） |
| `scrap [info/confirm]` | `:1032`；`:1788–1799`；`J/ScrapService.java:253–330` | 所有人 | 否 | 拆旧装 → 碎片 / 骨尘 / 孔石（`P/scrap.yml:12–98`） | 否 | **LEAK L9** |
| `reforge` | `:1033`；`:1801–1809`；`J/ScrapService.java:392–420` | 所有人 | 否 | 扣重铸石重洗旧装副词条 | 否 | **LEAK L9**（同一转换链；单独看只是消耗） |
| `mail [read/claim/delete/list]` | `:1034–1037`；`J/MailService.java:122–180,430–447` | 所有人 | 否 | 领取邮件附件（币 / 晶钻 / NI） | 否 | OK（通道本身；发件来源是 L4 / L8） |
| `mail send` | `J/MailService.java:136–137,320` | OP（`corerpg.mail.send`） | — | 发任意附件 | — | ADMIN-ONLY |
| `friend` / `friends` | `:1038–1041`；`J/FriendService.java:59–115` | 所有人 | 否 | 无（grep `addCoin / giveNiItem` 为 0） | — | OK |
| `settings` / `setting` | `:1042`；`:1811–1850` | 所有人 | 否 | 无 | — | OK |
| `ladder [me/power/abyss/speed]` / `ladder refresh/set` | `:1043–1046`；`J/LadderService.java:404–430` | 查看所有人；`refresh/set` OP | 否 | 无 | — | OK / ADMIN-ONLY |
| `pet` / `pets` | `:1047–1050`；`J/PetService.java:261–300` | 所有人 | 否 | 消耗使魔物品 / 魂尘；纯外观（类注释 `:38–40` 「Cosmetic familiar … Not sold as power pets」，加成只「展示」`:136–142`） | 否 | OK |
| `guild` / `alliance` / `盟约`（create / info / invite / accept / leave / kick / donate / boss） | `:1051–1054`；`J/GuildService.java:313–364` | 所有人 | **否** | 建盟 5000 币（`P/guild.yml:4–8`）、捐碎片 / 骨尘换贡献（`:19–24`）、`boss` 扣 20 贡献开 `EmberGuildBoss`（`:27–34`） | 否 | **LEAK L11** |
| `arena` / `竞技` / `pvp`（status / stats / leave） | `:1055–1058`；`J/ArenaService.java:207–252` | 所有人 | 否 | 无 | — | OK |
| `arena queue 1v1/2v2` / `forfeit` | `J/ArenaService.java:226–249,374–382` | 所有人 | **否** | 传送到 `world` 对战垫（`P/arena.yml:21–23`）；胜 25 / 负或平 10 币（`:611–649`；世界缺失时即时随机结算 `:725–760`） | 否 | **LEAK L3** |
| `arena claim` | `J/ArenaService.java:242–243,308–321` | 所有人 | **否** | 每日 80 币 + `cosmetic_calamity_shard` ×1 | 否 | **LEAK L5** |
| `auction` / `寄售` / `ah` | `:1059–1069` | 玩家在 P1 下被拒；OP 可用 | **是**（`:1062–1066`，`P/ember-v1.yml:126 legacy_auction: false`） | — | — | OK（已封） |
| `warehouse` / `仓库` / `wh`（list / deposit / withdraw / unlock / info） | `:1070–1073`；`J/WarehouseService.java:102–138` | 所有人 | 否（只在 withdraw 里扣绑定部分 `:257–261`） | 存取同一份材料仓（`EmberVault` 复用）；`unlock` 扣币 / 晶钻扩格（`:335`） | 否 | OK（不是来源；但写同一份 P1 数据，ARCH R5 ② → S0-10） |
| `skill` / `技能` [info] | `:1074–1077`；`J/SkillService.java:212–227` | 所有人 | 是（P1 世界内只有烬斩 `:267`） | 无 | — | OK |
| `flex` / `轻技`（equip / unequip / cast / info） | `:1078–1081`；`J/FlexSkillService.java:147–175` | 所有人（P1 主菜单链接 `TM/ember_hub.yml:246`） | 否 | 无经济；5 格冲刺位移（ARCH R6） | — | OK（非经济；战斗面未建模见 ARCH R6） |
| `raid` / `团本`（status / ring / claim-ring / claim） | `:1082–1085`；`J/RaidService.java:53–90` | 所有人只看状态；自领要 OP（`:80`） | 否 | 无 | — | OK |
| `raid grant-ring <玩家>` | `J/RaidService.java:55–70` | OP / 控制台 | 否 | 团戒 ×1 / 周 | 否 | ADMIN-ONLY（`DP/EmberRaid/option.yml:43` 调用 → L2 下游） |
| `set` / `套装` | `:1086–1089`；`J/SetService.java:85–100` | 所有人 | 是（P1 世界不算旧套 `:113`） | 无（显示） | — | OK |
| 未知子命令 | `:1090` | — | — | 显示帮助 | — | OK |
| `/hub`（`spawn` / `lobby` / `回城`，独立命令） | `:940`；`:1498–1516` | 所有人 | 否 | 控制台 `mvtp` 回 `ember_hub`；副本内拒绝 | — | OK |

小计：**LEAK 入口 16 行**（`calamity join`、`elite`、`enter <旧 11 种>`、`vip claim`、`pass free`、`pass claim`、`scrap`、`reforge`、`guild`、`arena queue/forfeit`、`arena claim`）对应 §0 的 L2–L5、L7–L11；L1 / L6 / L12 不是 `/corerpg` 子命令（分别在 `/dp start`、经验管线、击杀事件里），见 §2–§3。

---

## 2. 旧 DungeonPlus 副本（13 个非 `EmberQ0*` 的 `Ember*` 目录）

`DP/` 下小写目录（`ember_daily`、`ember_daily_ash`…`ember_raid`、`ember_arena`、`ember_fest_gq26_v1` 等）是地图模板，不是副本定义，本节不列。

### 2.1 开本条件与奖励

所有旧本奖励脚本都以**控制台**执行（`console=true`），最后一行都是 `mvtp %player_name% ember_hub`；所有旧本开本条件只有「人数 + `%corerpg_gate_<id>%` 或 OP」，**没有** P1 本那种一次性通行（对照 `DP/EmberQ01/option.yml:16` `%corerpg_p1_pass_q01%`、`DP/EmberQ0R1/option.yml:17`）【配】。`%corerpg_gate_<id>%` 只比余烬等级（`J/CoreRpgExpansion.java:53–61` → `J/ProgressService.java:64–66`；精英另加「本周未通关」`J/EliteService.java:40–47`），**不看 P1 开关**【码】。

| 副本 | 地图 | 人数 / 门槛（option.yml） | 通关奖励（option.yml 行） | MM 小怪 / Boss 掉落 | P1 是否拦 |
|---|---|---|---|---|---|
| `EmberDaily`（庭院） | `ember_daily` `:6` | 1–2 人 `:13`；Lv10 `:14` | `:24–28`：核心碎片 1、附魔晶 1、碎片 5、`xp 3L`、`corerpg progress daily_clear`（余烬经验 60 + 战令 20，`P/progress.yml:18,44`） | `MM/EmberDaily.yml` 7 处 `corerpg mmgive`（旧装 / 碎片 / 核心 / 骨尘） | **否** |
| `EmberDailyAsh` | `ember_daily_ash` `:6` | 同上 `:13–14` | `:24–28` 同庭院 | `MM/EmberDailyAsh.yml` 8 处 | **否** |
| `EmberDailyCrypt` | `ember_daily_crypt` `:6` | 同上 | `:24–28` 同上 | `MM/EmberDailyCrypt.yml` 7 处（另 `MM/EmberCrypt.yml` 8 处，归属哪个副本：未确认） | **否** |
| `EmberDailyTide` | `ember_daily_tide` `:6` | 同上 | `:24–28` 同上 | `MM/EmberDailyTide.yml` 8 处 | **否** |
| `EmberDailySpire` | `ember_daily_spire` `:6` | 同上 | `:24–28` 同上 | `MM/EmberDailySpire.yml` 8 处 | **否** |
| `EmberDailyFrost` | `ember_daily_frost` `:6` | 同上 | `:24–28` 同上 | `MM/EmberDailyFrost.yml` 8 处 | **否** |
| `EmberDailyRail` | `ember_daily_rail` `:6` | 同上 | `:24–28` 同上 | `MM/EmberDailyRail.yml` 8 处 | **否** |
| `EmberWeekly` | `ember_weekly` `:8` | 1–3 人 `:15`；Lv20 `:17` | `:29–39`：核心 3、附魔晶 2、碎片 8、骨尘 4、`loot weekly_t1`（周首通 T1 护符 + 概率 T1 刃 / 护符，`P/loot.yml:9–13`）、`xp 6L`、`weekly_clear`（余烬经验 200） | `MM/EmberWeekly.yml` 12 处 | **否** |
| `EmberAbyss` | `ember_abyss` `:10` | 1–2 人 `:17`；Lv25 `:19` | `:25` 开局 `abyss progress 0`；`:36–39`：`abyss settle`（按层发碎片 / 骨尘 / 核心 / 孔石 / T2 / 稳定符，`J/AbyssSettleService.java:374–463`）、`loot abyss_t2`（`P/loot.yml:5–8`）、`abyss_clear` | `MM/EmberAbyss.yml` 26 处 | **否** |
| `EmberRaid` | `ember_raid` `:9` | 3–5 人 `:16`；Lv35 `:18` | `:37–47`：核心 4、附魔晶 2、碎片 10、骨尘 6、`loot raid_gem`、`raid grant-ring`（周 1）、`raid_clear`（余烬经验 250） | `MM/EmberRaid.yml` 15 处 | **否** |
| `EmberEliteWeekly` | `ember_elite` `:8` | 1–2 人 `:15`；Lv40 + 本周未通 `:17` | `:30–37`：核心 4、碎片 12、骨尘 6、附魔晶 1、`loot elite_gem`、`elite weekly-first`、`elite_weekly`、`quest event` | `MM/EmberEliteWeekly.yml` 8 处 | **否** |
| `EmberGuildBoss` | `ember_raid` `:8` | 1–5 人 `:15`；`%corerpg_guildboss_pass%` 或 OP `:18` | `:32–38`：碎片 8、外观碎片 1、核心 1、`loot guild_gem`、骨尘 4、`guild_boss_clear`（余烬经验 100） | `MM/EmberGuildBoss.yml` 11 处 | **否**（但只有盟主扣贡献后由控制台开，见 L11） |
| `EmberCalamity` | `ember_calamity` `:7` | 1–5 人 `:14`；**仅 OP** `:17` | `:32–35`：核心 2、附魔晶 1、灾厄余烬 2、`gear_ember_t3_talisman` | 公共窗口的灾厄使走 `ember_event`，见 §3 | 是（OP-only，ADMIN-ONLY） |

> 旧本里的 `corerpg mmgive` 掉落不受 P1 拦截：`J/CoreRpgPlugin.java:1424–1425,1448–1449` 只在 P1 主线实例（`EmberRunService.blocksLegacy`，`J/p1/EmberRunService.java:154–159` = `EmberRunMaps.byWorld` 命中或世界名以 `dungeon_emberq0` 开头）和挂机庭（`J/p1/EmberAfkService.java:216–219`）返回【码】。旧本实例世界名是 `dungeon_EmberDaily_…` 等，不匹配。

### 2.2 玩家怎么进

| 入口 | 覆盖哪些副本 | 是否扣体力 | 证据 | 结论 |
|---|---|---|---|---|
| **手打 `/dp start <Id>`** | 12 个（`EmberCalamity` 除外；`EmberGuildBoss` 需通行） | **否**（体力只在 `TicketEntryService` 里扣，`J/TicketEntryService.java:148`；`/dp start` 不经过 CoreRpg） | `SR/permissions.yml:5–7` `dungeon.start: default: true`（注释 `:1`「允许所有玩家使用 DungeonPlus 开始 / 离开」）；DP 只有 5 秒开本间隔 `plugins/DungeonPlus/config.yml:140–142`；历史实测非 OP `/dp start EmberGuildBoss` 曾开本成功（`docs/tests/smoke-gating-drops-20260926.md:100`），`DP/EmberCalamity/option.yml:17` 的 OP 门正是为此加的 | **L1**【配 / 文，待测】 |
| `/corerpg enter <kind>` | 7 个日常 + 周本 + 深渊 + 团本 + 精英 | 是（30 / 45 / 30 / 50 / 40，`P/cash.yml:17–28`）；周本 / 精英 / 团本每周首次 0（`J/StaminaService.java:300–314`，周重置发放 `:208–212`） | `J/TicketEntryService.java:113–228`，不调用 `EmberMode` | **L2**【码】 |
| `/corerpg elite [start]` | 精英 | 同上 | `J/EliteService.java:63–70,110–122` | **L2** |
| `/corerpg guild boss` | 盟 Boss | 扣盟贡献 20 | `J/GuildService.java:782–830`、`P/guild.yml:27–34` | **L11** |
| TrMenu 菜单按钮 | `TM/ember_daily.yml:88/129/170/211/252/293/334`、`TM/ember_weekly.yml:77`、`TM/ember_abyss.yml:79`、`TM/ember_raid.yml:79`（都是 `corerpg enter …`）；`TM/ember_calamity.yml:53`（`calamity join`）、`:98`（`dp start EmberCalamity`，OP 门挡住）；`TM/ember_guild.yml:109`；`TM/ember_hub_legacy.yml:268`（`corerpg elite start`） | — | 这些菜单只从 `TM/ember_hub_legacy.yml:218/233/283/298/313/533` 链过去；`ember_hub_legacy` 不绑定命令（`TM/ember_hub_legacy.yml:1`、`TM/ember_hub.yml:2–4`）；P1 主菜单 `TM/ember_hub.yml` 的全部 `menu:` / `command:` 目标（`:45–438`）里没有任何旧本菜单；普通玩家能否手打 `/trmenu open <菜单>`：**未确认**（TrMenu 权限节点未静态核对） | 菜单本身不是 P1 入口（待测第 7 条） |
| 枢纽 NPC | — | — | `P/hub_npcs.yml:18,34,50,67` 只开 `ember_p1_forge / ember_help / ember_p1_gear / ember_p1_adventure` | 无旧入口 |
| 传送门 / `/warp` | — | — | `plugins/Multiverse-Portals/portals.yml` 为 `portals: {}`；无 Essentials（`SR/logs/latest.log` Enabling 列表） | 无旧入口 |

### 2.3 P1 主菜单可达图（静态）

`/ember`、`/menu` → `TM/ember_hub.yml`（`:6–9`）→ P1 菜单（`ember_p1_afk / fest / adventure / gear / forge / abyss / season / sign`）+ `ember_bestiary`（`:230`，→ `ember_pet` `TM/ember_bestiary.yml:84`）、`ember_flex_skill`（`:246`）、`ember_life`（`:290`）、`ember_mail`（`:318`）、`ember_friends`（`:332`）、`ember_settings`（`:371`）、`gacha`（`:391`）、`ember_help`（`:249/304`）。其中带命令的旧系统按钮只有：`TM/ember_life.yml:31–120`（`life buy …`，币消耗）、`:138`（`stamina convert`，DEAD）、`TM/ember_mail.yml:72`（`mail claim all`，通道）、`TM/ember_pet.yml:54–92`（外观）、`TM/ember_friends.yml:88`（`dp team`，组队）。**没有**旧本、`pass / vip / arena / scrap / socket / guild` 按钮（这些只在 `ember_pass.yml:45/103`、`ember_vip.yml:62`、`ember_arena.yml:73/92`、`ember_disassemble.yml:57/76`、`ember_socket.yml:56–155`、`ember_guild.yml:109`，全部只从 `ember_hub_legacy` / `ember_shop` / `ember_forge` 链入，而这三个也只从 `ember_hub_legacy` 链入：`TM/ember_hub_legacy.yml:383/399/448/500/533/565/649/715/741`）【配】。

---

## 3. 旧击杀币 / 旧怪物掉落

### 3.1 判定链（`J/CoreRpgPlugin.java:831–884`，`onDeath`）

1. 公共灾厄使被杀 → `CalamityService.onCalamityKilled`（`:836–837`）——**在 P1 拦截之前**，所以不论在哪个世界都会结算灾厄奖励（`J/CalamityService.java:433–462`；参战奖 A `:543–557`：灾厄余烬 / 核心碎片 / 外观碎片；日箱 B `:564+`、`P/calamity.yml:28–36`：核心 2、附魔晶 1、灾厄余烬 2、10% 保护卷、周首凝核 1）。
2. P1 主线实例 → return（`:839`）；挂机庭（P1 开 + `legacy_payouts: false`）→ return（`:840`，`P/ember-v1.yml:101`）。
3. 其余世界：「合格击杀」（`:879–884`：自定义名含「余烬 / Ember / Crypt」，或类型为僵尸 / 骷髅 / 僵尸村民）→ `coin.kill_reward: 1` 币（`P/config.yml:22`）+ 活跃 / 余烬经验 2（日顶 100，`P/progress.yml:53–54`）+ 旧悬赏进度（P1 下悬赏领取已关 `:1287`，进度仍累计）。
4. 击杀币封顶只对 `afk_caps.worlds` 里的世界生效（`:849–855`；`P/config.yml:99–106`：`ember_afk`、`world`，日 150 币）；**不在列表里的世界（旧本实例、`ember_event`）直接放行、不封顶**（`:850–851` `inst = !contains(...)` → `allowCoin = … || inst`）【码】。
5. MM `~onDeath` 的 `corerpg mmgive / mmxp`（`:1402–1461`）同样只在 P1 实例 / 挂机庭被拦（`:1424–1425,1448–1449`）；`afkCapped`（`:1472–1494`）只管 `afk_caps.worlds`。

### 3.2 各世界

| 世界 | 来源 | 旧击杀币 / 旧掉落 | P1 玩家能否到达 | 证据 |
|---|---|---|---|---|
| `dungeon_EmberQ0*`（P1 主线 / 团本 / Boss / 节日实例） | P1 怪 | **不发**（`:839`；`MM/EmberP1Main.yml`、`MM/EmberFestival.yml` 各仅 1 处 `mmgive` 匹配，且被 `:1424` 拦） | 是（正常玩法） | OK |
| `ember_afk`（挂机庭） | `MM/EmberAfk.yml` 28 处 `mmgive` | **不发**（`:840`、`:1424–1425`） | 是 | OK |
| `ember_hub`（枢纽） | 无刷怪（CoreWorldRules 关自然刷怪 / 刷怪笼，只放行自定义刷怪：未确认枢纽是否有自定义刷怪点） | 理论上会发（不在拦截范围），实际无怪 | 是 | OK（待测：无） |
| 旧本实例 `dungeon_EmberDaily*_…` 等 | §2 的 MM 小怪 / Boss | **发且不封顶**（击杀币 1 / 只 + 经验 2 + `mmgive` 材料 / 旧装） | 经 L1 / L2 | **L12** |
| `ember_event`（灾厄公共窗） | 灾厄使 + 召唤物（`MM/EmberCalamity.yml` 12 处 `mmgive`：T3 旧装、孔石、核心 2、稳定符等） | **发且不封顶** + 灾厄 A / B | 经 `calamity join`（Lv30） | **L7** |
| `world`（主世界） | 竞技场对战垫（`P/arena.yml:21`）；`MM/RandomSpawns` 示例对 `world` 做 REPLACE，是否真的刷：未确认 | 击杀币封顶 150 / 日（`P/config.yml:101`）；竞技币另算（L3） | 经 `arena queue`（传送到对战垫） | L3（击杀币部分：未确认有怪） |
| `ember_daily_crypt`、`ember_daily_ash`（Multiverse 常驻世界） | 建图用 | 未确认有无怪 | 无玩家入口（无传送门 / 无 `/warp`；`mvtp` 普通玩家无权限，D202 实测） | 待测第 7 条 |
| `world_nether`、`world_the_end` | 原版 | 合格击杀 = 僵尸 / 骷髅类 → 1 币 + 2 经验，不封顶 | 无传送门；能否通过原版下界门从 `world` 过去：未确认 | 待测第 7 条 |

---

## 4. ARCH §3 点名的其他旧领取路径

| 路径 | 现状 | 证据 | 结论 |
|---|---|---|---|
| 竞技场（日箱 + 对战币） | 开放，不查 P1；对战币无场次上限 | §1 表 `arena` 行 | **L3 / L5** |
| 勋阶（VIP）日领 | 开放；勋阶 0 也 20 币 / 日 | `J/CashService.java:506–520`、`P/cash.yml:99–103` | **L10** |
| 月卡 | 要晶钻买，玩家无晶钻来源 | `J/CashService.java:579`、`:279–303`；晶钻来源只有 `J/CoreRpgPlugin.java:1656–1657`（OP）与邮件附件 `J/MailService.java:434–438`（发件要 OP，`P/mail.yml:11–16` 模板只给管理员发） | DEAD（管理员给晶钻后变 LEAK：每日 200 币 + 30 体力，`P/cash.yml:72–80`） |
| 战令免费轨补给 / 等级奖励 | 开放 | `J/CashService.java:526–544`、`J/ProgressService.java:279–311` | **L4 / L8** |
| 战令付费轨 / 商城 | 要晶钻 | `J/CashService.java:392–443` | DEAD |
| 旧签到 / 旧活跃 / 旧悬赏 | P1 下已改道 | `J/CoreRpgPlugin.java:1207,1237,1287` | OK（已封） |
| 旧宝石（镶嵌） | `P/enhance.yml:2 enabled: true`，`/corerpg socket` 对所有人开放（`J/CoreRpgPlugin.java:1365–1395`）；只靠 P1 装备 id 不在 `socket:` 表（`P/enhance.yml:73+`）而不生效；孔石仍可从旧本 / 灾厄 / `scrap` 拿到并镶到旧装上 | ARCH §3 O6、G5 | OK*（P1 世界不读旧属性 `J/StatService.java:318`；但「保持关闭」不是显式的 → S0-7） |
| 深渊免费票（`cash.yml abyss.free_tickets`） | 0；发放函数恒返回 0 | `P/cash.yml:88–89`、`J/TicketGrantService.java:110–113` | DEAD |
| 日常 / 周本 / 团本 / 精英免费票 | 同上 0 | `P/cash.yml:46–47,82–94` | DEAD；**但**周本 / 精英 / 团本的「本周首次免费」是另一条路（体力周额度，`J/StaminaService.java:208–212`）→ L2 |
| 旧主线（引路人 / 章节奖励） | P1 下改为打开冒险页 | `J/QuestService.java:238,465` | OK（已封） |
| 旧挂机发奖 | P1 下关闭 | `J/AfkTierService.java:289–293`、`P/ember-v1.yml:101` | OK（已封） |
| 寄售行 | P1 下对玩家关闭 | `J/CoreRpgPlugin.java:1062–1066`、`P/ember-v1.yml:126` | OK（已封） |
| 盟约（建盟 / 捐献 / 周盟 Boss） | 开放；主菜单已移除入口（`TM/ember_hub.yml:333` 注释），命令仍通 | `J/GuildService.java:313–830` | **L11** |
| 旧天赋 / 旧誓约 | 开放；只影响旧属性 | `J/TalentService.java:267–462`、`J/CovenantService.java:185–260` | OK*（与 P1 Q06 誓约同名不同物，ARCH O8） |
| 拆解 / 重铸 | 开放 | `J/ScrapService.java:253–420` | **L9** |
| 旧强化 / 锻造 / 部件 | 开放；只认旧 id | `P/enhance.yml:4–12`、`P/forge.yml:7–10`、`J/PartService.java:66` | OK*（币 / 材料消耗；与 P1 精工 / 强化共用币池但未建模） |
| 材料仓 `warehouse` | 开放；与 P1 金库共用数据（ARCH R5 ②） | `J/WarehouseService.java:102–335` | OK（不是来源；写路径需 S0-10 往返测） |
| 邮件 | 通道；发件 OP | `J/MailService.java:136–137,320,422–447` | OK（来源在 L4 / L8） |
| 扭蛋 CoreGacha（`gacha`，P1 主菜单 `TM/ember_hub.yml:391`） | 设计为只出无属性外观 | 本次未审计 CoreGacha 源码 | 未确认（不在 N1 范围） |
| 灾厄公共窗 | 开放（Lv30） | §3 | **L7** |
| 旧余烬经验管线（L6） | 所有旧来源都写同一个 `emberLevel` | `J/ProgressService.java:168–196`、`J/p1/EmberLoadoutService.java:182–189`、`J/p1/EmberFormula.java:21–29` | **L6** |

---

## 5. S0：默认拒绝 + 白名单（**S0-1～S0-5 / S0-8 已落地 · D198–D202**；S0-9 / S0-10 仍提案）

> **2026-10-05 更新（D198）：** S0-1、S0-2 已实现并随 CoreRpg 1.65.35 上线（`J/LegacyGate.java` + `J/CoreRpgExpansion.java` gate_ / guildboss_pass + `J/TicketEntryService.java` tryEnter；P1 开着才生效，OP / `corerpg.admin` / 控制台放行）。S0-3～S0-10 仍是提案。
>
> **2026-10-05 更新（D199）：** S0-3（含 S0-6 / S0-7）已实现并随 CoreRpg 1.65.36 上线：`J/CoreRpgPlugin.java onCommand → legacyRouteRefused` + `J/LegacyGate.refuseRoute`，白名单在 `P/ember-v1.yml legacy_gate.allow`。S0-4（D200，1.65.37）、S0-5（D201，1.65.38）随后也已上线；S0-8～S0-10 仍是提案。
>
> **2026-10-05 更新（D200）：** S0-4（①～④ 全部）已实现并随 CoreRpg 1.65.37 上线：`ProgressService.grantEmberXp / grantPassXp` 开头 `legacyXpBlocked`、`onDeath` / `cmdMmCredit` 的 `legacyKillPayoutBlocked(world)`、公共灾厄 `blocksCalamitySettle`；配置 `P/ember-v1.yml legacy_gate.payout_guard / xp_sources_allow / kill_payout_worlds`。

> 本节是给后续例行窗口的**草案**。每条都尽量小到「一个窗口：改一处 + 静态测 + 进下一次合并冒烟」。按风险从高到低排；S0-1～S0-3 合起来就能封住 L1～L5、L7～L11 的入口，S0-4 是纵深防御（即使有人绕过入口，发奖端也不发）。所有闸门都只在 `EmberMode.active()` 为真时生效，P1 关掉（旧模式）时行为不变；管理员（`corerpg.admin`）和**控制台**一律放行——DP / MM 的发奖脚本都以控制台身份调 `/corerpg …`，绝不能挡控制台。

### 5.1 白名单（P1 开着时普通玩家仍可达）

- **世界：** `ember_hub`、`ember_afk`、`dungeon_EmberQ0*`（Q01–Q07、R1–R3、B1/B2、节日 F1 至 10-08）。其余（`ember_event`、旧本实例、`world`、`world_nether`、`world_the_end`、`ember_daily_*` 常驻图）在 P1 下不应有玩家入口。
- **`/corerpg` 子命令：** `help / status / coin（查看） / sign / activity / bounty / stats / p1|ember（玩家部分） / quest（talk） / afk / life / level / enter q0x / tickets（查看） / cash（查看） / stamina（查看） / pass（查看） / storage（查看） / mail（读 / 领 / 删） / friend / settings / ladder（查看） / pet / skill / flex / set / enderchest / warehouse / calamity status / abyss status / elite status / raid status`，以及 `/hub`。
- **DP：** 玩家侧只需要 `dp team` / `dp leave` 一类（`TM/ember_friends.yml:88`）；开本一律走 CoreRpg 的控制台 `dp start-console`（`J/TicketEntryService.java:177`，P1 本经 `EmberRunService.tryEnter`）。

### 5.2 按风险排序的窗口

| # | 封什么 | 建议改哪里 | 大小 / 风险 | 测法 |
|---|---|---|---|---|
| **S0-1** ✅ **已建（D198，CoreRpg 1.65.35）** | **L1（`/dp start` 直开旧本）+ L2 的 DP 侧** | `J/CoreRpgExpansion.java:53–61`：`gate_*` 在 `EmberMode.active()` 时对旧 gate id（`daily / weekly / abyss / raid / elite`）返回 `"no"`；`guildboss_pass`（`:89`）同理。DP 会对全队每人求值，所以不管谁开、走哪条路都会被拒；OP 仍被 `%player_is_op%` 放行。**备选**：把 `SR/permissions.yml:5–7` `dungeon.start` 改为 `default: false`——前提是先确认 P1 所有开本都只走控制台 `dp start-console`（`J/TicketEntryService.java:177`、P1 `EmberRunService`），且没有菜单让玩家自己执行 `dp start`（`TM/ember_calamity.yml:98` 是唯一一处，且只对 OP 有效）。 | 改 1 个方法约 5 行；风险低（只影响旧本） | 静态：`rg gate_` 确认无 P1 本使用这些 id；冒烟：待测第 1、6 条 |
| **S0-2** ✅ **已建（D198，CoreRpg 1.65.35）** | **L2（`/corerpg enter <旧>` / `/corerpg elite`）** | `J/TicketEntryService.java:115` 的 `if (kind.p1())` 分支之后加：`EmberMode.active() && !admin` → 提示「P1 模式下旧副本已关闭」并 `return true`。`EliteService.cmdStart`（`:63`）走的就是 `tryEnter(ELITE)`，一并覆盖。 | 约 5 行；低 | 冒烟：待测第 2 条（应被拒） |
| **S0-3** ✅ **已建（D199，CoreRpg 1.65.36）** | **L3 / L4 / L5 / L7 / L8 / L9 / L10 / L11 入口** | `J/CoreRpgPlugin.java:944`（`sub` 解析后）加**路由级默认拒绝**：仅当 `sender instanceof Player && EmberMode.active() && !sender.hasPermission("corerpg.admin")`，`sub` 不在白名单（§5.1）时拒绝；对带子动作的命令（`calamity join`、`pass free/claim`、`vip claim`、`arena *`、`abyss settle/evacuate`）按「子命令 + 动作」匹配。白名单写进 `P/ember-v1.yml` 新节 `legacy_gate:`（仿 `legacy_auction` `:126` 的写法，默认拒绝、可逐条放开），而不是硬编码。第一批拒绝：`arena`、`pass free`、`pass claim`、`vip claim`、`calamity join`、`guild`、`scrap`、`reforge`、`socket`、`enhance`、`forge`、`part`、`covenant`、`talent`（旧）、`shop`、`monthly`、`stamina convert`、`elite`（非 status）、`abyss`（P1 外的 settle）。 | 一次改 1 处 + 1 段配置；中（要逐条核对白名单，别误伤 P1 子命令；`abyss evacuate` 在 P1 本内的用途要先确认，见待测第 9 条） | 静态：把 §1 表逐行跑一遍白名单判断；冒烟：待测第 3–5、11 条（应被拒） |
| **S0-4** ✅ **已建（D200，CoreRpg 1.65.37）** | **L6 / L12 + 灾厄发奖（纵深防御）** | ① `J/ProgressService.java:168` `grantEmberXp` / `:423` `grantPassXp`：P1 开着时忽略旧 source（`daily_clear / weekly_clear / abyss_clear / raid_clear / guild_boss_clear / elite_weekly / elite / boss / kill / bounty`），只保留 P1 结算路径；② `J/CoreRpgPlugin.java:839–840`：P1 开着时，除 `ember_hub` 外所有非 P1 世界都不发旧击杀币 / 旧经验；③ `:1424–1425,1448–1449`：`mmgive / mmxp` 同理；④ `J/CoreRpgPlugin.java:836–837` / `J/CalamityService.java:433`：P1 开着时不结算公共灾厄（或连同调度一起停）。 | 4 处小改，可拆成 2 个窗口（经验一窗、击杀 / 灾厄一窗）；中（要确认 P1 自己的经验不走 `grantEmberXp(source)` 的旧 source 名） | 静态：`rg grantEmberXp\|grantPassXp` 列出全部调用点逐一标注 P1 / 旧；冒烟：待测第 10 条 |
| **S0-5** ✅ **已建（D201，CoreRpg 1.65.38）** | **L3 无上限的根因**（即使 S0-3 关了竞技场，日后重开也要有上限） | `J/ArenaService.java:624` `settleWinLoss`：加每日计币场次上限（配置 `P/arena.yml match.daily_coin_matches`），`forfeit` / 掉线结算不发参战币 | 小；低 | 静态 |
| **S0-6** | 晶钻相关 DEAD 路径（防止日后误开） | `shop / monthly / pass_unlock` 在 P1 下并入 S0-3 拒绝表；`cash give` 保持 OP | 随 S0-3 | — |
| **S0-7** | 旧宝石显式关闭（ARCH O6 / G5） | `P/enhance.yml` 加 `socket.enabled: false` 或 S0-3 拒绝 `socket`；孔石来源（旧本 / 灾厄 / `scrap`）随 S0-1～S0-4 一并断 | 随 S0-3 | 待测第 11 条 |
| **S0-8** ✅ **实测不需要（D202，2026-10-05 18:14）** | 旧本菜单 / 旧枢纽菜单的「手打 `trmenu open`」风险 | 待测第 7 条已做：普通玩家（非 OP 新号 FreshQ736）执行 `/trmenu`、`/trmenu open ember_hub_legacy / ember_daily / ember_arena / ember_shop / ember_calamity / ember_guild` 全部回「no permission」；旧菜单都没有 `Bindings: Commands`（只有 `ember_hub`（/ember、/menu）、`ember_help`、`ember_gacha` 和 `ember_p1_*` 绑了命令）；从这些入口按 `menu:` 动作走的静态可达闭包只到 `ember_bestiary / flex_skill / friends / life / mail / settings / pet`（`pet` 是纯外观，见 §1），碰不到任何旧本 / 旧枢纽菜单。所以不加 `Open-Requirement`。 | 无改动 | `tools/p1map/d202-s08-probe.sh`；`docs/tests/smoke-2026-10-05-d202-s08-probe.md` |
| **S0-9** | `ember_event` / `world` / 下界 / 末地的世界级兜底 | 进世界时（`PlayerChangedWorldEvent`）P1 开着、非 OP、目标不在白名单 → 送回 `ember_hub`。是最后一道闸，挡住所有未知传送路径（含 `mvtp` 若有权限） | 小；中（要排除竞技场 / 灾厄这类本来由控制台传送的合法路径——在 S0-3 关掉它们之后才安全） | 冒烟：待测第 5、7 条 |
| **S0-10** | 旧仓库写路径（ARCH R5 ②） | `J/WarehouseService.java:172` deposit / `:242` withdraw 在 P1 下改为只读或转给 `EmberVault` 的同一守卫；需 persist 往返测 | 中（涉及资产存取 → 按规矩要跑丢失 / 复制回归） | 资产回归（不是可达性问题，放最后） |

**推荐先做的前三个：S0-1 → S0-2 → S0-3。** S0-1 / S0-2 各约 5 行就把「最大量、不扣体力、新号即可」的旧本口子封掉；S0-3 一个路由闸 + 一段配置封住其余 8 条 LEAK 的入口，并且把「默认拒绝」变成以后新加命令的默认姿态（ARCH §5 S0 的核心诉求）。

---

## 6. 需要实服确认（并入下一次合并冒烟，1 个测试号即可）

> 用一个**新建的非 OP 测试号**（出生即 Lv10，`J/PlayerData.java:36`）；需要更高等级的条目，用管理员 `/corerpg xpreward` 抬级后再测（抬级本身是 ADMIN-ONLY，不算泄漏）。全部只读 / 只观察，不改配置。预计 5–8 分钟。

1. 新号 `/dp start EmberDaily`：能否开本？体力是否不变？通关后是否收到核心碎片 / 碎片 / 附魔晶（`DP/EmberDaily/option.yml:24–28`）？→ 定 L1。
2. 新号 `/corerpg enter daily`：是否扣 30 体力并开本？Lv20 号 `/corerpg enter weekly` 是否提示「本周首次免费」？→ 定 L2。
3. `/corerpg pass free` → `/corerpg mail claim all`：是否到账 80 币 + 碎片 2？同日再点应提示已领（代码日限 `J/CashService.java:529–531`；邮件正文 `P/mail.yml:26`「重复点击会再发一封」是旧文案，与代码不符）。→ 定 L4。
4. `/corerpg arena claim`、`/corerpg vip claim`：是否到账？（竞技场刷场 L3 需要第 2 个号，本轮只记「需双号」，不测。）→ 定 L5 / L10。
5. Lv30 号 `/corerpg calamity join`：是否被传送到 `ember_event`？窗口外是否也能进？→ 定 L7。
6. 新号（Lv10）`/dp start EmberWeekly`：应被 Lv20 门拒绝（验证 `gate_` 生效，作为 S0-1 的对照）。
7. 普通玩家能否执行 `/trmenu open ember_daily`、`/trmenu open ember_hub_legacy`、`/mvtp ember_event`、`/mv tp`？→ 定 S0-8 / S0-9 是否必要。
   - **结果（D202，2026-10-05 18:14，FreshQ736，非 OP）：** `/trmenu` 与所有 `trmenu open <旧菜单>` 都是「no permission」；`/mvtp ember_event`、`/mv tp ember_event`、`/mvtp world` 回「缺 multiverse.teleport.*」；`/mv list`、`/tp`、`/ni`、`/mm` 也都被拒；`/dp start EmberDaily` 被开本条件拒（S0-1）；`/warp`、`/lp` 不存在；`Multiverse-Portals/portals.yml` 没有任何传送门。→ **S0-8 不需要**；S0-9（世界级兜底）降为可选纵深防御，目前没有已知的普通玩家传送路径。
8. 穿 P1 装备的号能否实际打通一个旧日常（旧怪数值 vs P1 战力）——决定 L1 / L2 的实际风险大小，不影响「入口应关」的结论。
9. P1 主线实例里执行 `/corerpg abyss evacuate`：会不会触发 `dp leave` 之外的效果（例如对无会话结算）？→ 决定 S0-3 白名单里 `abyss evacuate` 的处理。
10. 旧日常通关前后 `/corerpg level`：余烬经验是否 +60、等级是否上涨？→ 定 L6。
11. 手持 P1 装备 `/corerpg socket list`：应提示不可镶嵌（验证「旧宝石关闭」只靠 id 不匹配）。

---

## 附：建模覆盖 grep（`tools/p1sim/`）

- 0 命中（`rg -n -i '<词>' tools/p1sim/*.py`）：`arena`、`vip`、`monthly`、`kill_reward`、`EmberDaily`、`EmberWeekly`、`mail`、`guild`、`ladder`、`pet`、`calamity`、`scrap`、`reforge`、`socket`、`covenant`、`warehouse`、`auction`、`gacha`、`free_tickets`；`p2econ.py` 对上述词同样 0 命中。
- 有命中但只针对 P1：`signin`（`tools/p1sim/signin.py`）、`afk`（`tools/p1sim/afk.py`）。
- `cash.yml` 只读 `stamina.base_max`（`tools/p1sim/p1config.py:17,84,103`）；体力药（`consumable_ember_stamina_30`）未建模。
- 等级只按主线结算经验累计（`tools/p1sim/p1sim.py:124` `level_of`、`:972` `self.xp += b['xp']`），没有旧经验来源 → L6。

## 变更记录

- 2026-10-05：初稿（D197，ARCH §6 N1）。只写文档，不改代码 / 配置 / 菜单 / 模拟器。
- 2026-10-05：D198 / CoreRpg 1.65.35 —— S0-1（旧 `gate_*` / `guildboss_pass` 在 P1 下回 `no`，封 L1 与 L2 的 DP 侧）+ S0-2（`tryEnter` 在 P1 下拒绝非 P1 kind，含 `/corerpg elite`，封 L2 命令侧）已建并上线；OP / 管理员 / 控制台放行，P1 关时不变；冒烟 `docs/tests/smoke-2026-10-05-1.65.35-s0gate.md`。§0 表 L1 / L2 的「P1 是否拦」从此为「是」（OP 除外）。
- 2026-10-05：D199 / CoreRpg 1.65.36 —— S0-3 路由级默认拒绝白名单（`legacy_gate.allow`）已建并上线，随附 S0-6（shop / monthly）与 S0-7（socket）；封 L3 / L4 / L5 / L7 / L8 / L9 / L10 / L11 的玩家入口；控制台 / OP / 管理员放行，P1 关时不变。
- 2026-10-05：D200 / CoreRpg 1.65.37 —— S0-4 旧发奖端纵深防御（旧经验 / 旧击杀币与经验 / mmgive·mmxp / 公共灾厄结算在 P1 下不发）已建并上线；P1 关时不变，`legacy_gate.payout_guard: false` 可整段关。
- 2026-10-05：D201 / CoreRpg 1.65.38 —— S0-5 竞技场对战币每日计币场次上限（`arena.yml match.daily_coin_matches: 5`）+ 认输 / 掉线方不发参与币 + 开打不足 30 秒的认输胜利不发胜利币（`match.forfeit_min_coin_seconds: 30`）；`/corerpg pass` 在 P1 下不再提示 `pass free`。L3 根因关闭（日后重开竞技场也有上限）。
- 2026-10-05：D202（只改文档）—— 待测第 7 条实服确认：普通玩家打不开任何旧 TrMenu 菜单、不能 `mvtp` / `mv tp` / `tp`，旧菜单无命令绑定，P1 菜单的 `menu:` 闭包碰不到旧菜单 → S0-8 关闭（不需要改动），S0-9 降为可选。
