# 状态 · D307：工坊菜单诚实方案 M（W1a+W1b+W1c+W1d）

**日期：** 2026-10-08（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-workshop-menu-honesty-2026-10-08.md`](../design/DESIGN-ember-workshop-menu-honesty-2026-10-08.md) 方案 M（`dcecdbf5`）  
**版本：** CoreRpg **1.65.97** · `balance_version` **60**（未抬）

## 人话

工坊确认键还写「消耗以聊天为准」。本窗**零改价 ≠ forge R**：预览+确认同屏嵌本次费用与缺料半行；互换写免费；分解仍聊天一键——**不跑 p1sim**。

## 改了什么

| 窗 | 改动 |
|---|---|
| **W1a** | `ember_p1_forge` 强化/升阶/精工/成色预览+确认嵌 `%held_enhance_cost%` / `held_upgrade_cost` / `held_next_c/q`；互换 `%held_swap_cost%`=免费；去掉「消耗以聊天为准」作唯一句；分解预览补 `%held_dismantle_yield%` |
| **W1b** | `%held_*_lack%` 缺料半行（口径对齐 `ForgeService.lacking`）；材料够为空 |
| **W1c** | 保留 `held_next_*` / `blade_next_*`；费用行 == `costShort`/`UpgradeRules` 同源 |
| **W1d** | `ember_enhance` Open tell + 规则卡旁注「P1 请走主菜单工坊」 |
| jar | `EmberGearNextHint` enhance/upgrade/swap/dismantle/lack + `costShort` 含碎片/核心；`EmberRunPapi` held_*_cost/lack；**1.65.96 → 1.65.97** |
| 单测 | `EmberGearNextHintTest`（9）· `EmberRunPapiTest` LOADOUT 含 held_* |

## 不动

- `refineCost` / `qualityCost` / `enhanceCost` / `upgradeCost` 数值  
- Pack6 / 新材料轨 / 抬体力掉落 / 永久乘  
- 天赋 / 灰印 HOLD / 守招 / 事件 R/W / 调律 R / **工坊 R**  
- D299 近档主语义 · D306 日路由 · 分解聊天 token（B2.137）  
- 方案 R（确认灰锁）**后置**  
- bv **60** · login/proxy 未停（仅 play 换 jar）

## 验收

- 静态：菜单费用行与 `Cost.label()`/`costShort`/enhance 表一致；单测 PASS；禁项未动  
- 冒烟（live · **不跑 p1sim** · `mineflayer-tests/d307-workshop-menu-honesty-smoke.js`）：
  - 预览+确认见「本次：」费用 / 无「消耗以聊天为准」PASS
  - 互换「免费 · 交换强化…」PASS；缺料半行 / 有材料费用仍在 PASS
  - 升阶见 T1→T2 或需首通 Q04 PASS；分解仍聊天诚实 PASS
  - 旧 `ember_enhance` P1→工坊旁注 PASS
- play Enabling **1.65.97**；`version CoreRpg` = 1.65.97；TrMenu reload 69 菜单  
- 回滚：`/workspace/backup/CoreRpg-1.65.96-pre-d307.jar` + 还原 `ember_p1_forge.yml` / `ember_enhance.yml`

## 下一窗

总控另选题；forge R / 灰印 HOLD / 天赋 HOLD 仍等人感门禁。
