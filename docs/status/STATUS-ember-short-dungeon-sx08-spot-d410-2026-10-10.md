# 状态 · D410 抽测：短征 sx08 错层烬庭

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**（旁注：通关窗 PAPI/gate Failed-to-find-player 假红已软复检清 · Boss 垫空气同构 · live 第4次帽 SKIP · 地图 elite 壳/WE pending → PARTIAL 不挡绿）  
**证据：** `/workspace/tmp/d410-sx08-spot/` · summary `99-summary.json` · run `run.log` · soft `20-soft-recheck.json`  
**对照：** D407 sx07 spot · D391 FAIL 退票根因（rooms×3+boss）· DESIGN `DESIGN-ember-short-dungeon-sx08-2026-10-10.md`  
**上游 tip：** 键 `24ffa712` · MM/DP `4044d460` · 骨架 `50e5c79c` · day_line/fc 挂盘 `c3db34ee`

## tip / jar

| 项 | 值 |
|----|----|
| 键 tip | `24ffa712` |
| MM tip | `4044d460` |
| 菜单骨架 | `50e5c79c` |
| day_line/fc 挂 | `c3db34ee` |
| jar（通关窗 live） | **`1.65.118-d410.local`** · bv **72** · Enabling 已核 · sha256 `922a0c81…959c16` |
| runId | `sx08-mv1lyf6l-yjj` |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 八本菜单 + day_line + fc | **PASS** | `ember_p1_short` **Q=错层** · `enter sx08` · Q/I 挂 `%corerpg_p1_sx08_day_line%` + `%corerpg_p1_sx08_fc%` · I 合计含八本 · **无**假写个人 n/3 · `30-menu-short.json` / V8_menu_day_line |
| 2 | Q01 门闩 | **PASS** | `D410SxNo` →「未解锁短征（需本人首通 Q01）」 |
| 2b | `p1 enter sx08` | **PASS** | 预留体力 30 · 今日有奖 0/3 · bound · Director rooms×3+boss |
| 3 | 体力净扣 30 | **PASS** | 90→60（**非**净零假结算） |
| 3b | Director FIGHTING + 三房 | **PASS** | r1 下层庭 / r2 错层换层 / r3 上层甬道 started+cleared |
| 4 | LayerCast | **PASS** | MM「【错层】层间砸落——退进下层栏或侧龛 / 下撤半层！」+ CoreRpg 蓄力「层间砸落」「扫层斩」+ 上层守吏现身 |
| 5 | S47 结算 | **PASS** | 有奖 **币80+碎4+骨3**；首通另加 **币110+碎6+胚1**；到账币 **190** / 仓碎 **+10** / 骨 **+3** / 胚 **+1**；`short settle … reward day=1/3`；releaseEarly=false |
| 5b | DP EmberSx08 idle 释放 | **PASS** | 结算后实例关闭路径正常；无开战前全员离开假结算 |
| 6 | 日帽独立 | **PASS** | sx08 settle day=1/3；软复检 PAPI `今日有奖 1/3`；sx01–sx07 仍 0/3；随后进 sx01 仍独立 0/3；claim=`p1_sx08_day` |
| 6b | 第4次无结算仍可进 | **SKIP（旁注）** | 本窗仅 1 次通关未凑满 3；`daily_reward_cap=3` + claim 独立同构 |
| 7 | gate_daily 仍拒 | **PASS** | 软复检 `%corerpg_gate_daily%` → `no`（通关窗瞬断假红已清） |
| 7b | 旧 EmberDaily | **PASS（纪律）** | 未走 EmberDaily 路径 |
| 8 | afk / 三开关 | **PASS** | six_slot enabled/migrate/set_bonus 仍 true；`daily_kills=2400` 未拧；bv72；**≠关观察** |

## 旁注（非硬红）

| 项 | 说明 |
|----|------|
| 通关窗 PAPI 瞬断 | bot 换图/leave 时 console `Failed to find player`；hub 软复检 gate=`no` · sx08 day_line=`今日有奖 1/3` · fc=`已领取` 全绿 |
| Boss 垫空气 | elite 壳 vs runs boss@0,64,102 → 进 Boss 前 fill 空气（同构 sx06/07）；**地图 WE 仍 pending**（PARTIAL 不挡进本结算绿） |
| MM suffocate WARN | r3 途中 `EmberSx08Guard` 卡方块 WARN（damage cancelled）— 同 elite/WE 垫空气旁注级 |
| live 第4次 | 未连打 3 次凑满；配置帽=3 + 结算钩同构 |
| 并行占线 | 等 D408 复测 PASS / D409 final 让出后开跑；通关窗 jar 已是 d410；未为本抽停服 |

## 纪律核对

- Stage2 三开关仍 true · ×0.97 未改 · ≠关观察  
- gate_daily 仍拒 · 未假开旧七线  
- afk.tiers / daily_kills 未抬  
- login / proxy / MariaDB 未碰 · 禁切分支  

## 证据文件

`00-switches-pre.json` · `10-q01-gate.json` · `20-gate-daily-recheck.json` · `20-soft-recheck.json` · `30-menu-short.json` · `31-daycap-papi-*.json` · `40-before.json` · `41-enter.json` · `51-r1.json`–`53-r3.json` · `50-fight.json` · `60-after.json` · `90-endstate.json` · `99-summary.json` · `run.log` · `recheck.log`

---

*D410 spot · sx08 错层烬庭 · PASS · day_line `c3db34ee` · ≠关观察*

**本号 tip：** `b37aeca3`
