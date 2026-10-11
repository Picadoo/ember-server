# 状态 · D12196：Watchdog 14:07 拉起 + 崩溃重启加固

**日期：** 2026-10-11 14:10–14:15 CST  
**裁决：** ops/scripts · **PASS**（未改 CoreRpg jar）

## 现场
- 14:07:42 Paper Watchdog 杀 play；日志写 `Attempting to restart with ./start.sh`（**当时 JVM 仍持 13:42 启动时读入的旧 spigot.yml**：`restart-script: ./start.sh`；D12195 13:46 已改文件但未重启，故包装未生效）。
- login 同死；仅 waterfall :25565 存活；`server.pid` 残 83348/83796。
- `/tmp/ember-play-restart.log` 无 14:07 条目。

## 本窗交付
1. **人工拉起** play PID 见 `server-runtime/server.pid` · login 见 `login-runtime/server.pid` · JDK8 · MySQL CoreRpg+CoreGacha · SEVERE 0 · CoreRpg **1.65.723-d12194.local** · ops play=`[]`（login Picado OP 保留 playit）。
2. **D12195 加固**：`restart-on-crash.sh` → `setsid nohup ./_restart_waiter.sh`（外层秒退；内层独立会话等 pid/端口空闲 ≤120s 再 `start.sh auto`），play+login 各一份。避免 JVM 进程组带走 waiter。
3. TrMenu lore 先前两提交已 push（`d6c084db` hub+p1 · `1735415a` sig）；本窗 `trmenu reload` 77 菜单。

## 验证
- 外层 `restart-on-crash.sh` return 0 / ~0s；内层能写 `/tmp/ember-play-restart.log`。
- 下次 Watchdog 应走 `_restart_waiter`（当前 live 已读新 spigot.yml）。

## 禁
tip-twin densify · Six-slot · Talent without p1sim · 改 config 密码 · 长 bot 压测
