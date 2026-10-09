# 状态 · D400 抽测：短征 sx05 裂谷风廊

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**（旁注：live 第4次帽后进本 SKIP · 首轮 papi 竞态已补测 · 复测窗并行 d401 jar）  
**证据：** `/workspace/tmp/d400-sx05-spot/` · summary `99-summary.json`  
**对照：** D397 spot @`e3f8c948` · D391 FAIL 退票根因（rooms×3+boss）· DESIGN `DESIGN-ember-short-dungeon-sx05-2026-10-10.md`

## tip / jar

| 项 | 值 |
|----|----|
| 键 tip | `c89c0f41` |
| MM tip | `350f3666` |
| 菜单骨架 | `a01fad52` |
| day_line 挂 | `64aa7828` |
| jar（全链路窗） | `1.65.110-d400.local`（sha256 `0aae1701fcbb08bf772bb7408aa76646002d156cda4cc98f8f3f42bf764ca2f9`） |
| bv（全链路窗） | **68** |
| runId | `sx05-mv1jd2rx-8xh` |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 五本菜单 + K/I day_line | **PASS** | `ember_p1_short`：烬门/锈灯/霜雾/烬井/**裂谷** · `enter sx05` · K/I 挂 `%corerpg_p1_sx05_day_line%`（`64aa7828`）· **无**假写个人 n/3 · I 合计 `sx_day_left_sum` |
| 2 | Q01 门闩 | **PASS** | `D400SxNo` →「未解锁短征（需本人首通 Q01）」 |
| 2b | `p1 enter sx05` | **PASS** | 预留体力 30 · 今日有奖 0/3 · bound · Director 含 rooms×3+boss |
| 3 | 体力净扣 30 | **PASS** | 90→60 |
| 3b | Director FIGHTING + 三房 | **PASS** | r1 崖口台 / r2 风廊窄桥 / r3 对岸甬道 started+cleared |
| 4 | WindCast | **PASS** | MM「【裂谷】风剪条——退进侧龛或离开条带！」+ CoreRpg 蓄力「风剪条」「侧风斩」 |
| 5 | S44 结算 | **PASS** | 有奖 **币80+碎4+骨3**；首通另加 **币140+碎6+胚1**；到账币 **220** / 仓碎 **+10** / 骨 **+3** / 胚 **+1**；`short settle … reward day=1/3` |
| 6 | 日帽独立 | **PASS** | sx05 settle day=1/3；hub PAPI `今日有奖 1/3` · day=1 · left=2；sx01/sx04 仍「今日有奖 **0/3**」；sum=14；随后进 sx01 仍独立 0/3；claim=`p1_sx05_day` |
| 6b | 第4次无结算仍可进 | **SKIP（旁注）** | 本窗仅 1 次通关未凑满 3；`daily_reward_cap=3` + claim 独立 + 有奖/无奖结算钩同构 sx01–sx04 |
| 7 | gate_daily 仍拒 | **PASS** | `%corerpg_gate_daily%` → `no`（首扫曾因进服竞态 Failed to find player 假红；hub 补测 `gate-papi-recheck`） |
| 7b | 旧 EmberDaily | **PASS（纪律）** | 未走 EmberDaily 路径 |
| 8 | afk / 三开关 | **PASS** | six_slot enabled/migrate/set_bonus 仍 true；`daily_kills=2400` 未拧；全链路窗 bv68；**≠关观察** |

## 旁注（非硬红）

| 项 | 说明 |
|----|------|
| live 第4次 | 未连打 3 次凑满；配置帽=3 + 结算 `reward day=` + 无奖消息路径同前四本；建议有红再升满帽复测 |
| 地图 | `map_version ember_short_sx05@d400-pending`；坐标沿用白盒骨架（非完整裂谷风廊 WE）— **本抽测不卡** |
| 并行 d401 | 全链路抽完后 play 并行装上 `1.65.111-d401.local`（bv69）；papi 补测在该窗仍绿；三开关/afk 未拧 |
| 首轮 papi | enter 前 `papi parse` 竞态 Failed-to-find-player；settle 后补测 GATE=no · sx05=1/3 · sum=14 |

## 纪律核对

- Stage2 三开关仍 true · ×0.97 未改 · ≠关观察  
- gate_daily 仍拒 · 未假开旧七线  
- afk.tiers / daily_kills 未抬  
- login / proxy / MariaDB 未碰  

## 证据文件

`00-switches-pre.json` · `10-q01-gate.json` · `20-gate-daily-recheck2.json` · `30-menu-short.json` · `30-menu-short-post-dayline.json` · `31-daycap-papi-*.json` · `40-before.json` · `41-enter.json` · `51-r1.json`–`53-r3.json` · `50-fight.json` · `60-after.json` · `90-endstate.json` · `99-summary.json` · `run.log` · `gate-recheck.log`

---

*D400 spot · sx05 裂谷风廊 · PASS @ tip push · ≠关观察*
