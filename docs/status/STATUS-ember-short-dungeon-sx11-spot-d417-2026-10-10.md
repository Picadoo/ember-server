# 状态 · D417 抽测：短征 sx11 烬镜对廊

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**（旁注：地图 raid 壳/WE pending → PARTIAL 不挡绿 · live 第4次帽 SKIP · 首轮 papi 瞬切失败已软复检消）  
**证据：** `/workspace/tmp/d417-sx11-spot/` · summary `99-summary.json` · run `run.log` · soft `20-soft-recheck.json`  
**对照：** D414 sx10 spot · D391 FAIL 退票根因（rooms×3+boss）· DESIGN `DESIGN-ember-short-dungeon-sx11-2026-10-10.md`  
**上游 tip：** 键 `2ba2233d` · MM/DP `1b81b415` · 骨架 `f58c88d3` · day_line/fc 挂盘 `a0b94b7d`

## tip / jar

| 项 | 值 |
|----|----|
| 键 tip | `2ba2233d` |
| MM tip | `1b81b415` |
| 菜单骨架 | `f58c88d3` |
| day_line/fc 挂 | `a0b94b7d` |
| jar（通关窗 live） | **`1.65.122-d417.local`** · bv **75** · Enabling 已核 · sha256 `36b6637d…cadc0604` |
| runId | `sx11-mv1peknn-y37` |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 十一本菜单 + day_line + fc | **PASS** | `ember_p1_short` **X=烬镜** · `enter sx11` · X/I 挂 `%corerpg_p1_sx11_day_line%` + `%corerpg_p1_sx11_fc%` · I 合计含十一本 · **无**假写个人 n/3 · `30-menu-short.json` / V8_menu_day_line |
| 2 | Q01 门闩 | **PASS** | `D417SxNo` →「未解锁短征（需本人首通 Q01）」 |
| 2b | `p1 enter sx11` | **PASS** | 预留体力 30 · 今日有奖 0/3 · bound · Director rooms×3+boss |
| 3 | 体力净扣 30 | **PASS** | 90→60（**非**净零假结算） |
| 3b | Director FIGHTING + 三房 | **PASS** | r1 对廊前厅 / r2 左右对称双廊 / r3 合镜甬道 started+cleared |
| 4 | MirrorCast | **PASS** | MM「【烬镜】镜面扫线——退进合镜侧龛或回撤侧廊！」+ CoreRpg 蓄力「镜面扫线」「对折斩」+ 合镜守吏现身 |
| 5 | S50 结算 | **PASS** | 有奖 **币80+碎4+骨3**；首通另加 **币80+碎6+胚1**；到账币 **160** / 仓碎 **+10** / 骨 **+3** / 胚 **+1**；`short settle … reward day=1/3`；releaseEarly=false；claim=`p1_sx11_day` |
| 5b | DP EmberSx11 idle 释放 | **PASS** | 结算后实例关闭路径正常；无开战前全员离开假结算 |
| 6 | 日帽独立 | **PASS** | sx11 settle day=1/3；软复检 PAPI `今日有奖 1/3`；sx01 仍 0/3；随后进 sx01 仍独立；claim=`p1_sx11_day` |
| 6b | 第4次无结算仍可进 | **SKIP（旁注）** | 本窗仅 1 次通关未凑满 3；`daily_reward_cap=3` + claim 独立同构 |
| 7 | gate_daily 仍拒 | **PASS** | 软复检 `%corerpg_gate_daily%` → `no`（首轮瞬切 Failed to find player 已消） |
| 7b | 旧 EmberDaily | **PASS（纪律）** | 未走 EmberDaily 路径 |
| 8 | afk / 三开关 | **PASS** | six_slot enabled/migrate/set_bonus 仍 true；`daily_kills=2400` 未拧；×0.97 未改；bv75；**≠关观察** |

## 旁注（非硬红）

| 项 | 说明 |
|----|------|
| Boss 垫空气 | raid 壳 vs runs boss@0,64,102 → 进 Boss 前 fill 空气（同构 sx06–10）；**地图 WE 仍 pending**（PARTIAL 不挡进本结算绿） |
| live 第4次 | 未连打 3 次凑满；配置帽=3 + 结算钩同构 |
| 首轮 papi | 进本/离本瞬切 `Failed to find player`；hub 软复检 gate/day_line/fc 全绿 |
| 地图 | `map_version: ember_short_sx11@d417-pending` · raid 壳 |

## 纪律核对

- Stage2 三开关仍 true · ×0.97 未改 · ≠关观察  
- gate_daily 仍拒 · 未假开旧七线  
- afk.tiers / daily_kills 未抬  
- login / proxy / MariaDB 未碰 · 禁切分支  

## 证据文件

`00-switches-pre.json` · `10-q01-gate.json` · `20-gate-daily-recheck.json` · `20-soft-recheck.json` · `30-menu-short.json` · `31-daycap-papi-*.json` · `40-before.json` · `41-enter.json` · `51-r1.json`–`53-r3.json` · `50-fight.json` · `60-after.json` · `90-endstate.json` · `99-summary.json` · `run.log` · `recheck.log`

---

*D417 spot · sx11 烬镜对廊 · PASS · day_line `a0b94b7d` · ≠关观察*

**本号 tip：** `6c2bc08d`
