# TIP · 挂机庭 purgeLegacy Watchdog（2026-10-13）

## 根因
`EmberAfkService.purgeLegacy` 在主线程对 `w.getLivingEntities()` **一次扫全图生物**，挂机庭实体多时卡主线程 → 玩法服 Watchdog（约 2026-10-13）。

## 改法
- **禁** `getLivingEntities()` 全图扫（Watchdog）。
- 只扫 **活跃 Fight 玩家** 附近 `getNearbyEntities(r=48)`；无 Fight 直接 return。
- 一轮只清一名 Fight 玩家（`purgeCursor` round-robin）；每轮移除 **≤32**。
- 仍跳过 `mobs` 自家怪；仅 `Monster|Slime` 非自家 `remove()`。

## jar / bv
- 版本：`1.65.804-d13303.local`（同 jar 含 TIP-BX YieldBoost 菜单 peek）
- **bv 未动 = 123**（ECONOMY_BV / balance_version）
- 禁项未动：Stage2 三开关、×0.97、`afk.tiers` rates/caps、Pack6 / talent / ash imprint / R / K3 / sx20 / L13

## 装服
`CoreRpg.jar` → play（server-runtime plugins）；停 play → 拷 jar → 启 play。
