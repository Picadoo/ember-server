# STATUS · ARCH S3-12（CoreRpg 1.65.67 / D241）

**日期：** 2026-10-06 Asia/Shanghai · **上游：** live 1.65.66 / D240 / bv58（46736db）

一刀两件：A = 玩家可见的小修（`%ember_daily_left%` / `%ember_weekly_left%`），B = ARCH §5 S3 最后一项（词缀行为原语）。

## A · `%ember_daily_left%` / `%ember_weekly_left%`

**查到了什么**

- 两个键**没有出现在任何会渲染的菜单 / 全息 / DP 配置里**。只出现在 `plugins/TrMenu/menus/README-ember.md` 第 3 条（"可用 … 显示剩余次数"）和设计稿 `docs/design/DESIGN-dungeon-daily-weekly.md` §3.4 / §7（"今日剩余 X/3"、"本周剩余 X/1"）。D240 的快照工具把 README 也扫进去了，所以 D240 STATUS 写成了"live 配置引用"——更正：是 README 文档引用，不是玩家能看到的菜单（`docs/reviews/audit-20260926.md` 当时就记过"没有菜单在用"）。
- 设计稿里的"每天 3 次 / 每周 1 次"门票后来被 S0 体力替代（`design-ember-stamina-dnf-daily` §A.1–A.2：上限 90、日常单次 30 ≈ 每天 3 次；周常每周 1 次免费额度）。`PlayerData.dailyEntriesUsed` 已是死字段（只被重置，没人加）。

**怎么映射（不加新机制，只用现有计数器）**

| 键 | 值 | 来源 |
|---|---|---|
| `%ember_daily_left%` | `体力 ÷ 日常单次体力`（整除；不算体力库；单次 ≤ 0 时 "∞"） | `StaminaService.ensure` 后的 `stamina` + `costOf(DAILY)`（= `%corerpg_stamina%` / `%corerpg_stamina_cost_daily%`） |
| `%ember_weekly_left%` | 本周周常免费次数 | `ensure` 后的 `weeklyGrantCreditWeekly`（= `%corerpg_stamina_credit_weekly%`，周一 00:00 重置为 1） |

满体力 90 → **3**（月卡上限 120 → 4）；体力 59 → 1、29 → 0。新号周常 **1**，用掉免费次数后 0，下周回 1。

**代码 / 配置**

- 新增 `EmberPapiCounts`（`KEYS` + `resolve(StaminaService, PlayerData, key)` + `dailyLeftText`）；`EmberLadderExpansion` 在天梯分支前先路由这两个键（玩家为 null → "0"）。
- `ember_daily.yml` T 图标加一行 lore `今日还能进 %ember_daily_left% 次日常（按当前体力）`；`ember_weekly.yml` R 图标加 `本周免费剩余 %ember_weekly_left% 次`；README 第 3 条改写。注意：这两个菜单只能从 `ember_hub_legacy` 进，P1 模式下是管理员入口，普通玩家目前看不到——键现在有值，以后谁引用都不会再显示原文。
- 单测 `EmberPapiCountsTest` ×5，其中一条扫描 `src/main/resources` + `plugins/`（.yml/.md，跳过 MythicMobs SavedData）里所有 `%ember_*%`，要求每个都有提供方——以后再出现"引用了但没人提供"会直接挂测试。

## B · 词缀行为原语

见 `docs/design/DESIGN-ember-affix-primitives-d241.md`。要点：

- `p1.encounter` 新增 `AffixFamily` / `AffixBehavior` / `AffixCycle` / `EmberShape` / `EmberAffixes` + 12 个词缀各一类；`EmberRunDirector` 的 promote / affixTick / 静态助手改走原语（旧助手留一行委托），2448 → 2328 行。`EmberEncounter.SLICE` = "S3-12 affix behaviour primitives"。
- `EmberAffixPrimitivesTest` ×15。
- **伤害轨迹回放**（`EmberAffixReplayTest`）：46736db 旧静态方法逐字拷贝（`LegacyAffixPath`）↔ D241 原语（`PrimitiveAffixPath`），固定种子 200 × 12 词缀，640 tick × 50 ms，第 520 tick 精英死亡 → 18133 行轨迹，**sha256 相同** `de42455402bbce31…`；改 1 ULP 伤害能被抓到（第二个测试）。
- p1sim 本刀**未改**；差异表与"读原语导出表"的方案见设计稿 §3（p1sim 周期词缀比 Java 偏密 = 安全侧，改数字会动 gate，所以排在 S4 之后单独做）。

## 测试

全量 **518/0**（JDK8，class 52；+22：`EmberPapiCountsTest` 5 + `EmberAffixPrimitivesTest` 15 + `EmberAffixReplayTest` 2）。

## 冒烟

见 `docs/tests/smoke-2026-10-06-d241-papi-affix.md`。部署前 4/0/0；部署后 47/6/15 + 词缀补跑 21/0/1。6 个 FAIL 都是脚本时序问题（上一局结算后 bot 还在正在清理的副本里，下一次 `enter` 被忽略，强制词缀被下一条覆盖），不是插件问题；脚本加了 enter 重试后补跑这 6 个词缀全部通过。12 个词缀在 live 上都有 `affix X on` + `affix X done`；MySQL×2；SEVERE 0；Exception 0。

## 不变

`balance_version` **58**；所有词缀数值 / 节奏 / 伤害 / 文案 / 粒子；发放 / 消耗；其余 752 个 PAPI 键（跨 jar 逐字相同）；p1sim；C15；内容包。

## 部署

jar `50d1051f5428986f…`（sha256 `50d1051f5428986f5d0f5820f2d4bf9085c53b91a0861e12ee9f556d3f40bed7`）· play PID 2334508 · 07:36 Asia/Shanghai · 回滚 `/workspace/backup/CoreRpg-1.65.66-pre-1.65.67.jar`。

## 下一刀

**S4**：装备结构合并文档 + `source_map`。S4 之后可单独一刀让 p1sim 读原语导出表（需要重跑 gate）。
