# 状态 · D12203：Watchdog 世界 tick 减压（keepSpawn/autoLoad + doFireTick）

**日期：** 2026-10-11 16:48–16:52 CST  
**裁决：** Multiverse + live console · **PASS**（未改 CoreRpg jar）

## 现场
- 16:48:26 Paper Watchdog：`WorldServer.a` / `doTick`（非 spark；D12201 已关 backgroundProfiler，本轮 boot 后 profiler 未自动跑）。
- 崩溃后 `restart-on-crash` 已拉起 · MySQL×2 · SEVERE 0 · TPS ~20。

## 交付
1. **live：** `doFireTick false` 全世界（含 world/nether/end/event/daily）。
2. **live：** unload `world_nether` / `world_the_end` / `ember_event` / `ember_daily_ash` / `ember_daily_crypt`（`world` 为默认世界不可卸）。
3. **disk：** `plugins/Multiverse-Core/worlds.yml` — 仅 `ember_hub`/`ember_afk` 保持 `keepSpawnInMemory`+`autoLoad` true；其余 `keepSpawnInMemory false` + `autoLoad false`（日常图由 CoreRpg 按需 load/unload）。
4. **live：** `timings off`；spark profiler 未在跑。

## 禁
改 jar · tip-twin · Six-slot/stage1-wt · pet/gacha · 关 hub/afk 常驻
