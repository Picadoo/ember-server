# STATUS · Spark 性能分析

**日期：** 2026-09-13  
**插件：** `plugins/spark.jar` = spark **1.10.185**（Bukkit）  
**启服：** Loading/Enabling OK · PlaceholderAPI `spark` 扩展已注册  

## 常用命令（需 OP）

| 命令 | 用途 |
|------|------|
| `/spark tps` | TPS / MSPT |
| `/spark health` | 内存与健康 |
| `/spark profiler start` | 开始 CPU 采样 |
| `/spark profiler start --timeout 60` | 采 60s 自动停 |
| `/spark profiler stop` | 停并出报告链接 |
| `/spark tickmonitor` | 单 tick 尖刺 |

报告打开 lucko viewer（聊天里会给 URL）。

## 并发压测建议（后续）

1. `/spark profiler start --timeout 120`  
2. 多 bot 并行：进服、日本、挂机刷怪、技能、菜单  
3. `/spark profiler stop` 看热点：`MythicMobs` / `DungeonPlus` / `CoreRpg` / 区块  

脚本入口可放 `mineflayer-tests/load-bots.js`（下期写）。
