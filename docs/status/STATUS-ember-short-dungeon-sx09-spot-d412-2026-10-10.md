# 状态 · D412 抽测：短征 sx09 烬渠跳石

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**（旁注：地图 abyss 壳/WE pending → PARTIAL 不挡绿 · live 第4次帽 SKIP · 并行 D409 sx08 清本占线未挡本号）  
**证据：** `/workspace/tmp/d412-sx09-spot/` · summary `99-summary.json` · run `run.log` · soft `20-soft-recheck.json`  
**对照：** D410 sx08 spot · D391 FAIL 退票根因（rooms×3+boss）· DESIGN `DESIGN-ember-short-dungeon-sx09-2026-10-10.md`  
**上游 tip：** 键 `13583cfe` · MM/DP `58f0e791` · 骨架 `2ccd30f8` · day_line/fc 挂盘 `547d57fc`

## tip / jar

| 项 | 值 |
|----|----|
| 键 tip | `13583cfe` |
| MM tip | `58f0e791` |
| 菜单骨架 | `2ccd30f8` |
| day_line/fc 挂 | `547d57fc` |
| jar（通关窗 live） | **`1.65.119-d412.local`** · bv **73** · Enabling 已核 · sha256 `fab84c14…847959a` |
| runId | `sx09-mv1mo7ow-b9z` |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 九本菜单 + day_line + fc | **PASS** | `ember_p1_short` **T=烬渠** · `enter sx09` · T/I 挂 `%corerpg_p1_sx09_day_line%` + `%corerpg_p1_sx09_fc%` · I 合计含九本 · **无**假写个人 n/3 · `30-menu-short.json` / V8_menu_day_line |
| 2 | Q01 门闩 | **PASS** | `D412SxNo` →「未解锁短征（需本人首通 Q01）」 |
| 2b | `p1 enter sx09` | **PASS** | 预留体力 30 · 今日有奖 0/3 · bound · Director rooms×3+boss |
| 3 | 体力净扣 30 | **PASS** | 90→60（**非**净零假结算） |
| 3b | Director FIGHTING + 三房 | **PASS** | r1 渠岸庭 / r2 跳石渡渠 / r3 对岸甬道 started+cleared |
| 4 | CanalCast | **PASS** | MM「【烬渠】渠心溅浪——退进岸侧龛或回撤跳石！」+ CoreRpg 蓄力「渠心溅浪」「扫岸斩」+ 对岸守吏现身 |
| 5 | S48 结算 | **PASS** | 有奖 **币80+碎4+骨3**；首通另加 **币100+碎6+胚1**；到账币 **180** / 仓碎 **+10** / 骨 **+3** / 胚 **+1**；`short settle … reward day=1/3`；releaseEarly=false |
| 5b | DP EmberSx09 idle 释放 | **PASS** | 结算后实例关闭路径正常；无开战前全员离开假结算 |
| 6 | 日帽独立 | **PASS** | sx09 settle day=1/3；软复检 PAPI `今日有奖 1/3`；sx01 仍 0/3；随后进 sx01 仍独立 0/3；claim=`p1_sx09_day` |
| 6b | 第4次无结算仍可进 | **SKIP（旁注）** | 本窗仅 1 次通关未凑满 3；`daily_reward_cap=3` + claim 独立同构 |
| 7 | gate_daily 仍拒 | **PASS** | 软复检 `%corerpg_gate_daily%` → `no` |
| 7b | 旧 EmberDaily | **PASS（纪律）** | 未走 EmberDaily 路径 |
| 8 | afk / 三开关 | **PASS** | six_slot enabled/migrate/set_bonus 仍 true；`daily_kills=2400` 未拧；bv73；**≠关观察** |

## 旁注（非硬红）

| 项 | 说明 |
|----|------|
| Boss 垫空气 | abyss 壳 vs runs boss@0,64,102 → 进 Boss 前 fill 空气（同构 sx06–08）；**地图 WE 仍 pending**（PARTIAL 不挡进本结算绿） |
| live 第4次 | 未连打 3 次凑满；配置帽=3 + 结算钩同构 |
| 并行占线 | D409 sx08 清本 / D411 薄抽并行；本号独立 bot；未为本抽停服；jar 已是 d412 |

## 纪律核对

- Stage2 三开关仍 true · ×0.97 未改 · ≠关观察  
- gate_daily 仍拒 · 未假开旧七线  
- afk.tiers / daily_kills 未抬  
- login / proxy / MariaDB 未碰 · 禁切分支  

## 证据文件

`00-switches-pre.json` · `10-q01-gate.json` · `20-gate-daily-recheck.json` · `20-soft-recheck.json` · `30-menu-short.json` · `31-daycap-papi-*.json` · `40-before.json` · `41-enter.json` · `51-r1.json`–`53-r3.json` · `50-fight.json` · `60-after.json` · `90-endstate.json` · `99-summary.json` · `run.log` · `recheck.log`

---

*D412 spot · sx09 烬渠跳石 · PASS · day_line `547d57fc` · ≠关观察*

**本号 tip：** `9c737737`
