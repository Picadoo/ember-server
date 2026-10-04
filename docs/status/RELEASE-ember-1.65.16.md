# 发布凭证 · CoreRpg 1.65.16（2026-10-04）

P1 每日签到 + 在线时长（D180）。研究：`docs/design/RESEARCH-ember-signin-online-2026-10-04.md` · 设计：`docs/design/DESIGN-ember-signin-online-2026-10-04.md`。

## 线上状态（核对于 2026-10-04 21:36 Asia/Shanghai）

| 项 | 值 |
|---|---|
| CoreRpg 版本 | **1.65.16**（日志 `Enabling CoreRpg v1.65.16`，21:25:43）|
| class major | **52**（JDK 8，tools/jdk8u504-b01）|
| CoreRpg JAR sha256 | `f72fde2fc616077a5158cd487589f26d2e305fd3246fcd90821e368f7384b181` |
| jar 条目数 | 2357 |
| balance_version | **39** |
| 代码提交 | `2bb75d6`（文档 `de049db`）|
| 回滚包 | `/workspace/backup/CoreRpg-1.65.15-pre-1.65.16.jar` |
| MySQL | `[CoreRpg] [storage] MySQL connected` · `[CoreGacha] [db] MySQL connected` |
| SEVERE | 0 |
| 服务器 PID | 1025678（`server-runtime/stop.sh` → `start.sh`，21:25）|
| 启动日志 | `[P1 sign] signin=on special=[7, 14, 21, 28] makeup=3/month needs 60m · online=on idle=5m exclude=[ember_afk] milestones=4` |

## 本版裁决

| 裁决 | 说明 |
|---|---|
| D180 | 月历按本月第 n 次签到发奖（漏签不清零）+ 补签 3 次 / 月（要今天有效在线 60 分钟）；有效在线 15 / 30 / 60 / 120 分钟四档；只发账号计数（币 / 经验 / 锻造印记 / 首领徽记）；挂机庭分钟不算；P1 下旧 `/corerpg sign|activity|bounty` 不再发旧币；修订 D63（印记 / 徽记每月各 ≤2 可来自签到）|

## 验收

| 项 | 结果 |
|---|---|
| 单测 `mvn -o package` | 267 / 0（含 `EmberSignServiceTest` 8 条）|
| p1sim | `tools/p1sim/signin.py`（`tools/p1sim/out-signin-d180.md`）：21 格 21/21（最大 1.6 pp；1600 人复测 2.0 pp）；W30 基线 4.86 → 签到 + 在线上界 4.58（−0.28）→ 再加挂机庭最终版 share 1.0 4.73（−0.13）✔ |
| 冒烟 `tools/p1map/d180-signin-smoke.sh` | FreshQ263 **25 / 0**（FreshQ260 / 261 23 / 2 只是脚本读余额太早：首个会话里 `papi parse` 找不到玩家；已改成重连后读）：进服提示 · 主菜单图标 · 点格签到 · 再点 / 命令 / 重连都不重复（余额 20）· 补签要 60 分钟 · 加 60 分钟后点领 15 / 一键 30 + 60 · 补签补 1 日为第 2 次 · 每天 1 次 · 重连不重领 · 合计 85 = 20 + 45 + 20 · 旧 sign / activity / bounty 不发币 |
| 资产回归 | 无物品资产路径变化（只用现有账本 coin / xp / mark / sigmark 行）→ 未跑 persist-roundtrip |
| config.yml | **未改** |
