# 状态 · D12197：TrMenu lore 压缩 + pid/waiter ops

**日期：** 2026-10-11 14:44–14:47 CST  
**裁决：** menus+scripts · **PASS**（未改 CoreRpg jar）

## 交付
1. **ops**：根 `server.pid` → 符号链接 `server-runtime/server.pid`（原 857959 陈旧）；live play **108685** / login **108630** 与 runtime pidfile 一致。
2. **waiter**：`server-runtime/_restart_waiter.sh` + `login-runtime/_restart_waiter.sh` — 端口空闲 ≥10s 则强制清 pid 再起（不信任永久 `kill -0`）。
3. **TrMenu lore ≤3**（Karen）：`ember_skill_kit.yml` · `ember_help.yml` · `ember_p1_short.yml`（砍十九本墙，日线看各图按钮；PAPI/体力30/条件/动作保留）。
4. `trmenu reload` → **77** 菜单 · 无解析错误。

## 禁
tip-twin densify · Six-slot · Talent · p1sim · 改 jar / config 密码
