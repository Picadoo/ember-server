# 状态 · D262：周本「次数说明」票制假文案薄修

**日期：** 2026-10-07（上海时间）  
**上游：** D261 S0-10 实服 PASS（`1e71b883`）· CoreRpg **1.65.73** 在线  
**范围：** 仅 `plugins/TrMenu/menus/ember_weekly.yml`「次数说明」R 格玩家可见 lore/tell

## 债证（修前）

| 位置 | 原文 | 为何是玩家债 |
|---|---|---|
| L112 | `商城可再补 §f1 §7张 · 主线第 2 章送 1 张` | 「张」= 票观感；商城实售「周体力包 · +45」体力（`CashService` 购买成功句「已购买周体力 +…」）；主线送的是体力药 NI，不是周本免费「张」 |
| L110 / L120 | 写死 `45` | 同页进本格已用 `%corerpg_stamina_cost_weekly%`，R 格不一致 |

## 改动

- L110：其后扣 → `%corerpg_stamina_cost_weekly%` 体力  
- L112：→ `体力不够可买商城「周体力包」· 主线送体力药`  
- L120 tell：消耗 → `%corerpg_stamina_cost_weekly%` 体力  

零改数值 / 逻辑 / 其它菜单。未开六格、未做票头注释填充。

## 验收

- rg：`ember_weekly.yml` 无「补.*张」「送 1 张」  
- 热更：`trmenu reload`（未重启 play）

## 下一建议（非填充）

旁扫未并入本窗：NI `ember-dungeon-tickets.yml` 显示名仍「余烬日票」（迁移兑换物，改名需设计）；hub 主线格「每次 30 体力」写死（可用 cost 占位符，非假票）。
