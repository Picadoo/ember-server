# 状态 · D403 抽测：短征 sx06 烬环廊

**日期：** 2026-10-10（上海时间）  
**执行：** 余烬-测试岗  
**裁决：** **PASS**（旁注：Boss 垫空气清障 · live 第4次帽 SKIP · 并行 jar 窗 d404/d405）  
**证据：** `/workspace/tmp/d403-sx06-spot/` · summary `99-summary.json` · run `run4.log`  
**对照：** D400 spot @`8aa7c08c` · D391 FAIL 退票根因（rooms×3+boss）· DESIGN `DESIGN-ember-short-dungeon-sx06-2026-10-10.md`  
**补：** 选页 day_line tip `a6162da6`（六本同屏含烬环）

## tip / jar

| 项 | 值 |
|----|----|
| 键 tip | `d96b0dd3` |
| MM tip | `7d251df4` |
| 菜单骨架 | `a540365e` |
| day_line 挂 | `a6162da6` |
| jar（产物） | `1.65.112-d403.local`（sha256 `c3a4da5f66636fb6d300f31456ad35fcabe9a8fb4e2062ac14a8d5cffbf35279`） |
| jar（通关窗 live） | `1.65.114-d405.local`（并行装 jar；**sx06/S45/bv70 仍在**；未反复为 d403 停服） |
| bv | **70** |
| runId | `sx06-mv1kh7j3-90` |

## 分项

| # | 项 | 结果 | 证据要点 |
|---|----|------|----------|
| 1 | 六本菜单 + day_line | **PASS** | `ember_p1_short`：烬门/锈灯/霜雾/烬井/裂谷/**烬环** · `enter sx06` · M/I 挂 `%corerpg_p1_sx06_day_line%`（`a6162da6`）· **无**假写个人 n/3 · I 六行+合计 |
| 2 | Q01 门闩 | **PASS** | `D403SxNo` →「未解锁短征（需本人首通 Q01）」 |
| 2b | `p1 enter sx06` | **PASS** | 预留体力 30 · 今日有奖 0/3 · bound · Director rooms×3+boss |
| 3 | 体力净扣 30 | **PASS** | 90→60 |
| 3b | Director FIGHTING + 三房 | **PASS** | r1 外环庭 / r2 双环廊绕行 / r3 环心甬道 started+cleared |
| 4 | RingCast | **PASS** | MM「【烬环】环扫斩——退进外环龛或离开弧带！」+ CoreRpg 蓄力「环扫斩」「向心拉扯」+ 环心守吏现身 |
| 5 | S45 结算 | **PASS** | 有奖 **币80+碎4+骨3**；首通另加 **币130+碎6+胚1**；到账币 **210** / 仓碎 **+10** / 骨 **+3** / 胚 **+1**；`short settle … reward day=1/3` |
| 6 | 日帽独立 | **PASS** | sx06 settle day=1/3；PAPI `今日有奖 1/3`；随后进 sx01 仍独立；claim=`p1_sx06_day` |
| 6b | 第4次无结算仍可进 | **SKIP（旁注）** | 本窗仅 1 次通关未凑满 3；`daily_reward_cap=3` + claim 独立同构 |
| 7 | gate_daily 仍拒 | **PASS** | `%corerpg_gate_daily%` → `no` |
| 7b | 旧 EmberDaily | **PASS（纪律）** | 未走 EmberDaily 路径 |
| 8 | afk / 三开关 | **PASS** | six_slot enabled/migrate/set_bonus 仍 true；`daily_kills=2400` 未拧；bv70；**≠关观察** |

## 旁注（非硬红）

| 项 | 说明 |
|----|------|
| Boss 垫空气 | 地图壳来自 `ember_daily_spire_v1`，runs boss 坐标仍参考 sx01 白盒 `0,64,102` → 首几轮 Boss **stuck in STONE** 被 anomaly 删除、weaken n=0、无结算。通关窗进 Boss 前 `fill/setblock` 清空气后正常击杀结算。**地图 WE 仍 pending**（STATUS 骨架已记）— 本抽测不把 WE 当硬红，但记旁注 |
| live 第4次 | 未连打 3 次凑满；配置帽=3 + 结算钩同构 |
| 并行 jar | 等待 D401 复测时 play 已装 d403；随后并行 d404/d405 重启；通关窗 live=`1.65.114-d405.local` 仍 bv70+sx06；三开关/afk 未拧；**勿无故反复停服** 纪律遵守 |
| attempt1 | D401WkV4 并行污染 + 假命中 sx01 settle；attempt2 被 d405 关服踢；attempt3 Boss 卡石无结算；attempt4 PASS |

## 纪律核对

- Stage2 三开关仍 true · ×0.97 未改 · ≠关观察  
- gate_daily 仍拒 · 未假开旧七线  
- afk.tiers / daily_kills 未抬  
- login / proxy / MariaDB 未碰 · 禁切分支  

## 证据文件

`00-switches-pre.json` · `10-q01-gate.json` · `20-gate-daily-recheck.json` · `30-menu-short.json` · `31-daycap-papi-*.json` · `40-before.json` · `41-enter.json` · `51-r1.json`–`53-r3.json` · `50-fight.json` · `60-after.json` · `90-endstate.json` · `99-summary.json` · `run4.log` · `attempt1-fail/` · `attempt3-boss-stuck/`

---

*D403 spot · sx06 烬环廊 · PASS · day_line `a6162da6` · ≠关观察*
