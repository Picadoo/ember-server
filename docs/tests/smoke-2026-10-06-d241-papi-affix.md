# Smoke · D241 ARCH S3-12 `%ember_daily/weekly_left%` + 词缀原语（CoreRpg 1.65.67）

**日期：** 2026-10-06 Asia/Shanghai · **脚本：** `tools/p1map/d241-papi-affix-smoke.sh`（`MODE=pre|post|affix`；快照 `tools/p1map/papi-snapshot.sh`）· **bots：** FreshQ828、FreshQ829、FreshQ830

jar `50d1051f5428986f…` · play PID 2334508 · MySQL×2 · SEVERE 0 · Exception 0

| 阶段 | 结果 |
|---|---|
| pre（1.65.66，07:35） | **4/0/0**：FreshQ828 注册 → 第 2 会话 PAPI 可解析 + 752 键基线 `pre.tsv`；两个键原样显示 `%ember_daily_left%` / `%ember_weekly_left%`（体力 90、单次 30、周免 1） |
| post（1.65.67，07:37–07:50） | **47/6/15** |
| affix 补跑（07:54–08:04） | **21/0/1** |

1. 启动：`Enabling CoreRpg v1.65.67`、CoreRpg + CoreGacha MySQL、E1 SoT bv58、无 FAIL-CLOSED、`ember` 扩展注册。
2. **FreshQ828 跨 jar**：新会话快照与 `pre.tsv` 相比**只有这两个键变了**（752 键，剔除按分钟跳动的键同 D240）。`daily_left` 3 = 90/30，`weekly_left` 1 = `corerpg_stamina_credit_weekly`；`stamina set 59` → 1，`29` → 0，之后设回 200。
3. **FreshQ829 新号第 2 会话**：`daily_left` **3**、`weekly_left` **1**；第 2 ↔ 第 3 会话 752 键逐字相同；仍返回原文的键只剩 `corerpg_nosuch` / `ember_nosuch`（边界探针）。
4. **FreshQ830 Q01 回归**：进本 + 三房 + 结算（首通），无发放拒绝。
5. **12 个词缀**（`runs firstclear` 后在 Q01 重复本 r1 强制，survival 停留 10 次治疗再清房）：post 阶段 split / regen / frost / molten / jailer / firechain 通过；blazing / shield / charge / mortar / venom / arcane 6 个 **FAIL「promoted」**——日志显示上一局结算后约 1 s 就发 `enter`，bot 还在正在清理的副本里，`enter` 无回应，强制词缀被下一条覆盖，所以这 6 局根本没开。属脚本时序，不是插件行为。脚本 `q01()` 改为 enter 最多重试 4 次（间隔 10 s），`MODE=affix` 补跑这 6 个（11 次 enter，其中 5 次被忽略后重试成功）：全部 promoted + defeated + settled，`venom + hit=` 可见。
6. live 日志合计：12 个词缀各 1 次 `affix X on` + `affix X done`；`firechain link` ×2、`jailer rooted=` ×1、`molten scheduled` ×1、`venom + hit=` ×1。
7. SOFT（16）：post 阶段 6 个失败局各带 2 条连带 SOFT（done / settle 未见）+ venom / arcane 施放未见（这两局没开）+ regen 聊天未见；补跑阶段 arcane 施放未见（精英在 warn + spin 结束前就被清掉）。施放细节由单测里的回放覆盖（arcane 247 次施放 / 105 次命中）。
