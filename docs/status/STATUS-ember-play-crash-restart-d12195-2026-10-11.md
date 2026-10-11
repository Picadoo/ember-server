# 状态 · D12195：Play Watchdog 崩溃自动重启包装

**日期：** 2026-10-11  
**裁决：** scripts/docs · **PASS**（未改 jar）  
**根因：** `spigot.yml` `restart-script: ./start.sh` 在 Watchdog 关停时仍见旧 `server.pid` → start.sh「Server already running」。  
**交付：**
- `server-runtime/restart-on-crash.sh`（+x）：立即返回；后台等 pid/25567 空闲 ≤120s → `rm -f server.pid` → `./start.sh auto`；日志 `/tmp/ember-play-restart.log`
- `login-runtime/restart-on-crash.sh`：同逻辑，端口 25566；日志 `/tmp/ember-login-restart.log`
- `spigot.yml` `restart-script: ./restart-on-crash.sh`（play + login；`restart-on-crash: true` 不变）

**验证：** 外层脚本 return 0 / 0s；live play PID 未因本改动重启（下次崩溃/开机生效）。  
**禁：** tip-twin densify · Six-slot · Talent · p1sim · 改 config 密码

**后续（D12196）：** 14:07 Watchdog 时 JVM 仍持启动时旧 `restart-script: ./start.sh`（D12195 改文件后未重启）。已人工拉起并加固为 `setsid nohup ./_restart_waiter.sh`。
