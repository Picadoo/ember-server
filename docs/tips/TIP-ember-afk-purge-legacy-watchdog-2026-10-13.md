# TIP · 挂机庭 purgeLegacy Watchdog（2026-10-13）

## 根因
`EmberAfkService.purgeLegacy` 在主线程对 `w.getLivingEntities()` **一次扫全图生物**，挂机庭实体多时卡主线程 → 玩法服 Watchdog（约 19:26）。

## 改法
- **禁** `getLivingEntities()` 全图扫。
- 半径 `r = max(24, leash + spawnRadius + 8)`，对挂机庭**在线玩家附近** `getNearbyEntities` 清理。
- **一轮只清一名玩家**（`purgeCursor` round-robin）；无人则直接 return。
- 仍跳过 `mobs` 自家怪；仅 `Monster|Slime` 非自家 `remove()`。

## jar / bv
- 版本：`1.65.803-afk-purge.local`
- **bv 未动 = 123**（ECONOMY_BV / balance_version）
- 禁项未动：Stage2 三开关、×0.97、`afk.tiers`

## 装服
`CoreRpg.jar` → play（server-runtime plugins）；优先 `corerpg reload`。
