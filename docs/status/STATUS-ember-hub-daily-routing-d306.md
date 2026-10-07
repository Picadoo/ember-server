# 状态 · D306：枢纽「今天该打哪」日路由诚实方案 M（W1a+W1b+W1c+W1d）

**日期：** 2026-10-08（上海时间）  
**上游：** 总控批 A · [`DESIGN-ember-hub-daily-routing-2026-10-08.md`](../design/DESIGN-ember-hub-daily-routing-2026-10-08.md) 方案 M（`d5844f77`）  
**版本：** CoreRpg **1.65.96** · `balance_version` **60**（未抬）

## 人话

Q07 后入口变多，枢纽仍靠一行 `p1_next`。本窗**零经济 / 零新模式 / 不抬体力掉落**：决策短签 + 四态同屏 + 钉 D285/D305/灰锁——**不跑 p1sim**。

## 改了什么

| 窗 | 改动 |
|---|---|
| **W1a** | `%corerpg_p1_route_pri%` / `route_sec`（`EmberHubRoute` 口径表）+ 枢纽 Open tell「今天：」+ 新增「今天该打哪」决策卡 `H` |
| **W1b** | 决策卡同屏四态：体力（含 主线30/深渊30/团本50）· 精选 · 花样 · 挂机状态/今日 |
| **W1c** | 满额主推「去冒险」；体力&lt;30 推挂机/补给；Q07 前不推深渊/团本；不重开 D305 层名片 |
| **W1d** | 冒险页「本周短目标」互指枢纽「今天该打哪」 |
| jar | `EmberHubRoute` + `EmberRunPapi` route_* / featured_left + `EmberRunService.featuredLeft`；**1.65.95 → 1.65.96** |
| 单测 | `EmberHubRouteTest` · `EmberRunPapiTest` ENTRY 含 route_* |

## 不动

- 体力日回 / `base_max` 90 / 单局 30·50 · 掉落 / event_rate / ALTS  
- Pack6 / 新模式图包 / 假推荐  
- 天赋 / 灰印 HOLD / 守招 / 工坊 R / 事件 R/W / 调律 R  
- D305 层名片主交付 · D285 满额→冒险路径 · Q07 灰锁  
- 方案 R（高亮/菜单序）**后置**  
- bv **60** · login/proxy 未停（仅 play 换 jar）

## 验收

- 静态：口径表与 `cash.yml` costs（30/30/50）一致；单测 PASS；禁项未动  
- 冒烟（live · **不跑 p1sim** · `mineflayer-tests/d306-hub-daily-routing-smoke.js`）：
  - 枢纽见「今天该打哪」+ 优先句 + 四态 + 消耗白话 PASS
  - Q07 前不推荐深渊/团本为优先 PASS
  - 体力&lt;30 见挂机/补给优先 PASS
  - 冒险短目标互指 PASS；枢纽 D305「四层各有名片」仍在
- play Enabling **1.65.96**；`version CoreRpg` = 1.65.96；TrMenu reload  
- 回滚：`/workspace/backup/CoreRpg-1.65.95-pre-d306.jar` + 还原 hub/adventure 菜单

## 下一窗

- **R**（高亮/菜单序）**后置**；仅总控另批 M+R 时开  
- **禁** Pack6 / 抬体力 / 假推荐 / 重开 D305  

