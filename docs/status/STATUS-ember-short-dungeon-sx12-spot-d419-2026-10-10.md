# 状态 · D419 抽测：短征 sx12 烬枢转厅

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**（旁注：地图 raid 壳/WE pending → PARTIAL 不挡绿 · live 第4次帽 SKIP · 首轮 papi 瞬切失败 + 开关读取笔误已软复检消）  
**证据：** `/workspace/tmp/d419-sx12-spot/` · summary `99-summary.json` · run `run.log` · soft `20-soft-recheck.json`  
**对照：** D417 sx11 spot · D414 sx10 spot · D391 FAIL 退票根因（rooms×3+boss）· DESIGN `DESIGN-ember-short-dungeon-sx12-2026-10-10.md`  
**上游 tip：** 键 `86744492` · MM/DP `08c06d03` · 骨架 `9825b9f6` · day_line/fc 挂盘 `4dae0da4`

## tip / jar

| 项 | 值 |
|----|----|
| 键 tip | `86744492` |
| MM tip | `08c06d03` |
| 菜单骨架 | `9825b9f6` |
| day_line/fc 挂 | `4dae0da4` |
| jar（通关窗 live） | **`1.65.123-d419.local`** · bv **76** · Enabling 已核 · sha256 `87826547…fb554749` |
| runId | `sx12-mv1q2016-jbv` |
| 并行 | D418 已 PASS 收口后开抽；**未为本号停服**（jar 已由上游换装 live） |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 十二本菜单 + day_line + fc | **PASS** | `ember_p1_short` **Y=烬枢** · `enter sx12` · Y/I 挂 `%corerpg_p1_sx12_day_line%` + `%corerpg_p1_sx12_fc%` · I 合计含十二本 · **无**假写个人 n/3 · `30-menu-short.json` / V8_menu_day_line |
| 2 | Q01 门闩 | **PASS** | `D419SxNo` →「未解锁短征（需本人首通 Q01）」 |
| 2b | `p1 enter sx12` | **PASS** | 预留体力 30 · 今日有奖 0/3 · bound · Director rooms×3+boss（禁 D391 净零） |
| 3 | 体力净扣 30 | **PASS** | 净扣 30（**非**净零假结算） |
| 3b | Director FIGHTING + 三房 | **PASS** | r1 入枢庭 / r2 环枢侧厢 / r3 枢冠甬道 started+cleared |
| 4 | PivotCast | **PASS** | MM「【烬枢】枢扫斩——退进侧厢龛或枢冠侧台！」+ CoreRpg 蓄力「枢扫斩」+ 枢冠守吏现身 |
| 5 | S51 结算 | **PASS** | 有奖 **币80+碎4+骨3**；首通另加 **币70+碎6+胚1**；到账币 **150** / 仓碎 **+10** / 骨 **+3** / 胚 **+1**；`short settle … reward day=1/3`；releaseEarly=false；claim=`p1_sx12_day` |
| 5b | DP EmberSx12 idle 释放 | **PASS** | 结算后实例关闭路径正常；无开战前全员离开假结算 |
| 6 | 日帽独立 | **PASS** | sx12 settle day=1/3；软复检 PAPI sx12 `今日有奖 1/3`、sx01–sx11 仍 `0/3`；随后进 sx01 仍独立；claim=`p1_sx12_day` |
| 6b | 第4次无结算仍可进 | **SKIP（旁注）** | 本窗仅 1 次通关未凑满 3；`daily_reward_cap=3` + claim 独立同构 |
| 7 | gate_daily 仍拒 | **PASS** | 软复检 `%corerpg_gate_daily%` → `no`（首轮瞬切 Failed to find player 已消） |
| 7b | 旧 EmberDaily | **PASS（纪律）** | 未走 EmberDaily 路径 |
| 8 | afk / 三开关 | **PASS** | six_slot enabled/migrate/set_bonus 仍 true；`daily_kills=2400` 未拧；×0.97 未改；bv76；**≠关观察** |

## 旁注（非硬红）

| 项 | 说明 |
|----|------|
| Boss 垫空气 | raid 壳 vs runs boss@0,64,102 → 进 Boss 前 fill 空气（同构 sx06–11）；**地图 WE 仍 pending**（PARTIAL 不挡进本结算绿） |
| live 第4次 | 未连打 3 次凑满；配置帽=3 + 结算钩同构 |
| 首轮 papi | 进本/离本瞬切 `Failed to find player`；hub 软复检 gate/day_line/fc 全绿 |
| 开关读取 | 抽测脚本 `siy_slot` 笔误致首轮 V0 假红；磁盘/软复检六槽三开仍 true |
| 地图 | `map_version: ember_short_sx12@d419-pending` · raid 壳 |
| D418 并行 | 证据 `/workspace/tmp/d418-forge-remain-spot/` RESULT=PASS 后开抽；未停服打断 |

## 纪律核对

- Stage2 三开关仍 true · ×0.97 未改 · ≠关观察  
- gate_daily 仍拒 · 未假开旧七线  
- afk.tiers / daily_kills 未抬  
- login / proxy / MariaDB 未碰 · 禁切分支 / stash / reset --hard / force-push  

## 证据文件

`00-switches-pre.json` · `10-q01-gate.json` · `20-gate-daily-recheck.json` · `20-soft-recheck.json` · `30-menu-short.json` · `31-daycap-papi-*.json` · `40-before.json` · `41-enter.json` · `51-r1.json`–`53-r3.json` · `50-fight.json` · `60-after.json` · `90-endstate.json` · `99-summary.json` · `run.log` · `recheck.log`

---

*D419 spot · sx12 烬枢转厅 · PASS · day_line `4dae0da4` · ≠关观察*

**本号 tip：** `6107d1a6`
