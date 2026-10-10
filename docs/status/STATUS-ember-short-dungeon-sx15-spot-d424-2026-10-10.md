# 状态 · D424 抽测：短征 sx15 烬旋坡廊

**日期：** 2026-10-10（上海时间）  
**裁决：** **PASS**（旁注：Boss 窗 Paper Watchdog 一次 · 结算仍落盘 · 重启后软复检全绿 · 地图 WE pending · Cast 秒杀窗 SOFT）  
**证据：** `/workspace/tmp/d424-sx15-spot/` · soft `20-soft-recheck.txt` · settle 日志 `rows+6 reward day=1/3`

## tip / jar

| 项 | 值 |
|----|-----|
| 批 A | `dad2bea4` |
| 键 | `7d2d1c0f` |
| MM | `614f54f1` |
| 菜单 | `efbaa173` |
| jar | **`1.65.126-d424.local`** · bv **79** · sha256 `2ad3dae6…e962cc` |
| runId | `sx15-mv1s9ddi-xqn` |
| bot | FreshQ852（通关）· FreshQ853（Q01 门闩） |

## 分项

| # | 项 | 结果 |
|---|----|------|
| 1 | 十五本菜单 + day_line/fc | **PASS**（N=烬旋） |
| 2 | Q01 门闩 | **PASS**（FreshQ853 未解锁） |
| 2b | enter sx15 | **PASS**（bound EmberSx15） |
| 3 | 三房 | **PASS**（r1–r3） |
| 4 | SpiralCast | **SOFT**（Boss 149.5s 后击杀；技能表已挂） |
| 5 | S54 结算 | **PASS**（`short settle rows+6 reward day=1/3`） |
| 6 | 日帽独立 | **PASS**（软复检 sx15 1/3 · sx01 0/3 · sum=44） |
| 7 | gate_daily | **PASS**（no） |
| 8 | SEVERE / Stage2 | **PASS**（重启后 SEVERE=0 · 三开关仍 true） |

## 旁注

- Boss 窗曾触发 Paper Watchdog → 自动重启；**结算行已在重启前写出**；重启后 live jar 仍 1.65.126-d424 · 软复检全绿。
- 地图 `ember_short_sx15@d424-pending` · 绕心螺旋≥2整圈 WE pending。

## 纪律

≠关观察 · ≠抬 afk · ≠开 R/K3 · ≠假开旧日常

---

*D424 spot · sx15 烬旋坡廊 · PASS · ≠关观察*
