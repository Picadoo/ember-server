# 状态 · D12201：Watchdog spark 背景采样关 + 签到菜单 lore 压缩

**日期：** 2026-10-11 16:13–16:20 CST  
**裁决：** spark config + TrMenu lore + docs · **PASS**（未改 CoreRpg jar）

## 交付
1. **Watchdog：** `plugins/spark/config.json` `backgroundProfiler: false`。16:13 堆栈主因 spark `BukkitWorldInfoProvider.pollCounts`。live 已 `/spark profiler cancel`；spark 无 config reload，**持久生效看下次重启**（本次不主动重启 play）。附：`ember_afk` / `ember_hub` `doFireTick false`（缓解 15:18 BlockFire）。
2. **TrMenu lore ≤3（Karen）：** `ember_p1_sign.yml`；砍空行与规则墙；保留 PAPI / ➥ / 动作；`tools/menus/gen_ember_p1_sign.py` 同步。
3. 校验：long-lore(>3)=0 · 空白 lore=0 · YAML OK。
4. `trmenu reload` → **77** 菜单 · 115 ms · 无解析错误（16:19:05 CST）。live CoreRpg **1.65.723-d12194.local** · SEVERE 0 · jar 未改。

## 禁
tip-twin densify · Six-slot / stage1-wt HOLD · p1sim · cosmetics pet/gacha · 改 jar · hub_legacy 大改
