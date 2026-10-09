# 状态 · D397 抽测：短征 sx04 烬井螺旋

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**（旁注：live 第4次帽后进本 SKIP · 配置+结算钩同构）  
**证据：** `/workspace/tmp/d397-sx04-spot/` · summary `99-summary.json`  
**对照：** D393 spot @`73bb9b67` · D391 FAIL 退票根因（rooms×3+boss）· DESIGN `DESIGN-ember-short-dungeon-sx04-2026-10-10.md`

## tip / jar

| 项 | 值 |
|----|----|
| 键 tip | `8bd534fe`（enter 帮助修 `877ce805`） |
| MM tip | `a2b3fe8b` |
| 菜单骨架 | `52eeaeb7` |
| day_line 挂 | `cde619e2` |
| jar | `1.65.109-d397.local`（play Enabling 已确认；sha256 `c8798a9208ade5f8845845dbf3dbc3848e0e46b881839fb2eaeea882273d4bcc`） |
| bv | **67** |
| runId | `sx04-mv1itw5o-1rq` |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 四本菜单 + G/I day_line | **PASS** | `ember_p1_short`：烬门/锈灯/霜雾/烬井 · `enter sx04` · G/I 挂 `%corerpg_p1_sx04_day_line%`（`cde619e2`）· **无**假写个人 n/3 · I 合计 `sx_day_left_sum` |
| 2 | Q01 门闩 | **PASS** | `D397SxNo` →「未解锁短征（需本人首通 Q01）」 |
| 2b | `p1 enter sx04` | **PASS** | 预留体力 30 · 今日有奖 0/3 · bound · Director 含 rooms×3+boss |
| 3 | 体力净扣 30 | **PASS** | 90→60 |
| 3b | Director FIGHTING + 三房 | **PASS** | r1 井口台 / r2 螺旋下井 / r3 井底甬道 started+cleared |
| 4 | FurnaceCast | **PASS** | MM「【烬井】井心喷焰——踏上井沿或拉开！」+ CoreRpg 蓄力「井心喷焰」「灰柱」 |
| 5 | S43 结算 | **PASS** | 有奖 **币80+碎4+骨3**；首通另加 **币150+碎6+胚1**；到账币 **230** / 仓碎 **+10** / 骨 **+3** / 胚 **+1**；`short settle … reward day=1/3` |
| 6 | 日帽独立 | **PASS** | sx04 settle day=1/3；hub PAPI `今日有奖 1/3` · day=1 · left=2；随后进 sx01 仍「今日有奖 **0/3**」；claim=`p1_sx04_day`；sum=11（含第四本） |
| 6b | 第4次无结算仍可进 | **SKIP（旁注）** | 本窗仅 1 次通关未凑满 3；`daily_reward_cap=3` + claim 独立 + 有奖/无奖结算钩同构 sx01–sx03 |
| 7 | gate_daily 仍拒 | **PASS** | `%corerpg_gate_daily%` → `no`（首扫曾因进服竞态 Failed to find player 假红；hub 补测） |
| 7b | 旧 EmberDaily | **PASS（纪律）** | 未走 EmberDaily 路径 |
| 8 | afk / 三开关 | **PASS** | six_slot enabled/migrate/set_bonus 仍 true；`daily_kills=2400` 未拧；bv67；**≠关观察** |

## 旁注（非硬红）

| 项 | 说明 |
|----|------|
| enter 帮助串 | jar 构建自 `8bd534fe` 时帮助串仍写 `sx01\|sx02`；后续 `877ce805` 已扩 sx01..sx04（文案）；**进本 `enter sx04` 实跑绿** |
| live 第4次 | 未连打 3 次凑满；配置帽=3 + 结算 `reward day=` + 无奖消息路径同前三本；建议有红再升满帽复测 |
| 地图 | `map_version ember_short_sx04@d397-pending`；坐标沿用白盒骨架（非完整烬井螺旋 WE）— **本抽测不卡** |

## 纪律核对

- Stage2 三开关仍 true · ×0.97 未改 · ≠关观察  
- gate_daily 仍拒 · 未假开旧七线  
- afk.tiers / daily_kills 未抬  
- login / proxy / MariaDB 未碰  

## 证据文件

`00-switches-pre.json` · `10-q01-gate.json` · `20-gate-daily-extra.json` · `30-menu-short.json` · `31-daycap-papi-*.json` · `40-before.json` · `41-enter.json` · `51-r1.json`–`53-r3.json` · `50-fight.json` · `60-after.json` · `90-endstate.json` · `99-summary.json` · `run.log` · `gate-recheck.log`
