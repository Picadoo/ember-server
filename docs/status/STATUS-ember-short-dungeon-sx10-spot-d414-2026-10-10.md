# 状态 · D414 抽测：短征 sx10 烬闸递室

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**（旁注：地图 raid 壳/WE pending → PARTIAL 不挡绿 · live 第4次帽 SKIP · 首轮 papi 瞬切失败已软复检消）  
**证据：** `/workspace/tmp/d414-sx10-spot/` · summary `99-summary.json` · run `run.log` · soft `20-soft-recheck.json`  
**对照：** D412 sx09 spot · D391 FAIL 退票根因（rooms×3+boss）· DESIGN `DESIGN-ember-short-dungeon-sx10-2026-10-10.md`  
**上游 tip：** 键 `9c8fb353` · MM/DP `efb26b5d` · 骨架 `57edf3fc` · day_line/fc 挂盘 `7c5e8267`

## tip / jar

| 项 | 值 |
|----|----|
| 键 tip | `9c8fb353` |
| MM tip | `efb26b5d` |
| 菜单骨架 | `57edf3fc` |
| day_line/fc 挂 | `7c5e8267` |
| jar（通关窗 live） | **`1.65.121-d414.local`** · bv **74** · Enabling 已核 · sha256 `cbcfb4c0…8526153e` |
| runId | `sx10-mv1olo2d-3po` |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 十本菜单 + day_line + fc | **PASS** | `ember_p1_short` **V=烬闸** · `enter sx10` · V/I 挂 `%corerpg_p1_sx10_day_line%` + `%corerpg_p1_sx10_fc%` · I 合计含十本 · **无**假写个人 n/3 · `30-menu-short.json` / V8_menu_day_line |
| 2 | Q01 门闩 | **PASS** | `D414SxNo` →「未解锁短征（需本人首通 Q01）」 |
| 2b | `p1 enter sx10` | **PASS** | 预留体力 30 · 今日有奖 0/3 · bound · Director rooms×3+boss |
| 3 | 体力净扣 30 | **PASS** | 90→60（**非**净零假结算） |
| 3b | Director FIGHTING + 三房 | **PASS** | r1 外闸庭 / r2 递闸密封室 / r3 末室甬道 started+cleared |
| 4 | GateCast | **PASS** | MM「【烬闸】闸心压浪——退进室侧龛或回撤闸后！」+ CoreRpg 蓄力「闸心压浪」「扫室斩」+ 末室守吏现身 |
| 5 | S49 结算 | **PASS** | 有奖 **币80+碎4+骨3**；首通另加 **币90+碎6+胚1**；到账币 **170** / 仓碎 **+10** / 骨 **+3** / 胚 **+1**；`short settle … reward day=1/3`；releaseEarly=false |
| 5b | DP EmberSx10 idle 释放 | **PASS** | 结算后实例关闭路径正常；无开战前全员离开假结算 |
| 6 | 日帽独立 | **PASS** | sx10 settle day=1/3；软复检 PAPI `今日有奖 1/3`；sx01 仍 0/3；随后进 sx01 仍独立 0/3；claim=`p1_sx10_day` |
| 6b | 第4次无结算仍可进 | **SKIP（旁注）** | 本窗仅 1 次通关未凑满 3；`daily_reward_cap=3` + claim 独立同构 |
| 7 | gate_daily 仍拒 | **PASS** | 软复检 `%corerpg_gate_daily%` → `no`（首轮瞬切 Failed to find player 已消） |
| 7b | 旧 EmberDaily | **PASS（纪律）** | 未走 EmberDaily 路径 |
| 8 | afk / 三开关 | **PASS** | six_slot enabled/migrate/set_bonus 仍 true；`daily_kills=2400` 未拧；bv74；**≠关观察** |

## 旁注（非硬红）

| 项 | 说明 |
|----|------|
| Boss 垫空气 | raid 壳 vs runs boss@0,64,102 → 进 Boss 前 fill 空气（同构 sx06–09）；**地图 WE 仍 pending**（PARTIAL 不挡进本结算绿） |
| live 第4次 | 未连打 3 次凑满；配置帽=3 + 结算钩同构 |
| 首轮 papi | 进本/离本瞬切 `Failed to find player`；hub 软复检 gate/day_line/fc 全绿 |
| 并行占线 | D415 薄抽并行；本号独立 bot；未为本抽停服；jar 已是 d414 |

## 纪律核对

- Stage2 三开关仍 true · ×0.97 未改 · ≠关观察  
- gate_daily 仍拒 · 未假开旧七线  
- afk.tiers / daily_kills 未抬  
- login / proxy / MariaDB 未碰 · 禁切分支  

## 证据文件

`00-switches-pre.json` · `10-q01-gate.json` · `20-gate-daily-recheck.json` · `20-soft-recheck.json` · `30-menu-short.json` · `31-daycap-papi-*.json` · `40-before.json` · `41-enter.json` · `51-r1.json`–`53-r3.json` · `50-fight.json` · `60-after.json` · `90-endstate.json` · `99-summary.json` · `run.log` · `recheck.log`

---

*D414 spot · sx10 烬闸递室 · PASS · day_line `7c5e8267` · ≠关观察*

**本号 tip：** `4a2a6c7a`
