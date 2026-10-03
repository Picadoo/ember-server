# 设计缺口落地（2026-09-26 22:50–23:05 CST）

测试：`mineflayer-tests/design-gaps-smoke.js`。新号、非 OP，经代理登录；RpgBot 只负责发物品和执行管理命令。最近一次 Gap56260 全部 PASS。

| 项 | 决定 / 规格依据 | 落地 | 验证 |
|----|----------------|------|------|
| T1–T3 装备附魔 | 同样一件一次；按阶加收附魔晶 | CoreEnchant 1.1.0：表按 NI id 匹配（`ni_ids`），每张表有自己的 `base_enchants`（T3 自带 耐久 II / 刃 锋利 I），以及 `extra_crystals`（T1 +1、T2 +2、T3 +3）。各档合计晶数：T0 1/2/3，T1 2/3/4，T2 3/4/5，T3 4/5/6。等级花费各阶都是 1/2/3。新增 `TierCrystalListener`，晶数不够就取消并提示。原版客户端仍按 1/2/3 晶点亮按钮，以服务端为准。 | T3 刃放 3 晶：拒绝，提示「需要 4 颗」，重登后晶仍是 3、无新附魔 ✅；放 6 晶：锋利 I→II，晶 6→2，等级 −1 ✅ |
| 第二经验来源 | 精英 +1、首领 +2、每日上限 | CoreRpg 1.5.0 新增 `corerpg xpreward <玩家> elite\|boss`（仅管理员/控制台），配置在 `progress.yml`，每日上限 6 级。9 个 MM 怪的 onDeath 用控制台执行：精英 = Crypt/Daily/Abyss 蛮兵、WeeklyBruteA/B、AbyssWatcher、RaidElite；首领 = RaidBoss、CalamityBoss。挂机怪不给。 | 机器人亲手击杀 EmberDailyBrute → 「击杀精英 · 经验等级 +1」✅；再连发 4 次 boss → 当日累计正好 6 级 ✅ |
| 勋阶日礼 | 设计没有层级数值 → 勋阶 0 只给象征性的 20 | `cash.yml vip.tier0_daily_claim_coin: 20`；勋阶 ≥1 仍是 120。菜单文案同步。目前没有任何途径能升勋阶，所以实际上所有人都拿 20。 | vip claim → +20 ✅ |
| 新号天赋点 | 规格明确：`points_per_level: 1`，`level_points_from: 2`，新号 ember_level ≈10（DESIGN-ember-covenant-talent §2 / growth-curve） | **未改**：现在的 9 点符合规格公式。另外发现 emberLevel 目前没有升级途径（一直是 10），这是另一个缺口。 | — |
| 商城占位货架 | 下架，不编价格 | `ember_shop.yml` 移除首充、加速券、仓库扩容、外观·刃焰红、周礼包、节日礼包，只留月卡、战令解锁、日票、周票。 | 商城只有真实 SKU ✅ |
| 战令赛季经验 | 规格给了来源（§5.3：挂机 / 日本 / 周本，有日上限），没有数值 → 取保守值 | `progress.yml`：签到 +10（内部）、日本通关 +20、周本通关 +40（DP 通关箱用控制台执行 `corerpg passxp`），每日上限 100，100 经验/级，最高 30 级。`/corerpg pass` 显示等级；战令菜单文案已更新。赛季重置和按等级发奖未定义，**未做**。 | 签到 +10 ✅；6 次 daily_clear 后到上限，「今日 100/100」，Lv.1 ✅ |
| 图录 | 跳过 | — | — |

新字段（玩家 yaml / MySQL blob）：`killLevelsDate`、`killLevelsToday`、`passXp`、`passXpDate`、`passXpToday`。

仍未做：
- 战令等级奖励和赛季重置（规格没有）。
- emberLevel 没有升级途径。
- 勋阶没有获取途径。
- 挂机击杀给战令经验（规格提到，但挂机容易刷，这次没接）。

回归（23:10 CST）：`new-player-smoke.js` 经代理跑，9/9 PASS。日本通关后等级 +4（通关箱 3 + 蛮兵精英 1），战令经验 +20。
