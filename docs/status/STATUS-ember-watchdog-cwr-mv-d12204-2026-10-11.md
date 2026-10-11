# 状态 · D12204：Watchdog 根因 CoreWorldRules orb 扫 + Multiverse persist

**日期：** 2026-10-11 17:14–17:18 CST  
**裁决：** CoreWorldRules 1.2.1 + MV `mvm set` · **PASS**（CoreRpg jar 未改）

## 根因
- Watchdog 栈：`CoreWorldRulesPlugin.java:226` = `sweepExperienceOrbs()`（`runTaskTimer` 每 100 tick 扫**全部已加载世界**的 ExperienceOrb）。
- 叠加 Multiverse 重启后把 `keepSpawnInMemory/autoLoad` 写回 true → nether/end/event/daily_* 常驻 → 实体追踪 + 扫 orb 主线程尖峰。
- D12203 只改磁盘 YAML、未 `mvm set` 同步内存，故下次 save/restart 被覆盖。

## 交付
1. **live+disk config：** `experience_orbs.sweep_interval_ticks: 0`（NMS+EntitySpawnEvent 已挡 orb）；`/cwr reload` → `sweepInterval=0`。
2. **代码 1.2.1：** 扫 orb 改为每 tick 只扫一个世界（round-robin）；默认间隔 1200。jar 已部署，**下次 play 重启**才加载新 class（当前进程仍 1.2.0 class + 已 reload 的 interval=0）。
3. **MV live：** `mvm set memory/autoload` — 仅 hub/afk true；其余 false；unload nether/end/event/daily_*。`worlds.yml` 已与 live 一致（persist）。
4. **live：** 已加载图 `doFireTick false`；timings off；spark profiler 未跑。

## 禁
CoreRpg jar · tip-twin · Six-slot · pet/gacha 菜单
