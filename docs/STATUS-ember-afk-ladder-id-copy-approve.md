# STATUS · B2.9 挂机庭 / 天梯去内部代号批准 A

**日期：** 2026-09-28 23:22 CST（Asia/Shanghai）  
**依据：** `docs/design-ember-afk-ladder-id-copy.md` · parent design tip `a8b8ef2`  
**Verdict：** **APPROVED A · 待插件**

## 结论

批准方案 **A**，只施工玩家可见展示文案：HD `database.yml` 的 `ember_afk_hub` 与 `ember_ladder_power/abyss/speed` 说明行，以及 TrMenu `ember_ladder.yml` 的 Open / lore / tell。当前批准记录**未修改任何 HD/TrMenu YAML**。

**不包含：** calamity OP #6；`EmberCalamity/option.yml` 不动；`ember_weekly.yml` EmberWeekly tells 不动。

## A 范围与替换

| 区域 | 旧 | 新 |
|---|---|---|
| HD `ember_afk_hub` | `&6挂机庭 &7\| Ember AFK` | `&6挂机庭` |
| HD `ember_afk_hub` | `&a碎片·骨尘 &8MM→NI` | `&a掉落：碎片 · 骨尘` |
| HD `ember_afk_hub` | `&7EmberAfkZombie / EmberAfkSkeleton` | `&7打僵尸拿碎片 · 打骷髅拿骨尘` |
| HD `ember_ladder_power` 副题 | `&7展示分 power_score · 不乘伤害` | `&7展示分 · 不乘伤害` |
| HD `ember_ladder_power` 底注 | `&8周结算：外观称号 only` | `&8周结算：外观称号` |
| HD `ember_ladder_abyss` 底注 | `&8对齐 EmberAbyss · 周结算外观` | `&8对齐深渊层数 · 周结算外观` |
| HD `ember_ladder_speed` 副题 | `&7EmberWeekly 通关用时` | `&7周本通关用时` |

TrMenu 只改 `ember_ladder.yml` 玩家可见 Open / 说明按钮 / 三个榜单 lore-tell：去字面 board id、`EmberAbyss`、`EmberWeekly`、`power_score` 与 `only`，改为「挂机庭三块全息」「对齐深渊层数」「周本通关秒数」「按展示分排名」等人话；所有 `%ember_*%` PAPI 占位符原样保留。

## 硬约束

- 零改全息 location、HD board key、排行榜人名/分值行。
- 零改 `over_chance*`、`afk_caps`、MM id、CoreRpg 数值、天梯结算/奖励、其它菜单。
- 零改 `EmberCalamity/option.yml`（OP #6）及 `ember_weekly.yml` EmberWeekly tells。
- 插件岗施工后热更 HD / TrMenu；再由测试岗做静态检索与挂机落地、天梯菜单冒烟。

## 交接

- **插件：** 按批准 A 施工；完整替换清单见批准消息，不要触碰 YAML 之外范围。
- **策划：** 已批 A，等待插件施工；本记录仅为批准，不宣称已落地或已验收。
