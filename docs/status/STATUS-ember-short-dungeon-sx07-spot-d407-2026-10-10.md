# 状态 · D407 抽测：短征 sx07 烬塔回升

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**（旁注：通关窗后并行 d409 装 jar 重启 · 菜单断言脚本误用 · Boss 垫空气同构 · live 第4次帽 SKIP）  
**证据：** `/workspace/tmp/d407-sx07-spot/` · summary `99-summary.json` · run `run.log` · soft `20-soft-recheck-post-d409boot.json`  
**对照：** D403 spot · D391 FAIL 退票根因（rooms×3+boss）· DESIGN `DESIGN-ember-short-dungeon-sx07-2026-10-10.md`  
**上游 tip：** 键 `52adffc2` · MM/DP `bac92843` · 骨架 `355b4753` · day_line/fc 挂盘 `5ba8870a`

## tip / jar

| 项 | 值 |
|----|----|
| 键 tip | `52adffc2` |
| MM tip | `bac92843` |
| 菜单骨架 | `355b4753` |
| day_line/fc 挂 | `5ba8870a` |
| jar（通关窗 live） | **`1.65.116-d407.local`** · bv **71** · Enabling 已核 |
| jar（并行后） | `1.65.117-d409.local`（D408/D409 装 jar 重启 @07:17 CST；**未**为本抽反复停服） |
| runId | `sx07-mv1l2kjg-x9h` |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 七本菜单 + day_line + fc | **PASS** | `ember_p1_short` O=烬塔 · `enter sx07` · O/I 挂 `%corerpg_p1_sx07_day_line%` + `%corerpg_p1_sx07_fc%` · I 合计含七本 · **无**假写个人 n/3 · `30-menu-short.json` / V8_menu_day_line |
| 2 | Q01 门闩 | **PASS** | `D407SxNo` →「未解锁短征（需本人首通 Q01）」 |
| 2b | `p1 enter sx07` | **PASS** | 预留体力 30 · 今日有奖 0/3 · bound · Director rooms×3+boss |
| 3 | 体力净扣 30 | **PASS** | 90→60（**非**净零假结算） |
| 3b | Director FIGHTING + 三房 | **PASS** | r1 塔基庭 / r2 折返上行 / r3 塔冠甬道 started+cleared |
| 4 | TowerCast | **PASS** | MM「【烬塔】塔冠坠焰——退进侧窗廊龛或下撤半层！」+ CoreRpg 蓄力「塔冠坠焰」「扫顶柱」+ 塔冠守吏现身 |
| 5 | S46 结算 | **PASS** | 有奖 **币80+碎4+骨3**；首通另加 **币120+碎6+胚1**；到账币 **200** / 仓碎 **+10** / 骨 **+3** / 胚 **+1**；`short settle … reward day=1/3`；releaseEarly=false |
| 5b | DP EmberSx07 idle 释放 | **PASS** | 结算后 `dungeon_EmberSx07_*` 清理释放；无开战前全员离开假结算 |
| 6 | 日帽独立 | **PASS** | sx07 settle day=1/3；软复检 PAPI `今日有奖 1/3`；随后进 sx01 仍独立 0/3；claim=`p1_sx07_day` |
| 6b | 第4次无结算仍可进 | **SKIP（旁注）** | 本窗仅 1 次通关未凑满 3；`daily_reward_cap=3` + claim 独立同构 |
| 7 | gate_daily 仍拒 | **PASS** | 软复检 `%corerpg_gate_daily%` → `no`（通关窗瞬断假红已清） |
| 7b | 旧 EmberDaily | **PASS（纪律）** | 未走 EmberDaily 路径 |
| 8 | afk / 三开关 | **PASS** | six_slot enabled/migrate/set_bonus 仍 true；`daily_kills=2400` 未拧；bv71；**≠关观察** |

## 旁注（非硬红）

| 项 | 说明 |
|----|------|
| 脚本 V1_menu_seven | 首跑断言误用 `menu.six`（undefined）记 FAIL；静态菜单与 V8_menu_day_line 已证七本+day_line+fc；已改脚本 |
| 通关窗 PAPI 瞬断 | bot 换图/leave 时 console `Failed to find player`；软复检 hub 在线全绿 |
| Boss 垫空气 | tide 壳 vs runs boss@0,64,102 → 进 Boss 前 fill 空气（同构 sx06）；**地图 WE 仍 pending** |
| live 第4次 | 未连打 3 次凑满；配置帽=3 + 结算钩同构 |
| 并行 jar | D408 薄抽并行；通关后 play 被装 `1.65.117-d409.local` 重启；本抽证据采自 **d407** 窗；三开关/afk 未拧 |

## 纪律核对

- Stage2 三开关仍 true · ×0.97 未改 · ≠关观察  
- gate_daily 仍拒 · 未假开旧七线  
- afk.tiers / daily_kills 未抬  
- login / proxy / MariaDB 未碰 · 禁切分支  

## 证据文件

`00-switches-pre.json` · `10-q01-gate.json` · `20-gate-daily-recheck.json` · `20-soft-recheck-post-d409boot.json` · `30-menu-short.json` · `31-daycap-papi-*.json` · `40-before.json` · `41-enter.json` · `51-r1.json`–`53-r3.json` · `50-fight.json` · `60-after.json` · `90-endstate.json` · `99-summary.json` · `run.log` · `recheck2.log`

---

*D407 spot · sx07 烬塔回升 · PASS · day_line `5ba8870a` · ≠关观察*
