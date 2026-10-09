# 状态 · D395+D396 薄抽：短征日帽同屏 / 假平面清零

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**  
**证据：** `/workspace/tmp/d395-d396-short-daycap/` · `99-results.json` · `99-results-retest.json` · `50-menu-unlocked.json`  
**上游施工：** [`STATUS-ember-short-daycap-papi-d395-2026-10-10.md`](STATUS-ember-short-daycap-papi-d395-2026-10-10.md) · [`STATUS-ember-short-menu-daycap-d396-2026-10-10.md`](STATUS-ember-short-menu-daycap-d396-2026-10-10.md) · DESIGN [`DESIGN-ember-short-menu-polish-2026-10-10.md`](../design/DESIGN-ember-short-menu-polish-2026-10-10.md)

## tip / jar

| 项 | 值 |
|----|----|
| 键 tip（D395） | `ee765f56` |
| 菜单 tip（D396） | `0c9770a9` |
| jar | `1.65.108-d395.local`（play Enabling 已确认；md5 `556352be80f75b61d459a66f5b03c632`） |
| bv | **66** |
| trmenu | `ember_p1_short` 在载（71 菜单） |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | A/C/E/I 见 `day_line` | **PASS** | 静态挂 `%corerpg_p1_sx0N_day_line%`（A/C/E 主+灰×2；I 三行+`sx_day_left_sum`）；活测解锁态 lore「今日有奖 0/3」；I 合计 9；unreplaced=0 |
| 2 | 无草案假字 | **PASS** | 菜单无「草案/另号/币70/碎片5」；D393 旁注「菜单 lore 草案」**已消** |
| 3 | 金额实发 | **PASS** | A/C/E 通关 **币80+碎片4+骨尘3**（S40/S41/S42）；首通 200/8/1 · 180/6/1 · 160/8/1；I 通关有奖 80/4/3；eco 钉一致 |
| 4 | PAPI 真值 | **PASS** | console/`papi parse me`：三本日帽 `今日有奖 0/3`；day=0；left=3；left_sum=9 |
| 5 | afk / 三开关 | **PASS** | six_slot enabled/migrate/set_bonus 仍 true；`daily_kills=2400` 未拧；本号未改配置 |

## 旁注

| 项 | 说明 |
|----|------|
| 灰态金额 | 未通 Q01 时 A/C/E 灰态 lore **不**列 S40–S42 金额行（仅 day_line）；金额在解锁主 lore + I 说明。以解锁态验收金额。 |
| 首扫 bot `/papi parse me` | 无权限假红；改 console `papi parse <player>` + 短 op 后同会话绿（纪律同 D387/D388）。 |

## 不动（本号抽测）

关观察 · 切分支 · login/proxy/MariaDB · 改 live 开关 / afk / 日表 / S40–S42 · 改配置（只读扫）

## 人话

打开短征选页，烬门/锈灯/霜雾悬停都能看见「今日有奖 n/3」，说明格还有三本合计；奖励预告已是施工钉 80/4/3，旧草案 70/5/3 没了。插件日帽键和菜单挂键都对上了；观察三开关没动。

---

*D395+D396 spot PASS · tip ee765f56 / 0c9770a9 · jar 1.65.108-d395.local · ≠关观察。*
