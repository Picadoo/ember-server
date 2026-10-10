# 状态 · D421 抽测：短征 sx13 烬衡悬梁

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**（旁注：地图 raid 壳/WE pending → PARTIAL 不挡绿 · live 第4次帽 SKIP · 首轮 papi 瞬切失败已软复检消）  
**证据：** `/workspace/tmp/d421-sx13-spot/` · summary `99-summary.json` · run `run.log` · soft `20-soft-recheck.json`  
**对照：** D419 sx12 spot · D417 sx11 spot · D391 FAIL 退票根因（rooms×3+boss）· DESIGN `DESIGN-ember-short-dungeon-sx13-2026-10-10.md`  
**上游 tip：** 键 `2688b0b2` · MM/DP `0ecf4134` · 骨架 `77c06810`（同族 `f980b4a9`）· day_line/fc 挂盘 `94c826e2`

## tip / jar

| 项 | 值 |
|----|----|
| 键 tip | `2688b0b2` |
| MM tip | `0ecf4134` |
| 菜单骨架 | `77c06810` |
| day_line/fc 挂 | `94c826e2` |
| jar（通关窗 live） | **`1.65.124-d421.local`** · bv **77** · Enabling 已核 · sha256 `33958666…d406e01` |
| runId | `sx13-mv1qrhbv-hlq` |
| 并行 | 上游已换装 live；**未为本号停服** |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 十三本菜单 + day_line + fc | **PASS** | `ember_p1_short` **W=烬衡** · `enter sx13` · W/I 挂 `%corerpg_p1_sx13_day_line%` + `%corerpg_p1_sx13_fc%` · I 合计含十三本 · **无**假写个人 n/3 · `30-menu-short.json` / V8_menu_day_line |
| 2 | Q01 门闩 | **PASS** | `D421SxNo` →「未解锁短征（需本人首通 Q01）」 |
| 2b | `p1 enter sx13` | **PASS** | 预留体力 30 · 今日有奖 0/3 · bound · Director rooms×3+boss（禁 D391 净零） |
| 3 | 体力净扣 30 | **PASS** | 净扣 30（**非**净零假结算） |
| 3b | Director FIGHTING + 三房 | **PASS** | r1 衡门庭 / r2 悬梁衡廊 / r3 衡冠甬道 started+cleared |
| 4 | BalanceCast | **PASS** | MM「【烬衡】衡扫斩——退进衡冠侧龛或已过衡梁侧台！」+ CoreRpg 蓄力「衡扫斩」+ 衡冠守吏现身 |
| 5 | S52 结算 | **PASS** | 有奖 **币80+碎4+骨3**；首通另加 **币60+碎6+胚1**；到账币 **140** / 仓碎 **+10** / 骨 **+3** / 胚 **+1**；`short settle … reward day=1/3`；releaseEarly=false；claim=`p1_sx13_day` |
| 5b | DP EmberSx13 idle 释放 | **PASS** | 结算后实例关闭路径正常；无开战前全员离开假结算 |
| 6 | 日帽独立 | **PASS** | sx13 settle day=1/3；软复检 PAPI sx13 `今日有奖 1/3`、sx01–sx12 仍 `0/3`；随后进 sx01 仍独立；claim=`p1_sx13_day` |
| 6b | 第4次无结算仍可进 | **SKIP（旁注）** | 本窗仅 1 次通关未凑满 3；`daily_reward_cap=3` + claim 独立同构 |
| 7 | gate_daily 仍拒 | **PASS** | 软复检 `%corerpg_gate_daily%` → `no`（首轮瞬切 Failed to find player 已消） |
| 7b | 旧 EmberDaily | **PASS（纪律）** | 未走 EmberDaily 路径 |
| 8 | afk / 三开关 | **PASS** | six_slot enabled/migrate/set_bonus 仍 true；`daily_kills=2400` 未拧；×0.97 未改；bv77；**≠关观察** |

## 旁注（非硬红）

| 项 | 说明 |
|----|------|
| Boss 垫空气 | raid 壳 vs runs boss@0,64,102 → 进 Boss 前 fill 空气（同构 sx06–12）；**地图 WE 仍 pending**（PARTIAL 不挡进本结算绿） |
| live 第4次 | 未连打 3 次凑满；配置帽=3 + 结算钩同构 |
| 首轮 papi | 进本/离本瞬切 `Failed to find player`；hub 软复检 gate/day_line/fc 全绿 |
| 地图 | `map_version: ember_short_sx13@d421-pending` · raid 壳 |

## 纪律核对

- Stage2 三开关仍 true · ×0.97 未改 · ≠关观察  
- gate_daily 仍拒 · 未假开旧七线  
- afk.tiers / daily_kills 未抬  
- login / proxy / MariaDB 未碰 · 禁切分支 / stash / reset --hard / force-push  

## 证据文件

`00-switches-pre.json` · `10-q01-gate.json` · `20-gate-daily-recheck.json` · `20-soft-recheck.json` · `30-menu-short.json` · `31-daycap-papi-*.json` · `40-before.json` · `41-enter.json` · `51-r1.json`–`53-r3.json` · `50-fight.json` · `60-after.json` · `90-endstate.json` · `99-summary.json` · `run.log` · `recheck.log`

---

*D421 spot · sx13 烬衡悬梁 · PASS · day_line `94c826e2` · ≠关观察*
